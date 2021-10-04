package iskallia.vault.network.message;

import iskallia.vault.skill.PlayerVaultStats;
import iskallia.vault.skill.ability.AbilityNode;
import iskallia.vault.skill.ability.AbilityTree;
import iskallia.vault.skill.ability.config.AbilityConfig;
import iskallia.vault.world.data.PlayerAbilitiesData;
import iskallia.vault.world.data.PlayerVaultStatsData;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class AbilitySelectSpecializationMessage {
   private final String ability;
   @Nullable
   private final String specialization;

   public AbilitySelectSpecializationMessage(String ability, @Nullable String specialization) {
      this.ability = ability;
      this.specialization = specialization;
   }

   public static void encode(AbilitySelectSpecializationMessage message, PacketBuffer buffer) {
      buffer.func_180714_a(message.ability);
      buffer.writeBoolean(message.specialization != null);
      if (message.specialization != null) {
         buffer.func_180714_a(message.specialization);
      }
   }

   public static AbilitySelectSpecializationMessage decode(PacketBuffer buffer) {
      return new AbilitySelectSpecializationMessage(buffer.func_150789_c(32767), buffer.readBoolean() ? buffer.func_150789_c(32767) : null);
   }

   public static void handle(AbilitySelectSpecializationMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         ServerPlayerEntity sender = context.getSender();
         if (sender != null) {
            String specialization = message.specialization;
            PlayerAbilitiesData abilitiesData = PlayerAbilitiesData.get((ServerWorld)sender.field_70170_p);
            AbilityTree abilityTree = abilitiesData.getAbilities(sender);
            AbilityNode<?, ?> abilityNode = abilityTree.getNodeByName(message.ability);
            if (abilityNode != null) {
               PlayerVaultStatsData statsData = PlayerVaultStatsData.get((ServerWorld)sender.field_70170_p);
               PlayerVaultStats stats = statsData.getVaultStats(sender);
               if (specialization != null) {
                  if (!abilityNode.getGroup().hasSpecialization(specialization)) {
                     return;
                  }

                  AbilityConfig specConfig = abilityNode.getGroup().getAbilityConfig(specialization, abilityNode.getLevel());
                  if (specConfig == null) {
                     return;
                  }

                  if (stats.getVaultLevel() < specConfig.getLevelRequirement()) {
                     return;
                  }
               } else if (abilityNode.getSpecialization() == null) {
                  return;
               }

               abilitiesData.selectSpecialization(sender, abilityNode.getGroup().getParentName(), specialization);
            }
         }
      });
      context.setPacketHandled(true);
   }
}
