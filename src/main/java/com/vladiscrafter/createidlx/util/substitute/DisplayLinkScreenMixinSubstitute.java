package com.vladiscrafter.createidlx.util.substitute;

import com.simibubi.create.api.behaviour.display.DisplaySource;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkBlockEntity;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.source.SingleLineDisplaySource;
import com.simibubi.create.content.redstone.nixieTube.NixieTubeBlockEntity;
import com.simibubi.create.content.trains.display.FlapDisplayBlockEntity;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.ModularGuiLine;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.gui.widget.Label;
import com.simibubi.create.foundation.gui.widget.ScrollInput;
import com.simibubi.create.foundation.utility.CreateLang;
import com.vladiscrafter.createidlx.CreateIDLX;
import com.vladiscrafter.createidlx.config.CIDLXConfigs;
import com.vladiscrafter.createidlx.content.displayLink.DisplayLinkRichLabelEditorScreen;
import com.vladiscrafter.createidlx.foundation.gui.CreateIDLXIcons;
import com.vladiscrafter.createidlx.mixin.accessor.catnip.UIRenderHelperAccessor;
import com.vladiscrafter.createidlx.mixin.accessor.create.ModularGuiLineAccessor;
import com.vladiscrafter.createidlx.mixin.accessor.create.SingleLineDisplaySourceAccessor;
import com.vladiscrafter.createidlx.util.bridge.DisplayLinkScreenMixinSubstitutionHolder;
import com.vladiscrafter.createidlx.util.bridge.DisplayLinkVisualizationConfigHolder;
import com.vladiscrafter.createidlx.util.gui.CreateIDLXGuiTooltipBuffer;
import com.vladiscrafter.createidlx.util.widget.BackgroundlessIconButton;
import com.vladiscrafter.createidlx.util.widget.ExpandedEditBox;
import com.vladiscrafter.createidlx.util.widget.InBoundsSelectionScrollInput;
import net.createmod.catnip.data.Pair;
import net.createmod.catnip.gui.ScreenOpener;
import net.createmod.catnip.gui.TextureSheetSegment;
import net.createmod.catnip.gui.UIRenderHelper;
import net.createmod.catnip.gui.widget.AbstractSimiWidget;
import net.createmod.catnip.theme.Color;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.vladiscrafter.createidlx.util.gui.UIRenderHelperAdditions.*;
import static net.minecraft.client.gui.screens.Screen.hasShiftDown;

public class DisplayLinkScreenMixinSubstitute {
    private ExpandedEditBox attachedLabelBox;
    private String attachedLabelText = "";
    private boolean attachedLabelTextInitialized;

    private boolean centerText;
    /*private boolean cutOutSectionGaps;*/
    private boolean markTruncationWithEllipsis;

    private IconButton richLabelEditorButton;
    private BackgroundlessIconButton clipboardGuideButton;

    private IconButton showVisualizationSettingsButton;
    private boolean visualizationSettingsInitialized = false;
    private boolean visualizationSettingsVisible = false;

    private IconButton centerTextButton;
    /*private IconButton cutOutSectionGapsButton;*/
    private IconButton markTruncationWithEllipsisButton;

    private final List<AbstractWidget> visualizationSettingWidgets = new ArrayList<>();

    private final List<Class<? extends BlockEntity>>
            visualizationSettingsSupporters = List.of(FlapDisplayBlockEntity.class, NixieTubeBlockEntity.class, SignBlockEntity.class),
            centerTextSupporters = List.of(FlapDisplayBlockEntity.class, NixieTubeBlockEntity.class);

    private final Component optionEnabled = CreateLang.translateDirect("gui.schematicannon.optionEnabled");
    private final Component optionDisabled = CreateLang.translateDirect("gui.schematicannon.optionDisabled");

