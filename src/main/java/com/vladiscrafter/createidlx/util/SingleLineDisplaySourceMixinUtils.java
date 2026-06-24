package com.vladiscrafter.createidlx.util;

import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.trains.display.FlapDisplaySection;
import com.vladiscrafter.createidlx.config.CIDLXConfigs;
import com.vladiscrafter.createidlx.mixin.accessor.create.FlapDisplaySectionAccessor;
import com.vladiscrafter.createidlx.util.bridge.DisplayLinkVisualizationConfigHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static com.simibubi.create.content.trains.display.FlapDisplaySection.*;

public class SingleLineDisplaySourceMixinUtils {
    private SingleLineDisplaySourceMixinUtils() {}

    public static boolean anyVisualizationConfigEnabled(DisplayLinkContext context) {
        return getCenterText(context) /*|| getCutOutSectionGaps(context)*/ || getMarkTruncationWithEllipsis(context);
    }

    public static boolean getCenterText(DisplayLinkContext context) {
        return getVisualizationConfig(context).getBoolean("CenterText");
    }

    /*public static boolean getCutOutSectionGaps(DisplayLinkContext context) {
        return getVisualizationConfig(context).getBoolean("CutOutSectionGaps");
    }*/

    public static boolean getMarkTruncationWithEllipsis(DisplayLinkContext context) {
        return getVisualizationConfig(context).getBoolean("MarkTruncationWithEllipsis");
    }

    public static CompoundTag getVisualizationConfig(DisplayLinkContext context) {
        return ((DisplayLinkVisualizationConfigHolder) context.blockEntity()).createidlx$getVisualizationConfig();
    }

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
        boolean isDollarSignPlaceholderEnabled = CIDLXConfigs.server.enableDollarPlaceholder.get();
        boolean isBracketsPlaceholderEnabled = CIDLXConfigs.server.enableBracketsPlaceholder.get();
        boolean isEscapingOfPlaceholdersEnabled = CIDLXConfigs.server.enableEscapingOfPlaceholders.get();
        boolean isEscapingOfDisabledPlaceholdersHidden = CIDLXConfigs.server.hideEscapingOfDisabledPlaceholders.get();

        StringBuilder breakableLabel = new StringBuilder(label);
        ArrayList<String> labelParts = new ArrayList<>(), placeholders = new ArrayList<>();
        boolean hasEscapedPlaceholders = false;

        boolean startsByPlaceholder = breakableLabel.isEmpty() || getPlaceholderLength(breakableLabel.toString(), 0) > 0;
        if (startsByPlaceholder && !breakableLabel.isEmpty()) {
            placeholders.add(breakableLabel.substring(0, getPlaceholderLength(breakableLabel.toString(), 0)));
            breakableLabel.delete(0, getPlaceholderLength(breakableLabel.toString(), 0));
        }

        boolean endsByPlaceholder = false;

        while (!breakableLabel.isEmpty()) {
            StringBuilder labelPart = new StringBuilder();
            boolean foundPlaceholder = false;

            for (int i = 0; i < breakableLabel.length(); i++) {
                if (getPlaceholderLength(breakableLabel.toString(), i) > 0) {
                    labelParts.add(!labelPart.isEmpty() ? labelPart.toString() : "");
                    if (!label.isEmpty()) breakableLabel.delete(0, labelPart.toString().length());

                    foundPlaceholder = true;
                    placeholders.add(breakableLabel.substring(0, getPlaceholderLength(breakableLabel.toString(), 0)));
                    breakableLabel.delete(0, getPlaceholderLength(breakableLabel.toString(), 0));

                    if (breakableLabel.isEmpty()) endsByPlaceholder = true;

                    break;
                }

                if (getPlaceholderLength(breakableLabel.toString(), i) == 0) {
                    labelPart.append(breakableLabel.charAt(i));
                }
            }

            if (!foundPlaceholder) {
                labelParts.add(breakableLabel.toString());
                breakableLabel.setLength(0);
            }
        }

        if (isEscapingOfPlaceholdersEnabled) {
            for (int p = 0; p < labelParts.size(); p++) {
                String part = labelParts.get(p);
                if (isDollarSignPlaceholderEnabled || isEscapingOfDisabledPlaceholdersHidden) part = part.replace("\\$", "$");
                if (isBracketsPlaceholderEnabled || isEscapingOfDisabledPlaceholdersHidden) part = part.replace("\\{}", "{}");
                labelParts.set(p, part);
            }
        }
        /*log("labelParts: " + labelParts);
        log("placeholders: " + placeholders);*/

