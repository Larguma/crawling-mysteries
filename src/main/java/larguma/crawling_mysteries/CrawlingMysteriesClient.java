package larguma.crawling_mysteries;

import dev.emi.trinkets.api.client.TrinketRenderer;
import dev.emi.trinkets.api.client.TrinketRendererRegistry;
import larguma.crawling_mysteries.block.entity.ModBlockEntities;
import larguma.crawling_mysteries.block.entity.renderer.TombstoneBlockEntityRenderer;
import larguma.crawling_mysteries.entity.ModEntities;
import larguma.crawling_mysteries.entity.client.EternalGuardianRenderer;
import larguma.crawling_mysteries.item.ModItems;
import larguma.crawling_mysteries.particle.ModParticles;
import larguma.crawling_mysteries.particle.custom.EternalFireParticle;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;

public class CrawlingMysteriesClient implements ClientModInitializer {

  @SuppressWarnings("unchecked")
  @Override
  public void onInitializeClient() {
    BlockEntityRendererFactories.register(ModBlockEntities.TOMBSTONE_BLOCK_ENTITY, TombstoneBlockEntityRenderer::new);

    EntityRendererRegistry.register(ModEntities.ETERNAL_GUARDIAN, EternalGuardianRenderer::new);

    ParticleFactoryRegistry.getInstance().register(ModParticles.ETERNAL_FIRE_PARTICLE, EternalFireParticle.Factory::new);

    // Trinkets
    if (CrawlingMysteries.CONFIG.enableTrinketsRender()) {
      // Cryptic Eye
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
              matrices.translate(-0.2f, -0.4f, 0.5f);
              MinecraftClient.getInstance().getItemRenderer()
                  .renderItem(stack, ModelTransformationMode.HEAD, light, OverlayTexture.DEFAULT_UV, matrices,
                      vertexConsumers, null, 0);
            }
          });

      // Eternal Guardian's Band
      TrinketRendererRegistry.registerRenderer(ModItems.ETERNAL_GUARDIANS_BAND,
          (stack, slotReference, contextModel, matrices, vertexConsumers, light, entity, limbAngle, limbDistance,
              tickDelta, animationProgress, headYaw, headPitch) -> {
            if (entity instanceof AbstractClientPlayerEntity player) {
              TrinketRenderer.translateToRightArm(matrices,
                  (PlayerEntityModel<AbstractClientPlayerEntity>) contextModel, player);
              matrices.translate(0f, -0.75f, 0f);
              MinecraftClient.getInstance().getItemRenderer()
                  .renderItem(stack, ModelTransformationMode.HEAD, light, OverlayTexture.DEFAULT_UV, matrices,
                      vertexConsumers, null, 0);
            }
          });
    }
  }
}
