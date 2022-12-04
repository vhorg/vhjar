package iskallia.vault.util.scheduler;

import iskallia.vault.client.gui.screen.summary.VaultEndScreen;
import iskallia.vault.core.vault.stat.VaultSnapshot;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber({Dist.CLIENT})
public class EndScreenScheduler {
   public VaultSnapshot snapshot = null;
   public static final EndScreenScheduler INSTANCE = new EndScreenScheduler();

   public static EndScreenScheduler getInstance() {
      return INSTANCE;
   }

   @SubscribeEvent
   public static void onClientTick(ClientTickEvent event) {
      if (getInstance().snapshot != null) {
         if (event.phase != Phase.START) {
            if (Minecraft.getInstance().player != null && !Minecraft.getInstance().player.isDeadOrDying() && Minecraft.getInstance().screen == null) {
               Minecraft.getInstance().setScreen(new VaultEndScreen(getInstance().snapshot, new TextComponent("Vault Exit")));
               getInstance().snapshot = null;
            }
         }
      }
   }
}
