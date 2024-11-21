package iskallia.vault.gear.attribute.ability.special.base.template.value;

import io.netty.buffer.ByteBuf;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.gear.attribute.ability.special.base.SpecialAbilityConfigValue;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.Tag;

public class FloatValue extends SpecialAbilityConfigValue {
   private final float value;

   public FloatValue(float value) {
      this.value = value;
   }

   public FloatValue(BitBuffer buffer) {
      this.value = buffer.readFloat();
   }

   public FloatValue(ByteBuf buf) {
      this.value = buf.readFloat();
   }

   public FloatValue(Tag tag) {
      this.value = ((FloatTag)tag).getAsFloat();
   }

   public float getValue() {
      return this.value;
   }

   @Override
   public void write(BitBuffer buffer) {
      buffer.writeFloat(this.value);
   }

   @Override
   public void netWrite(ByteBuf buf) {
      buf.writeFloat(this.value);
   }

   @Override
   public Tag nbtWrite() {
      return FloatTag.valueOf(this.value);
   }
}
