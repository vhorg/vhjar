package iskallia.vault.client.gui.screen.player;

import iskallia.vault.client.ClientStatisticsData;
import iskallia.vault.client.gui.framework.render.Tooltips;
import iskallia.vault.client.gui.framework.render.spi.ITooltipRenderFunction;
import iskallia.vault.client.gui.overlay.VaultBarOverlay;
import iskallia.vault.client.gui.screen.player.element.GearAttributeStatLabel;
import iskallia.vault.client.gui.screen.player.element.StatLabel;
import iskallia.vault.client.gui.screen.player.element.StatLabelElementBuilder;
import iskallia.vault.client.gui.screen.player.element.VaultGodFavorIconElement;
import iskallia.vault.config.MenuPlayerStatDescriptionConfig;
import iskallia.vault.core.vault.influence.VaultGod;
import iskallia.vault.core.vault.stat.StatTotals;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.mana.Mana;
import iskallia.vault.util.StatUtils;
import iskallia.vault.util.calc.AbilityPowerHelper;
import iskallia.vault.util.calc.AttributeLimitHelper;
import iskallia.vault.util.calc.CooldownHelper;
import iskallia.vault.util.calc.LuckyHitHelper;
import iskallia.vault.util.calc.SoulChanceHelper;
import iskallia.vault.util.function.Memo;
import java.beans.Introspector;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.extensions.IForgePlayer;
import org.jetbrains.annotations.NotNull;

public class StatisticsElementContainerScreenData {
   protected final Player player;
   private static final TextColor EXTENDED_DESCRIPTION_COLOR = TextColor.fromLegacyFormat(ChatFormatting.GRAY);
   private static final TextColor EXTENDED_POSITIVE_INFLUENCES_COLOR = TextColor.fromLegacyFormat(ChatFormatting.GREEN);
   private static final TextColor EXTENDED_NEGATIVE_INFLUENCES_COLOR = TextColor.fromLegacyFormat(ChatFormatting.RED);
   private static final Style EXTENDED_DESCRIPTION_STYLE = Style.EMPTY.withColor(EXTENDED_DESCRIPTION_COLOR);
   private static final Style EXTENDED_POSITIVE_INFLUENCES_STYLE = Style.EMPTY.withColor(EXTENDED_POSITIVE_INFLUENCES_COLOR);
   private static final Style EXTENDED_NEGATIVE_INFLUENCES_STYLE = Style.EMPTY.withColor(EXTENDED_NEGATIVE_INFLUENCES_COLOR);
   private static final TextComponent BLANK_LINE = new TextComponent(" ");

   public StatisticsElementContainerScreenData(Player player) {
      this.player = player;
   }

   protected float getVaultLevelPercentage() {
      return (float)VaultBarOverlay.vaultExp / VaultBarOverlay.tnl;
   }

   protected int getVaultLevel() {
      return VaultBarOverlay.vaultLevel;
   }

   protected ITooltipRenderFunction getVaultLevelTooltip() {
      return Tooltips.shift(
         Tooltips.multi(Memo.of(() -> List.of(new TextComponent("Vault Experience"), Tooltips.DEFAULT_HOLD_SHIFT_COMPONENT))),
         Tooltips.multi(
            () -> List.of(
               new TextComponent("Vault Experience"),
               new TextComponent("Experience: " + VaultBarOverlay.vaultExp + "/" + VaultBarOverlay.tnl).withStyle(ChatFormatting.GRAY),
               new TextComponent("Level: " + VaultBarOverlay.vaultLevel).withStyle(ChatFormatting.GRAY)
            )
         )
      );
   }

   @NotNull
   protected VaultGodFavorIconElement.ValueSupplier getValueProviderIdona() {
      return VaultGodFavorIconElement.ValueSupplier.of(
         this::getReputationIdona, this::getTooltipTitleIdona, this::getTooltipExtendedIdona, this.getColorIdona()
      );
   }

   private int getReputationIdona() {
      return ClientStatisticsData.getReputation(VaultGod.IDONA);
   }

