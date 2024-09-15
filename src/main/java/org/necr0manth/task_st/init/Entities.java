package org.necr0manth.task_st.init;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import org.necr0manth.task_st.TaskStMod;
import org.necr0manth.task_st.entities.BallLightning;
import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;

@SimplyRegister
public class Entities {
    @RegistryName("ball_lightning")
    public static final EntityType<BallLightning> BALL_LIGHTNING_ENTITY_TYPE = EntityType.Builder.<BallLightning>of(BallLightning::new, MobCategory.MISC)
            .fireImmune()
            .build(TaskStMod.id("ball_lightning").toString());
}
