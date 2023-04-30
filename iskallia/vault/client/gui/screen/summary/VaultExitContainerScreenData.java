package iskallia.vault.client.gui.screen.summary;

import iskallia.vault.client.gui.framework.render.Tooltips;
import iskallia.vault.client.gui.framework.render.spi.ITooltipRenderFunction;
import iskallia.vault.client.gui.helper.UIHelper;
import iskallia.vault.client.gui.overlay.VaultBarOverlay;
import iskallia.vault.client.gui.screen.summary.element.StatLabelListElement;
import iskallia.vault.core.vault.Modifiers;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.player.Completion;
import iskallia.vault.core.vault.stat.StatCollector;
import iskallia.vault.core.vault.stat.VaultChestType;
import iskallia.vault.core.vault.stat.VaultSnapshot;
import iskallia.vault.core.vault.time.TickClock;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.util.VaultRarity;
import iskallia.vault.util.function.Memo;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nonnull;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;

public class VaultExitContainerScreenData {
   protected final VaultSnapshot snapshot;
   private final UUID asPlayer;

   public VaultExitContainerScreenData(VaultSnapshot snapshot, UUID asPlayer) {
      this.snapshot = snapshot;
      this.asPlayer = asPlayer;
   }

   protected int getUnspentSkillPoints() {
      return VaultBarOverlay.unspentSkillPoints;
   }

   protected int getUnspentKnowledgePoints() {
      return VaultBarOverlay.unspentKnowledgePoints;
   }

   public VaultSnapshot getSnapshot() {
      return this.snapshot;
   }

   public float getVaultLevelPercentageWithReward(Vault vault) {
      int reward = this.getStatsCollector().getExperience(vault);
      return (float)(VaultBarOverlay.vaultExp + reward) / VaultBarOverlay.tnl;
   }

   public float getVaultLevelPercentage() {
      return (float)VaultBarOverlay.vaultExp / VaultBarOverlay.tnl;
   }

