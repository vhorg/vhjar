package iskallia.vault.item;

import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.crystal.CrystalData;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemVaultCrystalSeal extends Item {
   public ItemVaultCrystalSeal(ResourceLocation id) {
      super(new Properties().tab(ModItems.VAULT_MOD_GROUP).stacksTo(1));
      this.setRegistryName(id);
   }

   public boolean configure(CrystalData crystal) {
      return ModConfigs.VAULT_CRYSTAL.applySeal(this, ModItems.VAULT_CRYSTAL, crystal);
   }

   @Nullable
   public static String getEventKey(ItemStack stack) {
      if (!stack.isEmpty() && stack.getItem() instanceof ItemVaultCrystalSeal) {
         CompoundTag tag = stack.getOrCreateTag();
         return !tag.contains("eventKey", 8) ? null : tag.getString("eventKey");
      } else {
         return null;
      }
   }

   public static void setEventKey(ItemStack stack, String eventKey) {
      if (!stack.isEmpty() && stack.getItem() instanceof ItemVaultCrystalSeal) {
         stack.getOrCreateTag().putString("eventKey", eventKey);
      }
   }

   public void inventoryTick(ItemStack stack, Level world, Entity entity, int itemSlot, boolean isSelected) {
      if (!world.isClientSide()) {
         String eventKey = getEventKey(stack);
         if (eventKey != null) {
            boolean hasEvent = ModConfigs.ARCHITECT_EVENT.isEnabled() || ModConfigs.RAID_EVENT_CONFIG.isEnabled();
            if (!hasEvent) {
               stack.setCount(0);
            }
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
      if (ModConfigs.isInitialized()) {
         CrystalData crystal = new CrystalData();
         if (this.configure(crystal)) {
            tooltip.add(new TextComponent("Sets a vault crystal's objective").withStyle(ChatFormatting.GRAY));
            tooltip.add(new TextComponent("to: ").withStyle(ChatFormatting.GRAY).append(crystal.getObjective().getName()));
         }

         String eventKey = getEventKey(stack);
         if (eventKey != null) {
            tooltip.add(new TextComponent("Event Item").withStyle(ChatFormatting.AQUA));
            tooltip.add(new TextComponent("Expires after the event finishes.").withStyle(ChatFormatting.GRAY));
         }
      }
   }
}
