package iskallia.vault.core.event.common;

import iskallia.vault.core.event.Event;
import net.minecraft.server.level.ServerPlayer;

public class SoulShardChanceEvent extends Event<SoulShardChanceEvent, SoulShardChanceEvent.Data> {
   public SoulShardChanceEvent() {
   }

   protected SoulShardChanceEvent(SoulShardChanceEvent parent) {
      super(parent);
   }

   public SoulShardChanceEvent createChild() {
      return new SoulShardChanceEvent(this);
   }

   public SoulShardChanceEvent.Data invoke(ServerPlayer killer, float chance) {
      return this.invoke(new SoulShardChanceEvent.Data(killer, chance));
   }

   public static class Data {
      private final ServerPlayer killer;
      private float chance;

      public Data(ServerPlayer killer, float chance) {
         this.killer = killer;
         this.chance = chance;
      }

      public ServerPlayer getKiller() {
         return this.killer;
      }

      public float getChance() {
         return this.chance;
      }

      public void setChance(float chance) {
         this.chance = chance;
      }
   }
}
