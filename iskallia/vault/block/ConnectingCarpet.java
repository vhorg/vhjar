package iskallia.vault.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CarpetBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;

public class ConnectingCarpet extends CarpetBlock {
   public static BooleanProperty WEST = BooleanProperty.create("west");
   public static BooleanProperty EAST = BooleanProperty.create("east");
   public static final EnumProperty<ConnectingCarpet.North> NORTH = EnumProperty.create("north", ConnectingCarpet.North.class);
   public static final EnumProperty<ConnectingCarpet.South> SOUTH = EnumProperty.create("south", ConnectingCarpet.South.class);

   public BlockState rotate(BlockState pState, Rotation pRot) {
      boolean east = (Boolean)pState.getValue(EAST);
      boolean west = (Boolean)pState.getValue(WEST);
      ConnectingCarpet.North northState = (ConnectingCarpet.North)pState.getValue(NORTH);
      ConnectingCarpet.South southState = (ConnectingCarpet.South)pState.getValue(SOUTH);
      boolean north = northState == ConnectingCarpet.North.ALL
         || northState == ConnectingCarpet.North.JUST_NORTH
         || northState == ConnectingCarpet.North.NORTH_AND_NORTH_EAST
         || northState == ConnectingCarpet.North.NORTH_AND_NORTH_WEST;
      boolean north_east = northState == ConnectingCarpet.North.ALL
         || northState == ConnectingCarpet.North.JUST_NORTH_EAST
         || northState == ConnectingCarpet.North.NORTH_AND_NORTH_EAST
         || northState == ConnectingCarpet.North.NORTH_EAST_AND_NORTH_WEST;
      boolean north_west = northState == ConnectingCarpet.North.ALL
         || northState == ConnectingCarpet.North.JUST_NORTH_WEST
         || northState == ConnectingCarpet.North.NORTH_AND_NORTH_WEST
         || northState == ConnectingCarpet.North.NORTH_EAST_AND_NORTH_WEST;
      boolean south = southState == ConnectingCarpet.South.ALL
         || southState == ConnectingCarpet.South.JUST_SOUTH
         || southState == ConnectingCarpet.South.SOUTH_AND_SOUTH_EAST
         || southState == ConnectingCarpet.South.SOUTH_AND_SOUTH_WEST;
      boolean south_east = southState == ConnectingCarpet.South.ALL
         || southState == ConnectingCarpet.South.JUST_SOUTH_EAST
         || southState == ConnectingCarpet.South.SOUTH_AND_SOUTH_EAST
         || southState == ConnectingCarpet.South.SOUTH_EAST_AND_SOUTH_WEST;
      boolean south_west = southState == ConnectingCarpet.South.ALL
         || southState == ConnectingCarpet.South.JUST_SOUTH_WEST
         || southState == ConnectingCarpet.South.SOUTH_AND_SOUTH_WEST
         || southState == ConnectingCarpet.South.SOUTH_EAST_AND_SOUTH_WEST;
      switch (pRot) {
         case NONE:
            return pState;
         case CLOCKWISE_90:
            ConnectingCarpet.North northTempxx = ConnectingCarpet.North.NONE;
            ConnectingCarpet.South southTempxx = ConnectingCarpet.South.NONE;
            if (south_east && east && north_east) {
               southTempxx = ConnectingCarpet.South.ALL;
            } else if (!south_east && east && north_east) {
               southTempxx = ConnectingCarpet.South.SOUTH_AND_SOUTH_EAST;
            } else if (south_east && east && !north_east) {
               southTempxx = ConnectingCarpet.South.SOUTH_AND_SOUTH_WEST;
            } else if (south_east && !east && north_east) {
               southTempxx = ConnectingCarpet.South.SOUTH_EAST_AND_SOUTH_WEST;
            } else if (!south_east && east && !north_east) {
               southTempxx = ConnectingCarpet.South.JUST_SOUTH;
            } else if (!south_east && !east && north_east) {
               southTempxx = ConnectingCarpet.South.JUST_SOUTH_EAST;
            } else if (south_east && !east && !north_east) {
               southTempxx = ConnectingCarpet.South.JUST_SOUTH_WEST;
            }

            if (south_west && west && north_west) {
               northTempxx = ConnectingCarpet.North.ALL;
            } else if (!south_west && west && north_west) {
               northTempxx = ConnectingCarpet.North.NORTH_AND_NORTH_EAST;
            } else if (south_west && west && !north_west) {
               northTempxx = ConnectingCarpet.North.NORTH_AND_NORTH_WEST;
            } else if (south_west && !west && north_west) {
               northTempxx = ConnectingCarpet.North.NORTH_EAST_AND_NORTH_WEST;
            } else if (!south_west && west && !north_west) {
               northTempxx = ConnectingCarpet.North.JUST_NORTH;
            } else if (!south_west && !west && north_west) {
               northTempxx = ConnectingCarpet.North.JUST_NORTH_EAST;
            } else if (south_west && !west && !north_west) {
               northTempxx = ConnectingCarpet.North.JUST_NORTH_WEST;
            }

            return (BlockState)((BlockState)((BlockState)((BlockState)pState.setValue(EAST, north)).setValue(WEST, south)).setValue(NORTH, northTempxx))
               .setValue(SOUTH, southTempxx);
         case CLOCKWISE_180:
            ConnectingCarpet.North northTempx = ConnectingCarpet.North.NONE;
            ConnectingCarpet.South southTempx = ConnectingCarpet.South.NONE;
            if (north && north_east && north_west) {
               southTempx = ConnectingCarpet.South.ALL;
            } else if (north && north_west && !north_east) {
               southTempx = ConnectingCarpet.South.SOUTH_AND_SOUTH_EAST;
            } else if (north && north_east && !north_west) {
               southTempx = ConnectingCarpet.South.SOUTH_AND_SOUTH_WEST;
            } else if (north_west && north_east && !north) {
               southTempx = ConnectingCarpet.South.SOUTH_EAST_AND_SOUTH_WEST;
            } else if (!north_west && !north_east && north) {
               southTempx = ConnectingCarpet.South.JUST_SOUTH;
            } else if (north_west && !north_east && !north) {
               southTempx = ConnectingCarpet.South.JUST_SOUTH_EAST;
            } else if (!north_west && north_east && !north) {
               southTempx = ConnectingCarpet.South.JUST_SOUTH_WEST;
            }

            if (south && south_east && south_west) {
               northTempx = ConnectingCarpet.North.ALL;
            } else if (south && south_west && !south_east) {
               northTempx = ConnectingCarpet.North.NORTH_AND_NORTH_EAST;
            } else if (south && south_east && !south_west) {
               northTempx = ConnectingCarpet.North.NORTH_AND_NORTH_WEST;
            } else if (south_west && south_east && !south) {
               northTempx = ConnectingCarpet.North.NORTH_EAST_AND_NORTH_WEST;
            } else if (!south_west && !south_east && south) {
               northTempx = ConnectingCarpet.North.JUST_NORTH;
            } else if (south_west && !south_east && !south) {
               northTempx = ConnectingCarpet.North.JUST_NORTH_EAST;
            } else if (!south_west && south_east && !south) {
               northTempx = ConnectingCarpet.North.JUST_NORTH_WEST;
            }

            return (BlockState)((BlockState)((BlockState)((BlockState)pState.setValue(EAST, west)).setValue(WEST, east)).setValue(NORTH, northTempx))
               .setValue(SOUTH, southTempx);
         case COUNTERCLOCKWISE_90:
            ConnectingCarpet.North northTemp = ConnectingCarpet.North.NONE;
            ConnectingCarpet.South southTemp = ConnectingCarpet.South.NONE;
            if (north_west && west && south_west) {
               southTemp = ConnectingCarpet.South.ALL;
            } else if (!north_west && west && south_west) {
               southTemp = ConnectingCarpet.South.SOUTH_AND_SOUTH_EAST;
            } else if (north_west && west && !south_west) {
               southTemp = ConnectingCarpet.South.SOUTH_AND_SOUTH_WEST;
            } else if (north_west && !west && south_west) {
               southTemp = ConnectingCarpet.South.SOUTH_EAST_AND_SOUTH_WEST;
            } else if (!north_west && west && !south_west) {
               southTemp = ConnectingCarpet.South.JUST_SOUTH;
            } else if (!north_west && !west && south_west) {
               southTemp = ConnectingCarpet.South.JUST_SOUTH_EAST;
            } else if (north_west && !west && !south_west) {
               southTemp = ConnectingCarpet.South.JUST_SOUTH_WEST;
            }

            if (north_east && east && south_east) {
               northTemp = ConnectingCarpet.North.ALL;
            } else if (!north_east && east && south_east) {
               northTemp = ConnectingCarpet.North.NORTH_AND_NORTH_EAST;
            } else if (north_east && east && !south_east) {
               northTemp = ConnectingCarpet.North.NORTH_AND_NORTH_WEST;
            } else if (north_east && !east && south_east) {
               northTemp = ConnectingCarpet.North.NORTH_EAST_AND_NORTH_WEST;
            } else if (!north_east && east && !south_east) {
               northTemp = ConnectingCarpet.North.JUST_NORTH;
            } else if (!north_east && !east && south_east) {
               northTemp = ConnectingCarpet.North.JUST_NORTH_EAST;
            } else if (north_east && !east && !south_east) {
               northTemp = ConnectingCarpet.North.JUST_NORTH_WEST;
            }

            return (BlockState)((BlockState)((BlockState)((BlockState)pState.setValue(EAST, south)).setValue(WEST, north)).setValue(NORTH, northTemp))
               .setValue(SOUTH, southTemp);
         default:
            return pState;
      }
   }

