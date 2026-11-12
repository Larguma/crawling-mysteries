package dev.larguma.crawlingmysteries.command.codex;

import java.util.Set;
import java.util.stream.Collectors;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import dev.larguma.crawlingmysteries.codex.CodexEntry;
import dev.larguma.crawlingmysteries.codex.CodexRegistry;
import dev.larguma.crawlingmysteries.codex.CodexUnlockManager;
import dev.larguma.crawlingmysteries.command.helper.CommandHelper;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class UnlockAllEntries {

  public UnlockAllEntries(CommandDispatcher<CommandSourceStack> dispatcher) {
    dispatcher.register(CommandHelper.MOD_COMMAND
        .then(Commands.literal("codex")
            .then(Commands.literal("unlockall")
                .then(Commands.argument("player", EntityArgument.player())
                    .executes(this::executeSelect))
                .executes(this::executeSelf))));
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
    Set<String> allEntryIds = CodexRegistry.getAll().stream()
        .map(CodexEntry::id)
        .collect(Collectors.toSet());

    CodexUnlockManager.unlockEntries(player, allEntryIds);

    context.getSource().sendSuccess(
        () -> Component.translatable("command.crawlingmysteries.codex.unlockall.success", allEntryIds.size(),
            player.getName()),
        true);

    return allEntryIds.size();
  }

}
