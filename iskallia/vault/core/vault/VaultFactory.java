package iskallia.vault.core.vault;

import iskallia.vault.core.Version;
import iskallia.vault.core.random.JavaRandom;
import iskallia.vault.core.vault.enhancement.EnhancementTaskManager;
import iskallia.vault.core.vault.objective.Objectives;
import iskallia.vault.core.vault.overlay.VaultOverlay;
import iskallia.vault.core.vault.player.ClassicListenersLogic;
import iskallia.vault.core.vault.player.Listeners;
import iskallia.vault.core.vault.stat.StatsCollector;
import iskallia.vault.core.vault.time.TickTimer;
import iskallia.vault.core.world.generator.GridGenerator;
import iskallia.vault.item.crystal.CrystalData;
import java.util.Random;
import java.util.UUID;
import net.minecraft.core.Direction;

public class VaultFactory {
   public static Vault create(Version version, CrystalData crystal) {
      int level = Math.max(crystal.getLevel(), 0);
      long seed = new Random().nextLong();
      JavaRandom random = JavaRandom.ofScrambled(seed);
      Vault vault = new Vault();
      vault.set(Vault.ID, UUID.randomUUID())
         .set(Vault.VERSION, version)
         .set(Vault.SEED, Long.valueOf(seed))
         .set(Vault.LEVEL, new VaultLevel().set(VaultLevel.VALUE, Integer.valueOf(level)))
         .set(Vault.CLOCK, new TickTimer())
         .set(
            Vault.WORLD,
            new WorldManager()
               .set(WorldManager.FACING, Direction.from2DDataValue(random.nextInt(4)))
               .set(WorldManager.RANDOM_TICK_SPEED, Integer.valueOf(0))
               .set(
                  WorldManager.LOOT_LOGIC,
                  new ClassicLootLogic()
                     .setIf(ClassicLootLogic.ADD_CATALYST_FRAGMENTS, crystal::canGenerateCatalystFragments)
                     .setIf(ClassicLootLogic.ADD_RUNES, crystal::canGenerateRunes)
               )
               .set(WorldManager.PORTAL_LOGIC, new ClassicPortalLogic())
               .set(WorldManager.MOB_LOGIC, new ClassicMobLogic())
               .set(WorldManager.GENERATOR, new GridGenerator().set(GridGenerator.CELL_X, Integer.valueOf(47)).set(GridGenerator.CELL_Z, Integer.valueOf(47)))
               .set(WorldManager.RENDERER, new WorldRenderer())
         )
         .set(Vault.OVERLAY, new VaultOverlay())
         .set(Vault.OBJECTIVES, new Objectives())
         .set(Vault.MODIFIERS, new Modifiers())
         .set(Vault.LISTENERS, new Listeners().set(Listeners.LOGIC, new ClassicListenersLogic()))
         .set(Vault.STATS, new StatsCollector())
         .set(Vault.DISCOVERY, new DiscoveryGoalsManager())
         .set(Vault.ENHANCEMENT_TASKS, new EnhancementTaskManager())
         .set(Vault.QUESTS, new QuestManager());
      crystal.configure(vault, random);
      return vault;
   }
}
