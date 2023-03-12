package iskallia.vault.gear.modification.operation;

import com.google.common.collect.Lists;
import iskallia.vault.VaultMod;
import iskallia.vault.config.gear.VaultGearTagConfig;
import iskallia.vault.gear.VaultGearModifierHelper;
import iskallia.vault.gear.modification.GearModification;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.modification.ReforgeTagModificationFocus;
import java.util.List;
import java.util.Random;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ReforgeAddTaggedModification extends GearModification {
   public ReforgeAddTaggedModification() {
      super(VaultMod.id("reforge_all_add_tag"));
   }

   @Override
   public ItemStack getDisplayStack() {
      return new ItemStack(ModItems.FACETED_FOCUS);
   }

   @Override
   public List<Component> getDescription(ItemStack materialStack) {
      if (!materialStack.isEmpty() && materialStack.getItem() instanceof ReforgeTagModificationFocus) {
         VaultGearTagConfig.ModTagGroup modTag = ReforgeTagModificationFocus.getModifierTag(materialStack);
         return modTag == null
            ? Lists.newArrayList(
               new Component[]{
                  new TranslatableComponent("the_vault.gear_modification.reforge_all_add_tag.1"),
                  new TranslatableComponent("the_vault.gear_modification.reforge_all_add_tag.2")
               }
            )
            : Lists.newArrayList(
               new Component[]{
                  new TranslatableComponent("the_vault.gear_modification.reforge_all_add_tag.1"),
                  new TranslatableComponent("the_vault.gear_modification.reforge_all_add_tag.2.value", new Object[]{modTag.getDisplayComponent()})
               }
            );
      } else {
         return Lists.newArrayList(
            new Component[]{
               new TranslatableComponent("the_vault.gear_modification.reforge_all_add_tag.1"),
               new TranslatableComponent("the_vault.gear_modification.reforge_all_add_tag.2")
            }
         );
      }
   }

   @Override
   public Component getInvalidDescription(ItemStack materialStack) {
      if (!materialStack.isEmpty() && materialStack.getItem() instanceof ReforgeTagModificationFocus) {
         VaultGearTagConfig.ModTagGroup modTag = ReforgeTagModificationFocus.getModifierTag(materialStack);
         return (Component)(modTag == null
            ? super.getInvalidDescription(materialStack)
            : new TranslatableComponent("the_vault.gear_modification.reforge_all_add_tag.value.invalid", new Object[]{modTag.getDisplayComponent()})
               .withStyle(ChatFormatting.RED));
      } else {
         return super.getInvalidDescription(materialStack);
      }
   }

   @Override
   public boolean doModification(ItemStack stack, ItemStack materialStack, Player player, Random rand) {
      if (!materialStack.isEmpty() && materialStack.getItem() instanceof ReforgeTagModificationFocus) {
         VaultGearTagConfig.ModTagGroup modTag = ReforgeTagModificationFocus.getModifierTag(materialStack);
         return modTag == null ? false : VaultGearModifierHelper.reForgeAllWithTag(modTag, stack, rand);
      } else {
         return false;
      }
   }
}
