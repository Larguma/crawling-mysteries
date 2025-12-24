package dev.larguma.crawlingmysteries.datagen.sound;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import dev.larguma.crawlingmysteries.sound.ModSounds;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.SoundDefinition;
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider;

public class ModSoundDefinitionProvider extends SoundDefinitionsProvider {

  public ModSoundDefinitionProvider(PackOutput output, ExistingFileHelper helper) {
    super(output, CrawlingMysteries.MOD_ID, helper);
  }

  @Override
  public void registerSounds() {
    add(ModSounds.OST_01, SoundDefinition.definition().with(sound(CrawlingMysteries.MOD_ID + ":ost_01").stream(true)));
    add(ModSounds.BEER_POUR, SoundDefinition.definition().with(sound(CrawlingMysteries.MOD_ID + ":beer_pour").volume(0.5f)));
    add(ModSounds.BEER_DRINK, SoundDefinition.definition().with(sound(CrawlingMysteries.MOD_ID + ":beer_drink")));
  }

}
