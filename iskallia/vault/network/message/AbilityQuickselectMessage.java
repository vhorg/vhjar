package iskallia.vault.network.message;

import iskallia.vault.init.ModConfigs;
import iskallia.vault.skill.ability.AbilityNode;
import iskallia.vault.skill.ability.AbilityTree;
import iskallia.vault.skill.ability.KeyBehavior;
import iskallia.vault.skill.ability.group.AbilityGroup;
import iskallia.vault.world.data.PlayerAbilitiesData;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent.Context;

public class AbilityQuickselectMessage {
   public static final int RELEASED = 0;
   public static final int PRESSED = 1;
   private final String abilityName;
   private final int action;

   public AbilityQuickselectMessage(String abilityName, int action) {
      this.abilityName = abilityName;
      this.action = action;
   }

   public static void encode(AbilityQuickselectMessage pkt, FriendlyByteBuf buffer) {
      buffer.writeUtf(pkt.abilityName);
      buffer.writeVarInt(pkt.action);
   }

   public static AbilityQuickselectMessage decode(FriendlyByteBuf buffer) {
      return new AbilityQuickselectMessage(buffer.readUtf(32767), buffer.readVarInt());
   }

   public static void handle(AbilityQuickselectMessage pkt, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         ServerPlayer sender = context.getSender();
         if (sender != null) {
            AbilityGroup<?, ?> ability = ModConfigs.ABILITIES.getAbilityGroupByName(pkt.abilityName);
            if (ability != null) {
               PlayerAbilitiesData abilitiesData = PlayerAbilitiesData.get((ServerLevel)sender.level);
               AbilityTree abilityTree = abilitiesData.getAbilities(sender);
               AbilityNode<?, ?> abilityNode = abilityTree.getNodeOf(ability);
               if (abilityNode.isLearned()) {
                  abilityTree.quickSelectAbility(sender.server, ability.getParentName());
                  if (abilityNode.equals(abilityTree.getSelectedAbility()) && !abilityTree.isOnCooldown(abilityNode)) {
                     if (abilityNode.getKeyBehavior() != KeyBehavior.INSTANT_ON_RELEASE && abilityNode.getKeyBehavior() != KeyBehavior.TOGGLE_ON_RELEASE) {
                        if (abilityNode.getKeyBehavior() == KeyBehavior.ACTIVATE_ON_HOLD) {
                           if (pkt.action == 1) {
                              abilityTree.keyDown(sender.server);
                           } else if (pkt.action == 0) {
                              abilityTree.keyUp(sender.server);
                           }
                        }
                     } else if (pkt.action == 1) {
                        abilityTree.keyUp(sender.server);
                     }
                  }
               }
            }
         }
      });
      context.setPacketHandled(true);
   }
}
