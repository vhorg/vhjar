package iskallia.vault.mixin;

import iskallia.vault.item.tool.ToolItem;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(
   value = {LootTable.class},
   priority = 100
)
public class MixinLootTable {
   private static final ResourceLocation QUIVER = new ResourceLocation("supplementaries:quiver");
   private static final ResourceLocation MANA_BOTTLE = new ResourceLocation("botania:mana_bottle");

   @Inject(
      method = {"getRandomItems(Lnet/minecraft/world/level/storage/loot/LootContext;)Ljava/util/List;"},
      at = {@At("RETURN")},
      cancellable = true
   )
   public void getRandomItems(LootContext context, CallbackInfoReturnable<List<ItemStack>> ci) {
      List<ItemStack> loot = (List<ItemStack>)ci.getReturnValue();
      if (ToolItem.handleLoot(context, loot)) {
         loot.removeIf(ItemStack::isEmpty);
         ci.setReturnValue(loot);
      }
   }

   @Inject(
      method = {"getRandomItems(Lnet/minecraft/world/level/storage/loot/LootContext;)Ljava/util/List;"},
      at = {@At("RETURN")},
      cancellable = true
   )
   public void removeItems(LootContext context, CallbackInfoReturnable<List<ItemStack>> ci) {
      List<ItemStack> loot = (List<ItemStack>)ci.getReturnValue();
      loot.removeIf(stack -> QUIVER.equals(stack.getItem().getRegistryName()) ? true : MANA_BOTTLE.equals(stack.getItem().getRegistryName()));
      ci.setReturnValue(loot);
   }
}
