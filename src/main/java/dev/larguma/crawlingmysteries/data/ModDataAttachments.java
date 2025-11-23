package dev.larguma.crawlingmysteries.data;

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

  public static void register(IEventBus eventBus) {
    ATTACHMENT_TYPES.register(eventBus);
  }
}
