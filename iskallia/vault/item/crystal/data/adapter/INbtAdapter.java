package iskallia.vault.item.crystal.data.adapter;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.nbt.Tag;

public interface INbtAdapter<T, N extends Tag, C> {
   Optional<N> writeNbt(@Nullable T var1, C var2);

   Optional<T> readNbt(@Nullable N var1, C var2);
}
