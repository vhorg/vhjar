package iskallia.vault.network.message;

import iskallia.vault.init.ModConfigs;
import iskallia.vault.skill.ability.AbilityGroup;
import iskallia.vault.skill.ability.AbilityNode;
import iskallia.vault.skill.ability.AbilityTree;
import iskallia.vault.skill.ability.config.AbilityConfig;
import iskallia.vault.world.data.PlayerAbilitiesData;
import java.util.function.Supplier;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class AbilityQuickselectMessage {
   private final String abilityName;

   public AbilityQuickselectMessage(String abilityName) {
      this.abilityName = abilityName;
   }

   public static void encode(AbilityQuickselectMessage pkt, PacketBuffer buffer) {
      buffer.func_180714_a(pkt.abilityName);
   }

   public static AbilityQuickselectMessage decode(PacketBuffer buffer) {
      return new AbilityQuickselectMessage(buffer.func_150789_c(32767));
   }

   public static void handle(AbilityQuickselectMessage pkt, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(
         () -> {
            ServerPlayerEntity sender = context.getSender();
            if (sender != null) {
               AbilityGroup<?, ?> ability = ModConfigs.ABILITIES.getAbilityGroupByName(pkt.abilityName);
               if (ability != null) {
                  PlayerAbilitiesData abilitiesData = PlayerAbilitiesData.get((ServerWorld)sender.field_70170_p);
                  AbilityTree abilityTree = abilitiesData.getAbilities(sender);
                  AbilityNode<?, ?> abilityNode = abilityTree.getNodeOf(ability);
                  if (abilityNode.isLearned()) {
                     abilityTree.quickSelectAbility(sender.field_71133_b, ability.getParentName());
                     if (abilityNode.equals(abilityTree.getSelectedAbility())
                        && abilityNode.getAbilityConfig().getBehavior() == AbilityConfig.Behavior.RELEASE_TO_PERFORM
                        && !abilityTree.isOnCooldown(abilityNode)) {
                        abilityTree.keyUp(sender.field_71133_b);
                     }
                  }
               }
            }
         }
      );
      context.setPacketHandled(true);
   }
}
