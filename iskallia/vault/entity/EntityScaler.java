package iskallia.vault.entity;

import iskallia.vault.config.VaultMobGearConfig;
import iskallia.vault.config.VaultMobsConfig;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.entity.entity.EternalEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;

public class EntityScaler {
   private static final String SCALED_TAG = "vault_scaled";

   public static boolean isScaled(Entity entity) {
      return entity.getTags().contains("vault_scaled");
   }

   public static void setScaled(Entity entity) {
      entity.addTag("vault_scaled");
   }

   public static void scale(Vault vault, LivingEntity entity) {
      if (!(entity instanceof Player) && !(entity instanceof EternalEntity)) {
         if (!isScaled(entity)) {
            vault.ifPresent(Vault.LEVEL, level -> {
               VaultMobsConfig.scale(entity, level.get());
               VaultMobGearConfig.applyEquipment(entity, level.get());
            });
            setScaled(entity);
            if (entity instanceof Mob mob) {
               mob.setPersistenceRequired();
            }
         }
      }
   }
}
