package iskallia.vault.gear.modification.operation;

import iskallia.vault.VaultMod;
import iskallia.vault.gear.VaultGearLegendaryHelper;
import iskallia.vault.gear.VaultGearModifierHelper;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.data.AttributeGearData;
import iskallia.vault.gear.modification.GearModification;
import iskallia.vault.init.ModItems;
import iskallia.vault.util.MiscUtils;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.function.TriFunction;

public class CorruptGearModification extends GearModification {
   public CorruptGearModification() {
      super(VaultMod.id("corrupt_gear"));
   }

   @Override
   public ItemStack getDisplayStack() {
      return new ItemStack(ModItems.VORPAL_FOCUS);
   }

   @Override
   public GearModification.Result canApply(ItemStack stack, ItemStack materialStack, Player player, Random rand) {
      return !AttributeGearData.<AttributeGearData>read(stack).isModifiable()
         ? GearModification.Result.errorUnmodifiable()
         : GearModification.Result.makeSuccess();
   }

   @Override
   public GearModification.Result doModification(ItemStack stack, ItemStack materialStack, Player player, Random rand) {
      List<Integer> indices = IntStream.range(0, CorruptGearModification.Operation.values().length).boxed().collect(Collectors.toList());
      Collections.shuffle(indices, rand);

      for (int index : indices) {
         CorruptGearModification.Operation operation = MiscUtils.getEnumEntry(CorruptGearModification.Operation.class, index);
         GearModification.Result result = operation.apply(stack.copy(), player, rand);
         if (result.success()) {
            operation.apply(stack, player, rand);
            return GearModification.Result.makeSuccess();
         }
      }

      return GearModification.Result.errorInternal();
   }

   static enum Operation {
      NOTHING((stack, player, random) -> GearModification.Result.makeSuccess()),
      IMPROVE_ONE(
         (stack, player, random) -> VaultGearLegendaryHelper.improveExistingModifier(stack, 1, random, List.of(VaultGearModifier.AffixCategory.CORRUPTED))
      ),
      REROLL_ALL((stack, player, random) -> {
         GearModification.Result result = VaultGearModifierHelper.reForgeAllModifiers(stack, random);
         return !result.success() ? result : VaultGearModifierHelper.generateCorruptedImplicit(stack, random);
      }),
      ADD_IMPLICIT((stack, player, random) -> VaultGearModifierHelper.generateCorruptedImplicit(stack, random));

      private final TriFunction<ItemStack, Player, Random, GearModification.Result> applyFn;

      private Operation(TriFunction<ItemStack, Player, Random, GearModification.Result> applyFn) {
         this.applyFn = applyFn;
      }

      private GearModification.Result apply(ItemStack stack, Player player, Random random) {
         GearModification.Result res = (GearModification.Result)this.applyFn.apply(stack, player, random);
         return !res.success() ? res : VaultGearModifierHelper.setGearCorrupted(stack);
      }
   }
}
