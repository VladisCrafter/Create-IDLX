package com.vladiscrafter.createidlx.util.widget;

import com.vladiscrafter.createidlx.config.CIDLXConfigs;
import com.vladiscrafter.createidlx.mixin.accessor.minecraft.EditBoxAccessor;
import com.vladiscrafter.createidlx.util.attachedLabel.AttachedLabelPart;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;

import static com.vladiscrafter.createidlx.util.attachedLabel.AttachedLabelProcessingUtils.getPlaceholder;
import static com.vladiscrafter.createidlx.util.gui.PlaceholderColoringUtils.getHighlightColor;

public class PlaceholderColoringEditBox extends EditBox {
    boolean coloring;
    int selectedPlaceholderIndex, selectedPlaceholderDisplayPos, selectedPlaceholderLength;
    Vector2i selectedPlaceholderX;

    final boolean rleCenterAlignSelectedPlaceholder = CIDLXConfigs.client.rleCenterAlignSelectedPlaceholder.get();
    final boolean stretchColoringForPartlyVisiblePlaceholders = CIDLXConfigs.client.stretchColoringForPartlyVisiblePlaceholders.get();
    /*final boolean hideEscapingOfDisabledPlaceholders = CIDLXConfigs.server.hideEscapingOfDisabledPlaceholders.get();*/

    public PlaceholderColoringEditBox(Font font, int x, int y, int width, int height, Component message) {
        super(font, x, y, width, height, null, message);
        this.setBordered(false);
        this.coloring = true;
    }

    public boolean isColoring() {
        return coloring;
    }

    public void setColoring(boolean coloring) {
        this.coloring = coloring;
    }

    public void setSelectedPlaceholderIndex(int selectedPlaceholderIndex) {
        this.selectedPlaceholderIndex = selectedPlaceholderIndex;
    }

    public void setSelectedPlaceholderDisplayMarks(int selectedPlaceholderDisplayPos, int selectedPlaceholderLength, int cursorPosCache, boolean select) {
        this.selectedPlaceholderDisplayPos = selectedPlaceholderDisplayPos;
        this.selectedPlaceholderLength = selectedPlaceholderLength;
        /*boolean moveBack = selectedPlaceholderDisplayPos < ((EditBoxAccessor) this).createidlx$getDisplayPos();*/
        if (rleCenterAlignSelectedPlaceholder) setCursorPosition(0);

        setCursorPosition(selectedPlaceholderDisplayPos);
        setHighlightPos(selectedPlaceholderDisplayPos);

        if (rleCenterAlignSelectedPlaceholder) setCursorPosition(selectedPlaceholderDisplayPos + selectedPlaceholderLength
                + ((EditBoxAccessor) this).createidlx$getFont().plainSubstrByWidth(getValue()
                        .substring(selectedPlaceholderDisplayPos), getWidth() / 2)
                .length() - selectedPlaceholderLength / 2);

        setCursorPosition(selectedPlaceholderDisplayPos + selectedPlaceholderLength);
        if (!select) setHighlightPos(selectedPlaceholderDisplayPos + selectedPlaceholderLength);

        if (cursorPosCache > -1) {
            setCursorPosition(cursorPosCache);
            setHighlightPos(cursorPosCache);
        }
    }

    public Vector2i getSelectedPlaceholderX() {
        return selectedPlaceholderX;
    }

    @Override
    public void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        highlightPlaceholders(guiGraphics, ((EditBoxAccessor) this).createidlx$getFont(), ((EditBoxAccessor) this).createidlx$getDisplayPos(),
                getValue(), this.getX(), this.getY(), this.getWidth());
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
    }

    @SuppressWarnings("SuspiciousNameCombination")
    public void highlightPlaceholders(GuiGraphics graphics, Font font, int displayPos, String full, int widgetX, int widgetY, int width) {
        if (full.isEmpty() || displayPos >= full.length()) return;

        String visible = font.plainSubstrByWidth(full.substring(displayPos), width);
        if (visible.isEmpty()) return;

        int fX = widgetX - font.width(full.substring(0, displayPos));

        int heightExpansion = CIDLXConfigs.client.coloringBackgroundHeightIncrease.get();
        int sY = widgetY - 1 - heightExpansion;
        int eY = sY + 10 + heightExpansion * 2;

        int plI = -1;
        for (int i = 0; i < full.length(); i++) {
            AttachedLabelPart.Placeholder placeholder = getPlaceholder(full, i);
            if (!placeholder.isType(AttachedLabelPart.PlaceholderType.INVALID)) plI++;

            int placeholderLength = placeholder.length();
            String pl = full.substring(i, i + placeholderLength);
            int plW = font.width(pl);

            AttachedLabelPart.PlaceholderType placeholderType = placeholder.type();
            AttachedLabelPart.PlaceholderState placeholderState = placeholder.state();

            if (placeholderLength > 0) {
                int minX = Math.max(widgetX - 4, fX);
                int maxX = (fX + plW > widgetX + font.width(visible) && stretchColoringForPartlyVisiblePlaceholders) ? widgetX + width + 4 : fX + plW;

                /*if (placeholderState == AttachedLabelPart.PlaceholderState.ESCAPED_DISABLED && !hideEscapingOfDisabledPlaceholders) {
                    if (maxX - minX >= plW) minX += font.width(String.valueOf('\\'));
                    placeholderState = AttachedLabelPart.PlaceholderState.DISABLED;
                }*/

                if (minX < maxX) {
                    if (coloring) graphics.fill(minX, sY, maxX, eY, 0, getHighlightColor(placeholderType, placeholderState));
                    if (plI == selectedPlaceholderIndex) selectedPlaceholderX = new Vector2i(minX, maxX);
                }
                i += placeholderLength - 1;
                fX += plW;
            } else fX += font.width(String.valueOf(full.charAt(i)));
        }
    }
}
