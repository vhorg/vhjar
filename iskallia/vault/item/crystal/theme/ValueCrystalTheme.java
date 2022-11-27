package iskallia.vault.item.crystal.theme;

import com.google.gson.JsonObject;
import iskallia.vault.VaultMod;
import iskallia.vault.core.data.key.ThemeKey;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.VaultRegistry;
import iskallia.vault.core.vault.WorldManager;
import iskallia.vault.core.world.generator.GridGenerator;
import iskallia.vault.core.world.generator.layout.ClassicVaultLayout;
import iskallia.vault.core.world.generator.layout.DIYVaultLayout;
import iskallia.vault.core.world.generator.layout.GridLayout;
import iskallia.vault.core.world.generator.theme.ClassicVaultTheme;
import iskallia.vault.core.world.generator.theme.DIYVaultTheme;
import iskallia.vault.core.world.generator.theme.Theme;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.TooltipFlag;

public class ValueCrystalTheme extends CrystalTheme {
   private ResourceLocation id;

   protected ValueCrystalTheme() {
   }

   public ValueCrystalTheme(ResourceLocation id) {
      this.id = id;
   }

   @Override
   public void configure(Vault vault, RandomSource random) {
      ThemeKey key = VaultRegistry.THEME.getKey(this.id);
      if (key == null) {
         VaultMod.LOGGER.error("Theme with key [" + this.id + "] does not exist");
      } else {
         Theme theme = key.get(vault.get(Vault.VERSION));
         if (theme != null && vault.has(Vault.WORLD)) {
            vault.get(Vault.WORLD).ifPresent(WorldManager.GENERATOR, generator -> {
               if (generator instanceof GridGenerator grid) {
                  this.configureLayout(grid.get(GridGenerator.LAYOUT), theme);
               }
            });
            vault.get(Vault.WORLD).ifPresent(WorldManager.RENDERER, renderer -> renderer.setTheme(theme));
         }
      }
   }

   private void configureLayout(GridLayout layout, Theme theme) {
      if (layout instanceof ClassicVaultLayout classic) {
         if (theme instanceof ClassicVaultTheme classicTheme) {
            classic.set(ClassicVaultLayout.START_POOL, classicTheme.getStarts())
               .set(ClassicVaultLayout.ROOM_POOL, classicTheme.getRooms())
               .set(ClassicVaultLayout.TUNNEL_POOL, classicTheme.getTunnels());
         } else if (theme instanceof DIYVaultTheme diyTheme) {
            classic.set(ClassicVaultLayout.START_POOL, diyTheme.getStarts())
               .set(ClassicVaultLayout.ROOM_POOL, diyTheme.getCommonRooms())
               .set(ClassicVaultLayout.TUNNEL_POOL, diyTheme.getTunnels());
         }
      } else if (layout instanceof DIYVaultLayout diy) {
         if (theme instanceof ClassicVaultTheme classicTheme) {
            diy.set(DIYVaultLayout.START_POOL, classicTheme.getStarts())
               .set(DIYVaultLayout.COMMON_ROOM_POOL, classicTheme.getRooms())
               .set(DIYVaultLayout.CHALLENGE_ROOM_POOL, classicTheme.getRooms())
               .set(DIYVaultLayout.OMEGA_ROOM_POOL, classicTheme.getRooms())
               .set(DIYVaultLayout.TUNNEL_POOL, classicTheme.getTunnels());
         } else if (theme instanceof DIYVaultTheme diyTheme) {
            diy.set(DIYVaultLayout.START_POOL, diyTheme.getStarts())
               .set(DIYVaultLayout.COMMON_ROOM_POOL, diyTheme.getCommonRooms())
               .set(DIYVaultLayout.CHALLENGE_ROOM_POOL, diyTheme.getChallengeRooms())
               .set(DIYVaultLayout.OMEGA_ROOM_POOL, diyTheme.getOmegaRooms())
               .set(DIYVaultLayout.TUNNEL_POOL, diyTheme.getTunnels());
         }
      }
   }

   @Override
   public void addText(List<Component> tooltip, TooltipFlag flag) {
      ThemeKey theme = VaultRegistry.THEME.getKey(this.id);
      if (theme != null) {
         tooltip.add(new TextComponent("Theme: ").append(new TextComponent(theme.getName()).withStyle(ChatFormatting.GOLD)));
      }
   }

   public CompoundTag serializeNBT() {
      CompoundTag nbt = new CompoundTag();
      nbt.putString("type", "value");
      nbt.putString("id", this.id.toString());
      return nbt;
   }

   public void deserializeNBT(CompoundTag nbt) {
      this.id = new ResourceLocation(nbt.getString("id"));
   }

   @Override
   public JsonObject serializeJson() {
      JsonObject object = new JsonObject();
      object.addProperty("type", "value");
      object.addProperty("id", this.id.toString());
      return object;
   }

   @Override
   public void deserializeJson(JsonObject json) {
      this.id = new ResourceLocation(json.get("id").getAsString());
   }
}
