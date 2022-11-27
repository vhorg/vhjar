package iskallia.vault.client.render;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;

public class DynamicHumanoidModelLayer<T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>> extends HumanoidArmorLayer<T, M, A> {
   private boolean slim = false;
   private final A slimInner;
   private final A slimOuter;

   public DynamicHumanoidModelLayer(RenderLayerParent<T, M> parent, A inner, A outer, A slimInner, A slimOuter) {
      super(parent, inner, outer);
      this.slimInner = slimInner;
      this.slimOuter = slimOuter;
   }

   public void setSlim(boolean slim) {
      this.slim = slim;
   }

   public A getArmorModel(EquipmentSlot slot) {
      if (this.slim) {
         return slot == EquipmentSlot.LEGS ? this.slimInner : this.slimOuter;
      } else {
         return (A)super.getArmorModel(slot);
      }
   }
}
