package iskallia.vault.core.vault.modifier.modifier;

import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.modifier.spi.EntityAttributeModifier;
import iskallia.vault.core.vault.modifier.spi.ModifierContext;
import iskallia.vault.core.vault.modifier.spi.VaultModifier;
import iskallia.vault.core.vault.modifier.spi.predicate.IModifierImmunity;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.entity.entity.EternalEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class MobAttributeModifier extends EntityAttributeModifier<EntityAttributeModifier.Properties> {
   public MobAttributeModifier(ResourceLocation id, EntityAttributeModifier.Properties properties, VaultModifier.Display display) {
      super(id, properties, display);
      this.setDescriptionFormatter(properties.getType().getDescriptionFormatter());
   }

   @Override
   public void initServer(VirtualWorld world, Vault vault, ModifierContext context) {
      CommonEvents.ENTITY_SPAWN.register(context.getUUID(), event -> {
         if (event.getEntity() instanceof LivingEntity entity && !(event.getEntity() instanceof EternalEntity var5)) {
            if (entity.level == world) {
               if (!IModifierImmunity.of(entity).test(this)) {
                  if (!context.hasTarget() || context.getTarget().equals(entity.getUUID())) {
                     this.applyToEntity(entity, context.getUUID(), context);
                     entity.setHealth(entity.getMaxHealth());
                  }
               }
            }
         }
      });
   }
}
