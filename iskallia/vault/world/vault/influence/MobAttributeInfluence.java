package iskallia.vault.world.vault.influence;

import iskallia.vault.Vault;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class MobAttributeInfluence extends VaultInfluence {
   public static final ResourceLocation ID = Vault.id("mob_attribute");
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
      ModifiableAttributeInstance instance = le.func_110148_a(this.targetAttribute);
      AttributeModifier existing = instance.func_111127_a(this.modifier.func_111167_a());
      if (existing == null) {
         instance.func_233769_c_(this.modifier);
      }
   }

   @Override
   public CompoundNBT serializeNBT() {
      CompoundNBT tag = super.serializeNBT();
      tag.func_74778_a("attribute", this.targetAttribute.getRegistryName().toString());
      tag.func_218657_a("modifier", this.modifier.func_233801_e_());
      return tag;
   }

   @Override
   public void deserializeNBT(CompoundNBT tag) {
      super.deserializeNBT(tag);
      this.targetAttribute = (Attribute)ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation(tag.func_74779_i("attribute")));
      this.modifier = AttributeModifier.func_233800_a_(tag.func_74775_l("modifier"));
   }
}
