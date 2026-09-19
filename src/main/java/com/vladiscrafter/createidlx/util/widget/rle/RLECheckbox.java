package com.vladiscrafter.createidlx.util.widget.rle;

import com.vladiscrafter.createidlx.foundation.gui.CreateIDLXGuiTextures;
import com.vladiscrafter.createidlx.util.widget.TextureButton;
import net.createmod.catnip.gui.element.ScreenElement;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.NotNull;

public class RLECheckbox extends TextureButton {
    protected boolean on;
    protected RLECheckbox linkedCheckbox;

    final ScreenElement tex = CreateIDLXGuiTextures.RLE_PL_PROPERTIES_CHECKBOX_OFF;
    final ScreenElement hoverTex = CreateIDLXGuiTextures.RLE_PL_PROPERTIES_CHECKBOX_OFF_H;
    final ScreenElement onTex = CreateIDLXGuiTextures.RLE_PL_PROPERTIES_CHECKBOX_ON;
    final ScreenElement onHoverTex = CreateIDLXGuiTextures.RLE_PL_PROPERTIES_CHECKBOX_ON_H;

    public RLECheckbox(int x, int y) {
        super(x, y, 9, 9, CreateIDLXGuiTextures.RLE_PL_PROPERTIES_CHECKBOX_OFF, CreateIDLXGuiTextures.RLE_PL_PROPERTIES_CHECKBOX_OFF_H);
        this.on = false;
        this.linkedCheckbox = null;
        super.setNonFocusable();
        super.withCallback(this::toggle);
    }

    public boolean isOn() {
        return this.on;
    }

    public void setOn(boolean on) {
        this.on = on;
    }

    public RLECheckbox getLinkedCheckbox() {
        return linkedCheckbox;
    }

    public void setLinkedCheckbox(RLECheckbox linkedCheckbox) {
        this.linkedCheckbox = linkedCheckbox;
    }

    public void toggle() {
        this.on ^= true;
        if (linkedCheckbox != null) linkedCheckbox.setOn(!on);
    }

    @Override
    public void doRender(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        if (visible) {
            isHovered = isMouseOver(mouseX, mouseY);
            if (on) {
                if (!isHoveredOrFocused()) onTex.render(graphics, getX(), getY());
                else onHoverTex.render(graphics, getX(), getY());
            } else {
                if (!isHoveredOrFocused()) tex.render(graphics, getX(), getY());
                else hoverTex.render(graphics, getX(), getY());
            }
        }
    }
}
