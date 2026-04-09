package com.vladiscrafter.createidlx.mixin.create.displayLink;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllKeys;
import com.simibubi.create.api.behaviour.display.DisplaySource;
import com.simibubi.create.api.behaviour.display.DisplayTarget;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkBlockEntity;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkScreen;
import com.simibubi.create.content.redstone.displayLink.source.SingleLineDisplaySource;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.gui.widget.Label;
import com.simibubi.create.foundation.gui.widget.ScrollInput;
import com.simibubi.create.foundation.utility.CreateLang;
import com.vladiscrafter.createidlx.CreateIDLX;
import com.vladiscrafter.createidlx.util.gui.CreateIDLXGuiContext;
import com.vladiscrafter.createidlx.foundation.gui.CreateIDLXIcons;
import com.vladiscrafter.createidlx.config.CIDLXConfigs;
import com.vladiscrafter.createidlx.util.gui.CreateIDLXGuiTooltipBuffer;
import com.vladiscrafter.createidlx.util.ponder.PonderSceneOpener;
import com.vladiscrafter.createidlx.util.widget.InBoundsSelectionScrollInput;
import net.createmod.catnip.gui.AbstractSimiScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(DisplayLinkScreen.class)
public abstract class DisplayLinkScreenMixin extends AbstractSimiScreen {

    @Shadow private List<DisplaySource> sources;
    @Shadow private ScrollInput sourceTypeSelector;
    @Shadow private Label sourceTypeLabel;

    @Shadow protected abstract void initGathererSourceSubOptions(int i);

    @Shadow private DisplayLinkBlockEntity blockEntity;
    @Shadow private BlockState targetState;
    @Shadow private DisplayTarget target;

    @Shadow public abstract void onClose();

    @Unique private IconButton createidlx$placeholdersGuideButton;
    @Unique private IconButton createidlx$clipboardGuideButton;

    @Unique boolean createidlx$isPlaceholdersGuideButtonEnabled = CIDLXConfigs.client.enablePlaceholdersGuideButton.get();
    @Unique boolean createidlx$isActiveSpecifiersTooltipEnabled = CIDLXConfigs.client.enableActiveSpecifiersTooltip.get();
    @Unique boolean createidlx$isProgressBarSupportStateTooltipEnabled = CIDLXConfigs.client.enableProgressBarSupportStateTooltip.get();

    @Unique boolean createidlx$isDollarSignSpecifierEnabled = CIDLXConfigs.server.enableDollarSpecifier.get();
    @Unique boolean createidlx$isBracketsSpecifierEnabled = CIDLXConfigs.server.enableBracketsSpecifier.get();
    @Unique boolean createidlx$isCrudeProgressBarSupportEnabled = CIDLXConfigs.server.enableCrudeProgressBarSupport.get();

    @Inject(method = "initGathererOptions", at = @At("TAIL"))
    private void createidlx$replaceSourceTypeSelector(CallbackInfo ci) {
        if (sources == null || sources.isEmpty()) return;
        if (sourceTypeSelector instanceof InBoundsSelectionScrollInput) return;
        if (!CIDLXConfigs.client.truncateOverflowingStrings.get()) return;

        int currentState = Math.max(sources.indexOf(blockEntity.activeSource), 0);
        List<Component> options = sources.stream()
                .map(DisplaySource::getName)
                .toList();

        if (sources.size() > 1) {
            if (sourceTypeSelector == null) return;

            removeWidget(sourceTypeSelector);
            removeWidget(sourceTypeLabel);

            sourceTypeSelector = new InBoundsSelectionScrollInput(
                    guiLeft + 61, guiTop + 26, 135, 16, true, false)
                    .forOptions(options)
                    .writingTo(sourceTypeLabel)
                    .titled(CreateLang.translateDirect("display_link.information_type"))
                    .calling(this::initGathererSourceSubOptions)
                    .setState(currentState);

            addRenderableWidget(sourceTypeSelector);
            CreateIDLXGuiTooltipBuffer.registerWidget("SourceTypeSelector", sourceTypeSelector);
            initGathererSourceSubOptions(currentState);
            return;
        }

        removeWidget(sourceTypeLabel);

        sourceTypeSelector = new InBoundsSelectionScrollInput(
                guiLeft + 61, guiTop + 26, 135, 16, true, true)
                .forOptions(options)
                .writingTo(sourceTypeLabel)
                .titled(CreateLang.translateDirect("display_link.information_type"))
                .calling(this::initGathererSourceSubOptions)
                .setState(0);

        addRenderableWidget(sourceTypeSelector);
        CreateIDLXGuiTooltipBuffer.registerWidget("SourceTypeSelector", sourceTypeSelector);
        initGathererSourceSubOptions(0);
    }

