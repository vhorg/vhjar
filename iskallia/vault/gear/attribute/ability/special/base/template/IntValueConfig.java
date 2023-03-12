package iskallia.vault.gear.attribute.ability.special.base.template;

import com.google.gson.annotations.Expose;
import io.netty.buffer.ByteBuf;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.gear.attribute.ability.special.base.SpecialAbilityModification;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

public class IntValueConfig extends SpecialAbilityModification.Config<IntValueConfig> {
   @Expose
   private int value;

   public IntValueConfig(int value) {
      this.value = value;
   }

   public int getValue() {
      return this.value;
   }

   public void write(BitBuffer buffer, IntValueConfig config) {
      buffer.writeInt(config.getValue());
   }

   public void netWrite(ByteBuf buf, IntValueConfig config) {
      buf.writeInt(config.getValue());
   }

   public Tag nbtWrite(IntValueConfig config) {
      CompoundTag tag = new CompoundTag();
      tag.putInt("value", config.getValue());
      return tag;
   }
}