    final boolean areGuideWidgetsEnabled = CIDLXConfigs.client.enableGuideWidgets.get();
    /*final boolean isActivePlaceholdersTooltipEnabled = CIDLXConfigs.client.enableActivePlaceholdersTooltip.get();*/
    final boolean isClipboardIconHighlightingEnabled = CIDLXConfigs.client.enableClipboardIconHighlighting.get();
    final boolean areRedirectsToPonderScenesEnabled = CIDLXConfigs.client.enableRedirectsToPonderScenes.get();
    final boolean areVisualizationSettingsButtonsEnabled = CIDLXConfigs.client.enableVisualizationSettingsButtons.get();
    final boolean areVisualizationSettingsButtonsAlwaysShown = CIDLXConfigs.client.alwaysShowVisualizationSettingsButtons.get();

    final boolean isRichLabelEditorEnabled = CIDLXConfigs.client.enableRichLabelEditor.get();
    final float richLabelEditorButtonOutlineFadeInTime = CIDLXConfigs.client.richLabelEditorButtonOutlineFadeInTime.getF();
    final float richLabelEditorButtonOutlineFadeOutTime = CIDLXConfigs.client.richLabelEditorButtonOutlineFadeOutTime.getF();
    final float richLabelEditorButtonOutlineIdleStateAlpha = (float) CIDLXConfigs.client.richLabelEditorButtonOutlineIdleStateAlpha.get() / 255;

    final boolean isDollarSignPlaceholderEnabled = CIDLXConfigs.server.enableDollarPlaceholder.get();
    final boolean isBracketsPlaceholderEnabled = CIDLXConfigs.server.enableBracketsPlaceholder.get();

    private float richLabelEditorButtonOutlineAlpha;

    private final DisplayLinkScreenMixinSubstitutionHolder screen;

    public DisplayLinkScreenMixinSubstitute(DisplayLinkScreenMixinSubstitutionHolder screen) {
        this.screen = screen;
        this.richLabelEditorButtonOutlineAlpha = richLabelEditorButtonOutlineIdleStateAlpha;
    }

    public void pullVisualizationSettings() {
        if (visualizationSettingsInitialized) return;

        CompoundTag visualizationConfig = ((DisplayLinkVisualizationConfigHolder) screen.createidlx$getBlockEntity()).createidlx$getVisualizationConfig();

        centerText = visualizationConfig.getBoolean("CenterText");
        /*createidlx$cutOutSectionGaps = visualizationConfig.getBoolean("CutOutSectionGaps");*/
        markTruncationWithEllipsis = visualizationConfig.getBoolean("MarkTruncationWithEllipsis");

        visualizationSettingsInitialized = true;
    }

    public void updateRichLabelEditorButtonOutlineAlpha() {
        if (richLabelEditorButton != null && richLabelEditorButton.isHovered())
            richLabelEditorButtonOutlineAlpha = richLabelEditorButtonOutlineFadeInTime == 0 ? 1f : Math.clamp(richLabelEditorButtonOutlineAlpha += ((float) 1 / (richLabelEditorButtonOutlineFadeInTime * 20)), richLabelEditorButtonOutlineIdleStateAlpha, 1f);
        else
            richLabelEditorButtonOutlineAlpha = richLabelEditorButtonOutlineFadeOutTime == 0 ? 0 : Math.clamp(richLabelEditorButtonOutlineAlpha -= ((float) 1 / (richLabelEditorButtonOutlineFadeOutTime * 20)), richLabelEditorButtonOutlineIdleStateAlpha, 1f);
    }

