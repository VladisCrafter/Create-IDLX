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
import com.vladiscrafter.createidlx.mixin.accessor.minecraft.EditBoxAccessor;
import com.vladiscrafter.createidlx.util.attachedLabel.AttachedLabelProcessingUtils;
import com.vladiscrafter.createidlx.util.gui.UIRenderHelperAdditions;
import com.vladiscrafter.createidlx.util.widget.ExpandedEditBox;
import com.vladiscrafter.createidlx.util.widget.TextureButton;
import com.vladiscrafter.createidlx.util.widget.rle.RLECaptureGroupScrollInput;
import com.vladiscrafter.createidlx.util.widget.rle.RLECheckbox;
import com.vladiscrafter.createidlx.util.widget.rle.RLEPlaceholderTabButton;
import net.createmod.catnip.config.ui.ConfigHelper;
import net.createmod.catnip.config.ui.SubMenuConfigScreen;
import net.createmod.catnip.gui.AbstractSimiScreen;
import net.createmod.catnip.gui.ScreenOpener;
import net.createmod.catnip.gui.UIRenderHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.vladiscrafter.createidlx.util.attachedLabel.AttachedLabelPart.*;
import static com.vladiscrafter.createidlx.util.attachedLabel.AttachedLabelPart.PlaceholderType.*;

public class DisplayLinkRichLabelEditorScreen extends AbstractSimiScreen {
    protected String text, oldText, cachedText;
    protected Screen initiatorScreen;
    protected Consumer<String> onConfirm;

    protected LinkedHashMap<Integer, Placeholder> placeholders;
    int cachedPlaceholdersAmount;
    int selectedPlaceholderIndex;
    Placeholder selectedPlaceholder;

    protected TextureButton configButton;
    protected ExpandedEditBox label;
    protected IconButton confirmButton, resetButton;

    protected TextureButton newPlaceholderTabButton;
    protected LinkedList<RLEPlaceholderTabButton> placeholderTabButtons;

    protected RLECheckbox standardPlaceholderCheckbox, alternativePlaceholderCheckbox;
    protected RLECaptureGroupScrollInput leftGroupScrollInput, rightGroupScrollInput;
    protected Label leftGroupScrollInputLabel, rightGroupScrollInputLabel;
    protected LinkedHashMap<CaptureGroupValueType, Number> leftGroupCache, rightGroupCache;
    protected SelectionScrollInput leftGroupOperationSelector, middleGroupOperationSelector, rightGroupOperationSelector;
    protected Label leftGroupOperationSelectorLabel, middleGroupOperationSelectorLabel, rightGroupOperationSelectorLabel;
    protected Component leftGroupScrollInputLabelCache, rightGroupScrollInputLabelCache;
    protected TextureButton resetCaptureGroupsButton;

    final CreateIDLXGuiTextures BACKGROUND = CreateIDLXGuiTextures.RICH_LABEL_EDITOR;
    final int PLACEHOLDER_TABS_SPACE = 336;
    final Component header = Component.literal("Rich Label Editor");
    final Component plType = Component.literal("Type");
    final Component plCaptureGroups = Component.literal("Capture Groups");
    final Component plFlags = Component.literal("Flags");
    final Component plTypeS = Component.literal("Standard");
    final Component plTypeA = Component.literal("Alternative");
    final Component plValueNone = Component.literal("—");
    final List<Component> plOperations = List.of(Component.literal("Cut"), Component.literal("Preserve"), Component.literal("None"));
    final List<Component> plOperationsAlt = List.of(Component.literal("Drop"), Component.literal("Keep"), Component.literal("None"));
    final DefaultClosingMode defaultClosingMode = CIDLXConfigs.client.rleDefaultClosingMode.get();
    final CounterPosition counterPosition = CIDLXConfigs.client.rleCounterPosition.get();
    final TabWidthDistributionMode tabWidthDistributionMode = CIDLXConfigs.client.rleTabWidthDistributionMode.get();
    final boolean enableScreenConfigButton = CIDLXConfigs.client.rleEnableScreenConfigButton.get();

    public DisplayLinkRichLabelEditorScreen(String text, Screen initiator, Consumer<String> onConfirm) {
        this.text = text;
        this.oldText = text;
        this.cachedText = text;
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
    }

