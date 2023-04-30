package iskallia.vault.util;

import iskallia.vault.VaultMod;
import iskallia.vault.config.PlayerResearchTransferConfig;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.research.ResearchTree;
import iskallia.vault.research.type.Research;
import iskallia.vault.world.data.PlayerResearchesData;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class PlayerResearchTransferUtil {
   @SubscribeEvent
   public static void onPlayerLogin(PlayerLoggedInEvent event) {
      if (event.getPlayer() instanceof ServerPlayer player) {
         PlayerResearchTransferConfig var12 = ModConfigs.PLAYER_RESEARCH_TRANSFER;
         PlayerResearchesData data = PlayerResearchesData.get(player.getLevel());
         ResearchTree researches = data.getResearches(player);

         for (String oldResearch : researches.getResearchesDone().stream().filter(var12.getRemovedResearches()::contains).toList()) {
            Optional<String> newResearchOptional = var12.getNewResearch(oldResearch);
            if (newResearchOptional.isEmpty()) {
               VaultMod.LOGGER.debug("Attempted to remove [{}] but found no new research to replace it with.", oldResearch);
            } else {
               String newResearch = newResearchOptional.get();
               Research remove = ModConfigs.RESEARCHES.getByName(oldResearch);
               if (remove == null) {
                  VaultMod.LOGGER.debug("Attempted to remove [{}] but found no registered research with that name.", oldResearch);
               } else {
                  Research add = ModConfigs.RESEARCHES.getByName(newResearch);
                  if (add == null) {
                     VaultMod.LOGGER.debug("Attempted to remove [{}] but found no registered research with the new name [{}].", oldResearch, newResearch);
                  } else {
                     data.removeResearch(player, remove);
                     data.research(player, add);
                     VaultMod.LOGGER.debug("Removed research [{}] and replaced it with [{}].", oldResearch, newResearch);
                     player.sendMessage(
                        new TextComponent("Due to necessary changes in the pack you have had the research [")
                           .append(oldResearch)
                           .append("] removed and [")
                           .append(newResearch)
                           .append("] added in its place.")
                           .withStyle(ChatFormatting.AQUA),
                        Util.NIL_UUID
                     );
                  }
               }
            }
         }
      }
   }
}
