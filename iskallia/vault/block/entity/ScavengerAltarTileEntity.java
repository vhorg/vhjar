package iskallia.vault.block.entity;

import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.init.ModParticles;
import iskallia.vault.network.message.ScavengerAltarConsumeMessage;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.PacketDistributor;

public class ScavengerAltarTileEntity extends BlockEntity {
   private static final Random rand = new Random();
   private ItemStack heldItem = ItemStack.EMPTY;
   private UUID itemPlacedBy = null;
   public static final int MAX_CONSUME_TICKS = 40;
   public int ticksToConsume = 40;
   public int ticksToConsumeOld;
   public boolean consuming = false;
   private static final BlockPos[] list = new BlockPos[]{
      BlockPos.ZERO.north(),
      BlockPos.ZERO.north().north(),
      BlockPos.ZERO.north().east(),
      BlockPos.ZERO.north().west(),
      BlockPos.ZERO.south(),
      BlockPos.ZERO.south().south(),
      BlockPos.ZERO.south().east(),
      BlockPos.ZERO.south().west(),
      BlockPos.ZERO.west(),
      BlockPos.ZERO.west().west(),
      BlockPos.ZERO.east(),
      BlockPos.ZERO.east().east()
   };

   protected ScavengerAltarTileEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
      super(typeIn, pos, state);
   }

   public ScavengerAltarTileEntity(BlockPos pos, BlockState state) {
      this(ModBlocks.SCAVENGER_ALTAR_TILE_ENTITY, pos, state);
   }

   public void setItemPlacedBy(UUID itemPlacedBy) {
      this.itemPlacedBy = itemPlacedBy;
   }

   public UUID getItemPlacedBy() {
      return this.itemPlacedBy;
   }

   public void setHeldItem(ItemStack heldItem) {
      this.heldItem = heldItem;
   }

   public ItemStack getHeldItem() {
      return this.heldItem;
   }

   public static void tickClient(Level world, BlockPos pos, BlockState state, ScavengerAltarTileEntity tile) {
      tile.playEffects(world);
      if (!tile.heldItem.isEmpty()) {
         tile.ticksToConsumeOld = tile.ticksToConsume;
         if (tile.ticksToConsume > 0) {
            tile.ticksToConsume--;
         }
      }
   }

   public static void tickServer(Level world, BlockPos pos, BlockState state, ScavengerAltarTileEntity tile) {
      if (!tile.heldItem.isEmpty()) {
         if (tile.ticksToConsume > 0) {
            if (!tile.consuming) {
               tile.consuming = true;
               world.playSound(null, pos, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 1.0F, 1.0F);
               tile.setChanged();
               world.sendBlockUpdated(pos, state, state, 3);
            }

            tile.ticksToConsume--;
         } else {
            tile.ticksToConsume = 40;
            tile.consuming = false;
            ItemStack original = tile.heldItem.copy();
            CommonEvents.SCAVENGER_ALTAR_CONSUME.invoke(world, tile);
            if (tile.heldItem.getCount() != original.getCount()) {
               world.playSound(null, pos, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.BLOCKS, 1.0F, 1.0F);
               world.playSound(null, pos, SoundEvents.PLAYER_BURP, SoundSource.BLOCKS, 1.0F, 1.0F);
               ModNetwork.CHANNEL.send(PacketDistributor.ALL.noArg(), new ScavengerAltarConsumeMessage(tile.getBlockPos()));
            } else {
               world.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1.0F, 2.0F);
            }

            Block.popResource(world, pos.above(), tile.heldItem);
            tile.setHeldItem(ItemStack.EMPTY);
            tile.setItemPlacedBy(null);
         }

         tile.setChanged();
         world.sendBlockUpdated(pos, state, state, 3);
      }
   }

   @OnlyIn(Dist.CLIENT)
   private void playEffects(Level level) {
      if (level.isClientSide) {
         BlockPos pos = this.getBlockPos().above();
         Vec3 vec3 = new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
         int particleSpeed = this.consuming && this.ticksToConsume > 32.0F ? 5 : 40;

         for (BlockPos blockpos : list) {
            if (rand.nextInt(particleSpeed) == 0) {
               float f = -0.5F + rand.nextFloat() + blockpos.getX();
               float f1 = -2.0F + rand.nextFloat() + blockpos.getY();
               float f2 = -0.5F + rand.nextFloat() + blockpos.getZ();
               level.addParticle((ParticleOptions)ModParticles.SCAVENGER_CORE.get(), vec3.x, vec3.y, vec3.z, f, f1, f2);
            }

            if (rand.nextInt(particleSpeed) == 0) {
               float f = -0.5F + rand.nextFloat() + blockpos.above().getX();
               float f1 = -2.0F + rand.nextFloat() + blockpos.above().getY();
               float f2 = -0.5F + rand.nextFloat() + blockpos.above().getZ();
               level.addParticle((ParticleOptions)ModParticles.SCAVENGER_CORE.get(), vec3.x, vec3.y, vec3.z, f, f1, f2);
            }

            if (rand.nextInt(particleSpeed) == 0) {
               float f = -0.5F + rand.nextFloat() + blockpos.above().above().getX();
               float f1 = -2.0F + rand.nextFloat() + blockpos.above().above().getY();
               float f2 = -0.5F + rand.nextFloat() + blockpos.above().above().getZ();
               level.addParticle((ParticleOptions)ModParticles.SCAVENGER_CORE.get(), vec3.x, vec3.y, vec3.z, f, f1, f2);
            }
         }

         if (rand.nextInt(10) == 0) {
            Vec3 rPos = new Vec3(
               pos.getX() + 0.5 + (rand.nextFloat() - rand.nextFloat()) * rand.nextFloat() * 3.0F / 10.0F,
               pos.getY() - 0.25 + (rand.nextFloat() - rand.nextFloat()) * rand.nextFloat() * 3.0F / 10.0F,
               pos.getZ() + 0.5 + (rand.nextFloat() - rand.nextFloat()) * rand.nextFloat() * 3.0F / 10.0F
            );
            level.addParticle((ParticleOptions)ModParticles.SCAVENGER_CORE.get(), rPos.x, rPos.y, rPos.z, 0.0, 0.0, 0.0);
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static void spawnConsumeParticles(BlockPos pos) {
      Level level = Minecraft.getInstance().level;
      if (level != null) {
         for (int i = 0; i < 40; i++) {
            Random random = level.getRandom();
            Vec3 offset = new Vec3(
               random.nextDouble() / 3.0 * (random.nextBoolean() ? 1 : -1), 0.0, random.nextDouble() / 3.0 * (random.nextBoolean() ? 1 : -1)
            );
            level.addParticle(
               (ParticleOptions)ModParticles.SCAVENGER_CORE_CONSUME.get(),
               true,
               pos.getX() + 0.5 + offset.x,
               pos.getY() + random.nextDouble() * 0.15F + 0.25,
               pos.getZ() + 0.5 + offset.z,
               offset.x / 2.0,
               random.nextDouble() * 0.1 + 0.1,
               offset.z / 2.0
            );
         }

         for (int i = 0; i < 30; i++) {
            Random random = level.getRandom();
            Vec3 offset = new Vec3(
               random.nextDouble() / 3.0 * (random.nextBoolean() ? 1 : -1), 0.0, random.nextDouble() / 3.0 * (random.nextBoolean() ? 1 : -1)
            );
            level.addParticle(
               (ParticleOptions)ModParticles.SCAVENGER_CORE_CONSUME.get(),
               true,
               pos.getX() + 0.5 + offset.x,
               pos.above().getY() + random.nextDouble() * 0.15F,
               pos.getZ() + 0.5 + offset.z,
               offset.x / 20.0,
               random.nextDouble() * 0.2 + 0.2,
               offset.z / 20.0
            );
         }
      }
   }

   public void sendUpdates() {
      this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
      this.level.updateNeighborsAt(this.worldPosition, this.getBlockState().getBlock());
      this.setChanged();
   }

   protected void saveAdditional(CompoundTag pTag) {
      super.saveAdditional(pTag);
      pTag.put("HeldItem", this.getHeldItem().save(new CompoundTag()));
      if (this.getItemPlacedBy() != null) {
         pTag.putUUID("ItemPlacedBy", this.getItemPlacedBy());
      }

      pTag.putInt("TicksToConsume", this.ticksToConsume);
      pTag.putBoolean("Consuming", this.consuming);
   }

   public void load(CompoundTag pTag) {
      super.load(pTag);
      this.heldItem = ItemStack.of(pTag.getCompound("HeldItem"));
      if (pTag.contains("ItemPlacedBy")) {
         this.itemPlacedBy = pTag.getUUID("ItemPlacedBy");
      }

      this.ticksToConsume = pTag.getInt("TicksToConsume");
      this.consuming = pTag.getBoolean("Consuming");
   }

   public CompoundTag getUpdateTag() {
      return this.saveWithoutMetadata();
   }

   @Nullable
   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }
}
