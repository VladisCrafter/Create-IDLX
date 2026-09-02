package com.vladiscrafter.createidlx.util.bridge;

import com.simibubi.create.api.behaviour.display.DisplaySource;
import com.simibubi.create.api.behaviour.display.DisplayTarget;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkBlockEntity;
import com.simibubi.create.foundation.gui.widget.Label;
import com.simibubi.create.foundation.gui.widget.ScrollInput;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Collection;
import java.util.List;


public interface DisplayLinkScreenMixinSubstitutionHolder {
    List<DisplaySource> createidlx$getSources();
    ScrollInput createidlx$getSourceTypeSelector();
    Label createidlx$getSourceTypeLabel();
    DisplayLinkBlockEntity createidlx$getBlockEntity();
    BlockState createidlx$getTargetState();
    DisplayTarget createidlx$getTarget();
    int createidlx$getGuiLeft();
    int createidlx$getGuiTop();

    void createidlx$callInitGathererSourceSubOptions(int i);
    void createidlx$callOnClose();
    void createidlx$callTick();

    <T extends GuiEventListener & Renderable & NarratableEntry> void createidlx$callAddRenderableWidget(T widget);
    <W extends GuiEventListener & Renderable & NarratableEntry> void createidlx$callAddRenderableWidgets(Collection<W> widgets);
    <T extends GuiEventListener & NarratableEntry> void createidlx$callAddWidget(T listener);
    void createidlx$callRemoveWidget(GuiEventListener listener);
    void createidlx$callRemoveWidgets(Collection<? extends GuiEventListener> widgets);
}
