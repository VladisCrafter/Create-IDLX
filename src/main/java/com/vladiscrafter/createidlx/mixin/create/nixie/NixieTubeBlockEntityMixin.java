package com.vladiscrafter.createidlx.mixin.create.nixie;

import com.simibubi.create.content.redstone.nixieTube.NixieTubeBlockEntity;
import com.vladiscrafter.createidlx.util.bridge.NixieTubeBlockEntityInternalCustomTextHolder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NixieTubeBlockEntity.class)
public class NixieTubeBlockEntityMixin implements NixieTubeBlockEntityInternalCustomTextHolder {
    @Unique private String createidlx$internalCustomText = "";

    @Inject(method = "write", at = @At("TAIL"))
    private void createidlx$writeInternalCustomText(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket, CallbackInfo ci) {
        nbt.putString("InternalCustomText", createidlx$internalCustomText);
    }

    @Inject(method = "read", at = @At("TAIL"))
    private void createidlx$readInternalCustomText(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket, CallbackInfo ci) {
        if (nbt.contains("InternalCustomText")) {
            createidlx$internalCustomText = nbt.getString("InternalCustomText");
        }
    }

    @Override
    public String createidlx$getInternalCustomText() {
        return createidlx$internalCustomText;
    }

    @Override
    public void createidlx$setInternalCustomText(String text) {
        createidlx$internalCustomText = text;
    }
}
