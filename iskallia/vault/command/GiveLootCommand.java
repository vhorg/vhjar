package iskallia.vault.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import iskallia.vault.block.VaultCrateBlock;
import iskallia.vault.block.item.FinalVaultFrameBlockItem;
import iskallia.vault.block.item.LootStatueBlockItem;
import iskallia.vault.block.item.TrophyStatueBlockItem;
import iskallia.vault.config.LegacyLootTablesConfig;
import iskallia.vault.core.random.JavaRandom;
import iskallia.vault.core.vault.CrateLootGenerator;
import iskallia.vault.core.vault.objective.AwardCrateObjective;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.VaultDollItem;
import iskallia.vault.util.EntityHelper;
import iskallia.vault.util.WeekKey;
import iskallia.vault.world.data.PlayerVaultStatsData;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootContext.Builder;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.server.command.EnumArgument;

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
   public void build(LiteralArgumentBuilder<CommandSourceStack> builder) {
      builder.then(
         Commands.literal("arena_crate")
            .then(
               Commands.argument("champion", StringArgumentType.word())
                  .executes(
                     ctx -> this.giveArenaCrate(
                        ctx, ((CommandSourceStack)ctx.getSource()).getPlayerOrException(), StringArgumentType.getString(ctx, "champion")
                     )
                  )
            )
      );
      builder.then(
         Commands.literal("raffle_boss_crate")
            .then(
               Commands.argument("boss_name", StringArgumentType.word())
                  .executes(
                     ctx -> this.giveRaffleBossCrate(
                        ctx, ((CommandSourceStack)ctx.getSource()).getPlayerOrException(), StringArgumentType.getString(ctx, "boss_name")
                     )
                  )
            )
      );
      builder.then(
         Commands.literal("normal_boss_crate").executes(ctx -> this.giveNormalBossCrate(ctx, ((CommandSourceStack)ctx.getSource()).getPlayerOrException()))
      );
      builder.then(
         Commands.literal("loot_statue")
            .then(
               Commands.argument("name", StringArgumentType.word())
                  .executes(
                     ctx -> this.giveLootStatue(StringArgumentType.getString(ctx, "name"), ctx, ((CommandSourceStack)ctx.getSource()).getPlayerOrException())
                  )
            )
      );
      builder.then(
         Commands.literal("paxel")
            .then(
               Commands.argument("enhancementId", StringArgumentType.string())
                  .executes(
                     ctx -> this.givePaxel(
                        ctx, ((CommandSourceStack)ctx.getSource()).getPlayerOrException(), StringArgumentType.getString(ctx, "enhancementId")
                     )
                  )
            )
      );
      builder.then(Commands.literal("set_gear_name").then(Commands.argument("name", StringArgumentType.word()).executes(this::setGearName)));
      builder.then(
         Commands.literal("record_trophy")
            .then(
               Commands.argument("year", IntegerArgumentType.integer())
                  .then(Commands.argument("week", IntegerArgumentType.integer()).executes(this::giveTrophy))
            )
      );
      builder.then(Commands.literal("record_box").executes(this::giveTrophyBox));
      builder.then(
         Commands.literal("final_vault_frame")
            .then(
               Commands.argument("ownerUUID", UuidArgument.uuid())
                  .then(
                     Commands.argument("ownerNickname", StringArgumentType.word())
                        .executes(
                           ctx -> this.giveFinalVaultFrame(ctx, UuidArgument.getUuid(ctx, "ownerUUID"), StringArgumentType.getString(ctx, "ownerNickname"))
                        )
                  )
            )
      );
      builder.then(
         Commands.literal("vault_doll")
            .then(
               Commands.argument("playerIGN", StringArgumentType.word())
                  .executes(ctx -> this.giveVaultDoll(ctx, StringArgumentType.getString(ctx, "playerIGN")))
            )
      );
      builder.then(
         Commands.literal("crate")
            .then(
               Commands.argument("crateType", EnumArgument.enumArgument(VaultCrateBlock.Type.class))
                  .then(
                     ((RequiredArgumentBuilder)Commands.argument("level", IntegerArgumentType.integer(0))
                           .then(Commands.argument("player", EntityArgument.player()).executes(this::giveCrate)))
                        .executes(this::giveCrate)
                  )
            )
      );
   }

   private int giveCrate(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
      VaultCrateBlock.Type type = (VaultCrateBlock.Type)ctx.getArgument("crateType", VaultCrateBlock.Type.class);
      ItemStack crate = ItemStack.EMPTY;

      ServerPlayer player;
      try {
         player = EntityArgument.getPlayer(ctx, "player");
      } catch (CommandSyntaxException var8) {
         player = ((CommandSourceStack)ctx.getSource()).getPlayerOrException();
      }

      int level = IntegerArgumentType.getInteger(ctx, "level");
      switch (type) {
         case BOUNTY:
            crate = ModConfigs.REWARD_CONFIG.generateReward(level, "common").createRewardCrate();
            break;
         case CAKE:
         case MONOLITH:
         case SCAVENGER:
         case BOSS:
            AwardCrateObjective objective = AwardCrateObjective.ofConfig(type, type.toString().toLowerCase(), level, true);
            CrateLootGenerator crateLootGenerator = new CrateLootGenerator(
               objective.get(AwardCrateObjective.LOOT_TABLE),
               0.0F,
               objective.has(AwardCrateObjective.ADD_ARTIFACT),
               objective.get(AwardCrateObjective.ARTIFACT_CHANCE)
            );
            crate = VaultCrateBlock.getCrateWithLoot(type, crateLootGenerator.createLootForCommand(JavaRandom.ofInternal(new Random().nextLong()), level));
      }

      ItemHandlerHelper.giveItemToPlayer(player, crate);
      return 0;
   }

   private int setGearName(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
      ServerPlayer sPlayer = ((CommandSourceStack)context.getSource()).getPlayerOrException();
      ItemStack heldItem = sPlayer.getMainHandItem();
      return heldItem.isEmpty() ? 0 : 0;
   }

   public int giveTrophyBox(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
      ServerPlayer sPlayer = ((CommandSourceStack)context.getSource()).getPlayerOrException();
      ServerLevel sWorld = sPlayer.getLevel();
      Builder builder = new Builder(sWorld).withRandom(sWorld.random).withLuck(sPlayer.getLuck());
      int playerLevel = PlayerVaultStatsData.get(sWorld).getVaultStats(sPlayer.getUUID()).getVaultLevel();
      LegacyLootTablesConfig.Level config = ModConfigs.LOOT_TABLES.getForLevel(playerLevel);
      LootTable bossBonusTbl = sPlayer.getServer().getLootTables().get(config.getScavengerCrate());
      NonNullList<ItemStack> quickBossLoot = NonNullList.create();
      quickBossLoot.addAll(bossBonusTbl.getRandomItems(builder.create(LootContextParamSets.EMPTY)));
      Collections.shuffle(quickBossLoot);
      ItemStack box = new ItemStack(Items.WHITE_SHULKER_BOX);
      box.getOrCreateTag().put("BlockEntityTag", new CompoundTag());
      ContainerHelper.saveAllItems(box.getOrCreateTag().getCompound("BlockEntityTag"), quickBossLoot);
      sPlayer.addItem(box);
      sPlayer.sendMessage(new TextComponent("Generated Recordbox for Vault level " + playerLevel), Util.NIL_UUID);
      return 0;
   }

   public int giveTrophy(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
      ServerPlayer sPlayer = ((CommandSourceStack)context.getSource()).getPlayerOrException();
      int year = IntegerArgumentType.getInteger(context, "year");
      int week = IntegerArgumentType.getInteger(context, "week");
      ItemStack statue = TrophyStatueBlockItem.getTrophy(sPlayer.getLevel(), WeekKey.of(year, week));
      if (!statue.isEmpty()) {
         sPlayer.addItem(statue);
      } else {
         sPlayer.sendMessage(new TextComponent("No record set!"), Util.NIL_UUID);
      }

      return 0;
   }

   private int giveLootStatue(String name, CommandContext<CommandSourceStack> context, ServerPlayer player) {
      ItemStack statue = LootStatueBlockItem.getStatueBlockItem(name);
      player.addItem(statue);
      return 0;
   }

   public int giveArenaCrate(CommandContext<CommandSourceStack> context, ServerPlayer player, String championName) {
      ServerLevel world = player.getLevel();
      Builder builder = new Builder(world).withRandom(world.random).withLuck(player.getLuck());
      LootContext ctx = builder.create(LootContextParamSets.EMPTY);
      NonNullList<ItemStack> stacks = NonNullList.create();
      stacks.add(LootStatueBlockItem.getStatueBlockItem(championName));
      int level = PlayerVaultStatsData.get(world).getVaultStats(player).getVaultLevel();
      List<ItemStack> items = world.getServer().getLootTables().get(ModConfigs.LOOT_TABLES.getForLevel(level).getArenaCrate()).getRandomItems(ctx);
      stacks.addAll(items);
      ItemStack crate = VaultCrateBlock.getCrateWithLoot(VaultCrateBlock.Type.ARENA, stacks);
      EntityHelper.giveItem(player, crate);
      return 0;
   }

   public int giveNormalBossCrate(CommandContext<CommandSourceStack> context, ServerPlayer player) {
      ServerLevel world = player.getLevel();
      Builder builder = new Builder(world).withRandom(world.random).withLuck(player.getLuck());
      LootContext ctx = builder.create(LootContextParamSets.EMPTY);
      int level = PlayerVaultStatsData.get(world).getVaultStats(player).getVaultLevel();
      NonNullList<ItemStack> stacks = NonNullList.create();
      ItemStack crate = VaultCrateBlock.getCrateWithLoot(VaultCrateBlock.Type.BOSS, stacks);
      EntityHelper.giveItem(player, crate);
      return 0;
   }

   public int giveRaffleBossCrate(CommandContext<CommandSourceStack> context, ServerPlayer player, String bossName) {
      ServerLevel world = player.getLevel();
      Builder builder = new Builder(world).withRandom(world.random).withLuck(player.getLuck());
      LootContext ctx = builder.create(LootContextParamSets.EMPTY);
      NonNullList<ItemStack> stacks = NonNullList.create();
      stacks.add(LootStatueBlockItem.getStatueBlockItem(bossName));
      int level = PlayerVaultStatsData.get(world).getVaultStats(player).getVaultLevel();
      ItemStack crate = VaultCrateBlock.getCrateWithLoot(VaultCrateBlock.Type.BOSS, stacks);
      EntityHelper.giveItem(player, crate);
      return 0;
   }

   public int givePaxel(CommandContext<CommandSourceStack> context, ServerPlayer player, String enhancementSId) {
      ItemStack paxelStack = new ItemStack(ModItems.VAULTERITE_PICKAXE);
      EntityHelper.giveItem(player, paxelStack);
      return 0;
   }

   public int giveFinalVaultFrame(CommandContext<CommandSourceStack> context, UUID ownerUUID, String ownerNickname) throws CommandSyntaxException {
      ItemStack frameStack = new ItemStack(ModBlocks.FINAL_VAULT_FRAME_BLOCK_ITEM);
      FinalVaultFrameBlockItem.writeToItemStack(frameStack, ownerUUID, ownerNickname);
      ServerPlayer player = ((CommandSourceStack)context.getSource()).getPlayerOrException();
      EntityHelper.giveItem(player, frameStack);
      return 0;
   }

   private int giveVaultDoll(CommandContext<CommandSourceStack> context, String playerIGN) throws CommandSyntaxException {
      ServerPlayer serverPlayer = ((CommandSourceStack)context.getSource()).getPlayerOrException();
      ItemStack dollStack = new ItemStack(ModItems.VAULT_DOLL);
      ServerLevel serverLevel = ((CommandSourceStack)context.getSource()).getLevel();
      serverLevel.getServer().getProfileCache().get(playerIGN).ifPresentOrElse(gp -> {
         VaultDollItem.setNewDollAttributes(dollStack, gp, ((CommandSourceStack)context.getSource()).getLevel());
         EntityHelper.giveItem(serverPlayer, dollStack);
      }, () -> serverPlayer.sendMessage(new TextComponent("Unable to find player's IGN: " + playerIGN).withStyle(ChatFormatting.RED), Util.NIL_UUID));
      return 0;
   }
}
