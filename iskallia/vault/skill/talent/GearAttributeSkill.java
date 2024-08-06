package iskallia.vault.skill.talent;

import iskallia.vault.gear.VaultGearHelper;
import iskallia.vault.gear.attribute.VaultGearAttributeInstance;
import iskallia.vault.skill.base.Skill;
import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.snapshot.AttributeSnapshotHelper;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Stream;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public interface GearAttributeSkill {
   Stream<VaultGearAttributeInstance<?>> getGearAttributes(SkillContext var1);

   default UUID getUuid() {
      return this instanceof Skill skill && skill.getId() != null
         ? Mth.createInsecureUUID(new Random(skill.getId().hashCode()))
         : Mth.createInsecureUUID(new Random(this.getClass().getSimpleName().hashCode()));
   }

   default void onAddModifiers(SkillContext context) {
      context.getSource()
         .as(LivingEntity.class)
         .ifPresent(entity -> VaultGearHelper.getModifiers(this.getUuid(), this.getGearAttributes(context)).forEach((attribute, modifier) -> {
            AttributeInstance present = entity.getAttribute(attribute);
            if (present != null) {
               AttributeModifier current = present.getModifier(modifier.getId());
               if (current == null || current.getAmount() != modifier.getAmount() || current.getOperation() != modifier.getOperation()) {
                  present.removeModifier(modifier.getId());
                  present.addTransientModifier(modifier);
               }
            }
         }));
   }

   default void onRemoveModifiers(SkillContext context) {
      context.getSource()
         .as(LivingEntity.class)
         .ifPresent(entity -> VaultGearHelper.getModifiers(this.getUuid(), this.getGearAttributes(context)).forEach((attribute, modifier) -> {
            AttributeInstance present = entity.getAttribute(attribute);
            if (present != null) {
               present.removeModifier(modifier.getId());
            }
         }));
   }

   default void refreshSnapshot(ServerPlayer player) {
      AttributeSnapshotHelper.getInstance().refreshSnapshotDelayed(player);
   }
}
