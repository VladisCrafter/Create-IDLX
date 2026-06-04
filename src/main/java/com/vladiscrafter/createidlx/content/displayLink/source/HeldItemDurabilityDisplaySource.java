package com.vladiscrafter.createidlx.content.displayLink.source;

import com.simibubi.create.content.kinetics.deployer.DeployerBlockEntity;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.source.PercentOrProgressBarDisplaySource;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;
import com.simibubi.create.content.trains.display.FlapDisplaySection;
import com.simibubi.create.foundation.gui.ModularGuiLineBuilder;
import com.vladiscrafter.createidlx.CreateIDLX;
import com.vladiscrafter.createidlx.mixin.accessor.create.DeployerBlockEntityAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class HeldItemDurabilityDisplaySource extends PercentOrProgressBarDisplaySource {
    private final MutableComponent INDESTRUCTIBLE = CreateIDLX.translate("display_source.held_item_durability.indestructible_template");
    private final MutableComponent INVALID_DAMAGE = CreateIDLX.translate("display_source.held_item_durability.invalid_damage_template");

    @Override
    protected MutableComponent provideLine(DisplayLinkContext context, DisplayTargetStats stats) {
        Float rawProgress = this.getProgress(context);
        int mode = getMode(context);
        
        if (mode == 0 || mode == 1) {
            if (rawProgress == null) return EMPTY_LINE;
        }

        if (progressBarActive(context)) return super.provideLine(context, stats);
        if (!progressBarActive(context) && (mode == 1)) return formatNumeric(context, rawProgress);

        if (!(context.getSourceBlockEntity() instanceof DeployerBlockEntity dpe)) {
            return EMPTY_LINE;
        }

        ItemStack heldItem = ((DeployerBlockEntityAccessor) dpe).createidlx$getPlayer().getMainHandItem();
        if (heldItem.isEmpty()) return EMPTY_LINE;

        int rawMax = heldItem.getMaxDamage();
        int rawDamage = heldItem.getDamageValue();
        int rawCurrent = rawMax - rawDamage;

        MutableComponent max = rawMax != 0 ? Component.literal("" + rawMax) : INDESTRUCTIBLE;
        MutableComponent damage = rawMax != 0 ? Component.literal("" + rawDamage) : INVALID_DAMAGE;
        MutableComponent current = rawMax != 0 ? Component.literal("" + rawCurrent) : INDESTRUCTIBLE;

        return switch (mode) {
            case 2 -> damage;
            case 3 -> current;
            case 4 -> max;
            case 5 -> CreateIDLX.translate("display_source.held_item_durability.ratio_template", damage, max);
            case 6 -> CreateIDLX.translate("display_source.held_item_durability.ratio_template", current, max);
            default -> EMPTY_LINE;
        };
    }

    @Override
    protected MutableComponent formatNumeric(DisplayLinkContext context, Float currentLevel) {
        return Component.literal(Mth.clamp(Math.round(currentLevel * 100), 0, 100) + "%");
    }

    private int getMode(DisplayLinkContext context) {
        return context.sourceConfig()
                .getInt("Mode");
    }

    @Override
    protected Float getProgress(DisplayLinkContext context) {
        if (!(context.getSourceBlockEntity() instanceof DeployerBlockEntity dpe))
            return null;

        ItemStack heldItem = ((DeployerBlockEntityAccessor) dpe).createidlx$getPlayer().getMainHandItem();
        float total = heldItem.getMaxDamage();
        float damage = heldItem.getDamageValue();
        float current = total - damage;

        if (total == 0f) return 1f;

        return current / total;
    }

    @Override
    protected boolean progressBarActive(DisplayLinkContext context) {
        return getMode(context) == 0;
    }

    @Override
    protected boolean allowsLabeling(DisplayLinkContext context) {
        return true;
    }

    @Override
    protected String getFlapDisplayLayoutName(DisplayLinkContext context) {
        return !progressBarActive(context) ? "Number" : super.getFlapDisplayLayoutName(context);
    }

    @Override
    protected FlapDisplaySection createSectionForValue(DisplayLinkContext context, int size) {
        return !progressBarActive(context)
                ? new FlapDisplaySection(size * FlapDisplaySection.MONOSPACE, "numeric", false, false)
                : super.createSectionForValue(context, size);
    }

    @Override
    protected String getTranslationKey() {
        return "held_item_durability";
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initConfigurationWidgets(DisplayLinkContext context, ModularGuiLineBuilder builder,
                                         boolean isFirstLine) {
        super.initConfigurationWidgets(context, builder, isFirstLine);
        if (isFirstLine) return;

        builder.addSelectionScrollInput(0, 137, (ssi, l) -> {
            ssi.forOptions(CreateIDLX.translatedOptions("display_source.held_item_durability",
                            "progress_bar", "percent", "damage", "current_durability", "max_durability", "damage_n_total", "current_n_total"))
                    .titled(CreateIDLX.translate("display_source.held_item_durability.display"));
        }, "Mode");
    }
}
