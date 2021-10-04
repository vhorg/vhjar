package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.item.catalyst.CompoundModifierOutcome;
import iskallia.vault.item.catalyst.ModifierRollType;
import iskallia.vault.item.catalyst.SingleModifierOutcome;
import iskallia.vault.util.data.WeightedList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;

public class VaultCrystalCatalystConfig extends Config {
   private static final Random rand = new Random();
   @Expose
   private final Map<String, VaultCrystalCatalystConfig.TaggedPool> TAGGED_MODIFIER_POOLS = new HashMap<>();
   @Expose
   private final WeightedList<CompoundModifierOutcome> OUTCOMES = new WeightedList<>();

   @Override
   public String getName() {
      return "vault_crystal_catalyst_modifiers";
   }

   @Nullable
   public CompoundModifierOutcome getModifiers() {
      return this.OUTCOMES.getRandom(rand);
   }

   @Nullable
   public VaultCrystalCatalystConfig.TaggedPool getPool(String poolName) {
      return this.TAGGED_MODIFIER_POOLS.get(poolName);
   }

   @Override
   protected void reset() {
      this.TAGGED_MODIFIER_POOLS.clear();
      this.OUTCOMES.clear();
      this.TAGGED_MODIFIER_POOLS
         .put(
            "BAD",
            new VaultCrystalCatalystConfig.TaggedPool(
               "negative",
               15597568,
               new WeightedList<String>().add("Crowded", 1).add("Fast", 1).add("Rush", 1).add("Hard", 1).add("Unlucky", 1).add("Locked", 1)
            )
         );
      this.TAGGED_MODIFIER_POOLS
         .put(
            "GOOD",
            new VaultCrystalCatalystConfig.TaggedPool(
               "positive", 43520, new WeightedList<String>().add("Lonely", 1).add("Easy", 1).add("Treasure", 1).add("Gilded", 1).add("Hoard", 1)
            )
         );
      this.TAGGED_MODIFIER_POOLS
         .put(
            "VERY_BAD",
            new VaultCrystalCatalystConfig.TaggedPool("very negative", 7798784, new WeightedList<String>().add("Chaotic", 1).add("Hard", 1).add("Unlucky", 1))
         );
      this.OUTCOMES.add(new CompoundModifierOutcome().addOutcome(new SingleModifierOutcome(ModifierRollType.ADD_RANDOM_MODIFIER, "GOOD")), 1);
      this.OUTCOMES
         .add(
            new CompoundModifierOutcome()
               .addOutcome(new SingleModifierOutcome(ModifierRollType.ADD_RANDOM_MODIFIER, "GOOD"))
               .addOutcome(new SingleModifierOutcome(ModifierRollType.ADD_RANDOM_MODIFIER, "BAD")),
            1
         );
      this.OUTCOMES
         .add(
            new CompoundModifierOutcome()
               .addOutcome(new SingleModifierOutcome(ModifierRollType.ADD_RANDOM_MODIFIER, "GOOD"))
               .addOutcome(new SingleModifierOutcome(ModifierRollType.ADD_SPECIFIC_MODIFIER, "BAD")),
            1
         );
      this.OUTCOMES
         .add(
            new CompoundModifierOutcome()
               .addOutcome(new SingleModifierOutcome(ModifierRollType.ADD_SPECIFIC_MODIFIER, "GOOD"))
               .addOutcome(new SingleModifierOutcome(ModifierRollType.ADD_RANDOM_MODIFIER, "BAD")),
            1
         );
      this.OUTCOMES
         .add(
            new CompoundModifierOutcome()
               .addOutcome(new SingleModifierOutcome(ModifierRollType.ADD_SPECIFIC_MODIFIER, "GOOD"))
               .addOutcome(new SingleModifierOutcome(ModifierRollType.ADD_SPECIFIC_MODIFIER, "BAD")),
            1
         );
   }

   public static class TaggedPool {
      @Expose
      private final String displayName;
      @Expose
      private final int color;
      @Expose
      private final WeightedList<String> modifiers;

      public TaggedPool(String displayName, int color, WeightedList<String> modifiers) {
         this.displayName = displayName;
         this.color = color;
         this.modifiers = modifiers;
      }

      public IFormattableTextComponent getDisplayName() {
         StringTextComponent cmp = new StringTextComponent(this.displayName);
         cmp.func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(this.color)));
         return cmp;
      }

      @Nullable
      public String getModifier(Random random) {
         return this.getModifier(random, mod -> false);
      }

      @Nullable
      public String getModifier(Random random, Predicate<String> modifierFilter) {
         WeightedList<String> filteredModifiers = this.modifiers.copy();
         filteredModifiers.removeIf(entry -> modifierFilter.test(entry.value));
         return filteredModifiers.getRandom(random);
      }
   }
}
