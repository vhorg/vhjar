package iskallia.vault.util;

import net.minecraft.advancements.Advancement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.level.ServerPlayer;

public class AdvancementHelper {
   public static boolean grantCriterion(ServerPlayer player, ResourceLocation advancementId, String criterion) {
      MinecraftServer server = player.getServer();
      if (server == null) {
         return false;
      } else {
         ServerAdvancementManager advancementManager = server.getAdvancements();
         Advancement advancement = advancementManager.getAdvancement(advancementId);
         if (advancement == null) {
            return false;
         } else {
            player.getAdvancements().award(advancement, criterion);
            return true;
         }
      }
   }
}
