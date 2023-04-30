package iskallia.vault.bounty.task;

import iskallia.vault.block.VaultOreBlock;
import iskallia.vault.bounty.TaskRegistry;
import iskallia.vault.bounty.TaskReward;
import iskallia.vault.bounty.task.properties.MiningProperties;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.world.data.BountyData;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.ForgeRegistries;

@EventBusSubscriber
public class MiningTask extends Task<MiningProperties> {
   public MiningTask(UUID bountyId, MiningProperties properties, TaskReward taskReward) {
      super(TaskRegistry.MINING, bountyId, properties, taskReward);
   }

   public MiningTask(CompoundTag tag) {
      this.deserializeNBT(tag);
   }

   @Override
   protected <E> boolean doValidate(ServerPlayer player, E event) {
      if (event instanceof BreakEvent e) {
         BlockState state = e.getState();
         if (state.getBlock() instanceof VaultOreBlock block && (Boolean)state.getValue(VaultOreBlock.GENERATED)) {
            ResourceLocation id = ForgeRegistries.BLOCKS.getKey(block);
            if (id == null) {
               return false;
            } else {
               return id.equals(this.getProperties().getBlockId()) ? true : ModConfigs.BOUNTY_ORES.getValidOres(this.getProperties().getBlockId()).contains(id);
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   @Override
   public boolean isComplete() {
      return this.amountObtained >= this.getProperties().getAmount();
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
      this.properties = new MiningProperties(tag.getCompound("properties"));
   }

   @SubscribeEvent
   public static void onOreBroken(BreakEvent event) {
      if (event.getPlayer() instanceof ServerPlayer player) {
         for (Task<?> task : BountyData.get().getAllLegendaryById(player, TaskRegistry.MINING).stream().filter(taskx -> !taskx.isComplete()).toList()) {
            if (task.validate(player, event)) {
               task.increment(1.0);
               if (task.isComplete()) {
                  task.complete(player);
               }

               return;
            }
         }

         for (Task<?> taskx : BountyData.get().getAllActiveById(player, TaskRegistry.MINING).stream().filter(taskxx -> !taskxx.isComplete()).toList()) {
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
