package dev.larguma.crawlingmysteries.client.block;

import org.joml.Matrix4f;
import org.joml.Quaternionf;

import com.mojang.blaze3d.vertex.PoseStack;

import dev.larguma.crawlingmysteries.block.entity.custom.TombstoneBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class TombstoneBlockEntityRenderer extends GeoBlockRenderer<TombstoneBlockEntity> {
  protected final Font font;

  public TombstoneBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    super(new TombstoneBlockEntityModel());
    this.font = context.getFont();
  }

  @Override
  public void render(TombstoneBlockEntity entity, float tickDelta, PoseStack poseStack,
      MultiBufferSource bufferSource, int packedLight, int packedOverlay) {

    renderNameTag(entity, poseStack, bufferSource, packedLight);

  }

  private void renderNameTag(final TombstoneBlockEntity entity, final PoseStack poseStack,
      final MultiBufferSource bufferSource, int packedLight) {
    if (!entity.hasTombstoneOwner())
      return;
    String text = entity.getTombstoneOwner().getName();
    if (text == null) {
      return;
    }

    Minecraft minecraftClient = Minecraft.getInstance();
    Vec3 playerPos = minecraftClient.player.position();
    BlockPos blockPos = entity.getBlockPos();
    double d = playerPos.distanceToSqr(blockPos.getX(), blockPos.getY(), blockPos.getZ());
    if (d > 4096.0) {
      return;
    }

    LocalPlayer player = minecraftClient.player;
    Quaternionf rotation = new Quaternionf(0.0F, 0.0F, 0.0F, 1.0F);
    Boolean inverseView = minecraftClient.options.getCameraType().isMirrored();
    float yaw;
    float pitch;
    if (Boolean.TRUE.equals(inverseView)) {
      yaw = player.getYHeadRot() + 180.0F;
      pitch = -player.getXRot();
    } else {
      yaw = player.getYHeadRot();
      pitch = player.getXRot();
    }

    poseStack.pushPose();
    poseStack.translate(0.5F, 1.5F, 0.5F);
    poseStack.mulPose(rotation.rotationYXZ(-yaw * ((float) Math.PI / 180), pitch * ((float) Math.PI / 180), 0.0F));
    poseStack.scale(-0.025F, -0.025F, 0.025F);
    Matrix4f matrix4f = poseStack.last().pose();
    float g = minecraftClient.options.getBackgroundOpacity(0.25F);
    int j = (int) (g * 255.0F) << 24;
    float h = (float) -font.width(text) / 2;
    font.drawInBatch(text, h, 0F, 0xFFFFFFFF, false, matrix4f, bufferSource, Font.DisplayMode.NORMAL, j, packedLight);
    poseStack.popPose();
  }
}