    @Override
    protected void init() {
        setWindowSize(BACKGROUND.getWidth(), BACKGROUND.getHeight());
        super.init();
        clearWidgets();

        int x = guiLeft, y = guiTop;

        label = new ExpandedEditBox(font, x + 14, y + 120, 330, 18, CommonComponents.EMPTY);
        label.setBordered(false);
        label.setTextColor(0xffffff);
        label.setFocused(false);
        label.setValue(text);
        label.setResponder(this::onLabelUpdate);

        int hX = x + BACKGROUND.getWidth() / 2 - font.width(header) / 2;
        configButton = new TextureButton(hX - 13, y + 3, 9, 9, CreateIDLXGuiTextures.RLE_CONFIG);
        configButton.visible = enableScreenConfigButton;
        configButton.withCallback(() -> ScreenOpener.open(SubMenuConfigScreen.find(ConfigHelper.ConfigPath.parse(
                                    "createidlx:client." + CIDLXConfigs.client.displayLinkGUI.getName()
                                            + "." + CIDLXConfigs.client.richLabelEditorCustomization.getName()
                                            + "." + CIDLXConfigs.client.rleEnableScreenConfigButton.getName()))));

        confirmButton = new IconButton(x + BACKGROUND.getWidth() - 25, y + BACKGROUND.getHeight() - 24, AllIcons.I_CONFIRM).withCallback(this::onConfirm);
        resetButton = new IconButton(x + 7, y + BACKGROUND.getHeight() - 24, AllIcons.I_REFRESH).withCallback(this::onReset);

        addRenderableWidgets(label, configButton, confirmButton, resetButton);

        clearGroupCaches();
        onLabelUpdate(text, true);
        buildPlaceholderTabs(0);
    }

    private void buildPlaceholderTabs(int index) {
        if (placeholderTabButtons == null) placeholderTabButtons = new LinkedList<>();
        placeholderTabButtons.forEach(this::removeWidget);
        placeholderTabButtons.clear();
        if (newPlaceholderTabButton != null) removeWidget(newPlaceholderTabButton);

        int i = 0, x = guiLeft, tX = guiLeft + 4 + 2, y = guiTop + 16;

        if (tabWidthDistributionMode == TabWidthDistributionMode.EVEN) {
            tX += calculatePlaceholderButtonLeftGap();
            for (Placeholder placeholder : placeholders.values()) {
                final int lI = i;
                placeholderTabButtons.add(new RLEPlaceholderTabButton(tX, y, calculatePlaceholderButtonWidth(false), lI, placeholder)
                        .withCallback(() -> selectPlaceholder(lI, placeholder)));
                tX += calculatePlaceholderButtonWidth(true);
                i++;
            }
        } else for (Placeholder placeholder : placeholders.values()) {
            final int lI = i;
            placeholderTabButtons.add(new RLEPlaceholderTabButton(tX, y, calculatePlaceholderButtonWidths().get(lI), lI, placeholder)
                    .withCallback(() -> selectPlaceholder(lI, placeholder)));
            tX += calculatePlaceholderButtonWidths().get(i) + 2;
            i++;
        }

        newPlaceholderTabButton = new TextureButton(x + (!placeholders.isEmpty() ? 345 : 171), y + 2, 9, 9, CreateIDLXGuiTextures.RLE_TABS_NEW, CreateIDLXGuiTextures.RLE_TABS_NEW_H);
        newPlaceholderTabButton.withCallback(() -> label.insertText(dollar.getString()));
        newPlaceholderTabButton.setNonFocusable();

        addRenderableWidgets(placeholderTabButtons);
        addRenderableWidget(newPlaceholderTabButton);

        int ri = Math.min(index, placeholders.size() - 1);
        if (!placeholders.isEmpty()) selectPlaceholder(ri, placeholderTabButtons.get(ri).getPlaceholder());
    }

