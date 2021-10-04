package iskallia.vault.entity.eternal;

import iskallia.vault.init.ModConfigs;
import iskallia.vault.world.data.EternalsData;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.EquipmentSlotType.Group;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.INBTSerializable;

public class EternalData implements INBTSerializable<CompoundNBT>, EternalDataAccess {
   private UUID uuid = UUID.randomUUID();
   private String name;
   private String originalName;
   private long eternalSeed = 3274487651937260739L;
   private final Map<EquipmentSlotType, ItemStack> equipment = new HashMap<>();
   private EternalAttributes attributes = new EternalAttributes();
   private EternalAura ability = null;
   private int level = 0;
   private int usedLevels = 0;
   private int levelExp = 0;
   private boolean alive = true;
   private boolean ancient = false;
   private final EternalsData dataDelegate;

   private EternalData(EternalsData dataDelegate, String name, boolean isAncient) {
      this.dataDelegate = dataDelegate;
      this.name = name;
      this.originalName = name;
      this.ancient = isAncient;
      this.attributes.initializeAttributes();
   }

   private EternalData(EternalsData dataDelegate, CompoundNBT nbt) {
      this.dataDelegate = dataDelegate;
      this.deserializeNBT(nbt);
   }

   public static EternalData createEternal(EternalsData data, String name, boolean isAncient) {
      return new EternalData(data, name, isAncient);
   }

   public static EternalData fromNBT(EternalsData data, CompoundNBT nbt) {
      return new EternalData(data, nbt);
   }

   @Override
   public UUID getId() {
      return this.uuid;
   }

   @Override
   public long getSeed() {
      return this.eternalSeed;
   }

   public void shuffleSeed() {
      this.eternalSeed = new Random().nextLong();
      this.dataDelegate.func_76185_a();
   }

   public void setName(String name) {
      this.name = name;
      this.dataDelegate.func_76185_a();
   }

   @Override
   public String getName() {
      return this.name;
   }

   public String getOriginalName() {
      return this.originalName;
   }

   public EternalAttributes getAttributes() {
      return this.attributes;
   }

   public void addAttributeValue(Attribute attribute, float value) {
      if (this.usedLevels < this.level) {
         this.usedLevels++;
         this.attributes.addAttributeValue(attribute, value);
         this.dataDelegate.func_76185_a();
      }
   }

   @Override
   public int getLevel() {
      return this.level;
   }

   public void setLevel(int level) {
      this.level = level;
   }

   public int getUsedLevels() {
      return this.usedLevels;
   }

   @Override
   public int getMaxLevel() {
      UUID playerId = this.dataDelegate.getOwnerOf(this.getId());
      return playerId == null ? 0 : this.dataDelegate.getEternals(playerId).getNonAncientEternalCount() + (this.isAncient() ? 5 : 0);
   }

   public float getLevelPercent() {
      int expNeeded = ModConfigs.ETERNAL.getExpForLevel(this.getLevel() + 1);
      return (float)this.levelExp / expNeeded;
   }

   @Override
   public boolean isAlive() {
      return this.alive;
   }

   public void setAlive(boolean alive) {
      this.alive = alive;
      this.dataDelegate.func_76185_a();
   }

   @Override
   public boolean isAncient() {
      return this.ancient;
   }

   public void setAncient(boolean ancient) {
      this.ancient = ancient;
      this.dataDelegate.func_76185_a();
   }

   public boolean addExp(int xp) {
      if (this.level >= this.getMaxLevel()) {
         return false;
      } else {
         this.levelExp += xp;
         int expNeeded = ModConfigs.ETERNAL.getExpForLevel(this.getLevel() + 1);
         if (this.levelExp >= expNeeded) {
            this.level++;
            this.levelExp -= expNeeded;
         }

         this.dataDelegate.func_76185_a();
         return true;
      }
   }

   @Nullable
   public EternalAura getAura() {
      return this.ability;
   }

   public void setAura(@Nullable String auraName) {
      if (auraName == null) {
         this.ability = null;
      } else {
         this.ability = new EternalAura(auraName);
      }

      this.dataDelegate.func_76185_a();
   }

   @Nullable
   @Override
   public String getAbilityName() {
      return this.ability == null ? null : this.ability.getAuraName();
   }

   @Override
   public Map<EquipmentSlotType, ItemStack> getEquipment() {
      return Collections.unmodifiableMap(this.equipment);
   }

   public ItemStack getStack(EquipmentSlotType slot) {
      return this.equipment.getOrDefault(slot, ItemStack.field_190927_a);
   }

   public void setStack(EquipmentSlotType slot, ItemStack stack) {
      this.equipment.put(slot, stack);
      this.dataDelegate.func_76185_a();
   }

   @Override
   public Map<Attribute, Float> getEntityAttributes() {
      return Collections.unmodifiableMap(this.attributes.getAttributes());
   }

   public EternalData.EquipmentInventory getEquipmentInventory(Runnable onChange) {
      return new EternalData.EquipmentInventory(this, onChange);
   }

