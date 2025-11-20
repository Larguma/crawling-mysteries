package dev.larguma.crawlingmysteries.block.entity.client.renderer;

import org.joml.Matrix4f;
import org.joml.Quaternionf;

import com.mojang.blaze3d.vertex.PoseStack;

import dev.larguma.crawlingmysteries.block.entity.TombstoneBlockEntity;
import dev.larguma.crawlingmysteries.block.entity.client.block.TombstoneBlockEntityModel;
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
    Quaternionf rotation = new Quaternionf(0.0f, 0.0f, 0.0f, 1.0f);
    Boolean inverseView = minecraftClient.options.getCameraType().isMirrored();
    float yaw = 0f;
    float pitch = 0f;
    if (inverseView) {
      yaw = player.getYHeadRot() + 180.0f;
      pitch = -player.getXRot();
    } else {
      yaw = player.getYHeadRot();
      pitch = player.getXRot();
    }

    poseStack.pushPose();
    poseStack.translate(0.5f, 1.5f, 0.5f);
    poseStack.mulPose(rotation.rotationYXZ(-yaw * ((float) Math.PI / 180), pitch * ((float) Math.PI / 180), 0.0f));
    poseStack.scale(-0.025f, -0.025f, 0.025f);
    Matrix4f matrix4f = poseStack.last().pose();
    float g = minecraftClient.options.getBackgroundOpacity(0.25f);
    int j = (int) (g * 255.0f) << 24;
    float h = -font.width(text) / 2;
    font.drawInBatch(text, h, 0f, 0xFFFFFFFF, false, matrix4f, bufferSource, Font.DisplayMode.NORMAL, j, packedLight);
    poseStack.popPose();

  }
}
