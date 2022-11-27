package iskallia.vault.mixin;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({StructureTemplatePool.class})
public class MixinStructureTemplatePool {
   @Shadow
   @Mutable
   @Final
   public static Codec<StructureTemplatePool> DIRECT_CODEC;

   @Inject(
      method = {"<clinit>"},
      at = {@At("RETURN")}
   )
   private static void test(CallbackInfo ci) {
      DIRECT_CODEC = RecordCodecBuilder.create(
         builder -> builder.group(
               ResourceLocation.CODEC.fieldOf("name").forGetter(StructureTemplatePool::getName),
               ResourceLocation.CODEC.fieldOf("fallback").forGetter(StructureTemplatePool::getFallback),
               Codec.mapPair(StructurePoolElement.CODEC.fieldOf("element"), Codec.INT.fieldOf("weight"))
                  .codec()
                  .listOf()
                  .fieldOf("elements")
                  .forGetter(pool -> pool.rawTemplates)
            )
            .apply(builder, StructureTemplatePool::new)
      );
   }
}
