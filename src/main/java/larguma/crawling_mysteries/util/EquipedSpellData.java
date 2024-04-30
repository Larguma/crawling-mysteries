package larguma.crawling_mysteries.util;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtList;

public class EquipedSpellData {
  public static void addSpell(IEntityDataSaver player, int slotIndex, int spellId) {
    NbtCompound data = player.getPersistentData();
    NbtList spells = data.getList("spells", 3);
    spells.set(slotIndex, NbtInt.of(spellId));
  }

  public static int getSpell(IEntityDataSaver player, int slotIndex) {
    NbtCompound data = player.getPersistentData();
    NbtList spells = data.getList("spells", 3);
    return spells.getInt(slotIndex);
  }
}
