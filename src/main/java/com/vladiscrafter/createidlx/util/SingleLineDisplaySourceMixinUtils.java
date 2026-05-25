package com.vladiscrafter.createidlx.util;

import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.trains.display.FlapDisplaySection;
import com.vladiscrafter.createidlx.config.CIDLXConfigs;
import com.vladiscrafter.createidlx.mixin.accessor.create.FlapDisplaySectionAccessor;
import com.vladiscrafter.createidlx.util.bridge.DisplayLinkVisualizationConfigHolder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

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

    public static String breakDownAndAssembleLabel(String label, String rawInfo) {
        if (label.isEmpty()) return rawInfo;

        StringBuilder result = new StringBuilder();
        ArrayList<String> sections = breakDownLabel(label);

        if (getCoveringPlaceholdersInLabel(label).getLeft()) result.append(rawInfo);
        for (int i = 0; i < sections.size(); i++) {
            result.append(sections.get(i));
            if (sections.size() > i + 1) result.append(rawInfo);
        }
        if (getCoveringPlaceholdersInLabel(label).getRight()) result.append(rawInfo);

        return result.toString();
    }

    public static List<MutableComponent> breakDownAndAssembleLabelAsComponentList(String label, String rawInfo) {
        List<MutableComponent> result = new ArrayList<>();
        ArrayList<String> sections = breakDownLabel(label);

        if (getCoveringPlaceholdersInLabel(label).getLeft()) result.add(Component.literal(rawInfo));
        for (int i = 0; i < sections.size(); i++) {
            result.add(Component.literal(sections.get(i)));
            if (sections.size() > i + 1) result.add(Component.literal(rawInfo));
        }
        if (getCoveringPlaceholdersInLabel(label).getRight()) result.add(Component.literal(rawInfo));

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
        return getIntermediatePlaceholdersCountInLabel(label)
                + (getCoveringPlaceholdersInLabel(label).getLeft() ? 1 : 0)
                + (getCoveringPlaceholdersInLabel(label).getRight() ? 1 : 0);
    }

    public static int getIntermediatePlaceholdersCountInLabel(String label) {
        return processLabel(label).getLeft().getRight();
    }

    public static Pair<Boolean, Boolean> getCoveringPlaceholdersInLabel(String label) {
        return processLabel(label).getRight().getRight();
    }

    public static boolean hasEscapedPlaceholders(String label) {
        return processLabel(label).getRight().getRight().getLeft();
    }

    private static Pair<Pair<ArrayList<String>, Integer>, Pair<Boolean, Pair<Boolean, Boolean>>> processLabel(String label) {
        boolean isDollarSignPlaceholderEnabled = CIDLXConfigs.server.enableDollarPlaceholder.get();
        boolean isBracketsPlaceholderEnabled = CIDLXConfigs.server.enableBracketsPlaceholder.get();
        boolean isEscapingOfPlaceholdersEnabled = CIDLXConfigs.server.enableEscapingOfPlaceholders.get();
        boolean isEscapingOfDisabledPlaceholdersHidden = CIDLXConfigs.server.hideEscapingOfDisabledPlaceholders.get();

        StringBuilder breakableLabel = new StringBuilder(label);
        ArrayList<String> labelParts = new ArrayList<>();
        int intermediatePlaceholders = 0;
        boolean hasEscapedPlaceholders = false;

        boolean startsByPlaceholder = breakableLabel.isEmpty() || getPlaceholderLength(breakableLabel, 0) > 0;
        if (startsByPlaceholder && !breakableLabel.isEmpty())
            breakableLabel.delete(0, getPlaceholderLength(breakableLabel, 0));

        boolean endsByPlaceholder = false;

        while (!breakableLabel.isEmpty()) {
            StringBuilder labelPart = new StringBuilder();
            boolean foundPlaceholder = false;

            for (int i = 0; i < breakableLabel.length(); i++) {
                if (getPlaceholderLength(breakableLabel, i) > 0) {
                    labelParts.add(!labelPart.isEmpty() ? labelPart.toString() : "");

                    foundPlaceholder = true;
                    breakableLabel.delete(0, i + getPlaceholderLength(breakableLabel, i));

                    if (breakableLabel.isEmpty()) endsByPlaceholder = true;
                    else intermediatePlaceholders++;

                    break;
                }

                if (getPlaceholderLength(breakableLabel, i) == 0) {
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

        return Pair.of(Pair.of(labelParts, intermediatePlaceholders), Pair.of(hasEscapedPlaceholders, Pair.of(startsByPlaceholder, endsByPlaceholder)));
    }

    private static int getPlaceholderLength(StringBuilder text, int i) {
        boolean isDollarSignPlaceholderEnabled = CIDLXConfigs.server.enableDollarPlaceholder.get();
        boolean isBracketsPlaceholderEnabled = CIDLXConfigs.server.enableBracketsPlaceholder.get();
        boolean isEscapingOfPlaceholdersEnabled = CIDLXConfigs.server.enableEscapingOfPlaceholders.get();

        if ((i == 0 || text.charAt(i - 1) != '\\') || !isEscapingOfPlaceholdersEnabled) {
            if ((text.charAt(i) == '$' && isDollarSignPlaceholderEnabled)) return 1;
            else if ((text.charAt(i) == '{' && (text.length() >= i + 1 && text.charAt(i + 1) == '}') && isBracketsPlaceholderEnabled)) return 2;
        }

        return 0;
    }

    private static int getEscapedPlaceholderLength(StringBuilder text, int i) {
        boolean isDollarSignPlaceholderEnabled = CIDLXConfigs.server.enableDollarPlaceholder.get();
        boolean isBracketsPlaceholderEnabled = CIDLXConfigs.server.enableBracketsPlaceholder.get();

        if (text.charAt(i) == '\\') {
            if (text.length() >= i + 2 && text.charAt(i + 1) == '$' && isDollarSignPlaceholderEnabled)
                return 1;

            else if (text.length() >= i + 3 && (text.charAt(i + 1) == '{' && text.charAt(i + 2) == '}') && isBracketsPlaceholderEnabled)
                return 2;

            else return 0;
        } else return 0;
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
        boolean trimmed = false;
        boolean lastTruncated = false;

        for (FlapDisplaySection section : sections) {
            if (trimmed) break;

            float sectionWidth = section.getSize();
            totalWidth += sectionWidth;

//            send(String.format("Checking section '%s' with a width of %f (total width with it: %f, max width: %f)",
//                    section.getText().getString(), sectionWidth, totalWidth, maxWidth));

            if (totalWidth == maxWidth && truncateLast) {
                maxWidth -= MONOSPACE;
                lastTruncated = true;
//                send("Truncation engaged; ellipsis: " + ellipsis);
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

//                send(String.format("Unclamped section '%s' is overflowing by %f; trimmed to %d characters and added '%s' as the last section.",
//                        unclampedSectionText, overflow, trimmedTextLength, section.getText().getString()));
                clampedSections.add(section);

                trimmed = true;
            } else clampedSections.add(section);
        }

        if (lastTruncated) maxWidth += MONOSPACE;
//        send(String.format("Final totalWidth: %f (leftSpace: %f)", totalWidth, maxWidth - totalWidth));
        return Pair.of(clampedSections, maxWidth - totalWidth);
    }

    public static String buildLayoutSignature(String label, String layoutKey, int valueSize, DisplayLinkContext context) {
        CompoundTag visualizationConfig = ((DisplayLinkVisualizationConfigHolder) context.blockEntity()).createidlx$getVisualizationConfig();
        StringBuilder signature = new StringBuilder("IDLX");

        signature
                .append("_Layout:").append(layoutKey)
                .append("_Placeholders:").append(getTotalPlaceholdersCountInLabel(label))
                .append("->[").append(getCoveringPlaceholdersInLabel(label).getLeft() ? "1" : "0")
                .append("+").append(getIntermediatePlaceholdersCountInLabel(label))
                .append("+").append(getCoveringPlaceholdersInLabel(label).getRight() ? "1" : "0")
                .append("]_LabelSections:").append(breakDownLabel(label).size()).append("->[");

        for (String part : breakDownLabel(label)) signature.append(part.length()).append("+");
        signature.deleteCharAt(signature.lastIndexOf("+"));

        signature.append("]_ValueWidth:").append(valueSize);

        signature.append("_VisualizationConfig:").append(anyVisualizationConfigEnabled(context) ? "T" : "F");
        if (anyVisualizationConfigEnabled(context)) signature.append("->[")
                .append(visualizationConfig.getBoolean("CenterText") ? "T" : "F")
                /*.append("+").append(visualizationConfig.getBoolean("CutOutSectionGaps") ? "T" : "F")*/
                .append("+").append(visualizationConfig.getBoolean("MarkTruncationWithEllipsis") ? "T" : "F").append("]");

        return signature.toString();
    }

//    public static void send(String text) {
//        if (Minecraft.getInstance().player != null) Minecraft.getInstance().player.sendSystemMessage(Component.literal(text));
//    }

    @Deprecated
    public static String assembleFullLine(DisplayLinkContext context, String raw) {
        boolean isDollarSignPlaceholderEnabled = CIDLXConfigs.server.enableDollarPlaceholder.get();
        boolean isBracketsPlaceholderEnabled = CIDLXConfigs.server.enableBracketsPlaceholder.get();
        boolean isEscapingOfDisabledPlaceholdersHidden = CIDLXConfigs.server.hideEscapingOfDisabledPlaceholders.get();

        String label = context.sourceConfig().getString("Label");
        if (label.isEmpty()) return raw;

        String result = label;
        if (getIntermediatePlaceholdersCountInLabel(label) > 0) {
            if (isDollarSignPlaceholderEnabled)
                result = result.replaceAll("(?<!\\\\)\\$", Matcher.quoteReplacement(raw));
            if (isBracketsPlaceholderEnabled)
                result = result.replaceAll("(?<!\\\\)\\{}", Matcher.quoteReplacement(raw));
        } else {
            result = label + " " + raw;
        }

        if (isDollarSignPlaceholderEnabled) result = result.replaceAll("\\\\\\$", "\\$");
        if (isBracketsPlaceholderEnabled) result = result.replaceAll("\\\\\\{}", "{}");
        if (isEscapingOfDisabledPlaceholdersHidden) result = result
                .replaceAll("\\\\\\$", "\\$").replaceAll("\\\\\\{}", "{}");

        return result;
    }
}
