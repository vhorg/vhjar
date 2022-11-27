package iskallia.vault.network.message;

import iskallia.vault.container.VaultArtisanStationContainer;
import iskallia.vault.gear.modification.GearModification;
import iskallia.vault.gear.modification.GearModificationAction;
import iskallia.vault.gear.modification.GearModificationRegistry;
import iskallia.vault.init.ModSounds;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent.Context;

public class VaultArtisanRequestModificationMessage {
   private final ResourceLocation modification;

   public VaultArtisanRequestModificationMessage(ResourceLocation modification) {
      this.modification = modification;
   }

   public static void encode(VaultArtisanRequestModificationMessage message, FriendlyByteBuf buffer) {
      buffer.writeResourceLocation(message.modification);
   }

   public static VaultArtisanRequestModificationMessage decode(FriendlyByteBuf buffer) {
      return new VaultArtisanRequestModificationMessage(buffer.readResourceLocation());
   }

   public static void handle(VaultArtisanRequestModificationMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(
         () -> {
            ServerPlayer requester = context.getSender();
            if (requester != null && requester.containerMenu instanceof VaultArtisanStationContainer container) {
               GearModification modification = GearModificationRegistry.getModification(message.modification);
               if (modification != null) {
                  GearModificationAction action = container.getModificationAction(modification);
                  if (action != null) {
                     if (action.canApply(container, requester)) {
                        action.apply(container, requester);
                        Level level = requester.getLevel();
                        level.playSound(
                           null, container.getTilePos(), ModSounds.ARTISAN_SMITHING, SoundSource.BLOCKS, 0.2F, level.random.nextFloat() * 0.1F + 0.9F
                        );
                     }
                  }
               }
            }
         }
      );
      context.setPacketHandled(true);
   }
}
