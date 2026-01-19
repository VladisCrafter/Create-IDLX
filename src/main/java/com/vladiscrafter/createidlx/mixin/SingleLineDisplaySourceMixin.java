package com.vladiscrafter.createidlx.mixin;

import java.util.List;
import java.util.regex.Matcher;

import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;
import com.simibubi.create.content.redstone.displayLink.source.SingleLineDisplaySource;
import com.simibubi.create.content.trains.display.FlapDisplayBlockEntity;
import com.simibubi.create.content.trains.display.FlapDisplayLayout;
import com.simibubi.create.content.trains.display.FlapDisplaySection;
import com.simibubi.create.foundation.gui.ModularGuiLineBuilder;
import com.simibubi.create.foundation.utility.CreateLang;

import com.vladiscrafter.createidlx.CreateIDLX;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(SingleLineDisplaySource.class)
public abstract class SingleLineDisplaySourceMixin {

    // ------ OVERWRITES ------

    /**
     * @author VladisCrafter
     * @reason Inject the Attached Label tooltip with information about the $ and {} specifiers
     */
    @Overwrite
    @OnlyIn(Dist.CLIENT)
    protected void addLabelingTextBox(ModularGuiLineBuilder builder) {
        builder.addTextInput(0, 137, (e, t) -> {
            e.setValue("");
            t.withTooltip(ImmutableList.of(
                    CreateLang.translateDirect("display_source.label")
                            .withStyle(s -> s.withColor(0x5391E1)),
                    CreateIDLX.translate("gui.display_link.attached_label_edit_box_1")
                            .withStyle(ChatFormatting.GRAY),
                    CreateIDLX.translate("gui.display_link.attached_label_edit_box_2")
                            .withStyle(ChatFormatting.GRAY),
                    CreateIDLX.translate("gui.display_link.attached_label_edit_box_3")
                            .withStyle(ChatFormatting.GRAY),
                    CreateLang.translateDirect("gui.schedule.lmb_edit")
                            .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC)
            ));
        }, "Label");
    }

    // ------ UTILITY METHODS ------

    @Unique
    private static boolean createidlx$hasUnescapedSpecifiers(String s) {
        for (int i = 0; i < s.length(); i++) {
            if ((s.charAt(i) == '$' && (i == 0 || s.charAt(i - 1) != '\\'))
                    || (s.charAt(i) == '{' && s.charAt(i + 1) == '}' && (i == 0 || s.charAt(i - 1) != '\\'))) {
                return true;
            }
        }
        return false;
    }

    @Unique
    private static boolean createidlx$hasEscapedSpecifiers(String s) {
        for (int i = 0; i < s.length(); i++) {
            if ((s.charAt(i) == '$' && (i > 0 && s.charAt(i - 1) == '\\'))
                    || (s.charAt(i) == '{' && s.charAt(i + 1) == '}' && (i > 0 && s.charAt(i - 1) == '\\'))) {
                return true;
            }
        }
        return false;
    }

    @Unique
    private static String createidlx$assembleFullLine(DisplayLinkContext context, String raw) {
        String label = context.sourceConfig().getString("Label");
        if (label.isEmpty()) return raw;

        String result;
        if (createidlx$hasUnescapedSpecifiers(label)) {
            result = label.replaceAll("(?<!\\\\)\\$", Matcher.quoteReplacement(raw))
                    .replaceAll("(?<!\\\\)\\{}", Matcher.quoteReplacement(raw));
        } else {
            result = label + " " + raw;
        }
        return result.replaceAll("\\\\\\$", "\\$").replaceAll("\\\\\\{}", "{}");
    }

    // ------ INVOKERS ------

    @Invoker("provideLine")
    protected abstract MutableComponent createidlx$invokeProvideLine(DisplayLinkContext context, DisplayTargetStats stats);

    @Invoker("allowsLabeling")
    protected abstract boolean createidlx$invokeAllowsLabeling( DisplayLinkContext context);

    /**
    createSectionForValue() is currently unused since a single, non-separable literal component is passed to be displayed now

    (this might be changed in the future if add-on compatibility issues arise)
     */
    @Invoker("createSectionForValue")
    protected abstract FlapDisplaySection createidlx$invokeCreateSectionForValue(DisplayLinkContext context,int size);


    // ------ MODIFIERS & INJECTORS ------

    @ModifyReturnValue(method = "provideText", at = @At("RETURN"))
    private List<MutableComponent> createidlx$modifyProvideText(List<MutableComponent> originalValue,
                                                                DisplayLinkContext context, DisplayTargetStats stats) {
        if (originalValue.isEmpty()) return originalValue;

        if (!this.createidlx$invokeAllowsLabeling(context)) return originalValue;

        String label = context.sourceConfig().getString("Label");
        if (label.isEmpty()) return originalValue;

        if (!createidlx$hasUnescapedSpecifiers(label) && !createidlx$hasEscapedSpecifiers(label)) return originalValue;

        MutableComponent raw = this.createidlx$invokeProvideLine(context, stats);
        if (raw == SingleLineDisplaySource.EMPTY_LINE) return originalValue;

        String fullLine = createidlx$assembleFullLine(context, raw.getString());
        return ImmutableList.of(Component.literal(fullLine));
    }

    @ModifyReturnValue(method = "provideFlapDisplayText", at = @At("RETURN"))
    private List<List<MutableComponent>> createidlx$modifyFlapDisplayText(List<List<MutableComponent>> originalValue,
                                                                          DisplayLinkContext context, DisplayTargetStats stats) {
        if (!this.createidlx$invokeAllowsLabeling(context)) return originalValue;

        String label = context.sourceConfig().getString("Label");
        if (label.isEmpty()) return originalValue;

        if (!createidlx$hasUnescapedSpecifiers(label) && !createidlx$hasEscapedSpecifiers(label)) return originalValue;

        MutableComponent raw = this.createidlx$invokeProvideLine(context, stats);
        if (raw == SingleLineDisplaySource.EMPTY_LINE) return originalValue;

        String fullLine = createidlx$assembleFullLine(context, raw.getString());
        return ImmutableList.of(ImmutableList.of(Component.literal(fullLine)));
    }

    @Inject(method = "loadFlapDisplayLayout", at = @At("HEAD"), cancellable = true)
    private void createidlx$overrideFlapDisplayLayout(DisplayLinkContext context, FlapDisplayBlockEntity flapDisplay,
                                                      FlapDisplayLayout layout, CallbackInfo ci) {
        if (!this.createidlx$invokeAllowsLabeling(context)) return;

        String label = context.sourceConfig().getString("Label");
        if (label.isEmpty()) return;

        if (!createidlx$hasUnescapedSpecifiers(label) && !createidlx$hasEscapedSpecifiers(label)) return;

        String layoutKey = "IDLX_WithSpecifiers";

        if (layout.isLayout(layoutKey)) {
            ci.cancel();
            return;
        }

        int maxCharCount = flapDisplay.getMaxCharCount();

        FlapDisplaySection section = new FlapDisplaySection(
                maxCharCount * FlapDisplaySection.MONOSPACE, "alphabet", false, false);

        layout.configure(layoutKey, ImmutableList.of(section));
        ci.cancel();
    }

}
