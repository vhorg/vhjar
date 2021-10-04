package iskallia.vault.skill.ability.effect.sub;

import iskallia.vault.entity.EternalEntity;
import iskallia.vault.skill.ability.config.sub.SummonEternalDamageConfig;
import iskallia.vault.skill.ability.effect.SummonEternalAbility;
import java.util.UUID;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;

public class SummonEternalDamageAbility extends SummonEternalAbility<SummonEternalDamageConfig> {
   private static final UUID INCREASED_DAMAGE_MOD_UUID = UUID.fromString("68ab19f2-a345-49ed-b5c4-0746d8508685");

   protected void postProcessEternal(EternalEntity eternalEntity, SummonEternalDamageConfig config) {
      super.postProcessEternal(eternalEntity, config);
      ModifiableAttributeInstance instance = eternalEntity.func_110148_a(Attributes.field_233823_f_);
      instance.func_233767_b_(
         new AttributeModifier(INCREASED_DAMAGE_MOD_UUID, "Eternal increased damage", config.getIncreasedDamagePercent(), Operation.MULTIPLY_BASE)
      );
      eternalEntity.sizeMultiplier *= 1.2F;
   }
}
