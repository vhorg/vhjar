package iskallia.vault.init;

import iskallia.vault.VaultMod;
import iskallia.vault.network.message.AbilityActivityMessage;
import iskallia.vault.network.message.AbilityFocusMessage;
import iskallia.vault.network.message.AbilityKnownOnesMessage;
import iskallia.vault.network.message.AbilityLevelMessage;
import iskallia.vault.network.message.AbilityQuickselectMessage;
import iskallia.vault.network.message.AbilitySelectSpecializationMessage;
import iskallia.vault.network.message.ActiveEternalMessage;
import iskallia.vault.network.message.AnimalPenParticleMessage;
import iskallia.vault.network.message.BossMusicMessage;
import iskallia.vault.network.message.CheerReceiveMessage;
import iskallia.vault.network.message.ClientboundArchetypeMessage;
import iskallia.vault.network.message.ClientboundCuriosScrollMessage;
import iskallia.vault.network.message.ClientboundSyncVaultAllowWaypointsMessage;
import iskallia.vault.network.message.ClientboundUpdateAltarIndexMessage;
import iskallia.vault.network.message.ClientboundUpdateDifficultyMessage;
import iskallia.vault.network.message.DiffuserParticleMessage;
import iskallia.vault.network.message.EffectMessage;
import iskallia.vault.network.message.EternalInteractionMessage;
import iskallia.vault.network.message.EternalSyncMessage;
import iskallia.vault.network.message.FighterSizeMessage;
import iskallia.vault.network.message.ForgeParticleMessage;
import iskallia.vault.network.message.HistoricFavoritesMessage;
import iskallia.vault.network.message.InvalidConfigsMessage;
import iskallia.vault.network.message.KnownTalentsMessage;
import iskallia.vault.network.message.MonolithIgniteMessage;
import iskallia.vault.network.message.OmegaStatueUIMessage;
import iskallia.vault.network.message.OpenVaultSnapshotMessage;
import iskallia.vault.network.message.PartyMembersMessage;
import iskallia.vault.network.message.PartyStatusMessage;
import iskallia.vault.network.message.PlayerDamageMultiplierMessage;
import iskallia.vault.network.message.PlayerSnapshotMessage;
import iskallia.vault.network.message.PlayerStatisticsMessage;
import iskallia.vault.network.message.ProficiencyMessage;
import iskallia.vault.network.message.RaffleClientMessage;
import iskallia.vault.network.message.RaffleServerMessage;
import iskallia.vault.network.message.RecyclerParticleMessage;
import iskallia.vault.network.message.RenameUIMessage;
import iskallia.vault.network.message.ResearchMessage;
import iskallia.vault.network.message.ResearchTreeMessage;
import iskallia.vault.network.message.SandEventContributorMessage;
import iskallia.vault.network.message.SandEventUpdateMessage;
import iskallia.vault.network.message.ScavengerAltarConsumeMessage;
import iskallia.vault.network.message.ScoreboardDamageMessage;
import iskallia.vault.network.message.ServerboundAbilityKeyMessage;
import iskallia.vault.network.message.ServerboundAbilitySelectMessage;
import iskallia.vault.network.message.ServerboundAddHistoricFavoriteMessage;
import iskallia.vault.network.message.ServerboundChangeDifficultyMessage;
import iskallia.vault.network.message.ServerboundCuriosScrollMessage;
import iskallia.vault.network.message.ServerboundOpenAbilitiesMessage;
import iskallia.vault.network.message.ServerboundOpenArchetypesMessage;
import iskallia.vault.network.message.ServerboundOpenHistoricMessage;
import iskallia.vault.network.message.ServerboundOpenResearchesMessage;
import iskallia.vault.network.message.ServerboundOpenStatisticsMessage;
import iskallia.vault.network.message.ServerboundOpenTalentsMessage;
import iskallia.vault.network.message.ServerboundOpenVaultExitMessage;
import iskallia.vault.network.message.ServerboundPickaxeOffsetKeyMessage;
import iskallia.vault.network.message.ServerboundRenameEternalMessage;
import iskallia.vault.network.message.ServerboundSelectArchetypeMessage;
import iskallia.vault.network.message.ServerboundSendSnapshotLinkMessage;
import iskallia.vault.network.message.ServerboundToggleEternalPlayerSkinMessage;
import iskallia.vault.network.message.ShardGlobalTradeMessage;
import iskallia.vault.network.message.ShardTradeMessage;
import iskallia.vault.network.message.ShardTradeTradeMessage;
import iskallia.vault.network.message.SpiritExtractorBuyItemsMessage;
import iskallia.vault.network.message.StepHeightMessage;
import iskallia.vault.network.message.SyncOverSizedContentMessage;
import iskallia.vault.network.message.SyncOverSizedStackMessage;
import iskallia.vault.network.message.TalentLevelMessage;
import iskallia.vault.network.message.TrappedMobChestParticlesMessage;
import iskallia.vault.network.message.TrinketJumpMessage;
import iskallia.vault.network.message.VaultArtisanRequestModificationMessage;
import iskallia.vault.network.message.VaultCharmControllerScrollMessage;
import iskallia.vault.network.message.VaultForgeRecipeMessage;
import iskallia.vault.network.message.VaultForgeRequestCraftMessage;
import iskallia.vault.network.message.VaultGoalMessage;
import iskallia.vault.network.message.VaultLevelMessage;
import iskallia.vault.network.message.VaultMessage;
import iskallia.vault.network.message.VaultModifierMessage;
import iskallia.vault.network.message.VaultOverlayMessage;
import iskallia.vault.network.message.VaultPlayerHistoricDataMessage;
import iskallia.vault.network.message.VaultPlayerStatsMessage;
import iskallia.vault.network.message.WorldListUpdateMessage;
import iskallia.vault.network.message.bounty.ClientboundBountyCompleteMessage;
import iskallia.vault.network.message.bounty.ClientboundBountyProgressMessage;
import iskallia.vault.network.message.bounty.ClientboundRefreshBountiesMessage;
import iskallia.vault.network.message.bounty.ServerboundAbandonBountyMessage;
import iskallia.vault.network.message.bounty.ServerboundActivateBountyMessage;
import iskallia.vault.network.message.bounty.ServerboundBountyProgressMessage;
import iskallia.vault.network.message.bounty.ServerboundClaimRewardMessage;
import iskallia.vault.network.message.bounty.ServerboundRerollMessage;
import iskallia.vault.network.message.relic.RelicAssembleButtonMessage;
import iskallia.vault.network.message.relic.SelectRelicMessage;
import iskallia.vault.network.message.transmog.DiscoveredEntriesMessage;
import iskallia.vault.network.message.transmog.SelectDiscoveredModelMessage;
import iskallia.vault.network.message.transmog.TransmogButtonMessage;
import iskallia.vault.util.ModVersion;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.apache.maven.artifact.versioning.ArtifactVersion;

