package iskallia.vault.util;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;

public class TextUtil {
   static final ChatFormatting[] rainbow = new ChatFormatting[]{
      ChatFormatting.RED,
      ChatFormatting.GOLD,
      ChatFormatting.YELLOW,
      ChatFormatting.GREEN,
      ChatFormatting.BLUE,
      ChatFormatting.LIGHT_PURPLE,
      ChatFormatting.DARK_PURPLE
   };

   public static TextComponent applyRainbowTo(String text) {
      StringBuilder sb = new StringBuilder();

      for (int i = 0; i < text.length(); i++) {
         char c = text.charAt(i);
         sb.append(getNextColor(i));
         sb.append(c);
      }

      return new TextComponent(sb.toString());
   }

   private static ChatFormatting getNextColor(int index) {
      return rainbow[index % rainbow.length];
   }

   public static TextComponent formatLocationPathAsProperNoun(ResourceLocation location) {
      String[] split = location.getPath().split("_");
      AtomicReference<String> name = new AtomicReference<>("");
      Arrays.stream(split).forEach(s -> name.set(name.get() + org.apache.commons.lang3.StringUtils.capitalize(s) + " "));
      return new TextComponent(name.get().trim());
   }

   public static <T extends Component> TextComponent listToComponent(List<T> components) {
      TextComponent component = new TextComponent("");
      components.forEach(c -> {
         if (components.indexOf(c) == components.size() - 1) {
            component.append(c);
         } else {
            component.append(c).append("\n");
         }
      });
      return component;
   }

   public static int getLineHeight(FormattedText component, int width) {
      return Minecraft.getInstance().font.split(component, width).size();
   }
}
