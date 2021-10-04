package iskallia.vault.block.entity;

import iskallia.vault.block.VaultLootableBlock;
import iskallia.vault.block.VaultOreBlock;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.world.data.VaultRaidData;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.logic.objective.TroveObjective;
import iskallia.vault.world.vault.logic.objective.VaultObjective;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nonnull;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

public class VaultLootableTileEntity extends TileEntity implements ITickableTileEntity {
   private VaultLootableBlock.Type type;

   public VaultLootableTileEntity() {
      super(ModBlocks.VAULT_LOOTABLE_TILE_ENTITY);
   }

   public VaultLootableTileEntity setType(VaultLootableBlock.Type type) {
      this.type = type;
      return this;
   }

   public void func_73660_a() {
      if (this.type != null && this.func_145831_w() != null && !this.func_145831_w().func_201670_d()) {
         ServerWorld world = (ServerWorld)this.func_145831_w();
         BlockState state = world.func_180495_p(this.func_174877_v());
         if (state.func_177230_c() instanceof VaultLootableBlock) {
            VaultRaid vault = VaultRaidData.get(world).getAt(world, this.func_174877_v());
            if (vault == null) {
               return;
            }

            VaultLootableBlock.GeneratedBlockState placingState = vault.getProperties()
               .getBase(VaultRaid.HOST)
               .map(hostUUID -> this.type.generateBlock(world, this.func_174877_v(), world.func_201674_k(), hostUUID))
               .orElse(new VaultLootableBlock.GeneratedBlockState(Blocks.field_150350_a.func_176223_P()));
            if (world.func_175656_a(this.func_174877_v(), placingState.getState())) {
               placingState.getPostProcessor().accept(world, this.func_174877_v());
            }
         }
      }
   }

   public void func_230337_a_(BlockState state, CompoundNBT nbt) {
      super.func_230337_a_(state, nbt);
      if (nbt.func_150297_b("Type", 3)) {
         this.type = VaultLootableBlock.Type.values()[nbt.func_74762_e("Type")];
      }
   }

   public CompoundNBT func_189515_b(CompoundNBT compound) {
      CompoundNBT nbt = super.func_189515_b(compound);
      if (this.type != null) {
         nbt.func_74768_a("Type", this.type.ordinal());
      }

      return nbt;
   }

   public interface ExtendedGenerator extends VaultLootableTileEntity.Generator {
      void postProcess(ServerWorld var1, BlockPos var2);
   }

   public interface Generator {
      @Nonnull
      BlockState generate(ServerWorld var1, BlockPos var2, Random var3, String var4, UUID var5);
   }

   public static class VaultOreBlockGenerator implements VaultLootableTileEntity.Generator {
      @Nonnull
      @Override
      public BlockState generate(ServerWorld world, BlockPos pos, Random random, String poolName, UUID playerUUID) {
         VaultRaid vault = VaultRaidData.get(world).getAt(world, pos);
         if (vault == null) {
            return Blocks.field_150350_a.func_176223_P();
         } else {
            VaultObjective objective = vault.getActiveObjective(TroveObjective.class).orElse(null);
            if (objective == null) {
               return ModConfigs.VAULT_LOOTABLES.ORE.get(world, pos, random, poolName, playerUUID);
            } else {
               BlockState generatedBlock;
               do {
                  generatedBlock = ModConfigs.VAULT_LOOTABLES.ORE.get(world, pos, random, poolName, playerUUID);
               } while (!(generatedBlock.func_177230_c() instanceof VaultOreBlock));

               return generatedBlock;
            }
         }
      }
   }
}
