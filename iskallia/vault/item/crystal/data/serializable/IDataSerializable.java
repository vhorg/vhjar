package iskallia.vault.item.crystal.data.serializable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public interface IDataSerializable {
   void writeData(DataOutput var1) throws IOException;

   void readData(DataInput var1) throws IOException;
}
