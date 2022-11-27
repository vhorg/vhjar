package iskallia.vault.network.message;

import iskallia.vault.skill.PlayerVaultStats;
import iskallia.vault.skill.ability.AbilityNode;
import iskallia.vault.skill.ability.AbilityTree;
import iskallia.vault.skill.ability.config.spi.AbstractAbilityConfig;
import iskallia.vault.world.data.PlayerAbilitiesData;
import iskallia.vault.world.data.PlayerVaultStatsData;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent.Context;

public class AbilitySelectSpecializationMessage {
   private final String ability;
   @Nullable
   private final String specialization;

   public AbilitySelectSpecializationMessage(String ability, @Nullable String specialization) {
      this.ability = ability;
      this.specialization = specialization;
   }

   public static void encode(AbilitySelectSpecializationMessage message, FriendlyByteBuf buffer) {
      buffer.writeUtf(message.ability);
      buffer.writeBoolean(message.specialization != null);
      if (message.specialization != null) {
         buffer.writeUtf(message.specialization);
      }
   }

   public static AbilitySelectSpecializationMessage decode(FriendlyByteBuf buffer) {
      return new AbilitySelectSpecializationMessage(buffer.readUtf(32767), buffer.readBoolean() ? buffer.readUtf(32767) : null);
   }

   public static void handle(AbilitySelectSpecializationMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         ServerPlayer sender = context.getSender();
         if (sender != null) {
            String specialization = message.specialization;
            PlayerAbilitiesData abilitiesData = PlayerAbilitiesData.get((ServerLevel)sender.level);
            AbilityTree abilityTree = abilitiesData.getAbilities(sender);
            AbilityNode<?, ?> abilityNode = abilityTree.getNodeByName(message.ability);
            if (abilityNode != null) {
               PlayerVaultStatsData statsData = PlayerVaultStatsData.get((ServerLevel)sender.level);
               PlayerVaultStats stats = statsData.getVaultStats(sender);
               if (specialization != null) {
                  if (!abilityNode.getGroup().hasSpecialization(specialization)) {
                     return;
                  }

                  AbstractAbilityConfig specConfig = abilityNode.getGroup().getAbilityConfig(specialization, abilityNode.getLevel());
                  if (specConfig == null) {
                     return;
                  }

                  if (stats.getVaultLevel() < specConfig.getLevelRequirement()) {
                     return;
                  }
               } else if (abilityNode.getSpecialization() == null) {
                  return;
               }

               abilitiesData.selectSpecialization(sender, abilityNode, specialization);
            }
         }
      });
      context.setPacketHandled(true);
   }
}
