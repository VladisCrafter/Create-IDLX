package com.vladiscrafter.createidlx.mixin.accessor.catnip;

import net.createmod.catnip.gui.UIRenderHelper;
import net.createmod.catnip.theme.Color;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(UIRenderHelper.class)
public interface UIRenderHelperAccessor {
    @Invoker("drawTexturedQuad")
    static void createidlx$callDrawTexturedQuad(Matrix4f m, Color c, int left, int right, int top, int bot, int z, float u1, float u2, float v1, float v2) {
    }
}
