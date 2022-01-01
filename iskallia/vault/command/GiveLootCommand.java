package iskallia.vault.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import iskallia.vault.block.VaultCrateBlock;
import iskallia.vault.block.item.LootStatueBlockItem;
import iskallia.vault.block.item.TrophyStatueBlockItem;
import iskallia.vault.config.LootTablesConfig;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.util.EntityHelper;
import iskallia.vault.util.StatueType;
import iskallia.vault.util.WeekKey;
import iskallia.vault.world.data.PlayerVaultStatsData;
import iskallia.vault.world.data.VaultRaidData;
import java.util.Collections;
import java.util.List;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootContext.Builder;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.Util;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.server.ServerWorld;

public class GiveLootCommand extends Command {
   @Override
   public String getName() {
      return "give_loot";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public boolean isDedicatedServerOnly() {
      return false;
   }

   @Override
   public void build(LiteralArgumentBuilder<CommandSource> builder) {
      builder.then(
         Commands.func_197057_a("raffle_boss_crate")
            .then(
               Commands.func_197056_a("boss_name", StringArgumentType.word())
                  .executes(
                     ctx -> this.giveRaffleBossCrate(ctx, ((CommandSource)ctx.getSource()).func_197035_h(), StringArgumentType.getString(ctx, "boss_name"))
                  )
            )
      );
      builder.then(Commands.func_197057_a("normal_boss_crate").executes(ctx -> this.giveNormalBossCrate(ctx, ((CommandSource)ctx.getSource()).func_197035_h())));
      builder.then(Commands.func_197057_a("raid_reward_crate").executes(ctx -> this.giveRaidRewardCrate(ctx, ((CommandSource)ctx.getSource()).func_197035_h())));
      builder.then(
         Commands.func_197057_a("record_trophy")
            .then(
               Commands.func_197056_a("year", IntegerArgumentType.integer())
                  .then(Commands.func_197056_a("week", IntegerArgumentType.integer()).executes(this::giveTrophy))
            )
      );
      builder.then(Commands.func_197057_a("record_box").executes(this::giveTrophyBox));
   }

   public int giveTrophyBox(CommandContext<CommandSource> context) throws CommandSyntaxException {
      ServerPlayerEntity sPlayer = ((CommandSource)context.getSource()).func_197035_h();
      ServerWorld sWorld = sPlayer.func_71121_q();
      Builder builder = new Builder(sWorld).func_216023_a(sWorld.field_73012_v).func_186469_a(sPlayer.func_184817_da());
      int playerLevel = PlayerVaultStatsData.get(sWorld).getVaultStats(sPlayer.func_110124_au()).getVaultLevel();
      LootTablesConfig.Level config = ModConfigs.LOOT_TABLES.getForLevel(playerLevel);
      LootTable bossBonusTbl = sPlayer.func_184102_h().func_200249_aQ().func_186521_a(config.getScavengerCrate());
      NonNullList<ItemStack> quickBossLoot = NonNullList.func_191196_a();
      quickBossLoot.addAll(bossBonusTbl.func_216113_a(builder.func_216022_a(LootParameterSets.field_216260_a)));
      Collections.shuffle(quickBossLoot);
      ItemStack box = new ItemStack(Items.field_221972_gr);
      box.func_196082_o().func_218657_a("BlockEntityTag", new CompoundNBT());
      ItemStackHelper.func_191282_a(box.func_196082_o().func_74775_l("BlockEntityTag"), quickBossLoot);
      sPlayer.func_191521_c(box);
      sPlayer.func_145747_a(new StringTextComponent("Generated Recordbox for Vault level " + playerLevel), Util.field_240973_b_);
      return 0;
   }

   public int giveTrophy(CommandContext<CommandSource> context) throws CommandSyntaxException {
      ServerPlayerEntity sPlayer = ((CommandSource)context.getSource()).func_197035_h();
      int year = IntegerArgumentType.getInteger(context, "year");
      int week = IntegerArgumentType.getInteger(context, "week");
      ItemStack statue = TrophyStatueBlockItem.getTrophy(sPlayer.func_71121_q(), WeekKey.of(year, week));
      if (!statue.func_190926_b()) {
         sPlayer.func_191521_c(statue);
      } else {
         sPlayer.func_145747_a(new StringTextComponent("No record set!"), Util.field_240973_b_);
      }

      return 0;
   }

   private int giveRaidRewardCrate(CommandContext<CommandSource> ctx, ServerPlayerEntity player) {
      EntityHelper.giveItem(player, VaultRaidData.generateRaidRewardCrate());
      return 0;
   }

   public int giveNormalBossCrate(CommandContext<CommandSource> context, ServerPlayerEntity player) {
      ServerWorld world = player.func_71121_q();
      Builder builder = new Builder(world).func_216023_a(world.field_73012_v).func_186469_a(player.func_184817_da());
      LootContext ctx = builder.func_216022_a(LootParameterSets.field_216260_a);
      int level = PlayerVaultStatsData.get(world).getVaultStats(player).getVaultLevel();
      NonNullList<ItemStack> stacks = NonNullList.func_191196_a();
      stacks.addAll(world.func_73046_m().func_200249_aQ().func_186521_a(ModConfigs.LOOT_TABLES.getForLevel(level).getBossCrate()).func_216113_a(ctx));
      ItemStack crate = VaultCrateBlock.getCrateWithLoot(ModBlocks.VAULT_CRATE, stacks);
      EntityHelper.giveItem(player, crate);
      return 0;
   }

   public int giveRaffleBossCrate(CommandContext<CommandSource> context, ServerPlayerEntity player, String bossName) {
      ServerWorld world = player.func_71121_q();
      Builder builder = new Builder(world).func_216023_a(world.field_73012_v).func_186469_a(player.func_184817_da());
      LootContext ctx = builder.func_216022_a(LootParameterSets.field_216260_a);
      NonNullList<ItemStack> stacks = NonNullList.func_191196_a();
      stacks.add(LootStatueBlockItem.getStatueBlockItem(bossName, StatueType.VAULT_BOSS));
      int level = PlayerVaultStatsData.get(world).getVaultStats(player).getVaultLevel();
      List<ItemStack> items = world.func_73046_m().func_200249_aQ().func_186521_a(ModConfigs.LOOT_TABLES.getForLevel(level).getBossCrate()).func_216113_a(ctx);
      stacks.addAll(items);
      ItemStack crate = VaultCrateBlock.getCrateWithLoot(ModBlocks.VAULT_CRATE, stacks);
      EntityHelper.giveItem(player, crate);
      return 0;
   }
}
