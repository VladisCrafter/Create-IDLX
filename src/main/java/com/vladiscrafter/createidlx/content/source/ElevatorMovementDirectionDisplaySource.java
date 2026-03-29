package com.vladiscrafter.createidlx.content.source;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.content.contraptions.elevator.ElevatorColumn;
import com.simibubi.create.content.contraptions.elevator.ElevatorContactBlockEntity;
import com.simibubi.create.content.contraptions.elevator.ElevatorPulleyBlockEntity;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.source.SingleLineDisplaySource;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;
import com.simibubi.create.foundation.gui.ModularGuiLineBuilder;
import com.simibubi.create.foundation.utility.CreateLang;
import com.vladiscrafter.createidlx.CreateIDLX;
import com.vladiscrafter.createidlx.util.widget.ModularGuiLineBuilderExt;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ElevatorMovementDirectionDisplaySource extends SingleLineDisplaySource {
    @Override
    protected MutableComponent provideLine(DisplayLinkContext context, DisplayTargetStats stats) {
        if (!(context.getSourceBlockEntity() instanceof ElevatorPulleyBlockEntity epbe))
            return EMPTY_LINE;

        epbe = (ElevatorPulleyBlockEntity) context.getSourceBlockEntity();
        float elevatorSpeed = epbe.getMovementSpeed();

        String state;
        if (Math.abs(elevatorSpeed) < 0.0001f) state = "still";
        else if (elevatorSpeed < 0) state = "up";
        else state = "down";

        int displayMode = context.sourceConfig().getInt("DisplayMode");

        return switch (displayMode) {
            case 0 -> Component.literal(state.equals("still") ? "×" : (state.equals("up") ? "↑" : "↓"));
            case 1 -> Component.literal(state.equals("still") ? "■" : (state.equals("up") ? "▲" : "▼"));
            case 2 -> CreateIDLX.translate("display_source.elevator_movement_direction." + state);
            default -> EMPTY_LINE;
        };
    }

    @Override
    public int getPassiveRefreshTicks() {
        return 20;
    }

    @Override
    protected boolean allowsLabeling(DisplayLinkContext context) {
        return true;
    }

    @Override
    protected String getTranslationKey() {
        return "elevator_movement_direction";
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initConfigurationWidgets(DisplayLinkContext context, ModularGuiLineBuilder builder,
                                         boolean isFirstLine) {
        super.initConfigurationWidgets(context, builder, isFirstLine);
        if (isFirstLine)
            return;

        builder.addSelectionScrollInput(0, 137, (ssi, l) -> {
            ssi.forOptions(CreateIDLX.translatedOptions("display_source.elevator_movement_direction",
                            "arrows", "triangles", "words"))
                    .titled(CreateIDLX.translate("display_source.elevator_movement_direction.display"));
        }, "DisplayMode");
    }
}
