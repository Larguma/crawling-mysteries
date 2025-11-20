package dev.larguma.crawlingmysteries.item.client.curio.renderer;

import org.joml.Quaternionf;

import com.mojang.blaze3d.vertex.PoseStack;

import dev.larguma.crawlingmysteries.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

public class ObserverRenderer implements ICurioRenderer {

  @Override
  public <T extends LivingEntity, M extends EntityModel<T>> void render(ItemStack stack, SlotContext slotContext,
      PoseStack poseStack, RenderLayerParent<T, M> renderLayerParent, MultiBufferSource renderTypeBuffer, int light,
      float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
    if (slotContext.entity() instanceof AbstractClientPlayer player && Config.RENDER_TRINKETS.get()) {
      if (player.isInvisible()) {
        return;
      }

      poseStack.pushPose();
      poseStack.scale(0.5f, 0.5f, 0.5f);
      poseStack.mulPose((new Quaternionf()).rotationY((float) Math.toRadians(180)));
      translateToFace(poseStack, player, netHeadYaw, headPitch);
      // x: + is left, - is right
      // y: + is down, - is up
      // z: + is back, - is forward
      poseStack.translate(-1.0f, -0.7f, -0.4f);
      Minecraft.getInstance().getItemRenderer()
          .renderStatic(stack, ItemDisplayContext.NONE, light, OverlayTexture.NO_OVERLAY,
              poseStack, renderTypeBuffer, slotContext.entity().level(), 0);
      poseStack.popPose();
    }
  }

  static void translateToFace(PoseStack poseStack, AbstractClientPlayer player, float netHeadYaw, float headPitch) {
    if (player.isSwimming() || player.isFallFlying()) {
      // poseStack.mulPose(new Quaternionf()).rotationZ(-model.head.roll));
      poseStack.mulPose(new Quaternionf().rotationY((float) Math.toRadians(netHeadYaw)));
      poseStack.mulPose(new Quaternionf().rotationX((float) Math.toRadians(-45.0F)));
    } else {
      if (player.isCrouching() && !player.isPassenger()) {
        poseStack.translate(0.0F, 0.25F, 0.0F);
      }
      poseStack.mulPose(new Quaternionf().rotationY((float) Math.toRadians(netHeadYaw)));
      poseStack.mulPose(new Quaternionf().rotationX((float) Math.toRadians(headPitch)));

    }
    poseStack.translate(0.0F, -1.25F, -0.3F);
  }

}
