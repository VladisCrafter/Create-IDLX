package com.vladiscrafter.createidlx.infrastructure.ponder.scenes;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import com.simibubi.create.foundation.utility.CreateLang;
import com.vladiscrafter.createidlx.CreateIDLX;
import com.vladiscrafter.createidlx.foundation.gui.CreateIDLXIcons;
import com.vladiscrafter.createidlx.mixin.create.displayLink.DisplayLinkScreenMixin;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.WorldSectionElement;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.regex.Matcher;

public class DisplayLinkExtendedScenes {

    public static void attachedLabel(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("attached_label", "Using Display Links' Attached Label and Placeholders");
        scene.configureBasePlate(0, 0, 7);
        scene.showBasePlate();

        Selection speedo = util.select().fromTo(4, 1, 2, 5, 1, 2);
        BlockPos linkPos = util.grid().at(3, 1, 2);
        BlockPos board = util.grid().at(5, 2, 4);
        Selection fullBoard = util.select().fromTo(5, 2, 4, 1, 1, 4);
        Selection largeCog = util.select().position(5, 0, 7);
        Selection smallCog = util.select().fromTo(6, 1, 7, 6, 1, 4);
        Selection link = util.select().position(linkPos);

        Selection cuckoo = util.select().position(4, 2, 2);
        Selection stockpile = util.select().fromTo(4, 3, 2, 5, 3, 2);
        Selection nixie = util.select().fromTo(4, 4, 2, 5, 4, 2);
        Selection smart = util.select().fromTo(4, 6, 2, 5, 6, 2);

        scene.idle(15);

        scene.world().showSection(largeCog, Direction.UP);
        scene.world().showSection(smallCog, Direction.WEST);
        scene.idle(5);
        scene.world().showSection(fullBoard, Direction.NORTH);
        scene.idle(25);

        Vec3 target = util.vector().of(4.5, 2.75, 4.25);
        scene.overlay().showControls(target, Pointing.RIGHT, 20).withItem(AllBlocks.DISPLAY_LINK.asStack()).rightClick();
        scene.idle(6);
        scene.overlay().chaseBoundingBoxOutline(PonderPalette.OUTPUT, link, new AABB(board).expandTowards(-4, -1, 0)
                .deflate(0, 0, 3 / 16f), 50);
        scene.idle(35);

        scene.world().showSection(speedo, Direction.DOWN);
        scene.idle(10);
        scene.world().showSection(link, Direction.EAST);
        scene.idle(20);

        scene.world().dyeDisplayBoard(board, 1, DyeColor.LIGHT_GRAY);
        scene.world().setDisplayBoardText(board, 1,
                Component.literal(160 + " ").append(CreateLang.translateDirect("generic.unit.rpm")));
        scene.world().flashDisplayLink(linkPos);
        scene.idle(10);

        scene.overlay().showText(120)
                .text("By default, the information string provided to the Display Link gets passed to the target display as is")
                .pointAt(target.add(-1, -0.5, 0))
                .attachKeyFrame()
                .placeNearTarget();
        scene.idle(130);

        scene.overlay().showText(100)
                .text("Open the Interface to set an Attached Label text in its respective field")
                .pointAt(util.vector().centerOf(linkPos))
                .attachKeyFrame()
                .placeNearTarget();
        scene.idle(110);

        scene.overlay().showControls(util.vector().centerOf(linkPos), Pointing.LEFT, 10).rightClick();
        scene.idle(20);

        scene.addLazyKeyframe();

        scene.overlay().showText(20)
                .text("NETWORK SPEED:")
                .pointAt(util.vector().centerOf(linkPos))
                .colored(PonderPalette.INPUT)
                .placeNearTarget();
        scene.idle(30);
        scene.effects().indicateSuccess(linkPos);
        scene.world().setDisplayBoardText(board, 1, CreateIDLX.translate("ponder.attached_label.text_3")
                .append(Component.literal(" " + 160 + " ").append(CreateLang.translateDirect("generic.unit.rpm"))));
        scene.world().flashDisplayLink(linkPos);
        scene.idle(60);

        scene.overlay().showText(120)
                .text("The inserted text will be displayed before the information string and separated from it with a space")
                .pointAt(target.add(-3.5, -0.55, 0))
                .attachKeyFrame()
                .placeNearTarget();
        scene.idle(130);

        scene.world().hideSection(speedo, Direction.SOUTH);
        scene.world().setDisplayBoardText(board, 1, CommonComponents.EMPTY);
        scene.idle(10);

        ElementLink<WorldSectionElement> cuckooElement = scene.world().showIndependentSection(cuckoo, Direction.SOUTH);
        scene.world().moveSection(cuckooElement, util.vector().of(0, -1, 0), 0);
        scene.idle(10);
        scene.world().dyeDisplayBoard(board, 1, DyeColor.YELLOW);
        scene.world().setDisplayBoardText(board, 1, Component.literal("11:55"));
        scene.world().flashDisplayLink(linkPos);
        scene.idle(10);

        scene.overlay().showText(120)
                .text("Use placeholders ('$' or '{}') to mark places, where the information string should be embedded, inside the label text")
                .pointAt(util.vector().centerOf(linkPos))
                .attachKeyFrame()
                .placeNearTarget();
        scene.idle(130);

        scene.addLazyKeyframe();

        scene.overlay().showText(20)
                .text("IT IS $, MY DUDES!")
                .pointAt(util.vector().centerOf(linkPos))
                .colored(PonderPalette.INPUT)
                .placeNearTarget();
        scene.idle(30);
        scene.world().setDisplayBoardText(board, 1, Component.literal(CreateIDLX.translate("ponder.attached_label.text_6").getString()
                .replaceAll("\\$", "11:55")));
        scene.world().flashDisplayLink(linkPos);
        scene.idle(60);
        scene.world().hideIndependentSection(cuckooElement, Direction.SOUTH);
        scene.world().setDisplayBoardText(board, 1, CommonComponents.EMPTY);
        scene.idle(10);

        ElementLink<WorldSectionElement> stockpileElement = scene.world().showIndependentSection(stockpile, Direction.SOUTH);
        scene.world().moveSection(stockpileElement, util.vector().of(0, -2, 0), 0);
        scene.idle(10);
        scene.world().dyeDisplayBoard(board, 1, DyeColor.CYAN);
        scene.world().setDisplayBoardText(board, 1, Component.literal("64%"));
        scene.world().flashDisplayLink(linkPos);
        scene.idle(10);

        scene.overlay().showText(120)
                .text("Utilizing multiple placeholders in one string is possible, and mixing both types doesn't affect the result")
                .pointAt(util.vector().centerOf(linkPos))
                .attachKeyFrame()
                .placeNearTarget();
        scene.idle(130);

        scene.addLazyKeyframe();

        scene.overlay().showText(20)
                .text("{} | $ | {} | $")
                .pointAt(util.vector().centerOf(linkPos))
                .colored(PonderPalette.INPUT)
                .placeNearTarget();
        scene.idle(30);
        scene.world().setDisplayBoardText(board, 1, Component.literal(CreateIDLX.translate("ponder.attached_label.text_8").getString()
                .replaceAll("\\$", "64%")
                .replaceAll("\\{}", "64%")));
        scene.world().flashDisplayLink(linkPos);
        scene.idle(60);
        scene.world().hideIndependentSection(stockpileElement, Direction.SOUTH);
        scene.world().setDisplayBoardText(board, 1, CommonComponents.EMPTY);
        scene.idle(10);

        ElementLink<WorldSectionElement> nixieElement = scene.world().showIndependentSection(nixie, Direction.SOUTH);
        scene.world().moveSection(nixieElement, util.vector().of(0, -3, 0), 0);
        scene.idle(10);
        scene.world().dyeDisplayBoard(board, 1, DyeColor.LIME);
        scene.world().setDisplayBoardText(board, 1, Component.literal("12"));
        scene.world().flashDisplayLink(linkPos);
        scene.idle(10);

        scene.overlay().showText(120)
                .text("If a literal '$' character has to be inserted (same applies to '{}'), without it turning into a placeholder, use backslash-escaping on it")
                .pointAt(util.vector().centerOf(linkPos))
                .attachKeyFrame()
                .placeNearTarget();
        scene.idle(130);

        scene.addLazyKeyframe();

        scene.overlay().showText(20)
                .text("TOTAL PRICE: \\${}/STACK")
                .pointAt(util.vector().centerOf(linkPos))
                .colored(PonderPalette.INPUT)
                .placeNearTarget();
        scene.idle(30);
        scene.world().setDisplayBoardText(board, 1, Component.literal(CreateIDLX.translate("ponder.attached_label.text_10").getString()
                .replaceAll("\\\\\\$", Matcher.quoteReplacement("$"))
                .replaceAll("\\{}", "12")));
        scene.world().flashDisplayLink(linkPos);
        scene.idle(60);
        scene.world().hideIndependentSection(nixieElement, Direction.SOUTH);
        scene.world().setDisplayBoardText(board, 1, CommonComponents.EMPTY);
        scene.idle(10);

        ElementLink<WorldSectionElement> smartElement = scene.world().showIndependentSection(smart, Direction.SOUTH);
        scene.world().moveSection(smartElement, util.vector().of(0, -5, 0), 0);
        scene.idle(10);
        scene.world().dyeDisplayBoard(board, 1, DyeColor.ORANGE);
        scene.world().setDisplayBoardText(board, 1, Component.literal(2750 + " ")
                .append(CreateLang.translateDirect("generic.unit.millibuckets")));
        scene.world().flashDisplayLink(linkPos);
        scene.idle(10);

        scene.overlay().showText(120)
                .text("As long as there are no unescaped placeholders present in the label altogether, default concatenation by a space will be applied")
                .pointAt(util.vector().centerOf(linkPos))
                .attachKeyFrame()
                .placeNearTarget();
        scene.idle(130);

        scene.addLazyKeyframe();

        scene.overlay().showText(20)
                .text("\\$ \\{} \\$ \\{} \\$ \\{}")
                .pointAt(util.vector().centerOf(linkPos))
                .colored(PonderPalette.INPUT)
                .placeNearTarget();
        scene.idle(30);
        scene.world().setDisplayBoardText(board, 1, Component.literal("$ {} $ {} $ {} " + 2750 + " ")
                .append(CreateLang.translateDirect("generic.unit.millibuckets")));
        scene.world().flashDisplayLink(linkPos);
        scene.idle(60);

        scene.addLazyKeyframe();

        scene.overlay().showControls(util.vector().centerOf(linkPos), Pointing.LEFT, 100).showing(CreateIDLXIcons.placeholdersIcon);
        scene.idle(10);
        scene.overlay().showText(100)
                .text("Hover over the Placeholders Usage Guide button in the Interface to see a tooltip briefly explaining placeholders")
                .colored(PonderPalette.BLUE)
                .placeNearTarget();
        scene.idle(110);

        scene.markAsFinished();
    }

