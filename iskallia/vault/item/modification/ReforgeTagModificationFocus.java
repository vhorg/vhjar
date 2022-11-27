package iskallia.vault.item.modification;

import iskallia.vault.config.gear.VaultGearTagConfig;
import iskallia.vault.gear.modification.GearModification;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.item.gear.DataTransferItem;
import javax.annotation.Nullable;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class ReforgeTagModificationFocus extends GearModificationItem implements DataTransferItem {
   public ReforgeTagModificationFocus(ResourceLocation id, GearModification modification) {
      super(id, modification);
   }

   public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> items) {
      if (ModConfigs.isInitialized()) {
         if (this.allowdedIn(tab)) {
            for (String tag : ModConfigs.VAULT_GEAR_TAG_CONFIG.getTags()) {
               ItemStack focus = new ItemStack(this);
               setModifierTag(focus, tag);
               items.add(focus);
            }
         }
      }
   }

   @Override
   public ItemStack convertStack(ItemStack stack) {
      ItemStack result = DataTransferItem.super.convertStack(stack);
      if (getModifierTag(result) == null) {
         String randomTag = ModConfigs.VAULT_GEAR_TAG_CONFIG.getRandomTag();
         if (randomTag != null) {
            setModifierTag(result, randomTag);
         }
      }

      return result;
   }

   @Nullable
   public static VaultGearTagConfig.ModGroupTag getModifierTag(ItemStack stack) {
      if (!stack.isEmpty() && stack.getItem() instanceof ReforgeTagModificationFocus) {
         String tagStr = stack.getOrCreateTag().getString("modTag");
         return ModConfigs.VAULT_GEAR_TAG_CONFIG.getGroupTag(tagStr);
      } else {
         return null;
      }
   }

   public static void setModifierTag(ItemStack stack, String tag) {
      if (!stack.isEmpty() && stack.getItem() instanceof ReforgeTagModificationFocus) {
         stack.getOrCreateTag().putString("modTag", tag);
      }
   }
}
