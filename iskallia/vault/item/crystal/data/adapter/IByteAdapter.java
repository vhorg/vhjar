package iskallia.vault.item.crystal.data.adapter;

import io.netty.buffer.ByteBuf;
import java.util.Optional;
import javax.annotation.Nullable;

public interface IByteAdapter<T, C> {
   void writeBytes(@Nullable T var1, ByteBuf var2, C var3);

   Optional<T> readBytes(ByteBuf var1, C var2);
}
