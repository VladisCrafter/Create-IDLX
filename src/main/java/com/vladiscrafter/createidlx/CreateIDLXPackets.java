package com.vladiscrafter.createidlx;

import com.vladiscrafter.createidlx.content.clipboard.ClipboardDisplaySourceConfigurationPacket;
import com.vladiscrafter.createidlx.content.displayLink.DisplayLinkBehaviour;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkBlockEntity;
import com.simibubi.create.content.equipment.clipboard.ClipboardContent;
import com.simibubi.create.content.equipment.clipboard.ClipboardOverrides.ClipboardType;
import com.simibubi.create.AllDataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public final class CreateIDLXPackets {
    private CreateIDLXPackets() {}

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(CreateIDLX.ID);

        registrar.playToServer(
                ClipboardDisplaySourceConfigurationPacket.TYPE,
                ClipboardDisplaySourceConfigurationPacket.STREAM_CODEC,
                CreateIDLXPackets::handleClipboardDisplaySource
        );
    }

    private static void handleClipboardDisplaySource(ClipboardDisplaySourceConfigurationPacket packet, IPayloadContext pc) {
        pc.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) pc.player();
            Level level = player.level();

            BlockEntity be = level.getBlockEntity(packet.pos());
            if (!(be instanceof DisplayLinkBlockEntity displayLink)) return;

            DisplayLinkBehaviour behaviour = displayLink.getBehaviour(DisplayLinkBehaviour.TYPE);
            if (behaviour == null) return;

            ItemStack clipboard = player.getMainHandItem();
            ClipboardContent clipboardContent = clipboard.getOrDefault(AllDataComponents.CLIPBOARD_CONTENT, ClipboardContent.EMPTY);

            if (!packet.paste()) {
                CompoundTag copied = new CompoundTag();
                if (!behaviour.copyToClipboard(copied, packet.includeLabel(), packet.includeConfig(), packet.includeTarget())) return;

                clipboardContent = clipboardContent.setType(ClipboardType.WRITTEN);
                clipboardContent = clipboardContent.setCopiedValues(copied);
                clipboard.set(AllDataComponents.CLIPBOARD_CONTENT, clipboardContent);
                return;
            }

            CompoundTag copiedValues = clipboardContent.copiedValues().orElse(null);
            if (copiedValues == null) return;

            behaviour.applyFromClipboard(
                    copiedValues.getCompound("DisplaySource"), packet.includeLabel(), packet.includeConfig(), packet.includeTarget());
        });
    }
}