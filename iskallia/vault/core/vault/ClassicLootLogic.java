package iskallia.vault.core.vault;

import iskallia.vault.block.VaultChestBlock;
import iskallia.vault.core.Version;
import iskallia.vault.core.data.adapter.Adapter;
import iskallia.vault.core.data.key.FieldKey;
import iskallia.vault.core.data.key.SupplierKey;
import iskallia.vault.core.data.key.registry.FieldRegistry;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.event.common.ChestGenerationEvent;
import iskallia.vault.core.util.WeightedList;
import iskallia.vault.core.vault.stat.ChestStat;
import iskallia.vault.core.vault.stat.StatCollector;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.gear.DataTransferItem;
import iskallia.vault.item.gear.VaultLootItem;
import iskallia.vault.snapshot.AttributeSnapshot;
import iskallia.vault.snapshot.AttributeSnapshotHelper;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

public class ClassicLootLogic extends LootLogic {
   public static final SupplierKey<LootLogic> KEY = SupplierKey.of("classic", LootLogic.class).with(Version.v1_0, ClassicLootLogic::new);
   public static final FieldRegistry FIELDS = LootLogic.FIELDS.merge(new FieldRegistry());
   public static final FieldKey<Void> ADD_CATALYST_FRAGMENTS = FieldKey.of("add_catalyst_fragments", Void.class)
      .with(Version.v1_0, Adapter.ofVoid(), DISK.all())
      .register(FIELDS);

   @Override
   public SupplierKey<LootLogic> getKey() {
      return KEY;
   }

   @Override
   public FieldRegistry getFields() {
      return FIELDS;
   }

   @Override
   protected void onPreGenerate(VirtualWorld world, Vault vault, ChestGenerationEvent.Data data) {
      if (this.applyTrap(world, vault, data)) {
         data.setLootTable(null);
         world.setBlock(data.getPos(), Blocks.AIR.defaultBlockState(), 3);
      }
   }

   @Override
   protected void onPostGenerate(VirtualWorld world, Vault vault, ChestGenerationEvent.Data data) {
      this.initLootData(vault, data);
      this.generateCatalystFragments(data);
   }

   protected boolean applyTrap(VirtualWorld world, Vault vault, ChestGenerationEvent.Data data) {
      boolean canBeTrapped = data.getState().getBlock() == ModBlocks.WOODEN_CHEST
         || data.getState().getBlock() == ModBlocks.GILDED_CHEST
         || data.getState().getBlock() == ModBlocks.LIVING_CHEST
         || data.getState().getBlock() == ModBlocks.ORNATE_CHEST;
      if (!canBeTrapped) {
         return false;
      } else {
         AttributeSnapshot snapshot = AttributeSnapshotHelper.getInstance().getSnapshot(data.getPlayer());
         float disarmChance = snapshot.getAttributeValue(ModGearAttributes.TRAP_DISARMING, VaultGearAttributeTypeMerger.floatSum());
         if (data.getRandom().nextFloat() < disarmChance) {
            return false;
         } else {
            double probability = ModConfigs.VAULT_CHEST.getTrapProbability(vault.get(Vault.LEVEL).get());
            WeightedList<String> pool = ModConfigs.VAULT_CHEST.getEffectPool(vault.get(Vault.LEVEL).get());
            probability = CommonEvents.CHEST_TRAP_GENERATION.invoke(data.getPlayer(), probability, pool).getProbability();
            if (!(data.getRandom().nextFloat() >= probability) && pool != null) {
               pool.getRandom(data.getRandom()).map(ModConfigs.VAULT_CHEST::getEffectByName).ifPresent(effect -> effect.apply(world, vault, data.getPlayer()));
               vault.getOptional(Vault.STATS)
                  .map(c -> c.get(data.getPlayer().getUUID()))
                  .ifPresent(stats -> stats.get(StatCollector.CHESTS).add(ChestStat.ofTrapped(((VaultChestBlock)data.getState().getBlock()).getType())));
               return true;
            } else {
               return false;
            }
         }
      }
   }

   private void initLootData(Vault vault, ChestGenerationEvent.Data data) {
      List<ItemStack> loot = data.getLoot();
      loot.forEach(stackx -> {
         if (stackx.getItem() instanceof VaultLootItem lootItemx) {
            lootItemx.initializeLoot(vault, stackx);
         }
      });

      for (int i = 0; i < loot.size(); i++) {
         ItemStack stack = loot.get(i);
         if (stack.getItem() instanceof DataTransferItem lootItem) {
            loot.set(i, lootItem.convertStack(stack, data.getRandom()));
         }
      }
   }

   protected void generateCatalystFragments(ChestGenerationEvent.Data data) {
      if (this.has(ADD_CATALYST_FRAGMENTS)) {
         double probability = ModConfigs.VAULT_CHEST_META.getCatalystChance(data.getState().getBlock(), data.getRarity());
         probability = CommonEvents.CHEST_CATALYST_GENERATION.invoke(data.getPlayer(), probability).getProbability();
         if (data.getRandom().nextFloat() < probability) {
            data.getLoot().add(new ItemStack(ModItems.VAULT_CATALYST_FRAGMENT));
         }
      }
   }
}
