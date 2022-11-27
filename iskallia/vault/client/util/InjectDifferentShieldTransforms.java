package iskallia.vault.client.util;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import iskallia.vault.mixin.AccessorBakedOverride;
import java.lang.reflect.Type;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrides.BakedOverride;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.BuiltInModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber({Dist.CLIENT})
public class InjectDifferentShieldTransforms {
   private static final Gson GSON = new GsonBuilder()
      .registerTypeAdapter(ItemTransform.class, new net.minecraft.client.renderer.block.model.ItemTransform.Deserializer())
      .registerTypeAdapter(InjectDifferentShieldTransforms.ShieldTransforms.class, new InjectDifferentShieldTransforms.Deserializer())
      .create();
   private static final String SHIELD_TRANSFORMS_JSON = "{\"thirdperson_righthand\":{\"rotation\":[0,90,0],\"translation\":[10,6,-4],\"scale\":[1,1,1]},\"thirdperson_lefthand\":{\"rotation\":[0,90,0],\"translation\":[10,6,12],\"scale\":[1,1,1]},\"firstperson_righthand\":{\"rotation\":[0,180,5],\"translation\":[-10,-0.75,-10],\"scale\":[1.25,1.25,1.25]},\"firstperson_lefthand\":{\"rotation\":[0,180,5],\"translation\":[10,-2.5,-10],\"scale\":[1.25,1.25,1.25]},\"gui\":{\"rotation\":[15,-25,-5],\"translation\":[2,3,0],\"scale\":[0.65,0.65,0.65]},\"fixed\":{\"rotation\":[0,180,0],\"translation\":[-2,4,-5],\"scale\":[0.5,0.5,0.5]},\"ground\":{\"rotation\":[0,0,0],\"translation\":[4,4,2],\"scale\":[0.25,0.25,0.25]}}";
   private static final String SHIELD_BLOCK_TRANSFORMS_JSON = "{\"thirdperson_righthand\":{\"rotation\":[45,135,0],\"translation\":[3.51,11,-2],\"scale\":[1,1,1]},\"thirdperson_lefthand\":{\"rotation\":[45,135,0],\"translation\":[13.51,3,5],\"scale\":[1,1,1]},\"firstperson_righthand\":{\"rotation\":[0,180,-5],\"translation\":[-15,1.25,-11],\"scale\":[1.25,1.25,1.25]},\"firstperson_lefthand\":{\"rotation\":[0,180,-5],\"translation\":[5,3,-11],\"scale\":[1.25,1.25,1.25]},\"gui\":{\"rotation\":[15,-25,-5],\"translation\":[2,3,0],\"scale\":[0.65,0.65,0.65]}}";

