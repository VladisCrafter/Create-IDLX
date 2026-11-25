package com.vladiscrafter.createidlx.content.displayLink;

import com.simibubi.create.AllKeys;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.gui.widget.Label;
import com.simibubi.create.foundation.gui.widget.SelectionScrollInput;
import com.vladiscrafter.createidlx.CreateIDLX;
import com.vladiscrafter.createidlx.config.CIDLXConfigs;
import com.vladiscrafter.createidlx.foundation.gui.CreateIDLXGuiTextures;
import com.vladiscrafter.createidlx.mixin.accessor.create.SelectionScrollInputAccessor;
import com.vladiscrafter.createidlx.mixin.accessor.minecraft.EditBoxAccessor;
import com.vladiscrafter.createidlx.util.attachedLabel.AttachedLabelProcessingUtils;
import com.vladiscrafter.createidlx.util.gui.PlaceholderColoringUtils;
import com.vladiscrafter.createidlx.util.gui.ScrollingTruncatedString;
import com.vladiscrafter.createidlx.util.gui.UIRenderHelperAdditions;
import com.vladiscrafter.createidlx.util.widget.PlaceholderColoringEditBox;
import com.vladiscrafter.createidlx.util.widget.TextureButton;
import com.vladiscrafter.createidlx.util.widget.rle.RLECaptureGroupScrollInput;
import com.vladiscrafter.createidlx.util.widget.rle.RLECheckbox;
import com.vladiscrafter.createidlx.util.widget.rle.RLEPlaceholderTabButton;
import net.createmod.catnip.config.ui.ConfigHelper;
import net.createmod.catnip.config.ui.SubMenuConfigScreen;
import net.createmod.catnip.gui.AbstractSimiScreen;
import net.createmod.catnip.gui.ScreenOpener;
import net.createmod.catnip.gui.UIRenderHelper;
import net.createmod.catnip.gui.element.ScreenElement;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.*;
import net.minecraft.util.FormattedCharSequence;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.vladiscrafter.createidlx.util.attachedLabel.AttachedLabelPart.*;
import static com.vladiscrafter.createidlx.util.attachedLabel.AttachedLabelPart.PlaceholderType.*;
import static com.vladiscrafter.createidlx.foundation.gui.CreateIDLXGuiTextures.*;
import static net.createmod.catnip.gui.widget.AbstractSimiWidget.HEADER_RGB;

public class DisplayLinkRichLabelEditorScreen extends AbstractSimiScreen {
    protected String text, oldText, cachedText, previewInput;
    MutableComponent previewOutput;
    protected Screen initiatorScreen;
    protected Consumer<String> onConfirm;

    protected LinkedHashMap<Integer, Placeholder> placeholders;
    int cachedPlaceholdersAmount;
    int selectedPlaceholderIndex;
    Placeholder selectedPlaceholder;

    protected TextureButton configButton;
    protected PlaceholderColoringEditBox labelBox;
    protected IconButton confirmButton, resetButton;

    protected TextureButton newPlaceholderTabButton;
    protected LinkedList<RLEPlaceholderTabButton> placeholderTabButtons;

    protected RLECheckbox standardPlaceholderCheckbox, alternativePlaceholderCheckbox;
    protected RLECaptureGroupScrollInput leftGroupScrollInput, rightGroupScrollInput;
    protected LinkedHashMap<CaptureGroupValueType, Number> leftGroupCache, rightGroupCache;
    protected Label leftGroupScrollInputLabel, rightGroupScrollInputLabel;
    protected Component leftGroupScrollInputLabelCache, rightGroupScrollInputLabelCache;
    protected SelectionScrollInput leftGroupRoundingScrollInput, rightGroupRoundingScrollInput;
    protected int leftGroupRoundingCache, rightGroupRoundingCache;
    protected Label leftGroupRoundingScrollInputLabel, rightGroupRoundingScrollInputLabel;
    protected Component leftGroupRoundingLabelCache, rightGroupRoundingLabelCache;
    protected SelectionScrollInput leftGroupOperationSelector, middleGroupOperationSelector, rightGroupOperationSelector;
    protected Label leftGroupOperationSelectorLabel, middleGroupOperationSelectorLabel, rightGroupOperationSelectorLabel;
    protected TextureButton resetCaptureGroupsButton;
    protected EditBox previewInputBox;

    private long previewOutputStringLastStateChangeMillis = Util.getMillis();

    final CreateIDLXGuiTextures BACKGROUND = RICH_LABEL_EDITOR;
    final int PLACEHOLDER_TABS_SPACE = 336;
    final Component header = Component.literal("Rich Label Editor");
    final Component plType = Component.literal("Type");
    final Component plCaptureGroups = Component.literal("Capture Groups");
    final Component plFlags = Component.literal("Flags");
    final Component plTypeS = Component.literal("Standard");
    final Component plTypeA = Component.literal("Alternative");
    final Component plValueNone = Component.literal("—");
    final Component plRoundingNone = Component.literal("–");
    final Component prInputExample = Component.literal("Input Information Example");
    final Component prOutputExample = Component.literal("A Formatted Output Line Will be Displayed Here");
    final List<Component> plOperations = List.of(Component.literal("Cut"), Component.literal("Preserve"), Component.literal("None"));
    final List<Component> plOperationsAlt = List.of(Component.literal("Drop"), Component.literal("Keep"), Component.literal("None"));
    final List<Component> plRoundingsD = List.of(Component.literal("Ceil"), Component.literal("Floor"), Component.literal("Auto"));
    final List<Component> plRoundingsS = List.of(Component.literal("↑"), Component.literal("↓"), Component.literal("⇵"));

    final DefaultClosingMode defaultClosingMode = CIDLXConfigs.client.rleDefaultClosingMode.get();
    final CounterPosition counterPosition = CIDLXConfigs.client.rleCounterPosition.get();
    final boolean enableScreenConfigButton = CIDLXConfigs.client.rleEnableScreenConfigButton.get();
    final boolean enableOutputLinePreview = CIDLXConfigs.client.rleEnableOutputLinePreview.get();
    final TabWidthDistributionMode tabWidthDistributionMode = CIDLXConfigs.client.rleTabWidthDistributionMode.get();
    final boolean configEnableClearerValueFieldSplitters = CIDLXConfigs.client.rleConfigEnableClearerValueFieldSplitters.get();
    final boolean rleConfigEnablePlaceholderPointer = CIDLXConfigs.client.rleConfigEnablePlaceholderPointer.get();
    final boolean rleConfigEnablePlaceholderPointerClamps = CIDLXConfigs.client.rleConfigEnablePlaceholderPointerClamps.get();
    final boolean enableResetCaptureGroupsButton = CIDLXConfigs.client.rleEnableResetCaptureGroupsButton.get();
    final boolean rleResetButtonHighlightFieldsToBeReset = CIDLXConfigs.client.rleResetButtonHighlightFieldsToBeReset.get();
    final boolean resetButtonHoldShiftToResetValues = CIDLXConfigs.client.rleResetButtonHoldShiftToResetValues.get();
    final boolean resetButtonHoldShiftToResetRoundings = CIDLXConfigs.client.rleResetButtonHoldShiftToResetRoundings.get();
    final boolean resetButtonHoldShiftToResetOperations = CIDLXConfigs.client.rleResetButtonHoldShiftToResetOperations.get();
    final boolean highlightProcessedInfoPartsInPreview = CIDLXConfigs.client.rleHighlightProcessedInfoPartsInPreview.get();
    final boolean underlineSelectedPlaceholderInPreview = CIDLXConfigs.client.rleUnderlineSelectedPlaceholderInPreview.get();
    final boolean enableOutputLinePreviewFieldHints = CIDLXConfigs.client.rleEnableOutputLinePreviewFieldHints.get();

