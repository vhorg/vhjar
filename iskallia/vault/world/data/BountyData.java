package iskallia.vault.world.data;

import iskallia.vault.bounty.Bounty;
import iskallia.vault.bounty.BountyList;
import iskallia.vault.bounty.TaskRegistry;
import iskallia.vault.bounty.TaskReward;
import iskallia.vault.bounty.task.ItemDiscoveryTask;
import iskallia.vault.bounty.task.Task;
import iskallia.vault.bounty.task.properties.TaskProperties;
import iskallia.vault.config.bounty.task.TaskConfig;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.util.nbt.NBTHelper;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber(
   bus = Bus.FORGE
)
public class BountyData extends SavedData {
   protected static final String DATA_NAME = "the_vault_Bounties";
   private final HashMap<UUID, BountyList> active = new HashMap<>();
   private final HashMap<UUID, BountyList> available = new HashMap<>();
   private final HashMap<UUID, BountyList> complete = new HashMap<>();

   private Optional<Bounty> getActiveFor(UUID playerId, UUID bountyId) {
      return !this.active.containsKey(playerId) ? Optional.empty() : this.active.get(playerId).findById(bountyId);
   }

   public BountyList getAllAvailableFor(UUID playerId) {
      if (this.available.containsKey(playerId)) {
         return this.available.get(playerId);
      } else {
         BountyList list = this.available.computeIfAbsent(playerId, id -> new BountyList());
         this.setDirty();
         return list;
      }
   }

   public BountyList getAllActiveFor(UUID playerId) {
      if (this.active.containsKey(playerId)) {
         return this.active.get(playerId);
      } else {
         BountyList list = this.active.computeIfAbsent(playerId, id -> new BountyList());
         this.setDirty();
         return list;
      }
   }

   public BountyList getAllCompletedFor(UUID playerId) {
      if (this.complete.containsKey(playerId)) {
         return this.complete.get(playerId);
      } else {
         BountyList list = this.complete.computeIfAbsent(playerId, id -> new BountyList());
         this.setDirty();
         return list;
      }
   }

   public BountyList getAllBountiesFor(UUID playerId) {
      BountyList list = new BountyList();
      list.addAll((Collection<? extends Bounty>)this.active.computeIfAbsent(playerId, id -> new BountyList()));
      list.addAll((Collection<? extends Bounty>)this.available.computeIfAbsent(playerId, id -> new BountyList()));
      list.addAll((Collection<? extends Bounty>)this.complete.computeIfAbsent(playerId, id -> new BountyList()));
      return list;
   }

   public CompoundTag getAllBountiesAsTagFor(UUID playerId) {
      CompoundTag bounties = new CompoundTag();
      bounties.put("active", this.getAllActiveFor(playerId).serializeNBT());
      bounties.put("available", this.getAllAvailableFor(playerId).serializeNBT());
      bounties.put("abandoned", this.getAllCompletedFor(playerId).serializeNBT());
      return bounties;
   }

   public <T extends Task<?>> List<T> getAllActiveTasksById(ServerPlayer player, ResourceLocation taskId) {
      return this.active
         .values()
         .stream()
         .flatMap(Collection::stream)
         .filter(bounty -> bounty.getPlayerId().equals(player.getUUID()))
         .map(Bounty::getTask)
         .filter(task -> task.getTaskType().equals(taskId))
         .toList();
   }

   public void resetAvailableBountiesFor(UUID playerId) {
      this.available.remove(playerId);
      int totalBounties = 3;
      int amountToGenerate = totalBounties - this.getAllActiveFor(playerId).size() - this.getAllCompletedFor(playerId).size();
      this.available.put(playerId, this.generate(playerId, amountToGenerate));
      this.setDirty();
   }

