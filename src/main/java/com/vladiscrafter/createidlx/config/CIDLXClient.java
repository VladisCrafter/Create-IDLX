package com.vladiscrafter.createidlx.config;

public class CIDLXClient extends CIDLXConfigBase {

    public final ConfigBool enablePlaceholdersGuideButton = b(true, "enableGuideButtons", "Show the hover-on Placeholders & String Slicing Usage Guides tooltip buttons in Display Link interface.");

    @Override public String getName() { return "client"; }
}
