package larguma.crawling_mysteries.networking;

import io.wispforest.owo.network.OwoNetChannel;
import larguma.crawling_mysteries.CrawlingMysteries;
import larguma.crawling_mysteries.networking.packet.KeycodePacket;
import larguma.crawling_mysteries.spell.SpellEffect;
import larguma.crawling_mysteries.util.SpellUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

public class ModMessages {

    public static final OwoNetChannel CHANNEL = OwoNetChannel.create(new Identifier(CrawlingMysteries.MOD_ID, "main"));

    public static final Identifier ETERNAL_GUARDIAN_MASK_EFFECT_ID = new Identifier(CrawlingMysteries.MOD_ID,
            "eternal_guardian_mask_effect");

    public static void init() {
        CHANNEL.registerServerbound(KeycodePacket.class, (message, access) -> {
            access.player().NBT
            SpellEffect spellEffect = SpellUtils.getSpellEffectFromInt(message.key());
            spellEffect.receive(access.player());
        });
    }

    @Environment(EnvType.CLIENT)
    public static final class Client {
        public static void init() {
        }
    }
}
