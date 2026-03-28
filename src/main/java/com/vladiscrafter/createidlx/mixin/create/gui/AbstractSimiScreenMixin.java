package com.vladiscrafter.createidlx.mixin.create.gui;

import com.vladiscrafter.createidlx.util.gui.CreateIDLXGuiTooltipBuffer;
import net.createmod.catnip.gui.AbstractSimiScreen;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractSimiScreen.class)
public abstract class AbstractSimiScreenMixin {

    @Inject(method = "render", at = @At("TAIL"))
    private void createidlx$renderDeferredTooltips(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        CreateIDLXGuiTooltipBuffer.renderDeferred(graphics);
    }
}