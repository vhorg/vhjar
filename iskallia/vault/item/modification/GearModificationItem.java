package iskallia.vault.item.modification;

import iskallia.vault.gear.modification.GearModification;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.item.BasicItem;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;

public class GearModificationItem extends BasicItem {
   private final GearModification modification;

   public GearModificationItem(ResourceLocation id, GearModification modification) {
      super(id);
      this.modification = modification;
   }

   public GearModificationItem(ResourceLocation id, GearModification modification, Properties properties) {
      super(id, properties);
      this.modification = modification;
   }

   @Override
   public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
      if (ModConfigs.isInitialized()) {
         tooltip.add(new TranslatableComponent("the_vault.gear_modification.information").withStyle(ChatFormatting.GRAY));
         this.modification.getDescription(stack).forEach(cmp -> tooltip.add(cmp.copy().withStyle(ChatFormatting.GRAY)));
      }
   }
}