    public DisplayLinkRichLabelEditorScreen(String text, Screen initiator, Consumer<String> onConfirm) {
        this.text = text;
        this.oldText = text;
        this.cachedText = text;
        this.previewInput = "";
        this.previewOutput = CommonComponents.EMPTY.copy();
        this.initiatorScreen = initiator;
        this.onConfirm = onConfirm;

        this.placeholders = new LinkedHashMap<>();
        this.cachedPlaceholdersAmount = -1;
        this.selectedPlaceholderIndex = 0;
        this.selectedPlaceholder = invalid.copy();

        this.placeholderTabButtons = new LinkedList<>();

        this.leftGroupCache = new LinkedHashMap<>(3);
        this.rightGroupCache = new LinkedHashMap<>(3);

        this.leftGroupScrollInputLabelCache = plValueNone.copy();
        this.rightGroupScrollInputLabelCache = plValueNone.copy();

        this.leftGroupRoundingCache = 2;
        this.rightGroupRoundingCache = 2;

        this.leftGroupRoundingLabelCache = plRoundingNone.copy();
        this.rightGroupRoundingLabelCache = plRoundingNone.copy();
    }

    @Override
    protected void init() {
        setWindowSize(BACKGROUND.getWidth(), BACKGROUND.getHeight());
        super.init();
        clearWidgets();

        int x = guiLeft, y = guiTop;

        labelBox = new PlaceholderColoringEditBox(font, x + 14, y + 120, 332, 8, CommonComponents.EMPTY);
        labelBox.setTextColor(0xffffff);
        labelBox.setFocused(false);
        labelBox.setMaxLength(Integer.MAX_VALUE);
        labelBox.setColoring(CIDLXConfigs.client.colorPlaceholdersInRichLabelEditor.get());
        labelBox.setValue(text);
        labelBox.setResponder(this::onLabelUpdate);

        int hX = x + BACKGROUND.getWidth() / 2 - font.width(header) / 2;
        configButton = new TextureButton(hX - 13, y + 3, 9, 9, RLE_CONFIG);
        configButton.visible = enableScreenConfigButton;
        configButton.withCallback(() -> ScreenOpener.open(SubMenuConfigScreen.find(ConfigHelper.ConfigPath.parse(
                                    "createidlx:client." + CIDLXConfigs.client.displayLinkGUI.getName()
                                            + "." + CIDLXConfigs.client.richLabelEditorCustomization.getName()
                                            + "." + CIDLXConfigs.client.rleEnableScreenConfigButton.getName()))));

        confirmButton = new IconButton(x + BACKGROUND.getWidth() - 25, y + BACKGROUND.getHeight() - 24, AllIcons.I_CONFIRM).withCallback(this::onConfirm);
        resetButton = new IconButton(x + 7, y + BACKGROUND.getHeight() - 24, AllIcons.I_REFRESH).withCallback(this::onReset);

        addRenderableWidgets(labelBox, configButton, confirmButton, resetButton);

        clearGroupCaches(true, true);
        onLabelUpdate(text, true);
        buildPlaceholderTabs(0);
        buildPreviewStuff();
    }

    private void buildPlaceholderTabs(int index) {
        if (placeholderTabButtons == null) placeholderTabButtons = new LinkedList<>();
        placeholderTabButtons.forEach(this::removeWidget);
        placeholderTabButtons.clear();
        if (newPlaceholderTabButton != null) removeWidget(newPlaceholderTabButton);

        int i = 0, x = guiLeft, tX = guiLeft + 4 + 2, y = guiTop + 16;
        boolean even = tabWidthDistributionMode == TabWidthDistributionMode.EVEN;

        if (even) tX += calculatePlaceholderButtonLeftGap();
        for (Placeholder placeholder : placeholders.sequencedValues()) {
            final int lI = i;
            RLEPlaceholderTabButton t = new RLEPlaceholderTabButton(tX, y,
                    (even ? calculatePlaceholderButtonWidth(false) : calculatePlaceholderButtonWidths().get(lI)), lI, placeholder);
            t.withCallback(() -> selectPlaceholder(true, lI, placeholder));
            t.withSecondaryCallback(() -> onPlaceholderUpdate(toStringIndex(t.getIndex()), t.getPlaceholder(), invalid.copy()));
            placeholderTabButtons.add(t);
            tX += (even ? calculatePlaceholderButtonWidth(true) : calculatePlaceholderButtonWidths().get(i) + 2);
            i++;
        }

        newPlaceholderTabButton = new TextureButton(x + (!placeholders.isEmpty() ? 345 : 171), y + 2, 9, 9, RLE_TABS_NEW, RLE_TABS_NEW_H);
        newPlaceholderTabButton.withCallback(() -> createNewPlaceholder());
        newPlaceholderTabButton.setNonFocusable();

        addRenderableWidgets(placeholderTabButtons);
        addRenderableWidget(newPlaceholderTabButton);

        int ri = Math.min(index, placeholders.size() - 1);
        selectPlaceholder(false, placeholders.isEmpty() ? -1 : ri, placeholders.isEmpty() ? invalid.copy() : placeholderTabButtons.get(ri).getPlaceholder());
    }

