package larguma.crawling_mysteries.particle.custom;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

public class EternalFireParticle extends SpriteBillboardParticle {

  protected EternalFireParticle(ClientWorld level, double xCoord, double yCoord, double zCoord,
      SpriteProvider spriteSet, double xd, double yd, double zd) {
    super(level, xCoord, yCoord, zCoord, xd, yd, zd);

    this.x = xCoord;
    this.y = yCoord;
    this.z = zCoord;
    this.velocityX = xd;
    this.velocityY = yd + (double) (this.random.nextFloat() / 500.0f);
    this.velocityZ = zd;
    this.scale *= 0.75f;
    this.maxAge = this.random.nextInt(50) + 80;
    this.setSprite(spriteSet);
    this.setBoundingBoxSpacing(0.25f, 0.25f);

    this.red = 1f;
    this.green = 1f;
    this.blue = 1f;
  }

  @Override
  public void tick() {
    super.tick();
    fadeOut();
  }

  private void fadeOut() {
    this.alpha = (-(1 / (float) maxAge) * age + 1);
  }

  @Override
  public ParticleTextureSheet getType() {
    return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
  }

  @Environment(EnvType.CLIENT)
  public static class Factory implements ParticleFactory<DefaultParticleType> {
    private final SpriteProvider sprites;

    public Factory(SpriteProvider spriteSet) {
      this.sprites = spriteSet;
    }

    @Override
    public Particle createParticle(DefaultParticleType particleType, ClientWorld level, double x, double y, double z,
        double dx, double dy, double dz) {
      return new EternalFireParticle(level, x, y, z, this.sprites, dx, dy, dz);
    }
  }

}
