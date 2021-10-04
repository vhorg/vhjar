package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.talent.ArchetypeTalentGroup;
import iskallia.vault.skill.talent.TalentGroup;
import iskallia.vault.skill.talent.type.AngelTalent;
import iskallia.vault.skill.talent.type.ArtisanTalent;
import iskallia.vault.skill.talent.type.AttributeTalent;
import iskallia.vault.skill.talent.type.BreakableTalent;
import iskallia.vault.skill.talent.type.CarelessTalent;
import iskallia.vault.skill.talent.type.CriticalStrikeTalent;
import iskallia.vault.skill.talent.type.EffectTalent;
import iskallia.vault.skill.talent.type.ElvishTalent;
import iskallia.vault.skill.talent.type.ExperiencedTalent;
import iskallia.vault.skill.talent.type.FatalStrikeChanceTalent;
import iskallia.vault.skill.talent.type.FatalStrikeDamageTalent;
import iskallia.vault.skill.talent.type.FatalStrikeTalent;
import iskallia.vault.skill.talent.type.LuckyAltarTalent;
import iskallia.vault.skill.talent.type.ParryTalent;
import iskallia.vault.skill.talent.type.ResistanceTalent;
import iskallia.vault.skill.talent.type.SoulShardTalent;
import iskallia.vault.skill.talent.type.StepTalent;
import iskallia.vault.skill.talent.type.ThornsChanceTalent;
import iskallia.vault.skill.talent.type.ThornsDamageTalent;
import iskallia.vault.skill.talent.type.ThornsTalent;
import iskallia.vault.skill.talent.type.TwerkerTalent;
import iskallia.vault.skill.talent.type.UnbreakableTalent;
import iskallia.vault.skill.talent.type.archetype.BarbaricTalent;
import iskallia.vault.skill.talent.type.archetype.CommanderTalent;
import iskallia.vault.skill.talent.type.archetype.FrenzyTalent;
import iskallia.vault.skill.talent.type.archetype.GlassCannonTalent;
import iskallia.vault.skill.talent.type.archetype.WardTalent;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.potion.Effects;
import net.minecraftforge.common.ForgeMod;

public class TalentsConfig extends Config {
   @Expose
   public TalentGroup<EffectTalent> HASTE;
   @Expose
   public TalentGroup<ResistanceTalent> RESISTANCE;
   @Expose
   public TalentGroup<EffectTalent> STRENGTH;
   @Expose
   public TalentGroup<EffectTalent> FIRE_RESISTANCE;
   @Expose
   public TalentGroup<EffectTalent> SPEED;
   @Expose
   public TalentGroup<EffectTalent> WATER_BREATHING;
   @Expose
   public TalentGroup<AttributeTalent> WELL_FIT;
   @Expose
   public TalentGroup<AttributeTalent> REACH;
   @Expose
   public TalentGroup<TwerkerTalent> TWERKER;
   @Expose
   public TalentGroup<ElvishTalent> ELVISH;
   @Expose
   public TalentGroup<CarelessTalent> CARELESS;
   @Expose
   public TalentGroup<AngelTalent> ANGEL;
   @Expose
   public TalentGroup<ExperiencedTalent> EXPERIENCED;
   @Expose
   public TalentGroup<ParryTalent> PARRY;
   @Expose
   public TalentGroup<AttributeTalent> STONE_SKIN;
   @Expose
   public TalentGroup<UnbreakableTalent> UNBREAKABLE;
   @Expose
   public TalentGroup<CriticalStrikeTalent> CRITICAL_STRIKE;
   @Expose
   public TalentGroup<AttributeTalent> CHUNKY;
   @Expose
   public ArchetypeTalentGroup<FrenzyTalent> FRENZY;
   @Expose
   public TalentGroup<StepTalent> STEP;
   @Expose
   public TalentGroup<ArtisanTalent> ARTISAN;
   @Expose
   public TalentGroup<EffectTalent> LOOTER;
   @Expose
   public TalentGroup<EffectTalent> TREASURE_HUNTER;
   @Expose
   public TalentGroup<BreakableTalent> BREAKABLE;
   @Expose
   public TalentGroup<LuckyAltarTalent> LUCKY_ALTAR;
   @Expose
   public TalentGroup<FatalStrikeTalent> FATAL_STRIKE;
   @Expose
   public TalentGroup<FatalStrikeChanceTalent> FATAL_STRIKE_CHANCE;
   @Expose
   public TalentGroup<FatalStrikeDamageTalent> FATAL_STRIKE_DAMAGE;
   @Expose
   public TalentGroup<ThornsTalent> THORNS;
   @Expose
   public TalentGroup<ThornsChanceTalent> THORNS_CHANCE;
   @Expose
   public TalentGroup<ThornsDamageTalent> THORNS_DAMAGE;
   @Expose
   public ArchetypeTalentGroup<GlassCannonTalent> GLASS_CANNON;
   @Expose
   public ArchetypeTalentGroup<CommanderTalent> COMMANDER;
   @Expose
   public ArchetypeTalentGroup<WardTalent> WARD;
   @Expose
   public ArchetypeTalentGroup<BarbaricTalent> BARBARIC;
   @Expose
   public TalentGroup<SoulShardTalent> SOUL_HUNTER;

