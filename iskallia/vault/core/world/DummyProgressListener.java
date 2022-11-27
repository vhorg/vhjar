package iskallia.vault.core.world;

import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkStatus;
import org.jetbrains.annotations.Nullable;

public class DummyProgressListener implements ChunkProgressListener {
   public void updateSpawnPos(ChunkPos center) {
   }

   public void onStatusChange(ChunkPos chunkPos, @Nullable ChunkStatus status) {
   }

   public void start() {
   }

   public void stop() {
   }
}
