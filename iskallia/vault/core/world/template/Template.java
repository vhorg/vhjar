package iskallia.vault.core.world.template;

import iskallia.vault.VaultMod;
import iskallia.vault.core.data.key.Keyed;
import iskallia.vault.core.world.data.entity.EntityPredicate;
import iskallia.vault.core.world.data.entity.PartialEntity;
import iskallia.vault.core.world.data.tile.PartialTile;
import iskallia.vault.core.world.data.tile.TilePredicate;
import iskallia.vault.core.world.generator.JigsawData;
import iskallia.vault.core.world.template.configured.ConfiguredTemplate;
import iskallia.vault.init.ModBlocks;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Clearable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.CommandBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.fml.loading.FMLEnvironment;

public abstract class Template extends Keyed<Template> {
   public static final TilePredicate ALL_TILES = (state, nbt) -> true;
   public static final TilePredicate JIGSAWS = TilePredicate.of(Blocks.JIGSAW);
   public static final TilePredicate PLACEHOLDERS = TilePredicate.of(ModBlocks.PLACEHOLDER);
   public static final TilePredicate VAULT_PORTALS = (state, nbt) -> {
      Block block = state.getBlock().asWhole().orElse(null);
      return block == ModBlocks.VAULT_PORTAL
         ? true
         : block == Blocks.JIGSAW && new JigsawData(PartialTile.of(state, nbt)).getFinalState().getBlock() == ModBlocks.VAULT_PORTAL;
   };
   public static final EntityPredicate ALL_ENTITIES = (pos, blockPos, nbt) -> true;
   protected static final Direction[] FLOWING_DIRECTIONS = new Direction[]{Direction.UP, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
   protected static final Template.TilePlacementResult EMPTY_TILE_RESULT = new Template.TilePlacementResult(
      new ArrayList<>(), new ArrayList<>(), new ArrayList<>()
   );

   public abstract Iterator<ResourceLocation> getTags();

   public abstract void addTag(ResourceLocation var1);

   public abstract boolean hasTag(ResourceLocation var1);

   public Iterator<PartialTile> getTiles(PlacementSettings settings) {
      return this.getTiles(ALL_TILES, settings);
   }

   public Iterator<PartialEntity> getEntities(PlacementSettings settings) {
      return this.getEntities(ALL_ENTITIES, settings);
   }

   public Iterator<PartialTile> getTiles(TilePredicate filter) {
      return this.getTiles(filter, PlacementSettings.EMPTY);
   }

   public Iterator<PartialEntity> getEntities(EntityPredicate filter) {
      return this.getEntities(filter, PlacementSettings.EMPTY);
   }

   public abstract Iterator<PartialTile> getTiles(TilePredicate var1, PlacementSettings var2);

   public abstract Iterator<PartialEntity> getEntities(EntityPredicate var1, PlacementSettings var2);

   public <T extends ConfiguredTemplate> T configure(ConfiguredTemplate.Factory<T> factory, PlacementSettings settings) {
      return factory.create(this, settings);
   }

   public void place(ServerLevelAccessor world, PlacementSettings settings) {
      Template.TilePlacementResult result = this.placeTiles(world, settings);
      this.fixFluidPlacement(world, result);
      this.updateBlockEntities(world, result);
      this.placeEntities(world, settings);
   }

   protected Template.TilePlacementResult placeTiles(ServerLevelAccessor world, PlacementSettings settings) {
      if (settings.doIgnoreTiles()) {
         return EMPTY_TILE_RESULT;
      } else {
         Template.TilePlacementResult result = new Template.TilePlacementResult(
            new ArrayList<>(1024), new ArrayList<>(settings.doKeepFluids() ? 4096 : 0), new ArrayList<>(settings.doKeepFluids() ? 4096 : 0)
         );
         this.getTiles(settings)
            .forEachRemaining(
               tile -> {
                  FluidState fluidPresent = settings.doKeepFluids() ? world.getFluidState(tile.getPos()) : null;
                  BlockState state = tile.getState()
                     .asWhole()
                     .orElseGet(
                        () -> {
                           if (FMLEnvironment.production) {
                              VaultMod.LOGGER
                                 .error(
                                    "Could not resolve tile '%s' at (%d, %d, %d)"
                                       .formatted(tile.toString(), tile.getPos().getX(), tile.getPos().getY(), tile.getPos().getZ())
                                 );
                           }

                           return ModBlocks.ERROR_BLOCK.defaultBlockState();
                        }
                     );
                  if (tile.getEntity().asWhole().isPresent()) {
                     BlockEntity blockentity = world.getBlockEntity(tile.getPos());
                     Clearable.tryClear(blockentity);
                     world.setBlock(tile.getPos(), Blocks.BARRIER.defaultBlockState(), 20);
                  }

                  if (world.setBlock(tile.getPos(), state, settings.getFlags())) {
                     if (fluidPresent != null) {
                        if (state.getFluidState().isSource()) {
                           result.sourcePositions.add(tile.getPos());
                        } else if (state.getBlock() instanceof LiquidBlockContainer container) {
                           container.placeLiquid(world, tile.getPos(), state, fluidPresent);
                           if (!fluidPresent.isSource()) {
                              result.flowingPositions.add(tile.getPos());
                           }
                        }
                     }

                     tile.getEntity().asWhole().ifPresent(nbt -> {
                        result.placedBlockEntities.add(tile);
                        BlockEntity blockEntity = world.getBlockEntity(tile.getPos());
                        if (blockEntity != null) {
                           blockEntity.load(nbt);
                        }

                        if (blockEntity instanceof CommandBlockEntity) {
                           world.scheduleTick(tile.getPos(), state.getBlock(), 1);
                        }
                     });
                  }
               }
            );
         return result;
      }
   }

   protected void fixFluidPlacement(ServerLevelAccessor world, Template.TilePlacementResult result) {
      boolean fluidPlaced = true;

      while (fluidPlaced && !result.flowingPositions.isEmpty()) {
         fluidPlaced = false;
         Iterator<BlockPos> iterator = result.flowingPositions.iterator();

         while (iterator.hasNext()) {
            BlockPos flowingPos = iterator.next();
            FluidState current = world.getFluidState(flowingPos);

            for (int i = 0; i < FLOWING_DIRECTIONS.length && !current.isSource(); i++) {
               BlockPos neighborPos = flowingPos.relative(FLOWING_DIRECTIONS[i]);
               FluidState neighborFluid = world.getFluidState(neighborPos);
               if (neighborFluid.isSource() && !result.sourcePositions.contains(neighborPos)) {
                  current = neighborFluid;
               }
            }

            if (current.isSource()) {
               BlockState state = world.getBlockState(flowingPos);
               Block block = state.getBlock();
               if (block instanceof LiquidBlockContainer) {
                  ((LiquidBlockContainer)block).placeLiquid(world, flowingPos, state, current);
                  fluidPlaced = true;
                  iterator.remove();
               }
            }
         }
      }
   }

   protected void updateBlockEntities(ServerLevelAccessor world, Template.TilePlacementResult result) {
      for (PartialTile tile : result.placedBlockEntities) {
         BlockEntity blockEntity = world.getBlockEntity(tile.getPos());
         if (blockEntity != null) {
            blockEntity.setChanged();
         }
      }
   }

   protected void placeEntities(ServerLevelAccessor world, PlacementSettings settings) {
      if (!settings.doIgnoreEntities()) {
         this.getEntities(settings).forEachRemaining(entity -> {
            CompoundTag nbt = entity.getNbt().asWhole().<CompoundTag>map(CompoundTag::copy).orElseGet(CompoundTag::new);
            ListTag posNBT = new ListTag();
            posNBT.add(DoubleTag.valueOf(entity.getPos().x));
            posNBT.add(DoubleTag.valueOf(entity.getPos().y));
            posNBT.add(DoubleTag.valueOf(entity.getPos().z));
            nbt.put("Pos", posNBT);
            nbt.remove("UUID");

            Entity spawned;
            try {
               spawned = (Entity)EntityType.create(nbt, world.getLevel()).orElse(null);
               if (spawned == null) {
                  return;
               }
            } catch (Exception var7) {
               return;
            }

            spawned.moveTo(entity.getPos().x, entity.getPos().y, entity.getPos().z, spawned.getYRot(), spawned.getXRot());
            if (settings.doFinalizeEntities() && spawned instanceof Mob) {
               ((Mob)spawned).finalizeSpawn(world, world.getCurrentDifficultyAt(new BlockPos(entity.getPos())), settings.getMobSpawnType(), null, nbt);
            }

            world.addFreshEntityWithPassengers(spawned);
         });
      }
   }

   public static class TilePlacementResult {
      public final List<PartialTile> placedBlockEntities;
      public final List<BlockPos> flowingPositions;
      public final List<BlockPos> sourcePositions;

      public TilePlacementResult(List<PartialTile> placedBlockEntities, List<BlockPos> flowingPositions, List<BlockPos> sourcePositions) {
         this.placedBlockEntities = placedBlockEntities;
         this.flowingPositions = flowingPositions;
         this.sourcePositions = sourcePositions;
      }
   }
}
