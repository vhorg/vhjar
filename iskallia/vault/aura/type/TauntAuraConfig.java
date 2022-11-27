package iskallia.vault.aura.type;

import com.google.gson.annotations.Expose;
import iskallia.vault.aura.ActiveAura;
import iskallia.vault.aura.EntityAuraProvider;
import iskallia.vault.config.EternalAuraConfig;
import iskallia.vault.util.EntityHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;

public class TauntAuraConfig extends EternalAuraConfig.AuraConfig {
   @Expose
   private final int tauntInterval;

   public TauntAuraConfig(int tauntInterval) {
      super("Taunt", "Taunt", "Periodically taunts enemies nearby", "taunt", 8.0F);
      this.tauntInterval = tauntInterval;
   }

   @Override
   public void onTick(Level world, ActiveAura aura) {
      super.onTick(world, aura);
      if (aura.getAuraProvider() instanceof EntityAuraProvider) {
         if (world.getGameTime() % this.tauntInterval == 0L) {
            LivingEntity auraProvider = ((EntityAuraProvider)aura.getAuraProvider()).getSource();
            EntityHelper.getNearby(world, new BlockPos(aura.getOffset()), aura.getRadius(), Mob.class).forEach(mob -> mob.setTarget(auraProvider));
         }
      }
   }
}
