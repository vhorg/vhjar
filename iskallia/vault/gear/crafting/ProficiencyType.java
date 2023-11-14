package iskallia.vault.gear.crafting;

import iskallia.vault.init.ModItems;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;

public enum ProficiencyType {
   HELMET(() -> ModItems.HELMET.defaultItem()),
   CHESTPLATE(() -> ModItems.CHESTPLATE.defaultItem()),
   LEGGINGS(() -> ModItems.LEGGINGS.defaultItem()),
   BOOTS(() -> ModItems.BOOTS.defaultItem()),
   AXE(() -> ModItems.AXE.defaultItem()),
   SWORD(() -> ModItems.SWORD.defaultItem()),
   SHIELD(() -> ModItems.SHIELD.defaultItem()),
   IDOL(() -> ModItems.IDOL_BENEVOLENT.defaultItem()),
   WAND(() -> ModItems.WAND.defaultItem()),
   FOCUS(() -> ModItems.FOCUS.defaultItem()),
   MAGNET(() -> ModItems.MAGNET.defaultItem()),
   UNKNOWN(() -> ItemStack.EMPTY);

   private final Supplier<ItemStack> displayStack;

   private ProficiencyType(Supplier<ItemStack> displayStack) {
      this.displayStack = displayStack;
   }

   public Supplier<ItemStack> getDisplayStack() {
      return this.displayStack;
   }

   public MutableComponent getDisplayName() {
      return new TranslatableComponent(String.format("the_vault.proficiency.%s", this.name().toLowerCase(Locale.ROOT)));
   }

   public static List<ProficiencyType> getCraftableTypes() {
      List<ProficiencyType> proficiencies = new ArrayList<>(Arrays.asList(values()));
      proficiencies.remove(UNKNOWN);
      proficiencies.remove(IDOL);
      return proficiencies;
   }
}