    private void buildPlaceholderConfig(int index) {
        if (standardPlaceholderCheckbox != null) removeWidget(standardPlaceholderCheckbox);
        if (alternativePlaceholderCheckbox != null) removeWidget(alternativePlaceholderCheckbox);

        if (leftGroupScrollInput != null) removeWidget(leftGroupScrollInput);
        if (leftGroupScrollInputLabel != null) removeWidget(leftGroupScrollInputLabel);

        if (rightGroupScrollInput != null) removeWidget(rightGroupScrollInput);
        if (rightGroupScrollInputLabel != null) removeWidget(rightGroupScrollInputLabel);

        if (leftGroupOperationSelector != null) removeWidget(leftGroupOperationSelector);
        if (leftGroupOperationSelectorLabel != null) removeWidget(leftGroupOperationSelectorLabel);

        if (middleGroupOperationSelector != null) removeWidget(middleGroupOperationSelector);
        if (middleGroupOperationSelectorLabel != null) removeWidget(middleGroupOperationSelectorLabel);

        if (rightGroupOperationSelector != null) removeWidget(rightGroupOperationSelector);
        if (rightGroupOperationSelectorLabel != null) removeWidget(rightGroupOperationSelectorLabel);

        if (resetCaptureGroupsButton != null) removeWidget(resetCaptureGroupsButton);

        if (placeholders.isEmpty()) return;

        Placeholder placeholder = placeholderTabButtons.get(index).getPlaceholder();
        int psi = placeholders.keySet().toArray(new Integer[placeholders.size()])[index];

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

        leftGroupScrollInputLabel = new Label(x + 109, y + 17, CommonComponents.EMPTY).withShadow();
        leftGroupScrollInput = new RLECaptureGroupScrollInput(x + 104, y + 11, 56, 18, placeholder.properties().leftGroup(), leftGroupCache);
        leftGroupScrollInput.withScrollCallback((pcg, cg) -> onPlaceholderUpdate(psi,
                    placeholder.copy().withProperties(placeholder.properties().copy().withLeftGroup(pcg.copy())),
                    placeholder.withProperties(placeholder.properties().copy().withLeftGroup(cg.copy())))).onChanged();
        leftGroupScrollInputLabel.colored(leftGroupScrollInput.isActive() ? 0xFFFFFF : 0xA3A3A3);
        if (placeholder.properties().leftGroup().operation() != CaptureGroupOperation.NONE) {
            leftGroupScrollInput.writingTo(leftGroupScrollInputLabel);
            leftGroupScrollInputLabelCache = leftGroupScrollInputLabel.text;
        } else leftGroupScrollInputLabel.text = leftGroupScrollInputLabelCache;

        rightGroupScrollInputLabel = new Label(x + 183, y + 17, CommonComponents.EMPTY).withShadow();
        rightGroupScrollInput = new RLECaptureGroupScrollInput(x + 178, y + 11, 56, 18, placeholder.properties().rightGroup(), rightGroupCache);
        rightGroupScrollInput.withScrollCallback((pcg, cg) -> onPlaceholderUpdate(psi,
                placeholder.copy().withProperties(placeholder.properties().copy().withRightGroup(pcg.copy())),
                placeholder.withProperties(placeholder.properties().copy().withRightGroup(cg.copy())))).onChanged();
        rightGroupScrollInputLabel.colored(rightGroupScrollInput.isActive() ? 0xFFFFFF : 0xA3A3A3);
        if (placeholder.properties().rightGroup().operation() != CaptureGroupOperation.NONE) {
            rightGroupScrollInput.writingTo(rightGroupScrollInputLabel);
            rightGroupScrollInputLabelCache = rightGroupScrollInputLabel.text;
        } else rightGroupScrollInputLabel.text = rightGroupScrollInputLabelCache;

        leftGroupOperationSelectorLabel = new Label(x + 109, y + 38, CommonComponents.EMPTY).withShadow();
        leftGroupOperationSelector = new SelectionScrollInput(x + 104, y + 32, !alt ? 63 : 40, 18);
        leftGroupOperationSelector.calling(s -> selectCaptureGroupOperation(psi, placeholder, 0, s));
        leftGroupOperationSelector.forOptions(!alt ? plOperations : plOperationsAlt).writingTo(leftGroupOperationSelectorLabel)
                .setState(placeholder.properties().leftGroup().operation().ordinal()).onChanged();

        if (alt) {
            middleGroupOperationSelectorLabel = new Label(x + 154, y + 38, CommonComponents.EMPTY).withShadow();
            middleGroupOperationSelector = new SelectionScrollInput(x + 149, y + 32, !alt ? 63 : 40, 18);
            middleGroupOperationSelector.calling(s -> selectCaptureGroupOperation(psi, placeholder, 1, s));
            middleGroupOperationSelector.forOptions(plOperationsAlt).writingTo(middleGroupOperationSelectorLabel)
                    .setState(placeholder.properties().middleGroupOperation().ordinal()).onChanged();
        }

        rightGroupOperationSelectorLabel = new Label(x + (!alt ? 176 : 199), y + 38, CommonComponents.EMPTY).withShadow();
        rightGroupOperationSelector = new SelectionScrollInput(x + (!alt ? 171 : 194), y + 32, !alt ? 63 : 40, 18);
        rightGroupOperationSelector.calling(s -> selectCaptureGroupOperation(psi, placeholder, 2, s));
        rightGroupOperationSelector.forOptions(!alt ? plOperations : plOperationsAlt).writingTo(rightGroupOperationSelectorLabel)
                .setState(placeholder.properties().rightGroup().operation().ordinal()).onChanged();

        resetCaptureGroupsButton = new TextureButton(x + 164, y + 16, 11, 11, CreateIDLXGuiTextures.RLE_PL_CAPTURE_GROUPS_RESET, CreateIDLXGuiTextures.RLE_PL_CAPTURE_GROUPS_RESET_H);
        resetCaptureGroupsButton.withCallback(() -> resetCaptureGroups(psi, placeholder, AllKeys.shiftDown()));
        resetCaptureGroupsButton.withSpecialHoverTexture(AllKeys.SHIFT_MODIFIER, CreateIDLXGuiTextures.RLE_PL_CAPTURE_GROUPS_RESET_HR).setNonFocusable();

        addRenderableWidgets(standardPlaceholderCheckbox, alternativePlaceholderCheckbox,
                leftGroupScrollInputLabel, leftGroupScrollInput,
                rightGroupScrollInputLabel, rightGroupScrollInput,
                leftGroupOperationSelectorLabel, leftGroupOperationSelector,
                rightGroupOperationSelectorLabel, rightGroupOperationSelector);
        if (alt) addRenderableWidgets(middleGroupOperationSelectorLabel, middleGroupOperationSelector);
        addRenderableWidgets(resetCaptureGroupsButton);
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
        CreateIDLXGuiTextures.RLE_FIELD.render(graphics, x, y + 106);
        renderPlaceholderTabStuff(graphics, x, y);
        renderCounter(graphics, x, y);
    }

