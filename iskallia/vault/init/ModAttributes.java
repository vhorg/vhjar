package iskallia.vault.init;

import iskallia.vault.VaultMod;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.registries.IForgeRegistry;

public class ModAttributes {
   public static Attribute CRIT_CHANCE;
   public static Attribute CRIT_MULTIPLIER;
   public static Attribute TP_CHANCE;
   public static Attribute TP_INDIRECT_CHANCE;
   public static Attribute TP_RANGE;
   public static Attribute POTION_RESISTANCE;
   public static Attribute SIZE_SCALE;
   public static Attribute BREAK_ARMOR_CHANCE;
   public static Attribute MANA_REGEN;
   public static Attribute MANA_MAX;
   public static Attribute HEALING_MAX;
   public static Attribute REACH;
   public static Attribute CROSSBOW_CHARGE_TIME;
   public static Attribute THORNS_CHANCE;
   public static Attribute THORNS_DAMAGE;
   public static Attribute HEAL_RANGE;
   public static Attribute HEAL_AMOUNT;

   public static void register(Register<Attribute> event) {
      Attributes.ATTACK_DAMAGE.setSyncable(true);
      Attributes.KNOCKBACK_RESISTANCE.setSyncable(true);
      CRIT_CHANCE = register(event.getRegistry(), "generic.crit_chance", new RangedAttribute("attribute.name.generic.crit_chance", 0.0, 0.0, 1.0))
         .setSyncable(true);
      CRIT_MULTIPLIER = register(
            event.getRegistry(), "generic.crit_multiplier", new RangedAttribute("attribute.name.generic.crit_multiplier", 0.0, 0.0, 1024.0)
         )
         .setSyncable(true);
      TP_CHANCE = register(event.getRegistry(), "generic.tp_chance", new RangedAttribute("attribute.name.generic.tp_chance", 0.0, 0.0, 1.0)).setSyncable(true);
      TP_INDIRECT_CHANCE = register(
            event.getRegistry(), "generic.indirect_tp_chance", new RangedAttribute("attribute.name.generic.indirect_tp_chance", 0.0, 0.0, 1.0)
         )
         .setSyncable(true);
      TP_RANGE = register(event.getRegistry(), "generic.tp_range", new RangedAttribute("attribute.name.generic.tp_range", 32.0, 0.0, 1024.0)).setSyncable(true);
      POTION_RESISTANCE = register(
            event.getRegistry(), "generic.potion_resistance", new RangedAttribute("attribute.name.generic.potion_resistance", 0.0, 0.0, 1.0)
         )
         .setSyncable(true);
      SIZE_SCALE = register(event.getRegistry(), "generic.size_scale", new RangedAttribute("attribute.name.generic.size_scale", 1.0, 0.0, 512.0))
         .setSyncable(true);
      BREAK_ARMOR_CHANCE = register(
            event.getRegistry(), "generic.break_armor_chance", new RangedAttribute("attribute.name.generic.break_armor_chance", 0.0, 0.0, 512.0)
         )
         .setSyncable(true);
      MANA_MAX = register(event.getRegistry(), "generic.mana_max", new RangedAttribute("attribute.name.generic.mana_max", 100.0, 0.0, 4096.0))
         .setSyncable(true);
      MANA_REGEN = register(event.getRegistry(), "generic.mana_regen", new RangedAttribute("attribute.name.generic.mana_regen", 1.0, 0.0, 4096.0))
         .setSyncable(true);
      HEALING_MAX = register(event.getRegistry(), "generic.healing_max", new RangedAttribute("attribute.name.generic.healing_max", 0.0, 0.0, 512.0))
         .setSyncable(true);
      REACH = register(event.getRegistry(), "generic.reach", new RangedAttribute("attribute.name.generic.reach", 0.0, 0.0, 512.0)).setSyncable(true);
      CROSSBOW_CHARGE_TIME = register(
            event.getRegistry(), "generic.crossbow_charge_time", new RangedAttribute("attribute.name.generic.crossbow_charge_time", 0.0, 0.0, 512.0)
         )
         .setSyncable(true);
      THORNS_CHANCE = register(event.getRegistry(), "generic.thorns_chance", new RangedAttribute("attribute.name.generic.thorns_chance", 0.0, 0.0, 512.0))
         .setSyncable(true);
      THORNS_DAMAGE = register(event.getRegistry(), "generic.thorns_damage", new RangedAttribute("attribute.name.generic.thorns_damage", 0.0, 0.0, 512.0))
         .setSyncable(true);
      HEAL_RANGE = register(event.getRegistry(), "generic.heal_range", new RangedAttribute("attribute.name.generic.heal_range", 0.0, 0.0, 512.0))
         .setSyncable(true);
      HEAL_AMOUNT = register(event.getRegistry(), "generic.heal_amount", new RangedAttribute("attribute.name.generic.heal_amount", 0.0, 0.0, 512.0))
         .setSyncable(true);
   }

   private static Attribute register(IForgeRegistry<Attribute> registry, String name, Attribute attribute) {
      registry.register((Attribute)attribute.setRegistryName(VaultMod.id(name)));
      return attribute;
   }
}
