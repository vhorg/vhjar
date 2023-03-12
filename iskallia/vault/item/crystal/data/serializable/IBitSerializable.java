package iskallia.vault.item.crystal.data.serializable;

import iskallia.vault.core.net.BitBuffer;

public interface IBitSerializable {
   void writeBits(BitBuffer var1);

   void readBits(BitBuffer var1);
}
