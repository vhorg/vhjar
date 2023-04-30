package iskallia.vault.block;

import com.google.common.base.Functions;
import iskallia.vault.block.item.EasterEggBlockItem;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class EasterEggBlock extends Block {
   public static final VoxelShape SHAPE = Block.box(6.0, 0.0, 6.0, 10.0, 6.75, 10.0);
   public static final EnumProperty<EasterEggBlock.Color> COLOR = EnumProperty.create("color", EasterEggBlock.Color.class);

   public EasterEggBlock(Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(COLOR, EasterEggBlock.Color.PINK));
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
      pBuilder.add(new Property[]{COLOR});
   }

   public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
      return SHAPE;
   }

   public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> items) {
      for (EasterEggBlock.Color color : EasterEggBlock.Color.values()) {
         items.add(EasterEggBlockItem.fromColor(this, color));
      }
   }

   public BlockState getStateForPlacement(BlockPlaceContext context) {
      BlockState blockState = this.defaultBlockState();
      CompoundTag nbt = context.getItemInHand().getTag();
      if (nbt != null) {
         EasterEggBlock.Color color = EasterEggBlock.Color.fromString(nbt.getString("color"));
         if (color != null) {
            blockState = (BlockState)blockState.setValue(COLOR, color);
         }
      }

      return blockState;
   }

   public List<ItemStack> getDrops(BlockState state, net.minecraft.world.level.storage.loot.LootContext.Builder builder) {
      List<ItemStack> drops = new ArrayList<>();
      ItemStack stack = (ItemStack)builder.getOptionalParameter(LootContextParams.TOOL);
      if (stack != null) {
         drops.add(EasterEggBlockItem.fromColor(this, (EasterEggBlock.Color)state.getValue(COLOR)));
      }

      return drops;
   }

   public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
      ItemStack itemStack = super.getCloneItemStack(state, target, level, pos, player);
      itemStack.getOrCreateTag().putString("color", ((EasterEggBlock.Color)state.getValue(COLOR)).getSerializedName());
      return itemStack;
   }

   public static enum Color implements StringRepresentable {
      PINK,
      YELLOW,
      GREEN,
      PURPLE,
      MAGENTA,
      GOLD;

      private static final Map<String, EasterEggBlock.Color> NAME_TO_TYPE = Arrays.stream(values())
         .collect(Collectors.toMap(EasterEggBlock.Color::getSerializedName, Functions.identity()));

      public static EasterEggBlock.Color fromString(String name) {
         return NAME_TO_TYPE.get(name.toLowerCase(Locale.ROOT));
      }

      @Nonnull
      public String getSerializedName() {
         return this.name().toLowerCase(Locale.ROOT);
      }
   }
}
