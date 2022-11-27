package iskallia.vault.item.consumable;

import iskallia.vault.init.ModItems;
import iskallia.vault.util.calc.AbsorptionHelper;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.food.FoodProperties.Builder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;

public class AbsorptionAppleItem extends Item {
   public static FoodProperties VAULT_FOOD = new Builder().saturationMod(0.0F).nutrition(0).fast().alwaysEat().build();

   public AbsorptionAppleItem(ResourceLocation id) {
      super(new Properties().tab(ModItems.VAULT_MOD_GROUP).food(VAULT_FOOD).stacksTo(64));
      this.setRegistryName(id);
   }

   public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
      super.appendHoverText(stack, level, tooltip, flag);
      tooltip.add(new TextComponent("Gives you +1 temporary heart"));
   }

   public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity entityLiving) {
      if (entityLiving instanceof ServerPlayer sPlayer) {
         float targetAbsorption = sPlayer.getAbsorptionAmount() + 2.0F;
         if (targetAbsorption > AbsorptionHelper.getMaxAbsorption(sPlayer)) {
            return stack;
         }

         sPlayer.setAbsorptionAmount(targetAbsorption);
      }

      return super.finishUsingItem(stack, world, entityLiving);
   }
}
