package com.vladiscrafter.createidlx.util.ponder;

import com.simibubi.create.AllBlocks;
import com.vladiscrafter.createidlx.CreateIDLX;
import com.vladiscrafter.createidlx.mixin.accessor.ponder.PonderUIAccessor;
import net.createmod.catnip.gui.ScreenOpener;
import net.createmod.ponder.foundation.ui.PonderUI;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class PonderSceneOpener {
    public static void open(String path) {
        open(AllBlocks.DISPLAY_LINK.asStack(), path);
    }

    public static void open(ItemStack item, String path) {
        open(item, ResourceLocation.fromNamespaceAndPath(CreateIDLX.ID, path));
    }

    public static void open(ItemStack item, ResourceLocation id) {
        PonderUI ui = PonderUI.of(item);
        PonderUIAccessor accessor = (PonderUIAccessor) ui;
        if (accessor.createidlx$getScenes().isEmpty()) return;

        int index = 0;
        boolean foundScene = false;

        while (!foundScene) {
            if (index >= accessor.createidlx$getScenes().size()) {
                index = 0;
                break;
            }

            if (id.toString().equals(accessor.createidlx$getScenes().get(index).getId().toString())) foundScene = true;
            else index++;
        }

        accessor.createidlx$setIndex(index);
        accessor.createidlx$getLazyIndex().setValue(index);

        ScreenOpener.transitionTo(ui);
    }
}
