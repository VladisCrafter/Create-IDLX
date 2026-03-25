package com.vladiscrafter.createidlx.config;

import com.vladiscrafter.createidlx.CreateIDLXClient;
import net.createmod.catnip.config.ConfigBase;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class CIDLXConfigs {

    private static final Map<ModConfig.Type, ConfigBase> CONFIGS = new EnumMap<>(ModConfig.Type.class);

    public static CIDLXClient client;
    public static CIDLXServer server;

    private static CIDLXClient client() {
        return client;
    }

    private static CIDLXServer server() {
        return server;
    }

    public static ConfigBase byType(ModConfig.Type type) { return CONFIGS.get(type); }

    private static <T extends CIDLXConfigBase> T register(Supplier<T> factory, ModConfig.Type side) {
        Pair<T, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(builder -> {
            T config = factory.get();
            config.registerAll(builder);
            return config;
        });

        T config = specPair.getLeft();
        config.specification = specPair.getRight();
        CONFIGS.put(side, config);
        return config;
    }

    public static void register(BiConsumer<ModConfig.Type, ForgeConfigSpec> biConsumer) {
        client = register(CIDLXClient::new, ModConfig.Type.CLIENT);
        server = register(CIDLXServer::new, ModConfig.Type.SERVER);

        for (Map.Entry<ModConfig.Type, ConfigBase> pair : CONFIGS.entrySet())
            biConsumer.accept(pair.getKey(), pair.getValue().specification);
    }

    public static void onLoad(ModConfig modConfig) {
        for (ConfigBase config : CONFIGS.values())
            if (config.specification == modConfig
                    .getSpec())
                config.onLoad();
    }

    public static void onReload(ModConfig modConfig) {
        for (ConfigBase config : CONFIGS.values())
            if (config.specification == modConfig
                    .getSpec())
                config.onReload();
    }

}