    private void buildPlaceholderConfig(int index) {
        List<? extends AbstractWidget> placeholderConfigWidgets = Arrays.asList(
                standardPlaceholderCheckbox, alternativePlaceholderCheckbox,
                leftGroupScrollInput, leftGroupScrollInputLabel,
                rightGroupScrollInput, rightGroupScrollInputLabel,
                leftGroupRoundingScrollInput, leftGroupRoundingScrollInputLabel,
                rightGroupRoundingScrollInput, rightGroupRoundingScrollInputLabel,
                leftGroupOperationSelector, leftGroupOperationSelectorLabel,
                middleGroupOperationSelector, middleGroupOperationSelectorLabel,
                rightGroupOperationSelector, rightGroupOperationSelectorLabel,
                resetCaptureGroupsButton);

        placeholderConfigWidgets.stream().filter(Objects::nonNull).forEach(this::removeWidget);

        if (index == -1 || placeholders.isEmpty()) return;

        Placeholder placeholder = placeholderTabButtons.get(index).getPlaceholder();
        int psi = toStringIndex(index);

        int x = guiLeft + 11, y = guiTop + 44;

        standardPlaceholderCheckbox = new RLECheckbox(x + 12, y + 19);
        alternativePlaceholderCheckbox = new RLECheckbox(x + 12, y + 36);

        standardPlaceholderCheckbox.setLinkedCheckbox(alternativePlaceholderCheckbox);
        alternativePlaceholderCheckbox.setLinkedCheckbox(standardPlaceholderCheckbox);

        standardPlaceholderCheckbox.withCallback(() -> togglePlaceholderType(psi, placeholder));
        alternativePlaceholderCheckbox.withCallback(() -> togglePlaceholderType(psi, placeholder));

        if (!placeholder.isType(INVALID)) {
            if (placeholder.isStandard()) standardPlaceholderCheckbox.setOn(true);
            else alternativePlaceholderCheckbox.setOn(true);
        }
        boolean alt = placeholder.isAlternative();

        leftGroupRoundingScrollInputLabel = new Label(x + 103, y + 17, CommonComponents.EMPTY).withShadow();
        leftGroupRoundingScrollInput = new SelectionScrollInput(x + 98, y + 11, 16, 18);
        leftGroupRoundingScrollInput.forOptions(plRoundingsD).format(plRoundingsS::get);
        leftGroupRoundingScrollInput.setState(placeholder.properties().leftGroup().rounding().ordinal());
        leftGroupRoundingScrollInput.calling(i -> onPlaceholderUpdate(psi, placeholder.copy(),
                placeholder.copy().withProperties(placeholder.properties().copy().withLeftGroup(placeholder.properties().leftGroup().copy()
                        .withRounding(CaptureGroupRounding.values()[i])))));
        ((SelectionScrollInputAccessor) leftGroupRoundingScrollInput).createidlx$callUpdateTooltip();
        if (placeholder.properties().leftGroup().operation() != CaptureGroupOperation.NONE) {
            leftGroupRoundingScrollInput.writingTo(leftGroupRoundingScrollInputLabel);
            leftGroupRoundingLabelCache = leftGroupRoundingScrollInputLabel.text;
        } else {
            leftGroupRoundingScrollInput.setActive(false);
            leftGroupRoundingScrollInputLabel.text = leftGroupRoundingLabelCache;
        }

        rightGroupRoundingScrollInputLabel = new Label(x + 229, y + 17, CommonComponents.EMPTY).withShadow();
        rightGroupRoundingScrollInput = new SelectionScrollInput(x + 224, y + 11, 16, 18);
        rightGroupRoundingScrollInput.forOptions(plRoundingsD).format(plRoundingsS::get);
        rightGroupRoundingScrollInput.setState(placeholder.properties().rightGroup().rounding().ordinal());
        rightGroupRoundingScrollInput.calling(i -> onPlaceholderUpdate(psi, placeholder.copy(),
                placeholder.copy().withProperties(placeholder.properties().copy().withRightGroup(placeholder.properties().rightGroup().copy()
                        .withRounding(CaptureGroupRounding.values()[i])))));
        ((SelectionScrollInputAccessor) rightGroupRoundingScrollInput).createidlx$callUpdateTooltip();
        if (placeholder.properties().rightGroup().operation() != CaptureGroupOperation.NONE) {
            rightGroupRoundingScrollInput.writingTo(rightGroupRoundingScrollInputLabel);
            rightGroupRoundingLabelCache = rightGroupRoundingScrollInputLabel.text;
        } else {
            rightGroupRoundingScrollInput.setActive(false);
            rightGroupRoundingScrollInputLabel.text = rightGroupRoundingLabelCache;
        }

        leftGroupScrollInputLabel = new Label(x + 118, y + 17, CommonComponents.EMPTY).withShadow();
        leftGroupScrollInput = new RLECaptureGroupScrollInput(x + 114, y + 11, 46, 18,
                placeholder.properties().leftGroup(), leftGroupCache);
        leftGroupScrollInput.withScrollCallback((pcg, cg) -> onPlaceholderUpdate(psi,
                    placeholder.copy().withProperties(placeholder.properties().copy().withLeftGroup(pcg.copy())),
                    placeholder.withProperties(placeholder.properties().copy().withLeftGroup(cg.copy())))).updateTooltip();
        leftGroupScrollInputLabel.colored(leftGroupScrollInput.isActive() ? 0xFFFFFF : 0xA3A3A3);
        if (placeholder.properties().leftGroup().operation() != CaptureGroupOperation.NONE) {
            leftGroupScrollInput.writingTo(leftGroupScrollInputLabel);
            leftGroupScrollInputLabelCache = leftGroupScrollInputLabel.text;
        } else leftGroupScrollInputLabel.text = leftGroupScrollInputLabelCache;

        rightGroupScrollInputLabel = new Label(x + 183, y + 17, CommonComponents.EMPTY).withShadow();
        rightGroupScrollInput = new RLECaptureGroupScrollInput(x + 178, y + 11, 46, 18,
                placeholder.properties().rightGroup(), rightGroupCache);
        rightGroupScrollInput.withScrollCallback((pcg, cg) -> onPlaceholderUpdate(psi,
                placeholder.copy().withProperties(placeholder.properties().copy().withRightGroup(pcg.copy())),
                placeholder.withProperties(placeholder.properties().copy().withRightGroup(cg.copy())))).updateTooltip();
        rightGroupScrollInputLabel.colored(rightGroupScrollInput.isActive() ? 0xFFFFFF : 0xA3A3A3);
        if (placeholder.properties().rightGroup().operation() != CaptureGroupOperation.NONE) {
            rightGroupScrollInput.writingTo(rightGroupScrollInputLabel);
            rightGroupScrollInputLabelCache = rightGroupScrollInputLabel.text;
        } else rightGroupScrollInputLabel.text = rightGroupScrollInputLabelCache;

        readjustPlaceholderConfigLabelPositions(x, y);

        leftGroupOperationSelectorLabel = new Label(x + 103, y + 38, CommonComponents.EMPTY).withShadow();
        leftGroupOperationSelector = new SelectionScrollInput(x + 98, y + 32, !alt ? 69 : 44, 18);
        leftGroupOperationSelector.forOptions(!alt ? plOperations : plOperationsAlt).writingTo(leftGroupOperationSelectorLabel)
                .setState(placeholder.properties().leftGroup().operation().ordinal());
        leftGroupOperationSelector.calling(s -> selectCaptureGroupOperation(psi, placeholder, 0, s));
        ((SelectionScrollInputAccessor) leftGroupOperationSelector).createidlx$callUpdateTooltip();

        if (alt) {
            middleGroupOperationSelectorLabel = new Label(x + 152, y + 38, CommonComponents.EMPTY).withShadow();
            middleGroupOperationSelector = new SelectionScrollInput(x + 147, y + 32, 44, 18);
            middleGroupOperationSelector.forOptions(plOperationsAlt).writingTo(middleGroupOperationSelectorLabel)
                    .setState(placeholder.properties().middleGroupOperation().ordinal());
            middleGroupOperationSelector.calling(s -> selectCaptureGroupOperation(psi, placeholder, 1, s));
            ((SelectionScrollInputAccessor) middleGroupOperationSelector).createidlx$callUpdateTooltip();
        }

        rightGroupOperationSelectorLabel = new Label(x + (!alt ? 176 : 201), y + 38, CommonComponents.EMPTY).withShadow();
        rightGroupOperationSelector = new SelectionScrollInput(x + (!alt ? 171 : 196), y + 32, !alt ? 69 : 44, 18);
        rightGroupOperationSelector.forOptions(!alt ? plOperations : plOperationsAlt).writingTo(rightGroupOperationSelectorLabel)
                .setState(placeholder.properties().rightGroup().operation().ordinal());
        rightGroupOperationSelector.calling(s -> selectCaptureGroupOperation(psi, placeholder, 2, s));
        ((SelectionScrollInputAccessor) rightGroupOperationSelector).createidlx$callUpdateTooltip();

        if (enableResetCaptureGroupsButton) {
            resetCaptureGroupsButton = new TextureButton(x + 164, y + 16, 11, 11, RLE_PL_CONFIG_RESET, RLE_PL_CONFIG_RESET_H);
            resetCaptureGroupsButton.withCallback(() -> resetPlaceholderProperties(psi, placeholder)); // TODO: mind the config when adding tooltip text
            if (resetButtonHoldShiftToResetValues || resetButtonHoldShiftToResetRoundings || resetButtonHoldShiftToResetOperations)
                resetCaptureGroupsButton.withSpecialHoverTexture(AllKeys.SHIFT_MODIFIER, RLE_PL_CONFIG_RESET_HR);
            resetCaptureGroupsButton.setNonFocusable();
        }

        addRenderableWidgets(standardPlaceholderCheckbox, alternativePlaceholderCheckbox,
                leftGroupRoundingScrollInputLabel, leftGroupRoundingScrollInput,
                rightGroupRoundingScrollInputLabel, rightGroupRoundingScrollInput,
                leftGroupScrollInputLabel, leftGroupScrollInput,
                rightGroupScrollInputLabel, rightGroupScrollInput,
                leftGroupOperationSelectorLabel, leftGroupOperationSelector,
                rightGroupOperationSelectorLabel, rightGroupOperationSelector);
        if (alt) addRenderableWidgets(middleGroupOperationSelectorLabel, middleGroupOperationSelector);
        if (enableResetCaptureGroupsButton) addRenderableWidget(resetCaptureGroupsButton);
    }

