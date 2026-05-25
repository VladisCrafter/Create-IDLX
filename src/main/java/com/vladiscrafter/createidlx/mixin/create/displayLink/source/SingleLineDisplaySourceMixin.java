package com.vladiscrafter.createidlx.mixin.create.displayLink.source;

import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.source.SingleLineDisplaySource;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;
import com.simibubi.create.content.trains.display.FlapDisplayBlockEntity;
import com.simibubi.create.content.trains.display.FlapDisplayLayout;
import com.simibubi.create.content.trains.display.FlapDisplaySection;
import com.vladiscrafter.createidlx.util.bridge.FlapDisplayLayoutVisualizationConfigHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

import static com.simibubi.create.content.trains.display.FlapDisplaySection.*;
import static com.vladiscrafter.createidlx.util.CreateIDLXMixinUtils.*;

@Pseudo
@Mixin(SingleLineDisplaySource.class)
public abstract class SingleLineDisplaySourceMixin {
    @Invoker("allowsLabeling")
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    protected abstract boolean createidlx$invokeAllowsLabeling(DisplayLinkContext context);

    @Invoker("provideLine")
    protected abstract MutableComponent createidlx$invokeProvideLine(DisplayLinkContext context, DisplayTargetStats stats);

    @Invoker("getFlapDisplayLayoutName")
    protected abstract String createidlx$invokeGetFlapDisplayLayoutName(DisplayLinkContext context);

    @ModifyReturnValue(method = "provideText", at = @At("RETURN"))
    private List<MutableComponent> createidlx$placeholderifyProvideText(List<MutableComponent> originalValue,
                                                                        DisplayLinkContext context, DisplayTargetStats stats) {
        if (isCountdownFinished(context) && hasOverridingFinishLabel(context))
            return ImmutableList.of(Component.literal(context.sourceConfig().getString("FinishLabel")));

        if (originalValue.isEmpty()) return originalValue;
        if (!this.createidlx$invokeAllowsLabeling(context)) return originalValue;

        String label = context.sourceConfig().getString("Label");
        if (label.isEmpty()) {
            if (anyVisualizationConfigEnabled(context)) label = setToPrimitivePlaceholder();
            else return originalValue;
        }

        if (getTotalPlaceholdersCountInLabel(label) == 0) {
            if (anyVisualizationConfigEnabled(context)) label = appendPrimitivePlaceholder(label);
            else return originalValue;
        }

        if (!shouldBeProcessed(label) && !hasOverridingFinishLabel(context)) return originalValue;

        MutableComponent rawLine = this.createidlx$invokeProvideLine(context, stats);
        String information = (rawLine == SingleLineDisplaySource.EMPTY_LINE) ? "" : rawLine.getString();

        return ImmutableList.of(Component.literal(breakDownAndAssembleLabel(label, information)));
    }

    @ModifyReturnValue(method = "provideFlapDisplayText", at = @At("RETURN"))
    private List<List<MutableComponent>> createidlx$placeholderifyProvideFlapDisplayText(List<List<MutableComponent>> originalValue,
                                                                                         DisplayLinkContext context, DisplayTargetStats stats) {
        if (isCountdownFinished(context) && hasOverridingFinishLabel(context))
            return ImmutableList.of(ImmutableList.of(Component.literal(context.sourceConfig().getString("FinishLabel"))));

        if (originalValue.isEmpty()) return originalValue;

        if (!this.createidlx$invokeAllowsLabeling(context)) return originalValue;

        String layoutKey = createidlx$invokeGetFlapDisplayLayoutName(context);

        String label = context.sourceConfig().getString("Label");
        if (label.isEmpty()) {
            if (anyVisualizationConfigEnabled(context)) label = setToPrimitivePlaceholder();
            else return originalValue;
        }

        if (getTotalPlaceholdersCountInLabel(label) == 0) {
            if (anyVisualizationConfigEnabled(context)) label = appendPrimitivePlaceholder(label);
            else return originalValue;
        }

        if (!shouldBeProcessed(label) && !hasOverridingFinishLabel(context)) return originalValue;

        MutableComponent rawLine = this.createidlx$invokeProvideLine(context, stats);
        String information = (rawLine == SingleLineDisplaySource.EMPTY_LINE) ? "" : rawLine.getString();

        BlockEntity be = context.getTargetBlockEntity();
        if (be instanceof FlapDisplayBlockEntity) {
            FlapDisplayBlockEntity flapDisplay = ((FlapDisplayBlockEntity) be).getController();

            int maxLength = flapDisplay.getMaxCharCount();
            float maxWidth = maxLength * MONOSPACE;
            float valueWidth = Math.min(information.length() * (layoutKey.equals("Progress") ? WIDE_MONOSPACE / MONOSPACE : 1), maxWidth);

            ArrayList<String> labelSections = breakDownLabel(label);
            ArrayList<FlapDisplaySection> unclampedSections = new ArrayList<>();

            if (getCoveringPlaceholdersInLabel(label).getLeft()) {
                unclampedSections.add(createValueSection(valueWidth, layoutKey, information));
            }

            if (!labelSections.isEmpty()) for (int i = 0; i < labelSections.size(); i++) {
                unclampedSections.add(createLabelSection(labelSections.get(i)));
                if (labelSections.size() > i + 1) {
                    unclampedSections.add(createValueSection(valueWidth, layoutKey, information));
                }
            }
            if (getCoveringPlaceholdersInLabel(label).getRight()) {
                unclampedSections.add(createValueSection(valueWidth, layoutKey, information));
            }

            Pair<ArrayList<FlapDisplaySection>, Float> sectionsClampResult
                    = clampSections(unclampedSections, maxWidth, true, getMarkTruncationWithEllipsis(context));
            ArrayList<FlapDisplaySection> clampedSections = sectionsClampResult.getLeft();

            return List.of(assembleLabelFromSectionsAsComponentList(clampedSections));
        }

        return List.of(breakDownAndAssembleLabelAsComponentList(label, information));
    }

