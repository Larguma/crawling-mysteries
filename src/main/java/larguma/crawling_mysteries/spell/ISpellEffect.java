package larguma.crawling_mysteries.spell;

import net.minecraft.server.network.ServerPlayerEntity;

public interface ISpellEffect {
  void receive(ServerPlayerEntity player) ;
}
