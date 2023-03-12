package iskallia.vault.core.vault.enhancement;

import iskallia.vault.block.entity.VaultEnhancementAltarTileEntity;
import iskallia.vault.core.data.DataObject;
import iskallia.vault.core.data.key.registry.FieldRegistry;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.event.common.BlockUseEvent;
import iskallia.vault.core.random.ChunkRandom;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.init.ModBlocks;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkHooks;

public class EnhancementTaskManager extends DataObject<EnhancementTaskManager> {
   public static final FieldRegistry FIELDS = new FieldRegistry();

   @Override
   public FieldRegistry getFields() {
      return FIELDS;
   }

   public void initServer(VirtualWorld world, Vault vault) {
      CommonEvents.BLOCK_USE.of(ModBlocks.ENHANCEMENT_ALTAR).at(BlockUseEvent.Phase.HEAD).in(world).register(this, data -> {
         if (data.getWorld() == world) {
            Player player = data.getPlayer();
            BlockPos pos = data.getPos();
            BlockEntity entity = data.getWorld().getBlockEntity(data.getPos());
            ChunkRandom random = ChunkRandom.any();
            random.setBlockSeed(vault.get(Vault.SEED), pos.getX(), pos.getY(), pos.getZ(), 1215835893);
            if (entity instanceof VaultEnhancementAltarTileEntity altar) {
               List<EnhancementTask<?>> tasks = EnhancementData.get(player);
               if (tasks.stream().anyMatch(task -> task.isFinished() && task.getAltar().equals(altar.getUUID()))) {
                  NetworkHooks.openGui((ServerPlayer)player, altar, buffer -> buffer.writeBlockPos(pos));
                  data.setResult(InteractionResult.SUCCESS);
               } else if (tasks.stream().allMatch(EnhancementTask::isFinished)) {
                  EnhancementData.add(altar.getConfig().create(vault, player, altar, random));
                  data.setResult(InteractionResult.SUCCESS);
               } else {
                  data.setResult(InteractionResult.FAIL);
               }
            } else {
               data.setResult(InteractionResult.FAIL);
            }
         }
      });
      CommonEvents.LISTENER_LEAVE.register(this, data -> {
         if (data.getVault() == vault) {
            EnhancementData.remove(vault, data.getListener().getId());
         }
      });
   }

   public void releaseServer() {
      CommonEvents.BLOCK_USE.release(this);
      CommonEvents.LISTENER_LEAVE.release(this);
   }
}
