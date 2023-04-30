package iskallia.vault.core.vault;

import iskallia.vault.block.VaultChestBlock;
import iskallia.vault.config.ShopPedestalConfig;
import iskallia.vault.container.oversized.OverSizedItemStack;
import iskallia.vault.core.Version;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.key.FieldKey;
import iskallia.vault.core.data.key.SupplierKey;
import iskallia.vault.core.data.key.registry.FieldRegistry;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.event.common.ChestGenerationEvent;
import iskallia.vault.core.event.common.LootableBlockGenerationEvent;
import iskallia.vault.core.event.common.ShopPedestalGenerationEvent;
import iskallia.vault.core.util.WeightedList;
import iskallia.vault.core.vault.stat.ChestStat;
import iskallia.vault.core.vault.stat.StatCollector;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.init.ModItems;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.init.ModSounds;
import iskallia.vault.network.message.TrappedMobChestParticlesMessage;
import iskallia.vault.snapshot.AttributeSnapshot;
import iskallia.vault.snapshot.AttributeSnapshotHelper;
import iskallia.vault.world.vault.chest.MobTrapEffect;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.network.PacketDistributor;

public class ClassicLootLogic extends LootLogic {
   public static final SupplierKey<LootLogic> KEY = SupplierKey.of("classic", LootLogic.class).with(Version.v1_0, ClassicLootLogic::new);
   public static final FieldRegistry FIELDS = LootLogic.FIELDS.merge(new FieldRegistry());
   public static final FieldKey<Void> ADD_CATALYST_FRAGMENTS = FieldKey.of("add_catalyst_fragments", Void.class)
      .with(Version.v1_0, Adapters.ofVoid(), DISK.all())
      .register(FIELDS);
   public static final FieldKey<Void> ADD_RUNES = FieldKey.of("add_runes", Void.class).with(Version.v1_11, Adapters.ofVoid(), DISK.all()).register(FIELDS);

   @Override
   public SupplierKey<LootLogic> getKey() {
      return KEY;
   }

   @Override
   public FieldRegistry getFields() {
      return FIELDS;
   }

   @Override
   protected void onChestPreGenerate(VirtualWorld world, Vault vault, ChestGenerationEvent.Data data) {
      if (this.applyTrap(world, vault, data)) {
         data.setLootTable(null);
         world.setBlock(data.getPos(), Blocks.AIR.defaultBlockState(), 3);
      }
   }

   @Override
   protected void onChestPostGenerate(VirtualWorld world, Vault vault, ChestGenerationEvent.Data data) {
      this.generateCatalystFragments(data, vault);
      this.generateRunes(data, vault);
      this.initializeLoot(vault, data.getLoot(), data.getPos(), data.getRandom());
   }

   @Override
   protected void onBlockPreGenerate(VirtualWorld world, Vault vault, LootableBlockGenerationEvent.Data data) {
   }

   @Override
   protected void onBlockPostGenerate(VirtualWorld world, Vault vault, LootableBlockGenerationEvent.Data data) {
      this.initializeLoot(vault, data.getLoot(), data.getPos(), data.getRandom());
   }

   @Override
   protected void onShopPedestalGenerate(VirtualWorld world, Vault vault, ShopPedestalGenerationEvent.Data data) {
      int level = vault.getOptional(Vault.LEVEL).map(VaultLevel::get).orElse(0);
      ShopPedestalConfig.ShopOffer offer = ModConfigs.SHOP_PEDESTAL.getForLevel(level, data.getRandom());
      if (offer != null && !offer.isEmpty()) {
         ItemStack stack = this.initializeLoot(vault, offer.offer().copy(), data.getPos(), data.getRandom());
         data.getTileEntity().setOffer(stack, OverSizedItemStack.of(offer.currency().overSizedStack()));
      }

      data.getTileEntity().setInitialized(true);
      data.getTileEntity().setChanged();
      world.sendBlockUpdated(data.getPos(), data.getState(), data.getState(), 3);
   }

