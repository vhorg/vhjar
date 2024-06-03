package iskallia.vault.item.crystal.recipe;

public interface AnvilMenuProxy {
   boolean isFake();

   void setFake(boolean var1);

   static AnvilMenuProxy of(Object object) {
      return object instanceof AnvilMenuProxy proxy ? proxy : null;
   }
}
