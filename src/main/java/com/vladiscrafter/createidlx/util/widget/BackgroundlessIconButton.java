package com.vladiscrafter.createidlx.util.widget;

import com.simibubi.create.foundation.gui.widget.IconButton;
import com.vladiscrafter.createidlx.CreateIDLX;
import com.vladiscrafter.createidlx.util.ponder.PonderSceneOpener;
import net.createmod.catnip.gui.element.ScreenElement;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;

@SuppressWarnings({"UnusedReturnValue", "unused", "unchecked"})
public class BackgroundlessIconButton extends IconButton {
    protected ScreenElement hoveredIcon;

    public BackgroundlessIconButton(int x, int y, ScreenElement icon) {
        this(x, y, icon, icon);
    }

    public BackgroundlessIconButton(int x, int y, ScreenElement icon, ScreenElement hoveredIcon) {
        this(x, y, 18, 18, icon, hoveredIcon);
    }

    public BackgroundlessIconButton(int x, int y, int w, int h, ScreenElement icon) {
        this(x, y, w, h, icon, icon);
    }

    public BackgroundlessIconButton(int x, int y, int w, int h, ScreenElement icon, ScreenElement hoveredIcon) {
        super(x, y, w, h, icon);
        this.hoveredIcon = hoveredIcon;
    }

    public <T extends BackgroundlessIconButton> T withHoveredIcon(ScreenElement hoveredIcon) {
        this.hoveredIcon = hoveredIcon;
        return (T) this;
    }

    public <T extends BackgroundlessIconButton> T withPonderScene(String sceneId, Runnable screenCloser, boolean appendHint) {
        this.onClick = (mX, mY) -> {
            screenCloser.run();
            PonderSceneOpener.open(sceneId);
        };
        if (appendHint) this.getToolTip().addLast(CreateIDLX.translate("gui.generic.click_to_ponder").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
        return (T) this;
    }

    public void setHoveredIcon(ScreenElement hoveredIcon) {
        this.hoveredIcon = hoveredIcon;
    }

    public void setPonderScene(String sceneId, Runnable screenCloser, boolean appendHint) {
        this.onClick = (mX, mY) -> {
            screenCloser.run();
            PonderSceneOpener.open(sceneId);
        };
        if (appendHint) this.getToolTip().addLast(CreateIDLX.translate("gui.generic.click_to_ponder").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
    }

    @Override
    public void doRender(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        if (visible) {
            isHovered = isMouseOver(mouseX, mouseY);
            if (!isHoveredOrFocused() || hoveredIcon == null) icon.render(graphics, getX() + 1, getY() + 1);
            else hoveredIcon.render(graphics, getX() + 1, getY() + 1);
        }
    }
}
