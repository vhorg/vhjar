package iskallia.vault.gear.trinket;

import com.google.gson.annotations.Expose;
import iskallia.vault.config.TrinketConfig;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.item.gear.TrinketItem;
import iskallia.vault.util.MiscUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistryEntry;

public abstract class TrinketEffect<T extends TrinketEffect.Config> extends ForgeRegistryEntry<TrinketEffect<?>> {
   public TrinketEffect(ResourceLocation name) {
      this.setRegistryName(name);
   }

   public abstract Class<T> getConfigClass();

   public abstract T getDefaultConfig();

   public TrinketConfig.Trinket getTrinketConfig() {
      return ModConfigs.TRINKET.getTrinketConfig(this);
   }

   public T getConfig() {
      return (T)this.getTrinketConfig().getConfig();
   }

   public boolean isUsable(ItemStack trinket, Player player) {
      if (!TrinketItem.isIdentified(trinket)) {
         return false;
      } else {
         return !TrinketItem.hasUsesLeft(trinket)
            ? false
            : MiscUtils.getVault(player).map(vault -> TrinketItem.isUsableInVault(trinket, vault.get(Vault.ID))).orElse(false);
      }
   }

   public void onEquip(LivingEntity entity, ItemStack stack) {
   }

   public void onUnEquip(LivingEntity entity, ItemStack stack) {
   }

   public void onWornTick(LivingEntity entity, ItemStack stack) {
   }

   public static class Config {
      @Expose
      private String curiosSlot = "trinket";

      public Config() {
      }

      public Config(String curiosSlot) {
         this.curiosSlot = curiosSlot;
      }

      public boolean hasCuriosSlot() {
         return !this.getCuriosSlot().isEmpty();
      }

      public String getCuriosSlot() {
         return this.curiosSlot;
      }
   }

   public static class Simple extends TrinketEffect<TrinketEffect.Simple.NoOpConfig> {
      public Simple(ResourceLocation name) {
         super(name);
      }

      @Override
      public final Class<TrinketEffect.Simple.NoOpConfig> getConfigClass() {
         return TrinketEffect.Simple.NoOpConfig.class;
      }

      public final TrinketEffect.Simple.NoOpConfig getDefaultConfig() {
         return new TrinketEffect.Simple.NoOpConfig();
      }

      public static class NoOpConfig extends TrinketEffect.Config {
      }
   }
}
