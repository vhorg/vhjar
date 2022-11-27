package iskallia.vault.mixin;

import net.minecraft.nbt.NbtAccounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin({NbtAccounter.class})
public abstract class MixinNBTSizeTracker {
   @Overwrite
   public void accountBits(long bits) {
   }
}