   public int getVaultLevel() {
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

   public StatCollector getStatsCollector() {
      Vault vault = this.snapshot.getEnd();
      return vault.get(Vault.STATS).get(this.asPlayer);
   }

   public Modifiers getModifiers() {
      Vault vault = this.snapshot.getEnd();
      return vault.get(Vault.MODIFIERS);
   }

   public static List<Component> getXpReceipt(Vault vault, StatCollector statCollector, boolean shiftDown, float xpMultiplier) {
      List<Component> list = new ArrayList<>();
      float chestsXp = getTotalChestsXp(statCollector);
      list.add(
         new TextComponent("  Chests - ")
            .append(shiftDown ? String.format("(x%.1f) %.1f xp", xpMultiplier, chestsXp) : String.format("%.1f xp", chestsXp * xpMultiplier))
      );
      float minedBlocksXp = getMinedBlocksXp(statCollector);
      list.add(
         new TextComponent("  Mined Blocks - ")
            .append(shiftDown ? String.format("(x%.1f) %.1f xp", xpMultiplier, minedBlocksXp) : String.format("%.1f xp", minedBlocksXp * xpMultiplier))
      );
      float treasureRoomXp = getTreasureRoomXp(statCollector);
      list.add(
         new TextComponent("  Treasure Rooms - ")
            .append(shiftDown ? String.format("(x%.1f) %.1f xp", xpMultiplier, treasureRoomXp) : String.format("%.1f xp", treasureRoomXp * xpMultiplier))
      );
      float mobsKilledXp = getMobsKilledXp(statCollector);
      list.add(
         new TextComponent("  Mobs Unalived - ")
            .append(shiftDown ? String.format("(x%.1f) %.1f xp", xpMultiplier, mobsKilledXp) : String.format("%.1f xp", mobsKilledXp * xpMultiplier))
      );
      float completionXp = getCompletionXp(vault, statCollector);
      list.add(
         new TextComponent("  Objective - ")
            .append(shiftDown ? String.format("(x%.1f) %.1f xp", xpMultiplier, completionXp) : String.format("%.1f xp", completionXp * xpMultiplier))
      );
      return list;
   }

   public static int getChestCount(StatCollector statCollector, VaultChestType chestType, VaultRarity vaultRarity) {
      return statCollector.getLootedChests(chestType, vaultRarity);
   }

   public static int getTrappedChestCount(StatCollector statCollector, VaultChestType chestType) {
      return statCollector.getTrappedChests(chestType);
   }

   public static float getMinedBlocksXp(StatCollector statCollector) {
      float xp = 0.0F;
      Map<ResourceLocation, Float> map = ModConfigs.VAULT_STATS.getBlocksMined();
      Object2IntMap<ResourceLocation> group = statCollector.getMinedBlocks();
      ObjectIterator var4 = group.object2IntEntrySet().iterator();

      while (var4.hasNext()) {
         Entry<ResourceLocation> entry = (Entry<ResourceLocation>)var4.next();
         ResourceLocation loc = (ResourceLocation)entry.getKey();
         int amount = entry.getIntValue();
         float xpMul = map.get(loc);
         xp += xpMul * amount;
      }

      return xp;
   }

   public static float getMinedOresXp(StatCollector statCollector) {
      float xp = 0.0F;
      Map<ResourceLocation, Float> map = ModConfigs.VAULT_STATS.getBlocksMined();
      Object2IntMap<ResourceLocation> group = statCollector.getMinedBlocks();
      ObjectIterator var4 = group.object2IntEntrySet().iterator();

      while (var4.hasNext()) {
         Entry<ResourceLocation> entry = (Entry<ResourceLocation>)var4.next();
         ResourceLocation loc = (ResourceLocation)entry.getKey();
         int amount = entry.getIntValue();
         String path = loc.getPath();
         if (!path.equals("treasure_sand") && !path.equals("coin_pile")) {
            float xpMul = map.get(loc);
            xp += xpMul * amount;
         }
      }

      return xp;
   }

   public static float getCompletionXp(Vault vault, StatCollector statCollector) {
      return ModConfigs.VAULT_STATS.getCompletion(vault).get(statCollector.getCompletion());
   }

   public static int getCoinPile(StatCollector statCollector) {
      int coinPile = 0;
      Object2IntMap<ResourceLocation> group = statCollector.getMinedBlocks();
      ObjectIterator var3 = group.object2IntEntrySet().iterator();

      while (var3.hasNext()) {
         Entry<ResourceLocation> entry = (Entry<ResourceLocation>)var3.next();
         ResourceLocation loc = (ResourceLocation)entry.getKey();
         int amount = entry.getIntValue();
         String path = loc.getPath();
         if (path.equals("coin_pile")) {
            coinPile = amount;
         }
      }

      return coinPile;
   }

   public static int getTreasureSand(StatCollector statCollector) {
      int treasureSand = 0;
      Object2IntMap<ResourceLocation> group = statCollector.getMinedBlocks();
      ObjectIterator var3 = group.object2IntEntrySet().iterator();

      while (var3.hasNext()) {
         Entry<ResourceLocation> entry = (Entry<ResourceLocation>)var3.next();
         ResourceLocation loc = (ResourceLocation)entry.getKey();
         int amount = entry.getIntValue();
         String path = loc.getPath();
         if (path.equals("treasure_sand")) {
            treasureSand = amount;
         }
      }

      return treasureSand;
   }

   public static int getOresMined(StatCollector statCollector) {
      int oresMined = 0;
      Object2IntMap<ResourceLocation> group = statCollector.getMinedBlocks();
      ObjectIterator var3 = group.object2IntEntrySet().iterator();

      while (var3.hasNext()) {
         Entry<ResourceLocation> entry = (Entry<ResourceLocation>)var3.next();
         ResourceLocation loc = (ResourceLocation)entry.getKey();
         int amount = entry.getIntValue();
         String path = loc.getPath();
         if (!path.equals("treasure_sand") && !path.equals("coin_pile")) {
            oresMined += amount;
         }
      }

      return oresMined;
   }

   public static int getMobsKilled(StatCollector statCollector) {
      AtomicInteger mobsKilled = new AtomicInteger();
      statCollector.getEntitiesKilled().forEach((resourceLocation, integer) -> mobsKilled.updateAndGet(v -> v + integer));
      return mobsKilled.get();
   }

   public static float getMobsKilledXp(StatCollector statCollector) {
      AtomicReference<Float> totalXp = new AtomicReference<>(0.0F);
      statCollector.getEntitiesKilled().forEach((resourceLocation, integer) -> {
         Map<ResourceLocation, Float> map = ModConfigs.VAULT_STATS.getMobsKilled();
         float xpMul = map.getOrDefault(resourceLocation, map.get(new ResourceLocation("default")));
         float xp = xpMul * integer.intValue();
         totalXp.updateAndGet(v -> v + xp);
      });
      return totalXp.get();
   }

   public static float getTreasureRoomXp(StatCollector statCollector) {
      return statCollector.getTreasureRoomsOpened() * ModConfigs.VAULT_STATS.getTreasureRoomsOpened();
   }

   public static int getTotalChests(StatCollector statCollector) {
      int woodenOmega = getChestCount(statCollector, VaultChestType.WOODEN, VaultRarity.OMEGA);
      int woodenEpic = getChestCount(statCollector, VaultChestType.WOODEN, VaultRarity.EPIC);
      int woodenRare = getChestCount(statCollector, VaultChestType.WOODEN, VaultRarity.RARE);
      int woodenCommon = getChestCount(statCollector, VaultChestType.WOODEN, VaultRarity.COMMON);
      int woodenTrapped = getTrappedChestCount(statCollector, VaultChestType.WOODEN);
      int gildedOmega = getChestCount(statCollector, VaultChestType.GILDED, VaultRarity.OMEGA);
      int gildedEpic = getChestCount(statCollector, VaultChestType.GILDED, VaultRarity.EPIC);
      int gildedRare = getChestCount(statCollector, VaultChestType.GILDED, VaultRarity.RARE);
      int gildedCommon = getChestCount(statCollector, VaultChestType.GILDED, VaultRarity.COMMON);
      int gildedTrapped = getTrappedChestCount(statCollector, VaultChestType.GILDED);
      int altarOmega = getChestCount(statCollector, VaultChestType.ALTAR, VaultRarity.OMEGA);
      int altarEpic = getChestCount(statCollector, VaultChestType.ALTAR, VaultRarity.EPIC);
      int altarRare = getChestCount(statCollector, VaultChestType.ALTAR, VaultRarity.RARE);
      int altarCommon = getChestCount(statCollector, VaultChestType.ALTAR, VaultRarity.COMMON);
      int altarTrapped = getTrappedChestCount(statCollector, VaultChestType.ALTAR);
      int livingOmega = getChestCount(statCollector, VaultChestType.LIVING, VaultRarity.OMEGA);
      int livingEpic = getChestCount(statCollector, VaultChestType.LIVING, VaultRarity.EPIC);
      int livingRare = getChestCount(statCollector, VaultChestType.LIVING, VaultRarity.RARE);
      int livingCommon = getChestCount(statCollector, VaultChestType.LIVING, VaultRarity.COMMON);
      int livingTrapped = getTrappedChestCount(statCollector, VaultChestType.LIVING);
      int ornateOmega = getChestCount(statCollector, VaultChestType.ORNATE, VaultRarity.OMEGA);
      int ornateEpic = getChestCount(statCollector, VaultChestType.ORNATE, VaultRarity.EPIC);
      int ornateRare = getChestCount(statCollector, VaultChestType.ORNATE, VaultRarity.RARE);
      int ornateCommon = getChestCount(statCollector, VaultChestType.ORNATE, VaultRarity.COMMON);
      int ornateTrapped = getTrappedChestCount(statCollector, VaultChestType.ORNATE);
      int treasureOmega = getChestCount(statCollector, VaultChestType.TREASURE, VaultRarity.OMEGA);
      int treasureEpic = getChestCount(statCollector, VaultChestType.TREASURE, VaultRarity.EPIC);
      int treasureRare = getChestCount(statCollector, VaultChestType.TREASURE, VaultRarity.RARE);
      int treasureCommon = getChestCount(statCollector, VaultChestType.TREASURE, VaultRarity.COMMON);
      int treasureTrapped = getTrappedChestCount(statCollector, VaultChestType.TREASURE);
      int totalOmega = woodenOmega + gildedOmega + altarOmega + livingOmega + ornateOmega + treasureOmega;
      int totalEpic = woodenEpic + gildedEpic + altarEpic + livingEpic + ornateEpic + treasureEpic;
      int totalRare = woodenRare + gildedRare + altarRare + livingRare + ornateRare + treasureRare;
      int totalCommon = woodenCommon + gildedCommon + altarCommon + livingCommon + ornateCommon + treasureCommon;
      int totalTrapped = woodenTrapped + gildedTrapped + altarTrapped + livingTrapped + ornateTrapped + treasureTrapped;
      return totalTrapped + totalCommon + totalRare + totalEpic + totalOmega;
   }

   public static float getTotalChestsXp(StatCollector statCollector) {
      int woodenOmega = getChestCount(statCollector, VaultChestType.WOODEN, VaultRarity.OMEGA);
      int woodenEpic = getChestCount(statCollector, VaultChestType.WOODEN, VaultRarity.EPIC);
      int woodenRare = getChestCount(statCollector, VaultChestType.WOODEN, VaultRarity.RARE);
      int woodenCommon = getChestCount(statCollector, VaultChestType.WOODEN, VaultRarity.COMMON);
      float woodenTotal = getXp(VaultChestType.WOODEN, woodenCommon, woodenRare, woodenEpic, woodenOmega);
      int gildedOmega = getChestCount(statCollector, VaultChestType.GILDED, VaultRarity.OMEGA);
      int gildedEpic = getChestCount(statCollector, VaultChestType.GILDED, VaultRarity.EPIC);
      int gildedRare = getChestCount(statCollector, VaultChestType.GILDED, VaultRarity.RARE);
      int gildedCommon = getChestCount(statCollector, VaultChestType.GILDED, VaultRarity.COMMON);
      float gildedTotal = getXp(VaultChestType.GILDED, gildedCommon, gildedRare, gildedEpic, gildedOmega);
      int altarOmega = getChestCount(statCollector, VaultChestType.ALTAR, VaultRarity.OMEGA);
      int altarEpic = getChestCount(statCollector, VaultChestType.ALTAR, VaultRarity.EPIC);
      int altarRare = getChestCount(statCollector, VaultChestType.ALTAR, VaultRarity.RARE);
      int altarCommon = getChestCount(statCollector, VaultChestType.ALTAR, VaultRarity.COMMON);
      float altarTotal = getXp(VaultChestType.ALTAR, altarCommon, altarRare, altarEpic, altarOmega);
      int livingOmega = getChestCount(statCollector, VaultChestType.LIVING, VaultRarity.OMEGA);
      int livingEpic = getChestCount(statCollector, VaultChestType.LIVING, VaultRarity.EPIC);
      int livingRare = getChestCount(statCollector, VaultChestType.LIVING, VaultRarity.RARE);
      int livingCommon = getChestCount(statCollector, VaultChestType.LIVING, VaultRarity.COMMON);
      float livingTotal = getXp(VaultChestType.LIVING, livingCommon, livingRare, livingEpic, livingOmega);
      int ornateOmega = getChestCount(statCollector, VaultChestType.ORNATE, VaultRarity.OMEGA);
      int ornateEpic = getChestCount(statCollector, VaultChestType.ORNATE, VaultRarity.EPIC);
      int ornateRare = getChestCount(statCollector, VaultChestType.ORNATE, VaultRarity.RARE);
      int ornateCommon = getChestCount(statCollector, VaultChestType.ORNATE, VaultRarity.COMMON);
      float ornateTotal = getXp(VaultChestType.ORNATE, ornateCommon, ornateRare, ornateEpic, ornateOmega);
      int treasureOmega = getChestCount(statCollector, VaultChestType.TREASURE, VaultRarity.OMEGA);
      int treasureEpic = getChestCount(statCollector, VaultChestType.TREASURE, VaultRarity.EPIC);
      int treasureRare = getChestCount(statCollector, VaultChestType.TREASURE, VaultRarity.RARE);
      int treasureCommon = getChestCount(statCollector, VaultChestType.TREASURE, VaultRarity.COMMON);
      float treasureTotal = getXp(VaultChestType.TREASURE, treasureCommon, treasureRare, treasureEpic, treasureOmega);
      return woodenTotal + gildedTotal + altarTotal + livingTotal + ornateTotal + treasureTotal;
   }

   public static float getXp(VaultChestType vaultChestType, int commonChests, int rareChests, int epicChests, int omegaChests) {
      Map<VaultChestType, Map<VaultRarity, Float>> map = ModConfigs.VAULT_STATS.getChests();
      float xpCommonMul = map.get(vaultChestType).get(VaultRarity.COMMON);
      float xpRareMul = map.get(vaultChestType).get(VaultRarity.RARE);
      float xpEpicMul = map.get(vaultChestType).get(VaultRarity.EPIC);
      float xpOmegaMul = map.get(vaultChestType).get(VaultRarity.OMEGA);
      float xpCommon = commonChests * xpCommonMul;
      float xpRare = rareChests * xpRareMul;
      float xpEpic = epicChests * xpEpicMul;
      float xpOmega = omegaChests * xpOmegaMul;
      return xpCommon + xpRare + xpEpic + xpOmega;
   }

   public static float getDamageDealt(StatCollector statCollector) {
      Object2FloatMap<ResourceLocation> group = statCollector.getDamageDealt();
      float damageDealt = 0.0F;
      ObjectIterator var3 = group.object2FloatEntrySet().iterator();

      while (var3.hasNext()) {
         it.unimi.dsi.fastutil.objects.Object2FloatMap.Entry<ResourceLocation> entry = (it.unimi.dsi.fastutil.objects.Object2FloatMap.Entry<ResourceLocation>)var3.next();
         float amount = entry.getFloatValue();
         damageDealt += amount;
      }

      return damageDealt;
   }

   public static float getDamageReceived(StatCollector statCollector) {
      Object2FloatMap<ResourceLocation> group = statCollector.getDamageReceived();
      float damageReceived = 0.0F;
      ObjectIterator var3 = group.object2FloatEntrySet().iterator();

      while (var3.hasNext()) {
         it.unimi.dsi.fastutil.objects.Object2FloatMap.Entry<ResourceLocation> entry = (it.unimi.dsi.fastutil.objects.Object2FloatMap.Entry<ResourceLocation>)var3.next();
         float amount = entry.getFloatValue();
         damageReceived += amount;
      }

      return damageReceived;
   }

   public Completion getCompletion() {
      return this.getStatsCollector().getCompletion();
   }

   public String getCompletionTranslationString() {
      return "screen.the_vault." + this.getCompletion().toString().toLowerCase();
   }

   @Nonnull
   public List<StatLabelListElement.Stat<?>> getOverviewLoot() {
      Vault vault = this.snapshot.getEnd();
      Vault startVault = this.snapshot.getStart();
      StatCollector statCollector = this.getStatsCollector();
      return List.of(
         StatLabelListElement.Stat.ofInteger(() -> "Coin Piles Collected", () -> "Coin Piles: " + getCoinPile(statCollector), () -> getCoinPile(statCollector)),
         StatLabelListElement.Stat.ofInteger(() -> "Ores Mined", () -> "Value: " + getMinedOresXp(statCollector) + "xp", () -> getOresMined(statCollector)),
         StatLabelListElement.Stat.ofInteger(
            () -> "Treasure Rooms Opened", () -> "Value: " + getTreasureRoomXp(statCollector) + "xp", statCollector::getTreasureRoomsOpened
         ),
         StatLabelListElement.Stat.ofInteger(
            () -> "Chests Looted", () -> "Value: " + getTotalChestsXp(statCollector) + "xp", () -> getTotalChests(statCollector)
         )
      );
   }

   @Nonnull
   public List<StatLabelListElement.Stat<?>> getOverviewCombat() {
      Vault vault = this.snapshot.getEnd();
      Vault startVault = this.snapshot.getStart();
      StatCollector statCollector = this.getStatsCollector();
      return List.of(
         StatLabelListElement.Stat.ofInteger(() -> "Mobs Unalived", () -> "Value: " + getMobsKilledXp(statCollector) + "xp", () -> getMobsKilled(statCollector)),
         StatLabelListElement.Stat.ofFloat(() -> "Damage Dealt", () -> "Dealt: " + getDamageDealt(statCollector), () -> getDamageDealt(statCollector)),
         StatLabelListElement.Stat.ofFloat(
            () -> "Damage Received", () -> "Received: " + getDamageReceived(statCollector), () -> getDamageReceived(statCollector)
         )
      );
   }

   @Nonnull
   public List<StatLabelListElement.Stat<?>> getOverviewGeneric() {
      Vault vault = this.snapshot.getEnd();
      Vault startVault = this.snapshot.getStart();
      float experienceMultiplier = this.getStatsCollector().getExpMultiplier();
      int endTime = vault.get(Vault.CLOCK).get(TickClock.LOGICAL_TIME);
      int startTime = startVault.get(Vault.CLOCK).get(TickClock.LOGICAL_TIME);
      return List.of(
         StatLabelListElement.Stat.ofLong(
            () -> "Vault Seed", () -> "Seed: " + (vault.get(Vault.SEED) & 281474976710655L), () -> vault.get(Vault.SEED) & 281474976710655L
         ),
         StatLabelListElement.Stat.ofInteger(() -> "Vault Level", () -> "Level: " + vault.get(Vault.LEVEL).get(), () -> vault.get(Vault.LEVEL).get()),
         StatLabelListElement.Stat.ofSeconds(
            () -> "Time Left",
            () -> UIHelper.formatTimeString(vault.get(Vault.CLOCK).get(TickClock.DISPLAY_TIME)),
            () -> vault.get(Vault.CLOCK).get(TickClock.DISPLAY_TIME) / 20
         ),
         StatLabelListElement.Stat.ofSeconds(() -> "Time Spent", () -> UIHelper.formatTimeString(endTime - startTime), () -> (endTime - startTime) / 20),
         StatLabelListElement.Stat.ofFloat(
            () -> "Experience Multiplier", () -> "Multiplier: " + String.format("%.1f", experienceMultiplier), () -> experienceMultiplier
         )
      );
   }

   @Nonnull
   public List<StatLabelListElement.Stat<?>> getQuickOverview() {
      Vault vault = this.snapshot.getEnd();
      Vault startVault = this.snapshot.getStart();
      StatCollector statCollector = this.getStatsCollector();
      int endTime = vault.get(Vault.CLOCK).get(TickClock.LOGICAL_TIME);
      int startTime = startVault.get(Vault.CLOCK).get(TickClock.LOGICAL_TIME);
      return List.of(
         StatLabelListElement.Stat.ofInteger(() -> "Mobs Unalived", () -> "Value: " + getMobsKilledXp(statCollector) + "xp", () -> getMobsKilled(statCollector)),
         StatLabelListElement.Stat.ofInteger(
            () -> "Chests Looted", () -> "Value: " + getTotalChestsXp(statCollector) + "xp", () -> getTotalChests(statCollector)
         ),
         StatLabelListElement.Stat.ofInteger(() -> "Ores Mined", () -> "Value: " + getMinedOresXp(statCollector) + "xp", () -> getOresMined(statCollector)),
         StatLabelListElement.Stat.ofSeconds(
            () -> "Time Left",
            () -> "Time Spent: " + UIHelper.formatTimeString(endTime - startTime),
            () -> vault.get(Vault.CLOCK).get(TickClock.DISPLAY_TIME) / 20
         )
      );
   }
}
