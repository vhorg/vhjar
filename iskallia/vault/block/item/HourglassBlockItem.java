package iskallia.vault.block.item;

import iskallia.vault.init.ModItems;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class HourglassBlockItem extends BlockItem {
   public HourglassBlockItem(Block blockIn) {
      super(blockIn, new Properties().tab(ModItems.VAULT_MOD_GROUP).stacksTo(1));
   }

   @OnlyIn(Dist.CLIENT)
   public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
      CompoundTag tag = stack.getOrCreateTag().getCompound("BlockEntityTag");
      if (tag.contains("ownerPlayerName")) {
         tooltip.add(new TextComponent(tag.getString("ownerPlayerName")).withStyle(ChatFormatting.GOLD));
      }

      super.appendHoverText(stack, worldIn, tooltip, flagIn);
   }

   public static void addHourglassOwner(ItemStack stack, UUID playerUUID, String playerName) {
      if (stack.getItem() instanceof HourglassBlockItem) {
         CompoundTag tileTag = new CompoundTag();
         tileTag.putUUID("ownerUUID", playerUUID);
         tileTag.putString("ownerPlayerName", playerName);
         stack.getOrCreateTag().put("BlockEntityTag", tileTag);
      }
   }
}
