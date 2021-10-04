package iskallia.vault.event;

import iskallia.vault.init.ModModels;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent.Item;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   bus = Bus.MOD,
   value = {Dist.CLIENT}
)
public class ClientInitEvents {
   @SubscribeEvent
   public static void onColorHandlerRegister(Item event) {
      ModModels.registerItemColors(event.getItemColors());
   }
}
