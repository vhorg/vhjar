package iskallia.vault.dynamodel.model.item.shield;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

public class BellShieldModel extends ShieldModel {
   public BellShieldModel(ResourceLocation id, String displayName) {
      super(id, displayName);
   }

   @Override
   public void onBlocked(LivingEntity attacked, DamageSource damageSource) {
      attacked.getLevel().playSound(null, attacked.getOnPos(), SoundEvents.BELL_BLOCK, SoundSource.BLOCKS, 2.0F, 1.0F);
   }
}
