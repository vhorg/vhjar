package iskallia.vault.quest.type;

import iskallia.vault.config.entry.DescriptionData;
import iskallia.vault.event.event.ForgeGearEvent;
import iskallia.vault.quest.base.Quest;
import iskallia.vault.world.data.QuestStatesData;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ForgeGearQuest extends Quest {
   public static final String FORGE_GEAR = "forge_gear";

   public ForgeGearQuest(
      String id,
      String name,
      DescriptionData descriptionData,
      ResourceLocation icon,
      ResourceLocation targetId,
      float targetProgress,
      String unlockedBy,
      Quest.QuestReward reward
   ) {
      super("forge_gear", id, name, descriptionData, icon, targetId, targetProgress, unlockedBy, reward);
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public void onForgeGear(ForgeGearEvent event) {
      if (event.getPlayer() instanceof ServerPlayer player) {
         if (!event.getRecipeId().equals(this.targetId)) {
            return;
         }

         if (!QuestStatesData.get().getState(player).getInProgress().contains(this.id)) {
            return;
         }

         this.progress(player, 1.0F);
      }
   }

   @Override
   public MutableComponent getTypeDescription() {
      return new TextComponent("Forge Vault Gear");
   }
}
