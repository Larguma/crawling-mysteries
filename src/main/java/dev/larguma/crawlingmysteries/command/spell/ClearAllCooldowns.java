package dev.larguma.crawlingmysteries.command.spell;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import dev.larguma.crawlingmysteries.command.helper.CommandHelper;
import dev.larguma.crawlingmysteries.spell.SpellCooldownManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class ClearAllCooldowns {

  public ClearAllCooldowns(CommandDispatcher<CommandSourceStack> dispatcher) {
    dispatcher.register(CommandHelper.MOD_COMMAND
        .then(Commands.literal("spell")
            .then(Commands.literal("cooldowns")
                .then(Commands.literal("clearall")
                    .then(Commands.argument("player", EntityArgument.player())
                        .executes(this::executeSelect))
                    .executes(this::executeSelf)))));
  };

  private int executeSelf(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
    ServerPlayer player = context.getSource().getPlayerOrException();
    return execute(context, player);
  }

  private int executeSelect(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
    ServerPlayer player = EntityArgument.getPlayer(context, "player");
    return execute(context, player);
  }

  private int execute(CommandContext<CommandSourceStack> context, ServerPlayer player) throws CommandSyntaxException {
    SpellCooldownManager.clearAllCooldowns(player);
    SpellCooldownManager.syncAllCooldownsToClient(player);

    context.getSource().sendSuccess(
        () -> Component.translatable("command.crawlingmysteries.spell.cooldowns.clearall.success", player.getName()),
        true);
    return 1;
  }
}