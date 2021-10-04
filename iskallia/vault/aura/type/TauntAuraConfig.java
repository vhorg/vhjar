package iskallia.vault.aura.type;

import com.google.gson.annotations.Expose;
import iskallia.vault.aura.ActiveAura;
import iskallia.vault.aura.EntityAuraProvider;
import iskallia.vault.config.EternalAuraConfig;
import iskallia.vault.util.EntityHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TauntAuraConfig extends EternalAuraConfig.AuraConfig {
   @Expose
   private final int tauntInterval;

   public TauntAuraConfig(int tauntInterval) {
      super("Taunt", "Taunt", "Periodically taunts enemies nearby", "taunt", 8.0F);
      this.tauntInterval = tauntInterval;
   }

   @Override
   public void onTick(World world, ActiveAura aura) {
      super.onTick(world, aura);
      if (aura.getAuraProvider() instanceof EntityAuraProvider) {
         if (world.func_82737_E() % this.tauntInterval == 0L) {
            LivingEntity auraProvider = ((EntityAuraProvider)aura.getAuraProvider()).getSource();
            EntityHelper.getNearby(world, new BlockPos(aura.getOffset()), aura.getRadius(), MobEntity.class).forEach(mob -> mob.func_70624_b(auraProvider));
         }
      }
   }
}