    private void readjustPlaceholderConfigLabelPositions(int x, int y) {
        if (leftGroupScrollInputLabel == null || rightGroupScrollInputLabel == null) return;

        int leftValueOverflow = font.width(leftGroupScrollInputLabel.text) - 40, rightValueOverflow = font.width(rightGroupScrollInputLabel.text) - 40;
        int maxLVO = 9 - font.width(leftGroupRoundingScrollInputLabel.text), maxRVO = 9 - font.width(rightGroupRoundingScrollInputLabel.text);

        if (leftValueOverflow < -4) leftGroupScrollInputLabel.setX(x + 118 + (font.width(leftGroupRoundingScrollInputLabel.text) - 6));

        if (leftValueOverflow > 0) leftGroupScrollInputLabel.setX(x + 118 - Math.min(leftValueOverflow, maxLVO));
        if (leftValueOverflow > maxLVO) leftGroupScrollInputLabel.text = Component.literal("..." + font
                .plainSubstrByWidth(leftGroupScrollInputLabel.text.getString(), 45 - font.width("..."), true));

        if (rightValueOverflow > 0) rightGroupScrollInputLabel.setX(x + 183 - Math.min(rightValueOverflow, maxRVO) - (rightValueOverflow == maxRVO ? 1 : 0));
        if (rightValueOverflow > maxRVO) rightGroupScrollInputLabel.text = Component.literal("..." + font
                .plainSubstrByWidth(rightGroupScrollInputLabel.text.getString(), 45 - font.width("..."), true));

        rightGroupRoundingScrollInputLabel.setX(x + 229 - (font.width(rightGroupRoundingScrollInputLabel.text) - 6));
    }

    private void buildPreviewStuff() {
        if (!enableOutputLinePreview) return;

        int x = guiLeft, y = guiTop + 142;

        if (previewInputBox != null) removeWidget(previewInputBox);

        previewInputBox = new EditBox(font, x + 8, y + 11, 345, 8, CommonComponents.EMPTY);
        previewInputBox.setBordered(false);
        previewInputBox.setMaxLength(128);
        previewInputBox.setTextColor(0x714A40);
        previewInputBox.setTextShadow(false);
        previewInputBox.setFocused(false);
        previewInputBox.setValue(previewInput);
        previewInputBox.setResponder(this::onPreviewInputUpdate);
        previewInputBox.setFormatter((s, i) -> previewInputBox.isFocused()
                ? FormattedCharSequence.forward(s, Style.EMPTY) : FormattedCharSequence.EMPTY);

        addRenderableWidget(previewInputBox);
    }

    @Override
    protected void renderWindow(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        int x = guiLeft;
        int y = guiTop;

        graphics.fillGradient(0, 0, this.width, this.height, -1072689136, -804253680);
        BACKGROUND.render(graphics, x, y);

        int hX = x + BACKGROUND.getWidth() / 2 - font.width(header) / 2;
        graphics.drawString(font, header, hX, y + 4, 0x505050, false);

        graphics.drawString(font, String.format("[%d; %d]", mouseX - x, mouseY - y), x + 10, y + 4, 0x505050, false);

        renderElements(graphics, x, y);

        if (!placeholders.isEmpty()) {
            graphics.drawString(font, String.format("Placeholders: %d; avg width: %d",
                    placeholders.size(), calculatePlaceholderButtonWidth(false)), x + 10, y - 10, 0xFFFFFF, false);

            if (tabWidthDistributionMode == TabWidthDistributionMode.EVEN) graphics.drawString(font, String.format("Gaps: %d (left: %d)",
                    calculatePlaceholderButtonGaps(), calculatePlaceholderButtonLeftGap()), x + 200, y - 10, 0xFFFFFF, false);
            else graphics.drawString(font, String.format("Tab widths: %s",
                    calculatePlaceholderButtonWidths()), x + 200, y - 10, 0xFFFFFF, false);
        }
    }

    private void renderElements(GuiGraphics graphics, int x, int y) {
        RLE_FIELD.render(graphics, x, y + 106);
        renderPlaceholderConfigStuff(graphics, x, y);
        renderCounter(graphics, x, y);
        renderPreviewStuff(graphics, x, y + 142);
        UIRenderHelperAdditions.drawStretched(graphics, x + 29, y + 210, 302, 24, RLE_BOTTOM_TABS_COVER, 512, 512);
    }

