package iskallia.vault.event;

import iskallia.vault.dynamodel.model.armor.ArmorModel;
import iskallia.vault.init.ModDynamicModels;
import iskallia.vault.world.data.DiscoveredModelsData;
import java.util.UUID;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   bus = Bus.FORGE
)
public class SpecialEvents {
   @SubscribeEvent
   public static void joiningWorldEvent(PlayerLoggedInEvent event) {
      String name = event.getPlayer().getDisplayName().getString();
      ServerLevel level = (ServerLevel)event.getPlayer().getLevel();
      UUID playerUUID = event.getPlayer().getUUID();
      ArmorModel armorType = null;
      if (name.equalsIgnoreCase("LupiCanis")) {
         armorType = ModDynamicModels.Armor.LUPICANIS;
      } else if (name.equalsIgnoreCase("Guybrrush")) {
         armorType = ModDynamicModels.Armor.GUYBRUSH;
      } else if (name.equalsIgnoreCase("SilentFoxxy")) {
         armorType = ModDynamicModels.Armor.SILENTFOXXY;
      }

      if (armorType != null) {
         DiscoveredModelsData discoversData = DiscoveredModelsData.get(level);
         ResourceLocation modelId = armorType.getId();
         if (!discoversData.getDiscoveredModels(playerUUID).contains(modelId)) {
            discoversData.discoverAllArmorPieceAndBroadcast(event.getPlayer(), armorType);
         }
      }
   }
}
