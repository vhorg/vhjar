package iskallia.vault.item;

import iskallia.vault.Vault;
import iskallia.vault.init.ModItems;
import iskallia.vault.util.MiscUtils;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.Properties;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public class BasicScavengerItem extends BasicTooltipItem {
   private static final ITextComponent SCAVENGER_ITEM_HINT = new TranslationTextComponent("tooltip.the_vault.scavenger_item")
      .func_240699_a_(TextFormatting.GOLD);

   public BasicScavengerItem(String id) {
      super(Vault.id("scavenger_" + id), new Properties().func_200916_a(ModItems.SCAVENGER_GROUP), Collections.singletonList(SCAVENGER_ITEM_HINT));
   }

   public BasicScavengerItem(ResourceLocation id, Properties properties, List<ITextComponent> components) {
      super(id, properties, MiscUtils.concat(components, SCAVENGER_ITEM_HINT));
   }

   public static void setVaultIdentifier(ItemStack stack, UUID identifier) {
      if (stack.func_77973_b() instanceof BasicScavengerItem) {
         stack.func_196082_o().func_186854_a("vault_id", identifier);
      }
   }

   @Nullable
   public static UUID getVaultIdentifier(ItemStack stack) {
      if (!(stack.func_77973_b() instanceof BasicScavengerItem)) {
         return null;
      } else {
         CompoundNBT tag = stack.func_196082_o();
         return !tag.func_186855_b("vault_id") ? null : stack.func_196082_o().func_186857_a("vault_id");
      }
   }
}