    @Inject(method = "loadFlapDisplayLayout", at = @At("HEAD"), cancellable = true)
    private void createidlx$placeholderifyLoadFlapDisplayLayout(DisplayLinkContext context, FlapDisplayBlockEntity flapDisplay,
                                                                FlapDisplayLayout layout, CallbackInfo ci) {
        if (!this.createidlx$invokeAllowsLabeling(context)) return;

        String layoutKey = createidlx$invokeGetFlapDisplayLayoutName(context);

        String label = context.sourceConfig().getString("Label");
        if (label.isEmpty()) {
            if (anyVisualizationConfigEnabled(context)) label = setToPrimitivePlaceholder();
            else return;
        }

        if (getTotalPlaceholdersCountInLabel(label) == 0) {
            if (anyVisualizationConfigEnabled(context)) label = appendPrimitivePlaceholder(label);
            else return;
        }

        if (!shouldBeProcessed(label) && (!hasOverridingFinishLabel(context))) return;

        DisplayTargetStats targetStats = context.blockEntity().activeTarget.provideStats(context);
        MutableComponent rawLine = createidlx$invokeProvideLine(context, targetStats);
        String information = rawLine == SingleLineDisplaySource.EMPTY_LINE ? "" : rawLine.getString();

        int maxLength = flapDisplay.getMaxCharCount();

        float maxWidth = maxLength * MONOSPACE;
        float valueWidth = Math.min(information.length() * (layoutKey.equals("Progress") ? WIDE_MONOSPACE / MONOSPACE : 1), maxWidth);

        String layoutName = buildLayoutSignature(label, layoutKey, Math.max(1, Math.min(information.length(), maxLength)), context);

        ArrayList<String> labelSections = breakDownLabel(label);
        ArrayList<FlapDisplaySection> unclampedSections = new ArrayList<>();

        if (getCoveringPlaceholdersInLabel(label).getLeft()) {
            unclampedSections.add(createValueSection(valueWidth, layoutKey, information));
        }

        if (!labelSections.isEmpty()) for (int i = 0; i < labelSections.size(); i++) {
            unclampedSections.add(createLabelSection(labelSections.get(i)));
            if (labelSections.size() > i + 1) {
                unclampedSections.add(createValueSection(valueWidth, layoutKey, information));
            }
        }
        if (getCoveringPlaceholdersInLabel(label).getRight()) {
            unclampedSections.add(createValueSection(valueWidth, layoutKey, information));
        }

        Pair<ArrayList<FlapDisplaySection>, Float> sectionsClampResult
                = clampSections(unclampedSections, maxWidth, true, getMarkTruncationWithEllipsis(context));
        ArrayList<FlapDisplaySection> clampedSections = sectionsClampResult.getLeft();

        float totalWidth = sectionsClampResult.getRight();
        int sectionSpaces = clampedSections.size() - 1;

        float leftSpaceWidth = totalWidth - sectionSpaces;
        if (leftSpaceWidth > 0f && !getCenterText(context)) {
            clampedSections.add(new FlapDisplaySection(leftSpaceWidth, "alphabet", false, false));
        }

        layout.configure(layoutName, ImmutableList.copyOf(clampedSections));

        /*if (layout instanceof FlapDisplayLayoutVisualizationConfigHolder holder)
            holder.createidlx$setCutOutSectionGaps(getCutOutSectionGaps(context));*/

        ci.cancel();
    }
}