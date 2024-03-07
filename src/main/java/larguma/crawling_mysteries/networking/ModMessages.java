package larguma.crawling_mysteries.networking;

import larguma.crawling_mysteries.CrawlingMysteries;
import larguma.crawling_mysteries.networking.packet.c2s.EternalGuardianMaskEffectC2SPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;

public class ModMessages {
   public static final Identifier ETERNAL_GUARDIAN_MASK_EFFECT_ID = new Identifier(CrawlingMysteries.MOD_ID, "eternal_guardian_mask_effect");

    public static void registerC2SPackets() {
        ServerPlayNetworking.registerGlobalReceiver(ETERNAL_GUARDIAN_MASK_EFFECT_ID, EternalGuardianMaskEffectC2SPacket::receive);
    }

    public static void registerS2CPackets() {

    }
}
