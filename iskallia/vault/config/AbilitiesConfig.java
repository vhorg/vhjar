package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.core.world.data.tile.TilePredicate;
import iskallia.vault.skill.ability.effect.DashAbility;
import iskallia.vault.skill.ability.effect.DashDamageAbility;
import iskallia.vault.skill.ability.effect.DashWarpAbility;
import iskallia.vault.skill.ability.effect.EmpowerAbility;
import iskallia.vault.skill.ability.effect.EmpowerIceArmourAbility;
import iskallia.vault.skill.ability.effect.EmpowerSlownessAuraAbility;
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
import iskallia.vault.skill.base.LearnableSkill;
import iskallia.vault.skill.base.Skill;
import iskallia.vault.skill.base.SpecializedSkill;
import iskallia.vault.skill.base.TieredSkill;
import iskallia.vault.skill.tree.AbilityTree;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.registries.ForgeRegistries;

public class AbilitiesConfig extends Config {
   private static final int DEFAULT_COLOR = TextColor.parseColor("#00FFFF").getValue();
   @Expose
   public AbilityTree tree;

   @Override
   public String getName() {
      return "abilities";
   }

   public Optional<AbilityTree> get() {
      return Optional.of(this.tree);
   }

   public Optional<Skill> getAbilityById(String name) {
      return this.tree.getForId(name);
   }

