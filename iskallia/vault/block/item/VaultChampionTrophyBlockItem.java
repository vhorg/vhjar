package iskallia.vault.block.item;

import com.mojang.authlib.GameProfile;
import iskallia.vault.block.VaultChampionTrophy;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModItems;
import iskallia.vault.util.McClientHelper;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class VaultChampionTrophyBlockItem extends BlockItem {
   public static final String NBT_OWNER_UUID = "OwnerUUID";
   public static final String NBT_OWNER_NICK = "OwnerNickname";
   public static final String NBT_VARIANT = "Variant";

   public VaultChampionTrophyBlockItem(Block block) {
      super(block, new Properties().tab(ModItems.VAULT_MOD_GROUP).stacksTo(1));
   }

   public void fillItemCategory(@Nonnull CreativeModeTab group, @Nonnull NonNullList<ItemStack> items) {
      if (this.allowdedIn(group)) {
         items.add(create(null, VaultChampionTrophy.Variant.GOLDEN));
         items.add(create(null, VaultChampionTrophy.Variant.PLATINUM));
         items.add(create(null, VaultChampionTrophy.Variant.BLUE_SILVER));
         items.add(create(null, VaultChampionTrophy.Variant.SILVER));
      }
   }

   public void inventoryTick(@Nonnull ItemStack itemStack, @Nonnull Level world, @Nonnull Entity entity, int itemSlot, boolean isSelected) {
      if (!world.isClientSide) {
         if (entity instanceof ServerPlayer player) {
            CompoundTag blockEntityTag = itemStack.getOrCreateTagElement("BlockEntityTag");
            if (!blockEntityTag.contains("OwnerUUID")) {
               blockEntityTag.putString("OwnerUUID", player.getUUID().toString());
               blockEntityTag.putString("OwnerNickname", player.getName().getString());
               super.inventoryTick(itemStack, world, entity, itemSlot, isSelected);
            }
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level worldIn, @Nonnull List<Component> tooltip, @Nonnull TooltipFlag flag) {
      super.appendHoverText(stack, worldIn, tooltip, flag);
      CompoundTag blockEntityTag = stack.getOrCreateTagElement("BlockEntityTag");
      String uuidString = blockEntityTag.getString("OwnerUUID");
      UUID ownerUUID = uuidString.isEmpty() ? null : UUID.fromString(uuidString);
      String ownerNickname = McClientHelper.getOnlineProfile(ownerUUID).<String>map(GameProfile::getName).orElse(blockEntityTag.getString("OwnerNickname"));
      MutableComponent titleText = new TextComponent("Vault Champion").withStyle(ChatFormatting.GOLD);
      MutableComponent championText = new TextComponent("Mighty " + ownerNickname).withStyle(ChatFormatting.GOLD).withStyle(ChatFormatting.BOLD);
      tooltip.add(new TextComponent(""));
      tooltip.add(titleText);
      tooltip.add(championText);
   }

   public static ItemStack create(ServerPlayer owner, VaultChampionTrophy.Variant variant) {
      return create(owner == null ? null : owner.getUUID(), owner == null ? null : owner.getName().getString(), variant);
   }

   public static ItemStack create(UUID ownerUUID, String ownerNickname, VaultChampionTrophy.Variant variant) {
      ItemStack itemStack = new ItemStack(ModBlocks.VAULT_CHAMPION_TROPHY_BLOCK_ITEM);
      CompoundTag nbt = itemStack.getOrCreateTag();
      CompoundTag blockEntityTag = itemStack.getOrCreateTagElement("BlockEntityTag");
      if (ownerUUID != null) {
         blockEntityTag.putString("OwnerUUID", ownerUUID.toString());
      }

      if (ownerNickname != null) {
         blockEntityTag.putString("OwnerNickname", ownerNickname);
      }

      blockEntityTag.putString("Variant", variant.getSerializedName());
      nbt.putInt("CustomModelData", variant.ordinal());
      return itemStack;
   }
}