   public CompoundNBT serializeNBT() {
      CompoundNBT nbt = new CompoundNBT();
      nbt.func_186854_a("Id", this.getId());
      nbt.func_74778_a("Name", this.getName());
      nbt.func_74778_a("originalName", this.getOriginalName());
      nbt.func_74772_a("eternalSeed", this.getSeed());
      CompoundNBT tag = new CompoundNBT();
      this.equipment.forEach((slot, stack) -> tag.func_218657_a(slot.func_188450_d(), stack.serializeNBT()));
      nbt.func_218657_a("equipment", tag);
      if (this.getAura() != null) {
         nbt.func_218657_a("ability", this.getAura().serializeNBT());
      }

      nbt.func_74768_a("level", this.level);
      nbt.func_74768_a("usedLevels", this.usedLevels);
      nbt.func_74768_a("levelExp", this.levelExp);
      nbt.func_74757_a("alive", this.alive);
      nbt.func_74757_a("ancient", this.ancient);
      nbt.func_218657_a("attributes", this.attributes.serializeNBT());
      return nbt;
   }

   public void deserializeNBT(CompoundNBT nbt) {
      this.uuid = nbt.func_186857_a("Id");
      this.name = nbt.func_74779_i("Name");
      this.originalName = nbt.func_150297_b("originalName", 8) ? nbt.func_74779_i("originalName") : this.name;
      this.eternalSeed = nbt.func_150297_b("eternalSeed", 4) ? nbt.func_74763_f("eternalSeed") : 3274487651937260739L;
      this.equipment.clear();
      CompoundNBT equipment = nbt.func_74775_l("equipment");

      for (EquipmentSlotType slot : EquipmentSlotType.values()) {
         if (equipment.func_150297_b(slot.func_188450_d(), 10)) {
            ItemStack stack = ItemStack.func_199557_a(equipment.func_74775_l(slot.func_188450_d()));
            this.equipment.put(slot, stack);
         }
      }

      if (nbt.func_150297_b("ability", 10)) {
         this.ability = new EternalAura(nbt.func_74775_l("ability"));
      } else {
         this.ability = null;
      }

      this.level = nbt.func_150297_b("level", 3) ? nbt.func_74762_e("level") : 0;
      this.usedLevels = nbt.func_150297_b("usedLevels", 3) ? nbt.func_74762_e("usedLevels") : 0;
      this.levelExp = nbt.func_74762_e("levelExp");
      this.alive = !nbt.func_150297_b("alive", 1) || nbt.func_74767_n("alive");
      this.ancient = nbt.func_150297_b("ancient", 1) && nbt.func_74767_n("ancient");
      if (!nbt.func_150297_b("attributes", 10)) {
         this.attributes = new EternalAttributes();
         this.attributes.initializeAttributes();
      } else {
         this.attributes = EternalAttributes.fromNBT(nbt.func_74775_l("attributes"));
      }

      if (nbt.func_74764_b("MainSlots")) {
         ListNBT mainSlotsList = nbt.func_150295_c("MainSlots", 10);

         for (int i = 0; i < Math.min(mainSlotsList.size(), EquipmentSlotType.values().length); i++) {
            EquipmentSlotType slotx = EquipmentSlotType.values()[i];
            if (slotx != EquipmentSlotType.OFFHAND) {
               this.equipment.put(slotx, ItemStack.func_199557_a(mainSlotsList.func_150305_b(i)));
            }
         }
      }
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (!(o instanceof EternalData)) {
         return false;
      } else {
         EternalData other = (EternalData)o;
         return this.uuid.equals(other.uuid);
      }
   }

   @Override
   public int hashCode() {
      return this.uuid.hashCode();
   }

   public static class EquipmentInventory implements IInventory {
      private final EternalData eternal;
      private final Runnable onChange;

      public EquipmentInventory(EternalData eternal, Runnable onChange) {
         this.eternal = eternal;
         this.onChange = onChange;
      }

      public int func_70302_i_() {
         return 5;
      }

      public boolean func_191420_l() {
         return this.eternal.getEquipment().entrySet().stream().anyMatch(entry -> !entry.getValue().func_190926_b());
      }

      public ItemStack func_70301_a(int index) {
         return this.eternal.getStack(this.getSlotFromIndex(index));
      }

      public ItemStack func_70298_a(int index, int count) {
         ItemStack stack = this.func_70301_a(index);
         if (!stack.func_190926_b() && count > 0) {
            ItemStack split = stack.func_77979_a(count);
            this.func_70299_a(index, stack);
            if (!split.func_190926_b()) {
               this.func_70296_d();
            }

            return split;
         } else {
            return ItemStack.field_190927_a;
         }
      }

      public ItemStack func_70304_b(int index) {
         EquipmentSlotType slotType = this.getSlotFromIndex(index);
         ItemStack equipment = this.eternal.getStack(slotType);
         this.eternal.setStack(slotType, ItemStack.field_190927_a);
         this.func_70296_d();
         return equipment;
      }

      public void func_70299_a(int index, ItemStack stack) {
         this.eternal.setStack(this.getSlotFromIndex(index), stack.func_77946_l());
         this.func_70296_d();
      }

      public void func_70296_d() {
         this.onChange.run();
         this.eternal.dataDelegate.func_76185_a();
      }

      public boolean func_70300_a(PlayerEntity player) {
         return true;
      }

      public void func_174888_l() {
         for (EquipmentSlotType slotType : EquipmentSlotType.values()) {
            this.eternal.setStack(slotType, ItemStack.field_190927_a);
         }

         this.func_70296_d();
      }

      private EquipmentSlotType getSlotFromIndex(int index) {
         return index == 0 ? EquipmentSlotType.MAINHAND : EquipmentSlotType.func_220318_a(Group.ARMOR, index - 1);
      }
   }
}
