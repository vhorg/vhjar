package iskallia.vault.skill.ability.effect;

import iskallia.vault.init.ModSounds;
import iskallia.vault.skill.ability.config.CleanseConfig;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.SoundCategory;

public class CleanseAbility<C extends CleanseConfig> extends AbilityEffect<C> {
   @Override
   public String getAbilityGroupName() {
      return "Cleanse";
   }

   public boolean onAction(C config, ServerPlayerEntity player, boolean active) {
      List<EffectInstance> effects = player.func_70651_bq().stream().filter(effect -> !effect.func_188419_a().func_188408_i()).collect(Collectors.toList());
      this.removeEffects(config, player, effects);
      player.field_70170_p
         .func_184148_a(
            player, player.func_226277_ct_(), player.func_226278_cu_(), player.func_226281_cx_(), ModSounds.CLEANSE_SFX, SoundCategory.MASTER, 0.7F, 1.0F
         );
      player.func_213823_a(ModSounds.CLEANSE_SFX, SoundCategory.MASTER, 0.7F, 1.0F);
      return true;
   }

   protected void removeEffects(C config, ServerPlayerEntity player, List<EffectInstance> effects) {
      effects.forEach(effect -> player.func_195063_d(effect.func_188419_a()));
   }
}
