package iskallia.vault.research;

import iskallia.vault.util.SideOnlyFixer;
import iskallia.vault.world.data.PlayerResearchesData;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;
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
   public static ResearchTree RESEARCH_TREE;

   private static ResearchTree getResearchTree(PlayerEntity player) {
      if (player.field_70170_p.field_72995_K) {
         return RESEARCH_TREE != null ? RESEARCH_TREE : new ResearchTree(player.func_110124_au());
      } else {
         return PlayerResearchesData.get((ServerWorld)player.field_70170_p).getResearches(player);
      }
   }

   private static void warnResearchRequirement(String researchName, String i18nKey) {
      TextComponent name = new StringTextComponent(researchName);
      Style style = Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(-203978));
      name.func_230530_a_(style);
      TextComponent text = new TranslationTextComponent("overlay.requires_research." + i18nKey, new Object[]{name});
      Minecraft.func_71410_x().field_71456_v.func_175188_a(text, false);
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public static void onItemCrafted(ItemCraftedEvent event) {
      PlayerEntity player = event.getPlayer();
      ResearchTree researchTree = getResearchTree(player);
      ItemStack craftedItemStack = event.getCrafting();
      IInventory craftingMatrix = event.getInventory();
      String restrictedBy = researchTree.restrictedBy(craftedItemStack.func_77973_b(), Restrictions.Type.CRAFTABILITY);
      if (restrictedBy != null) {
         if (event.getPlayer().field_70170_p.field_72995_K) {
            warnResearchRequirement(restrictedBy, "craft");
         }

         for (int i = 0; i < craftingMatrix.func_70302_i_(); i++) {
            ItemStack itemStack = craftingMatrix.func_70301_a(i);
            if (itemStack != ItemStack.field_190927_a) {
               ItemStack itemStackToDrop = itemStack.func_77946_l();
               itemStackToDrop.func_190920_e(1);
               player.func_146097_a(itemStackToDrop, false, false);
            }
         }

         int slot = SideOnlyFixer.getSlotFor(player.field_71071_by, craftedItemStack);
         if (slot != -1) {
            ItemStack stackInSlot = player.field_71071_by.func_70301_a(slot);
            if (stackInSlot.func_190916_E() < craftedItemStack.func_190916_E()) {
               craftedItemStack.func_190920_e(stackInSlot.func_190916_E());
            }

            stackInSlot.func_190918_g(craftedItemStack.func_190916_E());
         } else {
            craftedItemStack.func_190918_g(craftedItemStack.func_190916_E());
         }
      }
   }

   @SubscribeEvent
   public static void onItemUse(RightClickItem event) {
      if (event.isCancelable()) {
         PlayerEntity player = event.getPlayer();
         ResearchTree researchTree = getResearchTree(player);
         Item usedItem = event.getItemStack().func_77973_b();
         String restrictedBy = researchTree.restrictedBy(usedItem, Restrictions.Type.USABILITY);
         if (restrictedBy != null) {
            if (event.getSide() == LogicalSide.CLIENT) {
               warnResearchRequirement(restrictedBy, "usage");
            }

            event.setCanceled(true);
         }
      }
   }

   @SubscribeEvent
   public static void onRightClickEmpty(RightClickEmpty event) {
      if (event.isCancelable()) {
         PlayerEntity player = event.getPlayer();
         ResearchTree researchTree = getResearchTree(player);
         Item usedItem = event.getItemStack().func_77973_b();
         String restrictedBy = researchTree.restrictedBy(usedItem, Restrictions.Type.USABILITY);
         if (restrictedBy != null) {
            if (event.getSide() == LogicalSide.CLIENT) {
               warnResearchRequirement(restrictedBy, "usage");
            }

            event.setCanceled(true);
         }
      }
   }

   @SubscribeEvent
   public static void onBlockInteraction(RightClickBlock event) {
      if (event.isCancelable()) {
         PlayerEntity player = event.getPlayer();
         ResearchTree researchTree = getResearchTree(player);
         BlockState blockState = player.field_70170_p.func_180495_p(event.getPos());
         String restrictedBy = researchTree.restrictedBy(blockState.func_177230_c(), Restrictions.Type.BLOCK_INTERACTABILITY);
         if (restrictedBy != null) {
            if (event.getSide() == LogicalSide.CLIENT) {
               warnResearchRequirement(restrictedBy, "interact_block");
            }

            event.setCanceled(true);
         } else {
            ItemStack itemStack = event.getItemStack();
            if (itemStack != ItemStack.field_190927_a) {
               Item item = itemStack.func_77973_b();
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

   @SubscribeEvent
   public static void onBlockHit(LeftClickBlock event) {
      if (event.isCancelable()) {
         PlayerEntity player = event.getPlayer();
         ResearchTree researchTree = getResearchTree(player);
         BlockState blockState = player.field_70170_p.func_180495_p(event.getPos());
         String restrictedBy = researchTree.restrictedBy(blockState.func_177230_c(), Restrictions.Type.HITTABILITY);
         if (restrictedBy != null) {
            if (event.getSide() == LogicalSide.CLIENT) {
               warnResearchRequirement(restrictedBy, "hit");
            }

            event.setCanceled(true);
         } else {
            ItemStack itemStack = event.getItemStack();
            if (itemStack != ItemStack.field_190927_a) {
               Item item = itemStack.func_77973_b();
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

   @SubscribeEvent
   public static void onEntityInteraction(EntityInteract event) {
      if (event.isCancelable()) {
         PlayerEntity player = event.getPlayer();
         ResearchTree researchTree = getResearchTree(player);
         Entity entity = event.getEntity();
         String restrictedBy = researchTree.restrictedBy(entity.func_200600_R(), Restrictions.Type.ENTITY_INTERACTABILITY);
         if (restrictedBy != null) {
            if (event.getSide() == LogicalSide.CLIENT) {
               warnResearchRequirement(restrictedBy, "interact_entity");
            }

            event.setCanceled(true);
         } else {
            ItemStack itemStack = event.getItemStack();
            if (itemStack != ItemStack.field_190927_a) {
               Item item = itemStack.func_77973_b();
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

   @SubscribeEvent
   public static void onPlayerAttack(AttackEntityEvent event) {
      if (event.isCancelable()) {
         PlayerEntity player = event.getPlayer();
         ResearchTree researchTree = getResearchTree(player);
         Entity entity = event.getEntity();
         String restrictedBy = researchTree.restrictedBy(entity.func_200600_R(), Restrictions.Type.ENTITY_INTERACTABILITY);
         if (restrictedBy != null) {
            if (player.field_70170_p.field_72995_K) {
               warnResearchRequirement(restrictedBy, "interact_entity");
            }

            event.setCanceled(true);
         } else {
            ItemStack itemStack = player.func_184614_ca();
            if (itemStack != ItemStack.field_190927_a) {
               Item item = itemStack.func_77973_b();
               restrictedBy = researchTree.restrictedBy(item, Restrictions.Type.USABILITY);
               if (restrictedBy != null) {
                  if (player.field_70170_p.field_72995_K) {
                     warnResearchRequirement(restrictedBy, "usage");
                  }

                  event.setCanceled(true);
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
      PlayerEntity player = event.getPlayer();
      if (player != null) {
         ResearchTree researchTree = getResearchTree(player);
         Item item = event.getItemStack().func_77973_b();
         String restrictionCausedBy = Arrays.stream(Restrictions.Type.values())
            .map(type -> researchTree.restrictedBy(item, type))
            .filter(Objects::nonNull)
            .findFirst()
            .orElseGet(() -> null);
         if (restrictionCausedBy != null) {
            List<ITextComponent> toolTip = event.getToolTip();
            Style textStyle = Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(-5723992));
            Style style = Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(-203978));
            TextComponent text = new TranslationTextComponent("tooltip.requires_research");
            TextComponent name = new StringTextComponent(" " + restrictionCausedBy);
            text.func_230530_a_(textStyle);
            name.func_230530_a_(style);
            toolTip.add(new StringTextComponent(""));
            toolTip.add(text);
            toolTip.add(name);
         }
      }
   }
}
