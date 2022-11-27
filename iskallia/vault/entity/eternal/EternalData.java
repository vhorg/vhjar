package iskallia.vault.entity.eternal;

import iskallia.vault.init.ModConfigs;
import iskallia.vault.snapshot.AttributeSnapshot;
import iskallia.vault.snapshot.AttributeSnapshotHelper;
import iskallia.vault.world.data.EternalsData;
import iskallia.vault.world.data.PlayerVaultStatsData;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlot.Type;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.server.ServerLifecycleHooks;

public class EternalData implements INBTSerializable<CompoundTag>, EternalDataAccess {
   private UUID uuid = UUID.randomUUID();
   private String name;
   private String originalName;
   private long eternalSeed = 3274487651937260739L;
   private final Map<EquipmentSlot, ItemStack> equipment = new HashMap<>();
   private EternalAttributes attributes = new EternalAttributes();
   private EternalAura ability = null;
   private int level = 0;
   private int usedLevels = 0;
   private int levelExp = 0;
   private boolean alive = true;
   private boolean ancient = false;
   private final EternalsData dataDelegate;
   private AttributeSnapshot snapshot = AttributeSnapshot.EMPTY;
   private EternalsData.EternalVariant variant = EternalsData.EternalVariant.CAVE;
   private boolean isUsingPlayerSkin = false;

   private EternalData(EternalsData dataDelegate, String name, boolean isAncient, EternalsData.EternalVariant variant, boolean isUsingPlayerSkin) {
      this.dataDelegate = dataDelegate;
      this.name = name;
      this.originalName = name;
      this.ancient = isAncient;
      this.attributes.initializeAttributes();
      this.variant = variant;
      this.isUsingPlayerSkin = isUsingPlayerSkin;
      this.refreshSnapshot();
   }

   private EternalData(EternalsData dataDelegate, CompoundTag nbt) {
      this.dataDelegate = dataDelegate;
      this.deserializeNBT(nbt);
      this.refreshSnapshot();
   }

   public static EternalData createEternal(EternalsData data, String name, boolean isAncient, EternalsData.EternalVariant variant, boolean isUsingPlayerSkin) {
      return new EternalData(data, name, isAncient, variant, isUsingPlayerSkin);
   }

   public static EternalData fromNBT(EternalsData data, CompoundTag nbt) {
      return new EternalData(data, nbt);
   }

   private void refreshSnapshot() {
      this.snapshot = AttributeSnapshotHelper.getInstance().makeGearSnapshot(this::getStack);
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
      this.dataDelegate.setDirty();
   }

