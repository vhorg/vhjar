package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.VaultMod;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.util.WeightedList;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.data.AttributeGearData;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.init.ModDynamicModels;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import javax.annotation.Nullable;
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

   public Optional<UniqueGearConfig.Entry> getRandomEntry(@Nullable ResourceLocation poolId, RandomSource random) {
      WeightedList<ResourceLocation> pool = this.pools.getOrDefault(poolId, this.pools.get(DEFAULT_POOL));
      return pool.getRandom(random).map(id -> this.registry.get(id));
   }

   public Optional<UniqueGearConfig.Entry> getEntry(ResourceLocation id) {
      return Optional.ofNullable(this.registry.get(id));
   }

   public boolean hasPool(ResourceLocation id) {
      return this.pools.containsKey(id);
   }

   @Override
   protected void reset() {
      this.registry = new LinkedHashMap<>();
      this.registry
         .put(
            VaultMod.id("cakers"),
            new UniqueGearConfig.Entry("Cakers", ModDynamicModels.Armor.CAKE.getPiece(EquipmentSlot.HEAD).orElseThrow().getId())
               .addModifierIdentifier(UniqueGearConfig.AffixTargetType.PREFIX, VaultMod.id("armor"))
               .addModifierIdentifier(UniqueGearConfig.AffixTargetType.SUFFIX, VaultMod.id("armor"))
         );
      this.pools = new LinkedHashMap<>();
      this.pools.put(DEFAULT_POOL, new WeightedList<ResourceLocation>().add(VaultMod.id("cakers"), 1));
   }

   @Override
   protected void onLoad(@Nullable Config oldConfigInstance) {
      super.onLoad(oldConfigInstance);
      this.registry.forEach((id, entry) -> entry.id = id);
   }

   public static enum AffixTargetType {
      BASE_ATTRIBUTE(AttributeGearData::addAttribute),
      IMPLICIT((data, mod) -> data.addModifier(VaultGearModifier.AffixType.IMPLICIT, mod)),
      PREFIX((data, mod) -> data.addModifier(VaultGearModifier.AffixType.PREFIX, mod)),
      SUFFIX((data, mod) -> data.addModifier(VaultGearModifier.AffixType.SUFFIX, mod));

      private final BiConsumer<VaultGearData, VaultGearModifier<?>> applyFn;

      private AffixTargetType(BiConsumer<VaultGearData, VaultGearModifier<?>> applyFn) {
         this.applyFn = applyFn;
      }

      public void apply(VaultGearData data, VaultGearModifier<?> modifier) {
         this.applyFn.accept(data, modifier);
      }
   }

   public static class Entry {
      private ResourceLocation id;
      @Expose
      private String name;
      @Expose
      private ResourceLocation model;
      @Expose
      private Map<UniqueGearConfig.AffixTargetType, List<ResourceLocation>> modifierIdentifiers = new HashMap<>();
      @Expose
      private List<String> modifierTags = new ArrayList<>();

      public Entry(String name, ResourceLocation model) {
         this.name = name;
         this.model = model;
      }

      public UniqueGearConfig.Entry addModifierIdentifier(UniqueGearConfig.AffixTargetType affix, ResourceLocation id) {
         this.modifierIdentifiers.computeIfAbsent(affix, _affix -> new ArrayList<>()).add(id);
         return this;
      }

      public UniqueGearConfig.Entry addModifierTag(String tag) {
         this.modifierTags.add(tag);
         return this;
      }

      public ResourceLocation getId() {
         return Optional.ofNullable(this.id).orElseThrow();
      }

      public String getName() {
         return this.name;
      }

      public ResourceLocation getModel() {
         return this.model;
      }

      public Map<UniqueGearConfig.AffixTargetType, List<ResourceLocation>> getModifierIdentifiers() {
         return this.modifierIdentifiers;
      }

      public List<String> getModifierTags() {
         return this.modifierTags;
      }
   }
}
