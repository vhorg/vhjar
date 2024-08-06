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
import iskallia.vault.event.event.BountyCompleteEvent;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.bounty.ClientboundBountyAvailableMessage;
import iskallia.vault.network.message.bounty.ClientboundBountyProgressMessage;
import iskallia.vault.skill.base.Skill;
import iskallia.vault.skill.expertise.type.BountyHunterExpertise;
import iskallia.vault.skill.tree.ExpertiseTree;
import iskallia.vault.util.nbt.NBTHelper;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
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
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.network.NetworkDirection;
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
   private final HashMap<UUID, BountyList> legendary = new HashMap<>();

   private Optional<Bounty> getActiveFor(UUID playerId, UUID bountyId) {
      return !this.active.containsKey(playerId) ? Optional.empty() : this.active.get(playerId).findById(bountyId);
   }

   public BountyList getAllAvailableFor(UUID playerId) {
      if (this.available.containsKey(playerId)) {
         return this.available.get(playerId);
      } else {
         BountyList list = this.available.computeIfAbsent(playerId, id -> new BountyList());
         this.setDirty();
         this.syncToClient(playerId);
         return list;
      }
   }

   public BountyList getAllActiveFor(UUID playerId) {
      if (this.active.containsKey(playerId)) {
         return this.active.get(playerId);
      } else {
         BountyList list = this.active.computeIfAbsent(playerId, id -> new BountyList());
         this.setDirty();
         this.syncToClient(playerId);
         return list;
      }
   }

   public BountyList getAllCompletedFor(UUID playerId) {
      if (this.complete.containsKey(playerId)) {
         return this.complete.get(playerId);
      } else {
         BountyList list = this.complete.computeIfAbsent(playerId, id -> new BountyList());
         this.setDirty();
         this.syncToClient(playerId);
         return list;
      }
   }

   private Optional<Bounty> getLegendaryFor(UUID playerId, UUID bountyId) {
      return !this.legendary.containsKey(playerId) ? Optional.empty() : this.legendary.get(playerId).findById(bountyId);
   }

   public BountyList getAllLegendaryFor(UUID playerId) {
      if (this.legendary.containsKey(playerId)) {
         return this.legendary.get(playerId);
      } else {
         BountyList list = this.legendary.computeIfAbsent(playerId, id -> new BountyList());
         this.setDirty();
         this.syncToClient(playerId);
         return list;
      }
   }

   public BountyList getAllBountiesFor(UUID playerId) {
      BountyList list = new BountyList();
      list.addAll((Collection<? extends Bounty>)this.active.computeIfAbsent(playerId, id -> new BountyList()));
      list.addAll((Collection<? extends Bounty>)this.available.computeIfAbsent(playerId, id -> new BountyList()));
      list.addAll((Collection<? extends Bounty>)this.complete.computeIfAbsent(playerId, id -> new BountyList()));
      list.addAll((Collection<? extends Bounty>)this.legendary.computeIfAbsent(playerId, id -> new BountyList()));
      return list;
   }

   public CompoundTag getAllBountiesAsTagFor(UUID playerId) {
      CompoundTag bounties = new CompoundTag();
      bounties.put("active", this.getAllActiveFor(playerId).serializeNBT());
      bounties.put("available", this.getAllAvailableFor(playerId).serializeNBT());
      bounties.put("abandoned", this.getAllCompletedFor(playerId).serializeNBT());
      bounties.put("legendary", this.getAllLegendaryFor(playerId).serializeNBT());
      return bounties;
   }

   public <T extends Task<?>> List<T> getAllActiveById(ServerPlayer player, ResourceLocation taskId) {
      return this.active
         .values()
         .stream()
         .flatMap(Collection::stream)
         .filter(bounty -> bounty.getPlayerId().equals(player.getUUID()))
         .map(Bounty::getTask)
         .filter(task -> task.getTaskType().equals(taskId))
         .toList();
   }

   public <T extends Task<?>> List<T> getAllLegendaryById(ServerPlayer player, ResourceLocation taskId) {
      return this.legendary
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
      this.syncToClient(playerId);
   }

   private Bounty generateBounty(UUID playerId) {
      int vaultLevel = PlayerVaultStatsData.get(ServerLifecycleHooks.getCurrentServer()).getVaultStats(playerId).getVaultLevel();
      ResourceLocation taskId = ModConfigs.BOUNTY_CONFIG.getRandomTask();
      TaskConfig<?, ?> config = TaskConfig.getConfig(taskId);
      TaskProperties properties = config.getGeneratedTaskProperties(vaultLevel);
      TaskReward reward = ModConfigs.REWARD_CONFIG.generateReward(vaultLevel, properties.getRewardPool());
      UUID bountyId = UUID.randomUUID();
      return new Bounty(bountyId, playerId, TaskRegistry.createTask(taskId, bountyId, properties, reward));
   }

   private Bounty generateLegendaryBounty(UUID playerId) {
      int vaultLevel = PlayerVaultStatsData.get(ServerLifecycleHooks.getCurrentServer()).getVaultStats(playerId).getVaultLevel();
      ResourceLocation taskId = ModConfigs.BOUNTY_CONFIG.getRandomTask();
      TaskConfig<?, ?> config = TaskConfig.getConfig(taskId);
      TaskProperties properties = config.getGeneratedTaskProperties(vaultLevel);
      properties.setRewardPool("legendary");
      TaskReward reward = ModConfigs.REWARD_CONFIG.generateReward(vaultLevel, properties.getRewardPool());
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

   public void setActive(ServerPlayer player, UUID bountyId) {
      BountyList active = this.getAllActiveFor(player.getUUID());
      ExpertiseTree expertises = PlayerExpertisesData.get(player.getLevel()).getExpertises(player);
      int maxActive = 0;

      for (BountyHunterExpertise expertise : expertises.getAll(BountyHunterExpertise.class, Skill::isUnlocked)) {
         maxActive += expertise.getMaxActive();
      }

      if (maxActive == 0) {
         maxActive = 1;
      }

      if (active.size() < maxActive) {
         BountyList available = this.getAllAvailableFor(player.getUUID());
         Optional<Bounty> bountyOptional = available.findById(bountyId);
         if (!bountyOptional.isEmpty()) {
            if (available.removeById(bountyId)) {
               active.add(bountyOptional.get());
               this.setDirty();
               this.syncToClient(player.getUUID());
            }
         }
      }
   }

   public void abandon(ServerPlayer player, UUID bountyId) {
      UUID playerId = player.getUUID();
      Optional<Bounty> legendaryBounty = this.getLegendaryFor(playerId, bountyId);
      if (legendaryBounty.isPresent()) {
         this.getAllLegendaryFor(playerId).removeById(bountyId);
         this.setDirty();
         this.syncToClient(playerId);
      } else {
         Optional<Bounty> activeBounty = this.getActiveFor(playerId, bountyId);
         if (!activeBounty.isEmpty()) {
            ExpertiseTree expertises = PlayerExpertisesData.get(player.getLevel()).getExpertises(player);
            float abandonedPenaltyReduction = 0.0F;

            for (BountyHunterExpertise expertise : expertises.getAll(BountyHunterExpertise.class, Skill::isUnlocked)) {
               abandonedPenaltyReduction += expertise.getAbandonedPenaltyReduction();
            }

            Bounty active = activeBounty.get();
            active.setExpiration(
               Instant.now()
                  .plus((long)((float)ModConfigs.BOUNTY_CONFIG.getAbandonedPenaltySeconds() * (1.0F - abandonedPenaltyReduction)), ChronoUnit.SECONDS)
                  .toEpochMilli()
            );
            this.getAllActiveFor(playerId).removeById(bountyId);
            this.getAllCompletedFor(playerId).add(active);
            this.setDirty();
            this.syncToClient(playerId);
         }
      }
   }

   public void complete(ServerPlayer player, UUID bountyId) {
      UUID playerId = player.getUUID();
      Optional<Bounty> legendaryBounty = this.getLegendaryFor(playerId, bountyId);
      if (legendaryBounty.isPresent()) {
         this.getAllLegendaryFor(playerId).removeById(bountyId);
         legendaryBounty.get().getTask().getTaskReward().apply(player);
         this.setDirty();
         this.syncToClient(playerId);
         MinecraftForge.EVENT_BUS.post(new BountyCompleteEvent(player, legendaryBounty.get().getTask()));
      } else {
         Optional<Bounty> activeBounty = this.getActiveFor(playerId, bountyId);
         if (!activeBounty.isEmpty()) {
            ExpertiseTree expertises = PlayerExpertisesData.get(player.getLevel()).getExpertises(player);
            int waitingPeriodReduction = 0;

            for (BountyHunterExpertise expertise : expertises.getAll(BountyHunterExpertise.class, Skill::isUnlocked)) {
               waitingPeriodReduction += expertise.getWaitingPeriodReduction();
            }

            Bounty active = activeBounty.get();
            active.setExpiration(
               Instant.now().plus(Math.max(ModConfigs.BOUNTY_CONFIG.getWaitingPeriodSeconds() - waitingPeriodReduction, 0L), ChronoUnit.SECONDS).toEpochMilli()
            );
            this.getAllActiveFor(playerId).removeById(bountyId);
            this.getAllCompletedFor(playerId).add(active);
            active.getTask().getTaskReward().apply(player);
            this.setDirty();
            this.syncToClient(playerId);
            MinecraftForge.EVENT_BUS.post(new BountyCompleteEvent(player, active.getTask()));
         }
      }
   }

   public void reroll(ServerPlayer player, UUID bountyId) {
      UUID playerId = player.getUUID();
      this.getAllActiveFor(playerId).removeById(bountyId);
      this.getAllAvailableFor(playerId).removeById(bountyId);
      this.getAllCompletedFor(playerId).removeById(bountyId);
      this.getAllAvailableFor(playerId).add(this.generateBounty(playerId));
      this.setDirty();
      this.syncToClient(playerId);
   }

   public void setupLegendary(UUID playerId) {
      if (!this.legendary.containsKey(playerId) || this.legendary.get(playerId).isEmpty()) {
         BountyList list = new BountyList(List.of(this.generateLegendaryBounty(playerId)));
         this.legendary.put(playerId, list);
         this.setDirty();
         this.syncToClient(playerId);
      }
   }

   private boolean playerExists(UUID uuid) {
      return this.active.containsKey(uuid) || this.available.containsKey(uuid) || this.complete.containsKey(uuid);
   }

   private void clearAll() {
      this.available.clear();
      this.active.clear();
      this.complete.clear();
      this.legendary.clear();
   }

   public static BountyData create(CompoundTag nbt) {
      BountyData data = new BountyData();
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
      CompoundTag legendaryTag = tag.getCompound("legendary");
      legendaryTag.getAllKeys().forEach(idString -> {
         UUID playerId = UUID.fromString(idString);
         List<Bounty> list = NBTHelper.readList(legendaryTag, idString, CompoundTag.class, Bounty::new);
         this.legendary.put(playerId, new BountyList(list));
      });
   }

   @NotNull
   public CompoundTag save(CompoundTag nbt) {
      CompoundTag availableTag = new CompoundTag();
      CompoundTag activeTag = new CompoundTag();
      CompoundTag completeTag = new CompoundTag();
      CompoundTag legendaryTag = new CompoundTag();
      this.available
         .forEach((playerId, bountyList) -> NBTHelper.writeCollection(availableTag, playerId.toString(), bountyList, CompoundTag.class, Bounty::serializeNBT));
      this.active
         .forEach((playerId, bountyList) -> NBTHelper.writeCollection(activeTag, playerId.toString(), bountyList, CompoundTag.class, Bounty::serializeNBT));
      this.complete
         .forEach((playerId, bountyList) -> NBTHelper.writeCollection(completeTag, playerId.toString(), bountyList, CompoundTag.class, Bounty::serializeNBT));
      this.legendary
         .forEach((playerId, bountyList) -> NBTHelper.writeCollection(legendaryTag, playerId.toString(), bountyList, CompoundTag.class, Bounty::serializeNBT));
      nbt.put("available", availableTag);
      nbt.put("active", activeTag);
      nbt.put("complete", completeTag);
      nbt.put("legendary", legendaryTag);
      return nbt;
   }

   private void syncToClient(UUID playerId) {
      ServerPlayer serverPlayer = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(playerId);
      if (serverPlayer != null) {
         BountyList legendary = get().getAllLegendaryFor(serverPlayer.getUUID());
         BountyList active = get().getAllActiveFor(serverPlayer.getUUID());
         List<Bounty> available = get().getAllAvailableFor(serverPlayer.getUUID());
         List<Bounty> bounties = new ArrayList<>();
         bounties.addAll(legendary);
         bounties.addAll(active);
         ModNetwork.CHANNEL.sendTo(new ClientboundBountyProgressMessage(bounties), serverPlayer.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
         ModNetwork.CHANNEL.sendTo(new ClientboundBountyAvailableMessage(available), serverPlayer.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
      }
   }

   @SubscribeEvent
   public static void onPlayerJoin(PlayerLoggedInEvent event) {
      if (event.getPlayer() instanceof ServerPlayer serverPlayer) {
         if (get().playerExists(event.getPlayer().getUUID())) {
            syncBounties(serverPlayer);
         } else {
            get().resetAvailableBountiesFor(event.getPlayer().getUUID());
         }
      }
   }

   public static void syncBounties(ServerPlayer serverPlayer) {
      List<Bounty> bounties = new ArrayList<>();
      BountyList activeList = get().getAllActiveFor(serverPlayer.getUUID());
      if (!activeList.isEmpty()) {
         bounties.addAll(activeList);
      }

      BountyList legendaryList = get().getAllLegendaryFor(serverPlayer.getUUID());
      if (!legendaryList.isEmpty()) {
         bounties.addAll(legendaryList);
      }

      if (!bounties.isEmpty()) {
         ModNetwork.CHANNEL.sendTo(new ClientboundBountyProgressMessage(bounties), serverPlayer.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
      } else {
         ModNetwork.CHANNEL.sendTo(new ClientboundBountyProgressMessage(null), serverPlayer.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
      }

      ModNetwork.CHANNEL
         .sendTo(
            new ClientboundBountyAvailableMessage(get().getAllAvailableFor(serverPlayer.getUUID())),
            serverPlayer.connection.getConnection(),
            NetworkDirection.PLAY_TO_CLIENT
         );
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
               this.syncToClient(playerId);
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
      this.syncToClient(uuid);
   }

   @SubscribeEvent
   public static void onServerStarted(ServerStartedEvent event) {
      get().registerEvents();
   }

   @SubscribeEvent
   public static void onServerStopped(ServerStoppedEvent event) {
      get().deregisterEvents();
   }

   private void registerEvents() {
      CommonEvents.ENTITY_DROPS.register(this, ItemDiscoveryTask::onLootGeneration, -1);
      CommonEvents.CHEST_LOOT_GENERATION.post().register(this, ItemDiscoveryTask::onLootGeneration, -1);
      CommonEvents.COIN_STACK_LOOT_GENERATION.post().register(this, ItemDiscoveryTask::onLootGeneration, -1);
      CommonEvents.LOOTABLE_BLOCK_GENERATION_EVENT.post().register(this, ItemDiscoveryTask::onLootGeneration, -1);
   }

   private void deregisterEvents() {
      CommonEvents.ENTITY_DROPS.release(this);
      CommonEvents.CHEST_LOOT_GENERATION.post().release(this);
      CommonEvents.COIN_STACK_LOOT_GENERATION.post().release(this);
      CommonEvents.LOOTABLE_BLOCK_GENERATION_EVENT.post().release(this);
   }

   public static BountyData get() {
      return (BountyData)ServerLifecycleHooks.getCurrentServer()
         .overworld()
         .getDataStorage()
         .computeIfAbsent(BountyData::create, BountyData::new, "the_vault_Bounties");
   }
}
