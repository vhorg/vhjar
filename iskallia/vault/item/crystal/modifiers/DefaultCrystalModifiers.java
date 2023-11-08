package iskallia.vault.item.crystal.modifiers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.vault.modifier.VaultModifierStack;
import iskallia.vault.core.vault.modifier.registry.VaultModifierRegistry;
import iskallia.vault.init.ModConfigs;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

public class DefaultCrystalModifiers extends CrystalModifiers {
   private final List<VaultModifierStack> list = new ArrayList<>();
   private boolean randomModifiers = true;
   private boolean clarity = false;

   @Override
   public List<VaultModifierStack> getList() {
      return this.list;
   }

   @NotNull
   @Override
   public Iterator<VaultModifierStack> iterator() {
      return this.list.iterator();
   }

   @Override
   public boolean hasRandomModifiers() {
      return this.randomModifiers;
   }

   @Override
   public boolean hasClarity() {
      return this.clarity;
   }

   @Override
   public void setRandomModifiers(boolean randomModifiers) {
      this.randomModifiers = randomModifiers;
   }

   @Override
   public void setClarity(boolean clarity) {
      this.clarity = clarity;
   }

   @Override
   public void addText(List<Component> tooltip, TooltipFlag flag, float time) {
      if (this.clarity) {
         tooltip.add(new TextComponent("Clarity").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(4973509))));
         if (Screen.hasShiftDown()) {
            Component description = new TextComponent("  All curses on this crystal are revealed.").withStyle(ChatFormatting.DARK_GRAY);
            tooltip.add(description);
         }
      }

      int curseCount = this.getCurseCount();
      if (curseCount > 0) {
         Style style = Style.EMPTY.withColor(ModConfigs.VAULT_CRYSTAL.MODIFIER_STABILITY.curseColor);
         if (this.clarity) {
            this.addCatalystModifierInformation(
               vaultModifierStack -> ModConfigs.VAULT_CRYSTAL_CATALYST.isCurse(vaultModifierStack.getModifierId()),
               new TextComponent("Cursed").withStyle(style),
               tooltip
            );
         } else {
            MutableComponent component = new TextComponent("Cursed ").withStyle(style);
            tooltip.add(component.append("☠".repeat(curseCount)));
         }
      }

      this.addCatalystModifierInformation(
         stack -> ModConfigs.VAULT_CRYSTAL_CATALYST.isGood(stack.getModifierId()),
         new TextComponent("Positive Modifiers").withStyle(ChatFormatting.GREEN),
         tooltip
      );
      this.addCatalystModifierInformation(
         stack -> ModConfigs.VAULT_CRYSTAL_CATALYST.isBad(stack.getModifierId()),
         new TextComponent("Negative Modifiers").withStyle(ChatFormatting.RED),
         tooltip
      );
      this.addNonCatalystModifierInformation(
         stack -> ModConfigs.VAULT_CRYSTAL_CATALYST.isUnlisted(stack.getModifierId()),
         new TextComponent("Other Modifiers").withStyle(ChatFormatting.WHITE),
         tooltip
      );
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      CompoundTag nbt = new CompoundTag();
      ListTag list = new ListTag();

      for (VaultModifierStack stack : this.list) {
         list.add(stack.serializeNBT());
      }

      nbt.put("List", list);
      Adapters.BOOLEAN.writeNbt(this.randomModifiers).ifPresent(tag -> nbt.put("RandomModifiers", tag));
      Adapters.BOOLEAN.writeNbt(this.clarity).ifPresent(tag -> nbt.put("Clarity", tag));
      return Optional.of(nbt);
   }

   public void readNbt(CompoundTag nbt) {
      this.list.clear();
      ListTag list = nbt.getList("List", 10);

      for (int i = 0; i < list.size(); i++) {
         this.list.add(VaultModifierStack.of(list.getCompound(i)));
      }

      this.randomModifiers = Adapters.BOOLEAN.readNbt(nbt.get("RandomModifiers")).orElse(true);
      this.clarity = Adapters.BOOLEAN.readNbt(nbt.get("Clarity")).orElse(false);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      JsonObject json = new JsonObject();
      JsonArray array = new JsonArray();

      for (VaultModifierStack stack : this.list) {
         JsonObject element = new JsonObject();
         element.addProperty("modifier", stack.getModifierId().toString());
         element.addProperty("count", stack.getSize());
         array.add(element);
      }

      json.add("list", json);
      json.addProperty("random_modifiers", this.randomModifiers);
      json.addProperty("clarity", this.clarity);
      return Optional.of(json);
   }

   public void readJson(JsonObject json) {
      if (json.has("list")) {
         this.list.clear();

         for (JsonElement element : json.getAsJsonArray("list")) {
            JsonObject object = element.getAsJsonObject();
            this.list
               .add(
                  VaultModifierStack.of(VaultModifierRegistry.get(new ResourceLocation(object.get("modifier").getAsString())), object.get("count").getAsInt())
               );
         }
      }

      if (json.has("random_modifiers")) {
         this.randomModifiers = json.get("random_modifiers").getAsBoolean();
      }

      if (json.has("clarity")) {
         this.clarity = json.get("clarity").getAsBoolean();
      }
   }
}
