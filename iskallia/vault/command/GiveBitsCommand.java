package iskallia.vault.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.ItemBit;
import iskallia.vault.util.EntityHelper;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;

public class GiveBitsCommand extends Command {
   public static List<ItemBit> BIT_ITEMS;

   private static void initializeBits() {
      BIT_ITEMS = new LinkedList<>();
      BIT_ITEMS.add(ModItems.BIT_10000);
      BIT_ITEMS.add(ModItems.BIT_5000);
      BIT_ITEMS.add(ModItems.BIT_1000);
      BIT_ITEMS.add(ModItems.BIT_100);
      BIT_ITEMS.sort(Comparator.comparingInt(b -> -b.getValue()));
   }

   @Override
   public String getName() {
      return "give_bits";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public void build(LiteralArgumentBuilder<CommandSource> builder) {
      builder.then(
         Commands.func_197056_a("amount", IntegerArgumentType.integer())
            .executes(context -> this.receivedSub(context, IntegerArgumentType.getInteger(context, "amount")))
      );
   }

   private int receivedSub(CommandContext<CommandSource> context, int amount) throws CommandSyntaxException {
      dropBits(((CommandSource)context.getSource()).func_197035_h(), amount);
      return 0;
   }

   public static void dropBits(ServerPlayerEntity player, int bitsInput) {
      if (BIT_ITEMS == null) {
         initializeBits();
      }

      List<ItemStack> itemsToGive = new LinkedList<>();

      for (ItemBit bitItem : BIT_ITEMS) {
         if (bitsInput >= bitItem.getValue()) {
            int amount = bitsInput / bitItem.getValue();
            itemsToGive.add(new ItemStack(bitItem, amount));
            bitsInput %= bitItem.getValue();
         }
      }

      for (ItemStack itemStack : itemsToGive) {
         EntityHelper.giveItem(player, itemStack);
      }
   }

   @Override
   public boolean isDedicatedServerOnly() {
      return false;
   }
}
