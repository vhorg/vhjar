package iskallia.vault.entity.boss;

import iskallia.vault.init.ModEntities;
import iskallia.vault.init.ModItems;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class CatalystInhibitorEntity extends ThrowableItemProjectile {
   public CatalystInhibitorEntity(Level level, Player player, ItemStack catalystInhibitorStack) {
      super(ModEntities.CATALYST_INHIBITOR, player, level);
      this.setItem(catalystInhibitorStack);
   }

   public CatalystInhibitorEntity(EntityType<? extends ThrowableItemProjectile> entityType, Level level) {
      super(entityType, level);
   }

   protected Item getDefaultItem() {
      return ModItems.WOODEN_CATALYST_INHIBITOR;
   }

   protected void onHitEntity(EntityHitResult result) {
      if (!this.level.isClientSide() && this.getOwner() instanceof Player player) {
         if (result.getEntity() instanceof BossProtectionCatalystEntity catalyst) {
            catalyst.hitWithInhibitor(player, this.getItem());
         } else {
            this.handleOnHit();
         }
      }
   }

   private void handleOnHit() {
      if (this.level instanceof ServerLevel serverLevel && this.level.random.nextFloat() > 0.5F) {
         this.level.addFreshEntity(new ItemEntity(this.level, this.getX(), this.getY(), this.getZ(), this.getItem()));
      }
   }

   protected boolean canHitEntity(Entity entity) {
      return entity.isAlive() && entity instanceof BossProtectionCatalystEntity;
   }

   protected void onHit(HitResult result) {
      super.onHit(result);
      if (!this.level.isClientSide) {
         this.level.broadcastEntityEvent(this, (byte)3);
         this.discard();
      }
   }

   protected void onHitBlock(BlockHitResult hitResult) {
      if (!this.level.isClientSide()) {
         this.handleOnHit();
      }
   }
}
