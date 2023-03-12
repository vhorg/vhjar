package iskallia.vault.gear.attribute.ability.special.base.template;

import io.netty.buffer.ByteBuf;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.gear.attribute.ability.special.base.SpecialAbilityModification;
import java.util.function.Function;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;

public abstract class IntValueAbilityModification<C extends IntValueConfig> extends SpecialAbilityModification<IntValueConfig> {
   private final Function<Integer, C> configCtor;

   protected IntValueAbilityModification(ResourceLocation key) {
      this(key, value -> (C)(new IntValueConfig(value)));
   }

   protected IntValueAbilityModification(ResourceLocation key, Function<Integer, C> configCtor) {
      super(key);
      this.configCtor = configCtor;
   }

   public C read(BitBuffer buffer) {
      return this.configCtor.apply(buffer.readInt());
   }

   public C netRead(ByteBuf buf) {
      return this.configCtor.apply(buf.readInt());
   }

   public C nbtRead(Tag tag) {
      CompoundTag nbt = (CompoundTag)tag;
      return this.configCtor.apply(nbt.getInt("value"));
   }
}
