package com.vladiscrafter.createidlx.mixin.create.clipboard;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllDataComponents;
import com.simibubi.create.content.equipment.clipboard.ClipboardContent;
import com.simibubi.create.content.equipment.clipboard.ClipboardValueSettingsHandler;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkBlockEntity;
import com.vladiscrafter.createidlx.content.clipboard.ClipboardDisplaySourceScreen;
import net.createmod.catnip.gui.ScreenOpener;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
@Mixin(ClipboardValueSettingsHandler.class)
public abstract class ClipboardValueSettingsHandlerClientMixin {

    @Inject(method = "interact", at = @At("HEAD"), cancellable = true)
    private static void createidlx$openDisplayLinkClipboardScreenClient(PlayerInteractEvent event, boolean paste, CallbackInfo ci) {
        if (!(event.getLevel().getBlockEntity(event.getPos()) instanceof DisplayLinkBlockEntity displayLink))
            return;
        if (!AllBlocks.CLIPBOARD.isIn(event.getItemStack()))
            return;
        if (event.getEntity().isShiftKeyDown() || event.getEntity().isSpectator())
            return;
        if (!event.getLevel().isClientSide())
            return;

        CompoundTag snapshot = event.getItemStack()
                .getOrDefault(AllDataComponents.CLIPBOARD_CONTENT, ClipboardContent.EMPTY)
                .copiedValues()
                .orElse(null);

        ScreenOpener.open(new ClipboardDisplaySourceScreen(displayLink, paste, snapshot, event.getFace()));

        if (event instanceof ICancellableEvent cancellable) {
            cancellable.setCanceled(true);
        }

        ci.cancel();
    }
}