   private int getColorIdona() {
      return ClientStatisticsData.getFavour().orElse(null) == VaultGod.IDONA ? 16755200 : 16777215;
   }

   @NotNull
   private TextComponent getTooltipTitleIdona() {
      return this.getGodFavorTitle(VaultGod.IDONA);
   }

   @NotNull
   private List<Component> getTooltipExtendedIdona() {
      return List.of(
         this.getGodFavorTitle(VaultGod.IDONA),
         this.getGodFavorDescription(VaultGod.IDONA).withStyle(EXTENDED_DESCRIPTION_STYLE),
         BLANK_LINE,
         new TextComponent("  + Soul Shard Drop %").withStyle(EXTENDED_DESCRIPTION_STYLE),
         new TextComponent("  + Damage %").withStyle(EXTENDED_DESCRIPTION_STYLE),
         new TextComponent("  + Life Leech %").withStyle(EXTENDED_DESCRIPTION_STYLE)
      );
   }

   @NotNull
   protected VaultGodFavorIconElement.ValueSupplier getValueProviderTenos() {
      return VaultGodFavorIconElement.ValueSupplier.of(
         this::getReputationTenos, this::getTooltipTitleTenos, this::getTooltipExtendedTenos, this.getColorTenos()
      );
   }

   private int getReputationTenos() {
      return ClientStatisticsData.getReputation(VaultGod.TENOS);
   }

   @NotNull
   private TextComponent getTooltipTitleTenos() {
      return this.getGodFavorTitle(VaultGod.TENOS);
   }

   private int getColorTenos() {
      return ClientStatisticsData.getFavour().orElse(null) == VaultGod.TENOS ? 16755200 : 16777215;
   }

   @NotNull
   private List<Component> getTooltipExtendedTenos() {
      return List.of(
         this.getGodFavorTitle(VaultGod.TENOS),
         this.getGodFavorDescription(VaultGod.TENOS).withStyle(EXTENDED_DESCRIPTION_STYLE),
         BLANK_LINE,
         new TextComponent("  + Item Rarity %").withStyle(EXTENDED_DESCRIPTION_STYLE),
         new TextComponent("  + Item Quantity %").withStyle(EXTENDED_DESCRIPTION_STYLE),
         new TextComponent("  - Chest Trap Chance %").withStyle(EXTENDED_DESCRIPTION_STYLE)
      );
   }

   @NotNull
   protected VaultGodFavorIconElement.ValueSupplier getValueProviderVelara() {
      return VaultGodFavorIconElement.ValueSupplier.of(
         this::getReputationVelara, this::getTooltipTitleVelara, this::getTooltipExtendedVelara, this.getColorVelara()
      );
   }

   private int getReputationVelara() {
      return ClientStatisticsData.getReputation(VaultGod.VELARA);
   }

   private int getColorVelara() {
      return ClientStatisticsData.getFavour().orElse(null) == VaultGod.VELARA ? 16755200 : 16777215;
   }

   @NotNull
   private TextComponent getTooltipTitleVelara() {
      return this.getGodFavorTitle(VaultGod.VELARA);
   }

   @NotNull
   private List<Component> getTooltipExtendedVelara() {
      return List.of(
         this.getGodFavorTitle(VaultGod.VELARA),
         this.getGodFavorDescription(VaultGod.VELARA).withStyle(EXTENDED_DESCRIPTION_STYLE),
         BLANK_LINE,
         new TextComponent("  + Regeneration").withStyle(EXTENDED_DESCRIPTION_STYLE),
         new TextComponent("  + Healing Efficiency %").withStyle(EXTENDED_DESCRIPTION_STYLE),
         new TextComponent("  + Max Health %").withStyle(EXTENDED_DESCRIPTION_STYLE)
      );
   }

   @NotNull
   protected VaultGodFavorIconElement.ValueSupplier getValueProviderWendarr() {
      return VaultGodFavorIconElement.ValueSupplier.of(
         this::getReputationWendarr, this::getTooltipTitleWendarr, this::getTooltipExtendedWendarr, this.getColorWendarr()
      );
   }

