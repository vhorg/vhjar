package iskallia.vault.command;

import com.google.common.collect.Streams;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import iskallia.vault.VaultMod;
import iskallia.vault.altar.RequiredItems;
import iskallia.vault.core.Version;
import iskallia.vault.core.data.key.LootTableKey;
import iskallia.vault.core.data.key.VersionedKey;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.event.Event;
import iskallia.vault.core.random.JavaRandom;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.VaultRegistry;
import iskallia.vault.core.vault.influence.VaultGod;
import iskallia.vault.core.vault.player.Completion;
import iskallia.vault.core.vault.player.Listener;
import iskallia.vault.core.vault.stat.StatCollector;
import iskallia.vault.core.world.loot.generator.LootTableGenerator;
import iskallia.vault.core.world.loot.generator.TieredLootTableGenerator;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.dump.EntityAttrDump;
import iskallia.vault.dump.GearModelDump;
import iskallia.vault.dump.TranslationsDump;
import iskallia.vault.etching.EtchingRegistry;
import iskallia.vault.etching.EtchingSet;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.attribute.ability.AbilityLevelAttribute;
import iskallia.vault.gear.crafting.ProficiencyType;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.gear.trinket.TrinketEffect;
import iskallia.vault.gear.trinket.TrinketEffectRegistry;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.ErrorItem;
import iskallia.vault.item.gear.EtchingItem;
import iskallia.vault.item.gear.TrinketItem;
import iskallia.vault.skill.base.Skill;
import iskallia.vault.util.EntityHelper;
import iskallia.vault.util.InventoryUtil;
import iskallia.vault.util.VaultRarity;
import iskallia.vault.world.data.PlayerExpertisesData;
import iskallia.vault.world.data.PlayerProficiencyData;
import iskallia.vault.world.data.PlayerReputationData;
import iskallia.vault.world.data.PlayerStatsData;
import iskallia.vault.world.data.PlayerVaultStatsData;
import iskallia.vault.world.data.ServerVaults;
import iskallia.vault.world.data.VaultPlayerStats;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.ClickEvent.Action;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameRules.BooleanValue;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

public class DebugCommand extends Command {
   private static final SuggestionProvider<CommandSourceStack> SUGGEST_LOOT_TABLE = (context, builder) -> SharedSuggestionProvider.suggestResource(
      VaultRegistry.LOOT_TABLE.getKeys().stream().map(VersionedKey::getId), builder
   );