    @Inject(method = "initGathererOptions", at = @At("TAIL"))
    private void createidlx$cacheTargetWidgetTooltip(CallbackInfo ci) {
        CreateIDLXGuiTooltipBuffer.registerTooltip("TargetWidget", List.of(
                CreateLang.translateDirect("display_link.writing_to"),
                targetState.getBlock().getName()
                        .withStyle(s -> s.withColor(target == null ? 0xF68989 : 0xF2C16D)),
                CreateLang.translateDirect("display_link.targeted_location"),
                CreateLang.translateDirect("display_link.view_compatible")
                        .withStyle(ChatFormatting.GRAY)
        ));
    }

    @Override
    protected void removeWidget(@NotNull GuiEventListener widget) {
        super.removeWidget(widget);
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
        if (!createidlx$isPlaceholdersGuideButtonEnabled) return;

        if (i < 0 || i >= sources.size()) return;

        DisplaySource source = sources.get(i);
        if (!(source instanceof SingleLineDisplaySource)) return;

        createidlx$placeholdersGuideButton = new IconButton(guiLeft + 36, guiTop + 46, 16, 16, CreateIDLXIcons.I_PLACEHOLDER);
        createidlx$placeholdersGuideButton.withCallback((mX, mY) -> {
            onClose();
            PonderSceneOpener.openByIndex(AllBlocks.DISPLAY_LINK.asStack(), 2);
        });

        createidlx$clipboardGuideButton = new IconButton(guiLeft + 36, guiTop + (blockEntity.activeSource instanceof SingleLineDisplaySource ? 67 : 46), 16, 16, CreateIDLXIcons.I_CLIPBOARD);
        createidlx$clipboardGuideButton.withCallback((mX, mY) -> {
            onClose();
            PonderSceneOpener.openByIndex(AllBlocks.DISPLAY_LINK.asStack(), 3);
        });

        createidlx$clipboardGuideButton.getToolTip().addAll(CreateIDLX.translateMultilineTooltip("gui.display_link.clipboard_tooltip", 3, 0x5391E1, ChatFormatting.GRAY.getColor()));
        createidlx$clipboardGuideButton.getToolTip().addLast(CreateIDLX.translate("gui.generic.click_to_ponder").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));

        this.addRenderableWidget(createidlx$placeholdersGuideButton);
        this.addRenderableWidget(createidlx$clipboardGuideButton);
    }

    @Inject(method = "renderWindow", at = @At("TAIL"))
    private void injectPlaceholdersStatus(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        if (createidlx$placeholdersGuideButton != null && !AllKeys.shiftDown()) {

            createidlx$placeholdersGuideButton.setToolTip(CreateIDLX.translate("gui.display_link.placeholders_tooltip_header").withColor(0x5391E1));
            createidlx$placeholdersGuideButton.getToolTip().addAll(CreateIDLX.translateMultilineTooltip("gui.display_link.placeholders_tooltip", 3, ChatFormatting.GRAY.getColor()));
            createidlx$placeholdersGuideButton.getToolTip().addAll(List.of(
                    CreateIDLX.translate("gui.display_link.placeholders_tooltip_hint").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC),
                    CreateIDLX.translate("gui.generic.click_to_ponder").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC)));

        } else if (createidlx$placeholdersGuideButton != null) {

            createidlx$placeholdersGuideButton.setToolTip(CreateIDLX.translate("gui.display_link.placeholders_tooltip_detailed_header").withColor(0x5391E1));

            if (createidlx$isActiveSpecifiersTooltipEnabled) {
                createidlx$placeholdersGuideButton.getToolTip().add(
                        ((createidlx$isDollarSignSpecifierEnabled || createidlx$isBracketsSpecifierEnabled) ? CreateIDLX.translate("gui.display_link.placeholders_tooltip_detailed_1",
                                ((createidlx$isDollarSignSpecifierEnabled && createidlx$isBracketsSpecifierEnabled) ? CreateIDLX.translate("gui.display_link.active_placeholder.both").withColor(0x53e053)
                                        : (!createidlx$isDollarSignSpecifierEnabled && createidlx$isBracketsSpecifierEnabled) ? CreateIDLX.translate("gui.display_link.active_placeholder.brackets_only").withColor(0xe0b653)
                                        : CreateIDLX.translate("gui.display_link.active_placeholder.dollar_only").withColor(0xe0b653))).withStyle(ChatFormatting.GRAY)
                                : CreateIDLX.translate("gui.display_link.placeholders_tooltip_detailed_1_disabled").withColor(0xe05353)));
            }

            if (createidlx$isProgressBarSupportStateTooltipEnabled && (createidlx$isDollarSignSpecifierEnabled || createidlx$isBracketsSpecifierEnabled)) {
                createidlx$placeholdersGuideButton.getToolTip().addAll(CreateIDLX.translateMultiline("gui.display_link.placeholders_tooltip_detailed_2", ChatFormatting.GRAY.getColor(),
                                (createidlx$isCrudeProgressBarSupportEnabled) ? CreateIDLX.translate("gui.display_link.progress_bar_support.enabled").withColor(0xe0b653)
                                        : CreateIDLX.translate("gui.display_link.progress_bar_support.disabled")));
            }

        }
    }
}
