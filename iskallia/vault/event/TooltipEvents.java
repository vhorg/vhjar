package iskallia.vault.event;

import iskallia.vault.init.ModConfigs;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   bus = Bus.FORGE,
   value = {Dist.CLIENT}
)
public class TooltipEvents {
   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public static void addBlacklistOnTooltip(ItemTooltipEvent event) {
      ItemStack itemStack = event.getItemStack();
      List<Component> toolTip = event.getToolTip();
      Item item = itemStack.getItem();
      boolean isBlacklisted = item instanceof BlockItem blockItem
         ? ModConfigs.VAULT_GENERAL.isBlacklisted(blockItem.getBlock())
         : ModConfigs.VAULT_GENERAL.isBlacklisted(item);
      if (isBlacklisted) {
         toolTip.add(new TextComponent(""));
         toolTip.add(new TextComponent("☹ Disabled in the Vaults").withStyle(ChatFormatting.DARK_RED));
      }
   }
}
