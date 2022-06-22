package iskallia.vault.event;

import iskallia.vault.util.flag.ExplosionImmune;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.world.ExplosionEvent.Detonate;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   modid = "the_vault",
   bus = Bus.FORGE
)
public class ExplosionEvents {
   @SubscribeEvent
   static void preventBlocksFromExploding(Detonate event) {
      if (!event.getWorld().field_72995_K) {
         ServerWorld world = (ServerWorld)event.getWorld();
         event.getAffectedBlocks().removeIf(blockPos -> {
            BlockState blockState = world.func_180495_p(blockPos);
            return blockState == null ? false : blockState.func_177230_c() instanceof ExplosionImmune;
         });
      }
   }

   @SubscribeEvent
   static void preventItemsFromExploding(Detonate event) {
      if (!event.getWorld().field_72995_K) {
         event.getAffectedEntities().removeIf(entity -> {
            if (!(entity instanceof ItemEntity)) {
               return false;
            } else {
               Item item = ((ItemEntity)entity).func_92059_d().func_77973_b();
               return item instanceof ExplosionImmune;
            }
         });
      }
   }
}
