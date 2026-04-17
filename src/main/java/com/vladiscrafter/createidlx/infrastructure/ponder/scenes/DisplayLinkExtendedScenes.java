package com.vladiscrafter.createidlx.infrastructure.ponder.scenes;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllDataComponents;
import com.simibubi.create.content.equipment.clipboard.ClipboardContent;
import com.simibubi.create.content.equipment.clipboard.ClipboardOverrides;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import com.simibubi.create.foundation.utility.CreateLang;
import com.vladiscrafter.createidlx.CreateIDLX;
import com.vladiscrafter.createidlx.foundation.gui.CreateIDLXIcons;
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
import net.minecraft.world.item.ItemStack;
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

        Selection cuckoo = util.select().fromTo(4, 2, 2, 5, 2, 2);
        Selection stockpile = util.select().fromTo(4, 3, 2, 5, 3, 2);
        Selection nixie = util.select().fromTo(4, 5, 2, 5, 5, 2);
        Selection smartie = util.select().fromTo(4, 7, 2, 5, 7, 2);

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

        ElementLink<WorldSectionElement> speedoElement = scene.world().showIndependentSection(speedo, Direction.DOWN);
        scene.idle(10);
        scene.world().showSection(link, Direction.EAST);
        scene.idle(20);

        scene.world().dyeDisplayBoard(board, 1, DyeColor.LIGHT_GRAY);
        scene.world().setDisplayBoardText(board, 1,
                Component.literal(160 + " ").append(CreateLang.translateDirect("generic.unit.rpm")));
        scene.world().flashDisplayLink(linkPos);
        scene.idle(30);

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

        scene.world().hideIndependentSection(speedoElement, Direction.SOUTH);
        scene.world().setDisplayBoardText(board, 1, CommonComponents.EMPTY);
        scene.idle(10);

        ElementLink<WorldSectionElement> cuckooElement = scene.world().showIndependentSection(cuckoo, Direction.SOUTH);
        scene.world().moveSection(cuckooElement, util.vector().of(0, -1, 0), 0);
        scene.idle(10);
        scene.world().dyeDisplayBoard(board, 1, DyeColor.YELLOW);
        scene.world().setDisplayBoardText(board, 1, Component.literal("11:55"));
        scene.world().flashDisplayLink(linkPos);
        scene.idle(30);

        scene.overlay().showText(120)
                .text("Use placeholders ('$' or '{}') to mark places, where the information string should be embedded, inside the label text")
                .pointAt(util.vector().centerOf(linkPos))
                .attachKeyFrame()
                .placeNearTarget();
        scene.idle(130);

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
        scene.idle(30);

        scene.overlay().showText(120)
                .text("Utilizing multiple placeholders in one string is possible, and mixing both types doesn't affect the result")
                .pointAt(util.vector().centerOf(linkPos))
                .attachKeyFrame()
                .placeNearTarget();
        scene.idle(130);

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
        scene.world().moveSection(nixieElement, util.vector().of(0, -4, 0), 0);
        scene.idle(10);
        scene.world().dyeDisplayBoard(board, 1, DyeColor.LIME);
        scene.world().setDisplayBoardText(board, 1, Component.literal("12"));
        scene.world().flashDisplayLink(linkPos);
        scene.idle(30);

        scene.overlay().showText(120)
                .text("If a literal '$' character has to be inserted (same applies to '{}'), without it turning into a placeholder, use backslash-escaping on it")
                .pointAt(util.vector().centerOf(linkPos))
                .attachKeyFrame()
                .placeNearTarget();
        scene.idle(130);

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

        ElementLink<WorldSectionElement> smartieElement = scene.world().showIndependentSection(smartie, Direction.SOUTH);
        scene.world().moveSection(smartieElement, util.vector().of(0, -6, 0), 0);
        scene.idle(10);
        scene.world().dyeDisplayBoard(board, 1, DyeColor.ORANGE);
        scene.world().setDisplayBoardText(board, 1, Component.literal(2750 + " ")
                .append(CreateLang.translateDirect("generic.unit.millibuckets")));
        scene.world().flashDisplayLink(linkPos);
        scene.idle(30);

        scene.overlay().showText(120)
                .text("As long as there are no unescaped placeholders present in the label altogether, default concatenation by a space will be applied")
                .pointAt(util.vector().centerOf(linkPos))
                .attachKeyFrame()
                .placeNearTarget();
        scene.idle(130);

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

        scene.overlay().showControls(util.vector().centerOf(linkPos), Pointing.LEFT, 100).showing(CreateIDLXIcons.placeholdersIcon);
        scene.idle(10);
        scene.overlay().showText(100)
                .text("Hover over the Placeholders Usage Guide button in the Interface to see a tooltip briefly retelling all the above")
                .colored(PonderPalette.BLUE)
                .attachKeyFrame()
                .independent()
                .placeNearTarget();
        scene.idle(110);

        scene.markAsFinished();
    }

    public static void clipboardCopying(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("clipboard_copying", "Duplicating Display Link Properties using a Clipboard");
        scene.configureBasePlate(0, 0, 7);
        scene.showBasePlate();

        ItemStack filledClipboard = AllBlocks.CLIPBOARD.asStack();
        filledClipboard.set(AllDataComponents.CLIPBOARD_CONTENT, ClipboardContent.EMPTY.setType(ClipboardOverrides.ClipboardType.WRITTEN));

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
        scene.idle(15);

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

        scene.overlay().showText(80)
                .text("Setting up multiple Display Links with identical settings can be time-consuming")
                .colored(PonderPalette.RED)
                .pointAt(target.add(-3.5, 0.25, 0))
                .attachKeyFrame()
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
                .attachKeyFrame()
                .independent();
        scene.idle(90);

        scene.overlay().showText(60)
                .text("Right-click the Display Link with the Clipboard to open the Copying Interface...")
                .pointAt(util.vector().centerOf(link1Pos))
                .attachKeyFrame()
                .placeNearTarget();
        scene.idle(70);

        scene.overlay().showControls(util.vector().centerOf(link1Pos), Pointing.DOWN, 40).withItem(AllBlocks.CLIPBOARD.asStack()).rightClick();
        scene.idle(50);

        scene.overlay().showText(40)
                .text("...and choose which settings to save")
                .pointAt(util.vector().centerOf(link1Pos))
                .placeNearTarget();
        scene.idle(50);

        scene.overlay().showText(60)
                .text("The available options will be selected by default...")
                .pointAt(util.vector().centerOf(link1Pos))
                .attachKeyFrame()
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

        scene.overlay().showText(60)
                .text("...but you can deselect any of them, and respective settings won't be applied")
                .pointAt(util.vector().centerOf(link1Pos))
                .attachKeyFrame()
                .placeNearTarget();
        scene.idle(70);

        scene.overlay().showText(60)
                .text("Copy the Target Display Position")
                .pointAt(util.vector().centerOf(link1Pos))
                .colored(PonderPalette.BLACK)
                .placeNearTarget();
        scene.overlay().showControls(util.vector().centerOf(link1Pos), Pointing.DOWN, 60).showing(CreateIDLXIcons.targetIcon);
        scene.idle(70);

        scene.overlay().showText(80)
                .text("Then left-click the other Display Link with the Clipboard to open the Applying Interface")
                .pointAt(util.vector().centerOf(link2Pos))
                .attachKeyFrame()
                .placeNearTarget();
        scene.idle(90);

        scene.overlay().showControls(util.vector().centerOf(link2Pos), Pointing.DOWN, 40).withItem(filledClipboard).leftClick();
        scene.idle(50);

        scene.overlay().showText(60)
                .text("The options saved before will also be selected by default...")
                .pointAt(util.vector().centerOf(link2Pos))
                .attachKeyFrame()
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

        scene.overlay().showText(60)
                .text("...but you can deselect any of them here as well, and respective settings won't be applied")
                .pointAt(util.vector().centerOf(link2Pos))
                .attachKeyFrame()
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

        scene.overlay().showControls(util.vector().centerOf(link3Pos), Pointing.DOWN, 40).withItem(filledClipboard).leftClick();
        scene.idle(90);

        scene.effects().indicateSuccess(link3Pos);
        scene.world().setDisplayBoardText(board, 3, Component.literal(CreateIDLX.translate("ponder.clipboard_copying.text_2").getString()
                .replaceAll("\\$", Component.literal("12,288 ").append(CreateLang.translateDirect("generic.unit.stress")).getString())));
        scene.world().flashDisplayLink(link3Pos);
        scene.idle(60);

        scene.overlay().showControls(util.vector().centerOf(link3Pos), Pointing.DOWN, 100).showing(CreateIDLXIcons.clipboardIcon);
        scene.idle(10);
        scene.overlay().showText(100)
                .text("Hover over the Duplicating Display Link Properties button in the Interface to see a tooltip briefly retelling all the above")
                .colored(PonderPalette.BLUE)
                .attachKeyFrame()
                .placeNearTarget();
        scene.idle(110);

        scene.markAsFinished();
    }

    public static void clipboardCopiableProperties(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("clipboard_copiable_properties", "Types of Copiable Display Link Properties");
        scene.scaleSceneView(1f);
        scene.configureBasePlate(0, 0, 7);
        scene.showBasePlate();
        //
        // BOILER STATUS:  IDLE
        ItemStack filledClipboard = AllBlocks.CLIPBOARD.asStack();
        filledClipboard.set(AllDataComponents.CLIPBOARD_CONTENT, ClipboardContent.EMPTY.setType(ClipboardOverrides.ClipboardType.WRITTEN));

        Selection shaft = util.select().fromTo(6, 1, 1, 0, 1, 1);
        Selection input = util.select().position(6, 2, 1);
        Selection output = util.select().position(0, 2, 1);
        BlockPos board = util.grid().at(5, 3, 4);
        Selection fullBoard = util.select().fromTo(5, 3, 4, 1, 1, 4);
        Selection largeCog = util.select().position(5, 0, 7);
        Selection smallCog = util.select().fromTo(6, 1, 7, 6, 1, 4);
        BlockPos link1Pos = util.grid().at(3, 2, 1);
        Selection link1 = util.select().position(link1Pos);
        Selection shaftReplacement = util.select().fromTo(6, 1, 0, 0, 1, 0);
        BlockPos inputReplacementPos = util.grid().at(6, 4, 1);
        BlockPos outputReplacementPos = util.grid().at(0, 4, 1);
        Selection inputReplacement = util.select().position(inputReplacementPos);
        Selection outputReplacement = util.select().position(outputReplacementPos);
        BlockPos link2Pos = util.grid().at(4, 2, 1);
        BlockPos link3Pos = util.grid().at(2, 2, 1);
        Selection link2 = util.select().position(link2Pos);
        Selection link3 = util.select().position(link3Pos);
        Selection smartie = util.select().fromTo(3, 3, 1, 3, 3, 2);
        BlockPos link4InitialPos = util.grid().at(3, 4, 2);
        BlockPos link4Pos = util.grid().at(3, 2, 2);
        Selection link4 = util.select().position(link4InitialPos);
        Selection cuckoo1 = util.select().fromTo(5, 1, 2, 4, 1, 2);
        Selection cuckoo2 = util.select().fromTo(2, 1, 2, 1, 1, 2);
        BlockPos link5Pos = util.grid().at(4, 2, 2);
        BlockPos link6Pos = util.grid().at(2, 2, 2);
        Selection link5 = util.select().position(link5Pos);
        Selection link6 = util.select().position(link6Pos);
        Selection anchor = util.select().position(3, 4, 0);
        BlockPos link7InitialPos = util.grid().at(3, 5, 0);
        BlockPos link7Pos = util.grid().at(3, 2, 1);
        Selection link7 = util.select().position(link7InitialPos);
        Selection kinetics = util.select().fromTo(5, 5, 3, 1, 5, 2);
        Selection engine = util.select().position(3, 5, 1);
        BlockPos leverInitialPos = util.grid().at(3, 4, 3);
        BlockPos leverPos = util.grid().at(3, 2, 2);
        Selection lever = util.select().position(leverInitialPos);
        BlockPos gearshiftPos = util.grid().at(3, 5, 2);
        Selection piston1 = util.select().fromTo(5, 5, 1, 4, 5, 1);
        Selection piston2 = util.select().fromTo(2, 5, 1, 1, 5, 1);
        BlockPos secBoard = util.grid().at(5, 2, 0);
        Selection fullSecBoard = util.select().fromTo(5, 2, 0, 1, 2, 0);
        Selection secSmallCog = util.select().fromTo(0, 3, 4, 0, 3, 0);
        BlockPos secBoardMiddle = util.grid().at(3, 1, 0);
        BlockPos link8InitialPos = util.grid().at(5, 6, 0);
        BlockPos link9InitialPos = util.grid().at(1, 6, 0);
        BlockPos link8Pos = util.grid().at(5, 2, 2);
        BlockPos link9Pos = util.grid().at(1, 2, 2);
        Selection link8 = util.select().position(link8InitialPos);
        Selection link9 = util.select().position(link9InitialPos);
        Selection basePlate = util.select().fromTo(0, 0, 0, 6, 0, 6);
        Selection nether = util.select().layer(9);
        Selection steam = util.select().fromTo(0, 6, 1, 6, 7, 3);
        BlockPos link10InitialPos = util.grid().at(3, 8, 1);
        BlockPos link10Pos = util.grid().at(3, 3, 1);
        Selection link10 = util.select().position(link10InitialPos);

        ElementLink<WorldSectionElement> inputReplacementElement = scene.world().makeSectionIndependent(inputReplacement);
        scene.world().moveSection(inputReplacementElement, util.vector().of(0, -64, 0), 0);
        scene.world().hideIndependentSection(inputReplacementElement, Direction.DOWN);
        ElementLink<WorldSectionElement> outputReplacementElement = scene.world().makeSectionIndependent(outputReplacement);
        scene.world().moveSection(outputReplacementElement, util.vector().of(0, -64, 0), 0);
        scene.world().hideIndependentSection(outputReplacementElement, Direction.DOWN);

        ElementLink<WorldSectionElement> inputElement = scene.world().showIndependentSection(input, Direction.DOWN);
        scene.world().moveSection(inputElement, util.vector().of(0, -2, 0), 0);
        ElementLink<WorldSectionElement> outputElement = scene.world().showIndependentSection(output, Direction.DOWN);
        scene.world().moveSection(outputElement, util.vector().of(0, -2, 0), 0);

        scene.world().setKineticSpeed(output, 0f);
        scene.idle(15);

        scene.world().showSection(largeCog, Direction.UP);
        scene.world().showSection(smallCog, Direction.WEST);
        scene.idle(5);
        scene.world().showSection(fullBoard, Direction.NORTH);
        scene.idle(25);

        ElementLink<WorldSectionElement> shaftElement = scene.world().showIndependentSection(shaft, Direction.DOWN);
        scene.idle(10);

        Vec3 target = util.vector().of(4.5, 2.75, 4.25);

        scene.overlay().showControls(target, Pointing.RIGHT, 20).withItem(AllBlocks.DISPLAY_LINK.asStack()).rightClick();
        scene.idle(6);
        scene.overlay().chaseBoundingBoxOutline(PonderPalette.OUTPUT, link1, new AABB(board).expandTowards(-4, -1, 0)
                .deflate(0, 0, 3 / 16f), 50);
        scene.idle(35);
        scene.world().showSection(link1, Direction.DOWN);
        scene.idle(20);

        scene.overlay().showControls(util.vector().centerOf(link1Pos), Pointing.DOWN, 25).scroll();
        scene.overlay().showText(25)
                .text("-> Rotation Speed (RPM)")
                .pointAt(util.vector().centerOf(link1Pos))
                .colored(PonderPalette.WHITE)
                .placeNearTarget();
        scene.idle(30);
        scene.overlay().showText(25)
                .text("$ @ NETWORK #1")
                .pointAt(util.vector().centerOf(link1Pos))
                .colored(PonderPalette.INPUT)
                .placeNearTarget();
        scene.idle(30);

        scene.effects().indicateSuccess(link1Pos);
        scene.world().setDisplayBoardText(board, 1, Component.literal(CreateIDLX.translate("ponder.clipboard_copiable_properties.text_2").getString()
                .replaceAll("\\$", Component.literal("68 ").append(CreateLang.translateDirect("generic.unit.rpm")).getString())));
        scene.world().flashDisplayLink(link1Pos);
        scene.idle(20);

        scene.overlay().showText(210)
                .text("There are three types of Display Link properties a Clipboard can be used to copy:")
                .independent(40)
                .attachKeyFrame()
                .placeNearTarget();
        scene.idle(80);

        scene.overlay().showText(90)
                .text("the Attached Label,")
                .colored(PonderPalette.MEDIUM)
                .independent(74)
                .placeNearTarget();
        scene.overlay().showControls(util.vector().centerOf(link1Pos), Pointing.RIGHT, 90).showing(CreateIDLXIcons.labelIcon);
        scene.idle(20);

        scene.overlay().showText(80)
                .text("Type-Specific Parameters,")
                .colored(PonderPalette.MEDIUM)
                .independent(90)
                .placeNearTarget();
        scene.overlay().showControls(util.vector().topOf(link1Pos), Pointing.DOWN, 80).showing(CreateIDLXIcons.configIcon);
        scene.idle(20);

        scene.overlay().showText(70)
                .text("and the Target Display Position")
                .colored(PonderPalette.MEDIUM)
                .independent(106)
                .placeNearTarget();
        scene.overlay().showControls(util.vector().centerOf(link1Pos), Pointing.LEFT, 70).showing(CreateIDLXIcons.targetIcon);
        scene.idle(100);

        scene.addKeyframe();

        scene.world().hideIndependentSection(shaftElement, Direction.SOUTH);
        ElementLink<WorldSectionElement> link1Element = scene.world().makeSectionIndependent(link1);
        scene.world().moveSection(link1Element, util.vector().of(0.5, 0, 0), 15);
        scene.idle(15);

        ElementLink<WorldSectionElement> shaftReplacementElement = scene.world().showIndependentSection(shaftReplacement, Direction.SOUTH);
        scene.world().moveSection(shaftReplacementElement, util.vector().of(0, 0, 1), 0);
        scene.world().moveSection(link1Element, util.vector().of(0, -2, 0), 0);
        scene.world().hideIndependentSection(link1Element, Direction.WEST);
        scene.world().showSection(link2, Direction.EAST);
        scene.idle(20);
        scene.world().showSection(link3, Direction.DOWN);
        scene.idle(20);

        scene.overlay().showControls(util.vector().centerOf(link3Pos), Pointing.DOWN, 25).scroll();
        scene.overlay().showText(25)
                .text("-> Network Stress")
                .pointAt(util.vector().centerOf(link3Pos))
                .colored(PonderPalette.WHITE)
                .placeNearTarget();
        scene.idle(30);
        scene.overlay().showText(25)
                .text("-> Stress in SU")
                .pointAt(util.vector().centerOf(link3Pos))
                .colored(PonderPalette.WHITE)
                .placeNearTarget();
        scene.idle(30);

        scene.effects().indicateSuccess(link3Pos);
        scene.world().setDisplayBoardText(board, 2, Component.literal("1,088 ").append(CreateLang.translateDirect("generic.unit.stress")));
        scene.world().flashDisplayLink(link3Pos);
        scene.idle(20);

        scene.overlay().showText(80)
                .text("The Attached Label is the text that gets displayed along with the information part")
                .attachKeyFrame()
                .independent()
                .placeNearTarget();
        scene.idle(90);

        scene.overlay().showControls(util.vector().centerOf(link2Pos), Pointing.DOWN, 40).withItem(AllBlocks.CLIPBOARD.asStack()).rightClick();
        scene.idle(50);
        scene.overlay().showText(60)
                .text("Copy the Attached Label")
                .pointAt(util.vector().centerOf(link2Pos))
                .colored(PonderPalette.MEDIUM)
                .placeNearTarget();
        scene.overlay().showControls(util.vector().centerOf(link2Pos), Pointing.DOWN, 60).showing(CreateIDLXIcons.labelIcon);
        scene.idle(70);

        scene.overlay().showControls(util.vector().centerOf(link3Pos), Pointing.DOWN, 40).withItem(filledClipboard).leftClick();
        scene.idle(50);
        scene.overlay().showText(60)
                .text("Apply the Attached Label")
                .pointAt(util.vector().centerOf(link3Pos))
                .colored(PonderPalette.MEDIUM)
                .placeNearTarget();
        scene.overlay().showControls(util.vector().centerOf(link3Pos), Pointing.DOWN, 60).showing(CreateIDLXIcons.labelIcon);
        scene.idle(65);

        scene.effects().indicateSuccess(link3Pos);
        scene.world().setDisplayBoardText(board, 2, Component.literal(CreateIDLX.translate("ponder.clipboard_copiable_properties.text_2").getString()
                .replaceAll("\\$", Component.literal("1,088 ").append(CreateLang.translateDirect("generic.unit.stress")).getString())));
        scene.world().flashDisplayLink(link3Pos);
        scene.idle(60);

        scene.world().hideIndependentSection(shaftReplacementElement, Direction.UP);
        scene.world().hideSection(link2, Direction.UP);
        scene.world().setDisplayBoardText(board, 1, CommonComponents.EMPTY);
        scene.world().hideSection(link3, Direction.UP);
        scene.world().setDisplayBoardText(board, 2, CommonComponents.EMPTY);
        scene.world().hideIndependentSection(inputElement, Direction.UP);
        scene.world().hideIndependentSection(outputElement, Direction.UP);
        scene.idle(15);

        scene.world().restoreBlocks(inputReplacement);
        scene.world().restoreBlocks(outputReplacement);
        inputReplacementElement = scene.world().showIndependentSection(inputReplacement, Direction.UP);
        scene.world().moveSection(inputReplacementElement, util.vector().of(0, -4, 0), 0);
        outputReplacementElement = scene.world().showIndependentSection(outputReplacement, Direction.UP);
        scene.world().moveSection(outputReplacementElement, util.vector().of(0, -4, 0), 0);
        scene.idle(20);

        scene.addKeyframe();

        ElementLink<WorldSectionElement> smartieElement = scene.world().showIndependentSection(smartie, Direction.DOWN);
        scene.world().moveSection(smartieElement, util.vector().of(0, -2, 0), 0);
        scene.idle(10);
        ElementLink<WorldSectionElement> link4Element = scene.world().showIndependentSection(link4, Direction.DOWN);
        scene.world().moveSection(link4Element, util.vector().of(0, -2, 0), 0);
        scene.idle(20);

        scene.overlay().showControls(util.vector().centerOf(link4Pos), Pointing.DOWN, 25).scroll();
        scene.overlay().showText(25)
                .text("-> List matching Fluids")
                .pointAt(util.vector().centerOf(link4Pos))
                .colored(PonderPalette.WHITE)
                .placeNearTarget();
        scene.idle(30);

        scene.effects().indicateSuccess(link4Pos);
        scene.world().setDisplayBoardText(board, 1, Component.literal("1,0")
                .append(CreateLang.translateDirect("flap_display.cycles.fluid_units").getString().split(";")[1])
                .append(" ").append(Component.translatable("fluid.create.chocolate")));
        scene.world().setDisplayBoardText(board, 2, Component.literal("500")
                .append(CreateLang.translateDirect("flap_display.cycles.fluid_units").getString().split(";")[0])
                .append(" ").append(Component.translatable("fluid.create.honey")));
        scene.world().flashDisplayLink(link4InitialPos);
        scene.idle(20);

        scene.overlay().showControls(util.vector().centerOf(link4Pos), Pointing.DOWN, 100).showing(AllIcons.I_MTD_CLOSE)
                .withItem(filledClipboard);
        scene.idle(30);

        scene.overlay().showText(80)
                .text("This property cannot be applied to any Display Link with a selected Information Type that doesn't support it")
                .pointAt(util.vector().centerOf(link4Pos))
                .colored(PonderPalette.RED)
                .attachKeyFrame()
                .placeNearTarget();
        scene.idle(110);

        scene.world().hideIndependentSection(smartieElement, Direction.UP);
        scene.world().hideIndependentSection(link4Element, Direction.UP);
        scene.world().setDisplayBoardText(board, 1, CommonComponents.EMPTY);
        scene.world().setDisplayBoardText(board, 2, CommonComponents.EMPTY);
        scene.idle(20);

        scene.addKeyframe();

        scene.world().showSection(cuckoo1, Direction.DOWN);
        scene.world().showSection(cuckoo2, Direction.DOWN);
        scene.idle(10);
        scene.world().showSection(link5, Direction.DOWN);
        scene.idle(20);

        scene.overlay().showControls(util.vector().centerOf(link5Pos), Pointing.DOWN, 25).scroll();
        scene.overlay().showText(25)
                .text("-> Time of Day")
                .pointAt(util.vector().centerOf(link5Pos))
                .colored(PonderPalette.WHITE)
                .placeNearTarget();
        scene.idle(30);
        scene.overlay().showText(20)
                .text("THE TIME IS $...")
                .pointAt(util.vector().centerOf(link5Pos))
                .colored(PonderPalette.INPUT)
                .placeNearTarget();
        scene.idle(30);
        scene.overlay().showControls(util.vector().centerOf(link5Pos), Pointing.DOWN, 25).scroll();
        scene.overlay().showText(25)
                .text("-> 24-hour")
                .pointAt(util.vector().centerOf(link5Pos))
                .colored(PonderPalette.WHITE)
                .placeNearTarget();
        scene.idle(30);

        scene.effects().indicateSuccess(link5Pos);
        scene.world().setDisplayBoardText(board, 1, Component.literal(CreateIDLX.translate("ponder.clipboard_copiable_properties.text_15").getString()
                .replaceAll("\\$", Component.literal("15:55").getString())));
        scene.world().flashDisplayLink(link5Pos);
        scene.idle(20);

        scene.world().showSection(link6, Direction.DOWN);
        scene.idle(10);

        scene.overlay().showText(20)
                .text("-> Time of Day")
                .pointAt(util.vector().centerOf(link6Pos))
                .colored(PonderPalette.WHITE)
                .placeNearTarget();
        scene.idle(30);
        scene.overlay().showControls(util.vector().centerOf(link6Pos), Pointing.DOWN, 25).scroll();
        scene.overlay().showText(25)
                .text("...OR RATHER $")
                .pointAt(util.vector().centerOf(link6Pos))
                .colored(PonderPalette.INPUT)
                .placeNearTarget();
        scene.idle(30);

        scene.effects().indicateSuccess(link6Pos);
        scene.world().setDisplayBoardText(board, 2, Component.literal(CreateIDLX.translate("ponder.clipboard_copiable_properties.text_18").getString()
                .replaceAll("\\$", Component.literal(" 3:55 ").append(CreateLang.translateDirect("generic.daytime.pm")).getString())));
        scene.world().flashDisplayLink(link6Pos);
        scene.idle(20);

        scene.overlay().showText(100)
                .text("Type-Specific Parameters are additional settings that shape the final look of the information string")
                .attachKeyFrame()
                .independent()
                .placeNearTarget();
        scene.idle(110);
        scene.overlay().showText(120)
                .text("Basically, they are all the fields configurable in the Display Link UI except for the Attached Label and Target Line ones")
                .attachKeyFrame()
                .independent()
                .placeNearTarget();
        scene.idle(130);
        scene.overlay().showText(100)
                .text("The Time of Day Information Type, for example, has a Time Format parameter, being either 12-hour or 24-hour")
                .pointAt(target.add(-3.5, 0.25, 0))
                .attachKeyFrame()
                .placeNearTarget();
        scene.idle(110);

        scene.overlay().showControls(util.vector().centerOf(link5Pos), Pointing.DOWN, 40).withItem(AllBlocks.CLIPBOARD.asStack()).rightClick();
        scene.idle(50);
        scene.overlay().showText(60)
                .text("Copy Type-Specific Parameters")
                .pointAt(util.vector().centerOf(link5Pos))
                .colored(PonderPalette.MEDIUM)
                .placeNearTarget();
        scene.overlay().showControls(util.vector().centerOf(link5Pos), Pointing.DOWN, 60).showing(CreateIDLXIcons.configIcon);
        scene.idle(70);

        scene.overlay().showControls(util.vector().centerOf(link6Pos), Pointing.DOWN, 40).withItem(filledClipboard).leftClick();
        scene.idle(50);
        scene.overlay().showText(60)
                .text("Apply Type-Specific Parameters")
                .pointAt(util.vector().centerOf(link6Pos))
                .colored(PonderPalette.MEDIUM)
                .placeNearTarget();
        scene.overlay().showControls(util.vector().centerOf(link6Pos), Pointing.DOWN, 60).showing(CreateIDLXIcons.configIcon);
        scene.idle(65);

        scene.effects().indicateSuccess(link6Pos);
        scene.world().setDisplayBoardText(board, 2, Component.literal(CreateIDLX.translate("ponder.clipboard_copiable_properties.text_18").getString()
                .replaceAll("\\$", Component.literal("15:55").getString())));
        scene.world().flashDisplayLink(link6Pos);
        scene.idle(60);

        scene.world().hideSection(cuckoo1, Direction.UP);
        scene.world().hideSection(cuckoo2, Direction.UP);
        scene.world().hideSection(link5, Direction.UP);
        scene.world().hideSection(link6, Direction.UP);
        scene.world().setDisplayBoardText(board, 1, CommonComponents.EMPTY);
        scene.world().setDisplayBoardText(board, 2, CommonComponents.EMPTY);
        scene.idle(20);

        scene.addKeyframe();

        ElementLink<WorldSectionElement> anchorElement = scene.world().showIndependentSection(anchor, Direction.DOWN);
        scene.world().moveSection(anchorElement, util.vector().of(0, -3, 1), 0);
        scene.idle(10);
        ElementLink<WorldSectionElement> link7Element = scene.world().showIndependentSection(link7, Direction.DOWN);
        scene.world().moveSection(link7Element, util.vector().of(0, -3, 1), 0);
        scene.idle(20);

        scene.overlay().showControls(util.vector().centerOf(link7Pos), Pointing.DOWN, 25).scroll();
        scene.overlay().showText(25)
                .text("-> Player Deaths")
                .pointAt(util.vector().centerOf(link7Pos))
                .colored(PonderPalette.WHITE)
                .placeNearTarget();
        scene.idle(30);

        scene.effects().indicateSuccess(link7Pos);
        scene.world().setDisplayBoardText(board, 1, Component.literal("JOHN_CREATE       1804"));
        scene.world().flashDisplayLink(link7InitialPos);
        scene.idle(20);

        scene.overlay().showControls(util.vector().centerOf(link7Pos), Pointing.DOWN, 250).showing(AllIcons.I_MTD_CLOSE)
                .withItem(filledClipboard);
        scene.idle(30);

        scene.overlay().showText(120)
                .text("Those properties cannot be applied to any Display Link with a different selected Information Type or a straight up different block as the Source...")
                .pointAt(util.vector().centerOf(link7Pos))
                .colored(PonderPalette.RED)
                .attachKeyFrame()
                .placeNearTarget();
        scene.idle(130);
        scene.overlay().showText(100)
                .text("...since for each one the Type-Specific Parameters are technically unique (and some don't even have any)")
                .pointAt(util.vector().centerOf(link7Pos))
                .colored(PonderPalette.RED)
                .placeNearTarget();
        scene.idle(130);

        scene.world().hideIndependentSection(anchorElement, Direction.UP);
        scene.world().hideIndependentSection(link7Element, Direction.UP);
        scene.world().setDisplayBoardText(board, 1, CommonComponents.EMPTY);
        scene.idle(20);

        scene.addKeyframe();

        ElementLink<WorldSectionElement> kineticsElement = scene.world().showIndependentSection(kinetics, Direction.DOWN);
        scene.world().moveSection(kineticsElement, util.vector().of(0, -4, 0), 0);
        ElementLink<WorldSectionElement> engineElement = scene.world().showIndependentSection(engine, Direction.DOWN);
        scene.world().moveSection(engineElement, util.vector().of(0, -4, 0), 0);
        ElementLink<WorldSectionElement> leverElement = scene.world().showIndependentSection(lever, Direction.DOWN);
        scene.world().moveSection(leverElement, util.vector().of(0, -2, -1), 0);
        ElementLink<WorldSectionElement> piston1Element = scene.world().showIndependentSection(piston1, Direction.DOWN);
        scene.world().moveSection(piston1Element, util.vector().of(0, -4, 1), 0);
        ElementLink<WorldSectionElement> piston2Element = scene.world().showIndependentSection(piston2, Direction.DOWN);
        scene.world().moveSection(piston2Element, util.vector().of(-1, -4, 1), 0);
        scene.idle(10);

        ElementLink<WorldSectionElement> fullSecBoardElement = scene.world().showIndependentSection(fullSecBoard, Direction.SOUTH);
        scene.world().moveSection(fullSecBoardElement, util.vector().of(0, -1, 0), 0);
        scene.idle(30);

        scene.overlay().showControls(util.vector().topOf(secBoardMiddle), Pointing.DOWN, 20).withItem(AllBlocks.DISPLAY_LINK.asStack())
                .rightClick();
        scene.idle(6);
        scene.overlay().chaseBoundingBoxOutline(PonderPalette.OUTPUT, link8, new AABB(util.grid().at(5, 1, 0)).expandTowards(-4, 0, 0)
                .deflate(0, 0, 3 / 16f), 50);
        scene.idle(45);

        ElementLink<WorldSectionElement> link8Element = scene.world().showIndependentSection(link8, Direction.DOWN);
        scene.world().moveSection(link8Element, util.vector().of(0, -4, 2), 0);
        scene.idle(20);

        scene.overlay().showControls(util.vector().centerOf(link8Pos), Pointing.DOWN, 25).scroll();
        scene.overlay().showText(25)
                .text("-> Mechanical Piston Extension State")
                .pointAt(util.vector().centerOf(link8Pos))
                .colored(PonderPalette.WHITE)
                .placeNearTarget();
        scene.idle(30);
        scene.overlay().showText(25)
                .text("PISTON #1: $")
                .pointAt(util.vector().centerOf(link8Pos))
                .colored(PonderPalette.INPUT)
                .placeNearTarget();
        scene.idle(30);
        scene.overlay().showControls(util.vector().centerOf(link8Pos), Pointing.DOWN, 25).scroll();
        scene.overlay().showText(25)
                .text("-> Used to Total Extension Poles Ratio")
                .pointAt(util.vector().centerOf(link8Pos))
                .colored(PonderPalette.WHITE)
                .placeNearTarget();
        scene.idle(30);

        scene.effects().indicateSuccess(link8Pos);
        scene.world().dyeDisplayBoard(secBoard, 0, DyeColor.YELLOW);
        scene.world().setDisplayBoardText(secBoard, 0, Component.literal(CreateIDLX.translate("ponder.clipboard_copiable_properties.text_28").getString()
                .replaceAll("\\$", CreateIDLX.translate("display_source.mechanical_piston_extension_state.ratio_template",
                        "0", "1").getString())));
        scene.world().flashDisplayLink(link8InitialPos);
        scene.idle(20);

        scene.overlay().showControls(target, Pointing.RIGHT, 20).withItem(AllBlocks.DISPLAY_LINK.asStack()).rightClick();
        scene.idle(6);
        scene.overlay().chaseBoundingBoxOutline(PonderPalette.OUTPUT, link9, new AABB(board).expandTowards(-4, -1, 0)
                .deflate(0, 0, 3 / 16f), 50);
        scene.idle(35);

        ElementLink<WorldSectionElement> link9Element = scene.world().showIndependentSection(link9, Direction.DOWN);
        scene.world().moveSection(link9Element, util.vector().of(0, -4, 2), 0);
        scene.idle(20);

        scene.overlay().showControls(util.vector().centerOf(link9Pos), Pointing.DOWN, 25).scroll();
        scene.overlay().showText(25)
                .text("-> Mechanical Piston Extension State")
                .pointAt(util.vector().centerOf(link9Pos))
                .colored(PonderPalette.WHITE)
                .placeNearTarget();
        scene.idle(30);
        scene.overlay().showText(25)
                .text("PISTON #2: $")
                .pointAt(util.vector().centerOf(link9Pos))
                .colored(PonderPalette.INPUT)
                .placeNearTarget();
        scene.idle(30);
        scene.overlay().showControls(util.vector().centerOf(link9Pos), Pointing.DOWN, 25).scroll();
        scene.overlay().showText(25)
                .text("-> Used Extension Poles Percentage")
                .pointAt(util.vector().centerOf(link9Pos))
                .colored(PonderPalette.WHITE)
                .placeNearTarget();
        scene.idle(30);

        scene.effects().indicateSuccess(link9Pos);
        scene.world().setDisplayBoardText(board, 1, Component.literal(CreateIDLX.translate("ponder.clipboard_copiable_properties.text_31").getString()
                .replaceAll("\\$", "100%")));
        scene.world().flashDisplayLink(link9InitialPos);
        scene.idle(20);

        scene.addKeyframe();
        scene.idle(20);

        scene.effects().indicateRedstone(leverPos);
        scene.world().toggleRedstonePower(util.select().fromTo(leverInitialPos, gearshiftPos));
        scene.world().modifyKineticSpeed(kinetics, f -> -f);
        scene.world().moveSection(piston1Element, util.vector().of(1, 0, 0), 80);
        scene.world().moveSection(piston2Element, util.vector().of(1, 0, 0), 80);
        scene.idle(40);
        scene.world().setDisplayBoardText(secBoard, 0, Component.literal(CreateIDLX.translate("ponder.clipboard_copiable_properties.text_28").getString()
                .replaceAll("\\$", CreateIDLX.translate("display_source.mechanical_piston_extension_state.ratio_template",
                        "0,5", "1").getString())));
        scene.world().flashDisplayLink(link8InitialPos);
        scene.world().setDisplayBoardText(board, 1, Component.literal(CreateIDLX.translate("ponder.clipboard_copiable_properties.text_31").getString()
                .replaceAll("\\$", "50%")));
        scene.world().flashDisplayLink(link9InitialPos);
        scene.idle(40);
        scene.world().setDisplayBoardText(secBoard, 0, Component.literal(CreateIDLX.translate("ponder.clipboard_copiable_properties.text_28").getString()
                .replaceAll("\\$", CreateIDLX.translate("display_source.mechanical_piston_extension_state.ratio_template",
                        "1", "1").getString())));
        scene.world().flashDisplayLink(link8InitialPos);
        scene.world().setDisplayBoardText(board, 1, Component.literal(CreateIDLX.translate("ponder.clipboard_copiable_properties.text_31").getString()
                .replaceAll("\\$", "0%")));
        scene.world().flashDisplayLink(link9InitialPos);
        scene.idle(40);

        scene.overlay().showText(120)
                .text("The Target Display Position is the dimension, coordinates and the target line of the block the Display Link sends information to")
                .attachKeyFrame()
                .independent()
                .placeNearTarget();
        scene.idle(130);

        scene.overlay().showControls(util.vector().centerOf(link8Pos), Pointing.DOWN, 40).withItem(AllBlocks.CLIPBOARD.asStack()).rightClick();
        scene.idle(50);
        scene.overlay().showText(60)
                .text("Copy the Target Display Position")
                .pointAt(util.vector().centerOf(link8Pos))
                .colored(PonderPalette.MEDIUM)
                .placeNearTarget();
        scene.overlay().showControls(util.vector().centerOf(link8Pos), Pointing.DOWN, 60).showing(CreateIDLXIcons.targetIcon);
        scene.idle(70);

        scene.overlay().showControls(util.vector().centerOf(link9Pos), Pointing.DOWN, 40).withItem(filledClipboard).leftClick();
        scene.idle(50);
        scene.overlay().showText(60)
                .text("Apply the Target Display Position")
                .pointAt(util.vector().centerOf(link9Pos))
                .colored(PonderPalette.MEDIUM)
                .placeNearTarget();
        scene.overlay().showControls(util.vector().centerOf(link9Pos), Pointing.DOWN, 60).showing(CreateIDLXIcons.targetIcon);
        scene.idle(65);

        scene.effects().indicateSuccess(link9Pos);
        scene.world().setDisplayBoardText(board, 0, Component.literal(CreateIDLX.translate("ponder.clipboard_copiable_properties.text_28").getString()
                .replaceAll("\\$", CreateIDLX.translate("display_source.mechanical_piston_extension_state.ratio_template",
                        String.valueOf(1), String.valueOf(1)).getString())));
        scene.world().flashDisplayLink(link9InitialPos);
        scene.idle(60);

        scene.addKeyframe();
        scene.idle(20);

        scene.effects().indicateRedstone(leverPos);
        scene.world().toggleRedstonePower(util.select().fromTo(leverInitialPos, gearshiftPos));
        scene.world().modifyKineticSpeed(kinetics, f -> -f);
        scene.world().moveSection(piston1Element, util.vector().of(-1, 0, 0), 80);
        scene.world().moveSection(piston2Element, util.vector().of(-1, 0, 0), 80);
        scene.idle(40);
        scene.world().setDisplayBoardText(board, 0, Component.literal(CreateIDLX.translate("ponder.clipboard_copiable_properties.text_28").getString()
                .replaceAll("\\$", CreateIDLX.translate("display_source.mechanical_piston_extension_state.ratio_template",
                        "0,5", "1").getString())));
        scene.world().flashDisplayLink(link8InitialPos);
        scene.world().setDisplayBoardText(board, 1, Component.literal(CreateIDLX.translate("ponder.clipboard_copiable_properties.text_31").getString()
                .replaceAll("\\$", "50%")));
        scene.world().flashDisplayLink(link9InitialPos);
        scene.idle(40);
        scene.world().setDisplayBoardText(board, 0, Component.literal(CreateIDLX.translate("ponder.clipboard_copiable_properties.text_28").getString()
                .replaceAll("\\$", CreateIDLX.translate("display_source.mechanical_piston_extension_state.ratio_template",
                        "0", "1").getString())));
        scene.world().flashDisplayLink(link8InitialPos);
        scene.world().setDisplayBoardText(board, 1, Component.literal(CreateIDLX.translate("ponder.clipboard_copiable_properties.text_31").getString()
                .replaceAll("\\$", "100%")));
        scene.world().flashDisplayLink(link9InitialPos);
        scene.idle(40);

        scene.world().hideIndependentSection(kineticsElement, Direction.UP);
        scene.world().hideIndependentSection(engineElement, Direction.UP);
        scene.world().hideIndependentSection(leverElement, Direction.UP);
        scene.world().hideIndependentSection(piston1Element, Direction.UP);
        scene.world().hideIndependentSection(piston2Element, Direction.UP);
        scene.world().hideIndependentSection(link8Element, Direction.UP);
        scene.world().hideIndependentSection(link9Element, Direction.UP);
        scene.world().setDisplayBoardText(secBoard, 0, CommonComponents.EMPTY);
        scene.world().setDisplayBoardText(board, 0, CommonComponents.EMPTY);
        scene.world().setDisplayBoardText(board, 1, CommonComponents.EMPTY);
        scene.idle(10);
        scene.world().hideIndependentSection(fullSecBoardElement, Direction.NORTH);
        scene.idle(10);
        scene.world().hideSection(fullBoard, Direction.SOUTH);
        scene.idle(5);
        scene.world().hideSection(smallCog, Direction.EAST);
        scene.world().hideSection(largeCog, Direction.DOWN);
        scene.idle(15);

        scene.addKeyframe();

        ElementLink<WorldSectionElement> basePlateElement = scene.world().makeSectionIndependent(basePlate);
        scene.world().hideIndependentSection(basePlateElement, Direction.DOWN);
        scene.world().hideIndependentSection(inputReplacementElement, Direction.DOWN);
        scene.world().hideIndependentSection(outputReplacementElement, Direction.DOWN);
        scene.rotateCameraY(360);
        scene.idle(15);
        ElementLink<WorldSectionElement> netherElement = scene.world().showIndependentSection(nether, Direction.UP);
        scene.world().moveSection(netherElement, util.vector().of(0, -9, 0), 0);
        scene.idle(20);

        scene.world().restoreBlocks(largeCog);
        scene.world().restoreBlocks(smallCog);
        scene.world().restoreBlocks(fullBoard);
        scene.world().showSection(largeCog, Direction.UP);
        scene.world().showSection(smallCog, Direction.WEST);
        scene.idle(5);
        scene.world().showSection(fullBoard, Direction.NORTH);
        scene.idle(25);
        ElementLink<WorldSectionElement> steamElement = scene.world().showIndependentSection(steam, Direction.DOWN);
        scene.world().moveSection(steamElement, util.vector().of(0, -5, 0), 0);
        scene.idle(5);
        ElementLink<WorldSectionElement> link10Element = scene.world().showIndependentSection(link10, Direction.DOWN);
        scene.world().moveSection(link10Element, util.vector().of(0, -5, 0), 0);
        scene.idle(20);

        scene.overlay().showControls(util.vector().centerOf(link10Pos), Pointing.DOWN, 25).scroll();
        scene.overlay().showText(25)
                .text("-> Boiler Status")
                .pointAt(util.vector().centerOf(link10Pos))
                .colored(PonderPalette.WHITE)
                .placeNearTarget();
        scene.idle(30);

        scene.effects().indicateSuccess(link10Pos);
        scene.world().setDisplayBoardText(board, 1, CreateLang.translateDirect("boiler.status", CreateLang.translateDirect("boiler.idle")));
        scene.world().flashDisplayLink(link10InitialPos);
        scene.idle(20);

        scene.overlay().showControls(util.vector().centerOf(link10Pos), Pointing.DOWN, 140).showing(AllIcons.I_MTD_CLOSE)
                .withItem(filledClipboard);
        scene.idle(30);

        scene.overlay().showText(120)
                .text("This property cannot be applied to any Display Link that is too far away from the Target Display, or in a different dimension altogether")
                .pointAt(util.vector().centerOf(link10Pos))
                .colored(PonderPalette.RED)
                .attachKeyFrame()
                .placeNearTarget();
        scene.idle(130);

        scene.markAsFinished();
    }
}
