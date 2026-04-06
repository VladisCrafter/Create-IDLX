package com.vladiscrafter.createidlx.content.displayLink;

import com.simibubi.create.content.redstone.displayLink.DisplayLinkBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;

public class DisplayLinkBehaviour extends BlockEntityBehaviour {

    public static final BehaviourType<DisplayLinkBehaviour> TYPE = new BehaviourType<>();

    CompoundTag source;
    String sourceId;
    Component sourceName;
    String attachedLabel;
    CompoundTag sourceConfig;
    ResourceLocation targetDim;
    BlockPos targetPos;
    int targetLine;

    public DisplayLinkBehaviour(SmartBlockEntity be) {
        super(be);
    }

    public boolean copyToClipboard(CompoundTag tag, boolean includeLabel, boolean includeConfig, boolean includeTarget) {
        if (!includeLabel && !includeConfig && !includeTarget) return false;

        DisplayLinkBlockEntity displayLink = (DisplayLinkBlockEntity) this.blockEntity;
        CompoundTag displaySource = new CompoundTag();

        source = displayLink.getSourceConfig().copy();

        sourceId = source.contains("Id") ? source.getString("Id") : "";
        if (!sourceId.isEmpty()) displaySource.putString("SourceId", sourceId);

        sourceName = displayLink.activeSource != null ? displayLink.activeSource.getName() : null;
        if (sourceName != null && sourceName.getContents() instanceof TranslatableContents tc) displaySource.putString("SourceKey", tc.getKey());

        attachedLabel = source.contains("Label") ? source.getString("Label") : "";
        if (includeLabel) displaySource.putString("AttachedLabel", attachedLabel);

        sourceConfig = new CompoundTag();
        for (String setting : source.getAllKeys()) {
            if (!setting.equals("Id") && !setting.equals("Label")) sourceConfig.put(setting, source.get(setting).copy());
        }
        if (includeConfig && !sourceConfig.isEmpty()) displaySource.put("SourceConfig", sourceConfig.copy());

        targetDim = displayLink.getLevel().dimension().location();
        targetPos = displayLink.getTargetPosition();
        targetLine = displayLink.targetLine;

        if (includeTarget && targetDim != null && targetPos != null) {
            displaySource.putString("TargetDim", targetDim.toString());
            displaySource.put("TargetPos", NbtUtils.writeBlockPos(targetPos));
            displaySource.putInt("TargetLine", targetLine);
        }

        if (displaySource.isEmpty()) return false;

        tag.put("DisplaySource", displaySource);
        return true;
    }

    public boolean applyFromClipboard(CompoundTag displaySourceTag, boolean includeLabel, boolean includeConfig, boolean includeTarget) {
        if (!includeLabel && !includeConfig && !includeTarget) return false;
        if (displaySourceTag == null || displaySourceTag.isEmpty()) return false;

        DisplayLinkBlockEntity be = (DisplayLinkBlockEntity) this.blockEntity;

        String clipboardSourceId = displaySourceTag.getString("SourceId");
        CompoundTag currentSource = be.getSourceConfig().copy();
        String currentSourceId = currentSource.contains("Id") ? currentSource.getString("Id") : "";
        boolean sameSource = clipboardSourceId.equals(currentSourceId);

        if (includeLabel && displaySourceTag.contains("AttachedLabel")) currentSource.putString("Label", displaySourceTag.getString("AttachedLabel"));

        if (includeConfig && sameSource && displaySourceTag.contains("SourceConfig")) {
            CompoundTag clipboardConfig = displaySourceTag.getCompound("SourceConfig");
            for (String key : clipboardConfig.getAllKeys()) {
                if (!key.equals("Id") && !key.equals("Label")) currentSource.put(key, clipboardConfig.get(key).copy());
            }
        }

        if (includeTarget && displaySourceTag.contains("TargetDim")) {
            targetPos = NbtUtils.readBlockPos(displaySourceTag, "TargetPos").orElse(BlockPos.ZERO);
            targetLine = displaySourceTag.getInt("TargetLine");

            be.target(targetPos);
            be.targetLine = targetLine;
        }

        be.setSourceConfig(currentSource.copy());
        source = currentSource.copy();
        sourceId = source.contains("Id") ? source.getString("Id") : "";
        attachedLabel = source.contains("Label") ? source.getString("Label") : "";

        sourceConfig = new CompoundTag();
        for (String setting : source.getAllKeys()) {
            if (!setting.equals("Id") && !setting.equals("Label"))
                sourceConfig.put(setting, source.get(setting).copy());
        }

        be.setChanged();
        be.notifyUpdate();
        be.updateGatheredData();
        return true;
    }

    @Override
    public BehaviourType<?> getType() {
        return TYPE;
    }

    public CompoundTag getSource() {
        return source;
    }

    public void setSource(CompoundTag source) {
        this.source = (source != null) ? source.copy() : new CompoundTag();
    }
}
