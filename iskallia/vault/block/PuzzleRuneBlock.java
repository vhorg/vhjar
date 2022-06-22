package iskallia.vault.block;

import iskallia.vault.init.ModAttributes;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.item.ItemRelicBoosterPack;
import iskallia.vault.item.PuzzleRuneItem;
import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.AbstractBlock.Properties;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class PuzzleRuneBlock extends Block {
   public static final DirectionProperty FACING = BlockStateProperties.field_208157_J;
   public static final EnumProperty<PuzzleRuneBlock.Color> COLOR = EnumProperty.func_177709_a("color", PuzzleRuneBlock.Color.class);
   public static final BooleanProperty RUNE_PLACED = BooleanProperty.func_177716_a("rune_placed");

   public PuzzleRuneBlock() {
      super(Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151665_m).func_200948_a(-1.0F, 3600000.0F).func_226896_b_().func_222380_e());
      this.func_180632_j(
         (BlockState)((BlockState)((BlockState)((BlockState)this.field_176227_L.func_177621_b()).func_206870_a(FACING, Direction.SOUTH))
               .func_206870_a(COLOR, PuzzleRuneBlock.Color.YELLOW))
            .func_206870_a(RUNE_PLACED, false)
      );
   }

   public void func_149666_a(ItemGroup group, NonNullList<ItemStack> items) {
   }

   @Nullable
   public BlockState func_196258_a(BlockItemUseContext context) {
      return (BlockState)((BlockState)((BlockState)this.func_176223_P().func_206870_a(FACING, context.func_195992_f()))
            .func_206870_a(
               COLOR, ModAttributes.PUZZLE_COLOR.getOrDefault(context.func_195996_i(), PuzzleRuneBlock.Color.YELLOW).getValue(context.func_195996_i())
            ))
         .func_206870_a(RUNE_PLACED, false);
   }

   protected void func_206840_a(Builder<Block, BlockState> builder) {
      builder.func_206894_a(new Property[]{FACING}).func_206894_a(new Property[]{COLOR}).func_206894_a(new Property[]{RUNE_PLACED});
   }

   public ActionResultType func_225533_a_(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
      if (!world.field_72995_K) {
         ItemStack heldStack = player.func_184586_b(hand);
         if (heldStack.func_77973_b() instanceof PuzzleRuneItem && this.isValidKey(heldStack, state)) {
            heldStack.func_190918_g(1);
            BlockState blockState = world.func_180495_p(pos);
            world.func_180501_a(pos, (BlockState)blockState.func_206870_a(RUNE_PLACED, true), 3);
            world.func_184148_a(
               null, pos.func_177958_n(), pos.func_177956_o(), pos.func_177952_p(), SoundEvents.field_193781_bp, SoundCategory.BLOCKS, 1.0F, 1.0F
            );
         }
      }

      return super.func_225533_a_(state, world, pos, player, hand, hit);
   }

   public int func_180656_a(BlockState state, IBlockReader blockAccess, BlockPos pos, Direction side) {
      return state.func_177229_b(RUNE_PLACED) ? 15 : 0;
   }

   private boolean isValidKey(ItemStack stack, BlockState state) {
      return state.func_177229_b(RUNE_PLACED)
         ? false
         : ModAttributes.PUZZLE_COLOR.get(stack).map(attribute -> attribute.getValue(stack)).filter(value -> value == state.func_177229_b(COLOR)).isPresent();
   }

   public static enum Color implements IStringSerializable {
      YELLOW,
      PINK,
      GREEN,
      BLUE;

      public PuzzleRuneBlock.Color next() {
         return values()[(this.ordinal() + 1) % values().length];
      }

      public String func_176610_l() {
         return this.name().toLowerCase(Locale.ENGLISH);
      }
   }

   public static class Item extends BlockItem {
      public Item(Block block, net.minecraft.item.Item.Properties properties) {
         super(block, properties);
      }

      public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context) {
         PlayerEntity player = context.func_195999_j();
         World world = context.func_195991_k();
         if (player != null
            && player.func_184812_l_()
            && !world.field_72995_K
            && world.func_180495_p(context.func_195995_a()).getBlockState().func_177230_c() == ModBlocks.PUZZLE_RUNE_BLOCK) {
            ModAttributes.PUZZLE_COLOR.create(stack, ModAttributes.PUZZLE_COLOR.getOrCreate(stack, PuzzleRuneBlock.Color.YELLOW).getValue(stack).next());
            ItemRelicBoosterPack.successEffects(world, player.func_213303_ch());
            return ActionResultType.SUCCESS;
         } else {
            return super.onItemUseFirst(stack, context);
         }
      }
   }
}
