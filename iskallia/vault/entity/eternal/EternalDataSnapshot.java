package iskallia.vault.entity.eternal;

import iskallia.vault.util.calc.ParryHelper;
import iskallia.vault.util.calc.ResistanceHelper;
import iskallia.vault.world.data.EternalsData;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraftforge.registries.ForgeRegistries;

public class EternalDataSnapshot implements EternalDataAccess {
   public static final String ATTR_HEALTH = Attributes.field_233818_a_.getRegistryName().toString();
   public static final String ATTR_DAMAGE = Attributes.field_233823_f_.getRegistryName().toString();
   public static final String ATTR_SPEED = Attributes.field_233821_d_.getRegistryName().toString();
   private final UUID eternalUUID;
   private final long seed;
   private final String eternalName;
   private final Map<EquipmentSlotType, ItemStack> equipment;
   private final Map<String, Float> attributes;
   private final float parry;
   private final float resistance;
   private final int level;
   private final int usedLevels;
   private final int maxLevel;
   private final float levelPercent;
   private final boolean alive;
   private final boolean ancient;
   private final String abilityName;

   public EternalDataSnapshot(
      UUID eternalUUID,
      long seed,
      String eternalName,
      Map<EquipmentSlotType, ItemStack> equipment,
      Map<String, Float> attributes,
      float parry,
      float resistance,
      int level,
      int usedLevels,
      int maxLevel,
      float levelPercent,
      boolean alive,
      boolean ancient,
      String abilityName
   ) {
      this.eternalUUID = eternalUUID;
      this.seed = seed;
      this.eternalName = eternalName;
      this.equipment = equipment;
      this.attributes = attributes;
      this.parry = parry;
      this.resistance = resistance;
      this.level = level;
      this.usedLevels = usedLevels;
      this.maxLevel = maxLevel;
      this.levelPercent = levelPercent;
      this.alive = alive;
      this.ancient = ancient;
      this.abilityName = abilityName;
   }

   public static EternalDataSnapshot getFromEternal(EternalsData.EternalGroup playerGroup, EternalData eternal) {
      UUID eternalUUID = eternal.getId();
      long seed = eternal.getSeed();
      String eternalName = eternal.getName();
      Map<EquipmentSlotType, ItemStack> equipment = new HashMap<>();

      for (EquipmentSlotType slotType : EquipmentSlotType.values()) {
         ItemStack stack = eternal.getStack(slotType);
         if (!stack.func_190926_b()) {
            equipment.put(slotType, stack.func_77946_l());
         }
      }

      EternalAttributes eternalAttributes = eternal.getAttributes();
      Map<String, Float> attributes = new HashMap<>();
      float value = eternalAttributes.getAttributeValue(Attributes.field_233818_a_).orElse(0.0F);
      value = EternalHelper.getEternalGearModifierAdjustments(eternal, Attributes.field_233818_a_, value);
      attributes.put(ATTR_HEALTH, value);
      value = eternalAttributes.getAttributeValue(Attributes.field_233823_f_).orElse(0.0F);
      value = EternalHelper.getEternalGearModifierAdjustments(eternal, Attributes.field_233823_f_, value);
      attributes.put(ATTR_DAMAGE, value);
      value = eternalAttributes.getAttributeValue(Attributes.field_233821_d_).orElse(0.0F);
      value = EternalHelper.getEternalGearModifierAdjustments(eternal, Attributes.field_233821_d_, value);
      attributes.put(ATTR_SPEED, value);
      float parry = ParryHelper.getGearParryChance(eternal::getStack);
      float resistance = ResistanceHelper.getGearResistanceChance(eternal::getStack);
      int level = eternal.getLevel();
      int usedLevels = eternal.getUsedLevels();
      int maxLevel = eternal.getMaxLevel();
      float levelPercent = eternal.getLevelPercent();
      boolean alive = eternal.isAlive();
      boolean ancient = eternal.isAncient();
      String abilityName = eternal.getAura() != null ? eternal.getAura().getAuraName() : null;
      return new EternalDataSnapshot(
         eternalUUID, seed, eternalName, equipment, attributes, parry, resistance, level, usedLevels, maxLevel, levelPercent, alive, ancient, abilityName
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
   public Map<EquipmentSlotType, ItemStack> getEquipment() {
      return Collections.unmodifiableMap(this.equipment);
   }

   public ItemStack getEquipment(EquipmentSlotType slotType) {
      return this.equipment.getOrDefault(slotType, ItemStack.field_190927_a).func_77946_l();
   }

   public Map<String, Float> getAttributes() {
      return Collections.unmodifiableMap(this.attributes);
   }

   @Override
   public Map<Attribute, Float> getEntityAttributes() {
      return this.getAttributes().entrySet().stream().map(e -> {
         Attribute attr = (Attribute)ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation(e.getKey()));
         return attr != null ? new Tuple(attr, e.getValue()) : null;
      }).filter(Objects::nonNull).collect(Collectors.toMap(Tuple::func_76341_a, Tuple::func_76340_b));
   }

   public float getParry() {
      return this.parry;
   }

   public float getResistance() {
      return this.resistance;
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
      } else if (this.parry == other.parry && this.resistance == other.resistance && this.levelPercent == other.levelPercent) {
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
      } else {
         return false;
      }
   }

