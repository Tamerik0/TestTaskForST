package org.necr0manth.task_st.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.*;
import org.necr0manth.task_st.TaskStMod;
import org.necr0manth.task_st.entities.BallLightning;
import org.necr0manth.task_st.init.Entities;

import java.awt.*;
import java.lang.Math;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = TaskStMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class BallLightningEntityRenderer extends EntityRenderer<BallLightning> {
    static class LightningRing {
        Quaternionf rotation;
        float radius;
        float minRadius;
        float maxRadius;
        int segmentCount;
        int subSegmentCount;
        Color color;
        Color color0;
        Color color1;
        float seed;
        float rotationSpeed;
        float pulseSpeed;
        float d;
        long lastTimerRotated;

        void render(PoseStack poseStack, MultiBufferSource buffer, float deltaTime) {
            poseStack.pushPose();
            poseStack.mulPose(rotation);
            radius = (float) ((maxRadius + minRadius) / 2 + ((maxRadius - minRadius) / 2) * Math.sin(d));
            color = new Color((int) (color0.getRed() + (Math.sin(d) + 1) / 2 * (color1.getRed() - color0.getRed())),
                    (int) (color0.getGreen() + (Math.sin(d) + 1) / 2 * (color1.getGreen() - color0.getGreen())),
                    (int) (color0.getBlue() + (Math.sin(d) + 1) / 2 * (color1.getBlue() - color0.getBlue())));
            for (int i = 0; i < segmentCount; i++) {
                var angle = i * 2 * Math.PI / segmentCount;
                var angle1 = (i + 1) * 2 * Math.PI / segmentCount;
                EffectLib.renderLightningP2PRotate(poseStack, buffer, new Vec3(Math.cos(angle), Math.sin(angle), 0).scale(radius), new Vec3(Math.cos(angle1), Math.sin(angle1), 0).scale(radius), subSegmentCount, System.currentTimeMillis(), 12, 2.5f, true, 0, color.getRGB());
            }
            poseStack.popPose();
            if (System.currentTimeMillis() - lastTimerRotated >= 50) {
                rotation.mul(new Quaternionf().integrate(0.02f * rotationSpeed, (1 + seed % 3), (1 + (seed + 1) % 3), (1 + (seed + 2) % 3)));
                lastTimerRotated = System.currentTimeMillis();
            }
            d += pulseSpeed * deltaTime;
            seed += deltaTime * (new Random(System.currentTimeMillis()).nextFloat() - 0.5f);
        }
    }

    long lastTimerRendered;
    List<LightningRing> rings = new ArrayList<>();

    public BallLightningEntityRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
        int ringCount = 5;
        for (int i = 0; i < ringCount; i++) {
            var ring = new LightningRing();
            ring.rotation = Axis.YP.rotationDegrees((float) (i * 360) / ringCount);
            ring.minRadius = 0.4f;
            ring.maxRadius = 0.6f;
            ring.d = (float) (i * 2 * Math.PI / ringCount);
            ring.segmentCount = 9;
            ring.subSegmentCount = 5;
            ring.color0 = new Color(146, 53, 198);
            ring.color1 = new Color(101, 4, 123);
            ring.seed = i;
            ring.rotationSpeed = 2;
            ring.pulseSpeed = 5;
            rings.add(ring);
        }
    }

    @Override
    public void render(BallLightning pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        float power = pEntity.getPower();
        float deltaTime = Math.min((float) (System.currentTimeMillis() - lastTimerRendered) / 1000, 10);
        lastTimerRendered = System.currentTimeMillis();
        pPoseStack.pushPose();
        pPoseStack.translate(0, 0.5 * power, 0);
        pPoseStack.scale(power, power, power);
        for (LightningRing ring : rings) {
            ring.render(pPoseStack, pBuffer, deltaTime);
        }
        var random = new Random();
        var lightningsCount = random.nextInt(5, 10);

        for (int i = 0; i < lightningsCount; i++) {
            var p1 = new Vec3(random.nextDouble(-1, 1), random.nextDouble(-1, 1), random.nextDouble(-1, 1));
            p1 = p1.normalize().scale(random.nextDouble(0, 0.6));

            var p2 = new Vec3(random.nextDouble(-1, 1), random.nextDouble(-1, 1), random.nextDouble(-1, 1));
            p2 = p2.normalize().scale(random.nextDouble(0.4, 0.6));
            var color = new Color(250, 207, 114);
            color = new Color(color.getRed() + random.nextInt(-30, 255-color.getRed()), color.getBlue() + random.nextInt(-30, 30), color.getGreen() + random.nextInt(-30, 30));
            EffectLib.renderLightningP2PRotate(pPoseStack, pBuffer, p1, p2, random.nextInt(2, 5), random.nextInt(), 10, 7, true, 0, color.getRGB());
        }
        pPoseStack.popPose();
        super.render(pEntity, pEntityYaw, pPartialTick, pPoseStack, pBuffer, pPackedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(BallLightning pEntity) {
        return null;
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(Entities.BALL_LIGHTNING_ENTITY_TYPE, BallLightningEntityRenderer::new);
    }
}
