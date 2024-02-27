package larguma.crawling_mysteries.entity.client;

import larguma.crawling_mysteries.CrawlingMysteries;
import larguma.crawling_mysteries.entity.custom.EternalGuardianEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class EternalGuardianRenderer extends GeoEntityRenderer<EternalGuardianEntity> {

  public EternalGuardianRenderer(Context renderManager) {
    super(renderManager, new EternalGuardianModel());
  }

  @Override
  public Identifier getTextureLocation(EternalGuardianEntity animatable) {
    return new Identifier(CrawlingMysteries.MOD_ID, "textures/entity/eternal_guardian.png");
  }

  @Override
  public void render(EternalGuardianEntity entity, float entityYaw, float partialTick, MatrixStack poseStack,
      VertexConsumerProvider bufferSource, int packedLight) {
    super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
  }
}