    private void renderPlaceholderTabStuff(GuiGraphics graphics, int x, int y) {
        UIRenderHelperAdditions.drawStretched(graphics, x + 3, y + 15, 354, 16, CreateIDLXGuiTextures.RLE_TABS_BG_E, 512, 512);
        UIRenderHelperAdditions.drawStretched(graphics, x + 4, y + 15, 352, 16, CreateIDLXGuiTextures.RLE_TABS_BG_M, 512, 512);

        if (placeholders == null || placeholders.isEmpty()) return;

        CreateIDLXGuiTextures.RLE_TABS_SEPARATOR.render(graphics, x + 342, y + 17);

        CreateIDLXGuiTextures.RLE_PL_PROPERTIES_BG_L.render(graphics, x + 5, y + 38);
        UIRenderHelperAdditions.drawStretched(graphics, x + 11, y + 38, 338, 66, CreateIDLXGuiTextures.RLE_PL_PROPERTIES_BG_M, 512, 512);
        CreateIDLXGuiTextures.RLE_PL_PROPERTIES_BG_R.render(graphics, x + 349, y + 38);

        CreateIDLXGuiTextures.RLE_PL_PROPERTIES_SEPARATOR.render(graphics, x + 107, y + 46);
        CreateIDLXGuiTextures.RLE_PL_PROPERTIES_SEPARATOR.render(graphics, x + 249, y + 46);

        int sideNameplateInnerWidth = Math.max(40, Math.clamp(Math.max(font.width(plType) + 2, font.width(plFlags) + 2), 20, 92));
        int middleNameplateInnerWidth = Math.max(90, Math.clamp(font.width(plCaptureGroups) + 2, 20, 134));
        if (middleNameplateInnerWidth % 2 == 1 && middleNameplateInnerWidth < 134) middleNameplateInnerWidth++;

        int lX = x + 11 + 48 - ((int) Math.ceil((double) sideNameplateInnerWidth / 2));
        int mX = x + 11 + 100 + 69 - ((int) Math.ceil((double) middleNameplateInnerWidth / 2));
        int rX = x + 11 + 242 + 48 - ((int) Math.ceil((double) sideNameplateInnerWidth / 2)) + ((sideNameplateInnerWidth % 2 == 1 && sideNameplateInnerWidth < 92) ? 1 : 0);

        int fY = y + 34;

        CreateIDLXGuiTextures.RLE_PL_PROPERTIES_FG_L.render(graphics, lX - 2, fY);
        UIRenderHelperAdditions.drawStretched(graphics, lX, fY, sideNameplateInnerWidth, 16, CreateIDLXGuiTextures.RLE_PL_PROPERTIES_FG_M, 512, 512);
        CreateIDLXGuiTextures.RLE_PL_PROPERTIES_FG_R.render(graphics, lX + sideNameplateInnerWidth, fY);

        CreateIDLXGuiTextures.RLE_PL_PROPERTIES_FG_L.render(graphics, mX - 2, fY);
        UIRenderHelperAdditions.drawStretched(graphics, mX, fY, middleNameplateInnerWidth, 16, CreateIDLXGuiTextures.RLE_PL_PROPERTIES_FG_M, 512, 512);
        CreateIDLXGuiTextures.RLE_PL_PROPERTIES_FG_R.render(graphics, mX + middleNameplateInnerWidth, fY);

        CreateIDLXGuiTextures.RLE_PL_PROPERTIES_FG_L.render(graphics, rX - 2, fY);
        UIRenderHelperAdditions.drawStretched(graphics, rX, fY, sideNameplateInnerWidth, 16, CreateIDLXGuiTextures.RLE_PL_PROPERTIES_FG_M, 512, 512);
        CreateIDLXGuiTextures.RLE_PL_PROPERTIES_FG_R.render(graphics, rX + sideNameplateInnerWidth, fY);

        graphics.drawCenteredString(font, plType, x + 11 + 48, fY + 4, 0xffffff);
        graphics.drawCenteredString(font, plCaptureGroups, x + 11 + 100 + 69, fY + 4, 0xffffff);
        graphics.drawCenteredString(font, plFlags, x + 11 + 242 + 48, fY + 4, 0xffffff);

        graphics.drawString(font, plTypeS, x + 36, y + 64, 0xffffff);
        CreateIDLXGuiTextures.RLE_PL_TYPE_RADIO_WIRE.render(graphics, x + 27, y + 72);
        graphics.drawString(font, plTypeA, x + 36, y + 81, 0xffffff);

        CreateIDLXGuiTextures.RLE_PL_CAPTURE_GROUPS_BRACKETS.render(graphics, x + 115, y + 56);

        boolean alt = selectedPlaceholder.isAlternative();
        if (selectedPlaceholder.type() != INVALID) {
            drawInputBox(graphics, x + 115, y + 56, 56);
            drawInputBox(graphics, x + 189, y + 56, 56);

            drawInputBox(graphics, x + 115, y + 77, !alt ? 63 : 40);
            if (alt) drawInputBox(graphics, x + 160, y + 77, 40);
            drawInputBox(graphics, x + (!alt ? 182 : 205), y + 77, !alt ? 63 : 40);
        }

        if (resetCaptureGroupsButton.isHovered()) {
            leftGroupScrollInputLabel.colored(leftGroupScrollInput.isActive() ? (AllKeys.shiftDown() ? 0xFFD4CC : 0xCCD4FF) : 0xA3A3A3);
            rightGroupScrollInputLabel.colored(rightGroupScrollInput.isActive() ? (AllKeys.shiftDown() ? 0xFFD4CC : 0xCCD4FF) : 0xA3A3A3);
        } else {
            leftGroupScrollInputLabel.colored(leftGroupScrollInput.isActive() ? 0xFFFFFF : 0xA3A3A3);
            rightGroupScrollInputLabel.colored(rightGroupScrollInput.isActive() ? 0xFFFFFF : 0xA3A3A3);
        }

        if (resetCaptureGroupsButton.isHovered() && AllKeys.shiftDown()) {
                leftGroupOperationSelectorLabel.colored(leftGroupOperationSelector.getState() != 2 ? 0xFFD4CC : 0xFFFFFF);
                middleGroupOperationSelectorLabel.colored(middleGroupOperationSelector.getState() != 2 ? 0xFFD4CC : 0xFFFFFF);
                rightGroupOperationSelectorLabel.colored(rightGroupOperationSelector.getState() != 2 ? 0xFFD4CC : 0xFFFFFF);
        } else {
            leftGroupOperationSelectorLabel.colored(0xFFFFFF);
            middleGroupOperationSelectorLabel.colored(0xFFFFFF);
            rightGroupOperationSelectorLabel.colored(0xFFFFFF);
        }
    }

