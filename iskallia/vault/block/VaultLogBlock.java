package iskallia.vault.block;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class VaultLogBlock extends RotatedPillarBlock {
   private final Supplier<Block> stripped;

   public static VaultLogBlock log(Supplier<Block> stripped, MaterialColor topColor, MaterialColor barkColor) {
      return new VaultLogBlock(stripped, topColor, barkColor);
   }

   public static VaultLogBlock stripped(MaterialColor topColor, MaterialColor barkColor) {
      return new VaultLogBlock(null, topColor, barkColor);
   }

   private VaultLogBlock(Supplier<Block> stripped, MaterialColor topColor, MaterialColor barkColor) {
      super(
         Properties.of(Material.WOOD, state -> state.getValue(RotatedPillarBlock.AXIS) == Axis.Y ? topColor : barkColor).strength(2.0F).sound(SoundType.WOOD)
      );
      this.stripped = stripped;
   }

   public BlockState getStripped(BlockState existing) {
      return this.stripped.get().withPropertiesOf(existing);
   }

   public boolean isStrippable() {
      return this.stripped != null;
   }

   @SubscribeEvent
   public static void onStripLog(RightClickBlock event) {
      if (event.getHand() == InteractionHand.MAIN_HAND) {
         ItemStack itemStack = event.getItemStack();
         if (itemStack.getItem() instanceof AxeItem axe) {
            if (event.getPlayer() instanceof ServerPlayer player) {
               ServerLevel var11 = player.getLevel();
               BlockPos pos = event.getPos();
               BlockState state = var11.getBlockState(pos);
               if (state.getBlock() instanceof VaultLogBlock vaultLog && vaultLog.isStrippable()) {
                  BlockState stripped = vaultLog.getStripped(state);
                  var11.setBlock(pos, stripped, 3);
                  var11.playSound(null, pos, SoundEvents.AXE_STRIP, SoundSource.BLOCKS, 1.0F, 1.0F);
                  itemStack.hurtAndBreak(1, player, entity -> entity.broadcastBreakEvent(EquipmentSlot.MAINHAND));
               }
            }
         }
      }
   }
}
