package iskallia.vault.item.bottle;

import iskallia.vault.client.ClientAbilityData;
import iskallia.vault.mana.FullManaPlayer;
import iskallia.vault.skill.ability.effect.spi.core.Ability;
import iskallia.vault.skill.ability.effect.spi.core.InstantAbility;
import iskallia.vault.skill.base.Skill;
import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.skill.base.SpecializedSkill;
import iskallia.vault.skill.base.TieredSkill;
import iskallia.vault.skill.source.SkillSource;
import iskallia.vault.skill.tree.AbilityTree;
import iskallia.vault.world.data.PlayerAbilitiesData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class CastAbilityBottleEffect extends BottleEffect {
   public static final String TYPE = "cast_ability";
   private final String abilityId;

   public CastAbilityBottleEffect(String effectId, String abilityId) {
      super(effectId);
      this.abilityId = abilityId;
   }

   @Override
   protected String getType() {
      return "cast_ability";
   }

   @Override
   protected void trigger(ServerPlayer player) {
      AbilityTree tree = PlayerAbilitiesData.get((ServerLevel)player.level).getAbilities(player);
      tree.getForId(this.abilityId).ifPresent(skill -> {
         Skill var3 = skill instanceof SpecializedSkill specialized ? specialized.getSpecialization() : skill;
         if ((var3 instanceof TieredSkill tiered ? tiered.getChild() : var3) instanceof InstantAbility instantAbility) {
            instantAbility.onAction(SkillContext.of(player, SkillSource.of(player).setPos(player.position()).setMana(FullManaPlayer.INSTANCE)));
         }
      });
   }

   @Override
   public CompoundTag serializeData(CompoundTag tag) {
      tag.putString("abilityId", this.abilityId);
      return tag;
   }

   @Override
   protected String getTooltipText(String tooltipFormat) {
      return ClientAbilityData.getTree()
         .getForId(this.abilityId)
         .map(
            skill -> skill instanceof Ability ability && ability.getParent() instanceof TieredSkill tiered
               ? String.format(tooltipFormat, tiered.getName(), tiered.getTierOf(this.abilityId))
               : tooltipFormat
         )
         .orElse(tooltipFormat);
   }

   public static CastAbilityBottleEffect deserialize(String effectId, CompoundTag tag) {
      return new CastAbilityBottleEffect(effectId, tag.getString("abilityId"));
   }
}
