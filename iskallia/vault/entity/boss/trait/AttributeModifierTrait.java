package iskallia.vault.entity.boss.trait;

import iskallia.vault.VaultMod;
import iskallia.vault.entity.boss.VaultBossBaseEntity;
import iskallia.vault.entity.boss.VaultBossEntity;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;

public class AttributeModifierTrait implements ITrait {
   public static final String TYPE = "attribute_modifier";
   private String name;
   private double value;
   private String operator;
   private boolean applied = false;

   public AttributeModifierTrait setAttributes(String name, double value, String operator) {
      this.name = name;
      this.value = value;
      this.operator = operator;
      return this;
   }

   @Override
   public void deserializeNBT(CompoundTag nbt, VaultBossBaseEntity boss) {
      this.name = nbt.getString("Name");
      this.value = nbt.getDouble("Value");
      this.operator = nbt.getString("Operator");
      this.applied = nbt.getBoolean("Applied");
   }

   @Override
   public String getType() {
      return "attribute_modifier";
   }

   @Override
   public void apply(VaultBossEntity boss) {
      if (!this.applied) {
         Registry.ATTRIBUTE.getOptional(new ResourceLocation(this.name)).ifPresent(a -> {
            AttributeInstance attribute = boss.getAttribute(a);
            if (attribute != null) {
               this.applyModifier(attribute, this.operator, this.value);
            }
         });
         this.applied = true;
      }
   }

   public void applyModifier(AttributeInstance instance, String operator, double value) {
      if (operator.equalsIgnoreCase("multiply")) {
         instance.setBaseValue(instance.getBaseValue() * value);
      } else if (operator.equalsIgnoreCase("add")) {
         instance.setBaseValue(instance.getBaseValue() + value);
      } else if (operator.equalsIgnoreCase("set")) {
         instance.setBaseValue(value);
      }
   }

   @Override
   public void addStack(ITrait trait) {
      VaultMod.LOGGER.error("Cannot stack AttributeModifierTrait with attributes {} {} {}", this.name, this.value, this.operator);
   }

   @Override
   public CompoundTag serializeNBT() {
      CompoundTag nbt = new CompoundTag();
      nbt.putString("Name", this.name);
      nbt.putDouble("Value", this.value);
      nbt.putString("Operator", this.operator);
      if (this.applied) {
         nbt.putBoolean("Applied", true);
      }

      return nbt;
   }
}
