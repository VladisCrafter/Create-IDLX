package com.vladiscrafter.createidlx.util;

import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.trains.display.FlapDisplaySection;
import com.vladiscrafter.createidlx.mixin.accessor.create.FlapDisplaySectionAccessor;
import com.vladiscrafter.createidlx.util.bridge.DisplayLinkVisualizationConfigHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;

import static com.simibubi.create.content.trains.display.FlapDisplaySection.*;
import static com.vladiscrafter.createidlx.util.attachedLabel.AttachedLabelProcessingUtils.*;

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
