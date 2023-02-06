package iskallia.vault.mixin;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({LightTexture.class})
public interface AccessorLightTexture {
   @Accessor("lightPixels")
   NativeImage getLightPixels();

   @Accessor("lightTexture")
   DynamicTexture getLightTexture();

   @Accessor("updateLightTexture")
   void setUpdateLightTexture(boolean var1);
}
