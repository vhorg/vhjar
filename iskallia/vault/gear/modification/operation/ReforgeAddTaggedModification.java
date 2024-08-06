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
                  this.makeModificationComponent("description.1", new Component[0]), this.makeModificationComponent("description.2", new Component[0])
               }
            )
            : Lists.newArrayList(
               new Component[]{
                  this.makeModificationComponent("description.1", new Component[0]),
                  new TranslatableComponent(this.getTranslationKey("description.2.value"), new Object[]{modTag.getDisplayComponent()})
               }
            );
      } else {
         return Lists.newArrayList(
            new Component[]{
               this.makeModificationComponent("description.1", new Component[0]), this.makeModificationComponent("description.2", new Component[0])
            }
         );
      }
   }

   @Override
   public GearModification.Result doModification(ItemStack stack, ItemStack materialStack, Player player, Random rand) {
      if (!materialStack.isEmpty() && materialStack.getItem() instanceof ReforgeTagModificationFocus) {
         VaultGearTagConfig.ModTagGroup modTag = ReforgeTagModificationFocus.getModifierTag(materialStack);
         return modTag == null ? GearModification.Result.makeActionError("no_tag") : VaultGearModifierHelper.reForgeAllWithTag(modTag, stack, rand);
      } else {
         return GearModification.Result.errorInternal();
      }
   }
}
