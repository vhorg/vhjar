package iskallia.vault.world.vault.modifier.spi;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.HoverEvent.Action;
import net.minecraft.resources.ResourceLocation;

public abstract class VaultModifier<P> implements IVaultModifierBehaviorApply {
   private final ResourceLocation id;
   @Expose(
      deserialize = false
   )
   @SerializedName("properties")
   protected final P properties;
   @Expose(
      deserialize = false
   )
   @SerializedName("display")
   protected final VaultModifier.Display display;
   protected IVaultModifierTextFormatter<P> nameFormatter;
   protected IVaultModifierTextFormatter<P> descriptionFormatter;

   public VaultModifier(ResourceLocation id, P properties, VaultModifier.Display display) {
      this.id = id;
      this.properties = properties;
      this.display = display;
   }

   public ResourceLocation getId() {
      return this.id;
   }

   public P properties() {
      return this.properties;
   }

   public String getDisplayName() {
      return this.display.getName();
   }

   protected <T extends VaultModifier<P>> T setNameFormatter(@Nonnull IVaultModifierTextFormatter<P> nameFormatter) {
      this.nameFormatter = nameFormatter;
      return (T)this;
   }

   public String getDisplayNameFormatted(int modifierStackSize) {
      return Optional.ofNullable(this.nameFormatter)
         .map(formatter -> formatter.format(this.getDisplayName(), this.properties, modifierStackSize))
         .orElse(this.getDisplayName());
   }

   public TextColor getDisplayTextColor() {
      return this.display.getTextColor();
   }

   public String getDisplayDescription() {
      return this.display.getDescription();
   }

   protected <T extends VaultModifier<P>> T setDescriptionFormatter(IVaultModifierTextFormatter<P> descriptionFormatter) {
      this.descriptionFormatter = descriptionFormatter;
      return (T)this;
   }

   public String getDisplayDescriptionFormatted(int modifierStackSize) {
      return this.display
         .getDescriptionFormatted()
         .map(
            descriptionFormatted -> Optional.ofNullable(this.descriptionFormatter)
               .map(formatter -> formatter.format(descriptionFormatted, this.properties, modifierStackSize))
               .orElse(descriptionFormatted)
         )
         .orElse(this.getDisplayDescription());
   }

   public Optional<ResourceLocation> getIcon() {
      return this.display.getIcon();
   }

   public Component getNameComponent() {
      HoverEvent hover = new HoverEvent(Action.SHOW_TEXT, new TextComponent(this.getDisplayDescription()));
      return new TextComponent(this.getDisplayName()).setStyle(Style.EMPTY.withColor(this.getDisplayTextColor()).withHoverEvent(hover));
   }

   public Component getNameComponentFormatted(int modifierStackSize) {
      HoverEvent hover = new HoverEvent(Action.SHOW_TEXT, new TextComponent(this.getDisplayDescriptionFormatted(modifierStackSize)));
      return new TextComponent(this.getDisplayNameFormatted(modifierStackSize))
         .setStyle(Style.EMPTY.withColor(this.getDisplayTextColor()).withHoverEvent(hover));
   }

   public Component getChatDisplayNameComponent(int modifierStackSize) {
      return new TextComponent(modifierStackSize + "x ").append(this.getNameComponentFormatted(modifierStackSize));
   }

   public static class Display {
      @Expose
      private final String name;
      @Expose
      private final TextColor color;
      @Expose
      private final String description;
      @Expose
      private final String descriptionFormatted;
      @Expose
      private final ResourceLocation icon;

      public Display(String name, TextColor color, String description) {
         this(name, color, description, null, null);
      }

      public Display(String name, TextColor color, String description, @Nullable ResourceLocation icon) {
         this(name, color, description, null, icon);
      }

      public Display(String name, TextColor color, String description, @Nullable String descriptionFormatted) {
         this(name, color, description, descriptionFormatted, null);
      }

      public Display(String name, TextColor color, String description, @Nullable String descriptionFormatted, @Nullable ResourceLocation icon) {
         this.name = name;
         this.color = color;
         this.description = description;
         this.descriptionFormatted = descriptionFormatted;
         this.icon = icon;
      }

      public String getName() {
         return this.name;
      }

      public TextColor getTextColor() {
         return this.color;
      }

      public String getDescription() {
         return this.description;
      }

      public Optional<String> getDescriptionFormatted() {
         return Optional.ofNullable(this.descriptionFormatted);
      }

      public Optional<ResourceLocation> getIcon() {
         return Optional.ofNullable(this.icon);
      }
   }
}
