package dev.larguma.crawlingmysteries.util;

import java.util.Iterator;
import java.util.UUID;

import javax.annotation.Nullable;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

public class NbtHelper {
  /**
   * Reads a GameProfile from NBT.
   */
  @Nullable
  public static GameProfile toGameProfile(CompoundTag nbt) {
    UUID uUID = nbt.hasUUID("Id") ? nbt.getUUID("Id") : Util.NIL_UUID;
    String name = nbt.getString("Name");

    try {
      GameProfile gameProfile = new GameProfile(uUID, name);
      if (nbt.contains("Properties", 10)) {
        CompoundTag propertiesCompound = nbt.getCompound("Properties");
        Iterator<String> iterator = propertiesCompound.getAllKeys().iterator();

        while (iterator.hasNext()) {
          String key = iterator.next();
          ListTag nbtList = propertiesCompound.getList(key, 10);

          for (int i = 0; i < nbtList.size(); ++i) {
            CompoundTag currentNbtCompound = nbtList.getCompound(i);
            String value = currentNbtCompound.getString("Value");
            if (currentNbtCompound.contains("Signature", 8)) {
              gameProfile.getProperties().put(key,
                  new Property(key, value, currentNbtCompound.getString("Signature")));
            } else {
              gameProfile.getProperties().put(key, new Property(key, value));
            }
          }
        }
      }

      return gameProfile;
    } catch (Exception e) {
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
      Iterator<String> propertyKeyIterator = profile.getProperties().keySet().iterator();

      while (propertyKeyIterator.hasNext()) {
        String propertyKey = propertyKeyIterator.next();
        ListTag nbtList = new ListTag();

        CompoundTag nbtCompoundData;
        for (Iterator<Property> propertyIterator = profile.getProperties().get(propertyKey).iterator(); propertyIterator
            .hasNext(); nbtList.add(nbtCompoundData)) {
          Property property = propertyIterator.next();
          nbtCompoundData = new CompoundTag();
          nbtCompoundData.putString("Value", property.value());
          String propertySignature = property.signature();
          if (propertySignature != null) {
            nbtCompoundData.putString("Signature", propertySignature);
          }
        }

        nbtCompound.put(propertyKey, nbtList);
      }

      nbt.put("Properties", nbtCompound);
    }

    return nbt;
  }

  /**
   * Converts a BlockPos to a CompoundTag
   */
  public static CompoundTag fromBlockPos(BlockPos pos) {
    CompoundTag tag = new CompoundTag();
    tag.putInt("X", pos.getX());
    tag.putInt("Y", pos.getY());
    tag.putInt("Z", pos.getZ());
    return tag;
  }

  /**
   * Converts a CompoundTag to a BlockPos
   */
  public static BlockPos toBlockPos(CompoundTag tag) {
    return new BlockPos(tag.getInt("X"), tag.getInt("Y"), tag.getInt("Z"));
  }
}
