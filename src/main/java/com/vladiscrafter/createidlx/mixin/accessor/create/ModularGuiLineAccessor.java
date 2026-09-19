package com.vladiscrafter.createidlx.mixin.accessor.create;

import com.simibubi.create.foundation.gui.ModularGuiLine;
import net.createmod.catnip.data.Pair;
import net.minecraft.client.gui.components.AbstractWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(ModularGuiLine.class)
public interface ModularGuiLineAccessor {
    @Accessor("widgets")
    List<Pair<AbstractWidget, String>> createidlx$getWidgets();
}