    private void drawInputBox(GuiGraphics graphics, int x, int y, int width) {
        UIRenderHelper.drawStretched(graphics, x, y, width, 18, 0, AllGuiTextures.DATA_AREA);
        AllGuiTextures.DATA_AREA_START.render(graphics, x, y);
        AllGuiTextures.DATA_AREA_END.render(graphics, x + width - 2, y);
    }

    private void renderCounter(GuiGraphics graphics, int x, int y) {
        if (counterPosition == CounterPosition.HIDDEN) return;
        int selectedChars = label.getHighlighted().length();
        String counter = (selectedChars > 0 ? "/" : "") + text.length();
        String sCounter = "" + selectedChars;
        int cW = font.width(counter), sW = font.width(sCounter), tW = cW + (selectedChars > 0 ? sW : 0);

        int visibleTextLength = font.width(font.substrByWidth(FormattedText.of(text), label.getInnerWidth()));
        boolean labelOverflow = visibleTextLength >= 330 - tW;
        boolean lower = counterPosition == CounterPosition.SEPARATED || (counterPosition == CounterPosition.DYNAMIC && labelOverflow);
        int cX = x + 347 - cW + (!lower ? 2 : 0), cY = y + 120 + (lower ? 19 : 0);

        /*graphics.drawString(font, (visibleTextLength + counter.length()) + "; labelOverflow = " + labelOverflow, cX - 100, cY - 50, 0xb8b8b8, true);*/

        if (lower) {
            CreateIDLXGuiTextures.RLE_COUNTER_R.render(graphics, x + 347, y + 136);
            UIRenderHelperAdditions.drawStretched(graphics, x + 347 - tW, y + 136, tW, 16, CreateIDLXGuiTextures.RLE_COUNTER_M, 512, 512);
            CreateIDLXGuiTextures.RLE_COUNTER_L.render(graphics, x + 347 - tW - 6, y + 136);
        }
        if (counterPosition != CounterPosition.INLINE || !labelOverflow) {
            graphics.drawString(font, counter, cX, cY, 0xb8b8b8, true);
            if (selectedChars > 0) graphics.drawString(font, sCounter, cX - sW, cY, 0x7474ff, true);
        }
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
        if (i != -1) selectPlaceholder(i, placeholderTabButtons.get(i).getPlaceholder());
    }

