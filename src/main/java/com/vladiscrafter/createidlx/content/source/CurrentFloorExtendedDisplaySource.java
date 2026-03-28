package com.vladiscrafter.createidlx.content.source;

import com.simibubi.create.content.contraptions.elevator.ElevatorContactBlockEntity;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.source.SingleLineDisplaySource;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;
import com.simibubi.create.foundation.gui.ModularGuiLineBuilder;
import com.vladiscrafter.createidlx.CreateIDLX;
import com.vladiscrafter.createidlx.util.elevator.ElevatorContactBlockEntityExt;
import com.vladiscrafter.createidlx.util.widget.ModularGuiLineBuilderExt;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

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

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initConfigurationWidgets(DisplayLinkContext context, ModularGuiLineBuilder builder,
                                         boolean isFirstLine) {
        super.initConfigurationWidgets(context, builder, isFirstLine);
        if (isFirstLine)
            return;

        builder.addSelectionScrollInput(0, 116, (ssi, l) -> {
            ssi.forOptions(CreateIDLX.translatedOptions("display_source.current_floor_extended",
                            "short_name", "long_name", "short_n_long", "long_n_short"))
                    .titled(CreateIDLX.translate("display_source.current_floor_extended.display"));
        }, "FloorDisplayMode");

        ((ModularGuiLineBuilderExt) builder).createidlx$addBinaryScrollInput(120, 17, (ssi, l) -> {
            ssi.titled(CreateIDLX.translate("display_source.current_floor_extended.show_empty_floor_description"));
        }, "ShowEmptyFloorDescription");
    }
}
