package iskallia.vault.mixin;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.StructureFeatureManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({StructureFeatureManager.class})
public interface AccessorStructureFeatureManager {
   @Accessor("level")
   LevelAccessor getLevelAccessor();
}
