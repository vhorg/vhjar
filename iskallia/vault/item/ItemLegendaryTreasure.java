package iskallia.vault.item;

import iskallia.vault.init.ModConfigs;
import iskallia.vault.util.VaultRarity;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemLegendaryTreasure extends Item {
   private VaultRarity vaultRarity;

   public ItemLegendaryTreasure(ItemGroup group, ResourceLocation id, VaultRarity vaultRarity) {
      super(new Properties().func_200916_a(group).func_200917_a(1));
      this.setRegistryName(id);
      this.vaultRarity = vaultRarity;
   }

   public ActionResult<ItemStack> func_77659_a(World worldIn, PlayerEntity playerIn, Hand handIn) {
      if (worldIn.field_72995_K) {
         return super.func_77659_a(worldIn, playerIn, handIn);
      } else if (handIn != Hand.MAIN_HAND) {
         return super.func_77659_a(worldIn, playerIn, handIn);
      } else {
         ItemStack stack = playerIn.func_184614_ca();
         if (stack.func_77973_b() instanceof ItemLegendaryTreasure) {
            ItemLegendaryTreasure item = (ItemLegendaryTreasure)stack.func_77973_b();
            ItemStack toDrop = ItemStack.field_190927_a;
            switch (item.getRarity()) {
               case COMMON:
                  toDrop = ModConfigs.LEGENDARY_TREASURE_NORMAL.getRandom();
                  break;
               case RARE:
                  toDrop = ModConfigs.LEGENDARY_TREASURE_RARE.getRandom();
                  break;
               case EPIC:
                  toDrop = ModConfigs.LEGENDARY_TREASURE_EPIC.getRandom();
                  break;
               case OMEGA:
                  toDrop = ModConfigs.LEGENDARY_TREASURE_OMEGA.getRandom();
            }

            playerIn.func_71019_a(toDrop, false);
            stack.func_190918_g(1);
            ItemRelicBoosterPack.successEffects(worldIn, playerIn.func_213303_ch());
         }

         return super.func_77659_a(worldIn, playerIn, handIn);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void func_77624_a(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
      if (stack.func_77973_b() instanceof ItemLegendaryTreasure) {
         ItemLegendaryTreasure item = (ItemLegendaryTreasure)stack.func_77973_b();
         tooltip.add(new StringTextComponent(TextFormatting.GOLD + "Right-Click to identify..."));
         tooltip.add(new StringTextComponent("Rarity: " + item.getRarity().color + item.getRarity()));
      }

      super.func_77624_a(stack, worldIn, tooltip, flagIn);
   }

   public ITextComponent func_200295_i(ItemStack stack) {
      if (stack.func_77973_b() instanceof ItemLegendaryTreasure) {
         ItemLegendaryTreasure item = (ItemLegendaryTreasure)stack.func_77973_b();
         return new StringTextComponent(item.getRarity().color + "Legendary Treasure");
      } else {
         return super.func_200295_i(stack);
      }
   }

   public VaultRarity getRarity() {
      return this.vaultRarity;
   }
}
