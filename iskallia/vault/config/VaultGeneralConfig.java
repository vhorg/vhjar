package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.VaultMod;
import iskallia.vault.config.entry.LevelEntryList;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.util.GlobUtils;
import iskallia.vault.util.data.WeightedList;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class VaultGeneralConfig extends Config {
   @Expose
   private List<String> ITEM_BLACKLIST;
   @Expose
   private List<String> BLOCK_BLACKLIST;
   @Expose
   public float VAULT_EXIT_TNL_MIN;
   @Expose
   public float VAULT_EXIT_TNL_MAX;
   @Expose
   public boolean SAVE_PLAYER_SNAPSHOTS;
   @Expose
   private final LevelEntryList<VaultGeneralConfig.Level> VAULT_OBJECTIVES = new LevelEntryList<>();
   @Expose
   private final LevelEntryList<VaultGeneralConfig.Level> VAULT_COOP_OBJECTIVES = new LevelEntryList<>();

   @Override
   public String getName() {
      return "vault_general";
   }

   @Override
   protected void reset() {
      this.ITEM_BLACKLIST = new ArrayList<>();
      this.ITEM_BLACKLIST.add(Items.ENDER_CHEST.getRegistryName().toString());
      this.BLOCK_BLACKLIST = new ArrayList<>();
      this.BLOCK_BLACKLIST.add(Blocks.ENDER_CHEST.getRegistryName().toString());
      this.VAULT_EXIT_TNL_MIN = 0.0F;
      this.VAULT_EXIT_TNL_MAX = 0.0F;
      this.SAVE_PLAYER_SNAPSHOTS = false;
      this.VAULT_OBJECTIVES.clear();
      WeightedList<String> objectives = new WeightedList<>();
      objectives.add(VaultMod.id("summon_and_kill_boss").toString(), 1);
      objectives.add(VaultMod.id("scavenger_hunt").toString(), 1);
      this.VAULT_OBJECTIVES.add(new VaultGeneralConfig.Level(0, objectives));
      this.VAULT_COOP_OBJECTIVES.clear();
      objectives = new WeightedList<>();
      objectives.add(VaultMod.id("summon_and_kill_boss").toString(), 1);
      objectives.add(VaultMod.id("scavenger_hunt").toString(), 1);
      this.VAULT_COOP_OBJECTIVES.add(new VaultGeneralConfig.Level(0, objectives));
   }

   @Nullable
   public VaultGeneralConfig.Level getForLevel(LevelEntryList<VaultGeneralConfig.Level> levels, int level) {
      return levels.getForLevel(level).orElse(null);
   }

   public boolean isBlacklisted(ItemStack stack) {
      return this.isBlacklisted(stack.getItem());
   }

   public boolean isBlacklisted(Item item) {
      ResourceLocation registryName = item.getRegistryName();
      if (registryName == null) {
         return false;
      } else {
         String itemId = registryName.toString();

         for (String blacklistGlob : ModConfigs.VAULT_GENERAL.ITEM_BLACKLIST) {
            if (GlobUtils.matches(blacklistGlob, itemId)) {
               return true;
            }
         }

         return false;
      }
   }

   public boolean isBlacklisted(BlockState state) {
      return this.isBlacklisted(state.getBlock());
   }

   public boolean isBlacklisted(Block block) {
      ResourceLocation registryName = block.getRegistryName();
      if (registryName == null) {
         return false;
      } else {
         String blockId = registryName.toString();

         for (String blacklistGlob : ModConfigs.VAULT_GENERAL.BLOCK_BLACKLIST) {
            if (GlobUtils.matches(blacklistGlob, blockId)) {
               return true;
            }
         }

         return false;
      }
   }

   public static class Level implements LevelEntryList.ILevelEntry {
      @Expose
      private final int level;
      @Expose
      private final WeightedList<String> outcomes;

      public Level(int level, WeightedList<String> outcomes) {
         this.level = level;
         this.outcomes = outcomes;
      }

      @Override
      public int getLevel() {
         return this.level;
      }
   }
}
