package iskallia.vault.core.vault.stat;

import iskallia.vault.block.VaultChestBlock;
import iskallia.vault.core.Version;
import iskallia.vault.core.data.DataObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.basic.EnumAdapter;
import iskallia.vault.core.data.adapter.vault.CompoundAdapter;
import iskallia.vault.core.data.compound.ItemStackList;
import iskallia.vault.core.data.key.FieldKey;
import iskallia.vault.core.data.key.registry.FieldRegistry;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.player.Completion;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.util.VaultRarity;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.List;
import java.util.UUID;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.EventPriority;

public class StatCollector extends DataObject<StatCollector> {
   public static final FieldRegistry FIELDS = new FieldRegistry();
   public static final FieldKey<ChestStat.List> CHESTS = FieldKey.of("chests", ChestStat.List.class)
      .with(Version.v1_0, CompoundAdapter.of(ChestStat.List::new), DISK.all())
      .register(FIELDS);
   public static final FieldKey<MinedBlocksStat> MINED_BLOCKS = FieldKey.of("mined_blocks", MinedBlocksStat.class)
      .with(Version.v1_0, CompoundAdapter.of(MinedBlocksStat::new), DISK.all())
      .register(FIELDS);
   public static final FieldKey<Integer> TREASURE_ROOMS_OPENED = FieldKey.of("treasure_rooms_opened", Integer.class)
      .with(Version.v1_0, Adapters.INT_SEGMENTED_7, DISK.all())
      .register(FIELDS);
   public static final FieldKey<MobsStat> MOBS = FieldKey.of("mobs", MobsStat.class)
      .with(Version.v1_0, CompoundAdapter.of(MobsStat::new), DISK.all())
      .register(FIELDS);
   public static final FieldKey<Completion> COMPLETION = FieldKey.of("completion", Completion.class)
      .with(Version.v1_0, Adapters.ofEnum(Completion.class, EnumAdapter.Mode.ORDINAL), DISK.all())
      .register(FIELDS);
   public static final FieldKey<Float> BONUS_EXP_MULTIPLIER = FieldKey.of("exp_multiplier", Float.class)
      .with(Version.v1_0, Adapters.FLOAT, DISK.all().or(CLIENT.all()))
      .register(FIELDS);
   public static final FieldKey<Float> OBJECTIVE_EXP_MULTIPLIER = FieldKey.of("objective_exp_multiplier", Float.class)
      .with(Version.v1_14, Adapters.FLOAT, DISK.all().or(CLIENT.all()))
      .register(FIELDS);
   public static final FieldKey<ItemStackList> REWARD = FieldKey.of("reward", ItemStackList.class)
      .with(Version.v1_0, CompoundAdapter.of(ItemStackList::createLegacy), DISK.all())
      .register(FIELDS);

   public StatCollector() {
      this.set(CHESTS, new ChestStat.List());
      this.set(MINED_BLOCKS, new MinedBlocksStat());
      this.set(TREASURE_ROOMS_OPENED, Integer.valueOf(0));
      this.set(MOBS, new MobsStat());
      this.set(COMPLETION, Completion.COMPLETED);
      this.set(BONUS_EXP_MULTIPLIER, Float.valueOf(1.0F));
      this.set(OBJECTIVE_EXP_MULTIPLIER, Float.valueOf(1.0F));
      this.set(REWARD, ItemStackList.createLegacy());
   }

   @Override
   public FieldRegistry getFields() {
      return FIELDS;
   }

