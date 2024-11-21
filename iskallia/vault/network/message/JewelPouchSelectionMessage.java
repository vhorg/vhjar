package iskallia.vault.network.message;

import iskallia.vault.core.random.JavaRandom;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.item.JewelPouchItem;
import iskallia.vault.util.LootInitialization;
import iskallia.vault.world.data.PlayerVaultStatsData;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent.Context;

public record JewelPouchSelectionMessage(int index) {
   private static final RandomSource random = JavaRandom.ofNanoTime();

   public static void encode(JewelPouchSelectionMessage message, FriendlyByteBuf buffer) {
      buffer.writeInt(message.index);
   }

   public static JewelPouchSelectionMessage decode(FriendlyByteBuf buffer) {
      return new JewelPouchSelectionMessage(buffer.readInt());
   }

   public static void handle(JewelPouchSelectionMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(
         () -> {
            ServerPlayer sPlayer = context.getSender();
            ItemStack stack = sPlayer.getItemInHand(InteractionHand.MAIN_HAND);
            if (stack.getItem() instanceof JewelPouchItem) {
               List<JewelPouchItem.RolledJewel> outcomes = JewelPouchItem.getJewels(stack);
               if (message.index >= 0 && message.index < outcomes.size()) {
                  JewelPouchItem.RolledJewel outcome = outcomes.get(message.index);
                  ItemStack result = outcome.stack().copy();
                  if (!outcome.identified()) {
                     int vaultLevel = JewelPouchItem.getStoredLevel(stack)
                        .orElseGet(() -> PlayerVaultStatsData.get(sPlayer.getLevel()).getVaultStats(sPlayer).getVaultLevel());
                     result = LootInitialization.initializeVaultLoot(result, vaultLevel);
                  }

                  sPlayer.setItemInHand(InteractionHand.MAIN_HAND, result.copy());
                  successEffects(sPlayer.level, sPlayer.position());
               }
            }
         }
      );
      context.setPacketHandled(true);
   }

   public static void successEffects(Level world, Vec3 pos) {
      world.playSound(null, pos.x, pos.y, pos.z, SoundEvents.AXE_STRIP, SoundSource.PLAYERS, 1.0F, 2.0F);
      ((ServerLevel)world).sendParticles(ParticleTypes.DRAGON_BREATH, pos.x, pos.y, pos.z, 500, 1.0, 1.0, 1.0, 0.5);
   }
}
