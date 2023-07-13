package iskallia.vault.bounty.task;

import iskallia.vault.bounty.TaskRegistry;
import iskallia.vault.bounty.TaskReward;
import iskallia.vault.bounty.task.properties.KillEntityProperties;
import iskallia.vault.world.data.BountyData;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class KillEntityTask extends Task<KillEntityProperties> {
   public KillEntityTask(UUID bountyId, KillEntityProperties properties, TaskReward taskReward) {
      super(TaskRegistry.KILL_ENTITY, bountyId, properties, taskReward);
   }

   public KillEntityTask(CompoundTag tag) {
      this.deserializeNBT(tag);
   }

   @Override
   protected <E> boolean doValidate(ServerPlayer player, E event) {
      return event instanceof LivingDeathEvent e ? this.getProperties().getFilter().test(e.getEntity()) : false;
   }

   @Override
   public boolean isComplete() {
      return this.amountObtained >= this.properties.getAmount();
   }

   @Override
   public CompoundTag serializeNBT() {
      CompoundTag tag = super.serializeNBT();
      tag.put("properties", this.properties.serializeNBT());
      return tag;
   }

   @Override
   public void deserializeNBT(CompoundTag tag) {
      super.deserializeNBT(tag);
      this.properties = new KillEntityProperties(tag.getCompound("properties"));
   }

   @SubscribeEvent
   public static void onKillEntity(LivingDeathEvent event) {
      if (event.getSource().getEntity() instanceof ServerPlayer player) {
         for (Task<?> task : BountyData.get().getAllLegendaryById(player, TaskRegistry.KILL_ENTITY).stream().filter(taskx -> !taskx.isComplete()).toList()) {
            if (task.validate(player, event)) {
               task.increment(1.0);
               if (task.isComplete()) {
                  task.complete(player);
               }

               return;
            }
         }

         for (Task<?> taskx : BountyData.get().getAllActiveById(player, TaskRegistry.KILL_ENTITY).stream().filter(taskxx -> !taskxx.isComplete()).toList()) {
            if (taskx.validate(player, event)) {
               taskx.increment(1.0);
               if (taskx.isComplete()) {
                  taskx.complete(player);
               }

               return;
            }
         }
      }
   }
}
