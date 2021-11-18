package iskallia.vault.util;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementManager;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;

public class AdvancementHelper {
   public static boolean grantCriterion(ServerPlayerEntity player, ResourceLocation advancementId, String criterion) {
      MinecraftServer server = player.func_184102_h();
      if (server == null) {
         return false;
      } else {
         AdvancementManager advancementManager = server.func_191949_aK();
         Advancement advancement = advancementManager.func_192778_a(advancementId);
         if (advancement == null) {
            return false;
         } else {
            player.func_192039_O().func_192750_a(advancement, criterion);
            return true;
         }
      }
   }
}
