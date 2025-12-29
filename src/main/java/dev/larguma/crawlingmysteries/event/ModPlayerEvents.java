package dev.larguma.crawlingmysteries.event;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import dev.larguma.crawlingmysteries.data.ModDataAttachments;
import dev.larguma.crawlingmysteries.data.ModDataComponents;
import dev.larguma.crawlingmysteries.data.custom.HorseshoeDataComponent;
import dev.larguma.crawlingmysteries.item.ModItems;
import dev.larguma.crawlingmysteries.item.helper.ItemHelper;
import dev.larguma.crawlingmysteries.networking.packet.TavernMusicPacket;
import dev.larguma.crawlingmysteries.spell.SpellCooldownManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerRespawnEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = CrawlingMysteries.MOD_ID)
public class ModPlayerEvents {

  private static final ResourceLocation TAVERN_LOCATION = ResourceLocation
      .fromNamespaceAndPath(CrawlingMysteries.MOD_ID, "tavern");
  private static final TagKey<Structure> TAVERN_TAG = TagKey.create(Registries.STRUCTURE, TAVERN_LOCATION);
  private static final Map<UUID, Boolean> insideTavernMap = new HashMap<>();

  @SubscribeEvent
  public static void onPlayerTick(PlayerTickEvent.Post event) {
    if (event.getEntity().level().isClientSide) {
      return;
    }

    ServerPlayer player = (ServerPlayer) event.getEntity();
    if (player.tickCount % 20 != 0) {
      return;
    }

    boolean insideTavern = player.serverLevel().structureManager()
        .getStructureWithPieceAt(player.blockPosition(), TAVERN_TAG).isValid();

    if (insideTavernMap.getOrDefault(player.getUUID(), false) != insideTavern) {
      insideTavernMap.put(player.getUUID(), insideTavern);
      PacketDistributor.sendToPlayer(player, new TavernMusicPacket(insideTavern));
    }
  }

  @SubscribeEvent
  public static void playerLogin(PlayerLoggedInEvent event) {
    Player player = event.getEntity();
    if (player.level().isClientSide()) {
      return;
    }

    if (!player.getData(ModDataAttachments.STARTER_RECEIVED)) {
      player.getInventory().add(new ItemStack(ModItems.CRYPTIC_EYE.get()));
      player.setData(ModDataAttachments.STARTER_RECEIVED, true);
    }

    SpellCooldownManager.syncAllCooldownsToClient((ServerPlayer) player);
  }

  @SubscribeEvent
  public static void playerLogout(PlayerLoggedOutEvent event) {
    insideTavernMap.remove(event.getEntity().getUUID());
  }

  @SubscribeEvent
  public static void playerRespawn(PlayerRespawnEvent event) {
    Player player = event.getEntity();
    if (player.level().isClientSide()) {
      return;
    }

    if (!event.isEndConquered()) {
      SpellCooldownManager.clearAllCooldowns((ServerPlayer) player);
    }
    SpellCooldownManager.syncAllCooldownsToClient((ServerPlayer) player);
  }

  @SubscribeEvent
  public static void playerChangedDimension(PlayerChangedDimensionEvent event) {
    Player player = event.getEntity();
    if (player.level().isClientSide()) {
      return;
    }

    SpellCooldownManager.syncAllCooldownsToClient((ServerPlayer) player);
  }

  @SubscribeEvent
  public static void onLivingDamage(LivingDamageEvent.Pre event) {
    if (!(event.getEntity() instanceof Player player)) {
      return;
    }

    if (!event.getSource().is(DamageTypeTags.IS_FALL)) {
      return;
    }

    ItemStack horseshoe = ItemHelper.findEquippedTrinket(player, ModItems.LUCKY_HORSESHOE.get());
    if (horseshoe.isEmpty()) {
      return;
    }

    HorseshoeDataComponent component = horseshoe.get(ModDataComponents.HORSESHOE_TIER.get());

    float reduction = component.getFallReduction();

    if (reduction < 1.0f) {
      if (player.getRandom().nextFloat() < reduction) {
        event.setNewDamage(0);
      }
    } else {
      event.setNewDamage(0);
    }
  }

  @SubscribeEvent
  public static void onGrindstoneInteract(RightClickBlock event) {
    Level level = event.getLevel();
    BlockPos pos = event.getPos();

    if (level.getBlockState(pos).is(Blocks.GRINDSTONE) && event.getItemStack().is(ModItems.PETRIFIED_EYE.get())) {

      event.setCanceled(true);
      event.setCancellationResult(InteractionResult.SUCCESS);

      if (!level.isClientSide) {
        level.playSound(null, pos, SoundEvents.GRINDSTONE_USE, SoundSource.BLOCKS, 1.0f, 1.0f);

        if (!event.getEntity().isCreative()) {
          event.getItemStack().shrink(1);
        }

        if (level.random.nextFloat() < 0.3f) {
          ItemHelper.spawnItemAboveBlock(level, pos, new ItemStack(ModItems.AWAKENED_EYE.get()));
          ((ServerLevel) level).sendParticles(ParticleTypes.HAPPY_VILLAGER, pos.getX() + 0.5, pos.getY() + 0.5,
              pos.getZ() + 0.5, 5, 0.2, 0.2, 0.2, 0.0);
        } else {
          ItemHelper.spawnItemAboveBlock(level, pos, new ItemStack(Items.GRAVEL));
          level.playSound(null, pos, SoundEvents.ITEM_BREAK, SoundSource.BLOCKS, 1.0f, 0.8f);
          ((ServerLevel) level).sendParticles(ParticleTypes.SMOKE, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
              5, 0.2, 0.2, 0.2, 0.0);
        }
      } else {
        event.getEntity().swing(event.getHand());
      }
    }
  }
}
