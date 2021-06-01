package iskallia.vault.skill.ability.type;

import iskallia.vault.init.ModSounds;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.SoundCategory;

public class CleanseAbility extends PlayerAbility {
   public CleanseAbility(int cost, int cooldown) {
      super(cost, PlayerAbility.Behavior.RELEASE_TO_PERFORM);
      this.cooldown = cooldown;
   }

   @Override
   public void onAction(PlayerEntity player, boolean active) {
      List<Effect> effects = player.func_70651_bq()
         .stream()
         .<Effect>map(EffectInstance::func_188419_a)
         .filter(effect -> !effect.func_188408_i())
         .collect(Collectors.toList());
      effects.forEach(player::func_195063_d);
      player.field_70170_p
         .func_184148_a(
            player, player.func_226277_ct_(), player.func_226278_cu_(), player.func_226281_cx_(), ModSounds.CLEANSE_SFX, SoundCategory.MASTER, 0.7F, 1.0F
         );
      player.func_213823_a(ModSounds.CLEANSE_SFX, SoundCategory.MASTER, 0.7F, 1.0F);
   }
}