   private int getReputationWendarr() {
      return ClientStatisticsData.getReputation(VaultGod.WENDARR);
   }

   private int getColorWendarr() {
      return ClientStatisticsData.getFavour().orElse(null) == VaultGod.WENDARR ? 16755200 : 16777215;
   }

   @NotNull
   private TextComponent getTooltipTitleWendarr() {
      return this.getGodFavorTitle(VaultGod.WENDARR);
   }

   @NotNull
   private List<Component> getTooltipExtendedWendarr() {
      return List.of(
         this.getGodFavorTitle(VaultGod.WENDARR),
         this.getGodFavorDescription(VaultGod.WENDARR).withStyle(EXTENDED_DESCRIPTION_STYLE),
         BLANK_LINE,
         new TextComponent("  + Mana Regeneration %").withStyle(EXTENDED_DESCRIPTION_STYLE),
         new TextComponent("  + Maximum Mana %").withStyle(EXTENDED_DESCRIPTION_STYLE),
         new TextComponent("  + Cooldown Reduction %").withStyle(EXTENDED_DESCRIPTION_STYLE)
      );
   }

   @NotNull
   private TextComponent getGodFavorTitle(VaultGod vaultGodType) {
      return new TextComponent(vaultGodType.getName() + " " + Introspector.decapitalize(vaultGodType.getTitle()));
   }

   @NotNull
   private TextComponent getGodFavorDescription(VaultGod vaultGodType) {
      return new TextComponent(
         "Complete a%s %s altar for a chance to gain favour with %s. A favour will grant a buff in the subsequent vault. Completing the objective in said vault will grant a reputation point which slowly increases the power of the buffs."
            .formatted(vaultGodType == VaultGod.IDONA ? "n" : "", vaultGodType.getName(), vaultGodType.getName())
      );
   }

   @NotNull
   private TextComponent getPositiveInfluenceHeader() {
      return new TextComponent("Positive Influences");
   }

   @NotNull
   private TextComponent getNegativeInfluencesHeader() {
      return new TextComponent("Negative Influences");
   }

   @Nonnull
   protected List<StatLabelElementBuilder<?>> getStatListProminent() {
      return List.of(
         StatLabel.ofDouble(
            () -> "Damage",
            () -> ModConfigs.MENU_PLAYER_STAT_DESCRIPTIONS.getProminentStatDescriptionFor("damage_per_second"),
            () -> StatUtils.getAverageDps(this.player)
         ),
         StatLabel.ofFloat(() -> "Health", () -> ModConfigs.MENU_PLAYER_STAT_DESCRIPTIONS.getProminentStatDescriptionFor("health"), this::getPlayerMaxHealth),
         StatLabel.ofDoublePercent(
            () -> "Defense", () -> ModConfigs.MENU_PLAYER_STAT_DESCRIPTIONS.getProminentStatDescriptionFor("defense"), () -> StatUtils.getDefence(this.player)
         ),
         StatLabel.ofInteger(() -> "Mana", () -> ModConfigs.MENU_PLAYER_STAT_DESCRIPTIONS.getProminentStatDescriptionFor("mana"), this::getPlayerMaxMana),
         StatLabel.ofInteger(() -> "Greed", () -> ModConfigs.MENU_PLAYER_STAT_DESCRIPTIONS.getProminentStatDescriptionFor("greed"), () -> 0)
      );
   }

   private float getPlayerMaxHealth() {
      AttributeInstance attribute = this.player.getAttribute(Attributes.MAX_HEALTH);
      return attribute == null ? 0.0F : (float)attribute.getValue();
   }

   private int getPlayerMaxMana() {
      return (int)Mana.getMax(this.player);
   }

