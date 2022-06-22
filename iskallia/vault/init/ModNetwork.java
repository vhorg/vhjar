package iskallia.vault.init;

import iskallia.vault.network.message.AbilityActivityMessage;
import iskallia.vault.network.message.AbilityFocusMessage;
import iskallia.vault.network.message.AbilityKeyMessage;
import iskallia.vault.network.message.AbilityKnownOnesMessage;
import iskallia.vault.network.message.AbilityQuickselectMessage;
import iskallia.vault.network.message.AbilitySelectSpecializationMessage;
import iskallia.vault.network.message.AbilityUpgradeMessage;
import iskallia.vault.network.message.ActiveEternalMessage;
import iskallia.vault.network.message.AdvancedVendingUIMessage;
import iskallia.vault.network.message.BossMusicMessage;
import iskallia.vault.network.message.EffectMessage;
import iskallia.vault.network.message.EnteredEyesoreDomainMessage;
import iskallia.vault.network.message.EternalInteractionMessage;
import iskallia.vault.network.message.EternalSyncMessage;
import iskallia.vault.network.message.FighterSizeMessage;
import iskallia.vault.network.message.GlobalDifficultyMessage;
import iskallia.vault.network.message.KnownTalentsMessage;
import iskallia.vault.network.message.OmegaStatueUIMessage;
import iskallia.vault.network.message.OpenSkillTreeMessage;
import iskallia.vault.network.message.PartyMembersMessage;
import iskallia.vault.network.message.PartyStatusMessage;
import iskallia.vault.network.message.PlayerDamageMultiplierMessage;
import iskallia.vault.network.message.PlayerStatisticsMessage;
import iskallia.vault.network.message.RageSyncMessage;
import iskallia.vault.network.message.RenameUIMessage;
import iskallia.vault.network.message.ResearchMessage;
import iskallia.vault.network.message.ResearchTreeMessage;
import iskallia.vault.network.message.SandEventContributorMessage;
import iskallia.vault.network.message.SandEventUpdateMessage;
import iskallia.vault.network.message.ShardGlobalTradeMessage;
import iskallia.vault.network.message.ShardTradeMessage;
import iskallia.vault.network.message.ShardTraderScreenMessage;
import iskallia.vault.network.message.StepHeightMessage;
import iskallia.vault.network.message.SyncOversizedStackMessage;
import iskallia.vault.network.message.TalentUpgradeMessage;
import iskallia.vault.network.message.VaultCharmControllerScrollMessage;
import iskallia.vault.network.message.VaultGoalMessage;
import iskallia.vault.network.message.VaultLevelMessage;
import iskallia.vault.network.message.VaultModifierMessage;
import iskallia.vault.network.message.VaultOverlayMessage;
import iskallia.vault.network.message.VendingUIMessage;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class ModNetwork {
   private static final String NETWORK_VERSION = "0.24.0";
   public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
      new ResourceLocation("the_vault", "network"), () -> "0.24.0", version -> version.equals("0.24.0"), version -> version.equals("0.24.0")
   );
   private static int ID = 0;

   public static void initialize() {
      CHANNEL.registerMessage(nextId(), OpenSkillTreeMessage.class, OpenSkillTreeMessage::encode, OpenSkillTreeMessage::decode, OpenSkillTreeMessage::handle);
      CHANNEL.registerMessage(nextId(), VaultLevelMessage.class, VaultLevelMessage::encode, VaultLevelMessage::decode, VaultLevelMessage::handle);
      CHANNEL.registerMessage(nextId(), TalentUpgradeMessage.class, TalentUpgradeMessage::encode, TalentUpgradeMessage::decode, TalentUpgradeMessage::handle);
      CHANNEL.registerMessage(nextId(), ResearchMessage.class, ResearchMessage::encode, ResearchMessage::decode, ResearchMessage::handle);
      CHANNEL.registerMessage(nextId(), ResearchTreeMessage.class, ResearchTreeMessage::encode, ResearchTreeMessage::decode, ResearchTreeMessage::handle);
      CHANNEL.registerMessage(nextId(), AbilityKeyMessage.class, AbilityKeyMessage::encode, AbilityKeyMessage::decode, AbilityKeyMessage::handle);
      CHANNEL.registerMessage(
         nextId(), AbilityUpgradeMessage.class, AbilityUpgradeMessage::encode, AbilityUpgradeMessage::decode, AbilityUpgradeMessage::handle
      );
      CHANNEL.registerMessage(
         nextId(),
         AbilitySelectSpecializationMessage.class,
         AbilitySelectSpecializationMessage::encode,
         AbilitySelectSpecializationMessage::decode,
         AbilitySelectSpecializationMessage::handle
      );
      CHANNEL.registerMessage(
         nextId(), AbilityKnownOnesMessage.class, AbilityKnownOnesMessage::encode, AbilityKnownOnesMessage::decode, AbilityKnownOnesMessage::handle
      );
      CHANNEL.registerMessage(nextId(), AbilityFocusMessage.class, AbilityFocusMessage::encode, AbilityFocusMessage::decode, AbilityFocusMessage::handle);
      CHANNEL.registerMessage(
         nextId(), AbilityActivityMessage.class, AbilityActivityMessage::encode, AbilityActivityMessage::decode, AbilityActivityMessage::handle
      );
      CHANNEL.registerMessage(nextId(), VaultOverlayMessage.class, VaultOverlayMessage::encode, VaultOverlayMessage::decode, VaultOverlayMessage::handle);
      CHANNEL.registerMessage(nextId(), FighterSizeMessage.class, FighterSizeMessage::encode, FighterSizeMessage::decode, FighterSizeMessage::handle);
      CHANNEL.registerMessage(nextId(), VendingUIMessage.class, VendingUIMessage::encode, VendingUIMessage::decode, VendingUIMessage::handle);
      CHANNEL.registerMessage(
         nextId(), AdvancedVendingUIMessage.class, AdvancedVendingUIMessage::encode, AdvancedVendingUIMessage::decode, AdvancedVendingUIMessage::handle
      );
      CHANNEL.registerMessage(nextId(), RenameUIMessage.class, RenameUIMessage::encode, RenameUIMessage::decode, RenameUIMessage::handle);
      CHANNEL.registerMessage(nextId(), StepHeightMessage.class, StepHeightMessage::encode, StepHeightMessage::decode, StepHeightMessage::handle);
      CHANNEL.registerMessage(nextId(), BossMusicMessage.class, BossMusicMessage::encode, BossMusicMessage::decode, BossMusicMessage::handle);
      CHANNEL.registerMessage(nextId(), OmegaStatueUIMessage.class, OmegaStatueUIMessage::encode, OmegaStatueUIMessage::decode, OmegaStatueUIMessage::handle);
      CHANNEL.registerMessage(nextId(), VaultModifierMessage.class, VaultModifierMessage::encode, VaultModifierMessage::decode, VaultModifierMessage::handle);
      CHANNEL.registerMessage(nextId(), VaultGoalMessage.class, VaultGoalMessage::encode, VaultGoalMessage::decode, VaultGoalMessage::handle);
      CHANNEL.registerMessage(
         nextId(), AbilityQuickselectMessage.class, AbilityQuickselectMessage::encode, AbilityQuickselectMessage::decode, AbilityQuickselectMessage::handle
      );
      CHANNEL.registerMessage(
         nextId(), PlayerStatisticsMessage.class, PlayerStatisticsMessage::encode, PlayerStatisticsMessage::decode, PlayerStatisticsMessage::handle
      );
      CHANNEL.registerMessage(nextId(), PartyStatusMessage.class, PartyStatusMessage::encode, PartyStatusMessage::decode, PartyStatusMessage::handle);
      CHANNEL.registerMessage(nextId(), PartyMembersMessage.class, PartyMembersMessage::encode, PartyMembersMessage::decode, PartyMembersMessage::handle);
      CHANNEL.registerMessage(
         nextId(), SandEventUpdateMessage.class, SandEventUpdateMessage::encode, SandEventUpdateMessage::decode, SandEventUpdateMessage::handle
      );
      CHANNEL.registerMessage(
         nextId(),
         SandEventContributorMessage.class,
         SandEventContributorMessage::encode,
         SandEventContributorMessage::decode,
         SandEventContributorMessage::handle
      );
      CHANNEL.registerMessage(nextId(), EffectMessage.class, EffectMessage::encode, EffectMessage::decode, EffectMessage::handle);
      CHANNEL.registerMessage(nextId(), KnownTalentsMessage.class, KnownTalentsMessage::encode, KnownTalentsMessage::decode, KnownTalentsMessage::handle);
      CHANNEL.registerMessage(nextId(), ShardTradeMessage.class, ShardTradeMessage::encode, ShardTradeMessage::decode, ShardTradeMessage::handle);
      CHANNEL.registerMessage(
         nextId(), ShardTraderScreenMessage.class, ShardTraderScreenMessage::encode, ShardTraderScreenMessage::decode, ShardTraderScreenMessage::handle
      );
      CHANNEL.registerMessage(
         nextId(), SyncOversizedStackMessage.class, SyncOversizedStackMessage::encode, SyncOversizedStackMessage::decode, SyncOversizedStackMessage::handle
      );
      CHANNEL.registerMessage(
         nextId(), EternalInteractionMessage.class, EternalInteractionMessage::encode, EternalInteractionMessage::decode, EternalInteractionMessage::handle
      );
      CHANNEL.registerMessage(nextId(), EternalSyncMessage.class, EternalSyncMessage::encode, EternalSyncMessage::decode, EternalSyncMessage::handle);
      CHANNEL.registerMessage(
         nextId(), ShardGlobalTradeMessage.class, ShardGlobalTradeMessage::encode, ShardGlobalTradeMessage::decode, ShardGlobalTradeMessage::handle
      );
      CHANNEL.registerMessage(nextId(), RageSyncMessage.class, RageSyncMessage::encode, RageSyncMessage::decode, RageSyncMessage::handle);
      CHANNEL.registerMessage(nextId(), ActiveEternalMessage.class, ActiveEternalMessage::encode, ActiveEternalMessage::decode, ActiveEternalMessage::handle);
      CHANNEL.registerMessage(
         nextId(), GlobalDifficultyMessage.class, GlobalDifficultyMessage::encode, GlobalDifficultyMessage::decode, GlobalDifficultyMessage::handle
      );
      CHANNEL.registerMessage(
         nextId(),
         PlayerDamageMultiplierMessage.class,
         PlayerDamageMultiplierMessage::encode,
         PlayerDamageMultiplierMessage::decode,
         PlayerDamageMultiplierMessage::handle
      );
      CHANNEL.registerMessage(
         nextId(),
         VaultCharmControllerScrollMessage.class,
         VaultCharmControllerScrollMessage::encode,
         VaultCharmControllerScrollMessage::decode,
         VaultCharmControllerScrollMessage::handle
      );
      CHANNEL.registerMessage(
         nextId(),
         EnteredEyesoreDomainMessage.class,
         EnteredEyesoreDomainMessage::encode,
         EnteredEyesoreDomainMessage::decode,
         EnteredEyesoreDomainMessage::handle
      );
   }

   public static int nextId() {
      return ID++;
   }
}
