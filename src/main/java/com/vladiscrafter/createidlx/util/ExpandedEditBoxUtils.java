package com.vladiscrafter.createidlx.util;

import com.vladiscrafter.createidlx.config.CIDLXClient;
import com.vladiscrafter.createidlx.config.CIDLXConfigs;
import net.createmod.catnip.config.ConfigBase;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

import java.util.function.Function;

import static com.vladiscrafter.createidlx.util.PlaceholderProcessingUtils.*;

public class ExpandedEditBoxUtils {
    public static void highlightSpecialCharacters(GuiGraphics graphics, Font font, int displayPos, String full,
                                           int widgetX, int widgetY, int innerWidth, int widgetHeight, boolean isBordered) {
        boolean isEscapingOfDisabledPlaceholdersHidden = CIDLXConfigs.server.hideEscapingOfDisabledPlaceholders.get();

        if (full.isEmpty() || displayPos >= full.length()) return;

        String visible = font.plainSubstrByWidth(full.substring(displayPos), innerWidth);
        if (visible.isEmpty()) return;

        int x = isBordered ? widgetX + 4 : widgetX;
        int y = isBordered ? widgetY + (widgetHeight - 8) / 2 : widgetY;

        int fX = x - font.width(full.substring(0, displayPos));

        int heightExpansion = CIDLXConfigs.client.coloringBackgroundHeightIncrease.get();
        int sY = y - 1 - heightExpansion;
        int eY = sY + 10 + heightExpansion * 2;

        for (int i = 0; i < full.length(); i++) {
            Placeholder placeholder = getPlaceholder(full, i);

            int placeholderLength = placeholder.length();
            String pl = full.substring(i, i + placeholderLength);
            int plW = font.width(pl);

            PlaceholderType placeholderType = placeholder.type();

            if (placeholderLength > 0) {
                int minX = Math.max(widgetX /*- 4*/, fX), maxX = Math.min(fX + plW, widgetX + font.width(visible) /*widgetWidth + 3*/);

                if (placeholderType == PlaceholderType.ESCAPED_DISABLED && !isEscapingOfDisabledPlaceholdersHidden) {
                    if (maxX - minX >= plW) minX += font.width(String.valueOf('\\'));
                    placeholderType = PlaceholderType.DISABLED;
                }

                if (minX < maxX) graphics.fill(minX, sY, maxX, eY, 0, getHighlightColor(placeholderType));
                i += placeholderLength - 1;
                fX += plW;
            } else fX += font.width(String.valueOf(full.charAt(i)));
        }
    }

    private static int getHighlightColor(PlaceholderType placeholderType) {
        CIDLXClient cfg = CIDLXConfigs.client;

        int alpha = cfg.placeholdersColorsAlpha.get() << 24;
        int invisible = 0x00000000;

        return cfg.colorPlaceholders.get() ? switch (placeholderType) {
            case DOLLAR -> cfg.colorDollarPlaceholders.get()
                    ? cfg.dollarPlaceholderColor.get() | alpha : invisible;
            case BRACKETS -> cfg.colorBracketsPlaceholders.get()
                    ? cfg.bracketsPlaceholderColor.get() | alpha : invisible;
            case TRIM -> cfg.colorOriginalTrimmingPlaceholders.get()
                    ? cfg.originalTrimmingPlaceholderColor.get() | alpha : invisible;
            case TRIM_SHORT -> cfg.colorShortenedTrimmingPlaceholders.get()
                    ? cfg.shortenedTrimmingPlaceholderColor.get() | alpha : invisible;
            case TRIM_ALT -> cfg.colorAlternativeTrimmingPlaceholders.get()
                    ? cfg.alternativeTrimmingPlaceholderColor.get() | alpha : invisible;
            case ESCAPED -> cfg.colorEscapedPlaceholders.get()
                    ? cfg.escapedPlaceholderColor.get() | alpha : invisible;
            case DISABLED -> cfg.colorDisabledPlaceholders.get()
                    ? cfg.disabledPlaceholderColor.get() | alpha : invisible;
            case ESCAPED_DISABLED -> cfg.colorEscapedDisabledPlaceholders.get()
                    ? cfg.escapedDisabledPlaceholderColor.get() | alpha : invisible;
            case null, default -> cfg.colorInvalidPlaceholders.get()
                    ? cfg.invalidPlaceholderColor.get() | alpha : invisible;
        } : invisible;
    }
}