   @Override
   public String getName() {
      return "talents";
   }

   public List<TalentGroup<?>> getAll() {
      return Arrays.asList(
         this.HASTE,
         this.RESISTANCE,
         this.STRENGTH,
         this.FIRE_RESISTANCE,
         this.SPEED,
         this.WATER_BREATHING,
         this.WELL_FIT,
         this.TWERKER,
         this.ELVISH,
         this.CARELESS,
         this.ANGEL,
         this.REACH,
         this.EXPERIENCED,
         this.PARRY,
         this.STONE_SKIN,
         this.UNBREAKABLE,
         this.CRITICAL_STRIKE,
         this.LOOTER,
         this.CHUNKY,
         this.FRENZY,
         this.STEP,
         this.ARTISAN,
         this.TREASURE_HUNTER,
         this.BREAKABLE,
         this.LUCKY_ALTAR,
         this.FATAL_STRIKE,
         this.FATAL_STRIKE_CHANCE,
         this.FATAL_STRIKE_DAMAGE,
         this.THORNS,
         this.THORNS_CHANCE,
         this.THORNS_DAMAGE,
         this.GLASS_CANNON,
         this.COMMANDER,
         this.WARD,
         this.BARBARIC,
         this.SOUL_HUNTER
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
      this.HASTE = TalentGroup.ofEffect("Haste", Effects.field_76422_e, EffectTalent.Type.ICON_ONLY, 6, i -> {
         if (i < 3) {
            return 2;
         } else {
            return i == 3 ? 3 : 4;
         }
      }, EffectTalent.Operator.SET);
      this.RESISTANCE = new TalentGroup<>(
         "Resistance",
         new ResistanceTalent(3, 0.01F),
         new ResistanceTalent(3, 0.02F),
         new ResistanceTalent(3, 0.03F),
         new ResistanceTalent(3, 0.04F),
         new ResistanceTalent(3, 0.05F),
         new ResistanceTalent(3, 0.06F),
         new ResistanceTalent(3, 0.07F),
         new ResistanceTalent(3, 0.08F),
         new ResistanceTalent(3, 0.09F),
         new ResistanceTalent(3, 0.1F)
      );
      this.STRENGTH = TalentGroup.ofEffect("Strength", Effects.field_76420_g, EffectTalent.Type.ICON_ONLY, 2, i -> 3, EffectTalent.Operator.SET);
      this.FIRE_RESISTANCE = TalentGroup.ofEffect("Fire Resistance", Effects.field_76426_n, EffectTalent.Type.ICON_ONLY, 1, i -> 5, EffectTalent.Operator.SET);
      this.SPEED = TalentGroup.ofEffect("Speed", Effects.field_76424_c, EffectTalent.Type.ICON_ONLY, 5, i -> 2, EffectTalent.Operator.SET);
      this.WATER_BREATHING = TalentGroup.ofEffect("Water Breathing", Effects.field_76427_o, EffectTalent.Type.ICON_ONLY, 1, i -> 5, EffectTalent.Operator.SET);
      this.WELL_FIT = TalentGroup.ofAttribute("Well Fit", Attributes.field_233818_a_, "Extra Health", 10, i -> 1, i -> i * 2.0, i -> Operation.ADDITION);
      this.REACH = TalentGroup.ofAttribute(
         "Reach", (Attribute)ForgeMod.REACH_DISTANCE.get(), "Maximum Reach", 10, i -> 1, i -> i * 1.0, i -> Operation.ADDITION
      );
      this.TWERKER = new TalentGroup<>("Twerker", new TwerkerTalent(4));
      this.ELVISH = new TalentGroup<>("Elvish", new ElvishTalent(10));
      this.CARELESS = new TalentGroup<>("Careless", new CarelessTalent(3));
      this.ANGEL = new TalentGroup<>("Angel", new AngelTalent(200));
      this.EXPERIENCED = new TalentGroup<>(
         "Experienced",
         new ExperiencedTalent(2, 0.2F),
         new ExperiencedTalent(2, 0.4F),
         new ExperiencedTalent(2, 0.6F),
         new ExperiencedTalent(2, 0.8F),
         new ExperiencedTalent(2, 1.0F),
         new ExperiencedTalent(2, 1.2F),
         new ExperiencedTalent(2, 1.4F),
         new ExperiencedTalent(2, 1.6F),
         new ExperiencedTalent(2, 1.8F),
         new ExperiencedTalent(2, 2.0F)
      );
      this.PARRY = new TalentGroup<>(
         "Parry",
         new ParryTalent(2, 0.02F),
         new ParryTalent(2, 0.04F),
         new ParryTalent(2, 0.06F),
         new ParryTalent(2, 0.08F),
         new ParryTalent(2, 0.1F),
         new ParryTalent(2, 0.12F),
         new ParryTalent(2, 0.14F),
         new ParryTalent(2, 0.16F),
         new ParryTalent(2, 0.18F),
         new ParryTalent(2, 0.2F)
      );
      this.STONE_SKIN = TalentGroup.ofAttribute(
         "Stone Skin", Attributes.field_233820_c_, "Extra Knockback Resistance", 10, i -> 2, i -> i * 0.1F, i -> Operation.ADDITION
      );
      this.UNBREAKABLE = TalentGroup.of("Unbreakable", 10, i -> new UnbreakableTalent(2, i + 1));
      this.CRITICAL_STRIKE = TalentGroup.of("Critical Strike", 5, i -> new CriticalStrikeTalent(3, (i + 1) * 0.2F));
      this.CHUNKY = TalentGroup.ofAttribute(
         "Chunky", Attributes.field_233818_a_, "Extra Health 2", 10, i -> i < 5 ? 2 : 3, i -> i * 2.0, i -> Operation.ADDITION
      );
      this.FRENZY = ArchetypeTalentGroup.of("Frenzy", 3, i -> new FrenzyTalent(i * 2 - 1, (i + 1) * 0.1F, 2.0F));
      this.STEP = TalentGroup.of("Step", 1, i -> new StepTalent(4, 1.0F));
      this.ARTISAN = TalentGroup.of("Artisan", 2, i -> new ArtisanTalent(3, "All"));
      this.LOOTER = TalentGroup.ofEffect("Looter", Effects.field_188425_z, EffectTalent.Type.ICON_ONLY, 2, i -> 3, EffectTalent.Operator.SET);
      this.TREASURE_HUNTER = TalentGroup.ofEffect("Treasure Hunter", Effects.field_188425_z, EffectTalent.Type.ICON_ONLY, 3, i -> 3, EffectTalent.Operator.SET);
      this.BREAKABLE = new TalentGroup<>(
         "Breakable",
         new BreakableTalent(1, 0.1F, 0.0F),
         new BreakableTalent(1, 0.15F, -0.2F),
         new BreakableTalent(1, 0.2F, -0.3F),
         new BreakableTalent(1, 0.25F, -0.4F)
      );
      this.LUCKY_ALTAR = new TalentGroup<>("Lucky Altar", new LuckyAltarTalent(3, 0.1F), new LuckyAltarTalent(6, 0.2F), new LuckyAltarTalent(8, 0.25F));
      this.FATAL_STRIKE = new TalentGroup<>("Fatal Strike", new FatalStrikeTalent(5, 0.05F, 0.5F));
      this.FATAL_STRIKE_CHANCE = new TalentGroup<>(
         "Fatal Strike Chance",
         new FatalStrikeChanceTalent(2, 0.05F),
         new FatalStrikeChanceTalent(2, 0.06F),
         new FatalStrikeChanceTalent(2, 0.07F),
         new FatalStrikeChanceTalent(2, 0.08F),
         new FatalStrikeChanceTalent(2, 0.09F),
         new FatalStrikeChanceTalent(2, 0.1F),
         new FatalStrikeChanceTalent(2, 0.11F),
         new FatalStrikeChanceTalent(2, 0.12F),
         new FatalStrikeChanceTalent(2, 0.13F),
         new FatalStrikeChanceTalent(2, 0.15F)
      );
      this.FATAL_STRIKE_DAMAGE = new TalentGroup<>(
         "Fatal Strike Damage",
         new FatalStrikeDamageTalent(2, 1.0F),
         new FatalStrikeDamageTalent(2, 1.1F),
         new FatalStrikeDamageTalent(2, 1.2F),
         new FatalStrikeDamageTalent(2, 1.3F),
         new FatalStrikeDamageTalent(2, 1.4F),
         new FatalStrikeDamageTalent(2, 1.5F),
         new FatalStrikeDamageTalent(2, 1.6F),
         new FatalStrikeDamageTalent(2, 1.7F),
         new FatalStrikeDamageTalent(2, 1.8F),
         new FatalStrikeDamageTalent(2, 2.0F)
      );
      this.THORNS = new TalentGroup<>("Thorns", new ThornsTalent(5, 0.05F, 0.5F));
      this.THORNS_CHANCE = new TalentGroup<>(
         "Thorns Chance",
         new ThornsChanceTalent(2, 0.05F),
         new ThornsChanceTalent(2, 0.06F),
         new ThornsChanceTalent(2, 0.07F),
         new ThornsChanceTalent(2, 0.08F),
         new ThornsChanceTalent(2, 0.09F),
         new ThornsChanceTalent(2, 0.1F),
         new ThornsChanceTalent(2, 0.11F),
         new ThornsChanceTalent(2, 0.12F),
         new ThornsChanceTalent(2, 0.13F),
         new ThornsChanceTalent(2, 0.15F)
      );
      this.THORNS_DAMAGE = new TalentGroup<>(
         "Thorns Damage",
         new ThornsDamageTalent(2, 1.0F),
         new ThornsDamageTalent(2, 1.1F),
         new ThornsDamageTalent(2, 1.2F),
         new ThornsDamageTalent(2, 1.3F),
         new ThornsDamageTalent(2, 1.4F),
         new ThornsDamageTalent(2, 1.5F),
         new ThornsDamageTalent(2, 1.6F),
         new ThornsDamageTalent(2, 1.7F),
         new ThornsDamageTalent(2, 1.8F),
         new ThornsDamageTalent(2, 2.0F)
      );
      this.GLASS_CANNON = new ArchetypeTalentGroup<>("Glass Cannon", new GlassCannonTalent(2, 1.5F, 1.5F));
      this.COMMANDER = new ArchetypeTalentGroup<>("Commander", new CommanderTalent(2, 1.2F, 0.8F, 0.1F, 1.5F));
      this.WARD = new ArchetypeTalentGroup<>(
         "Ward", new WardTalent(2, 0.2F, 15, new EffectTalent(0, Effects.field_188425_z, 1, EffectTalent.Type.HIDDEN, EffectTalent.Operator.ADD), 0.01F)
      );
      this.BARBARIC = new ArchetypeTalentGroup<>("Barbaric", new BarbaricTalent(2, 100, 0.015F, 1));
      this.SOUL_HUNTER = new TalentGroup<>(
         "Soul Hunter", new SoulShardTalent(2, 0.25F), new SoulShardTalent(2, 0.5F), new SoulShardTalent(2, 0.75F), new SoulShardTalent(2, 1.0F)
      );
   }
}
