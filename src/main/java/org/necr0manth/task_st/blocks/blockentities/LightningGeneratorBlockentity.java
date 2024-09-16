package org.necr0manth.task_st.blocks.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.necr0manth.task_st.TaskStMod;
import org.necr0manth.task_st.init.BlockEntities;
import org.zeith.hammerlib.api.io.IAutoNBTSerializable;
import org.zeith.hammerlib.api.io.NBTSerializable;
import org.zeith.hammerlib.tiles.TileSyncableTickable;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mod.EventBusSubscriber
public class LightningGeneratorBlockentity extends TileSyncableTickable {
    static final Set<LightningGeneratorBlockentity> loadedInstances = new HashSet<>();
    public static final float maxDistance = 8;
    public static final int generatedEnergy = 16000;
    public static final int maxStoredEnergy = 16000;
    static class GeneratorEnergyStorage extends EnergyStorage{
        public GeneratorEnergyStorage(){
            super(maxStoredEnergy, 0, Integer.MAX_VALUE, 0);
        }
        public void generateEnergy(int energy){
            this.energy = Math.min(this.energy + energy, this.getMaxEnergyStored());
        }
    }
    private GeneratorEnergyStorage energyStorage = new GeneratorEnergyStorage();

    public LightningGeneratorBlockentity(BlockPos pos, BlockState state) {
        super(BlockEntities.LIGHTNING_GENERATOR_BLOCKENTITY_BLOCK_ENTITY_TYPE, pos, state);
    }

    @Override
    public void onLoad() {
        if(!getLevel().isClientSide)
            loadedInstances.add(this);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        loadedInstances.remove(this);
    }

    public void onChunkUnloaded() {
        super.onChunkUnloaded();
        loadedInstances.remove(this);
    }
    @Override
    public void saveAdditional(CompoundTag nbt)
    {
        super.saveAdditional(nbt);
        nbt.put("energyStored", energyStorage.serializeNBT());
    }

    @Override
    public void load(CompoundTag nbt)
    {
        super.load(nbt);
        if(nbt.contains("energyStored", CompoundTag.TAG_INT))
            energyStorage.deserializeNBT(nbt.get("energyStored"));
    }
    public void serverTick() {
        for (var dir : Direction.values()) {
            BlockEntity neighbor = level.getBlockEntity(worldPosition.relative(dir));
            if (neighbor != null) {
                neighbor.getCapability(ForgeCapabilities.ENERGY, dir.getOpposite()).ifPresent(neighborEnergy -> {
                    int energyAccepted = neighborEnergy.receiveEnergy(energyStorage.getEnergyStored(), false);
                    energyStorage.extractEnergy(energyAccepted, false);
                    sync();
                });
            }
        }
    }

    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinLevelEvent event) {
        if(event.getLevel().isClientSide)
            return;
        if (event.getEntity() instanceof LightningBolt lightning) {
            for (var i : loadedInstances) {
                i.handleLightning(lightning);
            }
        }
    }

    public void handleLightning(LightningBolt lightning) {
        var distance = lightning.position().distanceTo(getBlockPos().getCenter());
        if (distance > maxDistance)
            return;
        var energy = (int) (generatedEnergy * (1 - distance / maxDistance));
        energyStorage.generateEnergy(energy);
        sync();
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
        if (capability == ForgeCapabilities.ENERGY)
            return LazyOptional.of(() -> energyStorage).cast();
        return super.getCapability(capability, facing);
    }
}
