package iskallia.vault.core.vault.modifier.spi;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import iskallia.vault.VaultMod;
import iskallia.vault.core.vault.modifier.reputation.ScalarReputationProperty;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraftforge.registries.ForgeRegistries;

public class EntityAttributeModifier<P extends EntityAttributeModifier.Properties> extends VaultModifier<P> {
   private final long leastSignificantBits = UUID.nameUUIDFromBytes(this.getId().toString().getBytes(StandardCharsets.UTF_8)).getLeastSignificantBits();

   public EntityAttributeModifier(ResourceLocation id, P properties, VaultModifier.Display display) {
      super(id, properties, display);
      if (properties.getType() != null) {
         this.setDescriptionFormatter(properties.getType().getDescriptionFormatter());
      } else {
         this.setDescriptionFormatter((t, p, s) -> t);
      }
   }

   public void applyToEntity(LivingEntity entity, UUID contextUUID, ModifierContext context) {
      EntityAttributeModifier.ModifierType modifierType = this.properties.getType();

      for (ResourceLocation id : modifierType.getAttributeResourceLocations()) {
         Attribute attribute = (Attribute)ForgeRegistries.ATTRIBUTES.getValue(id);
         UUID uuid = this.getId(contextUUID);
         if (attribute == null) {
            VaultMod.LOGGER.error("Invalid entity attribute '%s' configured for vault modifier '%s'".formatted(id, this.getId()));
            return;
         }

         AttributeInstance attributeInstance = entity.getAttribute(attribute);
         if (attributeInstance == null) {
            return;
         }

         AttributeModifier modifier = attributeInstance.getModifier(uuid);
         double amount = this.properties.getAmount(context);
         if (modifier == null) {
            attributeInstance.addPermanentModifier(new AttributeModifier(uuid, this.getDisplayName(), amount, modifierType.getAttributeModifierOperation()));
         }
      }
   }

   public void removeFromEntity(LivingEntity entity) {
      EntityAttributeModifier.ModifierType modifierType = this.properties.getType();

      for (ResourceLocation attributeResourceLocation : modifierType.getAttributeResourceLocations()) {
         Attribute attribute = (Attribute)ForgeRegistries.ATTRIBUTES.getValue(attributeResourceLocation);
         if (attribute == null) {
            VaultMod.LOGGER.error("Invalid entity attribute '%s' configured for vault modifier '%s'".formatted(attributeResourceLocation, this.getId()));
         } else {
            AttributeInstance attributeInstance = entity.getAttribute(attribute);
            if (attributeInstance != null) {
               for (AttributeModifier modifier : new HashSet(attributeInstance.getModifiers())) {
                  if (this.isId(modifier.getId())) {
                     attributeInstance.removeModifier(modifier.getId());
                  }
               }
            }
         }
      }
   }

   protected UUID getId(UUID uuid) {
      return new UUID(uuid.getMostSignificantBits(), this.leastSignificantBits);
   }

   protected boolean isId(UUID uuid) {
      return uuid.getLeastSignificantBits() == this.leastSignificantBits;
   }