   @Override
   public String getName() {
      return "debug";
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
      builder.then(Commands.literal("dump_configs").executes(this::dumpConfigs));
      builder.then(
         Commands.literal("dump_blockstate")
            .then(
               Commands.argument("block_pos", BlockPosArgument.blockPos())
                  .executes(ctx -> this.dumpBlockstate(ctx, BlockPosArgument.getSpawnablePos(ctx, "block_pos")))
            )
      );
      builder.then(Commands.literal("dump_item_nbt").executes(this::dumpItemNBT));
      builder.then(Commands.literal("scan_inventory").executes(this::scanInventory));
      builder.then(
         Commands.literal("damage_all_gear").then(Commands.argument("damageAmount", IntegerArgumentType.integer(1)).executes(this::damageInventoryGear))
      );
      builder.then(Commands.literal("vault_kick").then(Commands.argument("player", EntityArgument.player()).executes(this::kickFromVault)));
      builder.then(Commands.literal("corrupt_gear").executes(this::corruptGear));
      builder.then(
         Commands.literal("add_ability_level_prefix")
            .then(
               Commands.argument("ability", StringArgumentType.word())
                  .then(Commands.argument("levelChange", IntegerArgumentType.integer()).executes(this::addAbilityLevel))
            )
      );
      builder.then(Commands.literal("apply_etching").then(Commands.argument("etching", ResourceLocationArgument.id()).executes(this::testApplyEtching)));
      builder.then(Commands.literal("give_all_etchings").executes(this::testGiveAllEtchings));
      builder.then(Commands.literal("give_all_trinkets").executes(this::testGiveAllTrinkets));
      builder.then(Commands.literal("dev_world").executes(this::setupDevWorld));
      builder.then(Commands.literal("reset_proficiencies").executes(this::resetProficiencies));
      builder.then(
         Commands.literal("insert_loot")
            .then(
               Commands.argument("pos", BlockPosArgument.blockPos())
                  .then(Commands.argument("id", ResourceLocationArgument.id()).suggests(SUGGEST_LOOT_TABLE).executes(this::insertLoot))
            )
      );
      builder.then(
         Commands.literal("generate_loot")
            .then(
               Commands.argument("id", ResourceLocationArgument.id())
                  .suggests(SUGGEST_LOOT_TABLE)
                  .then(
                     Commands.argument("quantity", FloatArgumentType.floatArg())
                        .then(
                           Commands.argument("rarity", FloatArgumentType.floatArg())
                              .then(Commands.argument("count", IntegerArgumentType.integer(1)).executes(this::generateLoot))
                        )
                  )
            )
      );
      builder.then(Commands.literal("debug_events").executes(this::debugEvents));
      builder.then(Commands.literal("prompt_vault_stats").executes(this::promptVaultStats));
      builder.then(
         ((LiteralArgumentBuilder)Commands.literal("altar_level").then(Commands.literal("get").executes(this::getAltarLevel)))
            .then(Commands.literal("set").then(Commands.argument("level", IntegerArgumentType.integer()).executes(this::setAltarLevel)))
      );
      builder.then(
         Commands.literal("expertise")
            .then(Commands.literal("reset_all").then(Commands.argument("player", EntityArgument.player()).executes(this::resetPlayerExpertises)))
      );
      builder.then(Commands.literal("expertise").then(Commands.literal("world_reset").executes(this::resetAllPlayerExpertises)));

      for (VaultGod god : VaultGod.values()) {
         builder.then(
            Commands.literal("reputation")
               .then(
                  Commands.literal(god.name().toLowerCase())
                     .then(Commands.argument("count", IntegerArgumentType.integer()).executes(context -> this.addReputation(god, context)))
               )
         );
      }
   }

   private int addReputation(VaultGod god, CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
      PlayerReputationData.addReputation(
         ((CommandSourceStack)context.getSource()).getPlayerOrException().getUUID(), god, IntegerArgumentType.getInteger(context, "count")
      );
      return 0;
   }