    public void replaceSourceTypeSelector() {
        List<DisplaySource> sources = screen.createidlx$getSources();
        ScrollInput sourceTypeSelector = screen.createidlx$getSourceTypeSelector();
        Label sourceTypeLabel = screen.createidlx$getSourceTypeLabel();
        DisplayLinkBlockEntity blockEntity = screen.createidlx$getBlockEntity();

        if (sources == null || sources.isEmpty()) return;
        if (sourceTypeSelector instanceof InBoundsSelectionScrollInput) return;
        if (!CIDLXConfigs.client.truncateOverflowingStrings.get()) return;

        int currentState = Math.max(sources.indexOf(blockEntity.activeSource), 0);
        List<Component> options = sources.stream()
                .map(DisplaySource::getName)
                .toList();

        if (sources.size() > 1) {
            if (sourceTypeSelector == null) return;

            screen.createidlx$callRemoveWidget(sourceTypeSelector);
            screen.createidlx$callRemoveWidget(sourceTypeLabel);

            sourceTypeSelector = new InBoundsSelectionScrollInput(
                    screen.createidlx$getGuiLeft() + 61, screen.createidlx$getGuiTop() + 26, 135, 16, true, false)
                    .forOptions(options)
                    .writingTo(sourceTypeLabel)
                    .titled(CreateLang.translateDirect("display_link.information_type"))
                    .calling(screen::createidlx$callInitGathererSourceSubOptions)
                    .setState(currentState);

            screen.createidlx$callAddRenderableWidget(sourceTypeSelector);
            CreateIDLXGuiTooltipBuffer.registerWidget("SourceTypeSelector", sourceTypeSelector);
            screen.createidlx$callInitGathererSourceSubOptions(currentState);
            return;
        }

        screen.createidlx$callRemoveWidget(sourceTypeLabel);

        sourceTypeSelector = new InBoundsSelectionScrollInput(
                screen.createidlx$getGuiLeft() + 61, screen.createidlx$getGuiTop() + 26, 135, 16, true, true)
                .forOptions(options)
                .writingTo(sourceTypeLabel)
                .titled(CreateLang.translateDirect("display_link.information_type"))
                .calling(screen::createidlx$callInitGathererSourceSubOptions)
                .setState(0);

        screen.createidlx$callAddRenderableWidget(sourceTypeSelector);
        CreateIDLXGuiTooltipBuffer.registerWidget("SourceTypeSelector", sourceTypeSelector);
        screen.createidlx$callInitGathererSourceSubOptions(0);
    }

    public void cacheTargetWidgetTooltip() {
        CreateIDLXGuiTooltipBuffer.registerTooltip("TargetWidget", List.of(
                CreateLang.translateDirect("display_link.writing_to"),
                screen.createidlx$getTargetState().getBlock().getName()
                        .withStyle(s -> s.withColor(screen.createidlx$getTarget() == null ? 0xF68989 : 0xF2C16D)),
                CreateLang.translateDirect("display_link.targeted_location"),
                CreateLang.translateDirect("display_link.view_compatible")
                        .withStyle(ChatFormatting.GRAY)
        ));
    }

    public void initRichLabelEditorButton(int i) {
        if (richLabelEditorButton != null) screen.createidlx$callRemoveWidget(richLabelEditorButton);

        if (i < 0 || i >= screen.createidlx$getSources().size()) return;
        DisplayLinkContext context = new DisplayLinkContext(screen.createidlx$getBlockEntity().getLevel(), screen.createidlx$getBlockEntity());
        if (!isRichLabelEditorButtonVisible(context)) return;

        richLabelEditorButton = new IconButton(screen.createidlx$getGuiLeft() + 36, screen.createidlx$getGuiTop() + 46, 16, 16, CreateIDLXIcons.placeholdersIcon);
        richLabelEditorButton.visible = allowsLabeling(context);
        richLabelEditorButton.withCallback(() -> {
            if (attachedLabelBox == null) return;
            attachedLabelText = attachedLabelBox.getValue();
            attachedLabelTextInitialized = true;
            ScreenOpener.open(new DisplayLinkRichLabelEditorScreen(attachedLabelText, (Screen) screen, this::setAttachedLabelText));
        });

        screen.createidlx$callAddRenderableWidget(richLabelEditorButton);
    }

