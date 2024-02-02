package iskallia.vault.item;

import iskallia.vault.core.vault.influence.VaultGod;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class GodTokenItem extends BasicItem {
   public GodTokenItem(ResourceLocation id) {
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

   @NotNull
   public Component getName(@NotNull ItemStack stack) {
      CompoundTag nbt = stack.getTag();
      if (nbt == null) {
         return super.getName(stack);
      } else {
         String type = nbt.getString("type");
         return new TranslatableComponent(stack.getItem().getDescriptionId() + "_" + type);
      }
   }

   public static VaultGod getGod(ItemStack stack) {
      CompoundTag nbt = stack.getTag();
      if (nbt == null) {
         return null;
      } else {
         String type = nbt.getString("type");
         return Enum.valueOf(VaultGod.class, type.toUpperCase());
      }
   }
}
