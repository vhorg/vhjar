package iskallia.vault.config.skillgate;

import com.google.gson.JsonObject;
import iskallia.vault.item.crystal.data.serializable.ISerializable;
import net.minecraft.nbt.CompoundTag;

public abstract class SkillGateType implements ISerializable<CompoundTag, JsonObject> {
   public abstract boolean allows(String var1);
}
