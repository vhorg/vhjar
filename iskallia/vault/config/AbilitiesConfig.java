package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.VaultMod;
import iskallia.vault.skill.ability.AbilityNode;
import iskallia.vault.skill.ability.group.AbilityGroup;
import iskallia.vault.skill.ability.group.DashAbilityGroup;
import iskallia.vault.skill.ability.group.ExecuteAbilityGroup;
import iskallia.vault.skill.ability.group.FarmerAbilityGroup;
import iskallia.vault.skill.ability.group.GhostWalkAbilityGroup;
import iskallia.vault.skill.ability.group.HealAbilityGroup;
import iskallia.vault.skill.ability.group.HunterAbilityGroup;
import iskallia.vault.skill.ability.group.ManaShieldAbilityGroup;
import iskallia.vault.skill.ability.group.MegaJumpAbilityGroup;
import iskallia.vault.skill.ability.group.NovaAbilityGroup;
import iskallia.vault.skill.ability.group.RampageAbilityGroup;
import iskallia.vault.skill.ability.group.SummonEternalAbilityGroup;
import iskallia.vault.skill.ability.group.TankAbilityGroup;
import iskallia.vault.skill.ability.group.TauntAbilityGroup;
import iskallia.vault.skill.ability.group.VeinMinerAbilityGroup;
import iskallia.vault.util.calc.CooldownHelper;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import net.minecraft.server.level.ServerPlayer;

public class AbilitiesConfig extends Config {
   @Expose
   public HealAbilityGroup HEAL;
   @Expose
   public DashAbilityGroup DASH;
   @Expose
   public NovaAbilityGroup NOVA;
   @Expose
   public ExecuteAbilityGroup EXECUTE;
   @Expose
   public GhostWalkAbilityGroup GHOST_WALK;
   @Expose
   public MegaJumpAbilityGroup MEGA_JUMP;
   @Expose
   public RampageAbilityGroup RAMPAGE;
   @Expose
   public SummonEternalAbilityGroup SUMMON_ETERNAL;
   @Expose
   public TankAbilityGroup TANK;
   @Expose
   public VeinMinerAbilityGroup VEIN_MINER;
   @Expose
   public HunterAbilityGroup HUNTER;
   @Expose
   public FarmerAbilityGroup FARMER;
   @Expose
   public ManaShieldAbilityGroup MANA_SHIELD;
   @Expose
   public TauntAbilityGroup TAUNT;

   @Override
   public String getName() {
      return "abilities";
   }

   public List<AbilityGroup<?, ?>> getAll() {
      return Arrays.asList(
         this.VEIN_MINER,
         this.HEAL,
         this.DASH,
         this.NOVA,
         this.MEGA_JUMP,
         this.GHOST_WALK,
         this.RAMPAGE,
         this.TANK,
         this.EXECUTE,
         this.SUMMON_ETERNAL,
         this.HUNTER,
         this.FARMER,
         this.MANA_SHIELD,
         this.TAUNT
      );
   }

   public AbilityGroup<?, ?> getAbilityGroupByName(String name) {
      return this.getAll()
         .stream()
         .filter(group -> group.getParentName().equals(name))
         .findFirst()
         .orElseThrow(() -> new IllegalStateException("Unknown ability with name " + name));
   }

   public Optional<AbilityGroup<?, ?>> getAbility(String name) {
      return this.getAll().stream().filter(group -> group.getParentName().equals(name)).findFirst();
   }

   public int getCooldown(AbilityNode<?, ?> abilityNode, ServerPlayer player) {
      return CooldownHelper.adjustCooldown(player, abilityNode);
   }

   @Override
   protected void reset() {
      this.HEAL = HealAbilityGroup.defaultConfig();
      this.DASH = DashAbilityGroup.defaultConfig();
      this.NOVA = NovaAbilityGroup.defaultConfig();
      this.EXECUTE = ExecuteAbilityGroup.defaultConfig();
      this.GHOST_WALK = GhostWalkAbilityGroup.defaultConfig();
      this.MEGA_JUMP = MegaJumpAbilityGroup.defaultConfig();
      this.RAMPAGE = RampageAbilityGroup.defaultConfig();
      this.SUMMON_ETERNAL = SummonEternalAbilityGroup.defaultConfig();
      this.TANK = TankAbilityGroup.defaultConfig();
      this.VEIN_MINER = VeinMinerAbilityGroup.defaultConfig();
      this.HUNTER = HunterAbilityGroup.defaultConfig();
      this.FARMER = FarmerAbilityGroup.defaultConfig();
      this.MANA_SHIELD = ManaShieldAbilityGroup.defaultConfig();
      this.TAUNT = TauntAbilityGroup.defaultConfig();
   }

   @Override
   protected boolean isValid() {
      boolean valid = true;

      for (AbilityGroup<?, ?> abilityGroup : this.getAll()) {
         boolean configurationValid = abilityGroup.isConfigurationValid();
         valid = valid && configurationValid;
         if (!configurationValid) {
            VaultMod.LOGGER
               .error("Ability: Mismatch between number of configured levels and specialization levels for %s".formatted(abilityGroup.getParentName()));
         }
      }

      return valid;
   }
}
