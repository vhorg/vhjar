package iskallia.vault.quest.type;

import iskallia.vault.config.entry.DescriptionData;
import iskallia.vault.event.event.GearModificationEvent;
import iskallia.vault.quest.base.Quest;
import iskallia.vault.world.data.QuestStatesData;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ModifyGearQuest extends Quest {
   public static final String MODIFY_GEAR = "modify_gear";

   public ModifyGearQuest(
      String id,
      String name,
      DescriptionData descriptionData,
      ResourceLocation icon,
      ResourceLocation targetId,
      float targetProgress,
      String unlockedBy,
      Quest.QuestReward reward
   ) {
      super("modify_gear", id, name, descriptionData, icon, targetId, targetProgress, unlockedBy, reward);
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public void onForgeGear(GearModificationEvent event) {
      if (event.getPlayer() instanceof ServerPlayer player) {
         ResourceLocation modificationId = event.getModification().getRegistryName();
         if (modificationId == null || !modificationId.equals(this.targetId)) {
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
