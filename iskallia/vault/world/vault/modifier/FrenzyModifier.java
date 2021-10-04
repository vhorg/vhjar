package iskallia.vault.world.vault.modifier;

import com.google.gson.annotations.Expose;
import iskallia.vault.init.ModConfigs;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.util.ResourceLocation;

public class FrenzyModifier extends TexturedVaultModifier {
   public static final Operation FRENZY_HEALTH_OPERATION = Operation.MULTIPLY_TOTAL;
   @Expose
   private final float damageMultiplier;
   @Expose
   private final float additionalMovementSpeed;
   @Expose
   private final boolean doHealthReduction;
   private UUID healthModifierID = null;
   private UUID damageModifierID = null;
   private UUID movementSpeedModifierID = null;

   public FrenzyModifier(String name, ResourceLocation icon, float damageMultiplier, float additionalMovementSpeed, boolean doHealthReduction) {
      super(name, icon);
      this.damageMultiplier = damageMultiplier;
      this.additionalMovementSpeed = additionalMovementSpeed;
      this.doHealthReduction = doHealthReduction;
   }

   public float getDamageMultiplier() {
      return this.damageMultiplier;
   }

   public UUID getDamageModifierID() {
      if (this.damageModifierID == null) {
         Random r = new Random(this.getName().hashCode());
         this.damageModifierID = new UUID(r.nextLong(), r.nextLong());
      }

      return this.damageModifierID;
   }

   public float getAdditionalMovementSpeed() {
      return this.additionalMovementSpeed;
   }

   public UUID getMovementSpeedModifierID() {
      if (this.movementSpeedModifierID == null) {
         Random r = new Random(this.getName().hashCode());

         for (int i = 0; i < 5; i++) {
            r.nextLong();
         }

         this.movementSpeedModifierID = new UUID(r.nextLong(), r.nextLong());
      }

      return this.movementSpeedModifierID;
   }

   public static boolean isFrenzyHealthModifier(UUID uuid) {
      for (FrenzyModifier modifier : ModConfigs.VAULT_MODIFIERS.FRENZY_MODIFIERS) {
         if (modifier.doHealthReduction && uuid.equals(modifier.getHealthModifierID())) {
            return true;
         }
      }

      return false;
   }

   @Nullable
   public UUID getHealthModifierID() {
      if (!this.doHealthReduction) {
         return null;
      } else {
         if (this.healthModifierID == null) {
            Random r = new Random(this.getName().hashCode());

            for (int i = 0; i < 10; i++) {
               r.nextLong();
            }

            this.healthModifierID = new UUID(r.nextLong(), r.nextLong());
         }

         return this.healthModifierID;
      }
   }

   public void applyToEntity(LivingEntity entity) {
      this.applyModifier(
         entity,
         Attributes.field_233823_f_,
         new AttributeModifier(this.getDamageModifierID(), "Frenzy Damage Multiplier", this.getDamageMultiplier(), Operation.MULTIPLY_BASE)
      );
      this.applyModifier(
         entity,
         Attributes.field_233821_d_,
         new AttributeModifier(this.getMovementSpeedModifierID(), "Frenzy MovementSpeed Addition", this.getAdditionalMovementSpeed(), Operation.ADDITION)
      );
      if (this.doHealthReduction) {
         this.applyModifier(
            entity, Attributes.field_233818_a_, new AttributeModifier(this.getHealthModifierID(), "Frenzy MaxHealth 1", 1.0, FRENZY_HEALTH_OPERATION)
         );
      }
   }

   private void applyModifier(LivingEntity entity, Attribute attribute, AttributeModifier modifier) {
      ModifiableAttributeInstance attributeInstance = entity.func_110148_a(attribute);
      if (attributeInstance != null) {
         attributeInstance.func_233769_c_(modifier);
      }
   }
}