   public static enum ModifierType {
      @SerializedName("max_health_additive")
      MAX_HEALTH_ADDITIVE(
         List.of(new ResourceLocation("generic.max_health")), Operation.ADDITION, EntityAttributeModifier.ModifierType.Constants.DESCRIPTION_FORMATTER_ADDITIVE
      ),
      @SerializedName("max_health_additive_percentile")
      MAX_HEALTH_ADDITIVE_PERCENTILE(
         List.of(new ResourceLocation("generic.max_health")),
         Operation.MULTIPLY_BASE,
         EntityAttributeModifier.ModifierType.Constants.DESCRIPTION_FORMATTER_ADDITIVE_PERCENTILE
      ),
      @SerializedName("max_health_multiplicative_percentile")
      MAX_HEALTH_MULTIPLICATIVE_PERCENTILE(
         List.of(new ResourceLocation("generic.max_health")),
         Operation.MULTIPLY_TOTAL,
         EntityAttributeModifier.ModifierType.Constants.DESCRIPTION_FORMATTER_MULTIPLICATIVE_PERCENTILE
      ),
      @SerializedName("attack_damage_additive_percentile")
      ATTACK_DAMAGE_ADDITIVE_PERCENTILE(
         List.of(new ResourceLocation("generic.attack_damage")),
         Operation.MULTIPLY_BASE,
         EntityAttributeModifier.ModifierType.Constants.DESCRIPTION_FORMATTER_ADDITIVE_PERCENTILE
      ),
      @SerializedName("speed_additive_percentile")
      SPEED_ADDITIVE_PERCENTILE(
         List.of(new ResourceLocation("generic.movement_speed"), new ResourceLocation("generic.flying_speed")),
         Operation.MULTIPLY_BASE,
         EntityAttributeModifier.ModifierType.Constants.DESCRIPTION_FORMATTER_ADDITIVE_PERCENTILE
      ),
      @SerializedName("mana_max_additive")
      MANA_MAX_ADDITIVE(
         List.of(VaultMod.id("generic.mana_max")), Operation.ADDITION, EntityAttributeModifier.ModifierType.Constants.DESCRIPTION_FORMATTER_ADDITIVE
      ),
      @SerializedName("mana_max_additive_percentile")
      MANA_MAX_ADDITIVE_PERCENTILE(
         List.of(VaultMod.id("generic.mana_max")),
         Operation.MULTIPLY_BASE,
         EntityAttributeModifier.ModifierType.Constants.DESCRIPTION_FORMATTER_ADDITIVE_PERCENTILE
      ),
      @SerializedName("mana_regen_additive")
      MANA_REGEN_ADDITIVE(
         List.of(VaultMod.id("generic.mana_regen")), Operation.ADDITION, EntityAttributeModifier.ModifierType.Constants.DESCRIPTION_FORMATTER_ADDITIVE
      ),
      @SerializedName("mana_regen_additive_percentile")
      MANA_REGEN_ADDITIVE_PERCENTILE(
         List.of(VaultMod.id("generic.mana_regen")),
         Operation.MULTIPLY_BASE,
         EntityAttributeModifier.ModifierType.Constants.DESCRIPTION_FORMATTER_ADDITIVE_PERCENTILE
      ),
      @SerializedName("crit_chance_additive")
      CRIT_CHANCE_ADDITIVE(
         List.of(VaultMod.id("generic.crit_chance")), Operation.ADDITION, EntityAttributeModifier.ModifierType.Constants.DESCRIPTION_FORMATTER_ADDITIVE
      );

      private final List<ResourceLocation> attributeResourceLocations;
      private final Operation attributeModifierOperation;
      private final IVaultModifierTextFormatter<EntityAttributeModifier.Properties> descriptionFormatter;

      private ModifierType(
         List<ResourceLocation> attributeResourceLocations,
         Operation attributeModifierOperation,
         IVaultModifierTextFormatter<EntityAttributeModifier.Properties> descriptionFormatter
      ) {
         this.attributeResourceLocations = attributeResourceLocations;
         this.attributeModifierOperation = attributeModifierOperation;
         this.descriptionFormatter = descriptionFormatter;
      }

      public List<ResourceLocation> getAttributeResourceLocations() {
         return this.attributeResourceLocations;
      }

      public Operation getAttributeModifierOperation() {
         return this.attributeModifierOperation;
      }

      public <P extends EntityAttributeModifier.Properties> IVaultModifierTextFormatter<P> getDescriptionFormatter() {
         return (IVaultModifierTextFormatter<P>)this.descriptionFormatter;
      }

      private static class Constants {
         public static final IVaultModifierTextFormatter<EntityAttributeModifier.Properties> DESCRIPTION_FORMATTER_ADDITIVE = (t, p, s) -> t.formatted(
            Mth.floor(Math.abs(p.getAmount() * s))
         );
         public static final IVaultModifierTextFormatter<EntityAttributeModifier.Properties> DESCRIPTION_FORMATTER_ADDITIVE_PERCENTILE = (t, p, s) -> t.formatted(
            (int)(Math.abs(p.getAmount()) * s * 100.0)
         );
         public static final IVaultModifierTextFormatter<EntityAttributeModifier.Properties> DESCRIPTION_FORMATTER_MULTIPLICATIVE_PERCENTILE = (t, p, s) -> t.formatted(
            (int)(Math.abs(p.getAmount()) * s * 100.0)
         );
      }
   }

   public static class Properties {
      @Expose
      private final EntityAttributeModifier.ModifierType type;
      @Expose
      private final double amount;
      @Expose
      private final ScalarReputationProperty reputation;

      public Properties(EntityAttributeModifier.ModifierType type, double amount, ScalarReputationProperty reputation) {
         this.type = type;
         this.amount = amount;
         this.reputation = reputation;
      }

      public EntityAttributeModifier.ModifierType getType() {
         return this.type;
      }

      public double getAmount() {
         return this.amount;
      }

      public double getAmount(ModifierContext context) {
         return this.reputation != null ? this.reputation.apply(this.amount, context) : this.amount;
      }
   }
}
