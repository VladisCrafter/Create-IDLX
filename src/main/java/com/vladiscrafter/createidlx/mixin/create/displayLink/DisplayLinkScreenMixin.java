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
import javax.annotation.ParametersAreNonnullByDefault;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(DisplayLinkScreen.class)
public abstract class DisplayLinkScreenMixin extends AbstractSimiScreen {

    @Shadow(remap = false) private List<DisplaySource> sources;
    @Shadow(remap = false) private ScrollInput sourceTypeSelector;
    @Shadow(remap = false) private Label sourceTypeLabel;

    @Shadow(remap = false) protected abstract void initGathererSourceSubOptions(int i);

    @Shadow(remap = false) private DisplayLinkBlockEntity blockEntity;
    @Shadow(remap = false) private BlockState targetState;
    @Shadow(remap = false) private DisplayTarget target;

    @Shadow(remap = false) public abstract void onClose();

    @Unique private IconButton createidlx$placeholdersGuideButton;
    @Unique private IconButton createidlx$clipboardGuideButton;

    @Unique boolean createidlx$areGuideButtonsEnabled = CIDLXConfigs.client.enableGuideButtons.get();
    @Unique boolean createidlx$areGuideButtonRedirectsEnabled = CIDLXConfigs.client.enableGuideButtonRedirects.get();
    @Unique boolean createidlx$isActivePlaceholdersTooltipEnabled = CIDLXConfigs.client.enableActivePlaceholdersTooltip.get();
    @Unique boolean createidlx$isProgressBarSupportStateTooltipEnabled = CIDLXConfigs.client.enableProgressBarSupportStateTooltip.get();

    @Unique boolean createidlx$isDollarSignPlaceholderEnabled = CIDLXConfigs.server.enableDollarPlaceholder.get();
    @Unique boolean createidlx$isBracketsPlaceholderEnabled = CIDLXConfigs.server.enableBracketsPlaceholder.get();
    @Unique boolean createidlx$isCrudeProgressBarSupportEnabled = CIDLXConfigs.server.enableCrudeProgressBarSupport.get();

    @Inject(method = "initGathererOptions", at = @At("TAIL"), remap = false)
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

    @Inject(method = "initGathererOptions", at = @At("TAIL"), remap = false)
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
    protected void removeWidget(@ParametersAreNonnullByDefault GuiEventListener widget) {
        super.removeWidget(widget);
    }

    @Inject(method = "initGathererSourceSubOptions", at = @At("HEAD"), remap = false)
    private void createidlx$enterSourceConfig(int i, CallbackInfo ci) {
        CreateIDLXGuiContext.enter(sources.get(i));
    }

    @Inject(method = "initGathererSourceSubOptions", at = @At("RETURN"), remap = false)
    private void createidlx$exitSourceConfig(int i, CallbackInfo ci) {
        CreateIDLXGuiContext.exit();
    }

    @Inject(method = "initGathererSourceSubOptions", at = @At("TAIL"))
    private void createidlx$initGuideButtons(int i, CallbackInfo ci) {
        if (!createidlx$areGuideButtonsEnabled) return;

        if (createidlx$placeholdersGuideButton != null) this.removeWidget(createidlx$placeholdersGuideButton);
        if (createidlx$clipboardGuideButton != null) this.removeWidget(createidlx$clipboardGuideButton);

        if (i < 0 || i >= sources.size()) return;
        DisplaySource source = sources.get(i);

        createidlx$placeholdersGuideButton = new IconButton(guiLeft + 36, guiTop + 46, 16, 16, CreateIDLXIcons.placeholdersIcon);
        createidlx$placeholdersGuideButton.visible = source instanceof SingleLineDisplaySource;
        if (createidlx$areGuideButtonRedirectsEnabled) createidlx$placeholdersGuideButton.withCallback((mX, mY) -> {
            onClose();
            PonderSceneOpener.openByIndex(AllBlocks.DISPLAY_LINK.asStack(), 2);
        });
        else createidlx$placeholdersGuideButton.active = false;

        createidlx$clipboardGuideButton = new IconButton(guiLeft + 36, guiTop + (source instanceof SingleLineDisplaySource ? 67 : 46), 16, 16, CreateIDLXIcons.clipboardIcon);
        if (createidlx$areGuideButtonRedirectsEnabled) createidlx$clipboardGuideButton.withCallback((mX, mY) -> {
            onClose();
            PonderSceneOpener.openByIndex(AllBlocks.DISPLAY_LINK.asStack(), 3);
        });
        else createidlx$clipboardGuideButton.active = false;

        createidlx$clipboardGuideButton.getToolTip().addAll(CreateIDLX.translateMultilineTooltip("gui.display_link.clipboard_tooltip", 3, 0x5391E1, ChatFormatting.GRAY.getColor()));
        if (createidlx$areGuideButtonRedirectsEnabled) createidlx$clipboardGuideButton.getToolTip().add(CreateIDLX.translate("gui.generic.click_to_ponder").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));

