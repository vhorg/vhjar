package iskallia.vault.skill.ability.type;

import com.google.gson.annotations.Expose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.EntityDamageSource;

public class SelfSustainAbility extends PlayerAbility {
   @Expose
   private final int sustain;

   public SelfSustainAbility(int cost, int sustain) {
      super(cost, PlayerAbility.Behavior.RELEASE_TO_PERFORM);
      this.sustain = sustain;
   }

   public int getSustain() {
      return this.sustain;
   }

   @Override
   public void onAction(PlayerEntity player, boolean active) {
      float health = player.func_110143_aJ();
      player.func_70097_a(EntityDamageSource.func_76365_a(player), 1.0F);
      player.func_70606_j(health - this.sustain);
      player.func_71024_bL().func_75122_a(this.sustain, this.sustain / 5.0F);
   }
}
