package iskallia.vault.block;

import iskallia.vault.block.entity.SkillAltarTileEntity;
import iskallia.vault.container.SkillAltarContainer;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.world.data.SkillAltarData;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public class SkillAltarBlock extends Block implements EntityBlock {
   public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
   private static final VoxelShape X_AXIS_SHAPE = Shapes.join(
      Shapes.box(0.1875, 0.0, 0.0, 0.8125, 0.375, 1.0), Shapes.box(0.4375, 0.375, 0.09375, 0.5625, 0.96875, 0.90625), BooleanOp.OR
   );
   private static final VoxelShape Z_AXIS_SHAPE = Shapes.join(
      Shapes.box(0.0, 0.0, 0.1875, 1.0, 0.375, 0.8125), Shapes.box(0.09375, 0.375, 0.4375, 0.90625, 0.96875, 0.5625), BooleanOp.OR
   );

   public SkillAltarBlock() {
      super(Properties.of(Material.STONE).strength(1.5F, 6.0F));
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH));
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING});
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      return (BlockState)this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new SkillAltarTileEntity(pos, state);
   }

   public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
      if (level.getBlockEntity(pos) instanceof SkillAltarTileEntity skillAltarTile && placer != null) {
         skillAltarTile.setOwner(placer.getUUID());
      }
   }

   public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      return ((Direction)state.getValue(FACING)).getAxis() == Axis.X ? X_AXIS_SHAPE : Z_AXIS_SHAPE;
   }

   public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
      if (!state.is(newState.getBlock())) {
         if (level.getBlockEntity(pos) instanceof SkillAltarTileEntity skillAltar) {
            Containers.dropContents(level, pos, skillAltar.getRegretOrbInventory());
         }

         super.onRemove(state, level, pos, newState, isMoving);
      }
   }

   public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
      BlockEntity be = level.getBlockEntity(pos);
      if (!(be instanceof SkillAltarTileEntity)) {
         return super.use(state, level, pos, player, hand, hit);
      } else if (level.isClientSide()) {
         return InteractionResult.SUCCESS;
      } else {
         openGui(pos, (ServerPlayer)player);
         return InteractionResult.SUCCESS;
      }
   }

   private static void openGui(BlockPos pos, ServerPlayer player) {
      openGui(pos, player, 0);
   }

   public static void openGui(BlockPos pos, ServerPlayer player, int templateIndex) {
      player.getLevel()
         .getBlockEntity(pos, ModBlocks.SKILL_ALTAR_TILE_ENTITY)
         .ifPresent(skillAltar -> openGui(pos, player, templateIndex, skillAltar.getOwnerId()));
   }

   private static void openGui(BlockPos pos, ServerPlayer player, int templateIndex, UUID ownerId) {
      SkillAltarData skillAltarData = SkillAltarData.get((ServerLevel)player.level);
      List<SkillAltarData.SkillIcon> skillIcons = skillAltarData.getSkillTemplates(ownerId)
         .values()
         .stream()
         .map(SkillAltarData.SkillTemplate::getIcon)
         .toList();
      SkillAltarData.SkillTemplate template = skillAltarData.getSkillTemplate(ownerId, templateIndex);
      networkOpenGui(pos, player, templateIndex, skillIcons, template);
   }

   private static void networkOpenGui(
      final BlockPos pos,
      ServerPlayer player,
      final int templateIndex,
      final List<SkillAltarData.SkillIcon> skillIcons,
      final SkillAltarData.SkillTemplate template
   ) {
      NetworkHooks.openGui(player, new MenuProvider() {
         public Component getDisplayName() {
            return ModBlocks.SKILL_ALTAR.getName();
         }

         public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player playerx) {
            return new SkillAltarContainer(windowId, inventory, pos, template, templateIndex, skillIcons);
         }
      }, buffer -> {
         buffer.writeBlockPos(pos);
         SkillAltarData.SkillTemplate.writeTo(template, buffer);
         buffer.writeInt(templateIndex);
         buffer.writeCollection(skillIcons, (b, icon) -> icon.writeTo(b));
      });
   }
}
