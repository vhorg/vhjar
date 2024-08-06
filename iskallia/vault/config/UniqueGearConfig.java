package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.VaultMod;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.util.WeightedList;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.init.ModDynamicModels;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;

public class UniqueGearConfig extends Config {
   public static final ResourceLocation DEFAULT_POOL = VaultMod.id("default");
   @Expose
   private Map<ResourceLocation, UniqueGearConfig.Entry> registry;
   @Expose
   private Map<ResourceLocation, WeightedList<ResourceLocation>> pools;

   @Override
   public String getName() {
      return "unique_gear";
   }

   public Optional<UniqueGearConfig.Entry> getRandomEntry(ResourceLocation poolId, RandomSource random) {
      WeightedList<ResourceLocation> pool = this.pools.getOrDefault(poolId, this.pools.get(DEFAULT_POOL));
      return pool.getRandom(random).map(id -> this.registry.get(id));
   }

   @Override
   protected void reset() {
      this.registry = new LinkedHashMap<>();
      this.registry
         .put(
            VaultMod.id("cakers"),
            new UniqueGearConfig.Entry("Cakers", ModDynamicModels.Armor.CAKE.getPiece(EquipmentSlot.HEAD).orElseThrow().getId())
               .addModifierIdentifier(VaultGearModifier.AffixType.PREFIX, VaultMod.id("armor"))
               .addModifierIdentifier(VaultGearModifier.AffixType.SUFFIX, VaultMod.id("armor"))
         );
      this.pools = new LinkedHashMap<>();
      this.pools.put(DEFAULT_POOL, new WeightedList<ResourceLocation>().add(VaultMod.id("cakers"), 1));
   }

   public static class Entry {
      @Expose
      private String name;
      @Expose
      private ResourceLocation model;
      @Expose
      private Map<VaultGearModifier.AffixType, List<ResourceLocation>> modifierIdentifiers;
      @Expose
      private Map<VaultGearModifier.AffixType, List<String>> modifierTags;

      public Entry(String name, ResourceLocation model) {
         this.name = name;
         this.model = model;
         this.modifierIdentifiers = new HashMap<>();
         this.modifierTags = new HashMap<>();
      }

      public UniqueGearConfig.Entry addModifierIdentifier(VaultGearModifier.AffixType affix, ResourceLocation id) {
         this.modifierIdentifiers.computeIfAbsent(affix, _affix -> new ArrayList<>()).add(id);
         return this;
      }

      public UniqueGearConfig.Entry addModifierTags(VaultGearModifier.AffixType affix, String tag) {
         this.modifierTags.computeIfAbsent(affix, _affix -> new ArrayList<>()).add(tag);
         return this;
      }

      public String getName() {
         return this.name;
      }

      public ResourceLocation getModel() {
         return this.model;
      }

      public Map<VaultGearModifier.AffixType, List<ResourceLocation>> getModifierIdentifiers() {
         return this.modifierIdentifiers;
      }

      public Map<VaultGearModifier.AffixType, List<String>> getModifierTags() {
         return this.modifierTags;
      }
   }
}
