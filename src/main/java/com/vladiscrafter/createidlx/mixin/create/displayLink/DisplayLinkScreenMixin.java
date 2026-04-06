package com.vladiscrafter.createidlx.mixin.create.displayLink;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllDataComponents;
import com.simibubi.create.api.behaviour.display.DisplaySource;
import com.simibubi.create.api.behaviour.display.DisplayTarget;
import com.simibubi.create.content.equipment.clipboard.ClipboardContent;
import com.simibubi.create.content.equipment.clipboard.ClipboardOverrides.ClipboardType;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkBlockEntity;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkScreen;
import com.simibubi.create.content.redstone.displayLink.source.SingleLineDisplaySource;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.gui.widget.Label;
import com.simibubi.create.foundation.gui.widget.ScrollInput;
import com.simibubi.create.foundation.utility.CreateLang;
import com.vladiscrafter.createidlx.CreateIDLX;
import com.vladiscrafter.createidlx.util.gui.CreateIDLXGuiContext;
import com.vladiscrafter.createidlx.foundation.gui.CreateIDLXIcons;
import com.vladiscrafter.createidlx.config.CIDLXConfigs;
import com.vladiscrafter.createidlx.util.gui.CreateIDLXGuiTooltipBuffer;
import com.vladiscrafter.createidlx.util.widget.InBoundsSelectionScrollInput;
import net.createmod.catnip.gui.AbstractSimiScreen;
import net.createmod.catnip.gui.ScreenOpener;
import net.createmod.ponder.foundation.ui.PonderUI;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(DisplayLinkScreen.class)
public abstract class DisplayLinkScreenMixin extends AbstractSimiScreen {
//    @Unique
//    private IconButton createidlx$specifierHelpButton;

    @Shadow private List<DisplaySource> sources;
    @Shadow private ScrollInput sourceTypeSelector;
    @Shadow private Label sourceTypeLabel;

    @Shadow protected abstract void initGathererSourceSubOptions(int i);

    @Shadow private DisplayLinkBlockEntity blockEntity;
    @Shadow private BlockState targetState;
    @Shadow private DisplayTarget target;

    @Shadow
    public abstract void onClose();

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
            CreateIDLXGuiTooltipBuffer.registerSourceTypeSelectorWidget(sourceTypeSelector);
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
        CreateIDLXGuiTooltipBuffer.registerSourceTypeSelectorWidget(sourceTypeSelector);
        initGathererSourceSubOptions(0);
    }

    @Inject(method = "initGathererOptions", at = @At("TAIL"))
    private void createidlx$cacheTargetWidgetTooltip(CallbackInfo ci) {
        CreateIDLXGuiTooltipBuffer.registerTargetWidgetTooltip(List.of(
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
        placeholdersGuideButton.withCallback((mX, mY) -> {
            onClose();
            ScreenOpener.transitionTo(PonderUI.of(AllBlocks.DISPLAY_LINK.asStack()));
        });

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

        IconButton clipboardButton = new IconButton(guiLeft + 36, guiTop + 67, 16, 16, AllIcons.I_NONE);
        clipboardButton.active = false;

        this.addRenderableWidget(placeholdersGuideButton);
        this.addRenderableWidget(clipboardButton);
//        this.createidlx$specifierHelpButton = placeholdersGuideButton;
    }

    @Inject(method = "renderWindow", at = @At("TAIL"))
    private void injectClipboardIcon(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        int itemX = guiLeft + 37;
        int itemY = guiTop + 68;

        ItemStack clipboard = AllBlocks.CLIPBOARD.asStack();
        clipboard.set(AllDataComponents.CLIPBOARD_CONTENT, ClipboardContent.EMPTY.setType(ClipboardType.WRITTEN));
        graphics.renderItem(clipboard, itemX, itemY);

        List<Component> clipboardTip = new ArrayList<>(List.of());
        clipboardTip.addAll(CreateIDLX.translateMultiline("gui.display_link.clipboard_tooltip_1", ChatFormatting.GRAY));
        clipboardTip.addAll(CreateIDLX.translateMultiline("gui.display_link.clipboard_tooltip_2", ChatFormatting.GRAY));
        clipboardTip.addAll(CreateIDLX.translateMultiline("gui.display_link.clipboard_tooltip_3", ChatFormatting.GRAY));
        clipboardTip.addFirst(CreateIDLX.translate("gui.display_link.clipboard_tooltip_header").withStyle(s -> s.withColor(0x5391E1)));

        if (mouseX >= itemX && mouseX < itemX + 16 && mouseY >= itemY && mouseY < itemY + 16) {
            graphics.renderComponentTooltip(Minecraft.getInstance().font, clipboardTip, mouseX, mouseY);
        }
    }

}
