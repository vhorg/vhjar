package iskallia.vault.core.vault.objective;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.block.VaultCrateBlock;
import iskallia.vault.config.LegacyLootTablesConfig;
import iskallia.vault.core.Version;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.basic.EnumAdapter;
import iskallia.vault.core.data.adapter.vault.CompoundAdapter;
import iskallia.vault.core.data.adapter.vault.RegistryKeyAdapter;
import iskallia.vault.core.data.compound.ItemStackList;
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
import iskallia.vault.core.vault.VaultLevel;
import iskallia.vault.core.vault.VaultRegistry;
import iskallia.vault.core.vault.player.Listener;
import iskallia.vault.core.vault.player.Runner;
import iskallia.vault.core.vault.stat.StatCollector;
import iskallia.vault.core.world.loot.LootTable;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.init.ModConfigs;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.NonNullList;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class AwardCrateObjective extends Objective {
   public static final SupplierKey<Objective> KEY = SupplierKey.of("award_crate", Objective.class).with(Version.v1_0, AwardCrateObjective::new);
   public static final FieldRegistry FIELDS = Objective.FIELDS.merge(new FieldRegistry());
   public static final FieldKey<VaultCrateBlock.Type> TYPE = FieldKey.of("type", VaultCrateBlock.Type.class)
      .with(Version.v1_0, Adapters.ofEnum(VaultCrateBlock.Type.class, EnumAdapter.Mode.ORDINAL), DISK.all())
      .register(FIELDS);
   public static final FieldKey<LootTableKey> LOOT_TABLE = FieldKey.of("loot_table", LootTableKey.class)
      .with(Version.v1_0, RegistryKeyAdapter.<LootTableKey, LootTable>of(() -> VaultRegistry.LOOT_TABLE).asNullable(), DISK.all())
      .register(FIELDS);
   public static final FieldKey<Float> ITEM_QUANTITY = FieldKey.of("item_quantity", Float.class)
      .with(Version.v1_23, Adapters.FLOAT, DISK.all())
      .register(FIELDS);
   public static final FieldKey<Void> ADD_ARTIFACT = FieldKey.of("add_artifact", Void.class).with(Version.v1_0, Adapters.ofVoid(), DISK.all()).register(FIELDS);
   public static final FieldKey<Float> ARTIFACT_CHANCE = FieldKey.of("artifact_chance", Float.class)
      .with(Version.v1_0, Adapters.FLOAT, DISK.all())
      .register(FIELDS);
   public static final FieldKey<Void> AWARDED = FieldKey.of("awarded", Void.class).with(Version.v1_0, Adapters.ofVoid(), DISK.all()).register(FIELDS);
   public static final FieldKey<UUIDList> AWARDED_PLAYERS = FieldKey.of("awarded_players", UUIDList.class)
      .with(Version.v1_1, CompoundAdapter.of(UUIDList::create), DISK.all())
      .register(FIELDS);
   public static final FieldKey<ItemStackList> ADDITIONAL_ITEMS = FieldKey.of("additional_items", ItemStackList.class)
      .with(Version.v1_28, CompoundAdapter.of(ItemStackList::create), DISK.all())
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

   protected void awardCrate(Vault vault, Listener listener, ChunkRandom random) {
      if (vault.has(Vault.STATS)) {
         StatCollector stats = vault.get(Vault.STATS).get(listener.get(Listener.ID));
         if (stats != null) {
            int level = vault.getOptional(Vault.LEVEL).map(VaultLevel::get).orElse(0);
            float xpMul = Mth.clamp(stats.getExpMultiplier(), 0.0F, 1.0F);
            float artifactChance = this.get(ARTIFACT_CHANCE) * xpMul;
            List<ItemStack> additional = new ArrayList<>(this.getOr(ADDITIONAL_ITEMS, ItemStackList.create()));
            if (listener instanceof Runner runner) {
               additional.addAll(runner.getOr(Runner.ADDITIONAL_CRATE_ITEMS, ItemStackList.create()));
            }

            CrateLootGenerator crateLootGenerator = new CrateLootGenerator(
               this.get(LOOT_TABLE), this.getOr(ITEM_QUANTITY, Float.valueOf(0.0F)), additional, this.has(ADD_ARTIFACT), artifactChance
            );
            VaultCrateBlock.Type crateType = this.get(TYPE);
            listener.getPlayer()
               .ifPresent(
                  player -> CommonEvents.CRATE_AWARD_EVENT
                     .invoke(
                        player,
                        ItemStack.EMPTY,
                        crateLootGenerator,
                        vault,
                        listener,
                        crateType,
                        List.of(),
                        vault.get(Vault.VERSION),
                        random,
                        CrateAwardEvent.Phase.PRE
                     )
               );
            NonNullList<ItemStack> items = crateLootGenerator.generate(vault, listener, random);
            ItemStack crate = VaultCrateBlock.getCrateWithLootWithAntiques(crateType, level, items);
            listener.getPlayer()
               .ifPresent(
                  player -> CommonEvents.CRATE_AWARD_EVENT
                     .invoke(player, crate, crateLootGenerator, vault, listener, crateType, items, vault.get(Vault.VERSION), random, CrateAwardEvent.Phase.POST)
               );
            stats.get(StatCollector.REWARD).add(crate);
         }
      }
   }

   @Override
   public boolean render(Vault vault, PoseStack matrixStack, Window window, float partialTicks, Player player) {
      return false;
   }

   @Override
   public boolean isActive(VirtualWorld world, Vault vault, Objective objective) {
      return objective == this;
   }
}
