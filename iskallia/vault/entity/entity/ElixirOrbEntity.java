package iskallia.vault.entity.entity;

import iskallia.vault.init.ModEntities;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.SummonElixirOrbMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Entity.MovementEmission;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkDirection;

public class ElixirOrbEntity extends Entity {
   private int age;
   private int size;

   public ElixirOrbEntity(EntityType<? extends ElixirOrbEntity> type, Level world) {
      super(type, world);
      this.setInvulnerable(true);
   }

   public ElixirOrbEntity(Level world, double x, double y, double z, int size, int age) {
      this(ModEntities.ELIXIR_ORB, world);
      this.setPos(x, y, z);
      this.setYRot((float)(this.random.nextDouble() * 360.0));
      this.setDeltaMovement(this.random.nextDouble() * 0.4 - 0.2, this.random.nextDouble() * 0.4, this.random.nextDouble() * 0.4 - 0.2);
      this.age = age;
      this.size = size;
   }

   public int getAge() {
      return this.age;
   }

   public int getSize() {
      return this.size;
   }

   protected MovementEmission getMovementEmission() {
      return MovementEmission.NONE;
   }

   public boolean isAttackable() {
      return false;
   }

   public SoundSource getSoundSource() {
      return SoundSource.AMBIENT;
   }

   public void tick() {
      super.tick();
      this.xo = this.getX();
      this.yo = this.getY();
      this.zo = this.getZ();
      if (this.isEyeInFluid(FluidTags.WATER)) {
         Vec3 velocity = this.getDeltaMovement();
         this.setDeltaMovement(velocity.x * 0.99, Math.min(velocity.y + 5.0E-4, 0.06), velocity.z * 0.99);
      } else if (!this.isNoGravity()) {
         this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.03, 0.0));
      }

      if (this.level.getFluidState(this.blockPosition()).is(FluidTags.LAVA)) {
         this.setDeltaMovement((this.random.nextFloat() - this.random.nextFloat()) * 0.2F, 0.2F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
      }

      if (!this.level.noCollision(this.getBoundingBox())) {
         this.moveTowardsClosestSpace(this.getX(), (this.getBoundingBox().minY + this.getBoundingBox().maxY) / 2.0, this.getZ());
      }

      this.move(MoverType.SELF, this.getDeltaMovement());
      float friction = 0.98F;
      if (this.onGround) {
         BlockPos pos = new BlockPos(this.getX(), this.getY() - 1.0, this.getZ());
         friction = this.level.getBlockState(pos).getFriction(this.level, pos, this) * 0.98F;
      }

      this.setDeltaMovement(this.getDeltaMovement().multiply(friction, 0.98, friction));
      if (this.onGround) {
         this.setDeltaMovement(this.getDeltaMovement().multiply(1.0, -0.9, 1.0));
      }

      if (--this.age < 0) {
         this.discard();
      }
   }

   protected void defineSynchedData() {
   }

   protected void doWaterSplashEffect() {
   }

   public void addAdditionalSaveData(CompoundTag pCompound) {
      pCompound.putInt("Age", this.age);
      pCompound.putInt("Size", this.size);
   }

   public void readAdditionalSaveData(CompoundTag pCompound) {
      this.age = pCompound.getInt("Age");
      this.size = pCompound.getInt("Size");
   }

   public Packet<?> getAddEntityPacket() {
      return ModNetwork.CHANNEL.toVanillaPacket(new SummonElixirOrbMessage(this), NetworkDirection.PLAY_TO_CLIENT);
   }
}
