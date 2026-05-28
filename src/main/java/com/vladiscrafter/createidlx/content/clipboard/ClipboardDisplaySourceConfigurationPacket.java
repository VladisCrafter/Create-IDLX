package com.vladiscrafter.createidlx.content.clipboard;

import com.simibubi.create.content.equipment.clipboard.ClipboardOverrides;
import com.simibubi.create.content.equipment.clipboard.ClipboardOverrides.ClipboardType;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkBlockEntity;
import com.vladiscrafter.createidlx.content.displayLink.DisplayLinkBehaviour;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record ClipboardDisplaySourceConfigurationPacket(
        BlockPos pos,
        boolean paste,
        boolean includeLabel,
        boolean includeConfig,
        boolean includeTarget
) {
    public static void encode(ClipboardDisplaySourceConfigurationPacket msg, FriendlyByteBuf buf) {
        buf.writeBlockPos(msg.pos());
        buf.writeBoolean(msg.paste());
        buf.writeBoolean(msg.includeLabel());
        buf.writeBoolean(msg.includeConfig());
        buf.writeBoolean(msg.includeTarget());
    }

    public static ClipboardDisplaySourceConfigurationPacket decode(FriendlyByteBuf buf) {
        return new ClipboardDisplaySourceConfigurationPacket(
                buf.readBlockPos(),
                buf.readBoolean(),
                buf.readBoolean(),
                buf.readBoolean(),
                buf.readBoolean()
        );
    }

    public static void handle(ClipboardDisplaySourceConfigurationPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            Level level = player.level();
            if (!level.hasChunkAt(packet.pos())) return;

            BlockEntity be = level.getBlockEntity(packet.pos());
            if (!(be instanceof DisplayLinkBlockEntity displayLink)) return;

            DisplayLinkBehaviour behaviour = displayLink.getBehaviour(DisplayLinkBehaviour.TYPE);
            if (behaviour == null) return;

            ItemStack clipboard = player.getMainHandItem();
            CompoundTag clipboardContent = clipboard.getOrCreateTag();

            if (!packet.paste()) {
                CompoundTag copied = new CompoundTag();
                if (!behaviour.copyToClipboard(copied, packet.includeLabel(), packet.includeConfig(), packet.includeTarget())) return;

//                clipboardContent.putInt("Type", ClipboardType.WRITTEN.ordinal());
                ClipboardOverrides.switchTo(ClipboardType.WRITTEN, clipboard);
                clipboard.getOrCreateTag().put("CopiedValues", copied);
                return;
            }

            @SuppressWarnings("DataFlowIssue")
            CompoundTag copiedValues = Minecraft.getInstance().player.getMainHandItem().getTagElement("CopiedValues");
            if (copiedValues == null) return;

            behaviour.applyFromClipboard(
                    copiedValues.getCompound("DisplaySource"),
                    packet.includeLabel(),
                    packet.includeConfig(),
                    packet.includeTarget()
            );
        });

        ctx.get().setPacketHandled(true);
    }
}