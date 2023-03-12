package iskallia.vault.snapshot;

import iskallia.vault.etching.EtchingSet;
import iskallia.vault.gear.attribute.VaultGearAttribute;
import iskallia.vault.gear.attribute.VaultGearAttributeRegistry;
import iskallia.vault.gear.attribute.type.EffectAttributeMerger;
import iskallia.vault.gear.attribute.type.VaultGearAttributeType;
import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.init.ModGearAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.common.extensions.IForgeFriendlyByteBuf;

public class AttributeSnapshot {
   public static final AttributeSnapshot EMPTY = new AttributeSnapshot();
   Map<VaultGearAttribute<?>, AttributeSnapshot.AttributeValue<?, ?>> gearAttributeValues = new HashMap<>();
   List<EtchingSet<?>> etchings = new ArrayList<>();

   protected AttributeSnapshot() {
   }

   public AttributeSnapshot(FriendlyByteBuf buf) {
      this.read(buf);
   }

   public <T> List<T> getAttributeValueList(VaultGearAttribute<T> attribute) {
      return this.getAttributeValue(attribute, VaultGearAttributeTypeMerger.asList());
   }

   public <T, V> V getAttributeValue(VaultGearAttribute<T> attribute, VaultGearAttributeTypeMerger<T, V> merger) {
      AttributeSnapshot.AttributeValue<T, V> attributeCache = (AttributeSnapshot.AttributeValue<T, V>)this.gearAttributeValues.get(attribute);
      V merged = merger.getBaseValue();
      if (attributeCache == null) {
         return merged;
      } else {
         for (Object value : attributeCache.cachedValues) {
            merged = merger.merge(merged, (T)value);
         }

         return merged;
      }
   }

   public Set<MobEffect> getImmunities() {
      return this.getAttributeValue(ModGearAttributes.EFFECT_IMMUNITY, VaultGearAttributeTypeMerger.asSet());
   }

   public EffectAttributeMerger.CombinedEffects getGrantedPotions() {
      return this.getAttributeValue(ModGearAttributes.EFFECT, EffectAttributeMerger.getInstance());
   }

   public List<EtchingSet<?>> getEtchings() {
      return Collections.unmodifiableList(this.etchings);
   }

   public boolean hasEtching(EtchingSet<?> set) {
      return this.getEtchings().contains(set);
   }

   public void write(FriendlyByteBuf buf) {
      buf.writeInt(this.gearAttributeValues.size());
      this.gearAttributeValues.forEach((attribute, value) -> {
         buf.writeResourceLocation(attribute.getRegistryName());
         value.write(buf, (VaultGearAttribute<?>)attribute);
      });
      buf.writeCollection(this.etchings, IForgeFriendlyByteBuf::writeRegistryId);
   }

   protected void read(FriendlyByteBuf buf) {
      int attrCacheSize = buf.readInt();

      for (int i = 0; i < attrCacheSize; i++) {
         VaultGearAttribute<?> attribute = VaultGearAttributeRegistry.getAttribute(buf.readResourceLocation());
         AttributeSnapshot.AttributeValue<?, ?> value = new AttributeSnapshot.AttributeValue<>(buf, attribute);
         this.gearAttributeValues.put(attribute, value);
      }

      this.etchings = buf.readList(IForgeFriendlyByteBuf::readRegistryId);
   }

   public static class AttributeValue<T, V> {
      private final List<T> cachedValues = new ArrayList<>();

      AttributeValue() {
      }

      private AttributeValue(FriendlyByteBuf buf, VaultGearAttribute<T> attribute) {
         this.read(buf, attribute);
      }

      void addCachedValue(Object object) {
         this.cachedValues.add((T)object);
      }

      void addCachedValues(List<?> objects) {
         objects.forEach(this::addCachedValue);
      }

      private void write(FriendlyByteBuf buf, VaultGearAttribute<?> attribute) {
         VaultGearAttributeType<T> type = (VaultGearAttributeType<T>)attribute.getType();
         buf.writeCollection(this.cachedValues, type::netWrite);
      }

      private void read(FriendlyByteBuf buf, VaultGearAttribute<T> attribute) {
         VaultGearAttributeType<T> type = attribute.getType();
         this.cachedValues.addAll(buf.readList(type::netRead));
      }
   }
}
