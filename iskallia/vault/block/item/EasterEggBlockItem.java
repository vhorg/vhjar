package iskallia.vault.block.item;

import iskallia.vault.block.EasterEggBlock;
import iskallia.vault.init.ModItems;
import javax.annotation.Nonnull;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class EasterEggBlockItem extends BlockItem {
   public EasterEggBlockItem(Block block) {
      super(block, new Properties().stacksTo(64).tab(ModItems.VAULT_MOD_GROUP));
   }

   public static ItemStack fromColor(Block block, EasterEggBlock.Color color) {
      ItemStack stack = new ItemStack(block);
      stack.getOrCreateTag().putString("color", color.getSerializedName());
      return stack;
   }

   @Nonnull
   public Component getName(ItemStack itemStack) {
      CompoundTag nbt = itemStack.getTag();
      if (nbt == null) {
         return super.getName(itemStack);
      } else {
         String color = nbt.getString("color");
         return new TranslatableComponent(itemStack.getItem().getDescriptionId() + "_" + color);
      }
   }

   public void inventoryTick(ItemStack stack, Level world, Entity entity, int slotId, boolean isSelected) {
      super.inventoryTick(stack, world, entity, slotId, isSelected);
      if (!stack.hasTag() || !stack.getTag().contains("color", 8)) {
         stack.getOrCreateTag().putString("color", EasterEggBlock.Color.PINK.getSerializedName());
      }
   }
}
