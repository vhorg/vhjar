package iskallia.vault.init;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModPotions {
   public static final DeferredRegister<Potion> REGISTRY = DeferredRegister.create(ForgeRegistries.POTIONS, "the_vault");
   public static RegistryObject<Potion> TIME_ACCELERATION_X2 = REGISTRY.register(
      "time_acceleration_x2", () -> new Potion(new MobEffectInstance[]{new MobEffectInstance(ModEffects.TIMER_ACCELERATION, 200, 1)})
   );
   public static RegistryObject<Potion> TIME_ACCELERATION_X3 = REGISTRY.register(
      "time_acceleration_x3", () -> new Potion(new MobEffectInstance[]{new MobEffectInstance(ModEffects.TIMER_ACCELERATION, 200, 2)})
   );
   public static RegistryObject<Potion> TIME_ACCELERATION_X4 = REGISTRY.register(
      "time_acceleration_x4", () -> new Potion(new MobEffectInstance[]{new MobEffectInstance(ModEffects.TIMER_ACCELERATION, 200, 3)})
   );
}
