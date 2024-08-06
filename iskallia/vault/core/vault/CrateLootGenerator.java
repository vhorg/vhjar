package iskallia.vault.core.vault;

import iskallia.vault.core.Version;
import iskallia.vault.core.data.key.LootTableKey;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.vault.player.Listener;
import iskallia.vault.core.world.loot.generator.LootTableGenerator;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.gear.DataInitializationItem;
import iskallia.vault.item.gear.DataTransferItem;
import iskallia.vault.item.gear.VaultLevelItem;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;

public class CrateLootGenerator {
   @Nullable
   private final LootTableKey lootTable;
   private final float itemQuantity;
   private final List<ItemStack> additionalItems;
   private final boolean addArtifact;
   private final float artifactChance;

   public CrateLootGenerator(@Nullable LootTableKey lootTable, float itemQuantity, List<ItemStack> additionalItems, boolean addArtifact, float artifactChance) {
      this.lootTable = lootTable;
      this.itemQuantity = itemQuantity;
      this.additionalItems = additionalItems;
      this.addArtifact = addArtifact;
      this.artifactChance = artifactChance;
   }

   public NonNullList<ItemStack> generate(Vault vault, Listener listener, RandomSource random) {
      NonNullList<ItemStack> loot = this.createLoot(vault, listener, random);

      for (int i = 0; i < loot.size(); i++) {
         ItemStack stack = (ItemStack)loot.get(i);
         VaultLevelItem.doInitializeVaultLoot(stack, vault, null);
         stack = DataTransferItem.doConvertStack(stack);
         DataInitializationItem.doInitialize(stack);
         loot.set(i, stack);
      }

      return loot;
   }

   public NonNullList<ItemStack> createLoot(Vault vault, Listener listener, RandomSource random) {
      NonNullList<ItemStack> loot = NonNullList.create();
      if (this.lootTable != null) {
         LootTableGenerator generator = new LootTableGenerator(vault.get(Vault.VERSION), this.lootTable, this.itemQuantity);
         generator.generate(random);
         generator.getItems().forEachRemaining(loot::add);
      }

      this.additionalItems.forEach(stack -> loot.add(stack.copy()));
      this.mergeLoot(loot);
      loot.removeIf(ItemStack::isEmpty);
      NonNullList<ItemStack> specialLoot = this.createSpecialLoot(listener, random);

      for (int i = 0; i < loot.size() - 54 + specialLoot.size(); i++) {
         loot.remove(random.nextInt(loot.size()));
      }

      loot.addAll(specialLoot);
      Collections.shuffle(loot);
      return loot;
   }

   private void mergeLoot(NonNullList<ItemStack> loot) {
      List<ItemStack> merged = new ArrayList<>();

      for (ItemStack stack : loot) {
         for (ItemStack result : merged) {
            if (result.is(stack.getItem())
               && !(result.hasTag() ^ stack.hasTag())
               && result.areCapsCompatible(stack)
               && (!result.hasTag() || result.getTag().equals(stack.getTag()))) {
               int difference = Math.min(stack.getCount(), result.getMaxStackSize() - result.getCount());
               stack.shrink(difference);
               result.grow(difference);
            }
         }

         if (!stack.isEmpty()) {
            merged.add(stack.copy());
         }
      }

      loot.clear();
      loot.addAll(merged);
   }

   public NonNullList<ItemStack> createSpecialLoot(@Nullable Listener listener, RandomSource random) {
      NonNullList<ItemStack> loot = NonNullList.create();
      if (this.addArtifact) {
         float probability = this.artifactChance;
         if (listener != null) {
            probability = CommonEvents.ARTIFACT_CHANCE.invoke(listener, probability).getProbability();
         }

         if (random.nextFloat() < probability) {
            loot.add(new ItemStack(ModItems.UNIDENTIFIED_ARTIFACT));
         }
      }

      if (random.nextFloat() < ModConfigs.AUGMENT.getDropChance()) {
         loot.add(new ItemStack(ModItems.AUGMENT));
      }

      return loot;
   }

   public NonNullList<ItemStack> createLootForCommand(RandomSource random, int vaultLevel) {
      NonNullList<ItemStack> loot = NonNullList.create();
      if (this.lootTable != null) {
         LootTableGenerator generator = new LootTableGenerator(Version.v1_0, this.lootTable, 0.0F);
         generator.generate(random);
         generator.getItems().forEachRemaining(loot::add);
      }

      loot.removeIf(ItemStack::isEmpty);
      NonNullList<ItemStack> specialLoot = this.createSpecialLoot(null, random);

      for (int i = 0; i < loot.size() - 54 + specialLoot.size(); i++) {
         loot.remove(random.nextInt(loot.size()));
      }

      loot.addAll(specialLoot);
      Collections.shuffle(loot);
      loot.forEach(stackx -> {
         if (stackx.getItem() instanceof VaultGearItem lootItemx) {
            lootItemx.setItemLevel(stackx, vaultLevel);
         }
      });

      for (int i = 0; i < loot.size(); i++) {
         ItemStack stack = (ItemStack)loot.get(i);
         if (stack.getItem() instanceof DataTransferItem lootItem) {
            loot.set(i, lootItem.convertStack(stack, random));
         }
      }

      return loot;
   }
}
