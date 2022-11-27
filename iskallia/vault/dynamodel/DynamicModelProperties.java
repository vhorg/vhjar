package iskallia.vault.dynamodel;

public class DynamicModelProperties {
   boolean discoverOnRoll;
   boolean allowTransmogrification;

   public DynamicModelProperties discoverOnRoll() {
      this.discoverOnRoll = true;
      return this;
   }

   public boolean isDiscoveredOnRoll() {
      return this.discoverOnRoll;
   }

   public DynamicModelProperties allowTransmogrification() {
      this.allowTransmogrification = true;
      return this;
   }

   public boolean doesAllowTransmogrification() {
      return this.allowTransmogrification;
   }
}
