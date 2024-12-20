package iskallia.vault.item.crystal.data.serializable;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.util.INBTSerializable;

public interface INbtSerializable<N extends Tag> extends INBTSerializable<N> {
   Optional<N> writeNbt();

   void readNbt(N var1);

   @Deprecated
   @Nullable
   default N serializeNBT() {
      return this.writeNbt().orElse(null);
   }

   @Deprecated
   default void deserializeNBT(N nbt) {
      this.readNbt(nbt);
   }
}