   protected boolean applyTrap(VirtualWorld world, Vault vault, ChestGenerationEvent.Data data) {
      boolean canBeTrapped = data.getState().getBlock() == ModBlocks.WOODEN_CHEST
         || data.getState().getBlock() == ModBlocks.GILDED_CHEST
         || data.getState().getBlock() == ModBlocks.LIVING_CHEST
         || data.getState().getBlock() == ModBlocks.ORNATE_CHEST
         || data.getState().getBlock() == ModBlocks.GILDED_STRONGBOX
         || data.getState().getBlock() == ModBlocks.ORNATE_STRONGBOX
         || data.getState().getBlock() == ModBlocks.LIVING_STRONGBOX;
      if (!canBeTrapped) {
         return false;
      } else {
         double probability = ModConfigs.VAULT_CHEST.getTrapProbability(vault.get(Vault.LEVEL).get());
         WeightedList<String> pool = ModConfigs.VAULT_CHEST.getEffectPool(vault.get(Vault.LEVEL).get());
         probability = CommonEvents.CHEST_TRAP_GENERATION.invoke(data.getPlayer(), probability, pool).getProbability();
         if (!(data.getRandom().nextFloat() >= probability) && pool != null) {
            AttributeSnapshot snapshot = AttributeSnapshotHelper.getInstance().getSnapshot(data.getPlayer());
            float disarmChance = snapshot.getAttributeValue(ModGearAttributes.TRAP_DISARMING, VaultGearAttributeTypeMerger.floatSum());
            if (data.getRandom().nextFloat() < disarmChance) {
               world.playSound(null, data.getPos(), ModSounds.DISARM_TRAP, SoundSource.BLOCKS, 1.0F, 1.0F);
               return false;
            } else {
               pool.getRandom(data.getRandom()).map(ModConfigs.VAULT_CHEST::getEffectByName).ifPresent(effect -> {
                  if (effect instanceof MobTrapEffect) {
                     world.playSound(null, data.getPos(), ModSounds.MOB_TRAP, SoundSource.BLOCKS, 1.0F, 1.0F);
                     ModNetwork.CHANNEL.send(PacketDistributor.ALL.noArg(), new TrappedMobChestParticlesMessage(data.getPos()));
                  }

                  effect.apply(world, vault, data.getPlayer());
               });
               vault.getOptional(Vault.STATS)
                  .map(c -> c.get(data.getPlayer().getUUID()))
                  .ifPresent(stats -> stats.get(StatCollector.CHESTS).add(ChestStat.ofTrapped(((VaultChestBlock)data.getState().getBlock()).getType())));
               return true;
            }
         } else {
            return false;
         }
      }
   }

   protected void generateCatalystFragments(ChestGenerationEvent.Data data, Vault vault) {
      if (this.has(ADD_CATALYST_FRAGMENTS)) {
         if (!vault.has(Vault.LEVEL) || vault.get(Vault.LEVEL).get() >= ModConfigs.VAULT_CHEST_META.getCatalystMinLevel()) {
            double probability = ModConfigs.VAULT_CHEST_META.getCatalystChance(data.getState().getBlock(), data.getRarity());
            probability = CommonEvents.CHEST_CATALYST_GENERATION.invoke(data.getPlayer(), probability).getProbability();
            if (data.getRandom().nextFloat() < probability) {
               data.getLoot().add(new ItemStack(ModItems.VAULT_CATALYST_FRAGMENT));
            }
         }
      }
   }

   protected void generateRunes(ChestGenerationEvent.Data data, Vault vault) {
      if (this.has(ADD_RUNES)) {
         double probability = ModConfigs.VAULT_CHEST_META.getRuneChance(data.getState().getBlock(), data.getRarity());
         if (data.getRandom().nextFloat() < probability) {
            data.getLoot().add(new ItemStack(ModItems.RUNE));
         }
      }
   }
}
