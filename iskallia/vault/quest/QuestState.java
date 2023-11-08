package iskallia.vault.quest;

import com.google.common.collect.ImmutableSet;
import iskallia.vault.config.quest.QuestConfig;
import iskallia.vault.core.SkyVaultsChunkGenerator;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModGameRules;
import iskallia.vault.init.ModItems;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.ClientboundToastMessage;
import iskallia.vault.network.message.quest.QuestSyncMessage;
import iskallia.vault.quest.base.Quest;
import iskallia.vault.util.EntityHelper;
import iskallia.vault.util.nbt.NBTHelper;
import iskallia.vault.world.data.QuestStatesData;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.server.ServerLifecycleHooks;

public class QuestState implements INBTSerializable<CompoundTag> {
   protected UUID playerId;
   protected Map<String, Float> inProgress = new HashMap<>();
   protected Set<String> readyToComplete = new HashSet<>();
   protected Set<String> completed = new HashSet<>();
   private boolean isSkyVaultWorld;

   public QuestState(UUID playerId) {
      this.playerId = playerId;
   }

   public QuestState(CompoundTag tag) {
      this.deserializeNBT(tag);
   }

   public void initialize() {
      if (this.getServerPlayer() != null) {
         ServerLevel serverLevel = this.getServerPlayer().getLevel();
         if (!this.isInitialized()) {
            EntityHelper.giveItem(this.getServerPlayer(), ModItems.QUEST_BOOK.getDefaultInstance());
            this.<QuestConfig>getConfig(serverLevel).getQuests().stream().filter(quest -> quest.getUnlockedBy().isEmpty()).forEach(this::setInProgress);
         }

         this.addNewQuests(this.getServerPlayer());
         if (serverLevel.getGameRules().getBoolean(ModGameRules.QUEST_EXPERT_MODE)) {
            this.setExpertMode(serverLevel);
         }

         this.syncAndPersist();
      }
   }

   private void addNewQuests(ServerPlayer serverPlayer) {
      QuestConfig config = this.getConfig(serverPlayer.getLevel());
      config.getQuests()
         .stream()
         .filter(
            quest -> !this.getCompleted().contains(quest.getUnlockedBy())
               ? false
               : !this.getCompleted().contains(quest.getId())
                  && !this.getReadyToComplete().contains(quest.getId())
                  && !this.getInProgress().contains(quest.getId())
         )
         .forEach(this::setInProgress);
   }

   public void setExpertMode(ServerLevel level) {
      QuestConfig config = this.getConfig(level);

      for (Quest quest : config.getQuests()) {
         if (!this.isQuestActivated(quest.getId())) {
            this.setInProgress(quest);
         }
      }

      this.syncAndPersist();
   }

   private boolean isQuestActivated(String questId) {
      return this.getCompleted().contains(questId) || this.getReadyToComplete().contains(questId) || this.getInProgress().contains(questId);
   }

   public <C extends QuestConfig> C getConfig(ServerLevel level) {
      if (SkyVaultsChunkGenerator.matches(level)) {
         this.isSkyVaultWorld = true;
         return (C)ModConfigs.SKY_QUESTS;
      } else {
         return (C)ModConfigs.QUESTS;
      }
   }

   public boolean isInitialized() {
      return !this.getInProgress().isEmpty() || !this.getReadyToComplete().isEmpty() || !this.getCompleted().isEmpty();
   }

   public UUID getPlayerId() {
      return this.playerId;
   }

   public boolean isSkyVaultWorld() {
      return this.isSkyVaultWorld;
   }

   @Nullable
   private ServerPlayer getServerPlayer() {
      return ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(this.playerId);
   }

   public ImmutableSet<String> getInProgress() {
      return ImmutableSet.copyOf(this.inProgress.keySet());
   }

   public ImmutableSet<String> getReadyToComplete() {
      return ImmutableSet.copyOf(this.readyToComplete);
   }

   public ImmutableSet<String> getCompleted() {
      return ImmutableSet.copyOf(this.completed);
   }

   public void setInProgress(Quest quest) {
      this.inProgress.put(quest.getId(), 0.0F);
      this.syncAndPersist();
   }

   private void setReadyToComplete(Quest quest) {
      this.inProgress.remove(quest.getId());
      this.readyToComplete.add(quest.getId());
      if (this.getServerPlayer() != null) {
         ModNetwork.CHANNEL
            .sendTo(
               new ClientboundToastMessage(quest.getName(), "You have completed a quest!", quest.getIcon()),
               this.getServerPlayer().connection.getConnection(),
               NetworkDirection.PLAY_TO_CLIENT
            );
      }
   }

   public void setComplete(Quest quest) {
      ServerPlayer serverPlayer = this.getServerPlayer();
      if (serverPlayer != null) {
         String id = quest.getId();
         if (this.readyToComplete.contains(id)) {
            this.readyToComplete.remove(id);
            this.completed.add(id);
            Quest nextQuest = this.<QuestConfig>getConfig(serverPlayer.getLevel()).getNextQuest(quest);
            if (nextQuest != null
               && !this.getInProgress().contains(nextQuest.getId())
               && !this.getReadyToComplete().contains(nextQuest.getId())
               && !this.getCompleted().contains(nextQuest.getId())) {
               this.setInProgress(nextQuest);
            }

            quest.getReward().apply(serverPlayer);
            this.syncAndPersist();
         }
      }
   }

   public void addProgress(Quest quest, float amount) {
      String id = quest.getId();
      if (this.inProgress.containsKey(id)) {
         float progress = this.inProgress.merge(id, amount, (currentAmount, newAmount) -> Math.min(currentAmount + newAmount, quest.getTargetProgress()));
         if (progress >= quest.getTargetProgress()) {
            this.setReadyToComplete(quest);
         }

         this.syncAndPersist();
      }
   }

   public void syncAndPersist() {
      QuestStatesData questStatesData = QuestStatesData.get();
      questStatesData.setDirty();
      if (this.getServerPlayer() != null) {
         ModNetwork.CHANNEL.sendTo(new QuestSyncMessage(this), this.getServerPlayer().connection.connection, NetworkDirection.PLAY_TO_CLIENT);
      }
   }

   public CompoundTag serializeNBT() {
      CompoundTag tag = new CompoundTag();
      CompoundTag inProgressTag = new CompoundTag();

      for (String key : this.inProgress.keySet()) {
         inProgressTag.putFloat(key, this.inProgress.get(key));
      }

      NBTHelper.writeCollection(tag, "Completed", this.completed, StringTag.class, StringTag::valueOf);
      NBTHelper.writeCollection(tag, "ReadyToComplete", this.readyToComplete, StringTag.class, StringTag::valueOf);
      tag.put("InProgress", inProgressTag);
      tag.putBoolean("isSkyVaultWorld", this.isSkyVaultWorld);
      return tag;
   }

   public void deserializeNBT(CompoundTag tag) {
      this.completed.clear();
      this.readyToComplete.clear();
      this.inProgress.clear();
      NBTHelper.readCollection(tag, "Completed", StringTag.class, StringTag::getAsString, this.completed);
      NBTHelper.readCollection(tag, "ReadyToComplete", StringTag.class, StringTag::getAsString, this.readyToComplete);
      CompoundTag inProgressTag = tag.getCompound("InProgress");

      for (String key : inProgressTag.getAllKeys()) {
         this.inProgress.put(key, inProgressTag.getFloat(key));
      }

      this.isSkyVaultWorld = tag.getBoolean("isSkyVaultWorld");
   }

   public void reset() {
      this.completed.clear();
      this.readyToComplete.clear();
      this.inProgress.clear();
      this.initialize();
   }
}
