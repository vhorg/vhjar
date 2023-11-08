package iskallia.vault.block.entity;

import iskallia.vault.block.VaultArtifactBlock;
import iskallia.vault.client.particles.ArtifactProjectorParticleOptions;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModItems;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.init.ModParticles;
import iskallia.vault.init.ModSounds;
import iskallia.vault.network.message.ClientboundTESyncMessage;
import iskallia.vault.world.data.PlayerGreedData;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

public class ArtifactProjectorTileEntity extends BlockEntity {
   private static final Random RANDOM = new Random();
   private UUID owner;
   public int ticksToConsume = 80;
   public boolean consuming = false;
   public boolean completed = false;
   public int time;
   public float flip;
   public float oFlip;
   public float flipT;
   public float flipA;
   public float open;
   public float oOpen;
   public float rot;
   public float oRot;
   public float tRot;

   public ArtifactProjectorTileEntity(BlockPos pWorldPosition, BlockState pBlockState) {
      super(ModBlocks.ARTIFACT_PROJECTOR_ENTITY, pWorldPosition, pBlockState);
   }

   public AABB getRenderBoundingBox() {
      return super.getRenderBoundingBox().inflate(6.0);
   }

   public void consume(Player pPlayer) {
      if (this.level instanceof ServerLevel sWorld) {
         if (pPlayer.getUUID().toString().equals(this.getOwner().toString())) {
            BlockState state = sWorld.getBlockState(this.worldPosition.above(3));
            if (!this.completed) {
               if (!state.hasProperty(VaultArtifactBlock.ORDER_PROPERTY)) {
                  return;
               }

               if (!state.hasProperty(HorizontalDirectionalBlock.FACING)
                  || state.getValue(HorizontalDirectionalBlock.FACING) != this.getBlockState().getValue(HorizontalDirectionalBlock.FACING)) {
                  return;
               }

               List<BlockPos> validPositions = VaultArtifactBlock.isValidArtifactSetup(
                  sWorld, this.worldPosition.above(3), sWorld.getBlockState(this.worldPosition.above(3))
               );
               if (!validPositions.isEmpty()) {
                  validPositions.forEach(at -> sWorld.removeBlock(at, false));
                  PlayerGreedData.get().onArtifactCompleted(pPlayer.getUUID());
                  this.completed = true;
                  this.consuming = true;
                  this.ticksToConsume = 80;
                  CompoundTag saveTag = new CompoundTag();
                  this.saveAdditional(saveTag);
                  ModNetwork.CHANNEL
                     .send(
                        PacketDistributor.TRACKING_CHUNK.with(() -> this.level.getChunkAt(this.worldPosition)),
                        new ClientboundTESyncMessage(this.worldPosition, saveTag)
                     );
                  sWorld.playSound(null, this.worldPosition.above(3), ModSounds.ARTIFACT_COMPLETE, SoundSource.BLOCKS, 1.0F, 0.6F);
               }
            } else {
               PlayerGreedData.get().onArtifactCompleted(pPlayer.getUUID());
               this.consuming = true;
               this.ticksToConsume = 80;
               CompoundTag saveTag = new CompoundTag();
               this.saveAdditional(saveTag);
               ModNetwork.CHANNEL
                  .send(
                     PacketDistributor.TRACKING_CHUNK.with(() -> this.level.getChunkAt(this.worldPosition)),
                     new ClientboundTESyncMessage(this.worldPosition, saveTag)
                  );
               sWorld.playSound(null, this.worldPosition.above(3), ModSounds.ARTIFACT_COMPLETE, SoundSource.BLOCKS, 1.0F, 0.6F);
            }
         }
      }
   }

   public UUID getOwner() {
      return this.owner;
   }

   public void setOwner(UUID owner) {
      this.owner = owner;
   }

   public void load(CompoundTag tag) {
      super.load(tag);
      this.owner = tag.getUUID("owner");
      this.ticksToConsume = tag.getInt("ticksToConsume");
      this.consuming = tag.getBoolean("consuming");
      this.completed = tag.getBoolean("completed");
   }

   protected void saveAdditional(CompoundTag tag) {
      super.saveAdditional(tag);
      tag.putUUID("owner", this.owner);
      tag.putInt("ticksToConsume", this.ticksToConsume);
      tag.putBoolean("consuming", this.consuming);
      tag.putBoolean("completed", this.completed);
   }

