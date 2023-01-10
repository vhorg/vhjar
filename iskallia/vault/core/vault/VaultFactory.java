package iskallia.vault.core.vault;

import iskallia.vault.VaultMod;
import iskallia.vault.core.Version;
import iskallia.vault.core.random.JavaRandom;
import iskallia.vault.core.vault.objective.Objectives;
import iskallia.vault.core.vault.objective.VictoryObjective;
import iskallia.vault.core.vault.overlay.VaultOverlay;
import iskallia.vault.core.vault.player.ClassicListenersLogic;
import iskallia.vault.core.vault.player.Listeners;
import iskallia.vault.core.vault.stat.StatsCollector;
import iskallia.vault.core.vault.time.TickTimer;
import iskallia.vault.core.world.generator.GridGenerator;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.item.crystal.CrystalData;
import iskallia.vault.item.crystal.layout.CrystalLayout;
import iskallia.vault.item.crystal.objective.CrystalObjective;
import iskallia.vault.world.vault.modifier.VaultModifierStack;
import iskallia.vault.world.vault.modifier.modifier.PlayerInventoryRestoreModifier;
import iskallia.vault.world.vault.modifier.registry.VaultModifierRegistry;
import iskallia.vault.world.vault.modifier.spi.VaultModifier;
import java.util.Random;
import java.util.UUID;
import net.minecraft.core.Direction;

public class VaultFactory {
   public static Vault create(Version version, CrystalData crystal, boolean casual) {
      int level = Math.max(crystal.getLevel(), 0);
      long seed = new Random().nextLong();
      JavaRandom random = JavaRandom.ofScrambled(seed);
      Vault vault = new Vault();
      vault.set(Vault.ID, UUID.randomUUID())
         .set(Vault.VERSION, version)
         .set(Vault.SEED, Long.valueOf(seed))
         .set(Vault.LEVEL, new VaultLevel().set(VaultLevel.VALUE, Integer.valueOf(level)))
         .set(Vault.CLOCK, new TickTimer().set(TickTimer.DISPLAY_TIME, Integer.valueOf(30000)))
         .set(
            Vault.WORLD,
            new WorldManager()
               .set(WorldManager.FACING, Direction.from2DDataValue(random.nextInt(4)))
               .set(WorldManager.RANDOM_TICK_SPEED, Integer.valueOf(0))
               .set(WorldManager.LOOT_LOGIC, new ClassicLootLogic().setIf(ClassicLootLogic.ADD_CATALYST_FRAGMENTS, () -> !crystal.preventsRandomModifiers()))
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
         .set(Vault.CRYSTAL, crystal.serializeNBT());
      CrystalLayout layout = crystal.getLayout() == null ? ModConfigs.VAULT_CRYSTAL.getRandomLayout(level, random).orElse(null) : crystal.getLayout();
      if (layout != null) {
         layout.configure(vault);
      }

      if (crystal.getTheme() != null) {
         crystal.getTheme().configure(vault, random);
      }

      vault.ifPresent(Vault.OBJECTIVES, objectives -> {
         if (crystal.getObjective() != null) {
            objectives.set(Objectives.KEY, CrystalObjective.getId(crystal.getObjective()));
            crystal.getObjective().configure(vault, random);
         } else {
            ModConfigs.VAULT_CRYSTAL.getRandomObjective(level, random).ifPresentOrElse(objective -> {
               objectives.set(Objectives.KEY, CrystalObjective.getId(objective));
               objective.configure(vault, random);
            }, () -> objectives.add(VictoryObjective.of(300)));
         }
      });

      for (VaultModifierStack stack : crystal.getModifiers()) {
         vault.ifPresent(Vault.MODIFIERS, m -> m.addPermanentModifier(stack.getModifier(), stack.getSize(), true));
      }

      if (casual) {
         vault.ifPresent(Vault.MODIFIERS, m -> m.addPermanentModifier(VaultModifierRegistry.getOrDefault(VaultMod.id("phoenix"), null), 1, false));
      }

      if (!crystal.preventsRandomModifiers()) {
         for (VaultModifier<?> modifier : ModConfigs.VAULT_MODIFIER_POOLS.getRandom(VaultMod.id("default"), vault.get(Vault.LEVEL).get(), random)) {
            if (!casual || !(modifier instanceof PlayerInventoryRestoreModifier)) {
               vault.ifPresent(Vault.MODIFIERS, m -> m.addPermanentModifier(modifier, 1, true));
            }
         }
      }

      return vault;
   }
}
