package iskallia.vault.core.world.generator.theme;

import iskallia.vault.core.data.key.TemplatePoolKey;

public class ClassicVaultTheme extends Theme {
   protected TemplatePoolKey starts;
   protected TemplatePoolKey rooms;
   protected TemplatePoolKey tunnels;

   public ClassicVaultTheme(
      TemplatePoolKey starts,
      TemplatePoolKey rooms,
      TemplatePoolKey tunnels,
      float ambientLight,
      int fogColor,
      int grassColor,
      int foliageColor,
      int waterColor,
      int waterFogColor,
      String particle,
      float particleProbability
   ) {
      super(ambientLight, fogColor, grassColor, foliageColor, waterColor, waterFogColor, particle, particleProbability);
      this.starts = starts;
      this.rooms = rooms;
      this.tunnels = tunnels;
   }

   public TemplatePoolKey getStarts() {
      return this.starts;
   }

   public TemplatePoolKey getRooms() {
      return this.rooms;
   }

   public TemplatePoolKey getTunnels() {
      return this.tunnels;
   }
}
