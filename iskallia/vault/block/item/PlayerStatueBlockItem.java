package iskallia.vault.block.item;

import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModItems;
import iskallia.vault.util.StatueType;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.Properties;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.world.World;

public class PlayerStatueBlockItem extends BlockItem {
   public PlayerStatueBlockItem() {
      super(ModBlocks.PLAYER_STATUE, new Properties().func_200916_a(ModItems.VAULT_MOD_GROUP).func_200917_a(1));
   }

   public void func_77624_a(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
      CompoundNBT nbt = stack.func_77978_p();
      if (nbt != null) {
         CompoundNBT blockEntityTag = nbt.func_74775_l("BlockEntityTag");
         String nickname = blockEntityTag.func_74779_i("PlayerNickname");
         boolean hasCrown = blockEntityTag.func_74767_n("HasCrown");
         StringTextComponent titleText = new StringTextComponent(hasCrown ? " Arena Champion" : " Vault Boss");
         titleText.func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(-26266)));
         tooltip.add(titleText);
         StringTextComponent text = new StringTextComponent(" Nickname: " + nickname);
         text.func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(-26266)));
         tooltip.add(text);
         super.func_77624_a(stack, worldIn, tooltip, flagIn);
      } else {
         tooltip.add(new StringTextComponent(""));
         tooltip.add(new StringTextComponent("Statue: "));
         StringTextComponent tip = new StringTextComponent(" Right-click to generate trade!");
         tip.func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(16755200)));
         tooltip.add(tip);
      }
   }

   protected boolean func_195944_a(BlockItemUseContext p_195944_1_, BlockState p_195944_2_) {
      return false;
   }

   public ActionResult<ItemStack> func_77659_a(World worldIn, PlayerEntity playerIn, Hand handIn) {
      if (handIn == Hand.OFF_HAND) {
         return super.func_77659_a(worldIn, playerIn, handIn);
      } else {
         ItemStack statue = LootStatueBlockItem.getStatueBlockItem(
            playerIn.func_200200_C_().getString(), StatueType.values()[worldIn.field_73012_v.nextInt(StatueType.values().length)], false, true
         );
         playerIn.func_184611_a(Hand.MAIN_HAND, statue);
         return super.func_77659_a(worldIn, playerIn, handIn);
      }
   }
}
