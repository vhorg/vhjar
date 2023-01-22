package iskallia.vault.core.vault;

import iskallia.vault.core.data.key.LootPoolKey;
import iskallia.vault.core.data.key.LootTableKey;
import iskallia.vault.core.data.key.PaletteKey;
import iskallia.vault.core.data.key.TemplateKey;
import iskallia.vault.core.data.key.TemplatePoolKey;
import iskallia.vault.core.data.key.ThemeKey;
import iskallia.vault.core.data.key.registry.KeyRegistry;
import iskallia.vault.core.data.key.registry.SupplierRegistry;
import iskallia.vault.core.vault.objective.AwardCrateObjective;
import iskallia.vault.core.vault.objective.BailObjective;
import iskallia.vault.core.vault.objective.CakeObjective;
import iskallia.vault.core.vault.objective.DeathObjective;
import iskallia.vault.core.vault.objective.FindExitObjective;
import iskallia.vault.core.vault.objective.KillBossObjective;
import iskallia.vault.core.vault.objective.MonolithObjective;
import iskallia.vault.core.vault.objective.ObeliskObjective;
import iskallia.vault.core.vault.objective.Objective;
import iskallia.vault.core.vault.objective.ScavengerObjective;
import iskallia.vault.core.vault.objective.TrackSpeedrunObjective;
import iskallia.vault.core.vault.objective.VictoryObjective;
import iskallia.vault.core.vault.player.ClassicListenersLogic;
import iskallia.vault.core.vault.player.Listener;
import iskallia.vault.core.vault.player.ListenersLogic;
import iskallia.vault.core.vault.player.Runner;
import iskallia.vault.core.vault.time.TickClock;
import iskallia.vault.core.vault.time.TickTimer;
import iskallia.vault.core.vault.time.modifier.ClockModifier;
import iskallia.vault.core.vault.time.modifier.FruitExtension;
import iskallia.vault.core.vault.time.modifier.ModifierExtension;
import iskallia.vault.core.vault.time.modifier.PylonExtension;
import iskallia.vault.core.vault.time.modifier.RelicExtension;
import iskallia.vault.core.vault.time.modifier.TimeAltarExtension;
import iskallia.vault.core.vault.time.modifier.TrinketExtension;
import iskallia.vault.core.vault.time.modifier.VoidFluidExtension;
import iskallia.vault.core.world.generator.GridGenerator;
import iskallia.vault.core.world.generator.VaultGenerator;
import iskallia.vault.core.world.generator.layout.ClassicCircleLayout;
import iskallia.vault.core.world.generator.layout.ClassicInfiniteLayout;
import iskallia.vault.core.world.generator.layout.ClassicPolygonLayout;
import iskallia.vault.core.world.generator.layout.ClassicSpiralLayout;
import iskallia.vault.core.world.generator.layout.DIYVaultLayout;
import iskallia.vault.core.world.generator.layout.GridLayout;
import iskallia.vault.core.world.generator.theme.Theme;
import iskallia.vault.core.world.loot.LootPool;
import iskallia.vault.core.world.loot.LootTable;
import iskallia.vault.core.world.processor.Palette;
import iskallia.vault.core.world.template.Template;
import iskallia.vault.core.world.template.data.TemplatePool;

public class VaultRegistry {
   public static KeyRegistry<TemplateKey, Template> TEMPLATE = new KeyRegistry<>();
   public static KeyRegistry<PaletteKey, Palette> PALETTE = new KeyRegistry<>();
   public static KeyRegistry<TemplatePoolKey, TemplatePool> TEMPLATE_POOL = new KeyRegistry<>();
   public static KeyRegistry<ThemeKey, Theme> THEME = new KeyRegistry<>();
   public static KeyRegistry<LootPoolKey, LootPool> LOOT_POOL = new KeyRegistry<>();
   public static KeyRegistry<LootTableKey, LootTable> LOOT_TABLE = new KeyRegistry<>();
   public static final SupplierRegistry<TickClock> CLOCK = new SupplierRegistry<TickClock>().add(TickTimer.KEY);
   public static final SupplierRegistry<ClockModifier> CLOCK_MODIFIER = new SupplierRegistry<ClockModifier>()
      .add(FruitExtension.KEY)
      .add(ModifierExtension.KEY)
      .add(TrinketExtension.KEY)
      .add(RelicExtension.KEY)
      .add(VoidFluidExtension.KEY)
      .add(TimeAltarExtension.KEY)
      .add(PylonExtension.KEY);
   public static final SupplierRegistry<GridLayout> GRID_LAYOUT = new SupplierRegistry<GridLayout>()
      .add(ClassicInfiniteLayout.KEY)
      .add(ClassicSpiralLayout.KEY)
      .add(ClassicCircleLayout.KEY)
      .add(ClassicPolygonLayout.KEY)
      .add(DIYVaultLayout.KEY);
   public static final SupplierRegistry<VaultGenerator> GENERATOR = new SupplierRegistry<GridGenerator>().add(GridGenerator.KEY);
   public static final SupplierRegistry<Listener> LISTENER = new SupplierRegistry<Listener>().add(Runner.KEY);
   public static final SupplierRegistry<Objective> OBJECTIVE = new SupplierRegistry<Objective>()
      .add(BailObjective.KEY)
      .add(DeathObjective.KEY)
      .add(ObeliskObjective.KEY)
      .add(KillBossObjective.KEY)
      .add(VictoryObjective.KEY)
      .add(ScavengerObjective.KEY)
      .add(CakeObjective.KEY)
      .add(AwardCrateObjective.KEY)
      .add(FindExitObjective.KEY)
      .add(TrackSpeedrunObjective.KEY)
      .add(MonolithObjective.KEY);
   public static final SupplierRegistry<ListenersLogic> LISTENERS_LOGIC = new SupplierRegistry<ListenersLogic>().add(ClassicListenersLogic.KEY);
   public static final SupplierRegistry<LootLogic> CHEST_LOGIC = new SupplierRegistry<LootLogic>().add(ClassicLootLogic.KEY);
   public static final SupplierRegistry<PortalLogic> PORTAL_LOGIC = new SupplierRegistry<PortalLogic>().add(ClassicPortalLogic.KEY);
   public static final SupplierRegistry<MobLogic> MOB_LOGIC = new SupplierRegistry<MobLogic>().add(ClassicMobLogic.KEY);
}
