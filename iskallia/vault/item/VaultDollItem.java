package iskallia.vault.item;

import com.mojang.authlib.GameProfile;
import iskallia.vault.block.VaultCrateBlock;
import iskallia.vault.block.VaultOreBlock;
import iskallia.vault.client.render.DollISTER;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.event.common.ChestGenerationEvent;
import iskallia.vault.core.event.common.CoinStacksGenerationEvent;
import iskallia.vault.core.event.common.CrateAwardEvent;
import iskallia.vault.core.event.common.LootableBlockGenerationEvent;
import iskallia.vault.core.vault.ClientVaults;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.VaultLevel;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModEntities;
import iskallia.vault.init.ModItems;
import iskallia.vault.integration.IntegrationCurios;
import iskallia.vault.util.VHSmpUtil;
import iskallia.vault.world.data.DollLootData;
import iskallia.vault.world.data.ServerVaults;
import iskallia.vault.world.data.VaultPartyData;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.LootContext.Builder;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.IItemRenderProperties;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import org.jetbrains.annotations.Nullable;

@EventBusSubscriber(
   bus = Bus.FORGE
)
public class VaultDollItem extends BasicItem {
   private static final String PLAYER_PROFILE_TAG = "playerProfile";
   private static final String VAULT_UUID_TAG = "vaultUUID";
   private static final String XP_POINTS_TAG = "xpPoints";
   private static final String XP_PERCENT_TAG = "xpPercent";
   private static final String LOOT_PERCENT_TAG = "lootPercent";

   public VaultDollItem(ResourceLocation id, Properties properties) {
      super(id, properties);
      CommonEvents.CHEST_LOOT_GENERATION.register(this, this::handleChestLoot, -1);
      CommonEvents.COIN_STACK_LOOT_GENERATION.register(this, this::handleCoinStackLoot, -1);
      CommonEvents.LOOTABLE_BLOCK_GENERATION_EVENT.register(this, this::handleLootableBlockLoot, -1);
      CommonEvents.CRATE_AWARD_EVENT.register(this, this::handleCrateLoot, -1);
   }

   private void handleCrateLoot(CrateAwardEvent.Data data) {
      if (data.getPhase() == CrateAwardEvent.Phase.PRE) {
         ServerPlayer player = data.getPlayer();
         ServerLevel serverLevel = player.getLevel();
         Vault vault = data.getVault();
         UUID vaultId = vault.get(Vault.ID);
         int level = vault.getOptional(Vault.LEVEL).map(VaultLevel::get).orElse(0);
         ItemStack doll = getFirstDollMatching(player, stack -> isDollOwnedByDifferentPlayer(serverLevel, stack, player) && this.isTheSameVault(stack, vaultId));
         if (doll.isEmpty()) {
            return;
         }

         getDollUUID(doll).ifPresent(dollId -> {
            float percentage = getLootPercent(doll);
            if (data.getRandom().nextFloat() < percentage) {
               NonNullList<ItemStack> items = data.getCrateLootGenerator().generate(vault, data.getListener(), data.getRandom());
               ItemStack crate = VaultCrateBlock.getCrateWithLootWithAntiques(data.getCrateType(), level, items);
               DollLootData.get(serverLevel, dollId).addLoot(crate);
            }
         });
      }
   }

   private void handleLootableBlockLoot(LootableBlockGenerationEvent.Data data) {
      if (data.getPhase() == LootableBlockGenerationEvent.Phase.POST) {
         this.handleLoot(data.getPlayer(), data::getLoot);
      }
   }

   private void handleCoinStackLoot(CoinStacksGenerationEvent.Data data) {
      if (data.getPhase() == CoinStacksGenerationEvent.Phase.POST) {
         this.handleLoot(data.getPlayer(), data::getLoot);
      }
   }

   private void handleChestLoot(ChestGenerationEvent.Data data) {
      if (data.getPhase() == ChestGenerationEvent.Phase.POST) {
         this.handleLoot(data.getPlayer(), data::getLoot);
      }
   }

