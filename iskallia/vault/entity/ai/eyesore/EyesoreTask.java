package iskallia.vault.entity.ai.eyesore;

import iskallia.vault.world.data.VaultRaidData;
import iskallia.vault.world.vault.VaultRaid;
import java.util.Random;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.server.ServerWorld;
import org.apache.commons.lang3.ObjectUtils;

public abstract class EyesoreTask<T extends LivingEntity> {
   private final T entity;

   public EyesoreTask(T entity) {
      this.entity = entity;
   }

   public T getEntity() {
      return this.entity;
   }

   public ServerWorld getWorld() {
      return (ServerWorld)this.getEntity().field_70170_p;
   }

   public VaultRaid getVault() {
      return VaultRaidData.get(this.getWorld()).getAt(this.getWorld(), this.getEntity().func_233580_cy_());
   }

   public Random getRandom() {
      return (Random)ObjectUtils.firstNonNull(new Random[]{this.getWorld().func_201674_k(), this.getEntity().func_70681_au()});
   }

   public abstract void tick();

   public abstract boolean isFinished();

   public abstract void reset();
}
