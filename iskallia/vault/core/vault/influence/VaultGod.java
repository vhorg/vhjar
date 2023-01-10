package iskallia.vault.core.vault.influence;

import java.util.Random;
import javax.annotation.Nonnull;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.HoverEvent.Action;
import net.minecraft.util.StringRepresentable;

public enum VaultGod implements StringRepresentable {
   VELARA("Velara", "The Benevolent", ChatFormatting.GREEN),
   TENOS("Tenos", "The Omniscient", ChatFormatting.AQUA),
   WENDARR("Wendarr", "The Timekeeper", ChatFormatting.GOLD),
   IDONA("Idona", "The Malevolent", ChatFormatting.RED);

   private final String name;
   private final String title;
   private final ChatFormatting color;

   private VaultGod(String name, String title, ChatFormatting color) {
      this.name = name;
      this.title = title;
      this.color = color;
   }

   public static VaultGod fromName(String name) {
      for (VaultGod type : values()) {
         if (name.equalsIgnoreCase(type.getName())) {
            return type;
         }
      }

      return null;
   }

   public String getName() {
      return this.name;
   }

   public String getTitle() {
      return this.title;
   }

   public int getColor() {
      return this.color.getColor();
   }

   public ChatFormatting getChatColor() {
      return this.color;
   }

   @Nonnull
   public String getSerializedName() {
      return this.getName().toLowerCase();
   }

   public Component getHoverChatComponent() {
      return new TextComponent("[Vault God] ").withStyle(ChatFormatting.WHITE).append(new TextComponent(this.name + ", " + this.title).withStyle(this.color));
   }

   public Component getIdolDescription() {
      String s = this.getName().endsWith("s") ? "" : "s";
      return new TextComponent(String.format("%s'%s Idol", this.getName(), s)).withStyle(this.getChatColor());
   }

   public MutableComponent getChosenPrefix() {
      String prefix = "[" + this.getName().charAt(0) + "C] ";
      MutableComponent cmp = new TextComponent(prefix).withStyle(this.color);
      String s = this.getName().endsWith("s") ? "" : "s";
      MutableComponent hover = new TextComponent(String.format("%s'%s Chosen", this.getName(), s)).withStyle(this.getChatColor());
      cmp.withStyle(style -> style.withHoverEvent(new HoverEvent(Action.SHOW_TEXT, hover)));
      return cmp;
   }

   public VaultGod getOther(Random rand) {
      int i;
      do {
         i = rand.nextInt(values().length);
      } while (i == this.ordinal());

      return values()[i];
   }
}
