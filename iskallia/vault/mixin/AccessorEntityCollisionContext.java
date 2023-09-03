package iskallia.vault.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({EntityCollisionContext.class})
public interface AccessorEntityCollisionContext {
   @Accessor
   Entity getEntity();
}
