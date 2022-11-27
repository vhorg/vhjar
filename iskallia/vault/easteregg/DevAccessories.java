package iskallia.vault.easteregg;

import iskallia.vault.entity.entity.FighterEntity;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class DevAccessories {
   @SubscribeEvent
   public static void onVaultFighterBossKilled(LivingDeathEvent event) {
      LivingEntity entityLiving = event.getEntityLiving();
      if (entityLiving.isEffectiveAi()) {
         if (entityLiving instanceof FighterEntity fighter) {
            Entity trueSource = event.getSource().getEntity();
            if (fighter.getTags().contains("vault_boss") && trueSource instanceof Player) {
               onDevBossKill(fighter, (ServerPlayer)trueSource);
            }
         }
      }
   }

   public static void onDevBossKill(FighterEntity boss, ServerPlayer player) {
      ServerBossEvent bossInfo = boss.bossInfo;
      if (bossInfo != null) {
         ServerLevel world = (ServerLevel)boss.getCommandSenderWorld();
         if (!(world.getRandom().nextDouble() > 0.4)) {
            String bossName = bossInfo.getName().getString();
            if (!bossName.equalsIgnoreCase("iskall85") && !bossName.equalsIgnoreCase("iGoodie") && bossName.equalsIgnoreCase("Douwsky")) {
            }
         }
      }
   }
}
