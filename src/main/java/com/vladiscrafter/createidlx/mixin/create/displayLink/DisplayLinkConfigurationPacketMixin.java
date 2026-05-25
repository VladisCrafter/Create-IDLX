package com.vladiscrafter.createidlx.mixin.create.displayLink;

import com.simibubi.create.content.redstone.displayLink.DisplayLinkBlockEntity;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkConfigurationPacket;
import com.vladiscrafter.createidlx.util.bridge.DisplayLinkVisualizationConfigHolder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DisplayLinkConfigurationPacket.class)
public abstract class DisplayLinkConfigurationPacketMixin {

    @Shadow @Final
    private CompoundTag configData;

    @Inject(method = "applySettings", at = @At("HEAD"))
    private void createidlx$voidDuplicatedVisualizationConfig(ServerPlayer player, DisplayLinkBlockEntity be, CallbackInfo ci) {
        if (!(be instanceof DisplayLinkVisualizationConfigHolder holder)) return;

        if (configData.contains("Visualization", Tag.TAG_COMPOUND))
            holder.createidlx$setVisualizationConfig(configData.getCompound("Visualization"));

        configData.remove("Visualization");
    }
}