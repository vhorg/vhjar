package iskallia.vault.client.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import iskallia.vault.world.data.GlobalDifficultyData;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.Button.IPressable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;

public class DifficultyButton extends Button {
   private final String text;
   private final String key;
   GlobalDifficultyData.Difficulty difficulty = GlobalDifficultyData.Difficulty.STANDARD;

   public DifficultyButton(String text, String key, int x, int y, int width, int height, ITextComponent label, IPressable onPress) {
      super(x, y, width, height, label, onPress);
      this.text = text;
      this.key = key;
   }

   public void getNextOption() {
      this.difficulty = this.difficulty.getNext();
      this.func_238482_a_(new StringTextComponent(this.text + ": " + this.difficulty.toString()));
   }

   public String getKey() {
      return this.key.replace(" ", "");
   }

   public GlobalDifficultyData.Difficulty getCurrentOption() {
      return this.difficulty;
   }

   public void func_230443_a_(MatrixStack matrixStack, int mouseX, int mouseY) {
      List<StringTextComponent> tooltips = new ArrayList<>();
      tooltips.add(new StringTextComponent(this.text));
      GuiUtils.drawHoveringText(
         matrixStack, tooltips, mouseX, mouseY, this.field_230688_j_ + mouseX, this.field_230689_k_ + mouseY, -1, Minecraft.func_71410_x().field_71466_p
      );
   }
}
