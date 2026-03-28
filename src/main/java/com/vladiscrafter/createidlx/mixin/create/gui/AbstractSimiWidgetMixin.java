package com.vladiscrafter.createidlx.mixin.create.gui;

import com.vladiscrafter.createidlx.util.gui.CreateIDLXGuiTooltipBuffer;
import net.createmod.catnip.gui.widget.AbstractSimiWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(AbstractSimiWidget.class)
public abstract class AbstractSimiWidgetMixin {

    @Inject(method = "renderTooltip", at = @At("HEAD"), cancellable = true)
    private void createidlx$deferTooltips(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        if (!((AbstractSimiWidget) (Object) this).isHovered()) return;

        List<Component> tooltip = ((AbstractSimiWidget) (Object) this).getToolTip();
        if (!CreateIDLXGuiTooltipBuffer.isLabelingTextBoxTooltip(tooltip) && !CreateIDLXGuiTooltipBuffer.isTargetWidgetTooltip(tooltip)) return;

        CreateIDLXGuiTooltipBuffer.defer(tooltip, mouseX, mouseY);
        ci.cancel();
    }
}