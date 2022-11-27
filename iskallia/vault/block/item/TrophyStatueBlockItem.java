package iskallia.vault.block.item;

import iskallia.vault.client.gui.helper.UIHelper;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModItems;
import iskallia.vault.util.WeekKey;
import iskallia.vault.world.data.PlayerVaultStatsData;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringUtil;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TrophyStatueBlockItem extends BlockItem {
   public TrophyStatueBlockItem(Block block) {
      super(block, new Properties().tab(ModItems.VAULT_MOD_GROUP).stacksTo(1));
   }

   @OnlyIn(Dist.CLIENT)
   public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> toolTip, TooltipFlag flagIn) {
      CompoundTag nbt = stack.getTag();
      if (nbt != null && nbt.contains("BlockEntityTag", 10)) {
         this.addStatueInformation(nbt.getCompound("BlockEntityTag"), toolTip);
      }

      super.appendHoverText(stack, worldIn, toolTip, flagIn);
   }

   @OnlyIn(Dist.CLIENT)
   protected void addStatueInformation(CompoundTag dataTag, List<Component> toolTip) {
      WeekKey week = WeekKey.deserialize(dataTag.getCompound("trophyWeek"));
      PlayerVaultStatsData.PlayerRecordEntry recordEntry = PlayerVaultStatsData.PlayerRecordEntry.deserialize(dataTag.getCompound("recordEntry"));
      Component weekCmp = new TextComponent(week.getWeek() + " / " + week.getYear());
      Component recordCmp = new TextComponent(UIHelper.formatTimeString(recordEntry.getTickCount())).withStyle(ChatFormatting.GOLD);
      toolTip.add(TextComponent.EMPTY);
      toolTip.add(new TextComponent("Week: ").append(weekCmp));
      toolTip.add(new TextComponent("Record: ").append(recordCmp));
   }

   public static ItemStack getTrophy(ServerLevel serverWorld, WeekKey week) {
      PlayerVaultStatsData statsData = PlayerVaultStatsData.get(serverWorld);
      PlayerVaultStatsData.PlayerRecordEntry record = statsData.getFastestVaultTime(week);
      if (StringUtil.isNullOrEmpty(record.getPlayerName())) {
         return ItemStack.EMPTY;
      } else {
         ItemStack stack = new ItemStack(ModBlocks.TROPHY_STATUE);
         CompoundTag nbt = new CompoundTag();
         nbt.putString("PlayerNickname", record.getPlayerName());
         nbt.put("TrophyWeek", week.serialize());
         nbt.put("RecordEntry", record.serialize());
         CompoundTag stackNBT = new CompoundTag();
         stackNBT.put("BlockEntityTag", nbt);
         stack.setTag(stackNBT);
         return stack;
      }
   }

   public ItemStack getDefaultInstance() {
      return super.getDefaultInstance();
   }

   protected boolean canPlace(BlockPlaceContext ctx, BlockState state) {
      if (!ctx.getItemInHand().hasTag()) {
         return false;
      } else {
         CompoundTag tag = ctx.getItemInHand().getOrCreateTag();
         CompoundTag blockTag = tag.getCompound("BlockEntityTag");
         return blockTag.contains("PlayerNickname", 8) && blockTag.contains("StatueType", 3) ? super.canPlace(ctx, state) : false;
      }
   }
}
