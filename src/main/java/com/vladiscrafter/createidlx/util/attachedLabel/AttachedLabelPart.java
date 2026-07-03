package com.vladiscrafter.createidlx.util.attachedLabel;

public class AttachedLabelPart {
    public enum PlaceholderType { DOLLAR, BRACKETS, TRIM, TRIM_SHORT, TRIM_ALT, ESCAPED, DISABLED, ESCAPED_DISABLED, INVALID }

    public enum CaptureGroupOperation { REMOVE, EXTRACT, N_A }
    public enum CaptureGroupType { INTEGER, FRACTION, PERCENTAGE, N_A }

    public static Placeholder invalid = new Placeholder("", PlaceholderType.INVALID);

    public record Placeholder(String raw, PlaceholderType type) implements AttachedLabelBreakdownResult {
        public String getString() {
            return raw;
        }

        public String asDebugString() {
            return debug(raw, type.name());
        }

        public int length() {
            return raw.length();
        }

        public boolean isActive() {
            return type != PlaceholderType.ESCAPED && type != PlaceholderType.DISABLED
                    && type != PlaceholderType.ESCAPED_DISABLED && type != PlaceholderType.INVALID;
        }

        public boolean isEscaped() {
            return type == PlaceholderType.ESCAPED || type == PlaceholderType.ESCAPED_DISABLED;
        }

        public boolean isDisabled() {
            return type == PlaceholderType.DISABLED || type == PlaceholderType.ESCAPED_DISABLED;
        }
    }

    public record PlaceholderProperties(CaptureGroup leftGroup, CaptureGroup middleGroup, CaptureGroup rightGroup) {}

    public record CaptureGroup(String raw, CaptureGroupOperation operation, CaptureGroupType type) {}

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

    private static String debug(String raw, String type) {
        return String.format("[ %s ]< %s >", raw, type);
    }
}
