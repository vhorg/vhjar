package iskallia.vault.mixin;

import java.util.List;
import net.minecraft.client.model.geom.builders.CubeDefinition;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({CubeListBuilder.class})
public interface AccessorCubeListBuilder {
   @Accessor("cubes")
   List<CubeDefinition> getCuboids();
}
