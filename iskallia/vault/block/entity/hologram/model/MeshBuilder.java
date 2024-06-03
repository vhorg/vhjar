package iskallia.vault.block.entity.hologram.model;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.model.geom.builders.CubeDefinition;
import net.minecraft.world.phys.Vec2;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MeshBuilder {
   private Vec2 texture = Vec2.ZERO;
   private boolean mirrored = false;
   private List<CubeDefinition> cuboids = new ArrayList<>();

   public Vec2 getTexture() {
      return this.texture;
   }

   public void setTexture(Vec2 texture) {
      this.texture = texture;
   }

   public boolean isMirrored() {
      return this.mirrored;
   }

   public void setMirrored(boolean mirrored) {
      this.mirrored = mirrored;
   }

   public List<CubeDefinition> getCuboids() {
      return this.cuboids;
   }

   public MeshBuilder build(List<MeshBuilderAction> actions) {
      actions.forEach(action -> action.apply(this));
      return this;
   }
}
