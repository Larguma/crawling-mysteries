package larguma.crawling_mysteries;

import com.mojang.authlib.GameProfile;

import dev.emi.trinkets.api.client.TrinketRenderer;
import dev.emi.trinkets.api.client.TrinketRendererRegistry;
import larguma.crawling_mysteries.block.entity.ModBlockEntities;
import larguma.crawling_mysteries.block.entity.TombstoneBlockEntity;
import larguma.crawling_mysteries.block.entity.renderer.TombstoneBlockEntityRenderer;
import larguma.crawling_mysteries.datagen.ModBlockTagProvider;
import larguma.crawling_mysteries.item.ModItems;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityType;

public class CrawlingMysteriesClient implements ClientModInitializer {

  @SuppressWarnings("unchecked")
  @Override
  public void onInitializeClient() {
    BlockEntityRendererFactories.register(ModBlockEntities.TOMBSTONE_BLOCK_ENTITY, TombstoneBlockEntityRenderer::new);

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
