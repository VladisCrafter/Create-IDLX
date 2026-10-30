package com.vladiscrafter.createidlx.util.widget;

import com.simibubi.create.AllKeys;
import net.createmod.catnip.gui.element.ScreenElement;
import net.createmod.catnip.gui.widget.AbstractSimiWidget;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;

@SuppressWarnings({"UnusedReturnValue", "unused", "unchecked"})
public class TextureButton extends AbstractSimiWidget {
    protected ScreenElement tex;
    protected ScreenElement hoverTex;
    protected LinkedHashMap<AllKeys, ScreenElement> specialHoverTextures;
    protected boolean focusable = true;

    public TextureButton(int x, int y, ScreenElement tex) {
        this(x, y, tex, tex);
    }

    public TextureButton(int x, int y, ScreenElement tex, ScreenElement hoverTex) {
        this(x, y, 18, 18, tex, hoverTex);
    }

    public TextureButton(int x, int y, int w, int h, ScreenElement tex) {
        this(x, y, w, h, tex, tex);
    }

    public TextureButton(int x, int y, int w, int h, ScreenElement tex, ScreenElement hoverTex) {
        super(x, y, w, h);
        this.tex = tex;
        this.hoverTex = hoverTex;
        this.specialHoverTextures = new LinkedHashMap<>();
    }

    public <T extends TextureButton> T withTexture(ScreenElement tex) {
        this.tex = tex;
        return (T) this;
    }

    public <T extends TextureButton> T withHoverTexture(ScreenElement hoverTex) {
        this.hoverTex = hoverTex;
        return (T) this;
    }

    public <T extends TextureButton> T withSpecialHoverTexture(AllKeys modifier, ScreenElement specialTex) {
        specialHoverTextures.putLast(modifier, specialTex);
        return (T) this;
    }

    public void setTexture(ScreenElement tex) {
        this.tex = tex;
    }
    
    public void setHoverTexture(ScreenElement hoverTex) {
        this.hoverTex = hoverTex;
    }

    public void addSpecialHoverTexture(AllKeys modifier, ScreenElement specialTex) {
        specialHoverTextures.putLast(modifier, specialTex);
    }

    public void setNonFocusable() {
        this.focusable = false;
    }

    @Override
    public void setFocused(boolean focused) {
        super.setFocused(focusable && focused);
    }

    @Override
    public void doRender(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        if (visible) {
            isHovered = isMouseOver(mouseX, mouseY);
            ScreenElement t = (!isHoveredOrFocused() || hoverTex == null) ? tex : hoverTex;

            if (isHoveredOrFocused()) for (AllKeys modifier : specialHoverTextures.sequencedKeySet())
                if (AllKeys.isKeyDown(modifier.getKeybind().getKey().getValue())) t = specialHoverTextures.getOrDefault(modifier, hoverTex);

            t.render(graphics, getX(), getY());
        }
    }
}
