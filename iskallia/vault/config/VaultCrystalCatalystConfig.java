package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import iskallia.vault.VaultMod;
import iskallia.vault.util.data.WeightedList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;

public class VaultCrystalCatalystConfig extends Config {
   public static final String MODIFIER_POOL_GOOD = "GOOD";
   public static final String MODIFIER_POOL_BAD = "BAD";
   public static final String MODIFIER_POOL_CURSE = "CURSE";
   @Expose
   @SerializedName("MODIFIER_POOLS")
   private final Map<String, VaultCrystalCatalystConfig.ModifierPool> MODIFIER_POOLS = new HashMap<>();
   @Expose
   @SerializedName("MODIFIER_POOL_GROUPS")
   private final WeightedList<VaultCrystalCatalystConfig.ModifierPoolGroup> MODIFIER_POOL_GROUPS = new WeightedList<>();

   @Override
   public String getName() {
      return "vault_crystal_catalyst_modifiers";
   }

   @Nullable
   public VaultCrystalCatalystConfig.ModifierPoolGroup getRandomModifierPoolGroup(Random random) {
      return this.MODIFIER_POOL_GROUPS.getRandom(random);
   }

   @Nullable
   public VaultCrystalCatalystConfig.ModifierPool getModifierPoolById(String modifierPoolId) {
      return this.MODIFIER_POOLS.get(modifierPoolId);
   }

   public boolean isUnlisted(ResourceLocation resourceLocation) {
      return this.MODIFIER_POOLS.values().stream().noneMatch(set -> set.modifierIdList.contains(resourceLocation));
   }

   public boolean isGood(ResourceLocation resourceLocation) {
      return this.modifierPoolHas("GOOD", resourceLocation);
   }

   public boolean isBad(ResourceLocation resourceLocation) {
      return this.modifierPoolHas("BAD", resourceLocation);
   }

   public boolean isCurse(ResourceLocation resourceLocation) {
      return this.modifierPoolHas("CURSE", resourceLocation);
   }

   public boolean modifierPoolHas(String modifierPoolId, ResourceLocation resourceLocation) {
      return this.MODIFIER_POOLS.containsKey(modifierPoolId) && this.MODIFIER_POOLS.get(modifierPoolId).contains(resourceLocation);
   }

   @Override
   protected void reset() {
      this.MODIFIER_POOLS.clear();
      this.MODIFIER_POOL_GROUPS.clear();
      this.MODIFIER_POOLS
         .put(
            "GOOD",
            new VaultCrystalCatalystConfig.ModifierPool(
               Arrays.asList(
                  VaultMod.id("item_rarity"),
                  VaultMod.id("item_quantity"),
                  VaultMod.id("soul_hunter"),
                  VaultMod.id("prismatic"),
                  VaultMod.id("plentiful"),
                  VaultMod.id("speedy"),
                  VaultMod.id("stronk"),
                  VaultMod.id("extended"),
                  VaultMod.id("personal_space"),
                  VaultMod.id("easy"),
                  VaultMod.id("reinforced")
               )
            )
         );
      this.MODIFIER_POOLS
         .put(
            "BAD",
            new VaultCrystalCatalystConfig.ModifierPool(
               Arrays.asList(
                  VaultMod.id("draining"),
                  VaultMod.id("limited"),
                  VaultMod.id("clumsy"),
                  VaultMod.id("vulnerable"),
                  VaultMod.id("inert"),
                  VaultMod.id("poisonous"),
                  VaultMod.id("withering"),
                  VaultMod.id("fatiguing"),
                  VaultMod.id("freezing"),
                  VaultMod.id("hunger"),
                  VaultMod.id("tired"),
                  VaultMod.id("slowed"),
                  VaultMod.id("weakened"),
                  VaultMod.id("chunky_mobs"),
                  VaultMod.id("furious_mobs"),
                  VaultMod.id("speedy_mobs"),
                  VaultMod.id("trapped"),
                  VaultMod.id("rushed"),
                  VaultMod.id("crowded"),
                  VaultMod.id("difficult"),
                  VaultMod.id("frail")
               )
            )
         );
      this.MODIFIER_POOLS
         .put(
            "CURSE",
            new VaultCrystalCatalystConfig.ModifierPool(
               Arrays.asList(
                  VaultMod.id("hunger"),
                  VaultMod.id("tired"),
                  VaultMod.id("slowed"),
                  VaultMod.id("weakened"),
                  VaultMod.id("jupiter_gravity"),
                  VaultMod.id("crab_walk"),
                  VaultMod.id("confused")
               )
            )
         );
      this.MODIFIER_POOL_GROUPS.add(new VaultCrystalCatalystConfig.ModifierPoolGroup(List.of(VaultMod.id("catalyst_positive"))), 1);
      this.MODIFIER_POOL_GROUPS
         .add(new VaultCrystalCatalystConfig.ModifierPoolGroup(List.of(VaultMod.id("catalyst_positive"), VaultMod.id("catalyst_negative"))), 1);
   }

   public static class ModifierPool {
      @Expose
      @SerializedName("modifiers")
      private final Set<ResourceLocation> modifierIdList;

      public ModifierPool(Collection<ResourceLocation> modifierIdList) {
         this.modifierIdList = new HashSet<>(modifierIdList);
      }

      public boolean contains(ResourceLocation modifierId) {
         return this.modifierIdList.contains(modifierId);
      }
   }

   public static class ModifierPoolGroup {
      @Expose
      @SerializedName("pools")
      private final List<ResourceLocation> modifierPoolIdList;

      public ModifierPoolGroup(List<ResourceLocation> modifierPoolIdList) {
         this.modifierPoolIdList = modifierPoolIdList;
      }

      public List<ResourceLocation> getModifierPoolIdList() {
         return this.modifierPoolIdList;
      }
   }
}
