package iskallia.vault.research;

import iskallia.vault.util.SideOnlyFixer;
import iskallia.vault.world.data.PlayerResearchesData;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.BaseComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.ItemCraftedEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickEmpty;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(
   modid = "the_vault"
)
public class StageManager {
   public static ResearchTree RESEARCH_TREE = ResearchTree.empty();

   public static ResearchTree getResearchTree(Player player) {
      return player.level.isClientSide ? RESEARCH_TREE : PlayerResearchesData.get((ServerLevel)player.level).getResearches(player);
   }

   private static void warnResearchRequirement(String researchName, String i18nKey) {
      BaseComponent name = new TextComponent(researchName);
      Style style = Style.EMPTY.withColor(TextColor.fromRgb(-203978));
      name.setStyle(style);
      BaseComponent text = new TranslatableComponent("overlay.requires_research." + i18nKey, new Object[]{name});
      Minecraft.getInstance().gui.setOverlayMessage(text, false);
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public static void onItemCrafted(ItemCraftedEvent event) {
      Player player = event.getPlayer();
      if (!player.isCreative()) {
         ResearchTree researchTree = getResearchTree(player);
         ItemStack craftedItemStack = event.getCrafting();
         Container craftingMatrix = event.getInventory();
         String restrictedBy = researchTree.restrictedBy(craftedItemStack.getItem(), Restrictions.Type.CRAFTABILITY);
         if (restrictedBy != null) {
            if (event.getPlayer().level.isClientSide) {
               warnResearchRequirement(restrictedBy, "craft");
            }

            for (int i = 0; i < craftingMatrix.getContainerSize(); i++) {
               ItemStack itemStack = craftingMatrix.getItem(i);
               if (itemStack != ItemStack.EMPTY) {
                  ItemStack itemStackToDrop = itemStack.copy();
                  itemStackToDrop.setCount(1);
                  player.drop(itemStackToDrop, false, false);
               }
            }

            int slot = SideOnlyFixer.getSlotFor(player.getInventory(), craftedItemStack);
            if (slot != -1) {
               ItemStack stackInSlot = player.getInventory().getItem(slot);
               if (stackInSlot.getCount() < craftedItemStack.getCount()) {
                  craftedItemStack.setCount(stackInSlot.getCount());
               }

               stackInSlot.shrink(craftedItemStack.getCount());
            } else {
               craftedItemStack.shrink(craftedItemStack.getCount());
            }
         }
      }
   }

   @SubscribeEvent
   public static void onItemUse(RightClickItem event) {
      if (event.isCancelable()) {
         Player player = event.getPlayer();
         if (!player.isCreative()) {
            ResearchTree researchTree = getResearchTree(player);
            Item usedItem = event.getItemStack().getItem();
            String restrictedBy = researchTree.restrictedBy(usedItem, Restrictions.Type.USABILITY);
            if (restrictedBy != null) {
               if (event.getSide() == LogicalSide.CLIENT) {
                  warnResearchRequirement(restrictedBy, "usage");
               }

               event.setCanceled(true);
            }
         }
      }
   }

   @SubscribeEvent
   public static void onRightClickEmpty(RightClickEmpty event) {
      if (event.isCancelable()) {
         Player player = event.getPlayer();
         if (!player.isCreative()) {
            ResearchTree researchTree = getResearchTree(player);
            Item usedItem = event.getItemStack().getItem();
            String restrictedBy = researchTree.restrictedBy(usedItem, Restrictions.Type.USABILITY);
            if (restrictedBy != null) {
               if (event.getSide() == LogicalSide.CLIENT) {
                  warnResearchRequirement(restrictedBy, "usage");
               }

               event.setCanceled(true);
            }
         }
      }
   }

   @SubscribeEvent
   public static void onBlockInteraction(RightClickBlock event) {
      if (event.isCancelable()) {
         Player player = event.getPlayer();
         if (!player.isCreative()) {
            ResearchTree researchTree = getResearchTree(player);
            BlockState blockState = player.level.getBlockState(event.getPos());
            String restrictedBy = researchTree.restrictedBy(blockState.getBlock(), Restrictions.Type.BLOCK_INTERACTABILITY);
            if (restrictedBy != null) {
               if (event.getSide() == LogicalSide.CLIENT) {
                  warnResearchRequirement(restrictedBy, "interact_block");
               }

               event.setCanceled(true);
            } else {
               ItemStack itemStack = event.getItemStack();
               if (itemStack != ItemStack.EMPTY) {
                  Item item = itemStack.getItem();
                  restrictedBy = researchTree.restrictedBy(item, Restrictions.Type.USABILITY);
                  if (restrictedBy != null) {
                     if (event.getSide() == LogicalSide.CLIENT) {
                        warnResearchRequirement(restrictedBy, "usage");
                     }

                     event.setCanceled(true);
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onBlockHit(LeftClickBlock event) {
      if (event.isCancelable()) {
         Player player = event.getPlayer();
         if (!player.isCreative()) {
            ResearchTree researchTree = getResearchTree(player);
            BlockState blockState = player.level.getBlockState(event.getPos());
            String restrictedBy = researchTree.restrictedBy(blockState.getBlock(), Restrictions.Type.HITTABILITY);
            if (restrictedBy != null) {
               if (event.getSide() == LogicalSide.CLIENT) {
                  warnResearchRequirement(restrictedBy, "hit");
               }

               event.setCanceled(true);
            } else {
               ItemStack itemStack = event.getItemStack();
               if (itemStack != ItemStack.EMPTY) {
                  Item item = itemStack.getItem();
                  restrictedBy = researchTree.restrictedBy(item, Restrictions.Type.USABILITY);
                  if (restrictedBy != null) {
                     if (event.getSide() == LogicalSide.CLIENT) {
                        warnResearchRequirement(restrictedBy, "usage");
                     }

                     event.setCanceled(true);
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onEntityInteraction(EntityInteract event) {
      if (event.isCancelable()) {
         Player player = event.getPlayer();
         if (!player.isCreative()) {
            ResearchTree researchTree = getResearchTree(player);
            Entity entity = event.getEntity();
            String restrictedBy = researchTree.restrictedBy(entity.getType(), Restrictions.Type.ENTITY_INTERACTABILITY);
            if (restrictedBy != null) {
               if (event.getSide() == LogicalSide.CLIENT) {
                  warnResearchRequirement(restrictedBy, "interact_entity");
               }

               event.setCanceled(true);
            } else {
               ItemStack itemStack = event.getItemStack();
               if (itemStack != ItemStack.EMPTY) {
                  Item item = itemStack.getItem();
                  restrictedBy = researchTree.restrictedBy(item, Restrictions.Type.USABILITY);
                  if (restrictedBy != null) {
                     if (event.getSide() == LogicalSide.CLIENT) {
                        warnResearchRequirement(restrictedBy, "usage");
                     }

                     event.setCanceled(true);
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onPlayerAttack(AttackEntityEvent event) {
      if (event.isCancelable()) {
         Player player = event.getPlayer();
         if (!player.isCreative()) {
            ResearchTree researchTree = getResearchTree(player);
            Entity entity = event.getEntity();
            String restrictedBy = researchTree.restrictedBy(entity.getType(), Restrictions.Type.ENTITY_INTERACTABILITY);
            if (restrictedBy != null) {
               if (player.level.isClientSide) {
                  warnResearchRequirement(restrictedBy, "interact_entity");
               }

               event.setCanceled(true);
            } else {
               ItemStack itemStack = player.getMainHandItem();
               if (itemStack != ItemStack.EMPTY) {
                  Item item = itemStack.getItem();
                  restrictedBy = researchTree.restrictedBy(item, Restrictions.Type.USABILITY);
                  if (restrictedBy != null) {
                     if (player.level.isClientSide) {
                        warnResearchRequirement(restrictedBy, "usage");
                     }

                     event.setCanceled(true);
                  }
               }
            }
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public static void onItemTooltip(ItemTooltipEvent event) {
      Player player = event.getPlayer();
      if (player != null) {
         ResearchTree researchTree = getResearchTree(player);
         Item item = event.getItemStack().getItem();
         String restrictionCausedBy = Arrays.stream(Restrictions.Type.values())
            .map(type -> researchTree.restrictedBy(item, type))
            .filter(Objects::nonNull)
            .findFirst()
            .orElseGet(() -> null);
         if (restrictionCausedBy != null) {
            List<Component> toolTip = event.getToolTip();
            Style textStyle = Style.EMPTY.withColor(TextColor.fromRgb(-5723992));
            Style style = Style.EMPTY.withColor(TextColor.fromRgb(-203978));
            BaseComponent text = new TranslatableComponent("tooltip.requires_research");
            BaseComponent name = new TextComponent(" " + restrictionCausedBy);
            text.setStyle(textStyle);
            name.setStyle(style);
            toolTip.add(new TextComponent(""));
            toolTip.add(text);
            toolTip.add(name);
         }
      }
   }
}
