package com.vladiscrafter.createidlx.mixin;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;
import com.simibubi.create.content.redstone.displayLink.source.SingleLineDisplaySource;
import com.simibubi.create.content.trains.display.FlapDisplayBlockEntity;
import com.simibubi.create.content.trains.display.FlapDisplayLayout;
import com.simibubi.create.content.trains.display.FlapDisplaySection;

import com.vladiscrafter.createidlx.CreateIDLX;
import com.vladiscrafter.createidlx.CreateIDLXMixinUtils;
import com.vladiscrafter.createidlx.config.CIDLXConfigs;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(SingleLineDisplaySource.class)
public abstract class SingleLineDisplaySourceMixin {

    // ------ INVOKERS ------

    @Invoker("provideLine")
    protected abstract MutableComponent createidlx$invokeProvideLine(DisplayLinkContext context, DisplayTargetStats stats);

    @Invoker("allowsLabeling")
    protected abstract boolean createidlx$invokeAllowsLabeling(DisplayLinkContext context);

    /**
    createSectionForValue() is currently unused since a single, non-separable literal component is passed to be displayed now

    (this might be changed in the future if add-on compatibility issues arise)
     */
    @Invoker("createSectionForValue")
    protected abstract FlapDisplaySection createidlx$invokeCreateSectionForValue(DisplayLinkContext context, int size);

    @Invoker("getFlapDisplayLayoutName")
    protected abstract String createidlx$invokeGetFlapDisplayLayoutName(DisplayLinkContext context);

    // ------ MODIFIERS & INJECTORS ------

    @ModifyReturnValue(method = "provideText", at = @At("RETURN"))
    private List<MutableComponent> createidlx$modifyProvideText(List<MutableComponent> originalValue,
                                                                DisplayLinkContext context, DisplayTargetStats stats) {
        boolean isCrudeProgressBarSupportEnabled = CIDLXConfigs.server.enableCrudeProgressBarSupport.get();

        if (originalValue.isEmpty()) return originalValue;

        if (!this.createidlx$invokeAllowsLabeling(context)) return originalValue;

        String layoutKey = createidlx$invokeGetFlapDisplayLayoutName(context);
        if (layoutKey.equals("Progress") && !isCrudeProgressBarSupportEnabled) return originalValue;

        String label = context.sourceConfig().getString("Label");
        if (label.isEmpty()) return originalValue;

        if (!CreateIDLXMixinUtils.hasUnescapedSpecifiers(label) && !CreateIDLXMixinUtils.hasEscapedSpecifiers(label)) return originalValue;

        MutableComponent raw = this.createidlx$invokeProvideLine(context, stats);
        if (raw == SingleLineDisplaySource.EMPTY_LINE) return originalValue;

        String fullLine = CreateIDLXMixinUtils.assembleFullLine(context, raw.getString());
        return ImmutableList.of(Component.literal(fullLine));
    }

    @ModifyReturnValue(method = "provideFlapDisplayText", at = @At("RETURN"))
    private List<List<MutableComponent>> createidlx$modifyFlapDisplayText(List<List<MutableComponent>> originalValue,
                                                                          DisplayLinkContext context, DisplayTargetStats stats) {
        boolean isCrudeProgressBarSupportEnabled = CIDLXConfigs.server.enableCrudeProgressBarSupport.get();

        if (originalValue.isEmpty()) return originalValue;

        if (!this.createidlx$invokeAllowsLabeling(context)) return originalValue;

        String layoutKey = createidlx$invokeGetFlapDisplayLayoutName(context);
        if (layoutKey.equals("Progress") && !isCrudeProgressBarSupportEnabled) return originalValue;

        String label = context.sourceConfig().getString("Label");
        if (label.isEmpty()) return originalValue;

        if (!CreateIDLXMixinUtils.hasUnescapedSpecifiers(label) && !CreateIDLXMixinUtils.hasEscapedSpecifiers(label)) return originalValue;

        MutableComponent raw = this.createidlx$invokeProvideLine(context, stats);
        if (raw == SingleLineDisplaySource.EMPTY_LINE) return originalValue;

        String fullLine = CreateIDLXMixinUtils.assembleFullLine(context, raw.getString());
        return ImmutableList.of(ImmutableList.of(Component.literal(fullLine)));
    }

