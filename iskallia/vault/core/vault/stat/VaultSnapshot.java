package iskallia.vault.core.vault.stat;

import iskallia.vault.core.Version;
import iskallia.vault.core.data.sync.context.DiskSyncContext;
import iskallia.vault.core.net.ArrayBitBuffer;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.item.crystal.data.serializable.IBitSerializable;

public class VaultSnapshot implements IBitSerializable {
   private Version version;
   private Vault start;
   private Vault end;
   private long[] cache;

   public VaultSnapshot(Version version) {
      this.version = version;
   }

   public VaultSnapshot(Version version, Vault start, Vault end) {
      this.version = version;
      this.start = start;
      this.end = end;
   }

   public VaultSnapshot(BitBuffer buffer) {
      this.readBits(buffer);
   }

   public Version getVersion() {
      return this.version;
   }

   public Vault getStart() {
      return this.start;
   }

   public Vault getEnd() {
      return this.end;
   }

   public long[] getCache() {
      this.compute();
      return this.cache;
   }

   private void compute() {
      if (this.cache == null) {
         ArrayBitBuffer buffer = ArrayBitBuffer.empty();
         buffer.writeEnum(this.version);
         buffer.writeBoolean(this.start != null);
         if (this.start != null) {
            this.start.write(buffer, new DiskSyncContext(this.version));
         }

         buffer.writeBoolean(this.end != null);
         if (this.end != null) {
            this.end.write(buffer, new DiskSyncContext(this.version));
         }

         this.cache = buffer.toLongArray();
      }
   }

   public VaultSnapshot setStart(Vault start) {
      this.start = new Vault();
      ArrayBitBuffer buffer = ArrayBitBuffer.empty();
      start.write(buffer, new DiskSyncContext(this.version));
      buffer.setPosition(0);
      this.start.read(buffer, new DiskSyncContext(this.version));
      this.cache = null;
      return this;
   }

   public VaultSnapshot setEnd(Vault end) {
      this.end = new Vault();
      ArrayBitBuffer buffer = ArrayBitBuffer.empty();
      end.write(buffer, new DiskSyncContext(this.version));
      buffer.setPosition(0);
      this.end.read(buffer, new DiskSyncContext(this.version));
      this.cache = null;
      return this;
   }

   public boolean matches(Vault other) {
      return this.start == null ? false : this.start.get(Vault.ID).equals(other.get(Vault.ID));
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      this.compute();

      for (long l : this.cache) {
         buffer.writeLong(l);
      }
   }

   @Override
   public void readBits(BitBuffer buffer) {
      this.version = buffer.readEnum(Version.class);
      if (buffer.readBoolean()) {
         this.start = new Vault().read(buffer, new DiskSyncContext(this.version));
      }

      if (buffer.readBoolean()) {
         this.end = new Vault().read(buffer, new DiskSyncContext(this.version));
      }
   }
}
