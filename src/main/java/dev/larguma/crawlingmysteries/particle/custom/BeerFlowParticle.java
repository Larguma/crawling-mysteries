package dev.larguma.crawlingmysteries.particle.custom;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;

public class BeerFlowParticle extends TextureSheetParticle {

  protected BeerFlowParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd,
      SpriteSet spriteSet) {
    super(level, x, y, z);

    this.xd = xd;
    this.yd = yd;
    this.zd = zd;

    this.setSize(0.01F, 0.01F);
    this.gravity = 0.06F;
    this.lifetime = (int) (64.0D / (Math.random() * 0.8D + 0.2D));

    // Golden
    this.rCol = 0.95F;
    this.gCol = 0.75F;
    this.bCol = 0.1F;

    this.rCol *= 0.9F + this.random.nextFloat() * 0.1F;
    this.gCol *= 0.9F + this.random.nextFloat() * 0.1F;

    this.pickSprite(spriteSet);
  }

  @Override
  public void tick() {
    this.xo = this.x;
    this.yo = this.y;
    this.zo = this.z;

    if (this.age++ >= this.lifetime) {
      this.remove();
      return;
    }

    this.yd -= 0.04D * (double) this.gravity;
    this.move(this.xd, this.yd, this.zd);

    this.xd *= 0.98F;
    this.yd *= 0.98F;
    this.zd *= 0.98F;

    if (this.onGround) {
      this.lifetime = 2;
    }
  }

  @Override
  public ParticleRenderType getRenderType() {
    return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
  }

  public static class Provider implements ParticleProvider<SimpleParticleType> {
    private final SpriteSet spriteSet;

    public Provider(SpriteSet spriteSet) {
      this.spriteSet = spriteSet;
    }

    @Nullable
    @Override
    public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z,
        double xSpeed, double ySpeed, double zSpeed) {
      return new BeerFlowParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
    }
  }
}
