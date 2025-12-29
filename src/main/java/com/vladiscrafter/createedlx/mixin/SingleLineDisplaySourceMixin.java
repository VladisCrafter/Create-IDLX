package com.vladiscrafter.createedlx.mixin;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;
import com.simibubi.create.content.redstone.displayLink.source.SingleLineDisplaySource;
import com.simibubi.create.content.trains.display.FlapDisplayBlockEntity;
import com.simibubi.create.content.trains.display.FlapDisplayLayout;
import com.simibubi.create.content.trains.display.FlapDisplaySection;
import com.simibubi.create.foundation.gui.ModularGuiLineBuilder;
import com.simibubi.create.foundation.utility.CreateLang;

import com.vladiscrafter.createedlx.CreateEDLX;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(SingleLineDisplaySource.class)
public abstract class SingleLineDisplaySourceMixin {

    protected abstract MutableComponent provideLine(DisplayLinkContext context, DisplayTargetStats stats);

    protected abstract boolean allowsLabeling(DisplayLinkContext context);

    @Unique
    protected String createedlx$fullLine = "";

    /**
     * @author VladisCrafter
     * @reason Inject the Attached Label tooltip with information about the $ specifier
     */
    @Overwrite
    @OnlyIn(Dist.CLIENT)
    protected void addLabelingTextBox(ModularGuiLineBuilder builder) {
        builder.addTextInput(0, 137, (e, t) -> {
            e.setValue("");
            t.withTooltip(ImmutableList.of(
                    CreateLang.translateDirect("display_source.label")
                            .withStyle(s -> s.withColor(0x5391E1)),
                    CreateEDLX.translate("gui.display_link.attached_label_edit_box_1")
                            .withStyle(ChatFormatting.GRAY),
                    CreateEDLX.translate("gui.display_link.attached_label_edit_box_2")
                            .withStyle(ChatFormatting.GRAY),
                    CreateLang.translateDirect("gui.schedule.lmb_edit")
                            .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC)
            ));
        }, "Label");
    }

    /**
     * @author VladisCrafter
     * @reason Make the Attached Label act as a pseudo-formatted string whenever a non-escaped $ specifier is present
     */
    @Overwrite
    public List<MutableComponent> provideText(DisplayLinkContext context, DisplayTargetStats stats) {
        MutableComponent line = provideLine(context, stats);
        if (line == SingleLineDisplaySource.EMPTY_LINE)
            return SingleLineDisplaySource.EMPTY;

        String lineStr = line.getString();

        if (allowsLabeling(context)) {
            String label = context.sourceConfig()
                    .getString("Label");

            if (!label.isEmpty()) {
                boolean hasSpecifiers = false;

                for (int i = 0; i < label.length(); i++) {
                    if ((i == 0 || label.charAt(i - 1) != '\\') && label.charAt(i) == '$') {
                        hasSpecifiers = true;
                        break;
                    }
                }

                if (hasSpecifiers) {
                    lineStr = label.replaceAll("(?<!\\\\)\\$", Matcher.quoteReplacement(lineStr));
                } else lineStr = label + " " + lineStr;

                lineStr = lineStr.replaceAll(Pattern.quote("\\" + "$"), Matcher.quoteReplacement("$")
                );
            }
        }

        createedlx$fullLine = lineStr;
        return ImmutableList.of(Component.literal(lineStr));
    }

//    /**
//     * @author VladisCrafter
//     * @reason Make the method return the previously formatted string as the Attached Label
//     */
//    @Overwrite
//    public List<List<MutableComponent>> provideFlapDisplayText(DisplayLinkContext context, DisplayTargetStats stats) {
//
//        if (allowsLabeling(context)) {
//            String label = context.sourceConfig()
//                    .getString("Label");
//            if (!label.isEmpty()) {
//                return ImmutableList.of(provideText(context, stats));
//            }
//        }
//
//        return ((SingleLineDisplaySource)(Object)this)
//                .super$provideFlapDisplayText(context, stats);
//    }

    @Inject(method = "provideFlapDisplayText", at = @At("HEAD"), cancellable = true)
    private void createedlx$overrideFlapDisplayText(DisplayLinkContext context, DisplayTargetStats stats,
                                                    CallbackInfoReturnable<List<List<MutableComponent>>> cir) {
        if (allowsLabeling(context)) {
            String label = context.sourceConfig().getString("Label");
            if (!label.isEmpty()) {
                cir.setReturnValue(ImmutableList.of(provideText(context, stats)));
            }
        }
    }

    /**
     * @author VladisCrafter
     * @reason Make the method account the fullLine length to calculate required display space
     */
    @Overwrite
    public void loadFlapDisplayLayout(DisplayLinkContext context, FlapDisplayBlockEntity flapDisplay,
                                      FlapDisplayLayout layout) {
        String layoutKey = getFlapDisplayLayoutName(context);

        if (!allowsLabeling(context)) {
            if (!layout.isLayout(layoutKey))
                layout.configure(layoutKey,
                        ImmutableList.of(createSectionForValue(context, flapDisplay.getMaxCharCount())));
            return;
        }

        String label = context.sourceConfig()
                .getString("Label");

        if (label.isEmpty()) {
            if (!layout.isLayout(layoutKey))
                layout.configure(layoutKey,
                        ImmutableList.of(createSectionForValue(context, flapDisplay.getMaxCharCount())));
            return;
        }

        String layoutName = createedlx$fullLine.length() + "_Labeled_" + layoutKey;
        if (layout.isLayout(layoutName))
            return;

        int maxCharCount = flapDisplay.getMaxCharCount();
        FlapDisplaySection labelSection = new FlapDisplaySection(
                Math.min(maxCharCount, createedlx$fullLine.length()) * FlapDisplaySection.MONOSPACE, "alphabet", false, false);

        if (createedlx$fullLine.length() < maxCharCount)
            layout.configure(layoutName,
                    ImmutableList.of(labelSection, createSectionForValue(context, maxCharCount - createedlx$fullLine.length())));
        else
            layout.configure(layoutName, ImmutableList.of(labelSection));
    }

    /**
     * @author VladisCrafter
     * @reason Make the whole mixin not crash for fox’s sake
     */
    @Overwrite
    protected String getFlapDisplayLayoutName(DisplayLinkContext context) {
        return "Default";
    }

    /**
     * @author VladisCrafter
     * @reason Same as above
     */
    @Overwrite
    protected FlapDisplaySection createSectionForValue(DisplayLinkContext context, int size) {
        return new FlapDisplaySection(size * FlapDisplaySection.MONOSPACE, "alphabet", false, false);
    }

}
