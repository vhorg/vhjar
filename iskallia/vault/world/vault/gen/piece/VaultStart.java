package iskallia.vault.world.vault.gen.piece;

import iskallia.vault.VaultMod;
import iskallia.vault.block.entity.HourglassTileEntity;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.logic.objective.TreasureHuntObjective;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class VaultStart extends VaultPiece {
   public static final ResourceLocation ID = VaultMod.id("start");

   public VaultStart() {
      super(ID);
   }

   public VaultStart(ResourceLocation template, BoundingBox boundingBox, Rotation rotation) {
      super(ID, template, boundingBox, rotation);
   }

   @Override
   public void tick(ServerLevel world, VaultRaid vault) {
      vault.getActiveObjective(TreasureHuntObjective.class).ifPresent(treasureHunt -> {
         Optional<BlockPos> opt = vault.getProperties().getBase(VaultRaid.START_POS);
         Direction facing = vault.getProperties().getBaseOrDefault(VaultRaid.START_FACING, Direction.NORTH);
         opt.ifPresent(pos -> {
            pos = pos.relative(facing, 1).relative(facing.getClockWise(), 8).relative(Direction.DOWN, 1);
            if (world.getBlockState(pos).getBlock() != ModBlocks.HOURGLASS) {
               world.setBlock(pos, ModBlocks.HOURGLASS.defaultBlockState(), 3);
            }

            BlockEntity te = world.getBlockEntity(pos);
            if (te instanceof HourglassTileEntity) {
               int totalSand = vault.getGenerator().getPieces(VaultRoom.class).size() * ModConfigs.TREASURE_HUNT.sandPerRoom;
               ((HourglassTileEntity)te).setTotalSand(totalSand);
            }
         });
      });
   }
}
