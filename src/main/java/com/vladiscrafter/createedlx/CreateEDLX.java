package com.vladiscrafter.createedlx;

import com.simibubi.create.Create;
import net.createmod.catnip.lang.LangBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod(value = CreateEDLX.ID)
public class CreateEDLX {
    public static final String ID = "createedlx";

    public static final Logger LOGGER = LogUtils.getLogger();

    public CreateEDLX(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);
    }

    public static MutableComponent translate(String key, Object... args) {
        Object[] args1 = LangBuilder.resolveBuilders(args);
        return Component.translatable(ID + "." + key, args1);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
//        LOGGER.info("HELLO FROM COMMON SETUP");
    }
}
