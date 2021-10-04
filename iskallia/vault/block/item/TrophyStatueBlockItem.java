package iskallia.vault.block.item;

import iskallia.vault.client.gui.helper.UIHelper;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.util.StatueType;
import iskallia.vault.util.WeekKey;
import iskallia.vault.world.data.PlayerVaultStatsData;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.StringUtils;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TrophyStatueBlockItem extends LootStatueBlockItem {
   public TrophyStatueBlockItem(Block block) {
      super(block, StatueType.TROPHY);
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   protected void addStatueInformation(CompoundNBT dataTag, List<ITextComponent> toolTip) {
      super.addStatueInformation(dataTag, toolTip);
      if (dataTag.func_150297_b("recordEntry", 10) && dataTag.func_150297_b("trophyWeek", 10)) {
         WeekKey week = WeekKey.deserialize(dataTag.func_74775_l("trophyWeek"));
         PlayerVaultStatsData.PlayerRecordEntry recordEntry = PlayerVaultStatsData.PlayerRecordEntry.deserialize(dataTag.func_74775_l("recordEntry"));
         ITextComponent weekCmp = new StringTextComponent(week.getWeek() + " / " + week.getYear());
         ITextComponent recordCmp = new StringTextComponent(UIHelper.formatTimeString(recordEntry.getTickCount())).func_240699_a_(TextFormatting.GOLD);
         toolTip.add(StringTextComponent.field_240750_d_);
         toolTip.add(new StringTextComponent("Week: ").func_230529_a_(weekCmp));
         toolTip.add(new StringTextComponent("Record: ").func_230529_a_(recordCmp));
      }
   }

   public static ItemStack getTrophy(ServerWorld serverWorld, WeekKey week) {
      PlayerVaultStatsData statsData = PlayerVaultStatsData.get(serverWorld);
      PlayerVaultStatsData.PlayerRecordEntry record = statsData.getFastestVaultTime(week);
      if (StringUtils.func_151246_b(record.getPlayerName())) {
         return ItemStack.field_190927_a;
      } else {
         ItemStack stack = new ItemStack(ModBlocks.TROPHY_STATUE);
         CompoundNBT nbt = new CompoundNBT();
         nbt.func_74778_a("PlayerNickname", record.getPlayerName());
         nbt.func_74768_a("StatueType", StatueType.TROPHY.ordinal());
         nbt.func_74768_a("Interval", -1);
         nbt.func_218657_a("LootItem", ItemStack.field_190927_a.serializeNBT());
         nbt.func_74768_a("ItemsRemaining", -1);
         nbt.func_74768_a("TotalItems", -1);
         nbt.func_218657_a("trophyWeek", week.serialize());
         nbt.func_218657_a("recordEntry", record.serialize());
         CompoundNBT stackNBT = new CompoundNBT();
         stackNBT.func_218657_a("BlockEntityTag", nbt);
         stack.func_77982_d(stackNBT);
         return stack;
      }
   }
}
