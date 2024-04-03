package larguma.crawling_mysteries;

import dev.emi.trinkets.api.client.TrinketRenderer;
import dev.emi.trinkets.api.client.TrinketRendererRegistry;
import larguma.crawling_mysteries.block.entity.ModBlockEntities;
import larguma.crawling_mysteries.block.entity.renderer.TombstoneBlockEntityRenderer;
import larguma.crawling_mysteries.entity.ModEntities;
import larguma.crawling_mysteries.entity.client.EternalGuardianRenderer;
import larguma.crawling_mysteries.event.KeyInputHandler;
import larguma.crawling_mysteries.item.ModItems;
import larguma.crawling_mysteries.networking.ModMessages;
import larguma.crawling_mysteries.particle.ModParticles;
import larguma.crawling_mysteries.particle.custom.EternalFireParticle;
import larguma.crawling_mysteries.screen.ModScreenHandler;
import larguma.crawling_mysteries.screen.client.SpellSelectMenuScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.util.math.RotationAxis;

@Environment(EnvType.CLIENT)
public class CrawlingMysteriesClient implements ClientModInitializer {

  @SuppressWarnings("unchecked")
  @Override
  public void onInitializeClient() {
    BlockEntityRendererFactories.register(ModBlockEntities.TOMBSTONE_BLOCK_ENTITY, TombstoneBlockEntityRenderer::new);
    EntityRendererRegistry.register(ModEntities.ETERNAL_GUARDIAN, EternalGuardianRenderer::new);
    KeyInputHandler.register();
    ModMessages.Client.init();
    ParticleFactoryRegistry.getInstance().register(ModParticles.ETERNAL_FIRE_PARTICLE,
        EternalFireParticle.Factory::new);
    HandledScreens.register(ModScreenHandler.SPELL_SELECT_MENU_HANDLER_TYPE, SpellSelectMenuScreen::new);

    // Trinkets
    if (CrawlingMysteries.CONFIG.enableTrinketsRender()) {
      TrinketRendererRegistry.registerRenderer(ModItems.CRYPTIC_EYE,
          (stack, slotReference, contextModel, matrices, vertexConsumers, light, entity, limbAngle, limbDistance,
              tickDelta, animationProgress, headYaw, headPitch) -> {
            if (entity instanceof AbstractClientPlayerEntity player) {
              TrinketRenderer.translateToFace(matrices,
                  (PlayerEntityModel<AbstractClientPlayerEntity>) contextModel, player, headYaw, headPitch);
              // x: + gauche; - droite
              // y: - bas; + haut
              // z: + derrière; - devant
              matrices.translate(-0.2f, -0.4f, 0.5f);
              MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformationMode.HEAD, light,
                  OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, null, 0);
            }
          });

      TrinketRendererRegistry.registerRenderer(ModItems.ETERNAL_GUARDIANS_BAND,
          (stack, slotReference, contextModel, matrices, vertexConsumers, light, entity, limbAngle, limbDistance,
              tickDelta, animationProgress, headYaw, headPitch) -> {
            if (entity instanceof AbstractClientPlayerEntity player) {
              TrinketRenderer.translateToRightArm(matrices,
                  (PlayerEntityModel<AbstractClientPlayerEntity>) contextModel, player);
              matrices.translate(0f, -0.75f, 0f);
              MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformationMode.HEAD, light,
                  OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, null, 0);
            }
          });

      TrinketRendererRegistry.registerRenderer(ModItems.ETERNAL_GUARDIAN_MASK,
          (stack, slotReference, contextModel, matrices, vertexConsumers, light, entity, limbAngle, limbDistance,
              tickDelta, animationProgress, headYaw, headPitch) -> {
            if (entity instanceof AbstractClientPlayerEntity player) {
              TrinketRenderer.translateToFace(matrices,
                  (PlayerEntityModel<AbstractClientPlayerEntity>) contextModel, player, headYaw, headPitch);
              matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180));
              MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformationMode.HEAD, light,
                  OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, null, 0);
            }
          });
    }
  }
}
