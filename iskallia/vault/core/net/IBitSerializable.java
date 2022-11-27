package iskallia.vault.core.net;

public interface IBitSerializable {
   void write(BitBuffer var1);

   void read(BitBuffer var1);
}
