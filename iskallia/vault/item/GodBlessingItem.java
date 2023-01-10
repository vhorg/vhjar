package iskallia.vault.item;

import iskallia.vault.core.vault.influence.VaultGod;
import javax.annotation.Nonnull;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class GodBlessingItem extends BasicItem {
   public GodBlessingItem(ResourceLocation id) {
      super(id);
   }

   public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> items) {
      if (tab == CreativeModeTab.TAB_SEARCH) {
         for (VaultGod god : VaultGod.values()) {
            ItemStack stack = new ItemStack(this);
            stack.getOrCreateTag().putString("type", god.getName().toLowerCase());
            items.add(stack);
         }
      }
   }

   @Nonnull
   public Component getName(ItemStack itemStack) {
      CompoundTag nbt = itemStack.getTag();
      if (nbt == null) {
         return super.getName(itemStack);
      } else {
         String type = nbt.getString("type");
         return new TranslatableComponent(itemStack.getItem().getDescriptionId() + "_" + type);
      }
   }
}
