package iskallia.vault.etching.set;

import com.google.gson.annotations.Expose;
import iskallia.vault.etching.EtchingSet;
import iskallia.vault.util.damage.PlayerDamageHelper;
import java.util.UUID;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class DragonSet extends EtchingSet<DragonSet.Config> {
   private static final UUID DAMAGE_MULTIPLIER_ID = UUID.fromString("b7c6e0c4-1568-441c-aa75-e1525fe3b8b6");

   public DragonSet(ResourceLocation name) {
      super(name);
   }

   @Override
   public Class<DragonSet.Config> getConfigClass() {
      return DragonSet.Config.class;
   }

   public DragonSet.Config getDefaultConfig() {
      return new DragonSet.Config(0.5F);
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
