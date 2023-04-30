package iskallia.vault.core.vault;

import iskallia.vault.config.quest.QuestConfig;
import iskallia.vault.core.SkyVaultsChunkGenerator;
import iskallia.vault.core.data.DataObject;
import iskallia.vault.core.data.key.registry.FieldRegistry;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.quest.base.InVaultQuest;
import net.minecraftforge.server.ServerLifecycleHooks;

public class QuestManager extends DataObject<QuestManager> {
   public static final FieldRegistry FIELDS = new FieldRegistry();

   @Override
   public FieldRegistry getFields() {
      return FIELDS;
   }

   public void initServer(VirtualWorld world, Vault vault) {
      if (!world.isClientSide) {
         QuestConfig config = ModConfigs.QUESTS;
         if (SkyVaultsChunkGenerator.matches(ServerLifecycleHooks.getCurrentServer().overworld())) {
            config = ModConfigs.SKY_QUESTS;
         }

         config.getQuests().forEach(quest -> {
            if (quest instanceof InVaultQuest inVaultQuest) {
               inVaultQuest.initServer(this, world, vault);
            }
         });
      }
   }

   public void releaseServer() {
      CommonEvents.release(this);
   }
}
