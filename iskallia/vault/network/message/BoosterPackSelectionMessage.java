package iskallia.vault.network.message;

import iskallia.vault.init.ModSounds;
import iskallia.vault.item.BoosterPackItem;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent.Context;

public record BoosterPackSelectionMessage(int index) {
   public static void encode(BoosterPackSelectionMessage message, FriendlyByteBuf buffer) {
      buffer.writeInt(message.index);
   }

   public static BoosterPackSelectionMessage decode(FriendlyByteBuf buffer) {
      return new BoosterPackSelectionMessage(buffer.readInt());
   }

   public static void handle(BoosterPackSelectionMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         ServerPlayer player = context.getSender();
         ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
         if (stack.getItem() instanceof BoosterPackItem) {
            List<ItemStack> outcomes = BoosterPackItem.getOutcomes(stack);
            if (outcomes != null && message.index >= 0 && message.index < outcomes.size()) {
               player.setItemInHand(InteractionHand.MAIN_HAND, outcomes.get(message.index()).copy());
               successEffects(player.level, player.position());
            }
         }
      });
      context.setPacketHandled(true);
   }

   public static void successEffects(Level world, Vec3 pos) {
      world.playSound(null, pos.x, pos.y, pos.z, ModSounds.BOOSTER_PACK_SUCCESS_SFX, SoundSource.PLAYERS, 1.0F, 1.0F);
      ((ServerLevel)world).sendParticles(ParticleTypes.DRAGON_BREATH, pos.x, pos.y, pos.z, 500, 1.0, 1.0, 1.0, 0.5);
   }
}
