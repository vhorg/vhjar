package iskallia.vault.init;

import iskallia.vault.client.gui.tooltip.MagnetTooltipComponent;
import iskallia.vault.client.gui.tooltip.PaxelTooltipComponent;
import iskallia.vault.item.MagnetItem;
import iskallia.vault.item.paxel.PaxelItem;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ModTooltips {
   public static void register(FMLClientSetupEvent event) {
      MinecraftForgeClient.registerTooltipComponentFactory(PaxelItem.PaxelTooltip.class, PaxelTooltipComponent::new);
      MinecraftForgeClient.registerTooltipComponentFactory(MagnetItem.MagnetTooltip.class, MagnetTooltipComponent::new);
   }
}
