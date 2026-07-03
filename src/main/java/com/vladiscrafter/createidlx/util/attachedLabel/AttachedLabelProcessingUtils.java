package com.vladiscrafter.createidlx.util.attachedLabel;

import com.simibubi.create.content.trains.display.FlapDisplaySection;
import com.vladiscrafter.createidlx.config.CIDLXConfigs;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.vladiscrafter.createidlx.util.attachedLabel.AttachedLabelPart.*;
import static com.vladiscrafter.createidlx.util.SingleLineDisplaySourceMixinUtils.*;

public class AttachedLabelProcessingUtils {
    public static String setToPrimitivePlaceholder() {
        boolean isDollarSignPlaceholderEnabled = CIDLXConfigs.server.enableDollarPlaceholder.get();

        return (isDollarSignPlaceholderEnabled ? "$" : "{}");
    }

    public static String appendPrimitivePlaceholder(String label) {
        boolean isDollarSignPlaceholderEnabled = CIDLXConfigs.server.enableDollarPlaceholder.get();

        return label + " " + (isDollarSignPlaceholderEnabled ? "$" : "{}");
    }

    public static String breakDownAndAssembleLabel(String label, String rawInfo) {
        if (label.isEmpty()) return rawInfo;

        StringBuilder result = new StringBuilder();
        breakDownAndAssembleLabel(label, rawInfo, result::append);

        return result.toString();
    }

    public static List<MutableComponent> breakDownAndAssembleLabelAsComponentList(String label, String rawInfo) {
        if (label.isEmpty()) return List.of(Component.literal(rawInfo));

        List<MutableComponent> result = new ArrayList<>();
        breakDownAndAssembleLabel(label, rawInfo, part -> result.add(Component.literal(part)));

        return result;
    }

    private static void breakDownAndAssembleLabel(String label, String rawInfo, Consumer<String> addAction) {
        ArrayList<AttachedLabelBreakdownResult> sections = trimRawInfo(label, rawInfo);

        for (AttachedLabelBreakdownResult section : sections) {
            addAction.accept(section.getString());
        }
    }

    public static ArrayList<FlapDisplaySection> breakDownAndAssembleLabelAsSectionList(String label, String rawInfo,
                                                                                       String layoutKey, float maxValueWidth, float valueWidthMod) {
        ArrayList<AttachedLabelBreakdownResult> sections = trimRawInfo(label, rawInfo);
        ArrayList<FlapDisplaySection> result = new ArrayList<>();

        if (!sections.isEmpty()) for (AttachedLabelBreakdownResult section : sections)
            result.add(section instanceof Placeholder
                    ? createValueSection(Math.min(section.length() * valueWidthMod, maxValueWidth), layoutKey, section.getString())
                    : createLabelSection(section.getString()));

        return result;
    }

