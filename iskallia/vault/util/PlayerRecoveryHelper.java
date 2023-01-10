package iskallia.vault.util;

import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.snapshot.AttributeSnapshot;
import iskallia.vault.snapshot.AttributeSnapshotHelper;
import iskallia.vault.util.calc.PlayerStat;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class PlayerRecoveryHelper {
   @SubscribeEvent
   public static void onHeal(LivingHealEvent event) {
      LivingEntity healed = event.getEntityLiving();
      if (!healed.getCommandSenderWorld().isClientSide()) {
         if (healed instanceof ServerPlayer player) {
            float multiplier = 1.0F;
            AttributeSnapshot snapshot = AttributeSnapshotHelper.getInstance().getSnapshot(player);
            multiplier += snapshot.getAttributeValue(ModGearAttributes.HEALING_EFFECTIVENESS, VaultGearAttributeTypeMerger.floatSum());
            multiplier = CommonEvents.PLAYER_STAT.invoke(PlayerStat.HEALING_EFFECTIVENESS, player, multiplier).getValue();
            event.setAmount(event.getAmount() * Math.max(0.0F, multiplier));
         }
      }
   }
}
