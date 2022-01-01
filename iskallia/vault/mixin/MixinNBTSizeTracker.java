package iskallia.vault.mixin;

import net.minecraft.nbt.NBTSizeTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin({NBTSizeTracker.class})
public class MixinNBTSizeTracker {
   @Overwrite
   public void func_152450_a(long bits) {
   }
}
