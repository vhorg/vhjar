package iskallia.vault.quest.base;

import iskallia.vault.config.entry.DescriptionData;
import iskallia.vault.core.vault.QuestManager;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.world.storage.VirtualWorld;
import net.minecraft.resources.ResourceLocation;

public abstract class InVaultQuest extends Quest {
   protected InVaultQuest(
      String type,
      String id,
      String name,
      DescriptionData descriptionData,
      ResourceLocation icon,
      ResourceLocation targetId,
      float targetProgress,
      String unlockedBy,
      Quest.QuestReward reward
   ) {
      super(type, id, name, descriptionData, icon, targetId, targetProgress, unlockedBy, reward);
   }

   public abstract void initServer(QuestManager var1, VirtualWorld var2, Vault var3);
}
