package iskallia.vault.world.vault.time.extension;

import iskallia.vault.Vault;
import iskallia.vault.item.ItemVaultFruit;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class FruitExtension extends TimeExtension {
   public static final ResourceLocation ID = Vault.id("fruit");
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
   public CompoundNBT serializeNBT() {
      CompoundNBT nbt = super.serializeNBT();
      nbt.func_74778_a("Fruit", this.getFruit().getRegistryName().toString());
      return nbt;
   }

   @Override
   public void deserializeNBT(CompoundNBT nbt) {
      super.deserializeNBT(nbt);
      this.fruit = Registry.field_212630_s
         .func_241873_b(new ResourceLocation(nbt.func_74779_i("Fruit")))
         .filter(item -> item instanceof ItemVaultFruit)
         .map(item -> (ItemVaultFruit)item)
         .orElseThrow(() -> {
            Vault.LOGGER.error("Fruit item <" + nbt.func_74779_i("Fruit") + "> is not defined.");
            return new IllegalStateException();
         });
   }
}
