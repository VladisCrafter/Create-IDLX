package com.vladiscrafter.createidlx;

import com.vladiscrafter.createidlx.infrastructure.ponder.CIDLXPonderPlugin;
import net.createmod.catnip.config.ui.BaseConfigScreen;
import net.createmod.ponder.foundation.PonderIndex;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.ModList;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

import java.util.function.Supplier;

/**
 * Client-only initialization code.
 * This class is only loaded on the client side to avoid loading client-only classes on the server.
 */
@OnlyIn(Dist.CLIENT)
public class CreateIDLXClientInit {
    
    public static void init() {
        PonderIndex.addPlugin(new CIDLXPonderPlugin());
    }

    public static void onLoadComplete(FMLLoadCompleteEvent event) {
        var container = ModList.get()
                .getModContainerById(CreateIDLX.ID)
                .orElseThrow(() -> new IllegalStateException("Create: IDLX mod container missing on LoadComplete"));
        
        Supplier<IConfigScreenFactory> configScreen = () -> (mc, previousScreen) -> new BaseConfigScreen(previousScreen, CreateIDLX.ID);
        container.registerExtensionPoint(IConfigScreenFactory.class, configScreen);
    }
}

