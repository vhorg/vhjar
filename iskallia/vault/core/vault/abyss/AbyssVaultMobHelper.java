package iskallia.vault.core.vault.abyss;

import iskallia.vault.core.vault.Vault;
import iskallia.vault.init.ModConfigs;
import java.util.UUID;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class AbyssVaultMobHelper {
   private static final UUID ID_DAMAGE_MODIFIER = UUID.fromString("ba0400aa-22a9-4082-9d5a-4d169e6e6cb8");
   private static final UUID ID_HEALTH_MODIFIER = UUID.fromString("168b090d-1a20-4ca3-918b-3ec2cbef35d3");
   private static final UUID ID_KB_MODIFIER = UUID.fromString("a8146de7-c48c-4a25-8e71-e4095a25bef8");

   public static void scaleAbyssal(Vault vault, LivingEntity entity) {
      if (AbyssHelper.hasAbyssEffect(vault)) {
         float effect = AbyssHelper.getAbyssEffect(vault) * AbyssHelper.getAbyssDistanceModifier(entity.blockPosition(), vault);
         AttributeInstance dmgInstance = entity.getAttribute(Attributes.ATTACK_DAMAGE);
         if (dmgInstance != null) {
            dmgInstance.addPermanentModifier(
               new AttributeModifier(ID_DAMAGE_MODIFIER, "Abyss Damage Modifier", effect * ModConfigs.ABYSS.getMobDamageIncrease(), Operation.MULTIPLY_BASE)
            );
         }

         AttributeInstance healthInstance = entity.getAttribute(Attributes.MAX_HEALTH);
         if (healthInstance != null) {
            healthInstance.addPermanentModifier(
               new AttributeModifier(ID_HEALTH_MODIFIER, "Abyss Health Modifier", effect * ModConfigs.ABYSS.getMobHealthIncrease(), Operation.MULTIPLY_BASE)
            );
         }

         AttributeInstance knockBackInstance = entity.getAttribute(Attributes.MAX_HEALTH);
         if (knockBackInstance != null) {
            knockBackInstance.addPermanentModifier(
               new AttributeModifier(ID_KB_MODIFIER, "Abyss KnockBack Modifier", effect * ModConfigs.ABYSS.getMobKnockBackResistance(), Operation.ADDITION)
            );
         }
      }
   }
}
