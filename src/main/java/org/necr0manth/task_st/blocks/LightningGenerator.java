package org.necr0manth.task_st.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.CryingObsidianBlock;
import net.minecraft.world.level.block.LightningRodBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;
import org.necr0manth.task_st.blocks.blockentities.LightningGeneratorBlockentity;
import org.zeith.hammerlib.api.forge.BlockAPI;
public class LightningGenerator extends BaseEntityBlock {
    public LightningGenerator() {
        super(Properties.of().explosionResistance(800));
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new LightningGeneratorBlockentity(pos, state);
    }
    public RenderShape getRenderShape(BlockState p_60550_) {
        return RenderShape.MODEL;
    }
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType)
    {
        return BlockAPI.ticker(pLevel);
    }
}
