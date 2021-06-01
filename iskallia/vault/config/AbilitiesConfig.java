package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.init.ModEffects;
import iskallia.vault.skill.ability.AbilityGroup;
import iskallia.vault.skill.ability.AbilityNode;
import iskallia.vault.skill.ability.type.CleanseAbility;
import iskallia.vault.skill.ability.type.DashAbility;
import iskallia.vault.skill.ability.type.EffectAbility;
import iskallia.vault.skill.ability.type.ExecuteAbility;
import iskallia.vault.skill.ability.type.GhostWalkAbility;
import iskallia.vault.skill.ability.type.MegaJumpAbility;
import iskallia.vault.skill.ability.type.RampageAbility;
import iskallia.vault.skill.ability.type.SelfSustainAbility;
import iskallia.vault.skill.ability.type.SummonEternalAbility;
import iskallia.vault.skill.ability.type.TankAbility;
import iskallia.vault.skill.ability.type.VeinMinerAbility;
import java.util.Arrays;
import java.util.List;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effects;

public class AbilitiesConfig extends Config {
   @Expose
   public AbilityGroup<EffectAbility> NIGHT_VISION;
   @Expose
   public AbilityGroup<EffectAbility> INVISIBILITY;
   @Expose
   public AbilityGroup<GhostWalkAbility> GHOST_WALK;
   @Expose
   public AbilityGroup<RampageAbility> RAMPAGE;
   @Expose
   public AbilityGroup<VeinMinerAbility> VEIN_MINER;
   @Expose
   public AbilityGroup<SelfSustainAbility> SELF_SUSTAIN;
   @Expose
   public AbilityGroup<DashAbility> DASH;
   @Expose
   public AbilityGroup<MegaJumpAbility> MEGA_JUMP;
   @Expose
   public AbilityGroup<CleanseAbility> CLEANSE;
   @Expose
   public AbilityGroup<TankAbility> TANK;
   @Expose
   public AbilityGroup<ExecuteAbility> EXECUTE;
   @Expose
   public AbilityGroup<SummonEternalAbility> SUMMON_ETERNAL;

   @Override
   public String getName() {
      return "abilities";
   }

   public List<AbilityGroup<?>> getAll() {
      return Arrays.asList(
         this.NIGHT_VISION,
         this.INVISIBILITY,
         this.VEIN_MINER,
         this.SELF_SUSTAIN,
         this.DASH,
         this.MEGA_JUMP,
         this.GHOST_WALK,
         this.RAMPAGE,
         this.CLEANSE,
         this.TANK,
         this.EXECUTE,
         this.SUMMON_ETERNAL
      );
   }

   public AbilityGroup<?> getByName(String name) {
      return this.getAll()
         .stream()
         .filter(group -> group.getParentName().equals(name))
         .findFirst()
         .orElseThrow(() -> new IllegalStateException("Unknown ability with name " + name));
   }

   public int cooldownOf(AbilityNode<?> abilityNode, PlayerEntity player) {
      AbilityGroup<?> abilityGroup = this.getByName(abilityNode.getGroup().getParentName());
      return abilityGroup.getAbility(abilityNode.getLevel()).getCooldown(player);
   }

   @Override
   protected void reset() {
      this.NIGHT_VISION = AbilityGroup.ofEffect("Night Vision", Effects.field_76439_r, EffectAbility.Type.ICON_ONLY, 1, i -> 1);
      this.INVISIBILITY = AbilityGroup.ofEffect("Invisibility", Effects.field_76441_p, EffectAbility.Type.ICON_ONLY, 1, i -> 1);
      this.GHOST_WALK = AbilityGroup.ofGhostWalkEffect("Ghost Walk", ModEffects.GHOST_WALK, EffectAbility.Type.ICON_ONLY, 6, i -> 1);
      this.RAMPAGE = AbilityGroup.ofRampage("Rampage", ModEffects.RAMPAGE, EffectAbility.Type.ICON_ONLY, 9, i -> 1);
      this.VEIN_MINER = new AbilityGroup<>(
         "Vein Miner",
         new VeinMinerAbility(1, 4),
         new VeinMinerAbility(1, 8),
         new VeinMinerAbility(1, 16),
         new VeinMinerAbility(2, 32),
         new VeinMinerAbility(2, 64)
      );
      this.SELF_SUSTAIN = new AbilityGroup<>("Self Sustain", new SelfSustainAbility(1, 1), new SelfSustainAbility(1, 2), new SelfSustainAbility(1, 4));
      this.DASH = new AbilityGroup<>(
         "Dash",
         new DashAbility(2, 1),
         new DashAbility(1, 2),
         new DashAbility(1, 3),
         new DashAbility(1, 4),
         new DashAbility(1, 5),
         new DashAbility(1, 6),
         new DashAbility(1, 7),
         new DashAbility(1, 8),
         new DashAbility(1, 9),
         new DashAbility(1, 10)
      );
      this.MEGA_JUMP = new AbilityGroup<>("Mega Jump", new MegaJumpAbility(1, 0), new MegaJumpAbility(1, 2), new MegaJumpAbility(1, 3));
      this.CLEANSE = new AbilityGroup<>(
         "Cleanse",
         new CleanseAbility(1, 600),
         new CleanseAbility(1, 540),
         new CleanseAbility(1, 500),
         new CleanseAbility(1, 460),
         new CleanseAbility(1, 400),
         new CleanseAbility(1, 360),
         new CleanseAbility(1, 320),
         new CleanseAbility(1, 280),
         new CleanseAbility(1, 240),
         new CleanseAbility(1, 200)
      );
      this.TANK = AbilityGroup.ofTank("Tank", ModEffects.TANK, EffectAbility.Type.ICON_ONLY, 5, i -> 3);
      this.EXECUTE = AbilityGroup.ofExecute("Execute", ModEffects.EXECUTE, EffectAbility.Type.ICON_ONLY, 5, i -> 1);
      this.SUMMON_ETERNAL = new AbilityGroup<>(
         "Summon Eternal",
         new SummonEternalAbility(1, 12000, 12000, true, 1),
         new SummonEternalAbility(1, 10800, 10800, true, 1),
         new SummonEternalAbility(1, 9600, 9600, false, 2),
         new SummonEternalAbility(1, 8400, 8400, false, 2),
         new SummonEternalAbility(1, 7200, 7200, false, 3)
      );
   }
}
