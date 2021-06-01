package iskallia.vault.item;

import iskallia.vault.block.PuzzleRuneBlock;
import iskallia.vault.init.ModAttributes;
import iskallia.vault.init.ModBlocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.Item.Properties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class PuzzleRuneItem extends BasicItem {
   public PuzzleRuneItem(ResourceLocation id, Properties properties) {
      super(id, properties);
   }

   public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context) {
      PlayerEntity player = context.func_195999_j();
      World world = context.func_195991_k();
      if (player != null
         && player.func_184812_l_()
         && !world.field_72995_K
         && world.func_180495_p(context.func_195995_a()).getBlockState().func_177230_c() != ModBlocks.PUZZLE_RUNE_BLOCK) {
         ModAttributes.PUZZLE_COLOR.create(stack, ModAttributes.PUZZLE_COLOR.getOrCreate(stack, PuzzleRuneBlock.Color.YELLOW).getValue(stack).next());
         ItemRelicBoosterPack.successEffects(world, player.func_213303_ch());
         return ActionResultType.SUCCESS;
      } else {
         return super.onItemUseFirst(stack, context);
      }
   }
}
