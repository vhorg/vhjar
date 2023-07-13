package iskallia.vault.util;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import iskallia.vault.client.gui.framework.text.TextBorder;
import java.util.Comparator;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.ClientCommandSourceStack;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.server.ServerLifecycleHooks;

public class TextComponentUtils {
   public static int getLength(Component cmp) {
      return cmp.getString().length();
   }

   public static MutableComponent substring(CommandSourceStack sourceStack, Component cmp, int beginIndex) {
      return substring(sourceStack, cmp, beginIndex, getLength(cmp));
   }

   public static MutableComponent substring(CommandSourceStack sourceStack, Component cmp, int beginIndex, int endIndex) {
      return substringRecursively(resolveAndFlatten(sourceStack, cmp), beginIndex, endIndex, new Counter());
   }

   private static MutableComponent substringRecursively(Component cmp, int beginIndex, int endIndex, Counter cursor) {
      if (beginIndex > endIndex) {
         throw new IllegalArgumentException("substring beginIndex > endIndex");
      } else if (!(cmp instanceof MutableComponent txt)) {
         throw new IllegalArgumentException("Non-flattened component passed");
      } else {
         int elementBeginIndex = beginIndex - cursor.getValue();
         int elementEndIndex = endIndex - cursor.getValue();
         if (elementEndIndex <= 0) {
            return new TextComponent("");
         } else {
            String txtString = txt.getContents();
            if (elementBeginIndex >= txtString.length()) {
               cursor.setValue(cursor.getValue() + txtString.length());
               MutableComponent result = new TextComponent("");

               for (Component child : cmp.getSiblings()) {
                  result.append(substringRecursively(child, beginIndex, endIndex, cursor));
               }

               return result;
            } else {
               String txtSlice = txtString.substring(Math.max(elementBeginIndex, 0), Math.min(elementEndIndex, txtString.length()));
               MutableComponent result = new TextComponent(txtSlice).withStyle(cmp.getStyle());
               cursor.setValue(cursor.getValue() + txtString.length());

               for (Component child : cmp.getSiblings()) {
                  result.append(substringRecursively(child, beginIndex, endIndex, cursor));
               }

               return result;
            }
         }
      }
   }

   public static MutableComponent replace(CommandSourceStack sourceStack, Component cmp, String replace, Component replaceWith) {
      return replaceRecursively(resolveAndFlatten(sourceStack, cmp), replace, replaceWith);
   }

   private static MutableComponent replaceRecursively(Component cmp, String replace, Component replaceWith) {
      MutableComponent result = new TextComponent("").setStyle(cmp.getStyle());
      if (!(cmp instanceof MutableComponent txt)) {
         throw new IllegalArgumentException("Non-flattened component passed");
      } else {
         String txtString = txt.getContents();
         if (txtString.contains(replace)) {
            String[] parts = txtString.split(replace, -1);

            for (int i = 0; i < parts.length; i++) {
               result.append(new TextComponent(parts[i]).setStyle(cmp.getStyle()));
               if (i + 1 < parts.length) {
                  result.append(replaceWith);
               }
            }
         } else {
            result.append(cmp.plainCopy().setStyle(cmp.getStyle()));
         }

         for (Component child : cmp.getSiblings()) {
            result.append(replaceRecursively(child, replace, replaceWith));
         }

         return result;
      }
   }

   public static void applyStyle(Component cmp, Style style) {
      if (cmp instanceof MutableComponent mutable) {
         mutable.setStyle(style.applyTo(cmp.getStyle()));
      }

      cmp.getSiblings().forEach(child -> applyStyle(child, style));
   }

   private static MutableComponent resolveAndFlatten(CommandSourceStack sourceStack, Component cmp) {
      try {
         return resolveFlatten(ComponentUtils.updateForEntity(sourceStack, cmp, null, 0));
      } catch (CommandSyntaxException var3) {
         var3.printStackTrace();
         return new TextComponent("Formatting Error: " + cmp.getString());
      }
   }

   public static CommandSourceStack createSourceStack(LogicalSide side) {
      return side.isServer() ? createServerSourceStack() : createClientSourceStack();
   }

   public static CommandSourceStack createServerSourceStack() {
      ServerLevel overWorld = ServerLifecycleHooks.getCurrentServer().overworld();
      return new CommandSourceStack(CommandSource.NULL, Vec3.ZERO, Vec2.ZERO, overWorld, 2, "", TextComponent.EMPTY, overWorld.getServer(), null);
   }

   @OnlyIn(Dist.CLIENT)
   public static CommandSourceStack createClientSourceStack() {
      Player player = Minecraft.getInstance().player;
      return player == null
         ? new ClientCommandSourceStack(CommandSource.NULL, Vec3.ZERO, Vec2.ZERO, 2, "", TextComponent.EMPTY, null)
         : ClientCommandHandler.getSource();
   }

   private static MutableComponent resolveFlatten(Component cmp) {
      MutableComponent result = new TextComponent("");
      cmp.visit((style, content) -> {
         result.append(new TextComponent(content).withStyle(style));
         return Optional.empty();
      }, Style.EMPTY);
      return result;
   }

   public static Comparator<Component> componentComparator() {
      return (o1, o2) -> {
         Comparator<String> stringComparator = Comparator.naturalOrder();
         return stringComparator.compare(o1.getString(), o2.getString());
      };
   }

   public static int getWidth(Component component) {
      return TextBorder.DEFAULT_FONT.get().width(component.getVisualOrderText());
   }
}
