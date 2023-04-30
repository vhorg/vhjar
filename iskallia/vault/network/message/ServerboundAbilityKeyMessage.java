package iskallia.vault.network.message;

import iskallia.vault.init.ModNetwork;
import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.skill.tree.AbilityTree;
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
      Context ctx = contextSupplier.get();
      ctx.enqueueWork(() -> {
         ServerPlayer sender = ctx.getSender();
         if (sender != null) {
            PlayerAbilitiesData abilitiesData = PlayerAbilitiesData.get((ServerLevel)sender.level);
            AbilityTree abilityTree = abilitiesData.getAbilities(sender);
            SkillContext context = SkillContext.of(sender);
            switch (message.opcode) {
               case KeyUp:
                  abilityTree.onKeyUp(context);
                  break;
               case KeyDown:
                  abilityTree.onKeyDown(context);
                  break;
               case CancelKeyDown:
                  abilityTree.onCancelKeyDown(context);
            }
         }
      });
      ctx.setPacketHandled(true);
   }

   public static enum Opcode {
      KeyUp,
      KeyDown,
      ScrollUp,
      ScrollDown,
      CancelKeyDown;
   }
}
