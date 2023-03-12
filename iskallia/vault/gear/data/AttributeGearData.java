package iskallia.vault.gear.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import iskallia.vault.core.net.ArrayBitBuffer;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.gear.attribute.VaultGearAttribute;
import iskallia.vault.gear.attribute.VaultGearAttributeInstance;
import iskallia.vault.gear.attribute.VaultGearAttributeRegistry;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.MagnetItem;
import iskallia.vault.util.data.BitSerializers;
import iskallia.vault.util.data.LazyHolder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;

public class AttributeGearData {
   public static final String TAG_KEY = "vaultGearData";
   private GearDataVersion version = GearDataVersion.current();
   private final LazyHolder<UUID> identifier = new LazyHolder<>(UUID::randomUUID, BitSerializers.UUID);
   List<VaultGearAttributeInstance<?>> attributes = new ArrayList<>();

   protected AttributeGearData() {
   }

   protected AttributeGearData(BitBuffer buf) {
      this.read(buf);
   }

   protected AttributeGearData(CompoundTag tag) {
      GearDataVersion version = GearDataVersion.values()[tag.getInt("version")];
      this.fromNbt(tag, version);
   }

   public List<VaultGearAttributeInstance<?>> getAttributes() {
      return this.attributes;
   }

   public static boolean hasData(ItemStack stack) {
      CompoundTag tag = stack.getTag();
      return tag != null && tag.contains("vaultGearData", 12);
   }

   @Nonnull
   protected static <T extends AttributeGearData> T read(ItemStack stack, Function<BitBuffer, T> bufferCtor, Supplier<T> ctor) {
      CompoundTag tag = stack.getTag();
      return tag != null && tag.contains("vaultGearData", 12) ? bufferCtor.apply(ArrayBitBuffer.backing(tag.getLongArray("vaultGearData"), 0)) : ctor.get();
   }

   @Nonnull
   public static <T extends AttributeGearData> T read(ItemStack stack) {
      if (stack.getItem() == ModItems.MAGNET && MagnetItem.isLegacy(stack)) {
         return (T)(new VaultGearData());
      } else {
         return stack.getItem() instanceof VaultGearItem
            ? read(stack, (Function<BitBuffer, T>)(VaultGearData::new), (Supplier<T>)(VaultGearData::new))
            : read(stack, (Function<BitBuffer, T>)(AttributeGearData::new), (Supplier<T>)(AttributeGearData::new));
      }
   }

   public static Optional<UUID> readUUID(ItemStack stack) {
      if (!hasData(stack)) {
         return Optional.empty();
      } else {
         BitBuffer buf = ArrayBitBuffer.backing(stack.getOrCreateTag().getLongArray("vaultGearData"), 0);
         buf.readEnum(GearDataVersion.class);
         return Optional.of(buf.readUUID());
      }
   }

   @Nonnull
   public static <T extends AttributeGearData> T fromNbt(ItemStack stack, CompoundTag tag) {
      return (T)(stack.getItem() instanceof VaultGearItem ? new VaultGearData(tag) : new AttributeGearData(tag));
   }

   public void write(ItemStack stack) {
      this.markChanged();
      ArrayBitBuffer buffer = ArrayBitBuffer.empty();
      this.write(buffer);
      stack.getOrCreateTag().putLongArray("vaultGearData", buffer.toLongArray());
   }

   protected void markChanged() {
      this.identifier.refresh();
   }

   @Nonnull
   public UUID getIdentifier() {
      return this.identifier.get();
   }

   public <T> Optional<T> getFirstValue(VaultGearAttribute<T> attribute) {
      return Optional.ofNullable(this.get(attribute, VaultGearAttributeTypeMerger.firstNonNull()));
   }

   public <T, V> V get(VaultGearAttribute<T> attribute, VaultGearAttributeTypeMerger<T, V> merger) {
      V merged = merger.getBaseValue();

      for (VaultGearAttributeInstance<?> instance : this.attributes) {
         if (instance.getAttribute().equals(attribute)) {
            merged = merger.merge(merged, (T)instance.getValue());
         }
      }

      return merged;
   }

   public boolean has(VaultGearAttribute<?> attribute) {
      for (VaultGearAttributeInstance<?> instance : this.attributes) {
         if (instance.getAttribute().equals(attribute)) {
            return true;
         }
      }

      return false;
   }

   @Nullable
   public <T> T updateAttribute(VaultGearAttribute<T> attribute, T value) {
      for (VaultGearAttributeInstance instance : this.attributes) {
         if (instance.getAttribute().equals(attribute)) {
            return (T)instance.setValue(value);
         }
      }

      this.attributes.add(new VaultGearAttributeInstance<>(attribute, value));
      return null;
   }

   public void clear() {
      this.attributes.clear();
   }

   protected void write(BitBuffer buf) {
      buf.writeEnum(GearDataVersion.current());
      this.identifier.write(buf);
      buf.writeCollection(this.attributes, VaultGearAttributeRegistry::writeAttributeInstance);
   }

   protected void read(BitBuffer buf) {
      this.version = buf.readEnum(GearDataVersion.class);
      this.identifier.read(buf);
      this.attributes = buf.readCollection(ArrayList::new, this.versioned(VaultGearAttributeRegistry::readAttributeInstance));
   }

   public CompoundTag toNbt() {
      CompoundTag tag = new CompoundTag();
      tag.putInt("version", this.version.ordinal());
      ListTag attributes = new ListTag();
      this.attributes.stream().map(VaultGearAttributeRegistry::serializeAttributeInstance).forEach(attributes::add);
      tag.put("attributes", attributes);
      return tag;
   }

   protected void fromNbt(CompoundTag tag, GearDataVersion version) {
      this.clear();
      this.version = GearDataVersion.current();
      ListTag attributes = tag.getList("attributes", 10);

      for (int i = 0; i < attributes.size(); i++) {
         CompoundTag attrData = attributes.getCompound(i);
         VaultGearAttributeInstance<?> inst = VaultGearAttributeRegistry.deserializeAttributeInstance(attrData, version);
         if (inst != null) {
            this.attributes.add(inst);
         }
      }

      this.markChanged();
   }

   public JsonObject serialize() {
      JsonObject obj = new JsonObject();
      obj.addProperty("id", this.getIdentifier().toString());
      JsonArray attributes = new JsonArray();
      this.attributes.forEach(attr -> attributes.add(attr.serialize(VaultGearModifier.AffixType.IMPLICIT)));
      obj.add("attributes", attributes);
      return obj;
   }

   public <T> Function<BitBuffer, T> versioned(BiFunction<BitBuffer, GearDataVersion, T> fn) {
      return buf -> fn.apply(buf, this.version);
   }
}
