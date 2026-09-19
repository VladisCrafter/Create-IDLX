package com.vladiscrafter.createidlx.foundation.gui;

import com.vladiscrafter.createidlx.CreateIDLX;
import net.createmod.catnip.gui.TextureSheetSegment;
import net.createmod.catnip.gui.UIRenderHelper;
import net.createmod.catnip.gui.element.ScreenElement;
import net.createmod.catnip.theme.Color;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public enum CreateIDLXGuiTextures implements ScreenElement, TextureSheetSegment {

    CLIPBOARD_DISPLAY_SOURCE_COPYING("clipboard_display_source", 0, 0, 235, 122),
    CLIPBOARD_DISPLAY_SOURCE_PASTING("clipboard_display_source", 0, 134, 235, 122),

    RICH_LABEL_EDITOR("rich_label_editor", 0, 0, 360, 240, 512, 512),
    RLE_FIELD("rich_label_editor", 0, 242, 360, 36, 512, 512),
    RLE_COUNTER_L("rich_label_editor", 338, 280, 6, 16, 512, 512),
    RLE_COUNTER_M("rich_label_editor", 345, 280, 1, 16, 512, 512),
    RLE_COUNTER_R("rich_label_editor", 347, 280, 6, 16, 512, 512),
    RLE_CONFIG("rich_label_editor", 362, 0, 9, 9, 512, 512),
    RLE_TABS_BG_E("rich_label_editor", 373, 0, 1, 16, 512, 512),
    RLE_TABS_BG_M("rich_label_editor", 374, 0, 1, 16, 512, 512),
    RLE_TAB_ACTIVE_L("rich_label_editor", 377, 0, 1, 13, 512, 512),
    RLE_TAB_ACTIVE_M("rich_label_editor", 378, 0, 1, 13, 512, 512),
    RLE_TAB_ACTIVE_R("rich_label_editor", 379, 0, 1, 13, 512, 512),
    RLE_TAB_INACTIVE_L("rich_label_editor", 382, 0, 1, 13, 512, 512),
    RLE_TAB_INACTIVE_M("rich_label_editor", 383, 0, 1, 13, 512, 512),
    RLE_TAB_INACTIVE_R("rich_label_editor", 384, 0, 1, 13, 512, 512),
    RLE_TABS_SEPARATOR("rich_label_editor", 387, 0, 1, 11, 512, 512),
    RLE_TABS_NEW("rich_label_editor", 390, 0, 9, 9, 512, 512),
    RLE_TABS_NEW_H("rich_label_editor", 401, 0, 9, 9, 512, 512),
    RLE_PL_PROPERTIES_BG_L("rich_label_editor", 362, 18, 6, 66, 512, 512),
    RLE_PL_PROPERTIES_BG_M("rich_label_editor", 370, 18, 1, 66, 512, 512),
    RLE_PL_PROPERTIES_BG_R("rich_label_editor", 373, 18, 6, 66, 512, 512),
    RLE_PL_PROPERTIES_SEPARATOR("rich_label_editor", 381, 18, 4, 50, 512, 512),
    RLE_PL_PROPERTIES_FG_L("rich_label_editor", 387, 18, 2, 16, 512, 512),
    RLE_PL_PROPERTIES_FG_M("rich_label_editor", 391, 18, 1, 16, 512, 512),
    RLE_PL_PROPERTIES_FG_R("rich_label_editor", 394, 18, 2, 16, 512, 512),
    RLE_PL_CAPTURE_GROUPS_RESET("rich_label_editor", 398, 18, 11, 11, 512, 512),
    RLE_PL_CAPTURE_GROUPS_RESET_H("rich_label_editor", 411, 18, 11, 11, 512, 512),
    RLE_PL_TYPE_RADIO_WIRE("rich_label_editor", 424, 18, 1, 8, 512, 512),
    RLE_PL_PROPERTIES_CHECKBOX_OFF("rich_label_editor", 427, 18, 9, 9, 512, 512),
    RLE_PL_PROPERTIES_CHECKBOX_OFF_H("rich_label_editor", 438, 18, 9, 9, 512, 512),
    RLE_PL_PROPERTIES_CHECKBOX_ON("rich_label_editor", 449, 18, 9, 9, 512, 512),
    RLE_PL_PROPERTIES_CHECKBOX_ON_H("rich_label_editor", 460, 18, 9, 9, 512, 512),
    RLE_PL_CAPTURE_GROUPS_BRACKETS("rich_label_editor", 362, 86, 130, 39, 512, 512),
    ;

    public final ResourceLocation location;
    private final int width;
    private final int height;
    private final int startX;
    private final int startY;
    private final int texWidth;
    private final int texHeight;

    CreateIDLXGuiTextures(String location, int startX, int startY, int width, int height) {
        this(location, startX, startY, width, height, 256, 256);
    }

    CreateIDLXGuiTextures(String location, int startX, int startY, int width, int height, int texWidth, int texHeight) {
        this(CreateIDLX.ID, location, startX, startY, width, height, texWidth, texHeight);
    }

    CreateIDLXGuiTextures(String namespace, String location, int startX, int startY, int width, int height, int texWidth, int texHeight) {
        this.location = ResourceLocation.fromNamespaceAndPath(namespace, "textures/gui/" + location + ".png");
        this.width = width;
        this.height = height;
        this.startX = startX;
        this.startY = startY;
        this.texWidth = texWidth;
        this.texHeight = texHeight;
    }

    @Override
    public ResourceLocation getLocation() {
        return location;
    }

    @OnlyIn(Dist.CLIENT)
    public void render(GuiGraphics graphics, int x, int y) {
        graphics.blit(location, x, y, startX, startY, width, height, texWidth, texHeight);
    }

    @OnlyIn(Dist.CLIENT)
    public void render(GuiGraphics graphics, int x, int y, Color c) {
        bind();
        UIRenderHelper.drawColoredTexture(graphics, c, x, y, startX, startY, width, height);
    }

    @Override
    public int getStartX() {
        return startX;
    }

    @Override
    public int getStartY() {
        return startY;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }
}
