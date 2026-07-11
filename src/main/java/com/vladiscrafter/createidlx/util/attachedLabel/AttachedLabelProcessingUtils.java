package com.vladiscrafter.createidlx.util.attachedLabel;

import com.simibubi.create.content.trains.display.FlapDisplaySection;
import com.vladiscrafter.createidlx.config.CIDLXConfigs;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.vladiscrafter.createidlx.util.attachedLabel.AttachedLabelPart.*;
import static com.vladiscrafter.createidlx.util.attachedLabel.AttachedLabelPart.PlaceholderType.*;
import static com.vladiscrafter.createidlx.util.attachedLabel.AttachedLabelPart.CaptureGroupOperation.*;
import static com.vladiscrafter.createidlx.util.attachedLabel.AttachedLabelPart.CaptureGroupRounding.*;
import static com.vladiscrafter.createidlx.util.SingleLineDisplaySourceMixinUtils.*;

@SuppressWarnings({"UnusedAssignment", "UnnecessaryLocalVariable", "ExtractMethodRecommender"})
public class AttachedLabelProcessingUtils {
    private static final Pattern CG_ALLOWED = Pattern.compile("\\d|\\.|/|%");

    private static final Pattern INT_PATTERN = Pattern.compile("^\\d+$");
    private static final Pattern FLOAT_PATTERN = Pattern.compile("^\\d?\\.\\d+$");
    private static final Pattern FRACTION_PATTERN = Pattern.compile("^\\d*/[1-9]+$");
    private static final Pattern PERCENT_PATTERN = Pattern.compile("^\\d+%$");

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
        ArrayList<AttachedLabelBreakdownResult> sections = moldInfoPartsViaPlaceholders(label, rawInfo);

