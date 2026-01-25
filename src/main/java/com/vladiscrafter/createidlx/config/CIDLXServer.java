package com.vladiscrafter.createidlx.config;

public class CIDLXServer extends CIDLXConfigBase {

    public final ConfigBool enableDollarSpecifier = b(true, "enableDollarSignPlaceholder", "Treat $ (dollar sign) as a placeholder for the Attached Label.");
    public final ConfigBool enableBracketsSpecifier = b(true, "enableCurlyBracketsPlaceholder", "Treat {} (curly brackets) as a placeholder for the Attached Label.");

    @Override public String getName() { return "server"; }
}
