package larguma.crawling_mysteries.block.entity.renderer;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import larguma.crawling_mysteries.block.entity.TombstoneBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.font.TextRenderer.TextLayerType;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class TombstoneBlockEntityRenderer implements BlockEntityRenderer<TombstoneBlockEntity> {

  protected final TextRenderer textRenderer;

  public TombstoneBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
    this.textRenderer = ctx.getTextRenderer();
  }

  @Override
  public void render(TombstoneBlockEntity entity, float tickDelta, MatrixStack matrices,
      VertexConsumerProvider vertexConsumers, int light,
      int overlay) {

    String text = entity.getTombOwner().getName();
    if (text == null) {
      return;
    }

    MinecraftClient minecraftClient = MinecraftClient.getInstance();

    Vec3d playerPos = minecraftClient.player.getPos();
    BlockPos blockPos = entity.getPos();
    double d = playerPos.squaredDistanceTo(blockPos.getX(), blockPos.getY(), blockPos.getZ());
    if (d > 4096.0) {
      return;
    }

    ClientPlayerEntity player = minecraftClient.player;
    Quaternionf rotation = new Quaternionf(0.0f, 0.0f, 0.0f, 1.0f);
    Boolean inverseView = minecraftClient.options.getPerspective().isFrontView();
    float yaw = 0f;
    float pitch = 0f;
    if (inverseView) {
      yaw = player.getYaw(tickDelta) + 180.0f;
      pitch = -player.getPitch(tickDelta);
    } else {
      yaw = player.getYaw(tickDelta);
      pitch = player.getPitch(tickDelta);
    }

    matrices.push();
    matrices.translate(0.5f, 1.5f, 0.5f);
    matrices.multiply(rotation.rotationYXZ(-yaw * ((float) Math.PI / 180),
        pitch * ((float) Math.PI / 180), 0.0f));
    matrices.scale(-0.025f, -0.025f, 0.025f);
    Matrix4f matrix4f = matrices.peek().getPositionMatrix();
    float g = minecraftClient.options.getTextBackgroundOpacity(0.25f);
    int j = (int) (g * 255.0f) << 24;
    float h = -textRenderer.getWidth(text) / 2;
    textRenderer.draw(text, h, 0f, 0xFFFFFFFF, false, matrix4f, vertexConsumers,
        TextLayerType.NORMAL, j, light);
    matrices.pop();
  }
}