   private void handleLoot(ServerPlayer player, Supplier<List<ItemStack>> getLoot) {
      if (player != null) {
         ServerLevel serverLevel = player.getLevel();
         getPlayerVaultId(serverLevel)
            .ifPresent(
               vaultId -> {
                  ItemStack doll = getFirstDollMatching(
                     player, stack -> isDollOwnedByDifferentPlayer(serverLevel, stack, player) && this.isTheSameVault(stack, vaultId)
                  );
                  if (!doll.isEmpty()) {
                     getDollUUID(doll).ifPresent(dollId -> addPercentageOfLoot(serverLevel, doll, dollId, getLoot.get()));
                  }
               }
            );
      }
   }

   @SubscribeEvent
   public static void onDollTooltip(ItemTooltipEvent event) {
      ItemStack stack = event.getItemStack();
      if (stack.getItem() == ModItems.VAULT_DOLL) {
         List<Component> tooltip = event.getToolTip();
         getPlayerGameProfile(stack)
            .ifPresent(
               gp -> {
                  tooltip.add(
                     1,
                     new TranslatableComponent("tooltip.the_vault.doll_owner", new Object[]{new TextComponent(gp.getName()).withStyle(ChatFormatting.WHITE)})
                        .withStyle(ChatFormatting.GRAY)
                  );
                  float lootPercent = getLootPercent(stack);
                  ChatFormatting lootColor = getRangeBasedColor(
                     lootPercent, ModConfigs.VAULT_ITEMS.VAULT_DOLL.lootPercentageMin, ModConfigs.VAULT_ITEMS.VAULT_DOLL.lootPercentageMax
                  );
                  tooltip.add(
                     2,
                     new TranslatableComponent(
                           "tooltip.the_vault.doll_loot_efficiency",
                           new Object[]{new TextComponent(String.format("%d", (int)(lootPercent * 100.0F)) + "%").withStyle(lootColor)}
                        )
                        .withStyle(ChatFormatting.GRAY)
                  );
                  float xpPercent = getXpPercent(stack);
                  ChatFormatting xpColor = getRangeBasedColor(
                     xpPercent, ModConfigs.VAULT_ITEMS.VAULT_DOLL.xpPercentageMin, ModConfigs.VAULT_ITEMS.VAULT_DOLL.xpPercentageMax
                  );
                  tooltip.add(
                     3,
                     new TranslatableComponent(
                           "tooltip.the_vault.doll_experience_efficiency",
                           new Object[]{new TextComponent(String.format("%d", (int)(xpPercent * 100.0F)) + "%").withStyle(xpColor)}
                        )
                        .withStyle(ChatFormatting.GRAY)
                  );
                  Player player = event.getPlayer();
                  if (player != null) {
                     getVaultUUID(stack)
                        .ifPresentOrElse(
                           vaultId -> {
                              boolean isInThisVault = ClientVaults.getActive().map(vault -> vaultId.equals(vault.get(Vault.ID))).orElse(false);
                              if (isInThisVault) {
                                 tooltip.add(4, new TranslatableComponent("tooltip.the_vault.doll_status.active").withStyle(ChatFormatting.YELLOW));
                              } else {
                                 tooltip.add(
                                    4,
                                    new TranslatableComponent(
                                          "tooltip.the_vault.doll_status.completed_by", new Object[]{getCompletedBy(stack), getVaultLevel(stack)}
                                       )
                                       .withStyle(ChatFormatting.GRAY)
                                 );
                                 tooltip.add(5, new TranslatableComponent("tooltip.the_vault.doll_status.completed").withStyle(ChatFormatting.GREEN));
                              }
                           },
                           () -> tooltip.add(4, new TranslatableComponent("tooltip.the_vault.doll_status.ready").withStyle(ChatFormatting.WHITE))
                        );
                  }
               }
            );
      }
   }

   private static String getCompletedBy(ItemStack doll) {
      return doll.getOrCreateTag().getString("completedBy");
   }

   private static int getVaultLevel(ItemStack doll) {
      return doll.getOrCreateTag().getInt("vaultLevel");
   }

