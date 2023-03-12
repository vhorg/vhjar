package iskallia.vault.gear.attribute.ability.special.base.template;

import io.netty.buffer.ByteBuf;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.gear.attribute.ability.special.base.SpecialAbilityModification;
import javax.annotation.Nullable;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

public abstract class NoOpAbilityModification extends SpecialAbilityModification<NoOpConfig> {
   protected NoOpAbilityModification(ResourceLocation key) {
      super(key);
   }

   @Override
   public Class<NoOpConfig> getConfigClass() {
      return NoOpConfig.class;
   }

   public NoOpConfig read(BitBuffer buffer) {
      return NoOpConfig.INSTANCE;
   }

   public NoOpConfig netRead(ByteBuf buf) {
      return NoOpConfig.INSTANCE;
   }

   public NoOpConfig nbtRead(Tag tag) {
      return NoOpConfig.INSTANCE;
   }

   @Nullable
   public MutableComponent getValueDisplay(NoOpConfig config) {
      return null;
   }
}
