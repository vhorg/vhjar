package iskallia.vault.mixin;

import net.minecraft.client.renderer.texture.TextureAtlasSprite.Info;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({Info.class})
public interface AccessorInfo {
   @Accessor("name")
   ResourceLocation getName();

   @Accessor("width")
   int getWidth();

   @Accessor("height")
   int getHeight();

   @Accessor("metadata")
   AnimationMetadataSection getMetaData();
}
