package com.vladiscrafter.createidlx.mixin.create.displayLink;

import com.simibubi.create.content.redstone.displayLink.DisplayLinkBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.vladiscrafter.createidlx.content.displayLink.DisplayLinkBehaviour;
import com.vladiscrafter.createidlx.util.bridge.DisplayLinkVisualizationConfigHolder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(DisplayLinkBlockEntity.class)
public abstract class DisplayLinkBlockEntityMixin implements DisplayLinkVisualizationConfigHolder {

    @Inject(method = "addBehaviours", at = @At("TAIL"), remap = false)
    private void createidlx$addClipboardBehaviour(List<BlockEntityBehaviour> behaviours, CallbackInfo ci) {
        SmartBlockEntity self = (SmartBlockEntity) (Object) this;

        if (self.getBehaviour(DisplayLinkBehaviour.TYPE) == null) {
            behaviours.add(new DisplayLinkBehaviour(self));
        }
    }

    @Unique private CompoundTag createidlx$visualizationConfig = new CompoundTag();

    @Override
    public CompoundTag createidlx$getVisualizationConfig() {
        return createidlx$visualizationConfig;
    }

    @Override
    public void createidlx$setVisualizationConfig(CompoundTag tag) {
        createidlx$visualizationConfig = (tag == null) ? new CompoundTag() : tag.copy();
    }

    @Inject(method = "read", at = @At("RETURN"))
    private void createidlx$readVisualizationConfig(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket, CallbackInfo ci) {
        if (tag.contains("Visualization", Tag.TAG_COMPOUND))
            createidlx$visualizationConfig = tag.getCompound("Visualization").copy();
        else
            createidlx$visualizationConfig = new CompoundTag();
    }

    @Inject(method = "writeGatheredData", at = @At("TAIL"))
    private void createidlx$writeVisualizationConfig(CompoundTag tag, CallbackInfo ci) {
        if (!createidlx$visualizationConfig.isEmpty())
            tag.put("Visualization", createidlx$visualizationConfig.copy());
    }
}