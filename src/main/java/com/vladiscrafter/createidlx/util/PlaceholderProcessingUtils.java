package com.vladiscrafter.createidlx.util;

import com.simibubi.create.content.trains.display.FlapDisplaySection;
import com.vladiscrafter.createidlx.config.CIDLXConfigs;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static com.vladiscrafter.createidlx.util.SingleLineDisplaySourceMixinUtils.*;

public class PlaceholderProcessingUtils {
    public static String setToPrimitivePlaceholder() {
        boolean isDollarSignPlaceholderEnabled = CIDLXConfigs.server.enableDollarPlaceholder.get();

        return (isDollarSignPlaceholderEnabled ? "$" : "{}");
    }

    public static String appendPrimitivePlaceholder(String label) {
        boolean isDollarSignPlaceholderEnabled = CIDLXConfigs.server.enableDollarPlaceholder.get();

        return label + " " + (isDollarSignPlaceholderEnabled ? "$" : "{}");
    }

    public static ArrayList<String> breakDownLabel(String label) {
        return processLabel(label).getLeft().getLeft();
    }

    public static ArrayList<String> extractPlaceholders(String label) {
        return processLabel(label).getLeft().getRight();
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
        ArrayList<String> sections = breakDownLabel(label);
        ArrayList<String> trimmedInfoParts = trimRawInfo(label, rawInfo);

        if (getCoveringPlaceholdersInLabel(label).getLeft()) addAction.accept(trimmedInfoParts.getFirst());
        for (int i = 0; i < sections.size(); i++) {
            addAction.accept(sections.get(i));
            if (sections.size() > i + 1 && trimmedInfoParts.size() >= i + 2) addAction.accept(trimmedInfoParts.get(i + 1));
        }
        if (getCoveringPlaceholdersInLabel(label).getRight()) addAction.accept(trimmedInfoParts.getLast());
    }

    public static ArrayList<FlapDisplaySection> breakDownAndAssembleLabelAsSectionList(String label, String rawInfo,
                                                                                       String layoutKey, float maxValueWidth, float valueWidthMod) {
        ArrayList<String> labelSections = breakDownLabel(label);
        ArrayList<String> infoSections = trimRawInfo(label, rawInfo);
        ArrayList<FlapDisplaySection> result = new ArrayList<>();
        int vI = 0;

        if (getCoveringPlaceholdersInLabel(label).getLeft()) {
            result.add(createValueSection(Math.min(infoSections.getFirst().length() * valueWidthMod, maxValueWidth),
                    layoutKey, infoSections.getFirst()));
            vI++;
        }

        if (!labelSections.isEmpty()) for (int i = 0; i < labelSections.size(); i++) {
            result.add(createLabelSection(labelSections.get(i)));
            if (labelSections.size() > i + 1) {
                result.add(createValueSection(Math.min(infoSections.get(vI).length() * valueWidthMod, maxValueWidth),
                        layoutKey, infoSections.get(vI)));
                vI++;
            }
        }
        if (getCoveringPlaceholdersInLabel(label).getRight() && vI < infoSections.size()) {
            result.add(createValueSection(Math.min(infoSections.getLast().length() * valueWidthMod, maxValueWidth),
                    layoutKey, infoSections.getLast()));
        }

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
        return processLabel(label).getLeft().getRight().toArray().length;
    }

    public static Pair<Boolean, Boolean> getCoveringPlaceholdersInLabel(String label) {
        return processLabel(label).getRight().getRight();
    }

    public static boolean hasEscapedPlaceholders(String label) {
        return processLabel(label).getRight().getRight().getLeft();
    }

