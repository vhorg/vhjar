package iskallia.vault.world.vault.influence;

import iskallia.vault.VaultMod;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.registries.ForgeRegistries;

public class MobAttributeInfluence extends VaultInfluence {
   public static final ResourceLocation ID = VaultMod.id("mob_attribute");
   private Attribute targetAttribute;
   private AttributeModifier modifier;

   MobAttributeInfluence() {
      super(ID);
   }

   public MobAttributeInfluence(Attribute targetAttribute, AttributeModifier modifier) {
      this();
      this.targetAttribute = targetAttribute;
      this.modifier = modifier;
   }

   public void applyTo(LivingEntity le) {
      AttributeInstance instance = le.getAttribute(this.targetAttribute);
      AttributeModifier existing = instance.getModifier(this.modifier.getId());
      if (existing == null) {
         instance.addPermanentModifier(this.modifier);
      }
   }

   @Override
   public CompoundTag serializeNBT() {
      CompoundTag tag = super.serializeNBT();
      tag.putString("attribute", this.targetAttribute.getRegistryName().toString());
      tag.put("modifier", this.modifier.save());
      return tag;
   }

   @Override
   public void deserializeNBT(CompoundTag tag) {
      super.deserializeNBT(tag);
      this.targetAttribute = (Attribute)ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation(tag.getString("attribute")));
      this.modifier = AttributeModifier.load(tag.getCompound("modifier"));
   }
}
