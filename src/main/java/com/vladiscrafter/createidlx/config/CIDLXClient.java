package com.vladiscrafter.createidlx.config;

public class CIDLXClient extends CIDLXConfigBase {

    public final ConfigBool enablePlaceholdersGuideButton = b(true, "Enable Guide Buttons", "Show the hover-on Placeholders & String Slicing Usage Guides tooltip buttons in Display Link interface.");

    @Override public String getName() { return "client"; }
}
