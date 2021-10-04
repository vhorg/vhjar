package iskallia.vault.world.vault.logic.objective.architect.processor;

import iskallia.vault.util.MiscUtils;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.gen.piece.VaultObelisk;
import iskallia.vault.world.vault.gen.piece.VaultPiece;
import iskallia.vault.world.vault.logic.VaultSpawner;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

public class VaultSpawnerSpawningPostProcessor extends VaultPieceProcessor {
   private final int blocksPerSpawn;

   public VaultSpawnerSpawningPostProcessor(int blocksPerSpawn) {
      this.blocksPerSpawn = blocksPerSpawn;
   }

   @Override
   public void postProcess(VaultRaid vault, ServerWorld world, VaultPiece piece, Direction generatedDirection) {
      if (!(piece instanceof VaultObelisk)) {
         vault.getProperties().getBase(VaultRaid.LEVEL).ifPresent(vaultLevel -> {
            AxisAlignedBB box = AxisAlignedBB.func_216363_a(piece.getBoundingBox());
            float size = (float)((box.field_72336_d - box.field_72340_a) * (box.field_72337_e - box.field_72338_b) * (box.field_72334_f - box.field_72339_c));
            float runs = size / this.blocksPerSpawn;

            while (runs > 0.0F && (!(runs < 1.0F) || !(rand.nextFloat() >= runs))) {
               runs--;
               LivingEntity spawned = null;

               while (spawned == null) {
                  BlockPos pos = MiscUtils.getRandomPos(box, rand);
                  spawned = VaultSpawner.spawnMob(vault, world, vaultLevel, pos.func_177958_n(), pos.func_177956_o(), pos.func_177952_p(), rand);
               }
            }
         });
      }
   }
}
