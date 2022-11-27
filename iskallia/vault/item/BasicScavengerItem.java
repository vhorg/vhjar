package iskallia.vault.item;

import iskallia.vault.VaultMod;
import iskallia.vault.init.ModItems;
import java.util.Collections;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;

public class BasicScavengerItem extends BasicItem {
   private static final Component SCAVENGER_ITEM_HINT = new TranslatableComponent("tooltip.the_vault.scavenger_item").withStyle(ChatFormatting.GOLD);

   public BasicScavengerItem(String id) {
      super(VaultMod.id("scavenger_" + id), new Properties().tab(ModItems.SCAVENGER_GROUP));
      this.withTooltip(Collections.singletonList(SCAVENGER_ITEM_HINT));
   }

   public static void setVaultIdentifier(ItemStack stack, UUID identifier) {
      if (stack.getItem() instanceof BasicScavengerItem) {
         stack.getOrCreateTag().putUUID("vault_id", identifier);
      }
   }

   @Nullable
   public static UUID getVaultIdentifier(ItemStack stack) {
      if (!(stack.getItem() instanceof BasicScavengerItem)) {
         return null;
      } else {
         CompoundTag tag = stack.getOrCreateTag();
         return !tag.hasUUID("vault_id") ? null : stack.getOrCreateTag().getUUID("vault_id");
      }
   }
}
