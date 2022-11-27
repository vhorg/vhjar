package iskallia.vault.client.gui.framework.spatial.spi;

public interface IMutableSize extends ISize {
   IMutableSize width(int var1);

   IMutableSize width(ISize var1);

   IMutableSize height(int var1);

   IMutableSize height(ISize var1);

   IMutableSize size(int var1, int var2);

   IMutableSize size(ISize var1);
}
