package iskallia.vault.gear.attribute.ability.special.base;

import io.netty.buffer.ByteBuf;
import iskallia.vault.core.net.BitBuffer;
import net.minecraft.nbt.Tag;

public abstract class SpecialAbilityConfigValue {
   public abstract void write(BitBuffer var1);

   public abstract void netWrite(ByteBuf var1);

   public abstract Tag nbtWrite();
}
