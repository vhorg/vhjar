package iskallia.vault.gear.trinket.effects;

import com.google.common.collect.Lists;
import com.google.gson.annotations.Expose;
import iskallia.vault.gear.attribute.VaultGearAttribute;
import iskallia.vault.gear.attribute.VaultGearAttributeInstance;
import iskallia.vault.gear.attribute.VaultGearAttributeRegistry;
import iskallia.vault.gear.trinket.GearAttributeTrinket;
import iskallia.vault.gear.trinket.TrinketEffect;
import iskallia.vault.util.MiscUtils;
import java.util.List;
import net.minecraft.resources.ResourceLocation;

public class AttributeTrinket<T extends Number> extends TrinketEffect<AttributeTrinket.Config<?>> implements GearAttributeTrinket {
   private final VaultGearAttribute<T> defaultAttribute;
   private final T defaultAttributeValue;

   public AttributeTrinket(ResourceLocation name, VaultGearAttribute<T> attribute, T attributeValue) {
      super(name);
      this.defaultAttribute = attribute;
      this.defaultAttributeValue = attributeValue;
   }

   @Override
   public Class<AttributeTrinket.Config<?>> getConfigClass() {
      return MiscUtils.cast(AttributeTrinket.Config.class);
   }

   public AttributeTrinket.Config<T> getDefaultConfig() {
      return new AttributeTrinket.Config<>(this.defaultAttribute, this.defaultAttributeValue);
   }

   @Override
   public List<VaultGearAttributeInstance<?>> getAttributes() {
      return Lists.newArrayList(new VaultGearAttributeInstance[]{this.getConfig().toAttributeInstance()});
   }

   public static class Config<T extends Number> extends TrinketEffect.Config {
      @Expose
      private final ResourceLocation key;
      @Expose
      private final double value;

      public Config(VaultGearAttribute<T> attribute, T value) {
         this.key = attribute.getRegistryName();
         this.value = value.doubleValue();
      }

      public VaultGearAttribute<T> getAttribute() {
         return (VaultGearAttribute<T>)VaultGearAttributeRegistry.getAttribute(this.key);
      }

      public VaultGearAttributeInstance<?> toAttributeInstance() {
         return VaultGearAttributeInstance.cast(this.getAttribute(), this.value);
      }
   }
}
