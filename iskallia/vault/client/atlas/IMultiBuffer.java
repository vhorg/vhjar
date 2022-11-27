package iskallia.vault.client.atlas;

import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;

public interface IMultiBuffer {
   void begin();

   VertexConsumer getFor(ResourceLocation var1) throws IllegalStateException;

   void end(Function<Buffer, Supplier<ShaderInstance>> var1);
}
