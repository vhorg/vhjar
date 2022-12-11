package iskallia.vault.core.vault.objective;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.block.VaultCrateBlock;
import iskallia.vault.config.LegacyLootTablesConfig;
import iskallia.vault.core.Version;
import iskallia.vault.core.data.adapter.Adapter;
import iskallia.vault.core.data.compound.UUIDList;
import iskallia.vault.core.data.key.FieldKey;
import iskallia.vault.core.data.key.LootTableKey;
import iskallia.vault.core.data.key.SupplierKey;
import iskallia.vault.core.data.key.registry.FieldRegistry;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.event.common.CrateAwardEvent;
import iskallia.vault.core.random.ChunkRandom;
import iskallia.vault.core.vault.CrateLootGenerator;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.VaultRegistry;
import iskallia.vault.core.vault.player.Listener;
import iskallia.vault.core.vault.player.Runner;
import iskallia.vault.core.vault.stat.StatCollector;
import iskallia.vault.core.world.loot.LootTable;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.init.ModConfigs;
import java.util.List;
import net.minecraft.core.NonNullList;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class AwardCrateObjective extends Objective {
   public static final SupplierKey<Objective> KEY = SupplierKey.of("award_crate", Objective.class).with(Version.v1_0, AwardCrateObjective::new);
   public static final FieldRegistry FIELDS = Objective.FIELDS.merge(new FieldRegistry());
   public static final FieldKey<VaultCrateBlock.Type> TYPE = FieldKey.of("type", VaultCrateBlock.Type.class)
      .with(Version.v1_0, Adapter.ofEnum(VaultCrateBlock.Type.class), DISK.all())
      .register(FIELDS);
   public static final FieldKey<LootTableKey> LOOT_TABLE = FieldKey.of("loot_table", LootTableKey.class)
      .with(Version.v1_0, Adapter.<LootTableKey, LootTable>ofRegistryKey(() -> VaultRegistry.LOOT_TABLE).asNullable(), DISK.all())
      .register(FIELDS);
   public static final FieldKey<Void> ADD_ARTIFACT = FieldKey.of("add_artifact", Void.class).with(Version.v1_0, Adapter.ofVoid(), DISK.all()).register(FIELDS);
   public static final FieldKey<Float> ARTIFACT_CHANCE = FieldKey.of("artifact_chance", Float.class)
      .with(Version.v1_0, Adapter.ofFloat(), DISK.all())
      .register(FIELDS);
   public static final FieldKey<Void> AWARDED = FieldKey.of("awarded", Void.class).with(Version.v1_0, Adapter.ofVoid(), DISK.all()).register(FIELDS);
   public static final FieldKey<UUIDList> AWARDED_PLAYERS = FieldKey.of("awarded_players", UUIDList.class)
      .with(Version.v1_1, Adapter.ofCompound(), DISK.all(), UUIDList::create)
      .register(FIELDS);

   protected AwardCrateObjective() {
   }

   public static AwardCrateObjective ofConfig(VaultCrateBlock.Type type, String id, int level, boolean hasArtifact) {
      LegacyLootTablesConfig.Level config = ModConfigs.LOOT_TABLES.getForLevel(level);
      AwardCrateObjective objective = new AwardCrateObjective();
      objective.set(TYPE, type).set(LOOT_TABLE, config == null ? null : VaultRegistry.LOOT_TABLE.getKey(config.getCompletionCrate(id)));
      if (hasArtifact) {
         objective.set(ADD_ARTIFACT).set(ARTIFACT_CHANCE, Float.valueOf(config == null ? 0.0F : config.getArtifactChance()));
      }

      objective.set(AWARDED_PLAYERS, UUIDList.create());
      return objective;
   }

   @Override
   public SupplierKey<Objective> getKey() {
      return KEY;
   }

   @Override
   public FieldRegistry getFields() {
      return FIELDS;
   }

   @Override
   public void tickServer(VirtualWorld world, Vault vault) {
      if (vault.get(Vault.VERSION) == Version.v1_0 && !this.has(AWARDED)) {
         ChunkRandom random = ChunkRandom.any();
         random.setSeed(vault.get(Vault.SEED));
         vault.ifPresent(Vault.LISTENERS, listeners -> {
            for (Listener listener : listeners.getAll()) {
               if (listener instanceof Runner) {
                  this.awardCrate(vault, listener, random);
               }
            }
         });
         this.set(AWARDED);
      }

      super.tickServer(world, vault);
   }

   @Override
   public void tickListener(VirtualWorld world, Vault vault, Listener listener) {
      if (listener instanceof Runner && this.has(AWARDED_PLAYERS) && !this.get(AWARDED_PLAYERS).contains(listener.get(Listener.ID))) {
         ChunkRandom random = ChunkRandom.any();
         random.setSeed(vault.get(Vault.SEED) ^ listener.getId().getLeastSignificantBits());
         this.awardCrate(vault, listener, random);
         this.get(AWARDED_PLAYERS).add(listener.get(Listener.ID));
      }

      super.tickListener(world, vault, listener);
   }

   private void awardCrate(Vault vault, Listener listener, ChunkRandom random) {
      if (vault.has(Vault.STATS)) {
         StatCollector stats = vault.get(Vault.STATS).get(listener.get(Listener.ID));
         if (stats != null) {
            float xpMul = Mth.clamp(stats.getExpMultiplier(), 0.0F, 1.0F);
            float artifactChance = this.get(ARTIFACT_CHANCE) * xpMul;
            CrateLootGenerator crateLootGenerator = new CrateLootGenerator(this.get(LOOT_TABLE), this.has(ADD_ARTIFACT), artifactChance);
            VaultCrateBlock.Type crateType = this.get(TYPE);
            listener.getPlayer()
               .ifPresent(
                  player -> CommonEvents.CRATE_AWARD_EVENT
                     .invoke(
                        player, ItemStack.EMPTY, crateLootGenerator, vault, listener, crateType, List.of(), Version.latest(), random, CrateAwardEvent.Phase.PRE
                     )
               );
            NonNullList<ItemStack> items = crateLootGenerator.generate(vault, listener, random);
            ItemStack crate = VaultCrateBlock.getCrateWithLoot(crateType, items);
            listener.getPlayer()
               .ifPresent(
                  player -> CommonEvents.CRATE_AWARD_EVENT
                     .invoke(player, crate, crateLootGenerator, vault, listener, crateType, items, Version.latest(), random, CrateAwardEvent.Phase.POST)
               );
            stats.get(StatCollector.REWARD).add(crate);
         }
      }
   }

   @Override
   public boolean render(PoseStack matrixStack, Window window, float partialTicks, Player player) {
      return false;
   }

   @Override
   public boolean isActive(Vault vault, Objective objective) {
      return objective == this;
   }
}
