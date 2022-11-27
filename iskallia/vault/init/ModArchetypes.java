package iskallia.vault.init;

import iskallia.vault.VaultMod;
import iskallia.vault.skill.archetype.AbstractArchetype;
import iskallia.vault.skill.archetype.ArchetypeRegistry;
import iskallia.vault.skill.archetype.archetype.BarbarianArchetype;
import iskallia.vault.skill.archetype.archetype.BerserkerArchetype;
import iskallia.vault.skill.archetype.archetype.CommanderArchetype;
import iskallia.vault.skill.archetype.archetype.DefaultArchetype;
import iskallia.vault.skill.archetype.archetype.TreasureHunterArchetype;
import iskallia.vault.skill.archetype.archetype.VampireArchetype;
import iskallia.vault.skill.archetype.archetype.WardArchetype;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.registries.IForgeRegistry;

public class ModArchetypes {
   public static final DefaultArchetype DEFAULT = new DefaultArchetype(VaultMod.id("default"));
   public static final BerserkerArchetype BERSERKER = new BerserkerArchetype(VaultMod.id("berserker"));
   public static final CommanderArchetype COMMANDER = new CommanderArchetype(VaultMod.id("commander"));
   public static final TreasureHunterArchetype TREASURE_HUNTER = new TreasureHunterArchetype(VaultMod.id("treasure_hunter"));
   public static final WardArchetype WARD = new WardArchetype(VaultMod.id("ward"));
   public static final BarbarianArchetype BARBARIAN = new BarbarianArchetype(VaultMod.id("barbarian"));
   public static final VampireArchetype VAMPIRE = new VampireArchetype(VaultMod.id("vampire"));

   public static void init(Register<AbstractArchetype<?>> event) {
      IForgeRegistry<AbstractArchetype<?>> registry = event.getRegistry();
      registry.register(DEFAULT);
      registry.register(BERSERKER);
      registry.register(COMMANDER);
      registry.register(TREASURE_HUNTER);
      registry.register(WARD);
      registry.register(BARBARIAN);
      registry.register(VAMPIRE);
      ArchetypeRegistry.registerDefaultArchetype(DEFAULT);
   }
}
