package iskallia.vault.config.bounty;

import com.google.gson.annotations.Expose;
import iskallia.vault.bounty.TaskReward;
import iskallia.vault.config.Config;
import iskallia.vault.config.entry.ItemStackPool;
import iskallia.vault.config.entry.LevelEntryMap;
import iskallia.vault.config.entry.RangeEntry;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.item.gear.DataTransferItem;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public class RewardConfig extends Config {
   @Expose
   protected LevelEntryMap<RewardConfig.RewardEntry> LEVELS = new LevelEntryMap<>();

   @Override
   public String getName() {
      return "bounty/rewards";
   }

   @Override
   protected void reset() {
      for (int i = 0; i < 30; i += 10) {
         ItemStackPool pool = new ItemStackPool(1, 10);
         pool.addItemStack(new ItemStack(Items.STICK), 1, 32);
         pool.addItemStack(new ItemStack(Items.APPLE), 1, 32);
         ItemStack sword = new ItemStack(Items.STONE_SWORD);
         EnchantmentHelper.enchantItem(rand, sword, i + 10, true);
         pool.addItemStack(sword, 1);
         this.LEVELS.put(Integer.valueOf(i), new RewardConfig.RewardEntry(new RangeEntry(1, 2), pool, new ArrayList<>()));
      }
   }

   public TaskReward generateReward(@Nullable Player player, int vaultLevel) {
      Optional<RewardConfig.RewardEntry> entry = this.LEVELS.getForLevel(vaultLevel);
      if (entry.isEmpty()) {
         throw new IllegalArgumentException("No Reward Entry found for level: " + vaultLevel);
      } else {
         RewardConfig.RewardEntry rewardEntry = entry.get();
         int vaultExp = rewardEntry.vaultExp.getRandom();
         List<ResourceLocation> discoverModels = rewardEntry.discoverModels;
         List<ItemStack> items = new ArrayList<>();

         for (ItemStack reward : rewardEntry.itemPool.getRandomStacks()) {
            if (player != null && reward.getItem() instanceof VaultGearItem gearItem) {
               gearItem.setPlayerLevel(reward, player);
            }

            reward = DataTransferItem.doConvertStack(reward);
            items.add(reward);
         }

         return new TaskReward(vaultExp, items, discoverModels);
      }
   }

   public static class RewardEntry {
      @Expose
      private RangeEntry vaultExp;
      @Expose
      private ItemStackPool itemPool;
      @Expose
      private List<ResourceLocation> discoverModels;

      public RewardEntry(RangeEntry vaultExp, ItemStackPool itemPool) {
         this.vaultExp = vaultExp;
         this.itemPool = itemPool;
         this.discoverModels = new ArrayList<>();
      }

      public RewardEntry(RangeEntry vaultExp, ItemStackPool itemPool, List<ResourceLocation> discoverModels) {
         this.vaultExp = vaultExp;
         this.itemPool = itemPool;
         this.discoverModels = discoverModels;
      }
   }
}