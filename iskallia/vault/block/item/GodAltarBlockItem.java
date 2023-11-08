package iskallia.vault.block.item;

import iskallia.vault.core.vault.influence.VaultGod;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModItems;
import javax.annotation.Nonnull;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;

public class GodAltarBlockItem extends BlockItem {
   public GodAltarBlockItem() {
      super(ModBlocks.GOD_ALTAR, new Properties().tab(ModItems.VAULT_MOD_GROUP));
   }

   public static ItemStack fromType(VaultGod god) {
      ItemStack stack = new ItemStack(ModBlocks.GOD_ALTAR);
      stack.getOrCreateTag().putString("god", god.getName().toLowerCase());
      return stack;
   }

   @Nonnull
   public Component getName(ItemStack itemStack) {
      CompoundTag nbt = itemStack.getTag();
      if (nbt != null && nbt.contains("god", 8)) {
         String god = nbt.getString("god");
         return new TranslatableComponent(itemStack.getItem().getDescriptionId() + "." + god);
      } else {
         return super.getName(itemStack);
      }
   }
}