   public CompoundTag getUpdateTag() {
      return this.saveWithoutMetadata();
   }

   @Nullable
   public Packet<ClientGamePacketListener> getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public static void tickClient(Level world, BlockPos pos, BlockState state, ArtifactProjectorTileEntity tile) {
      if (tile.consuming) {
         tile.ticksToConsume--;
         if (tile.ticksToConsume >= 40) {
            Random rand = new Random();

            for (int i = 0; i < 5; i++) {
               world.addParticle(
                  new ArtifactProjectorParticleOptions(
                     (ParticleType<ArtifactProjectorParticleOptions>)ModParticles.ARTIFACT_PROJECTOR.get(),
                     40,
                     ((Direction)state.getValue(HorizontalDirectionalBlock.FACING)).toYRot()
                  ),
                  true,
                  pos.above(3).getX() + 0.5,
                  pos.above(3).getY() + 0.5,
                  pos.above(3).getZ() + 0.5,
                  rand.nextFloat() * 3.0F,
                  rand.nextInt(360),
                  rand.nextFloat() + 0.5F
               );
            }
         }

         if (tile.ticksToConsume <= 0) {
            tile.consuming = false;
         }
      }

      tile.oOpen = tile.open;
      tile.oRot = tile.rot;
      Player player = world.getNearestPlayer(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 6.0, false);
      if (player != null) {
         double d0 = player.getX() - (pos.getX() + 0.5);
         double d1 = player.getZ() - (pos.getZ() + 0.5);
         tile.tRot = (float)Mth.atan2(d1, d0);
         tile.open += 0.1F;
         if (tile.open < 0.5F || RANDOM.nextInt(40) == 0) {
            float f1 = tile.flipT;

            do {
               tile.flipT = tile.flipT + (RANDOM.nextInt(4) - RANDOM.nextInt(4));
            } while (f1 == tile.flipT);
         }
      } else {
         tile.tRot += 0.02F;
         tile.open -= 0.1F;
      }

      while (tile.rot >= (float) Math.PI) {
         tile.rot -= (float) (Math.PI * 2);
      }

      while (tile.rot < (float) -Math.PI) {
         tile.rot += (float) (Math.PI * 2);
      }

      while (tile.tRot >= (float) Math.PI) {
         tile.tRot -= (float) (Math.PI * 2);
      }

      while (tile.tRot < (float) -Math.PI) {
         tile.tRot += (float) (Math.PI * 2);
      }

      float f2 = tile.tRot - tile.rot;

      while (f2 >= (float) Math.PI) {
         f2 -= (float) (Math.PI * 2);
      }

      while (f2 < (float) -Math.PI) {
         f2 += (float) (Math.PI * 2);
      }

      tile.rot += f2 * 0.4F;
      tile.open = Mth.clamp(tile.open, 0.0F, 1.1F);
      tile.time++;
      tile.oFlip = tile.flip;
      float f = (tile.flipT - tile.flip) * 0.4F;
      float f3 = 0.2F;
      f = Mth.clamp(f, -0.2F, 0.2F);
      tile.flipA = tile.flipA + (f - tile.flipA) * 0.9F;
      tile.flip = tile.flip + tile.flipA;
   }

   public static void tickServer(Level sWorld, BlockPos pPos, BlockState state, ArtifactProjectorTileEntity tile) {
      if (tile.consuming) {
         tile.ticksToConsume--;
         if (tile.ticksToConsume <= 0) {
            ItemStack seal = new ItemStack(ModItems.CRYSTAL_SEAL_HERALD);
            if (!sWorld.isClientSide && sWorld.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS) && !sWorld.restoringBlockSnapshots) {
               BlockPos pos = pPos.above(3);
               ItemEntity itementity = new ItemEntity(sWorld, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, seal);
               itementity.setDeltaMovement(new Vec3(0.0, 0.0, 0.0));
               itementity.setDefaultPickUpDelay();
               sWorld.addFreshEntity(itementity);
            }

            sWorld.playSound(null, tile.worldPosition.above(3), SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.6F, 0.9F);
            tile.consuming = false;
         }
      }
   }
}
