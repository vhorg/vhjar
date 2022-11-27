package iskallia.vault.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public abstract class MixinChunkGenerator {
   public void generate(WorldGenLevel world, ChunkAccess chunk, StructureFeatureManager manager, CallbackInfo ci) {
      new BlockPos(chunk.getPos().x * 16, 0, chunk.getPos().z * 16);
   }
}