   public void serialize(PacketBuffer buffer) {
      buffer.func_179252_a(this.eternalUUID);
      buffer.writeLong(this.seed);
      buffer.func_180714_a(this.eternalName);
      buffer.writeInt(this.equipment.size());
      this.equipment.forEach((slot, stack) -> {
         buffer.func_179249_a(slot);
         buffer.func_150788_a(stack);
      });
      buffer.writeInt(this.attributes.size());
      this.attributes.forEach((attr, value) -> {
         buffer.func_180714_a(attr);
         buffer.writeFloat(value);
      });
      buffer.writeFloat(this.parry);
      buffer.writeFloat(this.resistance);
      buffer.writeInt(this.level);
      buffer.writeInt(this.usedLevels);
      buffer.writeInt(this.maxLevel);
      buffer.writeFloat(this.levelPercent);
      buffer.writeBoolean(this.alive);
      buffer.writeBoolean(this.ancient);
      buffer.writeBoolean(this.abilityName != null);
      if (this.abilityName != null) {
         buffer.func_180714_a(this.abilityName);
      }
   }

   public static EternalDataSnapshot deserialize(PacketBuffer buffer) {
      UUID eternalUUID = buffer.func_179253_g();
      long seed = buffer.readLong();
      String eternalName = buffer.func_150789_c(32767);
      Map<EquipmentSlotType, ItemStack> equipment = new HashMap<>();
      int equipmentSize = buffer.readInt();

      for (int i = 0; i < equipmentSize; i++) {
         EquipmentSlotType type = (EquipmentSlotType)buffer.func_179257_a(EquipmentSlotType.class);
         ItemStack stack = buffer.func_150791_c();
         equipment.put(type, stack);
      }

      Map<String, Float> attributes = new HashMap<>();
      int attrSize = buffer.readInt();

      for (int i = 0; i < attrSize; i++) {
         String attribute = buffer.func_150789_c(32767);
         float val = buffer.readFloat();
         attributes.put(attribute, val);
      }

      float parry = buffer.readFloat();
      float resistance = buffer.readFloat();
      int level = buffer.readInt();
      int usedLevels = buffer.readInt();
      int maxLevel = buffer.readInt();
      float levelPercent = buffer.readFloat();
      boolean alive = buffer.readBoolean();
      boolean ancient = buffer.readBoolean();
      String abilityName = buffer.readBoolean() ? buffer.func_150789_c(32767) : null;
      return new EternalDataSnapshot(
         eternalUUID, seed, eternalName, equipment, attributes, parry, resistance, level, usedLevels, maxLevel, levelPercent, alive, ancient, abilityName
      );
   }
}