   @SubscribeEvent(
      priority = EventPriority.LOW
   )
   public static void onOreBroken(BreakEvent event) {
      BlockState state = event.getState();
      if (state.getBlock() instanceof VaultOreBlock && (Boolean)state.getValue(VaultOreBlock.GENERATED)) {
         Player player = event.getPlayer();
         Level level = player.getLevel();
         if (!player.isCreative() && level instanceof ServerLevel serverLevel) {
            getPlayerVaultId(serverLevel).ifPresent(vaultId -> {
               ItemStack doll = getFirstDollMatching(player, stack -> isDollSetToVault(stack, vaultId));
               if (!doll.isEmpty()) {
                  addPercentageOfBlockDrops(event, state, player, level, serverLevel, doll);
               }
            });
         }
      }
   }

   private static void addPercentageOfBlockDrops(BreakEvent event, BlockState state, Player player, Level level, ServerLevel serverLevel, ItemStack doll) {
      BlockPos pos = event.getPos();
      ItemStack tool = event.getPlayer().getMainHandItem();
      getDollUUID(doll)
         .ifPresent(
            dollId -> {
               Builder lootContext = new Builder(serverLevel)
                  .withRandom(level.random)
                  .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                  .withParameter(LootContextParams.TOOL, tool)
                  .withOptionalParameter(LootContextParams.THIS_ENTITY, player)
                  .withOptionalParameter(LootContextParams.BLOCK_ENTITY, null);
               List<ItemStack> drops = state.getBlock().getDrops(state, lootContext);
               addPercentageOfLoot(serverLevel, doll, dollId, drops);
            }
         );
   }

   private static ChatFormatting getRangeBasedColor(float value, float min, float max) {
      float thirdOfRange = (max - min) / 3.0F;
      if (value < min + thirdOfRange) {
         return ChatFormatting.RED;
      } else {
         return value < max - thirdOfRange ? ChatFormatting.GOLD : ChatFormatting.GREEN;
      }
   }

   public void onCraftedBy(ItemStack stack, Level level, Player player) {
      setNewDollAttributes(stack, player);
   }

   private static Optional<UUID> getPlayerVaultId(ServerLevel serverLevel) {
      return ServerVaults.get(serverLevel).map(vault -> vault.get(Vault.ID));
   }

   public static void markDollOnVaultJoin(VirtualWorld world, Player player, UUID vaultId) {
      ItemStack doll = getFirstDollMatching(player, stack -> isDollOwnedByDifferentPlayer(world, stack, player) && getVaultUUID(stack).isEmpty());
      if (!doll.isEmpty()) {
         setVaultUUID(doll, vaultId);
      }
   }

   public static void onVaultCompletion(Player player, UUID vaultId, int vaultLevel, int experiencePoints) {
      ItemStack doll = getFirstDollMatching(player, stack -> isDollSetToVault(stack, vaultId));
      if (!doll.isEmpty()) {
         float xpPercentage = getXpPercent(doll);
         setExperience(doll, (int)(xpPercentage * experiencePoints));
         setCompletedBy(doll, player.getDisplayName().getString(), vaultLevel);
      }
   }

   private static boolean isDollSetToVault(ItemStack stack, UUID vaultId) {
      return stack.getItem() == ModItems.VAULT_DOLL && getVaultUUID(stack).map(uuid -> uuid.equals(vaultId)).orElse(false);
   }

   private static void setExperience(ItemStack doll, int experiencePoints) {
      if (doll.getItem() instanceof VaultDollItem) {
         doll.getOrCreateTag().putInt("xpPoints", experiencePoints);
      }
   }

   public static int getExperience(CompoundTag tag) {
      return tag.getInt("xpPoints");
   }

   private static void addPercentageOfLoot(ServerLevel serverLevel, ItemStack doll, UUID dollId, List<ItemStack> items) {
      float percentage = getLootPercent(doll);
      items.forEach(stack -> {
         if (serverLevel.random.nextFloat() < percentage) {
            DollLootData.get(serverLevel, dollId).addLoot(stack.copy());
         }
      });
   }

