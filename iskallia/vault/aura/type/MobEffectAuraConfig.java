package iskallia.vault.aura.type;

import com.google.gson.annotations.Expose;
import iskallia.vault.aura.ActiveAura;
import iskallia.vault.config.EternalAuraConfig;
import iskallia.vault.entity.entity.EternalEntity;
import iskallia.vault.util.EntityHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

public class MobEffectAuraConfig extends EternalAuraConfig.AuraConfig {
   @Expose
   private final String effect;
   @Expose
   private final int amplifier;

   public MobEffectAuraConfig(MobEffect effect, int amplifier, String name, String icon) {
      super("Mob_" + name, name, "Applies " + name + " to enemies in its radius", icon, 8.0F);
      this.effect = effect.getRegistryName().toString();
      this.amplifier = amplifier;
   }

   @Override
   public void onTick(Level world, ActiveAura aura) {
      super.onTick(world, aura);
      if (world.getGameTime() % 20L == 0L) {
         MobEffect effect = (MobEffect)ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(this.effect));
         if (effect != null) {
            EntityHelper.getNearby(world, new BlockPos(aura.getOffset()), aura.getRadius(), Mob.class).forEach(entity -> {
               if (!(entity instanceof EternalEntity)) {
                  entity.addEffect(new MobEffectInstance(effect, 259, this.amplifier, true, true));
               }
            });
         }
      }
   }
}
