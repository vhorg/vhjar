package iskallia.vault.gear.trinket.effects;

import iskallia.vault.gear.trinket.TrinketEffect;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class WingsTrinket extends TrinketEffect.Simple {
   public WingsTrinket(ResourceLocation name) {
      super(name);
   }

   @Override
   public void onWornTick(LivingEntity entity, ItemStack stack) {
      super.onWornTick(entity, stack);
      int ticks = entity.getFallFlyingTicks();
      if (ticks > 0 && entity.isFallFlying()) {
         stack.elytraFlightTick(entity, ticks);
      }
   }
}
