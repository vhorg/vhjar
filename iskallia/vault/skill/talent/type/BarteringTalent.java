package iskallia.vault.skill.talent.type;

import com.google.gson.annotations.Expose;
import iskallia.vault.event.event.ShopPedestalPriceEvent;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.util.MiscUtils;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class BarteringTalent extends PlayerTalent {
   @Expose
   private float costReduction;

   public BarteringTalent(int cost, float costReduction) {
      super(cost);
      this.costReduction = costReduction;
   }

   public float getCostReduction() {
      return this.costReduction;
   }

   @SubscribeEvent
   public static void adjustPrice(ShopPedestalPriceEvent event) {
      MiscUtils.getTalent(event.getPlayer(), ModConfigs.TALENTS.BARTERING).ifPresent(node -> {
         float multiplier = 1.0F - node.getTalent().getCostReduction();
         ItemStack costStack = event.getCost().copy();
         costStack.setCount(Mth.floor(costStack.getCount() * multiplier));
         event.setNewCost(costStack);
      });
   }
}
