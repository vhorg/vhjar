package iskallia.vault.block;

import iskallia.vault.block.entity.VaultPortalTileEntity;
import iskallia.vault.core.Version;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.vault.EntityState;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.VaultFactory;
import iskallia.vault.core.vault.player.Runner;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.entity.entity.SpiritEntity;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.util.BlockHelper;
import iskallia.vault.world.data.ServerVaults;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Direction.Axis;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockBehaviour.StatePredicate;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class VaultPortalBlock extends NetherPortalBlock implements EntityBlock {
   public static final StatePredicate FRAME = (state, reader, p) -> Arrays.stream(ModConfigs.VAULT_PORTAL.getValidFrameBlocks())
      .anyMatch(b -> b == state.getBlock());
   public static final EnumProperty<VaultPortalBlock.Style> STYLE = EnumProperty.create("style", VaultPortalBlock.Style.class);

   public VaultPortalBlock() {
      super(Properties.copy(Blocks.NETHER_PORTAL));
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(AXIS, Axis.X)).setValue(STYLE, VaultPortalBlock.Style.RAINBOW)
      );
   }

   public RenderShape getRenderShape(BlockState pState) {
      return RenderShape.ENTITYBLOCK_ANIMATED;
   }

   public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      super.createBlockStateDefinition(builder);
      builder.add(new Property[]{STYLE});
   }

   public void randomTick(BlockState state, ServerLevel world, BlockPos pos, Random random) {
   }

   public void entityInside(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Entity entity) {
      if (!level.isClientSide) {
         if (entity instanceof SpiritEntity spirit) {
            spirit.teleportOut();
         } else if (entity instanceof Player && !entity.isPassenger() && entity.canChangeDimensions()) {
            VoxelShape playerVoxel = Shapes.create(entity.getBoundingBox().move(-pos.getX(), -pos.getY(), -pos.getZ()));
            if (Shapes.joinIsNotEmpty(playerVoxel, state.getShape(level, pos), BooleanOp.AND)) {
               ServerPlayer player = (ServerPlayer)entity;
               BlockEntity te = level.getBlockEntity(pos);
               VaultPortalTileEntity portal = te instanceof VaultPortalTileEntity ? (VaultPortalTileEntity)te : null;
               if (player.isOnPortalCooldown()) {
                  player.setPortalCooldown();
               } else if (ServerVaults.get(level).isPresent()) {
                  CommonEvents.VAULT_PORTAL_COLLIDE.invoke((ServerLevel)level, state, pos, player);
               } else {
                  UUID vaultId = portal.getData().getVaultId();
                  Vault vault;
                  if (vaultId == null) {
                     vault = ServerVaults.add(VaultFactory.create(Version.latest(), portal.getData().copy()));
                     this.fill(level, pos, tileEntity -> {
                        tileEntity.getData().setVaultId(vault.get(Vault.ID));
                        tileEntity.setChanged();
                     }, new HashSet<>());
                  } else {
                     vault = ServerVaults.get(vaultId).orElse(null);
                  }

                  VirtualWorld world = ServerVaults.getWorld(vault).orElse(null);
                  if (world != null && vault != null) {
                     vault.ifPresent(
                        Vault.LISTENERS,
                        listeners -> listeners.add(world, vault, new Runner().set(Runner.JOIN_STATE, new EntityState(player)).set(Runner.ID, player.getUUID()))
                     );
                  }
               }
            }
         }
      }
   }

   public void fill(Level world, BlockPos pos, Consumer<VaultPortalTileEntity> action, Set<BlockPos> traversed) {
      action.accept((VaultPortalTileEntity)world.getBlockEntity(pos));
      traversed.add(pos);

      for (Direction offset : Direction.values()) {
         BlockPos next = pos.relative(offset);
         if (!traversed.contains(next) && world.getBlockEntity(next) instanceof VaultPortalTileEntity) {
            this.fill(world, pos.relative(offset), action, traversed);
         }
      }
   }

   public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor iWorld, BlockPos currentPos, BlockPos facingPos) {
      if (iWorld instanceof ServerLevel sLevel) {
         if (ServerVaults.isVaultWorld(sLevel)) {
            return state;
         } else {
            Axis facingAxis = facing.getAxis();
            Axis portalAxis = (Axis)state.getValue(AXIS);
            boolean flag = portalAxis != facingAxis && facingAxis.isHorizontal();
            return !flag && !facingState.is(this) && !new VaultPortalSize(sLevel, currentPos, portalAxis, FRAME).validatePortal()
               ? Blocks.AIR.defaultBlockState()
               : super.updateShape(state, facing, facingState, sLevel, currentPos, facingPos);
         }
      } else {
         return state;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void animateTick(BlockState state, Level world, BlockPos pos, Random rand) {
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
      return ModBlocks.VAULT_PORTAL_TILE_ENTITY.create(pPos, pState);
   }

   @Nullable
   public <A extends BlockEntity> BlockEntityTicker<A> getTicker(Level level, BlockState state, BlockEntityType<A> tBlockEntityType) {
      return BlockHelper.getTicker(tBlockEntityType, ModBlocks.VAULT_PORTAL_TILE_ENTITY, VaultPortalTileEntity::tick);
   }

   public static enum Style implements StringRepresentable {
      RAINBOW,
      FINAL,
      VELARA,
      TENOS,
      WENDARR,
      IDONA;

      public String getSerializedName() {
         return this.name().toLowerCase();
      }
   }
}
