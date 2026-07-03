package com.vladiscrafter.createidlx.util.attachedLabel;

import static com.vladiscrafter.createidlx.util.attachedLabel.AttachedLabelPart.*;

public sealed interface AttachedLabelBreakdownResult permits Placeholder, PlainPart {
    String getString();
    String asDebugString();
    int length();
}
