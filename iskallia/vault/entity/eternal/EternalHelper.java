package iskallia.vault.entity.eternal;

import iskallia.vault.entity.EternalEntity;
import iskallia.vault.init.ModAttributes;
import iskallia.vault.init.ModEntities;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class EternalHelper {
   private static final UUID ETERNAL_SIZE_INCREASE = UUID.fromString("de6b75be-deb2-4711-8fac-08465031b2c3");

   public static EternalEntity spawnEternal(World world, EternalDataAccess dataAccess) {
      return spawnEternal(
         world, dataAccess.getLevel(), dataAccess.isAncient(), dataAccess.getName(), dataAccess.getEquipment(), dataAccess.getEntityAttributes()
      );
   }

   private static EternalEntity spawnEternal(
      World world, int level, boolean isAncient, String name, Map<EquipmentSlotType, ItemStack> equipment, Map<Attribute, Float> attributes
   ) {
      EternalEntity eternal = (EternalEntity)ModEntities.ETERNAL.func_200721_a(world);
      eternal.func_200203_b(
         new StringTextComponent("[")
            .func_240699_a_(TextFormatting.GREEN)
            .func_230529_a_(new StringTextComponent(String.valueOf(level)).func_240699_a_(TextFormatting.RED))
            .func_230529_a_(new StringTextComponent("] " + name).func_240699_a_(TextFormatting.GREEN))
      );
      eternal.setSkinName(name);
      equipment.forEach((slot, stack) -> {
         eternal.func_184201_a(slot, stack.func_77946_l());
         eternal.func_184642_a(slot, 0.0F);
      });
      attributes.forEach((attribute, value) -> eternal.func_110148_a(attribute).func_111128_a(value.floatValue()));
      eternal.func_70691_i(2.1474836E9F);
      if (isAncient) {
         eternal.func_110148_a(ModAttributes.SIZE_SCALE).func_111128_a(1.2F);
      }

      return eternal;
   }

   public static float getEternalGearModifierAdjustments(EternalDataAccess dataAccess, Attribute attribute, float value) {
      return getEternalGearModifierAdjustments(dataAccess.getEquipment(), attribute, value);
   }

   public static float getEternalGearModifierAdjustments(Map<EquipmentSlotType, ItemStack> equipments, Attribute attribute, float value) {
      Map<Operation, List<AttributeModifier>> modifiers = new HashMap<>();
      equipments.forEach(
         (slotType, stack) -> {
            if (!stack.func_190926_b()) {
               stack.func_111283_C(slotType)
                  .get(attribute)
                  .forEach(modifierx -> modifiers.computeIfAbsent(modifierx.func_220375_c(), op -> new ArrayList<>()).add(modifierx));
            }
         }
      );

      for (AttributeModifier modifier : modifiers.getOrDefault(Operation.ADDITION, Collections.emptyList())) {
         value = (float)(value + modifier.func_111164_d());
      }

      float val = value;

      for (AttributeModifier modifier : modifiers.getOrDefault(Operation.MULTIPLY_BASE, Collections.emptyList())) {
         val = (float)(val + value * modifier.func_111164_d());
      }

      for (AttributeModifier modifier : modifiers.getOrDefault(Operation.MULTIPLY_TOTAL, Collections.emptyList())) {
         val = (float)(val * modifier.func_111164_d());
      }

      return val;
   }
}
