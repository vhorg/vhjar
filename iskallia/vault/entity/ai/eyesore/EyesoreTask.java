package iskallia.vault.entity.ai.eyesore;

import iskallia.vault.world.vault.VaultRaid;
import java.util.Random;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import org.apache.commons.lang3.ObjectUtils;

public class EyesoreTask<T extends LivingEntity> {
   private final T entity;

   public EyesoreTask(T entity) {
      this.entity = entity;
   }

   public T getEntity() {
      return this.entity;
   }

   public ServerLevel getWorld() {
      return (ServerLevel)this.getEntity().level;
   }

   public VaultRaid getVault() {
      return null;
   }

   public Random getRandom() {
      return (Random)ObjectUtils.firstNonNull(new Random[]{this.getWorld().getRandom(), this.getEntity().getRandom()});
   }
}
