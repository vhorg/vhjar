package iskallia.vault.mixin;

import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.init.ModItems;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({MatchTool.class})
public class MixinMatchTool {
   @Shadow
   @Final
   ItemPredicate predicate;

   @Inject(
      method = {"test(Lnet/minecraft/world/level/storage/loot/LootContext;)Z"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void test(LootContext context, CallbackInfoReturnable<Boolean> ci) {
      ItemStack stack = (ItemStack)context.getParamOrNull(LootContextParams.TOOL);
      if (stack != null && stack.getItem() == ModItems.TOOL) {
         VaultGearData data = VaultGearData.read(stack);
         if (this.predicate.matches(this.copy(Items.NETHERITE_PICKAXE, stack)) && data.get(ModGearAttributes.PICKING, VaultGearAttributeTypeMerger.anyTrue())) {
            ci.setReturnValue(true);
         } else if (this.predicate.matches(this.copy(Items.NETHERITE_AXE, stack)) && data.get(ModGearAttributes.AXING, VaultGearAttributeTypeMerger.anyTrue())) {
            ci.setReturnValue(true);
         } else if (this.predicate.matches(this.copy(Items.NETHERITE_SHOVEL, stack))
            && data.get(ModGearAttributes.SHOVELLING, VaultGearAttributeTypeMerger.anyTrue())) {
            ci.setReturnValue(true);
         } else if (this.predicate.matches(this.copy(Items.NETHERITE_HOE, stack))
            && data.get(ModGearAttributes.REAPING, VaultGearAttributeTypeMerger.anyTrue())) {
            ci.setReturnValue(true);
         } else if (this.predicate.matches(this.copy(Items.NETHERITE_SWORD, stack))
            && data.get(ModGearAttributes.REAPING, VaultGearAttributeTypeMerger.anyTrue())) {
            ci.setReturnValue(true);
         } else if (this.predicate.matches(this.copy(Items.SHEARS, stack)) && data.get(ModGearAttributes.REAPING, VaultGearAttributeTypeMerger.anyTrue())) {
            ci.setReturnValue(true);
         }
      }
   }

   private ItemStack copy(Item item, ItemStack reference) {
      CompoundTag nbt = reference.save(new CompoundTag());
      if (item.getRegistryName() == null) {
         nbt.putString("id", "minecraft:air");
      } else {
         nbt.putString("id", item.getRegistryName().toString());
      }

      return ItemStack.of(nbt);
   }
}
