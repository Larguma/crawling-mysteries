package dev.larguma.crawlingmysteries.client.entity;

import dev.larguma.crawlingmysteries.entity.custom.EternalGuardianEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class EternalGuardianEntityRenderer extends GeoEntityRenderer<EternalGuardianEntity> {

  public EternalGuardianEntityRenderer(Context context) {
    super(context, new EternalGuardianEntityModel());
  }
}
