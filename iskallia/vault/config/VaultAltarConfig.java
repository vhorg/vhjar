package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.altar.RequiredItem;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.skill.talent.TalentTree;
import iskallia.vault.skill.talent.type.LuckyAltarTalent;
import iskallia.vault.util.MiscUtils;
import iskallia.vault.world.data.GlobalDifficultyData;
import iskallia.vault.world.data.PlayerStatsData;
import iskallia.vault.world.data.PlayerTalentsData;
import iskallia.vault.world.data.PlayerVaultStatsData;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootContext.Builder;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class VaultAltarConfig extends Config {
   @Expose
   public List<VaultAltarConfig.AltarConfigItem> ITEMS = new ArrayList<>();
   @Expose
   public float PULL_SPEED;
   @Expose
   public double PLAYER_RANGE_CHECK;
   @Expose
   public double ITEM_RANGE_CHECK;
   @Expose
   public int INFUSION_TIME;
   @Expose
   public float LUCKY_ALTAR_CHANCE;

   @Override
   public String getName() {
      return "vault_altar";
   }

   @Override
   protected void reset() {
      this.ITEMS.add(new VaultAltarConfig.AltarConfigItem("minecraft:cobblestone", 1000, 6000));
      this.ITEMS.add(new VaultAltarConfig.AltarConfigItem("minecraft:gold_ingot", 300, 900));
      this.ITEMS.add(new VaultAltarConfig.AltarConfigItem("minecraft:iron_ingot", 400, 1300));
      this.ITEMS.add(new VaultAltarConfig.AltarConfigItem("minecraft:sugar_cane", 800, 1600));
      this.ITEMS.add(new VaultAltarConfig.AltarConfigItem("minecraft:oak_log", 400, 800));
      this.ITEMS.add(new VaultAltarConfig.AltarConfigItem("minecraft:spruce_log", 400, 800));
      this.ITEMS.add(new VaultAltarConfig.AltarConfigItem("minecraft:acacia_log", 400, 800));
      this.ITEMS.add(new VaultAltarConfig.AltarConfigItem("minecraft:jungle_log", 400, 800));
      this.ITEMS.add(new VaultAltarConfig.AltarConfigItem("minecraft:dark_oak_log", 400, 800));
      this.ITEMS.add(new VaultAltarConfig.AltarConfigItem("minecraft:apple", 400, 800));
      this.ITEMS.add(new VaultAltarConfig.AltarConfigItem("minecraft:redstone", 400, 1000));
      this.ITEMS.add(new VaultAltarConfig.AltarConfigItem("minecraft:ink_sac", 300, 600));
      this.ITEMS.add(new VaultAltarConfig.AltarConfigItem("minecraft:slime_ball", 200, 800));
      this.ITEMS.add(new VaultAltarConfig.AltarConfigItem("minecraft:rotten_flesh", 500, 1500));
      this.ITEMS.add(new VaultAltarConfig.AltarConfigItem("minecraft:blaze_rod", 80, 190));
      this.ITEMS.add(new VaultAltarConfig.AltarConfigItem("minecraft:brick", 500, 1500));
      this.ITEMS.add(new VaultAltarConfig.AltarConfigItem("minecraft:bone", 500, 1500));
      this.ITEMS.add(new VaultAltarConfig.AltarConfigItem("minecraft:spider_eye", 150, 400));
      this.ITEMS.add(new VaultAltarConfig.AltarConfigItem("minecraft:melon_slice", 1000, 5000));
      this.ITEMS.add(new VaultAltarConfig.AltarConfigItem("minecraft:pumpkin", 1000, 5000));
      this.ITEMS.add(new VaultAltarConfig.AltarConfigItem("minecraft:sand", 1000, 5000));
      this.ITEMS.add(new VaultAltarConfig.AltarConfigItem("minecraft:gravel", 1000, 5000));
      this.ITEMS.add(new VaultAltarConfig.AltarConfigItem("minecraft:wheat", 1000, 2000));
      this.ITEMS.add(new VaultAltarConfig.AltarConfigItem("minecraft:wheat_seeds", 1000, 2000));
      this.ITEMS.add(new VaultAltarConfig.AltarConfigItem("minecraft:carrot", 1000, 2000));
      this.ITEMS.add(new VaultAltarConfig.AltarConfigItem("minecraft:potato", 1000, 2000));
      this.ITEMS.add(new VaultAltarConfig.AltarConfigItem("minecraft:obsidian", 100, 300));
      this.ITEMS.add(new VaultAltarConfig.AltarConfigItem("minecraft:leather", 300, 800));
      this.ITEMS.add(new VaultAltarConfig.AltarConfigItem("minecraft:string", 500, 1200));
      this.PULL_SPEED = 1.0F;
      this.PLAYER_RANGE_CHECK = 32.0;
      this.ITEM_RANGE_CHECK = 8.0;
      this.INFUSION_TIME = 5;
      this.LUCKY_ALTAR_CHANCE = 0.1F;
   }

   public List<RequiredItem> getRequiredItemsFromConfig(ServerWorld world, BlockPos pos, ServerPlayerEntity player) {
      LootContext ctx = new Builder(world)
         .func_216015_a(LootParameters.field_216281_a, player)
         .func_216023_a(world.field_73012_v)
         .func_186469_a(player.func_184817_da())
         .func_216022_a(LootParameterSets.field_237453_h_);
      int vaultLevel = PlayerVaultStatsData.get(world).getVaultStats(player).getVaultLevel();
      int altarLevel = PlayerStatsData.get(world).get(player.func_110124_au()).getCrystals().size();
      float amtMultiplier = 1.0F;
      float luckyAltarChance = this.LUCKY_ALTAR_CHANCE;
      GlobalDifficultyData.Difficulty difficulty = GlobalDifficultyData.get(world).getCrystalCost();
      amtMultiplier = (float)(amtMultiplier * difficulty.getMultiplier());
      TalentTree talents = PlayerTalentsData.get(world).getTalents(player);

      for (LuckyAltarTalent talent : talents.getTalents(LuckyAltarTalent.class)) {
         luckyAltarChance += talent.getLuckyAltarChance();
      }

      if (rand.nextFloat() < luckyAltarChance) {
         amtMultiplier = 0.1F;
         this.spawnLuckyEffects(world, pos);
      }

      LootTable lootTable = world.func_73046_m().func_200249_aQ().func_186521_a(ModConfigs.LOOT_TABLES.getForLevel(vaultLevel).getAltar());
      RequiredItem resource = new RequiredItem(Items.field_151119_aD, 0, 0);
      RequiredItem richity = new RequiredItem(Items.field_151045_i, 0, 0);
      RequiredItem farmable = new RequiredItem(Items.field_151034_e, 0, 0);
      RequiredItem misc = new RequiredItem(Items.field_151110_aK, 0, 0);

      try {
         lootTable.getPool("Resource").func_216091_a(resource::setItem, ctx);
         lootTable.getPool("Richity").func_216091_a(richity::setItem, ctx);
         lootTable.getPool("Farmable").func_216091_a(farmable::setItem, ctx);
         lootTable.getPool("Misc").func_216091_a(misc::setItem, ctx);
      } catch (Exception var22) {
      }

      double m1 = 800.0 * Math.atan(altarLevel / 250.0) / Math.PI + 1.0;
      double m2 = 200.0 / (1.0 + Math.exp((-altarLevel + 260.0) / 50.0));
      double m3 = 400.0 / (1.0 + Math.exp((-altarLevel + 200.0) / 40.0));
      resource.setAmountRequired((int)Math.round(resource.getItem().func_190916_E() * m1 * amtMultiplier));
      richity.setAmountRequired((int)Math.round(richity.getItem().func_190916_E() * m2 * amtMultiplier));
      farmable.setAmountRequired((int)Math.round(farmable.getItem().func_190916_E() * m3 * amtMultiplier));
      misc.setAmountRequired(misc.getItem().func_190916_E());
      resource.getItem().func_190920_e(1);
      richity.getItem().func_190920_e(1);
      farmable.getItem().func_190920_e(1);
      misc.getItem().func_190920_e(1);
      return Arrays.asList(resource, richity, farmable, misc);
   }

   private void spawnLuckyEffects(World world, BlockPos pos) {
      for (int i = 0; i < 30; i++) {
         Vector3d offset = MiscUtils.getRandomOffset(pos, rand, 2.0F);
         ((ServerWorld)world)
            .func_195598_a(ParticleTypes.field_197632_y, offset.field_72450_a, offset.field_72448_b, offset.field_72449_c, 3, 0.0, 0.0, 0.0, 1.0);
      }

      world.func_184133_a(null, pos, SoundEvents.field_187802_ec, SoundCategory.BLOCKS, 1.0F, 1.0F);
   }

   public class AltarConfigItem {
      @Expose
      public String ITEM_ID;
      @Expose
      public int MIN;
      @Expose
      public int MAX;

      public AltarConfigItem(String item, int min, int max) {
         this.ITEM_ID = item;
         this.MIN = min;
         this.MAX = max;
      }
   }
}
