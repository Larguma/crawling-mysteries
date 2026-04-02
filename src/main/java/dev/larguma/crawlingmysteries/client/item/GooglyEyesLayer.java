package dev.larguma.crawlingmysteries.client.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import dev.larguma.crawlingmysteries.block.entity.custom.BeerMugBlockEntity;
import dev.larguma.crawlingmysteries.data.ModDataComponents;
import dev.larguma.crawlingmysteries.item.custom.BeerMugItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class GooglyEyesLayer<T extends GeoAnimatable> extends GeoRenderLayer<T> {

  private static final ResourceLocation WHITE_TEXTURE = ResourceLocation
      .withDefaultNamespace("textures/block/white_concrete.png");
  private final double dx;
  private final double dy;
  private final double dz;
  private final float rotation;

  public GooglyEyesLayer(GeoRenderer<T> entityRendererIn, double dx, double dy, double dz, float rotation) {
    super(entityRendererIn);
    this.dx = dx;
    this.dy = dy;
    this.dz = dz;
    this.rotation = rotation;
  }

  @Override
  public void render(PoseStack poseStack, T animatable, BakedGeoModel bakedModel, RenderType renderType,
      MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
    boolean hasEyes = false;
    if (animatable instanceof BeerMugItem && this.getRenderer() instanceof GeoItemRenderer) {
      ItemStack stack = ((GeoItemRenderer<?>) this.getRenderer()).getCurrentItemStack();
      hasEyes = Boolean.TRUE.equals(stack.get(ModDataComponents.GOOGLY_EYES.get()));
    } else if (animatable instanceof BeerMugBlockEntity be) {
      hasEyes = be.hasGooglyEyes();
    }

    if (hasEyes) {
      renderGooglyEyes(poseStack, bufferSource, packedLight, packedOverlay, partialTick);
    }
  }

  private void renderGooglyEyes(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay,
      float partialTick) {
    VertexConsumer builder = bufferSource.getBuffer(RenderType.entityCutoutNoCull(WHITE_TEXTURE));

    float time = 0;
    if (Minecraft.getInstance().level != null) {
      time = Minecraft.getInstance().level.getGameTime() + partialTick;
    }

    poseStack.pushPose();
    poseStack.translate(dx, dy, dz);
    poseStack.mulPose(Axis.YP.rotationDegrees(rotation));
    poseStack.scale(0.08F, 0.08F, 0.08F);

    float leftPupilX = Mth.sin(time * 0.1F) * 0.1F;
    float leftPupilY = Mth.cos(time * 0.13F) * 0.1F;
    poseStack.pushPose();
    poseStack.translate(1.2F, 0, 0);
    renderEye(poseStack, builder, packedLight, packedOverlay, leftPupilX, leftPupilY);
    poseStack.popPose();

    float rightPupilX = Mth.sin(time * 0.11F + 2.0F) * 0.1F;
    float rightPupilY = Mth.cos(time * 0.14F + 1.0F) * 0.1F;
    poseStack.pushPose();
    poseStack.translate(-1.2F, 0, 0);
    renderEye(poseStack, builder, packedLight, packedOverlay, rightPupilX, rightPupilY);
    poseStack.popPose();

    poseStack.popPose();
  }

  private void renderEye(PoseStack poseStack, VertexConsumer builder, int packedLight, int packedOverlay,
      float pupilOffsetX, float pupilOffsetY) {
    PoseStack.Pose pose = poseStack.last();

    // White
    drawQuad(pose, builder, -1, -1, 0, 2, 2, 0xFFFFFFFF, packedLight, packedOverlay);

    // Black
    poseStack.pushPose();
    poseStack.translate(0, 0, -0.05F);

    poseStack.translate(0.2F + pupilOffsetX, -0.2F + pupilOffsetY, 0);
    PoseStack.Pose pupilPose = poseStack.last();
    drawQuad(pupilPose, builder, -0.4F, -0.4F, 0, 0.8F, 0.8F, 0xFF000000, packedLight, packedOverlay);
    poseStack.popPose();
  }

  private void drawQuad(PoseStack.Pose pose, VertexConsumer builder, float x, float y, float z, float w, float h,
      int color, int light, int overlay) {
    float r = ((color >> 16) & 0xFF) / 255.0F;
    float g = ((color >> 8) & 0xFF) / 255.0F;
    float b = (color & 0xFF) / 255.0F;
    float a = ((color >> 24) & 0xFF) / 255.0F;

    builder.addVertex(pose.pose(), x, y, z).setColor(r, g, b, a).setUv(0, 0).setOverlay(overlay).setLight(light)
        .setNormal(pose, 0, 0, 1);
    builder.addVertex(pose.pose(), x, y + h, z).setColor(r, g, b, a).setUv(0, 1).setOverlay(overlay).setLight(light)
        .setNormal(pose, 0, 0, 1);
    builder.addVertex(pose.pose(), x + w, y + h, z).setColor(r, g, b, a).setUv(1, 1).setOverlay(overlay).setLight(light)
        .setNormal(pose, 0, 0, 1);
    builder.addVertex(pose.pose(), x + w, y, z).setColor(r, g, b, a).setUv(1, 0).setOverlay(overlay).setLight(light)
        .setNormal(pose, 0, 0, 1);
  }
}