   @Override
   protected void reset() {
      this.tree = new AbilityTree();
      this.tree
         .skills
         .add(
            spec(
               "Dash",
               "Dash",
               tier("Dash_Base", "Dash", quintuple(new DashAbility(0, 1, 1, 10, 1.0F, 10))),
               tier("Dash_Damage", "Dash: Bullet", quintuple(new DashDamageAbility(0, 1, 1, 10, 1.0F, 10, 0.5F))),
               tier("Dash_Warp", "Dash: Warp", quintuple(new DashWarpAbility(0, 1, 1, 10, 1.0F, 1.0F)))
            )
         );
      this.tree
         .skills
         .add(
            spec(
               "Empower",
               "Empower",
               tier("Empower_Base", "Empower", quintuple(new EmpowerAbility(0, 1, 1, 10, 1.0F, 200, 0.5F))),
               tier("Empower_Ice_Armor", "Empower: Ice Armor", quintuple(new EmpowerIceArmourAbility(0, 1, 1, 10, 1.0F, 200, 10.0F, 0, 20, 1.0F))),
               tier("Empower_Slowness_Aura", "Empower: Slowness Aura", quintuple(new EmpowerSlownessAuraAbility(0, 1, 1, 10, 1.0F, 200, 10.0F, 0)))
            )
         );
      this.tree.skills.add(spec("Execute", "Execute", tier("Execute_Base", "Execute", quintuple(new ExecuteAbility(0, 1, 1, 10, 0.5F, 200)))));
      this.tree
         .skills
         .add(
            spec(
               "Farmer",
               "Farmer",
               tier("Farmer_Base", "Farmer", quintuple(new FarmerAbility(0, 1, 1, 10, 1.0F, 5, 10, 10))),
               tier("Farmer_Melon", "Farmer: Cultivator", quintuple(new FarmerMelonAbility(0, 1, 1, 10, 1.0F, 5, 10, 10))),
               tier("Farmer_Cactus", "Farmer: Gardener", quintuple(new FarmerCactusAbility(0, 1, 1, 10, 1.0F, 5, 10, 10))),
               tier("Farmer_Animal", "Farmer: Rancher", quintuple(new FarmerAnimalAbility(0, 1, 1, 10, 1.0F, 5, 10, 10, 0.5F)))
            )
         );
      this.tree
         .skills
         .add(
            spec(
               "Ghost_Walk",
               "Ghost Walk",
               tier("Ghost_Walk_Base", "Ghost Walk", quintuple(new GhostWalkAbility(0, 1, 1, 10, 1.0F, 200))),
               tier("Ghost_Walk_Spirit", "Ghost Walk: Spirit", quintuple(new GhostWalkSpiritAbility(0, 1, 1, 10, 1.0F, 200)))
            )
         );
      this.tree
         .skills
         .add(
            spec(
               "Heal",
               "Heal",
               tier("Heal_Base", "Heal", quintuple(new HealAbility(0, 1, 1, 10, 1.0F))),
               tier("Heal_Group", "Heal: Aid", quintuple(new HealGroupAbility(0, 1, 1, 10, 1.0F, 10.0F))),
               tier(
                  "Heal_Cleanse",
                  "Heal: Cleanse",
                  quintuple(new HealEffectAbility(0, 1, 1, 10, 1.0F, cleanseEffects(), HealEffectAbility.RemovalStrategy.DEFINED_ONLY))
               )
            )
         );
      this.tree
         .skills
         .add(
            spec(
               "Hunter",
               "Hunter",
               tier(
                  "Hunter_Base",
                  "Hunter",
                  quintuple(
                     new HunterAbility(
                        0, 1, 1, 10, 1.0F, 10.0, DEFAULT_COLOR, 200, List.of(TilePredicate.of("the_vault:wooden_chest{Hidden:0b}", true).orElseThrow())
                     )
                  )
               ),
               tier("Hunter_Blocks", "Hunter: Observer", quintuple(new HunterAbility(0, 1, 1, 10, 1.0F, 10.0, DEFAULT_COLOR, 200, observerKeys()))),
               tier(
                  "Hunter_Wooden",
                  "Hunter: Wooden",
                  quintuple(
                     new HunterAbility(
                        0, 1, 1, 10, 1.0F, 10.0, DEFAULT_COLOR, 200, List.of(TilePredicate.of("the_vault:wooden_chest{Hidden:0b}", true).orElseThrow())
                     )
                  )
               ),
               tier(
                  "Hunter_Gilded",
                  "Hunter: Gilded",
                  quintuple(
                     new HunterAbility(
                        0, 1, 1, 10, 1.0F, 10.0, DEFAULT_COLOR, 200, List.of(TilePredicate.of("the_vault:gilded_chest{Hidden:0b}", true).orElseThrow())
                     )
                  )
               ),
               tier(
                  "Hunter_Living",
                  "Hunter: Living",
                  quintuple(
                     new HunterAbility(
                        0, 1, 1, 10, 1.0F, 10.0, DEFAULT_COLOR, 200, List.of(TilePredicate.of("the_vault:living_chest{Hidden:0b}", true).orElseThrow())
                     )
                  )
               ),
               tier(
                  "Hunter_Ornate",
                  "Hunter: Ornate",
                  quintuple(
                     new HunterAbility(
                        0, 1, 1, 10, 1.0F, 10.0, DEFAULT_COLOR, 200, List.of(TilePredicate.of("the_vault:ornate_chest{Hidden:0b}", true).orElseThrow())
                     )
                  )
               ),
               tier(
                  "Hunter_Coins",
                  "Hunter: Coins",
                  quintuple(
                     new HunterAbility(
                        0, 1, 1, 10, 1.0F, 10.0, DEFAULT_COLOR, 200, List.of(TilePredicate.of("the_vault:coin_pile{Hidden:0b}", true).orElseThrow())
                     )
                  )
               )
            )
         );
      this.tree
         .skills
         .add(
            spec(
               "Javelin",
               "Javelin",
               tier("Javelin_Base", "Javelin", quintuple(new JavelinAbility(0, 1, 1, 10, 1.0F, 0.5F, 1.0F, 1.0F))),
               tier("Javelin_Piercing", "Javelin: Piercing", quintuple(new JavelinPiercingAbility(0, 1, 1, 10, 1.0F, 0.5F, 1.0F, 1))),
               tier("Javelin_Scatter", "Javelin: Scatter", quintuple(new JavelinScatterAbility(0, 1, 1, 10, 1.0F, 0.5F, 1.0F, 1, 1, 1))),
               tier("Javelin_Sight", "Javelin: Sight", quintuple(new JavelinSightAbility(0, 1, 1, 10, 1.0F, 0.5F, 1.0F, 10.0F, 1)))
            )
         );
      this.tree
         .skills
         .add(
            spec(
               "Mana_Shield",
               "Mana Shield",
               tier("Mana_Shield_Base", "Mana Shield", quintuple(new ManaShieldAbility(0, 1, 1, 10, 1.0F, 0.5F, 0.5F))),
               tier(
                  "Mana_Shield_Retribution",
                  "Mana Shield: Retribution",
                  quintuple(new ManaShieldRetributionAbility(0, 1, 1, 10, 1.0F, 0.5F, 0.5F, 10.0F, 0.5F))
               )
            )
         );
      this.tree
         .skills
         .add(
            spec(
               "Mega_Jump",
               "Mega Jump",
               tier("Mega_Jump_Base", "Mega Jump", quintuple(new MegaJumpAbility(0, 1, 1, 10, 1.0F, 10))),
               tier("Mega_Jump_Break_Up", "Mega Jump: Drill", quintuple(new MegaJumpBreakUpAbility(0, 1, 1, 10, 1.0F, 10))),
               tier("Mega_Jump_Break_Down", "Mega Jump: Dig", quintuple(new MegaJumpBreakDownAbility(0, 1, 1, 10, 1.0F, 3, 3)))
            )
         );
      this.tree
         .skills
         .add(
            spec(
               "Nova",
               "Nova",
               tier("Nova_Base", "Nova", quintuple(new NovaAbility(0, 1, 1, 10, 1.0F, 10.0F, 0.5F, 0.5F))),
               tier("Nova_Slow", "Nova: Frost", quintuple(new NovaSpeedAbility(0, 1, 1, 10, 1.0F, 10.0F, 0.5F, 0.5F, 200, 0, 20))),
               tier("Nova_Dot", "Nova: Poison", quintuple(new NovaDotAbility(0, 1, 1, 10, 1.0F, 10.0F, 0.5F, 0.5F, 200)))
            )
         );
      this.tree
         .skills
         .add(
            spec(
               "Rampage",
               "Rampage",
               tier("Rampage_Base", "Rampage", quintuple(new RampageAbility(0, 1, 1, 10, 1.0F, 0.5F))),
               tier("Rampage_Leech", "Rampage: Vampire", quintuple(new RampageLeechAbility(0, 1, 1, 10, 1.0F, 0.5F, 0.5F))),
               tier("Rampage_Chain", "Rampage: Chain", quintuple(new RampageChainAbility(0, 1, 1, 10, 1.0F, 0.5F, 5)))
            )
         );
      this.tree
         .skills
         .add(
            spec(
               "Shell",
               "Shell",
               tier("Shell_Base", "Shell", quintuple(new ShellAbility(0, 1, 1, 10, 1.0F))),
               tier("Shell_Porcupine", "Shell: Porcupine", quintuple(new ShellPorcupineAbility(0, 1, 1, 10, 1.0F, 0.5F, 0.5F, 5.0F))),
               tier("Shell_Quill", "Shell: Quill", quintuple(new ShellQuillAbility(0, 1, 1, 10, 1.0F, 0.5F, 0.5F, 5.0F, 3)))
            )
         );
      this.tree
         .skills
         .add(
            spec(
               "Stonefall",
               "Stonefall",
               tier("Stonefall_Base", "Stonefall", quintuple(new StonefallAbility(0, 1, 1, 10, 1.0F, 200, 0.5F, 10.0F, 0.5F))),
               tier("Stonefall_Snow", "Stonefall: Surefoot", quintuple(new StonefallSnowAbility(0, 1, 1, 10, 1.0F, 200, 0.5F, 10.0F, 0.5F, 0.5F))),
               tier("Stonefall_Cold", "Stonefall: Coldsnap", quintuple(new StonefallColdAbility(0, 1, 1, 10, 1.0F, 200, 0.5F, 10.0F, 0.5F, 0, 200, 4)))
            )
         );
      this.tree
         .skills
         .add(
            spec(
               "Summon_Eternal",
               "Summon Eternal",
               tier("Summon_Eternal_Base", "Summon Eternal", quintuple(new SummonEternalAbility(0, 1, 1, 10, 1.0F, 1, 200, 0.5F, false)))
            )
         );
      this.tree
         .skills
         .add(
            spec(
               "Taunt",
               "Taunt",
               tier("Taunt_Base", "Taunt", quintuple(new TauntAbility(0, 1, 1, 10, 1.0F, 10.0F, 200, 1))),
               tier("Taunt_Repel", "Taunt: Fear", quintuple(new TauntRepelAbility(0, 1, 1, 10, 1.0F, 10.0F, 200, 10.0F))),
               tier("Taunt_Charm", "Taunt: Charm", quintuple(new TauntCharmAbility(0, 1, 1, 10, 1.0F, 10.0F, 200, 3, 0.5F)))
            )
         );
      this.tree
         .skills
         .add(
            spec(
               "Totem",
               "Totem",
               tier("Totem_Base", "Totem: Rejuvenation", quintuple(new TotemAbility(0, 1, 1, 10, 1.0F, 200, 10.0F, 1.0F))),
               tier("Totem_Player_Damage", "Totem: Wrath", quintuple(new TotemPlayerDamageAbility(0, 1, 1, 10, 1.0F, 200, 10.0F, 0.5F))),
               tier("Totem_Mana_Regen", "Totem: Spirit", quintuple(new TotemManaRegenAbility(0, 1, 1, 10, 1.0F, 200, 10.0F, 0.5F))),
               tier("Totem_Mob_Damage", "Totem: Hatred", quintuple(new TotemMobDamageAbility(0, 1, 1, 10, 1.0F, 200, 10.0F, 0.5F, 20)))
            )
         );
      this.tree
         .skills
         .add(
            spec(
               "Vein_Miner",
               "Vein Miner",
               tier("Vein_Miner_Base", "Vein Miner", quintuple(new VeinMinerAbility(0, 1, 1, 10, 16))),
               tier("Vein_Miner_Fortune", "Vein Miner: Fortune", quintuple(new VeinMinerFortuneAbility(0, 1, 1, 10, 16, 1))),
               tier("Vein_Miner_Durability", "Vein Miner: Finesse", quintuple(new VeinMinerDurabilityAbility(0, 1, 1, 10, 16, 1))),
               tier("Vein_Miner_Void", "Vein Miner: Void", quintuple(new VeinMinerVoidAbility(0, 1, 1, 10, 16)))
            )
         );
      this.tree
         .skills
         .add(
            spec(
               "Smite",
               "Smite",
               tier("Smite_Base", "Smite", quintuple(new SmiteAbility(0, 1, 1, 10, 1.0F, 0.5F, 10, 20.0F, -1864448, 5.0F))),
               tier("Smite_Archon", "Smite: Archon", quintuple(new SmiteArchonAbility(0, 1, 1, 10, 1.0F, 0.5F, 10, 20.0F, -1864448, 5.0F, 0.0F))),
               tier("Smite_Thunderstorm", "Smite: Thunderstorm", quintuple(new SmiteThunderstormAbility(0, 1, 1, 10, 1.0F, 0.5F, 10, 20.0F, -1864448, 5.0F)))
            )
         );
   }

