package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.skill.talent.TalentGroup;
import iskallia.vault.skill.talent.type.AngelTalent;
import iskallia.vault.skill.talent.type.AttributeTalent;
import iskallia.vault.skill.talent.type.BarteringTalent;
import iskallia.vault.skill.talent.type.BlacksmithTalent;
import iskallia.vault.skill.talent.type.ConditionalDamageTalent;
import iskallia.vault.skill.talent.type.EffectTalent;
import iskallia.vault.skill.talent.type.ExperiencedTalent;
import iskallia.vault.skill.talent.type.LowHealthDamageTalent;
import iskallia.vault.skill.talent.type.LowHealthResistanceTalent;
import iskallia.vault.skill.talent.type.LowManaDamageTalent;
import iskallia.vault.skill.talent.type.LowManaHealingEfficiencyTalent;
import iskallia.vault.skill.talent.type.LuckyAltarTalent;
import iskallia.vault.skill.talent.type.VanillaAttributeTalent;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraftforge.common.ForgeMod;

public class TalentsConfig extends Config {
   @Expose
   public TalentGroup<EffectTalent> HASTE;
   @Expose
   public TalentGroup<EffectTalent> STRENGTH;
   @Expose
   public TalentGroup<EffectTalent> SPEED;
   @Expose
   public TalentGroup<VanillaAttributeTalent> REACH;
   @Expose
   public TalentGroup<VanillaAttributeTalent> STONE_SKIN;
   @Expose
   public TalentGroup<AttributeTalent> UNBREAKABLE;
   @Expose
   public TalentGroup<AttributeTalent> CRITICAL_STRIKE;
   @Expose
   public TalentGroup<AttributeTalent> FATAL_STRIKE_CHANCE;
   @Expose
   public TalentGroup<AttributeTalent> FATAL_STRIKE_DAMAGE;
   @Expose
   public TalentGroup<AttributeTalent> THORNS_CHANCE;
   @Expose
   public TalentGroup<AttributeTalent> THORNS_DAMAGE;
   @Expose
   public TalentGroup<AttributeTalent> DAMAGE_UNDEAD;
   @Expose
   public TalentGroup<AttributeTalent> DAMAGE_SPIDERS;
   @Expose
   public TalentGroup<AttributeTalent> DAMAGE_ILLAGERS;
   @Expose
   public TalentGroup<AngelTalent> ANGEL;
   @Expose
   public TalentGroup<ExperiencedTalent> EXPERIENCED;
   @Expose
   public TalentGroup<LuckyAltarTalent> LUCKY_ALTAR;
   @Expose
   public TalentGroup<ConditionalDamageTalent> WEAKNESS_AFFINITY;
   @Expose
   public TalentGroup<ConditionalDamageTalent> WITHER_AFFINITY;
   @Expose
   public TalentGroup<ConditionalDamageTalent> SLOWNESS_AFFINITY;
   @Expose
   public TalentGroup<BarteringTalent> BARTERING;
   @Expose
   public TalentGroup<BlacksmithTalent> BLACKSMITH;
   @Expose
   public TalentGroup<LowHealthResistanceTalent> LAST_STAND;
   @Expose
   public TalentGroup<LowHealthDamageTalent> BERSERKING;
   @Expose
   public TalentGroup<LowManaHealingEfficiencyTalent> METHODICAL;
   @Expose
   public TalentGroup<LowManaDamageTalent> DEPLETED;

   @Override
   public String getName() {
      return "talents";
   }

   public List<TalentGroup<?>> getAll() {
      return Arrays.asList(
         this.HASTE,
         this.STRENGTH,
         this.SPEED,
         this.REACH,
         this.STONE_SKIN,
         this.UNBREAKABLE,
         this.CRITICAL_STRIKE,
         this.FATAL_STRIKE_CHANCE,
         this.FATAL_STRIKE_DAMAGE,
         this.THORNS_CHANCE,
         this.THORNS_DAMAGE,
         this.DAMAGE_SPIDERS,
         this.DAMAGE_UNDEAD,
         this.DAMAGE_ILLAGERS,
         this.ANGEL,
         this.EXPERIENCED,
         this.LUCKY_ALTAR,
         this.WEAKNESS_AFFINITY,
         this.WITHER_AFFINITY,
         this.SLOWNESS_AFFINITY,
         this.BARTERING,
         this.BLACKSMITH,
         this.LAST_STAND,
         this.BERSERKING,
         this.METHODICAL,
         this.DEPLETED
      );
   }

   public TalentGroup<?> getByName(String name) {
      return this.getAll()
         .stream()
         .filter(group -> group.getParentName().equals(name))
         .findFirst()
         .orElseThrow(() -> new IllegalStateException("Unknown talent with name " + name));
   }

   public Optional<TalentGroup<?>> getTalent(String name) {
      return this.getAll().stream().filter(group -> group.getParentName().equals(name)).findFirst();
   }

