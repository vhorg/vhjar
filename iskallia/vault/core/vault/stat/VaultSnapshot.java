package iskallia.vault.core.vault.stat;

import iskallia.vault.core.Version;
import iskallia.vault.core.data.sync.context.DiskSyncContext;
import iskallia.vault.core.net.ArrayBitBuffer;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.core.net.IBitSerializable;
import iskallia.vault.core.vault.Vault;

public class VaultSnapshot implements IBitSerializable {
   private Version version;
   private Vault start;
   private Vault end;

   public VaultSnapshot(Version version) {
      this.version = version;
   }

   public VaultSnapshot(Version version, Vault start, Vault end) {
      this.version = version;
      this.start = start;
      this.end = end;
   }

   public VaultSnapshot(BitBuffer buffer) {
      this.read(buffer);
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

   public VaultSnapshot setStart(Vault start) {
      this.start = new Vault();
      ArrayBitBuffer buffer = ArrayBitBuffer.empty();
      start.write(buffer, new DiskSyncContext(this.version));
      buffer.setPosition(0);
      this.start.read(buffer, new DiskSyncContext(this.version));
      return this;
   }

   public VaultSnapshot setEnd(Vault end) {
      this.end = new Vault();
      ArrayBitBuffer buffer = ArrayBitBuffer.empty();
      end.write(buffer, new DiskSyncContext(this.version));
      buffer.setPosition(0);
      this.end.read(buffer, new DiskSyncContext(this.version));
      return this;
   }

   public boolean matches(Vault other) {
      return this.start == null ? false : this.start.get(Vault.ID).equals(other.get(Vault.ID));
   }

   @Override
   public void write(BitBuffer buffer) {
      buffer.writeEnum(this.version);
      buffer.writeBoolean(this.start != null);
      if (this.start != null) {
         this.start.write(buffer, new DiskSyncContext(this.version));
      }

      buffer.writeBoolean(this.end != null);
      if (this.end != null) {
         this.end.write(buffer, new DiskSyncContext(this.version));
      }
   }

   @Override
   public void read(BitBuffer buffer) {
      this.version = buffer.readEnum(Version.class);
      if (buffer.readBoolean()) {
         this.start = new Vault().read(buffer, new DiskSyncContext(this.version));
      }

      if (buffer.readBoolean()) {
         this.end = new Vault().read(buffer, new DiskSyncContext(this.version));
      }
   }
}
