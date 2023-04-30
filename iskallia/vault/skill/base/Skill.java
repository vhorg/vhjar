package iskallia.vault.skill.base;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.basic.TypeSupplierAdapter;
import iskallia.vault.core.net.ArrayBitBuffer;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.item.crystal.data.serializable.ISerializable;
import iskallia.vault.skill.ability.effect.DashAbility;
import iskallia.vault.skill.ability.effect.DashDamageAbility;
import iskallia.vault.skill.ability.effect.DashWarpAbility;
import iskallia.vault.skill.ability.effect.EmpowerAbility;
import iskallia.vault.skill.ability.effect.EmpowerIceArmourAbility;
import iskallia.vault.skill.ability.effect.ExecuteAbility;
import iskallia.vault.skill.ability.effect.FarmerAbility;
import iskallia.vault.skill.ability.effect.FarmerAnimalAbility;
import iskallia.vault.skill.ability.effect.FarmerCactusAbility;
import iskallia.vault.skill.ability.effect.FarmerMelonAbility;
import iskallia.vault.skill.ability.effect.GhostWalkAbility;
import iskallia.vault.skill.ability.effect.GhostWalkSpiritAbility;
import iskallia.vault.skill.ability.effect.HealAbility;
import iskallia.vault.skill.ability.effect.HealEffectAbility;
import iskallia.vault.skill.ability.effect.HealGroupAbility;
import iskallia.vault.skill.ability.effect.JavelinAbility;
import iskallia.vault.skill.ability.effect.JavelinPiercingAbility;
import iskallia.vault.skill.ability.effect.JavelinScatterAbility;
import iskallia.vault.skill.ability.effect.JavelinSightAbility;
import iskallia.vault.skill.ability.effect.ManaShieldAbility;
import iskallia.vault.skill.ability.effect.ManaShieldRetributionAbility;
import iskallia.vault.skill.ability.effect.MegaJumpAbility;
import iskallia.vault.skill.ability.effect.MegaJumpBreakDownAbility;
import iskallia.vault.skill.ability.effect.MegaJumpBreakUpAbility;
import iskallia.vault.skill.ability.effect.NovaAbility;
import iskallia.vault.skill.ability.effect.NovaDotAbility;
import iskallia.vault.skill.ability.effect.NovaSpeedAbility;
import iskallia.vault.skill.ability.effect.RampageAbility;
import iskallia.vault.skill.ability.effect.RampageChainAbility;
import iskallia.vault.skill.ability.effect.RampageLeechAbility;
import iskallia.vault.skill.ability.effect.ShellAbility;
import iskallia.vault.skill.ability.effect.ShellPorcupineAbility;
import iskallia.vault.skill.ability.effect.ShellQuillAbility;
import iskallia.vault.skill.ability.effect.SmiteAbility;
import iskallia.vault.skill.ability.effect.SmiteArchonAbility;
import iskallia.vault.skill.ability.effect.SmiteThunderstormAbility;
import iskallia.vault.skill.ability.effect.StonefallAbility;
import iskallia.vault.skill.ability.effect.StonefallColdAbility;
import iskallia.vault.skill.ability.effect.StonefallSnowAbility;
import iskallia.vault.skill.ability.effect.SummonEternalAbility;
import iskallia.vault.skill.ability.effect.TauntAbility;
import iskallia.vault.skill.ability.effect.TauntCharmAbility;
import iskallia.vault.skill.ability.effect.TauntRepelAbility;
import iskallia.vault.skill.ability.effect.TotemAbility;
import iskallia.vault.skill.ability.effect.TotemManaRegenAbility;
import iskallia.vault.skill.ability.effect.TotemMobDamageAbility;
import iskallia.vault.skill.ability.effect.TotemPlayerDamageAbility;
import iskallia.vault.skill.ability.effect.VeinMinerAbility;
import iskallia.vault.skill.ability.effect.VeinMinerDurabilityAbility;
import iskallia.vault.skill.ability.effect.VeinMinerFortuneAbility;
import iskallia.vault.skill.ability.effect.VeinMinerVoidAbility;
import iskallia.vault.skill.ability.effect.spi.HunterAbility;
import iskallia.vault.skill.expertise.type.AngelExpertise;
import iskallia.vault.skill.expertise.type.ArtisanExpertise;
import iskallia.vault.skill.expertise.type.BarteringExpertise;
import iskallia.vault.skill.expertise.type.BountyHunterExpertise;
import iskallia.vault.skill.expertise.type.DivineExpertise;
import iskallia.vault.skill.expertise.type.ExperiencedExpertise;
import iskallia.vault.skill.expertise.type.FortunateExpertise;
import iskallia.vault.skill.expertise.type.InfuserExpertise;
import iskallia.vault.skill.expertise.type.LuckyAltarExpertise;
import iskallia.vault.skill.expertise.type.MysticExpertise;
import iskallia.vault.skill.expertise.type.TrinketerExpertise;
import iskallia.vault.skill.talent.type.AlchemistTalent;
import iskallia.vault.skill.talent.type.ConditionalDamageTalent;
import iskallia.vault.skill.talent.type.EffectTalent;
import iskallia.vault.skill.talent.type.GearAttributeTalent;
import iskallia.vault.skill.talent.type.JavelinConductTalent;
import iskallia.vault.skill.talent.type.JavelinDamageTalent;
import iskallia.vault.skill.talent.type.JavelinFrugalTalent;
import iskallia.vault.skill.talent.type.JavelinThrowPowerTalent;
import iskallia.vault.skill.talent.type.PrudentTalent;
import iskallia.vault.skill.talent.type.VanillaAttributeTalent;
import iskallia.vault.skill.talent.type.health.HighHealthGearAttributeTalent;
import iskallia.vault.skill.talent.type.health.LowHealthDamageTalent;
import iskallia.vault.skill.talent.type.health.LowHealthResistanceTalent;
import iskallia.vault.skill.talent.type.luckyhit.DamageLuckyHitTalent;
import iskallia.vault.skill.talent.type.luckyhit.HealthLeechLuckyHitTalent;
import iskallia.vault.skill.talent.type.luckyhit.ManaLeechLuckyHitTalent;
import iskallia.vault.skill.talent.type.luckyhit.SweepingLuckyHitTalent;
import iskallia.vault.skill.talent.type.mana.HighManaGearAttributeTalent;
import iskallia.vault.skill.talent.type.mana.LowManaDamageTalent;
import iskallia.vault.skill.talent.type.mana.LowManaHealingEfficiencyTalent;
import iskallia.vault.skill.talent.type.onhit.CastOnHitTalent;
import iskallia.vault.skill.talent.type.onhit.DamageOnHitTalent;
import iskallia.vault.skill.talent.type.onhit.EffectOnHitTalent;
import iskallia.vault.skill.talent.type.onhit.SweepingOnHitTalent;
import iskallia.vault.skill.talent.type.onkill.CastOnKillTalent;
import iskallia.vault.skill.talent.type.onkill.SweepingOnKillTalent;
import iskallia.vault.skill.tree.AbilityTree;
import iskallia.vault.skill.tree.ExpertiseTree;
import iskallia.vault.skill.tree.TalentTree;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.nbt.CompoundTag;

