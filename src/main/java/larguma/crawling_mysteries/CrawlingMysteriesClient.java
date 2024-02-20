package larguma.crawling_mysteries;

import dev.emi.trinkets.api.client.TrinketRenderer;
import dev.emi.trinkets.api.client.TrinketRendererRegistry;
import larguma.crawling_mysteries.item.ModItems;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;

public class CrawlingMysteriesClient implements ClientModInitializer {

  @SuppressWarnings("unchecked")
  @Override
  public void onInitializeClient() {
    if (CrawlingMysteries.CONFIG.enableCrypticEyeRender())
      TrinketRendererRegistry.registerRenderer(ModItems.CRYPTIC_EYE,
          (stack, slotReference, contextModel, matrices, vertexConsumers, light, entity, limbAngle, limbDistance,
              tickDelta, animationProgress, headYaw, headPitch) -> {
            if (entity instanceof AbstractClientPlayerEntity player) {
              TrinketRenderer.translateToFace(matrices,
                  (PlayerEntityModel<AbstractClientPlayerEntity>) contextModel, player, headYaw, headPitch);
              // matrices.scale(x, y, z);
              // x: + gauche; - droite
              // y: + bas; - haut
              // z: + derrière; - devant
              matrices.translate(-0.2F, -0.4F, 0.5F);
              MinecraftClient.getInstance().getItemRenderer()
                  .renderItem(stack, ModelTransformationMode.HEAD, light, OverlayTexture.DEFAULT_UV, matrices,
                      vertexConsumers, null, 0);
            }
          });
  }

}