    private void renderPlaceholderConfigStuff(GuiGraphics graphics, int x, int y) {
        UIRenderHelperAdditions.drawStretched(graphics, x + 3, y + 15, 354, 16, RLE_TABS_BG_E, 512, 512);
        UIRenderHelperAdditions.drawStretched(graphics, x + 4, y + 15, 352, 16, RLE_TABS_BG_M, 512, 512);

        if (placeholders == null || placeholders.isEmpty()) return;

        RLE_TABS_SEPARATOR.render(graphics, x + 342, y + 17);

        RLE_PL_CONFIG_BG_L.render(graphics, x + 5, y + 38);
        UIRenderHelperAdditions.drawStretched(graphics, x + 11, y + 38, 338, 66, RLE_PL_CONFIG_BG_M, 512, 512);
        RLE_PL_CONFIG_BG_R.render(graphics, x + 349, y + 38);

        RLE_PL_CONFIG_SEPARATOR.render(graphics, x + 107 - 6, y + 46);
        RLE_PL_CONFIG_SEPARATOR.render(graphics, x + 249 + 6, y + 46);

        int sideNameplateInnerWidth = Math.max(40, Math.clamp(Math.max(font.width(plType) + 2, font.width(plFlags) + 2), 20, 92));
        int middleNameplateInnerWidth = Math.max(90, Math.clamp(font.width(plCaptureGroups) + 2 + 18, 20, 134));
        if (middleNameplateInnerWidth % 2 == 1 && middleNameplateInnerWidth < 134) middleNameplateInnerWidth++;

        int lX = x - 3 + 11 + 48 - ((int) Math.ceil((double) sideNameplateInnerWidth / 2));
        int mX = x + 11 + 100 + 69 - ((int) Math.ceil((double) middleNameplateInnerWidth / 2));
        int rX = x + 3 + 11 + 242 + 48 - ((int) Math.ceil((double) sideNameplateInnerWidth / 2))
                + ((sideNameplateInnerWidth % 2 == 1 && sideNameplateInnerWidth < 92) ? 1 : 0);

        int fY = y + 34;

        RLE_PL_CONFIG_FG_L.render(graphics, lX - 2, fY);
        UIRenderHelperAdditions.drawStretched(graphics, lX, fY, sideNameplateInnerWidth, 16, RLE_PL_CONFIG_FG_M, 512, 512);
        RLE_PL_CONFIG_FG_R.render(graphics, lX + sideNameplateInnerWidth, fY);

        RLE_PL_CONFIG_FG_L.render(graphics, mX - 2, fY);
        UIRenderHelperAdditions.drawStretched(graphics, mX, fY, middleNameplateInnerWidth, 16, RLE_PL_CONFIG_FG_M, 512, 512);
        RLE_PL_CONFIG_FG_R.render(graphics, mX + middleNameplateInnerWidth, fY);

        RLE_PL_CONFIG_FG_L.render(graphics, rX - 2, fY);
        UIRenderHelperAdditions.drawStretched(graphics, rX, fY, sideNameplateInnerWidth, 16, RLE_PL_CONFIG_FG_M, 512, 512);
        RLE_PL_CONFIG_FG_R.render(graphics, rX + sideNameplateInnerWidth, fY);

        graphics.drawCenteredString(font, plType, x - 3 + 11 + 48, fY + 4, 0xffffff);
        graphics.drawCenteredString(font, plCaptureGroups, x + 11 + 100 + 69, fY + 4, 0xffffff);
        graphics.drawCenteredString(font, plFlags, x + 3 + 11 + 242 + 48, fY + 4, 0xffffff);

        graphics.drawString(font, plTypeS, x + 36, y + 64, 0xffffff);
        RLE_PL_CONFIG_TYPES_CONNECTOR.render(graphics, x + 27, y + 72);
        graphics.drawString(font, plTypeA, x + 36, y + 81, 0xffffff);

        boolean cs = configEnableClearerValueFieldSplitters;
        boolean alt = selectedPlaceholder.isAlternative();
        int leftRoundingBoxExt = ((leftGroupRoundingScrollInput != null && leftGroupRoundingScrollInput.getState() == 2) ? 1 : 0) - (cs ? 1 : 0);
        int rightRoundingBoxExt = ((rightGroupRoundingScrollInput != null && rightGroupRoundingScrollInput.getState() == 2) ? 1 : 0) + (cs ? 1 : 0);
        ScreenElement splitter = cs ? AllGuiTextures.DATA_AREA_END : RLE_PL_CONFIG_TYPES_CONNECTOR;
        if (selectedPlaceholder.type() != INVALID) {
            drawInputBox(graphics, x + 124, y + 56, 47);
            drawInputBox(graphics, x + 109, y + 56, 16 + leftRoundingBoxExt);
            drawInputBox(graphics, x + 234, y + 56, 17);
            drawInputBox(graphics, x + 189, y + 56, 47 - rightRoundingBoxExt);

            drawInputBox(graphics, x + 109, y + 56, 62);
            drawInputBox(graphics, x + 189, y + 56, 62);
            splitter.render(graphics, x + 124 + leftRoundingBoxExt, y + (cs ? 56 : 61));
            splitter.render(graphics, x + 235 - rightRoundingBoxExt, y + (cs ? 56 : 61));

            drawInputBox(graphics, x + 109, y + 77, !alt ? 69 : 44);
            if (alt) drawInputBox(graphics, x + 158, y + 77, 44);
            drawInputBox(graphics, x + (!alt ? 182 : 207), y + 77, !alt ? 69 : 44);
        }

        int w = 0xFFFFFF, g = 0xA3A3A3, b = 0xCCD4FF, r = 0xFFD4CC;

        leftGroupScrollInputLabel.colored(leftGroupScrollInput.isActive() ? w : g);
        rightGroupScrollInputLabel.colored(rightGroupScrollInput.isActive() ? w : g);

        leftGroupRoundingScrollInputLabel.colored(leftGroupRoundingScrollInput.isActive() ? w : g);
        rightGroupRoundingScrollInputLabel.colored(rightGroupRoundingScrollInput.isActive() ? w : g);

        leftGroupOperationSelectorLabel.colored(w);
        if (alt) middleGroupOperationSelectorLabel.colored(w);
        rightGroupOperationSelectorLabel.colored(w);

        if (resetCaptureGroupsButton != null && rleResetButtonHighlightFieldsToBeReset && resetCaptureGroupsButton.isHovered()) {
            boolean s = AllKeys.shiftDown();
            int rw = (s ? r : w), rb = (s ? r : b),
                    vc = (resetButtonHoldShiftToResetValues ? rw : rb),
                    rc = (resetButtonHoldShiftToResetRoundings ? rw : rb),
                    oc = (resetButtonHoldShiftToResetOperations ? rw : rb);

            if (leftGroupScrollInput.isActive() && !(leftGroupScrollInput.getValueType() == CaptureGroupValueType.CHARS
                    && leftGroupScrollInput.getValue().intValue() == 0)) leftGroupScrollInputLabel.colored(vc);
            if (rightGroupScrollInput.isActive() && !(rightGroupScrollInput.getValueType() == CaptureGroupValueType.CHARS
                    && rightGroupScrollInput.getValue().intValue() == 0)) rightGroupScrollInputLabel.colored(vc);

            if (leftGroupRoundingScrollInput.isActive() && leftGroupRoundingScrollInput.getState() != 2) leftGroupRoundingScrollInputLabel.colored(rc);
            if (rightGroupRoundingScrollInput.isActive() && rightGroupRoundingScrollInput.getState() != 2) rightGroupRoundingScrollInputLabel.colored(rc);

            if (leftGroupOperationSelector.getState() != 2) leftGroupOperationSelectorLabel.colored(oc);
            if (alt && middleGroupOperationSelector.getState() != 2) middleGroupOperationSelectorLabel.colored(oc);
            if (rightGroupOperationSelector.getState() != 2) rightGroupOperationSelectorLabel.colored(oc);
        }

        if (labelBox.getSelectedPlaceholderX() != null && (rleConfigEnablePlaceholderPointer || rleConfigEnablePlaceholderPointerClamps)) {
            boolean c = labelBox.isColoring();
            int sps = labelBox.getSelectedPlaceholderX().x(), spe = labelBox.getSelectedPlaceholderX().y();
            int ptrX = Math.round(sps + (float) (spe - sps) / 2) - 7;
            if (rleConfigEnablePlaceholderPointer) {
                RLE_PL_CONFIG_POINTER.render(graphics, ptrX, y + 101);
                graphics.fill(sps, y + 119, spe, y + 129, 0xFFA3A3A3);
            }

            if (rleConfigEnablePlaceholderPointerClamps) {
                graphics.fill(sps, y + 116, spe, y + 117, 0xFF575757);
                graphics.fill(sps, y + 117, spe, y + 119, 0xFF6E6E6E);
                graphics.fill(sps + 1, y + 117, spe - 1, y + 118, c ? 0xFFFFFFFF : 0xFFA3A3A3);

                graphics.fill(sps, y + 131, spe, y + 132, 0xFF575757);
                graphics.fill(sps, y + 129, spe, y + 131, 0xFF6E6E6E);
                graphics.fill(sps + 1, y + 130, spe - 1, y + 131, c ? 0xFFFFFFFF : 0xFFA3A3A3);

                if (c) {
                    graphics.fill(sps + 1, y + 117, spe - 1, y + 118, PlaceholderColoringUtils.getHighlightColor(selectedPlaceholder));
                    graphics.fill(sps + 1, y + 130, spe - 1, y + 131, PlaceholderColoringUtils.getHighlightColor(selectedPlaceholder));
                }
            }
        }
    }

