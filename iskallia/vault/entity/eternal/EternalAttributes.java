package iskallia.vault.entity.eternal;

import iskallia.vault.init.ModConfigs;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.registries.ForgeRegistries;

public class EternalAttributes implements INBTSerializable<CompoundNBT> {
   public static final String HEALTH = "health";
   public static final String DAMAGE = "damage";
   public static final String MOVEMENT_SPEED = "movespeed";
   private final Map<Attribute, Float> attributes = new HashMap<>();

   public EternalAttributes() {
   }

   private EternalAttributes(CompoundNBT tag) {
      this.deserializeNBT(tag);
   }

   public static EternalAttributes fromNBT(CompoundNBT tag) {
      return new EternalAttributes(tag);
   }

   void initializeAttributes() {
      ModConfigs.ETERNAL_ATTRIBUTES.createAttributes().forEach(this.attributes::put);
   }

   public Optional<Float> getAttributeValue(Attribute attribute) {
      return Optional.ofNullable(this.attributes.get(attribute));
   }

   public Map<Attribute, Float> getAttributes() {
      return Collections.unmodifiableMap(this.attributes);
   }

   private void setAttributeValue(Attribute attribute, float value) {
      this.attributes.put(attribute, value);
   }

   void addAttributeValue(Attribute attribute, float value) {
      float existing = this.getAttributeValue(attribute).orElse(0.0F);
      this.setAttributeValue(attribute, existing + value);
   }

   public CompoundNBT serializeNBT() {
      CompoundNBT tag = new CompoundNBT();
      this.attributes.forEach((attribute, value) -> tag.func_74776_a(attribute.getRegistryName().toString(), value));
      return tag;
   }

   public void deserializeNBT(CompoundNBT tag) {
      this.attributes.clear();
      tag.func_150296_c().forEach(attributeKey -> {
         Attribute attr = (Attribute)ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation(attributeKey));
         if (attr != null) {
            this.attributes.put(attr, tag.func_74760_g(attributeKey));
         }
      });
   }
}
