package com.vladiscrafter.createidlx;

import com.simibubi.create.foundation.data.CreateRegistrate;
import com.vladiscrafter.createidlx.config.CIDLXConfigs;
import com.vladiscrafter.createidlx.registry.CreateIDLXDisplaySources;
import net.createmod.catnip.lang.LangBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Mod(value = CreateIDLX.ID)
public class CreateIDLX {
    public static final String ID = "createidlx";
    public static final String NAME = "Create: Improved Display Link Experience";

    public static final Logger LOGGER = LogUtils.getLogger();
    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(CreateIDLX.ID);

    public CreateIDLX(IEventBus modEventBus, ModContainer modContainer) {
        IEventBus forgeBus = NeoForge.EVENT_BUS;
        modEventBus.addListener(this::commonSetup);
        ModLoadingContext modLoadingContext = ModLoadingContext.get();

        CreateIDLX.REGISTRATE.registerEventListeners(modEventBus);

        modEventBus.addListener(CreateIDLXDisplaySources::register);
        modEventBus.addListener(CreateIDLXPackets::register);

        CIDLXConfigs.register(modLoadingContext, modContainer);

        modEventBus.addListener(this::onLoadConfig);
        modEventBus.addListener(this::onReloadConfig);
    }

    public static MutableComponent translate(String key, Object... args) {
        Object[] args1 = LangBuilder.resolveBuilders(args);
        return Component.translatable(ID + "." + key, args1);
    }

    public static List<MutableComponent> translateMultiline(String key, int color, Object... args) {
        String raw = CreateIDLX.translate(key, "%s").getString();
        String[] splitStr = raw.split(Pattern.quote("\n"));
        List<MutableComponent> split = new ArrayList<>(List.of());
        String[] parts;
        for (String string : splitStr) {
            if (string.contains("%s")) {
                parts = string.split(Pattern.quote("%s"));
                split.add(Component.literal(parts[0]).withStyle(s -> s.withColor(color))
                        .append((MutableComponent) args[0]).append(Component.literal(parts[1]).withStyle(s -> s.withColor(color))));
            }
            else split.add(Component.literal(string).withStyle(s -> s.withColor(color)));
        }
        return split;
    }

    public static List<Component> translateMultilineList(List<MutableComponent>... elements) {
        List<Component> finalList = new ArrayList<>(List.of());
        for (List<MutableComponent> element : elements) finalList.addAll(element);
        return finalList;
    }

    public static List<Component> translateMultilineTooltip(String key, int elementsWithoutHeader, int headerColor, int textStyle) {
        List<Component> finalList = new ArrayList<>(List.of());
        finalList.add(CreateIDLX.translate(key + "_header").withStyle(s -> s.withColor(headerColor)));
        for (int i = 1; i < elementsWithoutHeader + 1; i++) finalList.addAll(CreateIDLX.translateMultiline(key + "_" + i, textStyle));
        return finalList;
    }

    public static List<Component> translateMultilineTooltip(String key, int elementsWithoutHeader, int textStyle) {
        List<Component> finalList = new ArrayList<>(List.of());
        for (int i = 1; i < elementsWithoutHeader + 1; i++) finalList.addAll(CreateIDLX.translateMultiline(key + "_" + i, textStyle));
        return finalList;
    }

    public static List<Component> translatedOptions(String prefix, String... keys) {
        List<Component> result = new ArrayList<>(keys.length);
        for (String key : keys)
            result.add(translate((prefix != null ? prefix + "." : "") + key));
        return result;
    }

    public static ResourceLocation asResource(String path) {
        return ResourceLocation.fromNamespaceAndPath(ID, path);
    }

    private void commonSetup(FMLCommonSetupEvent event) {}

    private void onLoadConfig(ModConfigEvent.Loading evt) {
        CIDLXConfigs.onLoad(evt.getConfig());
    }

    private void onReloadConfig(ModConfigEvent.Reloading evt) {
        CIDLXConfigs.onReload(evt.getConfig());
    }
}
