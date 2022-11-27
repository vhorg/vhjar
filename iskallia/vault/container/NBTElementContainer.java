package iskallia.vault.container;

import iskallia.vault.container.spi.AbstractElementContainer;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.util.INBTSerializable;

public class NBTElementContainer<T extends INBTSerializable<CompoundTag>> extends AbstractElementContainer {
   protected T data;

   public NBTElementContainer(Supplier<MenuType<?>> menuTypeSupplier, int id, Player player, T data) {
      super(menuTypeSupplier.get(), id, player);
      this.data = data;
   }

   public T getData() {
      return this.data;
   }

   public boolean stillValid(@Nonnull Player player) {
      return true;
   }
}
