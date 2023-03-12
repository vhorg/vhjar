package iskallia.vault.world.vault.modifier;

import iskallia.vault.config.VaultModifierPoolsConfig;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.item.crystal.CrystalData;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.logic.objective.VaultObjective;
import iskallia.vault.world.vault.logic.objective.raid.RaidChallengeObjective;
import iskallia.vault.world.vault.player.VaultPlayer;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.resources.ResourceLocation;

public final class VaultModifiersGenerator {
   public static Set<VaultModifierStack> generateGlobal(VaultRaid vault, Random random) {
      int level = vault.getProperties().getValue(VaultRaid.LEVEL);
      VaultModifierPoolsConfig.ModifierPoolType type = VaultModifierPoolsConfig.ModifierPoolType.DEFAULT;
      CrystalData data = vault.getProperties().getBase(VaultRaid.CRYSTAL_DATA).orElse(null);
      if (vault.getProperties().getBase(VaultRaid.IS_RAFFLE).orElse(false)) {
         type = VaultModifierPoolsConfig.ModifierPoolType.RAFFLE;
      } else if (vault.getActiveObjective(RaidChallengeObjective.class).isPresent()) {
         type = VaultModifierPoolsConfig.ModifierPoolType.RAID;
      }

      ResourceLocation objectiveKey = vault.getAllObjectives().stream().findFirst().map(VaultObjective::getId).orElse(null);
      return ModConfigs.VAULT_MODIFIER_POOLS.getRandom(random, level, type, objectiveKey).stream().map(VaultModifierStack::of).collect(Collectors.toSet());
   }

   public static Set<VaultModifierStack> generatePlayer(VaultRaid vault, VaultPlayer player, Random random) {
      int level = player.getProperties().getValue(VaultRaid.LEVEL);
      VaultModifierPoolsConfig.ModifierPoolType type = VaultModifierPoolsConfig.ModifierPoolType.DEFAULT;
      if (vault.getProperties().getBase(VaultRaid.IS_RAFFLE).orElse(false)) {
         type = VaultModifierPoolsConfig.ModifierPoolType.RAFFLE;
      } else if (vault.getActiveObjective(RaidChallengeObjective.class).isPresent()) {
         type = VaultModifierPoolsConfig.ModifierPoolType.RAID;
      }

      ResourceLocation objectiveKey = vault.getAllObjectives().stream().findFirst().map(VaultObjective::getId).orElse(null);
      return ModConfigs.VAULT_MODIFIER_POOLS.getRandom(random, level, type, objectiveKey).stream().map(VaultModifierStack::of).collect(Collectors.toSet());
   }

   private VaultModifiersGenerator() {
   }
}
