package iskallia.vault.entity.entity;

import iskallia.vault.init.ModEntities;
import iskallia.vault.init.ModItems;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.SummonTeamTaskScoreboardMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DiodeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkDirection;
import org.jetbrains.annotations.Nullable;

public class TeamTaskScoreboardEntity extends HangingEntity {
   private int width;
   private int height;

   public TeamTaskScoreboardEntity(EntityType<? extends HangingEntity> entityType, Level level) {
      super(entityType, level);
   }

   public TeamTaskScoreboardEntity(Level level, BlockPos pos, Direction direction) {
      super(ModEntities.TEAM_TASK_SCOREBOARD, level, pos);
      this.calculateWidthAndHeightAndMoveCenter(direction);
      this.setDirection(direction);
   }

   public TeamTaskScoreboardEntity(Level level, BlockPos pos, Vec3 position, Direction direction, int width, int height) {
      super(ModEntities.TEAM_TASK_SCOREBOARD, level, pos);
      this.setPosRaw(position.x, position.y, position.z);
      this.width = width;
      this.height = height;
      this.setDirection(direction);
   }

   private void calculateWidthAndHeightAndMoveCenter(Direction direction) {
      this.width = 0;
      this.height = 0;
      BlockPos blockpos = this.pos.relative(direction.getOpposite());
      Direction counterClockwiseDirection = direction.getCounterClockWise();
      MutableBlockPos mutablePos = new MutableBlockPos();
      int[] heights = new int[4];

      for (int i = 0; i < 4; i++) {
         heights[i] = 0;

         for (int j = 0; j < 4; j++) {
            mutablePos.set(blockpos).move(counterClockwiseDirection, i).move(Direction.UP, j);
            BlockState blockstate = this.level.getBlockState(mutablePos);
            BlockPos blockPosInFront = mutablePos.offset(direction.getNormal());
            BlockState blockstateInFront = this.level.getBlockState(blockPosInFront);
            if (!blockstateInFront.isAir()
               || !this.level.noCollision(new AABB(blockPosInFront))
               || !Block.canSupportCenter(this.level, mutablePos, direction) && !blockstate.getMaterial().isSolid() && !DiodeBlock.isDiode(blockstate)) {
               break;
            }

            heights[i] = j + 1;
         }
      }

      this.width = 16;
      this.height = 16;
      int maxSurface = 0;
      int curHeight = heights[0];

      for (int i = 0; i < heights.length; i++) {
         if (heights[i] < curHeight) {
            curHeight = heights[i];
         }

         int surface = curHeight * (i + 1);
         if (surface == 0) {
            break;
         }

         if (surface > maxSurface) {
            maxSurface = surface;
            this.width = (i + 1) * 16;
            this.height = curHeight * 16;
         }
      }

      Vec3i centerOffsetNormal = counterClockwiseDirection.getNormal();
      BlockPos secondCorner = this.pos.relative(counterClockwiseDirection, this.width / 16).relative(Direction.UP, this.height / 16);
      this.setPos(
         this.getPos().getX() + (counterClockwiseDirection.getStepX() < 0 ? 1 : 0)
            + (secondCorner.getX() - this.pos.getX()) / 2.0
            + (direction.getStepX() < 0 ? 1 : 0)
            + direction.getStepX() * 0.5 / 16.0,
         this.getPos().getY() + this.height / 16.0 / 2.0,
         this.getPos().getZ() + (counterClockwiseDirection.getStepZ() < 0 ? 1 : 0)
            + (secondCorner.getZ() - this.pos.getZ()) / 2.0
            + (direction.getStepZ() < 0 ? 1 : 0)
            + direction.getStepZ() * 0.5 / 16.0
      );
   }

   public boolean survives() {
      if (!this.level.noCollision(this)) {
         return false;
      } else {
         MutableBlockPos mutablePos = new MutableBlockPos();

         for (int k = 0; k < this.width / 16; k++) {
            for (int l = 0; l < this.height / 16; l++) {
               int x = (int)(
                  this.direction.getAxis() == Axis.Z
                     ? this.position().x() - this.width / 16 / 2.0 + k
                     : Math.round(this.position().x()) + (this.direction.getOpposite().getStepX() < 0 ? -1 : 0)
               );
               int y = (int)(this.position().y() - this.height / 16 / 2.0) + l;
               int z = (int)(
                  this.direction.getAxis() == Axis.X
                     ? this.position().z() - this.width / 16 / 2.0 + k
                     : Math.round(this.position().z()) + (this.direction.getOpposite().getStepZ() < 0 ? -1 : 0)
               );
               mutablePos.set(x, y, z);
               BlockState blockstate = this.level.getBlockState(mutablePos);
               if (!Block.canSupportCenter(this.level, mutablePos, this.direction) && !blockstate.getMaterial().isSolid() && !DiodeBlock.isDiode(blockstate)) {
                  return false;
               }
            }
         }

         return this.level.getEntities(this, this.getBoundingBox(), HANGING_ENTITY).isEmpty();
      }
   }

   public void setPos(double pX, double pY, double pZ) {
      this.pos = new BlockPos(pX, pY, pZ);
      this.setPosRaw(pX, pY, pZ);
      this.recalculateBoundingBox();
      this.hasImpulse = true;
   }

   protected void recalculateBoundingBox() {
      if (this.direction != null) {
         double widthX = this.getWidth();
         double height = this.getHeight();
         double widthZ = this.getWidth();
         if (this.direction.getAxis() == Axis.Z) {
            widthZ = 1.0;
         } else {
            widthX = 1.0;
         }

         widthX /= 32.0;
         height /= 32.0;
         widthZ /= 32.0;
         this.setBoundingBox(
            new AABB(
               this.position().x() - widthX,
               this.position().y() - height,
               this.position().z() - widthZ,
               this.position().x() + widthX,
               this.position().y() + height,
               this.position().z() + widthZ
            )
         );
      }
   }

   public boolean canBeCollidedWith() {
      return true;
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public void dropItem(@Nullable Entity pBrokenEntity) {
      if (this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
         this.playSound(SoundEvents.PAINTING_BREAK, 1.0F, 1.0F);
         if (pBrokenEntity instanceof Player player && player.getAbilities().instabuild) {
            return;
         }

         this.spawnAtLocation(ModItems.TEAM_TASK_SCOREBOARD);
      }
   }

   public void playPlacementSound() {
      this.playSound(SoundEvents.PAINTING_PLACE, 1.0F, 1.0F);
   }

   public Packet<?> getAddEntityPacket() {
      return ModNetwork.CHANNEL.toVanillaPacket(new SummonTeamTaskScoreboardMessage(this), NetworkDirection.PLAY_TO_CLIENT);
   }

   public void addAdditionalSaveData(CompoundTag tag) {
      super.addAdditionalSaveData(tag);
      tag.putInt("Width", this.width);
      tag.putInt("Height", this.height);
      tag.putByte("Direction", (byte)this.direction.get2DDataValue());
   }

   public void readAdditionalSaveData(CompoundTag tag) {
      this.width = tag.getInt("Width");
      this.height = tag.getInt("Height");
      this.direction = Direction.from2DDataValue(tag.getByte("Direction"));
      super.readAdditionalSaveData(tag);
      this.setDirection(this.direction);
   }
}