    public void selectPlaceholder(int index, Placeholder placeholder) {
        if (index != selectedPlaceholderIndex) {
            clearGroupCaches();
            selectedPlaceholderIndex = index;
            selectedPlaceholder = placeholder;

            if (placeholder.properties().leftGroup().operation() == CaptureGroupOperation.NONE) leftGroupScrollInputLabelCache = plValueNone.copy();
            if (placeholder.properties().rightGroup().operation() == CaptureGroupOperation.NONE) rightGroupScrollInputLabelCache = plValueNone.copy();
        }

        for (RLEPlaceholderTabButton button : placeholderTabButtons) button.setSelected(index == button.getIndex());

        buildPlaceholderConfig(index);
    }

    private void clearGroupCaches() {
        leftGroupCache.clear();
        rightGroupCache.clear();
    }

    private void refreshPlaceholders() {
        placeholders = AttachedLabelProcessingUtils.mapPlaceholders(text);
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

    public void resetCaptureGroups(int i, Placeholder oldPlaceholder, boolean resetOperations) {
        Placeholder newPlaceholder = oldPlaceholder.copy();
        CaptureGroupOperation lgo = oldPlaceholder.properties().leftGroup().copy().operation(), rgo = oldPlaceholder.properties().rightGroup().copy().operation();
        newPlaceholder.properties().setLeftGroup(defaultCG.copy().withOperation(!resetOperations ? lgo : defaultCG.operation()));
        newPlaceholder.properties().setRightGroup(defaultCG.copy().withOperation(!resetOperations ? rgo : defaultCG.operation()));
        if (resetOperations) newPlaceholder.properties().setMiddleGroupOperation(defaultCG.operation());

        clearGroupCaches();
        leftGroupScrollInputLabelCache = plValueNone.copy();
        rightGroupScrollInputLabelCache = plValueNone.copy();

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
            LinkedHashMap<CaptureGroupValueType, Number> cache = left ? leftGroupCache : rightGroupCache;
            CaptureGroupOperation oldOperation = group.operation();

            if (newOperation == CaptureGroupOperation.NONE && oldOperation != CaptureGroupOperation.NONE) cacheCaptureGroupValue(group, cache);
            else if (oldOperation == CaptureGroupOperation.NONE && newOperation != CaptureGroupOperation.NONE) pullCachedCaptureGroupValue(group, cache);

            group.withOperation(newOperation);
        }

        if (oldPlaceholder.type() == TRIM_ALT && newOperation == CaptureGroupOperation.NONE) {
            cacheCaptureGroupValue(properties.leftGroup(), leftGroupCache);
            cacheCaptureGroupValue(properties.rightGroup(), rightGroupCache);

            properties.leftGroup().setOperation(CaptureGroupOperation.NONE);
            properties.setMiddleGroupOperation(CaptureGroupOperation.NONE);
            properties.rightGroup().setOperation(CaptureGroupOperation.NONE);
        } else if (oldPlaceholder.type() == BRACKETS && newOperation != CaptureGroupOperation.NONE) {
            pullCachedCaptureGroupValue(properties.leftGroup(), leftGroupCache);
            pullCachedCaptureGroupValue(properties.rightGroup(), rightGroupCache);

            properties.leftGroup().setOperation(CaptureGroupOperation.PRESERVE);
            properties.setMiddleGroupOperation(CaptureGroupOperation.PRESERVE);
            properties.rightGroup().setOperation(CaptureGroupOperation.PRESERVE);
        }

        newPlaceholder.reconstruct();
        onPlaceholderUpdate(i, oldPlaceholder, newPlaceholder);
    }