    @Inject(method = "loadFlapDisplayLayout", at = @At("HEAD"), cancellable = true)
    private void createidlx$overrideFlapDisplayLayout(DisplayLinkContext context, FlapDisplayBlockEntity flapDisplay,
                                                      FlapDisplayLayout layout, CallbackInfo ci) {
        boolean isCrudeProgressBarSupportEnabled = CIDLXConfigs.server.enableCrudeProgressBarSupport.get();

        if (!this.createidlx$invokeAllowsLabeling(context)) return;

        String layoutKey = createidlx$invokeGetFlapDisplayLayoutName(context);
        if (layoutKey.equals("Progress") && !isCrudeProgressBarSupportEnabled) return;

        String label = context.sourceConfig().getString("Label");
        if (label.isEmpty()) return;

        if (!CreateIDLXMixinUtils.hasUnescapedSpecifiers(label) && !CreateIDLXMixinUtils.hasEscapedSpecifiers(label)) return;

        String layoutName = label.length() + "_Labeled_WithSpecifiers_" + layoutKey;
        if (layout.isLayout(layoutName))
            return;

        int maxCharCount = flapDisplay.getMaxCharCount();

        FlapDisplaySection section = new FlapDisplaySection(
                maxCharCount * FlapDisplaySection.MONOSPACE, "alphabet", false, false);

        layout.configure(layoutKey, ImmutableList.of(section));
        ci.cancel();
    }

    /**
     * This is an unfinished attempt at implementing assembling of multiple sections to respect each's layout type
     *
     * (Didn't go well from the start and I ended up postponing it due to lack of time)
     */
//    @Inject(method = "loadFlapDisplayLayout", at = @At("HEAD"), cancellable = true)
//    private void createidlx$overrideFlapDisplayLayout(DisplayLinkContext context, FlapDisplayBlockEntity flapDisplay,
//                                                      FlapDisplayLayout layout, CallbackInfo ci) {
//        if (!this.createidlx$invokeAllowsLabeling(context)) return;
//
//        String layoutKey = createidlx$invokeGetFlapDisplayLayoutName(context);
//
//        String label = context.sourceConfig().getString("Label");
//        if (label.isEmpty()) return;
//
//        int actualLabelSize = CreateIDLXMixinUtils.getLabelSizeAccountingSpecifiers(label);
//
//        if (!CreateIDLXMixinUtils.hasUnescapedSpecifiers(label) && !CreateIDLXMixinUtils.hasEscapedSpecifiers(label)) return;
//
//        ArrayList<Object> labelParts = CreateIDLXMixinUtils.breakDownLabelWithSpecifiers(label).getA();
//        int amountOfSpecifiers = CreateIDLXMixinUtils.breakDownLabelWithSpecifiers(label).getB();
//
//        String layoutName = "IDLX_WithSpecifiers_" + actualLabelSize + "_" + labelParts.toArray().length + "_" + amountOfSpecifiers;
//        if (layout.isLayout(layoutName)) {
//            ci.cancel();
//            return;
//        }
//        CreateIDLX.LOGGER.info("New layoutName: {}", layoutName);
//
//        int maxCharCount = flapDisplay.getMaxCharCount();
//        int valueCharCount = maxCharCount - actualLabelSize;
//
//        ImmutableList.Builder<FlapDisplaySection> sections = ImmutableList.builder();
//
//        for (Object part : labelParts) {
//            if (part instanceof String s) {
//                int size = Math.min(s.length(), maxCharCount);
//                CreateIDLX.LOGGER.info("Created section '{}' with size of {}", s, size);
//
//                sections.add(new FlapDisplaySection(
//                        size * FlapDisplaySection.MONOSPACE, "alphabet", false, false));
//                continue;
//            }
//
//            if (part instanceof Character) {
//                int valueSize = Math.max(1, Math.round((float) valueCharCount / amountOfSpecifiers));
//
//                FlapDisplaySection valueSection = createidlx$invokeCreateSectionForValue(context, valueSize);
//                CreateIDLX.LOGGER.info("Created valueSection '{}' with size of {} (actually {})", valueSection, valueSize, valueSection.getSize());
//
//                sections.add(valueSection);
//            }
//        }
//
//        layout.configure(layoutKey, sections.build());
//        ci.cancel();
//    }

}
