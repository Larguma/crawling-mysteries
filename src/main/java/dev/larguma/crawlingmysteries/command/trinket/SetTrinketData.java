package dev.larguma.crawlingmysteries.command.trinket;

import java.util.Optional;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import dev.larguma.crawlingmysteries.command.helper.CommandHelper;
import dev.larguma.crawlingmysteries.data.ModDataComponents;
import dev.larguma.crawlingmysteries.data.ModDataComponents.ComponentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class SetTrinketData {

  public SetTrinketData(CommandDispatcher<CommandSourceStack> dispatcher) {
    dispatcher.register(CommandHelper.MOD_COMMAND
        .then(Commands.literal("trinket")
            .then(Commands.literal("setdata")
                .then(Commands.argument("player", EntityArgument.player())
                    .then(Commands.argument("trinket_item", StringArgumentType.string())
                        .suggests(CommandHelper.TRINKET_SUGGESTIONS)
                        .then(Commands.argument("data_component", StringArgumentType.string())
                            .suggests(CommandHelper.DATA_COMPONENT_SUGGESTIONS)
                            .then(Commands.argument("value", StringArgumentType.greedyString())
                                .executes(this::execute))))))));
  }

  private int execute(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
    ServerPlayer player = EntityArgument.getPlayer(context, "player");
    String trinketId = StringArgumentType.getString(context, "trinket_item");
    String dataComponent = StringArgumentType.getString(context, "data_component");
    String valueStr = StringArgumentType.getString(context, "value");

    Optional<ItemStack> foundStackOptional = CommandHelper.checkTrinketCommandValid(context, player, trinketId,
        dataComponent);
    if (foundStackOptional.isEmpty()) {
      return 0;
    }

    ItemStack foundStack = foundStackOptional.get();
    boolean success = ModDataComponents.setComponentValue(foundStack, dataComponent, valueStr);

    if (success) {
      context.getSource().sendSuccess(
          () -> Component.translatable("command.crawlingmysteries.spell.trinketdata.set.success", dataComponent,
              valueStr, player.getName(), trinketId),
          true);
      return 1;
    } else {
      ComponentType expectedType = ModDataComponents.getComponentType(dataComponent);
      context.getSource().sendFailure(
          Component.translatable("command.crawlingmysteries.spell.trinketdata.set.failure", dataComponent,
              expectedType != null ? expectedType.getTypeName() : "unknown"));
      return 0;
    }
  }
}
