package com.vladiscrafter.createidlx.mixin.create.displayLink.source;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.content.redstone.displayLink.source.SingleLineDisplaySource;
import com.simibubi.create.foundation.gui.ModularGuiLineBuilder;
import com.simibubi.create.foundation.utility.CreateLang;
import com.vladiscrafter.createidlx.util.gui.CreateIDLXGuiTooltipBuffer;
import com.vladiscrafter.createidlx.util.widget.ModularGuiLineBuilderExt;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@OnlyIn(Dist.CLIENT)
@Mixin(SingleLineDisplaySource.class)
public abstract class SingleLineDisplaySourceClientMixin {

    @Unique
    List<Component> createidlx$labelTooltip = ImmutableList.of(
            CreateLang.translateDirect("display_source.label")
                    .withStyle(s -> s.withColor(0x5391E1)),
            CreateLang.translateDirect("gui.schedule.lmb_edit")
                    .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));

    @Inject(method = "addLabelingTextBox", at = @At("HEAD"))
    private void createidlx$cacheLabelingTextBoxTooltip(ModularGuiLineBuilder builder, CallbackInfo ci) {
        CreateIDLXGuiTooltipBuffer.registerTooltip("LabelingTextBox", createidlx$labelTooltip);
    }

    @Inject(method = "addLabelingTextBox", at = @At("HEAD"), cancellable = true)
    private void createidlx$replaceLabelingTextBox(ModularGuiLineBuilder builder, CallbackInfo ci) {
        ((ModularGuiLineBuilderExt) builder).createidlx$addLengthUnrestrainedTextInput(0, 137, (e, t) -> {
            e.setValue("");
            t.withTooltip(createidlx$labelTooltip);
        }, "Label");
        ci.cancel();
    }
}
