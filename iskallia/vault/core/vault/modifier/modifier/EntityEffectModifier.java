package iskallia.vault.core.vault.modifier.modifier;

import com.google.gson.annotations.Expose;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.modifier.spi.ModifierContext;
import iskallia.vault.core.vault.modifier.spi.VaultModifier;
import iskallia.vault.core.vault.modifier.spi.predicate.IModifierImmunity;
import iskallia.vault.core.world.data.entity.EntityPredicate;
import iskallia.vault.core.world.storage.VirtualWorld;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.eventbus.api.EventPriority;

public class EntityEffectModifier extends VaultModifier<EntityEffectModifier.Properties> {
   public EntityEffectModifier(ResourceLocation id, EntityEffectModifier.Properties properties, VaultModifier.Display display) {
      super(id, properties, display);
      this.setDescriptionFormatter((t, p, s) -> t.formatted((int)Math.abs(p.getChance() * s * 100.0)));
   }

   @Override
   public void initServer(VirtualWorld world, Vault vault, ModifierContext context) {
      CommonEvents.ENTITY_SPAWN.register(context.getUUID(), EventPriority.HIGHEST, event -> {
         if (event.getEntity().level == world) {
            if (event.getEntity() instanceof LivingEntity entity) {
               if (!IModifierImmunity.of(entity).test(this)) {
                  Map<MobEffectInstance, Double> effects = ((EntityEffectModifier.ILivingEntityAccessor)entity).getEffects();
                  if (this.properties.filter.test(entity)) {
                     for (Entry<MobEffectInstance, Double> entry : effects.entrySet()) {
                        if (entry.getKey().getEffect() == this.properties.effect) {
                           effects.put(entry.getKey(), entry.getValue() + this.properties.chance);
                           return;
                        }
                     }

                     effects.put(new MobEffectInstance(this.properties.effect, 999999, this.properties.getAmplifier()), this.properties.chance);
                  }
               }
            }
         }
      });
      CommonEvents.ENTITY_SPAWN.register(context.getUUID(), EventPriority.LOWEST, event -> {
         if (event.getEntity().level == world) {
            if (event.getEntity() instanceof LivingEntity entity) {
               if (!IModifierImmunity.of(entity).test(this)) {
                  Map<MobEffectInstance, Double> effects = ((EntityEffectModifier.ILivingEntityAccessor)entity).getEffects();
                  Random random = entity.level.getRandom();
                  effects.forEach((instance, chance) -> {
                     if (random.nextDouble() < chance) {
                        entity.addEffect(instance);
                     }
                  });
               }
            }
         }
      });
   }

   public interface ILivingEntityAccessor {
      Map<MobEffectInstance, Double> getEffects();
   }

   public static class Properties {
      @Expose
      private final EntityPredicate filter;
      @Expose
      private final MobEffect effect;
      @Expose
      private final int amplifier;
      @Expose
      private final double chance;

      public Properties(EntityPredicate filter, MobEffect effect, int amplifier, double chance) {
         this.filter = filter;
         this.effect = effect;
         this.amplifier = amplifier;
         this.chance = chance;
      }

      public EntityPredicate getFilter() {
         return this.filter;
      }

      public MobEffect getEffect() {
         return this.effect;
      }

      public int getAmplifier() {
         return this.amplifier;
      }

      public double getChance() {
         return this.chance;
      }
   }
}
