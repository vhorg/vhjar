package iskallia.vault.item.crystal.model;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.item.crystal.CrystalData;
import iskallia.vault.item.crystal.data.serializable.ISerializable;
import iskallia.vault.item.tool.SpecialItemRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public abstract class CrystalModel implements ISerializable<CompoundTag, JsonObject> {
   public abstract void renderItem(
      SpecialItemRenderer var1, CrystalData var2, ItemStack var3, TransformType var4, PoseStack var5, MultiBufferSource var6, int var7, int var8
   );

   public abstract int getBlockColor(CrystalData var1, float var2);
}
