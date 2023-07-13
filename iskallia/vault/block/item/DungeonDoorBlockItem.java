package iskallia.vault.block.item;

import iskallia.vault.block.DungeonDoorBlock;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModItems;
import javax.annotation.Nonnull;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;

public class DungeonDoorBlockItem extends BlockItem {
   public DungeonDoorBlockItem() {
      super(ModBlocks.DUNGEON_DOOR, new Properties().tab(ModItems.VAULT_MOD_GROUP));
   }

   public static ItemStack fromType(DungeonDoorBlock.Type type) {
      ItemStack stack = new ItemStack(ModBlocks.DUNGEON_DOOR);
      stack.getOrCreateTag().putString("type", type.getSerializedName());
      return stack;
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
