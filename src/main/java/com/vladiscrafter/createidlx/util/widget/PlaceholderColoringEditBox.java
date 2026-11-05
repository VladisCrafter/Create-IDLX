package com.vladiscrafter.createidlx.util.widget;

import com.vladiscrafter.createidlx.config.CIDLXClient;
import com.vladiscrafter.createidlx.config.CIDLXConfigs;
import com.vladiscrafter.createidlx.mixin.accessor.minecraft.EditBoxAccessor;
import com.vladiscrafter.createidlx.util.attachedLabel.AttachedLabelPart;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

import static com.vladiscrafter.createidlx.util.attachedLabel.AttachedLabelProcessingUtils.getPlaceholder;

public class PlaceholderColoringEditBox extends EditBox {
    boolean coloring;

    public PlaceholderColoringEditBox(Font font, int x, int y, int width, int height, Component message) {
        super(font, x, y, width, height, null, message);
        this.coloring = true;
    }

    public void setColoring(boolean coloring) {
        this.coloring = coloring;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        highlightPlaceholders(guiGraphics, ((EditBoxAccessor) this).createidlx$getFont(), ((EditBoxAccessor) this).createidlx$getDisplayPos(),
                getValue(), this.getX(), this.getY(), this.getWidth(), getInnerWidth(), isBordered());
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
    }

    public static void highlightPlaceholders(GuiGraphics graphics, Font font, int displayPos, String full,
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
            AttachedLabelPart.Placeholder placeholder = getPlaceholder(full, i);

            int placeholderLength = placeholder.length();
            String pl = full.substring(i, i + placeholderLength);
            int plW = font.width(pl);

            AttachedLabelPart.PlaceholderType placeholderType = placeholder.type();
            AttachedLabelPart.PlaceholderState placeholderState = placeholder.state();

            if (placeholderLength > 0) {
                int minX = Math.max(widgetX /*- 4*/, fX), maxX = Math.min(fX + plW, widgetX + font.width(visible) /*widgetWidth + 3*/);

                if (placeholderState == AttachedLabelPart.PlaceholderState.ESCAPED_DISABLED && !isEscapingOfDisabledPlaceholdersHidden) {
                    if (maxX - minX >= plW) minX += font.width(String.valueOf('\\'));
                    placeholderState = AttachedLabelPart.PlaceholderState.DISABLED;
                }

                if (minX < maxX) graphics.fill(minX, sY, maxX, eY, 0, getHighlightColor(placeholderType, placeholderState));
                i += placeholderLength - 1;
                fX += plW;
            } else fX += font.width(String.valueOf(full.charAt(i)));
        }
    }

    public static int getHighlightColor(AttachedLabelPart.Placeholder placeholder) {
        return getHighlightColor(placeholder.type(), placeholder.state());
    }

    private static int getHighlightColor(AttachedLabelPart.PlaceholderType placeholderType, AttachedLabelPart.PlaceholderState placeholderState) {
        CIDLXClient cfg = CIDLXConfigs.client;

        int alpha = cfg.placeholdersColorsAlpha.get() << 24;
        int invisible = 0x00000000;

        if (placeholderState != AttachedLabelPart.PlaceholderState.ACTIVE) {
            return cfg.colorPlaceholders.get() ? switch (placeholderState) {
                case ESCAPED -> cfg.colorEscapedPlaceholders.get()
                        ? cfg.escapedPlaceholderColor.get() | alpha : invisible;
                case DISABLED -> cfg.colorDisabledPlaceholders.get()
                        ? cfg.disabledPlaceholderColor.get() | alpha : invisible;
                case ESCAPED_DISABLED -> cfg.colorEscapedDisabledPlaceholders.get()
                        ? cfg.escapedDisabledPlaceholderColor.get() | alpha : invisible;
                /*case INCOMPLETE -> cfg.colorIncompletePlaceholders.get()
                        ? cfg.incompletePlaceholderColor.get() | alpha : invisible;
                case UNOPTIMIZED -> cfg.colorUnoptimizedPlaceholders.get()
                        ? cfg.unoptimizedPlaceholderColor.get() | alpha : invisible;
                case SYNTACTICALLY_ERRORSOME -> cfg.colorSyntacticallyErrorsomePlaceholders.get()
                        ? cfg.syntacticallyErrorsomePlaceholderColor.get() | alpha : invisible;*/
                case null, default -> cfg.colorInvalidPlaceholders.get()
                        ? cfg.invalidPlaceholderColor.get() | alpha : invisible;
            } : invisible;
        }

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
            case null, default -> cfg.colorInvalidPlaceholders.get()
                    ? cfg.invalidPlaceholderColor.get() | alpha : invisible;
        } : invisible;
    }
}
