package iskallia.vault.item.bottle;

import iskallia.vault.skill.ability.effect.spi.core.Ability;
import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.skill.tree.AbilityTree;
import iskallia.vault.world.data.PlayerAbilitiesData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class CooldownReductionBottleEffect extends BottleEffect {
   public static final String TYPE = "cooldown_reduction";
   private final float amount;

   public CooldownReductionBottleEffect(String effectId, float amount) {
      super(effectId);
      this.amount = amount;
   }

   @Override
   public String getType() {
      return "cooldown_reduction";
   }

   @Override
   public void trigger(ServerPlayer player) {
      PlayerAbilitiesData abilitiesData = PlayerAbilitiesData.get((ServerLevel)player.level);
      AbilityTree abilityTree = abilitiesData.getAbilities(player);
      abilityTree.iterate(
         Ability.class, ability -> ability.getCooldown().ifPresent(cooldown -> ability.reduceCooldownBy((int)(cooldown.getRemainingTicks() * this.amount)))
      );
      abilityTree.sync(SkillContext.of(player));
   }

   @Override
   public CompoundTag serializeData(CompoundTag tag) {
      tag.putFloat("amount", this.amount);
      return tag;
   }

   @Override
   public String getTooltipText(String tooltipFormat) {
      return String.format(tooltipFormat, (int)(100.0F * this.amount));
   }

   public static BottleEffect deserialize(String effectId, CompoundTag tag) {
      return new CooldownReductionBottleEffect(effectId, tag.getFloat("amount"));
   }
}