public class ModNetwork {
   private static final ModVersion VERSION = ModList.get()
      .getModContainerById("the_vault")
      .map(ModContainer::getModInfo)
      .<ArtifactVersion>map(IModInfo::getVersion)
      .map(artifactVersion -> new ModVersion(artifactVersion.getQualifier()))
      .orElse(new ModVersion("1"));
   public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
      new ResourceLocation("the_vault", "network"), VERSION::toString, VERSION::accepted, VERSION::accepted
   );
   private static int ID = 0;

   public static void initialize() {
      VaultMod.LOGGER.info("Initializing network. Version: {}", VERSION.toString());
      CHANNEL.registerMessage(
         nextId(),
         ServerboundOpenStatisticsMessage.class,
         ServerboundOpenStatisticsMessage::encode,
         ServerboundOpenStatisticsMessage::decode,
         ServerboundOpenStatisticsMessage::handle
      );
      CHANNEL.registerMessage(
         nextId(),
         ServerboundOpenAbilitiesMessage.class,
         ServerboundOpenAbilitiesMessage::encode,
         ServerboundOpenAbilitiesMessage::decode,
         ServerboundOpenAbilitiesMessage::handle
      );
      CHANNEL.registerMessage(
         nextId(),
         ServerboundOpenTalentsMessage.class,
         ServerboundOpenTalentsMessage::encode,
         ServerboundOpenTalentsMessage::decode,
         ServerboundOpenTalentsMessage::handle
      );
      CHANNEL.registerMessage(
         nextId(),
         ServerboundOpenResearchesMessage.class,
         ServerboundOpenResearchesMessage::encode,
         ServerboundOpenResearchesMessage::decode,
         ServerboundOpenResearchesMessage::handle
      );
      CHANNEL.registerMessage(
         nextId(),
         ServerboundOpenArchetypesMessage.class,
         ServerboundOpenArchetypesMessage::encode,
         ServerboundOpenArchetypesMessage::decode,
         ServerboundOpenArchetypesMessage::handle
      );
      CHANNEL.registerMessage(nextId(), VaultLevelMessage.class, VaultLevelMessage::encode, VaultLevelMessage::decode, VaultLevelMessage::handle);
      CHANNEL.registerMessage(nextId(), TalentLevelMessage.class, TalentLevelMessage::encode, TalentLevelMessage::decode, TalentLevelMessage::handle);
      CHANNEL.registerMessage(nextId(), ResearchMessage.class, ResearchMessage::encode, ResearchMessage::decode, ResearchMessage::handle);
      CHANNEL.registerMessage(nextId(), ResearchTreeMessage.class, ResearchTreeMessage::encode, ResearchTreeMessage::decode, ResearchTreeMessage::handle);
      CHANNEL.registerMessage(
         nextId(),
         ServerboundAbilityKeyMessage.class,
         ServerboundAbilityKeyMessage::encode,
         ServerboundAbilityKeyMessage::decode,
         ServerboundAbilityKeyMessage::handle
      );
      CHANNEL.registerMessage(
         nextId(),
         ServerboundAbilitySelectMessage.class,
         ServerboundAbilitySelectMessage::encode,
         ServerboundAbilitySelectMessage::decode,
         ServerboundAbilitySelectMessage::handle
      );
      CHANNEL.registerMessage(nextId(), AbilityLevelMessage.class, AbilityLevelMessage::encode, AbilityLevelMessage::decode, AbilityLevelMessage::handle);
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
      CHANNEL.registerMessage(
         nextId(), ScoreboardDamageMessage.class, ScoreboardDamageMessage::encode, ScoreboardDamageMessage::decode, ScoreboardDamageMessage::handle
      );
      CHANNEL.registerMessage(nextId(), RaffleServerMessage.class, RaffleServerMessage::encode, RaffleServerMessage::decode, RaffleServerMessage::handle);
      CHANNEL.registerMessage(nextId(), RaffleClientMessage.class, RaffleClientMessage::encode, RaffleClientMessage::decode, RaffleClientMessage::handle);
      CHANNEL.registerMessage(nextId(), RenameUIMessage.class, RenameUIMessage::encode, RenameUIMessage::decode, RenameUIMessage::handle);
      CHANNEL.registerMessage(nextId(), StepHeightMessage.class, StepHeightMessage::encode, StepHeightMessage::decode, StepHeightMessage::handle);
      CHANNEL.registerMessage(nextId(), BossMusicMessage.class, BossMusicMessage::encode, BossMusicMessage::decode, BossMusicMessage::handle);
      CHANNEL.registerMessage(nextId(), OmegaStatueUIMessage.class, OmegaStatueUIMessage::encode, OmegaStatueUIMessage::decode, OmegaStatueUIMessage::handle);
      CHANNEL.registerMessage(nextId(), VaultModifierMessage.class, VaultModifierMessage::encode, VaultModifierMessage::decode, VaultModifierMessage::handle);
      CHANNEL.registerMessage(nextId(), CheerReceiveMessage.class, CheerReceiveMessage::encode, CheerReceiveMessage::decode, CheerReceiveMessage::handle);
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
         nextId(), ShardTradeTradeMessage.class, ShardTradeTradeMessage::encode, ShardTradeTradeMessage::decode, ShardTradeTradeMessage::handle
      );
      CHANNEL.registerMessage(
         nextId(), SyncOverSizedStackMessage.class, SyncOverSizedStackMessage::encode, SyncOverSizedStackMessage::decode, SyncOverSizedStackMessage::handle
      );
      CHANNEL.registerMessage(
         nextId(),
         SyncOverSizedContentMessage.class,
         SyncOverSizedContentMessage::encode,
         SyncOverSizedContentMessage::decode,
         SyncOverSizedContentMessage::handle
      );
      CHANNEL.registerMessage(
         nextId(), EternalInteractionMessage.class, EternalInteractionMessage::encode, EternalInteractionMessage::decode, EternalInteractionMessage::handle
      );
      CHANNEL.registerMessage(nextId(), EternalSyncMessage.class, EternalSyncMessage::encode, EternalSyncMessage::decode, EternalSyncMessage::handle);
      CHANNEL.registerMessage(
         nextId(), ShardGlobalTradeMessage.class, ShardGlobalTradeMessage::encode, ShardGlobalTradeMessage::decode, ShardGlobalTradeMessage::handle
      );
      CHANNEL.registerMessage(nextId(), ActiveEternalMessage.class, ActiveEternalMessage::encode, ActiveEternalMessage::decode, ActiveEternalMessage::handle);
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
         nextId(), WorldListUpdateMessage.class, WorldListUpdateMessage::encode, WorldListUpdateMessage::decode, WorldListUpdateMessage::handle
      );
      CHANNEL.registerMessage(nextId(), VaultMessage.Sync.class, VaultMessage.Sync::encode, VaultMessage.Sync::decode, VaultMessage.Sync::handle);
      CHANNEL.registerMessage(nextId(), VaultMessage.Unload.class, VaultMessage.Unload::encode, VaultMessage.Unload::decode, VaultMessage.Unload::handle);
      CHANNEL.registerMessage(
         nextId(), InvalidConfigsMessage.class, InvalidConfigsMessage::encode, InvalidConfigsMessage::decode, InvalidConfigsMessage::handle
      );
      CHANNEL.registerMessage(
         nextId(),
         ClientboundCuriosScrollMessage.class,
         ClientboundCuriosScrollMessage::encode,
         ClientboundCuriosScrollMessage::decode,
         ClientboundCuriosScrollMessage::handle
      );
      CHANNEL.registerMessage(
         nextId(),
         ServerboundCuriosScrollMessage.class,
         ServerboundCuriosScrollMessage::encode,
         ServerboundCuriosScrollMessage::decode,
         ServerboundCuriosScrollMessage::handle
      );
      CHANNEL.registerMessage(
         nextId(), PlayerSnapshotMessage.class, PlayerSnapshotMessage::encode, PlayerSnapshotMessage::decode, PlayerSnapshotMessage::handle
      );
      CHANNEL.registerMessage(nextId(), TrinketJumpMessage.class, TrinketJumpMessage::encode, TrinketJumpMessage::decode, TrinketJumpMessage::handle);
      CHANNEL.registerMessage(
         nextId(),
         SelectDiscoveredModelMessage.class,
         SelectDiscoveredModelMessage::encode,
         SelectDiscoveredModelMessage::decode,
         SelectDiscoveredModelMessage::handle
      );
      CHANNEL.registerMessage(
         nextId(), DiscoveredEntriesMessage.class, DiscoveredEntriesMessage::encode, DiscoveredEntriesMessage::decode, DiscoveredEntriesMessage::handle
      );
      CHANNEL.registerMessage(
         nextId(), TransmogButtonMessage.class, TransmogButtonMessage::encode, TransmogButtonMessage::decode, TransmogButtonMessage::handle
      );
      CHANNEL.registerMessage(nextId(), ProficiencyMessage.class, ProficiencyMessage::encode, ProficiencyMessage::decode, ProficiencyMessage::handle);
      CHANNEL.registerMessage(
         nextId(),
         ServerboundRenameEternalMessage.class,
         ServerboundRenameEternalMessage::encode,
         ServerboundRenameEternalMessage::decode,
         ServerboundRenameEternalMessage::handle
      );
      CHANNEL.registerMessage(
         nextId(),
         ServerboundToggleEternalPlayerSkinMessage.class,
         ServerboundToggleEternalPlayerSkinMessage::encode,
         ServerboundToggleEternalPlayerSkinMessage::decode,
         ServerboundToggleEternalPlayerSkinMessage::handle
      );
      CHANNEL.registerMessage(
         nextId(), VaultForgeRecipeMessage.class, VaultForgeRecipeMessage::encode, VaultForgeRecipeMessage::decode, VaultForgeRecipeMessage::handle
      );
      CHANNEL.registerMessage(
         nextId(),
         VaultForgeRequestCraftMessage.class,
         VaultForgeRequestCraftMessage::encode,
         VaultForgeRequestCraftMessage::decode,
         VaultForgeRequestCraftMessage::handle
      );
      CHANNEL.registerMessage(
         nextId(),
         VaultArtisanRequestModificationMessage.class,
         VaultArtisanRequestModificationMessage::encode,
         VaultArtisanRequestModificationMessage::decode,
         VaultArtisanRequestModificationMessage::handle
      );
      CHANNEL.registerMessage(nextId(), SelectRelicMessage.class, SelectRelicMessage::encode, SelectRelicMessage::decode, SelectRelicMessage::handle);
      CHANNEL.registerMessage(
         nextId(), RelicAssembleButtonMessage.class, RelicAssembleButtonMessage::encode, RelicAssembleButtonMessage::decode, RelicAssembleButtonMessage::handle
      );
      CHANNEL.registerMessage(
         nextId(),
         ClientboundArchetypeMessage.class,
         ClientboundArchetypeMessage::encode,
         ClientboundArchetypeMessage::decode,
         ClientboundArchetypeMessage::handle
      );
      CHANNEL.registerMessage(
         nextId(),
         ServerboundSelectArchetypeMessage.class,
         ServerboundSelectArchetypeMessage::encode,
         ServerboundSelectArchetypeMessage::decode,
         ServerboundSelectArchetypeMessage::handle
      );
      CHANNEL.registerMessage(
         nextId(),
         VaultPlayerStatsMessage.S2C.class,
         VaultPlayerStatsMessage.S2C::encode,
         VaultPlayerStatsMessage.S2C::decode,
         VaultPlayerStatsMessage.S2C::handle
      );
      CHANNEL.registerMessage(
         nextId(),
         VaultPlayerStatsMessage.C2S.class,
         VaultPlayerStatsMessage.C2S::encode,
         VaultPlayerStatsMessage.C2S::decode,
         VaultPlayerStatsMessage.C2S::handle
      );
      CHANNEL.registerMessage(
         nextId(),
         ServerboundOpenVaultExitMessage.class,
         ServerboundOpenVaultExitMessage::encode,
         ServerboundOpenVaultExitMessage::decode,
         ServerboundOpenVaultExitMessage::handle
      );
      CHANNEL.registerMessage(
         nextId(),
         SpiritExtractorBuyItemsMessage.class,
         SpiritExtractorBuyItemsMessage::encode,
         SpiritExtractorBuyItemsMessage::decode,
         SpiritExtractorBuyItemsMessage::handle
      );
      CHANNEL.registerMessage(
         nextId(),
         ScavengerAltarConsumeMessage.class,
         ScavengerAltarConsumeMessage::encode,
         ScavengerAltarConsumeMessage::decode,
         ScavengerAltarConsumeMessage::handle
      );
      CHANNEL.registerMessage(
         nextId(),
         ServerboundAbandonBountyMessage.class,
         ServerboundAbandonBountyMessage::encode,
         ServerboundAbandonBountyMessage::decode,
         ServerboundAbandonBountyMessage::handle
      );
      CHANNEL.registerMessage(
         nextId(),
         ServerboundClaimRewardMessage.class,
         ServerboundClaimRewardMessage::encode,
         ServerboundClaimRewardMessage::decode,
         ServerboundClaimRewardMessage::handle
      );
      CHANNEL.registerMessage(
         nextId(),
         ServerboundActivateBountyMessage.class,
         ServerboundActivateBountyMessage::encode,
         ServerboundActivateBountyMessage::decode,
         ServerboundActivateBountyMessage::handle
      );
      CHANNEL.registerMessage(
         nextId(),
         ClientboundBountyCompleteMessage.class,
         ClientboundBountyCompleteMessage::encode,
         ClientboundBountyCompleteMessage::decode,
         ClientboundBountyCompleteMessage::handle
      );
      CHANNEL.registerMessage(
         nextId(), ServerboundRerollMessage.class, ServerboundRerollMessage::encode, ServerboundRerollMessage::decode, ServerboundRerollMessage::handle
      );
      CHANNEL.registerMessage(
         nextId(),
         ClientboundRefreshBountiesMessage.class,
         ClientboundRefreshBountiesMessage::encode,
         ClientboundRefreshBountiesMessage::decode,
         ClientboundRefreshBountiesMessage::handle
      );
      CHANNEL.registerMessage(
         nextId(),
         ServerboundBountyProgressMessage.class,
         ServerboundBountyProgressMessage::encode,
         ServerboundBountyProgressMessage::decode,
         ServerboundBountyProgressMessage::handle
      );
      CHANNEL.registerMessage(
         nextId(),
         ClientboundBountyProgressMessage.class,
         ClientboundBountyProgressMessage::encode,
         ClientboundBountyProgressMessage::decode,
         ClientboundBountyProgressMessage::handle
      );
      CHANNEL.registerMessage(
         nextId(),
         ServerboundChangeDifficultyMessage.class,
         ServerboundChangeDifficultyMessage::encode,
         ServerboundChangeDifficultyMessage::decode,
         ServerboundChangeDifficultyMessage::handle
      );
      CHANNEL.registerMessage(
         nextId(),
         ClientboundUpdateDifficultyMessage.class,
         ClientboundUpdateDifficultyMessage::encode,
         ClientboundUpdateDifficultyMessage::decode,
         ClientboundUpdateDifficultyMessage::handle
      );
      CHANNEL.registerMessage(
         nextId(),
         ClientboundSyncVaultAllowWaypointsMessage.class,
         ClientboundSyncVaultAllowWaypointsMessage::encode,
         ClientboundSyncVaultAllowWaypointsMessage::decode,
         ClientboundSyncVaultAllowWaypointsMessage::handle
      );
      CHANNEL.registerMessage(
         nextId(), RecyclerParticleMessage.class, RecyclerParticleMessage::encode, RecyclerParticleMessage::decode, RecyclerParticleMessage::handle
      );
      CHANNEL.registerMessage(nextId(), ForgeParticleMessage.class, ForgeParticleMessage::encode, ForgeParticleMessage::decode, ForgeParticleMessage::handle);
      CHANNEL.registerMessage(
         nextId(), MonolithIgniteMessage.class, MonolithIgniteMessage::encode, MonolithIgniteMessage::decode, MonolithIgniteMessage::handle
      );
      CHANNEL.registerMessage(
         nextId(), DiffuserParticleMessage.class, DiffuserParticleMessage::encode, DiffuserParticleMessage::decode, DiffuserParticleMessage::handle
      );
      CHANNEL.registerMessage(
         nextId(), AnimalPenParticleMessage.class, AnimalPenParticleMessage::encode, AnimalPenParticleMessage::decode, AnimalPenParticleMessage::handle
      );
      CHANNEL.registerMessage(
         nextId(),
         ClientboundUpdateAltarIndexMessage.class,
         ClientboundUpdateAltarIndexMessage::encode,
         ClientboundUpdateAltarIndexMessage::decode,
         ClientboundUpdateAltarIndexMessage::handle
      );
      CHANNEL.registerMessage(
         nextId(),
         ServerboundOpenHistoricMessage.class,
         ServerboundOpenHistoricMessage::encode,
         ServerboundOpenHistoricMessage::decode,
         ServerboundOpenHistoricMessage::handle
      );
      CHANNEL.registerMessage(
         nextId(),
         VaultPlayerHistoricDataMessage.S2C.class,
         VaultPlayerHistoricDataMessage.S2C::encode,
         VaultPlayerHistoricDataMessage.S2C::decode,
         VaultPlayerHistoricDataMessage.S2C::handle
      );
      CHANNEL.registerMessage(
         nextId(), HistoricFavoritesMessage.class, HistoricFavoritesMessage::encode, HistoricFavoritesMessage::decode, HistoricFavoritesMessage::handle
      );
      CHANNEL.registerMessage(
         nextId(),
         ServerboundAddHistoricFavoriteMessage.class,
         ServerboundAddHistoricFavoriteMessage::encode,
         ServerboundAddHistoricFavoriteMessage::decode,
         ServerboundAddHistoricFavoriteMessage::handle
      );
      CHANNEL.registerMessage(
         nextId(),
         OpenVaultSnapshotMessage.S2C.class,
         OpenVaultSnapshotMessage.S2C::encode,
         OpenVaultSnapshotMessage.S2C::decode,
         OpenVaultSnapshotMessage.S2C::handle
      );
      CHANNEL.registerMessage(
         nextId(),
         ServerboundSendSnapshotLinkMessage.class,
         ServerboundSendSnapshotLinkMessage::encode,
         ServerboundSendSnapshotLinkMessage::decode,
         ServerboundSendSnapshotLinkMessage::handle
      );
      CHANNEL.registerMessage(
         nextId(),
         ServerboundPickaxeOffsetKeyMessage.class,
         ServerboundPickaxeOffsetKeyMessage::encode,
         ServerboundPickaxeOffsetKeyMessage::decode,
         ServerboundPickaxeOffsetKeyMessage::handle
      );
      CHANNEL.registerMessage(
         nextId(),
         TrappedMobChestParticlesMessage.class,
         TrappedMobChestParticlesMessage::encode,
         TrappedMobChestParticlesMessage::decode,
         TrappedMobChestParticlesMessage::handle
      );
   }

   public static int nextId() {
      return ID++;
   }
}