   private Bounty generateBounty(UUID playerId) {
      int vaultLevel = PlayerVaultStatsData.get(ServerLifecycleHooks.getCurrentServer()).getVaultStats(playerId).getVaultLevel();
      ResourceLocation taskId = ModConfigs.BOUNTY_CONFIG.getRandomTask();
      TaskConfig<?, ?> config = TaskConfig.getConfig(taskId);
      TaskProperties properties = config.getGeneratedTaskProperties(vaultLevel);
      TaskReward reward = ModConfigs.REWARD_CONFIG.generateReward(vaultLevel);
      UUID bountyId = UUID.randomUUID();
      return new Bounty(bountyId, playerId, TaskRegistry.createTask(taskId, bountyId, properties, reward));
   }

   private BountyList generate(UUID playerId, int amount) {
      BountyList list = new BountyList();

      for (int i = 0; i < amount; i++) {
         list.add(this.generateBounty(playerId));
      }

      return list;
   }

   public void setActive(UUID playerId, UUID bountyId) {
      BountyList active = this.getAllActiveFor(playerId);
      if (active.size() <= 0) {
         BountyList available = this.getAllAvailableFor(playerId);
         Optional<Bounty> bountyOptional = available.findById(bountyId);
         if (!bountyOptional.isEmpty()) {
            if (available.removeById(bountyId)) {
               active.add(bountyOptional.get());
               this.setDirty();
            }
         }
      }
   }

   public void abandon(UUID playerId, UUID bountyId) {
      Optional<Bounty> activeBounty = this.getActiveFor(playerId, bountyId);
      if (!activeBounty.isEmpty()) {
         Bounty active = activeBounty.get();
         active.setExpiration(Instant.now().plus(ModConfigs.BOUNTY_CONFIG.getAbandonedPenaltySeconds(), ChronoUnit.SECONDS).toEpochMilli());
         this.getAllActiveFor(playerId).removeById(bountyId);
         this.getAllCompletedFor(playerId).add(active);
         this.setDirty();
      }
   }

   public void complete(ServerPlayer player, UUID bountyId) {
      UUID playerId = player.getUUID();
      Optional<Bounty> activeBounty = this.getActiveFor(playerId, bountyId);
      if (!activeBounty.isEmpty()) {
         Bounty active = activeBounty.get();
         active.setExpiration(Instant.now().plus(ModConfigs.BOUNTY_CONFIG.getWaitingPeriodSeconds(), ChronoUnit.SECONDS).toEpochMilli());
         this.getAllActiveFor(playerId).removeById(bountyId);
         this.getAllCompletedFor(playerId).add(active);
         active.getTask().getTaskReward().apply(player);
         this.setDirty();
      }
   }

   public void reroll(ServerPlayer player, UUID bountyId) {
      UUID playerId = player.getUUID();
      this.getAllActiveFor(playerId).removeById(bountyId);
      this.getAllAvailableFor(playerId).removeById(bountyId);
      this.getAllCompletedFor(playerId).removeById(bountyId);
      this.getAllAvailableFor(playerId).add(this.generateBounty(playerId));
      this.setDirty();
   }

   private boolean playerExists(UUID uuid) {
      return this.active.containsKey(uuid) || this.available.containsKey(uuid) || this.complete.containsKey(uuid);
   }

   private void clearAll() {
      this.available.clear();
      this.active.clear();
      this.complete.clear();
   }

   public static BountyData create(CompoundTag nbt) {
      BountyData data = new BountyData();
      CommonEvents.ENTITY_DROPS.register(data, ItemDiscoveryTask::onLootGeneration, -1);
      CommonEvents.CHEST_LOOT_GENERATION.post().register(data, ItemDiscoveryTask::onLootGeneration, -1);
      CommonEvents.COIN_STACK_LOOT_GENERATION.post().register(data, ItemDiscoveryTask::onLootGeneration, -1);
      CommonEvents.LOOTABLE_BLOCK_GENERATION_EVENT.post().register(data, ItemDiscoveryTask::onLootGeneration, -1);
      data.load(nbt);
      return data;
   }