   public ConnectingCarpet(Properties pProperties) {
      super(pProperties.noOcclusion());
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)super.defaultBlockState().setValue(WEST, false)).setValue(EAST, false))
                  .setValue(NORTH, ConnectingCarpet.North.NONE))
               .setValue(SOUTH, ConnectingCarpet.South.NONE))
            .setValue(HorizontalDirectionalBlock.FACING, Direction.NORTH)
      );
   }

   protected BlockState updateCorners(BlockGetter world, BlockPos pos, BlockState state) {
      BlockState bs_north = world.getBlockState(pos.north());
      BlockState bs_north_east = world.getBlockState(pos.north().east());
      BlockState bs_north_west = world.getBlockState(pos.north().west());
      BlockState bs_east = world.getBlockState(pos.east());
      BlockState bs_south = world.getBlockState(pos.south());
      BlockState bs_south_east = world.getBlockState(pos.south().east());
      BlockState bs_south_west = world.getBlockState(pos.south().west());
      BlockState bs_west = world.getBlockState(pos.west());
      ConnectingCarpet.North north = ConnectingCarpet.North.NONE;
      ConnectingCarpet.South south = ConnectingCarpet.South.NONE;
      if (bs_north.getBlock() == this) {
         north = ConnectingCarpet.North.JUST_NORTH;
         if (bs_north_west.getBlock() == this && bs_north_east.getBlock() != this) {
            north = ConnectingCarpet.North.NORTH_AND_NORTH_WEST;
         }

         if (bs_north_west.getBlock() != this && bs_north_east.getBlock() == this) {
            north = ConnectingCarpet.North.NORTH_AND_NORTH_EAST;
         }

         if (bs_north_west.getBlock() == this && bs_north_east.getBlock() == this) {
            north = ConnectingCarpet.North.ALL;
         }
      } else {
         if (bs_north_west.getBlock() == this && bs_north_east.getBlock() != this) {
            north = ConnectingCarpet.North.JUST_NORTH_WEST;
         }

         if (bs_north_west.getBlock() != this && bs_north_east.getBlock() == this) {
            north = ConnectingCarpet.North.JUST_NORTH_EAST;
         }
      }

      if (bs_south.getBlock() == this) {
         south = ConnectingCarpet.South.JUST_SOUTH;
         if (bs_south_west.getBlock() == this && bs_south_east.getBlock() != this) {
            south = ConnectingCarpet.South.SOUTH_AND_SOUTH_WEST;
         }

         if (bs_south_west.getBlock() != this && bs_south_east.getBlock() == this) {
            south = ConnectingCarpet.South.SOUTH_AND_SOUTH_EAST;
         }

         if (bs_south_west.getBlock() == this && bs_south_east.getBlock() == this) {
            south = ConnectingCarpet.South.ALL;
         }
      } else {
         if (bs_south_west.getBlock() == this && bs_south_east.getBlock() != this) {
            south = ConnectingCarpet.South.JUST_SOUTH_WEST;
         }

         if (bs_south_west.getBlock() != this && bs_south_east.getBlock() == this) {
            south = ConnectingCarpet.South.JUST_SOUTH_EAST;
         }
      }

      boolean east = bs_east.getBlock() == this;
      boolean west = bs_west.getBlock() == this;
      return (BlockState)((BlockState)((BlockState)((BlockState)state.setValue(NORTH, north)).setValue(EAST, east)).setValue(SOUTH, south))
         .setValue(WEST, west);
   }

   public RenderShape getRenderShape(BlockState iBlockState) {
      return RenderShape.MODEL;
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      BlockGetter iblockreader = context.getLevel();
      BlockPos blockpos = context.getClickedPos();
      return this.updateCorners(iblockreader, blockpos, super.getStateForPlacement(context));
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{WEST, EAST, NORTH, SOUTH, HorizontalDirectionalBlock.FACING});
   }

   public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor world, BlockPos pos, BlockPos facingPos) {
      return !state.canSurvive(world, pos) ? Blocks.AIR.defaultBlockState() : this.updateCorners(world, pos, state);
   }

   public static enum North implements StringRepresentable {
      JUST_NORTH,
      NORTH_AND_NORTH_WEST,
      NORTH_AND_NORTH_EAST,
      JUST_NORTH_WEST,
      JUST_NORTH_EAST,
      NORTH_EAST_AND_NORTH_WEST,
      ALL,
      NONE;

      @Override
      public String toString() {
         return this.getSerializedName();
      }

      public String getSerializedName() {
         return switch (this) {
            case JUST_NORTH -> "north";
            case NORTH_AND_NORTH_WEST -> "north_and_north_west";
            case NORTH_AND_NORTH_EAST -> "north_and_north_east";
            case JUST_NORTH_WEST -> "north_west";
            case JUST_NORTH_EAST -> "north_east";
            case NORTH_EAST_AND_NORTH_WEST -> "north_east_and_north_west";
            case ALL -> "all";
            case NONE -> "none";
         };
      }
   }

   public static enum South implements StringRepresentable {
      JUST_SOUTH,
      SOUTH_AND_SOUTH_WEST,
      SOUTH_AND_SOUTH_EAST,
      JUST_SOUTH_WEST,
      JUST_SOUTH_EAST,
      SOUTH_EAST_AND_SOUTH_WEST,
      ALL,
      NONE;

      @Override
      public String toString() {
         return this.getSerializedName();
      }

      public String getSerializedName() {
         return switch (this) {
            case JUST_SOUTH -> "south";
            case SOUTH_AND_SOUTH_WEST -> "south_and_south_west";
            case SOUTH_AND_SOUTH_EAST -> "south_and_south_east";
            case JUST_SOUTH_WEST -> "south_west";
            case JUST_SOUTH_EAST -> "south_east";
            case SOUTH_EAST_AND_SOUTH_WEST -> "south_east_and_south_west";
            case ALL -> "all";
            case NONE -> "none";
         };
      }
   }
}