    @SuppressWarnings("DataFlowIssue")
    public void initClipboardGuideButton(int i) {
        if (!areGuideWidgetsEnabled) return;

        if (clipboardGuideButton != null) screen.createidlx$callRemoveWidget(clipboardGuideButton);

        if (i < 0 || i >= screen.createidlx$getSources().size()) return;
        DisplayLinkContext context = new DisplayLinkContext(screen.createidlx$getBlockEntity().getLevel(), screen.createidlx$getBlockEntity());

        clipboardGuideButton = new BackgroundlessIconButton(screen.createidlx$getGuiLeft() + 36, screen.createidlx$getGuiTop() + (isRichLabelEditorButtonVisible(context) ? 67 : 46), 16, 16, CreateIDLXIcons.clipboardIcon);
        if (isClipboardIconHighlightingEnabled) clipboardGuideButton.setHoveredIcon(CreateIDLXIcons.I_CLIPBOARD_HL);
        clipboardGuideButton.getToolTip().addAll(CreateIDLX.translateMultilineTooltip("gui.display_link.clipboard_tooltip", 3, AbstractSimiWidget.HEADER_RGB.getRGB(), ChatFormatting.GRAY.getColor()));

        if (areRedirectsToPonderScenesEnabled)
            clipboardGuideButton.setPonderScene("clipboard_copying", screen::createidlx$callOnClose, true);
        else clipboardGuideButton.active = false;

        screen.createidlx$callAddRenderableWidget(clipboardGuideButton);
    }

    @SuppressWarnings("DataFlowIssue")
    public void initVisualizationSettingsButton(int i) {
        if (showVisualizationSettingsButton != null)
            screen.createidlx$callRemoveWidget(showVisualizationSettingsButton);

        AllGuiTextures background = AllGuiTextures.DATA_GATHERER;
        int x = screen.createidlx$getGuiLeft();
        int y = screen.createidlx$getGuiTop();

        if (i < 0 || i >= screen.createidlx$getSources().size()) return;
        DisplaySource source = screen.createidlx$getSources().get(i);

        BlockEntity target = Minecraft.getInstance().level.getBlockEntity(screen.createidlx$getBlockEntity().getTargetPosition());

        pullVisualizationSettings();

        if (!(supportsVisualizationSettings(target))) {
            visualizationSettingsVisible = false;
            initVisualizationSettings();
            return;
        }

        showVisualizationSettingsButton = new IconButton(x + 6, y + background.getHeight() - 24, CreateIDLXIcons.I_VISUALIZATION_SETTINGS);
        showVisualizationSettingsButton.withCallback(() -> {
            visualizationSettingsVisible ^= true;
            initVisualizationSettings();
        });
        showVisualizationSettingsButton.setToolTip(translateLocal("visualization_settings.show_visualization_settings"));
        if (areVisualizationSettingsButtonsEnabled) {
            screen.createidlx$callAddRenderableWidget(showVisualizationSettingsButton);

            if (areVisualizationSettingsButtonsAlwaysShown) {
                visualizationSettingsVisible = true;
                initVisualizationSettings();
            }
        }

        screen.createidlx$callTick();
    }

