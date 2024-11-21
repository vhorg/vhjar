package iskallia.vault.block.entity.challenge.raid.action;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class MobVanillaAttributeChallengeAction extends VanillaAttributeChallengeAction {
   @Override
   public void onSummonMob(Entity entity) {
      if (entity instanceof LivingEntity living) {
         AttributeInstance attribute = living.getAttribute(this.getConfig().attribute);
         if (attribute != null) {
            attribute.removeModifier(this.uuid);
            attribute.addPermanentModifier(new AttributeModifier(this.uuid, "Raid Modifier", this.amount, this.getConfig().operation));
         }
      }
   }
}
