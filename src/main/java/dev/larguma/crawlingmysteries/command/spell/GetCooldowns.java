package dev.larguma.crawlingmysteries.command.spell;

import java.util.Optional;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import dev.larguma.crawlingmysteries.item.helper.CommandHelper;
import dev.larguma.crawlingmysteries.spell.ModSpells;
import dev.larguma.crawlingmysteries.spell.Spell;
import dev.larguma.crawlingmysteries.spell.SpellCooldownManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class GetCooldowns {

  public GetCooldowns(CommandDispatcher<CommandSourceStack> dispatcher) {
    dispatcher.register(CommandHelper.MOD_COMMAND
        .then(Commands.literal("spell")
            .then(Commands.literal("cooldowns")
                .then(Commands.literal("get")
                    .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.argument("spell", StringArgumentType.string())
                            .suggests(CommandHelper.SPELL_SUGGESTIONS)
                            .executes(this::execute)))))));
  };

  private int execute(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
    ServerPlayer player = EntityArgument.getPlayer(context, "player");
    String spellId = StringArgumentType.getString(context, "spell");

    Optional<Spell> spellOptional = ModSpells.getSpell(spellId);

    if (spellOptional.isEmpty()) {
      context.getSource().sendFailure(
          Component.translatable("command.crawlingmysteries.spell.cooldowns.invalid_spell", spellId));
      return 0;
    }

    Spell spell = spellOptional.get();
    String cooldown = SpellCooldownManager.getRemainingCooldownFormatted(player, spell);

    context.getSource().sendSuccess(
        () -> Component.translatable("command.crawlingmysteries.spell.cooldowns.get.success", spellId, player.getName(),
            cooldown),
        true);
    return 1;
  }
}