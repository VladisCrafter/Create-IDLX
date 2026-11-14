package com.vladiscrafter.createidlx.util.widget.rle;

import com.vladiscrafter.createidlx.config.CIDLXConfigs;
import com.vladiscrafter.createidlx.foundation.gui.CreateIDLXGuiTextures;
import com.vladiscrafter.createidlx.util.attachedLabel.AttachedLabelPart;
import com.vladiscrafter.createidlx.util.gui.UIRenderHelperAdditions;
import net.createmod.catnip.gui.TextureSheetSegment;
import net.createmod.catnip.gui.element.ScreenElement;
import net.createmod.catnip.gui.widget.AbstractSimiWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings({"UnusedReturnValue", "unused", "unchecked", "RedundantSuppression"})
public class RLEPlaceholderTabButton extends AbstractSimiWidget {
    protected int index;
    protected AttachedLabelPart.Placeholder placeholder;
    protected boolean selected;

    final boolean displayFullPlaceholdersInTabHeaders = CIDLXConfigs.client.rleDisplayFullPlaceholdersInTabHeaders.get();

    public RLEPlaceholderTabButton(int x, int y, int width, int index, AttachedLabelPart.Placeholder placeholder) {
        super(x, y, width, 13);
        this.index = index;
        this.placeholder = placeholder;
        this.selected = false;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public AttachedLabelPart.Placeholder getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(AttachedLabelPart.Placeholder placeholder) {
        this.placeholder = placeholder;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        this.setFocused(selected);
    }

    @Override
    public void setFocused(boolean focused) {
        super.setFocused(focused || selected);
    }

    @Override
    public void doRender(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        if (visible) {
            isHovered = isMouseOver(mouseX, mouseY);
            ScreenElement lTex = isHoveredOrFocused() ? CreateIDLXGuiTextures.RLE_TAB_ACTIVE_L : CreateIDLXGuiTextures.RLE_TAB_INACTIVE_L;
            TextureSheetSegment mTex = isHoveredOrFocused() ? CreateIDLXGuiTextures.RLE_TAB_ACTIVE_M : CreateIDLXGuiTextures.RLE_TAB_INACTIVE_M;
            ScreenElement rTex = isHoveredOrFocused() ? CreateIDLXGuiTextures.RLE_TAB_ACTIVE_R : CreateIDLXGuiTextures.RLE_TAB_INACTIVE_R;

            lTex.render(graphics, getX(), getY());
            UIRenderHelperAdditions.drawStretched(graphics, getX() + 1, getY(), getWidth() - 2, getHeight(), mTex, 512, 512);
            rTex.render(graphics, getX() + getWidth() - 1, getY());

            Font font = Minecraft.getInstance().font;
            MutableComponent indexText = Component.literal(String.format("#%d", index + 1)); // TODO: make translatable
            MutableComponent text = displayFullPlaceholdersInTabHeaders && font.width(placeholder.getString()) + 2 <= getWidth()
                    ? Component.literal(placeholder.getString())
                     : font.width(indexText) + 2 <= getWidth() ? indexText : Component.literal("" + (index + 1));
            graphics.drawString(font, text, getX() + (int) ((float) getWidth() / 2) - (font.width(text) / 2),
                    getY() + 3, isHoveredOrFocused() ? 0x4F4F4F : 0x424242, false);
        }
    }
}
