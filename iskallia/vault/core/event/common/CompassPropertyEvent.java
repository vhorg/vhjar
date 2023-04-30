package iskallia.vault.core.event.common;

import iskallia.vault.core.event.Event;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class CompassPropertyEvent extends Event<CompassPropertyEvent, CompassPropertyEvent.Data> {
   public CompassPropertyEvent() {
   }

   protected CompassPropertyEvent(CompassPropertyEvent parent) {
      super(parent);
   }

   public CompassPropertyEvent createChild() {
      return new CompassPropertyEvent(this);
   }

   public CompassPropertyEvent.Data invoke(ClientLevel world, LivingEntity entity, ItemStack compass, int seed, BlockPos target) {
      return this.invoke(new CompassPropertyEvent.Data(world, entity, compass, seed, target));
   }

   public static class Data {
      private final ClientLevel world;
      private final LivingEntity entity;
      private final ItemStack stack;
      private final int seed;
      private BlockPos target;

      public Data(ClientLevel world, LivingEntity entity, ItemStack stack, int seed, BlockPos target) {
         this.world = world;
         this.entity = entity;
         this.stack = stack;
         this.seed = seed;
         this.target = target;
      }

      public ClientLevel getWorld() {
         return this.world;
      }

      public LivingEntity getEntity() {
         return this.entity;
      }

      public ItemStack getStack() {
         return this.stack;
      }

      public int getSeed() {
         return this.seed;
      }

      public BlockPos getTarget() {
         return this.target;
      }

      public void setTarget(BlockPos target) {
         this.target = target;
      }
   }
}
