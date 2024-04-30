package larguma.crawling_mysteries.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import larguma.crawling_mysteries.CrawlingMysteries;
import larguma.crawling_mysteries.util.IEntityDataSaver;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;

@Mixin(Entity.class)
public abstract class ModEntityDataSaverMixin implements IEntityDataSaver {
  private NbtCompound persistentData;

  @Override
  public NbtCompound getPersistentData() {
    if (this.persistentData == null) {
      this.persistentData = new NbtCompound();
    }
    return persistentData;
  }

  @Inject(method = "writeNbt", at = @At("HEAD"))
  protected void injectWriteMethod(NbtCompound nbt, CallbackInfoReturnable<Void> info) {
    if (persistentData != null) {
      nbt.put(CrawlingMysteries.MOD_ID + "_data", persistentData);
    }
  }

  @Inject(method = "readNbt", at = @At("HEAD"))
  protected void injectReadMethod(NbtCompound nbt, CallbackInfo info) {
    if (nbt.contains(CrawlingMysteries.MOD_ID + "_data", 10)) {
      persistentData = nbt.getCompound(CrawlingMysteries.MOD_ID + "_data");
    }
  }
}