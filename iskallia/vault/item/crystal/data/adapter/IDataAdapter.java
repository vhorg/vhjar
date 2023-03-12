package iskallia.vault.item.crystal.data.adapter;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Optional;
import javax.annotation.Nullable;

public interface IDataAdapter<T, C> {
   void writeData(@Nullable T var1, DataOutput var2, C var3) throws IOException;

   Optional<T> readData(DataInput var1, C var2) throws IOException;
}
