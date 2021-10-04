package iskallia.vault.init;

import iskallia.vault.fluid.VoidFluid;
import net.minecraft.fluid.Fluid;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModFluids {
   public static final DeferredRegister<Fluid> REGISTRY = DeferredRegister.create(ForgeRegistries.FLUIDS, "the_vault");
   public static final RegistryObject<VoidFluid.Source> VOID_LIQUID = REGISTRY.register("void_liquid", VoidFluid.Source::new);
   public static final RegistryObject<VoidFluid.Flowing> FLOWING_VOID_LIQUID = REGISTRY.register("flowing_void_liquid", VoidFluid.Flowing::new);
}
