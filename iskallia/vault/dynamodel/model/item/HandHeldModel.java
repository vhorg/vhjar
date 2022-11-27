package iskallia.vault.dynamodel.model.item;

import iskallia.vault.dynamodel.DynamicModel;
import java.util.Map;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class HandHeldModel extends DynamicModel<HandHeldModel> {
   public HandHeldModel(ResourceLocation id, String displayName) {
      super(id, displayName);
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public BlockModel generateItemModel(Map<String, ResourceLocation> textures) {
      String jsonPattern = "{    \"parent\": \"item/generated\",    \"textures\": {{textures}},    \"display\": {        \"thirdperson_righthand\": {            \"rotation\": [ 0, -90, 55 ],            \"translation\": [ 0, 4.0, 0.5 ],            \"scale\": [ 0.85, 0.85, 0.85 ]        },        \"thirdperson_lefthand\": {            \"rotation\": [ 0, 90, -55 ],            \"translation\": [ 0, 4.0, 0.5 ],            \"scale\": [ 0.85, 0.85, 0.85 ]        },        \"firstperson_righthand\": {            \"rotation\": [ 0, -90, 25 ],            \"translation\": [ 1.13, 3.2, 1.13 ],            \"scale\": [ 0.68, 0.68, 0.68 ]        },        \"firstperson_lefthand\": {            \"rotation\": [ 0, 90, -25 ],            \"translation\": [ 1.13, 3.2, 1.13 ],            \"scale\": [ 0.68, 0.68, 0.68 ]        }    }}";
      return this.createUnbakedModel(jsonPattern, textures);
   }
}
