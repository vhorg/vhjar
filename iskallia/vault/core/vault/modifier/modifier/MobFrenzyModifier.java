package iskallia.vault.core.vault.modifier.modifier;

import com.google.gson.annotations.Expose;
import iskallia.vault.VaultMod;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.modifier.reputation.ScalarReputationProperty;
import iskallia.vault.core.vault.modifier.spi.EntityAttributeModifier;
import iskallia.vault.core.vault.modifier.spi.ModifierContext;
import iskallia.vault.core.vault.modifier.spi.VaultModifier;
import iskallia.vault.core.vault.modifier.spi.predicate.IModifierImmunity;
import iskallia.vault.core.vault.objective.KillBossObjective;
import iskallia.vault.core.vault.objective.ObeliskObjective;
import iskallia.vault.core.world.storage.VirtualWorld;
import java.util.UUID;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class MobFrenzyModifier extends VaultModifier<MobFrenzyModifier.Properties> {
   private final EntityAttributeModifier<?> attackDamageAttributeModifier;
   private final EntityAttributeModifier<?> movementSpeedAttributeModifier;

   public MobFrenzyModifier(ResourceLocation id, MobFrenzyModifier.Properties properties, VaultModifier.Display display) {
      super(id, properties, display);
      this.attackDamageAttributeModifier = new EntityAttributeModifier<>(
         VaultMod.id(id.getPath() + "/attack_damage"),
         new EntityAttributeModifier.Properties(
            EntityAttributeModifier.ModifierType.ATTACK_DAMAGE_ADDITIVE_PERCENTILE, properties.getDamage(), properties.reputation
         ),
         display
      );
      this.movementSpeedAttributeModifier = new EntityAttributeModifier<>(
         VaultMod.id(id.getPath() + "/movement_speed"),
         new EntityAttributeModifier.Properties(
            EntityAttributeModifier.ModifierType.SPEED_ADDITIVE_PERCENTILE, properties.getMovementSpeed(), properties.reputation
         ),
         display
      );
   }

   @Override
   public void initServer(VirtualWorld world, Vault vault, ModifierContext context) {
      CommonEvents.ENTITY_SPAWN.register(context.getUUID(), event -> {
         if (event.getEntity() instanceof LivingEntity entity) {
            if (!IModifierImmunity.of(entity).test(this)) {
               if (entity.level == world && !(entity instanceof Player)) {
                  long upperBits = context.getUUID().getMostSignificantBits();
                  long lowerBits = context.getUUID().getLeastSignificantBits();
                  this.attackDamageAttributeModifier.applyToEntity(entity, new UUID(upperBits++, lowerBits), context);
                  this.movementSpeedAttributeModifier.applyToEntity(entity, new UUID(upperBits, lowerBits), context);
               }
            }
         }
      });
      CommonEvents.ENTITY_TICK.register(context.getUUID(), event -> {
         LivingEntity entity = event.getEntityLiving();
         if (entity.level == world && !(entity instanceof Player)) {
            if (!IModifierImmunity.of(entity).test(this)) {
               boolean isBoss = vault.map(Vault.OBJECTIVES, objectives -> objectives.forEach(KillBossObjective.class, objective -> {
                  UUID bossId = objective.get(KillBossObjective.BOSS_ID);
                  return event.getEntity().getUUID().equals(bossId);
               }) || objectives.forEach(ObeliskObjective.class, objective -> {
                  ObeliskObjective.Wave[] waves = objective.get(ObeliskObjective.WAVES);

                  for (ObeliskObjective.Wave wave : waves) {
                     if (wave.get(ObeliskObjective.Wave.MOBS).contains(entity.getUUID())) {
                        return true;
                     }
                  }

                  return false;
               }), Boolean.valueOf(false));
               if (!isBoss && entity.getHealth() > this.properties.maxHealth) {
                  entity.setHealth(this.properties.maxHealth);
               }
            }
         }
      });
   }

   public static class Properties {
      @Expose
      private final float damage;
      @Expose
      private final float movementSpeed;
      @Expose
      private final float maxHealth;
      @Expose
      private final ScalarReputationProperty reputation;

      public Properties(float damage, float movementSpeed, float maxHealth, ScalarReputationProperty reputation) {
         this.damage = damage;
         this.movementSpeed = movementSpeed;
         this.maxHealth = maxHealth;
         this.reputation = reputation;
      }

      public float getDamage() {
         return this.damage;
      }

      public float getMovementSpeed() {
         return this.movementSpeed;
      }

      public float getMaxHealth() {
         return this.maxHealth;
      }
   }
}
