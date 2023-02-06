package iskallia.vault.mixin;

import iskallia.vault.core.Version;
import iskallia.vault.core.random.JavaRandom;
import iskallia.vault.core.world.loot.generator.LootTableGenerator;
import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.init.ModItems;
import java.util.List;
import java.util.Optional;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({LootTable.class})
public class MixinLootTable {
   @Inject(
      method = {"getRandomItems(Lnet/minecraft/world/level/storage/loot/LootContext;)Ljava/util/List;"},
      at = {@At("RETURN")},
      cancellable = true
   )
   public void getRandomItems(LootContext context, CallbackInfoReturnable<List<ItemStack>> ci) {
      ItemStack stack = (ItemStack)context.getParamOrNull(LootContextParams.TOOL);
      Entity entity = (Entity)context.getParamOrNull(LootContextParams.THIS_ENTITY);
      if (stack != null && stack.getItem() == ModItems.TOOL) {
         VaultGearData data = VaultGearData.read(stack);
         List<ItemStack> loot = (List<ItemStack>)ci.getReturnValue();
         if (entity != null && entity.isShiftKeyDown()) {
            if (data.get(ModGearAttributes.SMELTING, VaultGearAttributeTypeMerger.anyTrue())) {
               this.handleSmelting(context, loot);
            }

            if (data.get(ModGearAttributes.PULVERIZING, VaultGearAttributeTypeMerger.anyTrue())) {
               this.handlePulverizing(context, loot);
            }
         } else {
            if (data.get(ModGearAttributes.PULVERIZING, VaultGearAttributeTypeMerger.anyTrue())) {
               this.handlePulverizing(context, loot);
            }

            if (data.get(ModGearAttributes.SMELTING, VaultGearAttributeTypeMerger.anyTrue())) {
               this.handleSmelting(context, loot);
            }
         }

         loot.removeIf(ItemStack::isEmpty);
         ci.setReturnValue(loot);
      }
   }

   private void handleSmelting(LootContext context, List<ItemStack> loot) {
      RecipeManager recipes = context.getLevel().getRecipeManager();

      for (int i = loot.size() - 1; i >= 0; i--) {
         ItemStack raw = loot.get(i);
         Optional<SmeltingRecipe> opt = recipes.getRecipeFor(RecipeType.SMELTING, new SimpleContainer(new ItemStack[]{raw}), context.getLevel());
         if (opt.isPresent()) {
            ItemStack smelted = opt.get().getResultItem().copy();
            smelted.setCount(raw.getCount() * smelted.getCount());
            loot.set(i, smelted);
         }
      }
   }

   private void handlePulverizing(LootContext context, List<ItemStack> loot) {
      for (int i = loot.size() - 1; i >= 0; i--) {
         ItemStack raw = loot.get(i);
         iskallia.vault.core.world.loot.LootTable table = ModConfigs.TOOL_PULVERIZING.get(raw.getItem());
         if (table != null) {
            loot.remove(i);
            LootTableGenerator generator = new LootTableGenerator(Version.latest(), table, 1.0F);
            generator.generate(JavaRandom.ofNanoTime());
            generator.getItems().forEachRemaining(pulverized -> {
               pulverized.setCount(raw.getCount() * pulverized.getCount());
               loot.add(pulverized);
            });
         }
      }
   }
}
