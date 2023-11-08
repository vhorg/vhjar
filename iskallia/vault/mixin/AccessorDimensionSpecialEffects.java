package iskallia.vault.mixin;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({DimensionSpecialEffects.class})
public interface AccessorDimensionSpecialEffects {
   @Accessor("EFFECTS")
   static Object2ObjectMap<ResourceLocation, DimensionSpecialEffects> getEffects() {
      throw new IllegalStateException("Mixin failed to apply");
   }
}
