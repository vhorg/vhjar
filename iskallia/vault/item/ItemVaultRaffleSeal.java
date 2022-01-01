package iskallia.vault.item;

import iskallia.vault.init.ModItems;
import iskallia.vault.util.MiscUtils;
import iskallia.vault.util.NameProviderPublic;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.Properties;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemVaultRaffleSeal extends Item {
   public ItemVaultRaffleSeal(ResourceLocation id) {
      super(new Properties().func_200916_a(ModItems.VAULT_MOD_GROUP).func_200917_a(1));
      this.setRegistryName(id);
   }

   public static void setPlayerName(ItemStack stack, String name) {
      stack.func_196082_o().func_74778_a("PlayerName", name);
   }

   public static String getPlayerName(ItemStack stack) {
      return stack.func_196082_o().func_74779_i("PlayerName");
   }

   public void func_77663_a(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
      if (world instanceof ServerWorld && entity instanceof ServerPlayerEntity) {
         ServerWorld sWorld = (ServerWorld)world;
         ServerPlayerEntity player = (ServerPlayerEntity)entity;
         if (stack.func_190916_E() > 1) {
            while (stack.func_190916_E() > 1) {
               stack.func_190918_g(1);
               ItemStack gearPiece = new ItemStack(this);
               MiscUtils.giveItem(player, gearPiece);
            }
         }

         String raffleName = getPlayerName(stack);
         if (raffleName.isEmpty()) {
            setPlayerName(stack, NameProviderPublic.getRandomName());
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void func_77624_a(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
      tooltip.add(
         new StringTextComponent("Turns a crystal into a ")
            .func_240699_a_(TextFormatting.GRAY)
            .func_230529_a_(new StringTextComponent("Raffle").func_240699_a_(TextFormatting.GOLD))
            .func_230529_a_(new StringTextComponent(" crystal.").func_240699_a_(TextFormatting.GRAY))
      );
      String raffleName = getPlayerName(stack);
      if (!raffleName.isEmpty()) {
         tooltip.add(
            new StringTextComponent("Player Boss: ")
               .func_240699_a_(TextFormatting.GRAY)
               .func_230529_a_(new StringTextComponent(raffleName).func_240699_a_(TextFormatting.GREEN))
         );
      } else {
         tooltip.add(
            new StringTextComponent("Player Boss: ")
               .func_240699_a_(TextFormatting.GRAY)
               .func_230529_a_(new StringTextComponent("???").func_240699_a_(TextFormatting.GREEN))
         );
      }
   }
}
