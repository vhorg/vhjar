package iskallia.vault.entity.ai.eyesore;

import iskallia.vault.entity.EyesoreEntity;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.EnteredEyesoreDomainMessage;
import net.minecraftforge.fml.network.PacketDistributor;

public class CreepyIdleTask extends EyesoreTask<EyesoreEntity> {
   public int tick = 0;
   public boolean finished = false;

   public CreepyIdleTask(EyesoreEntity entity) {
      super(entity);
   }

   @Override
   public void tick() {
      if (!this.isFinished()) {
         if (this.tick == 40) {
            EnteredEyesoreDomainMessage packet = new EnteredEyesoreDomainMessage();
            ModNetwork.CHANNEL.send(PacketDistributor.TRACKING_ENTITY.with(this::getEntity), packet);
         }

         this.getEntity().func_184212_Q().func_187227_b(EyesoreEntity.WATCH_CLIENT, true);
         this.tick++;
         if (this.tick >= 300 || this.getEntity().func_189748_bU() != null) {
            this.getEntity().func_184212_Q().func_187227_b(EyesoreEntity.WATCH_CLIENT, false);
            this.finished = true;
         }
      }
   }

   @Override
   public boolean isFinished() {
      return this.finished;
   }

   @Override
   public void reset() {
      this.tick = 0;
      this.finished = false;
   }
}
