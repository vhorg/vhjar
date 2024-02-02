package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.world.data.PlayerTitlesData;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;

public class PlayerTitlesConfig extends Config {
   @Expose
   private Map<PlayerTitlesConfig.Affix, Map<String, PlayerTitlesConfig.Title>> titles;

   @Override
   public String getName() {
      return "player_titles";
   }

   public Map<String, PlayerTitlesConfig.Title> getAll(PlayerTitlesConfig.Affix affix) {
      return this.titles.get(affix);
   }

   public Optional<PlayerTitlesConfig.Title> get(PlayerTitlesConfig.Affix affix, String id) {
      return Optional.ofNullable(this.titles.get(affix)).map(map -> map.get(id));
   }

   @Override
   protected void reset() {
      this.titles = new LinkedHashMap<>();
      Map<String, PlayerTitlesConfig.Title> prefixes = this.titles.computeIfAbsent(PlayerTitlesConfig.Affix.PREFIX, k -> new LinkedHashMap<>());
      prefixes.put(
         "omega",
         new PlayerTitlesConfig.Title(1)
            .put(PlayerTitlesData.Type.TAB_LIST, new PlayerTitlesConfig.Display("Omega ", "#AAAAAA"))
            .put(PlayerTitlesData.Type.CHAT, new PlayerTitlesConfig.Display("Omega ", null))
      );
      prefixes.put(
         "sir",
         new PlayerTitlesConfig.Title(1)
            .put(PlayerTitlesData.Type.TAB_LIST, new PlayerTitlesConfig.Display("Sir ", "#AAAAAA"))
            .put(PlayerTitlesData.Type.CHAT, new PlayerTitlesConfig.Display("Sir ", null))
      );
      Map<String, PlayerTitlesConfig.Title> suffixes = this.titles.computeIfAbsent(PlayerTitlesConfig.Affix.SUFFIX, k -> new LinkedHashMap<>());
      suffixes.put(
         "of_doom",
         new PlayerTitlesConfig.Title(1)
            .put(PlayerTitlesData.Type.TAB_LIST, new PlayerTitlesConfig.Display(" of Doom", "#AAAAAA"))
            .put(PlayerTitlesData.Type.CHAT, new PlayerTitlesConfig.Display(" of Doom", null))
      );
      suffixes.put(
         "the_great",
         new PlayerTitlesConfig.Title(1)
            .put(PlayerTitlesData.Type.TAB_LIST, new PlayerTitlesConfig.Display(" the Great", "#AAAAAA"))
            .put(PlayerTitlesData.Type.CHAT, new PlayerTitlesConfig.Display(" the Great", null))
      );
   }

   public static enum Affix {
      PREFIX,
      SUFFIX;
   }

   public static class Display {
      @Expose
      private final String text;
      @Expose
      private final String color;

      public Display() {
         this("UNSPECIFIED", null);
      }

      public Display(String text, String color) {
         this.text = text;
         this.color = color;
      }

      public Optional<MutableComponent> getComponent() {
         if (this.text == null) {
            return Optional.empty();
         } else {
            TextComponent component = new TextComponent(this.text);
            if (this.color != null) {
               component.withStyle(Style.EMPTY.withColor(TextColor.parseColor(this.color)));
            }

            return Optional.of(component);
         }
      }
   }

   public static class Title {
      @Expose
      private final Map<PlayerTitlesData.Type, PlayerTitlesConfig.Display> display = new LinkedHashMap<>();
      @Expose
      private final int cost;

      public Title() {
         this(1);
      }

      public Title(int cost) {
         this.cost = cost;
      }

      public Map<PlayerTitlesData.Type, PlayerTitlesConfig.Display> getDisplay() {
         return this.display;
      }

      public PlayerTitlesConfig.Display getDisplay(PlayerTitlesData.Type type) {
         return this.display.get(type);
      }

      public int getCost() {
         return this.cost;
      }

      public PlayerTitlesConfig.Title put(PlayerTitlesData.Type type, PlayerTitlesConfig.Display display) {
         this.display.put(type, display);
         return this;
      }
   }
}
