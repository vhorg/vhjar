package iskallia.vault.gear.attribute.ability.special.base.template;

import io.netty.buffer.ByteBuf;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.gear.attribute.ability.special.base.SpecialAbilityModification;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

public class NoOpConfig extends SpecialAbilityModification.Config<NoOpConfig> {
   public static final NoOpConfig INSTANCE = new NoOpConfig();

   private NoOpConfig() {
   }

   public void write(BitBuffer buffer, NoOpConfig config) {
   }

   public void netWrite(ByteBuf buf, NoOpConfig config) {
   }

   public Tag nbtWrite(NoOpConfig config) {
      return new CompoundTag();
   }
}