   private static LearnableSkill[] quintuple(LearnableSkill skill) {
      return new LearnableSkill[]{skill, skill.copy(), skill.copy(), skill.copy(), skill.copy()};
   }

   private static TieredSkill tier(String id, String name, LearnableSkill... skill) {
      return setIdAndName(id, name, new TieredSkill(0, 1, 1, Stream.of(skill)));
   }

   private static SpecializedSkill spec(String id, String name, LearnableSkill... skill) {
      return setIdAndName(id, name, new SpecializedSkill(0, 1, 1, Stream.of(skill)));
   }

   private static <S extends Skill> S setIdAndName(String id, String name, S skill) {
      skill.setId(id);
      skill.setName(name);
      return skill;
   }

   private static List<MobEffect> cleanseEffects() {
      return ForgeRegistries.MOB_EFFECTS.getValues().stream().filter(effect -> effect.getCategory() == MobEffectCategory.HARMFUL).toList();
   }

   private static List<TilePredicate> observerKeys() {
      return List.of(
         TilePredicate.of("the_vault:obelisk", true).orElseThrow(),
         TilePredicate.of("the_vault:scavenger_altar", true).orElseThrow(),
         TilePredicate.of("the_vault:monolith", true).orElseThrow(),
         TilePredicate.of("the_vault:lodestone", true).orElseThrow(),
         TilePredicate.of("the_vault:crake_pedestal", true).orElseThrow()
      );
   }
}
