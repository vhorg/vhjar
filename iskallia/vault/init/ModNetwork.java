package iskallia.vault.init;

import iskallia.vault.VaultMod;
import iskallia.vault.network.message.AbilityActivityMessage;
import iskallia.vault.network.message.AbilityCooldownMessage;
import iskallia.vault.network.message.AbilityFocusMessage;
import iskallia.vault.network.message.AbilityKnownOnesMessage;
import iskallia.vault.network.message.AbilityLevelMessage;
import iskallia.vault.network.message.AbilityQuickselectMessage;
import iskallia.vault.network.message.AbilitySelectSpecializationMessage;
import iskallia.vault.network.message.ActiveEternalMessage;
import iskallia.vault.network.message.AngelToggleMessage;
import iskallia.vault.network.message.AnimalPenParticleMessage;
import iskallia.vault.network.message.BonkParticleMessage;
import iskallia.vault.network.message.ChainingParticleMessage;
import iskallia.vault.network.message.ClientboundArchetypeMessage;
import iskallia.vault.network.message.ClientboundChampionMessage;
import iskallia.vault.network.message.ClientboundCuriosScrollMessage;
import iskallia.vault.network.message.ClientboundFireballExplosionMessage;
import iskallia.vault.network.message.ClientboundHunterParticlesFromJavelinMessage;
import iskallia.vault.network.message.ClientboundHunterParticlesMessage;
import iskallia.vault.network.message.ClientboundMobEffectRemoveMessage;
import iskallia.vault.network.message.ClientboundMobEffectUpdateMessage;
import iskallia.vault.network.message.ClientboundNightVisionGogglesParticlesMessage;
import iskallia.vault.network.message.ClientboundPlayerLastDamageSourceMessage;
import iskallia.vault.network.message.ClientboundRefreshSpiritExtractorMessage;
import iskallia.vault.network.message.ClientboundRefreshToolViseMessage;
import iskallia.vault.network.message.ClientboundResetCryoChamberMessage;
import iskallia.vault.network.message.ClientboundSightParticlesFromJavelinMessage;
import iskallia.vault.network.message.ClientboundSyncSkillAltarDataMessage;
import iskallia.vault.network.message.ClientboundSyncVaultAllowWaypointsMessage;
import iskallia.vault.network.message.ClientboundToastMessage;
import iskallia.vault.network.message.ClientboundUpdateAltarIndexMessage;
import iskallia.vault.network.message.ClientboundUpdateDifficultyMessage;
import iskallia.vault.network.message.DiffuserParticleMessage;
import iskallia.vault.network.message.DiscoveredAlchemyModifierCraftsMessage;
import iskallia.vault.network.message.DiscoveredWorkbenchModifierCraftsMessage;
import iskallia.vault.network.message.EffectMessage;
import iskallia.vault.network.message.EternalInteractionMessage;
import iskallia.vault.network.message.EternalSyncMessage;
import iskallia.vault.network.message.ExpertiseLevelMessage;
import iskallia.vault.network.message.FighterSizeMessage;
import iskallia.vault.network.message.FloatingAltarItemParticleMessage;
import iskallia.vault.network.message.ForgeParticleMessage;
import iskallia.vault.network.message.ForgeRecipeSyncMessage;
import iskallia.vault.network.message.HistoricFavoritesMessage;
import iskallia.vault.network.message.InvalidConfigsMessage;
import iskallia.vault.network.message.JewelCuttingParticleMessage;
import iskallia.vault.network.message.KnownExpertisesMessage;
import iskallia.vault.network.message.KnownTalentsMessage;
import iskallia.vault.network.message.LuckyHitDamageParticleMessage;
import iskallia.vault.network.message.LuckyHitLeechParticleMessage;
import iskallia.vault.network.message.LuckyHitManaParticleMessage;
import iskallia.vault.network.message.LuckyHitParticleMessage;
import iskallia.vault.network.message.LuckyHitSweepingParticleMessage;
import iskallia.vault.network.message.LuckyHitVortexParticleMessage;
import iskallia.vault.network.message.MobCritParticleMessage;
import iskallia.vault.network.message.ModifierAlchemyCraftMessage;
import iskallia.vault.network.message.ModifierWorkbenchCraftMessage;
import iskallia.vault.network.message.MonolithIgniteMessage;
import iskallia.vault.network.message.NovaParticleMessage;
import iskallia.vault.network.message.OmegaShardGlobalTradeMessage;
import iskallia.vault.network.message.OmegaStatueUIMessage;
import iskallia.vault.network.message.OpenVaultSnapshotMessage;
import iskallia.vault.network.message.PartyMembersMessage;
import iskallia.vault.network.message.PartyStatusMessage;
import iskallia.vault.network.message.PlayerDamageMultiplierMessage;
import iskallia.vault.network.message.PlayerSnapshotMessage;
import iskallia.vault.network.message.PlayerStatisticsMessage;
import iskallia.vault.network.message.ProficiencyMessage;
import iskallia.vault.network.message.PylonConsumeParticleMessage;
import iskallia.vault.network.message.RecyclerParticleMessage;
import iskallia.vault.network.message.RenameUIMessage;
import iskallia.vault.network.message.ResearchMessage;
import iskallia.vault.network.message.ResearchTreeMessage;
import iskallia.vault.network.message.SandEventContributorMessage;
import iskallia.vault.network.message.SandEventUpdateMessage;
import iskallia.vault.network.message.ScavengerAltarConsumeMessage;
import iskallia.vault.network.message.ServerboundAbilityKeyMessage;
import iskallia.vault.network.message.ServerboundAbilitySelectMessage;
import iskallia.vault.network.message.ServerboundAddHistoricFavoriteMessage;
import iskallia.vault.network.message.ServerboundChangeDifficultyMessage;
import iskallia.vault.network.message.ServerboundCuriosScrollMessage;
import iskallia.vault.network.message.ServerboundMagnetToggleMessage;
import iskallia.vault.network.message.ServerboundOpenAbilitiesMessage;
import iskallia.vault.network.message.ServerboundOpenArchetypesMessage;
import iskallia.vault.network.message.ServerboundOpenExpertisesMessage;
import iskallia.vault.network.message.ServerboundOpenHistoricMessage;
import iskallia.vault.network.message.ServerboundOpenResearchesMessage;
import iskallia.vault.network.message.ServerboundOpenStatisticsMessage;
import iskallia.vault.network.message.ServerboundOpenTalentsMessage;
import iskallia.vault.network.message.ServerboundOpenVaultExitMessage;
import iskallia.vault.network.message.ServerboundPickaxeOffsetKeyMessage;
import iskallia.vault.network.message.ServerboundRenameEternalMessage;
import iskallia.vault.network.message.ServerboundResetBlackMarketTradesMessage;
import iskallia.vault.network.message.ServerboundSelectArchetypeMessage;
import iskallia.vault.network.message.ServerboundSendSnapshotLinkMessage;
import iskallia.vault.network.message.ServerboundSkillAltarActionMessage;
import iskallia.vault.network.message.ServerboundToggleEternalPlayerSkinMessage;
import iskallia.vault.network.message.ServerboundWardrobeSwapMessage;
import iskallia.vault.network.message.ServerboundWardrobeTabMessage;
import iskallia.vault.network.message.ServerboundWardrobeToggleSolidRenderMessage;
import iskallia.vault.network.message.ShardGlobalTradeMessage;
import iskallia.vault.network.message.ShardTradeMessage;
import iskallia.vault.network.message.ShardTradeTradeMessage;
import iskallia.vault.network.message.ShockedParticleMessage;
import iskallia.vault.network.message.SpiritExtractorMessage;
import iskallia.vault.network.message.StepHeightMessage;
import iskallia.vault.network.message.StonefallFrostParticleMessage;
import iskallia.vault.network.message.StonefallParticleMessage;
import iskallia.vault.network.message.StunnedParticleMessage;
import iskallia.vault.network.message.SummonElixirOrbMessage;
import iskallia.vault.network.message.SyncOverSizedContentMessage;
import iskallia.vault.network.message.SyncOverSizedStackMessage;
import iskallia.vault.network.message.TalentLevelMessage;
import iskallia.vault.network.message.TauntParticleMessage;
import iskallia.vault.network.message.ToolMessage;
import iskallia.vault.network.message.TrappedMobChestParticlesMessage;
import iskallia.vault.network.message.TrinketJumpMessage;
import iskallia.vault.network.message.VaultArtisanRequestModificationMessage;
import iskallia.vault.network.message.VaultCharmControllerScrollMessage;
import iskallia.vault.network.message.VaultEnchanterEnchantMessage;
import iskallia.vault.network.message.VaultEnhancementRequestMessage;
import iskallia.vault.network.message.VaultForgeRequestCraftMessage;
import iskallia.vault.network.message.VaultJewelApplicationStationMessage;
import iskallia.vault.network.message.VaultJewelCuttingRequestModificationMessage;
import iskallia.vault.network.message.VaultLevelMessage;
import iskallia.vault.network.message.VaultMessage;
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
import iskallia.vault.network.message.quest.QuestCompleteMessage;
import iskallia.vault.network.message.quest.QuestDebugModeMessage;
import iskallia.vault.network.message.quest.QuestProgressMessage;
import iskallia.vault.network.message.quest.QuestRequestSyncMessage;
import iskallia.vault.network.message.quest.QuestSyncMessage;
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
         ServerboundOpenExpertisesMessage.class,
         ServerboundOpenExpertisesMessage::encode,
         ServerboundOpenExpertisesMessage::decode,
         ServerboundOpenExpertisesMessage::handle
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
      CHANNEL.registerMessage(
         nextId(), ExpertiseLevelMessage.class, ExpertiseLevelMessage::encode, ExpertiseLevelMessage::decode, ExpertiseLevelMessage::handle
      );
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
         nextId(), AbilityCooldownMessage.class, AbilityCooldownMessage::encode, AbilityCooldownMessage::decode, AbilityCooldownMessage::handle
      );
      CHANNEL.registerMessage(
         nextId(), AbilityActivityMessage.class, AbilityActivityMessage::encode, AbilityActivityMessage::decode, AbilityActivityMessage::handle
      );
      CHANNEL.registerMessage(nextId(), FighterSizeMessage.class, FighterSizeMessage::encode, FighterSizeMessage::decode, FighterSizeMessage::handle);
      CHANNEL.registerMessage(nextId(), RenameUIMessage.class, RenameUIMessage::encode, RenameUIMessage::decode, RenameUIMessage::handle);
      CHANNEL.registerMessage(nextId(), StepHeightMessage.class, StepHeightMessage::encode, StepHeightMessage::decode, StepHeightMessage::handle);
      CHANNEL.registerMessage(nextId(), OmegaStatueUIMessage.class, OmegaStatueUIMessage::encode, OmegaStatueUIMessage::decode, OmegaStatueUIMessage::handle);
      CHANNEL.registerMessage(
         nextId(), AbilityQuickselectMessage.class, AbilityQuickselectMessage::encode, AbilityQuickselectMessage::decode, AbilityQuickselectMessage::handle
      );
      CHANNEL.registerMessage(nextId(), AngelToggleMessage.class, AngelToggleMessage::encode, AngelToggleMessage::decode, AngelToggleMessage::handle);
      CHANNEL.registerMessage(
         nextId(),
         ServerboundMagnetToggleMessage.class,
         ServerboundMagnetToggleMessage::encode,
         ServerboundMagnetToggleMessage::decode,
         ServerboundMagnetToggleMessage::handle
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
      CHANNEL.registerMessage(
         nextId(), KnownExpertisesMessage.class, KnownExpertisesMessage::encode, KnownExpertisesMessage::decode, KnownExpertisesMessage::handle
      );
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
      CHANNEL.registerMessage(
         nextId(),
         OmegaShardGlobalTradeMessage.class,
         OmegaShardGlobalTradeMessage::encode,
         OmegaShardGlobalTradeMessage::decode,
         OmegaShardGlobalTradeMessage::handle
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
         ServerboundResetBlackMarketTradesMessage.class,
         ServerboundResetBlackMarketTradesMessage::encode,
         ServerboundResetBlackMarketTradesMessage::decode,
         ServerboundResetBlackMarketTradesMessage::handle
      );
      CHANNEL.registerMessage(
         nextId(),
         ServerboundToggleEternalPlayerSkinMessage.class,
         ServerboundToggleEternalPlayerSkinMessage::encode,
         ServerboundToggleEternalPlayerSkinMessage::decode,
         ServerboundToggleEternalPlayerSkinMessage::handle
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
      CHANNEL.registerMessage(
         nextId(),
         VaultJewelCuttingRequestModificationMessage.class,
         VaultJewelCuttingRequestModificationMessage::encode,
         VaultJewelCuttingRequestModificationMessage::decode,
         VaultJewelCuttingRequestModificationMessage::handle
      );
      CHANNEL.registerMessage(
         nextId(),
         VaultJewelApplicationStationMessage.class,
         VaultJewelApplicationStationMessage::encode,
         VaultJewelApplicationStationMessage::decode,
         VaultJewelApplicationStationMessage::handle
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
         nextId(), SpiritExtractorMessage.class, SpiritExtractorMessage::encode, SpiritExtractorMessage::decode, SpiritExtractorMessage::handle
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
      CHANNEL.registerMessage(
         nextId(),
         JewelCuttingParticleMessage.class,
         JewelCuttingParticleMessage::encode,
         JewelCuttingParticleMessage::decode,
         JewelCuttingParticleMessage::handle
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
      CHANNEL.registerMessage(
         nextId(),
         ClientboundResetCryoChamberMessage.class,
         ClientboundResetCryoChamberMessage::encode,
         ClientboundResetCryoChamberMessage::decode,
         ClientboundResetCryoChamberMessage::handle
      );
      CHANNEL.registerMessage(
         nextId(), ChainingParticleMessage.class, ChainingParticleMessage::encode, ChainingParticleMessage::decode, ChainingParticleMessage::handle
      );
      CHANNEL.registerMessage(
         nextId(), ShockedParticleMessage.class, ShockedParticleMessage::encode, ShockedParticleMessage::decode, ShockedParticleMessage::handle
      );
      CHANNEL.registerMessage(
         nextId(),
         FloatingAltarItemParticleMessage.class,
         FloatingAltarItemParticleMessage::encode,
         FloatingAltarItemParticleMessage::decode,
         FloatingAltarItemParticleMessage::handle
      );
      CHANNEL.registerMessage(
         nextId(),
         PylonConsumeParticleMessage.class,
         PylonConsumeParticleMessage::encode,
         PylonConsumeParticleMessage::decode,
         PylonConsumeParticleMessage::handle
      );
      CHANNEL.registerMessage(nextId(), NovaParticleMessage.class, NovaParticleMessage::encode, NovaParticleMessage::decode, NovaParticleMessage::handle);
      CHANNEL.registerMessage(
         nextId(),
         StonefallFrostParticleMessage.class,
         StonefallFrostParticleMessage::encode,
         StonefallFrostParticleMessage::decode,
         StonefallFrostParticleMessage::handle
      );
      CHANNEL.registerMessage(
         nextId(), StonefallParticleMessage.class, StonefallParticleMessage::encode, StonefallParticleMessage::decode, StonefallParticleMessage::handle
      );
      CHANNEL.registerMessage(
         nextId(), StunnedParticleMessage.class, StunnedParticleMessage::encode, StunnedParticleMessage::decode, StunnedParticleMessage::handle
      );
      CHANNEL.registerMessage(nextId(), TauntParticleMessage.class, TauntParticleMessage::encode, TauntParticleMessage::decode, TauntParticleMessage::handle);
      CHANNEL.registerMessage(
         nextId(), MobCritParticleMessage.class, MobCritParticleMessage::encode, MobCritParticleMessage::decode, MobCritParticleMessage::handle
      );
      CHANNEL.registerMessage(
         nextId(), LuckyHitParticleMessage.class, LuckyHitParticleMessage::encode, LuckyHitParticleMessage::decode, LuckyHitParticleMessage::handle
      );
      CHANNEL.registerMessage(
         nextId(),
         LuckyHitSweepingParticleMessage.class,
         LuckyHitSweepingParticleMessage::encode,
         LuckyHitSweepingParticleMessage::decode,
         LuckyHitSweepingParticleMessage::handle
      );
      CHANNEL.registerMessage(
         nextId(),
         LuckyHitManaParticleMessage.class,
         LuckyHitManaParticleMessage::encode,
         LuckyHitManaParticleMessage::decode,
         LuckyHitManaParticleMessage::handle
      );
      CHANNEL.registerMessage(
         nextId(),
         LuckyHitLeechParticleMessage.class,
         LuckyHitLeechParticleMessage::encode,
         LuckyHitLeechParticleMessage::decode,
         LuckyHitLeechParticleMessage::handle
      );
      CHANNEL.registerMessage(
         nextId(),
         LuckyHitDamageParticleMessage.class,
         LuckyHitDamageParticleMessage::encode,
         LuckyHitDamageParticleMessage::decode,
         LuckyHitDamageParticleMessage::handle
      );
      CHANNEL.registerMessage(
         nextId(),
         LuckyHitVortexParticleMessage.class,
         LuckyHitVortexParticleMessage::encode,
         LuckyHitVortexParticleMessage::decode,
         LuckyHitVortexParticleMessage::handle
      );
      CHANNEL.registerMessage(nextId(), BonkParticleMessage.class, BonkParticleMessage::encode, BonkParticleMessage::decode, BonkParticleMessage::handle);
      CHANNEL.registerMessage(
         nextId(),
         ClientboundRefreshToolViseMessage.class,
         ClientboundRefreshToolViseMessage::encode,
         ClientboundRefreshToolViseMessage::decode,
         ClientboundRefreshToolViseMessage::handle
      );
      CHANNEL.registerMessage(
         nextId(),
         ClientboundRefreshSpiritExtractorMessage.class,
         ClientboundRefreshSpiritExtractorMessage::encode,
         ClientboundRefreshSpiritExtractorMessage::decode,
         ClientboundRefreshSpiritExtractorMessage::handle
      );
      CHANNEL.registerMessage(
         nextId(),
         ClientboundPlayerLastDamageSourceMessage.class,
         ClientboundPlayerLastDamageSourceMessage::encode,
         ClientboundPlayerLastDamageSourceMessage::decode,
         ClientboundPlayerLastDamageSourceMessage::handle
      );
      CHANNEL.registerMessage(nextId(), ToolMessage.Offset.class, ToolMessage.Offset::encode, ToolMessage.Offset::decode, ToolMessage.Offset::handle);
      CHANNEL.registerMessage(
         nextId(), ForgeRecipeSyncMessage.class, ForgeRecipeSyncMessage::encode, ForgeRecipeSyncMessage::decode, ForgeRecipeSyncMessage::handle
      );
      CHANNEL.registerMessage(
         nextId(),
         ServerboundWardrobeSwapMessage.class,
         ServerboundWardrobeSwapMessage::encode,
         ServerboundWardrobeSwapMessage::decode,
         ServerboundWardrobeSwapMessage::handle
      );
      CHANNEL.registerMessage(
         nextId(),
         ServerboundWardrobeToggleSolidRenderMessage.class,
         ServerboundWardrobeToggleSolidRenderMessage::encode,
         ServerboundWardrobeToggleSolidRenderMessage::decode,
         ServerboundWardrobeToggleSolidRenderMessage::handle
      );
      CHANNEL.registerMessage(
         nextId(), SummonElixirOrbMessage.class, SummonElixirOrbMessage::encode, SummonElixirOrbMessage::decode, SummonElixirOrbMessage::handle
      );
      CHANNEL.registerMessage(
         nextId(),
         ServerboundWardrobeTabMessage.class,
         ServerboundWardrobeTabMessage::encode,
         ServerboundWardrobeTabMessage::decode,
         ServerboundWardrobeTabMessage::handle
      );
      CHANNEL.registerMessage(
         nextId(),
         VaultEnhancementRequestMessage.class,
         VaultEnhancementRequestMessage::encode,
         VaultEnhancementRequestMessage::decode,
         VaultEnhancementRequestMessage::handle
      );
      CHANNEL.registerMessage(
         nextId(),
         DiscoveredWorkbenchModifierCraftsMessage.class,
         DiscoveredWorkbenchModifierCraftsMessage::encode,
         DiscoveredWorkbenchModifierCraftsMessage::decode,
         DiscoveredWorkbenchModifierCraftsMessage::handle
      );
      CHANNEL.registerMessage(
         nextId(),
         DiscoveredAlchemyModifierCraftsMessage.class,
         DiscoveredAlchemyModifierCraftsMessage::encode,
         DiscoveredAlchemyModifierCraftsMessage::decode,
         DiscoveredAlchemyModifierCraftsMessage::handle
      );
      CHANNEL.registerMessage(
         nextId(),
         ModifierWorkbenchCraftMessage.class,
         ModifierWorkbenchCraftMessage::encode,
         ModifierWorkbenchCraftMessage::decode,
         ModifierWorkbenchCraftMessage::handle
      );
      CHANNEL.registerMessage(
         nextId(),
         ModifierAlchemyCraftMessage.class,
         ModifierAlchemyCraftMessage::encode,
         ModifierAlchemyCraftMessage::decode,
         ModifierAlchemyCraftMessage::handle
      );
      CHANNEL.registerMessage(
         nextId(),
         VaultEnchanterEnchantMessage.class,
         VaultEnchanterEnchantMessage::encode,
         VaultEnchanterEnchantMessage::decode,
         VaultEnchanterEnchantMessage::handle
      );
      CHANNEL.registerMessage(
         nextId(),
         ServerboundSkillAltarActionMessage.class,
         ServerboundSkillAltarActionMessage::encode,
         ServerboundSkillAltarActionMessage::decode,
         ServerboundSkillAltarActionMessage::handle
      );
      CHANNEL.registerMessage(
         nextId(),
         ClientboundSyncSkillAltarDataMessage.class,
         ClientboundSyncSkillAltarDataMessage::encode,
         ClientboundSyncSkillAltarDataMessage::decode,
         ClientboundSyncSkillAltarDataMessage::handle
      );
      CHANNEL.registerMessage(nextId(), QuestSyncMessage.class, QuestSyncMessage::encode, QuestSyncMessage::decode, QuestSyncMessage::handle);
      CHANNEL.registerMessage(nextId(), QuestCompleteMessage.class, QuestCompleteMessage::encode, QuestCompleteMessage::decode, QuestCompleteMessage::handle);
      CHANNEL.registerMessage(
         nextId(), QuestRequestSyncMessage.class, QuestRequestSyncMessage::encode, QuestRequestSyncMessage::decode, QuestRequestSyncMessage::handle
      );
      CHANNEL.registerMessage(nextId(), QuestProgressMessage.class, QuestProgressMessage::encode, QuestProgressMessage::decode, QuestProgressMessage::handle);
      CHANNEL.registerMessage(
         nextId(), QuestDebugModeMessage.class, QuestDebugModeMessage::encode, QuestDebugModeMessage::decode, QuestDebugModeMessage::handle
      );
      CHANNEL.registerMessage(
         nextId(), ClientboundToastMessage.class, ClientboundToastMessage::encode, ClientboundToastMessage::decode, ClientboundToastMessage::handle
      );
      CHANNEL.registerMessage(
         nextId(),
         ClientboundMobEffectUpdateMessage.class,
         ClientboundMobEffectUpdateMessage::encode,
         ClientboundMobEffectUpdateMessage::decode,
         ClientboundMobEffectUpdateMessage::handle
      );
      CHANNEL.registerMessage(
         nextId(),
         ClientboundMobEffectRemoveMessage.class,
         ClientboundMobEffectRemoveMessage::encode,
         ClientboundMobEffectRemoveMessage::decode,
         ClientboundMobEffectRemoveMessage::handle
      );
      CHANNEL.registerMessage(
         nextId(),
         ClientboundHunterParticlesMessage.class,
         ClientboundHunterParticlesMessage::encode,
         ClientboundHunterParticlesMessage::decode,
         ClientboundHunterParticlesMessage::handle
      );
      CHANNEL.registerMessage(
         nextId(),
         ClientboundHunterParticlesFromJavelinMessage.class,
         ClientboundHunterParticlesFromJavelinMessage::encode,
         ClientboundHunterParticlesFromJavelinMessage::decode,
         ClientboundHunterParticlesFromJavelinMessage::handle
      );
      CHANNEL.registerMessage(
         nextId(),
         ClientboundSightParticlesFromJavelinMessage.class,
         ClientboundSightParticlesFromJavelinMessage::encode,
         ClientboundSightParticlesFromJavelinMessage::decode,
         ClientboundSightParticlesFromJavelinMessage::handle
      );
      CHANNEL.registerMessage(
         nextId(),
         ClientboundFireballExplosionMessage.class,
         ClientboundFireballExplosionMessage::encode,
         ClientboundFireballExplosionMessage::decode,
         ClientboundFireballExplosionMessage::handle
      );
      CHANNEL.registerMessage(
         nextId(),
         ClientboundNightVisionGogglesParticlesMessage.class,
         ClientboundNightVisionGogglesParticlesMessage::encode,
         ClientboundNightVisionGogglesParticlesMessage::decode,
         ClientboundNightVisionGogglesParticlesMessage::handle
      );
      CHANNEL.registerMessage(
         nextId(), ClientboundChampionMessage.class, ClientboundChampionMessage::encode, ClientboundChampionMessage::decode, ClientboundChampionMessage::handle
      );
   }

   public static int nextId() {
      return ID++;
   }
}
