package larguma.crawling_mysteries.entity.client;

import larguma.crawling_mysteries.CrawlingMysteries;
import larguma.crawling_mysteries.entity.custom.EternalGuardianEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class EternalGuardianModel extends GeoModel<EternalGuardianEntity> {

  @Override
  public Identifier getModelResource(EternalGuardianEntity animatable) {
    return new Identifier(CrawlingMysteries.MOD_ID, "geo/eternal_guardian.geo.json");
  }

  @Override
  public Identifier getTextureResource(EternalGuardianEntity animatable) {
    return new Identifier(CrawlingMysteries.MOD_ID, "textures/entity/eternal_guardian.png");
  }

  @Override
  public Identifier getAnimationResource(EternalGuardianEntity animatable) {
    return new Identifier(CrawlingMysteries.MOD_ID, "animations/eternal_guardian.animation.json");
  }
  
  @Override
    public void setCustomAnimations(EternalGuardianEntity animatable, long instanceId, AnimationState<EternalGuardianEntity> animationState) {
        CoreGeoBone head = getAnimationProcessor().getBone("head");

        if (head != null) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
            head.setRotX(entityData.headPitch() * MathHelper.RADIANS_PER_DEGREE);
            head.setRotY(entityData.netHeadYaw() * MathHelper.RADIANS_PER_DEGREE);
        }
    }
}
