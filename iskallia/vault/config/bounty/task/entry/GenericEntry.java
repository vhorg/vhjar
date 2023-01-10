package iskallia.vault.config.bounty.task.entry;

import com.google.gson.annotations.Expose;
import iskallia.vault.config.entry.RangeEntry;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;

public class GenericEntry<T> {
   @Expose
   private T value;
   @Expose
   private RangeEntry amount;
   @Expose
   private boolean floorToNearestTen;
   @Expose
   protected Set<ResourceLocation> validDimensions;
   @Expose
   protected boolean vaultOnly;
   @Expose
   private String rewardPool;

   public GenericEntry(T value, RangeEntry amount) {
      this.value = value;
      this.amount = amount;
      this.validDimensions = new HashSet<>();
      this.vaultOnly = false;
      this.rewardPool = "common";
   }

   public GenericEntry<T> rewardPool(String rewardPool) {
      this.rewardPool = rewardPool;
      return this;
   }

   public GenericEntry<T> vaultOnly() {
      this.vaultOnly = true;
      return this;
   }

   public GenericEntry<T> setValidDimensions(Set<ResourceLocation> validDimensions) {
      this.validDimensions = validDimensions;
      return this;
   }

   public GenericEntry<T> floorToNearestTen() {
      this.floorToNearestTen = true;
      return this;
   }

   public T getValue() {
      return this.value;
   }

   public RangeEntry getAmount() {
      return this.amount;
   }

   public int getRandomAmount() {
      return this.floorToNearestTen ? this.amount.getRandom() / 10 * 10 : this.amount.getRandom();
   }

   public boolean isVaultOnly() {
      return this.vaultOnly;
   }

   public Set<ResourceLocation> getValidDimensions() {
      return this.validDimensions;
   }

   public String getRewardPool() {
      return this.rewardPool;
   }
}
