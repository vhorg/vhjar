package iskallia.vault.gear.attribute.ability.special.base.template.value;

import io.netty.buffer.ByteBuf;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.gear.attribute.ability.special.base.SpecialAbilityConfigValue;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.Tag;

public class IntValue extends SpecialAbilityConfigValue {
   private final int value;

   public IntValue(int value) {
      this.value = value;
   }

   public IntValue(BitBuffer buffer) {
      this.value = buffer.readInt();
   }

   public IntValue(ByteBuf buf) {
      this.value = buf.readInt();
   }

   public IntValue(Tag tag) {
      this.value = ((IntTag)tag).getAsInt();
   }

   public int getValue() {
      return this.value;
   }

   @Override
   public void write(BitBuffer buffer) {
      buffer.writeInt(this.value);
   }

   @Override
   public void netWrite(ByteBuf buf) {
      buf.writeInt(this.value);
   }

   @Override
   public Tag nbtWrite() {
      return IntTag.valueOf(this.value);
   }
}
