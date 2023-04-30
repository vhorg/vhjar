package iskallia.vault.core.event.common;

import iskallia.vault.core.event.Event;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;

public class EntityReadEvent extends Event<EntityReadEvent, EntityReadEvent.Data> {
   public EntityReadEvent() {
   }

   protected EntityReadEvent(EntityReadEvent parent) {
      super(parent);
   }

   public EntityReadEvent createChild() {
      return new EntityReadEvent(this);
   }

   public EntityReadEvent.Data invoke(Entity entity, CompoundTag nbt) {
      return this.invoke(new EntityReadEvent.Data(entity, nbt));
   }

   public static class Data {
      private final Entity entity;
      private final CompoundTag nbt;

      public Data(Entity entity, CompoundTag nbt) {
         this.entity = entity;
         this.nbt = nbt;
      }

      public Entity getEntity() {
         return this.entity;
      }

      public CompoundTag getNbt() {
         return this.nbt;
      }
   }
}
