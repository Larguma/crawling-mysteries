package dev.larguma.crawlingmysteries.command.trinket;

import java.util.Optional;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import dev.larguma.crawlingmysteries.command.helper.CommandHelper;
import dev.larguma.crawlingmysteries.data.ModDataComponents;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class GetTrinketData {

  public GetTrinketData(CommandDispatcher<CommandSourceStack> dispatcher) {
    dispatcher.register(CommandHelper.MOD_COMMAND
        .then(Commands.literal("trinket")
            .then(Commands.literal("getdata")
                .then(Commands.argument("player", EntityArgument.player())
                    .then(Commands.argument("trinket_item", StringArgumentType.string())
                        .suggests(CommandHelper.TRINKET_SUGGESTIONS)
                        .then(Commands.argument("data_component", StringArgumentType.string())
                            .suggests(CommandHelper.DATA_COMPONENT_SUGGESTIONS)
                            .executes(this::execute)))))));
  }

  private int execute(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
    ServerPlayer player = EntityArgument.getPlayer(context, "player");
    String trinketItemId = StringArgumentType.getString(context, "trinket_item");
    String dataComponent = StringArgumentType.getString(context, "data_component");

    Optional<ItemStack> foundStackOptional = CommandHelper.checkTrinketCommandValid(context, player, trinketItemId,
        dataComponent);
    if (foundStackOptional.isEmpty()) {
      return 0;
    }

    ItemStack foundStack = foundStackOptional.get();
    Object value = ModDataComponents.getComponentValue(foundStack, dataComponent);

    context.getSource().sendSuccess(
        () -> Component.translatable("command.crawlingmysteries.spell.trinketdata.get.success", player.getName(),
            trinketItemId, dataComponent, String.valueOf(value)),
        true);
    return 1;
  }
}
