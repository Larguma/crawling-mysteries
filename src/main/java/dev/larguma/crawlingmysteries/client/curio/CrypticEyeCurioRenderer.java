package dev.larguma.crawlingmysteries.client.curio;

import com.mojang.blaze3d.vertex.PoseStack;

import dev.larguma.crawlingmysteries.Config;
import dev.larguma.crawlingmysteries.client.CurioRenderers;
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

public class CrypticEyeCurioRenderer implements ICurioRenderer {

  @Override
  public <T extends LivingEntity, M extends EntityModel<T>> void render(ItemStack stack, SlotContext slotContext,
      PoseStack matrixStack, RenderLayerParent<T, M> renderLayerParent, MultiBufferSource renderTypeBuffer, int light,
      float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {

    if (slotContext.entity() instanceof AbstractClientPlayer player && Config.RENDER_TRINKETS.get()) {
      if (player.isInvisible()) {
        return;
      }

      matrixStack.pushPose();
      CurioRenderers.translateToFace(matrixStack, player, netHeadYaw, headPitch);
      // x: + is left, - is right
      // y: + is down, - is up
      // z: + is back, - is forward
      matrixStack.translate(-0.2f, -0.4f, 0.5f);
      Minecraft.getInstance().getItemRenderer()
          .renderStatic(stack, ItemDisplayContext.HEAD, light, OverlayTexture.NO_OVERLAY,
              matrixStack, renderTypeBuffer, slotContext.entity().level(), 0);
      matrixStack.popPose();
    }
  }
}