    @SuppressWarnings("DataFlowIssue")
    private void initVisualizationSettings() {
        screen.createidlx$callRemoveWidgets(visualizationSettingWidgets);
        visualizationSettingWidgets.clear();

        if (!visualizationSettingsVisible) return;

        AllGuiTextures background = AllGuiTextures.DATA_GATHERER;
        int x = screen.createidlx$getGuiLeft() + 36;
        int y = screen.createidlx$getGuiTop() + background.getHeight() - 24;

        centerTextButton = new IconButton(x, y, CreateIDLXIcons.I_CENTER_TEXT);
        centerTextButton.visible = supportsCenterText(Minecraft.getInstance().level.getBlockEntity(screen.createidlx$getBlockEntity().getTargetPosition()));
        centerTextButton.green = centerText;
        centerTextButton.withCallback(() -> centerTextButton.green ^= true);
        centerTextButton.setToolTip(translateLocal("visualization_settings.center_text"));
        if (supportsCenterText(Minecraft.getInstance().level.getBlockEntity(screen.createidlx$getBlockEntity().getTargetPosition()))) {
            Collections.addAll(visualizationSettingWidgets, centerTextButton);
            x += 26;
        }

        /*cutOutSectionGapsButton = new IconButton(x, y, CreateIDLXIcons.I_CUT_OUT_SECTION_GAPS);
        cutOutSectionGapsButton.green = cutOutSectionGaps;
        cutOutSectionGapsButton.withCallback(() -> {
            cutOutSectionGapsButton.green ^= true;
        });
        cutOutSectionGapsButton.setToolTip(translateLocal("visualization_settings.cut_out_section_gaps"));
        Collections.addAll(visualizationSettingWidgets, cutOutSectionGapsButton);
        x += 26;*/

        markTruncationWithEllipsisButton = new IconButton(x, y, CreateIDLXIcons.I_MARK_TRUNCATION_WITH_ELLIPSIS);
        markTruncationWithEllipsisButton.green = markTruncationWithEllipsis;
        markTruncationWithEllipsisButton.withCallback(() -> markTruncationWithEllipsisButton.green ^= true);
        markTruncationWithEllipsisButton.setToolTip(translateLocal("visualization_settings.mark_truncation_with_ellipsis"));
        Collections.addAll(visualizationSettingWidgets, markTruncationWithEllipsisButton);

        screen.createidlx$callAddRenderableWidgets(visualizationSettingWidgets);
        visualizationSettingsInitialized = true;
    }

    public void cacheLabelingTextBox() {
        attachedLabelBox = null;

        for (ModularGuiLine line : screen.createidlx$getConfigWidgets()) {
            for (Pair<AbstractWidget, String> widget : ((ModularGuiLineAccessor) line).createidlx$getWidgets()) {
                if (widget.getFirst() instanceof ExpandedEditBox editBox) {
                    attachedLabelBox = editBox;
                    break;
                }
            }
            if (attachedLabelBox != null) break;
        }
        if (attachedLabelBox == null) return;

        if (!attachedLabelTextInitialized) {
            attachedLabelText = attachedLabelBox.getValue();
            attachedLabelTextInitialized = true;
        } else attachedLabelBox.setValue(attachedLabelText);
    }

    public void setAttachedLabelText(String text) {
        attachedLabelText = text;
        attachedLabelTextInitialized = true;

        if (attachedLabelBox != null) attachedLabelBox.setValue(text);
    }

    public void renderRichEditorButtonOutline(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        int x = screen.createidlx$getGuiLeft(), y = screen.createidlx$getGuiTop(); // [122; 54]

        DisplayLinkContext context = new DisplayLinkContext(screen.createidlx$getBlockEntity().getLevel(), screen.createidlx$getBlockEntity());
        if (!isRichLabelEditorButtonVisible(context)) return;

        /*graphics.drawString(Minecraft.getInstance().font, String.format("[%d; %d]", mouseX, mouseY), x + 58, y + 96, 0xFFFFFF, true);*/
        AllGuiTextures.DATA_AREA_SPEECH.render(graphics, x + 57, y + 46);

        Color c = Color.WHITE.scaleAlpha(richLabelEditorButtonOutlineAlpha);
        int z = 1;

        drawColored(graphics, x + 34, y + 44, z, c, AllGuiTextures.BRASS_FRAME_TL);
        drawColored(graphics, x + 52, y + 44, z, c, AllGuiTextures.BRASS_FRAME_TR);
        drawColored(graphics, x + 34, y + 62, z, c, AllGuiTextures.BRASS_FRAME_BL);
        drawColored(graphics, x + 52, y + 62, z, c, AllGuiTextures.BRASS_FRAME_BR);

        drawStretchedColored(graphics, x + 34, y + 48, z, 3, 14, c, AllGuiTextures.BRASS_FRAME_LEFT);
        drawStretchedColored(graphics, x + 53, y + 48, z, 3, 14, c, AllGuiTextures.BRASS_FRAME_RIGHT);
        drawCroppedColored(graphics, x + 38, y + 44, z, 14, 3, c, AllGuiTextures.BRASS_FRAME_TOP);
        drawCroppedColored(graphics, x + 38, y + 63, z, 14, 3, c, AllGuiTextures.BRASS_FRAME_BOTTOM);
    }

