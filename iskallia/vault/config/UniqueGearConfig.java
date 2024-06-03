package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.VaultMod;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.util.WeightedList;
import iskallia.vault.core.world.data.item.ItemPredicate;
import iskallia.vault.core.world.data.item.PartialStack;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.init.ModDynamicModels;
import iskallia.vault.init.ModItems;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public class UniqueGearConfig extends Config {
   @Expose
   private List<UniqueGearConfig.Entry> table;

   @Override
   public String getName() {
      return "unique_gear";
   }

   public Optional<UniqueGearConfig.Entry> getRandomEntry(ItemStack stack, int level, RandomSource random) {
      WeightedList<UniqueGearConfig.Entry> list = new WeightedList<>();

      for (UniqueGearConfig.Entry entry : this.table) {
         if (level >= entry.level && entry.item.test(stack)) {
            list.add(entry, entry.weight);
         }
      }

      return list.getRandom(random);
   }

   @Override
   protected void reset() {
      this.table = new ArrayList<>();
      this.table
         .add(
            new UniqueGearConfig.Entry(
                  0,
                  "Cakers",
                  PartialStack.of(new ItemStack(ModItems.HELMET)),
                  ModDynamicModels.Armor.CAKE.getPiece(EquipmentSlot.HEAD).orElseThrow().getId(),
                  1
               )
               .addModifierIdentifier(VaultGearModifier.AffixType.PREFIX, VaultMod.id("armor"))
               .addModifierIdentifier(VaultGearModifier.AffixType.SUFFIX, VaultMod.id("armor"))
         );
   }

   public static class Entry {
      @Expose
      private int level;
      @Expose
      private ItemPredicate item;
      @Expose
      private String name;
      @Expose
      private ResourceLocation model;
      @Expose
      private Map<VaultGearModifier.AffixType, List<ResourceLocation>> modifierIdentifiers;
      @Expose
      private Map<VaultGearModifier.AffixType, List<String>> modifierTags;
      @Expose
      private int weight;

      public Entry(int level, String name, ItemPredicate item, ResourceLocation model, int weight) {
         this.level = level;
         this.name = name;
         this.item = item;
         this.model = model;
         this.modifierIdentifiers = new HashMap<>();
         this.modifierTags = new HashMap<>();
         this.weight = weight;
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

      public int getWeight() {
         return this.weight;
      }
   }
}
