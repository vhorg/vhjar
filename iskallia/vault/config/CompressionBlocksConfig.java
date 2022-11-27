package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.config.entry.CompressionEntry;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.level.block.Blocks;

public class CompressionBlocksConfig extends Config {
   @Expose
   public List<CompressionEntry> compressions;

   @Override
   public String getName() {
      return "compression_blocks";
   }

   public List<CompressionEntry> getCompressions() {
      return this.compressions;
   }

   @Override
   protected void reset() {
      this.compressions = new ArrayList<>();
      this.compressions.add(new CompressionEntry(Blocks.COBBLESTONE.getRegistryName().toString(), "compressed_cobblestone"));
   }
}
