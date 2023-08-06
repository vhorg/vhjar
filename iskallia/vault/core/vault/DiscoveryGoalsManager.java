package iskallia.vault.core.vault;

import iskallia.vault.block.entity.AlchemyArchiveTileEntity;
import iskallia.vault.block.entity.ModifierDiscoveryTileEntity;
import iskallia.vault.config.gear.VaultAlchemyTableConfig;
import iskallia.vault.config.gear.VaultGearWorkbenchConfig;
import iskallia.vault.core.data.DataObject;
import iskallia.vault.core.data.key.registry.FieldRegistry;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.event.common.BlockUseEvent;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.discoverylogic.DiscoveryGoalsState;
import iskallia.vault.discoverylogic.goal.base.DiscoveryGoal;
import iskallia.vault.discoverylogic.goal.base.InVaultDiscoveryGoal;
import iskallia.vault.gear.VaultGearRarity;
import iskallia.vault.gear.VaultGearState;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.init.ModModelDiscoveryGoals;
import iskallia.vault.item.BottleItem;
import iskallia.vault.item.gear.IdolItem;
import iskallia.vault.util.MiscUtils;
import iskallia.vault.world.data.DiscoveredAlchemyModifiersData;
import iskallia.vault.world.data.DiscoveredWorkbenchModifiersData;
import iskallia.vault.world.data.DiscoveryGoalStatesData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Tuple;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class DiscoveryGoalsManager extends DataObject<DiscoveryGoalsManager> {
   public static final FieldRegistry FIELDS = new FieldRegistry();

   @Override
   public FieldRegistry getFields() {
      return FIELDS;
   }

   public void initServer(VirtualWorld world, Vault vault) {
      ModModelDiscoveryGoals.REGISTRY.forEach((id, discoveryGoal) -> {
         if (discoveryGoal instanceof InVaultDiscoveryGoal<?> goal) {
            goal.initServer(this, world, vault);
         }
      });
      CommonEvents.LISTENER_LEAVE.register(this, data -> data.getListener().getPlayer().ifPresent(serverPlayer -> {
         DiscoveryGoalStatesData worldData = DiscoveryGoalStatesData.get(serverPlayer.getLevel());
         DiscoveryGoalsState state = worldData.getState(serverPlayer);
         state.resetGoalIf(goalId -> {
            DiscoveryGoal<?> goal = ModModelDiscoveryGoals.REGISTRY.get(goalId);
            return goal instanceof InVaultDiscoveryGoal;
         });
      }));
      CommonEvents.BLOCK_USE
         .of(ModBlocks.MODIFIER_DISCOVERY)
         .at(BlockUseEvent.Phase.HEAD)
         .in(world)
         .register(
            this,
            data -> {
               if (data.getWorld() == world) {
                  Player player = data.getPlayer();
                  if (player instanceof ServerPlayer sPlayer) {
                     BlockPos pos = data.getPos();
                     if (world.getBlockEntity(pos) instanceof ModifierDiscoveryTileEntity discoveryTile) {
                        if (discoveryTile.canBeUsed(player)) {
                           int vaultLevel = vault.has(Vault.LEVEL) ? vault.get(Vault.LEVEL).get() : 0;
                           Map<Item, HashMap<ResourceLocation, ArrayList<ResourceLocation>>> itemCfg = new HashMap<>();
                           Set<ResourceLocation> availableIdolCraftIds = new HashSet<>();
                           DiscoveredWorkbenchModifiersData discoveredModifiers = DiscoveredWorkbenchModifiersData.get(world);

                           for (VaultGearWorkbenchConfig config : ModConfigs.VAULT_GEAR_WORKBENCH_CONFIG.values()) {
                              Item item = config.getGearItem();

                              for (VaultGearWorkbenchConfig.CraftableModifierConfig cfg : config.getAllCraftableModifiers()) {
                                 if (cfg.getUnlockCategory() == VaultGearWorkbenchConfig.UnlockCategory.VAULT_DISCOVERY) {
                                    ResourceLocation key = cfg.getWorkbenchCraftIdentifier();
                                    if (!discoveredModifiers.hasDiscoveredCraft(player, item, key) && cfg.getMinLevel() <= vaultLevel) {
                                       if (item instanceof IdolItem) {
                                          availableIdolCraftIds.add(key);
                                          if (itemCfg.containsKey(ModItems.IDOL_BENEVOLENT)) {
                                             if (itemCfg.get(ModItems.IDOL_BENEVOLENT).containsKey(cfg.getModifierIdentifier())) {
                                                itemCfg.get(ModItems.IDOL_BENEVOLENT).get(cfg.getModifierIdentifier()).add(cfg.getWorkbenchCraftIdentifier());
                                             } else {
                                                itemCfg.get(ModItems.IDOL_BENEVOLENT)
                                                   .put(cfg.getModifierIdentifier(), new ArrayList<>(List.of(cfg.getWorkbenchCraftIdentifier())));
                                             }
                                          } else {
                                             HashMap<ResourceLocation, ArrayList<ResourceLocation>> hashmap = new HashMap<>();
                                             hashmap.put(cfg.getModifierIdentifier(), new ArrayList<>(List.of(cfg.getWorkbenchCraftIdentifier())));
                                             itemCfg.put(ModItems.IDOL_BENEVOLENT, hashmap);
                                          }
                                       } else if (itemCfg.containsKey(item)) {
                                          if (itemCfg.get(item).containsKey(cfg.getModifierIdentifier())) {
                                             itemCfg.get(item).get(cfg.getModifierIdentifier()).add(cfg.getWorkbenchCraftIdentifier());
                                          } else {
                                             itemCfg.get(item).put(cfg.getModifierIdentifier(), new ArrayList<>(List.of(cfg.getWorkbenchCraftIdentifier())));
                                          }
                                       } else {
                                          HashMap<ResourceLocation, ArrayList<ResourceLocation>> hashmap = new HashMap<>();
                                          hashmap.put(cfg.getModifierIdentifier(), new ArrayList<>(List.of(cfg.getWorkbenchCraftIdentifier())));
                                          itemCfg.put(item, hashmap);
                                       }
                                    }
                                 }
                              }
                           }

                           if (!availableIdolCraftIds.isEmpty()) {
                              availableIdolCraftIds.forEach(keyx -> new Tuple(ModItems.IDOL_BENEVOLENT, keyx));
                           }

                           Entry<Item, HashMap<ResourceLocation, ArrayList<ResourceLocation>>> randomNestedMapEntry = MiscUtils.getRandomMapEntry(itemCfg);
                           Tuple<Item, ResourceLocation> tpl = null;
                           ArrayList<ResourceLocation> randomArrayList = new ArrayList<>();
                           if (randomNestedMapEntry != null) {
                              randomArrayList = MiscUtils.getRandomValueFromMap(randomNestedMapEntry.getValue());
                              Item item = randomNestedMapEntry.getKey();
                              if (randomArrayList != null && randomArrayList.size() != 0) {
                                 tpl = new Tuple(item, randomArrayList.get(0));
                              }
                           }

                           if (randomArrayList == null || randomArrayList.size() == 0) {
                              MutableComponent cmp = new TextComponent("No modifiers left to discover at your current level").withStyle(ChatFormatting.RED);
                              sPlayer.sendMessage(cmp, Util.NIL_UUID);
                           } else if (discoveryTile.setUsedByPlayer(sPlayer)) {
                              if (discoveredModifiers.compoundDiscoverWorkbenchCraft(sPlayer, (Item)tpl.getA(), (ResourceLocation)tpl.getB())) {
                                 data.setResult(InteractionResult.SUCCESS);
                                 Tuple<Item, ResourceLocation> finalTpl = tpl;
                                 VaultGearWorkbenchConfig.getConfig((Item)tpl.getA())
                                    .ifPresent(
                                       cfgx -> {
                                          VaultGearWorkbenchConfig.CraftableModifierConfig modifierCfg = cfgx.getConfig((ResourceLocation)finalTpl.getB());
                                          if (modifierCfg != null) {
                                             modifierCfg.createModifier()
                                                .ifPresent(
                                                   modifier -> {
                                                      ItemStack stack = new ItemStack((ItemLike)finalTpl.getA());
                                                      if (stack.getItem() instanceof VaultGearItem) {
                                                         VaultGearData vgData = VaultGearData.read(stack);
                                                         vgData.setState(VaultGearState.IDENTIFIED);
                                                         vgData.setRarity(VaultGearRarity.COMMON);
                                                         vgData.write(stack);
                                                      }

                                                      modifier.getConfigDisplay(stack)
                                                         .ifPresent(
                                                            configDisplay -> {
                                                               MutableComponent cmp = new TextComponent("")
                                                                  .append(player.getDisplayName())
                                                                  .append(" discovered the ")
                                                                  .append(stack.getHoverName())
                                                                  .append(" modifier: ")
                                                                  .append(configDisplay);
                                                               MiscUtils.broadcast(cmp);
                                                            }
                                                         );
                                                   }
                                                );
                                          }
                                       }
                                    );
                              }
                           }
                        }
                     }
                  }
               }
            }
         );
      CommonEvents.BLOCK_USE
         .of(ModBlocks.ALCHEMY_ARCHIVE)
         .at(BlockUseEvent.Phase.HEAD)
         .in(world)
         .register(
            this,
            data -> {
               if (data.getWorld() == world) {
                  Player player = data.getPlayer();
                  if (player instanceof ServerPlayer sPlayer) {
                     BlockPos pos = data.getPos();
                     if (world.getBlockEntity(pos) instanceof AlchemyArchiveTileEntity discoveryTile) {
                        if (discoveryTile.canBeUsed(player)) {
                           int vaultLevel = vault.has(Vault.LEVEL) ? vault.get(Vault.LEVEL).get() : 0;
                           List<ResourceLocation> itemConfigs = new ArrayList<>();
                           DiscoveredAlchemyModifiersData discoveredModifiers = DiscoveredAlchemyModifiersData.get(world);

                           for (VaultAlchemyTableConfig.CraftableModifierConfig cfg : ModConfigs.VAULT_ALCHEMY_TABLE.getAllCraftableModifiers()) {
                              if (cfg.getUnlockCategory() == VaultAlchemyTableConfig.UnlockCategory.VAULT_DISCOVERY) {
                                 ResourceLocation key = cfg.getWorkbenchCraftIdentifier();
                                 if (!discoveredModifiers.hasDiscoveredCraft(player, key) && cfg.getMinLevel() <= vaultLevel) {
                                    itemConfigs.add(key);
                                 }
                              }
                           }

                           ResourceLocation tpl = MiscUtils.getRandomEntry(itemConfigs);
                           if (tpl == null) {
                              MutableComponent cmp = new TextComponent("No modifiers left to discover at your current level").withStyle(ChatFormatting.RED);
                              sPlayer.sendMessage(cmp, Util.NIL_UUID);
                           } else if (discoveryTile.setUsedByPlayer(sPlayer)) {
                              if (discoveredModifiers.compoundDiscoverWorkbenchCraft(sPlayer, tpl)) {
                                 data.setResult(InteractionResult.SUCCESS);
                                 VaultAlchemyTableConfig cfgx = ModConfigs.VAULT_ALCHEMY_TABLE;
                                 VaultAlchemyTableConfig.CraftableModifierConfig modifierCfg = cfgx.getConfig(tpl);
                                 if (modifierCfg != null) {
                                    modifierCfg.createModifier()
                                       .ifPresent(
                                          modifier -> {
                                             ItemStack stack = BottleItem.create(null, null);
                                             if (stack.getItem() instanceof VaultGearItem) {
                                                VaultGearData vgData = VaultGearData.read(stack);
                                                vgData.setState(VaultGearState.IDENTIFIED);
                                                vgData.setRarity(VaultGearRarity.COMMON);
                                                vgData.write(stack);
                                             }

                                             modifier.getConfigDisplay(stack)
                                                .ifPresent(
                                                   configDisplay -> {
                                                      MutableComponent cmp = new TextComponent("")
                                                         .append(player.getDisplayName())
                                                         .append(" discovered the ")
                                                         .append(stack.getHoverName())
                                                         .append(" modifier: ")
                                                         .append(configDisplay);
                                                      MiscUtils.broadcast(cmp);
                                                   }
                                                );
                                          }
                                       );
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         );
   }

   public void releaseServer() {
      CommonEvents.release(this);
   }
}
