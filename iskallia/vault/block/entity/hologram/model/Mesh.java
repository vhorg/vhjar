package iskallia.vault.block.entity.hologram.model;

import java.util.List;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class Mesh {
   private int textureWidth;
   private int textureHeight;
   private List<MeshPart> parts;

   @OnlyIn(Dist.CLIENT)
   public LayerDefinition createMesh() {
      MeshDefinition data = new MeshDefinition();
      PartDefinition root = data.getRoot();

      for (MeshPart part : this.parts) {
         part.attach(root);
      }

      return LayerDefinition.create(data, this.textureWidth, this.textureHeight);
   }
}
