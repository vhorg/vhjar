package iskallia.vault.container.slot.player;

import com.mojang.datafixers.util.Pair;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.EquipmentSlotType.Group;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class PlayerArmorEditSlot extends Slot {
   private static final ResourceLocation[] ARMOR_SLOT_TEXTURES = new ResourceLocation[]{
      PlayerContainer.field_226619_g_, PlayerContainer.field_226618_f_, PlayerContainer.field_226617_e_, PlayerContainer.field_226616_d_
   };
   private final PlayerInventory playerInventory;
   private final EquipmentSlotType slotType;

   public PlayerArmorEditSlot(PlayerInventory inventory, EquipmentSlotType slotType, int index, int xPosition, int yPosition) {
      super(inventory, index, xPosition, yPosition);
      this.playerInventory = inventory;
      this.slotType = slotType;
   }

   public int func_75219_a() {
      return 1;
   }

   public boolean func_75214_a(ItemStack stack) {
      return stack.canEquip(this.slotType, this.playerInventory.field_70458_d);
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public Pair<ResourceLocation, ResourceLocation> func_225517_c_() {
      return this.slotType.func_188453_a() != Group.ARMOR ? null : Pair.of(PlayerContainer.field_226615_c_, ARMOR_SLOT_TEXTURES[this.slotType.func_188454_b()]);
   }
}
