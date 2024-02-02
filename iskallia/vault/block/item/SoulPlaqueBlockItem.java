package iskallia.vault.block.item;

import com.mojang.authlib.GameProfile;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.util.McClientHelper;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
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

public class SoulPlaqueBlockItem extends BlockItem {
   public SoulPlaqueBlockItem(Block block) {
      super(block, new Properties().tab(ModItems.VAULT_MOD_GROUP).stacksTo(1));
   }

   public void inventoryTick(@Nonnull ItemStack itemStack, @Nonnull Level world, @Nonnull Entity entity, int itemSlot, boolean isSelected) {
      if (!world.isClientSide) {
         if (entity instanceof ServerPlayer player) {
            if (getOwnerUUID(itemStack).isEmpty()) {
               setOwnerUUID(itemStack, player.getUUID());
               setOwnerName(itemStack, player.getName().getString());
            }

            int tier = ModConfigs.ASCENSION.getTier(getScore(itemStack));
            if (getTier(itemStack).orElse(0) != tier) {
               setTier(itemStack, tier);
            }

            super.inventoryTick(itemStack, world, entity, itemSlot, isSelected);
         }
      }
   }

   public static Optional<UUID> getOwnerUUID(ItemStack stack) {
      CompoundTag blockEntityTag = stack.getOrCreateTagElement("BlockEntityTag");
      String uuidString = blockEntityTag.getString("OwnerUuid");
      return uuidString.isEmpty() ? Optional.empty() : Optional.of(UUID.fromString(uuidString));
   }

   public static Optional<String> getOwnerName(ItemStack stack) {
      CompoundTag blockEntityTag = stack.getOrCreateTagElement("BlockEntityTag");
      String name = blockEntityTag.getString("OwnerName");
      return name.isEmpty() ? Optional.empty() : Optional.of(name);
   }

   public static OptionalInt getTier(ItemStack stack) {
      int tier = stack.getOrCreateTag().getInt("CustomModelData");
      return tier >= 1 && tier <= 8 ? OptionalInt.of(tier) : OptionalInt.empty();
   }

   public static int getScore(ItemStack stack) {
      return stack.getOrCreateTagElement("BlockEntityTag").getInt("Score");
   }

   public static void setOwnerUUID(ItemStack stack, UUID uuid) {
      stack.getOrCreateTagElement("BlockEntityTag").putString("OwnerUuid", uuid.toString());
   }

   public static void setOwnerName(ItemStack stack, String name) {
      stack.getOrCreateTagElement("BlockEntityTag").putString("OwnerName", name);
   }

   public static void setTier(ItemStack stack, int tier) {
      stack.getOrCreateTag().putInt("CustomModelData", tier);
   }

   public static void setScore(ItemStack stack, int score) {
      stack.getOrCreateTagElement("BlockEntityTag").putInt("Score", score);
   }

   @OnlyIn(Dist.CLIENT)
   public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level worldIn, @Nonnull List<Component> tooltip, @Nonnull TooltipFlag flag) {
      super.appendHoverText(stack, worldIn, tooltip, flag);
      String ownerNickname = McClientHelper.getOnlineProfile(getOwnerUUID(stack).orElse(null))
         .<String>map(GameProfile::getName)
         .orElseGet(() -> getOwnerName(stack).orElse(""));
      int score = getScore(stack);
      if (!ownerNickname.isEmpty()) {
         tooltip.add(new TextComponent("Awarded to: ").append(new TextComponent(ownerNickname).withStyle(ChatFormatting.YELLOW)));
      } else {
         tooltip.add(new TextComponent("Awarded to: ").append(new TextComponent("???").withStyle(ChatFormatting.GRAY)));
      }

      tooltip.add(new TextComponent("Score: ").append(new TextComponent(score + "").withStyle(ChatFormatting.GRAY)));
   }

   public static ItemStack create(ServerPlayer owner, int score) {
      return create(owner == null ? null : owner.getUUID(), owner == null ? null : owner.getName().getString(), score);
   }

   public static ItemStack create(UUID ownerUUID, String ownerNickname, int score) {
      ItemStack itemStack = new ItemStack(ModBlocks.SOUL_PLAQUE);
      if (ownerUUID != null) {
         setOwnerUUID(itemStack, ownerUUID);
      }

      if (ownerNickname != null) {
         setOwnerName(itemStack, ownerNickname);
      }

      setTier(itemStack, ModConfigs.ASCENSION.getTier(score));
      setScore(itemStack, score);
      return itemStack;
   }
}
