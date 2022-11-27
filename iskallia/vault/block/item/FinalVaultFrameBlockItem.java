package iskallia.vault.block.item;

import com.mojang.authlib.GameProfile;
import iskallia.vault.init.ModItems;
import iskallia.vault.util.McClientHelper;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class FinalVaultFrameBlockItem extends BlockItem {
   public FinalVaultFrameBlockItem(Block blockIn) {
      super(blockIn, new Properties().tab(ModItems.VAULT_MOD_GROUP).stacksTo(1));
   }

   @Nonnull
   public Rarity getRarity(@Nonnull ItemStack stack) {
      return Rarity.EPIC;
   }

   public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
      CompoundTag blockEntityTag = stack.getOrCreateTagElement("BlockEntityTag");
      String stringUUID = blockEntityTag.getString("OwnerUUID");
      UUID ownerUUID = stringUUID.isEmpty() ? new UUID(0L, 0L) : UUID.fromString(stringUUID);
      String ownerNickname = blockEntityTag.getString("OwnerNickname");
      String displayNickname = McClientHelper.getOnlineProfile(ownerUUID).<String>map(GameProfile::getName).orElse(ownerNickname);
      MutableComponent ownerText = new TextComponent("Owner:").withStyle(ChatFormatting.GOLD);
      MutableComponent displayText = new TextComponent(displayNickname).withStyle(ChatFormatting.GOLD).withStyle(ChatFormatting.BOLD);
      tooltip.add(ownerText.append(displayText));
      super.appendHoverText(stack, worldIn, tooltip, flagIn);
   }

   public void inventoryTick(@Nonnull ItemStack itemStack, Level world, @Nonnull Entity entity, int itemSlot, boolean isSelected) {
      if (!world.isClientSide) {
         if (entity instanceof ServerPlayer player) {
            CompoundTag blockEntityTag = itemStack.getOrCreateTagElement("BlockEntityTag");
            if (!blockEntityTag.contains("OwnerUUID")) {
               writeToItemStack(itemStack, player);
               super.inventoryTick(itemStack, world, entity, itemSlot, isSelected);
            }
         }
      }
   }

   public static void writeToItemStack(ItemStack itemStack, ServerPlayer owner) {
      writeToItemStack(itemStack, owner.getUUID(), owner.getName().getString());
   }

   public static void writeToItemStack(ItemStack itemStack, UUID ownerUUID, String ownerNickname) {
      CompoundTag blockEntityTag = itemStack.getOrCreateTagElement("BlockEntityTag");
      blockEntityTag.putString("OwnerUUID", ownerUUID.toString());
      blockEntityTag.putString("OwnerNickname", ownerNickname);
   }
}
