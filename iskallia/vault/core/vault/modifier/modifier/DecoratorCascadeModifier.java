package iskallia.vault.core.vault.modifier.modifier;

import com.google.gson.annotations.Expose;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.random.ChunkRandom;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.modifier.reputation.ScalarReputationProperty;
import iskallia.vault.core.vault.modifier.spi.ModifierContext;
import iskallia.vault.core.vault.modifier.spi.VaultModifier;
import iskallia.vault.core.world.data.PartialCompoundNbt;
import iskallia.vault.core.world.data.tile.PartialBlockState;
import iskallia.vault.core.world.data.tile.PartialTile;
import iskallia.vault.core.world.data.tile.TilePredicate;
import iskallia.vault.core.world.storage.VirtualWorld;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class DecoratorCascadeModifier extends VaultModifier<DecoratorCascadeModifier.Properties> {
   public DecoratorCascadeModifier(ResourceLocation id, DecoratorCascadeModifier.Properties properties, VaultModifier.Display display) {
      super(id, properties, display);
      this.setDescriptionFormatter((t, p, s) -> t.formatted((int)Math.abs(p.getChance() * s * 100.0F)));
   }

   @Override
   public void initServer(VirtualWorld world, Vault vault, ModifierContext context) {
      CommonEvents.SURFACE_GENERATION.in(world).register(context.getUUID(), data -> {
         List<PartialTile> tiles = new ArrayList<>();
         List<CompoundTag> pending = new ArrayList<>();
         data.getChunk().getBlockEntitiesPos().forEach(pos -> {
            BlockState state = data.getGenRegion().getBlockState(pos);
            BlockEntity entity = data.getGenRegion().getBlockEntity(pos);
            CompoundTag rawx = data.getChunk().getBlockEntityNbt(pos);
            if (rawx != null && !rawx.getBoolean("cascade_duped")) {
               PartialTile tilex = PartialTile.of(PartialBlockState.of(state), PartialCompoundNbt.of(entity), pos);
               if (this.properties.filter.test(tilex)) {
                  tiles.add(tilex);
                  pending.add(rawx);
               }
            }
         });
         ChunkRandom random = ChunkRandom.any();

         for (int i = 0; i < tiles.size(); i++) {
            PartialTile tile = tiles.get(i).copy();
            CompoundTag raw = pending.get(i);
            if (raw.contains("cascade_seed", 4)) {
               random.setSeed(raw.getLong("cascade_seed"));
            } else {
               random.setBlockSeed(vault.get(Vault.SEED), tile.getPos(), 237429473);
            }

            for (float p = this.properties.chance; p > 0.0F && random.nextFloat() < p; p--) {
               BlockPos result = this.getValidPosition(data.getGenRegion(), tile.getPos(), data.getChunk().getPos(), random);
               if (result != null) {
                  BlockState current = data.getGenRegion().getBlockState(result);
                  if (current.isAir()) {
                     tile.getState().set(BlockStateProperties.WATERLOGGED, false);
                  } else if (current.getBlock() instanceof LiquidBlock) {
                     tile.getState().set(BlockStateProperties.WATERLOGGED, true);
                  }

                  tile.place(data.getGenRegion(), result, 3);
                  CompoundTag rawResult = data.getChunk().getBlockEntityNbt(result);
                  if (rawResult != null) {
                     rawResult.putBoolean("cascade_duped", true);
                  }
               }
            }

            raw.putLong("cascade_seed", random.getSeed());
         }
      }, -100);
   }

   public BlockPos getValidPosition(WorldGenRegion world, BlockPos origin, ChunkPos chunkPos, RandomSource random) {
      int index = 0;
      BlockPos result = null;

      for (int y = origin.getY() - 3; y <= origin.getY() + 3; y++) {
         for (int x = origin.getX() - 3; x <= origin.getX() + 3; x++) {
            for (int z = origin.getZ() - 3; z <= origin.getZ() + 3; z++) {
               if (x >> 4 == chunkPos.x && z >> 4 == chunkPos.z) {
                  BlockPos pos = new BlockPos(x, y, z);
                  BlockState state = world.getBlockState(pos);
                  if (world.getBlockState(pos.below()).isFaceSturdy(world, pos, Direction.UP) && (state.isAir() || state.getBlock() instanceof LiquidBlock)) {
                     if (random.nextInt(++index) == 0) {
                        result = pos;
                     }
                  }
               }
            }
         }
      }

      return result;
   }

   @Override
   public void releaseServer(ModifierContext context) {
      CommonEvents.SURFACE_GENERATION.release(context.getUUID());
   }

   public static class Properties {
      @Expose
      private final TilePredicate filter;
      @Expose
      private final float chance;
      @Expose
      private final ScalarReputationProperty reputation;

      public Properties(TilePredicate filter, float chance, ScalarReputationProperty reputation) {
         this.filter = filter;
         this.chance = chance;
         this.reputation = reputation;
      }

      public TilePredicate getFilter() {
         return this.filter;
      }

      public float getChance() {
         return this.chance;
      }

      public double getChance(ModifierContext context) {
         return this.reputation != null ? this.reputation.apply(this.chance, context) : this.chance;
      }
   }
}
