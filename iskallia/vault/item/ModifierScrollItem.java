package iskallia.vault.item;

import iskallia.vault.block.entity.ModifierDiscoveryTileEntity;
import iskallia.vault.container.modifier.DiscoverableModifier;
import iskallia.vault.container.provider.ModifierScrollProvider;
import iskallia.vault.init.ModItems;
import java.util.AbstractCollection;
import java.util.AbstractList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

public class ModifierScrollItem extends Item {
   public ModifierScrollItem(ResourceLocation id) {
      super(new Properties().stacksTo(1).tab(ModItems.VAULT_MOD_GROUP));
      this.setRegistryName(id);
   }

   public void fillItemCategory(CreativeModeTab category, NonNullList<ItemStack> items) {
      if (this.allowdedIn(category)) {
         items.add(new ItemStack(this));
      }
   }

   public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag advanced) {
      String openName = getPlayerName(stack);
      if (openName != null) {
         tooltip.add(new TextComponent("Opened by " + openName).withStyle(ChatFormatting.GRAY));
      }
   }

   public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
      ItemStack inHand = player.getItemInHand(hand);
      if (player instanceof ServerPlayer sPlayer) {
         int slot = hand == InteractionHand.MAIN_HAND ? sPlayer.getInventory().selected : 40;
         this.openScroll(sPlayer, slot, inHand);
         return InteractionResultHolder.success(inHand);
      } else {
         return InteractionResultHolder.pass(inHand);
      }
   }

   private void openScroll(ServerPlayer sPlayer, int slot, ItemStack inHand) {
      UUID scrollUuid = getUuid(inHand);
      if (scrollUuid == null) {
         this.generateScrollContents(sPlayer, inHand);
         scrollUuid = getUuid(inHand);
      }

      UUID playerUid = getPlayerUuid(inHand);
      if (playerUid != null && !sPlayer.getUUID().equals(playerUid)) {
         sPlayer.sendMessage(new TextComponent("Scroll was already used by " + getPlayerName(inHand)).withStyle(ChatFormatting.RED), Util.NIL_UUID);
      } else {
         ModifierScrollProvider provider = new ModifierScrollProvider(sPlayer, slot, scrollUuid, inHand);
         NetworkHooks.openGui(sPlayer, provider, provider.extraDataWriter());
      }
   }

   private void generateScrollContents(ServerPlayer sPlayer, ItemStack stack) {
      setOpenInformation(stack, sPlayer);
      setDiscoverableModifiers(stack, ModifierDiscoveryTileEntity.generateRandomDiscoverableModifiers(sPlayer, 3));
   }

   public static List<DiscoverableModifier> getDiscoverableModifiers(ItemStack stack) {
      return stack.getOrCreateTag()
         .getList("DiscoverableModifiers", 10)
         .stream()
         .map(tag -> (CompoundTag)tag)
         .map(DiscoverableModifier::deserialize)
         .filter(Optional::isPresent)
         .map(Optional::get)
         .collect(Collectors.toList());
   }

   public static void setDiscoverableModifiers(ItemStack stack, List<DiscoverableModifier> discoverableModifiers) {
      stack.getOrCreateTag().put("DiscoverableModifiers", discoverableModifiers.stream().map(modifier -> {
         CompoundTag tag = new CompoundTag();
         modifier.serialize(tag);
         return tag;
      }).collect(ListTag::new, AbstractList::add, AbstractCollection::addAll));
   }

   public static void setOpenInformation(ItemStack stack, Player player) {
      CompoundTag tag = stack.getOrCreateTag();
      tag.putUUID("Uuid", UUID.randomUUID());
      tag.putUUID("PlayerUuid", player.getUUID());
      tag.putString("PlayerName", player.getName().getString());
   }

   @Nullable
   public static UUID getUuid(ItemStack stack) {
      return stack.hasTag() ? stack.getOrCreateTag().getUUID("Uuid") : null;
   }

   @Nullable
   public static UUID getPlayerUuid(ItemStack stack) {
      return stack.hasTag() ? stack.getOrCreateTag().getUUID("PlayerUuid") : null;
   }

   @Nullable
   public static String getPlayerName(ItemStack stack) {
      return stack.hasTag() ? stack.getOrCreateTag().getString("PlayerName") : null;
   }
}
