package com.vladiscrafter.createidlx;

import com.simibubi.create.foundation.data.CreateRegistrate;
import com.vladiscrafter.createidlx.config.CIDLXConfigs;
import com.vladiscrafter.createidlx.registry.CreateIDLXDisplaySources;
import net.createmod.catnip.lang.LangBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.ArrayList;
import java.util.List;

@Mod(value = CreateIDLX.ID)
public class CreateIDLX {
    public static final String ID = "createidlx";
    public static final String NAME = "Create: Improved Display Link Experience";

    public static final Logger LOGGER = LogUtils.getLogger();
    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(CreateIDLX.ID);

    public CreateIDLX() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::commonSetup);
        ModLoadingContext modLoadingContext = ModLoadingContext.get();

        CreateIDLX.REGISTRATE.registerEventListeners(modEventBus);

        modEventBus.addListener(CreateIDLXDisplaySources::register);

        CIDLXConfigs.register(modLoadingContext::registerConfig);

        modEventBus.addListener(this::onLoadConfig);
        modEventBus.addListener(this::onReloadConfig);
    }

    public static MutableComponent translate(String key, Object... args) {
        Object[] args1 = LangBuilder.resolveBuilders(args);
        return Component.translatable(ID + "." + key, args1);
    }

    public static List<Component> translatedOptions(String prefix, String... keys) {
        List<Component> result = new ArrayList<>(keys.length);
        for (String key : keys)
            result.add(translate((prefix != null ? prefix + "." : "") + key));
        return result;
    }

    public static ResourceLocation asResource(String path) {
        return ResourceLocation.tryBuild(ID, path);
    }

    private void commonSetup(FMLCommonSetupEvent event) {}

    private void onLoadConfig(ModConfigEvent.Loading evt) {
        CIDLXConfigs.onLoad(evt.getConfig());
    }

    private void onReloadConfig(ModConfigEvent.Reloading evt) {
        CIDLXConfigs.onReload(evt.getConfig());
    }
}
