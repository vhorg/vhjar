package iskallia.vault.entity.entity;

import iskallia.vault.init.ModEntities;
import iskallia.vault.util.BlockHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.AbstractArrow.Pickup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

public class DrillArrowEntity extends Arrow {
   private int maxBreakCount = 0;
   private int breakCount = 0;
   private boolean doBreak = true;

   public DrillArrowEntity(EntityType<? extends DrillArrowEntity> type, Level worldIn) {
      super(type, worldIn);
   }

   public DrillArrowEntity(Level worldIn, double x, double y, double z) {
      this(ModEntities.DRILL_ARROW, worldIn);
      this.setPos(x, y, z);
   }

   public DrillArrowEntity(Level worldIn, LivingEntity shooter) {
      this(worldIn, shooter.getX(), shooter.getEyeY() - 0.1F, shooter.getZ());
      this.setOwner(shooter);
      if (shooter instanceof Player) {
         this.pickup = Pickup.ALLOWED;
      }
   }

   public DrillArrowEntity setMaxBreakCount(int maxBreakCount) {
      this.maxBreakCount = maxBreakCount;
      return this;
   }

   public void tick() {
      if (this.doBreak && !this.getCommandSenderWorld().isClientSide()) {
         this.aoeBreak();
      }

      if (this.getCommandSenderWorld().isClientSide()) {
         this.playEffects();
      }

      super.tick();
   }

   private void playEffects() {
      Vec3 vec = this.position();

      for (int i = 0; i < 5; i++) {
         Vec3 v = vec.add(
            this.random.nextFloat() * 0.4F * (this.random.nextBoolean() ? 1 : -1),
            this.random.nextFloat() * 0.4F * (this.random.nextBoolean() ? 1 : -1),
            this.random.nextFloat() * 0.4F * (this.random.nextBoolean() ? 1 : -1)
         );
         this.level.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, v.x, v.y, v.z, 0.0, 0.0, 0.0);
      }
   }

   private void aoeBreak() {
      if (this.getOwner() instanceof ServerPlayer player) {
         Level world = this.getCommandSenderWorld();
         float vel = (float)this.getDeltaMovement().length();

         for (BlockPos offset : BlockHelper.getSpherePositions(this.blockPosition(), Math.max(4.5F, 4.5F * vel))) {
            if (this.breakCount >= this.maxBreakCount) {
               break;
            }

            BlockState state = world.getBlockState(offset);
            if (!state.isAir() && (!state.requiresCorrectToolForDrops() || state.is(BlockTags.NEEDS_IRON_TOOL))) {
               float hardness = state.getDestroySpeed(world, offset);
               if (hardness >= 0.0F && hardness <= 25.0F && this.destroyBlock(world, offset, state, player)) {
                  this.breakCount++;
               }
            }
         }
      }
   }

   private boolean destroyBlock(Level world, BlockPos pos, BlockState state, ServerPlayer player) {
      ItemStack miningItem = new ItemStack(Items.DIAMOND_PICKAXE);
      Block.dropResources(world.getBlockState(pos), world, pos, world.getBlockEntity(pos), player, miningItem);
      return state.onDestroyedByPlayer(world, pos, player, true, state.getFluidState());
   }

   protected void onHit(HitResult result) {
      if (result instanceof BlockHitResult && this.breakCount < this.maxBreakCount && !this.getCommandSenderWorld().isClientSide()) {
         this.aoeBreak();
      }

      if (this.breakCount >= this.maxBreakCount) {
         this.doBreak = false;
         super.onHit(result);
      }
   }

   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.doBreak = compound.getBoolean("break");
      this.breakCount = compound.getInt("breakCount");
      this.maxBreakCount = compound.getInt("maxBreakCount");
   }

   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putBoolean("break", this.doBreak);
      compound.putInt("breakCount", this.breakCount);
      compound.putInt("maxBreakCount", this.maxBreakCount);
   }

   public Packet<?> getAddEntityPacket() {
      return NetworkHooks.getEntitySpawningPacket(this);
   }
}
