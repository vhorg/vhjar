package iskallia.vault.entity.eternal;

import iskallia.vault.entity.entity.EternalEntity;
import iskallia.vault.init.ModAttributes;
import iskallia.vault.init.ModEntities;
import iskallia.vault.world.data.EternalsData;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class EternalHelper {
   private static final UUID ETERNAL_SIZE_INCREASE = UUID.fromString("de6b75be-deb2-4711-8fac-08465031b2c3");

   public static EternalEntity spawnEternal(Level world, EternalDataAccess dataAccess) {
      return spawnEternal(
         world,
         dataAccess.getLevel(),
         dataAccess.isAncient(),
         dataAccess.getName(),
         dataAccess.getEquipment(),
         dataAccess.getEntityAttributes(),
         dataAccess.getVariant(),
         dataAccess.isUsingPlayerSkin()
      );
   }

   private static EternalEntity spawnEternal(
      Level world,
      int level,
      boolean isAncient,
      String name,
      Map<EquipmentSlot, ItemStack> equipment,
      Map<Attribute, Float> attributes,
      EternalsData.EternalVariant variant,
      boolean isUsingPlayerSkin
   ) {
      EternalEntity eternal = (EternalEntity)ModEntities.ETERNAL.create(world);
      eternal.setCustomName(
         new TextComponent("[")
            .withStyle(ChatFormatting.GREEN)
            .append(new TextComponent(String.valueOf(level)).withStyle(ChatFormatting.RED))
            .append(new TextComponent("] " + name).withStyle(ChatFormatting.GREEN))
      );
      eternal.setSkinName(name);
      equipment.forEach((slot, stack) -> {
         eternal.setItemSlot(slot, stack.copy());
         eternal.setDropChance(slot, 0.0F);
      });
      attributes.forEach((attribute, value) -> eternal.getAttribute(attribute).setBaseValue(value.floatValue()));
      eternal.heal(2.1474836E9F);
      if (isAncient) {
         eternal.getAttribute(ModAttributes.SIZE_SCALE).setBaseValue(1.2F);
      }

      eternal.setVariant(variant);
      eternal.setUsingPlayerSkin(isUsingPlayerSkin);
      return eternal;
   }

   public static float getEternalGearModifierAdjustments(EternalDataAccess dataAccess, Attribute attribute, float value) {
      Map<Operation, List<AttributeModifier>> modifiers = new HashMap<>();

      for (EquipmentSlot slotType : EquipmentSlot.values()) {
         ItemStack stack = dataAccess.getEquipment().getOrDefault(slotType, ItemStack.EMPTY);
         if (!stack.isEmpty()) {
            stack.getAttributeModifiers(slotType)
               .get(attribute)
               .forEach(modifier -> modifiers.computeIfAbsent(modifier.getOperation(), op -> new ArrayList<>()).add(modifier));
         }
      }

      for (AttributeModifier modifier : modifiers.getOrDefault(Operation.ADDITION, Collections.emptyList())) {
         value = (float)(value + modifier.getAmount());
      }

      float val = value;

      for (AttributeModifier modifier : modifiers.getOrDefault(Operation.MULTIPLY_BASE, Collections.emptyList())) {
         val = (float)(val + value * modifier.getAmount());
      }

      for (AttributeModifier modifier : modifiers.getOrDefault(Operation.MULTIPLY_TOTAL, Collections.emptyList())) {
         val = (float)(val * modifier.getAmount());
      }

      return val;
   }
}
