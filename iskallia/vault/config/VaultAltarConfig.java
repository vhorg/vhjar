package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.altar.RequiredItem;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.skill.talent.TalentTree;
import iskallia.vault.skill.talent.type.LuckyAltarTalent;
import iskallia.vault.util.MiscUtils;
import iskallia.vault.world.data.PlayerStatsData;
import iskallia.vault.world.data.PlayerTalentsData;
import iskallia.vault.world.data.PlayerVaultStatsData;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootContext.Builder;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

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

   public List<RequiredItem> getRequiredItemsFromConfig(ServerLevel world, BlockPos pos, ServerPlayer player) {
      LootContext ctx = new Builder(world)
         .withParameter(LootContextParams.THIS_ENTITY, player)
         .withRandom(world.random)
         .withLuck(player.getLuck())
         .create(LootContextParamSets.PIGLIN_BARTER);
      int altarLevel = PlayerVaultStatsData.get(world).getVaultStats(player).getVaultLevel();
      int crystalsCrafted = PlayerStatsData.get(world.getServer()).get(player.getUUID()).getCrystals().size();
      float amtMultiplier = 1.0F;
      float luckyAltarChance = this.LUCKY_ALTAR_CHANCE;
      TalentTree talents = PlayerTalentsData.get(world).getTalents(player);

      for (LuckyAltarTalent talent : talents.getTalents(LuckyAltarTalent.class)) {
         luckyAltarChance += talent.getLuckyAltarChance();
      }

      if (rand.nextFloat() < luckyAltarChance) {
         amtMultiplier = 0.1F;
         this.spawnLuckyEffects(world, pos);
      }

      LootTable lootTable = world.getServer().getLootTables().get(ModConfigs.LOOT_TABLES.getForLevel(altarLevel).getAltar());
      RequiredItem resource = new RequiredItem(Items.CLAY_BALL, 0, 0);
      RequiredItem richity = new RequiredItem(Items.DIAMOND, 0, 0);
      RequiredItem farmable = new RequiredItem(Items.APPLE, 0, 0);
      RequiredItem misc = new RequiredItem(Items.EGG, 0, 0);

      try {
         lootTable.getPool("Resource").addRandomItems(resource::setItem, ctx);
         lootTable.getPool("Richity").addRandomItems(richity::setItem, ctx);
         lootTable.getPool("Farmable").addRandomItems(farmable::setItem, ctx);
         lootTable.getPool("Misc").addRandomItems(misc::setItem, ctx);
      } catch (Exception var21) {
      }

      double m1 = 250.0 / (1.0 + Math.exp((-crystalsCrafted + 260.0) / 140.0)) - 32.759;
      double m2 = crystalsCrafted / 8.0 + 1.0;
      double m3 = 800.0 * Math.atan(crystalsCrafted / 400.0) / Math.PI + 1.0;
      resource.setAmountRequired(Math.max((int)Math.round(resource.getItem().getCount() * m1 * amtMultiplier), 1));
      richity.setAmountRequired(Math.max((int)Math.round(richity.getItem().getCount() * m2 * amtMultiplier), 1));
      farmable.setAmountRequired(Math.max((int)Math.round(farmable.getItem().getCount() * m3 * amtMultiplier), 1));
      misc.setAmountRequired(misc.getItem().getCount());
      resource.getItem().setCount(1);
      richity.getItem().setCount(1);
      farmable.getItem().setCount(1);
      misc.getItem().setCount(1);
      return Arrays.asList(resource, richity, farmable, misc);
   }

   private void spawnLuckyEffects(Level world, BlockPos pos) {
      for (int i = 0; i < 30; i++) {
         Vec3 offset = MiscUtils.getRandomOffset(pos, rand, 2.0F);
         ((ServerLevel)world).sendParticles(ParticleTypes.HAPPY_VILLAGER, offset.x, offset.y, offset.z, 3, 0.0, 0.0, 0.0, 1.0);
      }

      world.playSound(null, pos, SoundEvents.PLAYER_LEVELUP, SoundSource.BLOCKS, 1.0F, 1.0F);
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
