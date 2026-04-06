package com.vladiscrafter.createidlx.mixin.create.clipboard;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllDataComponents;
import com.simibubi.create.content.equipment.clipboard.ClipboardContent;
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
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClipboardValueSettingsHandler.class)
public abstract class ClipboardValueSettingsHandlerMixin {

    @Inject(method = "interact", at = @At("HEAD"), cancellable = true)
    private static void createidlx$openDisplayLinkClipboardScreen(PlayerInteractEvent event, boolean paste, CallbackInfo ci) {
        if (!(event.getLevel().getBlockEntity(event.getPos()) instanceof DisplayLinkBlockEntity displayLink))
            return;
        if (!AllBlocks.CLIPBOARD.isIn(event.getItemStack()))
            return;
        if (event.getEntity().isShiftKeyDown() || event.getEntity().isSpectator())
            return;

        if (event.getLevel().isClientSide()) {
            CompoundTag snapshot = event.getItemStack()
                    .getOrDefault(AllDataComponents.CLIPBOARD_CONTENT, ClipboardContent.EMPTY)
                    .copiedValues()
                    .orElse(null);

            ScreenOpener.open(new ClipboardDisplaySourceScreen(displayLink, paste, snapshot, event.getFace()));
        }

        if (event instanceof ICancellableEvent cancellable) {
            cancellable.setCanceled(true);
        }

        if (event instanceof PlayerInteractEvent.RightClickBlock rcb) {
            rcb.setCancellationResult(InteractionResult.SUCCESS);
        }

        if (event instanceof PlayerInteractEvent.LeftClickBlock lcb) {
            lcb.setCanceled(true);
        }

        ci.cancel();
    }

//    @Inject(method = "interact", at = @At("TAIL"))
//    private static void createidlx$displayLinkClipboardMessages(PlayerInteractEvent event, boolean paste, CallbackInfo ci) {
//        Level level = event.getLevel();
//        if (level.isClientSide()) return;
//
//        BlockPos pos = event.getPos();
//        BlockEntity be = level.getBlockEntity(pos);
//        if (!(be instanceof DisplayLinkBlockEntity displayLink)) return;
//
//        Player player = event.getEntity();
//        ItemStack itemStack = event.getItemStack();
//        ClipboardContent clipboardContent = itemStack.getOrDefault(AllDataComponents.CLIPBOARD_CONTENT, ClipboardContent.EMPTY);
//        CompoundTag tag = clipboardContent.copiedValues().orElse(null);
//        if (tag == null || !tag.contains("DisplaySource")) return;
//
//        CompoundTag donorSource = tag.getCompound("DisplaySource");
//
//        String donorId = donorSource.getString("SourceId");
//        String attachedLabel = displayLink.getSourceConfig().getString("Label");
//        String recipientId = displayLink.getSourceConfig().getString("Id");
//
////        Component recipientIdTranslated = displayLink.activeSource.getName();
//
//        MutableComponent donorIdTranslated =
//                Component.translatable(donorId.split(":")[0] + ".display_source." + donorId.split(":")[1]);
//
//        boolean hasLabel = !attachedLabel.isEmpty();
//        boolean sameSource = donorId.equals(recipientId);
//
//        String message;
//        String reason;
//        String sources;
//
//        if (!paste) {
//            message = "copied_" + (hasLabel ? "all" : "config");
//            reason = "empty";
//            sources = "donor_only";
//        } else {
//            if (sameSource) {
//                message = "pasted_" + (hasLabel ? "all" : "config");
//                reason = hasLabel ? "empty" : "labelless";
//                sources = "same";
//            } else {
//                message = "pasted_" + (hasLabel ? "label" : "nothing");
//                reason = hasLabel ? "different" : "different_labelless";
//                sources = "different";
//            }
//        }
//
//        player.displayClientMessage(CreateIDLX.translate("clipboard." + message,
//                CreateIDLX.translate("clipboard.reason." + reason,
//                CreateIDLX.translate("clipboard.sources." + sources, donorIdTranslated, recipientId/*Translated*/))), true);
//    }
}