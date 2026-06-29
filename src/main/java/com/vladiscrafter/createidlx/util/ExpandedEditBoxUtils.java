package com.vladiscrafter.createidlx.util;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import org.apache.commons.lang3.tuple.Pair;

import static com.vladiscrafter.createidlx.util.PlaceholderProcessingUtils.*;

public class ExpandedEditBoxUtils {
    static final int DOLLAR_PLACEHOLDER_HIGHLIGHT_COLOR = 0xEAB444;
    static final int BRACKETS_PLACEHOLDER_HIGHLIGHT_COLOR = 0xC3C54F;
    static final int TRIM_PLACEHOLDER_HIGHLIGHT_COLOR = 0xA0D06B;
    static final int TRIM_SHORT_PLACEHOLDER_HIGHLIGHT_COLOR = 0x75D78D;
    static final int TRIM_ALT_PLACEHOLDER_HIGHLIGHT_COLOR = 0x69D3C6;
    static final int ESCAPED_PLACEHOLDER_HIGHLIGHT_COLOR = 0x831922;
    static final int DISABLED_PLACEHOLDER_HIGHLIGHT_COLOR = 0x000000;
    static final int INVALID_PLACEHOLDER_HIGHLIGHT_COLOR = 0x781d59;

    public static void highlightSpecialCharacters(GuiGraphics graphics, Font font, int displayPos, String full,
                                           int widgetX, int widgetY, int innerWidth, int widgetHeight, boolean isBordered) {
        if (full.isEmpty() || displayPos >= full.length()) return;

        String visible = font.plainSubstrByWidth(full.substring(displayPos), innerWidth);
        if (visible.isEmpty()) return;

        int x = isBordered ? widgetX + 4 : widgetX;
        int y = isBordered ? widgetY + (widgetHeight - 8) / 2 : widgetY;

        int fX = x - font.width(full.substring(0, displayPos));

        int sY = y - 1 - 2; // TODO: configurably snap 'y' to field height ('- 2' here and '+ 4' below)
        int eY = sY + 10 + 4;

        for (int i = 0; i < full.length(); i++) {
            Placeholder placeholder = getPlaceholder(full, i);

            int placeholderLength = placeholder.length();
            String pl = full.substring(i, i + placeholderLength);
            int plW = font.width(pl);

            PlaceholderType placeholderType = placeholder.type();

            if (placeholderLength > 0) {
                int minX = Math.max(widgetX /*- 4*/, fX), maxX = Math.min(fX + plW, widgetX + font.width(visible) /*widgetWidth + 3*/);

                if (minX < maxX) graphics.fill(minX, sY, maxX, eY, 0, getHighlightColor(placeholderType));
                i += placeholderLength - 1;
                fX += plW;
            } else fX += font.width(String.valueOf(full.charAt(i)));
        }
    }

    private static int getHighlightColor(PlaceholderType placeholderType) {
        return switch (placeholderType) {
            case DOLLAR -> DOLLAR_PLACEHOLDER_HIGHLIGHT_COLOR;
            case BRACKETS -> BRACKETS_PLACEHOLDER_HIGHLIGHT_COLOR;
            case TRIM -> TRIM_PLACEHOLDER_HIGHLIGHT_COLOR;
            case TRIM_SHORT -> TRIM_SHORT_PLACEHOLDER_HIGHLIGHT_COLOR;
            case TRIM_ALT -> TRIM_ALT_PLACEHOLDER_HIGHLIGHT_COLOR;
            case ESCAPED -> ESCAPED_PLACEHOLDER_HIGHLIGHT_COLOR;
            case DISABLED -> DISABLED_PLACEHOLDER_HIGHLIGHT_COLOR;
            case null, default -> INVALID_PLACEHOLDER_HIGHLIGHT_COLOR;
        } | 0xAA000000;
    }
}
