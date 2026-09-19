package com.vladiscrafter.createidlx.content.displayLink;

import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.vladiscrafter.createidlx.config.CIDLXConfigs;
import com.vladiscrafter.createidlx.foundation.gui.CreateIDLXGuiTextures;
import com.vladiscrafter.createidlx.util.attachedLabel.AttachedLabelProcessingUtils;
import com.vladiscrafter.createidlx.util.gui.UIRenderHelperAdditions;
import com.vladiscrafter.createidlx.util.widget.ExpandedEditBox;
import com.vladiscrafter.createidlx.util.widget.TextureButton;
import com.vladiscrafter.createidlx.util.widget.rle.RLECheckbox;
import com.vladiscrafter.createidlx.util.widget.rle.RLEPlaceholderTabButton;
import net.createmod.catnip.config.ui.ConfigHelper;
import net.createmod.catnip.config.ui.SubMenuConfigScreen;
import net.createmod.catnip.gui.AbstractSimiScreen;
import net.createmod.catnip.gui.ScreenOpener;
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
    protected String text, oldText;
    protected Screen initiatorScreen;
    protected Consumer<String> onConfirm;

    protected LinkedHashMap<Integer, Placeholder> placeholders;
    int selectedPlaceholderIndex;

    protected TextureButton configButton;
    protected ExpandedEditBox label;
    protected IconButton confirmButton, resetButton;

    protected TextureButton newPlaceholderTabButton;
    protected LinkedList<RLEPlaceholderTabButton> placeholderTabButtons;

    protected RLECheckbox standardPlaceholderCheckbox, alternativePlaceholderCheckbox;

    final CreateIDLXGuiTextures BACKGROUND = CreateIDLXGuiTextures.RICH_LABEL_EDITOR;
    final int PLACEHOLDER_TABS_SPACE = 336;
    final Component header = Component.literal("Rich Label Editor");
    final Component plType = Component.literal("Type");
    final Component plCaptureGroups = Component.literal("Capture Groups");
    final Component plFlags = Component.literal("Flags");

    final DefaultClosingMode defaultClosingMode = CIDLXConfigs.client.rleDefaultClosingMode.get();
    final CounterPosition counterPosition = CIDLXConfigs.client.rleCounterPosition.get();
    final TabWidthDistributionMode tabWidthDistributionMode = CIDLXConfigs.client.rleTabWidthDistributionMode.get();
    final boolean enableScreenConfigButton = CIDLXConfigs.client.rleEnableScreenConfigButton.get();

    public DisplayLinkRichLabelEditorScreen(String text, Screen initiator, Consumer<String> onConfirm) {
        this.text = text;
        this.oldText = text;
        this.initiatorScreen = initiator;
        this.onConfirm = onConfirm;

        this.placeholders = new LinkedHashMap<>();
        this.placeholderTabButtons = new LinkedList<>();
        this.selectedPlaceholderIndex = 0;
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

        configButton = new TextureButton(0, y + 3, 9, 9, CreateIDLXGuiTextures.RLE_CONFIG);
        configButton.visible = enableScreenConfigButton;
        int hX = x + BACKGROUND.getWidth() / 2 - font.width(header) / 2;
        configButton.setX(hX - 13);
        configButton.withCallback(() -> ScreenOpener.open(SubMenuConfigScreen.find(ConfigHelper.ConfigPath.parse(
                                    "createidlx:client." + CIDLXConfigs.client.displayLinkGUI.getName()
                                            + "." + CIDLXConfigs.client.richLabelEditorCustomization.getName()
                                            + "." + CIDLXConfigs.client.rleEnableScreenConfigButton.getName()))));

        confirmButton = new IconButton(x + BACKGROUND.getWidth() - 25, y + BACKGROUND.getHeight() - 24, AllIcons.I_CONFIRM).withCallback(this::onConfirm);
        resetButton = new IconButton(x + 7, y + BACKGROUND.getHeight() - 24, AllIcons.I_REFRESH).withCallback(this::onReset);

        addRenderableWidgets(label, configButton, confirmButton, resetButton);

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
                        .withCallback(() -> selectPlaceholder(lI)));
                tX += calculatePlaceholderButtonWidth(true);
                i++;
            }
        } else for (Placeholder placeholder : placeholders.values()) {
            final int lI = i;
            placeholderTabButtons.add(new RLEPlaceholderTabButton(tX, y, calculatePlaceholderButtonWidths().get(lI), lI, placeholder)
                    .withCallback(() -> selectPlaceholder(lI)));
            tX += calculatePlaceholderButtonWidths().get(i) + 2;
            i++;
        }

        newPlaceholderTabButton = new TextureButton(x + (!placeholders.isEmpty() ? 345 : 171), y + 2, 9, 9, CreateIDLXGuiTextures.RLE_TABS_NEW, CreateIDLXGuiTextures.RLE_TABS_NEW_H);
        newPlaceholderTabButton.withCallback(this::refreshPlaceholders); // this::addPlaceholder
        newPlaceholderTabButton.setNonFocusable();

        addRenderableWidgets(placeholderTabButtons);
        addRenderableWidget(newPlaceholderTabButton);

        selectPlaceholder(index);
    }

    private void buildPlaceholderConfig(int index) {
        if (standardPlaceholderCheckbox != null) removeWidget(standardPlaceholderCheckbox);
        if (alternativePlaceholderCheckbox != null) removeWidget(alternativePlaceholderCheckbox);

        Placeholder placeholder = placeholderTabButtons.get(index).getPlaceholder();
        PlaceholderType type = placeholder.type();

        int x = guiLeft + 11, y = guiTop + 44;

        standardPlaceholderCheckbox = new RLECheckbox(x + 12, y + 19);
        alternativePlaceholderCheckbox = new RLECheckbox(x + 12, y + 36);

        standardPlaceholderCheckbox.setLinkedCheckbox(alternativePlaceholderCheckbox);
        alternativePlaceholderCheckbox.setLinkedCheckbox(standardPlaceholderCheckbox);

        if (!placeholder.isType(INVALID)) {
            if (placeholder.isStandard()) standardPlaceholderCheckbox.setOn(true);
            else alternativePlaceholderCheckbox.setOn(true);
        }

        addRenderableWidgets(standardPlaceholderCheckbox, alternativePlaceholderCheckbox);
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

        graphics.drawString(font, Component.literal("Standard"), x + 36, y + 64, 0xffffff);
        CreateIDLXGuiTextures.RLE_PL_TYPE_RADIO_WIRE.render(graphics, x + 27, y + 72);
        graphics.drawString(font, Component.literal("Alternative"), x + 36, y + 81, 0xffffff);

        CreateIDLXGuiTextures.RLE_PL_CAPTURE_GROUPS_BRACKETS.render(graphics, x + 115, y + 56);

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

    public void selectPlaceholder(int index) {
        selectedPlaceholderIndex = index;

        for (RLEPlaceholderTabButton button : placeholderTabButtons) button.setSelected(index == button.getIndex());

        buildPlaceholderConfig(index);
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

    public void onLabelUpdate(String newText) {
        onLabelUpdate(newText, false);
    }

    public void onLabelUpdate(String newText, boolean forceUpdate) {
        if (!forceUpdate && Objects.equals(newText, text)) return;
        text = newText;
        refreshPlaceholders();
        buildPlaceholderTabs(selectedPlaceholderIndex);
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
