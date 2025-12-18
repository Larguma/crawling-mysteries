package dev.larguma.crawlingmysteries.particle.custom;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.entity.player.Player;

public class SoulSuckleParticles extends TextureSheetParticle {

  private final SpriteSet spriteSet;
  private final Player targetPlayer;
  private final float initialScale;

  protected SoulSuckleParticles(ClientLevel level, double x, double y, double z, SpriteSet spriteSet) {
    super(level, x, y, z);

    this.spriteSet = spriteSet;

    this.targetPlayer = level.getNearestPlayer(x, y, z, 10.0, false);

    this.friction = 1.0f;
    this.gravity = 0f;

    this.lifetime = 40;
    this.setSpriteFromAge(spriteSet);

    this.rCol = 0.4f;
    this.gCol = 0.9f;
    this.bCol = 1.0f;

    this.initialScale = 0.2f + this.random.nextFloat() * 0.15f;
    this.quadSize = this.initialScale;

    this.xd = (this.random.nextDouble() - 0.5) * 0.05;
    this.yd = this.random.nextDouble() * 0.03;
    this.zd = (this.random.nextDouble() - 0.5) * 0.05;
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

    // if no player, float up and fade
    if (this.targetPlayer == null || !this.targetPlayer.isAlive()) {
      this.yd += 0.01;
      this.alpha -= 0.05f;
      if (this.alpha <= 0) {
        this.remove();
      }
      this.move(this.xd, this.yd, this.zd);
      return;
    }

    double targetX = this.targetPlayer.getX();
    double targetY = this.targetPlayer.getY(0.6);
    double targetZ = this.targetPlayer.getZ();

    double dx = targetX - this.x;
    double dy = targetY - this.y;
    double dz = targetZ - this.z;
    double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

    if (distance < 0.4) {
      this.remove();
      return;
    }

    dx /= distance;
    dy /= distance;
    dz /= distance;

    // speed increases as particle ages
    float progress = (float) this.age / (float) this.lifetime;
    double acceleration = 0.02 + progress * 0.06;

    // add spiral motion
    double spiralAngle = this.age * 0.4 + this.hashCode() * 0.1;
    double spiralRadius = 0.03 * (1.0 - progress * 0.7);
    double spiralX = Math.cos(spiralAngle) * spiralRadius;
    double spiralZ = Math.sin(spiralAngle) * spiralRadius;

    this.xd += dx * acceleration + spiralX;
    this.yd += dy * acceleration;
    this.zd += dz * acceleration + spiralZ;

    // add drag
    this.xd *= 0.9;
    this.yd *= 0.9;
    this.zd *= 0.9;

    this.move(this.xd, this.yd, this.zd);

    this.setSpriteFromAge(this.spriteSet);

    // fade and shrink when near target
    this.alpha = 0.9f - progress * 0.3f;
    this.quadSize = this.initialScale * (1.0f - progress * 0.5f);

    this.rCol = 0.4f + progress * 0.6f;
    this.gCol = 0.9f + progress * 0.1f;
    this.bCol = 1.0f;
  }

  @Override
  public ParticleRenderType getRenderType() {
    return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
  }

  public static class Provider implements ParticleProvider<SimpleParticleType> {
    private final SpriteSet spriteSet;

    public Provider(SpriteSet spriteSet) {
      this.spriteSet = spriteSet;
    }

    @Nullable
    @Override
    public Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientLevel,
        double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
      return new SoulSuckleParticles(clientLevel, pX, pY, pZ, this.spriteSet);
    }
  }

}
