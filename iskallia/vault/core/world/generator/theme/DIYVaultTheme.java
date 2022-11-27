package iskallia.vault.core.world.generator.theme;

import iskallia.vault.core.data.key.TemplatePoolKey;

public class DIYVaultTheme extends Theme {
   protected TemplatePoolKey starts;
   protected TemplatePoolKey commonRooms;
   protected TemplatePoolKey challengeRooms;
   protected TemplatePoolKey omegaRooms;
   protected TemplatePoolKey tunnels;

   public DIYVaultTheme(
      TemplatePoolKey starts,
      TemplatePoolKey commonRooms,
      TemplatePoolKey challengeRooms,
      TemplatePoolKey omegaRooms,
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
      this.commonRooms = commonRooms;
      this.challengeRooms = challengeRooms;
      this.omegaRooms = omegaRooms;
      this.tunnels = tunnels;
   }

   public TemplatePoolKey getStarts() {
      return this.starts;
   }

   public TemplatePoolKey getCommonRooms() {
      return this.commonRooms;
   }

   public TemplatePoolKey getChallengeRooms() {
      return this.challengeRooms;
   }

   public TemplatePoolKey getOmegaRooms() {
      return this.omegaRooms;
   }

   public TemplatePoolKey getTunnels() {
      return this.tunnels;
   }
}
