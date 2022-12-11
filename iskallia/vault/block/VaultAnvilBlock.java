package iskallia.vault.block;

import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class VaultAnvilBlock extends AnvilBlock {
   public VaultAnvilBlock() {
      super(Properties.copy(Blocks.ANVIL).requiresCorrectToolForDrops().strength(5.0F, 1200.0F).sound(SoundType.ANVIL));
   }

   @SubscribeEvent
   public static void onAnvilRepair(AnvilRepairEvent event) {
      event.setBreakChance(0.0F);
   }
}
