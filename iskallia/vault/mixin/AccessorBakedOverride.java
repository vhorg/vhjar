package iskallia.vault.mixin;

import net.minecraft.client.renderer.block.model.ItemOverrides.BakedOverride;
import net.minecraft.client.resources.model.BakedModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({BakedOverride.class})
public interface AccessorBakedOverride {
   @Accessor("model")
   BakedModel getModel();
}