        return Pair.of(Pair.of(labelParts, placeholders), Pair.of(hasEscapedPlaceholders, Pair.of(startsByPlaceholder, endsByPlaceholder)));
    }

    public static int getPlaceholderLength(String text, int i) {
        boolean isDollarSignPlaceholderEnabled = CIDLXConfigs.server.enableDollarPlaceholder.get();
        boolean isBracketsPlaceholderEnabled = CIDLXConfigs.server.enableBracketsPlaceholder.get();
        boolean isEscapingOfPlaceholdersEnabled = CIDLXConfigs.server.enableEscapingOfPlaceholders.get();

        if ((i == 0 || text.charAt(i - 1) != '\\') || !isEscapingOfPlaceholdersEnabled) {
            int trimmedLength = Math.max(getTrimmedPlaceholderLength(text, i), 0);

            if (trimmedLength > 0) return trimmedLength;
            else if ((text.charAt(i) == '$' && isDollarSignPlaceholderEnabled)) return 1;
            else if ((text.charAt(i) == '{' && (text.length() > i + 1 && text.charAt(i + 1) == '}') && isBracketsPlaceholderEnabled)) return 2;
        }

        return 0;
    }

    private static int getTrimmedPlaceholderLength(String text, int i) {
        int length = 0;
        boolean hasLeftHalf = true;

        if (text.charAt(i) == '{' || text.charAt(i) == '}') length++;
        else if (text.charAt(i) == '$') {
            int altLength = getAltTrimmedPlaceholderLength(text, i);
            if (altLength > 0) return altLength;

            hasLeftHalf = false;
            length++;
        }
        else return 0;

        i++;
        boolean readLeftHalf = false, readRightHalf = false;

        int leftHalfDigits = 0;
        while (!readLeftHalf && hasLeftHalf) {
            if (i >= text.length()) return 0;
            if (!Character.isDigit(text.charAt(i)) && text.charAt(i) != '$') return 0;
            else if (Character.isDigit(text.charAt(i))) leftHalfDigits++;
            else if (text.charAt(i) == '$') readLeftHalf = true;
            length++;
            i++;
        }
        if (readLeftHalf && leftHalfDigits == 0) return 0;

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
        if (!readRightHalf || rightHalfDigits == 0) potentialLength = 0;

        length += potentialLength;

        return length;
    }

    private static int getAltTrimmedPlaceholderLength(String text, int i) {
        if (text.charAt(i) != '$' || text.length() <= i + 7) return 0; // 'i + 7' considering there can't be a shortened version of this trim
        if (text.charAt(i + 1) != '{') return 0;

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

    public static boolean hasOverridingFinishLabel(DisplayLinkContext context) {
        return context.sourceConfig().contains("FinishLabel")
                && !context.sourceConfig().getString("FinishLabel").isEmpty()
                && context.sourceConfig().getInt("OverrideLabelOnFinish") == 1;
    }

    public static boolean isCountdownFinished(DisplayLinkContext context) {
        return context.sourceConfig().contains("IsCountdownFinished")
                && context.sourceConfig().getBoolean("IsCountdownFinished");
    }

    public static FlapDisplaySection createValueSection(float valueSize, String layoutKey, String information) {
        String cycleType = switch (layoutKey) {
            case "Progress" -> "pixel";
            case "Number" -> "numeric";
            case "Instant" -> "instant";
            default -> "alphabet";
        };

        FlapDisplaySection section = new FlapDisplaySection(valueSize * MONOSPACE, cycleType, false, false);
        if (layoutKey.equals("Progress")) section.wideFlaps();
        section.setText(Component.literal(information));
        return section;
    }

    public static FlapDisplaySection createLabelSection(String text) {
        return createLabelSection(text, text.length());
    }

    public static FlapDisplaySection createLabelSection(String text, float sectionWidth) {
        FlapDisplaySection section = new FlapDisplaySection(sectionWidth * MONOSPACE, "alphabet", false, false);
        section.setText(Component.literal(text));
        return section;
    }

    public static Pair<ArrayList<FlapDisplaySection>, Float> clampSections(ArrayList<FlapDisplaySection> sections,
                                                                           float maxWidth, boolean truncateLast, boolean ellipsis) {
        float totalWidth = 0f;
        ArrayList<FlapDisplaySection> clampedSections = new ArrayList<>();
        boolean trimmed = false, lastTruncated = false;

        for (FlapDisplaySection section : sections) {
            if (trimmed) break;

            float sectionWidth = section.getSize();
            totalWidth += sectionWidth;

            /*log(String.format("Checking section '%s' with a width of %f (total width with it: %f, max width: %f)",
                    section.getText().getString(), sectionWidth, totalWidth, maxWidth));*/

            if (totalWidth == maxWidth && truncateLast) {
                maxWidth -= MONOSPACE;
                lastTruncated = true;
                /*log("Truncation engaged; ellipsis: " + ellipsis);*/
            }

            if (totalWidth > maxWidth && section.getText() != null) {
                float overflow = totalWidth - maxWidth;
                String unclampedSectionText = section.getText().getString();

                int trimmedTextLength = Mth.clamp(unclampedSectionText.length() - (int) Math.ceil(overflow / MONOSPACE),
                        0, unclampedSectionText.length());
                String clampedSectionText = unclampedSectionText.substring(0, trimmedTextLength);
                if (ellipsis) clampedSectionText = clampedSectionText.replaceAll(".$", "…"); // TODO: CreateIDLX.translate();

                ((FlapDisplaySectionAccessor) section).createidlx$setSize(clampedSectionText.length() * MONOSPACE);
                section.setText(Component.literal(clampedSectionText));
                totalWidth -= (unclampedSectionText.length() - clampedSectionText.length()) * MONOSPACE;

                /*log(String.format("Unclamped section '%s' is overflowing by %f; trimmed to %d characters and added '%s' as the last section.",
                        unclampedSectionText, overflow, trimmedTextLength, section.getText().getString()));*/
                clampedSections.add(section);

                trimmed = true;
            } else clampedSections.add(section);
        }

        if (lastTruncated) maxWidth += MONOSPACE;
        /*log(String.format("Final totalWidth: %f (leftSpace: %f)", totalWidth, maxWidth - totalWidth));*/
        return Pair.of(clampedSections, maxWidth - totalWidth);
    }

    public static String buildLayoutSignature(String label, String layoutKey, int valueSize, DisplayLinkContext context) {
        CompoundTag visualizationConfig = ((DisplayLinkVisualizationConfigHolder) context.blockEntity()).createidlx$getVisualizationConfig();
        ArrayList<String> brokenDownLabel = breakDownLabel(label);
        StringBuilder signature = new StringBuilder("IDLX");

        signature
                .append("_Layout:").append(layoutKey)
                .append("_LabelSections:").append(brokenDownLabel.size());

        if (!brokenDownLabel.isEmpty()) {
            signature.append("->[");

            for (String part : brokenDownLabel) signature.append(part.length()).append("+");
            signature.deleteCharAt(signature.lastIndexOf("+"));

            signature.append("]");
        }
        
        signature
                .append("_Placeholders:").append(getTotalPlaceholdersCountInLabel(label));

        if (getTotalPlaceholdersCountInLabel(label) > 0) {
            signature.append("->[");

            for (String placeholder : extractPlaceholders(label)) signature.append(placeholder).append("+");
            signature.deleteCharAt(signature.lastIndexOf("+"));

            signature.append("]");
        }
                
        signature
                .append("_ValueWidth:").append(valueSize)
                .append("_VisualizationConfig:").append(anyVisualizationConfigEnabled(context) ? "T" : "F");

        if (anyVisualizationConfigEnabled(context)) {
            signature.append("->[")
                    .append(visualizationConfig.getBoolean("CenterText") ? "T" : "F")
                    /*.append("+").append(visualizationConfig.getBoolean("CutOutSectionGaps") ? "T" : "F")*/
                    .append("+").append(visualizationConfig.getBoolean("MarkTruncationWithEllipsis") ? "T" : "F").append("]");
        }
        /*log("New layout signature: " + signature);*/
        return signature.toString();
    }

    public static void log(String text) {
        if (Minecraft.getInstance().player != null) Minecraft.getInstance().player.sendSystemMessage(Component.literal(text));
    }
}
