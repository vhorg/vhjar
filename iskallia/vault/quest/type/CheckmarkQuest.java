package iskallia.vault.quest.type;

import iskallia.vault.quest.base.Quest;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;

public class CheckmarkQuest extends Quest {
   public static final String CHECKMARK = "checkmark";

   public CheckmarkQuest(
      String id,
      String name,
      Quest.DescriptionData descriptionData,
      ResourceLocation icon,
      ResourceLocation targetId,
      float targetProgress,
      String unlockedBy,
      Quest.QuestReward reward
   ) {
      super("checkmark", id, name, descriptionData, icon, targetId, targetProgress, unlockedBy, reward);
   }

   @Override
   public MutableComponent getTypeDescription() {
      return new TextComponent("Read description and complete.");
   }
}
