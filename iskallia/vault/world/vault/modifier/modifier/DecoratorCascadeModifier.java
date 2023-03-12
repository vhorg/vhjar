package iskallia.vault.world.vault.modifier.modifier;

import com.google.gson.annotations.Expose;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.random.ChunkRandom;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.world.data.PartialNBT;
import iskallia.vault.core.world.data.PartialTile;
import iskallia.vault.core.world.data.TilePredicate;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.world.vault.modifier.reputation.ScalarReputationProperty;
import iskallia.vault.world.vault.modifier.spi.ModifierContext;
import iskallia.vault.world.vault.modifier.spi.VaultModifier;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class DecoratorCascadeModifier extends VaultModifier<DecoratorCascadeModifier.Properties> {
   public DecoratorCascadeModifier(ResourceLocation id, DecoratorCascadeModifier.Properties properties, VaultModifier.Display display) {
      super(id, properties, display);
      this.setDescriptionFormatter((t, p, s) -> t.formatted((int)Math.abs(p.getChance() * s * 100.0F)));
   }

   @Override
   public void initServer(VirtualWorld world, Vault vault, ModifierContext context) {
      CommonEvents.SURFACE_GENERATION.in(world).register(context.getUUID(), data -> {
         TilePredicate filter = TilePredicate.of(this.properties.filter);
         List<PartialTile> tiles = new ArrayList<>();
         List<CompoundTag> pending = new ArrayList<>();
         data.getChunk().getBlockEntitiesPos().forEach(pos -> {
            BlockState state = data.getGenRegion().getBlockState(pos);
            BlockEntity entity = data.getGenRegion().getBlockEntity(pos);
            CompoundTag rawx = data.getChunk().getBlockEntityNbt(pos);
            if (rawx != null) {
               PartialTile tilex = PartialTile.of(state, (CompoundTag)(entity == null ? PartialNBT.empty() : entity.serializeNBT())).setPos(pos);
               if (!rawx.getBoolean("cascade_duped")) {
                  if (filter.test(tilex)) {
                     tiles.add(tilex);
                     pending.add(rawx);
                  }
               }
            }
         });
         ChunkRandom random = ChunkRandom.any();

         for (int i = 0; i < tiles.size(); i++) {
            PartialTile tile = tiles.get(i);
            CompoundTag raw = pending.get(i);
            if (raw.contains("cascade_seed", 4)) {
               random.setSeed(raw.getLong("cascade_seed"));
            } else {
               random.setBlockSeed(vault.get(Vault.SEED), tile.getPos(), 237429473);
            }

            for (float p = this.properties.chance; p > 0.0F && random.nextFloat() < p; p--) {
               BlockPos result = this.getValidPosition(data.getGenRegion(), tile.getPos(), data.getChunk().getPos(), random);
               if (result != null) {
                  data.getGenRegion().setBlock(result, tile.getState().asBlockState(), 3);
                  if (tile.getNbt() != null) {
                     BlockEntity blockEntity = data.getGenRegion().getBlockEntity(result);
                     if (blockEntity != null) {
                        blockEntity.load(tile.getNbt());
                        CompoundTag rawResult = data.getChunk().getBlockEntityNbt(result);
                        if (rawResult != null) {
                           rawResult.putBoolean("cascade_duped", true);
                        }
                     }
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
                  if (state.isAir() && world.getBlockState(pos.below()).isFaceSturdy(world, pos, Direction.UP)) {
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
      private final String filter;
      @Expose
      private final float chance;
      @Expose
      private final ScalarReputationProperty reputation;

      public Properties(String filter, float chance, ScalarReputationProperty reputation) {
         this.filter = filter;
         this.chance = chance;
         this.reputation = reputation;
      }

      public String getFilter() {
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