    public static void clipboardCopying(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("clipboard_copying", "Duplicating Display Link Properties using a Clipboard");
        scene.configureBasePlate(0, 0, 7);
        scene.showBasePlate();

        Selection shaft1 = util.select().fromTo(6, 1, 2, 0, 1, 2);
        Selection shaft2 = util.select().fromTo(6, 1, 1, 0, 1, 1);
        Selection shaft3 = util.select().fromTo(6, 1, 0, 0, 1, 0);
        Selection outputs = util.select().fromTo(0, 0, 2, 0, 0, 0);
        BlockPos board = util.grid().at(5, 3, 4);
        Selection fullBoard = util.select().fromTo(5, 3, 4, 1, 1, 4);
        Selection largeCog = util.select().position(5, 0, 7);
        Selection smallCog = util.select().fromTo(6, 1, 7, 6, 1, 4);
        BlockPos link1Pos = util.grid().at(4, 2, 2);
        BlockPos link2Pos = util.grid().at(3, 2, 1);
        BlockPos link3Pos = util.grid().at(2, 2, 0);
        Selection link1 = util.select().position(link1Pos);
        Selection link2 = util.select().position(link2Pos);
        Selection link3 = util.select().position(link3Pos);

        scene.world().setKineticSpeed(outputs, 0f);

        scene.world().showSection(largeCog, Direction.UP);
        scene.world().showSection(smallCog, Direction.WEST);
        scene.idle(5);
        scene.world().showSection(fullBoard, Direction.NORTH);
        scene.idle(25);

        scene.world().showSection(shaft1, Direction.DOWN);
        scene.idle(5);
        scene.world().showSection(shaft2, Direction.DOWN);
        scene.idle(5);
        scene.world().showSection(shaft3, Direction.DOWN);
        scene.idle(10);

        Vec3 target = util.vector().of(4.5, 2.75, 4.25);

        scene.overlay().showControls(target, Pointing.RIGHT, 20).withItem(AllBlocks.DISPLAY_LINK.asStack()).rightClick();
        scene.idle(6);
        scene.overlay().chaseBoundingBoxOutline(PonderPalette.OUTPUT, link1, new AABB(board).expandTowards(-4, -1, 0)
                .deflate(0, 0, 3 / 16f), 50);
        scene.idle(35);
        scene.world().showSection(link1, Direction.DOWN);
        scene.idle(20);
        scene.overlay().showControls(util.vector().centerOf(link1Pos), Pointing.DOWN, 10).rightClick();
        scene.idle(20);
        scene.overlay().showText(20)
                .sharedText("consumption_label")
                .pointAt(util.vector().centerOf(link1Pos))
                .colored(PonderPalette.INPUT)
                .placeNearTarget();
        scene.idle(30);
        scene.overlay().showControls(util.vector().centerOf(link1Pos), Pointing.DOWN, 25).scroll();
        scene.overlay().showText(25)
                .sharedText("kinetic_stress_option")
                .pointAt(util.vector().centerOf(link1Pos))
                .colored(PonderPalette.WHITE)
                .placeNearTarget();

        scene.idle(30);
        scene.effects().indicateSuccess(link1Pos);
        scene.world().setDisplayBoardText(board, 1, CreateIDLX.translate("ponder.shared.consumption_label")
                .append(Component.literal( " 256 ").append(CreateLang.translateDirect("generic.unit.stress"))));
        scene.world().flashDisplayLink(link1Pos);
        scene.idle(20);

        scene.overlay().showControls(target, Pointing.RIGHT, 20).withItem(AllBlocks.DISPLAY_LINK.asStack()).rightClick();
        scene.idle(6);
        scene.overlay().chaseBoundingBoxOutline(PonderPalette.OUTPUT, link2, new AABB(board).expandTowards(-4, -1, 0)
                .deflate(0, 0, 3 / 16f), 50);
        scene.idle(35);
        scene.world().showSection(link2, Direction.DOWN);
        scene.idle(20);
        scene.overlay().showControls(util.vector().centerOf(link2Pos), Pointing.DOWN, 10).rightClick();
        scene.idle(20);
        scene.overlay().showText(20)
                .sharedText("consumption_label")
                .pointAt(util.vector().centerOf(link2Pos))
                .colored(PonderPalette.INPUT)
                .placeNearTarget();
        scene.idle(30);
        scene.overlay().showControls(util.vector().centerOf(link2Pos), Pointing.DOWN, 25).scroll();
        scene.overlay().showText(25)
                .sharedText("kinetic_stress_option")
                .pointAt(util.vector().centerOf(link2Pos))
                .colored(PonderPalette.WHITE)
                .placeNearTarget();

        scene.idle(30);
        scene.effects().indicateSuccess(link2Pos);
        scene.world().setDisplayBoardText(board, 2, CreateIDLX.translate("ponder.shared.consumption_label")
                .append(Component.literal( " 1 024 ").append(CreateLang.translateDirect("generic.unit.stress"))));
        scene.world().flashDisplayLink(link2Pos);
        scene.idle(20);

        scene.overlay().showControls(target, Pointing.RIGHT, 20).withItem(AllBlocks.DISPLAY_LINK.asStack())
                .rightClick();
        scene.idle(6);
        scene.overlay().chaseBoundingBoxOutline(PonderPalette.OUTPUT, link3, new AABB(board).expandTowards(-4, -1, 0)
                .deflate(0, 0, 3 / 16f), 50);
        scene.idle(35);
        scene.world().showSection(link3, Direction.DOWN);
        scene.idle(20);
        scene.overlay().showControls(util.vector().centerOf(link3Pos), Pointing.DOWN, 10).rightClick();
        scene.idle(20);
        scene.overlay().showText(20)
                .sharedText("consumption_label")
                .pointAt(util.vector().centerOf(link3Pos))
                .colored(PonderPalette.INPUT)
                .placeNearTarget();
        scene.idle(30);
        scene.overlay().showControls(util.vector().centerOf(link3Pos), Pointing.DOWN, 25).scroll();
        scene.overlay().showText(25)
                .sharedText("kinetic_stress_option")
                .pointAt(util.vector().centerOf(link3Pos))
                .colored(PonderPalette.WHITE)
                .placeNearTarget();

        scene.idle(30);
        scene.effects().indicateSuccess(link3Pos);
        scene.world().setDisplayBoardText(board, 3, CreateIDLX.translate("ponder.shared.consumption_label")
                .append(Component.literal( " 4 096 ").append(CreateLang.translateDirect("generic.unit.stress"))));
        scene.world().flashDisplayLink(link3Pos);
        scene.idle(20);

        scene.addLazyKeyframe();

        scene.overlay().showText(80)
                .text("Setting up multiple Display Links with identical settings can be time-consuming")
                .colored(PonderPalette.RED)
                .pointAt(target.add(-3.5, 0.25, 0))
                .placeNearTarget();
        scene.idle(90);

        scene.overlay().showControls(util.vector().centerOf(link1Pos), Pointing.DOWN, 10).rightClick();
        scene.idle(20);
        scene.overlay().showText(20)
                .text("$ REMAINING")
                .pointAt(util.vector().centerOf(link1Pos))
                .colored(PonderPalette.INPUT)
                .placeNearTarget();
        scene.idle(30);
        scene.overlay().showControls(util.vector().centerOf(link1Pos), Pointing.DOWN, 25).scroll();
        scene.overlay().showText(25)
                .text("-> Remaining SU")
                .pointAt(util.vector().centerOf(link1Pos))
                .placeNearTarget();
        scene.idle(30);

        scene.effects().indicateSuccess(link1Pos);
        scene.world().setDisplayBoardText(board, 1, Component.literal(CreateIDLX.translate("ponder.clipboard_copying.text_2").getString()
                .replaceAll("\\$", Component.literal("16,128 ").append(CreateLang.translateDirect("generic.unit.stress")).getString())));
        scene.world().flashDisplayLink(link1Pos);
        scene.idle(20);

        scene.overlay().showText(80)
                .text("A Clipboard can be used to speed up the process of setting identical values")
                .colored(PonderPalette.OUTPUT)
                .independent();
        scene.idle(90);

        scene.addLazyKeyframe();

        scene.overlay().showText(60)
                .text("Right-click the Display Link with the Clipboard to open the Copying Interface...")
                .pointAt(util.vector().centerOf(link1Pos))
                .placeNearTarget();
        scene.idle(70);

        scene.overlay().showControls(util.vector().centerOf(link1Pos), Pointing.DOWN, 40)
                .withItem(AllBlocks.CLIPBOARD.asStack()).rightClick();
        scene.idle(50);

        scene.overlay().showText(40)
                .text("...and choose which settings to save")
                .pointAt(util.vector().centerOf(link1Pos))
                .placeNearTarget();
        scene.idle(50);

        scene.addLazyKeyframe();

        scene.overlay().showText(60)
                .text("The available options will be selected by default...")
                .pointAt(util.vector().centerOf(link1Pos))
                .placeNearTarget();
        scene.idle(70);

        scene.overlay().showText(60)
                .text("Copy the Attached Label")
                .pointAt(util.vector().centerOf(link1Pos))
                .colored(PonderPalette.MEDIUM)
                .placeNearTarget();
        scene.overlay().showControls(util.vector().centerOf(link1Pos), Pointing.DOWN, 60).showing(CreateIDLXIcons.labelIcon);
        scene.idle(70);

        scene.overlay().showText(60)
                .text("Copy Type-Specific Parameters")
                .pointAt(util.vector().centerOf(link1Pos))
                .colored(PonderPalette.MEDIUM)
                .placeNearTarget();
        scene.overlay().showControls(util.vector().centerOf(link1Pos), Pointing.DOWN, 60).showing(CreateIDLXIcons.configIcon);
        scene.idle(70);

        scene.overlay().showText(60)
                .text("Copy the Target Display Position")
                .pointAt(util.vector().centerOf(link1Pos))
                .colored(PonderPalette.MEDIUM)
                .placeNearTarget();
        scene.overlay().showControls(util.vector().centerOf(link1Pos), Pointing.DOWN, 60).showing(CreateIDLXIcons.targetIcon);
        scene.idle(70);

        scene.addLazyKeyframe();

        scene.overlay().showText(60)
                .text("...but you can deselect any of them, and respective settings won't be applied")
                .pointAt(util.vector().centerOf(link1Pos))
                .placeNearTarget();
        scene.idle(70);

        scene.overlay().showText(60)
                .text("Copy the Target Display Position")
                .pointAt(util.vector().centerOf(link1Pos))
                .colored(PonderPalette.BLACK)
                .placeNearTarget();
        scene.overlay().showControls(util.vector().centerOf(link1Pos), Pointing.DOWN, 60).showing(CreateIDLXIcons.targetIcon);
        scene.idle(70);

        scene.addLazyKeyframe();

        scene.overlay().showText(80)
                .text("Then left-click the other Display Link with the Clipboard to open the Applying Interface")
                .pointAt(util.vector().centerOf(link2Pos))
                .placeNearTarget();
        scene.idle(90);

        scene.overlay().showControls(util.vector().centerOf(link2Pos), Pointing.DOWN, 40)
                .withItem(AllBlocks.CLIPBOARD.asStack()).leftClick();
        scene.idle(50);

        scene.addLazyKeyframe();

        scene.overlay().showText(60)
                .text("The options saved before will also be selected by default...")
                .pointAt(util.vector().centerOf(link2Pos))
                .placeNearTarget();
        scene.idle(70);

        scene.overlay().showText(60)
                .text("Apply the Attached Label")
                .pointAt(util.vector().centerOf(link2Pos))
                .colored(PonderPalette.MEDIUM)
                .placeNearTarget();
        scene.overlay().showControls(util.vector().centerOf(link2Pos), Pointing.DOWN, 60).showing(CreateIDLXIcons.labelIcon);
        scene.idle(70);

        scene.overlay().showText(60)
                .text("Apply Type-Specific Parameters")
                .pointAt(util.vector().centerOf(link2Pos))
                .colored(PonderPalette.MEDIUM)
                .placeNearTarget();
        scene.overlay().showControls(util.vector().centerOf(link2Pos), Pointing.DOWN, 60).showing(CreateIDLXIcons.configIcon);
        scene.idle(70);

        scene.addLazyKeyframe();

        scene.overlay().showText(60)
                .text("...but you can deselect any of them here as well, and respective settings won't be applied")
                .pointAt(util.vector().centerOf(link2Pos))
                .placeNearTarget();
        scene.idle(70);

        scene.overlay().showText(60)
                .text("Apply the Attached Label")
                .pointAt(util.vector().centerOf(link2Pos))
                .colored(PonderPalette.BLACK)
                .placeNearTarget();
        scene.overlay().showControls(util.vector().centerOf(link2Pos), Pointing.DOWN, 60).showing(CreateIDLXIcons.targetIcon);
        scene.idle(70);

        scene.effects().indicateSuccess(link2Pos);
        scene.world().setDisplayBoardText(board, 2, Component.literal("15,360 ").append(CreateLang.translateDirect("generic.unit.stress")));
        scene.world().flashDisplayLink(link2Pos);
        scene.idle(20);

        scene.overlay().showControls(util.vector().centerOf(link3Pos), Pointing.DOWN, 40)
                .withItem(AllBlocks.CLIPBOARD.asStack()).leftClick();
        scene.idle(90);

        scene.effects().indicateSuccess(link3Pos);
        scene.world().setDisplayBoardText(board, 3, Component.literal(CreateIDLX.translate("ponder.clipboard_copying.text_2").getString()
                .replaceAll("\\$", Component.literal("12,288 ").append(CreateLang.translateDirect("generic.unit.stress")).getString())));
        scene.world().flashDisplayLink(link3Pos);
        scene.idle(20);

        scene.markAsFinished();
    }
}
