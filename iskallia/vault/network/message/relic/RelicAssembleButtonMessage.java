package iskallia.vault.network.message.relic;

import iskallia.vault.block.RelicPedestalBlock;
import iskallia.vault.container.RelicPedestalContainer;
import iskallia.vault.container.inventory.RelicPedestalInventory;
import iskallia.vault.init.ModRelics;
import iskallia.vault.world.data.DiscoveredRelicsData;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkEvent.Context;

public record RelicAssembleButtonMessage() {
   private static final RelicAssembleButtonMessage instance = new RelicAssembleButtonMessage();

   public static void encode(RelicAssembleButtonMessage message, FriendlyByteBuf buffer) {
   }

   public static RelicAssembleButtonMessage decode(FriendlyByteBuf buffer) {
      return instance;
   }

   public static void handle(RelicAssembleButtonMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         ServerPlayer sender = context.getSender();
         if (sender.containerMenu instanceof RelicPedestalContainer container && sender != null && container.recipeFulfilled()) {
            ModRelics.RelicRecipe relicRecipe = ModRelics.RECIPE_REGISTRY.get(container.getSelectedRelicId());
            if (relicRecipe == null) {
               return;
            }

            BlockPos pedestalPos = container.getPedestalPos();
            ServerLevel world = sender.getLevel();
            BlockState blockState = world.getBlockState(pedestalPos);
            if (!(blockState.getBlock() instanceof RelicPedestalBlock)) {
               sender.closeContainer();
               return;
            }

            DiscoveredRelicsData discoveredRelicsData = DiscoveredRelicsData.get((ServerLevel)sender.level);
            discoveredRelicsData.discoverRelicAndBroadcast(sender, relicRecipe);
            BlockState newBlockState = (BlockState)blockState.setValue(RelicPedestalBlock.RELIC, relicRecipe);
            world.setBlock(pedestalPos, newBlockState, 3);
            world.playSound(null, pedestalPos, SoundEvents.BEACON_POWER_SELECT, SoundSource.MASTER, 1.0F, 1.0F);
            world.sendParticles(ParticleTypes.CLOUD, pedestalPos.getX(), pedestalPos.getY(), pedestalPos.getZ(), 1000, 1.0, 3.0, 1.0, 0.2);
            RelicPedestalInventory internalInventory = container.getInternalInventory();
            internalInventory.forEachInput(relativeIndex -> internalInventory.removeItem(relativeIndex, 1));
            container.sendAllDataToRemote();
            sender.closeContainer();
         }
      });
      context.setPacketHandled(true);
   }
}
