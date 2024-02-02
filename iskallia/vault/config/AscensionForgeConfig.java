package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.VaultMod;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class AscensionForgeConfig extends Config {
   @Expose
   protected List<AscensionForgeConfig.AscensionForgeListing> listings = new ArrayList<>();

   @Override
   public String getName() {
      return "ascension_forge";
   }

   @Override
   protected void reset() {
      this.listings.clear();
      this.listings.add(new AscensionForgeConfig.AscensionForgeListing(VaultMod.id("gear/armor/flamingo/boots"), null, 5));
      this.listings.add(new AscensionForgeConfig.AscensionForgeListing(VaultMod.id("gear/armor/flamingo/leggings"), null, 5));
      this.listings.add(new AscensionForgeConfig.AscensionForgeListing(VaultMod.id("gear/armor/flamingo/chestplate"), null, 5));
      this.listings.add(new AscensionForgeConfig.AscensionForgeListing(VaultMod.id("gear/armor/flamingo/helmet"), null, 5));
      this.listings.add(new AscensionForgeConfig.AscensionForgeListing(VaultMod.id("gear/sword/sword_1"), null, 5));
      this.listings.add(new AscensionForgeConfig.AscensionForgeListing(VaultMod.id("gear/axe/axe_1"), null, 5));
      this.listings.add(new AscensionForgeConfig.AscensionForgeListing(null, new ItemStack(Items.STICK).setHoverName(new TextComponent("Fancy Stick")), 5));
   }

   public List<AscensionForgeConfig.AscensionForgeListing> getListings() {
      return this.listings;
   }

   public static final class AscensionForgeListing {
      @Expose
      @Nullable
      private final ResourceLocation modelId;
      @Expose
      @Nullable
      private final ItemStack stack;
      @Expose
      private final int cost;

      public AscensionForgeListing(@Nullable ResourceLocation modelId, @Nullable ItemStack stack, int cost) {
         this.modelId = modelId;
         this.stack = stack;
         this.cost = cost;
      }

      @Nullable
      public ResourceLocation modelId() {
         return this.modelId;
      }

      @Nullable
      public ItemStack stack() {
         return this.stack;
      }

      public int cost() {
         return this.cost;
      }

      @Override
      public boolean equals(Object obj) {
         if (obj == this) {
            return true;
         } else if (obj != null && obj.getClass() == this.getClass()) {
            AscensionForgeConfig.AscensionForgeListing that = (AscensionForgeConfig.AscensionForgeListing)obj;
            return Objects.equals(this.modelId, that.modelId) && Objects.equals(this.stack, that.stack) && this.cost == that.cost;
         } else {
            return false;
         }
      }

      @Override
      public int hashCode() {
         return Objects.hash(this.modelId, this.stack, this.cost);
      }

      @Override
      public String toString() {
         return "AscensionForgeListing[modelId=" + this.modelId + ", stack=" + this.stack + ", cost=" + this.cost + "]";
      }
   }
}
