package iskallia.vault.skill.ability.group;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.ability.AbilityGroup;
import iskallia.vault.skill.ability.config.SummonEternalConfig;
import iskallia.vault.skill.ability.config.sub.SummonEternalCountConfig;
import iskallia.vault.skill.ability.config.sub.SummonEternalDamageConfig;
import iskallia.vault.skill.ability.config.sub.SummonEternalDebuffConfig;
import iskallia.vault.skill.ability.effect.SummonEternalAbility;
import java.util.ArrayList;
import java.util.List;

public class SummonEternalAbilityGroup extends AbilityGroup<SummonEternalConfig, SummonEternalAbility<SummonEternalConfig>> {
   @Expose
   private final List<SummonEternalCountConfig> countLevelConfiguration = new ArrayList<>();
   @Expose
   private final List<SummonEternalDamageConfig> damageLevelConfiguration = new ArrayList<>();
   @Expose
   private final List<SummonEternalDebuffConfig> debuffLevelConfiguration = new ArrayList<>();

   private SummonEternalAbilityGroup() {
      super("Summon Eternal");
   }

   protected SummonEternalConfig getSubConfig(String specialization, int level) {
      switch (specialization) {
         case "Summon Eternal_Additional":
            return this.countLevelConfiguration.get(level);
         case "Summon Eternal_Damage":
            return this.damageLevelConfiguration.get(level);
         case "Summon Eternal_Debuffs":
            return this.debuffLevelConfiguration.get(level);
         default:
            return null;
      }
   }

   @Override
   public String getSpecializationName(String specialization) {
      switch (specialization) {
         case "Summon Eternal_Additional":
            return "Army";
         case "Summon Eternal_Damage":
            return "Overpower";
         case "Summon Eternal_Debuffs":
            return "Ghoul";
         default:
            return "Summon Eternal";
      }
   }

   public static SummonEternalAbilityGroup defaultConfig() {
      SummonEternalAbilityGroup group = new SummonEternalAbilityGroup();
      group.addLevel(new SummonEternalConfig(1, 12000, 1, 3, 12000, 0.2F, true));
      group.addLevel(new SummonEternalConfig(1, 10800, 1, 3, 10800, 0.2F, true));
      group.addLevel(new SummonEternalConfig(1, 9600, 1, 3, 9600, 0.2F, false));
      group.addLevel(new SummonEternalConfig(1, 8400, 1, 3, 8400, 0.2F, false));
      group.addLevel(new SummonEternalConfig(1, 7200, 1, 3, 7200, 0.2F, false));
      group.countLevelConfiguration.add(new SummonEternalCountConfig(1, 12000, 1, 3, 12000, true, 0.2F, 1));
      group.countLevelConfiguration.add(new SummonEternalCountConfig(1, 10800, 1, 3, 10800, true, 0.2F, 1));
      group.countLevelConfiguration.add(new SummonEternalCountConfig(1, 9600, 1, 3, 9600, false, 0.2F, 1));
      group.countLevelConfiguration.add(new SummonEternalCountConfig(1, 8400, 1, 3, 8400, false, 0.2F, 1));
      group.countLevelConfiguration.add(new SummonEternalCountConfig(1, 7200, 1, 3, 7200, false, 0.2F, 1));
      group.debuffLevelConfiguration.add(new SummonEternalDebuffConfig(1, 12000, 1, 3, 12000, true, 0.2F, 0.1F, 20, 0));
      group.debuffLevelConfiguration.add(new SummonEternalDebuffConfig(1, 10800, 1, 3, 10800, true, 0.2F, 0.2F, 30, 0));
      group.debuffLevelConfiguration.add(new SummonEternalDebuffConfig(1, 9600, 1, 3, 9600, false, 0.2F, 0.3F, 40, 1));
      group.debuffLevelConfiguration.add(new SummonEternalDebuffConfig(1, 8400, 1, 3, 8400, false, 0.2F, 0.4F, 60, 1));
      group.debuffLevelConfiguration.add(new SummonEternalDebuffConfig(1, 7200, 1, 3, 7200, false, 0.2F, 0.5F, 80, 1));
      group.damageLevelConfiguration.add(new SummonEternalDamageConfig(1, 12000, 1, 3, 12000, true, 0.2F, 0.1F));
      group.damageLevelConfiguration.add(new SummonEternalDamageConfig(1, 10800, 1, 3, 10800, true, 0.2F, 0.2F));
      group.damageLevelConfiguration.add(new SummonEternalDamageConfig(1, 9600, 1, 3, 9600, false, 0.2F, 0.25F));
      group.damageLevelConfiguration.add(new SummonEternalDamageConfig(1, 8400, 1, 3, 8400, false, 0.2F, 0.3F));
      group.damageLevelConfiguration.add(new SummonEternalDamageConfig(1, 7200, 1, 3, 7200, false, 0.2F, 0.35F));
      return group;
   }
}
