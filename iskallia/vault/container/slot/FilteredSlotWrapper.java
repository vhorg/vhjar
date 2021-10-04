package iskallia.vault.container.slot;

import com.mojang.datafixers.util.Pair;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class FilteredSlotWrapper extends Slot {
   private final Slot decorated;
   private final Predicate<ItemStack> canInsert;

   public FilteredSlotWrapper(Slot decorated, Predicate<ItemStack> canInsert) {
      super(decorated.field_75224_c, decorated.getSlotIndex(), decorated.field_75223_e, decorated.field_75221_f);
      this.canInsert = canInsert;
      this.field_75222_d = decorated.field_75222_d;
      this.decorated = decorated;
   }

   public void func_75220_a(ItemStack oldStackIn, ItemStack newStackIn) {
      this.decorated.func_75220_a(oldStackIn, newStackIn);
   }

   public ItemStack func_190901_a(PlayerEntity thePlayer, ItemStack stack) {
      return this.decorated.func_190901_a(thePlayer, stack);
   }

   public boolean func_75214_a(ItemStack stack) {
      return !this.canInsert.test(stack) ? false : this.decorated.func_75214_a(stack);
   }

   public ItemStack func_75211_c() {
      return this.decorated.func_75211_c();
   }

   public boolean func_75216_d() {
      return this.decorated.func_75216_d();
   }

   public void func_75215_d(ItemStack stack) {
      this.decorated.func_75215_d(stack);
   }

   public void func_75218_e() {
      this.decorated.func_75218_e();
   }

   public int func_75219_a() {
      return this.decorated.func_75219_a();
   }

   public int func_178170_b(ItemStack stack) {
      return this.decorated.func_178170_b(stack);
   }

   @Nullable
   public Pair<ResourceLocation, ResourceLocation> func_225517_c_() {
      return this.decorated.func_225517_c_();
   }

   public ItemStack func_75209_a(int amount) {
      return this.decorated.func_75209_a(amount);
   }

   public boolean func_82869_a(PlayerEntity playerIn) {
      return this.decorated.func_82869_a(playerIn);
   }

   public boolean func_111238_b() {
      return this.decorated.func_111238_b();
   }

   public int getSlotIndex() {
      return this.decorated.getSlotIndex();
   }

   public boolean isSameInventory(Slot other) {
      return this.decorated.isSameInventory(other);
   }

   public Slot setBackground(ResourceLocation atlas, ResourceLocation sprite) {
      return this.decorated.setBackground(atlas, sprite);
   }
}
