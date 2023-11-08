package iskallia.vault.block.base;

import iskallia.vault.block.item.GodAltarBlockItem;
import iskallia.vault.core.vault.influence.VaultGod;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModParticles;
import iskallia.vault.util.BlockHelper;
import java.util.Random;
import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

public class GodAltarBlock extends FacedBlock implements EntityBlock {
   public static final EnumProperty<VaultGod> GOD = EnumProperty.create("god", VaultGod.class);
   public static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 16.0);

   public GodAltarBlock() {
      super(Properties.of(Material.STONE).strength(-1.0F, 3600000.0F).noDrops().noOcclusion());
   }

   @Override
   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      super.createBlockStateDefinition(builder);
      builder.add(new Property[]{GOD});
   }

   @Nonnull
   public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter worldIn, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
      return SHAPE;
   }

   public ParticleOptions getFlameParticle(BlockState state) {
      return switch ((VaultGod)state.getValue(GOD)) {
         case VELARA -> (SimpleParticleType)ModParticles.GREEN_FLAME.get();
         case TENOS -> (SimpleParticleType)ModParticles.YELLOW_FLAME.get();
         case WENDARR -> (SimpleParticleType)ModParticles.BLUE_FLAME.get();
         case IDONA -> (SimpleParticleType)ModParticles.RED_FLAME.get();
      };
   }

   @Override
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      BlockState state = super.getStateForPlacement(context);
      CompoundTag nbt = context.getItemInHand().getTag();
      if (nbt != null) {
         VaultGod god = VaultGod.fromName(nbt.getString("god"));
         if (god != null) {
            state = (BlockState)state.setValue(GOD, god);
         }
      }

      return state;
   }

   public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> items) {
      for (VaultGod god : VaultGod.values()) {
         items.add(GodAltarBlockItem.fromType(god));
      }
   }

   public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
      ItemStack itemStack = super.getCloneItemStack(state, target, level, pos, player);
      itemStack.getOrCreateTag().putString("god", ((VaultGod)state.getValue(GOD)).getSerializedName());
      return itemStack;
   }

   public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
      if (!world.isClientSide() && hand == InteractionHand.MAIN_HAND) {
         if (world.getBlockEntity(pos) instanceof GodAltarTileEntity altar) {
            altar.onClick((ServerLevel)world, (ServerPlayer)player);
         }

         return InteractionResult.SUCCESS;
      } else {
         return InteractionResult.SUCCESS;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void animateTick(BlockState stateIn, Level world, BlockPos pos, Random rand) {
      Direction facing = (Direction)stateIn.getValue(FACING);
      Direction rightDirection = facing.getClockWise();
      Direction leftDirection = rightDirection.getOpposite();
      if (world.getBlockEntity(pos) instanceof GodAltarTileEntity godAltarTileEntity && !godAltarTileEntity.isCompleted()) {
         for (int i = 0; i < 2; i++) {
            if (stateIn.getValue(GOD) == VaultGod.VELARA) {
               this.addFlameParticle(
                  world,
                  pos,
                  8.0 - rightDirection.getStepX() * 6.25 - facing.getStepX() * 4.5,
                  7.0,
                  8.0 - rightDirection.getStepZ() * 6.25 - facing.getStepZ() * 4.5
               );
               this.addFlameParticle(
                  world,
                  pos,
                  8.0 - rightDirection.getStepX() * 6.5 - facing.getStepX() * 2.5,
                  6.0,
                  8.0 - rightDirection.getStepZ() * 6.5 - facing.getStepZ() * 2.5
               );
               this.addFlameParticle(
                  world,
                  pos,
                  8.0 - rightDirection.getStepX() * 5.5 - facing.getStepX() * 6.5,
                  6.0,
                  8.0 - rightDirection.getStepZ() * 5.5 - facing.getStepZ() * 6.5
               );
               this.addFlameParticle(
                  world,
                  pos,
                  8.0 - rightDirection.getStepX() * 4.5 - facing.getStepX() * 1.5,
                  8.0,
                  8.0 - rightDirection.getStepZ() * 4.5 - facing.getStepZ() * 1.5
               );
               this.addFlameParticle(
                  world,
                  pos,
                  8.0 - leftDirection.getStepX() * 6.25 - facing.getStepX() * 4.5,
                  7.0,
                  8.0 - leftDirection.getStepZ() * 6.25 - facing.getStepZ() * 4.5
               );
               this.addFlameParticle(
                  world,
                  pos,
                  8.0 - leftDirection.getStepX() * 6.5 - facing.getStepX() * 2.5,
                  6.0,
                  8.0 - leftDirection.getStepZ() * 6.5 - facing.getStepZ() * 2.5
               );
               this.addFlameParticle(
                  world,
                  pos,
                  8.0 - leftDirection.getStepX() * 5.5 - facing.getStepX() * 6.5,
                  6.0,
                  8.0 - leftDirection.getStepZ() * 5.5 - facing.getStepZ() * 6.5
               );
               this.addFlameParticle(
                  world,
                  pos,
                  8.0 - leftDirection.getStepX() * 4.5 - facing.getStepX() * 1.5,
                  8.0,
                  8.0 - leftDirection.getStepZ() * 4.5 - facing.getStepZ() * 1.5
               );
            } else {
               this.addFlameParticle(
                  world,
                  pos,
                  8.0 - rightDirection.getStepX() * 5.5 - facing.getStepX() * 4.5,
                  6.5,
                  8.0 - rightDirection.getStepZ() * 5.5 - facing.getStepZ() * 4.5
               );
               this.addFlameParticle(
                  world,
                  pos,
                  8.0 - rightDirection.getStepX() * 6.5 - facing.getStepX() * 2.5,
                  6.0,
                  8.0 - rightDirection.getStepZ() * 6.5 - facing.getStepZ() * 2.5
               );
               this.addFlameParticle(
                  world,
                  pos,
                  8.0 - rightDirection.getStepX() * 6.5 - facing.getStepX() * 6.5,
                  6.0,
                  8.0 - rightDirection.getStepZ() * 6.5 - facing.getStepZ() * 6.5
               );
               this.addFlameParticle(
                  world,
                  pos,
                  8.0 - leftDirection.getStepX() * 5.5 - facing.getStepX() * 4.5,
                  6.5,
                  8.0 - leftDirection.getStepZ() * 5.5 - facing.getStepZ() * 4.5
               );
               this.addFlameParticle(
                  world,
                  pos,
                  8.0 - leftDirection.getStepX() * 6.5 - facing.getStepX() * 2.5,
                  6.0,
                  8.0 - leftDirection.getStepZ() * 6.5 - facing.getStepZ() * 2.5
               );
               this.addFlameParticle(
                  world,
                  pos,
                  8.0 - leftDirection.getStepX() * 6.5 - facing.getStepX() * 6.5,
                  6.0,
                  8.0 - leftDirection.getStepZ() * 6.5 - facing.getStepZ() * 6.5
               );
            }
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void addFlameParticle(Level world, BlockPos pos, double xOffset, double yOffset, double zOffset) {
      double x = pos.getX() + xOffset / 16.0;
      double y = pos.getY() + yOffset / 16.0;
      double z = pos.getZ() + zOffset / 16.0;
      world.addParticle(this.getFlameParticle(world.getBlockState(pos)), x, y, z, 0.0, 0.0, 0.0);
   }

   public static void snuffCandles(BlockState stateIn, Level world, BlockPos pos) {
      Direction facing = (Direction)stateIn.getValue(FACING);
      Direction rightDirection = facing.getClockWise();
      Direction leftDirection = rightDirection.getOpposite();

      for (int i = 0; i < 2; i++) {
         if (stateIn.getValue(GOD) == VaultGod.VELARA) {
            addSmokeParticle(
               world,
               pos,
               8.0 - rightDirection.getStepX() * 6.25 - facing.getStepX() * 4.5,
               7.0,
               8.0 - rightDirection.getStepZ() * 6.25 - facing.getStepZ() * 4.5
            );
            addSmokeParticle(
               world,
               pos,
               8.0 - rightDirection.getStepX() * 6.5 - facing.getStepX() * 2.5,
               6.0,
               8.0 - rightDirection.getStepZ() * 6.5 - facing.getStepZ() * 2.5
            );
            addSmokeParticle(
               world,
               pos,
               8.0 - rightDirection.getStepX() * 5.5 - facing.getStepX() * 6.5,
               6.0,
               8.0 - rightDirection.getStepZ() * 5.5 - facing.getStepZ() * 6.5
            );
            addSmokeParticle(
               world,
               pos,
               8.0 - rightDirection.getStepX() * 4.5 - facing.getStepX() * 1.5,
               8.0,
               8.0 - rightDirection.getStepZ() * 4.5 - facing.getStepZ() * 1.5
            );
            addSmokeParticle(
               world,
               pos,
               8.0 - leftDirection.getStepX() * 6.25 - facing.getStepX() * 4.5,
               7.0,
               8.0 - leftDirection.getStepZ() * 6.25 - facing.getStepZ() * 4.5
            );
            addSmokeParticle(
               world, pos, 8.0 - leftDirection.getStepX() * 6.5 - facing.getStepX() * 2.5, 6.0, 8.0 - leftDirection.getStepZ() * 6.5 - facing.getStepZ() * 2.5
            );
            addSmokeParticle(
               world, pos, 8.0 - leftDirection.getStepX() * 5.5 - facing.getStepX() * 6.5, 6.0, 8.0 - leftDirection.getStepZ() * 5.5 - facing.getStepZ() * 6.5
            );
            addSmokeParticle(
               world, pos, 8.0 - leftDirection.getStepX() * 4.5 - facing.getStepX() * 1.5, 8.0, 8.0 - leftDirection.getStepZ() * 4.5 - facing.getStepZ() * 1.5
            );
         } else {
            addSmokeParticle(
               world,
               pos,
               8.0 - rightDirection.getStepX() * 5.5 - facing.getStepX() * 4.5,
               6.5,
               8.0 - rightDirection.getStepZ() * 5.5 - facing.getStepZ() * 4.5
            );
            addSmokeParticle(
               world,
               pos,
               8.0 - rightDirection.getStepX() * 6.5 - facing.getStepX() * 2.5,
               6.0,
               8.0 - rightDirection.getStepZ() * 6.5 - facing.getStepZ() * 2.5
            );
            addSmokeParticle(
               world,
               pos,
               8.0 - rightDirection.getStepX() * 6.5 - facing.getStepX() * 6.5,
               6.0,
               8.0 - rightDirection.getStepZ() * 6.5 - facing.getStepZ() * 6.5
            );
            addSmokeParticle(
               world, pos, 8.0 - leftDirection.getStepX() * 5.5 - facing.getStepX() * 4.5, 6.5, 8.0 - leftDirection.getStepZ() * 5.5 - facing.getStepZ() * 4.5
            );
            addSmokeParticle(
               world, pos, 8.0 - leftDirection.getStepX() * 6.5 - facing.getStepX() * 2.5, 6.0, 8.0 - leftDirection.getStepZ() * 6.5 - facing.getStepZ() * 2.5
            );
            addSmokeParticle(
               world, pos, 8.0 - leftDirection.getStepX() * 6.5 - facing.getStepX() * 6.5, 6.0, 8.0 - leftDirection.getStepZ() * 6.5 - facing.getStepZ() * 6.5
            );
         }
      }
   }

   public static void addSmokeParticle(Level world, BlockPos pos, double xOffset, double yOffset, double zOffset) {
      double x = pos.getX() + xOffset / 16.0;
      double y = pos.getY() + yOffset / 16.0;
      double z = pos.getZ() + zOffset / 16.0;
      if (world instanceof ServerLevel serverLevel) {
         serverLevel.sendParticles(ParticleTypes.SMOKE, x, y, z, 3, 0.0, 0.15F, 0.0, 0.0);
      }
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new GodAltarTileEntity(pos, state);
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
      return BlockHelper.getTicker(type, ModBlocks.GOD_ALTAR_TILE_ENTITY, GodAltarTileEntity::tick);
   }
}
