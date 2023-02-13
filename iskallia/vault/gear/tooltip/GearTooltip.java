package iskallia.vault.gear.tooltip;

import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public record GearTooltip(boolean displayModifierDetail, boolean displayCraftingDetail, boolean displayBase) {
   @OnlyIn(Dist.CLIENT)
   public static GearTooltip itemTooltip() {
      return new GearTooltip(Screen.hasShiftDown(), Screen.hasShiftDown(), true);
   }

   @OnlyIn(Dist.CLIENT)
   public static GearTooltip toolTooltip() {
      return new GearTooltip(Screen.hasShiftDown(), true, true);
   }

   @OnlyIn(Dist.CLIENT)
   public static GearTooltip craftingView() {
      return new GearTooltip(true, true, false);
   }
}
