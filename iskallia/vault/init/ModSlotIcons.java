package iskallia.vault.init;

import iskallia.vault.VaultMod;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.TextureStitchEvent.Pre;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   value = {Dist.CLIENT},
   bus = Bus.MOD,
   modid = "the_vault"
)
public class ModSlotIcons {
   public static final List<ResourceLocation> REGISTRY = new LinkedList<>();
   public static final ResourceLocation COINS_NO_ITEM = register("gui/slot/coins_no_item");
   public static final ResourceLocation PLATING_NO_ITEM = register("gui/slot/plating_no_item");
   public static final ResourceLocation TOOL_VISE_SLOT_0_NO_ITEM = register("gui/slot/tool_vise/0_no_item");
   public static final ResourceLocation TOOL_VISE_SLOT_1_NO_ITEM = register("gui/slot/tool_vise/1_no_item");
   public static final ResourceLocation TOOL_VISE_SLOT_2_NO_ITEM = register("gui/slot/tool_vise/2_no_item");
   public static final ResourceLocation TOOL_VISE_SLOT_3_NO_ITEM = register("gui/slot/tool_vise/3_no_item");
   public static final ResourceLocation TOOL_VISE_SLOT_4_NO_ITEM = register("gui/slot/tool_vise/4_no_item");
   public static final ResourceLocation TOOL_VISE_SLOT_5_NO_ITEM = register("gui/slot/tool_vise/5_no_item");
   public static final ResourceLocation MAGNET_TABLE_SLOT_0_NO_ITEM = register("gui/slot/magnet_table/0_no_item");
   public static final ResourceLocation MAGNET_TABLE_SLOT_1_NO_ITEM = register("gui/slot/magnet_table/1_no_item");
   public static final ResourceLocation MAGNET_TABLE_SLOT_2_NO_ITEM = register("gui/slot/magnet_table/2_no_item");
   public static final ResourceLocation MAGNET_TABLE_SLOT_3_NO_ITEM = register("gui/slot/magnet_table/3_no_item");

   private static ResourceLocation register(String path) {
      ResourceLocation icon = VaultMod.id(path);
      REGISTRY.add(icon);
      return icon;
   }

   @SubscribeEvent
   public static void stitchIcons(Pre event) {
      if (event.getAtlas().location() == InventoryMenu.BLOCK_ATLAS) {
         for (ResourceLocation icon : REGISTRY) {
            event.addSprite(icon);
         }
      }
   }
}