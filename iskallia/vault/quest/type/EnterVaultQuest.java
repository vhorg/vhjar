package iskallia.vault.quest.type;

import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.vault.QuestManager;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.quest.base.InVaultQuest;
import iskallia.vault.quest.base.Quest;
import iskallia.vault.world.data.QuestStatesData;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;

public class EnterVaultQuest extends InVaultQuest {
   public static final String ENTER_VAULT = "enter_vault";

   public EnterVaultQuest(
      String id,
      String name,
      Quest.DescriptionData descriptionData,
      ResourceLocation icon,
      ResourceLocation targetId,
      float targetProgress,
      String unlockedBy,
      Quest.QuestReward reward
   ) {
      super("enter_vault", id, name, descriptionData, icon, targetId, targetProgress, unlockedBy, reward);
   }

   @Override
   public void initServer(QuestManager manager, VirtualWorld world, Vault vault) {
      CommonEvents.LISTENER_JOIN.register(manager, data -> data.getListener().getPlayer().ifPresent(serverPlayer -> {
         if (QuestStatesData.get().getState(serverPlayer).getInProgress().contains(this.id)) {
            this.progress(serverPlayer, 1.0F);
         }
      }));
   }

   @Override
   public MutableComponent getTypeDescription() {
      return new TextComponent("Enter a Vault");
   }
}
