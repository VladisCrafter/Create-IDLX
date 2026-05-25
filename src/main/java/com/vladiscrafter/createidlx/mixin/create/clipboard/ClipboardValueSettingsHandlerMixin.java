package com.vladiscrafter.createidlx.mixin.create.clipboard;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.equipment.clipboard.ClipboardValueSettingsHandler;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkBlockEntity;
import com.vladiscrafter.createidlx.CreateIDLX;
import com.vladiscrafter.createidlx.content.clipboard.ClipboardDisplaySourceScreen;
import net.createmod.catnip.gui.ScreenOpener;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
@Mixin(ClipboardValueSettingsHandler.class)
public abstract class ClipboardValueSettingsHandlerMixin {

    @Inject(method = "interact", at = @At("HEAD"), cancellable = true, remap = false)
    private static void createidlx$openDisplayLinkClipboardScreen(PlayerInteractEvent event, boolean paste, CallbackInfo ci) {
        if (!(event.getLevel().getBlockEntity(event.getPos()) instanceof DisplayLinkBlockEntity displayLink))
            return;
        if (!AllBlocks.CLIPBOARD.isIn(event.getItemStack()))
            return;
        if (event.getEntity().isShiftKeyDown() || event.getEntity().isSpectator())
            return;

        if (event.getLevel().isClientSide()) {
            CompoundTag snapshot = event.getItemStack()
                    .getOrCreateTag().getCompound("CopiedValues");

            ScreenOpener.open(new ClipboardDisplaySourceScreen(displayLink, paste, snapshot, event.getFace()));
        }

        if (event.isCancelable()) {
            event.setCanceled(true);
        }

        if (event instanceof PlayerInteractEvent.RightClickBlock rcb) {
            rcb.setCancellationResult(InteractionResult.SUCCESS);
        }

        if (event instanceof PlayerInteractEvent.LeftClickBlock lcb) {
            lcb.setCanceled(true);
        }

        ci.cancel();
    }
}