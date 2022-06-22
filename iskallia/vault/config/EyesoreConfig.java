package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.entity.EyesoreEntity;
import iskallia.vault.entity.EyesoreFireballEntity;
import iskallia.vault.entity.EyestalkEntity;
import iskallia.vault.world.data.VaultRaidData;
import iskallia.vault.world.vault.VaultRaid;
import net.minecraft.world.server.ServerWorld;

public class EyesoreConfig extends Config {
   @Expose
   public float health;
   @Expose
   public float extraHealthPerPlayer;
   @Expose
   public EyesoreConfig.MeleeAttack meleeAttack;
   @Expose
   public EyesoreConfig.BasicAttack basicAttack;
   @Expose
   public EyesoreConfig.LaserAttack laserAttack;
   @Expose
   public EyesoreConfig.EyeStalk eyeStalk;

   public float getHealth(EyesoreEntity entity) {
      if (!(entity.func_130014_f_() instanceof ServerWorld)) {
         return 10.0F;
      } else {
         ServerWorld world = (ServerWorld)entity.func_130014_f_();
         VaultRaid vault = VaultRaidData.get(world).getAt(world, entity.func_233580_cy_());
         float health = this.health;
         if (vault != null) {
            health += this.extraHealthPerPlayer * (vault.getPlayers().size() - 1);
         }

         return health;
      }
   }

   @Override
   public String getName() {
      return "eyesore";
   }

   @Override
   protected void reset() {
      this.health = 512.0F;
      this.meleeAttack = new EyesoreConfig.MeleeAttack(10.0F, 2.0F, 5.0F);
      this.basicAttack = new EyesoreConfig.BasicAttack(5.0F, 4.0F);
      this.laserAttack = new EyesoreConfig.LaserAttack(4.0F, 2.0F, 20);
      this.eyeStalk = new EyesoreConfig.EyeStalk(1.0F, 1.0F, 3.0F);
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
         ServerWorld world = (ServerWorld)entity.func_130014_f_();
         VaultRaid vault = VaultRaidData.get(world).getAt(world, entity.func_233580_cy_());
         float damage = this.baseDamage;
         if (vault != null) {
            damage += this.extraDamagePerPlayer * (vault.getPlayers().size() - 1);
         }

         return damage;
      }
   }

   public static class EyeStalk {
      @Expose
      public float baseDamage;
      @Expose
      public float extraDamagePerPlayer;
      @Expose
      public float knockback;

      public EyeStalk(float baseDamage, float extraDamagePerPlayer, float knockback) {
         this.baseDamage = baseDamage;
         this.extraDamagePerPlayer = extraDamagePerPlayer;
         this.knockback = knockback;
      }

      public float getDamage(EyestalkEntity entity) {
         ServerWorld world = (ServerWorld)entity.func_130014_f_();
         VaultRaid vault = VaultRaidData.get(world).getAt(world, entity.func_233580_cy_());
         float damage = this.baseDamage;
         if (vault != null) {
            damage += this.extraDamagePerPlayer * (vault.getPlayers().size() - 1);
         }

         return damage;
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
            ServerWorld world = (ServerWorld)entity.func_130014_f_();
            VaultRaid vault = VaultRaidData.get(world).getAt(world, entity.func_233580_cy_());
            float damage = this.baseDamage;
            if (vault != null) {
               damage += this.extraDamagePerPlayer * (vault.getPlayers().size() - 1);
            }

            return damage;
         }
      }
   }

   public static class MeleeAttack {
      @Expose
      public float baseDamage;
      @Expose
      public float extraDamagePerPlayer;
      @Expose
      public float knockback;

      public MeleeAttack(float baseDamage, float extraDamagePerPlayer, float knockback) {
         this.baseDamage = baseDamage;
         this.extraDamagePerPlayer = extraDamagePerPlayer;
         this.knockback = knockback;
      }

      public float getDamage(EyesoreEntity entity) {
         ServerWorld world = (ServerWorld)entity.func_130014_f_();
         VaultRaid vault = VaultRaidData.get(world).getAt(world, entity.func_233580_cy_());
         float damage = this.baseDamage;
         if (vault != null) {
            damage += this.extraDamagePerPlayer * (vault.getPlayers().size() - 1);
         }

         return damage;
      }
   }
}