   @SubscribeEvent
   public static void onClientTick(ClientTickEvent event) {
      ModelManager itemModelManager = Minecraft.getInstance().getItemRenderer().getItemModelShaper().getModelManager();
      ModelResourceLocation mrl = new ModelResourceLocation("shield#inventory");
      BakedModel shieldModel = itemModelManager.getModel(mrl);
      if (shieldModel instanceof BuiltInModel model) {
         if (!(shieldModel.getTransforms() instanceof InjectDifferentShieldTransforms.ShieldTransforms)) {
            model.itemTransforms = (ItemTransforms)GSON.fromJson(
               "{\"thirdperson_righthand\":{\"rotation\":[0,90,0],\"translation\":[10,6,-4],\"scale\":[1,1,1]},\"thirdperson_lefthand\":{\"rotation\":[0,90,0],\"translation\":[10,6,12],\"scale\":[1,1,1]},\"firstperson_righthand\":{\"rotation\":[0,180,5],\"translation\":[-10,-0.75,-10],\"scale\":[1.25,1.25,1.25]},\"firstperson_lefthand\":{\"rotation\":[0,180,5],\"translation\":[10,-2.5,-10],\"scale\":[1.25,1.25,1.25]},\"gui\":{\"rotation\":[15,-25,-5],\"translation\":[2,3,0],\"scale\":[0.65,0.65,0.65]},\"fixed\":{\"rotation\":[0,180,0],\"translation\":[-2,4,-5],\"scale\":[0.5,0.5,0.5]},\"ground\":{\"rotation\":[0,0,0],\"translation\":[4,4,2],\"scale\":[0.25,0.25,0.25]}}",
               InjectDifferentShieldTransforms.ShieldTransforms.class
            );
         }

         ItemOverrides overrides = shieldModel.getOverrides();

         for (BakedOverride override : overrides.overrides) {
            if (override instanceof AccessorBakedOverride) {
               AccessorBakedOverride accessorOverride = (AccessorBakedOverride)override;
               BakedModel shieldOverrideModel = accessorOverride.getModel();
               if (!(shieldOverrideModel.getTransforms() instanceof InjectDifferentShieldTransforms.ShieldTransforms)
                  && shieldOverrideModel instanceof BuiltInModel overrideModel) {
                  overrideModel.itemTransforms = (ItemTransforms)GSON.fromJson(
                     "{\"thirdperson_righthand\":{\"rotation\":[45,135,0],\"translation\":[3.51,11,-2],\"scale\":[1,1,1]},\"thirdperson_lefthand\":{\"rotation\":[45,135,0],\"translation\":[13.51,3,5],\"scale\":[1,1,1]},\"firstperson_righthand\":{\"rotation\":[0,180,-5],\"translation\":[-15,1.25,-11],\"scale\":[1.25,1.25,1.25]},\"firstperson_lefthand\":{\"rotation\":[0,180,-5],\"translation\":[5,3,-11],\"scale\":[1.25,1.25,1.25]},\"gui\":{\"rotation\":[15,-25,-5],\"translation\":[2,3,0],\"scale\":[0.65,0.65,0.65]}}",
                     InjectDifferentShieldTransforms.ShieldTransforms.class
                  );
               }
            }
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Deserializer implements JsonDeserializer<InjectDifferentShieldTransforms.ShieldTransforms> {
      public InjectDifferentShieldTransforms.ShieldTransforms deserialize(JsonElement pJson, Type pType, JsonDeserializationContext pContext) throws JsonParseException {
         JsonObject jsonobject = pJson.getAsJsonObject();
         ItemTransform itemtransform = this.getTransform(pContext, jsonobject, "thirdperson_righthand");
         ItemTransform itemtransform1 = this.getTransform(pContext, jsonobject, "thirdperson_lefthand");
         if (itemtransform1 == ItemTransform.NO_TRANSFORM) {
            itemtransform1 = itemtransform;
         }

         ItemTransform itemtransform2 = this.getTransform(pContext, jsonobject, "firstperson_righthand");
         ItemTransform itemtransform3 = this.getTransform(pContext, jsonobject, "firstperson_lefthand");
         if (itemtransform3 == ItemTransform.NO_TRANSFORM) {
            itemtransform3 = itemtransform2;
         }

         ItemTransform itemtransform4 = this.getTransform(pContext, jsonobject, "head");
         ItemTransform itemtransform5 = this.getTransform(pContext, jsonobject, "gui");
         ItemTransform itemtransform6 = this.getTransform(pContext, jsonobject, "ground");
         ItemTransform itemtransform7 = this.getTransform(pContext, jsonobject, "fixed");
         Builder<TransformType, ItemTransform> builder = ImmutableMap.builder();

         for (TransformType type : TransformType.values()) {
            if (type.isModded()) {
               ItemTransform transform = this.getTransform(pContext, jsonobject, type.getSerializeName());
               if (transform != ItemTransform.NO_TRANSFORM) {
                  builder.put(type, transform);
               }
            }
         }

         return new InjectDifferentShieldTransforms.ShieldTransforms(
            itemtransform1, itemtransform, itemtransform3, itemtransform2, itemtransform4, itemtransform5, itemtransform6, itemtransform7, builder.build()
         );
      }

      private ItemTransform getTransform(JsonDeserializationContext pContext, JsonObject pJson, String pName) {
         return pJson.has(pName) ? (ItemTransform)pContext.deserialize(pJson.get(pName), ItemTransform.class) : ItemTransform.NO_TRANSFORM;
      }
   }

   public static class ShieldTransforms extends ItemTransforms {
      public ShieldTransforms(
         ItemTransform p_111798_,
         ItemTransform p_111799_,
         ItemTransform p_111800_,
         ItemTransform p_111801_,
         ItemTransform p_111802_,
         ItemTransform p_111803_,
         ItemTransform p_111804_,
         ItemTransform p_111805_,
         ImmutableMap<TransformType, ItemTransform> moddedTransforms
      ) {
         super(p_111798_, p_111799_, p_111800_, p_111801_, p_111802_, p_111803_, p_111804_, p_111805_, moddedTransforms);
      }
   }
}
