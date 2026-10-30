package com.vladiscrafter.createidlx.util.attachedLabel;

import com.simibubi.create.content.trains.display.FlapDisplaySection;
import com.vladiscrafter.createidlx.config.CIDLXConfigs;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.apache.commons.lang3.math.Fraction;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.vladiscrafter.createidlx.util.attachedLabel.AttachedLabelPart.*;
import static com.vladiscrafter.createidlx.util.attachedLabel.AttachedLabelPart.PlaceholderType.*;
import static com.vladiscrafter.createidlx.util.attachedLabel.AttachedLabelPart.PlaceholderState.*;
import static com.vladiscrafter.createidlx.util.attachedLabel.AttachedLabelPart.CaptureGroupOperation.*;
import static com.vladiscrafter.createidlx.util.attachedLabel.AttachedLabelPart.CaptureGroupRounding.*;
import static com.vladiscrafter.createidlx.util.SingleLineDisplaySourceMixinUtils.*;

@SuppressWarnings({"UnusedAssignment", "UnnecessaryLocalVariable", "BooleanMethodIsAlwaysInverted"})
public class AttachedLabelProcessingUtils {
    private static final Pattern CG_ALLOWED = Pattern.compile("[+\\-\\d./%CcFfUuDdAa]");
    private static final Pattern CG_ALLOWED_ALT = Pattern.compile("[\\d./%CcFfUuDdAa]");

    private static final Pattern ROUNDED_CG_PATTERN = Pattern.compile("^[+CcFfUuDdAa-]?[^+A-Za-z-]+[+CcFfUuDdAa-]?$");
    private static final Pattern ROUNDED_CG_ALT_PATTERN = Pattern.compile("^[CcFfUuDdAa]?[^A-Za-z]+[CcFfUuDdAa]?$");

    private static final Pattern INT_PATTERN = Pattern.compile("^\\d+$");
    private static final Pattern FR_DECIMAL_PATTERN = Pattern.compile("^\\d?\\.\\d+$");
    private static final Pattern FR_PERCENT_PATTERN = Pattern.compile("^\\d+%$");
    private static final Pattern FR_PROPER_PATTERN = Pattern.compile("^\\d*/[1-9]\\d*$");

    public static String setToPrimitivePlaceholder() {
        boolean isDollarSignPlaceholderEnabled = CIDLXConfigs.server.enableDollarPlaceholder.get();

        return (isDollarSignPlaceholderEnabled ? "$" : "{}");
    }

    public static String appendPrimitivePlaceholder(String label) {
        boolean isDollarSignPlaceholderEnabled = CIDLXConfigs.server.enableDollarPlaceholder.get();

        return label + " " + (isDollarSignPlaceholderEnabled ? "$" : "{}");
    }

