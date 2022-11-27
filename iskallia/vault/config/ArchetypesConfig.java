package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.archetype.config.BarbarianConfig;
import iskallia.vault.skill.archetype.config.BerserkerConfig;
import iskallia.vault.skill.archetype.config.CommanderConfig;
import iskallia.vault.skill.archetype.config.DefaultConfig;
import iskallia.vault.skill.archetype.config.TreasureHunterConfig;
import iskallia.vault.skill.archetype.config.VampireConfig;
import iskallia.vault.skill.archetype.config.WardConfig;

public class ArchetypesConfig extends Config {
   private static final String NAME = "archetypes";
   @Expose
   public DefaultConfig DEFAULT;
   @Expose
   public BerserkerConfig BERSERKER;
   @Expose
   public CommanderConfig COMMANDER;
   @Expose
   public TreasureHunterConfig TREASURE_HUNTER;
   @Expose
   public WardConfig WARD;
   @Expose
   public BarbarianConfig BARBARIAN;
   @Expose
   public VampireConfig VAMPIRE;

   @Override
   public String getName() {
      return "archetypes";
   }

   @Override
   protected void reset() {
      this.DEFAULT = new DefaultConfig(1, 0);
      this.BERSERKER = new BerserkerConfig(2, 50, 0.2F, 5.0F);
      this.COMMANDER = new CommanderConfig(1, 50, 2.0F, -0.2F);
      this.TREASURE_HUNTER = new TreasureHunterConfig(3, 50, 0.5F, 0.5F, 1.5F);
      this.WARD = new WardConfig(2, 50, 0.25F, -0.25F);
      this.BARBARIAN = new BarbarianConfig(1, 50, 3, 0.025F, -0.005F);
      this.VAMPIRE = new VampireConfig(2, 50, 0.05F);
   }
}
