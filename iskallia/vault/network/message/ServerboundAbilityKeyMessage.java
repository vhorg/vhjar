package iskallia.vault.network.message;

import iskallia.vault.init.ModNetwork;
import iskallia.vault.skill.ability.AbilityTree;
import iskallia.vault.world.data.PlayerAbilitiesData;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent.Context;

public class ServerboundAbilityKeyMessage {
   private final ServerboundAbilityKeyMessage.Opcode opcode;

   private ServerboundAbilityKeyMessage(ServerboundAbilityKeyMessage.Opcode opcode) {
      this.opcode = opcode;
   }

   public static void send(ServerboundAbilityKeyMessage.Opcode opcode) {
      ModNetwork.CHANNEL.sendToServer(new ServerboundAbilityKeyMessage(opcode));
   }

   public static void encode(ServerboundAbilityKeyMessage message, FriendlyByteBuf buffer) {
      buffer.writeEnum(message.opcode);
   }

   public static ServerboundAbilityKeyMessage decode(FriendlyByteBuf buffer) {
      return new ServerboundAbilityKeyMessage((ServerboundAbilityKeyMessage.Opcode)buffer.readEnum(ServerboundAbilityKeyMessage.Opcode.class));
   }

   public static void handle(ServerboundAbilityKeyMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         ServerPlayer sender = context.getSender();
         if (sender != null) {
            PlayerAbilitiesData abilitiesData = PlayerAbilitiesData.get((ServerLevel)sender.level);
            AbilityTree abilityTree = abilitiesData.getAbilities(sender);
            switch (message.opcode) {
               case KeyUp:
                  abilityTree.keyUp(sender.server);
                  break;
               case KeyDown:
                  abilityTree.keyDown(sender.server);
                  break;
               case ScrollUp:
                  abilityTree.scrollUp(sender.server);
                  break;
               case ScrollDown:
                  abilityTree.scrollDown(sender.server);
                  break;
               case CancelKeyDown:
                  abilityTree.cancelKeyDown(sender.server);
            }
         }
      });
      context.setPacketHandled(true);
   }

   public static enum Opcode {
      KeyUp,
      KeyDown,
      ScrollUp,
      ScrollDown,
      CancelKeyDown;
   }
}
