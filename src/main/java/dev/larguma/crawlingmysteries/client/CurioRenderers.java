package dev.larguma.crawlingmysteries.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import dev.larguma.crawlingmysteries.client.curio.CrypticEyeCurioRenderer;
import dev.larguma.crawlingmysteries.client.curio.EternalGuardianMaskCurioRenderer;
import dev.larguma.crawlingmysteries.client.curio.EternalGuardiansBandCurioRenderer;
import dev.larguma.crawlingmysteries.item.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

public class CurioRenderers {

  public static void register() {
    CuriosRendererRegistry.register(ModItems.CRYPTIC_EYE.get(), () -> new CrypticEyeCurioRenderer());
    CuriosRendererRegistry.register(ModItems.ETERNAL_GUARDIANS_BAND.get(),
        () -> new EternalGuardiansBandCurioRenderer());
    CuriosRendererRegistry.register(ModItems.ETERNAL_GUARDIAN_MASK.get(),
        () -> new EternalGuardianMaskCurioRenderer());
  }

  // pitch = xRot, yaw = yRot, roll = zRot

  /**
   * Translate to the center of the player's face
   */
  public static void translateToFace(final PoseStack matrixStack, AbstractClientPlayer player, float netHeadYaw,
      float headPitch) {

    HumanoidModel<LivingEntity> model = getPlayerModel(player);
    if (player.isSwimming() || player.isFallFlying()) {
      matrixStack.mulPose(Axis.ZP.rotationDegrees(model.head.zRot));
      matrixStack.mulPose(Axis.YP.rotationDegrees(netHeadYaw));
      matrixStack.mulPose(Axis.XP.rotationDegrees(-45.0F));
    } else {
      if (player.isCrouching() && !model.riding) {
        matrixStack.translate(0.0F, 0.25F, 0.0F);
      }
      matrixStack.mulPose(Axis.YP.rotationDegrees(netHeadYaw));
      matrixStack.mulPose(Axis.XP.rotationDegrees(headPitch));
    }
    matrixStack.translate(0.0F, -0.25F, -0.3F);
  }

  /**
   * Translate to the center of the player's right arm
   */
  public static void translateToRightArm(final PoseStack matrixStack, AbstractClientPlayer player) {

    HumanoidModel<LivingEntity> model = getPlayerModel(player);

    if (player.isCrouching() && !model.riding && !player.isSwimming()) {
      matrixStack.translate(0.0F, 0.2, 0.0F);
    }
    matrixStack.mulPose(Axis.YP.rotation(model.body.yRot));
    matrixStack.translate(-0.3125F, 0.15625F, 0.0F);
    matrixStack.mulPose(Axis.ZP.rotation(model.rightArm.zRot));
    matrixStack.mulPose(Axis.YP.rotation(model.rightArm.yRot));
    matrixStack.mulPose(Axis.XP.rotation(model.rightArm.xRot));
    matrixStack.translate(-0.0625F, 0.625F, 0.0F);
  }

  public static HumanoidModel<LivingEntity> getPlayerModel(LivingEntity livingEntity) {
    EntityRenderer<? super LivingEntity> render = Minecraft.getInstance().getEntityRenderDispatcher()
        .getRenderer(livingEntity);

    if (render instanceof LivingEntityRenderer) {
      @SuppressWarnings("unchecked")
      LivingEntityRenderer<LivingEntity, EntityModel<LivingEntity>> livingRenderer = (LivingEntityRenderer<LivingEntity, EntityModel<LivingEntity>>) render;
      EntityModel<LivingEntity> model = livingRenderer.getModel();

      if (model instanceof HumanoidModel) {
        return (HumanoidModel<LivingEntity>) model;
      }
    }
    return null;
  }
}