   private static ItemStack getFirstDollMatching(Player player, Predicate<ItemStack> stackMatcher) {
      if (ModList.get().isLoaded("curios")) {
         ItemStack result = IntegrationCurios.getItemFromCuriosHeadSlot(player, stackMatcher);
         if (!result.isEmpty()) {
            return result;
         }
      }

      ItemStack result = getFromInventory(player.getInventory().offhand, stackMatcher);
      return !result.isEmpty() ? result : getFromInventory(player.getInventory().items, stackMatcher);
   }

   private static boolean isDollOwnedByDifferentPlayer(ServerLevel serverLevel, ItemStack stack, Player player) {
      return stack.getItem() == ModItems.VAULT_DOLL
         && getPlayerUUID(stack)
            .map(dollOnwerId -> !dollOnwerId.equals(player.getUUID()) && isDollOwnedByPlayerNotInPartyWithPlayer(serverLevel, dollOnwerId, player.getUUID()))
            .orElse(false);
   }

   private static boolean isDollOwnedByPlayerNotInPartyWithPlayer(ServerLevel serverLevel, UUID dollOwnerId, UUID playerId) {
      return VaultPartyData.get(serverLevel).getParty(playerId).map(party -> !party.hasMember(dollOwnerId)).orElse(true);
   }

   private boolean isTheSameVault(ItemStack stack, UUID vaultId) {
      return getVaultUUID(stack).map(uuid -> uuid.equals(vaultId)).orElse(false);
   }

   private static ItemStack getFromInventory(NonNullList<ItemStack> inventoryStacks, Predicate<ItemStack> stackMatcher) {
      for (ItemStack itemStack : inventoryStacks) {
         if (stackMatcher.test(itemStack)) {
            return itemStack;
         }
      }

      return ItemStack.EMPTY;
   }

   public static void setNewDollAttributes(ItemStack doll, Player player) {
      setNewDollAttributes(doll, player.getGameProfile(), player.getLevel());
   }

   public static void setNewDollAttributes(ItemStack doll, GameProfile gameProfile, Level level) {
      if (doll.getItem() instanceof VaultDollItem) {
         CompoundTag tag = doll.getOrCreateTag();
         setGameProfile(tag, gameProfile);
         setRandomChances(tag, level);
      }
   }

   private static void setRandomChances(CompoundTag tag, Level level) {
      tag.putFloat("xpPercent", level.random.nextFloat(ModConfigs.VAULT_ITEMS.VAULT_DOLL.xpPercentageMin, ModConfigs.VAULT_ITEMS.VAULT_DOLL.xpPercentageMax));
      tag.putFloat(
         "lootPercent", level.random.nextFloat(ModConfigs.VAULT_ITEMS.VAULT_DOLL.lootPercentageMin, ModConfigs.VAULT_ITEMS.VAULT_DOLL.lootPercentageMax)
      );
   }

   private static float getXpPercent(ItemStack doll) {
      return doll.getItem() instanceof VaultDollItem ? doll.getOrCreateTag().getFloat("xpPercent") : 0.0F;
   }

   private static float getLootPercent(ItemStack doll) {
      return doll.getItem() instanceof VaultDollItem ? doll.getOrCreateTag().getFloat("lootPercent") : 0.0F;
   }

   public static void setGameProfile(CompoundTag tag, GameProfile gameProfile) {
      tag.put("playerProfile", NbtUtils.writeGameProfile(new CompoundTag(), gameProfile));
   }

   private static void setCompletedBy(ItemStack doll, String completedBy, int vaultLevel) {
      CompoundTag tag = doll.getOrCreateTag();
      tag.putString("completedBy", completedBy);
      tag.putInt("vaultLevel", vaultLevel);
   }

   public static Optional<GameProfile> getPlayerGameProfile(ItemStack doll) {
      return doll.getItem() instanceof VaultDollItem ? getPlayerGameProfile(doll.getOrCreateTag()) : Optional.empty();
   }

