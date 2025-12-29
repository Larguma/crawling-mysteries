package dev.larguma.crawlingmysteries.networking.handler;

import dev.larguma.crawlingmysteries.client.gui.BetterToastOverlay;
import dev.larguma.crawlingmysteries.client.screen.CrypticCodexScreen;
import dev.larguma.crawlingmysteries.client.sound.MusicHandler;
import dev.larguma.crawlingmysteries.client.spell.ClientSpellCooldownManager;
import dev.larguma.crawlingmysteries.codex.CodexUnlockManager;
import dev.larguma.crawlingmysteries.networking.packet.BetterToastPacket;
import dev.larguma.crawlingmysteries.networking.packet.SpellCooldownSyncPacket;
import dev.larguma.crawlingmysteries.networking.packet.SyncUnlockedEntriesPacket;
import dev.larguma.crawlingmysteries.networking.packet.TavernMusicPacket;
import dev.larguma.crawlingmysteries.networking.packet.TotemAnimationPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ClientPayloadHandler {

  public static void handleSpellCooldownSync(final SpellCooldownSyncPacket packet, final IPayloadContext context) {
    context.enqueueWork(() -> {
      if (packet.remainingTicks() <= 0) {
        ClientSpellCooldownManager.clearCooldown(packet.spellId());
      } else {
        ClientSpellCooldownManager.setCooldown(packet.spellId(), packet.remainingTicks(), packet.totalTicks());
      }
    });
  }

  public static void handleBetterToast(final BetterToastPacket packet, final IPayloadContext context) {
    context.enqueueWork(() -> {
      BetterToastOverlay.ToastType type = BetterToastOverlay.ToastType.values()[packet.toastType()];
      BetterToastOverlay.showMessage(packet.message(), type, packet.getIconItem(), packet.getIconTexture());
    });
  }

  public static void handleTotemAnimation(final TotemAnimationPacket packet, final IPayloadContext context) {
    context.enqueueWork(() -> {
      Minecraft mc = Minecraft.getInstance();
      if (mc.player != null) {
        mc.particleEngine.createTrackingEmitter(mc.player, ParticleTypes.TOTEM_OF_UNDYING, 30);
        mc.level.playLocalSound(mc.player.getX(), mc.player.getY(), mc.player.getZ(), SoundEvents.TOTEM_USE,
            mc.player.getSoundSource(), 1.0F, 1.0F, false);
        mc.gameRenderer.displayItemActivation(packet.itemStack());
      }
    });
  }

  public static void handleSyncUnlockedEntries(final SyncUnlockedEntriesPacket packet, final IPayloadContext context) {
    context.enqueueWork(() -> {
      CodexUnlockManager.setUnlockedEntries(packet.unlockedEntries());
      CrypticCodexScreen.refreshIfOpen();
    });
  }

  public static void handleTavernMusic(final TavernMusicPacket packet, final IPayloadContext context) {
    context.enqueueWork(() -> {
      MusicHandler.setInsideTavern(packet.insideTavern());
    });
  }
}
