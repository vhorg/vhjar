package iskallia.vault.util.calc;

import iskallia.vault.core.event.CommonEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class AbsorptionHelper {
   public static float getMaxAbsorption(Player player) {
      float limit = 12.0F;
      float maxHealthPerc = 0.0F;
      limit += maxHealthPerc * player.getMaxHealth();
      return CommonEvents.PLAYER_STAT.invoke(PlayerStat.ABSORPTION, player, limit).getValue();
   }

   @SubscribeEvent
   public static void onPlayerTick(PlayerTickEvent event) {
      if (event.phase == Phase.START && event.side.isServer() && event.player.tickCount % 10 == 0) {
         Player player = event.player;
         float absorption = player.getAbsorptionAmount();
         if (absorption > 0.0F && absorption > getMaxAbsorption(player)) {
            player.setAbsorptionAmount(getMaxAbsorption(player));
         }
      }
   }
}
