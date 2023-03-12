package iskallia.vault.item.crystal.data.adapter;

import iskallia.vault.core.net.BitBuffer;
import java.util.Optional;
import javax.annotation.Nullable;

public interface IBitAdapter<T, C> {
   void writeBits(@Nullable T var1, BitBuffer var2, C var3);

   Optional<T> readBits(BitBuffer var1, C var2);
}