public abstract class Skill implements ISerializable<CompoundTag, JsonObject> {
   protected Skill parent;
   protected String id = null;
   protected String name;
   protected boolean present = false;

   public Skill getParent() {
      return this.parent;
   }

   public String getId() {
      return this.id;
   }

   public String getName() {
      return this.name;
   }

   public boolean isUnlocked() {
      return this.present;
   }

   public void setParent(Skill parent) {
      this.parent = parent;
   }

   public void setId(String id) {
      this.id = id;
   }

   public void setName(String name) {
      this.name = name;
   }

   public void setPresent(boolean present, SkillContext context) {
      if (this.present != (this.present = present)) {
         if (this.present) {
            this.onAdd(context);
         } else {
            this.onRemove(context);
         }
      }
   }

   public void onAdd(SkillContext context) {
   }

   public void onRemove(SkillContext context) {
   }

   public Optional<Skill> getForId(String id) {
      return Objects.equals(this.getId(), id) ? Optional.of(this) : Optional.empty();
   }

   public <T> void iterate(Class<T> type, Consumer<T> action) {
      if (type.isAssignableFrom(this.getClass())) {
         action.accept((T)this);
      }
   }

   public <T> List<T> getAll(Class<T> type, Predicate<T> predicate) {
      List<T> result = new ArrayList<>();
      this.iterate(type, e -> {
         if (predicate.test(e)) {
            result.add(e);
         }
      });
      return result;
   }

