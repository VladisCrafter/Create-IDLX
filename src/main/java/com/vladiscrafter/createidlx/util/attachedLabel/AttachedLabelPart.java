package com.vladiscrafter.createidlx.util.attachedLabel;

public class AttachedLabelPart {
    public enum PlaceholderType { DOLLAR, BRACKETS, TRIM, TRIM_SHORT, TRIM_ALT, ESCAPED, DISABLED, ESCAPED_DISABLED, INVALID }

    public enum CaptureGroupOperation { CUT, PRESERVE, NONE }
    public enum CaptureGroupRounding { CEIL, FLOOR, AUTO }

    public static CaptureGroup invalidCG = new CaptureGroup(0, CaptureGroupOperation.NONE, CaptureGroupRounding.AUTO);

    public static PlaceholderProperties invalidPr = new PlaceholderProperties(invalidCG, CaptureGroupOperation.NONE, invalidCG);

    public static Placeholder invalid = new Placeholder("", PlaceholderType.INVALID, invalidPr);

    public record Placeholder(String raw, PlaceholderType type, PlaceholderProperties properties) implements AttachedLabelBreakdownResult {
        public String getString() {
            return raw;
        }

        public String asDebugString() {
            return debug(raw, type.name()) + "\n    - " + properties.asDebugString();
        }

        public int length() {
            return raw.length();
        }

        public boolean isType(PlaceholderType comparable) {
            return type == comparable;
        }

        public boolean isActive() {
            return type != PlaceholderType.ESCAPED && type != PlaceholderType.DISABLED
                    && type != PlaceholderType.ESCAPED_DISABLED && type != PlaceholderType.INVALID;
        }

        public boolean isPrimitive() {
            return type == PlaceholderType.DOLLAR || type == PlaceholderType.BRACKETS;
        }

        public boolean isTrimming() {
            return type == PlaceholderType.TRIM || type == PlaceholderType.TRIM_SHORT || type == PlaceholderType.TRIM_ALT;
        }

        public boolean isEscaped() {
            return type == PlaceholderType.ESCAPED || type == PlaceholderType.ESCAPED_DISABLED;
        }

        public boolean isDisabled() {
            return type == PlaceholderType.DISABLED || type == PlaceholderType.ESCAPED_DISABLED;
        }
    }

    public record PlaceholderProperties(CaptureGroup leftGroup, CaptureGroupOperation middleGroupOperation, CaptureGroup rightGroup) {
        public PlaceholderProperties(CaptureGroup leftGroup, CaptureGroup rightGroup) {
            this(leftGroup, CaptureGroupOperation.NONE, rightGroup);
        }

        public String asDebugString() {
            return String.format("{\n        LG = %s\n        MGO = %s\n        RG = %s\n      }",
                    leftGroup.asDebugString(), middleGroupOperation, rightGroup.asDebugString());
        }
    }

    public record CaptureGroup(Number value, CaptureGroupOperation operation, CaptureGroupRounding rounding) {
        public CaptureGroup(Number value, CaptureGroupOperation operation) {
            this(value, operation, CaptureGroupRounding.AUTO);
        }

        public String asDebugString() {
            return String.format("{ V = %s | O = %s | R = %s }", value.toString(), operation, rounding);
        }
    }

    public record PlainPart(String raw) implements AttachedLabelBreakdownResult {
        public String getString() {
            return raw;
        }

        public String asDebugString() {
            return debug(raw, "PLAIN");
        }

        public int length() {
            return raw.length();
        }
    }

    public record ProcessedInfoPart(String raw) implements AttachedLabelBreakdownResult {
        public String getString() {
            return raw;
        }

        public String asDebugString() {
            return debug(raw, "PROCESSED");
        }

        public int length() {
            return raw.length();
        }
    }

    private static String debug(String raw, String type) {
        return String.format("[ %s ]\n  - < %s >", raw, type);
    }
}
