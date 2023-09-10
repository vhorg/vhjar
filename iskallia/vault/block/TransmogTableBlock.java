package iskallia.vault.block;

import com.google.common.collect.Sets;
import iskallia.vault.block.entity.TransmogTableTileEntity;
import iskallia.vault.dynamodel.model.armor.ArmorPieceModel;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModDynamicModels;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.NetworkHooks;

public class TransmogTableBlock extends Block implements EntityBlock {
   private static final Set<Long> CHAMPION_LIST = Sets.newHashSet(
      new Long[]{
         6429864731041569685L,
         2448539977348180107L,
         7352707271564777874L,
         4693136740730003400L,
         -8501855865975504906L,
         -5299334673525914076L,
         5570091698276434352L,
         7973039238502692033L,
         8358963723990234005L,
         -3317886489741321916L,
         8417884404976239497L,
         4886049405667317572L,
         -9137514920280909335L,
         7013972357902170887L,
         1603644902186812849L,
         -2703991592134697134L,
         -8398240994201074281L,
         -3509262462151683178L,
         6938983151738513391L,
         288621971766306260L,
         7323568528928070801L,
         -4188687347367565280L,
         -3325511366828596477L,
         1723692179382680874L,
         2256850891809661999L,
         -2007293558487436784L,
         8695023214094801682L,
         -3018326060607023160L,
         -1295540691216318578L,
         5845844909483154124L,
         5521630395145125160L,
         8704555222904558510L,
         -4882196526160383398L,
         4525952151672633934L,
         -1227726653841039330L,
         -6474799362138270716L,
         4305659288536578088L,
         -9006820501646684550L,
         7602108156488684461L,
         -67851277500103571L,
         2719731602807430855L,
         -5763024996529274456L,
         -2518946063819162305L,
         -544226752241766045L,
         -2007290093885560666L,
         6476060112674362202L,
         4087701544154057938L,
         225759358201069401L,
         4020683106015679817L,
         6896613037582234931L,
         -5888045982291673467L
      }
   );
   private static final Set<Long> GOBLIN_LIST = Sets.newHashSet(
      new Long[]{
         5110613782587649384L,
         593124799019528878L,
         -4365441881635131534L,
         8123504348404330494L,
         -3466705230727636273L,
         -5118137140963796610L,
         -7719392189824946528L,
         -598060658150328250L,
         -828607639601669554L,
         -3949770839911833592L,
         2690267933177279887L,
         4542237741271082467L,
         1303540098933870407L,
         3042724202892870281L,
         8319033837069071295L,
         -8503768710299709203L,
         267298067134289140L,
         3924707702712001491L,
         -8063210996910815760L,
         -3748192976979668763L,
         -2382237795783901803L,
         -2012603671192041240L,
         5396100273386976522L,
         -2466766800723093866L,
         3042724202892870281L,
         -4258886699357928933L
      }
   );
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
   public static final VoxelShape SHAPE = Shapes.or(
      Block.box(0.0, 13.0, 0.0, 16.0, 16.0, 16.0),
      new VoxelShape[]{Block.box(4.0, 11.0, 4.0, 12.0, 13.0, 12.0), Block.box(5.0, 2.0, 5.0, 11.0, 11.0, 11.0), Block.box(3.0, 0.0, 3.0, 13.0, 2.0, 13.0)}
   );

   public TransmogTableBlock() {
      super(Properties.of(Material.STONE).strength(0.5F).lightLevel(state -> 1).noOcclusion());
   }

   public static boolean canTransmogModel(Player player, Collection<ResourceLocation> discoveredModelIds, ResourceLocation modelId) {
      return ModDynamicModels.Armor.PIECE_REGISTRY
         .get(modelId)
         .map(ArmorPieceModel::getArmorModel)
         .map(armorModel -> {
            if (armorModel.equals(ModDynamicModels.Armor.CHAMPION)) {
               return !FMLEnvironment.production || CHAMPION_LIST.contains(hashId(player.getUUID()));
            } else if (!armorModel.equals(ModDynamicModels.Armor.GOBLIN)) {
               return null;
            } else {
               long id = hashId(player.getUUID());
               return !FMLEnvironment.production || GOBLIN_LIST.contains(id) || CHAMPION_LIST.contains(id);
            }
         })
         .or(
            () -> ModDynamicModels.Swords.REGISTRY
               .get(modelId)
               .map(
                  model -> !model.equals(ModDynamicModels.Swords.GODSWORD)
                     ? null
                     : !FMLEnvironment.production || CHAMPION_LIST.contains(hashId(player.getUUID()))
               )
         )
         .or(
            () -> ModDynamicModels.Axes.REGISTRY
               .get(modelId)
               .map(
                  model -> !model.equals(ModDynamicModels.Axes.GODAXE) ? null : !FMLEnvironment.production || CHAMPION_LIST.contains(hashId(player.getUUID()))
               )
         )
         .orElseGet(() -> discoveredModelIds.contains(modelId));
   }

   private static long hashId(UUID id) {
      return id.getMostSignificantBits() ^ id.getLeastSignificantBits();
   }

   public BlockState getStateForPlacement(BlockPlaceContext context) {
      return (BlockState)this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
   }

   public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
      return SHAPE;
   }

   public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
      if (world.isClientSide()) {
         return InteractionResult.SUCCESS;
      } else if (player instanceof ServerPlayer sPlayer) {
         if (world.getBlockEntity(pos) instanceof TransmogTableTileEntity transmogTableTileEntity) {
            NetworkHooks.openGui(sPlayer, transmogTableTileEntity, buffer -> buffer.writeBlockPos(pos));
            return InteractionResult.SUCCESS;
         } else {
            return InteractionResult.SUCCESS;
         }
      } else {
         return InteractionResult.SUCCESS;
      }
   }

   public BlockState rotate(BlockState state, Rotation rot) {
      return (BlockState)state.setValue(FACING, rot.rotate((Direction)state.getValue(FACING)));
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING});
   }

   public boolean isPathfindable(BlockState state, BlockGetter worldIn, BlockPos pos, PathComputationType type) {
      return false;
   }

   @OnlyIn(Dist.CLIENT)
   public int getDustColor(BlockState state, BlockGetter reader, BlockPos pos) {
      return state.getMapColor(reader, pos).col;
   }

   @Nullable
   public BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) {
      return ModBlocks.TRANSMOG_TABLE_TILE_ENTITY.create(pos, state);
   }

   public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
      if (!pState.is(pNewState.getBlock())) {
         if (pLevel.getBlockEntity(pPos) instanceof TransmogTableTileEntity transmogTableTileEntity) {
            Containers.dropContents(pLevel, pPos, transmogTableTileEntity.getInternalInventory());
            pLevel.updateNeighbourForOutputSignal(pPos, this);
         }

         super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
      }
   }
}
