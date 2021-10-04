package iskallia.vault.config;

import com.google.common.collect.Lists;
import com.google.gson.annotations.Expose;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.util.VaultRarity;
import iskallia.vault.world.vault.VaultRaid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class VaultScalingChestConfig extends Config {
   private static final Random rand = new Random();
   @Expose
   private final Map<String, List<VaultScalingChestConfig.Level>> traderChances = new HashMap<>();
   @Expose
   private final Map<String, List<VaultScalingChestConfig.Level>> statueChances = new HashMap<>();
   @Expose
   private float megaStatueChance;

   @Override
   public String getName() {
      return "vault_chest_scaling";
   }

   public boolean isMegaStatue() {
      return rand.nextFloat() < this.megaStatueChance;
   }

   public int traderCount(ResourceLocation id, VaultRarity rarity, int vaultLevel) {
      return this.generateCount(this.traderChances, id, rarity, vaultLevel);
   }

   public int statueCount(ResourceLocation id, VaultRarity rarity, int vaultLevel) {
      return this.generateCount(this.statueChances, id, rarity, vaultLevel);
   }

   private int generateCount(Map<String, List<VaultScalingChestConfig.Level>> pool, ResourceLocation id, VaultRarity rarity, int vaultLevel) {
      List<VaultScalingChestConfig.Level> lvls = pool.get(id.toString());
      if (lvls == null) {
         return 0;
      } else {
         VaultScalingChestConfig.Level lvl = this.getForLevel(lvls, vaultLevel);
         if (lvl == null) {
            return 0;
         } else {
            Float chance = lvl.chances.get(rarity.name());
            if (chance == null) {
               return 0;
            } else {
               int generatedAmount = MathHelper.func_76141_d(chance);
               float decimal = chance - generatedAmount;
               if (rand.nextFloat() < decimal) {
                  generatedAmount++;
               }

               return generatedAmount;
            }
         }
      }
   }

   @Nullable
   public VaultScalingChestConfig.Level getForLevel(List<VaultScalingChestConfig.Level> levels, int level) {
      for (int i = 0; i < levels.size(); i++) {
         if (level < levels.get(i).level) {
            if (i != 0) {
               return levels.get(i - 1);
            }
            break;
         }

         if (i == levels.size() - 1) {
            return levels.get(i);
         }
      }

      return null;
   }

   @Override
   protected void reset() {
      this.megaStatueChance = 0.2F;
      this.traderChances.clear();
      this.traderChances.put(ModBlocks.VAULT_CHEST.getRegistryName().toString(), setupDefault(new VaultScalingChestConfig.Level(0)));
      this.traderChances.put(ModBlocks.VAULT_ALTAR_CHEST.getRegistryName().toString(), setupDefault(new VaultScalingChestConfig.Level(0)));
      this.traderChances.put(ModBlocks.VAULT_TREASURE_CHEST.getRegistryName().toString(), setupDefault(new VaultScalingChestConfig.Level(0)));
      this.traderChances.put(ModBlocks.VAULT_COOP_CHEST.getRegistryName().toString(), setupDefault(new VaultScalingChestConfig.Level(0)));
      this.traderChances.put(ModBlocks.VAULT_BONUS_CHEST.getRegistryName().toString(), setupDefault(new VaultScalingChestConfig.Level(0)));
      this.traderChances.put(VaultRaid.SUMMON_AND_KILL_BOSS.get().getId().toString(), setupCommon(new VaultScalingChestConfig.Level(0)));
      this.traderChances.put(VaultRaid.SCAVENGER_HUNT.get().getId().toString(), setupCommon(new VaultScalingChestConfig.Level(0)));
      this.traderChances.put(VaultRaid.ARCHITECT_EVENT.get().getId().toString(), setupCommon(new VaultScalingChestConfig.Level(0)));
      this.traderChances.put(VaultRaid.ANCIENTS.get().getId().toString(), setupCommon(new VaultScalingChestConfig.Level(0)));
      this.statueChances.clear();
      this.statueChances.put(ModBlocks.VAULT_CHEST.getRegistryName().toString(), setupDefault(new VaultScalingChestConfig.Level(0)));
      this.statueChances.put(ModBlocks.VAULT_ALTAR_CHEST.getRegistryName().toString(), setupDefault(new VaultScalingChestConfig.Level(0)));
      this.statueChances.put(ModBlocks.VAULT_TREASURE_CHEST.getRegistryName().toString(), setupDefault(new VaultScalingChestConfig.Level(0)));
      this.statueChances.put(ModBlocks.VAULT_COOP_CHEST.getRegistryName().toString(), setupDefault(new VaultScalingChestConfig.Level(0)));
      this.statueChances.put(ModBlocks.VAULT_BONUS_CHEST.getRegistryName().toString(), setupDefault(new VaultScalingChestConfig.Level(0)));
      this.statueChances.put(VaultRaid.SUMMON_AND_KILL_BOSS.get().getId().toString(), setupCommon(new VaultScalingChestConfig.Level(0)));
      this.statueChances.put(VaultRaid.SCAVENGER_HUNT.get().getId().toString(), setupCommon(new VaultScalingChestConfig.Level(0)));
      this.statueChances.put(VaultRaid.ARCHITECT_EVENT.get().getId().toString(), setupCommon(new VaultScalingChestConfig.Level(0)));
      this.statueChances.put(VaultRaid.ANCIENTS.get().getId().toString(), setupCommon(new VaultScalingChestConfig.Level(0)));
   }

   private static List<VaultScalingChestConfig.Level> setupCommon(VaultScalingChestConfig.Level level) {
      level.chances.put(VaultRarity.COMMON.name(), 0.2F);
      return Lists.newArrayList(new VaultScalingChestConfig.Level[]{level});
   }

   private static List<VaultScalingChestConfig.Level> setupDefault(VaultScalingChestConfig.Level level) {
      level.chances.put(VaultRarity.COMMON.name(), 0.0F);
      level.chances.put(VaultRarity.RARE.name(), 0.05F);
      level.chances.put(VaultRarity.EPIC.name(), 0.2F);
      level.chances.put(VaultRarity.OMEGA.name(), 0.5F);
      return Lists.newArrayList(new VaultScalingChestConfig.Level[]{level});
   }

   public static class Level {
      @Expose
      private final int level;
      @Expose
      private final Map<String, Float> chances = new HashMap<>();

      public Level(int level) {
         this.level = level;
      }
   }
}
