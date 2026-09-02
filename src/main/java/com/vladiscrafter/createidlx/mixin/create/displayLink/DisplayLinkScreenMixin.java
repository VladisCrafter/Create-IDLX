package com.vladiscrafter.createidlx.mixin.create.displayLink;

import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.api.behaviour.display.DisplaySource;
import com.simibubi.create.api.behaviour.display.DisplayTarget;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkBlockEntity;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkScreen;
import com.simibubi.create.foundation.gui.widget.Label;
import com.simibubi.create.foundation.gui.widget.ScrollInput;
import com.vladiscrafter.createidlx.util.bridge.DisplayLinkScreenMixinSubstitutionHolder;
import com.vladiscrafter.createidlx.util.bridge.DisplayLinkVisualizationConfigHolder;
import com.vladiscrafter.createidlx.util.gui.CreateIDLXGuiContext;
import com.vladiscrafter.createidlx.util.substitute.DisplayLinkScreenMixinSubstitute;
import net.createmod.catnip.gui.AbstractSimiScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.List;

@Mixin(DisplayLinkScreen.class)
public abstract class DisplayLinkScreenMixin extends AbstractSimiScreen implements DisplayLinkScreenMixinSubstitutionHolder {
    @Shadow List<DisplaySource> sources;
    @Shadow ScrollInput sourceTypeSelector;
    @Shadow Label sourceTypeLabel;
    @Shadow private DisplayLinkBlockEntity blockEntity;
    @Shadow BlockState targetState;
    @Shadow DisplayTarget target;

    @Shadow protected abstract void initGathererSourceSubOptions(int i);
    @Shadow public abstract void onClose();
    @Shadow public abstract void tick();

    @Unique
    private final DisplayLinkScreenMixinSubstitute createidlx$substitute = new DisplayLinkScreenMixinSubstitute(this);

    @Inject(method = "init", at = @At("TAIL"))
    private void createidlx$pullVisualizationSettings(CallbackInfo ci) {
        createidlx$substitute.pullVisualizationSettings();
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void createidlx$updateLabelingBoxOutlineAlpha(CallbackInfo ci) {
        createidlx$substitute.updateRichLabelEditorButtonOutlineAlpha();
    }

    @Inject(method = "initGathererOptions", at = @At("TAIL"))
    private void createidlx$replaceSourceTypeSelector(CallbackInfo ci) {
        createidlx$substitute.replaceSourceTypeSelector();
    }

    @Inject(method = "initGathererOptions", at = @At("TAIL"))
    private void createidlx$cacheTargetWidgetTooltip(CallbackInfo ci) {
        createidlx$substitute.cacheTargetWidgetTooltip();
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
    private void createidlx$initButtons(int i, CallbackInfo ci) {
        createidlx$substitute.initRichLabelEditorButton(i);
        createidlx$substitute.initClipboardGuideButton(i);
        createidlx$substitute.initVisualizationSettingsButton(i);
    }

    @Inject(method = "renderWindow", at = @At("TAIL"))
    private void createidlx$renderRichEditorButtonOutline(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        createidlx$substitute.renderPlaceholdersStatusTooltips(graphics); // deprecated
        createidlx$substitute.renderRichEditorButtonOutline(graphics, mouseX, mouseY, partialTicks);
        createidlx$substitute.renderVisualizationSettingsTooltips();
    }

    @Inject(method = "onClose", at = @At(value = "INVOKE", target = "Lnet/createmod/catnip/platform/services/NetworkHelper;sendToServer(Lnet/minecraft/network/protocol/common/custom/CustomPacketPayload;)V"))
    private void createidlx$writeVisualizationConfig(CallbackInfo ci, @Local(name = "sourceData") CompoundTag sourceData) {
        CompoundTag createidlx$visualizationConfig = ((DisplayLinkVisualizationConfigHolder) blockEntity).createidlx$getVisualizationConfig();
        CompoundTag visualizationData = createidlx$substitute.getVisualizationData(createidlx$visualizationConfig);
        sourceData.put("Visualization", visualizationData);
    }

    @Override
    public List<DisplaySource> createidlx$getSources() {
        return sources;
    }

    @Override
    public ScrollInput createidlx$getSourceTypeSelector() {
        return sourceTypeSelector;
    }

    @Override
    public Label createidlx$getSourceTypeLabel() {
        return sourceTypeLabel;
    }

    @Override
    public DisplayLinkBlockEntity createidlx$getBlockEntity() {
        return blockEntity;
    }

    @Override
    public DisplayTarget createidlx$getTarget() {
        return target;
    }

    @Override
    public BlockState createidlx$getTargetState() {
        return targetState;
    }

    @Override
    public int createidlx$getGuiLeft() {
        return guiLeft;
    }

    @Override
    public int createidlx$getGuiTop() {
        return guiTop;
    }

    @Override
    public void createidlx$callInitGathererSourceSubOptions(int i) {
        initGathererSourceSubOptions(i);
    }

    @Override
    public void createidlx$callOnClose() {
        onClose();
    }

    @Override
    public void createidlx$callTick() {
        tick();
    }

    @Override
    public <T extends GuiEventListener & Renderable & NarratableEntry> void createidlx$callAddRenderableWidget(T widget) {
        this.addRenderableWidget(widget);
    }

    @Override
    public <W extends GuiEventListener & Renderable & NarratableEntry> void createidlx$callAddRenderableWidgets(Collection<W> widgets) {
        for (W widget : widgets) {
            this.addRenderableWidget(widget);
        }
    }

    @Override
    public <T extends GuiEventListener & NarratableEntry> void createidlx$callAddWidget(T widget) {
        this.addWidget(widget);
    }

    @Override
    public void createidlx$callRemoveWidget(@NotNull GuiEventListener widget) {
        super.removeWidget(widget);
    }

    @Override
    public void createidlx$callRemoveWidgets(Collection<? extends GuiEventListener> widgets) {
        for (GuiEventListener widget : widgets) {
            super.removeWidget(widget);
        }
    }
}
