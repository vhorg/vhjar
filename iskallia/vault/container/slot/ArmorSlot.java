package iskallia.vault.container.slot;

import com.mojang.datafixers.util.Pair;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.EquipmentSlot.Type;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ArmorSlot extends Slot {
   private static final ResourceLocation[] ARMOR_SLOT_TEXTURES = new ResourceLocation[]{
      InventoryMenu.EMPTY_ARMOR_SLOT_BOOTS,
      InventoryMenu.EMPTY_ARMOR_SLOT_LEGGINGS,
      InventoryMenu.EMPTY_ARMOR_SLOT_CHESTPLATE,
      InventoryMenu.EMPTY_ARMOR_SLOT_HELMET
   };
   private final EquipmentSlot slotType;

   public ArmorSlot(Container inventory, EquipmentSlot slotType, int index, int xPosition, int yPosition) {
      super(inventory, index, xPosition, yPosition);
      this.slotType = slotType;
   }

   public int getMaxStackSize() {
      return 1;
   }

   public boolean mayPlace(ItemStack stack) {
      try {
         return stack.canEquip(this.slotType, null);
      } catch (Exception var3) {
         return Mob.getEquipmentSlotForItem(stack) == this.slotType;
      }
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
      return this.slotType.getType() != Type.ARMOR ? null : Pair.of(InventoryMenu.BLOCK_ATLAS, ARMOR_SLOT_TEXTURES[this.slotType.getIndex()]);
   }
}
