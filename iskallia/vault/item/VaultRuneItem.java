package iskallia.vault.item;

import iskallia.vault.core.random.JavaRandom;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.world.generator.layout.DIYRoomEntry;
import iskallia.vault.core.world.loot.LootRoll;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.gear.VaultLevelItem;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class VaultRuneItem extends Item implements VaultLevelItem {
   public VaultRuneItem(CreativeModeTab group, ResourceLocation id) {
      super(new Properties().tab(group).stacksTo(8));
      this.setRegistryName(id);
   }

   public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
      if (this.allowdedIn(group)) {
         this.addStack(items, DIYRoomEntry.ofType(DIYRoomEntry.Type.COMMON, 1));
         this.addStack(items, DIYRoomEntry.ofType(DIYRoomEntry.Type.CHALLENGE, 1));
         this.addStack(items, DIYRoomEntry.ofType(DIYRoomEntry.Type.OMEGA, 1));
      }
   }

   public void addStack(NonNullList<ItemStack> items, DIYRoomEntry... entries) {
      ItemStack stack = new ItemStack(ModItems.RUNE);
      ListTag list = new ListTag();

      for (DIYRoomEntry entry : entries) {
         list.add(entry.serializeNBT());
      }

      stack.getOrCreateTag().put("entries", list);
      items.add(stack);
   }

   public static List<DIYRoomEntry> getEntries(ItemStack stack) {
      List<DIYRoomEntry> entries = new ArrayList<>();
      if (stack.getTag() != null) {
         ListTag list = stack.getTag().getList("entries", 10);

         for (int i = 0; i < list.size(); i++) {
            entries.add(DIYRoomEntry.fromNBT(list.getCompound(i)));
         }
      }

      return entries;
   }

   @OnlyIn(Dist.CLIENT)
   public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
      for (DIYRoomEntry entry : getEntries(stack)) {
         int count = entry.get(DIYRoomEntry.COUNT);
         String roomStr = count > 1 ? "Rooms" : "Room";
         Component txt = new TextComponent("- Has ")
            .withStyle(ChatFormatting.GRAY)
            .append(new TextComponent(String.valueOf(count)).withStyle(ChatFormatting.GOLD))
            .append(" ")
            .append(entry.getName())
            .append(new TextComponent(" " + roomStr).withStyle(ChatFormatting.GRAY));
         tooltip.add(txt);
      }
   }

   @Override
   public void initializeVaultLoot(Vault vault, ItemStack stack, @Nullable BlockPos pos) {
      if (stack.getTag() != null) {
         ListTag list = stack.getTag().getList("entries", 10);

         for (int i = 0; i < list.size(); i++) {
            CompoundTag entry = list.getCompound(i);
            if (entry.contains("count", 10)) {
               LootRoll roll = LootRoll.fromNBT(entry.getCompound("count"));
               JavaRandom random = JavaRandom.ofNanoTime();
               entry.putInt("count", roll.get(random));
            }
         }
      }
   }
}
