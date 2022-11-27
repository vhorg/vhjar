package iskallia.vault.block;

import iskallia.vault.block.base.FacedBlock;
import iskallia.vault.block.property.HiddenIntegerProperty;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.util.MathUtilities;
import iskallia.vault.util.ServerScheduler;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.registries.ForgeRegistries;

public class VaultArtifactBlock extends FacedBlock {
   public static final int ARTIFACT_COUNT = 25;
   public static final IntegerProperty ORDER_PROPERTY = HiddenIntegerProperty.create("order", 1, 25);
   public static final VoxelShape EAST_SHAPE = Block.box(15.75, 0.0, 0.0, 16.0, 16.0, 16.0);
   public static final VoxelShape NORTH_SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 0.25);
   public static final VoxelShape WEST_SHAPE = Block.box(0.0, 0.0, 0.0, 0.25, 16.0, 16.0);
   public static final VoxelShape SOUTH_SHAPE = Block.box(0.0, 0.0, 15.75, 16.0, 16.0, 16.0);

   public VaultArtifactBlock() {
      super(Properties.of(Material.CLAY, MaterialColor.WOOD).sound(SoundType.WOOL).noOcclusion());
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.SOUTH));
   }

   public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
   }

   public int getOrder(ItemStack stack) {
      CompoundTag nbt = stack.getOrCreateTag();
      return nbt.contains("CustomModelData") ? nbt.getInt("CustomModelData") : 1;
   }

   public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
      return switch ((Direction)state.getValue(FACING)) {
         case EAST -> EAST_SHAPE;
         case NORTH -> NORTH_SHAPE;
         case WEST -> WEST_SHAPE;
         default -> SOUTH_SHAPE;
      };
   }

   @Nonnull
   @Override
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      ItemStack artifactBlockItem = context.getItemInHand();
      return (BlockState)super.getStateForPlacement(context).setValue(ORDER_PROPERTY, this.getOrder(artifactBlockItem));
   }

   @Override
   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      super.createBlockStateDefinition(builder);
      builder.add(new Property[]{ORDER_PROPERTY});
   }

   public void onBlockExploded(BlockState state, Level world, BlockPos pos, Explosion explosion) {
      if (world instanceof ServerLevel sWorld) {
         List<BlockPos> validPositions = isValidArtifactSetup(sWorld, pos, state);
         if (!validPositions.isEmpty()) {
            validPositions.forEach(at -> world.removeBlock(at, false));
            ServerScheduler.INSTANCE.schedule(5, () -> {
               ItemStack frameStack = new ItemStack(ModBlocks.FINAL_VAULT_FRAME_BLOCK_ITEM);
               Block.popResource(sWorld, pos, frameStack);
            });
         }
      }
   }

   public boolean canDropFromExplosion(BlockState state, BlockGetter world, BlockPos pos, Explosion explosion) {
      return false;
   }

   public static List<BlockPos> isValidArtifactSetup(ServerLevel world, BlockPos at, BlockState state) {
      int order = (25 - (Integer)state.getValue(ORDER_PROPERTY) + 24) % 25;
      int shiftVertical = order / 5;
      int shiftHorizontal = order % 5;
      BlockPos yPos = at.above(shiftVertical);

      for (Direction dir : Direction.values()) {
         if (!dir.getAxis().isVertical()) {
            BlockPos startPos = yPos.relative(dir, -shiftHorizontal);
            List<BlockPos> artifactPositions = hasFullArtifactSet(world, startPos, dir);
            if (!artifactPositions.isEmpty()) {
               return artifactPositions;
            }
         }
      }

      return Collections.emptyList();
   }

   private static List<BlockPos> hasFullArtifactSet(ServerLevel world, BlockPos start, Direction facing) {
      List<BlockPos> positions = new ArrayList<>();

      for (int order = 0; order < 25; order++) {
         BlockPos at = start.below(order / 5).relative(facing, order % 5);
         BlockState offsetState = world.getBlockState(at);
         if (!(offsetState.getBlock() instanceof VaultArtifactBlock)) {
            return Collections.emptyList();
         }

         int orderAt = (25 - (Integer)offsetState.getValue(ORDER_PROPERTY) + 24) % 25;
         if (order != orderAt) {
            return Collections.emptyList();
         }

         positions.add(at);
      }

      return positions;
   }

   public List<ItemStack> getDrops(BlockState state, net.minecraft.world.level.storage.loot.LootContext.Builder builder) {
      Integer order = (Integer)state.getValue(ORDER_PROPERTY);
      ItemStack artifactStack = createArtifact(order);
      return new ArrayList<>(Collections.singletonList(artifactStack));
   }

   public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
      Integer order = (Integer)state.getValue(ORDER_PROPERTY);
      return createArtifact(order);
   }

   public static ItemStack createRandomArtifact() {
      return createArtifact(MathUtilities.getRandomInt(0, 25) + 1);
   }

   public static ItemStack createArtifact(int order) {
      Item artifactItem = (Item)ForgeRegistries.ITEMS.getValue(ModBlocks.VAULT_ARTIFACT.getRegistryName());
      ItemStack itemStack = new ItemStack(artifactItem, 1);
      CompoundTag nbt = new CompoundTag();
      nbt.putInt("CustomModelData", Mth.clamp(order, 0, 25));
      itemStack.setTag(nbt);
      return itemStack;
   }
}
