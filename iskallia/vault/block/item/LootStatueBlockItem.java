package iskallia.vault.block.item;

import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.util.NameProviderPublic;
import iskallia.vault.util.StatueType;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.Properties;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class LootStatueBlockItem extends BlockItem {
   private final StatueType type;

   public LootStatueBlockItem(Block block, StatueType type) {
      super(block, new Properties().func_200916_a(ModItems.VAULT_MOD_GROUP).func_200917_a(1));
      this.type = type;
   }

   @OnlyIn(Dist.CLIENT)
   public void func_77624_a(ItemStack stack, @Nullable World worldIn, List<ITextComponent> toolTip, ITooltipFlag flagIn) {
      CompoundNBT nbt = stack.func_77978_p();
      if (nbt != null && nbt.func_150297_b("BlockEntityTag", 10)) {
         this.addStatueInformation(nbt.func_74775_l("BlockEntityTag"), toolTip);
      }

      super.func_77624_a(stack, worldIn, toolTip, flagIn);
   }

   public void func_77663_a(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
      super.func_77663_a(stack, world, entity, itemSlot, isSelected);
      if (!world.func_201670_d()) {
         CompoundNBT tag = stack.func_190925_c("BlockEntityTag");
         if (!tag.func_150297_b("PlayerNickname", 8)) {
            String name = NameProviderPublic.getRandomName();
            initRandomStatue(tag, this.type, name);
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   protected void addStatueInformation(CompoundNBT dataTag, List<ITextComponent> toolTip) {
      String nickname = dataTag.func_74779_i("PlayerNickname");
      toolTip.add(new StringTextComponent("Player: "));
      toolTip.add(new StringTextComponent("- ").func_230529_a_(new StringTextComponent(nickname).func_240699_a_(TextFormatting.GOLD)));
      if (this.type.dropsItems()) {
         ITextComponent itemDescriptor = new StringTextComponent("NOT SELECTED").func_240699_a_(TextFormatting.RED);
         if (dataTag.func_74764_b("LootItem")) {
            ItemStack lootItem = ItemStack.func_199557_a(dataTag.func_74775_l("LootItem"));
            itemDescriptor = new StringTextComponent(lootItem.func_200301_q().getString()).func_240699_a_(TextFormatting.GREEN);
         }

         toolTip.add(StringTextComponent.field_240750_d_);
         toolTip.add(new StringTextComponent("Item: ").func_240699_a_(TextFormatting.WHITE));
         toolTip.add(new StringTextComponent("- ").func_230529_a_(itemDescriptor));
      }
   }

   private static StatueType getStatueType(ItemStack stack) {
      return stack.func_77973_b() instanceof LootStatueBlockItem ? ((LootStatueBlockItem)stack.func_77973_b()).type : StatueType.GIFT_NORMAL;
   }

   public static ItemStack getStatueBlockItem(String nickname, StatueType type) {
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
         case OMEGA:
            itemStack = new ItemStack(ModBlocks.OMEGA_STATUE);
            break;
         case OMEGA_VARIANT:
            itemStack = new ItemStack(ModBlocks.OMEGA_STATUE_VARIANT);
      }

      CompoundNBT nbt = new CompoundNBT();
      initRandomStatue(nbt, type, nickname);
      CompoundNBT stackNBT = new CompoundNBT();
      stackNBT.func_218657_a("BlockEntityTag", nbt);
      itemStack.func_77982_d(stackNBT);
      return itemStack;
   }

   private static void initRandomStatue(CompoundNBT out, StatueType type, String name) {
      out.func_74778_a("PlayerNickname", name);
      out.func_74768_a("StatueType", type.ordinal());
      if (type.dropsItems()) {
         out.func_74768_a("Interval", ModConfigs.STATUE_LOOT.getInterval(type));
         if (!type.isOmega()) {
            ItemStack loot = ModConfigs.STATUE_LOOT.randomLoot(type);
            out.func_218657_a("LootItem", loot.serializeNBT());
         }

         int decay = ModConfigs.STATUE_LOOT.getDecay(type);
         out.func_74768_a("ItemsRemaining", decay);
         out.func_74768_a("TotalItems", decay);
      }
   }
}
