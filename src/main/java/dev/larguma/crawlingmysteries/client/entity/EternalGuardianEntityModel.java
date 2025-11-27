package dev.larguma.crawlingmysteries.client.entity;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import dev.larguma.crawlingmysteries.entity.custom.EternalGuardianEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class EternalGuardianEntityModel extends DefaultedEntityGeoModel<EternalGuardianEntity> {

  public EternalGuardianEntityModel() {
    super(ResourceLocation.fromNamespaceAndPath(CrawlingMysteries.MOD_ID, "eternal_guardian"));
  }

  @Override
  public void setCustomAnimations(EternalGuardianEntity animatable, long instanceId,
      AnimationState<EternalGuardianEntity> animationState) {
    GeoBone head = getAnimationProcessor().getBone("head");

    if (head != null) {
      EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
      head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
      head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
    }
  }
}
