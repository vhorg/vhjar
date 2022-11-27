package iskallia.vault.mixin;

import com.mojang.datafixers.util.Pair;
import java.util.List;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({StructureTemplatePool.class})
public interface AccessorStructureTemplatePool {
   @Accessor
   List<Pair<StructurePoolElement, Integer>> getRawTemplates();
}
