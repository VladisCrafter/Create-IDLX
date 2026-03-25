package com.vladiscrafter.createidlx;

import com.vladiscrafter.createidlx.foundation.ponder.CIDLXPonderPlugin;
import net.createmod.catnip.config.ui.BaseConfigScreen;
import net.createmod.ponder.foundation.PonderIndex;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.client.ConfigScreenHandler;

@EventBusSubscriber(modid = CreateIDLX.ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class CreateIDLXClient {
    public CreateIDLXClient(IEventBus modEventBus, ModContainer container) {
        modEventBus.addListener(CreateIDLXClient::onClientSetup);
        modEventBus.addListener(CreateIDLXClient::onLoadComplete);
    }

    @SubscribeEvent static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(CreateIDLXClient::clientInit);
    }

    private static void clientInit() {
        PonderIndex.addPlugin(new CIDLXPonderPlugin());

    }

    @SubscribeEvent
    public static void onLoadComplete(FMLLoadCompleteEvent event) {
        ModContainer container = ModList.get()
                .getModContainerById(CreateIDLX.ID)
                .orElseThrow(() -> new IllegalStateException("Create: IDLX mod container missing on LoadComplete"));
        container.registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory((mc, screen) -> new BaseConfigScreen(screen, CreateIDLX.ID)));
    }
}
