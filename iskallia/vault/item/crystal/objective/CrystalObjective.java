package iskallia.vault.item.crystal.objective;

import com.google.gson.JsonObject;
import iskallia.vault.item.crystal.CrystalProperty;
import iskallia.vault.item.crystal.data.serializable.ISerializable;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;

public abstract class CrystalObjective extends CrystalProperty implements ISerializable<CompoundTag, JsonObject> {
   public abstract Optional<Integer> getColor(float var1);
}
