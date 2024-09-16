package org.necr0manth.task_st.init;

import com.mojang.datafixers.types.Type;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.necr0manth.task_st.TaskStMod;
import org.necr0manth.task_st.blocks.blockentities.LightningGeneratorBlockentity;
import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;

import java.util.Set;

@SimplyRegister
public interface BlockEntities {
    @RegistryName("lightning_generator_blockentity")
    BlockEntityType<LightningGeneratorBlockentity> LIGHTNING_GENERATOR_BLOCKENTITY_BLOCK_ENTITY_TYPE = BlockEntityType.Builder.of(LightningGeneratorBlockentity::new, Blocks.LIGHTNING_GENERATOR_BLOCK).build(null);
}
