package com.vladiscrafter.createidlx.mixin.create.displayLink;

import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllKeys;
import com.simibubi.create.api.behaviour.display.DisplaySource;
import com.simibubi.create.api.behaviour.display.DisplayTarget;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkBlockEntity;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkScreen;
import com.simibubi.create.content.redstone.displayLink.source.SingleLineDisplaySource;
import com.simibubi.create.content.trains.display.FlapDisplayBlockEntity;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.gui.widget.Label;
import com.simibubi.create.foundation.gui.widget.ScrollInput;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.utility.CreateLang;
import com.vladiscrafter.createidlx.CreateIDLX;
import com.vladiscrafter.createidlx.util.bridge.DisplayLinkVisualizationConfigHolder;
import com.vladiscrafter.createidlx.util.gui.CreateIDLXGuiContext;
import com.vladiscrafter.createidlx.foundation.gui.CreateIDLXIcons;
import com.vladiscrafter.createidlx.config.CIDLXConfigs;
import com.vladiscrafter.createidlx.util.gui.CreateIDLXGuiTooltipBuffer;
import com.vladiscrafter.createidlx.util.ponder.PonderSceneOpener;
import com.vladiscrafter.createidlx.util.widget.InBoundsSelectionScrollInput;
import net.createmod.catnip.gui.AbstractSimiScreen;
import net.createmod.catnip.lang.FontHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Collections;
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

    @Shadow public abstract void tick();

    @Unique private IconButton createidlx$placeholdersGuideButton;
    @Unique private IconButton createidlx$clipboardGuideButton;

    @Unique private boolean createidlx$centerText;
    /*@Unique private boolean createidlx$cutOutSectionGaps;*/
    @Unique private boolean createidlx$markTruncationWithEllipsis;

    @Unique private IconButton createidlx$showVisualizationSettingsButton;
    @Unique private boolean createidlx$visualizationSettingsInitialized = false;
    @Unique private boolean createidlx$visualizationSettingsVisible = false;

    @Unique private IconButton createidlx$centerTextButton;
    /*@Unique private IconButton createidlx$cutOutSectionGapsButton;*/
    @Unique private IconButton createidlx$markTruncationWithEllipsisButton;

    @Unique private List<AbstractWidget> createidlx$visualizationSettingWidgets = new ArrayList<>();

    @Unique private final Component createidlx$optionEnabled = CreateLang.translateDirect("gui.schematicannon.optionEnabled");
    @Unique private final Component createidlx$optionDisabled = CreateLang.translateDirect("gui.schematicannon.optionDisabled");

    @Unique boolean createidlx$areGuideButtonsEnabled = CIDLXConfigs.client.enableGuideButtons.get();
    @Unique boolean createidlx$isActivePlaceholdersTooltipEnabled = CIDLXConfigs.client.enableActivePlaceholdersTooltip.get();
    @Unique boolean createidlx$isAlternativeClipboardIconEnabled = CIDLXConfigs.client.enableAlternativeClipboardIcon.get();
    @Unique boolean createidlx$areRedirectsToPonderScenesEnabled = CIDLXConfigs.client.enableRedirectsToPonderScenes.get();

    @Unique boolean isCreateidlx$areVisualizationSettingsButtonsEnabled = CIDLXConfigs.client.enableVisualizationSettingsButtons.get();

    @Unique boolean createidlx$isDollarSignPlaceholderEnabled = CIDLXConfigs.server.enableDollarPlaceholder.get();
    @Unique boolean createidlx$isBracketsPlaceholderEnabled = CIDLXConfigs.server.enableBracketsPlaceholder.get();

    @Inject(method = "tick", at = @At("TAIL"))
    private void createidlx$tickTooltips(CallbackInfo ci) {
        CompoundTag visualizationConfig = ((DisplayLinkVisualizationConfigHolder) blockEntity).createidlx$getVisualizationConfig();

        if (!createidlx$visualizationSettingsInitialized)
            createidlx$centerText = visualizationConfig.getBoolean("CenterText");
        /*if (!createidlx$visualizationSettingsInitialized)
            createidlx$cutOutSectionGaps = visualizationConfig.getBoolean("CutOutSectionGaps");*/
        if (!createidlx$visualizationSettingsInitialized)
            createidlx$markTruncationWithEllipsis = visualizationConfig.getBoolean("MarkTruncationWithEllipsis");
        createidlx$handleTooltips();
    }

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
    private void createidlx$initGuideButtons(int i, CallbackInfo ci) {
        if (!createidlx$areGuideButtonsEnabled) return;

        if (createidlx$placeholdersGuideButton != null) this.removeWidget(createidlx$placeholdersGuideButton);
        if (createidlx$clipboardGuideButton != null) this.removeWidget(createidlx$clipboardGuideButton);

        if (i < 0 || i >= sources.size()) return;
        DisplaySource source = sources.get(i);

        createidlx$placeholdersGuideButton = new IconButton(guiLeft + 36, guiTop + 46, 16, 16, CreateIDLXIcons.placeholdersIcon);
        createidlx$placeholdersGuideButton.visible = source instanceof SingleLineDisplaySource;
        if (createidlx$areRedirectsToPonderScenesEnabled) createidlx$placeholdersGuideButton.withCallback((mX, mY) -> {
            onClose();
            PonderSceneOpener.openByIndex(AllBlocks.DISPLAY_LINK.asStack(), 2);
        });
        else createidlx$placeholdersGuideButton.active = false;

        createidlx$clipboardGuideButton = new IconButton(guiLeft + 36, guiTop + (source instanceof SingleLineDisplaySource ? 67 : 46), 16, 16, CreateIDLXIcons.clipboardIcon);
        if (createidlx$isAlternativeClipboardIconEnabled) createidlx$clipboardGuideButton.setIcon(CreateIDLXIcons.I_CLIPBOARD_ITEM);
        if (createidlx$areRedirectsToPonderScenesEnabled) createidlx$clipboardGuideButton.withCallback((mX, mY) -> {
            onClose();
            PonderSceneOpener.openByIndex(AllBlocks.DISPLAY_LINK.asStack(), 3);
        });
        else createidlx$clipboardGuideButton.active = false;

        createidlx$clipboardGuideButton.getToolTip().addAll(CreateIDLX.translateMultilineTooltip("gui.display_link.clipboard_tooltip", 3, 0x5391E1, ChatFormatting.GRAY.getColor()));
        if (createidlx$areRedirectsToPonderScenesEnabled) createidlx$clipboardGuideButton.getToolTip().addLast(CreateIDLX.translate("gui.generic.click_to_ponder").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));

        this.addRenderableWidget(createidlx$placeholdersGuideButton);
        this.addRenderableWidget(createidlx$clipboardGuideButton);
    }

    @Inject(method = "initGathererSourceSubOptions", at = @At("TAIL"))
    private void createidlx$initVisualizationSettingsButton(int i, CallbackInfo ci) {

        if (createidlx$showVisualizationSettingsButton != null) this.removeWidget(createidlx$showVisualizationSettingsButton);

        AllGuiTextures background = AllGuiTextures.DATA_GATHERER;
        int x = guiLeft;
        int y = guiTop;

        if (i < 0 || i >= sources.size()) return;
        DisplaySource source = sources.get(i);

        assert minecraft != null;
        ClientLevel level = minecraft.level;
        BlockEntity target = level.getBlockEntity(blockEntity.getTargetPosition());

        if (!(source instanceof SingleLineDisplaySource) || !(target instanceof FlapDisplayBlockEntity)) {
            createidlx$visualizationSettingsVisible = false;
            createidlx$initVisualizationSettings();
            return;
        }

        createidlx$showVisualizationSettingsButton = new IconButton(x + 6, y + background.getHeight() - 24, CreateIDLXIcons.I_VISUALIZATION_SETTINGS);
        createidlx$showVisualizationSettingsButton.withCallback(() -> {
            createidlx$visualizationSettingsVisible ^= true;
            createidlx$initVisualizationSettings();
        });
        createidlx$showVisualizationSettingsButton.setToolTip(createidlx$translateLocal("visualization_settings.show_visualization_settings"));
        if (isCreateidlx$areVisualizationSettingsButtonsEnabled) addRenderableWidget(createidlx$showVisualizationSettingsButton);

        tick();
    }

    @Unique
    private void createidlx$initVisualizationSettings() {
        removeWidgets(createidlx$visualizationSettingWidgets);
        createidlx$visualizationSettingWidgets.clear();

        if (!createidlx$visualizationSettingsVisible) return;

        AllGuiTextures background = AllGuiTextures.DATA_GATHERER;
        int x = guiLeft + 36;
        int y = guiTop + background.getHeight() - 24;

        createidlx$centerTextButton = new IconButton(x, y, CreateIDLXIcons.I_CENTER_TEXT);
        createidlx$centerTextButton.green = createidlx$centerText;
        createidlx$centerTextButton.withCallback(() -> {
            createidlx$centerTextButton.green ^= true;
        });
        createidlx$centerTextButton.setToolTip(createidlx$translateLocal("visualization_settings.center_text"));
        Collections.addAll(createidlx$visualizationSettingWidgets, createidlx$centerTextButton);
        x += 26;

        /*createidlx$cutOutSectionGapsButton = new IconButton(x, y, CreateIDLXIcons.I_CUT_OUT_SECTION_GAPS);
        createidlx$cutOutSectionGapsButton.green = createidlx$cutOutSectionGaps;
        createidlx$cutOutSectionGapsButton.withCallback(() -> {
            createidlx$cutOutSectionGapsButton.green ^= true;
        });
        createidlx$cutOutSectionGapsButton.setToolTip(createidlx$translateLocal("visualization_settings.cut_out_section_gaps"));
        Collections.addAll(createidlx$visualizationSettingWidgets, createidlx$cutOutSectionGapsButton);
        x += 26;*/

        createidlx$markTruncationWithEllipsisButton = new IconButton(x, y, CreateIDLXIcons.I_MARK_TRUNCATION_WITH_ELLIPSIS);
        createidlx$markTruncationWithEllipsisButton.green = createidlx$markTruncationWithEllipsis;
        createidlx$markTruncationWithEllipsisButton.withCallback(() -> {
            createidlx$markTruncationWithEllipsisButton.green ^= true;
        });
        createidlx$markTruncationWithEllipsisButton.setToolTip(createidlx$translateLocal("visualization_settings.mark_truncation_with_ellipsis"));
        Collections.addAll(createidlx$visualizationSettingWidgets, createidlx$markTruncationWithEllipsisButton);

        addRenderableWidgets(createidlx$visualizationSettingWidgets);
        createidlx$visualizationSettingsInitialized = true;
    }

    @Unique
    private void createidlx$handleTooltips() {
        if (!createidlx$visualizationSettingsVisible) return;

        for (AbstractWidget w : createidlx$visualizationSettingWidgets)
            if (w instanceof IconButton button) {
                if (!button.getToolTip().isEmpty()) {
                    button.setToolTip(button.getToolTip().get(0));
                    button.getToolTip().add(TooltipHelper.holdShift(FontHelper.Palette.BLUE, hasShiftDown()));
                }
            }

        if (hasShiftDown()) {
            createidlx$fillVisualizationSettingTooltip(createidlx$centerTextButton, "center_text_tooltip");
            /*createidlx$fillVisualizationSettingTooltip(createidlx$cutOutSectionGapsButton, "cut_out_section_gaps_tooltip");*/
            createidlx$fillVisualizationSettingTooltip(createidlx$markTruncationWithEllipsisButton, "mark_truncation_with_ellipsis_tooltip");
        }
    }

    @Unique
    private void createidlx$fillVisualizationSettingTooltip(IconButton button, String key) {
        if (!button.isHovered()) return;

        List<Component> tip = button.getToolTip();
        tip.add((button.green ? createidlx$optionEnabled : createidlx$optionDisabled).plainCopy()
                .withStyle(button.green ? ChatFormatting.DARK_GREEN : ChatFormatting.RED));
        tip.addAll(CreateIDLX.translateMultiline("gui.display_link.visualization_settings." + key, ChatFormatting.GRAY.getColor()));
    }

    @Inject(method = "renderWindow", at = @At("TAIL"))
    private void createidlx$injectPlaceholdersStatus(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        if (createidlx$placeholdersGuideButton == null) return;

        if (!AllKeys.shiftDown()) {
            createidlx$placeholdersGuideButton.setToolTip(createidlx$translateLocal("placeholders_tooltip_header").withColor(0x5391E1));
            createidlx$placeholdersGuideButton.getToolTip().addAll(CreateIDLX.translateMultilineTooltip("gui.display_link.placeholders_tooltip", 3, ChatFormatting.GRAY.getColor()));
            createidlx$placeholdersGuideButton.getToolTip().add(createidlx$translateLocal("placeholders_tooltip_hint").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
        } else {
            createidlx$placeholdersGuideButton.setToolTip(createidlx$translateLocal("placeholders_tooltip_detailed_header").withColor(0x5391E1));

            if (createidlx$isActivePlaceholdersTooltipEnabled) {
                createidlx$placeholdersGuideButton.getToolTip().add(
                        ((createidlx$isDollarSignPlaceholderEnabled || createidlx$isBracketsPlaceholderEnabled) ? createidlx$translateLocal("placeholders_tooltip_detailed_1",
                                ((createidlx$isDollarSignPlaceholderEnabled && createidlx$isBracketsPlaceholderEnabled) ? createidlx$translateLocal("active_placeholder.both").withColor(0x53e053)
                                        : (!createidlx$isDollarSignPlaceholderEnabled && createidlx$isBracketsPlaceholderEnabled) ? createidlx$translateLocal("active_placeholder.brackets_only").withColor(0xe0b653)
                                        : createidlx$translateLocal("active_placeholder.dollar_only").withColor(0xe0b653))).withStyle(ChatFormatting.GRAY)
                                : createidlx$translateLocal("placeholders_tooltip_detailed_1_disabled").withColor(0xe05353)));
            }

//            if (createidlx$isProgressBarSupportStateTooltipEnabled && (createidlx$isDollarSignPlaceholderEnabled || createidlx$isBracketsPlaceholderEnabled)) {
//                createidlx$placeholdersGuideButton.getToolTip().addAll(CreateIDLX.translateMultiline("gui.display_link.placeholders_tooltip_detailed_2", ChatFormatting.GRAY.getColor(),
//                                (createidlx$isCrudeProgressBarSupportEnabled) ? createidlx$translateLocal("progress_bar_support.enabled").withColor(0xe0b653)
//                                        : createidlx$translateLocal("progress_bar_support.disabled")));
//            }

        }

        if (createidlx$areRedirectsToPonderScenesEnabled) createidlx$placeholdersGuideButton.getToolTip().addLast(CreateIDLX.translate("gui.generic.click_to_ponder").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
    }

    @Inject(method = "onClose", at = @At(value = "INVOKE", target = "Lnet/createmod/catnip/platform/services/NetworkHelper;sendToServer(Lnet/minecraft/network/protocol/common/custom/CustomPacketPayload;)V"))
    private void createidlx$writeVisualizationConfig(CallbackInfo ci, @Local(name = "sourceData") CompoundTag sourceData) {
        CompoundTag createidlx$visualizationConfig = ((DisplayLinkVisualizationConfigHolder) blockEntity).createidlx$getVisualizationConfig();
        CompoundTag visualizationData = new CompoundTag();

        visualizationData.putBoolean("CenterText",
                (createidlx$centerTextButton != null)
                ? createidlx$centerTextButton.green : createidlx$visualizationConfig.getBoolean("CenterText"));
        /*visualizationData.putBoolean("CutOutSectionGaps",
                (createidlx$cutOutSectionGapsButton != null)
                ? createidlx$cutOutSectionGapsButton.green : createidlx$visualizationConfig.getBoolean("CutOutSectionGaps"));*/
        visualizationData.putBoolean("MarkTruncationWithEllipsis",
                (createidlx$markTruncationWithEllipsisButton != null)
                ? createidlx$markTruncationWithEllipsisButton.green : createidlx$visualizationConfig.getBoolean("MarkTruncationWithEllipsis"));
        sourceData.put("Visualization", visualizationData);
    }

    @Unique
    private MutableComponent createidlx$translateLocal(String key, Object... args) {
        return CreateIDLX.translate("gui.display_link." + key, args);
    }
}
