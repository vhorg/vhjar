package iskallia.vault.core.vault;

import iskallia.vault.block.entity.ModifierDiscoveryTileEntity;
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
import iskallia.vault.init.ModModelDiscoveryGoals;
import iskallia.vault.util.MiscUtils;
import iskallia.vault.world.data.DiscoveredWorkbenchModifiersData;
import iskallia.vault.world.data.DiscoveryGoalStatesData;
import java.util.ArrayList;
import java.util.List;
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
      CommonEvents.LISTENER_LEAVE.register(this, data -> data.getListener().getPlayer().ifPresent(serverPlayer -> {
         DiscoveryGoalStatesData worldData = DiscoveryGoalStatesData.get(serverPlayer.getLevel());
         DiscoveryGoalsState state = worldData.getState(serverPlayer);
         state.resetGoalIf(goalId -> {
            DiscoveryGoal<?> goal = ModModelDiscoveryGoals.REGISTRY.get(goalId);
            return goal instanceof InVaultDiscoveryGoal;
         });
      }));
      ModModelDiscoveryGoals.REGISTRY.forEach((id, discoveryGoal) -> {
         if (discoveryGoal instanceof InVaultDiscoveryGoal<?> goal) {
            goal.initServer(this, world, vault);
         }
      });
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
                           List<Tuple<Item, ResourceLocation>> itemConfigs = new ArrayList<>();
                           DiscoveredWorkbenchModifiersData discoveredModifiers = DiscoveredWorkbenchModifiersData.get(world);

                           for (VaultGearWorkbenchConfig config : ModConfigs.VAULT_GEAR_WORKBENCH_CONFIG.values()) {
                              Item item = config.getGearItem();

                              for (VaultGearWorkbenchConfig.CraftableModifierConfig cfg : config.getAllCraftableModifiers()) {
                                 if (cfg.getUnlockCategory() == VaultGearWorkbenchConfig.UnlockCategory.VAULT_DISCOVERY) {
                                    ResourceLocation key = cfg.getWorkbenchCraftIdentifier();
                                    if (!discoveredModifiers.hasDiscoveredCraft(player, item, key) && cfg.getMinLevel() <= vaultLevel) {
                                       itemConfigs.add(new Tuple(item, key));
                                    }
                                 }
                              }
                           }

                           Tuple<Item, ResourceLocation> tpl = MiscUtils.getRandomEntry(itemConfigs);
                           if (tpl == null) {
                              MutableComponent cmp = new TextComponent("No modifiers left to discover at your current level").withStyle(ChatFormatting.RED);
                              sPlayer.sendMessage(cmp, Util.NIL_UUID);
                           } else if (discoveryTile.setUsedByPlayer(sPlayer)) {
                              if (discoveredModifiers.discoverWorkbenchCraft(sPlayer, (Item)tpl.getA(), (ResourceLocation)tpl.getB())) {
                                 data.setResult(InteractionResult.SUCCESS);
                                 VaultGearWorkbenchConfig.getConfig((Item)tpl.getA())
                                    .ifPresent(
                                       cfgx -> {
                                          VaultGearWorkbenchConfig.CraftableModifierConfig modifierCfg = cfgx.getConfig((ResourceLocation)tpl.getB());
                                          if (modifierCfg != null) {
                                             modifierCfg.createModifier()
                                                .ifPresent(
                                                   modifier -> {
                                                      ItemStack stack = new ItemStack((ItemLike)tpl.getA());
                                                      if (stack.getItem() instanceof VaultGearItem) {
                                                         VaultGearData vgData = VaultGearData.read(stack);
                                                         vgData.setState(VaultGearState.IDENTIFIED);
                                                         vgData.setRarity(VaultGearRarity.COMMON);
                                                         vgData.write(stack);
                                                      }

                                                      modifier.getConfigDisplay(stack)
                                                         .ifPresent(
                                                            configDisplay -> {
                                                               MutableComponent cmpx = new TextComponent("")
                                                                  .append(player.getDisplayName())
                                                                  .append(" discovered the ")
                                                                  .append(stack.getHoverName())
                                                                  .append(" modifier: ")
                                                                  .append(configDisplay);
                                                               MiscUtils.broadcast(cmpx);
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
   }

   public void releaseServer() {
      CommonEvents.release(this);
   }
}