   @Nonnull
   protected List<StatLabelElementBuilder<?>> getStatListPlayer() {
      List<StatLabelElementBuilder<?>> result = new ArrayList<>(
         List.of(
            GearAttributeStatLabel.of(this.player, ModGearAttributes.ARMOR, LivingEntity::getArmorValue),
            GearAttributeStatLabel.of(this.player, ModGearAttributes.ATTACK_DAMAGE, player -> player.getAttributeValue(Attributes.ATTACK_DAMAGE)),
            GearAttributeStatLabel.of(this.player, ModGearAttributes.ABILITY_POWER, AbilityPowerHelper::getAbilityPower),
            GearAttributeStatLabel.of(this.player, ModGearAttributes.ATTACK_SPEED, player -> player.getAttributeValue(Attributes.ATTACK_SPEED) - 4.0),
            GearAttributeStatLabel.of(this.player, ModGearAttributes.REACH, IForgePlayer::getReachDistance),
            GearAttributeStatLabel.of(this.player, ModGearAttributes.ATTACK_RANGE, IForgePlayer::getAttackRange),
            GearAttributeStatLabel.of(
               this.player, ModGearAttributes.KNOCKBACK_RESISTANCE, player -> (float)player.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE)
            ),
            GearAttributeStatLabel.of(this.player, ModGearAttributes.HEALTH, LivingEntity::getMaxHealth),
            GearAttributeStatLabel.ofFloat(this.player, ModGearAttributes.HEALING_EFFECTIVENESS),
            GearAttributeStatLabel.of(this.player, ModGearAttributes.MANA_REGEN_ADDITIVE_PERCENTILE, Mana::getRegenPerSecond),
            GearAttributeStatLabel.of(this.player, ModGearAttributes.MANA_ADDITIVE, player -> Mth.floor(Mana.getMax(player))),
            GearAttributeStatLabel.ofFloat(
               this.player,
               ModGearAttributes.COOLDOWN_REDUCTION,
               CooldownHelper::getCooldownMultiplierUnlimited,
               AttributeLimitHelper::getCooldownReductionLimit
            ),
            GearAttributeStatLabel.ofFloat(this.player, ModGearAttributes.RESISTANCE, AttributeLimitHelper::getResistanceLimit),
            GearAttributeStatLabel.ofFloat(this.player, ModGearAttributes.BLOCK, AttributeLimitHelper::getBlockChanceLimit),
            GearAttributeStatLabel.ofFloat(this.player, ModGearAttributes.CRITICAL_HIT_TAKEN_REDUCTION),
            GearAttributeStatLabel.ofFloat(this.player, ModGearAttributes.DURABILITY_WEAR_REDUCTION),
            GearAttributeStatLabel.ofFloat(this.player, ModGearAttributes.THORNS_DAMAGE_FLAT),
            GearAttributeStatLabel.ofInteger(this.player, ModGearAttributes.ON_HIT_CHAIN),
            GearAttributeStatLabel.ofFloat(this.player, ModGearAttributes.ON_HIT_STUN),
            GearAttributeStatLabel.ofFloat(this.player, ModGearAttributes.SHOCKING_HIT_CHANCE),
            GearAttributeStatLabel.ofFloat(this.player, ModGearAttributes.SWEEPING_HIT_CHANCE),
            GearAttributeStatLabel.ofFloat(this.player, ModGearAttributes.ITEM_QUANTITY),
            GearAttributeStatLabel.ofFloat(this.player, ModGearAttributes.ITEM_RARITY),
            GearAttributeStatLabel.ofFloat(this.player, ModGearAttributes.TRAP_DISARMING),
            GearAttributeStatLabel.of(this.player, ModGearAttributes.SOUL_CHANCE, SoulChanceHelper::getSoulChance),
            GearAttributeStatLabel.of(this.player, ModGearAttributes.LUCKY_HIT_CHANCE, LuckyHitHelper::getLuckyHitChance),
            GearAttributeStatLabel.ofFloat(this.player, ModGearAttributes.COPIOUSLY),
            GearAttributeStatLabel.ofFloat(this.player, ModGearAttributes.MINING_SPEED),
            GearAttributeStatLabel.ofFloat(this.player, ModGearAttributes.DAMAGE_INCREASE),
            GearAttributeStatLabel.ofFloat(this.player, ModGearAttributes.DAMAGE_ILLAGERS),
            GearAttributeStatLabel.ofFloat(this.player, ModGearAttributes.DAMAGE_SPIDERS),
            GearAttributeStatLabel.ofFloat(this.player, ModGearAttributes.DAMAGE_UNDEAD),
            GearAttributeStatLabel.ofFloat(this.player, ModGearAttributes.DAMAGE_NETHER)
         )
      );
      result.sort(StatLabelElementBuilder.COMPARATOR);
      result.addAll(
         List.of(
            GearAttributeStatLabel.ofFloat(this.player, ModGearAttributes.VELARA_AFFINITY),
            GearAttributeStatLabel.ofFloat(this.player, ModGearAttributes.TENOS_AFFINITY),
            GearAttributeStatLabel.ofFloat(this.player, ModGearAttributes.WENDARR_AFFINITY),
            GearAttributeStatLabel.ofFloat(this.player, ModGearAttributes.IDONA_AFFINITY)
         )
      );
      return result;
   }

   @Nonnull
   protected List<StatLabelElementBuilder<?>> getStatListVault(StatTotals data) {
      MenuPlayerStatDescriptionConfig config = ModConfigs.MENU_PLAYER_STAT_DESCRIPTIONS;
      return List.of(
         StatLabel.ofInteger(() -> "Total Vaults", () -> config.getVaultStatDescriptionFor("vaults_total"), data::getTotalVaults),
         StatLabel.ofInteger(() -> "Completed", () -> config.getVaultStatDescriptionFor("vaults_completed"), data::getCompleted),
         StatLabel.ofInteger(() -> "Survived", () -> config.getVaultStatDescriptionFor("vaults_bailed"), data::getBailed),
         StatLabel.ofInteger(() -> "Failed", () -> config.getVaultStatDescriptionFor("vaults_failed"), data::getFailed),
         StatLabel.ofInteger(() -> "Experience", () -> config.getVaultStatDescriptionFor("experience"), data::getExperience),
         StatLabel.ofFloat(
            () -> "Damage Dealt",
            () -> config.getVaultStatDescriptionFor("damage_dealt"),
            Memo.of(() -> data.getDamageDealt().values().stream().reduce(0.0F, Float::sum))
         ),
         StatLabel.ofFloat(
            () -> "Damage Taken",
            () -> config.getVaultStatDescriptionFor("damage_taken"),
            Memo.of(() -> data.getDamageReceived().values().stream().reduce(0.0F, Float::sum))
         ),
         StatLabel.ofInteger(
            () -> "Mobs Unalived",
            () -> config.getVaultStatDescriptionFor("mobs_unalived"),
            Memo.of(() -> data.getEntitiesKilled().values().stream().reduce(0, Integer::sum))
         ),
         StatLabel.ofInteger(
            () -> "Blocks Mined",
            () -> config.getVaultStatDescriptionFor("blocks_mined"),
            Memo.of(() -> data.getMinedBlocks().values().stream().reduce(0, Integer::sum))
         ),
         StatLabel.ofInteger(
            () -> "Trapped Chests",
            () -> config.getVaultStatDescriptionFor("trapped_chests"),
            Memo.of(() -> data.getTrappedChests().values().stream().reduce(0, Integer::sum))
         ),
         StatLabel.ofInteger(
            () -> "Chests Looted",
            () -> config.getVaultStatDescriptionFor("chests_looted"),
            Memo.of(() -> data.getLootedChests().values().stream().reduce(0, Integer::sum))
         ),
         StatLabel.ofInteger(() -> "Treasure Rooms Opened", () -> config.getVaultStatDescriptionFor("treasure_rooms_opened"), data::getTreasureRoomsOpened),
         StatLabel.ofInteger(() -> "Crystals Crafted", () -> config.getVaultStatDescriptionFor("crystals_crafted"), data::getCrystalsCrafted)
      );
   }
}
