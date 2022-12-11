package iskallia.vault.config.altar;

import com.google.gson.annotations.Expose;
import iskallia.vault.altar.RequiredItems;
import iskallia.vault.config.Config;
import iskallia.vault.config.altar.entry.AltarIngredientEntry;
import iskallia.vault.config.entry.LevelEntryMap;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.skill.talent.TalentTree;
import iskallia.vault.skill.talent.type.LuckyAltarTalent;
import iskallia.vault.util.MiscUtils;
import iskallia.vault.util.data.WeightedList;
import iskallia.vault.world.data.PlayerStatsData;
import iskallia.vault.world.data.PlayerTalentsData;
import iskallia.vault.world.data.PlayerVaultStatsData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class VaultAltarIngredientsConfig extends Config {
   @Expose
   private LevelEntryMap<Map<String, WeightedList<AltarIngredientEntry>>> LEVELS = new LevelEntryMap<>();

   @Override
   public String getName() {
      return "vault_altar/vault_altar_ingredients";
   }

   @Override
   protected void reset() {
      for (int i = 0; i < 31; i += 5) {
         Map<String, WeightedList<AltarIngredientEntry>> map = new HashMap<>();
         List<ItemStack> wool = List.of(
            new ItemStack(Items.BLACK_WOOL), new ItemStack(Items.BLUE_WOOL), new ItemStack(Items.BROWN_WOOL), new ItemStack(Items.CYAN_WOOL)
         );
         List<ItemStack> stone = List.of(new ItemStack(Items.STONE), new ItemStack(Items.ANDESITE), new ItemStack(Items.DIORITE));
         List<ItemStack> ingots = List.of(new ItemStack(Items.DIAMOND), new ItemStack(Items.GOLD_INGOT), new ItemStack(Items.IRON_INGOT));
         List<ItemStack> misc = List.of(new ItemStack(Items.HEART_OF_THE_SEA), new ItemStack(Items.TOTEM_OF_UNDYING), new ItemStack(Items.DRAGON_HEAD));
         List<ItemStack> farmable = List.of(new ItemStack(Items.SUGAR_CANE), new ItemStack(Items.CACTUS), new ItemStack(Items.WHEAT_SEEDS));
         map.put("resource", new WeightedList<>(Map.of(new AltarIngredientEntry(wool, 10, 50, 0.8), 1)).add(new AltarIngredientEntry(stone, 64, 128, 0.8), 1));
         map.put("mob", new WeightedList<>(Map.of(new AltarIngredientEntry(ingots, 64, 128, 0.8), 1)));
         map.put(
            "farmable", new WeightedList<>(Map.of(new AltarIngredientEntry(wool, 10, 50, 0.8), 1)).add(new AltarIngredientEntry(farmable, 64, 128, 0.8), 1)
         );
         map.put("misc", new WeightedList<>(Map.of(new AltarIngredientEntry(misc, 1, 1, 1.0), 1)));
         this.LEVELS.put(Integer.valueOf(i), map);
      }
   }

   protected Map<String, AltarIngredientEntry> getEntries(int vaultLevel) {
      Optional<Map<String, WeightedList<AltarIngredientEntry>>> pool = this.LEVELS.getForLevel(vaultLevel);
      if (pool.isEmpty()) {
         throw new IllegalArgumentException("No entry found for the given level: " + vaultLevel);
      } else {
         Map<String, WeightedList<AltarIngredientEntry>> map = pool.get();
         Map<String, AltarIngredientEntry> recipe = new HashMap<>();
         map.forEach((k, v) -> recipe.put(k, v.getRandom(Config.rand)));
         return recipe;
      }
   }

   public List<RequiredItems> getIngredients(ServerPlayer player, BlockPos pos) {
      ServerLevel level = player.getLevel();
      int altarLevel = PlayerVaultStatsData.get(level).getVaultStats(player).getVaultLevel();
      int crystalsCrafted = PlayerStatsData.get(level.getServer()).get(player.getUUID()).getCrystals().size();
      float amtMultiplier = 1.0F;
      float luckyAltarChance = ModConfigs.VAULT_ALTAR.LUCKY_ALTAR_CHANCE;
      TalentTree talents = PlayerTalentsData.get(level).getTalents(player);

      for (LuckyAltarTalent talent : talents.getTalents(LuckyAltarTalent.class)) {
         luckyAltarChance += talent.getLuckyAltarChance();
      }

      if (Config.rand.nextFloat() < luckyAltarChance) {
         amtMultiplier = 0.1F;
         this.spawnLuckyEffects(level, pos);
      }

      List<RequiredItems> requiredItems = new ArrayList<>();
      Map<String, AltarIngredientEntry> entries = this.getEntries(altarLevel);

      for (Entry<String, AltarIngredientEntry> entry : entries.entrySet()) {
         String poolId = entry.getKey();
         AltarIngredientEntry ingredientEntry = entry.getValue();
         List<ItemStack> items = ingredientEntry.getItems().stream().<ItemStack>map(ItemStack::copy).peek(itemStack -> itemStack.setCount(1)).toList();
         int amount = ingredientEntry.getAmount();
         if (ingredientEntry.getScale() != 0.0) {
            double scale = this.getScale(poolId, crystalsCrafted);
            amount = Math.max((int)(Math.round(amount * scale * amtMultiplier) * ingredientEntry.getScale()), 1);
         }

         requiredItems.add(new RequiredItems(poolId, items, amount));
      }

      return requiredItems;
   }

   private double getScale(String poolId, int crystalsCrafted) {
      double resourceAmount = 250.0 / (1.0 + Math.exp((-crystalsCrafted + 260.0) / 140.0)) - 32.759;
      double mobAmount = 287.5 / (1.0 + Math.exp((-crystalsCrafted + 260.0) / 140.0)) - 37.67285;
      double farmableAmount = 800.0 * Math.atan(crystalsCrafted / 400.0) / Math.PI + 1.0;
      double miscAmount = crystalsCrafted / 32.0 + 1.0;
      switch (poolId) {
         case "resource":
            return resourceAmount;
         case "mob":
            return mobAmount;
         case "farmable":
            return farmableAmount;
         default:
            return miscAmount;
      }
   }

   private void spawnLuckyEffects(Level world, BlockPos pos) {
      for (int i = 0; i < 30; i++) {
         Vec3 offset = MiscUtils.getRandomOffset(pos, Config.rand, 2.0F);
         ((ServerLevel)world).sendParticles(ParticleTypes.HAPPY_VILLAGER, offset.x, offset.y, offset.z, 3, 0.0, 0.0, 0.0, 1.0);
      }

      world.playSound(null, pos, SoundEvents.PLAYER_LEVELUP, SoundSource.BLOCKS, 1.0F, 1.0F);
   }
}
