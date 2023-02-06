package iskallia.vault.client.gui.screen.bounty.element.task;

import iskallia.vault.bounty.task.MiningTask;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;
import iskallia.vault.client.gui.screen.bounty.element.BountyElement;
import iskallia.vault.util.TextUtil;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag.Default;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

public class MiningTaskElement extends AbstractTaskElement<MiningTask> {
   protected MiningTaskElement(ISpatial spatial, MiningTask task, BountyElement.Status status) {
      super(spatial, task, status);
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
      ResourceLocation itemId = this.getTask().getProperties().getBlockId();
      Block block = (Block)ForgeRegistries.BLOCKS.getValue(itemId);
      MutableComponent name = (MutableComponent)(block == null ? TextUtil.formatLocationPathAsProperNoun(itemId) : block.getName());
      components.add(
         new TextComponent("Mine ")
            .append(new TextComponent(df.format(this.getTask().getProperties().getAmount()) + "x "))
            .append(name)
            .append(" in The Vault!")
      );
      return components;
   }

   @Override
   protected MutableComponent getTargetDisplayName() {
      ResourceLocation itemId = this.getTask().getProperties().getBlockId();
      Block block = (Block)ForgeRegistries.BLOCKS.getValue(itemId);
      return (MutableComponent)(block == null ? TextUtil.formatLocationPathAsProperNoun(itemId) : block.getName());
   }

   @Override
   protected List<Component> getExtendedDisplay() {
      ResourceLocation itemId = this.getTask().getProperties().getBlockId();
      Block item = (Block)ForgeRegistries.BLOCKS.getValue(itemId);
      return new ItemStack(item).getTooltipLines(Minecraft.getInstance().player, Default.ADVANCED);
   }
}
