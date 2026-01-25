package com.vladiscrafter.createidlx;

import net.createmod.catnip.config.ui.BaseConfigScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;

import java.util.function.Supplier;

@Mod(value = CreateIDLX.ID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = CreateIDLX.ID, value = Dist.CLIENT)
public class CreateIDLXClient {
    public CreateIDLXClient(IEventBus modEventBus, ModContainer container) {
        IEventBus forgeBus = NeoForge.EVENT_BUS;
        modEventBus.addListener(CreateIDLXClient::onLoadComplete);
    }

    @SubscribeEvent static void onClientSetup(FMLClientSetupEvent event) {}

    public static void onLoadComplete(FMLLoadCompleteEvent event) {
        ModContainer container = ModList.get()
                .getModContainerById(CreateIDLX.ID)
                .orElseThrow(() -> new IllegalStateException("Create: IDLX mod container missing on LoadComplete"));
        Supplier<IConfigScreenFactory> configScreen = () -> (mc, previousScreen) -> new BaseConfigScreen(previousScreen, CreateIDLX.ID);
        container.registerExtensionPoint(IConfigScreenFactory.class, configScreen);
    }
}
