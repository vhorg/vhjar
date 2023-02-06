package iskallia.vault.skill.talent.type;

import com.google.gson.annotations.Expose;
import iskallia.vault.util.damage.PlayerDamageHelper;
import java.util.UUID;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

public class LowManaDamageTalent extends LowManaTalent {
   private static final UUID DAMAGE_MULTIPLIER_ID = UUID.fromString("ed8528f4-cd3c-4aff-90b2-5471f742d4bd");
   @Expose
   private float damageIncrease;

   public LowManaDamageTalent(int cost, float healthThreshold, float damageIncrease) {
      super(cost, healthThreshold);
      this.damageIncrease = damageIncrease;
   }

   public float getDamageIncrease() {
      return this.damageIncrease;
   }

   @Override
   public void tick(ServerPlayer sPlayer) {
      super.tick(sPlayer);
      if (!this.shouldGetBenefits(sPlayer)) {
         this.removeExistingDamageBuff(sPlayer);
      } else {
         PlayerDamageHelper.DamageMultiplier existing = PlayerDamageHelper.getMultiplier(sPlayer, DAMAGE_MULTIPLIER_ID);
         if (existing != null && !Mth.equal(existing.getMultiplier(), this.getDamageIncrease())) {
            PlayerDamageHelper.removeMultiplier(sPlayer, existing);
            existing = null;
         }

         if (existing == null) {
            PlayerDamageHelper.applyTimedMultiplier(
               DAMAGE_MULTIPLIER_ID, sPlayer, this.getDamageIncrease(), PlayerDamageHelper.Operation.ADDITIVE_MULTIPLY, true, 20
            );
         } else {
            existing.refreshDuration(sPlayer.getServer());
         }
      }
   }

   @Override
   public void onRemoved(Player player) {
      super.onRemoved(player);
      if (player instanceof ServerPlayer sPlayer) {
         this.removeExistingDamageBuff(sPlayer);
      }
   }

   private void removeExistingDamageBuff(ServerPlayer player) {
      PlayerDamageHelper.DamageMultiplier existing = PlayerDamageHelper.getMultiplier(player, DAMAGE_MULTIPLIER_ID);
      if (existing != null) {
         PlayerDamageHelper.removeMultiplier(player, existing);
      }
   }
}
