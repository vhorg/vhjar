package iskallia.vault.etching.set;

import com.google.gson.annotations.Expose;
import iskallia.vault.etching.EtchingSet;
import iskallia.vault.util.damage.PlayerDamageHelper;
import java.util.UUID;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class BloodSet extends EtchingSet<BloodSet.Config> {
   private static final UUID DAMAGE_MULTIPLIER_ID = UUID.fromString("58034bae-0103-479b-b39a-58b083243d65");

   public BloodSet(ResourceLocation name) {
      super(name);
   }

   @Override
   public Class<BloodSet.Config> getConfigClass() {
      return BloodSet.Config.class;
   }

   public BloodSet.Config getDefaultConfig() {
      return new BloodSet.Config(2.0F);
   }

   @Override
   public void tick(ServerPlayer player) {
      super.tick(player);
      if (PlayerDamageHelper.getMultiplier(player, DAMAGE_MULTIPLIER_ID) == null) {
         float dmgMultiplier = this.getConfig().getIncreasedDamage();
         PlayerDamageHelper.applyMultiplier(DAMAGE_MULTIPLIER_ID, player, dmgMultiplier, PlayerDamageHelper.Operation.ADDITIVE_MULTIPLY);
      }
   }

   @Override
   public void remove(ServerPlayer player) {
      super.remove(player);
      PlayerDamageHelper.removeMultiplier(player, DAMAGE_MULTIPLIER_ID);
   }

   public static class Config {
      @Expose
      private float increasedDamage;

      public Config(float increasedDamage) {
         this.increasedDamage = increasedDamage;
      }

      public float getIncreasedDamage() {
         return this.increasedDamage;
      }
   }
}
