package iskallia.vault.item.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.EnderPearlEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;

public class VaultPearlEntity extends EnderPearlEntity {
   public VaultPearlEntity(World worldIn, LivingEntity throwerIn) {
      super(worldIn, throwerIn);
   }

   protected void func_70227_a(RayTraceResult result) {
      Type raytraceresult$type = result.func_216346_c();
      if (raytraceresult$type == Type.ENTITY) {
         this.func_213868_a((EntityRayTraceResult)result);
      } else if (raytraceresult$type == Type.BLOCK) {
         this.func_230299_a_((BlockRayTraceResult)result);
      }

      Entity entity = this.func_234616_v_();

      for (int i = 0; i < 32; i++) {
         this.field_70170_p
            .func_195594_a(
               ParticleTypes.field_197599_J,
               this.func_226277_ct_(),
               this.func_226278_cu_() + this.field_70146_Z.nextDouble() * 2.0,
               this.func_226281_cx_(),
               this.field_70146_Z.nextGaussian(),
               0.0,
               this.field_70146_Z.nextGaussian()
            );
      }

      if (!this.field_70170_p.field_72995_K && !this.field_70128_L) {
         if (entity instanceof ServerPlayerEntity) {
            ServerPlayerEntity serverplayerentity = (ServerPlayerEntity)entity;
            if (serverplayerentity.field_71135_a.func_147298_b().func_150724_d()
               && serverplayerentity.field_70170_p == this.field_70170_p
               && !serverplayerentity.func_70608_bn()) {
               if (entity.func_184218_aH()) {
                  entity.func_184210_p();
               }

               entity.func_70634_a(this.func_226277_ct_(), this.func_226278_cu_(), this.func_226281_cx_());
               entity.field_70143_R = 0.0F;
            }
         } else if (entity != null) {
            entity.func_70634_a(this.func_226277_ct_(), this.func_226278_cu_(), this.func_226281_cx_());
            entity.field_70143_R = 0.0F;
         }

         this.func_70106_y();
      }
   }
}
