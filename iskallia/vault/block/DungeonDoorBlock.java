package iskallia.vault.block;

import com.google.common.base.Functions;
import iskallia.vault.block.entity.DungeonDoorTileEntity;
import iskallia.vault.block.item.DungeonDoorBlockItem;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.util.BlockHelper;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.game.ClientboundClearTitlesPacket;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;

public class DungeonDoorBlock extends DoorBlock implements EntityBlock {
   public static final EnumProperty<DungeonDoorBlock.Type> TYPE = EnumProperty.create("type", DungeonDoorBlock.Type.class);

   public DungeonDoorBlock() {
      super(Properties.of(Material.METAL, MaterialColor.DIAMOND).strength(-1.0F, 3600000.0F).sound(SoundType.METAL).noOcclusion());
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.getStateDefinition().any())
                           .setValue(FACING, Direction.NORTH))
                        .setValue(OPEN, Boolean.FALSE))
                     .setValue(HINGE, DoorHingeSide.LEFT))
                  .setValue(POWERED, Boolean.FALSE))
               .setValue(HALF, DoubleBlockHalf.LOWER))
            .setValue(TYPE, DungeonDoorBlock.Type.SPIDER)
      );
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      super.createBlockStateDefinition(builder);
      builder.add(new Property[]{TYPE});
   }

   public PushReaction getPistonPushReaction(BlockState state) {
      return PushReaction.BLOCK;
   }

   public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
   }

   public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
      return true;
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
      return ModBlocks.DUNGEON_DOOR_TILE_ENTITY.create(pPos, pState);
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
      return BlockHelper.getTicker(pBlockEntityType, ModBlocks.DUNGEON_DOOR_TILE_ENTITY, DungeonDoorTileEntity::tick);
   }

   public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
      Boolean isOpen = (Boolean)state.getValue(OPEN);
      if (!isOpen) {
         this.setOpen(player, world, state, pos, true);
         CommonEvents.TREASURE_ROOM_OPEN.invoke(world, player, pos);
         if (world instanceof ServerLevel) {
            BlockEntity te = state.getValue(HALF) == DoubleBlockHalf.LOWER ? world.getBlockEntity(pos) : world.getBlockEntity(pos.below());
            this.announceDungeon((ServerLevel)world, (DungeonDoorTileEntity)te);
         }

         return InteractionResult.SUCCESS;
      } else {
         return InteractionResult.SUCCESS;
      }
   }

   public void announceDungeon(ServerLevel world, DungeonDoorTileEntity tileEntity) {
      if (tileEntity != null) {
         Component difficultyDisplay = tileEntity.getDifficulty().getDisplay();
         ClientboundSetTitleTextPacket titlePacket = new ClientboundSetTitleTextPacket(TextComponent.EMPTY);
         ClientboundSetSubtitleTextPacket subtitlePacket = new ClientboundSetSubtitleTextPacket(difficultyDisplay);
         AABB box = new AABB(tileEntity.getBlockPos().offset(-15, -15, -15), tileEntity.getBlockPos().offset(15, 15, 15));
         world.getEntities((Entity)null, box, e -> e instanceof ServerPlayer).forEach(entity -> {
            if (entity instanceof ServerPlayer player) {
               player.connection.send(new ClientboundClearTitlesPacket(true));
               player.connection.send(titlePacket);
               player.connection.send(subtitlePacket);
            }
         });
      }
   }

   public BlockState getStateForPlacement(BlockPlaceContext context) {
      BlockState state = super.getStateForPlacement(context);
      if (state == null) {
         return null;
      } else {
         CompoundTag nbt = context.getItemInHand().getTag();
         if (nbt != null) {
            DungeonDoorBlock.Type type = DungeonDoorBlock.Type.fromString(nbt.getString("type"));
            if (type != null) {
               state = (BlockState)state.setValue(TYPE, type);
            }
         }

         return state;
      }
   }

   public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> items) {
      for (DungeonDoorBlock.Type type : DungeonDoorBlock.Type.values()) {
         items.add(DungeonDoorBlockItem.fromType(type));
      }
   }

   public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
      ItemStack itemStack = super.getCloneItemStack(state, target, level, pos, player);
      itemStack.getOrCreateTag().putString("type", ((DungeonDoorBlock.Type)state.getValue(TYPE)).getSerializedName());
      return itemStack;
   }

   public static enum Type implements StringRepresentable {
      SPIDER,
      ILLAGER,
      ZOMBIE,
      SKELETON,
      PIGLIN,
      WITCH;

      private static final Map<String, DungeonDoorBlock.Type> NAME_TO_TYPE = Arrays.stream(values())
         .collect(Collectors.toMap(DungeonDoorBlock.Type::getSerializedName, Functions.identity()));

      public static DungeonDoorBlock.Type fromString(String name) {
         return NAME_TO_TYPE.get(name.toLowerCase(Locale.ROOT));
      }

      public String getSerializedName() {
         return this.name().toLowerCase(Locale.ROOT);
      }
   }
}