    public static List<MutableComponent> assembleLabelFromSectionsAsComponentList(ArrayList<FlapDisplaySection> sections) {
        List<MutableComponent> result = new ArrayList<>();

        for (FlapDisplaySection section : sections) result.add(Component.literal("").append(section.getText()));

        return result;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean shouldBeProcessed(String label) {
        return getTotalPlaceholdersCountInLabel(label) > 0 || hasEscapedPlaceholders(label);
    }

    public static int getTotalPlaceholdersCountInLabel(String label) {
        return extractPlaceholders(label).size();
    }

    public static ArrayList<PlainPart> extractPlainParts(String label) {
        return extractParts(label, PlainPart.class);
    }

    public static ArrayList<Placeholder> extractPlaceholders(String label) {
        return extractParts(label, Placeholder.class);
    }

    private static <T extends AttachedLabelBreakdownResult> ArrayList<T> extractParts(String label, Class<T> type) {
        return plainifyEscapedPlaceholders(label).stream()
                .filter(type::isInstance).map(type::cast).collect(Collectors.toCollection(ArrayList::new));
    }

    public static boolean hasEscapedPlaceholders(String label) {
        boolean hasEscapedPlaceholders = false;

        for (AttachedLabelBreakdownResult part : processLabel(label))
            if (part instanceof Placeholder placeholder && placeholder.isEscaped()) hasEscapedPlaceholders = true;

        return hasEscapedPlaceholders;
    }

    private static ArrayList<AttachedLabelBreakdownResult> processLabel(String label) {
        boolean isEscapingOfPlaceholdersEnabled = CIDLXConfigs.server.enableEscapingOfPlaceholders.get();

        StringBuilder breakableLabel = new StringBuilder(label);
        ArrayList<AttachedLabelBreakdownResult> brokenDownLabel = new ArrayList<>();
        boolean hasEscapedPlaceholders = false;

        while (!breakableLabel.isEmpty()) {
            StringBuilder labelPart = new StringBuilder();
            boolean foundPlaceholder = false;

            for (int i = 0; i < breakableLabel.length(); i++) {
                Placeholder placeholder = getPlaceholder(breakableLabel.toString(), i);

                if (placeholder.length() > 0) {
                    if (!labelPart.isEmpty()) brokenDownLabel.add(new PlainPart(labelPart.toString()));
                    if (!label.isEmpty()) breakableLabel.delete(0, labelPart.toString().length());

                    foundPlaceholder = true;
                    brokenDownLabel.add(placeholder);
                    breakableLabel.delete(0, placeholder.length());
                    break;
                } else if (placeholder.length() == 0) {
                    labelPart.append(breakableLabel.charAt(i));
                }
            }

            if (!foundPlaceholder) {
                brokenDownLabel.add(new PlainPart(breakableLabel.toString()));
                breakableLabel.setLength(0);
            }
        }

        if (isEscapingOfPlaceholdersEnabled) {
            for (int p = 0; p < brokenDownLabel.size(); p++) {
                if (!(brokenDownLabel.get(p) instanceof Placeholder placeholder)) continue;
                if (!placeholder.isEscaped()) continue;

                String part = brokenDownLabel.get(p).getString();

                if (removeEscapeBackslash(PlaceholderType.DOLLAR)) part = part.replace("\\$", "$");
                if (removeEscapeBackslash(PlaceholderType.BRACKETS)) part = part.replace("\\{}", "{}");

                if (removeEscapeBackslash(PlaceholderType.TRIM))
                    part = part.replaceAll("\\\\(?=[{}]\\d+\\$\\d+[}{])", "");

                if (removeEscapeBackslash(PlaceholderType.TRIM_SHORT))
                    part = part.replaceAll("\\\\(?=[{}]\\d+\\$)", "")
                            .replaceAll("\\\\(?=\\$\\d+[}{])", "");

                if (removeEscapeBackslash(PlaceholderType.TRIM_ALT))
                    part = part.replaceAll("\\\\(?=\\$\\{\\d+[+-]{3}\\d+})", "");

                brokenDownLabel.set(p, new PlainPart(part));
            }
        }

        log("\nProcessed label: \n- " + brokenDownLabel.stream()
                .map(AttachedLabelBreakdownResult::asDebugString).collect(Collectors.joining("\n- ")));

        return brokenDownLabel;
    }

    public static ArrayList<AttachedLabelBreakdownResult> plainifyEscapedPlaceholders(String label) {
        ArrayList<AttachedLabelBreakdownResult> oldParts = processLabel(label);
        ArrayList<AttachedLabelBreakdownResult> newParts = new ArrayList<>();

        for (AttachedLabelBreakdownResult oldPart : oldParts) {
            if (oldPart instanceof Placeholder placeholder && placeholder.isEscaped()) newParts.add(new PlainPart(oldPart.getString()));
            else newParts.add(oldPart);
        }

        return newParts; // currently wraps processLabel() for any chain that gets to rendering the finished text
    }

    public static Placeholder getPlaceholder(String text, int i) {
        boolean isDollarSignPlaceholderEnabled = CIDLXConfigs.server.enableDollarPlaceholder.get();
        boolean isBracketsPlaceholderEnabled = CIDLXConfigs.server.enableBracketsPlaceholder.get();
        boolean isOriginalTrimmingPlaceholderEnabled = CIDLXConfigs.server.enableOriginalTrimmingPlaceholder.get();
        boolean isShortenedOriginalTrimmingPlaceholderEnabled = CIDLXConfigs.server.enableShortenedOriginalTrimmingPlaceholder.get();
        boolean isAlternativeTrimmingPlaceholderEnabled = CIDLXConfigs.server.enableAlternativeTrimmingPlaceholder.get();
        boolean isEscapingOfPlaceholdersEnabled = CIDLXConfigs.server.enableEscapingOfPlaceholders.get();

        int inI = i;
        int length = invalid.length();
        PlaceholderType type = invalid.type();
        boolean isEscaped = false;

        if (text.isEmpty()) return invalid;

        if (text.length() > i + 1 && text.charAt(i) == '\\' && isEscapingOfPlaceholdersEnabled) {
            isEscaped = true;
            length++;
            i++;
        }

        if (text.length() > i + 7 && text.charAt(i) == '$' && text.charAt(i + 1) == '{' && text.charAt(i + 2) != '}') {
            int altLength = getAlternativeTrimmingPlaceholderLength(text, i);
            if (altLength > 0) {
                length += altLength;
                type = PlaceholderType.TRIM_ALT;
            }
        } else if (text.charAt(i) == '$' || text.charAt(i) == '}' || text.charAt(i) == '{') {
            Placeholder original = getOriginalTrimmingPlaceholder(text, i);
            int origLength = original.length();
            if (origLength > 2) {
                length += origLength;
                type = original.type();
            } else {
                if (text.charAt(i) == '$') {
                    length += 1;
                    type = PlaceholderType.DOLLAR;
                } else if (text.charAt(i) == '{' && (text.length() > i + 1 && text.charAt(i + 1) == '}')) {
                    length += 2;
                    type = PlaceholderType.BRACKETS;
                }
            }
        }


        if ((type == PlaceholderType.DOLLAR && !isDollarSignPlaceholderEnabled)
                || (type == PlaceholderType.BRACKETS && !isBracketsPlaceholderEnabled)
                || (type == PlaceholderType.TRIM && !isOriginalTrimmingPlaceholderEnabled)
                || (type == PlaceholderType.TRIM_SHORT && !isShortenedOriginalTrimmingPlaceholderEnabled)
                || (type == PlaceholderType.TRIM_ALT && !isAlternativeTrimmingPlaceholderEnabled))
            type = PlaceholderType.DISABLED;

        if (isEscaped) {
            if (length == 1) return invalid;
            else type = type == PlaceholderType.DISABLED ? PlaceholderType.ESCAPED_DISABLED : PlaceholderType.ESCAPED;
        }

        return new Placeholder(text.substring(inI, inI + length), type);
    }

    private static Placeholder getOriginalTrimmingPlaceholder(String text, int i) {
        int inI = i;
        int length = 0;
        boolean hasLeftHalf = true, hasRightHalf = true;

        if (text.charAt(i) != '$' && text.charAt(i) != '{' && text.charAt(i) != '}') return invalid;
        else if (text.charAt(i) == '$') hasLeftHalf = false;
        length++;
        i++;

        boolean readLeftHalf = false, readRightHalf = false;

        int leftHalfDigits = 0;
        while (!readLeftHalf && hasLeftHalf) {
            if (i >= text.length()) return invalid;
            if (!Character.isDigit(text.charAt(i)) && text.charAt(i) != '$') return invalid;
            else if (Character.isDigit(text.charAt(i))) leftHalfDigits++;
            else if (text.charAt(i) == '$') readLeftHalf = true;
            length++;
            i++;
        }
        if (readLeftHalf && leftHalfDigits == 0) return invalid;

        int potentialLength = 0;

        int rightHalfDigits = 0;
        while (!readRightHalf) {
            if (i >= text.length()) break;
            if (!Character.isDigit(text.charAt(i)) && text.charAt(i) != '}' && text.charAt(i) != '{') break;
            else if (Character.isDigit(text.charAt(i))) rightHalfDigits++;
            else if (text.charAt(i) == '}' || text.charAt(i) == '{') readRightHalf = true;
            potentialLength++;
            i++;
        }
        if (!readRightHalf || rightHalfDigits == 0) hasRightHalf = false;

        if (hasRightHalf) length += potentialLength;

        return new Placeholder((length > 2 ? text.substring(inI, inI + length) : ""),
                (hasLeftHalf && hasRightHalf) ? PlaceholderType.TRIM : PlaceholderType.TRIM_SHORT);
    }

    private static int getAlternativeTrimmingPlaceholderLength(String text, int i) {
        int lI = i + 2;
        int leftGroupDigits = 0, operators = 0, rightGroupDigits = 0;
        boolean readLeftGroup = false, readRightGroup = false;

        while (!readLeftGroup) {
            if (lI >= text.length()) break;
            if (!Character.isDigit(text.charAt(lI)) && text.charAt(lI) != '+' && text.charAt(lI) != '-') return 0;
            else if (Character.isDigit(text.charAt(lI))) {
                leftGroupDigits++;
                lI++;
            }
            else if (text.charAt(lI) == '+' || text.charAt(lI) == '-') readLeftGroup = true;
        }
        if (readLeftGroup && leftGroupDigits == 0) return 0;

        while (operators < 3) {
            if (text.charAt(lI) == '+' || text.charAt(lI) == '-') operators++;
            else return 0;
            lI++;
        }

        while (!readRightGroup) {
            if (lI >= text.length()) break;
            if (!Character.isDigit(text.charAt(lI)) && text.charAt(lI) != '}') return 0;
            else if (Character.isDigit(text.charAt(lI))) rightGroupDigits++;
            else if (text.charAt(lI) == '}') readRightGroup = true;
            lI++;
        }
        if (readRightGroup && rightGroupDigits == 0) return 0;

        return lI - i;
    }

    public static ArrayList<AttachedLabelBreakdownResult> trimRawInfo(String label, String rawInfo) {
        ArrayList<AttachedLabelBreakdownResult> parts = plainifyEscapedPlaceholders(label);
        ArrayList<AttachedLabelBreakdownResult> trimmedInfoParts = new ArrayList<>();

        for (AttachedLabelBreakdownResult unspecifiedPart : parts) {
            if (!(unspecifiedPart instanceof Placeholder part)) {
                trimmedInfoParts.add(unspecifiedPart);
                continue;
            }

            String placeholder = part.getString();
            String infoPart;

            if (placeholder.equals("$") || placeholder.equals("{}")) infoPart = rawInfo;

            else if (placeholder.charAt(0) == '$' && placeholder.charAt(1) == '{' && placeholder.charAt(placeholder.length() - 1) == '}') {
                int i = 2;
                String leftGroupUnparsed = "", rightGroupUnparsed = "";
                int leftGroup, rightGroup;
                boolean preserveLeftGroup, preserveMiddleGroup, preserveRightGroup;

                while (Character.isDigit(placeholder.charAt(i))) {
                    leftGroupUnparsed = leftGroupUnparsed.concat("" + placeholder.charAt(i));
                    i++;
                }
                leftGroup = Integer.parseInt(leftGroupUnparsed);

                preserveLeftGroup = placeholder.charAt(i) == '+';
                preserveMiddleGroup = placeholder.charAt(i + 1) == '+';
                preserveRightGroup = placeholder.charAt(i + 2) == '+';
                i += 3;

                while (Character.isDigit(placeholder.charAt(i))) {
                    rightGroupUnparsed = rightGroupUnparsed.concat("" + placeholder.charAt(i));
                    i++;
                }
                rightGroup = Integer.parseInt(rightGroupUnparsed);

                String leftSub = rawInfo.substring(0, Math.min(leftGroup, rawInfo.length()));
                String rightSub = rawInfo.substring(Math.max(0, rawInfo.length() - rightGroup));
                String middleSub = rawInfo.length() > leftGroup + rightGroup ? rawInfo.substring(leftGroup, rawInfo.length() - rightGroup) : "";

                infoPart = (preserveLeftGroup ? leftSub : "") + (preserveMiddleGroup ? middleSub : "") + (preserveRightGroup ? rightSub : "");
            }

            else {
                int rL = rawInfo.length();
                int trimFromLeft = 0, trimFromRight = 0;
                int cI = placeholder.indexOf('$'), l = placeholder.length();
                char cF = placeholder.charAt(0), cL = placeholder.charAt(l - 1);

                if (cF == '{' || cF == '}')
                    trimFromLeft = Mth.clamp(0, Integer.parseInt(placeholder.substring(1, cI)), rL) * (cF == '{' ? 1 : -1);
                if (cL == '{' || cL == '}')
                    trimFromRight = Mth.clamp(0, Integer.parseInt(placeholder.substring(cI + 1, l - 1)), rL) * (cL == '}' ? 1 : -1);

                int trimFromBoth = trimFromLeft + trimFromRight;
                if (trimFromBoth > rL) trimFromRight -= (trimFromBoth - rL);

                String leftSub = rawInfo.substring(0, Math.max(0, -trimFromLeft));
                String rightSub = rawInfo.substring(rL - Math.max(0, -trimFromRight), rL);

                if (trimFromLeft < 0 && trimFromRight < 0) infoPart = leftSub + rightSub;
                else if (trimFromLeft < 0) infoPart = leftSub;
                else if (trimFromRight < 0) infoPart = rightSub;
                else if (trimFromLeft > 0 || trimFromRight > 0) infoPart = rawInfo.substring(trimFromLeft, rL - trimFromRight);
                else infoPart = rawInfo;
            }

            trimmedInfoParts.add(new Placeholder(infoPart, part.type()));
        }

        return trimmedInfoParts;
    }

    public static boolean removeEscapeBackslash(PlaceholderType type) {
        boolean isDollarSignPlaceholderEnabled = CIDLXConfigs.server.enableDollarPlaceholder.get();
        boolean isBracketsPlaceholderEnabled = CIDLXConfigs.server.enableBracketsPlaceholder.get();
        boolean isOriginalTrimmingPlaceholderEnabled = CIDLXConfigs.server.enableOriginalTrimmingPlaceholder.get();
        boolean isShortenedOriginalTrimmingPlaceholderEnabled = CIDLXConfigs.server.enableShortenedOriginalTrimmingPlaceholder.get();
        boolean isAlternativeTrimmingPlaceholderEnabled = CIDLXConfigs.server.enableAlternativeTrimmingPlaceholder.get();
        boolean isEscapingOfPlaceholdersEnabled = CIDLXConfigs.server.enableEscapingOfPlaceholders.get();
        boolean isEscapingOfDisabledPlaceholdersHidden = CIDLXConfigs.server.hideEscapingOfDisabledPlaceholders.get();

        return isEscapingOfPlaceholdersEnabled && (isEscapingOfDisabledPlaceholdersHidden || switch (type) {
            case DOLLAR -> isDollarSignPlaceholderEnabled;
            case BRACKETS -> isBracketsPlaceholderEnabled;
            case TRIM -> isOriginalTrimmingPlaceholderEnabled;
            case TRIM_SHORT -> isShortenedOriginalTrimmingPlaceholderEnabled;
            case TRIM_ALT -> isAlternativeTrimmingPlaceholderEnabled;
            default -> false;
        });
    }
}
