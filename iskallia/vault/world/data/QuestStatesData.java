package iskallia.vault.world.data;

import iskallia.vault.core.SkyVaultsChunkGenerator;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.quest.QuestState;
import iskallia.vault.quest.base.InVaultQuest;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber
public class QuestStatesData extends SavedData {
   protected static final String DATA_NAME = "the_vault_QuestStates";
   private boolean isSkyVaultWorld = false;
   private final Map<UUID, QuestState> STATES = new HashMap<>();

   public QuestStatesData() {
   }

   public QuestStatesData(CompoundTag tag) {
      this.load(tag);
   }

   public void registerQuestEvents() {
      if (SkyVaultsChunkGenerator.matches(ServerLifecycleHooks.getCurrentServer().overworld())) {
         ModConfigs.SKY_QUESTS.getQuests().stream().filter(quest -> !(quest instanceof InVaultQuest)).forEach(MinecraftForge.EVENT_BUS::register);
      } else {
         ModConfigs.QUESTS.getQuests().stream().filter(quest -> !(quest instanceof InVaultQuest)).forEach(MinecraftForge.EVENT_BUS::register);
      }
   }

   public boolean isSkyVaultWorld() {
      return this.isSkyVaultWorld;
   }

   public QuestState getState(ServerPlayer player) {
      return this.getState(player.getUUID());
   }

   private QuestState getState(UUID playerId) {
      if (!this.STATES.containsKey(playerId)) {
         QuestState state = new QuestState(playerId);
         this.STATES.put(playerId, state);
         this.setDirty();
      }

      return this.STATES.get(playerId);
   }

   public static QuestStatesData create(CompoundTag tag) {
      return new QuestStatesData(tag);
   }

   public void load(@NotNull CompoundTag tag) {
      this.isSkyVaultWorld = tag.getBoolean("isSkyVaultWorld");

      for (String key : tag.getAllKeys()) {
         if (!key.equals("isSkyVaultWorld")) {
            UUID playerId = UUID.fromString(key);
            this.getState(playerId).deserializeNBT(tag.getCompound(key));
         }
      }

      this.setDirty();
   }

   @NotNull
   public CompoundTag save(@NotNull CompoundTag tag) {
      tag.putBoolean("isSkyVaultWorld", this.isSkyVaultWorld);
      this.STATES.forEach((uuid, questState) -> tag.put(uuid.toString(), questState.serializeNBT()));
      return tag;
   }

   public static QuestStatesData get() {
      return (QuestStatesData)ServerLifecycleHooks.getCurrentServer()
         .overworld()
         .getDataStorage()
         .computeIfAbsent(QuestStatesData::create, QuestStatesData::new, "the_vault_QuestStates");
   }

   @SubscribeEvent
   public static void onPlayerJoin(PlayerLoggedInEvent event) {
      if (event.getPlayer() instanceof ServerPlayer player) {
         get().getState(player).initialize();
      }
   }

   @SubscribeEvent
   public static void onWorldLoad(Load event) {
      if (event.getWorld() instanceof ServerLevel serverLevel) {
         if (serverLevel.equals(ServerLifecycleHooks.getCurrentServer().overworld())) {
            get().registerQuestEvents();
         }
      }
   }
}
