package com.vladiscrafter.createidlx.util.gui;

import com.vladiscrafter.createidlx.config.CIDLXConfigs;
import net.minecraft.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

public final class MarqueeEffectTruncatedLabel {

    private MarqueeEffectTruncatedLabel() {}

    public static void render(GuiGraphics graphics, Font font, String text, int textX, int textY, int width, boolean isSourceTypeSelector, long lastStateChangeMillis) {
        if (text == null || text.isEmpty()) return;

        final boolean addMarqueeEffectToTruncatedStrings = CIDLXConfigs.client.addMarqueeEffectToTruncatedStrings.get();
        final long fixedCharTravelTime = (long) CIDLXConfigs.client.fixedCharTravelTime.get();
        final long fixedStringTravelTime = (long) ((int) (CIDLXConfigs.client.fixedStringTravelTime.get() * 1000));
        final long maximalStringTravelTime = (long) ((int) (CIDLXConfigs.client.maximalStringTravelTime.get() * 1000));
        final long minimalStringTravelTime = (long) ((int) (CIDLXConfigs.client.minimalStringTravelTime.get() * 1000));
        final long stringPauseTime = (long) ((int) (CIDLXConfigs.client.stringPauseTime.get() * 1000));
        final boolean isStringTravelTimeFixed = fixedStringTravelTime > 0;
        final boolean isMaximalStringTravelTimeSpecified = maximalStringTravelTime > 0;
        final boolean isMinimalStringTravelTimeSpecified = minimalStringTravelTime > 0;

        int textWidth = font.width(text);
        int offset = 0;

        int availableWidthForCheck = Math.max(0, width - 6);
        int availableWidth = isSourceTypeSelector ? Math.max(0, width - 8) : Math.max(0, width - 10);

        if (textWidth <= availableWidthForCheck) {
            if (isSourceTypeSelector) graphics.drawString(font, text, textX - offset, textY, 0xFFFFFF, true);
            else graphics.drawString(font, text, textX - offset + 1, textY - 1, 0xFFFFFF, true);
            return;
        }

        long elapsed = Util.getMillis() - lastStateChangeMillis;

        int overflow = textWidth - availableWidth;

        long pauseMillis = Math.max(0L, stringPauseTime);
        long travelMillis = Math.max(0L,
                isStringTravelTimeFixed ? fixedStringTravelTime :
                        isMinimalStringTravelTimeSpecified && isMaximalStringTravelTimeSpecified ? Math.min(Math.max(minimalStringTravelTime, fixedCharTravelTime * overflow), maximalStringTravelTime) :
                                isMinimalStringTravelTimeSpecified ? Math.max(minimalStringTravelTime, fixedCharTravelTime * overflow) :
                                        isMaximalStringTravelTimeSpecified ? Math.min(fixedCharTravelTime * overflow, maximalStringTravelTime) :
                                                fixedCharTravelTime * overflow);

        long period = (pauseMillis + travelMillis) * 2;
        long t = addMarqueeEffectToTruncatedStrings ? elapsed % period : 0;

        if (t < pauseMillis) {
            offset = 0;
        } else if (t < pauseMillis + travelMillis) {
            double progress = (double) (t - pauseMillis) / (double) travelMillis;
            offset = (int) Math.round(overflow * progress);
        } else if (t < pauseMillis + travelMillis + pauseMillis) {
            offset = overflow;
        } else {
            double progress = (double) (t - pauseMillis - travelMillis - pauseMillis) / (double) travelMillis;
            offset = overflow - (int) Math.round(overflow * progress);
        }

        int minX = isSourceTypeSelector ? textX - 4 : textX - 3;
        int maxX = isSourceTypeSelector ? textX + availableWidth + 4 : textX + availableWidth + 5;
        int stringX = isSourceTypeSelector ? textX - offset : textX - offset + 1;
        int stringY = isSourceTypeSelector ? textY : textY - 1;

        graphics.enableScissor(minX, stringY, maxX, stringY + font.lineHeight);
        try {
            graphics.drawString(font, text, stringX, stringY, 0xFFFFFF, true);
        } finally {
            graphics.disableScissor();
        }
    }

    public static boolean shouldGetTruncated(String text, Font font, int width) {
        final boolean truncateOverflowingStrings = CIDLXConfigs.client.truncateOverflowingStrings.get();
        return text != null && !text.isEmpty() && truncateOverflowingStrings && font.width(text) > Math.max(0, width - 5);
    }
}
