package iskallia.vault.gear.attribute;

import com.google.gson.JsonObject;
import iskallia.vault.config.gear.VaultGearTierConfig;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.gear.attribute.config.ConfigurableAttributeGenerator;
import iskallia.vault.gear.data.GearDataVersion;
import iskallia.vault.gear.data.VaultGearData;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public final class VaultGearModifier<T> extends VaultGearAttributeInstance<T> {
   private int rolledTier = -1;
   private boolean legendary = false;
   private String modifierGroup = "";
   private ResourceLocation modifierIdentifier = null;
   private long gameTimeAdded = Long.MIN_VALUE;

   VaultGearModifier(VaultGearAttribute<T> attribute) {
      super(attribute);
   }

   public VaultGearModifier(VaultGearAttribute<T> attribute, T value) {
      super(attribute, value);
   }

   public void setRolledTier(int rolledTier) {
      this.rolledTier = rolledTier;
   }

   public int getRolledTier() {
      return this.rolledTier;
   }

   public void setLegendary(boolean legendary) {
      this.legendary = legendary;
   }

   public boolean isLegendary() {
      return this.legendary;
   }

   public void setModifierGroup(String modifierGroup) {
      this.modifierGroup = modifierGroup;
   }

   public String getModifierGroup() {
      return this.modifierGroup;
   }

   public void setModifierIdentifier(ResourceLocation modifierIdentifier) {
      this.modifierIdentifier = modifierIdentifier;
   }

   public ResourceLocation getModifierIdentifier() {
      return this.modifierIdentifier;
   }

   public void setGameTimeAdded(long gameTimeAdded) {
      this.gameTimeAdded = gameTimeAdded;
   }

   public long getGameTimeAdded() {
      return this.gameTimeAdded;
   }

   public boolean hasGameTimeAdded() {
      return this.getGameTimeAdded() != Long.MIN_VALUE;
   }

   public void resetGameTimeAdded() {
      this.setGameTimeAdded(Long.MIN_VALUE);
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public Optional<MutableComponent> getDisplay(VaultGearData data, VaultGearModifier.AffixType type, ItemStack stack, boolean displayDetail) {
      return super.getDisplay(data, type, stack, displayDetail)
         .map(
            displayText -> {
               if (!this.isLegendary()) {
                  return (MutableComponent)displayText;
               } else {
                  Style style = displayText.getStyle();
                  String rawString = displayText.getString();
                  MutableComponent legendaryCt = new TextComponent("✦ ").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(15853364)));
                  int time = rawString.length();
                  time = (int)(time * 1.4);
                  int step = (int)(System.currentTimeMillis() / 90L % time);
                  if (step >= rawString.length()) {
                     return legendaryCt.append(new TextComponent(rawString).setStyle(style));
                  } else {
                     int stepCap = Math.min(step + 1, rawString.length());
                     String start = rawString.substring(0, step);
                     String highlight = rawString.substring(step, stepCap);
                     String end = rawString.substring(stepCap);
                     return legendaryCt.append(new TextComponent(start).setStyle(style))
                        .append(new TextComponent(highlight).setStyle(Style.EMPTY.withColor(ChatFormatting.WHITE)))
                        .append(new TextComponent(end).setStyle(style));
                  }
               }
            }
         )
         .map(displayText -> {
            if (!this.hasGameTimeAdded()) {
               return (MutableComponent)displayText;
            } else {
               int showDuration = 600;
               long added = this.getGameTimeAdded();
               Level currentWorld = Minecraft.getInstance().level;
               if (currentWorld != null && currentWorld.getGameTime() - added <= showDuration) {
                  displayText.append(new TextComponent(" [new]").withStyle(ChatFormatting.GOLD));
                  return (MutableComponent)displayText;
               } else {
                  return (MutableComponent)displayText;
               }
            }
         })
         .map(displayText -> {
            if (!displayDetail) {
               return (MutableComponent)displayText;
            } else {
               MutableComponent tierDisplay = VaultGearTierConfig.getConfig(stack.getItem()).map(tierConfig -> {
                  Object config = tierConfig.getTierConfig(this);
                  if (config != null) {
                     ConfigurableAttributeGenerator configGen = this.getAttribute().getGenerator();
                     return configGen.getConfigDisplay(this.getAttribute().getReader(), config);
                  } else {
                     return null;
                  }
               }).orElse(null);
               if (tierDisplay != null) {
                  Style txtStyle = Style.EMPTY.withColor(ChatFormatting.GRAY).withItalic(false).withUnderlined(false).withBold(false);
                  String legendaryInfo = this.isLegendary() ? "Legendary " : "";
                  if (tierDisplay.getString().isEmpty()) {
                     displayText.append(new TextComponent(" (%sT%s)".formatted(legendaryInfo, this.getRolledTier() + 1)).setStyle(txtStyle));
                  } else {
                     displayText.append(new TextComponent(" (%sT%s: ".formatted(legendaryInfo, this.getRolledTier() + 1)).setStyle(txtStyle));
                     displayText.append(tierDisplay.setStyle(txtStyle));
                     displayText.append(new TextComponent(")").setStyle(txtStyle));
                  }
               }

               return (MutableComponent)displayText;
            }
         });
   }

   @Override
   public void write(BitBuffer buf) {
      super.write(buf);
      buf.writeInt(this.rolledTier);
      buf.writeBoolean(this.legendary);
      buf.writeString(this.modifierGroup);
      buf.writeNullable(this.modifierIdentifier, BitBuffer::writeIdentifier);
      buf.writeLong(this.gameTimeAdded);
   }

   @Override
   public void read(BitBuffer buf, GearDataVersion version) {
      super.read(buf, version);
      this.rolledTier = buf.readInt();
      this.legendary = buf.readBoolean();
      this.modifierGroup = buf.readString();
      this.modifierIdentifier = buf.readNullable(BitBuffer::readIdentifier);
      this.gameTimeAdded = buf.readLong();
   }

   @Override
   public void toNbt(CompoundTag tag) {
      super.toNbt(tag);
      tag.putInt("rolledTier", this.rolledTier);
      tag.putBoolean("legendary", this.legendary);
      tag.putString("modifierGroup", this.modifierGroup);
      if (this.modifierIdentifier != null) {
         tag.putString("modifierIdentifier", this.modifierIdentifier.toString());
      }

      tag.putLong("gameTimeAdded", this.gameTimeAdded);
   }

   @Override
   protected void fromNbt(CompoundTag tag, GearDataVersion version) {
      super.fromNbt(tag, version);
      this.rolledTier = tag.getInt("rolledTier");
      this.legendary = tag.getBoolean("legendary");
      this.modifierGroup = tag.getString("modifierGroup");
      if (tag.contains("modifierIdentifier", 8)) {
         this.modifierIdentifier = new ResourceLocation(tag.getString("modifierIdentifier"));
      }

      this.gameTimeAdded = tag.getLong("gameTimeAdded");
   }

   @Override
   public JsonObject serialize(VaultGearModifier.AffixType type) {
      JsonObject obj = super.serialize(type);
      obj.addProperty("legendary", this.legendary);
      return obj;
   }

   public static enum AffixType {
      IMPLICIT("Implicits", isPositive -> ""),
      PREFIX("Prefixes", isPositive -> isPositive ? "+" : "-"),
      SUFFIX("Suffixes", isPositive -> isPositive ? "+" : "-");

      private final String displayName;
      private final Function<Boolean, String> affixPrefix;

      private AffixType(String displayName, Function<Boolean, String> affixPrefix) {
         this.displayName = displayName;
         this.affixPrefix = affixPrefix;
      }

      public String getDisplayName() {
         return this.displayName;
      }

      public String getAffixPrefix(boolean isPositive) {
         return this.affixPrefix.apply(isPositive);
      }
   }
}
