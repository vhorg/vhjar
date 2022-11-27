package iskallia.vault.container.slot;

import com.mojang.datafixers.util.Pair;
import javax.annotation.Nonnull;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.jetbrains.annotations.Nullable;

public class ArmorTabSlot extends TabSlot {
   private static final ResourceLocation[] TEXTURE_EMPTY_SLOTS = new ResourceLocation[]{
      InventoryMenu.EMPTY_ARMOR_SLOT_BOOTS,
      InventoryMenu.EMPTY_ARMOR_SLOT_LEGGINGS,
      InventoryMenu.EMPTY_ARMOR_SLOT_CHESTPLATE,
      InventoryMenu.EMPTY_ARMOR_SLOT_HELMET
   };
   private final EquipmentSlot equipmentSlot;
   private final Player player;

   public ArmorTabSlot(Container container, int index, int x, int y, EquipmentSlot equipmentSlot, Player player) {
      super(container, index, x, y);
      this.equipmentSlot = equipmentSlot;
      this.player = player;
   }

   public int getMaxStackSize() {
      return 1;
   }

   public boolean mayPlace(ItemStack itemStack) {
      return itemStack.canEquip(this.equipmentSlot, this.player);
   }

   public boolean mayPickup(@Nonnull Player player) {
      ItemStack itemStack = this.getItem();
      return (itemStack.isEmpty() || player.isCreative() || !EnchantmentHelper.hasBindingCurse(itemStack)) && super.mayPickup(player);
   }

   @Nullable
   public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
      return Pair.of(InventoryMenu.BLOCK_ATLAS, TEXTURE_EMPTY_SLOTS[this.equipmentSlot.getIndex()]);
   }
}
