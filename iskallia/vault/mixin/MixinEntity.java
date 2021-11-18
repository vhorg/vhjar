package iskallia.vault.mixin;

import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Entity.class})
public class MixinEntity {
   @Shadow
   public World field_70170_p;

   @Inject(
      method = {"baseTick"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/entity/Entity;attackEntityFrom(Lnet/minecraft/util/DamageSource;F)Z"
      )}
   )
   public void baseTick(CallbackInfo ci) {
      Entity self = (Entity)this;
      if (!self.field_70170_p.field_72995_K) {
         if (self.getClass() == ItemEntity.class) {
            ItemEntity itemEntity = (ItemEntity)self;
            Item artifactItem = (Item)ForgeRegistries.ITEMS.getValue(ModBlocks.VAULT_ARTIFACT.getRegistryName());
            if (itemEntity.func_92059_d().func_77973_b() == artifactItem) {
               ServerWorld world = (ServerWorld)self.field_70170_p;
               ItemEntity newItemEntity = new ItemEntity(world, self.func_226277_ct_(), self.func_226278_cu_(), self.func_226281_cx_());
               newItemEntity.func_92058_a(new ItemStack(ModItems.ARTIFACT_FRAGMENT));
               this.spawnParticles(world, self.func_233580_cy_());
               world.func_217440_f(newItemEntity);
               itemEntity.func_70106_y();
            }
         }
      }
   }

   private void spawnParticles(World world, BlockPos pos) {
      for (int i = 0; i < 20; i++) {
         double d0 = world.field_73012_v.nextGaussian() * 0.02;
         double d1 = world.field_73012_v.nextGaussian() * 0.02;
         double d2 = world.field_73012_v.nextGaussian() * 0.02;
         ((ServerWorld)world)
            .func_195598_a(
               ParticleTypes.field_197631_x,
               pos.func_177958_n() + world.field_73012_v.nextDouble() - d0,
               pos.func_177956_o() + world.field_73012_v.nextDouble() - d1,
               pos.func_177952_p() + world.field_73012_v.nextDouble() - d2,
               10,
               d0,
               d1,
               d2,
               0.5
            );
      }

      world.func_184133_a(null, pos, SoundEvents.field_187658_bx, SoundCategory.BLOCKS, 1.0F, 1.0F);
   }
}
