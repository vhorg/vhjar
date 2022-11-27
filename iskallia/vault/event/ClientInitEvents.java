package iskallia.vault.event;

import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModModels;
import java.awt.Color;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent.Block;
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

   @SubscribeEvent
   public static void onColorHandlerRegisterBlock(Block event) {
      event.getBlockColors().register((pState, pLevel, pPos, pTintIndex) -> {
         float transition = (float)(System.currentTimeMillis() % 10000L) / 10000.0F;
         return Color.getHSBColor(transition, 1.0F, 1.0F).getRGB();
      }, new net.minecraft.world.level.block.Block[]{ModBlocks.VAULT_PORTAL});
   }
}
