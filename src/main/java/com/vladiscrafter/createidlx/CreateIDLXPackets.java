package com.vladiscrafter.createidlx;

import com.vladiscrafter.createidlx.content.clipboard.ClipboardDisplaySourceConfigurationPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public final class CreateIDLXPackets {
    private CreateIDLXPackets() {}

    private static final String PROTOCOL_VERSION = "1";

    @SuppressWarnings("removal")
    public static final SimpleChannel NETWORK = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(CreateIDLX.ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int id = 0;

    public static void register() {
        NETWORK.registerMessage(
                id++,
                ClipboardDisplaySourceConfigurationPacket.class,
                ClipboardDisplaySourceConfigurationPacket::encode,
                ClipboardDisplaySourceConfigurationPacket::decode,
                ClipboardDisplaySourceConfigurationPacket::handle
        );
    }
}