   public Skill mergeFrom(Skill other, SkillContext context) {
      Skill result = other.copy();
      result.present = this.present;
      return result;
   }

   public <T extends Skill> T copy() {
      ArrayBitBuffer buffer = ArrayBitBuffer.empty();
      Adapters.SKILL.writeBits(this, buffer);
      buffer.setPosition(0);
      return (T)Adapters.SKILL.readBits(buffer).orElseThrow();
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      Adapters.UTF_8.asNullable().writeBits(this.id, buffer);
      Adapters.UTF_8.asNullable().writeBits(this.name, buffer);
      Adapters.BOOLEAN.writeBits(this.present, buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      this.id = Adapters.UTF_8.asNullable().readBits(buffer).orElse(null);
      this.name = Adapters.UTF_8.asNullable().readBits(buffer).orElse(null);
      this.present = Adapters.BOOLEAN.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      CompoundTag nbt = new CompoundTag();
      Adapters.UTF_8.writeNbt(this.id).ifPresent(tag -> nbt.put("id", tag));
      Adapters.UTF_8.writeNbt(this.name).ifPresent(tag -> nbt.put("name", tag));
      Adapters.BOOLEAN.writeNbt(this.present).ifPresent(tag -> nbt.put("present", tag));
      return Optional.of(nbt);
   }

   public void readNbt(CompoundTag nbt) {
      this.id = Adapters.UTF_8.readNbt(nbt.get("id")).orElse(null);
      this.name = Adapters.UTF_8.readNbt(nbt.get("name")).orElse(null);
      this.present = Adapters.BOOLEAN.readNbt(nbt.get("present")).orElse(false);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      JsonObject json = new JsonObject();
      Adapters.UTF_8.writeJson(this.id).ifPresent(element -> json.add("id", element));
      Adapters.UTF_8.writeJson(this.name).ifPresent(element -> json.add("name", element));
      Adapters.BOOLEAN.writeJson(this.present).ifPresent(element -> json.add("present", element));
      return Optional.of(json);
   }

   public void readJson(JsonObject json) {
      this.id = Adapters.UTF_8.readJson(json.get("id")).orElse(null);
      this.name = Adapters.UTF_8.readJson(json.get("name")).orElse(null);
      this.present = Adapters.BOOLEAN.readJson(json.get("present")).orElse(false);
   }

   public static class Adapter extends TypeSupplierAdapter<Skill> {
      public Adapter() {
         super("type", false);
         this.register("talents", TalentTree.class, TalentTree::new);
         this.register("abilities", AbilityTree.class, AbilityTree::new);
         this.register("expertises", ExpertiseTree.class, ExpertiseTree::new);
         this.register("tiered", TieredSkill.class, TieredSkill::new);
         this.register("specialized", SpecializedSkill.class, SpecializedSkill::new);
         this.register("grouped", GroupedSkill.class, GroupedSkill::new);
         this.register("vanilla_attribute", VanillaAttributeTalent.class, VanillaAttributeTalent::new);
         this.register("low_health_damage", LowHealthDamageTalent.class, LowHealthDamageTalent::new);
         this.register("low_health_resistance", LowHealthResistanceTalent.class, LowHealthResistanceTalent::new);
         this.register("high_health_gear_attribute", HighHealthGearAttributeTalent.class, HighHealthGearAttributeTalent::new);
         this.register("high_mana_gear_attribute", HighManaGearAttributeTalent.class, HighManaGearAttributeTalent::new);
         this.register("lucky_altar", LuckyAltarExpertise.class, LuckyAltarExpertise::new);
         this.register("low_mana_healing_efficiency", LowManaHealingEfficiencyTalent.class, LowManaHealingEfficiencyTalent::new);
         this.register("low_mana_damage", LowManaDamageTalent.class, LowManaDamageTalent::new);
         this.register("experience", ExperiencedExpertise.class, ExperiencedExpertise::new);
         this.register("crafting_potential", ArtisanExpertise.class, ArtisanExpertise::new);
         this.register("creative_flight", AngelExpertise.class, AngelExpertise::new);
         this.register("effect", EffectTalent.class, EffectTalent::new);
         this.register("shop_pedestal", BarteringExpertise.class, BarteringExpertise::new);
         this.register("effect_damage", ConditionalDamageTalent.class, ConditionalDamageTalent::new);
         this.register("gear_attribute", GearAttributeTalent.class, GearAttributeTalent::new);
         this.register("cast_on_hit", CastOnHitTalent.class, CastOnHitTalent::new);
         this.register("damage_on_hit", DamageOnHitTalent.class, DamageOnHitTalent::new);
         this.register("sweeping_on_hit", SweepingOnHitTalent.class, SweepingOnHitTalent::new);
         this.register("effect_on_hit", EffectOnHitTalent.class, EffectOnHitTalent::new);
         this.register("cast_on_kill", CastOnKillTalent.class, CastOnKillTalent::new);
         this.register("sweeping_on_kill", SweepingOnKillTalent.class, SweepingOnKillTalent::new);
         this.register("damage_lucky_hit", DamageLuckyHitTalent.class, DamageLuckyHitTalent::new);
         this.register("health_leech_lucky_hit", HealthLeechLuckyHitTalent.class, HealthLeechLuckyHitTalent::new);
         this.register("mana_leech_lucky_hit", ManaLeechLuckyHitTalent.class, ManaLeechLuckyHitTalent::new);
         this.register("sweeping_lucky_hit", SweepingLuckyHitTalent.class, SweepingLuckyHitTalent::new);
         this.register("bounty_hunter", BountyHunterExpertise.class, BountyHunterExpertise::new);
         this.register("divine", DivineExpertise.class, DivineExpertise::new);
         this.register("trinketer", TrinketerExpertise.class, TrinketerExpertise::new);
         this.register("mystic", MysticExpertise.class, MysticExpertise::new);
         this.register("infuser", InfuserExpertise.class, InfuserExpertise::new);
         this.register("fortunate", FortunateExpertise.class, FortunateExpertise::new);
         this.register("javelin_throw_power", JavelinThrowPowerTalent.class, JavelinThrowPowerTalent::new);
         this.register("javelin_damage", JavelinDamageTalent.class, JavelinDamageTalent::new);
         this.register("javelin_conduct", JavelinConductTalent.class, JavelinConductTalent::new);
         this.register("javelin_frugal", JavelinFrugalTalent.class, JavelinFrugalTalent::new);
         this.register("prudent", PrudentTalent.class, PrudentTalent::new);
         this.register("alchemist", AlchemistTalent.class, AlchemistTalent::new);
         this.register("empower_speed", EmpowerAbility.class, EmpowerAbility::new);
         this.register("empower_ice_armor", EmpowerIceArmourAbility.class, EmpowerIceArmourAbility::new);
         this.register("mana_shield", ManaShieldAbility.class, ManaShieldAbility::new);
         this.register("mana_shield_retribution", ManaShieldRetributionAbility.class, ManaShieldRetributionAbility::new);
         this.register("rampage_damage", RampageAbility.class, RampageAbility::new);
         this.register("rampage_chain", RampageChainAbility.class, RampageChainAbility::new);
         this.register("rampage_leech", RampageLeechAbility.class, RampageLeechAbility::new);
         this.register("vein_miner", VeinMinerAbility.class, VeinMinerAbility::new);
         this.register("vein_miner_durability", VeinMinerDurabilityAbility.class, VeinMinerDurabilityAbility::new);
         this.register("vein_miner_void", VeinMinerVoidAbility.class, VeinMinerVoidAbility::new);
         this.register("vein_miner_fortune", VeinMinerFortuneAbility.class, VeinMinerFortuneAbility::new);
         this.register("farmer", FarmerAbility.class, FarmerAbility::new);
         this.register("farmer_animal", FarmerAnimalAbility.class, FarmerAnimalAbility::new);
         this.register("farmer_melon", FarmerMelonAbility.class, FarmerMelonAbility::new);
         this.register("farmer_cactus", FarmerCactusAbility.class, FarmerCactusAbility::new);
         this.register("execute", ExecuteAbility.class, ExecuteAbility::new);
         this.register("totem_player_damage", TotemPlayerDamageAbility.class, TotemPlayerDamageAbility::new);
         this.register("totem_mana_regen", TotemManaRegenAbility.class, TotemManaRegenAbility::new);
         this.register("totem_mob_damage", TotemMobDamageAbility.class, TotemMobDamageAbility::new);
         this.register("totem", TotemAbility.class, TotemAbility::new);
         this.register("dash", DashAbility.class, DashAbility::new);
         this.register("dash_damage", DashDamageAbility.class, DashDamageAbility::new);
         this.register("dash_warp", DashWarpAbility.class, DashWarpAbility::new);
         this.register("taunt", TauntAbility.class, TauntAbility::new);
         this.register("taunt_charm", TauntCharmAbility.class, TauntCharmAbility::new);
         this.register("taunt_repel", TauntRepelAbility.class, TauntRepelAbility::new);
         this.register("heal", HealAbility.class, HealAbility::new);
         this.register("heal_group", HealGroupAbility.class, HealGroupAbility::new);
         this.register("heal_cleanse", HealEffectAbility.class, HealEffectAbility::new);
         this.register("stonefall", StonefallAbility.class, StonefallAbility::new);
         this.register("stonefall_cold", StonefallColdAbility.class, StonefallColdAbility::new);
         this.register("stonefall_snow", StonefallSnowAbility.class, StonefallSnowAbility::new);
         this.register("summon_eternal", SummonEternalAbility.class, SummonEternalAbility::new);
         this.register("nova_burst", NovaAbility.class, NovaAbility::new);
         this.register("nova_slow", NovaSpeedAbility.class, NovaSpeedAbility::new);
         this.register("nova_dot", NovaDotAbility.class, NovaDotAbility::new);
         this.register("ghost_walk", GhostWalkAbility.class, GhostWalkAbility::new);
         this.register("ghost_walk_spirit", GhostWalkSpiritAbility.class, GhostWalkSpiritAbility::new);
         this.register("hunter", HunterAbility.class, HunterAbility::new);
         this.register("mega_jump", MegaJumpAbility.class, MegaJumpAbility::new);
         this.register("mega_jump_break_down", MegaJumpBreakDownAbility.class, MegaJumpBreakDownAbility::new);
         this.register("mega_jump_break_up", MegaJumpBreakUpAbility.class, MegaJumpBreakUpAbility::new);
         this.register("javelin", JavelinAbility.class, JavelinAbility::new);
         this.register("javelin_piercing", JavelinPiercingAbility.class, JavelinPiercingAbility::new);
         this.register("javelin_scatter", JavelinScatterAbility.class, JavelinScatterAbility::new);
         this.register("javelin_sight", JavelinSightAbility.class, JavelinSightAbility::new);
         this.register("smite", SmiteAbility.class, SmiteAbility::new);
         this.register("smite_archon", SmiteArchonAbility.class, SmiteArchonAbility::new);
         this.register("smite_thunderstorm", SmiteThunderstormAbility.class, SmiteThunderstormAbility::new);
         this.register("shell", ShellAbility.class, ShellAbility::new);
         this.register("shell_porcupine", ShellPorcupineAbility.class, ShellPorcupineAbility::new);
         this.register("shell_quill", ShellQuillAbility.class, ShellQuillAbility::new);
         this.register("empower_porcupine", RemovedSkill.class, () -> new RemovedSkill("empower_porcupine"));
      }

      public String getType(Skill value) {
         return value instanceof RemovedSkill removed ? removed.getType() : super.getType(value);
      }
   }
}
