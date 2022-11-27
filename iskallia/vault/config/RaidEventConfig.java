package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.VaultMod;
import iskallia.vault.util.data.WeightedList;
import iskallia.vault.world.vault.modifier.registry.VaultModifierRegistry;
import iskallia.vault.world.vault.modifier.spi.VaultModifier;
import net.minecraft.resources.ResourceLocation;

public class RaidEventConfig extends Config {
   @Expose
   private boolean enabled;
   @Expose
   private int soulShardTradeCost;
   @Expose
   private int temporaryModifierMinutes;
   @Expose
   private float viewerVoteChance;
   @Expose
   private WeightedList<ResourceLocation> viewerModifiers;

   public boolean isEnabled() {
      return this.enabled;
   }

   public int getSoulShardTradeCost() {
      return this.soulShardTradeCost;
   }

   public int getTemporaryModifierMinutes() {
      return this.temporaryModifierMinutes;
   }

   public float getViewerVoteChance() {
      return this.viewerVoteChance;
   }

   public VaultModifier getRandomModifier() {
      VaultModifier modifier = null;

      while (modifier == null) {
         modifier = VaultModifierRegistry.getOrDefault(this.viewerModifiers.getRandom(rand), null);
      }

      return modifier;
   }

   @Override
   public String getName() {
      return "raid_event";
   }

   @Override
   protected void reset() {
      this.enabled = false;
      this.soulShardTradeCost = 750;
      this.temporaryModifierMinutes = 6;
      this.viewerVoteChance = 0.2F;
      this.viewerModifiers = new WeightedList<>();
      this.viewerModifiers.add(VaultMod.id("gilded"), 1);
      this.viewerModifiers.add(VaultMod.id("plentiful"), 1);
      this.viewerModifiers.add(VaultMod.id("frail"), 1);
      this.viewerModifiers.add(VaultMod.id("trapped"), 1);
   }
}