   private void load(CompoundTag tag) {
      this.clearAll();
      CompoundTag availableTag = tag.getCompound("available");
      availableTag.getAllKeys().forEach(idString -> {
         UUID playerId = UUID.fromString(idString);
         List<Bounty> list = NBTHelper.readList(availableTag, idString, CompoundTag.class, Bounty::new);
         this.available.put(playerId, new BountyList(list));
      });
      CompoundTag activeTag = tag.getCompound("active");
      activeTag.getAllKeys().forEach(idString -> {
         UUID playerId = UUID.fromString(idString);
         List<Bounty> list = NBTHelper.readList(activeTag, idString, CompoundTag.class, Bounty::new);
         this.active.put(playerId, new BountyList(list));
      });
      CompoundTag completeTag = tag.getCompound("complete");
      completeTag.getAllKeys().forEach(idString -> {
         UUID playerId = UUID.fromString(idString);
         List<Bounty> list = NBTHelper.readList(completeTag, idString, CompoundTag.class, Bounty::new);
         this.complete.put(playerId, new BountyList(list));
      });
   }

   @NotNull
   public CompoundTag save(CompoundTag nbt) {
      CompoundTag availableTag = new CompoundTag();
      CompoundTag activeTag = new CompoundTag();
      CompoundTag completeTag = new CompoundTag();
      this.available
         .forEach((playerId, bountyList) -> NBTHelper.writeCollection(availableTag, playerId.toString(), bountyList, CompoundTag.class, Bounty::serializeNBT));
      this.active
         .forEach((playerId, bountyList) -> NBTHelper.writeCollection(activeTag, playerId.toString(), bountyList, CompoundTag.class, Bounty::serializeNBT));
      this.complete
         .forEach((playerId, bountyList) -> NBTHelper.writeCollection(completeTag, playerId.toString(), bountyList, CompoundTag.class, Bounty::serializeNBT));
      nbt.put("available", availableTag);
      nbt.put("active", activeTag);
      nbt.put("complete", completeTag);
      return nbt;
   }

   @SubscribeEvent
   public static void onPlayerJoin(PlayerLoggedInEvent event) {
      if (!event.getPlayer().getLevel().isClientSide()) {
         if (!get().playerExists(event.getPlayer().getUUID())) {
            get().resetAvailableBountiesFor(event.getPlayer().getUUID());
         }
      }
   }

   @SubscribeEvent
   public static void onServerTick(ServerTickEvent event) {
      if (event.phase == Phase.END) {
         get().tick();
      }
   }

   public void tick() {
      MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
      if (server.overworld().getGameTime() % 20L == 0L) {
         this.checkAbandonedExpiration();
      }
   }

   private void checkAbandonedExpiration() {
      if (!this.complete.isEmpty()) {
         for (Bounty bounty : this.complete.values().stream().flatMap(Collection::stream).filter(Bounty::isExpired).toList()) {
            UUID bountyId = bounty.getId();
            UUID playerId = bounty.getPlayerId();
            BountyList completedBounties = this.complete.get(playerId);
            if (completedBounties.removeById(bountyId)) {
               this.getAllAvailableFor(playerId).add(this.generateBounty(playerId));
               this.setDirty();
            }
         }
      }
   }

   public void resetAllBounties(UUID uuid) {
      this.active.computeIfAbsent(uuid, id -> new BountyList()).clear();
      this.available.computeIfAbsent(uuid, id -> new BountyList()).clear();
      this.complete.computeIfAbsent(uuid, id -> new BountyList()).clear();
      this.resetAvailableBountiesFor(uuid);
      this.setDirty();
   }

   public static BountyData get() {
      return (BountyData)ServerLifecycleHooks.getCurrentServer()
         .overworld()
         .getDataStorage()
         .computeIfAbsent(BountyData::create, BountyData::new, "the_vault_Bounties");
   }
}
