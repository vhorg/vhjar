package iskallia.vault.util;

public interface IBiomeAccessor {
   void setSeed(long var1);

   void setLegacyBiomes(boolean var1);

   void setLargeBiomes(boolean var1);

   long getSeed();

   boolean getLegacyBiomes();

   boolean getLargeBiomes();
}
