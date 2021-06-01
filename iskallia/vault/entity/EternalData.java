package iskallia.vault.entity;

import iskallia.vault.init.ModEntities;
import iskallia.vault.world.data.EternalsData;
import java.util.UUID;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.INBTSerializable;

public class EternalData implements INBTSerializable<CompoundNBT> {
   private UUID uuid = UUID.randomUUID();
   private String name;
   private ItemStack[] mainSlots = new ItemStack[EquipmentSlotType.values().length];

   protected EternalData() {
   }

   public EternalData(String name) {
      this.name = name;
   }

   public UUID getId() {
      return this.uuid;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getName() {
      return this.name;
   }

   public ItemStack getStack(EquipmentSlotType slot) {
      return this.mainSlots[slot.ordinal()] == null ? ItemStack.field_190927_a : this.mainSlots[slot.ordinal()];
   }

   public void setStack(EquipmentSlotType slot, ItemStack stack) {
      this.mainSlots[slot.ordinal()] = stack;
   }

   public EternalEntity create(World world) {
      EternalEntity eternal = (EternalEntity)ModEntities.ETERNAL.func_200721_a(world);
      EternalsData data = EternalsData.get((ServerWorld)world);
      int level = data.getEternals(data.getOwnerOf(this.getId())).getEternals().size();
      eternal.func_200203_b(
         new StringTextComponent("[")
            .func_230529_a_(new StringTextComponent(String.valueOf(level)).func_240699_a_(TextFormatting.GREEN))
            .func_230529_a_(new StringTextComponent("] " + this.getName()))
      );

      for (EquipmentSlotType slot : EquipmentSlotType.values()) {
         eternal.func_184201_a(slot, this.getStack(slot).func_77946_l());
      }

      return eternal;
   }

   public CompoundNBT serializeNBT() {
      CompoundNBT nbt = new CompoundNBT();
      if (this.getId() != null) {
         nbt.func_186854_a("Id", this.getId());
      }

      if (this.getName() != null) {
         nbt.func_74778_a("Name", this.getName());
      }

      if (this.mainSlots != null) {
         ListNBT mainSlotsList = new ListNBT();

         for (EquipmentSlotType slot : EquipmentSlotType.values()) {
            mainSlotsList.add(this.getStack(slot).serializeNBT());
         }

         nbt.func_218657_a("MainSlots", mainSlotsList);
      }

      return nbt;
   }

   public void deserializeNBT(CompoundNBT nbt) {
      if (nbt.func_74764_b("Id")) {
         this.uuid = nbt.func_186857_a("Id");
      }

      this.name = nbt.func_74779_i("Name");
      if (nbt.func_74764_b("MainSlots")) {
         ListNBT mainSlotsList = nbt.func_150295_c("MainSlots", 10);

         for (int i = 0; i < Math.min(mainSlotsList.size(), EquipmentSlotType.values().length); i++) {
            this.setStack(EquipmentSlotType.values()[i], ItemStack.func_199557_a(mainSlotsList.func_150305_b(i)));
         }
      }
   }

   public static EternalData fromNBT(CompoundNBT nbt) {
      EternalData eternal = new EternalData();
      eternal.deserializeNBT(nbt);
      return eternal;
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
}
