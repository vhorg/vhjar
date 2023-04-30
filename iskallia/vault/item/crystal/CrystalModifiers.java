package iskallia.vault.item.crystal;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import iskallia.vault.VaultMod;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.basic.SerializableAdapter;
import iskallia.vault.core.random.JavaRandom;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.modifier.VaultModifierStack;
import iskallia.vault.core.vault.modifier.modifier.PlayerInventoryRestoreModifier;
import iskallia.vault.core.vault.modifier.registry.VaultModifierRegistry;
import iskallia.vault.core.vault.modifier.spi.VaultModifier;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModGameRules;
import iskallia.vault.item.crystal.data.serializable.ISerializable;
import iskallia.vault.world.VaultMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
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
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;

public class CrystalModifiers implements Iterable<VaultModifierStack>, ISerializable<CompoundTag, JsonObject> {
   public static final SerializableAdapter<CrystalModifiers, CompoundTag, JsonObject> ADAPTER = new SerializableAdapter<>(CrystalModifiers::new, false);
   private ArrayList<VaultModifierStack> list = new ArrayList<>();
   private boolean randomModifiers = true;
   private boolean clarity = false;

   @NotNull
   @Override
   public Iterator<VaultModifierStack> iterator() {
      return this.list.iterator();
   }

   public boolean hasRandomModifiers() {
      return this.randomModifiers;
   }

   public boolean hasClarity() {
      return this.clarity;
   }

   public void setRandomModifiers(boolean randomModifiers) {
      this.randomModifiers = randomModifiers;
   }

   public void setClarity(boolean clarity) {
      this.clarity = clarity;
   }

   public void configure(Vault vault, RandomSource random) {
      boolean casual = ((VaultMode.GameRuleValue)ServerLifecycleHooks.getCurrentServer().getGameRules().getRule(ModGameRules.MODE)).get() == VaultMode.CASUAL;

      for (VaultModifierStack stack : this.list) {
         vault.ifPresent(Vault.MODIFIERS, m -> m.addModifier(stack.getModifier(), stack.getSize(), true, random));
      }

      if (casual) {
         VaultModifier<?> modifier = VaultModifierRegistry.getOrDefault(VaultMod.id("casual"), null);
         if (modifier != null) {
            vault.ifPresent(Vault.MODIFIERS, m -> m.addModifier(modifier, 1, false, random));
         }
      }

      if (this.randomModifiers) {
         for (VaultModifier<?> modifier : ModConfigs.VAULT_MODIFIER_POOLS.getRandom(VaultMod.id("default"), vault.get(Vault.LEVEL).get(), random)) {
            if (!casual || !(modifier instanceof PlayerInventoryRestoreModifier)) {
               vault.ifPresent(Vault.MODIFIERS, m -> m.addModifier(modifier, 1, true, random));
            }
         }
      }
   }

   protected boolean canAdd(CrystalData crystal, VaultModifierStack modifierStack) {
      return !crystal.isUnmodifiable();
   }

   public boolean addByCrafting(CrystalData crystal, List<VaultModifierStack> modifierStackList, CrystalData.Simulate simulate) {
      for (VaultModifierStack modifierStack : modifierStackList) {
         if (!this.addByCrafting(crystal, modifierStack, true, CrystalData.Simulate.TRUE)) {
            return false;
         }
      }

      if (simulate == CrystalData.Simulate.FALSE) {
         for (VaultModifierStack modifierStackx : modifierStackList) {
            this.addByCrafting(crystal, modifierStackx, true, simulate);
         }
      }

      return true;
   }

   public boolean addByCrafting(CrystalData crystal, VaultModifierStack modifierStack, boolean preventsRandomModifiers, CrystalData.Simulate simulate) {
      if (!this.canAdd(crystal, modifierStack)) {
         return false;
      } else {
         if (simulate == CrystalData.Simulate.FALSE) {
            if (preventsRandomModifiers) {
               this.randomModifiers = false;
            }

            crystal.setInstability(crystal.getInstability() + ModConfigs.VAULT_CRYSTAL.MODIFIER_STABILITY.instabilityPerCraft);
            this.add(modifierStack);
         }

         return true;
      }
   }

