package iskallia.vault.block.entity;

import iskallia.vault.client.particles.GridGatewayParticle;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModParticles;
import java.awt.Color;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

public class GridGatewayTileEntity extends BlockEntity {
   private static final int GRAY_COLOR = -11184811;
   private static final int LIGHT_CHECK_INTERVAL = 20;
   private int lightCheckCooldown = 0;
   private int lastLight = 0;
   private int completedBingos = 0;

   public GridGatewayTileEntity(BlockPos pPos, BlockState pState) {
      super(ModBlocks.GRID_GATEWAY_TILE_ENTITY, pPos, pState);
   }

   public int getCompletedBingos() {
      return this.completedBingos;
   }

   public void setCompletedBingos(int completedBingos) {
      this.completedBingos = completedBingos;
      this.sendUpdates();
   }

   public void load(CompoundTag nbt) {
      super.load(nbt);
      this.completedBingos = nbt.getInt("completedBingos");
   }

   protected void saveAdditional(CompoundTag nbt) {
      super.saveAdditional(nbt);
      nbt.putInt("completedBingos", this.completedBingos);
   }

   public CompoundTag getUpdateTag() {
      return this.saveWithoutMetadata();
   }

   @Nullable
   public Packet<ClientGamePacketListener> getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public void sendUpdates() {
      if (this.level != null) {
         this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
         this.level.updateNeighborsAt(this.worldPosition, this.getBlockState().getBlock());
         this.setChanged();
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static void clientTick(Level pLevel, BlockPos pPos, BlockState pState, GridGatewayTileEntity pBlockEntity) {
      int completedBingos = pBlockEntity.completedBingos;
      if (completedBingos > 0) {
         addBottomSideParticles(pPos, Direction.EAST, pBlockEntity, completedBingos, 0);
         addBottomSideParticles(pPos, Direction.WEST, pBlockEntity, completedBingos, 1);
      }

      if (completedBingos > 1) {
         addBottomSideParticles(pPos, Direction.NORTH, pBlockEntity, completedBingos, 2);
         addBottomSideParticles(pPos, Direction.SOUTH, pBlockEntity, completedBingos, 3);
      }

      if (completedBingos > 2) {
         addSideParticles(pPos, Direction.EAST, pBlockEntity, completedBingos, 4, false);
      }

      if (completedBingos > 3) {
         addSideParticles(pPos, Direction.WEST, pBlockEntity, completedBingos, 5, false);
      }

      if (completedBingos > 4) {
         addSideParticles(pPos, Direction.NORTH, pBlockEntity, completedBingos, 6, false);
      }

      if (completedBingos > 5) {
         addSideParticles(pPos, Direction.SOUTH, pBlockEntity, completedBingos, 7, false);
      }

      if (completedBingos > 6) {
         addSideParticles(pPos, Direction.EAST, pBlockEntity, completedBingos, 8, true);
      }

      if (completedBingos > 7) {
         addSideParticles(pPos, Direction.WEST, pBlockEntity, completedBingos, 9, true);
      }

      if (completedBingos > 8) {
         addSideParticles(pPos, Direction.NORTH, pBlockEntity, completedBingos, 10, true);
      }

      if (completedBingos > 9) {
         addSideParticles(pPos, Direction.SOUTH, pBlockEntity, completedBingos, 11, true);
      }

      addTopParticles(pPos, pBlockEntity, completedBingos);
   }

   @OnlyIn(Dist.CLIENT)
   private static void addTopParticles(BlockPos pos, GridGatewayTileEntity be, int completedBingos) {
      Random rnd = be.level.getRandom();
      if (!(rnd.nextFloat() > 0.3F)) {
         for (int i = 0; i < completedBingos; i++) {
            double randomX = pos.getX() + 0.5 + rnd.nextDouble(0.3) - 0.15;
            double randomZ = pos.getZ() + 0.5 + rnd.nextDouble(0.3) - 0.15;
            double randomY = pos.getY() + 1 + rnd.nextDouble(0.1);
            if (Minecraft.getInstance()
               .particleEngine
               .createParticle(
                  (ParticleOptions)ModParticles.GRID_GATEWAY.get(),
                  randomX,
                  randomY,
                  randomZ,
                  0.04 * rnd.nextFloat() - 0.02,
                  0.05 * rnd.nextFloat(),
                  0.04 * rnd.nextFloat() - 0.02
               ) instanceof GridGatewayParticle gridGatewayParticle) {
               gridGatewayParticle.setColor(be.getTintColor(12, completedBingos));
            }
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   private static void addSideParticles(BlockPos pos, Direction direction, GridGatewayTileEntity be, int completedBingos, int tintIndex, boolean top) {
      Random rnd = be.level.getRandom();
      if (!(rnd.nextFloat() > 0.1F)) {
         double randomX = pos.getX() + 0.5 + (direction.getStepX() == 0 ? rnd.nextDouble(0.6) - 0.3 : direction.getStepX() * (0.25 + rnd.nextDouble(0.12)));
         double randomZ = pos.getZ() + 0.5 + (direction.getStepZ() == 0 ? rnd.nextDouble(0.6) - 0.3 : direction.getStepZ() * (0.25 + rnd.nextDouble(0.12)));
         double randomY = pos.getY() + (top ? 0.575 : 0.2) + rnd.nextDouble(0.35);
         addParticle(be, completedBingos, tintIndex, randomX, randomY, randomZ);
      }
   }

   @OnlyIn(Dist.CLIENT)
   private static void addBottomSideParticles(BlockPos pos, Direction direction, GridGatewayTileEntity be, int completedBingos, int tintIndex) {
      Random rnd = be.level.getRandom();
      if (!(rnd.nextFloat() > 0.1F)) {
         double randomX = pos.getX() + 0.5 + (direction.getStepX() == 0 ? rnd.nextDouble(0.7) - 0.35 : direction.getStepX() * (0.4 + rnd.nextDouble(0.1)));
         double randomZ = pos.getZ() + 0.5 + (direction.getStepZ() == 0 ? rnd.nextDouble(0.7) - 0.35 : direction.getStepZ() * (0.4 + rnd.nextDouble(0.1)));
         double randomY = pos.getY() + 0.1 + rnd.nextDouble(0.1);
         addParticle(be, completedBingos, tintIndex, randomX, randomY, randomZ);
      }
   }

   @OnlyIn(Dist.CLIENT)
   private static void addParticle(GridGatewayTileEntity be, int completedBingos, int tintIndex, double randomX, double randomY, double randomZ) {
      if (Minecraft.getInstance().particleEngine.createParticle((ParticleOptions)ModParticles.GRID_GATEWAY.get(), randomX, randomY, randomZ, 0.0, 0.0, 0.0) instanceof GridGatewayParticle gridGatewayParticle
         )
       {
         gridGatewayParticle.setColor(be.getTintColor(tintIndex, completedBingos));
      }
   }

   public int getTintColor(int tintIndex, GridGatewayTileEntity tile) {
      return this.getTintColor(tintIndex, tile.completedBingos);
   }

   public int getTintColor(int tintIndex, int completedBingos) {
      if (completedBingos > 0 && tintIndex == 12) {
         float transition = (float)(System.currentTimeMillis() % 10000L) / 10000.0F;
         return Color.getHSBColor(transition, 1.0F, 0.7F + 0.3F * completedBingos / 10.0F).getRGB();
      } else if ((tintIndex != 0 && tintIndex != 1 || completedBingos >= 1)
         && (tintIndex != 2 && tintIndex != 3 || completedBingos >= 2)
         && (tintIndex <= 3 || tintIndex - 1 <= completedBingos)) {
         int colorShift = switch (tintIndex) {
            case 0, 1 -> 0;
            case 2, 3 -> 5;
            case 4 -> 1;
            case 5 -> 2;
            case 6 -> 6;
            case 7 -> 7;
            case 8 -> 9;
            case 9 -> 8;
            case 10 -> 4;
            case 11 -> 3;
            default -> 0;
         };
         float transition = (float)(System.currentTimeMillis() % 20000L + colorShift * 1000) / 20000.0F;
         return Color.getHSBColor(transition, 1.0F, 1.0F).getRGB();
      } else {
         return -11184811;
      }
   }

   public int getLight() {
      return (int)(this.completedBingos / 10.0F * 15.0F);
   }

   public static void serverTick(Level level, BlockPos blockPos, BlockState blockState, GridGatewayTileEntity gridGatewayTileEntity) {
      if (gridGatewayTileEntity.lightCheckCooldown++ >= 20) {
         gridGatewayTileEntity.lightCheckCooldown = 0;
         int light = gridGatewayTileEntity.getLight();
         if (gridGatewayTileEntity.lastLight != light) {
            gridGatewayTileEntity.lastLight = light;
            level.sendBlockUpdated(blockPos, blockState, blockState, 3);
         }
      }

      CommonEvents.GRID_GATEWAY_UPDATE.invoke(level, blockState, blockPos, gridGatewayTileEntity);
   }
}
