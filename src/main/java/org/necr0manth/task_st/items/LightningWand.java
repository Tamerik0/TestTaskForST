package org.necr0manth.task_st.items;

import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.necr0manth.task_st.entities.BallLightning;
import org.necr0manth.task_st.init.CreativeTabs;
import org.necr0manth.task_st.init.Items;
import org.zeith.hammerlib.api.items.ITabItem;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LightningWand extends Item implements ITabItem {
    public LightningWand() {
        super(new Properties().stacksTo(1));
    }

    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
    }

    public static final int maxShortClickTime = 6;
    public static final int maxUseTime = 10000000;
    public static final float maxPower = 2;
    public static final float defaultPower = 0.7f;
    public static final float distanceToProjectile = 3;

    public List<BallLightning> getProjectiles(ItemStack stack, Level level) {
        var projectiles = new ArrayList<BallLightning>();
        if (level instanceof ServerLevel serverLevel) {
            var tag = stack.getOrCreateTag();
            List<UUID> uuids = new ArrayList<>();
            if (tag.contains("projectiles")) {
                ListTag projList = tag.getList("projectiles", Tag.TAG_INT_ARRAY);
                for (var uuidTag : projList) {
                    uuids.add(NbtUtils.loadUUID(uuidTag));
                }
            }
            for (var uuid : uuids) {
                var proj = (BallLightning) serverLevel.getEntity(uuid);
                if (proj != null)
                    projectiles.add(proj);
            }
        }
        return projectiles;
    }

    public void saveProjectiles(ItemStack stack, List<BallLightning> projectiles) {
        ListTag list = new ListTag();
        for (var proj : projectiles) {
            list.add(NbtUtils.createUUID(proj.getUUID()));
        }
        stack.getOrCreateTag().put("projectiles", list);
    }

    public boolean getLongUse(ItemStack itemStack) {
        return itemStack.getOrCreateTag().getBoolean("longUse");
    }

    public void setLongUse(ItemStack itemStack, boolean longUse) {
        itemStack.getOrCreateTag().putBoolean("longUse", longUse);
    }

    public void releaseUsing(ItemStack itemStack, Level level, LivingEntity entity, int t) {
        var timeUsed = maxUseTime - t;
        for (var proj : getProjectiles(itemStack, level))
            proj.shoot(entity.getLookAngle());
        saveProjectiles(itemStack, new ArrayList<>());
        setLongUse(itemStack, false);
        if (entity instanceof Player player) {
            if (timeUsed <= maxShortClickTime) {
                player.swing(player.getUsedItemHand());
                player.getCooldowns().addCooldown(Items.LIGHTNING_WAND, 20);
            } else {
                player.getCooldowns().addCooldown(Items.LIGHTNING_WAND, 20);
            }
        }
    }

    public void onUseTick(Level level, LivingEntity entity, ItemStack itemStack, int t) {
        super.onUseTick(level, entity, itemStack, t);
        var timeUsed = maxUseTime - t;
        if (level.isClientSide())
            return;
        var centerPos = entity.getEyePosition().add(entity.getLookAngle().scale(distanceToProjectile));
        var projectiles = getProjectiles(itemStack, level);
        if (entity instanceof Player player) {
            if (player.isShiftKeyDown()) {
                int maxProj = 5;
                float speed = 0.25f;
                float s = Math.min(maxProj * defaultPower, (float) timeUsed / 20 * speed);
                int i = 0;
                while (s > 0) {
                    float p;
                    if (s >= defaultPower) {
                        p = defaultPower;
                        s -= defaultPower;
                    } else {
                        p = s;
                        s = 0;
                    }
                    BallLightning proj;
                    if (i < projectiles.size())
                        proj = projectiles.get(i);
                    else {
                        proj = new BallLightning(level, player);
                        level.addFreshEntity(proj);
                        projectiles.add(proj);
                    }
                    proj.power = p;
                    i++;
                }
                if (projectiles.size() == 1) {
                    projectiles.get(0).targetPosition = projectiles.get(0).position().add(centerPos.subtract(projectiles.get(0).getBoundingBox().getCenter()));
                    projectiles.get(0).setDeltaMovement(projectiles.get(0).targetPosition.subtract(projectiles.get(0).position()));
                } else {
                    var radius = 0.7;
                    Quaternionf r;
                    if (!player.getLookAngle().normalize().equals(new Vec3(0, 0.8, 0)))
                        r = new Quaternionf().lookAlong((float) player.getLookAngle().x, (float) player.getLookAngle().y, (float) player.getLookAngle().z, 0, 1, 0);
                    else
                        r = new Quaternionf();
                    for (int j = 0; j < projectiles.size(); j++) {
                        var angle = j * 2 * Math.PI / projectiles.size() + (float) timeUsed / 6;
                        var pos = r.transformInverse(new Vector3d(radius * Math.sin(angle), radius * Math.cos(angle), -distanceToProjectile)).add(player.getX(), player.getEyeY(), player.getZ());
                        projectiles.get(j).targetPosition = new Vec3(pos.x, pos.y + 1, pos.z);
                        projectiles.get(j).setDeltaMovement(projectiles.get(j).targetPosition.subtract(projectiles.get(j).position()));
                    }
                }
            } else {
                if (projectiles.size() > 1) {
                    for (int i = 1; i < projectiles.size(); i++) {
                        projectiles.get(i).remove(Entity.RemovalReason.DISCARDED);
                    }
                    projectiles = List.of(projectiles.get(0));
                }
                if (projectiles.size() == 1) {
                    projectiles.get(0).targetPosition = projectiles.get(0).position().add(centerPos.subtract(projectiles.get(0).getBoundingBox().getCenter()));
                    projectiles.get(0).setDeltaMovement(projectiles.get(0).targetPosition.subtract(projectiles.get(0).position()));
                    if (timeUsed > maxShortClickTime) {
                        setLongUse(itemStack, true);
                        projectiles.get(0).power = Math.min(defaultPower + (float) (timeUsed - maxShortClickTime) / 20, maxPower);
                    } else {
                        projectiles.get(0).power = defaultPower;
                    }
                }
            }
            saveProjectiles(itemStack, projectiles);
        }

    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide())
            return InteractionResultHolder.pass(player.getItemInHand(hand));
        setLongUse(player.getItemInHand(hand), false);
        var proj = new BallLightning(level, player);
        proj.setPower(0.01f);
        level.addFreshEntity(proj);
        saveProjectiles(player.getItemInHand(hand), List.of(proj));
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(player.getItemInHand(hand));
    }

    @Override
    public CreativeModeTab getItemCategory() {
        return CreativeTabs.MOD_TAB.tab();
    }

    public int getUseDuration(ItemStack pStack) {
        return maxUseTime;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack pStack) {
        return getLongUse(pStack) ? UseAnim.BOW : UseAnim.NONE;
    }
}
