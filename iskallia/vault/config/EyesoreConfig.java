package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.entity.entity.eyesore.EyesoreEntity;
import iskallia.vault.entity.entity.eyesore.EyesoreFireballEntity;
import net.minecraft.server.level.ServerLevel;

public class EyesoreConfig extends Config {
   @Expose
   public EyesoreConfig.BasicAttack basicAttack;
   @Expose
   public EyesoreConfig.LaserAttack laserAttack;

   @Override
   public String getName() {
      return "eyesore";
   }

   @Override
   protected void reset() {
      this.basicAttack = new EyesoreConfig.BasicAttack(5.0F, 4.0F);
      this.laserAttack = new EyesoreConfig.LaserAttack(4.0F, 2.0F, 20);
   }

   public static class BasicAttack {
      @Expose
      public float baseDamage;
      @Expose
      public float extraDamagePerPlayer;

      public BasicAttack(float baseDamage, float extraDamagePerPlayer) {
         this.baseDamage = baseDamage;
         this.extraDamagePerPlayer = extraDamagePerPlayer;
      }

      public float getDamage(EyesoreFireballEntity entity) {
         ServerLevel world = (ServerLevel)entity.getCommandSenderWorld();
         return this.baseDamage;
      }
   }

   public static class LaserAttack {
      @Expose
      public float baseDamage;
      @Expose
      public float extraDamagePerPlayer;
      @Expose
      public int tickDelay;

      public LaserAttack(float baseDamage, float extraDamagePerPlayer, int tickDelay) {
         this.baseDamage = baseDamage;
         this.extraDamagePerPlayer = extraDamagePerPlayer;
         this.tickDelay = tickDelay;
      }

      public float getDamage(EyesoreEntity entity, int tick) {
         if (tick % this.tickDelay != 0) {
            return 0.0F;
         } else {
            ServerLevel world = (ServerLevel)entity.getCommandSenderWorld();
            return this.baseDamage;
         }
      }
   }
}
