package dev.larguma.crawlingmysteries.util;

import java.util.Iterator;
import java.util.UUID;

import javax.annotation.Nullable;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

public class NbtHelper {
  @Nullable
  public static GameProfile toGameProfile(CompoundTag nbt) {
    UUID uUID = nbt.hasUUID("Id") ? nbt.getUUID("Id") : Util.NIL_UUID;
    String string = nbt.getString("Name");

    try {
      GameProfile gameProfile = new GameProfile(uUID, string);
      if (nbt.contains("Properties", 10)) {
        CompoundTag nbtCompound = nbt.getCompound("Properties");
        Iterator<String> var5 = nbtCompound.getAllKeys().iterator();

        while (var5.hasNext()) {
          String string2 = (String) var5.next();
          ListTag nbtList = nbtCompound.getList(string2, 10);

          for (int i = 0; i < nbtList.size(); ++i) {
            CompoundTag nbtCompound2 = nbtList.getCompound(i);
            String string3 = nbtCompound2.getString("Value");
            if (nbtCompound2.contains("Signature", 8)) {
              gameProfile.getProperties().put(string2,
                  new Property(string2, string3, nbtCompound2.getString("Signature")));
            } else {
              gameProfile.getProperties().put(string2, new Property(string2, string3));
            }
          }
        }
      }

      return gameProfile;
    } catch (Throwable var11) {
      return null;
    }
  }

  public static CompoundTag writeGameProfile(CompoundTag nbt, GameProfile profile) {
    if (!profile.getName().isEmpty()) {
      nbt.putString("Name", profile.getName());
    }

    if (!profile.getId().equals(Util.NIL_UUID)) {
      nbt.putUUID("Id", profile.getId());
    }

    if (!profile.getProperties().isEmpty()) {
      CompoundTag nbtCompound = new CompoundTag();
      Iterator<String> var3 = profile.getProperties().keySet().iterator();

      while (var3.hasNext()) {
        String string = (String) var3.next();
        ListTag nbtList = new ListTag();

        CompoundTag nbtCompound2;
        for (Iterator<Property> var6 = profile.getProperties().get(string).iterator(); var6.hasNext(); nbtList
            .add(nbtCompound2)) {
          Property property = (Property) var6.next();
          nbtCompound2 = new CompoundTag();
          nbtCompound2.putString("Value", property.value());
          String string2 = property.signature();
          if (string2 != null) {
            nbtCompound2.putString("Signature", string2);
          }
        }

        nbtCompound.put(string, nbtList);
      }

      nbt.put("Properties", nbtCompound);
    }

    return nbt;
  }
}
