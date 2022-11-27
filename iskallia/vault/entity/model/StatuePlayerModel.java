package iskallia.vault.entity.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.world.entity.player.Player;

public class StatuePlayerModel extends PlayerModel<Player> {
   private final ModelPart rightArmS;
   private final ModelPart leftArmS;
   private final ModelPart leftSleeveS;
   private final ModelPart rightSleeveS;

   public StatuePlayerModel(Context context) {
      super(context.bakeLayer(ModelLayers.PLAYER), false);
      ModelPart modelPartSlim = context.bakeLayer(ModelLayers.PLAYER_SLIM);
      this.rightArmS = modelPartSlim.getChild("right_arm");
      this.leftArmS = modelPartSlim.getChild("left_arm");
      this.leftSleeveS = modelPartSlim.getChild("left_sleeve");
      this.rightSleeveS = modelPartSlim.getChild("right_sleeve");
   }

   protected Iterable<ModelPart> bodyParts() {
      return Iterables.concat(super.bodyParts(), ImmutableList.of(this.rightArmS, this.leftArmS, this.leftSleeveS, this.rightSleeveS));
   }

   public void setSlim(boolean slim) {
      this.rightArmS.visible = slim;
      this.leftArmS.visible = slim;
      this.leftSleeveS.visible = slim;
      this.rightSleeveS.visible = slim;
      this.rightArm.visible = !slim;
      this.leftArm.visible = !slim;
      this.leftSleeve.visible = !slim;
      this.rightSleeve.visible = !slim;
      this.rightSleeveS.copyFrom(this.rightSleeve);
      this.leftSleeveS.copyFrom(this.leftSleeve);
      this.rightArmS.copyFrom(this.rightArm);
      this.leftArmS.copyFrom(this.leftArm);
   }
}