    private void drawInputBox(GuiGraphics graphics, int x, int y, int width) {
        UIRenderHelper.drawStretched(graphics, x, y, width, 18, 0, AllGuiTextures.DATA_AREA);
        AllGuiTextures.DATA_AREA_START.render(graphics, x, y);
        AllGuiTextures.DATA_AREA_END.render(graphics, x + width - 2, y);
    }

    private void renderCounter(GuiGraphics graphics, int x, int y) {
        if (counterPosition == CounterPosition.HIDDEN) return;
        int selectedChars = labelBox.getHighlighted().length();
        String counter = (selectedChars > 0 ? "/" : "") + text.length();
        String sCounter = "" + selectedChars;
        int cW = font.width(counter), sW = font.width(sCounter), tW = cW + (selectedChars > 0 ? sW : 0);

        int visibleTextLength = font.width(font.substrByWidth(FormattedText.of(text), labelBox.getInnerWidth()));
        boolean labelOverflow = visibleTextLength >= 330 - tW;
        boolean lower = counterPosition == CounterPosition.SEPARATED || (counterPosition == CounterPosition.DYNAMIC && labelOverflow);
        int cX = x + 347 - cW + (!lower ? 2 : 0), cY = y + 120 + (lower ? 19 : 0);

        /*graphics.drawString(font, (visibleTextLength + counter.length()) + "; labelOverflow = " + labelOverflow, cX - 100, cY - 50, 0xb8b8b8, true);*/

        if (lower) {
            RLE_COUNTER_R.render(graphics, x + 347, y + 136);
            UIRenderHelperAdditions.drawStretched(graphics, x + 347 - tW, y + 136, tW, 16, RLE_COUNTER_M, 512, 512);
            RLE_COUNTER_L.render(graphics, x + 347 - tW - 6, y + 136);
        }
        if (counterPosition != CounterPosition.INLINE || !labelOverflow) {
            graphics.drawString(font, counter, cX, cY, 0xb8b8b8, true);
            if (selectedChars > 0) graphics.drawString(font, sCounter, cX - sW, cY, 0x7474ff, true);
        }
    }

    private void renderPreviewStuff(GuiGraphics graphics, int x, int y) {
        if (!enableOutputLinePreview) return;

        int ix = x + 173;
        int ibel = previewInputBox != null && previewInputBox.isFocused() ? 166 : previewInputBox != null && previewInputBox.getValue().isEmpty()
                ? font.width(prInputExample) / 2 - 4 : Math.clamp(font.width(previewInput) / 2 - 4, 0, 166); // Input backplate extension length

        RLE_PREVIEW_INPUT_FIELD_LE.render(graphics, ix - ibel - 3, y);
        UIRenderHelperAdditions.drawStretched(graphics, ix - ibel, y, ibel, 30, RLE_PREVIEW_INPUT_FIELD_L, 512, 512);
        RLE_PREVIEW_INPUT_FIELD_M.render(graphics, ix, y);
        UIRenderHelperAdditions.drawStretched(graphics, ix + 14, y, ibel, 30, RLE_PREVIEW_INPUT_FIELD_R, 512, 512);
        RLE_PREVIEW_INPUT_FIELD_RE.render(graphics, ix + 14 + ibel, y);

        if (previewInputBox != null && !previewInputBox.isFocused()) {
            boolean hint = previewInputBox.getValue().isEmpty();
            Component t = hint ? prInputExample : Component.literal(font.plainSubstrByWidth(previewInput, 348));
            if (hint) t = t.copy().withStyle(ChatFormatting.ITALIC);
            int c = hint ? 0xCDBCA8 : 0x714A40;
            if (!hint || enableOutputLinePreviewFieldHints)
                graphics.drawString(font, t, ix + 7 - (font.width(t) / 2), y + 11, c, false);
        }

        RLE_PREVIEW_OUTPUT_FIELD.render(graphics, x, y + 30);
        if (previewOutput != null && !previewInput.isEmpty()) {
            int w = font.width(previewOutput);
            if (w > 330) ScrollingTruncatedString.draw(graphics, font, previewOutput,
                    x + 13, y + 45, 342, false, previewOutputStringLastStateChangeMillis);
            else graphics.drawString(font, previewOutput, x + 14, y + 44, 0xFFFFFF, true);
        } else if (enableOutputLinePreviewFieldHints) graphics.drawString(font,
                prOutputExample.copy().withStyle(ChatFormatting.ITALIC), x + 14, y + 44, 0xA3A3A3, false);
    }