   private int generateLoot(CommandContext<CommandSourceStack> context) {
      ResourceLocation id = ResourceLocationArgument.getId(context, "id");
      float quantity = FloatArgumentType.getFloat(context, "quantity");
      float rarity = FloatArgumentType.getFloat(context, "rarity");
      int count = IntegerArgumentType.getInteger(context, "count");
      LootTableKey table = VaultRegistry.LOOT_TABLE.getKey(id);
      if (table == null) {
         ((CommandSourceStack)context.getSource()).sendFailure(new TextComponent("Invalid loot table " + id));
         return 0;
      } else {
         TieredLootTableGenerator gen = new TieredLootTableGenerator(Version.latest(), table, rarity, quantity, 54);
         Map<ResourceLocation, Integer> cache = new HashMap<>();
         Map<VaultRarity, Integer> rarities = new HashMap<>();

         for (int i = 0; i < count; i++) {
            gen.generate(JavaRandom.ofNanoTime());
            Iterator<ItemStack> it = gen.getItems();

            while (it.hasNext()) {
               ItemStack stack = it.next();
               ResourceLocation itemId = stack.getItem() == ModItems.ERROR_ITEM ? ErrorItem.getId(stack) : stack.getItem().getRegistryName();
               cache.put(itemId, cache.getOrDefault(itemId, 0) + stack.getCount());
               it.remove();
            }

            VaultRarity rarityEnum = ModConfigs.VAULT_CHEST.getRarity(gen.getCDF());
            rarities.put(rarityEnum, rarities.getOrDefault(rarityEnum, 0) + 1);
         }

         StringBuilder copy = new StringBuilder();

         for (VaultRarity value : VaultRarity.values()) {
            int frequency = rarities.getOrDefault(value, 0);
            MutableComponent text = new TextComponent("")
               .append(new TextComponent(frequency + " "))
               .append(new TextComponent(value.name()).setStyle(Style.EMPTY.withColor(value.color)));
            ((CommandSourceStack)context.getSource()).sendSuccess(text, false);
            copy.append(text.getString()).append("\n");
         }

         List<Entry<ResourceLocation, Integer>> entries = new ArrayList<>(cache.entrySet());
         entries.sort((o1, o2) -> Integer.compare(o2.getValue(), o1.getValue()));
         entries.forEach(e -> {
            Item item = (Item)Registry.ITEM.getOptional(e.getKey()).orElse(null);
            Component textx = (Component)(item != null ? item.getName(new ItemStack(item)) : new TextComponent(e.getKey().toString()));
            MutableComponent result = new TextComponent("").append(new TextComponent(e.getValue() + " ")).append(textx);
            ((CommandSourceStack)context.getSource()).sendSuccess(result, false);
            copy.append(result.getString()).append("\n");
         });
         ((CommandSourceStack)context.getSource())
            .sendSuccess(
               new TextComponent("[Copy]")
                  .withStyle(ChatFormatting.GOLD)
                  .withStyle(style -> style.withClickEvent(new ClickEvent(Action.COPY_TO_CLIPBOARD, copy.toString()))),
               false
            );
         return 0;
      }
   }

   private int corruptGear(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
      ServerPlayer player = ((CommandSourceStack)context.getSource()).getPlayerOrException();
      ItemStack held = player.getItemInHand(InteractionHand.MAIN_HAND);
      if (!held.isEmpty() && held.getItem() instanceof VaultGearItem) {
         VaultGearData data = VaultGearData.read(held);
         if (!data.isModifiable()) {
            player.sendMessage(new TextComponent("Item already corrupted"), Util.NIL_UUID);
            return 0;
         } else {
            data.updateAttribute(ModGearAttributes.IS_CORRUPTED, Boolean.valueOf(true));
            data.write(held);
            return 0;
         }
      } else {
         player.sendMessage(new TextComponent("Not holding VaultGear item"), Util.NIL_UUID);
         return 0;
      }
   }

   private int addAbilityLevel(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
      ServerPlayer player = ((CommandSourceStack)context.getSource()).getPlayerOrException();
      ItemStack held = player.getItemInHand(InteractionHand.MAIN_HAND);
      if (!held.isEmpty() && held.getItem() instanceof VaultGearItem) {
         String abilityKey = StringArgumentType.getString(context, "ability");
         if (!abilityKey.equals("all_abilities")) {
            Skill ability = ModConfigs.ABILITIES.getAbilityById(abilityKey).orElse(null);
            if (ability == null) {
               player.sendMessage(new TextComponent("Unknown ability: " + abilityKey), Util.NIL_UUID);
               return 0;
            }
         }

         int levelChange = IntegerArgumentType.getInteger(context, "levelChange");
         VaultGearData data = VaultGearData.read(held);
         data.addModifier(
            VaultGearModifier.AffixType.PREFIX, new VaultGearModifier<>(ModGearAttributes.ABILITY_LEVEL, new AbilityLevelAttribute(abilityKey, levelChange))
         );
         data.write(held);
         return 0;
      } else {
         player.sendMessage(new TextComponent("Not holding VaultGear item"), Util.NIL_UUID);
         return 0;
      }
   }

