package com.vladiscrafter.createidlx;

import com.simibubi.create.foundation.data.CreateRegistrate;
import net.createmod.catnip.lang.LangBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod(value = CreateIDLX.ID)
public class CreateIDLX {
    public static final String ID = "createidlx";
    public static final String NAME = "Create: Improved Display Link Experience";

    public static final Logger LOGGER = LogUtils.getLogger();
    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(CreateIDLX.ID);

    public CreateIDLX(IEventBus modEventBus, ModContainer modContainer) {
        IEventBus forgeBus = NeoForge.EVENT_BUS;
        modEventBus.addListener(this::commonSetup);
        ModLoadingContext mlContext = ModLoadingContext.get();

        CreateIDLX.REGISTRATE.registerEventListeners(modEventBus);

        
    }

    public static MutableComponent translate(String key, Object... args) {
        Object[] args1 = LangBuilder.resolveBuilders(args);
        return Component.translatable(ID + "." + key, args1);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
//        LOGGER.info("HELLO FROM COMMON SETUP");
    }
}
