package iskallia.vault.config.bounty;

import com.google.gson.annotations.Expose;
import iskallia.vault.bounty.TaskReward;
import iskallia.vault.config.Config;
import iskallia.vault.config.entry.IntRangeEntry;
import iskallia.vault.config.entry.ItemStackPool;
import iskallia.vault.config.entry.LevelEntryMap;
import iskallia.vault.container.oversized.OverSizedItemStack;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.item.gear.DataInitializationItem;
import iskallia.vault.item.gear.DataTransferItem;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public class RewardConfig extends Config {
   @Expose
   protected HashMap<String, LevelEntryMap<RewardConfig.RewardEntry>> POOLS = new HashMap<>();

   @Override
   public String getName() {
      return "bounty/rewards";
   }

   @Override
   protected void reset() {
      this.resetLevels("common");
      this.resetLevels("rare");
      this.resetLevels("epic");
      this.resetLevels("omega");
   }

   private void resetLevels(String poolId) {
      LevelEntryMap<RewardConfig.RewardEntry> entryMap = new LevelEntryMap<>();

      for (int i = 0; i < 30; i += 10) {
         ItemStackPool pool = new ItemStackPool(1, 10);
         pool.addItemStack(new ItemStack(Items.STICK), 1, 32);
         pool.addItemStack(new ItemStack(Items.APPLE), 1, 32);
         ItemStack sword = new ItemStack(Items.STONE_SWORD);
         EnchantmentHelper.enchantItem(rand, sword, i + 10, true);
         pool.addItemStack(sword, 1);
         entryMap.put(Integer.valueOf(i), new RewardConfig.RewardEntry(new IntRangeEntry(1, 2), pool, new ArrayList<>()));
      }

      this.POOLS.put(poolId, entryMap);
   }

   public TaskReward generateReward(int vaultLevel, String poolId) {
      LevelEntryMap<RewardConfig.RewardEntry> entryMap = this.POOLS.get(poolId);
      Optional<RewardConfig.RewardEntry> entry = entryMap.getForLevel(vaultLevel);
      if (entry.isEmpty()) {
         throw new IllegalArgumentException("No Reward Entry found for level: " + vaultLevel);
      } else {
         RewardConfig.RewardEntry rewardEntry = entry.get();
         int vaultExp = rewardEntry.vaultExp.getRandom();
         List<ResourceLocation> discoverModels = rewardEntry.discoverModels;
         List<OverSizedItemStack> items = new ArrayList<>();

         for (ItemStack reward : rewardEntry.itemPool.getRandomStacks()) {
            if (reward.getItem() instanceof VaultGearItem gearItem) {
               gearItem.setItemLevel(reward, vaultLevel);
            }

            reward = DataTransferItem.doConvertStack(reward);
            DataInitializationItem.doInitialize(reward);
            items.add(OverSizedItemStack.of(reward));
         }

         return new TaskReward(vaultExp, items, discoverModels);
      }
   }

   public static class RewardEntry {
      @Expose
      private IntRangeEntry vaultExp;
      @Expose
      private ItemStackPool itemPool;
      @Expose
      private List<ResourceLocation> discoverModels;

      public RewardEntry(IntRangeEntry vaultExp, ItemStackPool itemPool) {
         this.vaultExp = vaultExp;
         this.itemPool = itemPool;
         this.discoverModels = new ArrayList<>();
      }

      public RewardEntry(IntRangeEntry vaultExp, ItemStackPool itemPool, List<ResourceLocation> discoverModels) {
         this.vaultExp = vaultExp;
         this.itemPool = itemPool;
         this.discoverModels = discoverModels;
      }
   }
}
