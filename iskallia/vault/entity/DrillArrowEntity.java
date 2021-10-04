package iskallia.vault.entity;

import iskallia.vault.init.ModEntities;
import iskallia.vault.util.BlockHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity.PickupStatus;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class DrillArrowEntity extends ArrowEntity {
   private int maxBreakCount = 0;
   private int breakCount = 0;
   private boolean doBreak = true;

   public DrillArrowEntity(EntityType<? extends DrillArrowEntity> type, World worldIn) {
      super(type, worldIn);
   }

   public DrillArrowEntity(World worldIn, double x, double y, double z) {
      this(ModEntities.DRILL_ARROW, worldIn);
      this.func_70107_b(x, y, z);
   }

   public DrillArrowEntity(World worldIn, LivingEntity shooter) {
      this(worldIn, shooter.func_226277_ct_(), shooter.func_226280_cw_() - 0.1F, shooter.func_226281_cx_());
      this.func_212361_a(shooter);
      if (shooter instanceof PlayerEntity) {
         this.field_70251_a = PickupStatus.ALLOWED;
      }
   }

   public DrillArrowEntity setMaxBreakCount(int maxBreakCount) {
      this.maxBreakCount = maxBreakCount;
      return this;
   }

   public void func_70071_h_() {
      if (this.doBreak && !this.func_130014_f_().func_201670_d()) {
         this.aoeBreak();
      }

      if (this.func_130014_f_().func_201670_d()) {
         this.playEffects();
      }

      super.func_70071_h_();
   }

   private void playEffects() {
      Vector3d vec = this.func_213303_ch();

      for (int i = 0; i < 5; i++) {
         Vector3d v = vec.func_72441_c(
            this.field_70146_Z.nextFloat() * 0.4F * (this.field_70146_Z.nextBoolean() ? 1 : -1),
            this.field_70146_Z.nextFloat() * 0.4F * (this.field_70146_Z.nextBoolean() ? 1 : -1),
            this.field_70146_Z.nextFloat() * 0.4F * (this.field_70146_Z.nextBoolean() ? 1 : -1)
         );
         this.field_70170_p.func_195594_a(ParticleTypes.field_218417_ae, v.field_72450_a, v.field_72448_b, v.field_72449_c, 0.0, 0.0, 0.0);
      }
   }

   private void aoeBreak() {
      Entity shooter = this.func_234616_v_();
      if (shooter instanceof ServerPlayerEntity) {
         ServerPlayerEntity player = (ServerPlayerEntity)shooter;
         World world = this.func_130014_f_();
         float vel = (float)this.func_213322_ci().func_72433_c();

         for (BlockPos offset : BlockHelper.getSphericalPositions(this.func_233580_cy_(), Math.max(4.5F, 4.5F * vel))) {
            if (this.breakCount >= this.maxBreakCount) {
               break;
            }

            BlockState state = world.func_180495_p(offset);
            if (!state.isAir(world, offset) && (!state.func_235783_q_() || state.getHarvestLevel() <= 2)) {
               float hardness = state.func_185887_b(world, offset);
               if (hardness >= 0.0F && hardness <= 25.0F && this.destroyBlock(world, offset, state, player)) {
                  this.breakCount++;
               }
            }
         }
      }
   }

   private boolean destroyBlock(World world, BlockPos pos, BlockState state, ServerPlayerEntity player) {
      ItemStack miningItem = new ItemStack(Items.field_151046_w);
      Block.func_220054_a(world.func_180495_p(pos), world, pos, world.func_175625_s(pos), null, miningItem);
      return state.removedByPlayer(world, pos, player, true, state.func_204520_s());
   }

   protected void func_70227_a(RayTraceResult result) {
      if (result instanceof BlockRayTraceResult && this.breakCount < this.maxBreakCount && !this.func_130014_f_().func_201670_d()) {
         this.aoeBreak();
      }

      if (this.breakCount >= this.maxBreakCount) {
         this.doBreak = false;
         super.func_70227_a(result);
      }
   }

   public void func_70037_a(CompoundNBT compound) {
      super.func_70037_a(compound);
      this.doBreak = compound.func_74767_n("break");
      this.breakCount = compound.func_74762_e("breakCount");
      this.maxBreakCount = compound.func_74762_e("maxBreakCount");
   }

   public void func_213281_b(CompoundNBT compound) {
      super.func_213281_b(compound);
      compound.func_74757_a("break", this.doBreak);
      compound.func_74768_a("breakCount", this.breakCount);
      compound.func_74768_a("maxBreakCount", this.maxBreakCount);
   }

   public IPacket<?> func_213297_N() {
      return NetworkHooks.getEntitySpawningPacket(this);
   }
}
