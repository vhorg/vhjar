package iskallia.vault.container.slot.player;

import com.mojang.datafixers.util.Pair;
import iskallia.vault.container.slot.ReadOnlySlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ArmorViewSlot extends ReadOnlySlot {
   private static final ResourceLocation[] ARMOR_SLOT_TEXTURES = new ResourceLocation[]{
      PlayerContainer.field_226616_d_, PlayerContainer.field_226617_e_, PlayerContainer.field_226618_f_, PlayerContainer.field_226619_g_
   };
   private final EquipmentSlotType equipmentSlotType;

   public ArmorViewSlot(PlayerEntity player, EquipmentSlotType equipmentSlotType, int xPosition, int yPosition) {
      super(player.field_71071_by, 39 - equipmentSlotType.func_188454_b(), xPosition, yPosition);
      this.equipmentSlotType = equipmentSlotType;
   }

   @OnlyIn(Dist.CLIENT)
   public Pair<ResourceLocation, ResourceLocation> func_225517_c_() {
      return Pair.of(PlayerContainer.field_226615_c_, ARMOR_SLOT_TEXTURES[this.equipmentSlotType.func_188454_b()]);
   }
}
