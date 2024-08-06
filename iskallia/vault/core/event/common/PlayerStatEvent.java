package iskallia.vault.core.event.common;

import iskallia.vault.core.event.Event;
import iskallia.vault.util.calc.PlayerStat;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.world.entity.LivingEntity;

public class PlayerStatEvent extends Event<PlayerStatEvent, PlayerStatEvent.Data> {
   public PlayerStatEvent() {
   }

   protected PlayerStatEvent(PlayerStatEvent parent) {
      super(parent);
   }

   public PlayerStatEvent createChild() {
      return new PlayerStatEvent(this);
   }

   public PlayerStatEvent.Data invoke(PlayerStat stat, LivingEntity entity, float value) {
      return this.invoke(new PlayerStatEvent.Data(stat, entity, value));
   }

   public PlayerStatEvent.Data invoke(PlayerStat stat, LivingEntity entity, float value, Consumer<PlayerStatEvent.Data> config) {
      PlayerStatEvent.Data data = new PlayerStatEvent.Data(stat, entity, value);
      config.accept(data);
      return this.invoke(data);
   }

   public PlayerStatEvent of(PlayerStat stat) {
      return this.filter(data -> data.stat == stat);
   }

   public static class Data {
      private final PlayerStat stat;
      private final LivingEntity entity;
      private float value;
      private LivingEntity enemy;

      public Data(PlayerStat stat, LivingEntity entity, float value) {
         this.stat = stat;
         this.entity = entity;
         this.value = value;
      }

      public PlayerStat getStat() {
         return this.stat;
      }

      public LivingEntity getEntity() {
         return this.entity;
      }

      public float getValue() {
         return this.value;
      }

      public Optional<LivingEntity> getEnemy() {
         return Optional.ofNullable(this.enemy);
      }

      public void setValue(float value) {
         this.value = value;
      }

      public PlayerStatEvent.Data setEnemy(LivingEntity attacker) {
         this.enemy = attacker;
         return this;
      }
   }
}
