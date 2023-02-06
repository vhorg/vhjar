package iskallia.vault.core.event.common;

import iskallia.vault.core.event.Event;
import java.util.List;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

public class EntityChainAttackedEvent extends Event<EntityChainAttackedEvent, EntityChainAttackedEvent.Data> {
   public EntityChainAttackedEvent() {
   }

   protected EntityChainAttackedEvent(EntityChainAttackedEvent parent) {
      super(parent);
   }

   public EntityChainAttackedEvent createChild() {
      return new EntityChainAttackedEvent(this);
   }

   public static class Data {
      private final LivingEntity attacker;
      private final List<Mob> attackedMobs;

      public Data(LivingEntity attacker, List<Mob> attackedMobs) {
         this.attacker = attacker;
         this.attackedMobs = attackedMobs;
      }

      public LivingEntity getAttacker() {
         return this.attacker;
      }

      public List<Mob> getAttackedMobs() {
         return this.attackedMobs;
      }
   }
}
