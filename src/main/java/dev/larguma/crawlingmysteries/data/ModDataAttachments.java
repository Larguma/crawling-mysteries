package dev.larguma.crawlingmysteries.data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import com.mojang.serialization.Codec;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class ModDataAttachments {
  private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister
      .create(NeoForgeRegistries.ATTACHMENT_TYPES, CrawlingMysteries.MOD_ID);

  public static final Supplier<AttachmentType<Boolean>> STARTER_RECEIVED = ATTACHMENT_TYPES.register("starter_received",
      () -> AttachmentType.builder(() -> false).serialize(Codec.BOOL).copyOnDeath().build());

  public static final Supplier<AttachmentType<Map<String, Long>>> SPELL_COOLDOWNS = ATTACHMENT_TYPES.register(
      "spell_cooldowns",
      () -> AttachmentType.builder(() -> (Map<String, Long>) new HashMap<String, Long>())
          .serialize(Codec.unboundedMap(Codec.STRING, Codec.LONG))
          .copyOnDeath()
          .build());

  public static final Supplier<AttachmentType<String>> LAST_USED_SPELL = ATTACHMENT_TYPES.register(
      "last_used_spell",
      () -> AttachmentType.builder(() -> "").serialize(Codec.STRING).copyOnDeath().build());

  public static final Supplier<AttachmentType<Set<String>>> UNLOCKED_CODEX_ENTRIES = ATTACHMENT_TYPES.register(
      "unlocked_codex_entries",
      () -> AttachmentType.builder(() -> (Set<String>) new HashSet<String>())
          .serialize(Codec.STRING.listOf().xmap(HashSet::new, list -> list.stream().toList()))
          .copyOnDeath()
          .build());

  public static void register(IEventBus eventBus) {
    ATTACHMENT_TYPES.register(eventBus);
  }
}
