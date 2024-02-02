package iskallia.vault.item;

import iskallia.vault.config.PlayerTitlesConfig;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.world.data.PlayerTitlesData;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

public class TitleScrollItem extends BasicItem {
   public TitleScrollItem(ResourceLocation id) {
      super(id);
   }

   public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> items) {
      if (ModConfigs.isInitialized()) {
         if (tab == CreativeModeTab.TAB_SEARCH) {
            for (PlayerTitlesConfig.Affix affix : PlayerTitlesConfig.Affix.values()) {
               Map<String, PlayerTitlesConfig.Title> titles = ModConfigs.PLAYER_TITLES.getAll(affix);

               for (String titleId : titles.keySet()) {
                  ItemStack stack = new ItemStack(this);
                  setTitle(stack, titleId, affix);
                  items.add(stack);
               }
            }
         }
      }
   }

   public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
      ItemStack stack = player.getItemInHand(hand);
      if (!world.isClientSide && player.getUUID().equals(getOwnerUUID(stack).orElse(null))) {
         String titleId = getTitleId(stack).orElse(null);
         PlayerTitlesConfig.Affix affix = getAffix(stack).orElse(null);
         if (titleId != null && affix != null) {
            PlayerTitlesData.setCustomName(player, titleId, affix);
            world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.NOTE_BLOCK_BELL, SoundSource.PLAYERS, 0.9F, 1.2F);
         }

         return InteractionResultHolder.success(stack);
      } else {
         return super.use(world, player, hand);
      }
   }

   public void inventoryTick(@Nonnull ItemStack itemStack, @Nonnull Level world, @Nonnull Entity entity, int itemSlot, boolean isSelected) {
      if (!world.isClientSide) {
         if (entity instanceof ServerPlayer player) {
            if (getOwnerUUID(itemStack).isEmpty()) {
               setOwner(itemStack, player);
            }

            super.inventoryTick(itemStack, world, entity, itemSlot, isSelected);
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level worldIn, @Nonnull List<Component> tooltip, @Nonnull TooltipFlag flag) {
      super.appendHoverText(stack, worldIn, tooltip, flag);
      getOwnerUUID(stack).ifPresentOrElse(uuid -> {
         String name = getOwnerName(stack).orElse("Unknown");
         tooltip.add(new TextComponent("").append(new TextComponent("Player: ")).append(new TextComponent(name).withStyle(ChatFormatting.YELLOW)));
      }, () -> tooltip.add(new TextComponent("").append(new TextComponent("Player: ")).append(new TextComponent("???").withStyle(ChatFormatting.GRAY))));
      PlayerTitlesData.Entry entry = new PlayerTitlesData.Entry();
      PlayerTitlesConfig.Affix affix = getAffix(stack).orElse(PlayerTitlesConfig.Affix.PREFIX);
      if (affix == PlayerTitlesConfig.Affix.PREFIX) {
         entry.setPrefix(getTitleId(stack).orElse(null));
      } else if (affix == PlayerTitlesConfig.Affix.SUFFIX) {
         entry.setSuffix(getTitleId(stack).orElse(null));
      }

      entry.getCustomName(new TextComponent("<Player>"), PlayerTitlesData.Type.TAB_LIST)
         .ifPresent(display -> tooltip.add(new TextComponent("").append(new TextComponent("Preview: ")).append(display)));
   }

   public static Optional<CompoundTag> get(ItemStack stack) {
      return Optional.ofNullable(stack.getTag());
   }

   public static Optional<UUID> getOwnerUUID(ItemStack stack) {
      CompoundTag nbt = stack.getTag();
      return nbt != null && nbt.contains("OwnerUUID", 8) ? Optional.of(UUID.fromString(nbt.getString("OwnerUUID"))) : Optional.empty();
   }

   public static Optional<String> getOwnerName(ItemStack stack) {
      CompoundTag nbt = stack.getTag();
      return nbt != null && nbt.contains("OwnerName", 8) ? Optional.of(nbt.getString("OwnerName")) : Optional.empty();
   }

   public static Optional<String> getTitleId(ItemStack stack) {
      CompoundTag nbt = stack.getTag();
      return nbt != null && nbt.contains("TitleId", 8) ? Optional.of(nbt.getString("TitleId")) : Optional.empty();
   }

   public static Optional<PlayerTitlesConfig.Affix> getAffix(ItemStack stack) {
      return get(stack).map(tag -> Enum.valueOf(PlayerTitlesConfig.Affix.class, tag.getString("Affix")));
   }

   public static void setOwnerUuid(ItemStack stack, UUID uuid) {
      CompoundTag nbt = stack.getOrCreateTag();
      nbt.putString("OwnerUUID", uuid.toString());
   }

   public static void setOwnerName(ItemStack stack, String name) {
      CompoundTag nbt = stack.getOrCreateTag();
      nbt.putString("OwnerName", name);
   }

   public static void setOwner(ItemStack stack, ServerPlayer player) {
      setOwnerUuid(stack, player.getUUID());
      setOwnerName(stack, player.getGameProfile().getName());
   }

   public static void setTitleId(ItemStack stack, String titleId) {
      CompoundTag tag = stack.getOrCreateTag();
      tag.putString("TitleId", titleId);
   }

   public static void setAffix(ItemStack stack, PlayerTitlesConfig.Affix affix) {
      CompoundTag tag = stack.getOrCreateTag();
      tag.putString("Affix", affix.name());
   }

   public static void setTitle(ItemStack stack, String titleId, PlayerTitlesConfig.Affix affix) {
      setTitleId(stack, titleId);
      setAffix(stack, affix);
   }
}
