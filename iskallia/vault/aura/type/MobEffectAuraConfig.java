package iskallia.vault.aura.type;

import com.google.gson.annotations.Expose;
import iskallia.vault.aura.ActiveAura;
import iskallia.vault.config.EternalAuraConfig;
import iskallia.vault.entity.EternalEntity;
import iskallia.vault.util.EntityHelper;
import net.minecraft.entity.MobEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

public class MobEffectAuraConfig extends EternalAuraConfig.AuraConfig {
   @Expose
   private final String effect;
   @Expose
   private final int amplifier;

   public MobEffectAuraConfig(Effect effect, int amplifier, String name, String icon) {
      super("Mob_" + name, name, "Applies " + name + " to enemies in its radius", icon, 8.0F);
      this.effect = effect.getRegistryName().toString();
      this.amplifier = amplifier;
   }

   @Override
   public void onTick(World world, ActiveAura aura) {
      super.onTick(world, aura);
      if (world.func_82737_E() % 20L == 0L) {
         Effect effect = (Effect)ForgeRegistries.POTIONS.getValue(new ResourceLocation(this.effect));
         if (effect != null) {
            EntityHelper.getNearby(world, new BlockPos(aura.getOffset()), aura.getRadius(), MobEntity.class).forEach(entity -> {
               if (!(entity instanceof EternalEntity)) {
                  entity.func_195064_c(new EffectInstance(effect, 259, this.amplifier, true, true));
               }
            });
         }
      }
   }
}
