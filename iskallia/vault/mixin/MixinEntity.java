package iskallia.vault.mixin;

import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModItems;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.ITeleporter;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Entity.class})
public abstract class MixinEntity {
   @Shadow
   public Level level;

   @Shadow
   @Nullable
   public abstract Entity changeDimension(ServerLevel var1, ITeleporter var2);

   @Inject(
      method = {"baseTick"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"
      )}
   )
   public void baseTick(CallbackInfo ci) {
      Entity self = (Entity)this;
      if (!self.level.isClientSide) {
         if (self.getClass() == ItemEntity.class) {
            ItemEntity itemEntity = (ItemEntity)self;
            Item artifactItem = (Item)ForgeRegistries.ITEMS.getValue(ModBlocks.VAULT_ARTIFACT.getRegistryName());
            if (itemEntity.getItem().getItem() == artifactItem) {
               ServerLevel world = (ServerLevel)self.level;
               ItemEntity newItemEntity = new ItemEntity(world, self.getX(), self.getY(), self.getZ(), new ItemStack(ModItems.ARTIFACT_FRAGMENT));
               this.spawnParticles(world, self.blockPosition());
               world.addFreshEntity(newItemEntity);
               itemEntity.remove(RemovalReason.DISCARDED);
            }
         }
      }
   }

   private void spawnParticles(Level world, BlockPos pos) {
      for (int i = 0; i < 20; i++) {
         double d0 = world.random.nextGaussian() * 0.02;
         double d1 = world.random.nextGaussian() * 0.02;
         double d2 = world.random.nextGaussian() * 0.02;
         ((ServerLevel)world)
            .sendParticles(
               ParticleTypes.FLAME,
               pos.getX() + world.random.nextDouble() - d0,
               pos.getY() + world.random.nextDouble() - d1,
               pos.getZ() + world.random.nextDouble() - d2,
               10,
               d0,
               d1,
               d2,
               0.5
            );
      }

      world.playSound(null, pos, SoundEvents.GENERIC_BURN, SoundSource.BLOCKS, 1.0F, 1.0F);
   }

   @Inject(
      method = {"changeDimension(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraftforge/common/util/ITeleporter;)Lnet/minecraft/world/entity/Entity;"},
      at = {@At("HEAD")},
      remap = false,
      cancellable = true
   )
   public void changeDimension(ServerLevel destination, ITeleporter teleporter, CallbackInfoReturnable<Entity> ci) {
      if (!destination.getServer().isSameThread()) {
         destination.getServer().execute(() -> this.changeDimension(destination, teleporter));
         ci.setReturnValue(null);
      }
   }
}
