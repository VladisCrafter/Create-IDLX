package com.vladiscrafter.createidlx.content.clipboard;

import com.vladiscrafter.createidlx.CreateIDLX;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.RegistryFriendlyByteBuf;

public record ClipboardDisplaySourceConfigurationPacket(
        BlockPos pos, boolean paste, boolean includeLabel, boolean includeConfig, boolean includeTarget) implements CustomPacketPayload {
    public static final StreamCodec<RegistryFriendlyByteBuf, ClipboardDisplaySourceConfigurationPacket> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, ClipboardDisplaySourceConfigurationPacket::pos,
                    ByteBufCodecs.BOOL, ClipboardDisplaySourceConfigurationPacket::paste,
                    ByteBufCodecs.BOOL, ClipboardDisplaySourceConfigurationPacket::includeLabel,
                    ByteBufCodecs.BOOL, ClipboardDisplaySourceConfigurationPacket::includeConfig,
                    ByteBufCodecs.BOOL, ClipboardDisplaySourceConfigurationPacket::includeTarget,
                    ClipboardDisplaySourceConfigurationPacket::new
            );

    public static final Type<ClipboardDisplaySourceConfigurationPacket> TYPE = new Type<>(CreateIDLX.asResource("clipboard_display_source"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