   public void initServer(VirtualWorld world, Vault vault, UUID uuid) {
      CommonEvents.CHEST_LOOT_GENERATION.post().register(this, data -> {
         if (data.getPlayer().getUUID().equals(uuid)) {
            if (data.getState().getBlock() instanceof VaultChestBlock chest) {
               this.get(CHESTS).add(ChestStat.ofLoot(chest.getType(), data.getRarity()));
            }
         }
      });
      CommonEvents.PLAYER_MINE.register(this, EventPriority.LOW, event -> {
         if (event.getPlayer().getUUID().equals(uuid)) {
            this.get(MINED_BLOCKS).onMine(event.getState(), event.getPlayer());
         }
      });
      CommonEvents.TREASURE_ROOM_OPEN.register(this, data -> {
         if (data.getPlayer().getUUID().equals(uuid)) {
            this.modify(TREASURE_ROOMS_OPENED, count -> count + 1);
         }
      });
      CommonEvents.ENTITY_DEATH.register(this, event -> {
         Entity source = event.getSource().getEntity();
         if (source != null && source.getUUID().equals(uuid) && event.getEntity().level == world) {
            this.get(MOBS).onKilled(event.getEntity());
         }
      });
      CommonEvents.ENTITY_DAMAGE.register(this, event -> {
         Entity source = event.getSource().getEntity();
         if (source != null && source.getUUID().equals(uuid)) {
            if (event.getEntity().getLevel() == world) {
               this.get(MOBS).onDamageDealt(event.getEntity(), event.getAmount());
            }
         }
      });
      CommonEvents.ENTITY_DAMAGE.register(this, event -> {
         Entity source = event.getSource().getEntity();
         if (source != null && event.getEntity().getUUID().equals(uuid)) {
            if (event.getEntity().getLevel() == world) {
               this.get(MOBS).onDamageReceived(source, event.getAmount());
            }
         }
      });
   }

   public void releaseServer() {
      CommonEvents.release(this);
   }

   public int getLootedChests(VaultChestType type, VaultRarity rarity) {
      return (int)this.get(CHESTS)
         .stream()
         .filter(stat -> !stat.has(ChestStat.TRAPPED) && stat.get(ChestStat.TYPE) == type && stat.get(ChestStat.RARITY) == rarity)
         .count();
   }

   public int getTrappedChests(VaultChestType type) {
      return (int)this.get(CHESTS).stream().filter(stat -> stat.has(ChestStat.TRAPPED) && stat.get(ChestStat.TYPE) == type).count();
   }

   public Object2IntMap<ResourceLocation> getMinedBlocks() {
      Object2IntMap<ResourceLocation> result = new Object2IntOpenHashMap();
      this.get(MINED_BLOCKS).forEach((id, entry) -> result.put(id, entry.get(MinedBlocksStat.Entry.COUNT)));
      return result;
   }

   public float getExpMultiplier() {
      return this.get(BONUS_EXP_MULTIPLIER);
   }

   public List<ItemStack> getReward() {
      return this.get(REWARD).stream().toList();
   }

   public int getTreasureRoomsOpened() {
      return this.get(TREASURE_ROOMS_OPENED);
   }

   public Object2IntMap<ResourceLocation> getEntitiesKilled() {
      Object2IntMap<ResourceLocation> result = new Object2IntOpenHashMap();
      this.get(MOBS).forEach((id, entry) -> result.put(id, entry.get(MobsStat.Entry.KILLED)));
      return result;
   }

   public Object2FloatMap<ResourceLocation> getDamageDealt() {
      Object2FloatMap<ResourceLocation> result = new Object2FloatOpenHashMap();
      this.get(MOBS).forEach((id, entry) -> result.put(id, entry.get(MobsStat.Entry.DAMAGE_DEALT)));
      return result;
   }

   public Object2FloatMap<ResourceLocation> getDamageReceived() {
      Object2FloatMap<ResourceLocation> result = new Object2FloatOpenHashMap();
      this.get(MOBS).forEach((id, entry) -> result.put(id, entry.get(MobsStat.Entry.DAMAGE_RECEIVED)));
      return result;
   }

   public int getExperience(Vault vault) {
      int objectiveExp = ModConfigs.VAULT_STATS.getCompletionExperience(vault, this);
      int statsExp = ModConfigs.VAULT_STATS.getStatsExperience(this);
      return (int)(objectiveExp * this.getOr(OBJECTIVE_EXP_MULTIPLIER, this.get(BONUS_EXP_MULTIPLIER)) + statsExp * this.get(BONUS_EXP_MULTIPLIER));
   }

   public Completion getCompletion() {
      return this.get(COMPLETION);
   }
}
