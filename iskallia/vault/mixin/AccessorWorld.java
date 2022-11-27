package iskallia.vault.mixin;

import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({Level.class})
public interface AccessorWorld {
   @Accessor("thread")
   Thread getThread();

   @Accessor("thread")
   @Mutable
   @Final
   void setThread(Thread var1);
}
