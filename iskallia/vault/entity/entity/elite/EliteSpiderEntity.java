package iskallia.vault.entity.entity.elite;

import iskallia.vault.entity.entity.VaultSpiderBabyEntity;
import iskallia.vault.init.ModEntities;
import javax.annotation.Nonnull;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.level.Level;

public class EliteSpiderEntity extends Spider {
   public EliteSpiderEntity(EntityType<? extends Spider> entityType, Level world) {
      super(entityType, world);
   }

   public void remove(@Nonnull RemovalReason reason) {
      if (!this.level.isClientSide && this.isDeadOrDying()) {
         Component component = this.getCustomName();
         boolean flag = this.isNoAi();
         int k = 8 + this.random.nextInt(3);

         for (int l = 0; l < k; l++) {
            VaultSpiderBabyEntity babySpider = (VaultSpiderBabyEntity)ModEntities.VAULT_SPIDER_BABY.create(this.level);
            if (babySpider != null) {
               if (this.isPersistenceRequired()) {
                  babySpider.setPersistenceRequired();
               }

               babySpider.setCustomName(component);
               babySpider.setNoAi(flag);
               babySpider.setInvulnerable(this.isInvulnerable());
               babySpider.moveTo(this.position());
               this.level.addFreshEntity(babySpider);
            }
         }
      }

      super.remove(reason);
   }
}
