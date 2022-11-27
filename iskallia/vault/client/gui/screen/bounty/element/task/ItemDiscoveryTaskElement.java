package iskallia.vault.client.gui.screen.bounty.element.task;

import iskallia.vault.bounty.task.ItemDiscoveryTask;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;
import iskallia.vault.util.TextUtil;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag.Default;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemDiscoveryTaskElement extends AbstractTaskElement<ItemDiscoveryTask> {
   protected ItemDiscoveryTaskElement(ISpatial spatial, ItemDiscoveryTask task) {
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
      List<MutableComponent> components = new ArrayList<>();
      DecimalFormat df = new DecimalFormat("0");
      ResourceLocation itemId = this.getTask().getProperties().getItemId();
      Item item = (Item)ForgeRegistries.ITEMS.getValue(itemId);
      MutableComponent name = (MutableComponent)(item == null ? TextUtil.formatLocationPathAsProperNoun(itemId) : (MutableComponent)item.getDescription());
      components.add(
         new TextComponent("Find ")
            .append(new TextComponent(df.format(this.getTask().getProperties().getAmount()) + "x "))
            .append(name)
            .append(" in the Vault!")
      );
      return components;
   }

   @Override
   protected MutableComponent getTargetDisplayName() {
      ResourceLocation itemId = this.getTask().getProperties().getItemId();
      Item item = (Item)ForgeRegistries.ITEMS.getValue(itemId);
      return (MutableComponent)(item == null ? TextUtil.formatLocationPathAsProperNoun(itemId) : (MutableComponent)item.getDescription());
   }

   @Override
   protected List<Component> getExtendedDisplay() {
      ResourceLocation itemId = this.getTask().getProperties().getItemId();
      Item item = (Item)ForgeRegistries.ITEMS.getValue(itemId);
      return new ItemStack(item).getTooltipLines(Minecraft.getInstance().player, Default.ADVANCED);
   }
}
