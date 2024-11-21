package iskallia.vault.item;

import iskallia.vault.client.util.ClientScheduler;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.crystal.CrystalData;
import iskallia.vault.item.crystal.modifiers.CrystalModifiers;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SoulFlameItem extends BasicItem {
   public SoulFlameItem(ResourceLocation id) {
      super(id);
   }

   public static ItemStack create(int stacks, String playerName, UUID playerUuid, CrystalModifiers modifiers) {
      ItemStack stack = new ItemStack(ModItems.SOUL_FLAME);
      setStacks(stack, stacks);
      setOwnerName(stack, playerName);
      setOwnerUuid(stack, playerUuid);
      setModifiers(stack, modifiers);
      return stack;
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

   public static int getStacks(ItemStack stack) {
      CompoundTag nbt = stack.getTag();
      return nbt != null && nbt.contains("Stacks", 3) ? nbt.getInt("Stacks") : 0;
   }

   public static Optional<UUID> getOwnerUUID(ItemStack stack) {
      CompoundTag nbt = stack.getTag();
      return nbt != null && nbt.contains("OwnerUUID", 8) ? Optional.of(UUID.fromString(nbt.getString("OwnerUUID"))) : Optional.empty();
   }

   public static Optional<String> getOwnerName(ItemStack stack) {
      CompoundTag nbt = stack.getTag();
      return nbt != null && nbt.contains("OwnerName", 8) ? Optional.of(nbt.getString("OwnerName")) : Optional.empty();
   }

   public static Optional<CrystalModifiers> getModifiers(ItemStack stack) {
      CompoundTag nbt = stack.getTag();
      return nbt != null && nbt.contains("Modifiers", 10) ? CrystalData.MODIFIERS.readNbt(nbt.getCompound("Modifiers")) : Optional.empty();
   }

   public static void setStacks(ItemStack stack, int stackCount) {
      CompoundTag nbt = stack.getOrCreateTag();
      nbt.putInt("Stacks", stackCount);
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

   private static void setModifiers(ItemStack stack, CrystalModifiers modifiers) {
      CompoundTag nbt = stack.getOrCreateTag();
      CrystalData.MODIFIERS.writeNbt(modifiers).ifPresent(tag -> nbt.put("Modifiers", tag));
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level worldIn, @Nonnull List<Component> tooltip, @Nonnull TooltipFlag flag) {
      super.appendHoverText(stack, worldIn, tooltip, flag);
      getOwnerUUID(stack).ifPresentOrElse(uuid -> {
         String name = getOwnerName(stack).orElse("Unknown");
         tooltip.add(new TextComponent("").append(new TextComponent("Player: ")).append(new TextComponent(name).withStyle(ChatFormatting.YELLOW)));
      }, () -> tooltip.add(new TextComponent("").append(new TextComponent("Player: ")).append(new TextComponent("???").withStyle(ChatFormatting.GRAY))));
      tooltip.add(new TextComponent("").append(new TextComponent("Stacks: ")).append(new TextComponent(getStacks(stack) + "").withStyle(ChatFormatting.AQUA)));
      getModifiers(stack).ifPresent(modifiers -> modifiers.addText(tooltip, tooltip.size(), flag, (float)ClientScheduler.INSTANCE.getTick()));
   }
}
