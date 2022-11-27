package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.world.entity.player.Player;

public class SandEventConfig extends Config {
   @Expose
   private final Map<String, Integer> HOURGLASS_TOTAL_SAND_REQUIRED = new HashMap<>();
   @Expose
   private final Map<String, Integer> VAULT_REDEMPTIONS_PER_SAND = new HashMap<>();
   @Expose
   private float minDistance = 48.0F;
   @Expose
   private float maxDistance = 128.0F;
   @Expose
   private boolean enabled = false;

   @Override
   public String getName() {
      return "sand_event";
   }

   public int getTotalSandRequired(Player player) {
      return this.HOURGLASS_TOTAL_SAND_REQUIRED.getOrDefault(player.getName().getString(), 200);
   }

   public int getRedemptionsRequiredPerSand(Player player) {
      return this.VAULT_REDEMPTIONS_PER_SAND.getOrDefault(player.getName().getString(), 100);
   }

   public float getMinDistance() {
      return this.minDistance;
   }

   public float getMaxDistance() {
      return this.maxDistance;
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   @Override
   protected void reset() {
      this.minDistance = 48.0F;
      this.maxDistance = 128.0F;
   }
}
