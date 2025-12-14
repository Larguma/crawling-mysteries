package dev.larguma.crawlingmysteries.command.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import dev.larguma.crawlingmysteries.data.ModDataComponents;
import dev.larguma.crawlingmysteries.item.helper.ItemHelper;
import dev.larguma.crawlingmysteries.spell.ModSpells;
import dev.larguma.crawlingmysteries.spell.Spell;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class CommandHelper {

  public static final LiteralArgumentBuilder<CommandSourceStack> MOD_COMMAND = Commands
      .literal(CrawlingMysteries.MOD_ID)
      .requires(player -> player.hasPermission(2));

  // #region Suggestions

  public static final SuggestionProvider<CommandSourceStack> TRINKET_SUGGESTIONS = (context, builder) -> {
    List<String> trinketIds = new ArrayList<>(ModSpells.getAllSpells().stream()
        .map(Spell::sourceItem)
        .distinct()
        .map(spell -> "\"" + spell.toString() + "\"")
        .toList());

    // Trinkets without spells but with data components
    trinketIds.add("\"crawlingmysteries:eternal_guardians_band\"");

    return SharedSuggestionProvider.suggest(trinketIds, builder);
  };

  public static final SuggestionProvider<CommandSourceStack> DATA_COMPONENT_SUGGESTIONS = (context, builder) -> {
    return SharedSuggestionProvider.suggest(ModDataComponents.getComponentNames(), builder);
  };

  public static final SuggestionProvider<CommandSourceStack> SPELL_SUGGESTIONS = (context, builder) -> {
    List<String> spellIds = ModSpells.getAllSpells().stream()
        .map(Spell::id)
        .toList();

    return SharedSuggestionProvider.suggest(spellIds, builder);
  };

  // #endregion Suggestions

  // #region Spell Component Helpers

  public static Optional<ItemStack> checkTrinketCommandValid(final CommandContext<CommandSourceStack> context,
      ServerPlayer player, String trinketItemId) {
    ResourceLocation trinketId;
    if (trinketItemId.contains(":")) {
      trinketId = ResourceLocation.parse(trinketItemId);
    } else {
      trinketId = ResourceLocation.fromNamespaceAndPath(CrawlingMysteries.MOD_ID, trinketItemId);
    }

    Optional<Item> itemOptional = BuiltInRegistries.ITEM.getOptional(trinketId);
    if (itemOptional.isEmpty()) {
      context.getSource().sendFailure(
          Component.translatable("command.crawlingmysteries.spell.trinketdata.item_not_found", trinketItemId));
      return Optional.empty();
    }

    Item targetItem = itemOptional.get();
    ItemStack foundStack = ItemHelper.findEquippedTrinket(player, targetItem);
    if (foundStack.isEmpty()) {
      context.getSource().sendFailure(
          Component.translatable("command.crawlingmysteries.spell.trinketdata.trinket_not_equipped",
              player.getName(), trinketItemId));
      return Optional.empty();
    }
    return Optional.of(foundStack);
  }

  public static Optional<ItemStack> checkTrinketCommandValid(final CommandContext<CommandSourceStack> context,
      ServerPlayer player, String trinketItemId, String dataComponent) {

    Optional<ItemStack> foundStack = checkTrinketCommandValid(context, player, trinketItemId);

    if (!ModDataComponents.hasComponent(dataComponent)) {
      context.getSource().sendFailure(
          Component.translatable("command.crawlingmysteries.spell.trinketdata.component_not_found", dataComponent,
              String.join(", ", ModDataComponents.getComponentNames())));
      return Optional.empty();
    }
    return foundStack;
  }

  // #endregion Trinket Data Component Helpers

}