    public void renderVisualizationSettingsTooltips() {
        if (!visualizationSettingsVisible) return;

        for (AbstractWidget widget : visualizationSettingWidgets)
            if (widget instanceof IconButton button) {
                if (!button.getToolTip().isEmpty()) {
                    button.setToolTip(button.getToolTip().getFirst());
                    button.getToolTip().add(holdShiftFixed());
                }
            }

        if (hasShiftDown()) {
            fillVisualizationSettingTooltip(centerTextButton, "center_text_tooltip");
            /*fillVisualizationSettingTooltip(cutOutSectionGapsButton, "cut_out_section_gaps_tooltip");*/
            fillVisualizationSettingTooltip(markTruncationWithEllipsisButton, "mark_truncation_with_ellipsis_tooltip");
        }
    }

    private MutableComponent holdShiftFixed() {
        return CreateLang.translateDirect("tooltip.holdForDescription", CreateLang.translateDirect("tooltip.keyShift")
                        .withStyle(hasShiftDown() ? ChatFormatting.WHITE : ChatFormatting.GRAY))
                .withStyle(ChatFormatting.DARK_GRAY);
    }

    @SuppressWarnings("DataFlowIssue")
    private void fillVisualizationSettingTooltip(IconButton button, String key) {
        if (!button.isHovered()) return;

        List<Component> tip = button.getToolTip();
        tip.add((button.green ? optionEnabled : optionDisabled).plainCopy()
                .withStyle(button.green ? ChatFormatting.DARK_GREEN : ChatFormatting.RED));
        tip.addAll(CreateIDLX.translateMultiline("gui.display_link.visualization_settings." + key, ChatFormatting.GRAY.getColor()));
    }

    public CompoundTag getVisualizationData(CompoundTag visualizationConfig) {
        CompoundTag visualizationData = new CompoundTag();

        visualizationData.putBoolean("CenterText",
                (centerTextButton != null)
                        ? centerTextButton.green : visualizationConfig.getBoolean("CenterText"));
        /*visualizationData.putBoolean("CutOutSectionGaps",
                (cutOutSectionGapsButton != null)
                ? cutOutSectionGapsButton.green : visualizationConfig.getBoolean("CutOutSectionGaps"));*/
        visualizationData.putBoolean("MarkTruncationWithEllipsis",
                (markTruncationWithEllipsisButton != null)
                        ? markTruncationWithEllipsisButton.green : visualizationConfig.getBoolean("MarkTruncationWithEllipsis"));

        return visualizationData;
    }

    private boolean isRichLabelEditorButtonVisible(DisplayLinkContext context) {
        return isRichLabelEditorEnabled && allowsLabeling(context);
    }

    private boolean allowsLabeling(DisplayLinkContext context) {
        DisplaySource source = context.blockEntity().activeSource;
        return source instanceof SingleLineDisplaySource && ((SingleLineDisplaySourceAccessor) source).createidlx$callAllowsLabeling(context);
    }

    private boolean supportsVisualizationSettings(BlockEntity target) {
        return visualizationSettingsSupporters.stream().anyMatch(c -> c.isInstance(target));
    }

    private boolean supportsCenterText(BlockEntity target) {
        return centerTextSupporters.stream().anyMatch(c -> c.isInstance(target));
    }

    private MutableComponent translateLocal(String key, Object... args) {
        return CreateIDLX.translate("gui.display_link." + key, args);
    }
}