        this.addRenderableWidget(createidlx$placeholdersGuideButton);
        this.addRenderableWidget(createidlx$clipboardGuideButton);
    }

    @Inject(method = "renderWindow", at = @At("TAIL"), remap = false)
    private void injectPlaceholdersStatus(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        if (createidlx$placeholdersGuideButton == null) return;

        if (!AllKeys.shiftDown()) {
            createidlx$placeholdersGuideButton.setToolTip(CreateIDLX.translate("gui.display_link.placeholders_tooltip_header").withStyle(s -> s.withColor(0x5391E1)));
            createidlx$placeholdersGuideButton.getToolTip().addAll(CreateIDLX.translateMultilineTooltip("gui.display_link.placeholders_tooltip", 3, ChatFormatting.GRAY.getColor()));
            createidlx$placeholdersGuideButton.getToolTip().add(CreateIDLX.translate("gui.display_link.placeholders_tooltip_hint").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
        } else {
            createidlx$placeholdersGuideButton.setToolTip(CreateIDLX.translate("gui.display_link.placeholders_tooltip_detailed_header").withStyle(s -> s.withColor(0x5391E1)));

            if (createidlx$isActivePlaceholdersTooltipEnabled) {
                createidlx$placeholdersGuideButton.getToolTip().add(
                        ((createidlx$isDollarSignPlaceholderEnabled || createidlx$isBracketsPlaceholderEnabled) ? CreateIDLX.translate("gui.display_link.placeholders_tooltip_detailed_1",
                                ((createidlx$isDollarSignPlaceholderEnabled && createidlx$isBracketsPlaceholderEnabled) ? CreateIDLX.translate("gui.display_link.active_placeholder.both").withStyle(s -> s.withColor(0x53e053))
                                        : (!createidlx$isDollarSignPlaceholderEnabled && createidlx$isBracketsPlaceholderEnabled) ? CreateIDLX.translate("gui.display_link.active_placeholder.brackets_only").withStyle(s -> s.withColor(0xe0b653))
                                        : CreateIDLX.translate("gui.display_link.active_placeholder.dollar_only").withStyle(s -> s.withColor(0xe0b653)))).withStyle(ChatFormatting.GRAY)
                                : CreateIDLX.translate("gui.display_link.placeholders_tooltip_detailed_1_disabled").withStyle(s -> s.withColor(0xe05353))));
            }

            if (createidlx$isProgressBarSupportStateTooltipEnabled && (createidlx$isDollarSignPlaceholderEnabled || createidlx$isBracketsPlaceholderEnabled)) {
                createidlx$placeholdersGuideButton.getToolTip().addAll(CreateIDLX.translateMultiline("gui.display_link.placeholders_tooltip_detailed_2", ChatFormatting.GRAY.getColor(),
                                (createidlx$isCrudeProgressBarSupportEnabled) ? CreateIDLX.translate("gui.display_link.progress_bar_support.enabled").withStyle(s -> s.withColor(0xe0b653))
                                        : CreateIDLX.translate("gui.display_link.progress_bar_support.disabled")));
            }

        }

        if (createidlx$areGuideButtonRedirectsEnabled) createidlx$placeholdersGuideButton.getToolTip().add(CreateIDLX.translate("gui.generic.click_to_ponder").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
    }
}
