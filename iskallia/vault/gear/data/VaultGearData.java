package iskallia.vault.gear.data;

import com.google.common.collect.Iterables;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.gear.VaultGearRarity;
import iskallia.vault.gear.VaultGearState;
import iskallia.vault.gear.attribute.VaultGearAttribute;
import iskallia.vault.gear.attribute.VaultGearAttributeInstance;
import iskallia.vault.gear.attribute.VaultGearAttributeSerializer;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.util.data.ObjectHolder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class VaultGearData extends AttributeGearData {
   private int itemLevel = 0;
   private VaultGearRarity rarity = VaultGearRarity.SCRAPPY;
   private VaultGearState state = VaultGearState.UNIDENTIFIED;
   private int repairSlots = 0;
   private int usedRepairSlots = 0;
   private LinkedList<VaultGearModifier<?>> baseModifiers = new LinkedList<>();
   private LinkedList<VaultGearModifier<?>> prefixes = new LinkedList<>();
   private LinkedList<VaultGearModifier<?>> suffixes = new LinkedList<>();

   protected VaultGearData() {
   }

   protected VaultGearData(BitBuffer buf) {
      this.read(buf);
   }

   protected VaultGearData(CompoundTag tag) {
      GearDataVersion version = GearDataVersion.values()[tag.getInt("version")];
      this.fromNbt(tag, version);
   }

   @Nonnull
   public static VaultGearData read(ItemStack stack) {
      return AttributeGearData.read(stack);
   }

   public int getItemLevel() {
      return this.itemLevel;
   }

   public void setItemLevel(int itemLevel) {
      this.itemLevel = itemLevel;
   }

   public VaultGearRarity getRarity() {
      return this.rarity;
   }

   public void setRarity(VaultGearRarity rarity) {
      this.rarity = rarity;
   }

   public VaultGearState getState() {
      return this.state;
   }

   public void setState(VaultGearState state) {
      this.state = state;
   }

   public int getRepairSlots() {
      return this.repairSlots;
   }

   public void setRepairSlots(int repairSlots) {
      this.repairSlots = repairSlots;
   }

   public int getUsedRepairSlots() {
      return this.usedRepairSlots;
   }

   public void setUsedRepairSlots(int usedRepairSlots) {
      this.usedRepairSlots = usedRepairSlots;
   }

   @Override
   public <T, V> V get(VaultGearAttribute<T> attribute, VaultGearAttributeTypeMerger<T, V> merger) {
      return this.get(attribute, VaultGearData.Type.ALL, merger);
   }

   public <T, V> V get(VaultGearAttribute<T> attribute, VaultGearData.Type type, VaultGearAttributeTypeMerger<T, V> merger) {
      ObjectHolder<V> obj = new ObjectHolder<>(merger.getBaseValue());
      type.getAttributeSource(this)
         .filter(instance -> instance.getAttribute().equals(attribute))
         .forEach(instance -> obj.set(merger.merge(obj.get(), (T)instance.getValue())));
      return obj.get();
   }

   public <T> List<VaultGearAttributeInstance<T>> getModifiers(VaultGearAttribute<T> attribute, VaultGearData.Type type) {
      List<VaultGearAttributeInstance<T>> modifiers = new ArrayList<>();
      type.getAttributeSource(this).forEach(instance -> {
         if (instance.getAttribute().equals(attribute)) {
            modifiers.add((VaultGearAttributeInstance<T>)instance);
         }
      });
      return modifiers;
   }

   @Override
   public boolean hasAttribute(VaultGearAttribute<?> attribute) {
      return this.has(attribute, VaultGearData.Type.ALL);
   }

   public boolean has(VaultGearAttribute<?> attribute, VaultGearData.Type type) {
      return type.getAttributeSource(this).anyMatch(instance -> instance.getAttribute().equals(attribute));
   }

   public boolean hasModifier(ResourceLocation modifierIdentifier) {
      return VaultGearData.Type.ALL
         .getAttributeSource(this)
         .filter(instance -> instance instanceof VaultGearModifier)
         .map(instance -> (VaultGearModifier)instance)
         .anyMatch(modifier -> modifier.getModifierIdentifier().equals(modifierIdentifier));
   }

   public Stream<? extends VaultGearModifier<?>> getModifiers(ResourceLocation modifierIdentifier) {
      return VaultGearData.Type.ALL
         .getAttributeSource(this)
         .filter(inst -> inst instanceof VaultGearModifier)
         .map(inst -> (VaultGearModifier<?>)inst)
         .filter(mod -> modifierIdentifier.equals(mod.getModifierIdentifier()));
   }

   public boolean removeModifier(VaultGearModifier<?> modifier) {
      return !this.isModifiable()
         ? false
         : this.removeFromModifierList(modifier, this.attributes)
            || this.removeFromModifierList(modifier, this.baseModifiers)
            || this.removeFromModifierList(modifier, this.prefixes)
            || this.removeFromModifierList(modifier, this.suffixes);
   }

   protected boolean removeFromModifierList(VaultGearModifier<?> modifier, List<? extends VaultGearAttributeInstance<?>> list) {
      Iterator<? extends VaultGearAttributeInstance<?>> iterator = list.iterator();

      while (iterator.hasNext()) {
         VaultGearAttributeInstance<?> instance = (VaultGearAttributeInstance<?>)iterator.next();
         if (instance.canBeModified() && instance == modifier) {
            iterator.remove();
            return true;
         }
      }

      return false;
   }

   public boolean addModifierFirst(VaultGearModifier.AffixType type, VaultGearModifier<?> modifier) {
      return this.addModifier(type, modifier, Deque::addFirst);
   }

   public boolean addModifier(VaultGearModifier.AffixType type, VaultGearModifier<?> modifier) {
      return this.addModifier(type, modifier, Deque::addLast);
   }

   protected boolean addModifier(
      VaultGearModifier.AffixType type, VaultGearModifier<?> modifier, BiConsumer<Deque<VaultGearModifier<?>>, VaultGearModifier<?>> addFn
   ) {
      if (!this.isModifiable()) {
         return false;
      } else if (!modifier.isValid()) {
         return false;
      } else {
         switch (type) {
            case IMPLICIT:
               addFn.accept(this.baseModifiers, modifier);
               break;
            case PREFIX:
               addFn.accept(this.prefixes, modifier);
               break;
            case SUFFIX:
               addFn.accept(this.suffixes, modifier);
         }

         return true;
      }
   }

   public List<VaultGearModifier<?>> getModifiers(VaultGearModifier.AffixType type) {
      switch (type) {
         case IMPLICIT:
            return Collections.unmodifiableList(this.baseModifiers);
         case PREFIX:
            return Collections.unmodifiableList(this.prefixes);
         case SUFFIX:
            return Collections.unmodifiableList(this.suffixes);
         default:
            return Collections.emptyList();
      }
   }

   public Stream<? extends VaultGearAttributeInstance<?>> getAllAttributes() {
      return VaultGearData.Type.ALL_MODIFIERS.getAttributeSource(this);
   }

   public Iterable<VaultGearModifier<?>> getAllModifierAffixes() {
      return Iterables.concat(this.prefixes, this.suffixes);
   }

   public Set<String> getExistingModifierGroups(VaultGearData.Type type) {
      return type.getAttributeSource(this)
         .filter(instance -> instance instanceof VaultGearModifier)
         .map(instance -> (VaultGearModifier)instance)
         .map(VaultGearModifier::getModifierGroup)
         .filter(group -> !group.isEmpty())
         .collect(Collectors.toSet());
   }

   public List<VaultGearModifier<?>> getModifiersFulfilling(Predicate<VaultGearModifier<?>> condition) {
      return VaultGearData.Type.ALL_MODIFIERS
         .getAttributeSource(this)
         .filter(instance -> instance instanceof VaultGearModifier)
         .map(instance -> (VaultGearModifier)instance)
         .filter(condition)
         .collect(Collectors.toList());
   }

   @Override
   public void clear() {
      super.clear();
      this.itemLevel = 0;
      this.rarity = VaultGearRarity.SCRAPPY;
      this.state = VaultGearState.UNIDENTIFIED;
      this.repairSlots = 0;
      this.usedRepairSlots = 0;
      this.baseModifiers.clear();
      this.prefixes.clear();
      this.suffixes.clear();
   }

   @Override
   protected void write(BitBuffer buf) {
      super.write(buf);
      buf.writeInt(this.itemLevel);
      buf.writeEnum(this.state);
      buf.writeEnum(this.rarity);
      buf.writeInt(this.repairSlots);
      buf.writeInt(this.usedRepairSlots);
      buf.writeCollection(this.baseModifiers, VaultGearAttributeSerializer::serialize);
      buf.writeCollection(this.prefixes, VaultGearAttributeSerializer::serialize);
      buf.writeCollection(this.suffixes, VaultGearAttributeSerializer::serialize);
   }

   @Override
   protected void read(BitBuffer buf) {
      super.read(buf);
      this.itemLevel = buf.readInt();
      this.state = buf.readEnum(VaultGearState.class);
      this.rarity = buf.readEnum(VaultGearRarity.class);
      this.repairSlots = buf.readInt();
      this.usedRepairSlots = buf.readInt();
      this.baseModifiers = buf.readCollection(i -> new LinkedList<>(), this.readVersionedModifier(VaultGearAttributeSerializer::deserialize));
      this.baseModifiers.removeIf(Objects::isNull);
      this.prefixes = buf.readCollection(i -> new LinkedList<>(), this.readVersionedModifier(VaultGearAttributeSerializer::deserialize));
      this.prefixes.removeIf(Objects::isNull);
      this.suffixes = buf.readCollection(i -> new LinkedList<>(), this.readVersionedModifier(VaultGearAttributeSerializer::deserialize));
      this.suffixes.removeIf(Objects::isNull);
   }

   @Override
   public CompoundTag toNbt() {
      CompoundTag tag = super.toNbt();
      tag.putInt("itemLevel", this.itemLevel);
      tag.putInt("state", this.state.ordinal());
      tag.putInt("rarity", this.rarity.ordinal());
      tag.putInt("repairSlots", this.repairSlots);
      tag.putInt("usedRepairSlots", this.usedRepairSlots);
      ListTag baseModifiers = new ListTag();
      this.baseModifiers.stream().map(VaultGearAttributeSerializer::serializeTag).forEach(baseModifiers::add);
      tag.put("baseModifiers", baseModifiers);
      ListTag prefixes = new ListTag();
      this.prefixes.stream().map(VaultGearAttributeSerializer::serializeTag).forEach(prefixes::add);
      tag.put("prefixes", prefixes);
      ListTag suffixes = new ListTag();
      this.suffixes.stream().map(VaultGearAttributeSerializer::serializeTag).forEach(suffixes::add);
      tag.put("suffixes", suffixes);
      return tag;
   }

   @Override
   protected void fromNbt(CompoundTag tag, GearDataVersion version) {
      this.clear();
      super.fromNbt(tag, version);
      this.itemLevel = tag.getInt("itemLevel");
      this.state = VaultGearState.values()[tag.getInt("state")];
      this.rarity = VaultGearRarity.values()[tag.getInt("rarity")];
      this.repairSlots = tag.getInt("repairSlots");
      this.usedRepairSlots = tag.getInt("usedRepairSlots");
      ListTag baseModifiers = tag.getList("baseModifiers", 10);
      baseModifiers.stream()
         .map(nbt -> (CompoundTag)nbt)
         .map(nbt -> this.deserializeModifier(nbt, version))
         .filter(Objects::nonNull)
         .forEach(this.baseModifiers::add);
      ListTag prefixes = tag.getList("prefixes", 10);
      prefixes.stream().map(nbt -> (CompoundTag)nbt).map(nbt -> this.deserializeModifier(nbt, version)).filter(Objects::nonNull).forEach(this.prefixes::add);
      ListTag suffixes = tag.getList("suffixes", 10);
      suffixes.stream().map(nbt -> (CompoundTag)nbt).map(nbt -> this.deserializeModifier(nbt, version)).filter(Objects::nonNull).forEach(this.suffixes::add);
   }

   private VaultGearModifier<?> deserializeModifier(CompoundTag nbt, GearDataVersion version) {
      if (GearDataVersion.V0_6.isLaterThan(version)) {
         nbt.putInt("type", 1);
      }

      return VaultGearAttributeSerializer.deserializeTag(nbt, version);
   }

   @Override
   public JsonObject serialize() {
      JsonObject obj = super.serialize();
      obj.addProperty("level", this.getItemLevel());
      obj.addProperty("rarity", this.getRarity().name());
      obj.addProperty("state", this.getState().name());
      obj.addProperty("maxRepairs", this.getRepairSlots());
      obj.addProperty("usedRepairs", this.getUsedRepairSlots());
      JsonArray implicits = new JsonArray();
      this.baseModifiers.forEach(attr -> implicits.add(attr.serialize(VaultGearModifier.AffixType.IMPLICIT)));
      obj.add("implicits", implicits);
      JsonArray prefixes = new JsonArray();
      this.prefixes.forEach(attr -> prefixes.add(attr.serialize(VaultGearModifier.AffixType.PREFIX)));
      obj.add("prefixes", prefixes);
      JsonArray suffixes = new JsonArray();
      this.suffixes.forEach(attr -> suffixes.add(attr.serialize(VaultGearModifier.AffixType.SUFFIX)));
      obj.add("suffixes", suffixes);
      return obj;
   }

   public static enum Type {
      ATTRIBUTES(data -> data.attributes.stream()),
      PREFIXES(data -> data.prefixes.stream()),
      SUFFIXES(data -> data.suffixes.stream()),
      ALL_MODIFIERS(data -> Stream.of(data.baseModifiers, data.prefixes, data.suffixes).flatMap(Collection::stream)),
      IMPLICIT_MODIFIERS(data -> data.baseModifiers.stream()),
      EXPLICIT_MODIFIERS(data -> Stream.of(data.prefixes, data.suffixes).flatMap(Collection::stream)),
      ALL(data -> Stream.of(data.attributes, data.baseModifiers, data.prefixes, data.suffixes).flatMap(Collection::stream));

      private final Function<VaultGearData, Stream<? extends VaultGearAttributeInstance<?>>> attributeSource;

      private Type(Function<VaultGearData, Stream<? extends VaultGearAttributeInstance<?>>> attributeSource) {
         this.attributeSource = attributeSource;
      }

      public Stream<? extends VaultGearAttributeInstance<?>> getAttributeSource(VaultGearData data) {
         return this.attributeSource.apply(data);
      }
   }
}