    public void createNewPlaceholder() {
        int cursor = labelBox.getCursorPosition(), highlight = ((EditBoxAccessor) labelBox).createidlx$getHighlightPos();
        List<Integer> placeholdersBeforeCursor = placeholders.sequencedKeySet().stream().filter(si -> si < cursor).toList();

        if (!placeholdersBeforeCursor.isEmpty() && cursor == highlight) {
            int closestPlaceholderIndex = placeholdersBeforeCursor.getLast();
            int placeholderEndIndex = closestPlaceholderIndex + placeholders.get(closestPlaceholderIndex).length();
            int finalIndex = Math.max(placeholderEndIndex, cursor);
            labelBox.setCursorPosition(finalIndex);
            labelBox.setHighlightPos(finalIndex);
        }

        clearGroupCaches(true, true);
        labelBox.insertText(dollar.getString());
    }

    private void autoselectNewestPlaceholder() {
        if (text.equals(cachedText) || placeholders.size() <= cachedPlaceholdersAmount || cachedPlaceholdersAmount == -1) return;

        int i, tsi = 0;
        for (int c = 0; c < text.length(); c++) {
            if (c >= cachedText.length() || text.charAt(c) != cachedText.charAt(c)) {
                tsi = c;
                break;
            }
        }
        final int si = tsi;
        i = new LinkedList<>(placeholders.keySet())
                .indexOf(placeholders.keySet().stream().filter(i1 -> i1 == si).findAny().orElse(placeholders.size() - 1));
        if (i > 0 && placeholderTabButtons.get(i).getPlaceholder().isTrimming()) i--;
        if (i != -1) selectPlaceholder(false, i, placeholderTabButtons.get(i).getPlaceholder());
    }

    public void selectPlaceholder(boolean manual, int index, Placeholder placeholder) {
        if (index > -1 && (index == 0 || index != selectedPlaceholderIndex)) {
            clearGroupCaches(true, true);
            selectedPlaceholderIndex = index;
            selectedPlaceholder = placeholder;

            if (placeholder.properties().leftGroup().operation() == CaptureGroupOperation.NONE)
                leftGroupScrollInputLabelCache = plValueNone.copy();
            if (placeholder.properties().rightGroup().operation() == CaptureGroupOperation.NONE)
                rightGroupScrollInputLabelCache = plValueNone.copy();

        }
        for (RLEPlaceholderTabButton button : placeholderTabButtons) button.setSelected(index == button.getIndex());
        labelBox.setSelectedPlaceholderIndex(index);
        labelBox.setSelectedPlaceholderDisplayMarks(toStringIndex(index), placeholder.length(),
                !manual ? labelBox.getCursorPosition() : -1,
                AllKeys.shiftDown() && (resetCaptureGroupsButton == null || !resetCaptureGroupsButton.isHovered()));

        buildPlaceholderConfig(index);
        refreshPreviewOutput();
    }

    private void refreshPlaceholders() {
        placeholders = AttachedLabelProcessingUtils.mapPlaceholders(text);
    }

    private void clearGroupCaches(boolean v, boolean r) {
        if (v) {
            leftGroupCache.clear();
            rightGroupCache.clear();
            leftGroupScrollInputLabelCache = plValueNone.copy();
            rightGroupScrollInputLabelCache = plValueNone.copy();
        }
        if (r) {
            leftGroupRoundingLabelCache = plRoundingNone.copy();
            rightGroupRoundingLabelCache = plRoundingNone.copy();
        }
    }

    private int toStringIndex(int placeholderIndex) {
        return placeholderIndex != -1 ? placeholders.keySet().stream().toList().get(placeholderIndex) : 0;
    }

    private int toPlaceholderIndex(int stringIndex) {
        return placeholders.keySet().stream().toList().indexOf(stringIndex);
    }

    private int calculatePlaceholderButtonWidth(boolean gap) {
        return (int) ((float) PLACEHOLDER_TABS_SPACE / placeholders.size()) - (gap ? 0 : 2);
    }

    private int calculatePlaceholderButtonLeftGap() {
        return calculatePlaceholderButtonGaps() / 2;
    }

    private int calculatePlaceholderButtonGaps() {
        return (int) ((float) PLACEHOLDER_TABS_SPACE - (calculatePlaceholderButtonWidth(true) * placeholders.size()));
    }

    private LinkedList<Integer> calculatePlaceholderButtonWidths() {
        int spare = PLACEHOLDER_TABS_SPACE - (int) ((float) PLACEHOLDER_TABS_SPACE / placeholders.size()) * placeholders.size();
        ArrayList<Placeholder> sorted = placeholders.values().stream()
                .sorted(Comparator.comparingInt(Placeholder::length).reversed())
                .limit(spare).collect(Collectors.toCollection(ArrayList::new));

        LinkedList<Integer> widths = new LinkedList<>();
        for (Placeholder placeholder : placeholders.values()) {
            int width = calculatePlaceholderButtonWidth(false);
            if (sorted.contains(placeholder)) {
                width++;
                sorted.remove(placeholder);
            }
            widths.add(width);
        }
        return widths;
    }

    public void resetPlaceholderProperties(int i, Placeholder oldPlaceholder) {
        boolean resetValues = !resetButtonHoldShiftToResetValues || AllKeys.shiftDown();
        boolean resetRoundings = !resetButtonHoldShiftToResetRoundings || AllKeys.shiftDown();
        boolean resetOperations = !resetButtonHoldShiftToResetOperations || AllKeys.shiftDown();

        Placeholder newPlaceholder = oldPlaceholder.copy();
        CaptureGroup olg = oldPlaceholder.properties().leftGroup().copy(), org = oldPlaceholder.properties().rightGroup().copy();

        if (resetValues) {
            newPlaceholder.properties().setLeftGroup(defaultCG.copy().withOperation((!resetOperations ? olg : defaultCG).operation()));
            newPlaceholder.properties().setRightGroup(defaultCG.copy().withOperation((!resetOperations ? org : defaultCG).operation()));
            if (!resetRoundings) {
                newPlaceholder.properties().leftGroup().setRounding(olg.rounding());
                newPlaceholder.properties().rightGroup().setRounding(org.rounding());
            }

            clearGroupCaches(true, resetRoundings);
            leftGroupScrollInputLabelCache = plValueNone.copy();
            rightGroupScrollInputLabelCache = plValueNone.copy();
        }

        if (resetRoundings) {
            newPlaceholder.properties().leftGroup().setRounding(defaultCG.rounding());
            newPlaceholder.properties().rightGroup().setRounding(defaultCG.rounding());

            clearGroupCaches(resetValues, true);
        }

        if (resetOperations) {
            if (!resetValues || !resetRoundings) {
                cacheCaptureGroupData(resetValues, resetRoundings, oldPlaceholder.properties().leftGroup(), leftGroupCache, true);
                cacheCaptureGroupData(resetValues, resetRoundings, oldPlaceholder.properties().rightGroup(), rightGroupCache, false);
            }

            newPlaceholder.properties().leftGroup().setOperation(defaultCG.operation());
            newPlaceholder.properties().setMiddleGroupOperation(defaultCG.operation());
            newPlaceholder.properties().rightGroup().setOperation(defaultCG.operation());
        }

        newPlaceholder.reconstruct();
        onPlaceholderUpdate(i, oldPlaceholder, newPlaceholder);
    }

