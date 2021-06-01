package iskallia.vault.block.item;

import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.util.StatueType;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.Properties;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.world.World;

public class LootStatueBlockItem extends BlockItem {
   public LootStatueBlockItem(Block block) {
      super(block, new Properties().func_200916_a(ModItems.VAULT_MOD_GROUP).func_200917_a(1));
   }

   public void func_77624_a(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
      CompoundNBT nbt = stack.func_77978_p();
      if (nbt != null) {
         CompoundNBT blockEntityTag = nbt.func_74775_l("BlockEntityTag");
         String nickname = blockEntityTag.func_74779_i("PlayerNickname");
         StringTextComponent text = new StringTextComponent(" Nickname: " + nickname);
         text.func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(-26266)));
         tooltip.add(text);
      }

      super.func_77624_a(stack, worldIn, tooltip, flagIn);
   }

   public static ItemStack forVaultBoss(String nickname, int variant, boolean hasCrown) {
      return getStatueBlockItem(nickname, StatueType.values()[variant], hasCrown, false);
   }

   public static ItemStack forArenaChampion(String nickname, int variant, boolean hasCrown) {
      return getStatueBlockItem(nickname, StatueType.values()[variant], hasCrown, false);
   }

   public static ItemStack forGift(String nickname, int variant, boolean hasCrown) {
      return getStatueBlockItem(nickname, StatueType.values()[variant], hasCrown, false);
   }

   public static ItemStack getStatueBlockItem(String nickname, StatueType type, boolean hasCrown, boolean blankStatue) {
      ItemStack itemStack = ItemStack.field_190927_a;
      switch (type) {
         case GIFT_NORMAL:
            itemStack = new ItemStack(ModBlocks.GIFT_NORMAL_STATUE);
            break;
         case GIFT_MEGA:
            itemStack = new ItemStack(ModBlocks.GIFT_MEGA_STATUE);
            break;
         case VAULT_BOSS:
            itemStack = new ItemStack(ModBlocks.VAULT_PLAYER_LOOT_STATUE);
            break;
         case ARENA_CHAMPION:
            itemStack = new ItemStack(ModBlocks.ARENA_PLAYER_LOOT_STATUE);
      }

      CompoundNBT nbt = new CompoundNBT();
      nbt.func_74778_a("PlayerNickname", nickname);
      nbt.func_74768_a("StatueType", type.ordinal());
      nbt.func_74768_a("Interval", ModConfigs.STATUE_LOOT.getInterval(type));
      ItemStack loot;
      if (blankStatue) {
         loot = ModConfigs.STATUE_LOOT.getLoot();
      } else {
         loot = ModConfigs.STATUE_LOOT.randomLoot(type);
      }

      nbt.func_218657_a("LootItem", loot.serializeNBT());
      nbt.func_74757_a("HasCrown", hasCrown);
      CompoundNBT stackNBT = new CompoundNBT();
      stackNBT.func_218657_a("BlockEntityTag", nbt);
      itemStack.func_77982_d(stackNBT);
      return itemStack;
   }
}
