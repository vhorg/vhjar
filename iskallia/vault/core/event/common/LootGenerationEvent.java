package iskallia.vault.core.event.common;

import iskallia.vault.core.event.Event;
import iskallia.vault.core.world.loot.generator.LootGenerator;

public class LootGenerationEvent extends Event<LootGenerationEvent, LootGenerationEvent.Data> {
   public LootGenerationEvent() {
   }

   protected LootGenerationEvent(LootGenerationEvent parent) {
      super(parent);
   }

   public LootGenerationEvent createChild() {
      return new LootGenerationEvent(this);
   }

   public LootGenerationEvent.Data invoke(LootGenerator generator, LootGenerationEvent.Phase phase) {
      return this.invoke(new LootGenerationEvent.Data(generator, phase));
   }

   public LootGenerationEvent pre() {
      return this.filter(data -> data.phase == LootGenerationEvent.Phase.PRE);
   }

   public LootGenerationEvent post() {
      return this.filter(data -> data.phase == LootGenerationEvent.Phase.POST);
   }

   public static class Data {
      private final LootGenerator generator;
      private final LootGenerationEvent.Phase phase;

      public Data(LootGenerator generator, LootGenerationEvent.Phase phase) {
         this.generator = generator;
         this.phase = phase;
      }

      public LootGenerator getGenerator() {
         return this.generator;
      }

      public LootGenerationEvent.Phase getPhase() {
         return this.phase;
      }
   }

   public static enum Phase {
      PRE,
      POST;
   }
}
