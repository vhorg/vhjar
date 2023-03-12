package iskallia.vault.gear.attribute.ability.special.base.template;

import com.google.gson.annotations.Expose;
import io.netty.buffer.ByteBuf;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.gear.attribute.ability.special.base.SpecialAbilityModification;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

public class FloatValueConfig extends SpecialAbilityModification.Config<FloatValueConfig> {
   @Expose
   private float value;

   public FloatValueConfig(float value) {
      this.value = value;
   }

   public float getValue() {
      return this.value;
   }

   public void write(BitBuffer buffer, FloatValueConfig config) {
      buffer.writeFloat(config.getValue());
   }

   public void netWrite(ByteBuf buf, FloatValueConfig config) {
      buf.writeFloat(config.getValue());
   }

   public Tag nbtWrite(FloatValueConfig config) {
      CompoundTag tag = new CompoundTag();
      tag.putFloat("value", config.getValue());
      return tag;
   }
}
