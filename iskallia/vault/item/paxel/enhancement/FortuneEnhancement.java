package iskallia.vault.item.paxel.enhancement;

import iskallia.vault.event.ActiveFlags;
import iskallia.vault.util.BlockDropCaptureHelper;
import iskallia.vault.util.BlockHelper;
import iskallia.vault.util.OverlevelEnchantHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Color;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class FortuneEnhancement extends PaxelEnhancement {
   protected int extraFortune;

   public FortuneEnhancement(int extraFortune) {
      this.extraFortune = extraFortune;
   }

   public int getExtraFortune() {
      return this.extraFortune;
   }

   @Override
   public Color getColor() {
      return Color.func_240743_a_(-22784);
   }

   @Override
   public CompoundNBT serializeNBT() {
      CompoundNBT nbt = super.serializeNBT();
      nbt.func_74768_a("ExtraFortune", this.extraFortune);
      return nbt;
   }

   @Override
   public void deserializeNBT(CompoundNBT nbt) {
      super.deserializeNBT(nbt);
      this.extraFortune = nbt.func_74762_e("ExtraFortune");
   }

   @SubscribeEvent(
      priority = EventPriority.LOW
   )
   public static void onBlockMined(BreakEvent event) {
      ServerPlayerEntity player = (ServerPlayerEntity)event.getPlayer();
      ItemStack heldStack = player.func_184614_ca();
      PaxelEnhancement enhancement = PaxelEnhancements.getEnhancement(heldStack);
      if (enhancement instanceof FortuneEnhancement) {
         FortuneEnhancement fortuneEnhancement = (FortuneEnhancement)enhancement;
         ActiveFlags.IS_FORTUNE_MINING.runIfNotSet(() -> {
            ServerWorld world = (ServerWorld)event.getWorld();
            ItemStack miningStack = OverlevelEnchantHelper.increaseFortuneBy(heldStack.func_77946_l(), fortuneEnhancement.getExtraFortune());
            BlockPos pos = event.getPos();
            BlockDropCaptureHelper.startCapturing();

            try {
               BlockHelper.breakBlock(world, player, pos, world.func_180495_p(pos), miningStack, true, true);
               BlockHelper.damageMiningItem(heldStack, player, 1);
            } finally {
               BlockDropCaptureHelper.getCapturedStacksAndStop().forEach(stack -> Block.func_180635_a(world, pos, stack));
            }

            event.setCanceled(true);
         });
      }
   }
}
