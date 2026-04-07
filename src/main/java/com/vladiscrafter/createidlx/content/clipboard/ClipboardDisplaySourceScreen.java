package com.vladiscrafter.createidlx.content.clipboard;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllDataComponents;
import com.simibubi.create.AllKeys;
import com.simibubi.create.content.equipment.clipboard.ClipboardContent;
import com.simibubi.create.content.equipment.clipboard.ClipboardOverrides.ClipboardType;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkBlockEntity;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkScreen;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.infrastructure.config.AllConfigs;
import com.vladiscrafter.createidlx.CreateIDLX;
import com.vladiscrafter.createidlx.foundation.gui.CreateIDLXGuiTextures;
import com.vladiscrafter.createidlx.util.widget.InBoundsSelectionScrollInput;
import net.createmod.catnip.gui.AbstractSimiScreen;
import com.simibubi.create.foundation.gui.widget.IconButton;
import net.createmod.catnip.gui.ScreenOpener;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.createmod.catnip.gui.widget.AbstractSimiWidget;
import net.createmod.catnip.gui.widget.BoxWidget;
import net.createmod.catnip.gui.widget.ElementWidget;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ClipboardDisplaySourceScreen extends AbstractSimiScreen {

    private final DisplayLinkBlockEntity displayLink;
    private final boolean paste;
    private final @Nullable CompoundTag clipboardSnapshot;
    private final Direction interactionFace;

    BlockState sourceState;
    BlockState targetState;

    private boolean labelAvailable = true;
    private boolean configAvailable = true;
    private boolean targetAvailable = true;

    private boolean labelEnabled = true;

    private String labelUnavailabilityReason;
    private String configUnavailabilityReason;
    private String targetUnavailabilityReason;

    private boolean includeLabel = labelAvailable;
    private boolean includeConfig = configAvailable;
    private boolean includeTarget = targetAvailable;

    private final CreateIDLXGuiTextures background;

    AbstractSimiWidget sourceWidget;
    InBoundsSelectionScrollInput sourceTypeSelector;

    AbstractSimiWidget clipboardWidget;

    protected IconButton labelButton;
    protected IconButton configButton;
    protected IconButton targetButton;

    protected IconButton confirmButton;

    public ClipboardDisplaySourceScreen(DisplayLinkBlockEntity displayLink, boolean paste,
                                        @Nullable CompoundTag clipboardSnapshot, Direction interactionFace) {
        this.displayLink = displayLink;
        this.paste = paste;
        this.background = !paste ? CreateIDLXGuiTextures.CLIPBOARD_DISPLAY_SOURCE_COPYING : CreateIDLXGuiTextures.CLIPBOARD_DISPLAY_SOURCE_PASTING;
        this.clipboardSnapshot = clipboardSnapshot;
        this.interactionFace = interactionFace;
    }

    @Override
    protected void init() {
        setWindowSize(background.getWidth(), background.getHeight());
        super.init();
        clearWidgets();

        int x = guiLeft;
        int y = guiTop;

        checkOptionsAvailability();

        labelButton = new IconButton(x + (!paste ? 36 : 103), y + (!paste ? 64 : 25), AllIcons.I_WHITELIST_OR);
        configButton = new IconButton(x + (!paste ? 76 : 143), y + (!paste ? 64 : 25), AllIcons.I_VIEW_SCHEDULE);
        targetButton = new IconButton(x + (!paste ? 116 : 183), y + (!paste ? 64 : 25), AllIcons.I_PASSIVE);

        labelButton.setToolTip(translateLocalBin("label_button")
                .withStyle(s -> s.withColor(0x5391E1)));
        configButton.setToolTip(translateLocalBin("config_button")
                .withStyle(s -> s.withColor(0x5391E1)));
        targetButton.setToolTip(translateLocalBin("target_button")
                .withStyle(s -> s.withColor(0x5391E1)));

        labelButton.active = labelAvailable;
        configButton.active = configAvailable;
        targetButton.active = targetAvailable;

        List<MutableComponent> labelButtonTooltipComponents =
                translateLocalMultiline("label_button.unavailable_reason." + labelUnavailabilityReason, ChatFormatting.GRAY);
        labelButtonTooltipComponents.addFirst(translateLocal("button.unavailable").withStyle(s -> s.withColor(0xff5d6c)));

        List<MutableComponent> labelButtonTooltipComponentsDisabled =
                translateLocalMultiline("label_button.disabled_reason.empty", ChatFormatting.GRAY);
        labelButtonTooltipComponentsDisabled.addFirst(translateLocal("button.disabled").withStyle(s -> s.withColor(0xe0b653)));

        labelButton.getToolTip().addAll(!labelAvailable ? labelButtonTooltipComponents
                        : labelEnabled ? translateLocalMultiline("label_button.tip", ChatFormatting.GRAY) : labelButtonTooltipComponentsDisabled);

        List<MutableComponent> configButtonTooltipComponents =
                translateLocalMultiline("config_button.unavailable_reason." + configUnavailabilityReason, ChatFormatting.GRAY);
        configButtonTooltipComponents.addFirst(translateLocal("button.unavailable").withStyle(s -> s.withColor(0xff5d6c)));

        configButton.getToolTip().addAll(!configAvailable ? configButtonTooltipComponents
                        : translateLocalMultiline("config_button.tip", ChatFormatting.GRAY));

        List<MutableComponent> targetButtonTooltipComponents =
                translateLocalMultiline("target_button.unavailable_reason." + targetUnavailabilityReason, ChatFormatting.GRAY);
        targetButtonTooltipComponents.addFirst(translateLocal("button.unavailable").withStyle(s -> s.withColor(0xff5d6c)));

        targetButton.getToolTip().addAll(!targetAvailable ? targetButtonTooltipComponents
                        : translateLocalMultiline("target_button.tip", ChatFormatting.GRAY));

        if (configUnavailabilityReason != null && configUnavailabilityReason.equals("different_sources")) configButton.getToolTip().addAll(List.of(
                translateLocal("config_button.unavailable_reason.different_sources.details.inheritor",
                        Component.literal(displayLink.activeSource.getName().getString())
                                .withStyle(s -> s.withColor(0xff5d6c))).withStyle(ChatFormatting.DARK_GRAY),
                translateLocal("config_button.unavailable_reason.different_sources.details.origin",
                        Component.translatable(clipboardSnapshot.getCompound("DisplaySource").getString("SourceKey"))
                        .withStyle(s -> s.withColor(0xe0b653))).withStyle(ChatFormatting.DARK_GRAY)));

        labelButton.withCallback(() -> includeLabel = !includeLabel);
        configButton.withCallback(() -> includeConfig = !includeConfig);
        targetButton.withCallback(() -> includeTarget = !includeTarget);

        confirmButton = new IconButton(x + background.getWidth() - 33, y + background.getHeight() - 24, AllIcons.I_CONFIRM).withCallback(this::onClose);

        addRenderableWidget(labelButton);
        addRenderableWidget(configButton);
        addRenderableWidget(targetButton);
        addRenderableWidget(confirmButton);

        initGathererOptions();
    }

    private void checkOptionsAvailability() {
        if (paste && clipboardSnapshot == null) {
            includeLabel = includeConfig = includeTarget = labelAvailable = configAvailable = targetAvailable = false;
            labelUnavailabilityReason = configUnavailabilityReason = targetUnavailabilityReason = "not_saved";
            return;
        }

        if (!displayLink.getSourceConfig().contains("Label")) {
            includeLabel = labelAvailable = false;
            labelUnavailabilityReason = !paste ? "origin_labelless" : "inheritor_labelless";
        } else if (paste && !clipboardSnapshot.getCompound("DisplaySource").contains("AttachedLabel")) {
            includeLabel = labelAvailable = false;
            labelUnavailabilityReason = "not_saved";
        } else if ((!paste && displayLink.getSourceConfig().getString("Label").isEmpty())
                || (paste && clipboardSnapshot.getCompound("DisplaySource").getString("AttachedLabel").isEmpty())) {
            includeLabel = labelEnabled = false;
            labelAvailable = true;
        }

        CompoundTag sourceConfigUnfiltered = displayLink.getSourceConfig();
        CompoundTag sourceConfig = new CompoundTag();
        for (String setting : sourceConfigUnfiltered.getAllKeys()) {
            if (!setting.equals("Id") && !setting.equals("Label")) sourceConfig.put(setting, sourceConfigUnfiltered.get(setting).copy());
        }

        if (sourceConfig.isEmpty()) {
            includeConfig = configAvailable = false;
            configUnavailabilityReason = !paste ? "origin_configless" : "inheritor_configless";
        } else if (paste && !clipboardSnapshot.getCompound("DisplaySource").contains("SourceConfig")) {
            includeConfig = configAvailable = false;
            configUnavailabilityReason = "not_saved";
        } else if
        (paste && !clipboardSnapshot.getCompound("DisplaySource").getString("SourceId").equals(displayLink.getSourceConfig().getString("Id"))) {
            includeConfig = configAvailable = false;
            configUnavailabilityReason = "different_sources";
        }

        BlockPos targetPos = BlockPos.ZERO;
        if (clipboardSnapshot != null)
            targetPos = NbtUtils.readBlockPos(clipboardSnapshot.getCompound("DisplaySource"), "TargetPos").orElse(BlockPos.ZERO);

        if (!paste && displayLink.getTargetPosition() == null) {
            includeTarget = targetAvailable = false;
            targetUnavailabilityReason = "target_invalid";
        } else if (paste && !clipboardSnapshot.getCompound("DisplaySource").contains("TargetDim")) {
            includeTarget = targetAvailable = false;
            targetUnavailabilityReason = "not_saved";
        } else if (paste && !clipboardSnapshot.getCompound("DisplaySource").getString("TargetDim")
                .equals(displayLink.getLevel().dimension().location().toString())) {
            includeTarget = targetAvailable = false;
            targetUnavailabilityReason = "target_in_other_dimension";
        } else if
        (paste && !displayLink.getTargetPosition().closerThan(targetPos, AllConfigs.server().logistics.displayLinkRange.get())) {
            includeTarget = targetAvailable = false;
            targetUnavailabilityReason = "target_too_far";
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (this.labelButton != null) labelButton.green = includeLabel;
        if (this.configButton != null) configButton.green = includeConfig;
        if (this.targetButton != null) targetButton.green = includeTarget;

        renderClipboardVisual();

        if (sourceState != null && sourceState.getBlock() != minecraft.level.getBlockState(displayLink.getSourcePosition()).getBlock()
                || targetState != null && targetState.getBlock() != minecraft.level.getBlockState(displayLink.getTargetPosition()).getBlock())
            initGathererOptions();
    }

    private void initGathererOptions() {
        int x = guiLeft;
        int y = guiTop;

        removeWidget(sourceWidget);
        removeWidget(sourceTypeSelector);
        removeWidget(clipboardWidget);

        sourceWidget = new ElementWidget(x + 37, y + (! paste ? 26 : 63))
                .showingElement(GuiGameElement.of(AllBlocks.DISPLAY_LINK.asStack()))
                .withCallback((mX, mY) -> ScreenOpener.open(new DisplayLinkScreen(displayLink)));

        sourceWidget.getToolTip().addAll(List.of(
                translateLocalBin("display_link_icon").withStyle(s -> s.withColor(0x5391E1)),
                translateLocal("source_widgets.hint").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC)
        ));

        Component displaySourceName = displayLink.activeSource != null ? displayLink.activeSource.getName() : translateLocal("display_source.invalid");

        sourceTypeSelector = (InBoundsSelectionScrollInput) new InBoundsSelectionScrollInput(
                guiLeft + 61, guiTop + (!paste ? 26 : 63), 135, 16, true, true)
                .forOptions(List.of(displaySourceName))
                .titled(translateLocalBin("display_source"))
                .setState(0);

        sourceTypeSelector.getToolTip().add(translateLocal("source_widgets.hint")
                .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));

        sourceTypeSelector.withCallback((mX, mY) -> ScreenOpener.open(new DisplayLinkScreen(displayLink)));

        addRenderableWidget(sourceWidget);
        addRenderableWidget(sourceTypeSelector);
    }

    private void renderClipboardVisual() {
        if (clipboardWidget != null) removeWidget(clipboardWidget);

        String labelStr = !paste ? displayLink.getSourceConfig().getString("Label")
                : clipboardSnapshot != null ? clipboardSnapshot.getCompound("DisplaySource").getString("AttachedLabel") : "";

        CompoundTag sourceConfigUnfiltered =
                !paste ? displayLink.getSourceConfig() : clipboardSnapshot != null
                        ? clipboardSnapshot.getCompound("DisplaySource").getCompound("SourceConfig") : new CompoundTag();
        CompoundTag sourceConfig = new CompoundTag();
        for (String setting : sourceConfigUnfiltered.getAllKeys()) {
            if (!setting.equals("Id") && !setting.equals("Label")) sourceConfig.put(setting, sourceConfigUnfiltered.copy());
        }

        String targetDimStr = !paste
                ? displayLink.getLevel().dimension().location().toString()
                : clipboardSnapshot != null ? clipboardSnapshot.getCompound("DisplaySource").getString("TargetDim") : "";

        Component targetDim = translateLocal("clipboard_visual.contents.target.detailed.dimension." +
                (targetDimStr.equals("minecraft:overworld") ? "overworld"
                        : targetDimStr.equals("minecraft:the_nether") ? "nether"
                            : targetDimStr.equals("minecraft:the_end") ? "end"
                                : "unknown"), targetDimStr);

        BlockPos targetPos = BlockPos.ZERO;
        if (!paste) targetPos = displayLink.getTargetPosition();
        else if (clipboardSnapshot != null)
            targetPos = NbtUtils.readBlockPos(clipboardSnapshot.getCompound("DisplaySource"), "TargetPos").orElse(BlockPos.ZERO);

        ItemStack clipboard = AllBlocks.CLIPBOARD.asStack();
        if (!paste) clipboard.set(AllDataComponents.CLIPBOARD_CONTENT, ClipboardContent.EMPTY.setType(
                ((includeLabel && labelAvailable) || (includeConfig && configAvailable) || (includeTarget && targetAvailable))
                        ? ClipboardType.WRITTEN : ClipboardType.EMPTY));
        else clipboard.set(AllDataComponents.CLIPBOARD_CONTENT, ClipboardContent.EMPTY.setType(
                clipboardSnapshot != null ? ClipboardType.WRITTEN : ClipboardType.EMPTY));

        clipboardWidget = new ElementWidget(guiLeft + (!paste ? 180 : 41), guiTop + (!paste ? 65 : 26))
                .showingElement(GuiGameElement.of(clipboard));

        clipboardWidget.getToolTip().add(translateLocalBin("clipboard_visual.title").withStyle(s -> s.withColor(0x5391E1)));

        Component label = translateLocalBin("clipboard_visual.contents.prefix",
                translateLocal("clipboard_visual.contents.label").withStyle(ChatFormatting.GRAY))
                .withStyle(!paste ? ChatFormatting.GREEN : ChatFormatting.WHITE);
        Component config = translateLocalBin("clipboard_visual.contents.prefix",
                translateLocal("clipboard_visual.contents.config").withStyle(ChatFormatting.GRAY))
                .withStyle(!paste ? ChatFormatting.GREEN : ChatFormatting.WHITE);
        Component target = translateLocalBin("clipboard_visual.contents.prefix",
                translateLocal("clipboard_visual.contents.target").withStyle(ChatFormatting.GRAY))
                .withStyle(!paste ? ChatFormatting.GREEN : ChatFormatting.WHITE);

        Component labelDetailed = translateLocalBin("clipboard_visual.contents.prefix",
                translateLocal("clipboard_visual.contents.label.detailed" + (labelStr.isEmpty() ? "_empty" : ""), labelStr).withStyle(ChatFormatting.GRAY))
                .withStyle(!paste ? ChatFormatting.GREEN : ChatFormatting.WHITE);
        Component configDetailed = translateLocalBin("clipboard_visual.contents.prefix",
                translateLocal("clipboard_visual.contents.config.detailed", sourceConfig.size()).withStyle(ChatFormatting.GRAY))
                .withStyle(!paste ? ChatFormatting.GREEN : ChatFormatting.WHITE);
        Component targetDetailed = translateLocalBin("clipboard_visual.contents.prefix",
                translateLocal("clipboard_visual.contents.target.detailed",
                        targetPos.getX(), targetPos.getY(), targetPos.getZ(), targetDim).withStyle(ChatFormatting.GRAY))
                .withStyle(!paste ? ChatFormatting.GREEN : ChatFormatting.WHITE);

        List<Component> components = new ArrayList<>();

        if ((labelAvailable && includeLabel) || (paste && (labelAvailable || !labelUnavailabilityReason.equals("not_saved"))))
            components.add(!AllKeys.shiftDown() ? label : labelDetailed);
        if ((configAvailable && includeConfig) || (paste && (configAvailable || !configUnavailabilityReason.equals("not_saved"))))
            components.add(!AllKeys.shiftDown() ? config :configDetailed);
        if ((targetAvailable && includeTarget) || (paste && (targetAvailable || !targetUnavailabilityReason.equals("not_saved"))))
            components.add(!AllKeys.shiftDown() ? target : targetDetailed);

        boolean noComponents = components.isEmpty();

        if (noComponents || (paste && clipboardSnapshot == null))
            components.add(translateLocal("clipboard_visual.contents.prefix.no_components").withStyle(ChatFormatting.DARK_GRAY));

        clipboardWidget.getToolTip().addAll(components);
        if(!noComponents && !AllKeys.shiftDown()) clipboardWidget.getToolTip().add(translateLocal("clipboard_visual.contents.hint")
                .withStyle(ChatFormatting.DARK_GRAY).withStyle(ChatFormatting.ITALIC));

        addRenderableWidget(clipboardWidget);
    }

    @Override
    protected void renderWindow(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        int x = guiLeft;
        int y = guiTop;
        background.render(graphics, x, y);
        MutableComponent header = translateLocalBin("title");
        graphics.drawString(font, header, x + background.getWidth() / 2 - font.width(header) / 2, y + 4, 0x592424, false);
    }

    private MutableComponent translateLocalBin(String key, Object... args) {
        return translateLocal(key + (!paste ? ".copy" : ".paste"), args);
    }

    private MutableComponent translateLocal(String key, Object... args) {
        return CreateIDLX.translate("gui.clipboard_display_source." + key, args);
    }

    private List<MutableComponent> translateLocalMultiline(String key, ChatFormatting style, Object... args) {
        String raw = CreateIDLX.translate("gui.clipboard_display_source." + key, args).getString();
        String[] splitStr = raw.split(Pattern.quote("\n"));
        List<MutableComponent> split = new ArrayList<>(List.of());
        for (String string : splitStr) split.add(Component.literal(string).withStyle(style));
        return split;
    }

    @Override
    public void onClose() {
        CatnipServices.NETWORK.sendToServer(new ClipboardDisplaySourceConfigurationPacket(
                displayLink.getBlockPos(), paste, includeLabel, includeConfig, includeTarget));
        super.onClose();
    }

    @Override
    protected void removeWidget(GuiEventListener widget) {
        if (widget != null) super.removeWidget(widget);
    }
}