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
   private final String abilityName;

   public AbilityQuickselectMessage(String abilityName) {
      this.abilityName = abilityName;
   }

   public static void encode(AbilityQuickselectMessage pkt, FriendlyByteBuf buffer) {
      buffer.writeUtf(pkt.abilityName);
   }

   public static AbilityQuickselectMessage decode(FriendlyByteBuf buffer) {
      return new AbilityQuickselectMessage(buffer.readUtf(32767));
   }

   public static void handle(AbilityQuickselectMessage pkt, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(
         () -> {
            ServerPlayer sender = context.getSender();
            if (sender != null) {
               AbilityGroup<?, ?> ability = ModConfigs.ABILITIES.getAbilityGroupByName(pkt.abilityName);
               if (ability != null) {
                  PlayerAbilitiesData abilitiesData = PlayerAbilitiesData.get((ServerLevel)sender.level);
                  AbilityTree abilityTree = abilitiesData.getAbilities(sender);
                  AbilityNode<?, ?> abilityNode = abilityTree.getNodeOf(ability);
                  if (abilityNode.isLearned()) {
                     abilityTree.quickSelectAbility(sender.server, ability.getParentName());
                     if (abilityNode.equals(abilityTree.getSelectedAbility())
                        && (abilityNode.getKeyBehavior() == KeyBehavior.INSTANT_ON_RELEASE || abilityNode.getKeyBehavior() == KeyBehavior.TOGGLE_ON_RELEASE)
                        && !abilityTree.isOnCooldown(abilityNode)) {
                        abilityTree.keyUp(sender.server);
                     }
                  }
               }
            }
         }
      );
      context.setPacketHandled(true);
   }
}
