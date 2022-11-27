package iskallia.vault.client;

import java.util.Collection;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.AlertScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.TextComponent;

public final class ClientInvalidConfigAlert {
   public static void showAlert(Collection<String> invalidConfigList) {
      String messagePrefix = "Some configs are invalid and have loaded defaults instead.\nSearch the log for 'invalid config' for more details.\n\n";
      Minecraft minecraft = Minecraft.getInstance();
      minecraft.setScreen(
         new AlertScreen(
            () -> minecraft.setScreen(null),
            new TextComponent(ChatFormatting.RED + "Invalid Configs"),
            new TextComponent(messagePrefix + String.join("\n", invalidConfigList)),
            CommonComponents.GUI_PROCEED
         )
      );
   }

   private ClientInvalidConfigAlert() {
   }
}