   private int resetAllPlayerExpertises(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
      ServerLevel level = ((CommandSourceStack)context.getSource()).getLevel();
      PlayerVaultStatsData statsData = PlayerVaultStatsData.get(level);
      statsData.resetAndReturnAllPlayerExpertisePoints(level);
      PlayerExpertisesData.get(level).resetAllPlayerExpertiseTrees(level);
      return 0;
   }

   private int resetPlayerExpertises(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
      ServerPlayer player = EntityArgument.getPlayer(context, "player");
      ServerLevel level = player.getLevel();
      PlayerVaultStatsData statsData = PlayerVaultStatsData.get(level);
      statsData.resetAndReturnExpertisePoints(player);
      PlayerExpertisesData.get(level).resetExpertiseTree(player);
      return 0;
   }

   private int resetProficiencies(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
      ServerPlayer player = ((CommandSourceStack)ctx.getSource()).getPlayerOrException();
      PlayerProficiencyData data = PlayerProficiencyData.get(player.getLevel());

      for (ProficiencyType type : ProficiencyType.values()) {
         data.setProficiency(player.getUUID(), type, 0);
      }

      data.sendProficiencyInformation(player);
      return 0;
   }

   private int getAltarLevel(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
      ServerPlayer player = ((CommandSourceStack)ctx.getSource()).getPlayerOrException();
      int level = PlayerStatsData.get().get(player).getCrystals().size();
      player.sendMessage(new TextComponent("Altar Level: " + level), player.getUUID());
      return 0;
   }

   private int setAltarLevel(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
      ServerPlayer player = ((CommandSourceStack)ctx.getSource()).getPlayerOrException();
      int level = IntegerArgumentType.getInteger(ctx, "level");
      PlayerStatsData playerStatsData = PlayerStatsData.get();
      playerStatsData.clearCrystals(player.getUUID());

      for (int i = 0; i <= level; i++) {
         playerStatsData.onCrystalCrafted(
            player.getUUID(),
            List.of(
               new RequiredItems("resource", List.of(ItemStack.EMPTY), 100),
               new RequiredItems("mob", List.of(ItemStack.EMPTY), 100),
               new RequiredItems("farmable", List.of(ItemStack.EMPTY), 100),
               new RequiredItems("misc", List.of(ItemStack.EMPTY), 100)
            )
         );
      }

      return 0;
   }

   private int promptVaultStats(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
      VaultPlayerStats.prompt(((CommandSourceStack)ctx.getSource()).getPlayerOrException());
      return 0;
   }

   private int debugEvents(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
      ServerPlayer player = ((CommandSourceStack)ctx.getSource()).getPlayerOrException();
      player.sendMessage(new TextComponent("~ Listing events ordered by highest priority ~").withStyle(ChatFormatting.GREEN), ChatType.SYSTEM, player.getUUID());
      List<Event<?, ?>> valid = new ArrayList<>();

      for (Event<?, ?> event : CommonEvents.REGISTRY) {
         for (Integer priority : event.getListeners().keySet()) {
            Map<Object, ? extends List<? extends Consumer<?>>> map = event.getListeners().get(priority);

            for (List<? extends Consumer<?>> listeners : map.values()) {
               if (!listeners.isEmpty()) {
                  valid.add(event);
               }
            }
         }
      }

      for (Event<?, ?> event : CommonEvents.REGISTRY) {
         if (valid.contains(event)) {
            player.sendMessage(
               new TextComponent("=== ")
                  .append(new TextComponent(event.getClass().getSimpleName()).withStyle(ChatFormatting.AQUA))
                  .append(new TextComponent(" ===").withStyle(ChatFormatting.WHITE)),
               ChatType.SYSTEM,
               player.getUUID()
            );

            for (Integer priority : event.getListeners().keySet()) {
               if (!event.getListeners().get(priority).isEmpty()) {
                  player.sendMessage(
                     new TextComponent("Priority: ")
                        .withStyle(ChatFormatting.WHITE)
                        .append(
                           new TextComponent(String.valueOf(priority))
                              .withStyle(priority == 0 ? ChatFormatting.GRAY : (priority < 0 ? ChatFormatting.RED : ChatFormatting.GREEN))
                        ),
                     ChatType.SYSTEM,
                     player.getUUID()
                  );

                  for (Entry<Object, ? extends List<? extends Consumer<?>>> map : event.getListeners().get(priority).entrySet()) {
                     player.sendMessage(
                        new TextComponent("   " + map.getKey().getClass().getSimpleName())
                           .withStyle(ChatFormatting.DARK_PURPLE)
                           .append(new TextComponent(" has ").withStyle(ChatFormatting.WHITE))
                           .append(new TextComponent(map.getValue().size() + "").withStyle(ChatFormatting.GOLD)),
                        ChatType.SYSTEM,
                        player.getUUID()
                     );
                  }
               }
            }
         }
      }

      return 0;
   }