    private static Pair<Pair<ArrayList<String>, ArrayList<String>>, Pair<Boolean, Pair<Boolean, Boolean>>> processLabel(String label) {
        boolean isEscapingOfPlaceholdersEnabled = CIDLXConfigs.server.enableEscapingOfPlaceholders.get();
        boolean isEscapingOfDisabledPlaceholdersHidden = CIDLXConfigs.server.hideEscapingOfDisabledPlaceholders.get();

        StringBuilder breakableLabel = new StringBuilder(label);
        ArrayList<String> labelParts = new ArrayList<>(), placeholders = new ArrayList<>();
        boolean hasEscapedPlaceholders = false;

        boolean mergeFirstTwoLabelParts = false;
        Placeholder startPlaceholder = getPlaceholder(breakableLabel.toString(), 0);
        boolean startsByPlaceholder = breakableLabel.isEmpty() || (startPlaceholder.length() > 0 && startPlaceholder.isActive());
        if (startsByPlaceholder && !breakableLabel.isEmpty()) {
            placeholders.add(breakableLabel.substring(0, startPlaceholder.length()));
            breakableLabel.delete(0, startPlaceholder.length());
        } else if (startPlaceholder.type() == PlaceholderType.ESCAPED) {
            labelParts.add(breakableLabel.substring(0, startPlaceholder.length()));
            breakableLabel.delete(0, startPlaceholder.length());
            mergeFirstTwoLabelParts = true;
        }

        boolean endsByPlaceholder = false;

        while (!breakableLabel.isEmpty()) {
            StringBuilder labelPart = new StringBuilder();
            boolean foundPlaceholder = false;

            for (int i = 0; i < breakableLabel.length(); i++) {
                Placeholder placeholder = getPlaceholder(breakableLabel.toString(), i);

                if (placeholder.length() > 0) {
                    if (placeholder.isActive()) {
                        labelParts.add(!labelPart.isEmpty() ? labelPart.toString() : "");
                        if (!label.isEmpty()) breakableLabel.delete(0, labelPart.toString().length());

                        foundPlaceholder = true;
                        placeholders.add(breakableLabel.substring(0, placeholder.length()));
                        breakableLabel.delete(0, placeholder.length());

                        if (breakableLabel.isEmpty()) endsByPlaceholder = true;

                        break;
                    } else if (placeholder.type() == PlaceholderType.ESCAPED) {
                        labelPart.append(breakableLabel, i, i + placeholder.length());
                        i += placeholder.length() - 1;
                    }
                } else if (placeholder.length() == 0) {
                    labelPart.append(breakableLabel.charAt(i));
                }
            }

            if (!foundPlaceholder) {
                labelParts.add(breakableLabel.toString());
                breakableLabel.setLength(0);
            }
        }

        if (mergeFirstTwoLabelParts) labelParts.set(0, labelParts.getFirst().concat(labelParts.remove(1)));

        if (isEscapingOfPlaceholdersEnabled) {
            for (int p = 0; p < labelParts.size(); p++) {
                String part = labelParts.get(p);

                if (removeEscapeBackslash(PlaceholderType.DOLLAR)) part = part.replace("\\$", "$");
                if (removeEscapeBackslash(PlaceholderType.BRACKETS)) part = part.replace("\\{}", "{}");

                if (removeEscapeBackslash(PlaceholderType.TRIM))
                    part = part.replaceAll("\\\\(?=[{}]\\d+\\$\\d+[}{])", "");

                if (removeEscapeBackslash(PlaceholderType.TRIM_SHORT))
                    part = part.replaceAll("\\\\(?=[{}]\\d+\\$)", "")
                            .replaceAll("\\\\(?=\\$\\d+[}{])", "");

                if (removeEscapeBackslash(PlaceholderType.TRIM_ALT))
                    part = part.replaceAll("\\\\(?=\\$\\{\\d+[+-]{3}\\d+})", "");

                labelParts.set(p, part);
            }
        }
        /*log("labelParts: " + labelParts);
        log("placeholders: " + placeholders);*/

        return Pair.of(Pair.of(labelParts, placeholders), Pair.of(hasEscapedPlaceholders, Pair.of(startsByPlaceholder, endsByPlaceholder)));
    }

    public static Placeholder getPlaceholder(String text, int i) {
        boolean isDollarSignPlaceholderEnabled = CIDLXConfigs.server.enableDollarPlaceholder.get();
        boolean isBracketsPlaceholderEnabled = CIDLXConfigs.server.enableBracketsPlaceholder.get();
        boolean isOriginalTrimmingPlaceholderEnabled = CIDLXConfigs.server.enableOriginalTrimmingPlaceholder.get();
        boolean isShortenedOriginalTrimmingPlaceholderEnabled = CIDLXConfigs.server.enableShortenedOriginalTrimmingPlaceholder.get();
        boolean isAlternativeTrimmingPlaceholderEnabled = CIDLXConfigs.server.enableAlternativeTrimmingPlaceholder.get();
        boolean isEscapingOfPlaceholdersEnabled = CIDLXConfigs.server.enableEscapingOfPlaceholders.get();

        int length = invalid.length;
        PlaceholderType type = invalid.type;
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

        if (isEscaped) {
            if (length == 1) return invalid;
            else type = PlaceholderType.ESCAPED;
        }

        if ((type == PlaceholderType.DOLLAR && !isDollarSignPlaceholderEnabled)
                || (type == PlaceholderType.BRACKETS && !isBracketsPlaceholderEnabled)
                || (type == PlaceholderType.TRIM && !isOriginalTrimmingPlaceholderEnabled)
                || (type == PlaceholderType.TRIM_SHORT && !isShortenedOriginalTrimmingPlaceholderEnabled)
                || (type == PlaceholderType.TRIM_ALT && !isAlternativeTrimmingPlaceholderEnabled)) {
            type = PlaceholderType.DISABLED;
        }

        return new Placeholder(length, type);
    }

    private static Placeholder getOriginalTrimmingPlaceholder(String text, int i) {
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

        return new Placeholder((length > 2 ? length : 0), (hasLeftHalf && hasRightHalf) ? PlaceholderType.TRIM : PlaceholderType.TRIM_SHORT);
    }

    private static int getAlternativeTrimmingPlaceholderLength(String text, int i) {;
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

    public static ArrayList<String> trimRawInfo(String label, String rawInfo) {
        ArrayList<String> placeholders = extractPlaceholders(label);
        ArrayList<String> trimmedInfoParts = new ArrayList<>();

        for (String placeholder : placeholders) {
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

            trimmedInfoParts.add(infoPart);
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

    public enum PlaceholderType { DOLLAR, BRACKETS, TRIM, TRIM_SHORT, TRIM_ALT, ESCAPED, DISABLED, INVALID }

    static Placeholder invalid = new Placeholder(0, PlaceholderType.INVALID);

    public record Placeholder(int length, PlaceholderType type) {
        public Pair<Integer, PlaceholderType> asPair() {
            return Pair.of(length, type);
        }

        public boolean isActive() {
            return type != PlaceholderType.ESCAPED && type != PlaceholderType.DISABLED && type != PlaceholderType.INVALID;
        }
    }
}
