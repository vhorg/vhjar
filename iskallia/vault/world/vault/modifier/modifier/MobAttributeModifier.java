package iskallia.vault.world.vault.modifier.modifier;

import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.world.vault.modifier.spi.EntityAttributeModifier;
import iskallia.vault.world.vault.modifier.spi.ModifierContext;
import iskallia.vault.world.vault.modifier.spi.VaultModifier;
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
         if (event.getEntity() instanceof LivingEntity entity) {
            if (entity.level == world) {
               this.applyToEntity(entity, context.getUUID());
               entity.setHealth(entity.getMaxHealth());
            }
         }
      });
   }
}
