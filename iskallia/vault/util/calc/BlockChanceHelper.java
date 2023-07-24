package iskallia.vault.util.calc;

import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.snapshot.AttributeSnapshot;
import iskallia.vault.snapshot.AttributeSnapshotHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;

public class BlockChanceHelper {
   public static void setPlayerBlocking(Player player) {
      if (player instanceof BlockChanceHelper.PlayerBlockAnimationAccess) {
         ((BlockChanceHelper.PlayerBlockAnimationAccess)player).setForceBlocking();
      }
   }

   public static boolean isPlayerBlocking(Player player) {
      return player instanceof BlockChanceHelper.PlayerBlockAnimationAccess blockAccess ? blockAccess.isForceBlocking() : false;
   }

   public static float getBlockChance(LivingEntity entity) {
      return Mth.clamp(getBlockChanceUnlimited(entity), 0.0F, AttributeLimitHelper.getBlockChanceLimit(entity));
   }

   public static float getBlockChanceUnlimited(LivingEntity entity) {
      float chance = 0.0F;
      if (entity.getItemBySlot(EquipmentSlot.OFFHAND).is(Items.SHIELD)) {
         chance += 0.05F;
      }

      AttributeSnapshot snapshot = AttributeSnapshotHelper.getInstance().getSnapshot(entity);
      chance += snapshot.getAttributeValue(ModGearAttributes.BLOCK, VaultGearAttributeTypeMerger.floatSum());
      return CommonEvents.PLAYER_STAT.invoke(PlayerStat.BLOCK_CHANCE, entity, chance).getValue();
   }

   public interface PlayerBlockAnimationAccess {
      void setForceBlocking();

      boolean isForceBlocking();
   }
}
