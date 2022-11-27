package iskallia.vault.block.wip_stuff;

import net.minecraft.world.item.ItemStack;

public interface ITool<S extends Enum<S>, P extends Enum<P>> {
   Class<P> getPerkClass();

   Class<S> getStatClass();

   int getStat(ItemStack var1, S var2);

   void setStat(ItemStack var1, S var2, int var3);

   int getPerk(ItemStack var1, P var2);

   void setPerk(ItemStack var1, P var2, int var3);

   default int getPerkPower(ItemStack stack) {
      return stack.getOrCreateTag().getInt("PerkPower");
   }

   default void setPerkPower(ItemStack stack, int power) {
      stack.getOrCreateTag().putInt("PerkPower", power);
   }

   default int getLevel(ItemStack stack) {
      return stack.getOrCreateTag().getInt("Level");
   }

   default void setLevel(ItemStack stack, int level) {
      stack.getOrCreateTag().putInt("Level", level);
   }
}
