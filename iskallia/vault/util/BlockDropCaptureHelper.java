package iskallia.vault.util;

import java.util.Stack;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class BlockDropCaptureHelper {
   private static final Stack<NonNullList<ItemStack>> capturing = new Stack<>();

   private BlockDropCaptureHelper() {
   }

   @SubscribeEvent
   public static void onDrop(EntityJoinWorldEvent event) {
      if (event.getWorld() instanceof ServerWorld && event.getEntity() instanceof ItemEntity) {
         ItemStack itemStack = ((ItemEntity)event.getEntity()).func_92059_d();
         if (!capturing.isEmpty()) {
            event.setCanceled(true);
            if (!itemStack.func_190926_b() && !capturing.isEmpty()) {
               capturing.peek().add(itemStack);
            }

            event.getEntity().func_70106_y();
         }
      }
   }

   public static void startCapturing() {
      capturing.push(NonNullList.func_191196_a());
   }

   public static NonNullList<ItemStack> getCapturedStacksAndStop() {
      return capturing.pop();
   }
}
