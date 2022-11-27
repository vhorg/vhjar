package iskallia.vault.mixin;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.CommandSuggestions;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.CommandSuggestions.SuggestionsList;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({CommandSuggestions.class})
public interface AccessorCommandSuggestions {
   @Accessor("screen")
   Screen getScreen();

   @Accessor("font")
   Font getFont();

   @Accessor("input")
   EditBox getEditBox();

   @Accessor("suggestions")
   void setSuggestions(SuggestionsList var1);
}
