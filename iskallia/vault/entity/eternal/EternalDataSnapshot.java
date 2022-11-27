package iskallia.vault.entity.eternal;

import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.snapshot.AttributeSnapshot;
import iskallia.vault.snapshot.AttributeSnapshotHelper;
import iskallia.vault.world.data.EternalsData;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public class EternalDataSnapshot implements EternalDataAccess {
   public static final String ATTR_HEALTH = Attributes.MAX_HEALTH.getRegistryName().toString();
   public static final String ATTR_DAMAGE = Attributes.ATTACK_DAMAGE.getRegistryName().toString();
   public static final String ATTR_SPEED = Attributes.MOVEMENT_SPEED.getRegistryName().toString();
   private final UUID eternalUUID;
   private final long seed;
   private final String eternalName;
   private final Map<EquipmentSlot, ItemStack> equipment;
   private final Map<String, Float> attributes;
   private final int level;
   private final int usedLevels;
   private final int maxLevel;
   private final float levelPercent;
   private final boolean alive;
   private final boolean ancient;
   private final String abilityName;
   private EternalsData.EternalVariant variant;
   private final boolean isUsingPlayerSkin;
   private final AttributeSnapshot attributeSnapshot;

   public EternalDataSnapshot(
      UUID eternalUUID,
      long seed,
      String eternalName,
      Map<EquipmentSlot, ItemStack> equipment,
      Map<String, Float> attributes,
      int level,
      int usedLevels,
      int maxLevel,
      float levelPercent,
      boolean alive,
      boolean ancient,
      String abilityName,
      EternalsData.EternalVariant variant,
      boolean isUsingPlayerSkin
   ) {
      this.eternalUUID = eternalUUID;
      this.seed = seed;
      this.eternalName = eternalName;
      this.equipment = equipment;
      this.attributes = attributes;
      this.level = level;
      this.usedLevels = usedLevels;
      this.maxLevel = maxLevel;
      this.levelPercent = levelPercent;
      this.alive = alive;
      this.ancient = ancient;
      this.abilityName = abilityName;
      this.attributeSnapshot = AttributeSnapshotHelper.getInstance().makeGearSnapshot(this::getEquipment);
      this.variant = variant;
      this.isUsingPlayerSkin = isUsingPlayerSkin;
   }

   public static EternalDataSnapshot getFromEternal(EternalsData.EternalGroup playerGroup, EternalData eternal) {
      UUID eternalUUID = eternal.getId();
      long seed = eternal.getSeed();
      String eternalName = eternal.getName();
      Map<EquipmentSlot, ItemStack> equipment = new HashMap<>();

      for (EquipmentSlot slotType : EquipmentSlot.values()) {
         ItemStack stack = eternal.getStack(slotType);
         if (!stack.isEmpty()) {
            equipment.put(slotType, stack.copy());
         }
      }

      EternalAttributes eternalAttributes = eternal.getAttributes();
      Map<String, Float> attributes = new HashMap<>();
      float value = eternalAttributes.getAttributeValue(Attributes.MAX_HEALTH).orElse(0.0F);
      value = EternalHelper.getEternalGearModifierAdjustments(eternal, Attributes.MAX_HEALTH, value);
      attributes.put(ATTR_HEALTH, value);
      value = eternalAttributes.getAttributeValue(Attributes.ATTACK_DAMAGE).orElse(0.0F);
      value = EternalHelper.getEternalGearModifierAdjustments(eternal, Attributes.ATTACK_DAMAGE, value);
      attributes.put(ATTR_DAMAGE, value);
      value = eternalAttributes.getAttributeValue(Attributes.MOVEMENT_SPEED).orElse(0.0F);
      value = EternalHelper.getEternalGearModifierAdjustments(eternal, Attributes.MOVEMENT_SPEED, value);
      attributes.put(ATTR_SPEED, value);
      int level = eternal.getLevel();
      int usedLevels = eternal.getUsedLevels();
      int maxLevel = eternal.getMaxLevel();
      float levelPercent = eternal.getLevelPercent();
      boolean alive = eternal.isAlive();
      boolean ancient = eternal.isAncient();
      String abilityName = eternal.getAura() != null ? eternal.getAura().getAuraName() : null;
      EternalsData.EternalVariant variant = eternal.getVariant();
      boolean isUsingPlayerSkin = eternal.isUsingPlayerSkin();
      return new EternalDataSnapshot(
         eternalUUID,
         seed,
         eternalName,
         equipment,
         attributes,
         level,
         usedLevels,
         maxLevel,
         levelPercent,
         alive,
         ancient,
         abilityName,
         variant,
         isUsingPlayerSkin
      );
   }

   @Override
   public UUID getId() {
      return this.eternalUUID;
   }

   @Override
   public long getSeed() {
      return this.seed;
   }

   @Override
   public String getName() {
      return this.eternalName;
   }

   @Override
   public Map<EquipmentSlot, ItemStack> getEquipment() {
      return Collections.unmodifiableMap(this.equipment);
   }

   @Nonnull
   public ItemStack getEquipment(EquipmentSlot slotType) {
      return this.equipment.getOrDefault(slotType, ItemStack.EMPTY).copy();
   }

   public Map<String, Float> getAttributes() {
      return Collections.unmodifiableMap(this.attributes);
   }

   public AttributeSnapshot getAttributeSnapshot() {
      return this.attributeSnapshot;
   }

   @Override
   public Map<Attribute, Float> getEntityAttributes() {
      return this.getAttributes().entrySet().stream().map(e -> {
         Attribute attr = (Attribute)ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation(e.getKey()));
         return attr != null ? new Tuple(attr, e.getValue()) : null;
      }).filter(Objects::nonNull).collect(Collectors.toMap(Tuple::getA, Tuple::getB));
   }

   public float getResistance() {
      return this.attributeSnapshot.getAttributeValue(ModGearAttributes.RESISTANCE, VaultGearAttributeTypeMerger.floatSum());
   }

   @Override
   public int getLevel() {
      return this.level;
   }

   public int getUsedLevels() {
      return this.usedLevels;
   }

   @Override
   public int getMaxLevel() {
      return this.maxLevel;
   }

   public float getLevelPercent() {
      return this.levelPercent;
   }

   @Override
   public boolean isAlive() {
      return this.alive;
   }

   @Override
   public boolean isAncient() {
      return this.ancient;
   }

   @Override
   public EternalsData.EternalVariant getVariant() {
      return this.variant;
   }

   @Override
   public boolean isUsingPlayerSkin() {
      return this.isUsingPlayerSkin;
   }

   @Nullable
   @Override
   public String getAbilityName() {
      return this.abilityName;
   }

   public boolean areStatisticsEqual(EternalDataSnapshot other) {
      if (this.alive != other.alive || !Objects.equals(this.abilityName, other.abilityName)) {
         return false;
      } else if (this.level != other.level || this.maxLevel != other.maxLevel || this.usedLevels != other.usedLevels) {
         return false;
      } else if (this.levelPercent != other.levelPercent) {
         return false;
      } else {
         float thisVal = this.attributes.get(ATTR_HEALTH);
         float thatVal = other.attributes.get(ATTR_HEALTH);
         if (thisVal != thatVal) {
            return false;
         } else {
            thisVal = this.attributes.get(ATTR_DAMAGE);
            thatVal = other.attributes.get(ATTR_DAMAGE);
            if (thisVal != thatVal) {
               return false;
            } else {
               thisVal = this.attributes.get(ATTR_SPEED);
               thatVal = other.attributes.get(ATTR_SPEED);
               return thisVal == thatVal;
            }
         }
      }
   }

   public void serialize(FriendlyByteBuf buffer, boolean useEquipment) {
      buffer.writeUUID(this.eternalUUID);
      buffer.writeLong(this.seed);
      buffer.writeUtf(this.eternalName);
      buffer.writeInt(this.equipment.size());
      this.equipment.forEach((slot, stack) -> {
         buffer.writeEnum(slot);
         buffer.writeItem(useEquipment ? stack : ItemStack.EMPTY);
      });
      buffer.writeInt(this.attributes.size());
      this.attributes.forEach((attr, value) -> {
         buffer.writeUtf(attr);
         buffer.writeFloat(value);
      });
      buffer.writeInt(this.level);
      buffer.writeInt(this.usedLevels);
      buffer.writeInt(this.maxLevel);
      buffer.writeFloat(this.levelPercent);
      buffer.writeBoolean(this.alive);
      buffer.writeBoolean(this.ancient);
      buffer.writeBoolean(this.abilityName != null);
      if (this.abilityName != null) {
         buffer.writeUtf(this.abilityName);
      }

      buffer.writeEnum(this.variant);
      buffer.writeBoolean(this.isUsingPlayerSkin);
   }

   public static EternalDataSnapshot deserialize(FriendlyByteBuf buffer) {
      UUID eternalUUID = buffer.readUUID();
      long seed = buffer.readLong();
      String eternalName = buffer.readUtf(32767);
      Map<EquipmentSlot, ItemStack> equipment = new HashMap<>();
      int equipmentSize = buffer.readInt();

      for (int i = 0; i < equipmentSize; i++) {
         EquipmentSlot type = (EquipmentSlot)buffer.readEnum(EquipmentSlot.class);
         ItemStack stack = buffer.readItem();
         equipment.put(type, stack);
      }

      Map<String, Float> attributes = new HashMap<>();
      int attrSize = buffer.readInt();

      for (int i = 0; i < attrSize; i++) {
         String attribute = buffer.readUtf(32767);
         float val = buffer.readFloat();
         attributes.put(attribute, val);
      }

      int level = buffer.readInt();
      int usedLevels = buffer.readInt();
      int maxLevel = buffer.readInt();
      float levelPercent = buffer.readFloat();
      boolean alive = buffer.readBoolean();
      boolean ancient = buffer.readBoolean();
      String abilityName = buffer.readBoolean() ? buffer.readUtf(32767) : null;
      EternalsData.EternalVariant variant = (EternalsData.EternalVariant)buffer.readEnum(EternalsData.EternalVariant.class);
      boolean isUsingPlayerSkin = buffer.readBoolean();
      return new EternalDataSnapshot(
         eternalUUID,
         seed,
         eternalName,
         equipment,
         attributes,
         level,
         usedLevels,
         maxLevel,
         levelPercent,
         alive,
         ancient,
         abilityName,
         variant,
         isUsingPlayerSkin
      );
   }
}
