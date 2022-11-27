package iskallia.vault.init;

import iskallia.vault.block.model.VaultChestModel;
import iskallia.vault.block.render.VaultChestRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent.RegisterLayerDefinitions;
import net.minecraftforge.client.event.TextureStitchEvent.Pre;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   value = {Dist.CLIENT},
   bus = Bus.MOD
)
public class ModChestModels {
   public static final ResourceLocation CHEST_SHEET = new ResourceLocation("textures/atlas/chest.png");

   @SubscribeEvent
   @OnlyIn(Dist.CLIENT)
   public static void registerLayerDefinitions(RegisterLayerDefinitions event) {
      event.registerLayerDefinition(VaultChestModel.TREASURE_LOCATION, VaultChestModel::createTreasureLayer);
      event.registerLayerDefinition(VaultChestModel.MOSSY_LOCATION, VaultChestModel::createMossylayer);
      event.registerLayerDefinition(VaultChestModel.SCAVANGER_LOCATION, VaultChestModel::createScavangerLayer);
   }

   @SubscribeEvent
   @OnlyIn(Dist.CLIENT)
   public static void stitchTextures(Pre event) {
      if (event.getAtlas().location().equals(CHEST_SHEET)) {
         VaultChestRenderer.MATERIAL_MAP.values().forEach(m -> event.addSprite(m.texture()));
      }
   }
}
