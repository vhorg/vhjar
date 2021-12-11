package iskallia.vault.item;

import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.catalyst.CompoundModifierOutcome;
import iskallia.vault.item.catalyst.ModifierRollResult;
import iskallia.vault.item.crystal.CrystalData;
import iskallia.vault.item.crystal.VaultCrystalItem;
import iskallia.vault.util.CodecUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.Properties;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class VaultCatalystItem extends Item {
   private static final Random rand = new Random();

   public VaultCatalystItem(ItemGroup group, ResourceLocation id) {
      super(new Properties().func_200916_a(group).func_200917_a(1));
      this.setRegistryName(id);
   }

   @OnlyIn(Dist.CLIENT)
   public void func_77624_a(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
      getModifierRolls(stack).ifPresent(result -> {
         if (!result.isEmpty()) {
            String modifierDesc = String.format("Adds Modifier%s:", result.size() <= 1 ? "" : "s");
            tooltip.add(StringTextComponent.field_240750_d_);
            tooltip.add(new StringTextComponent(modifierDesc).func_240699_a_(TextFormatting.GOLD));

            for (ModifierRollResult outcome : result) {
               tooltip.addAll(outcome.getTooltipDescription("- ", true));
            }
         }
      });
   }

   public void func_77663_a(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
      if (!world.func_201670_d()) {
         List<ModifierRollResult> results = getModifierRolls(stack).orElse(Collections.emptyList());
         if (results.isEmpty()) {
            setModifierRolls(stack, createRandomModifiers());
         }

         getSeed(stack);
      }
   }

   public static ItemStack createRandom() {
      ItemStack stack = new ItemStack(ModItems.VAULT_CATALYST);
      setModifierRolls(stack, createRandomModifiers());
      return stack;
   }

   private static List<ModifierRollResult> createRandomModifiers() {
      CompoundModifierOutcome randomOutcome = ModConfigs.VAULT_CRYSTAL_CATALYST.getModifiers();
      return randomOutcome == null
         ? Collections.emptyList()
         : randomOutcome.getRolls().stream().map(outcome -> outcome.resolve(rand)).collect(Collectors.toList());
   }

   public static long getSeed(ItemStack stack) {
      if (!(stack.func_77973_b() instanceof VaultCatalystItem)) {
         return 0L;
      } else {
         CompoundNBT nbt = stack.func_196082_o();
         if (!nbt.func_150297_b("Seed", 4)) {
            nbt.func_74772_a("Seed", rand.nextLong());
         }

         return nbt.func_74763_f("Seed");
      }
   }

   @Nullable
   public static List<String> getCrystalCombinationModifiers(ItemStack catalyst, ItemStack crystal) {
      CrystalData data = VaultCrystalItem.getData(crystal.func_77946_l());
      if (!data.canModifyWithCrafting()) {
         return null;
      } else {
         Optional<List<ModifierRollResult>> rollsOpt = getModifierRolls(catalyst);
         if (!rollsOpt.isPresent()) {
            return null;
         } else {
            List<ModifierRollResult> rolls = rollsOpt.get();
            long seed = VaultCrystalItem.getSeed(crystal) ^ getSeed(catalyst);
            Random rand = new Random(seed);

            for (int i = 0; i < rand.nextInt(32); i++) {
               rand.nextLong();
            }

            List<String> newModifiers = new ArrayList<>();

            for (ModifierRollResult modifierRoll : rolls) {
               List<String> usedModifiers = data.getModifiers().stream().map(CrystalData.Modifier::getModifierName).collect(Collectors.toList());
               String availableModifier = modifierRoll.getModifier(
                  rand, modifierStr -> usedModifiers.contains(modifierStr) || newModifiers.contains(modifierStr)
               );
               if (availableModifier == null) {
                  return null;
               }

               if (!data.addCatalystModifier(availableModifier, true, CrystalData.Modifier.Operation.ADD)) {
                  return null;
               }

               newModifiers.add(availableModifier);
            }

            return newModifiers;
         }
      }
   }

   public static Optional<List<ModifierRollResult>> getModifierRolls(ItemStack stack) {
      if (!(stack.func_77973_b() instanceof VaultCatalystItem)) {
         return Optional.empty();
      } else {
         CompoundNBT tag = stack.func_196082_o();
         return CodecUtils.readNBT(ModifierRollResult.CODEC.listOf(), tag.func_150295_c("modifiers", 10));
      }
   }

   public static void setModifierRolls(ItemStack stack, List<ModifierRollResult> result) {
      if (stack.func_77973_b() instanceof VaultCatalystItem) {
         CompoundNBT tag = stack.func_196082_o();
         CodecUtils.writeNBT(ModifierRollResult.CODEC.listOf(), result, nbt -> tag.func_218657_a("modifiers", nbt));
      }
   }
}
