package com.vladiscrafter.createidlx.util.ponder;

import com.vladiscrafter.createidlx.mixin.ponder.PonderUIAccessor;
import net.createmod.catnip.gui.ScreenOpener;
import net.createmod.ponder.foundation.ui.PonderUI;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

public class PonderSceneOpener {
    public static void openByIndex(ItemStack item, int targetIndex) {
        PonderUI ui = PonderUI.of(item);
        PonderUIAccessor accessor = (PonderUIAccessor) ui;
        if (accessor.createidlx$getScenes().isEmpty()) return;

        int index = Mth.clamp(targetIndex, 0, accessor.createidlx$getScenes().size() - 1);

        accessor.createidlx$setIndex(index);
        accessor.createidlx$getLazyIndex().setValue(index);

        ScreenOpener.transitionTo(ui);
    }
}
