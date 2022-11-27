package iskallia.vault.mixin;

import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({Slot.class})
public interface AccessorSlot {
   @Accessor("x")
   @Mutable
   @Final
   void setX(int var1);

   @Accessor("y")
   @Mutable
   @Final
   void setY(int var1);
}
