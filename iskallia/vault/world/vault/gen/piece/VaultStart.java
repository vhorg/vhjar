package iskallia.vault.world.vault.gen.piece;

import iskallia.vault.Vault;
import iskallia.vault.block.entity.HourglassTileEntity;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.logic.objective.TreasureHuntObjective;
import java.util.Optional;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.server.ServerWorld;

public class VaultStart extends VaultPiece {
   public static final ResourceLocation ID = Vault.id("start");

   public VaultStart() {
      super(ID);
   }

   public VaultStart(ResourceLocation template, MutableBoundingBox boundingBox, Rotation rotation) {
      super(ID, template, boundingBox, rotation);
   }

   @Override
   public void tick(ServerWorld world, VaultRaid vault) {
      vault.getActiveObjective(TreasureHuntObjective.class).ifPresent(treasureHunt -> {
         Optional<BlockPos> opt = vault.getProperties().getBase(VaultRaid.START_POS);
         Direction facing = vault.getProperties().getBaseOrDefault(VaultRaid.START_FACING, Direction.NORTH);
         opt.ifPresent(pos -> {
            pos = pos.func_177967_a(facing, 1).func_177967_a(facing.func_176746_e(), 8).func_177967_a(Direction.DOWN, 1);
            if (world.func_180495_p(pos).func_177230_c() != ModBlocks.HOURGLASS) {
               world.func_180501_a(pos, ModBlocks.HOURGLASS.func_176223_P(), 3);
            }

            TileEntity te = world.func_175625_s(pos);
            if (te instanceof HourglassTileEntity) {
               int totalSand = vault.getGenerator().getPieces(VaultRoom.class).size() * ModConfigs.TREASURE_HUNT.sandPerRoom;
               ((HourglassTileEntity)te).setTotalSand(totalSand);
            }
         });
      });
   }
}
