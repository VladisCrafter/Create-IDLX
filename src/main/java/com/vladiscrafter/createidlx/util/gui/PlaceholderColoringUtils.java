package com.vladiscrafter.createidlx.util.gui;

import com.vladiscrafter.createidlx.config.CIDLXClient;
import com.vladiscrafter.createidlx.config.CIDLXConfigs;
import com.vladiscrafter.createidlx.util.attachedLabel.AttachedLabelPart;

public class PlaceholderColoringUtils {
    public static int getHighlightColor(AttachedLabelPart.Placeholder placeholder) {
        return getHighlightColor(placeholder.type(), placeholder.state());
    }

    public static int getHighlightColor(AttachedLabelPart.PlaceholderType placeholderType, AttachedLabelPart.PlaceholderState placeholderState) {
        CIDLXClient cfg = CIDLXConfigs.client;

        int alpha = cfg.placeholdersColorsAlpha.get() << 24;
        int invisible = 0x00FFFFFF;

        if (placeholderState != AttachedLabelPart.PlaceholderState.ACTIVE) {
            return cfg.colorPlaceholders.get() ? switch (placeholderState) {
                case ESCAPED -> cfg.colorEscapedPlaceholders.get()
                        ? cfg.escapedPlaceholderColor.get() | alpha : invisible;
                case DISABLED -> cfg.colorDisabledPlaceholders.get()
                        ? cfg.disabledPlaceholderColor.get() | alpha : invisible;
                /*case ESCAPED_DISABLED -> cfg.colorEscapedDisabledPlaceholders.get()
                        ? cfg.escapedDisabledPlaceholderColor.get() | alpha : invisible;
                case INCOMPLETE -> cfg.colorIncompletePlaceholders.get()
                        ? cfg.incompletePlaceholderColor.get() | alpha : invisible;
                case UNOPTIMIZED -> cfg.colorUnoptimizedPlaceholders.get()
                        ? cfg.unoptimizedPlaceholderColor.get() | alpha : invisible;
                case SYNTACTICALLY_ERRORSOME -> cfg.colorSyntacticallyErrorsomePlaceholders.get()
                        ? cfg.syntacticallyErrorsomePlaceholderColor.get() | alpha : invisible;*/
                case null, default -> cfg.colorInvalidPlaceholders.get()
                        ? cfg.invalidPlaceholderColor.get() | alpha : invisible;
            } : invisible;
        }

        return cfg.colorPlaceholders.get() ? switch (placeholderType) {
            case DOLLAR -> cfg.colorDollarPlaceholders.get()
                    ? cfg.dollarPlaceholderColor.get() | alpha : invisible;
            case BRACKETS -> cfg.colorBracketsPlaceholders.get()
                    ? cfg.bracketsPlaceholderColor.get() | alpha : invisible;
            case TRIM -> cfg.colorOriginalTrimmingPlaceholders.get()
                    ? cfg.originalTrimmingPlaceholderColor.get() | alpha : invisible;
            case TRIM_SHORT -> cfg.colorShortenedTrimmingPlaceholders.get()
                    ? cfg.shortenedTrimmingPlaceholderColor.get() | alpha : invisible;
            case TRIM_ALT -> cfg.colorAlternativeTrimmingPlaceholders.get()
                    ? cfg.alternativeTrimmingPlaceholderColor.get() | alpha : invisible;
            case null, default -> cfg.colorInvalidPlaceholders.get()
                    ? cfg.invalidPlaceholderColor.get() | alpha : invisible;
        } : invisible;
    }
}
