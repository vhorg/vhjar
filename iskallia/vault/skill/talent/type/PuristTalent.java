package iskallia.vault.skill.talent.type;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.array.ArrayAdapter;
import iskallia.vault.core.data.adapter.basic.EnumAdapter;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.gear.VaultGearRarity;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.skill.base.LearnableSkill;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class PuristTalent extends LearnableSkill {
   private Set<VaultGearRarity> rarities;
   private Set<EquipmentSlot> slots;
   private float damageIncrease;
   public static final ArrayAdapter<VaultGearRarity> RARITIES = Adapters.ofArray(
      VaultGearRarity[]::new, Adapters.ofEnum(VaultGearRarity.class, EnumAdapter.Mode.NAME)
   );
   public static final ArrayAdapter<EquipmentSlot> SLOTS = Adapters.ofArray(EquipmentSlot[]::new, Adapters.ofEnum(EquipmentSlot.class, EnumAdapter.Mode.NAME));

   public Set<VaultGearRarity> getRarity() {
      return this.rarities;
   }

   public Set<EquipmentSlot> getSlots() {
      return this.slots;
   }

   public float getDamageIncrease() {
      return this.damageIncrease;
   }

   public int getCount(LivingEntity entity) {
      int count = 0;

      for (EquipmentSlot slot : this.slots) {
         ItemStack stack = entity.getItemBySlot(slot);
         if (VaultGearData.hasData(stack)) {
            VaultGearData data = VaultGearData.read(stack);
            if (this.rarities.contains(data.getRarity())) {
               count++;
            }
         }
      }

      return count;
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      RARITIES.writeBits(this.rarities.toArray(new VaultGearRarity[0]), buffer);
      SLOTS.writeBits(this.slots.toArray(new EquipmentSlot[0]), buffer);
      Adapters.FLOAT.writeBits(Float.valueOf(this.damageIncrease), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.rarities = Arrays.stream(RARITIES.readBits(buffer).orElse(new VaultGearRarity[0])).collect(Collectors.toSet());
      this.slots = Arrays.stream(SLOTS.readBits(buffer).orElse(new EquipmentSlot[0])).collect(Collectors.toSet());
      this.damageIncrease = Adapters.FLOAT.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         RARITIES.writeNbt(this.rarities.toArray(new VaultGearRarity[0])).ifPresent(tag -> nbt.put("rarities", tag));
         SLOTS.writeNbt(this.slots.toArray(new EquipmentSlot[0])).ifPresent(tag -> nbt.put("slots", tag));
         Adapters.FLOAT.writeNbt(Float.valueOf(this.damageIncrease)).ifPresent(tag -> nbt.put("damageIncrease", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.rarities = Arrays.stream(RARITIES.readNbt(nbt.get("rarities")).orElse(new VaultGearRarity[0])).collect(Collectors.toSet());
      this.slots = Arrays.stream(SLOTS.readNbt(nbt.get("slots")).orElse(new EquipmentSlot[0])).collect(Collectors.toSet());
      this.damageIncrease = Adapters.FLOAT.readNbt(nbt.get("damageIncrease")).orElse(0.0F);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         RARITIES.writeJson(this.rarities.toArray(new VaultGearRarity[0])).ifPresent(tag -> json.add("rarities", tag));
         SLOTS.writeJson(this.slots.toArray(new EquipmentSlot[0])).ifPresent(tag -> json.add("slots", tag));
         Adapters.FLOAT.writeJson(Float.valueOf(this.damageIncrease)).ifPresent(tag -> json.add("damageIncrease", tag));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.rarities = Arrays.stream(RARITIES.readJson(json.get("rarities")).orElse(new VaultGearRarity[0])).collect(Collectors.toSet());
      this.slots = Arrays.stream(SLOTS.readJson(json.get("slots")).orElse(new EquipmentSlot[0])).collect(Collectors.toSet());
      this.damageIncrease = Adapters.FLOAT.readJson(json.get("damageIncrease")).orElse(0.0F);
   }
}
