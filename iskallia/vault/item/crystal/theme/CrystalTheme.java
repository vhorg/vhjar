package iskallia.vault.item.crystal.theme;

import com.google.gson.JsonObject;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.item.crystal.data.serializable.ISerializable;
import java.util.List;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;

public abstract class CrystalTheme implements ISerializable<CompoundTag, JsonObject> {
   public abstract void configure(Vault var1, RandomSource var2);

   public abstract void addText(List<Component> var1, TooltipFlag var2);

   public abstract Optional<Integer> getColor();
}