   private int insertLoot(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
      BlockPos pos = BlockPosArgument.getLoadedBlockPos(context, "pos");
      ResourceLocation id = ResourceLocationArgument.getId(context, "id");
      BlockEntity blockEntity = ((CommandSourceStack)context.getSource()).getLevel().getBlockEntity(pos);
      if (blockEntity != null) {
         blockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler -> {
            LootTableGenerator generator = new LootTableGenerator(Version.latest(), VaultRegistry.LOOT_TABLE.getKey(id), 0.0F);
            generator.generate(JavaRandom.ofNanoTime());
            generator.getItems().forEachRemaining(stack -> ItemHandlerHelper.insertItem(handler, stack, false));
         });
      }

      return 0;
   }

   private int setupDevWorld(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
      ServerPlayer player = ((CommandSourceStack)context.getSource()).getPlayerOrException();
      MinecraftServer srv = player.getServer();
      ServerLevel level = player.getLevel();
      GameRules rules = level.getGameRules();
      ((BooleanValue)rules.getRule(GameRules.RULE_DAYLIGHT)).set(false, srv);
      ((BooleanValue)rules.getRule(GameRules.RULE_WEATHER_CYCLE)).set(false, srv);
      ((BooleanValue)rules.getRule(GameRules.RULE_DOMOBSPAWNING)).set(false, srv);
      ((BooleanValue)rules.getRule(GameRules.RULE_DOFIRETICK)).set(false, srv);
      ((BooleanValue)rules.getRule(GameRules.RULE_DO_TRADER_SPAWNING)).set(false, srv);
      level.setDayTime(6000L);
      level.setWeatherParameters(6000, 0, false, false);
      List<Entity> entities = Streams.stream(level.getEntities().getAll()).filter(entity -> !(entity instanceof Player)).toList();
      entities.forEach(entity -> entity.setRemoved(RemovalReason.DISCARDED));
      return 0;
   }

   private int testApplyEtching(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
      ServerPlayer player = ((CommandSourceStack)context.getSource()).getPlayerOrException();
      ItemStack held = player.getItemInHand(InteractionHand.MAIN_HAND);
      if (!held.isEmpty() && held.getItem() instanceof VaultGearItem) {
         ResourceLocation etchingKey = ResourceLocationArgument.getId(context, "etching");
         EtchingSet<?> etchingSet = EtchingRegistry.getEtchingSet(etchingKey);
         if (etchingSet == null) {
            player.sendMessage(new TextComponent("Unknown etching set: " + etchingKey), Util.NIL_UUID);
            return 0;
         } else {
            VaultGearData data = VaultGearData.read(held);
            data.updateAttribute(ModGearAttributes.ETCHING, etchingSet);
            data.write(held);
            return 0;
         }
      } else {
         player.sendMessage(new TextComponent("Not holding VaultGear item"), Util.NIL_UUID);
         return 0;
      }
   }

   private int testGiveAllTrinkets(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
      ServerPlayer player = ((CommandSourceStack)context.getSource()).getPlayerOrException();

      for (TrinketEffect<?> trinket : TrinketEffectRegistry.getOrderedEntries()) {
         EntityHelper.giveItem(player, TrinketItem.createRandomTrinket(trinket));
      }

      return 0;
   }

   private int testGiveAllEtchings(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
      ServerPlayer player = ((CommandSourceStack)context.getSource()).getPlayerOrException();

      for (EtchingSet<?> set : EtchingRegistry.getOrderedEntries()) {
         EntityHelper.giveItem(player, EtchingItem.createEtchingStack(set));
      }

      return 0;
   }

   private int kickFromVault(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
      ServerPlayer player = EntityArgument.getPlayer(context, "player");
      ServerLevel world = player.getLevel();
      ServerVaults.get(world).ifPresent(vault -> vault.ifPresent(Vault.LISTENERS, listeners -> {
         Listener listener = listeners.get(player.getUUID());
         listeners.remove((VirtualWorld)world, vault, listener);
         vault.ifPresent(Vault.STATS, collector -> {
            StatCollector stats = collector.get(listener.get(Listener.ID));
            stats.set(StatCollector.COMPLETION, Completion.BAILED);
         });
      }));
      return 0;
   }

   private int dumpBlockstate(CommandContext<CommandSourceStack> context, BlockPos blockPos) throws CommandSyntaxException {
      ServerPlayer player = ((CommandSourceStack)context.getSource()).getPlayerOrException();
      ServerLevel world = player.getLevel();
      BlockState blockState = world.getBlockState(blockPos);
      VaultMod.LOGGER.info("Blockstate {} = {}", blockPos, blockState);
      player.sendMessage(new TextComponent(blockState.toString()), Util.NIL_UUID);
      return 0;
   }

   private int damageInventoryGear(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
      ServerPlayer player = ((CommandSourceStack)context.getSource()).getPlayerOrException();
      int durabilityAmount = IntegerArgumentType.getInteger(context, "damageAmount");
      InventoryUtil.findAllItems(player).forEach(access -> {
         ItemStack foundStack = access.getStack();
         if (foundStack.getItem() instanceof VaultGearItem && foundStack.isDamageableItem()) {
            foundStack.hurtAndBreak(durabilityAmount, player, pl -> pl.broadcastBreakEvent(InteractionHand.MAIN_HAND));
            access.setStack(foundStack);
         }
      });
      return 0;
   }

   private int scanInventory(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
      ServerPlayer player = ((CommandSourceStack)context.getSource()).getPlayerOrException();
      String msg = InventoryUtil.findAllItems(player)
         .stream()
         .map(InventoryUtil.ItemAccess::getStack)
         .<CharSequence>map(ItemStack::toString)
         .collect(Collectors.joining(", "));
      VaultMod.LOGGER.info(msg);
      player.sendMessage(new TextComponent(msg), Util.NIL_UUID);
      return 0;
   }

   private int dumpItemNBT(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
      ServerPlayer player = ((CommandSourceStack)context.getSource()).getPlayerOrException();
      ItemStack heldStack = player.getMainHandItem();
      VaultMod.LOGGER.info("Held Stack NBT = {}", heldStack.getTag());
      player.sendMessage(new TextComponent(heldStack.getTag().toString()), Util.NIL_UUID);
      return 0;
   }

   private int dumpConfigs(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
      String gamePath = FMLPaths.GAMEDIR.get().toString();
      String dumpPath = gamePath + File.separator + "config-dump";

      try {
         new GearModelDump().dumpToFile(dumpPath);
         new EntityAttrDump().dumpToFile(dumpPath);
         new TranslationsDump().dumpToFile(dumpPath);
         return 0;
      } catch (IOException var5) {
         var5.printStackTrace();
         throw new CommandRuntimeException(new TextComponent("Unable to dump file.."));
      }
   }
}
