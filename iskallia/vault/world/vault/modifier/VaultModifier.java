package iskallia.vault.world.vault.modifier;

import com.google.gson.annotations.Expose;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.player.VaultPlayer;
import java.util.Random;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.util.text.event.HoverEvent.Action;
import net.minecraft.world.server.ServerWorld;

public abstract class VaultModifier implements IVaultModifier {
   protected static final Random rand = new Random();
   @Expose
   private final String name;
   @Expose
   private String color = String.valueOf(65535);
   @Expose
   private String description = "This is a description.";

   public VaultModifier(String name) {
      this.name = name;
   }

   public VaultModifier format(int color, String description) {
      this.color = String.valueOf(color);
      this.description = description;
      return this;
   }

   public String getName() {
      return this.name;
   }

   public int getColor() {
      return Integer.parseInt(this.color);
   }

   public ITextComponent getNameComponent() {
      HoverEvent hover = new HoverEvent(Action.field_230550_a_, new StringTextComponent(this.getDescription()));
      return new StringTextComponent(this.getName())
         .func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(this.getColor())).func_240716_a_(hover));
   }

   public String getDescription() {
      return this.description;
   }

   @Override
   public void apply(VaultRaid vault, VaultPlayer player, ServerWorld world, Random random) {
   }

   @Override
   public void remove(VaultRaid vault, VaultPlayer player, ServerWorld world, Random random) {
   }

   @Override
   public void tick(VaultRaid vault, VaultPlayer player, ServerWorld world) {
   }

   public static String migrateModifierName(String modifier) {
      if (modifier.equalsIgnoreCase("Slow")) {
         return "Freezing";
      } else if (modifier.equalsIgnoreCase("Poison")) {
         return "Poisonous";
      } else if (modifier.equalsIgnoreCase("Wither")) {
         return "Withering";
      } else {
         return modifier.equalsIgnoreCase("Chilling") ? "Fatiguing" : modifier;
      }
   }
}
