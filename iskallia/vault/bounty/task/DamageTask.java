package iskallia.vault.bounty.task;

import iskallia.vault.bounty.TaskRegistry;
import iskallia.vault.bounty.TaskReward;
import iskallia.vault.bounty.task.properties.DamageProperties;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.bounty.ClientboundBountyCompleteMessage;
import iskallia.vault.world.data.BountyData;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.registries.ForgeRegistries;

@EventBusSubscriber
public class DamageTask extends Task<DamageProperties> {
   public DamageTask(UUID bountyId, DamageProperties properties, TaskReward taskReward) {
      super(TaskRegistry.DAMAGE_ENTITY, bountyId, properties, taskReward);
   }

   public DamageTask(CompoundTag tag) {
      this.deserializeNBT(tag);
   }

   @Override
   protected <E> boolean doValidate(ServerPlayer player, E event) {
      if (event instanceof LivingHurtEvent e) {
         ResourceLocation entityId = ForgeRegistries.ENTITIES.getKey(e.getEntity().getType());
         if (entityId == null) {
            return false;
         } else {
            return entityId.equals(this.getProperties().getEntityId())
               ? true
               : ModConfigs.BOUNTY_ENTITIES.getValidEntities(this.getProperties().getEntityId()).contains(entityId);
         }
      } else {
         return false;
      }
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
      this.properties = new DamageProperties(tag.getCompound("properties"));
   }

   @SubscribeEvent
   public static void onDamageEntity(LivingHurtEvent event) {
      if (event.getSource().getEntity() instanceof ServerPlayer player) {
         BountyData data = BountyData.get();
         data.getAllActiveTasksById(player, TaskRegistry.DAMAGE_ENTITY)
            .stream()
            .filter(task -> !task.isComplete())
            .filter(task -> task.validate(player, event))
            .peek(task -> task.increment(event.getAmount()))
            .filter(Task::isComplete)
            .forEach(
               task -> ModNetwork.CHANNEL
                  .sendTo(new ClientboundBountyCompleteMessage(task.taskType), player.connection.connection, NetworkDirection.PLAY_TO_CLIENT)
            );
      }
   }
}