   @Override
   protected void reset() {
      this.HASTE = TalentGroup.ofEffect("Haste", MobEffects.DIG_SPEED, 3);
      this.STRENGTH = TalentGroup.ofEffect("Strength", MobEffects.DAMAGE_BOOST, 2);
      this.SPEED = TalentGroup.ofEffect("Speed", MobEffects.MOVEMENT_SPEED, 2);
      this.REACH = TalentGroup.ofAttribute("Reach", (Attribute)ForgeMod.REACH_DISTANCE.get(), Operation.ADDITION, 3, i -> i * 1.0);
      this.STONE_SKIN = TalentGroup.ofAttribute("Stone Skin", Attributes.KNOCKBACK_RESISTANCE, Operation.ADDITION, 5, i -> i * 0.2F);
      this.UNBREAKABLE = TalentGroup.ofGearAttribute("Unbreakable", ModGearAttributes.DURABILITY_WEAR_REDUCTION, 5, i -> i * 0.07F);
      this.CRITICAL_STRIKE = TalentGroup.ofGearAttribute("Critical Strike", ModGearAttributes.VANILLA_CRITICAL_HIT_CHANCE, 4, i -> i * 0.25F);
      this.THORNS_CHANCE = TalentGroup.ofGearAttribute("Thorns Chance", ModGearAttributes.THORNS_CHANCE, 5, i -> i * 0.05F);
      this.THORNS_DAMAGE = TalentGroup.ofGearAttribute("Thorns Damage", ModGearAttributes.THORNS_DAMAGE, 5, i -> i * 0.15F);
      this.FATAL_STRIKE_CHANCE = TalentGroup.ofGearAttribute("Fatal Strike Chance", ModGearAttributes.FATAL_STRIKE_CHANCE, 5, i -> i * 0.05F);
      this.FATAL_STRIKE_DAMAGE = TalentGroup.ofGearAttribute("Fatal Strike Damage", ModGearAttributes.FATAL_STRIKE_DAMAGE, 5, i -> i * 0.2F);
      this.DAMAGE_UNDEAD = TalentGroup.ofGearAttribute("Arthropod Mastery", ModGearAttributes.DAMAGE_UNDEAD, 5, i -> i * 0.1F);
      this.DAMAGE_SPIDERS = TalentGroup.ofGearAttribute("Undead Mastery", ModGearAttributes.DAMAGE_SPIDERS, 5, i -> i * 0.1F);
      this.DAMAGE_ILLAGERS = TalentGroup.ofGearAttribute("Illager Mastery", ModGearAttributes.DAMAGE_ILLAGERS, 5, i -> i * 0.1F);
      this.ANGEL = new TalentGroup<>("Angel", new AngelTalent(100));
      this.EXPERIENCED = new TalentGroup<>(
         "Experienced",
         new ExperiencedTalent(1, 0.5F),
         new ExperiencedTalent(1, 1.0F),
         new ExperiencedTalent(1, 1.5F),
         new ExperiencedTalent(1, 2.0F),
         new ExperiencedTalent(1, 3.0F),
         new ExperiencedTalent(2, 4.0F),
         new ExperiencedTalent(2, 5.0F),
         new ExperiencedTalent(3, 7.5F),
         new ExperiencedTalent(3, 10.0F),
         new ExperiencedTalent(8, 20.0F)
      );
      this.LUCKY_ALTAR = new TalentGroup<>(
         "Lucky Altar",
         new LuckyAltarTalent(3, 0.1F),
         new LuckyAltarTalent(3, 0.15F),
         new LuckyAltarTalent(3, 0.2F),
         new LuckyAltarTalent(6, 0.25F),
         new LuckyAltarTalent(8, 0.3F)
      );
      this.WEAKNESS_AFFINITY = new TalentGroup<>(
         "Weakness Affinity",
         new ConditionalDamageTalent(1, MobEffects.WEAKNESS, 0.1),
         new ConditionalDamageTalent(2, MobEffects.WEAKNESS, 0.2),
         new ConditionalDamageTalent(3, MobEffects.WEAKNESS, 0.3)
      );
      this.WITHER_AFFINITY = new TalentGroup<>(
         "Wither Affinity",
         new ConditionalDamageTalent(1, MobEffects.WITHER, 0.1),
         new ConditionalDamageTalent(2, MobEffects.WITHER, 0.2),
         new ConditionalDamageTalent(3, MobEffects.WITHER, 0.3)
      );
      this.SLOWNESS_AFFINITY = new TalentGroup<>(
         "Slowness Affinity",
         new ConditionalDamageTalent(1, MobEffects.MOVEMENT_SLOWDOWN, 0.1),
         new ConditionalDamageTalent(2, MobEffects.MOVEMENT_SLOWDOWN, 0.2),
         new ConditionalDamageTalent(3, MobEffects.MOVEMENT_SLOWDOWN, 0.3)
      );
      this.BARTERING = new TalentGroup<>("Bartering", new BarteringTalent(1, 0.1F), new BarteringTalent(2, 0.2F), new BarteringTalent(3, 0.3F));
      this.BLACKSMITH = new TalentGroup<>("Blacksmith", new BlacksmithTalent(1, 0.1F), new BlacksmithTalent(2, 0.2F), new BlacksmithTalent(3, 0.3F));
      this.LAST_STAND = new TalentGroup<>(
         "Last Stand",
         new LowHealthResistanceTalent(1, 0.2F, 0.05F),
         new LowHealthResistanceTalent(2, 0.2F, 0.1F),
         new LowHealthResistanceTalent(3, 0.2F, 0.15F)
      );
      this.BERSERKING = new TalentGroup<>(
         "Berserking", new LowHealthDamageTalent(1, 0.2F, 0.15F), new LowHealthDamageTalent(2, 0.2F, 0.25F), new LowHealthDamageTalent(3, 0.2F, 0.5F)
      );
      this.METHODICAL = new TalentGroup<>(
         "Methodical",
         new LowManaHealingEfficiencyTalent(1, 0.2F, 0.05F),
         new LowManaHealingEfficiencyTalent(2, 0.2F, 0.1F),
         new LowManaHealingEfficiencyTalent(3, 0.2F, 0.15F)
      );
      this.DEPLETED = new TalentGroup<>(
         "Depleted", new LowManaDamageTalent(1, 0.2F, 0.15F), new LowManaDamageTalent(2, 0.2F, 0.25F), new LowManaDamageTalent(3, 0.2F, 0.5F)
      );
   }
}
