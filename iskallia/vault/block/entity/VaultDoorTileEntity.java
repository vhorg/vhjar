package iskallia.vault.block.entity;

import iskallia.vault.block.UnknownVaultDoorBlock;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.world.data.VaultRaidData;
import iskallia.vault.world.vault.VaultRaid;
import java.util.UUID;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoorBlock;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

public class VaultDoorTileEntity extends TileEntity implements ITickableTileEntity {
   public VaultDoorTileEntity() {
      super(ModBlocks.VAULT_DOOR_TILE_ENTITY);
   }

   public void func_73660_a() {
      if (this.func_145831_w() != null && !this.func_145831_w().field_72995_K) {
         ServerWorld world = (ServerWorld)this.func_145831_w();
         BlockState state = world.func_180495_p(this.func_174877_v());
         if (state.func_177230_c() instanceof UnknownVaultDoorBlock && state.func_177229_b(DoorBlock.field_176523_O) == DoubleBlockHalf.LOWER) {
            VaultRaid vault = VaultRaidData.get(world).getAt(world, this.func_174877_v());
            if (vault == null) {
               return;
            }

            UUID hostUUID = vault.getProperties().getBase(VaultRaid.HOST).orElse(null);
            BlockState newBlock = ModConfigs.VAULT_LOOTABLES.DOOR.get(world, this.func_174877_v(), world.func_201674_k(), "DOOR", hostUUID);
            if (newBlock.func_177230_c() instanceof DoorBlock) {
               BlockState newState = (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)newBlock.func_206870_a(
                              DoorBlock.field_176520_a, state.func_177229_b(DoorBlock.field_176520_a)
                           ))
                           .func_206870_a(DoorBlock.field_176519_b, state.func_177229_b(DoorBlock.field_176519_b)))
                        .func_206870_a(DoorBlock.field_176521_M, state.func_177229_b(DoorBlock.field_176521_M)))
                     .func_206870_a(DoorBlock.field_176522_N, state.func_177229_b(DoorBlock.field_176522_N)))
                  .func_206870_a(DoorBlock.field_176523_O, state.func_177229_b(DoorBlock.field_176523_O));
               world.func_180501_a(this.func_174877_v().func_177984_a(), Blocks.field_150350_a.func_176223_P(), 27);
               world.func_180501_a(this.func_174877_v(), newState, 11);
               world.func_180501_a(
                  this.func_174877_v().func_177984_a(), (BlockState)newState.func_206870_a(DoorBlock.field_176523_O, DoubleBlockHalf.UPPER), 11
               );
            }

            boolean drilling = false;

            for (int i = 1; i < 32; i++) {
               BlockPos p = this.func_174877_v().func_177967_a(((Direction)state.func_177229_b(DoorBlock.field_176520_a)).func_176734_d(), i);
               if (this.func_145831_w().func_180495_p(p).func_196958_f() && this.func_145831_w().func_180495_p(p.func_177984_a()).func_196958_f()) {
                  if (drilling) {
                     break;
                  }
               } else if (!drilling) {
                  drilling = true;
               }

               this.func_145831_w().func_175656_a(p, Blocks.field_150350_a.func_176223_P());
               this.func_145831_w().func_175656_a(p.func_177984_a(), Blocks.field_150350_a.func_176223_P());
            }
         }
      }
   }
}