        for (AttachedLabelBreakdownResult section : sections) {
            addAction.accept(section.getString());
        }
    }

    public static ArrayList<FlapDisplaySection> breakDownAndAssembleLabelAsSectionList(String label, String rawInfo,
                                                                                       String layoutKey, float maxValueWidth, float valueWidthMod) {
        ArrayList<AttachedLabelBreakdownResult> sections = moldInfoPartsViaPlaceholders(label, rawInfo);
        ArrayList<FlapDisplaySection> result = new ArrayList<>();

        if (!sections.isEmpty()) for (AttachedLabelBreakdownResult section : sections)
            result.add(section instanceof ProcessedInfoPart
                    ? createValueSection(Math.min(section.length() * valueWidthMod, maxValueWidth), layoutKey, section.getString())
                    : createLabelSection(section.getString()));

        return result;
    }

    public static List<MutableComponent> assembleLabelFromSectionsAsComponentList(ArrayList<FlapDisplaySection> sections) {
        List<MutableComponent> result = new ArrayList<>();

        for (FlapDisplaySection section : sections) result.add(Component.literal("").append(section.getText()));

        return result; // TODO: rename this one and others above accordingly
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

                if (removeEscapeBackslash(DOLLAR)) part = part.replace("\\$", "$");
                if (removeEscapeBackslash(BRACKETS)) part = part.replace("\\{}", "{}");

                if (removeEscapeBackslash(TRIM)) part = part.replaceAll("\\\\(?=[{}]\\d+\\$\\d+[}{])", "");

                if (removeEscapeBackslash(TRIM_SHORT)) part = part.replaceAll("\\\\(?=[{}]\\d+\\$)", "")
                            .replaceAll("\\\\(?=\\$\\d+[}{])", "");

                if (removeEscapeBackslash(TRIM_ALT)) part = part.replaceAll("\\\\(?=\\$\\{\\d+[+-]{3}\\d+})", "");

                brokenDownLabel.set(p, new PlainPart(part));
            }
        }

        /*log("\nProcessed label:\n\n- " + brokenDownLabel.stream()
                .map(AttachedLabelBreakdownResult::asDebugString).collect(Collectors.joining("\n- ")));*/

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
        PlaceholderProperties properties = invalidPr;
        boolean isEscaped = false;

        if (text.isEmpty()) return invalid;

        if (text.length() > i + 1 && text.charAt(i) == '\\' && isEscapingOfPlaceholdersEnabled) {
            isEscaped = true;
            length++;
            i++;
        }

        if (text.length() > i + 7 && text.charAt(i) == '$' && text.charAt(i + 1) == '{' && text.charAt(i + 2) != '}') {
            Placeholder alt = getAlternativeTrimmingPlaceholder(text, i);
            if (alt.length() > 0) {
                length += alt.length();
                type = alt.type();
                properties = alt.properties();
            }
        } else if (text.charAt(i) == '$' || text.charAt(i) == '}' || text.charAt(i) == '{') {
            Placeholder original = getOriginalTrimmingPlaceholder(text, i);
            int origLength = original.length();
            if (origLength > 2) {
                length += origLength;
                type = original.type();
                properties = original.properties();
            } else {
                if (text.charAt(i) == '$') {
                    length += 1;
                    type = DOLLAR;
                } else if (text.charAt(i) == '{' && (text.length() > i + 1 && text.charAt(i + 1) == '}')) {
                    length += 2;
                    type = BRACKETS;
                }
            }
        }

        if ((type == DOLLAR && !isDollarSignPlaceholderEnabled)
                || (type == BRACKETS && !isBracketsPlaceholderEnabled)
                || (type == TRIM && !isOriginalTrimmingPlaceholderEnabled)
                || (type == TRIM_SHORT && !isShortenedOriginalTrimmingPlaceholderEnabled)
                || (type == TRIM_ALT && !isAlternativeTrimmingPlaceholderEnabled))
            type = DISABLED;

        if (isEscaped) {
            if (length == 1) return invalid;
            else type = type == DISABLED ? ESCAPED_DISABLED : ESCAPED;
        }

        return new Placeholder(text.substring(inI, inI + length), type, properties);
    }

    private static Placeholder getOriginalTrimmingPlaceholder(String text, int i) {
        int inI = i;
        int length = 0;
        String raw = "";
        boolean hasLeftHalf = true, hasRightHalf = true;
        boolean readLeftHalf = false, readRightHalf = false;
        PlaceholderType type = TRIM;

        Number leftGroupValue = 0, rightGroupValue = 0;
        CaptureGroupOperation leftGroupOperation = NONE, middleGroupOperation = NONE, rightGroupOperation = NONE;
        CaptureGroupRounding leftGroupRounding = AUTO, rightGroupRounding = AUTO;

        if (text.charAt(i) != '$' && text.charAt(i) != '{' && text.charAt(i) != '}') return invalid;
        else if (text.charAt(i) == '$') hasLeftHalf = false;
        else leftGroupOperation = text.charAt(i) == '{' ? CUT : PRESERVE;
        length++;
        i++;

        String leftGroupRaw = "";
        while (!readLeftHalf && hasLeftHalf) {
            if (i >= text.length()) return invalid;
            if (!CG_ALLOWED.matcher("" + text.charAt(i)).matches() && text.charAt(i) != '$') return invalid;
            else if (CG_ALLOWED.matcher("" + text.charAt(i)).matches()) leftGroupRaw = leftGroupRaw.concat("" + text.charAt(i));
            else if (text.charAt(i) == '$') readLeftHalf = true;
            length++;
            i++;
        }
        if (readLeftHalf && leftGroupRaw.isEmpty()) return invalid;
        if (!leftGroupRaw.isEmpty()) {
            leftGroupValue = resolveCaptureGroupValue(leftGroupRaw);
            if (leftGroupValue.intValue() == -1) return invalid;
            if (leftGroupValue instanceof Float && leftGroupValue.floatValue() > 1.0) return invalid;
        }
        /*log(String.format("\n[orig] left CG parsed: '%s' -> %s", leftGroupRaw, leftGroupValue));*/

        int potentialLength = 0;

        String rightGroupRaw = "";
        while (!readRightHalf) {
            if (i >= text.length()) break;
            if (!CG_ALLOWED.matcher("" + text.charAt(i)).matches() && text.charAt(i) != '}' && text.charAt(i) != '{') break;
            else if (CG_ALLOWED.matcher("" + text.charAt(i)).matches()) rightGroupRaw = rightGroupRaw.concat("" + text.charAt(i));
            else if (text.charAt(i) == '}' || text.charAt(i) == '{') {
                readRightHalf = true;
                rightGroupOperation = text.charAt(i) == '}' ? CUT : PRESERVE;
            }
            potentialLength++;
            i++;
        }
        if (!readRightHalf || rightGroupRaw.isEmpty()) hasRightHalf = false;
        if (!rightGroupRaw.isEmpty()) {
            rightGroupValue = resolveCaptureGroupValue(rightGroupRaw);
            if (rightGroupValue.intValue() == -1) return invalid;
            if (rightGroupValue instanceof Float && rightGroupValue.floatValue() > 1.0) return invalid;
        }
        /*log(String.format("[orig] right CG parsed: '%s' -> %s\n", rightGroupRaw, rightGroupValue));*/

        if (hasRightHalf) length += potentialLength;

        middleGroupOperation = (leftGroupOperation == PRESERVE || rightGroupOperation == PRESERVE) ? CUT : PRESERVE;

        if (length > 2) raw = text.substring(inI, inI + length);
        if (!hasLeftHalf || !hasRightHalf) type = TRIM_SHORT;

        CaptureGroup leftGroup = new CaptureGroup(leftGroupValue, leftGroupOperation);
        CaptureGroup rightGroup = new CaptureGroup(rightGroupValue, rightGroupOperation);
        PlaceholderProperties properties = new PlaceholderProperties(leftGroup, middleGroupOperation, rightGroup);

        return new Placeholder(raw, type, properties);
    }

    private static Placeholder getAlternativeTrimmingPlaceholder(String text, int i) {
        int lI = i + 2;
        boolean readLeftGroup = false, readRightGroup = false;
        PlaceholderType type = TRIM_ALT;

        Number leftGroupValue = 0, rightGroupValue = 0;
        CaptureGroupOperation[] operations = new CaptureGroupOperation[3];
        CaptureGroupRounding leftGroupRounding = AUTO, rightGroupRounding = AUTO;

        String leftGroupRaw = "";
        while (!readLeftGroup) {
            if (lI >= text.length()) break;
            if (!CG_ALLOWED.matcher("" + text.charAt(lI)).matches() && text.charAt(lI) != '+' && text.charAt(lI) != '-') return invalid;
            else if (CG_ALLOWED.matcher("" + text.charAt(lI)).matches()) {
                leftGroupRaw = leftGroupRaw.concat("" + text.charAt(lI));
                lI++;
            }
            else if (text.charAt(lI) == '+' || text.charAt(lI) == '-') readLeftGroup = true;
        }
        if (readLeftGroup && leftGroupRaw.isEmpty()) return invalid;
        else {
            leftGroupValue = resolveCaptureGroupValue(leftGroupRaw);
            if (leftGroupValue.intValue() == -1) return invalid;
            if (leftGroupValue instanceof Float && leftGroupValue.floatValue() > 1.0) return invalid;
        }
        /*log(String.format("\n[alt] left CG parsed: '%s' -> %s", leftGroupRaw, leftGroupValue));*/

        for (int oI = 0; oI < 3; oI++) {
            operations[oI] = text.charAt(lI) == '+' ? PRESERVE : text.charAt(lI) == '-' ? CUT : NONE;
            if (operations[oI] == NONE) return invalid;
            lI++;
        }
        CaptureGroupOperation leftGroupOperation = operations[0], middleGroupOperation = operations[1], rightGroupOperation = operations[2];

        String rightGroupRaw = "";
        while (!readRightGroup) {
            if (lI >= text.length()) break;
            if (!CG_ALLOWED.matcher("" + text.charAt(lI)).matches() && text.charAt(lI) != '}') return invalid;
            else if (CG_ALLOWED.matcher("" + text.charAt(lI)).matches()) rightGroupRaw = rightGroupRaw.concat("" + text.charAt(lI));
            else if (text.charAt(lI) == '}') readRightGroup = true;
            lI++;
        }
        if (readRightGroup && rightGroupRaw.isEmpty()) return invalid;
        else {
            rightGroupValue = resolveCaptureGroupValue(rightGroupRaw);
            if (rightGroupValue.intValue() == -1) return invalid;
            if (rightGroupValue instanceof Float && rightGroupValue.floatValue() > 1.0) return invalid;
        }
        /*log(String.format("[alt] right CG parsed: '%s' -> %s\n", rightGroupRaw, rightGroupValue));*/

        CaptureGroup leftGroup = new CaptureGroup(leftGroupValue, leftGroupOperation);
        CaptureGroup rightGroup = new CaptureGroup(rightGroupValue, rightGroupOperation);
        PlaceholderProperties properties = new PlaceholderProperties(leftGroup, middleGroupOperation, rightGroup);

        return new Placeholder(text.substring(i, lI), type, properties);
    }

    public static Number resolveCaptureGroupValue(String raw) {
        if (INT_PATTERN.matcher(raw).matches()) return Integer.parseInt(raw);
        else if (FLOAT_PATTERN.matcher(raw).matches()) return Float.parseFloat(raw);
        else if (FRACTION_PATTERN.matcher(raw).matches())
            return (float) ((!raw.substring(0, raw.indexOf('/')).isEmpty()) ? Integer.parseInt(raw.substring(0, raw.indexOf('/'))) : 1)
                    / Integer.parseInt(raw.substring(raw.indexOf('/') + 1));
        else if (PERCENT_PATTERN.matcher(raw).matches())
            return (float) Integer.parseInt(raw.substring(0, raw.indexOf('%'))) / 100;
        else return -1;
    }

    public static ArrayList<AttachedLabelBreakdownResult> moldInfoPartsViaPlaceholders(String label, String rawInfo) {
        ArrayList<AttachedLabelBreakdownResult> parts = plainifyEscapedPlaceholders(label);
        ArrayList<AttachedLabelBreakdownResult> trimmedInfoParts = new ArrayList<>();

        for (AttachedLabelBreakdownResult part : parts) {
            if (!(part instanceof Placeholder placeholder)) {
                trimmedInfoParts.add(part);
                continue;
            }

            if (placeholder.isPrimitive()) {
                trimmedInfoParts.add(new ProcessedInfoPart(rawInfo));
                continue;
            }

            PlaceholderType type = placeholder.type();
            PlaceholderProperties pr = placeholder.properties();
            CaptureGroup leftGroup = pr.leftGroup(), rightGroup = pr.rightGroup();
            int leftGroupValue = unpackCaptureGroupValue(leftGroup, rawInfo), rightGroupValue = unpackCaptureGroupValue(rightGroup, rawInfo);
            CaptureGroupOperation leftGroupOperation = leftGroup.operation(),
                    middleGroupOperation = pr.middleGroupOperation(),
                    rightGroupOperation = rightGroup.operation();

            int rL = rawInfo.length();

            String leftSub = rawInfo.substring(0, Math.min(leftGroupValue, rL));
            String middleSub = rL > leftGroupValue + rightGroupValue ? rawInfo.substring(leftGroupValue, rL - rightGroupValue) : "";
            String rightSub = rawInfo.substring(Math.max(0, type == TRIM_ALT ? Math.max(rL - rightGroupValue, leftGroupValue) : rL - rightGroupValue));
            // TODO 1: per-placeholder toggleable bifurcation (with some kind of ':' modifier, perhaps)
            // TODO 2: one side's PRESERVE should respect the other's CUT (also toggleable?)

            String infoPart = ""
                    .concat(leftGroupOperation == PRESERVE ? leftSub : "")
                    .concat(middleGroupOperation == PRESERVE ? middleSub : "")
                    .concat(rightGroupOperation == PRESERVE ? rightSub : "");

            trimmedInfoParts.add(new ProcessedInfoPart(infoPart));
        }

        return trimmedInfoParts;
    }

    public static int unpackCaptureGroupValue(CaptureGroup captureGroup, String rawInfo) {
        return captureGroup.value() instanceof Float
                ? Math.round(captureGroup.value().floatValue() * rawInfo.length())
                : captureGroup.value().intValue();
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
