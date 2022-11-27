package iskallia.vault.skill.talent.type;

import com.google.gson.annotations.Expose;
import iskallia.vault.gear.attribute.VaultGearAttribute;
import iskallia.vault.gear.attribute.VaultGearAttributeInstance;
import iskallia.vault.gear.attribute.VaultGearAttributeRegistry;
import iskallia.vault.skill.talent.GearAttributeTalent;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;

public class AttributeTalent extends PlayerTalent implements GearAttributeTalent {
   @Expose
   private final List<AttributeTalent.Instance> attributes = new ArrayList<>();

   public AttributeTalent(int cost, VaultGearAttribute<?> attribute, double value) {
      super(cost);
      this.attributes.add(new AttributeTalent.Instance(attribute.getRegistryName().toString(), value));
   }

   @Override
   public List<VaultGearAttributeInstance<?>> getAttributes() {
      List<VaultGearAttributeInstance<?>> instances = new ArrayList<>();

      for (AttributeTalent.Instance inst : this.attributes) {
         if (inst.getAttribute() != null) {
            instances.add(inst.toAttributeInstance());
         }
      }

      return instances;
   }

   public static class Instance {
      @Expose
      private String attribute;
      @Expose
      private double value;

      public Instance(String attribute, double value) {
         this.attribute = attribute;
         this.value = value;
      }

      @Nullable
      public VaultGearAttribute<?> getAttribute() {
         return VaultGearAttributeRegistry.getAttribute(new ResourceLocation(this.attribute));
      }

      public double getValue() {
         return this.value;
      }

      public VaultGearAttributeInstance<?> toAttributeInstance() {
         return VaultGearAttributeInstance.cast(this.getAttribute(), this.getValue());
      }
   }
}
