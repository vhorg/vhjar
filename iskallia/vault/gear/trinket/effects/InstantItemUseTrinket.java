package iskallia.vault.gear.trinket.effects;

import iskallia.vault.gear.trinket.TrinketEffect;
import iskallia.vault.gear.trinket.TrinketHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent.Tick;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class InstantItemUseTrinket extends TrinketEffect.Simple {
   public InstantItemUseTrinket(ResourceLocation name) {
      super(name);
   }

   @SubscribeEvent
   public static void onItemUse(Tick event) {
      if (event.getEntityLiving() instanceof Player player) {
         if (!TrinketHelper.getTrinkets(player, InstantItemUseTrinket.class).stream().anyMatch(trinket -> trinket.isUsable(player))) {
            ItemStack inUse = event.getItem();
            if (inUse.isEdible() || inUse.getUseAnimation() == UseAnim.EAT) {
               event.setDuration(1);
            }
         }
      }
   }
}
