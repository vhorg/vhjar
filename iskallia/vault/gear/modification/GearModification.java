package iskallia.vault.gear.modification;

import com.google.common.collect.Lists;
import iskallia.vault.event.event.GearModificationEvent;
import iskallia.vault.gear.crafting.VaultGearCraftingHelper;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.registries.ForgeRegistryEntry;

public abstract class GearModification extends ForgeRegistryEntry<GearModification> {
   private final Component description;
   private final Component invalidDescription;

   public GearModification(ResourceLocation id) {
      this(id, component(id.getPath()), component(id.getPath() + ".invalid"));
   }

   public GearModification(ResourceLocation id, Component description, Component invalidDescription) {
      this.invalidDescription = invalidDescription;
      this.setRegistryName(id);
      this.description = description;
   }

   private static TranslatableComponent component(String append) {
      return new TranslatableComponent(String.format("the_vault.gear_modification.%s", append));
   }

   public List<Component> getDescription(ItemStack materialStack) {
      return Lists.newArrayList(new Component[]{this.description.copy()});
   }

   public Component getInvalidDescription(ItemStack materialStack) {
      return this.invalidDescription.copy().withStyle(ChatFormatting.RED);
   }

   public Predicate<ItemStack> getStackFilter() {
      return stack -> this.getDisplayStack().getItem() == stack.getItem();
   }

   public abstract ItemStack getDisplayStack();

   public abstract boolean doModification(ItemStack var1, ItemStack var2, Player var3, Random var4);

   public boolean apply(ItemStack stack, ItemStack materialStack, Player player, Random rand) {
      if (!this.doModification(stack, materialStack, player, rand)) {
         return false;
      } else {
         VaultGearCraftingHelper.reducePotential(stack, player, this);
         MinecraftForge.EVENT_BUS.post(new GearModificationEvent(player, this));
         return true;
      }
   }

   public boolean canApply(ItemStack stack, ItemStack materialStack, Player player, Random rand) {
      return this.doModification(stack.copy(), materialStack, player, rand);
   }
}
