package iskallia.vault.world.vault.time.extension;

import iskallia.vault.VaultMod;
import iskallia.vault.item.ItemVaultFruit;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public class FruitExtension extends TimeExtension {
   public static final ResourceLocation ID = VaultMod.id("fruit");
   protected ItemVaultFruit fruit;

   public FruitExtension() {
   }

   public FruitExtension(ItemVaultFruit fruit) {
      this(ID, fruit);
   }

   public FruitExtension(ResourceLocation id, ItemVaultFruit fruit) {
      super(id, fruit.getExtraVaultTicks());
      this.fruit = fruit;
   }

   public ItemVaultFruit getFruit() {
      return this.fruit;
   }

   @Override
   public CompoundTag serializeNBT() {
      CompoundTag nbt = super.serializeNBT();
      nbt.putString("Fruit", this.getFruit().getRegistryName().toString());
      return nbt;
   }

   @Override
   public void deserializeNBT(CompoundTag nbt) {
      super.deserializeNBT(nbt);
      this.fruit = Registry.ITEM
         .getOptional(new ResourceLocation(nbt.getString("Fruit")))
         .filter(item -> item instanceof ItemVaultFruit)
         .map(item -> (ItemVaultFruit)item)
         .orElseThrow(() -> {
            VaultMod.LOGGER.error("Fruit item <" + nbt.getString("Fruit") + "> is not defined.");
            return new IllegalStateException();
         });
   }
}