    private void cacheCaptureGroupValue(CaptureGroup group, LinkedHashMap<CaptureGroupValueType, Number> cache) {
        cache.remove(group.valueType());
        cache.put(group.valueType(), group.value());
    }

    private void pullCachedCaptureGroupValue(CaptureGroup group, LinkedHashMap<CaptureGroupValueType, Number> cache) {
        CaptureGroupValueType cvt = !cache.isEmpty() ? cache.sequencedKeySet().getLast() : CaptureGroupValueType.CHARS;
        Number cv = !cache.isEmpty() ? cache.sequencedValues().getLast() : RLECaptureGroupScrollInput.NEW_INTEGER;
        group.withValueType(cvt).withValue(cv);
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
        int cursorPos = label.getCursorPosition();
        int highlightPos = ((EditBoxAccessor) label).createidlx$getHighlightPos();
        label.setValue(text);
        label.setCursorPosition(cursorPos);
        label.setHighlightPos(highlightPos);
        refreshPlaceholders();
        buildPlaceholderTabs(selectedPlaceholderIndex);
        autoselectNewestPlaceholder();
        cachedPlaceholdersAmount = placeholders.size();
        cachedText = text;
    }

    public void onConfirm() {
        text = label.getValue();
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

    public enum DefaultClosingMode { APPLY, RESET, ASK }
    public enum CounterPosition { INLINE, SEPARATED, DYNAMIC, HIDDEN }
    public enum TabWidthDistributionMode { EVEN, UNEVEN }
}
