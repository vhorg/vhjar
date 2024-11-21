package iskallia.vault.gear.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import iskallia.vault.core.net.ArrayBitBuffer;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.gear.attribute.VaultGearAttribute;
import iskallia.vault.gear.attribute.VaultGearAttributeInstance;
import iskallia.vault.gear.attribute.VaultGearAttributeSerializer;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.CardDeckItem;
import iskallia.vault.item.MagnetItem;
import iskallia.vault.item.tool.JewelItem;
import iskallia.vault.item.tool.ToolItem;
import iskallia.vault.item.tool.ToolMaterial;
import iskallia.vault.util.data.BitSerializers;
import iskallia.vault.util.data.LazyHolder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;

public class AttributeGearData {
   public static final String TAG_KEY = "vaultGearData";
   protected GearDataVersion version = GearDataVersion.current();
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
      return Collections.unmodifiableList(this.attributes);
   }

   public static boolean hasData(ItemStack stack) {
      if (stack.getItem() instanceof CardDeckItem && !CardDeckItem.hasCardDeck(stack)) {
         return false;
      } else {
         CompoundTag tag = stack.getTag();
         return tag != null && tag.contains("vaultGearData", 12);
      }
   }

   @Nonnull
   protected static <T extends AttributeGearData> T read(ItemStack stack, Function<BitBuffer, T> bufferCtor, Supplier<T> ctor) {
      CompoundTag tag = stack.getTag();
      return tag != null && tag.contains("vaultGearData", 12) ? bufferCtor.apply(ArrayBitBuffer.backing(tag.getLongArray("vaultGearData"), 0)) : ctor.get();
   }

   public static AttributeGearData empty() {
      return new AttributeGearData();
   }

   @Nonnull
   public static <T extends AttributeGearData> T read(ItemStack stack) {
      if (stack.getItem() == ModItems.MAGNET && MagnetItem.isLegacy(stack)) {
         return (T)(new VaultGearData());
      } else if (stack.getItem() instanceof JewelItem) {
         return read(stack, (Function<BitBuffer, T>)(JewelGearData::new), (Supplier<T>)(JewelGearData::new));
      } else if (stack.getItem() instanceof ToolItem) {
         CompoundTag tag = stack.getTag();
         if (tag != null && tag.contains("version")) {
            tag.remove("version");
            ToolMaterial material = ToolItem.getMaterial(stack);
            int amount = material == ToolMaterial.ECHOING_INGOT ? -50 : (material == ToolMaterial.OMEGA_POG ? -200 : 0);
            ToolItem.addCapacity(stack, amount);
         }

         return read(stack, (Function<BitBuffer, T>)(ToolGearData::new), (Supplier<T>)(ToolGearData::new));
      } else if (stack.getItem() instanceof VaultGearItem) {
         return read(stack, (Function<BitBuffer, T>)(VaultGearData::new), (Supplier<T>)(VaultGearData::new));
      } else {
         return stack.getItem() instanceof CardDeckItem
            ? read(stack, (Function<BitBuffer, T>)(CardDeckGearData::new), () -> (T)CardDeckGearData.newDeck(stack))
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
      this.markChanged(stack);
      this.writeUnchanged(stack);
   }

   @Deprecated
   public void writeUnchanged(ItemStack stack) {
      ArrayBitBuffer buffer = ArrayBitBuffer.empty();
      this.write(buffer);
      stack.getOrCreateTag().putLongArray("vaultGearData", buffer.toLongArray());
      if (!stack.isEmpty()) {
         GearDataCache.removeCache(stack);
         GearDataCache.createCache(stack);
      }
   }

   protected void markChanged(ItemStack stack) {
      this.identifier.refresh();
   }

   @Nonnull
   public UUID getIdentifier() {
      return this.identifier.get();
   }

   public void setIdentifier(UUID uuid) {
      this.identifier.set(uuid);
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

   public <T> Stream<VaultGearAttributeInstance<T>> getAttributes(VaultGearAttribute<T> attribute) {
      return this.attributes.stream().filter(instance -> instance.getAttribute().equals(attribute)).map(VaultGearAttributeInstance::cast);
   }

   public boolean hasAttribute(VaultGearAttribute<?> attribute) {
      for (VaultGearAttributeInstance<?> instance : this.attributes) {
         if (instance.getAttribute().equals(attribute)) {
            return true;
         }
      }

      return false;
   }

   public VaultGearAttributeInstance<?> removeAttribute(VaultGearAttribute<?> attribute) {
      Iterator<VaultGearAttributeInstance<?>> iterator = this.attributes.iterator();

      while (iterator.hasNext()) {
         VaultGearAttributeInstance<?> instance = iterator.next();
         if (instance.getAttribute().equals(attribute)) {
            iterator.remove();
            return instance;
         }
      }

      return null;
   }

   @Nullable
   public <T> T createOrReplaceAttributeValue(VaultGearAttribute<T> attribute, @Nonnull T value) {
      if (!this.isModifiable()) {
         return null;
      } else {
         Iterator<VaultGearAttributeInstance<?>> iterator = this.attributes.iterator();

         while (iterator.hasNext()) {
            VaultGearAttributeInstance instance = iterator.next();
            if (instance.getAttribute().equals(attribute)) {
               T prevValue = (T)instance.setValue(value);
               if (!instance.isValid()) {
                  iterator.remove();
                  return prevValue;
               }

               return prevValue;
            }
         }

         if (attribute.getType().isValid(value)) {
            this.attributes.add(new VaultGearAttributeInstance<>(attribute, value));
         }

         return null;
      }
   }

   public <T> T createOrReplaceAttribute(VaultGearAttributeInstance<?> instance) {
      if (!this.isModifiable()) {
         return null;
      } else {
         T prevValue = null;
         Iterator<VaultGearAttributeInstance<?>> iterator = this.attributes.iterator();

         while (iterator.hasNext()) {
            VaultGearAttributeInstance<?> existing = iterator.next();
            if (existing.getAttribute().equals(instance.getAttribute())) {
               prevValue = (T)existing.getValue();
               iterator.remove();
               break;
            }
         }

         this.addAttribute(instance);
         return prevValue;
      }
   }

   public boolean addAttribute(VaultGearAttributeInstance<?> instance) {
      return !this.isModifiable() ? false : this.attributes.add(instance);
   }

   public boolean isModifiable() {
      return !this.get(ModGearAttributes.IS_CORRUPTED, VaultGearAttributeTypeMerger.anyTrue());
   }

   public void clear() {
      this.attributes.clear();
   }

   protected void write(BitBuffer buf) {
      buf.writeEnum(GearDataVersion.current());
      this.identifier.write(buf);
      buf.writeCollection(this.attributes, VaultGearAttributeSerializer::serialize);
   }

   protected void read(BitBuffer buf) {
      this.version = buf.readEnum(GearDataVersion.class);
      this.identifier.read(buf);
      this.attributes = buf.readCollection(ArrayList::new, this.readVersionedAttribute(VaultGearAttributeSerializer::deserialize));
      this.attributes.removeIf(Objects::isNull);
   }

   public CompoundTag toNbt() {
      CompoundTag tag = new CompoundTag();
      tag.putInt("version", this.version.ordinal());
      ListTag attributes = new ListTag();
      this.attributes.stream().map(VaultGearAttributeSerializer::serializeTag).forEach(attributes::add);
      tag.put("attributes", attributes);
      return tag;
   }

   protected void fromNbt(CompoundTag tag, GearDataVersion version) {
      this.clear();
      this.version = GearDataVersion.current();
      ListTag attributes = tag.getList("attributes", 10);

      for (int i = 0; i < attributes.size(); i++) {
         CompoundTag attrData = attributes.getCompound(i);
         if (GearDataVersion.V0_6.isLaterThan(version)) {
            attrData.putInt("type", 0);
         }

         VaultGearAttributeInstance<?> inst = VaultGearAttributeSerializer.deserializeTag(attrData, version);
         if (inst != null) {
            this.attributes.add(inst);
         }
      }

      this.markChanged(ItemStack.EMPTY);
   }

   public JsonObject serialize() {
      JsonObject obj = new JsonObject();
      obj.addProperty("id", this.getIdentifier().toString());
      JsonArray attributes = new JsonArray();
      this.attributes.forEach(attr -> attributes.add(attr.serialize(VaultGearModifier.AffixType.IMPLICIT)));
      obj.add("attributes", attributes);
      return obj;
   }

   protected <T> Function<BitBuffer, T> readVersionedAttribute(BiFunction<BitBuffer, GearDataVersion, T> fn) {
      return GearDataVersion.V0_6.isLaterThan(this.version)
         ? buf -> (T)VaultGearAttributeSerializer.INSTANCE_SERIALIZER.deserialize(buf, this.version)
         : this.versioned(fn);
   }

   protected <T> Function<BitBuffer, T> readVersionedModifier(BiFunction<BitBuffer, GearDataVersion, T> fn) {
      return GearDataVersion.V0_6.isLaterThan(this.version)
         ? buf -> (T)VaultGearAttributeSerializer.MODIFIER_SERIALIZER.deserialize(buf, this.version)
         : this.versioned(fn);
   }

   protected <T> Function<BitBuffer, T> versioned(BiFunction<BitBuffer, GearDataVersion, T> fn) {
      return buf -> fn.apply(buf, this.version);
   }
}
