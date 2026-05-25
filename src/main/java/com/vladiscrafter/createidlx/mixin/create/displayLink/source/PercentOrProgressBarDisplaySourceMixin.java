package com.vladiscrafter.createidlx.mixin.create.displayLink.source;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.source.PercentOrProgressBarDisplaySource;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import static com.vladiscrafter.createidlx.util.CreateIDLXMixinUtils.*;

@Mixin(PercentOrProgressBarDisplaySource.class)
public class PercentOrProgressBarDisplaySourceMixin {
    @ModifyVariable(method = "provideLine", at = @At(value = "STORE"), name = "labelSize")
    private int createidlx$adaptProgressBarLabelSizeForPlaceholders(int labelSize, DisplayLinkContext context, DisplayTargetStats stats) {
        String label = context.sourceConfig().getString("Label");

        if (label.isEmpty() || getTotalPlaceholdersCountInLabel(label) == 0) return labelSize;
        else labelSize = breakDownAndAssembleLabel(label, "").length();

        return labelSize;
    }

    @ModifyExpressionValue(method = "provideLine", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/redstone/displayLink/source/PercentOrProgressBarDisplaySource;sizeForWideChars(I)I"))
    private int createidlx$adaptProgressBarLength(int length, DisplayLinkContext context, DisplayTargetStats stats) {
        String label = context.sourceConfig().getString("Label");
        int placeholders = getTotalPlaceholdersCountInLabel(label);

        if (label.isEmpty() || placeholders == 0) return length;
        else length = Math.max(1, (length / placeholders));

        return length;
    }
}
