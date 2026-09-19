package com.vladiscrafter.createidlx.util.gui;

import com.vladiscrafter.createidlx.mixin.accessor.catnip.UIRenderHelperAccessor;
import net.createmod.catnip.gui.TextureSheetSegment;
import net.createmod.catnip.gui.UIRenderHelper;
import net.createmod.catnip.theme.Color;
import net.minecraft.client.gui.GuiGraphics;

public class UIRenderHelperAdditions {
    public static void drawColored(GuiGraphics graphics, int left, int top, int z, Color color, TextureSheetSegment tex) {
        tex.bind();
        UIRenderHelper.drawColoredTexture(graphics, color, left, top, z, tex.getStartX(), tex.getStartY(), tex.getWidth(), tex.getHeight(), 256, 256);
    }

    public static void drawStretchedColored(GuiGraphics graphics, int left, int top, int z, int w, int h, Color color, TextureSheetSegment tex) {
        tex.bind();
        UIRenderHelperAccessor.createidlx$callDrawTexturedQuad(graphics.pose().last()
                        .pose(), color, left, left + w, top, top + h, z, tex.getStartX() / 256f, (tex.getStartX() + tex.getWidth()) / 256f,
                tex.getStartY() / 256f, (tex.getStartY() + tex.getHeight()) / 256f);
    }

    public static void drawCroppedColored(GuiGraphics graphics, int left, int top, int z, int w, int h, Color color, TextureSheetSegment tex) {
        tex.bind();
        UIRenderHelperAccessor.createidlx$callDrawTexturedQuad(graphics.pose().last()
                        .pose(), color, left, left + w, top, top + h, z, tex.getStartX() / 256f, (tex.getStartX() + w) / 256f,
                tex.getStartY() / 256f, (tex.getStartY() + h) / 256f);
    }

    public static void drawStretched(GuiGraphics graphics, int left, int top, int w, int h, TextureSheetSegment tex, int texW, int texH) {
        tex.bind();
        UIRenderHelperAccessor.createidlx$callDrawTexturedQuad(graphics.pose().last()
                        .pose(), Color.WHITE, left, left + w, top, top + h, 0, (float) tex.getStartX() / texW, (float) (tex.getStartX() + tex.getWidth()) / texW,
                (float) tex.getStartY() / texH, (float) (tex.getStartY() + tex.getHeight()) / texH);
    }
}
