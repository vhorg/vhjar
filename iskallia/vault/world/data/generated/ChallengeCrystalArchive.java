package iskallia.vault.world.data.generated;

import iskallia.vault.init.ModItems;
import iskallia.vault.item.crystal.CrystalData;
import iskallia.vault.util.MiscUtils;
import iskallia.vault.world.vault.VaultRaid;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.item.ItemStack;

public class ChallengeCrystalArchive {
   private static final List<ItemStack> generatedCrystals = new ArrayList<>();

   public static ItemStack getRandom() {
      return getRandom(new Random());
   }

   public static ItemStack getRandom(Random rand) {
      if (generatedCrystals.isEmpty()) {
         initialize();
      }

      return MiscUtils.getRandomEntry(generatedCrystals, rand);
   }

   private static void initialize() {
      CrystalData escape = baseData();
      escape.setType(CrystalData.Type.CLASSIC);
      escape.setSelectedObjective(VaultRaid.SUMMON_AND_KILL_BOSS.get().getId());
      escape.setTargetObjectiveCount(2);
      escape.addModifier("Frenzy");
      escape.addModifier("Super Healing");
      escape.addModifier("Super Healing");
      escape.addModifier("Impossible");
      escape.addModifier("Impossible");
      escape.addModifier("Phoenix");
      escape.addModifier("Giant");
      generatedCrystals.add(make(escape));
      CrystalData minesweeper = baseData();
      minesweeper.setType(CrystalData.Type.CLASSIC);
      minesweeper.setSelectedObjective(VaultRaid.SUMMON_AND_KILL_BOSS.get().getId());
      minesweeper.setTargetObjectiveCount(5);
      minesweeper.addModifier("Treasure");
      minesweeper.addModifier("Treasure");
      minesweeper.addModifier("Treasure");
      minesweeper.addModifier("Treasure");
      minesweeper.addModifier("Super Lucky");
      minesweeper.addModifier("Super Lucky");
      minesweeper.addModifier("Super Lucky");
      minesweeper.addModifier("Trapped");
      minesweeper.addModifier("Trapped");
      minesweeper.addModifier("Trapped");
      minesweeper.addModifier("Trapped");
      minesweeper.addModifier("Trapped");
      minesweeper.addModifier("Locked");
      generatedCrystals.add(make(minesweeper));
      CrystalData theHunt = baseData();
      theHunt.setType(CrystalData.Type.CLASSIC);
      theHunt.setSelectedObjective(VaultRaid.SCAVENGER_HUNT.get().getId());
      theHunt.setTargetObjectiveCount(8);
      theHunt.addModifier("Odyssey");
      theHunt.addModifier("Unlucky");
      theHunt.addModifier("Unlucky");
      theHunt.addModifier("Challenging");
      theHunt.addModifier("Challenging");
      theHunt.addModifier("Chaotic");
      theHunt.addModifier("Optimistic");
      generatedCrystals.add(make(theHunt));
      CrystalData buildAEscape = baseData();
      buildAEscape.setType(CrystalData.Type.COOP);
      buildAEscape.setSelectedObjective(VaultRaid.ARCHITECT_EVENT.get().getId());
      buildAEscape.setTargetObjectiveCount(28);
      buildAEscape.addModifier("Locked");
      buildAEscape.addModifier("Phoenix");
      buildAEscape.addModifier("Unlucky");
      buildAEscape.addModifier("Unlucky");
      buildAEscape.addModifier("Unlucky");
      buildAEscape.addModifier("Impossible");
      buildAEscape.addModifier("Impossible");
      buildAEscape.addModifier("Challenging");
      buildAEscape.addModifier("Optimistic");
      generatedCrystals.add(make(buildAEscape));
      CrystalData slowRun = baseData();
      slowRun.setType(CrystalData.Type.CLASSIC);
      slowRun.setSelectedObjective(VaultRaid.SUMMON_AND_KILL_BOSS.get().getId());
      slowRun.setTargetObjectiveCount(13);
      slowRun.addModifier("Mega Slowed");
      slowRun.addModifier("Mega Slowed");
      slowRun.addModifier("Mega Weakened");
      slowRun.addModifier("Mega Tired");
      slowRun.addModifier("Daycare");
      slowRun.addModifier("Hard");
      slowRun.addModifier("Crowded");
      slowRun.addModifier("Exploration");
      generatedCrystals.add(make(slowRun));
      CrystalData speedRun = baseData();
      speedRun.setType(CrystalData.Type.CLASSIC);
      speedRun.setSelectedObjective(VaultRaid.SUMMON_AND_KILL_BOSS.get().getId());
      speedRun.setTargetObjectiveCount(4);
      speedRun.addModifier("Rush");
      speedRun.addModifier("Rush");
      speedRun.addModifier("Phoenix");
      speedRun.addModifier("Odyssey");
      generatedCrystals.add(make(speedRun));
      CrystalData oclaf = baseData();
      oclaf.setType(CrystalData.Type.CLASSIC);
      oclaf.setSelectedObjective(VaultRaid.SUMMON_AND_KILL_BOSS.get().getId());
      oclaf.setTargetObjectiveCount(6);
      oclaf.addModifier("Frenzy");
      oclaf.addModifier("Impossible");
      oclaf.addModifier("Locked");
      generatedCrystals.add(make(oclaf));
      CrystalData fruity = baseData();
      fruity.setType(CrystalData.Type.CLASSIC);
      fruity.setSelectedObjective(VaultRaid.SUMMON_AND_KILL_BOSS.get().getId());
      fruity.setTargetObjectiveCount(4);
      fruity.addModifier("Rush");
      fruity.addModifier("Rush");
      fruity.addModifier("Fast");
      fruity.addModifier("Odyssey");
      fruity.addModifier("Odyssey");
      generatedCrystals.add(make(fruity));
      CrystalData mighty = baseData();
      mighty.setType(CrystalData.Type.CLASSIC);
      mighty.setSelectedObjective(VaultRaid.SCAVENGER_HUNT.get().getId());
      mighty.setTargetObjectiveCount(12);
      mighty.addModifier("Crowded");
      mighty.addModifier("Challenging");
      mighty.addModifier("Wither");
      mighty.addModifier("Poison");
      mighty.addModifier("Slow");
      mighty.addModifier("Chilling");
      generatedCrystals.add(make(mighty));
      CrystalData spawnHunter = baseData();
      spawnHunter.setType(CrystalData.Type.CLASSIC);
      spawnHunter.setSelectedObjective(VaultRaid.SCAVENGER_HUNT.get().getId());
      spawnHunter.setTargetObjectiveCount(5);
      spawnHunter.addModifier("Silent");
      spawnHunter.addModifier("Frenzy");
      spawnHunter.addModifier("Speedy");
      generatedCrystals.add(make(spawnHunter));
      CrystalData mobGrinder = baseData();
      mobGrinder.setType(CrystalData.Type.CLASSIC);
      mobGrinder.setSelectedObjective(VaultRaid.SUMMON_AND_KILL_BOSS.get().getId());
      mobGrinder.setTargetObjectiveCount(3);
      mobGrinder.addModifier("Impossible");
      mobGrinder.addModifier("Impossible");
      mobGrinder.addModifier("Impossible");
      mobGrinder.addModifier("Raging");
      mobGrinder.addModifier("Furious");
      mobGrinder.addModifier("Mega Slowed");
      mobGrinder.addModifier("Mega Weakened");
      generatedCrystals.add(make(mobGrinder));
      CrystalData panic = baseData();
      panic.setType(CrystalData.Type.CLASSIC);
      panic.setSelectedObjective(VaultRaid.SCAVENGER_HUNT.get().getId());
      panic.setTargetObjectiveCount(8);
      panic.addModifier("Silent");
      panic.addModifier("Faster");
      panic.addModifier("Locked");
      generatedCrystals.add(make(panic));
      CrystalData greed = baseData();
      greed.setType(CrystalData.Type.CLASSIC);
      greed.setSelectedObjective(VaultRaid.SUMMON_AND_KILL_BOSS.get().getId());
      greed.setTargetObjectiveCount(10);
      greed.addModifier("Trapped");
      greed.addModifier("Trapped");
      greed.addModifier("Hoard");
      greed.addModifier("Gilded");
      greed.addModifier("Hoard");
      greed.addModifier("Gilded");
      greed.addModifier("Locked");
      generatedCrystals.add(make(greed));
      CrystalData gamble = baseData();
      gamble.setType(CrystalData.Type.CLASSIC);
      gamble.setSelectedObjective(VaultRaid.SUMMON_AND_KILL_BOSS.get().getId());
      gamble.setTargetObjectiveCount(2);
      gamble.addModifier("Rush");
      gamble.addModifier("Rush");
      gamble.addModifier("Fast");
      gamble.addModifier("Odyssey");
      generatedCrystals.add(make(gamble));
      CrystalData builder = baseData();
      builder.setType(CrystalData.Type.COOP);
      builder.setSelectedObjective(VaultRaid.ARCHITECT_EVENT.get().getId());
      builder.setTargetObjectiveCount(35);
      builder.addModifier("Locked");
      generatedCrystals.add(make(builder));
      CrystalData dream = baseData();
      dream.setType(CrystalData.Type.CLASSIC);
      dream.setSelectedObjective(VaultRaid.SUMMON_AND_KILL_BOSS.get().getId());
      dream.setTargetObjectiveCount(5);
      dream.addModifier("Rush");
      dream.addModifier("Rush");
      dream.addModifier("Safe Zone");
      dream.addModifier("Treasure");
      dream.addModifier("Treasure");
      dream.addModifier("Treasure");
      dream.addModifier("Locked");
      generatedCrystals.add(make(dream));
      CrystalData hope = baseData();
      hope.setType(CrystalData.Type.CLASSIC);
      hope.setSelectedObjective(VaultRaid.SUMMON_AND_KILL_BOSS.get().getId());
      hope.setTargetObjectiveCount(5);
      hope.addModifier("Copious");
      hope.addModifier("Rich");
      hope.addModifier("Plentiful");
      hope.addModifier("Rush");
      hope.addModifier("Faster");
      hope.addModifier("Locked");
      generatedCrystals.add(make(hope));
   }

   private static ItemStack make(CrystalData data) {
      ItemStack crystal = new ItemStack(ModItems.VAULT_CRYSTAL);
      crystal.func_196082_o().func_218657_a("CrystalData", data.serializeNBT());
      return crystal;
   }

   private static CrystalData baseData() {
      CrystalData data = new CrystalData();
      data.setModifiable(false);
      data.setCanTriggerInfluences(false);
      data.setCanGenerateTreasureRooms(false);
      data.setPreventsRandomModifiers(true);
      return data;
   }
}
