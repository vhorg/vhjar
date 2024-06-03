package iskallia.vault.mixin;

import iskallia.vault.item.IAnvilPreventCombination;
import iskallia.vault.item.crystal.recipe.AnvilContext;
import iskallia.vault.item.crystal.recipe.AnvilMenuProxy;
import iskallia.vault.item.crystal.recipe.AnvilRecipe;
import iskallia.vault.item.crystal.recipe.AnvilRecipes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(
   value = {AnvilMenu.class},
   priority = 1001
)
public abstract class MixinAnvilMenu extends ItemCombinerMenu implements AnvilMenuProxy {
   @Shadow
   private String itemName;
   @Shadow
   @Final
   private DataSlot cost;
   @Shadow
   public int repairItemCountCost;
   @Unique
   private AnvilContext context;
   @Unique
   private AnvilRecipe recipe;
   @Unique
   private boolean fake;

   public MixinAnvilMenu(@Nullable MenuType<?> p_39773_, int p_39774_, Inventory p_39775_, ContainerLevelAccess p_39776_) {
      super(p_39773_, p_39774_, p_39775_, p_39776_);
   }

   @Override
   public boolean isFake() {
      return this.fake;
   }

   @Override
   public void setFake(boolean fake) {
      this.fake = fake;
   }

   @Inject(
      method = {"createResult"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;getEnchantments(Lnet/minecraft/world/item/ItemStack;)Ljava/util/Map;",
         ordinal = 1
      )},
      locals = LocalCapture.CAPTURE_FAILHARD,
      cancellable = true
   )
   protected void preventRepairResult(
      CallbackInfo ci, ItemStack inputLeft, int cost, int baseCost, int costIncrease, ItemStack inputLeftCopy, ItemStack inputRight
   ) {
      if (!inputLeftCopy.isEmpty() && !inputRight.isEmpty()) {
         if (inputLeftCopy.getItem() instanceof IAnvilPreventCombination preventCombination && preventCombination.shouldPreventAnvilCombination(inputRight)) {
            ci.cancel();
         }

         if (inputRight.getItem() instanceof IAnvilPreventCombination preventCombination && preventCombination.shouldPreventAnvilCombination(inputLeft)) {
            ci.cancel();
         }
      }
   }

   @Inject(
      method = {"createResult"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void createResult(CallbackInfo ci) {
      this.resultSlots.setItem(0, ItemStack.EMPTY);
      this.context = AnvilContext.ofAnvil(this.access, this.player, this.inputSlots, this.itemName);
      this.recipe = AnvilRecipes.get(this.context).orElse(null);
      if (this.recipe != null) {
         this.repairItemCountCost = 0;
         this.cost.set(this.context.getLevelCost());
         this.resultSlots.setItem(0, this.context.getOutput());
         this.broadcastChanges();
         ci.cancel();
      }
   }

   @Inject(
      method = {"onTake"},
      at = {@At("HEAD")},
      cancellable = true
   )
   protected void onTake(Player player, ItemStack stack, CallbackInfo ci) {
      if (this.recipe != null) {
         this.context.getTake().run();
         this.inputSlots.setItem(0, this.context.getInput()[0]);
         this.inputSlots.setItem(1, this.context.getInput()[1]);
         ci.cancel();
      }
   }

   @Inject(
      method = {"mayPickup"},
      at = {@At("HEAD")},
      cancellable = true
   )
   protected void mayPickup(Player player, boolean nonEmpty, CallbackInfoReturnable<Boolean> cir) {
      if (this.recipe != null) {
         cir.setReturnValue(player.getAbilities().instabuild || player.experienceLevel >= this.cost.get());
      }
   }
}
