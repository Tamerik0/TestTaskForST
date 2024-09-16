package org.necr0manth.task_st.init;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.necr0manth.task_st.blocks.LightningGenerator;
import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;

@SimplyRegister
public interface Blocks {
    @RegistryName("lightning_generator")
    Block LIGHTNING_GENERATOR_BLOCK = new LightningGenerator();
}
