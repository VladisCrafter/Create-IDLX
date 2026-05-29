package com.vladiscrafter.createidlx.mixin.minecraft.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import com.vladiscrafter.createidlx.CreateIDLX;
import com.vladiscrafter.createidlx.config.CIDLXConfigs;
import com.vladiscrafter.createidlx.util.widget.ExpandedEditBox;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EditBox.class)
public abstract class EditBoxMixin {
    @Shadow @Final private Font font;
    @Shadow private int displayPos;
    @Shadow public abstract String getValue();
    @Shadow public abstract int getInnerWidth();
    @Shadow public abstract boolean isBordered();

    @SuppressWarnings("ConstantValue")
    @Inject(method = "renderWidget", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/util/FormattedCharSequence;IIIZ)I", shift = At.Shift.AFTER, ordinal = 0))
    private void createidlx$highlightDollarSigns(GuiGraphics graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        boolean isDollarSignPlaceholderEnabled = CIDLXConfigs.server.enableDollarPlaceholder.get();
        boolean isBracketsPlaceholderEnabled = CIDLXConfigs.server.enableBracketsPlaceholder.get();
        boolean isEscapingOfPlaceholdersEnabled = CIDLXConfigs.server.enableEscapingOfPlaceholders.get();

        if (!((Object) this instanceof ExpandedEditBox)) return;

        String value = getValue();
        if (value.isEmpty() || displayPos >= value.length()) return;

        String visible = font.plainSubstrByWidth(value.substring(displayPos), getInnerWidth());
        if (visible.isEmpty()) return;

        int x = isBordered() ? ((EditBox) (Object) this).getX() + 4 : ((EditBox) (Object) this).getX();
        int y = isBordered() ? ((EditBox) (Object) this).getY() + (((EditBox) (Object) this).getHeight() - 8) / 2 : ((EditBox) (Object) this).getY();

        int sY = y - 1 /*- 3*/; // TODO: configurably snap 'y' to field height ('- 3' here and '+ 6' below)
        int eY = sY + 10 /*+ 6*/;

        for (int i = 0; i < visible.length(); i++) {
            char c = visible.charAt(i);
            char pC = (i > 0) ? visible.charAt(i - 1) : 0;
            char nC = (i + 1 < visible.length()) ? visible.charAt(i + 1) : c;
            int w = font.width(String.valueOf(c));
            int nW = font.width(String.valueOf(nC));
            int pW = font.width(String.valueOf(pC));

            int regularDollarHighlightColor = -1728020480;
            int regularBracketsHighlightColor = -1728053120;
            int escapedDollarHighlightColor = -1716868608;
            int escapedBracketsHighlightColor = -1716894720;

            RenderType renderType = RenderType.gui/*Overlay*/(); // TODO: configurably 'guiOverlay()' (or too dim?)

            boolean dollar = (c == '$' && isDollarSignPlaceholderEnabled);
            boolean brackets = (c == '{' && i + 1 < visible.length() && nC == '}' && isBracketsPlaceholderEnabled);

            if (i == 0 || (i > 0 && pC != '\\') || !isEscapingOfPlaceholdersEnabled) {
                if (dollar) graphics.fill(renderType, x, sY, x + w, eY, regularDollarHighlightColor);
                else if (brackets) graphics.fill(renderType, x, sY, x + w + nW, eY, regularBracketsHighlightColor);
            } else if (i > 0 && pC == '\\' && isEscapingOfPlaceholdersEnabled) {
                if (dollar) graphics.fill(renderType, x - pW, sY, x + w, eY, escapedDollarHighlightColor);
                else if (brackets) graphics.fill(renderType, x - pW, sY, x + w + nW, eY, escapedBracketsHighlightColor);
            }
            x += w;
        }
    }
}