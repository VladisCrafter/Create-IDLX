package com.vladiscrafter.createidlx.mixin.minecraft.gui;

import com.vladiscrafter.createidlx.util.ExpandedEditBoxUtils;
import com.vladiscrafter.createidlx.util.SingleLineDisplaySourceMixinUtils;
import com.vladiscrafter.createidlx.util.widget.ExpandedEditBox;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;

@Mixin(EditBox.class)
public abstract class EditBoxMixin {
    @Shadow @Final private Font font;
    @Shadow private int displayPos;
    @Shadow public abstract String getValue();
    @Shadow public abstract int getInnerWidth();
    @Shadow public abstract boolean isBordered();

    @Unique ArrayList<Integer> createidlx$escapeBackslashes;

    @SuppressWarnings("ConstantValue")
    @Inject(method = "renderWidget", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/util/FormattedCharSequence;IIIZ)I", ordinal = 0))
    private void createidlx$highlightSpecialCharacters(GuiGraphics graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (!((Object) this instanceof ExpandedEditBox eeb)) return;

        ExpandedEditBoxUtils.highlightSpecialCharacters(graphics, font, displayPos, getValue(),
                eeb.getX(), eeb.getY(), eeb.getWidth(), getInnerWidth(), isBordered());
    }
}
