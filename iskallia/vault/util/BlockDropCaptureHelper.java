package iskallia.vault.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class BlockDropCaptureHelper {
   private static final Stack<List<ItemEntity>> capturing = new Stack<>();

   private BlockDropCaptureHelper() {
   }

   @SubscribeEvent
   public static void onDrop(EntityJoinWorldEvent event) {
      if (event.getWorld() instanceof ServerWorld && event.getEntity() instanceof ItemEntity) {
         ItemStack itemStack = ((ItemEntity)event.getEntity()).func_92059_d();
         if (!capturing.isEmpty()) {
            event.setCanceled(true);
            if (!itemStack.func_190926_b() && !capturing.isEmpty()) {
               capturing.peek().add((ItemEntity)event.getEntity());
            }

            event.getEntity().func_70106_y();
         }
      }
   }

   public static void startCapturing() {
      capturing.push(new ArrayList<>());
   }

   public static List<ItemEntity> getCapturedStacksAndStop() {
      return capturing.pop();
   }
}
