package com.vladiscrafter.createidlx.mixin.create;

import com.simibubi.create.api.behaviour.display.DisplaySource;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkScreen;
import com.simibubi.create.content.redstone.displayLink.source.SingleLineDisplaySource;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.gui.widget.Label;
import com.simibubi.create.foundation.gui.widget.ScrollInput;
import com.simibubi.create.foundation.utility.CreateLang;
import com.vladiscrafter.createidlx.CreateIDLX;
import com.vladiscrafter.createidlx.util.gui.CreateIDLXGuiContext;
import com.vladiscrafter.createidlx.util.CreateIDLXIcons;
import com.vladiscrafter.createidlx.config.CIDLXConfigs;
import com.vladiscrafter.createidlx.util.widget.InBoundsSelectionScrollInput;
import net.createmod.catnip.gui.AbstractSimiScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(DisplayLinkScreen.class)
public abstract class DisplayLinkScreenMixin extends AbstractSimiScreen {
//    @Unique
//    private IconButton createidlx$specifierHelpButton;

    @Shadow private List<DisplaySource> sources;
    @Shadow private ScrollInput sourceTypeSelector;
    @Shadow private Label sourceTypeLabel;

    @Shadow protected abstract void initGathererSourceSubOptions(int i);

    @Inject(method = "initGathererOptions", at = @At("TAIL"))
    private void createidlx$replaceSourceTypeSelector(CallbackInfo ci) {
        if (sources == null || sources.isEmpty()) return;
        if (sourceTypeSelector == null) return;
        if (sourceTypeSelector instanceof InBoundsSelectionScrollInput) return;
        if (!CIDLXConfigs.client.truncateOverflowingStrings.get()) return;

        int currentState = sourceTypeSelector.getState();

        List<Component> options = sources.stream()
                .map(DisplaySource::getName)
                .toList();

        removeWidget(sourceTypeSelector);
        removeWidget(sourceTypeLabel);

        sourceTypeSelector = new InBoundsSelectionScrollInput(
                guiLeft + 61, guiTop + 26, 135, 16, true)
                .forOptions(options)
                .writingTo(sourceTypeLabel)
                .titled(CreateLang.translateDirect("display_link.information_type"))
                .calling(this::initGathererSourceSubOptions)
                .setState(currentState);

        addRenderableWidget(sourceTypeSelector);

        initGathererSourceSubOptions(currentState);
    }

    @Override
    protected void removeWidget(GuiEventListener widget) {
        if (widget != null) super.removeWidget(widget);
    }

    @Inject(method = "initGathererSourceSubOptions", at = @At("HEAD"))
    private void createidlx$enterSourceConfig(int i, CallbackInfo ci) {
        CreateIDLXGuiContext.enter(sources.get(i));
    }

    @Inject(method = "initGathererSourceSubOptions", at = @At("RETURN"))
    private void createidlx$exitSourceConfig(int i, CallbackInfo ci) {
        CreateIDLXGuiContext.exit();
    }

    @Inject(method = "initGathererSourceSubOptions", at = @At("TAIL"))
    private void createidlx$injectGuideButtons(int i, CallbackInfo ci) {
        boolean isPlaceholdersGuideButtonEnabled = CIDLXConfigs.client.enablePlaceholdersGuideButton.get();
        boolean isActiveSpecifiersTooltipEnabled = CIDLXConfigs.client.enableActiveSpecifiersTooltip.get();
        boolean isProgressBarSupportStateTooltipEnabled = CIDLXConfigs.client.enableProgressBarSupportStateTooltip.get();

        if (!isPlaceholdersGuideButtonEnabled) return;

        boolean isDollarSignSpecifierEnabled = CIDLXConfigs.server.enableDollarSpecifier.get();
        boolean isBracketsSpecifierEnabled = CIDLXConfigs.server.enableBracketsSpecifier.get();
        boolean isCrudeProgressBarSupportEnabled = CIDLXConfigs.server.enableCrudeProgressBarSupport.get();

        if (i < 0 || i >= sources.size()) return;

        DisplaySource source = sources.get(i);
        if (!(source instanceof SingleLineDisplaySource)) return;

        IconButton placeholdersGuideButton = new IconButton(guiLeft + 36, guiTop + 46, 16, 16, CreateIDLXIcons.I_SPECIFIER);
        placeholdersGuideButton.active = false;
        placeholdersGuideButton.withCallback(() -> {});

        placeholdersGuideButton.getToolTip().addAll(List.of(
                CreateIDLX.translate("gui.display_link.placeholders_tooltip_header")
                        .withStyle(s -> s.withColor(0x5391E1)),
                CreateIDLX.translate("gui.display_link.placeholders_tooltip_1")
                        .withStyle(ChatFormatting.GRAY),
                CreateIDLX.translate("gui.display_link.placeholders_tooltip_2")
                        .withStyle(ChatFormatting.GRAY),
                CreateIDLX.translate("gui.display_link.placeholders_tooltip_3")
                        .withStyle(ChatFormatting.GRAY)
        ));

        if (isActiveSpecifiersTooltipEnabled) {
            placeholdersGuideButton.getToolTip().add(
                    ((isDollarSignSpecifierEnabled || isBracketsSpecifierEnabled) ? CreateIDLX.translate("gui.display_link.placeholders_tooltip_4",
                            ((isDollarSignSpecifierEnabled && isBracketsSpecifierEnabled) ? CreateIDLX.translate("gui.display_link.active_placeholder.both").withStyle(s -> s.withColor(0x53e053))
                                    : (!isDollarSignSpecifierEnabled && isBracketsSpecifierEnabled) ? CreateIDLX.translate("gui.display_link.active_placeholder.brackets_only").withStyle(s -> s.withColor(0xe0b653))
                                    : CreateIDLX.translate("gui.display_link.active_placeholder.dollar_only").withStyle(s -> s.withColor(0xe0b653)))).withStyle(ChatFormatting.DARK_GRAY)
                            : CreateIDLX.translate("gui.display_link.placeholders_tooltip_4_disabled").withStyle(s -> s.withColor(0xe05353)))
            );
        }

        if (isProgressBarSupportStateTooltipEnabled && (isDollarSignSpecifierEnabled || isBracketsSpecifierEnabled)) {
            placeholdersGuideButton.getToolTip().add(
                CreateIDLX.translate("gui.display_link.placeholders_tooltip_5",
                        (isCrudeProgressBarSupportEnabled)
                                ? CreateIDLX.translate("gui.display_link.progress_bar_support.enabled").withStyle(s -> s.withColor(0xe0b653))
                                : CreateIDLX.translate("gui.display_link.progress_bar_support.disabled")
                ).withStyle(ChatFormatting.DARK_GRAY)
            );
        }

        this.addRenderableWidget(placeholdersGuideButton);
//        this.createidlx$specifierHelpButton = placeholdersGuideButton;
    }

}
