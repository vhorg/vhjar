package iskallia.vault.block.item;

import com.mojang.authlib.GameProfile;
import iskallia.vault.block.HeraldTrophyBlock;
import iskallia.vault.client.gui.helper.UIHelper;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModItems;
import iskallia.vault.util.McClientHelper;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class HeraldTrophyItem extends BlockItem {
   public static final String NBT_OWNER_UUID = "OwnerUUID";
   public static final String NBT_OWNER_NAME = "OwnerName";
   public static final String NBT_VARIANT = "Variant";
   public static final String NBT_TIME = "Time";

   public HeraldTrophyItem(Block block) {
      super(block, new Properties().tab(ModItems.VAULT_MOD_GROUP).stacksTo(1));
   }

   public String getDescriptionId(ItemStack stack) {
      HeraldTrophyBlock.Variant variant = getVariant(stack);
      return super.getDescriptionId(stack) + (variant == null ? "" : "." + variant.getName());
   }

   public void inventoryTick(@Nonnull ItemStack itemStack, @Nonnull Level world, @Nonnull Entity entity, int itemSlot, boolean isSelected) {
      if (!world.isClientSide) {
         if (entity instanceof ServerPlayer player) {
            CompoundTag blockEntityTag = itemStack.getOrCreateTagElement("BlockEntityTag");
            if (!blockEntityTag.contains("OwnerUUID")) {
               blockEntityTag.putString("OwnerUUID", player.getUUID().toString());
               blockEntityTag.putString("OwnerName", player.getName().getString());
               super.inventoryTick(itemStack, world, entity, itemSlot, isSelected);
            }
         }
      }
   }

   public static HeraldTrophyBlock.Variant getVariant(ItemStack stack) {
      CompoundTag blockEntityTag = stack.getOrCreateTagElement("BlockEntityTag");
      return HeraldTrophyBlock.Variant.fromString(blockEntityTag.getString("Variant"));
   }

   @OnlyIn(Dist.CLIENT)
   public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level worldIn, @Nonnull List<Component> tooltip, @Nonnull TooltipFlag flag) {
      super.appendHoverText(stack, worldIn, tooltip, flag);
      CompoundTag blockEntityTag = stack.getOrCreateTagElement("BlockEntityTag");
      String uuidString = blockEntityTag.getString("OwnerUUID");
      UUID ownerUUID = uuidString.isEmpty() ? null : UUID.fromString(uuidString);
      String ownerNickname = McClientHelper.getOnlineProfile(ownerUUID).<String>map(GameProfile::getName).orElse(blockEntityTag.getString("OwnerName"));
      int time = blockEntityTag.getInt("Time");
      if (!ownerNickname.isEmpty()) {
         tooltip.add(new TextComponent("Awarded to: ").append(new TextComponent(ownerNickname).withStyle(ChatFormatting.YELLOW)));
      } else {
         tooltip.add(new TextComponent("Awarded to: ").append(new TextComponent("???").withStyle(ChatFormatting.GRAY)));
      }

      tooltip.add(new TextComponent("Time: ").append(new TextComponent(UIHelper.formatTimeString(time)).withStyle(ChatFormatting.GRAY)));
   }

   public static ItemStack create(ServerPlayer owner, HeraldTrophyBlock.Variant variant, int time) {
      return create(owner == null ? null : owner.getUUID(), owner == null ? null : owner.getName().getString(), variant, time);
   }

   public static ItemStack create(UUID ownerUUID, String ownerNickname, HeraldTrophyBlock.Variant variant, int time) {
      ItemStack itemStack = new ItemStack(ModBlocks.HERALD_TROPHY_BLOCK_ITEM);
      CompoundTag nbt = itemStack.getOrCreateTag();
      CompoundTag blockEntityTag = itemStack.getOrCreateTagElement("BlockEntityTag");
      if (ownerUUID != null) {
         blockEntityTag.putString("OwnerUUID", ownerUUID.toString());
      }

      if (ownerNickname != null) {
         blockEntityTag.putString("OwnerName", ownerNickname);
      }

      blockEntityTag.putString("Variant", variant.getSerializedName());
      blockEntityTag.putInt("Time", time);
      nbt.putInt("CustomModelData", variant.ordinal());
      return itemStack;
   }
}