   public static Optional<GameProfile> getPlayerGameProfile(@Nullable CompoundTag tag) {
      return tag != null && tag.contains("playerProfile") ? Optional.ofNullable(NbtUtils.readGameProfile(tag.getCompound("playerProfile"))) : Optional.empty();
   }

   private static Optional<UUID> getDollUUID(ItemStack doll) {
      if (doll.getItem() instanceof VaultDollItem) {
         CompoundTag tag = doll.getOrCreateTag();
         return getDollUUID(tag);
      } else {
         return Optional.empty();
      }
   }

   public static Optional<UUID> getDollUUID(CompoundTag tag) {
      Optional<UUID> vaultUUID = getUUID(tag, "vaultUUID");
      Optional<UUID> playerUUID = getPlayerGameProfile(tag).flatMap(gp -> Optional.of(gp.getId()));
      return vaultUUID.isPresent() && playerUUID.isPresent()
         ? Optional.of(new UUID(vaultUUID.get().getMostSignificantBits(), playerUUID.get().getLeastSignificantBits()))
         : Optional.empty();
   }

   private static Optional<UUID> getPlayerUUID(ItemStack doll) {
      return getPlayerGameProfile(doll).flatMap(gp -> Optional.of(gp.getId()));
   }

   private static Optional<UUID> getUUID(ItemStack doll, String uuidTag) {
      if (doll.getItem() instanceof VaultDollItem) {
         CompoundTag tag = doll.getOrCreateTag();
         return getUUID(tag, uuidTag);
      } else {
         return Optional.empty();
      }
   }

   private static Optional<UUID> getUUID(CompoundTag tag, String uuidTag) {
      return !tag.contains(uuidTag) ? Optional.empty() : Optional.of(tag.getUUID(uuidTag));
   }

   private static void setVaultUUID(ItemStack doll, UUID vaultId) {
      if (doll.getItem() instanceof VaultDollItem) {
         CompoundTag tag = doll.getOrCreateTag();
         tag.putUUID("vaultUUID", vaultId);
      }
   }

   private static Optional<UUID> getVaultUUID(ItemStack doll) {
      return getUUID(doll, "vaultUUID");
   }

   public void initializeClient(Consumer<IItemRenderProperties> consumer) {
      consumer.accept(new IItemRenderProperties() {
         public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
            return DollISTER.INSTANCE;
         }
      });
   }

   public InteractionResult useOn(UseOnContext context) {
      ItemStack stack = context.getItemInHand();
      Player player = context.getPlayer();
      if (player != null
         && !ServerVaults.get(player.level).isPresent()
         && !VHSmpUtil.isArenaWorld(player)
         && !getVaultUUID(stack).isEmpty()
         && !this.playerCannotPlaceDoll(stack, player)) {
         Level level = context.getLevel();
         if (level instanceof ServerLevel serverLevel) {
            BlockPos clickedPos = context.getClickedPos();
            Direction direction = context.getClickedFace();
            BlockState blockstate = level.getBlockState(clickedPos);
            BlockPos placementPos;
            if (blockstate.getCollisionShape(level, clickedPos).isEmpty()) {
               placementPos = clickedPos;
            } else {
               placementPos = clickedPos.relative(direction);
            }

            if (ModEntities.DOLL_MINI_ME
                  .spawn(
                     serverLevel,
                     stack,
                     player,
                     placementPos,
                     MobSpawnType.SPAWN_EGG,
                     true,
                     !Objects.equals(clickedPos, placementPos) && direction == Direction.UP
                  )
               != null) {
               stack.shrink(1);
               level.gameEvent(player, GameEvent.ENTITY_PLACE, clickedPos);
            }

            return InteractionResult.CONSUME;
         } else {
            return InteractionResult.SUCCESS;
         }
      } else {
         return InteractionResult.PASS;
      }
   }

   private boolean playerCannotPlaceDoll(ItemStack stack, Player player) {
      return !player.isCreative() && getPlayerUUID(stack).map(uuid -> !uuid.equals(player.getUUID())).orElse(true);
   }
}
