package iskallia.vault.gear.modification;

import com.google.common.collect.Lists;
import iskallia.vault.event.event.GearModificationEvent;
import iskallia.vault.gear.crafting.VaultGearCraftingHelper;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.registries.ForgeRegistryEntry;

public abstract class GearModification extends ForgeRegistryEntry<GearModification> {
   private final Component description;

   public GearModification(ResourceLocation id) {
      this.setRegistryName(id);
      this.description = this.makeModificationComponent("description");
   }

   protected TranslatableComponent makeModificationComponent(String suffix, @Nullable Component... arguments) {
      return new TranslatableComponent(this.getTranslationKey(suffix), (Object[])(arguments == null ? new Object[0] : arguments));
   }

   protected String getTranslationKey(String suffix) {
      return String.format("the_vault.gear_modification.%s.%s", this.getRegistryName().getPath(), suffix);
   }

   protected String getGenericTranslationKey(String suffix) {
      return String.format("the_vault.gear_modification.%s", suffix);
   }

   public List<Component> getDescription(ItemStack materialStack) {
      return Lists.newArrayList(new Component[]{this.description.copy()});
   }

   public Predicate<ItemStack> getStackFilter() {
      return stack -> stack.is(this.getDisplayStack().getItem());
   }

   public abstract ItemStack getDisplayStack();

   public abstract GearModification.Result doModification(ItemStack var1, ItemStack var2, Player var3, Random var4);

   public boolean apply(ItemStack stack, ItemStack materialStack, Player player, Random rand) {
      if (!this.doModification(stack, materialStack, player, rand).success()) {
         return false;
      } else {
         VaultGearCraftingHelper.reducePotential(stack, player, this);
         MinecraftForge.EVENT_BUS.post(new GearModificationEvent(player, this));
         return true;
      }
   }

   public GearModification.Result canApply(ItemStack stack, ItemStack materialStack, Player player, Random rand) {
      return this.doModification(stack.copy(), materialStack, player, rand);
   }

   public record Result(boolean success, boolean genericError, String errorSuffixKey, @Nullable Component... arguments) {
      public static GearModification.Result makeSuccess() {
         return new GearModification.Result(true, false, "");
      }

      public static GearModification.Result makeGenericError(String suffix, @Nullable Component... arguments) {
         return new GearModification.Result(false, true, suffix, arguments);
      }

      public static GearModification.Result makeActionError(String suffix, @Nullable Component... arguments) {
         return new GearModification.Result(false, false, suffix, arguments);
      }

      public static GearModification.Result errorUnmodifiable() {
         return makeGenericError("unmodifiable");
      }

      public static GearModification.Result errorInternal() {
         return makeGenericError("internal_error");
      }

      public MutableComponent getError(GearModification mod) {
         if (this.genericError) {
            String key = mod.getGenericTranslationKey(this.errorSuffixKey);
            Object[] args = (Object[])(this.arguments == null ? new Object[0] : this.arguments);
            return new TranslatableComponent(key, args).withStyle(ChatFormatting.RED);
         } else {
            return mod.makeModificationComponent(this.errorSuffixKey).withStyle(ChatFormatting.RED);
         }
      }
   }
}
