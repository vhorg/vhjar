package iskallia.vault.item.tool;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.util.ClientScheduler;
import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.init.ModGearAttributes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class ToolItemRenderer extends SpecialItemRenderer {
   public static final ToolItemRenderer INSTANCE = new ToolItemRenderer();

   public void renderByItem(
      @NotNull ItemStack stack, @NotNull TransformType transformType, @NotNull PoseStack matrices, @NotNull MultiBufferSource buffer, int light, int overlay
   ) {
      ToolType type = ToolType.of(stack);
      VaultGearData data = VaultGearData.read(stack);
      ToolMaterial material = data.get(ModGearAttributes.TOOL_MATERIAL, VaultGearAttributeTypeMerger.of(() -> null, (a, b) -> b));
      if (material == null) {
         int total = type == null ? 16 * ToolType.values().length : 16;
         material = ToolMaterial.values()[(int)(ClientScheduler.INSTANCE.getTick() / total) % ToolMaterial.values().length];
      }

      if (type == null) {
         type = ToolType.values()[(int)(ClientScheduler.INSTANCE.getTick() >> 4) % ToolType.values().length];
      }

      ModelResourceLocation head = new ModelResourceLocation("the_vault:tool/%s/head/%s#inventory".formatted(type.getId(), material.getId()));
      ModelResourceLocation handle = new ModelResourceLocation("the_vault:tool/%s/handle#inventory".formatted(type.getId()));
      this.renderModel(handle, 16777215, stack, transformType, matrices, buffer, light, overlay, null);
      this.renderModel(head, 16777215, stack, transformType, matrices, buffer, light, overlay, null);
   }
}
