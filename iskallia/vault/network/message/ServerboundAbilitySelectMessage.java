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

public class ServerboundAbilitySelectMessage {
   private final String selectedAbility;

   private ServerboundAbilitySelectMessage(String selectedAbility) {
      this.selectedAbility = selectedAbility;
   }

   public static void send(String selectedAbility) {
      ModNetwork.CHANNEL.sendToServer(new ServerboundAbilitySelectMessage(selectedAbility));
   }

   public static void encode(ServerboundAbilitySelectMessage message, FriendlyByteBuf buffer) {
      buffer.writeUtf(message.selectedAbility);
   }

   public static ServerboundAbilitySelectMessage decode(FriendlyByteBuf buffer) {
      return new ServerboundAbilitySelectMessage(buffer.readUtf());
   }

   public static void handle(ServerboundAbilitySelectMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         ServerPlayer sender = context.getSender();
         if (sender != null) {
            PlayerAbilitiesData abilitiesData = PlayerAbilitiesData.get((ServerLevel)sender.level);
            AbilityTree abilityTree = abilitiesData.getAbilities(sender);
            abilityTree.onQuickSelect(message.selectedAbility, SkillContext.of(sender), sender);
         }
      });
      context.setPacketHandled(true);
   }
}
