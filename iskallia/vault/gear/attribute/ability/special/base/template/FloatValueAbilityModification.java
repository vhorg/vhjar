package iskallia.vault.gear.attribute.ability.special.base.template;

import io.netty.buffer.ByteBuf;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.gear.attribute.ability.special.base.SpecialAbilityModification;
import java.util.function.Function;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;

public abstract class FloatValueAbilityModification<C extends FloatValueConfig> extends SpecialAbilityModification<FloatValueConfig> {
   private final Function<Float, C> configCtor;

   protected FloatValueAbilityModification(ResourceLocation key) {
      this(key, value -> (C)(new FloatValueConfig(value)));
   }

   protected FloatValueAbilityModification(ResourceLocation key, Function<Float, C> configCtor) {
      super(key);
      this.configCtor = configCtor;
   }

   public C read(BitBuffer buffer) {
      return this.configCtor.apply(buffer.readFloat());
   }

   public C netRead(ByteBuf buf) {
      return this.configCtor.apply(buf.readFloat());
   }

   public C nbtRead(Tag tag) {
      CompoundTag nbt = (CompoundTag)tag;
      return this.configCtor.apply(nbt.getFloat("value"));
   }
}
