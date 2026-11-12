package dev.larguma.crawlingmysteries.command.codex;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import dev.larguma.crawlingmysteries.codex.CodexUnlockManager;
import dev.larguma.crawlingmysteries.command.helper.CommandHelper;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class UnlockEntry {

  public UnlockEntry(CommandDispatcher<CommandSourceStack> dispatcher) {
    dispatcher.register(CommandHelper.MOD_COMMAND
        .then(Commands.literal("codex")
            .then(Commands.literal("unlock")
                .then(Commands.argument("entry_id", StringArgumentType.string())
                    .suggests(CommandHelper.CODEX_ENTRY_SUGGESTIONS)
                    .then(Commands.argument("player", EntityArgument.player())
                        .executes(this::executeSelect))
                    .executes(this::executeSelf)))));
  }

  private int executeSelf(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
    ServerPlayer player = context.getSource().getPlayerOrException();
    return execute(context, player);
  }

  private int executeSelect(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
    ServerPlayer player = EntityArgument.getPlayer(context, "player");
    return execute(context, player);
  }

  private int execute(CommandContext<CommandSourceStack> context, ServerPlayer player) {
    String entryId = StringArgumentType.getString(context, "entry_id");

    CodexUnlockManager.unlockEntry(player, entryId);

    context.getSource().sendSuccess(
        () -> Component.translatable("command.crawlingmysteries.codex.unlock.success", entryId,
            player.getName()),
        true);

    return 1;
  }

}
