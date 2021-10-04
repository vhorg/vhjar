package iskallia.vault.aura;

import iskallia.vault.config.EternalAuraConfig;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.vector.Vector3d;

public class EntityAuraProvider extends AuraProvider {
   private final EternalAuraConfig.AuraConfig aura;
   private final LivingEntity entity;

   protected EntityAuraProvider(LivingEntity entity, EternalAuraConfig.AuraConfig aura) {
      super(entity.func_110124_au(), entity.func_130014_f_().func_234923_W_());
      this.aura = aura;
      this.entity = entity;
   }

   public static EntityAuraProvider ofEntity(LivingEntity entity, EternalAuraConfig.AuraConfig aura) {
      return new EntityAuraProvider(entity, aura);
   }

   public LivingEntity getSource() {
      return this.entity;
   }

   public LivingEntity getTrueSource() {
      return this.getSource();
   }

   @Override
   public boolean isValid() {
      return this.entity.func_70089_S();
   }

   @Override
   public Vector3d getLocation() {
      return new Vector3d(this.entity.func_226277_ct_(), this.entity.func_226278_cu_(), this.entity.func_226281_cx_());
   }

   @Override
   public EternalAuraConfig.AuraConfig getAura() {
      return this.aura;
   }
}