    public void togglePlaceholderType(int i, Placeholder oldPlaceholder) {
        PlaceholderType newType = oldPlaceholder.conditional(oldPlaceholder.isStandard());
        Placeholder newPlaceholder = oldPlaceholder.copy().withType(newType);
        onPlaceholderUpdate(i, oldPlaceholder, newPlaceholder);
    }

    public void selectCaptureGroupOperation(int i, Placeholder oldPlaceholder, int cgi, int oi) {
        CaptureGroupOperation newOperation = CaptureGroupOperation.values()[oi];
        Placeholder newPlaceholder = oldPlaceholder.copy();
        PlaceholderProperties properties = newPlaceholder.properties();

        if (cgi == 1) properties.withMiddleGroupOperation(newOperation);
        else {
            boolean left = cgi == 0;
            CaptureGroup group = left ? properties.leftGroup() : properties.rightGroup();
            LinkedHashMap<CaptureGroupValueType, Number> cachedV = left ? leftGroupCache : rightGroupCache;
            CaptureGroupOperation oldOperation = group.operation();

            if (newOperation == CaptureGroupOperation.NONE && oldOperation != CaptureGroupOperation.NONE)
                cacheCaptureGroupData(true, true, group, cachedV, left);
            else if (oldOperation == CaptureGroupOperation.NONE && newOperation != CaptureGroupOperation.NONE)
                pullCachedCaptureGroupData(group, cachedV, left);

            group.withOperation(newOperation);
        }

        if (oldPlaceholder.type() == TRIM_ALT && newOperation == CaptureGroupOperation.NONE) {
            cacheCaptureGroupData(true, true, properties.leftGroup(), leftGroupCache, true);
            cacheCaptureGroupData(true, true, properties.rightGroup(), rightGroupCache, false);

            properties.leftGroup().setOperation(CaptureGroupOperation.NONE);
            properties.setMiddleGroupOperation(CaptureGroupOperation.NONE);
            properties.rightGroup().setOperation(CaptureGroupOperation.NONE);
        } else if (oldPlaceholder.type() == BRACKETS && newOperation != CaptureGroupOperation.NONE) {
            pullCachedCaptureGroupData(properties.leftGroup(), leftGroupCache, true);
            pullCachedCaptureGroupData(properties.rightGroup(), rightGroupCache, false);

            properties.leftGroup().setOperation(CaptureGroupOperation.PRESERVE);
            properties.setMiddleGroupOperation(CaptureGroupOperation.PRESERVE);
            properties.rightGroup().setOperation(CaptureGroupOperation.PRESERVE);
        }

        newPlaceholder.reconstruct();
        onPlaceholderUpdate(i, oldPlaceholder, newPlaceholder);
    }

    private void cacheCaptureGroupData(boolean v, boolean r, CaptureGroup group, LinkedHashMap<CaptureGroupValueType, Number> cachedV, boolean left) {
        if (v) {
            cachedV.remove(group.valueType());
            cachedV.put(group.valueType(), group.value());
        }
        if (r) {
            if (left) leftGroupRoundingCache = group.rounding().ordinal();
            else rightGroupRoundingCache = group.rounding().ordinal();
        }
    }

    private void pullCachedCaptureGroupData(CaptureGroup group, LinkedHashMap<CaptureGroupValueType, Number> cachedV, boolean left) {
        CaptureGroupValueType cvt = !cachedV.isEmpty() ? cachedV.sequencedKeySet().getLast() : CaptureGroupValueType.CHARS;
        Number cv = !cachedV.isEmpty() ? cachedV.sequencedValues().getLast() : RLECaptureGroupScrollInput.NEW_INTEGER;
        int r = left ? leftGroupRoundingCache : rightGroupRoundingCache;
        group.withValueType(cvt).withValue(cv).withRounding(CaptureGroupRounding.values()[r]);
    }

    private void refreshPreviewOutput() {
        previewOutput = AttachedLabelProcessingUtils.breakDownAndAssembleLabelAsColoredComponent(text, previewInput,
                highlightProcessedInfoPartsInPreview, underlineSelectedPlaceholderInPreview, selectedPlaceholderIndex);
        previewOutputStringLastStateChangeMillis = Util.getMillis();
    }

    private void onPreviewInputUpdate(String input) {
        previewInput = input;
        refreshPreviewOutput();
    }

    public void onPlaceholderUpdate(int i, Placeholder oldPlaceholder, Placeholder newPlaceholder) {
        if (oldPlaceholder.equals(newPlaceholder)) return;

        selectedPlaceholder = newPlaceholder;
        String firstHalf = text.substring(0, i);
        String secondHalf = text.substring(Math.min(i + oldPlaceholder.length(), text.length()));

        onLabelUpdate(firstHalf + newPlaceholder.getString() + secondHalf);
    }

    public void onLabelUpdate(String newText) {
        onLabelUpdate(newText, false);
    }

    public void onLabelUpdate(String newText, boolean forceUpdate) {
        if (!forceUpdate && Objects.equals(newText, text)) return;
        text = newText;
        int cursorPos = labelBox.getCursorPosition();
        int highlightPos = ((EditBoxAccessor) labelBox).createidlx$getHighlightPos();
        labelBox.setValue(text);
        labelBox.setCursorPosition(cursorPos);
        labelBox.setHighlightPos(highlightPos);
        refreshPlaceholders();
        buildPlaceholderTabs(selectedPlaceholderIndex);
        autoselectNewestPlaceholder();
        cachedPlaceholdersAmount = placeholders.size();
        cachedText = text;
        refreshPreviewOutput();
    }

    public void onConfirm() {
        text = labelBox.getValue();
        close(text);
    }

    public void onReset() {
        text = oldText;
        close(text);
    }

    private void close(String text) {
        if (onConfirm != null) onConfirm.accept(text);
        ScreenOpener.open(initiatorScreen);
    }

    @Override
    public void onClose() {
        switch (defaultClosingMode) {
            case APPLY -> onConfirm();
            case RESET -> onReset();
            default -> onConfirm(); // TODO: actually ask & save decision to config
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        HashMap<? extends EditBox, List<? extends AbstractWidget>> boxesAndExceptions // the list is for widgets, using which will keep the key box focused
                = new HashMap<>(Map.of(labelBox, List.of(newPlaceholderTabButton), previewInputBox, List.of()));
        // TODO: somehow fix y offsets of input boxes

        for (EditBox box : boxesAndExceptions.keySet().stream().filter(Objects::nonNull).filter(EditBox::isFocused).toList()) {
            boolean result = box.mouseClicked(mouseX, mouseY, button);
            if (box.isHovered() || result) return result;
            if (boxesAndExceptions.get(box).stream().filter(Objects::nonNull).noneMatch(AbstractWidget::isHovered)) {
                box.setHighlightPos(box.getCursorPosition());
                box.setFocused(false);
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    public enum DefaultClosingMode { APPLY, RESET, ASK }
    public enum CounterPosition { INLINE, SEPARATED, DYNAMIC, HIDDEN }
    public enum TabWidthDistributionMode { EVEN, UNEVEN }
}
