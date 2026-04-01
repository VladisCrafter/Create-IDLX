package com.vladiscrafter.createidlx.content;

import com.simibubi.create.content.equipment.clipboard.ClipboardCloneable;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.vladiscrafter.createidlx.CreateIDLX;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class DisplayLinkBehaviour extends BlockEntityBehaviour implements ClipboardCloneable {

    public static final BehaviourType<DisplayLinkBehaviour> TYPE = new BehaviourType<>();

    CompoundTag source;
    String sourceId;
    String attachedLabel;
    CompoundTag sourceConfig;

    public DisplayLinkBehaviour(SmartBlockEntity be) {
        super(be);
        source = new CompoundTag();
        sourceId = "";
        attachedLabel = "";
        sourceConfig = new CompoundTag();
    }

    @Override
    public void write(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(nbt, registries, clientPacket);
        nbt.put("Source", source.copy());
    }

    @Override
    public void read(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(nbt, registries, clientPacket);
        source = nbt.contains("Source") ? nbt.getCompound("Source").copy() : new CompoundTag();
        sourceId = source.contains("Id") ? source.getString("Id") : "";
        attachedLabel = source.contains("Label") ? source.getString("Label") : "";
        sourceConfig = new CompoundTag();
        for (String setting : source.getAllKeys()) {
            if (!setting.equals("Id") && !setting.equals("Label")) sourceConfig.put(setting, source.get(setting).copy());
        }
    }

    @Override
    public String getClipboardKey() {
        return "DisplaySource";
    }

    @Override
    public boolean writeToClipboard(HolderLookup.@NotNull Provider registries, CompoundTag tag, Direction side) {
        DisplayLinkBlockEntity be = (DisplayLinkBlockEntity) this.blockEntity;
        source = be.getSourceConfig().copy();
        sourceId = source.contains("Id") ? source.getString("Id") : "";
        attachedLabel = source.contains("Label") ? source.getString("Label") : "";

        sourceConfig = new CompoundTag();
        for (String setting : source.getAllKeys()) {
            if (!setting.equals("Id") && !setting.equals("Label")) sourceConfig.put(setting, source.get(setting));
        }

        tag.putString("SourceId", source.getString("Id"));
        tag.putString("AttachedLabel", source.getString("Label"));
        tag.put("SourceConfig", sourceConfig.copy());

        CreateIDLX.LOGGER.info("SourceId: {} | Label: {} | SourceConfig: {}", source.getString("Id"), source.getString("Label"), sourceConfig.copy());
        return true;
    }

    @Override
    public boolean readFromClipboard(HolderLookup.@NotNull Provider registries, CompoundTag tag, Player player, Direction side, boolean simulate) {
        if (!tag.contains("SourceId")) return false;
        if (simulate) return true;

        DisplayLinkBlockEntity be = (DisplayLinkBlockEntity) this.blockEntity;

        String localSourceId = tag.getString("SourceId");
        String localAttachedLabel = tag.contains("AttachedLabel") ? tag.getString("AttachedLabel") : "";
        CompoundTag localSourceConfig = tag.contains("SourceConfig") ? tag.getCompound("SourceConfig") : new CompoundTag();

        CompoundTag currentSource = be.getSourceConfig().copy();
        String currentSourceId = currentSource.contains("Id") ? currentSource.getString("Id") : "";

        if (localSourceId.equals(currentSourceId)) {
            CompoundTag newSource = new CompoundTag();

            if (!currentSourceId.isEmpty()) newSource.putString("Id", currentSourceId);
            newSource.putString("Label", localAttachedLabel);

            for (String setting : localSourceConfig.getAllKeys()) {
                if (!setting.equals("Id") && !setting.equals("Label")) newSource.put(setting, localSourceConfig.get(setting).copy());
            }

            currentSource = newSource;
            CreateIDLX.LOGGER.info("Ё Pasting display source data");
        } else { if (currentSource.contains("Label")) currentSource.putString("Label", localAttachedLabel); }

        be.setSourceConfig(currentSource.copy());

        source = currentSource.copy();
        sourceId = source.contains("Id") ? source.getString("Id") : "";
        attachedLabel = source.contains("Label") ? source.getString("Label") : "";
        sourceConfig = new CompoundTag();
        for (String setting : source.getAllKeys()) {
            if (!setting.equals("Id") && !setting.equals("Label")) sourceConfig.put(setting, source.get(setting).copy());
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
