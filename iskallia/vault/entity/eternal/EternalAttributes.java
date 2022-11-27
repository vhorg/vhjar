package iskallia.vault.entity.eternal;

import iskallia.vault.init.ModConfigs;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.registries.ForgeRegistries;

public class EternalAttributes implements INBTSerializable<CompoundTag> {
   public static final String HEALTH = "health";
   public static final String DAMAGE = "damage";
   public static final String MOVEMENT_SPEED = "movespeed";
   private final Map<Attribute, Float> attributes = new HashMap<>();

   public EternalAttributes() {
   }

   private EternalAttributes(CompoundTag tag) {
      this.deserializeNBT(tag);
   }

   public static EternalAttributes fromNBT(CompoundTag tag) {
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

   public CompoundTag serializeNBT() {
      CompoundTag tag = new CompoundTag();
      this.attributes.forEach((attribute, value) -> tag.putFloat(attribute.getRegistryName().toString(), value));
      return tag;
   }

   public void deserializeNBT(CompoundTag tag) {
      this.attributes.clear();
      tag.getAllKeys().forEach(attributeKey -> {
         Attribute attr = (Attribute)ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation(attributeKey));
         if (attr != null) {
            this.attributes.put(attr, tag.getFloat(attributeKey));
         }
      });
   }
}
