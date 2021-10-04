package iskallia.vault.init;

import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Potion;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModPotions {
   public static final DeferredRegister<Potion> REGISTRY = DeferredRegister.create(ForgeRegistries.POTION_TYPES, "the_vault");
   public static RegistryObject<Potion> TIME_ACCELERATION_X2 = REGISTRY.register(
      "time_acceleration_x2", () -> new Potion(new EffectInstance[]{new EffectInstance(ModEffects.TIMER_ACCELERATION, 200, 1)})
   );
   public static RegistryObject<Potion> TIME_ACCELERATION_X3 = REGISTRY.register(
      "time_acceleration_x3", () -> new Potion(new EffectInstance[]{new EffectInstance(ModEffects.TIMER_ACCELERATION, 200, 2)})
   );
   public static RegistryObject<Potion> TIME_ACCELERATION_X4 = REGISTRY.register(
      "time_acceleration_x4", () -> new Potion(new EffectInstance[]{new EffectInstance(ModEffects.TIMER_ACCELERATION, 200, 3)})
   );
}
