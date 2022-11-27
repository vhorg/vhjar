package iskallia.vault.core.event.common;

import iskallia.vault.core.event.ForgeEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDamageEvent;

public class EntityDamageEvent extends ForgeEvent<EntityDamageEvent, LivingDamageEvent> {
   public EntityDamageEvent() {
   }

   protected EntityDamageEvent(EntityDamageEvent parent) {
      super(parent);
   }

   @Override
   protected void register() {
      MinecraftForge.EVENT_BUS.addListener(event -> this.invoke(event));
   }

   public EntityDamageEvent createChild() {
      return new EntityDamageEvent(this);
   }
}
