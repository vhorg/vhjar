package iskallia.vault.config.entry;

import com.google.gson.annotations.Expose;

public class CompressionEntry {
   @Expose
   String targetItemId;
   @Expose
   String compressionId;

   public CompressionEntry(String targetItemId, String compressionId) {
      this.targetItemId = targetItemId;
      this.compressionId = compressionId;
   }

   public String getTargetItemId() {
      return this.targetItemId;
   }

   public String getCompressionId() {
      return this.compressionId;
   }
}
