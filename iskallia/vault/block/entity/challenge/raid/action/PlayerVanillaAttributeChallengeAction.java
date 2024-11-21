package iskallia.vault.block.entity.challenge.raid.action;

import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;

public class PlayerVanillaAttributeChallengeAction extends VanillaAttributeChallengeAction {
   @Override
   public void onAddPlayer(Player player) {
      AttributeInstance attribute = player.getAttribute(this.getConfig().attribute);
      if (attribute != null) {
         attribute.removeModifier(this.uuid);
         attribute.addTransientModifier(new AttributeModifier(this.uuid, "Raid Modifier", this.amount, this.getConfig().operation));
      }
   }

   @Override
   public void onRemovePlayer(Player player) {
      AttributeInstance attribute = player.getAttribute(this.getConfig().attribute);
      if (attribute != null) {
         attribute.removeModifier(this.uuid);
      }
   }
}
