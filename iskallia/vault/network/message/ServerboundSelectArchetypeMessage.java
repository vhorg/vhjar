package iskallia.vault.network.message;

import iskallia.vault.skill.PlayerVaultStats;
import iskallia.vault.skill.archetype.AbstractArchetype;
import iskallia.vault.skill.archetype.AbstractArchetypeConfig;
import iskallia.vault.skill.archetype.ArchetypeContainer;
import iskallia.vault.skill.archetype.ArchetypeRegistry;
import iskallia.vault.world.data.PlayerArchetypeData;
import iskallia.vault.world.data.PlayerVaultStatsData;
import iskallia.vault.world.data.ServerVaults;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent.Context;

public class ServerboundSelectArchetypeMessage {
   private final ResourceLocation archetypeId;

   public ServerboundSelectArchetypeMessage(ResourceLocation archetypeId) {
      this.archetypeId = archetypeId;
   }

   public static void encode(ServerboundSelectArchetypeMessage message, FriendlyByteBuf buffer) {
      buffer.writeResourceLocation(message.archetypeId);
   }

   public static ServerboundSelectArchetypeMessage decode(FriendlyByteBuf buffer) {
      return new ServerboundSelectArchetypeMessage(buffer.readResourceLocation());
   }

   public static void handle(ServerboundSelectArchetypeMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         ServerPlayer player = context.getSender();
         if (player != null && !ServerVaults.isInVault(player)) {
            PlayerArchetypeData archetypeData = PlayerArchetypeData.get(player.server);
            ArchetypeContainer archetypeContainer = archetypeData.getArchetypeContainer(player);
            PlayerVaultStatsData playerSpeedrunStatsData = PlayerVaultStatsData.get(player.server);
            PlayerVaultStats vaultStats = playerSpeedrunStatsData.getVaultStats(player);
            AbstractArchetype<?> archetype = ArchetypeRegistry.getArchetype(message.archetypeId);
            AbstractArchetypeConfig config = archetype.getConfig();
            int learningCost = config.getLearningCost();
            int levelRequirement = config.getLevelRequirement();
            int vaultLevel = vaultStats.getVaultLevel();
            if (archetypeContainer.getCurrentArchetype() != archetype) {
               if (vaultLevel >= levelRequirement) {
                  if (config.getLearningCost() <= vaultStats.getUnspentArchetypePoints()) {
                     playerSpeedrunStatsData.spendArchetypePoints(player, learningCost);
                     archetypeData.set(player, message.archetypeId);
                  }
               }
            }
         }
      });
      context.setPacketHandled(true);
   }
}
