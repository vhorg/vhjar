package iskallia.vault.effect;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   modid = "the_vault",
   bus = Bus.FORGE
)
public class ChilledEffect extends MobEffect {
   public ChilledEffect(MobEffectCategory typeIn, int liquidColorIn, ResourceLocation id) {
      super(typeIn, liquidColorIn);
      this.setRegistryName(id);
   }
}