   public void setName(String name) {
      this.name = name;
      this.dataDelegate.setDirty();
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

   public AttributeSnapshot getAttributeSnapshot() {
      return this.snapshot;
   }

   public void addAttributeValue(Attribute attribute, float value) {
      if (this.usedLevels < this.level) {
         this.usedLevels++;
         this.attributes.addAttributeValue(attribute, value);
         this.dataDelegate.setDirty();
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
      AtomicInteger integer = new AtomicInteger(0);
      MinecraftServer srv = ServerLifecycleHooks.getCurrentServer();
      UUID playerId = this.dataDelegate.getOwnerOf(this.getId());
      integer.set(PlayerVaultStatsData.get(srv).getVaultStats(playerId).getVaultLevel());
      return integer.get();
   }

   public int getMaxLevel(ServerLevel level) {
      UUID playerId = this.dataDelegate.getOwnerOf(this.getId());
      return PlayerVaultStatsData.get(level).getVaultStats(playerId).getVaultLevel();
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
      this.dataDelegate.setDirty();
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

   public void setUsingPlayerSkin(boolean usingPlayerSkin) {
      this.isUsingPlayerSkin = usingPlayerSkin;
   }

   public void setAncient(boolean ancient) {
      this.ancient = ancient;
      this.dataDelegate.setDirty();
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

         this.dataDelegate.setDirty();
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

      this.dataDelegate.setDirty();
   }

   @Nullable
   @Override
   public String getAbilityName() {
      return this.ability == null ? null : this.ability.getAuraName();
   }

   @Override
   public Map<EquipmentSlot, ItemStack> getEquipment() {
      return Collections.unmodifiableMap(this.equipment);
   }

   public ItemStack getStack(EquipmentSlot slot) {
      return this.equipment.getOrDefault(slot, ItemStack.EMPTY);
   }

   public void setStack(EquipmentSlot slot, ItemStack stack) {
      this.equipment.put(slot, stack);
      this.dataDelegate.setDirty();
      this.refreshSnapshot();
   }

   @Override
   public Map<Attribute, Float> getEntityAttributes() {
      return Collections.unmodifiableMap(this.attributes.getAttributes());
   }

   public EternalData.EquipmentInventory getEquipmentInventory(Runnable onChange) {
      return new EternalData.EquipmentInventory(this, onChange);
   }

   public CompoundTag serializeNBT() {
      CompoundTag nbt = new CompoundTag();
      nbt.putUUID("Id", this.getId());
      nbt.putString("Name", this.getName());
      nbt.putString("originalName", this.getOriginalName());
      nbt.putLong("eternalSeed", this.getSeed());
      CompoundTag tag = new CompoundTag();
      this.equipment.forEach((slot, stack) -> tag.put(slot.getName(), stack.serializeNBT()));
      nbt.put("equipment", tag);
      if (this.getAura() != null) {
         nbt.put("ability", this.getAura().serializeNBT());
      }

      nbt.putInt("level", this.level);
      nbt.putInt("usedLevels", this.usedLevels);
      nbt.putInt("levelExp", this.levelExp);
      nbt.putBoolean("alive", this.alive);
      nbt.putBoolean("ancient", this.ancient);
      nbt.put("attributes", this.attributes.serializeNBT());
      nbt.putInt("variant", this.variant.getId());
      nbt.putBoolean("isUsingPlayerSkin", this.isUsingPlayerSkin);
      return nbt;
   }

   public void deserializeNBT(CompoundTag nbt) {
      this.uuid = nbt.getUUID("Id");
      this.name = nbt.getString("Name");
      this.originalName = nbt.contains("originalName", 8) ? nbt.getString("originalName") : this.name;
      this.eternalSeed = nbt.contains("eternalSeed", 4) ? nbt.getLong("eternalSeed") : 3274487651937260739L;
      this.equipment.clear();
      CompoundTag equipment = nbt.getCompound("equipment");

      for (EquipmentSlot slot : EquipmentSlot.values()) {
         if (equipment.contains(slot.getName(), 10)) {
            ItemStack stack = ItemStack.of(equipment.getCompound(slot.getName()));
            this.equipment.put(slot, stack);
         }
      }

      if (nbt.contains("ability", 10)) {
         this.ability = new EternalAura(nbt.getCompound("ability"));
      } else {
         this.ability = null;
      }

      this.level = nbt.contains("level", 3) ? nbt.getInt("level") : 0;
      this.usedLevels = nbt.contains("usedLevels", 3) ? nbt.getInt("usedLevels") : 0;
      this.levelExp = nbt.getInt("levelExp");
      this.alive = !nbt.contains("alive", 1) || nbt.getBoolean("alive");
      this.ancient = nbt.contains("ancient", 1) && nbt.getBoolean("ancient");
      if (!nbt.contains("attributes", 10)) {
         this.attributes = new EternalAttributes();
         this.attributes.initializeAttributes();
      } else {
         this.attributes = EternalAttributes.fromNBT(nbt.getCompound("attributes"));
      }

      this.variant = EternalsData.EternalVariant.byId(nbt.getInt("variant"));
      this.isUsingPlayerSkin = nbt.getBoolean("isUsingPlayerSkin");
      if (nbt.contains("MainSlots")) {
         ListTag mainSlotsList = nbt.getList("MainSlots", 10);

         for (int i = 0; i < Math.min(mainSlotsList.size(), EquipmentSlot.values().length); i++) {
            EquipmentSlot slotx = EquipmentSlot.values()[i];
            if (slotx != EquipmentSlot.OFFHAND) {
               this.equipment.put(slotx, ItemStack.of(mainSlotsList.getCompound(i)));
            }
         }
      }
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else {
         return !(o instanceof EternalData other) ? false : this.uuid.equals(other.uuid);
      }
   }

   @Override
   public int hashCode() {
      return this.uuid.hashCode();
   }

   public static class EquipmentInventory implements Container {
      private final EternalData eternal;
      private final Runnable onChange;

      public EquipmentInventory(EternalData eternal, Runnable onChange) {
         this.eternal = eternal;
         this.onChange = onChange;
      }

      public int getContainerSize() {
         return 5;
      }

      public boolean isEmpty() {
         return this.eternal.getEquipment().entrySet().stream().anyMatch(entry -> !entry.getValue().isEmpty());
      }

      public ItemStack getItem(int index) {
         return this.eternal.getStack(this.getSlotFromIndex(index));
      }

      public ItemStack removeItem(int index, int count) {
         ItemStack stack = this.getItem(index);
         if (!stack.isEmpty() && count > 0) {
            ItemStack split = stack.split(count);
            this.setItem(index, stack);
            if (!split.isEmpty()) {
               this.setChanged();
            }

            return split;
         } else {
            return ItemStack.EMPTY;
         }
      }

      public ItemStack removeItemNoUpdate(int index) {
         EquipmentSlot slotType = this.getSlotFromIndex(index);
         ItemStack equipment = this.eternal.getStack(slotType);
         this.eternal.setStack(slotType, ItemStack.EMPTY);
         this.setChanged();
         return equipment;
      }

      public void setItem(int index, ItemStack stack) {
         this.eternal.setStack(this.getSlotFromIndex(index), stack.copy());
         this.setChanged();
      }

      public void setChanged() {
         this.onChange.run();
         this.eternal.dataDelegate.setDirty();
      }

      public boolean stillValid(Player player) {
         return true;
      }

      public void clearContent() {
         for (EquipmentSlot slotType : EquipmentSlot.values()) {
            this.eternal.setStack(slotType, ItemStack.EMPTY);
         }

         this.setChanged();
      }

      private EquipmentSlot getSlotFromIndex(int index) {
         return index == 0 ? EquipmentSlot.MAINHAND : EquipmentSlot.byTypeAndIndex(Type.ARMOR, index - 1);
      }
   }
}
