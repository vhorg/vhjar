package iskallia.vault.skill.ability.effect.spi.core;

import iskallia.vault.effect.InfiniteDurationEffect;
import iskallia.vault.world.data.PlayerAbilitiesData;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;

public abstract class ToggleAbilityEffect extends InfiniteDurationEffect {
   private final String abilityGroup;

   protected ToggleAbilityEffect(String abilityGroup, int color, ResourceLocation resourceLocation) {
      super(MobEffectCategory.BENEFICIAL, color, resourceLocation);
      this.abilityGroup = abilityGroup;
   }

   public String getAbilityGroup() {
      return this.abilityGroup;
   }

   @ParametersAreNonnullByDefault
   public void removeAttributeModifiers(LivingEntity livingEntity, AttributeMap attributeMap, int amplifier) {
      super.removeAttributeModifiers(livingEntity, attributeMap, amplifier);
      if (livingEntity instanceof ServerPlayer player) {
         this.removeAttributeModifiers(player, attributeMap, amplifier);
      }
   }

   protected void removeAttributeModifiers(ServerPlayer player, AttributeMap attributeMap, int amplifier) {
      if (!player.hasEffect(this)) {
         PlayerAbilitiesData.setAbilityOnCooldown(player, this.abilityGroup);
      }
   }
}
