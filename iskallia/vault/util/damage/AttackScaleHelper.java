package iskallia.vault.util.damage;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class AttackScaleHelper {
   private static final Map<UUID, Float> lastAttackScale = new HashMap<>();

   public static float getLastAttackScale(Player player) {
      return lastAttackScale.getOrDefault(player.getUUID(), 0.0F);
   }

   @SubscribeEvent
   public static void onAttack(AttackEntityEvent event) {
      Player player = event.getPlayer();
      if (player instanceof ServerPlayer) {
         lastAttackScale.put(player.getUUID(), player.getAttackStrengthScale(0.0F));
      }
   }
}
