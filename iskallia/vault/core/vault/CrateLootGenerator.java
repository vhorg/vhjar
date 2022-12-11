package iskallia.vault.core.vault;

import iskallia.vault.core.Version;
import iskallia.vault.core.data.key.LootTableKey;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.vault.player.Listener;
import iskallia.vault.core.world.loot.generator.LootTableGenerator;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.gear.DataTransferItem;
import iskallia.vault.item.gear.VaultLootItem;
import java.util.Collections;
import javax.annotation.Nullable;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;

public class CrateLootGenerator {
   @Nullable
   private final LootTableKey lootTable;
   private final boolean addArtifact;
   private final float artifactChance;

   public CrateLootGenerator(@Nullable LootTableKey lootTable, boolean addArtifact, float artifactChance) {
      this.lootTable = lootTable;
      this.addArtifact = addArtifact;
      this.artifactChance = artifactChance;
   }

   public NonNullList<ItemStack> generate(Vault vault, Listener listener, RandomSource random) {
      NonNullList<ItemStack> items = this.createLoot(vault, listener, random);
      items.forEach(stackx -> {
         if (stackx.getItem() instanceof VaultLootItem lootItemx) {
            lootItemx.initializeLoot(vault, stackx);
         }
      });

      for (int i = 0; i < items.size(); i++) {
         ItemStack stack = (ItemStack)items.get(i);
         if (stack.getItem() instanceof DataTransferItem lootItem) {
            items.set(i, lootItem.convertStack(stack, random));
         }
      }

      return items;
   }

   public NonNullList<ItemStack> createLootForCommand(RandomSource random, int vaultLevel) {
      NonNullList<ItemStack> loot = NonNullList.create();
      if (this.lootTable != null) {
         LootTableGenerator generator = new LootTableGenerator(Version.v1_0, this.lootTable);
         generator.generate(random);
         generator.getItems().forEachRemaining(loot::add);
      }

      loot.removeIf(ItemStack::isEmpty);
      NonNullList<ItemStack> specialLoot = this.createSpecialLoot(random);

      for (int i = 0; i < loot.size() - 54 + specialLoot.size(); i++) {
         loot.remove(random.nextInt(loot.size()));
      }

      loot.addAll(specialLoot);
      Collections.shuffle(loot);
      loot.forEach(stackx -> {
         if (stackx.getItem() instanceof VaultGearItem lootItemx) {
            lootItemx.setLevel(stackx, vaultLevel);
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

   private NonNullList<ItemStack> createSpecialLoot(RandomSource random) {
      NonNullList<ItemStack> loot = NonNullList.create();
      if (this.addArtifact && random.nextFloat() < this.artifactChance) {
         loot.add(new ItemStack(ModItems.UNIDENTIFIED_ARTIFACT));
      }

      return loot;
   }

   public NonNullList<ItemStack> createLoot(Vault vault, Listener listener, RandomSource random) {
      NonNullList<ItemStack> loot = NonNullList.create();
      if (this.lootTable != null) {
         LootTableGenerator generator = new LootTableGenerator(vault.get(Vault.VERSION), this.lootTable);
         generator.generate(random);
         generator.getItems().forEachRemaining(loot::add);
      }

      loot.removeIf(ItemStack::isEmpty);
      NonNullList<ItemStack> specialLoot = this.createSpecialLoot(listener, random);

      for (int i = 0; i < loot.size() - 54 + specialLoot.size(); i++) {
         loot.remove(random.nextInt(loot.size()));
      }

      loot.addAll(specialLoot);
      Collections.shuffle(loot);
      return loot;
   }

   public NonNullList<ItemStack> createSpecialLoot(Listener listener, RandomSource random) {
      NonNullList<ItemStack> loot = NonNullList.create();
      if (this.addArtifact) {
         float probability = CommonEvents.ARTIFACT_CHANCE.invoke(listener, this.artifactChance).getProbability();
         if (random.nextFloat() < probability) {
            loot.add(new ItemStack(ModItems.UNIDENTIFIED_ARTIFACT));
         }
      }

      return loot;
   }
}