    public static LinkedHashMap<Integer, Placeholder> mapPlaceholders(String label) {
        ArrayList<AttachedLabelBreakdownResult> breakdown = processLabel(label);
        LinkedHashMap<Integer, Placeholder> result = new LinkedHashMap<>();
        int index = 0;

        for (AttachedLabelBreakdownResult part : breakdown) {
            if (part instanceof Placeholder) result.put(index, (Placeholder) part);
            index += part.length();
        }

        return result;
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

    public static List<MutableComponent> assembleLabelFromSectionsAsComponentList(List<FlapDisplaySection> sections) {
        ArrayList<MutableComponent> result = new ArrayList<>();

        for (FlapDisplaySection section : sections) result.add(Component.literal("").append(section.getText()));

        return result; // TODO: rename this one and others above accordingly
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean shouldBeProcessed(String label) { // TODO: doesn't get called for some reason?
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
            if (part instanceof Placeholder placeholder && placeholder.isEscaped()) {
                hasEscapedPlaceholders = true;
                break;
            }

        return hasEscapedPlaceholders;
    }

    private static ArrayList<AttachedLabelBreakdownResult> processLabel(String label) {
        boolean isEscapingOfPlaceholdersEnabled = CIDLXConfigs.server.enableEscapingOfPlaceholders.get();

        StringBuilder breakableLabel = new StringBuilder(label);
        ArrayList<AttachedLabelBreakdownResult> brokenDownLabel = new ArrayList<>();

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

                if (removeEscapeBackslash(TRIM)) part = part.replaceAll("\\\\(?=[{}][\\d./%CcFfUuDdAa]+\\$[\\d./%CcFfUuDdAa]+[}{])", "");

                if (removeEscapeBackslash(TRIM_SHORT)) part = part.replaceAll("\\\\(?=[{}][\\d./%CcFfUuDdAa]+\\$)", "")
                            .replaceAll("\\\\(?=\\$[\\d./%CcFfUuDdAa]+[}{])", "");

                if (removeEscapeBackslash(TRIM_ALT)) part = part.replaceAll("\\\\(?=\\$\\{[\\d./%CcFfUuDdAa]+[+-]{3}\\d+})", "");

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

        if (text.isEmpty()) return invalid.copy();

        int inI = i;
        int length = 0;
        PlaceholderType type = INVALID;
        PlaceholderState state = UNIDENTIFIED;
        PlaceholderProperties properties = defaultPr.copy();
        boolean isEscaped = false;

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

        if (type != INVALID) state = ACTIVE;

        if ((type == DOLLAR && !isDollarSignPlaceholderEnabled)
                || (type == BRACKETS && !isBracketsPlaceholderEnabled)
                || (type == TRIM && !isOriginalTrimmingPlaceholderEnabled)
                || (type == TRIM_SHORT && !isShortenedOriginalTrimmingPlaceholderEnabled)
                || (type == TRIM_ALT && !isAlternativeTrimmingPlaceholderEnabled))
            state = DISABLED;

        if (isEscaped) {
            if (length == 1) return invalid.copy();
            else state = state == DISABLED ? ESCAPED_DISABLED : ESCAPED;
        }

        return new Placeholder(text.substring(inI, inI + length), type, state, properties);
    }

    private static Placeholder getOriginalTrimmingPlaceholder(String text, int i) {
        int inI = i;
        int length = 0;
        String raw = "";
        boolean hasLeftHalf = true, hasRightHalf = true;
        boolean readLeftHalf = false, readRightHalf = false;
        PlaceholderType type = TRIM;

        CaptureGroup leftGroup = defaultCG.copy(), rightGroup = defaultCG.copy();
        CaptureGroupOperation leftGroupOperation = NONE, middleGroupOperation = NONE, rightGroupOperation = NONE;

        if (text.charAt(i) != '$' && text.charAt(i) != '{' && text.charAt(i) != '}') return invalid.copy();
        else if (text.charAt(i) == '$') hasLeftHalf = false;
        else leftGroupOperation = text.charAt(i) == '{' ? CUT : PRESERVE;
        length++;
        i++;

        String leftGroupRaw = "";
        while (!readLeftHalf && hasLeftHalf) {
            if (i >= text.length()) return invalid.copy();
            if (!CG_ALLOWED.matcher("" + text.charAt(i)).matches() && text.charAt(i) != '$') return invalid.copy();
            else if (CG_ALLOWED.matcher("" + text.charAt(i)).matches()) leftGroupRaw = leftGroupRaw.concat("" + text.charAt(i));
            else if (text.charAt(i) == '$') readLeftHalf = true;
            length++;
            i++;
        }
        if (readLeftHalf && leftGroupRaw.isEmpty()) return invalid.copy();
        if (hasLeftHalf && !hasValidRounding(leftGroupRaw, type)) return invalid.copy();
        String leftGroupRawNR = cleanUpCaptureGroupRounders(leftGroupRaw);
        if (!leftGroupRawNR.isEmpty()) {
            leftGroup = assembleCaptureGroup(leftGroupRaw, TRIM, leftGroupOperation);
            if (leftGroup.isInvalid()) return invalid.copy();
        }
        /*log(String.format("\n[orig] left CG parsed: '%s' -> %s", leftGroupRaw, leftGroup.asDebugString()));*/

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
        if (hasRightHalf && !hasValidRounding(rightGroupRaw, type)) return invalid.copy();
        String rightGroupRawNR = cleanUpCaptureGroupRounders(rightGroupRaw);
        if (!rightGroupRawNR.isEmpty()) {
            rightGroup = assembleCaptureGroup(rightGroupRaw, TRIM, rightGroupOperation);
            if (rightGroup.isInvalid()) return invalid.copy();
        }
        /*log(String.format("[orig] right CG parsed: '%s' -> %s\n", rightGroupRaw, rightGroup.asDebugString()));*/

        if (hasRightHalf) length += potentialLength;

        middleGroupOperation = (leftGroupOperation == PRESERVE || rightGroupOperation == PRESERVE) ? CUT : PRESERVE;

        if (length > 2) raw = text.substring(inI, inI + length);
        if (!hasLeftHalf || !hasRightHalf) type = TRIM_SHORT;

        PlaceholderProperties properties = new PlaceholderProperties(leftGroup, middleGroupOperation, rightGroup);

        return new Placeholder(raw, type, ACTIVE, properties);
    }

    private static Placeholder getAlternativeTrimmingPlaceholder(String text, int i) {
        int lI = i + 2;
        boolean readLeftGroup = false, readRightGroup = false;
        PlaceholderType type = TRIM_ALT;

        CaptureGroup leftGroup = defaultCG, rightGroup = defaultCG;
        CaptureGroupOperation[] operations = new CaptureGroupOperation[3];

        String leftGroupRaw = "";
        while (!readLeftGroup) {
            if (lI >= text.length()) break;
            if (!CG_ALLOWED.matcher("" + text.charAt(lI)).matches()) return invalid.copy();
            else if (CG_ALLOWED_ALT.matcher("" + text.charAt(lI)).matches()) {
                leftGroupRaw = leftGroupRaw.concat("" + text.charAt(lI));
                lI++;
            }
            else if (text.charAt(lI) == '+' || text.charAt(lI) == '-') readLeftGroup = true;
        }
        if (!hasValidRounding(leftGroupRaw, type)) return invalid.copy();
        String leftGroupRawNR = cleanUpCaptureGroupRounders(leftGroupRaw);
        if (readLeftGroup && leftGroupRawNR.isEmpty()) return invalid.copy();
        else {
            leftGroup = assembleCaptureGroup(leftGroupRaw, TRIM_ALT, NONE);
            if (leftGroup.isInvalid()) return invalid.copy();
        }
        /*log(String.format("\n[alt] left CG parsed: '%s' -> %s", leftGroupRaw, leftGroupValue));*/

        for (int oI = 0; oI < 3; oI++) {
            if (lI >= text.length()) return invalid.copy();
            operations[oI] = text.charAt(lI) == '+' ? PRESERVE : text.charAt(lI) == '-' ? CUT : NONE;
            if (operations[oI] == NONE) return invalid.copy();
            lI++;
        }
        CaptureGroupOperation leftGroupOperation = operations[0], middleGroupOperation = operations[1], rightGroupOperation = operations[2];
        leftGroup.setOperation(leftGroupOperation);

        String rightGroupRaw = "";
        while (!readRightGroup) {
            if (lI >= text.length()) return invalid.copy();
            if (!CG_ALLOWED.matcher("" + text.charAt(lI)).matches() && text.charAt(lI) != '}') return invalid.copy();
            else if (CG_ALLOWED_ALT.matcher("" + text.charAt(lI)).matches()) rightGroupRaw = rightGroupRaw.concat("" + text.charAt(lI));
            else if (text.charAt(lI) == '}') readRightGroup = true;
            lI++;
        }
        if (!hasValidRounding(rightGroupRaw, type)) return invalid.copy();
        String rightGroupRawNR = cleanUpCaptureGroupRounders(rightGroupRaw);
        if (readRightGroup && rightGroupRawNR.isEmpty()) return invalid.copy();
        else {
            rightGroup = assembleCaptureGroup(rightGroupRaw, TRIM_ALT, rightGroupOperation);
            if (rightGroup.isInvalid()) return invalid.copy();
        }
        /*log(String.format("[alt] right CG parsed: '%s' -> %s\n", rightGroupRaw, rightGroupValue));*/

        PlaceholderProperties properties = new PlaceholderProperties(leftGroup, middleGroupOperation, rightGroup);

        return new Placeholder(text.substring(i, lI), type, ACTIVE, properties);
    }

    private static boolean hasValidRounding(String raw, PlaceholderType type) {
        boolean result = type == TRIM
                ? ROUNDED_CG_PATTERN.matcher(raw).matches()
                : ROUNDED_CG_ALT_PATTERN.matcher(raw).matches();

        /*log(String.format("\n[hasValidRounding] CG %s for type %s is %svalid", raw, type, result ? "" : "NOT "));*/
        return result;
    }
    
    private static boolean matches(char a, char b) {
        return Character.toUpperCase(a) == b;
    }

    private static String cleanUpCaptureGroupRounders(String raw) {
        String result = raw.toUpperCase()
                .replace("+", "").replace("-", "")
                .replace("C", "").replace("F", "")
                .replace("U", "").replace("D", "")
                .replace("A", "");

        /*log(String.format("[cleanUpCaptureGroupRounders] %s -> %s\n", raw, result));*/
        return result;
    }

    private static CaptureGroup assembleCaptureGroup(String raw, PlaceholderType type, CaptureGroupOperation operation) {
        CaptureGroupRounding rounding = AUTO;
        for (char ch : raw.toCharArray()) {
            if (type == TRIM) rounding = ch == '+' ? CEIL : ch == '-' ? FLOOR : matches(ch, 'A') ? AUTO : rounding;
            rounding = (matches(ch, 'C') || matches(ch, 'U'))
                    ? CEIL : (matches(ch, 'F') || matches(ch, 'D'))
                    ? FLOOR : matches(ch, 'A') ? AUTO : rounding;
            /*log(String.format("[resolveCaptureGroupRounding] Checked char %s, rounding is %s", ch, rounding));*/
        }
        /*log(String.format("[resolveCaptureGroupRounding] CGR of %s for type %s is %s", raw, type, rounding));*/

        raw = cleanUpCaptureGroupRounders(raw);

        Number value = 0;
        CaptureGroupValueType valueType = CaptureGroupValueType.CHARS;

        if (INT_PATTERN.matcher(raw).matches()) value = Integer.parseInt(raw);
        else if (FR_DECIMAL_PATTERN.matcher(raw).matches()) {
            value = new BigDecimal(raw);
            valueType = CaptureGroupValueType.FR_DECIMAL;
        }
        else if (FR_PERCENT_PATTERN.matcher(raw).matches()) {
            value = new BigDecimal(raw.substring(0, raw.indexOf('%'))).movePointLeft(2);
            valueType = CaptureGroupValueType.FR_PERCENT;
        }
        else if (FR_PROPER_PATTERN.matcher(raw).matches()) {
            value = Fraction.getFraction(((!raw.substring(0, raw.indexOf('/')).isEmpty()) ? Integer.parseInt(raw.substring(0, raw.indexOf('/'))) : 1), (int) (Integer.parseInt(raw.substring(raw.indexOf('/') + 1))));
            valueType = CaptureGroupValueType.FR_PROPER;
        }
        else value = -1;

        return new CaptureGroup(valueType, value, operation, rounding);
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
            String rightSub = rawInfo.substring(Math.clamp(type == TRIM_ALT ? Math.max(rL - rightGroupValue, leftGroupValue) : rL - rightGroupValue, 0, rL));
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
        Number value = captureGroup.value();
        float fraction = value.floatValue() * rawInfo.length();
        return !(value instanceof Integer)
                ? (int) (captureGroup.rounding() == CEIL
                    ? Math.ceil(fraction) : captureGroup.rounding() == FLOOR
                        ? Math.floor(fraction) : Math.round(fraction))
                : value.intValue();
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

    public static String constructRawCaptureGroup(CaptureGroup captureGroup, boolean alt, boolean left) {
        String v = captureGroup.valueType() == CaptureGroupValueType.FR_PERCENT
        ? ((BigDecimal) captureGroup.value()).multiply(BigDecimal.valueOf(100)).stripTrailingZeros().toPlainString() : captureGroup.value().toString();
        String valuePart = v + (captureGroup.valueType() == CaptureGroupValueType.FR_PERCENT ? "%" : "");
        String rounder = alt ? captureGroup.rounding().name().charAt(0) + "" : switch (captureGroup.rounding()) {
            case CEIL -> "+";
            case FLOOR -> "-";
            default -> "";
        };

        return String.format((left ? "%2$s%1$s" : "%1$s%2$s"), valuePart, rounder);
    }

    public static String constructRawPlaceholder(Placeholder placeholder) {
        PlaceholderType type = placeholder.type();
        PlaceholderProperties properties = placeholder.properties();
        CaptureGroup leftGroup = properties.leftGroup(), rightGroup = properties.rightGroup();
        String escaping = placeholder.isEscaped() ? "\\" : "";

        String baked = switch (type) {
            case DOLLAR -> "$";
            case BRACKETS -> "{}";
            case TRIM -> String.format("%s%s$%s%s",
                    leftGroup.operation() == CUT ? "{" : "}",
                    constructRawCaptureGroup(leftGroup, false, true),
                    constructRawCaptureGroup(rightGroup, false, false),
                    rightGroup.operation() == CUT ? "}" : "{");
            case TRIM_SHORT -> (rightGroup.operation() == NONE)
                    ? String.format("%s%s$", leftGroup.operation() == CUT ? "{" : "}",
                    constructRawCaptureGroup(leftGroup, false, true))
                    : String.format("$%s%s", constructRawCaptureGroup(rightGroup, false, false),
                    leftGroup.operation() == CUT ? "{" : "}");
            case TRIM_ALT -> String.format("${%s%s%s%s%s}",
                    constructRawCaptureGroup(leftGroup, true, true),
                    leftGroup.operation() == CUT ? "-" : "+",
                    properties.middleGroupOperation() == CUT ? "-" : "+",
                    rightGroup.operation() == CUT ? "-" : "+",
                    constructRawCaptureGroup(rightGroup, true, false));
            default -> "";
        };

        return escaping + baked;
    }
}
