package com.vladiscrafter.createidlx.content.displayLink.source;

import com.simibubi.create.content.contraptions.elevator.ElevatorContactBlockEntity;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.source.SingleLineDisplaySource;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;
import com.vladiscrafter.createidlx.CreateIDLX;
import com.vladiscrafter.createidlx.util.elevator.ElevatorContactBlockEntityExt;
import com.vladiscrafter.createidlx.util.widget.ModularGuiLineBuilderExt;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CurrentFloorExtendedDisplaySource extends SingleLineDisplaySource {
    @Override
    protected MutableComponent provideLine(DisplayLinkContext context, DisplayTargetStats stats) {
        if (!(context.getSourceBlockEntity() instanceof ElevatorContactBlockEntity ecbe))
            return EMPTY_LINE;

        ecbe = (ElevatorContactBlockEntity) context.getSourceBlockEntity();

        String shortName = ecbe.lastReportedCurrentFloor;
        String longName = ((ElevatorContactBlockEntityExt) ecbe).createidlx$getLastReportedCurrentFloorLongName();

        int floorDisplayMode = context.sourceConfig().getInt("FloorDisplayMode");
        boolean showEmptyFloorDescription = context.sourceConfig().getInt("ShowEmptyFloorDescription") == 1;

        if (longName.isEmpty() && !showEmptyFloorDescription) return (floorDisplayMode == 1) ? EMPTY_LINE : Component.literal(shortName);
        longName = !(longName.isEmpty()) ? longName : CreateIDLX.translate("display_source.current_floor_extended.empty_floor_description").getString();

        return switch (floorDisplayMode) {
            case 0 -> Component.literal(shortName);
            case 1 -> Component.literal(longName);
            case 2 -> CreateIDLX.translate("display_source.current_floor_extended.double_template", shortName, longName);
            case 3 -> CreateIDLX.translate("display_source.current_floor_extended.double_template", longName, shortName);
            default -> EMPTY_LINE;
        };
    }

    @Override
    protected boolean allowsLabeling(DisplayLinkContext context) {
        return true;
    }

    @Override
    protected String getTranslationKey() {
        return "current_floor_extended";
    }
}