   public void add(VaultModifierStack modifierStack) {
      if (modifierStack.isEmpty()) {
         VaultMod.LOGGER.error("Attempted to add Empty modifier to crystal. If you see this stacktrace, please share it with the devs.", new Exception());
      } else {
         boolean found = false;
         ResourceLocation modifierId = modifierStack.getModifierId();

         for (VaultModifierStack modifier : this.list) {
            if (modifier.getModifierId().equals(modifierId)) {
               modifier.grow(modifierStack.getSize());
               found = true;
               break;
            }
         }

         if (!found) {
            this.list.add(modifierStack.copy());
         }

         this.sortModifiers();
      }
   }

   public boolean isEmpty() {
      return this.list.isEmpty();
   }

   public int getCurseCount() {
      return this.list
         .stream()
         .filter(vaultModifierStack -> ModConfigs.VAULT_CRYSTAL_CATALYST.isCurse(vaultModifierStack.getModifierId()))
         .map(VaultModifierStack::getSize)
         .reduce(0, Integer::sum);
   }

   public boolean hasCurse() {
      return this.getCurseCount() > 0;
   }

   public void removeAllCurses() {
      this.list.removeIf(stack -> ModConfigs.VAULT_CRYSTAL_CATALYST.isCurse(stack.getModifierId()));
   }

   public void removeRandomCurse() {
      List<VaultModifierStack> curseList = this.list.stream().filter(stack -> ModConfigs.VAULT_CRYSTAL_CATALYST.isCurse(stack.getModifierId())).toList();
      if (!curseList.isEmpty()) {
         RandomSource random = JavaRandom.ofNanoTime();
         VaultModifierStack modifierStack = curseList.get(random.nextInt(curseList.size()));
         if (modifierStack.shrink(1).isEmpty()) {
            this.list.remove(modifierStack);
         }
      }
   }

   public void sortModifiers() {
      this.list.sort(Comparator.comparing(VaultModifierStack::getSize).reversed());
   }

   public void addText(List<Component> tooltip, TooltipFlag flag) {
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

   private void addNonCatalystModifierInformation(Predicate<VaultModifierStack> filter, Component headerComponent, List<Component> tooltip) {
      List<VaultModifierStack> modifierList = this.list.stream().filter(filter).toList();
      if (!modifierList.isEmpty()) {
         tooltip.add(headerComponent);

         for (VaultModifierStack modifierStack : modifierList) {
            VaultModifier<?> vaultModifier = modifierStack.getModifier();
            TextComponent modifierName = new TextComponent(vaultModifier.getDisplayNameFormatted(modifierStack.getSize()));
            modifierName.setStyle(Style.EMPTY.withColor(vaultModifier.getDisplayTextColor()));
            if (modifierStack.getSize() > 1) {
               Component stackSize = new TextComponent("%dx".formatted(modifierStack.getSize())).withStyle(ChatFormatting.GRAY);
               tooltip.add(new TextComponent("  ").withStyle(ChatFormatting.GRAY).append(stackSize).append(" ").append(modifierName));
            } else {
               tooltip.add(new TextComponent("  ").append(modifierName));
            }

            if (Screen.hasShiftDown()) {
               String descriptionTxt = vaultModifier.getDisplayDescriptionFormatted(modifierStack.getSize());
               if (!descriptionTxt.isEmpty()) {
                  Component description = new TextComponent("  " + descriptionTxt).withStyle(ChatFormatting.DARK_GRAY);
                  tooltip.add(description);
               }
            }
         }
      }
   }

   private void addCatalystModifierInformation(Predicate<VaultModifierStack> filter, Component headerComponent, List<Component> tooltip) {
      List<VaultModifierStack> modifierList = this.list.stream().filter(filter).toList();
      if (!modifierList.isEmpty()) {
         tooltip.add(headerComponent);

         for (VaultModifierStack modifierStack : modifierList) {
            VaultModifier<?> vaultModifier = modifierStack.getModifier();
            TextComponent modifierName = new TextComponent(vaultModifier.getDisplayNameFormatted(modifierStack.getSize()));
            modifierName.setStyle(Style.EMPTY.withColor(vaultModifier.getDisplayTextColor()));
            Component stackSize = new TextComponent("%dx".formatted(modifierStack.getSize())).withStyle(ChatFormatting.GRAY);
            tooltip.add(new TextComponent("  ").withStyle(ChatFormatting.GRAY).append(stackSize).append(" ").append(modifierName));
            if (Screen.hasShiftDown()) {
               Component description = new TextComponent("  " + vaultModifier.getDisplayDescriptionFormatted(modifierStack.getSize()))
                  .withStyle(ChatFormatting.DARK_GRAY);
               tooltip.add(description);
            }
         }
      }
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
