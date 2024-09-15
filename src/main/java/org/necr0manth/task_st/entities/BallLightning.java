package org.necr0manth.task_st.entities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.necr0manth.task_st.init.Entities;

public class BallLightning extends ThrowableProjectile {
    private static final EntityDataAccessor<Float> POWER = SynchedEntityData.defineId(BallLightning.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> THROWN = SynchedEntityData.defineId(BallLightning.class, EntityDataSerializers.BOOLEAN);
    public Vec3 targetPosition;
    public float power = 1;

    public BallLightning(EntityType<? extends ThrowableProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        setNoGravity(true);
    }

    public BallLightning(Level level, LivingEntity owner) {
        this(Entities.BALL_LIGHTNING_ENTITY_TYPE, level);
        setOwner(owner);
        setPos(owner.getEyePosition().add(owner.getLookAngle().scale(1.5f)).subtract(this.getBoundingBox().getCenter()));
    }

    public void tick() {
        if (targetPosition != null)
            setDeltaMovement(targetPosition.subtract(position()));
        super.tick();
        if (getPower() != power && !this.level().isClientSide) {
            setPower(power);
            refreshDimensions();
        }
        if (targetPosition != null)
            setDeltaMovement(targetPosition.subtract(position()));
    }

    public void setPower(float power) {
        this.power = power;
        this.entityData.set(POWER, power);
    }

    public float getPower() {
        return this.entityData.get(POWER);
    }

    @Override
    public EntityDimensions getDimensions(Pose pPose) {
        return new EntityDimensions(getPower(), getPower(), false);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(POWER, 0.0f);
        this.entityData.define(THROWN, false);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (POWER.equals(key)) {
            this.refreshDimensions();
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putFloat("power", this.entityData.get(POWER));
        compound.putBoolean("thrown", this.entityData.get(THROWN));
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        setPower(pCompound.getFloat("power"));
        entityData.set(THROWN, pCompound.getBoolean("thrown"));
    }

    @Override
    protected void onHitBlock(BlockHitResult hitResult) {
        super.onHitBlock(hitResult);
        onHit();
    }

    @Override
    protected void onHitEntity(EntityHitResult hitResult) {
        super.onHitEntity(hitResult);
        onHit();
    }

    private void onHit() {
        if (this.level() instanceof ServerLevel serverLevel && entityData.get(THROWN)) {
            LightningBolt lightningBolt = EntityType.LIGHTNING_BOLT.create(serverLevel);
            lightningBolt.moveTo(Vec3.atBottomCenterOf(this.blockPosition()));
            lightningBolt.setDamage(getPower() * 10);
            lightningBolt.setVisualOnly(false);
            level().explode(this, this.getX(), this.getY(0.0625D), this.getZ(), 4.0F * getPower(), Level.ExplosionInteraction.TNT);
            serverLevel.addFreshEntity(lightningBolt);
            this.remove(RemovalReason.DISCARDED);
        }
    }

    public void shoot(Vec3 dir) {
        targetPosition = null;
        entityData.set(THROWN, true);
        shoot(dir.x, dir.y, dir.z, (float) (2/(getPower()+0.5)), 0);
    }
}
