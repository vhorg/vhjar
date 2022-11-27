package iskallia.vault.client.gui.screen.bounty.element.task;

import iskallia.vault.bounty.task.DamageTask;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;
import iskallia.vault.util.TextUtil;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.ForgeRegistries;

public class DamageEntityTaskElement extends AbstractTaskElement<DamageTask> {
   protected DamageEntityTaskElement(ISpatial spatial, DamageTask task) {
      super(spatial, task);
   }

   @Override
   protected float getProgressPercentage() {
      return (float)(this.getAmountObtained() / this.getAmountRequired());
   }

   @Override
   protected double getAmountRequired() {
      return this.getTask().getProperties().getAmount();
   }

   @Override
   protected double getAmountObtained() {
      return this.getTask().getAmountObtained();
   }

   @Override
   public List<MutableComponent> getDescription() {
      TextComponent entityName = TextUtil.formatLocationPathAsProperNoun(this.getTask().getProperties().getEntityId());
      List<MutableComponent> description = new ArrayList<>();
      description.add(new TextComponent("Deal Damage to ").append(entityName).append("s in:"));
      description.addAll(this.getDimensionsForDescription());
      return description;
   }

   @Override
   protected MutableComponent getTargetDisplayName() {
      EntityType<?> entity = (EntityType<?>)ForgeRegistries.ENTITIES.getValue(this.getTask().getProperties().getEntityId());
      return (MutableComponent)(entity == null
         ? TextUtil.formatLocationPathAsProperNoun(this.getTask().getProperties().getEntityId())
         : (MutableComponent)entity.getDescription());
   }

   @Override
   protected List<Component> getExtendedDisplay() {
      EntityType<?> entity = (EntityType<?>)ForgeRegistries.ENTITIES.getValue(this.getTask().getProperties().getEntityId());
      return List.of(
         (Component)(entity == null ? TextUtil.formatLocationPathAsProperNoun(this.getTask().getProperties().getEntityId()) : entity.getDescription())
      );
   }
}
