package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.config.entry.LevelEntryList;
import iskallia.vault.config.entry.SingleItemEntry;
import iskallia.vault.core.util.WeightedList;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.util.LootInitialization;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class VaultMobGearConfig extends Config {
   @Expose
   private final Map<ResourceLocation, LevelEntryList<VaultMobGearConfig.EquipmentSet>> MOB_GEAR = new HashMap<>();

   @Override
   public String getName() {
      return "vault_mobs_gear";
   }

   public Optional<VaultMobGearConfig.EquipmentSet> getEquipment(EntityType<?> entityType, int vaultLevel) {
      return Optional.ofNullable(this.MOB_GEAR.get(entityType.getRegistryName())).flatMap(levelList -> levelList.getForLevel(vaultLevel));
   }

   public static void applyEquipment(LivingEntity entity, int vaultLevel) {
      entity.detectEquipmentUpdates();
      VaultMobGearConfig.EquipmentSet set = ModConfigs.VAULT_MOBS_GEAR.getEquipment(entity.getType(), vaultLevel).orElse(null);
      if (set == null) {
         Arrays.stream(EquipmentSlot.values()).forEach(slotx -> entity.setItemSlot(slotx, ItemStack.EMPTY));
      } else {
         for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack equipment = set.getRandomEquipment(slot).orElse(ItemStack.EMPTY).copy();
            if (equipment.isEmpty()) {
               entity.setItemSlot(slot, ItemStack.EMPTY);
            } else {
               equipment = LootInitialization.initializeVaultLoot(equipment, vaultLevel);
               entity.setItemSlot(slot, equipment);
               if (equipment.getItem() instanceof VaultGearItem) {
                  if (entity instanceof AgeableMob ageable) {
                     ageable.setBaby(false);
                  }

                  if (entity instanceof Mob mob) {
                     mob.setBaby(false);
                  }
               }
            }
         }
      }
   }

   @Override
   protected void reset() {
      this.MOB_GEAR.clear();
      LevelEntryList<VaultMobGearConfig.EquipmentSet> equipmentSet = new LevelEntryList<>();
      VaultMobGearConfig.EquipmentSet set = new VaultMobGearConfig.EquipmentSet(0);
      set.slots
         .put(
            EquipmentSlot.HEAD.name(),
            new WeightedList<SingleItemEntry>().add(SingleItemEntry.EMPTY, 20).add(new SingleItemEntry(new ItemStack(Items.DIAMOND_HELMET)), 10)
         );
      set.slots.put(EquipmentSlot.CHEST.name(), new WeightedList<>());
      set.slots.put(EquipmentSlot.LEGS.name(), new WeightedList<>());
      set.slots.put(EquipmentSlot.FEET.name(), new WeightedList<>());
      set.slots.put(EquipmentSlot.MAINHAND.name(), new WeightedList<>());
      set.slots.put(EquipmentSlot.OFFHAND.name(), new WeightedList<>());
      equipmentSet.add(set);
      this.MOB_GEAR.put(EntityType.ZOMBIE.getRegistryName(), equipmentSet);
   }

   public static class EquipmentSet implements LevelEntryList.ILevelEntry {
      @Expose
      private int minLevel;
      @Expose
      private Map<String, WeightedList<SingleItemEntry>> slots = new LinkedHashMap<>();

      public EquipmentSet(int minLevel) {
         this.minLevel = minLevel;
      }

      @Override
      public int getLevel() {
         return this.minLevel;
      }

      public Optional<ItemStack> getRandomEquipment(EquipmentSlot slot) {
         WeightedList<SingleItemEntry> stackSet = this.slots.get(slot.name());
         return stackSet != null && !stackSet.isEmpty() ? stackSet.getRandom(Config.rand).map(SingleItemEntry::createItemStack) : Optional.empty();
      }
   }
}
