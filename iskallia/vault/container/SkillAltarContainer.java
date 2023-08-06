package iskallia.vault.container;

import iskallia.vault.block.SkillAltarBlock;
import iskallia.vault.block.entity.SkillAltarTileEntity;
import iskallia.vault.client.ClientAbilityData;
import iskallia.vault.client.ClientTalentData;
import iskallia.vault.client.gui.overlay.VaultBarOverlay;
import iskallia.vault.container.oversized.OverSizedSlotContainer;
import iskallia.vault.container.oversized.OverSizedTabSlot;
import iskallia.vault.container.slot.TabSlot;
import iskallia.vault.container.spi.AbstractElementContainer;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModContainers;
import iskallia.vault.init.ModItems;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.init.ModSlotIcons;
import iskallia.vault.init.ModSounds;
import iskallia.vault.network.message.ServerboundSkillAltarActionMessage;
import iskallia.vault.skill.base.Skill;
import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.skill.base.SpecializedSkill;
import iskallia.vault.skill.tree.AbilityTree;
import iskallia.vault.skill.tree.TalentTree;
import iskallia.vault.snapshot.AttributeSnapshotHelper;
import iskallia.vault.util.SidedHelper;
import iskallia.vault.world.data.PlayerAbilitiesData;
import iskallia.vault.world.data.PlayerTalentsData;
import iskallia.vault.world.data.PlayerVaultStatsData;
import iskallia.vault.world.data.SkillAltarData;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.chat.HoverEvent.Action;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class SkillAltarContainer extends OverSizedSlotContainer {
   protected final BlockPos pos;
   @Nullable
   protected SkillAltarData.SkillTemplate template;
   protected int templateIndex;
   private List<SkillAltarData.SkillIcon> skillIcons;
   protected final SkillAltarTileEntity tileEntity;

   @Nullable
   public SkillAltarData.SkillTemplate getTemplate() {
      return this.template;
   }

   protected SkillAltarContainer(
      MenuType<?> menuType,
      int id,
      Inventory playerInventory,
      BlockPos pos,
      @Nullable SkillAltarData.SkillTemplate template,
      int templateIndex,
      List<SkillAltarData.SkillIcon> skillIcons
   ) {
      super(menuType, id, playerInventory.player);
      this.pos = pos;
      this.template = template;
      this.templateIndex = templateIndex;
      this.skillIcons = skillIcons;
      if (this.player instanceof ServerPlayer serverPlayer) {
         PlayerAbilitiesData playerAbilitiesData = PlayerAbilitiesData.get(serverPlayer.getLevel());
         playerAbilitiesData.getAbilities(this.player).sync(SkillContext.of(serverPlayer));
      }

      if (this.player.level.getBlockEntity(pos) instanceof SkillAltarTileEntity abilityAtlarTile) {
         this.tileEntity = abilityAtlarTile;
      } else {
         this.tileEntity = null;
      }
   }

   public boolean isEmptyTemplate() {
      return this.template == null;
   }

   public boolean stillValid(Player pPlayer) {
      return true;
   }

   public List<SkillAltarData.SkillIcon> getSkillIcons() {
      return this.skillIcons;
   }

   public int getTemplateIndex() {
      return this.templateIndex;
   }

   public void openTab(int tabIndex) {
      ModNetwork.CHANNEL.sendToServer(new ServerboundSkillAltarActionMessage(this.pos, ServerboundSkillAltarActionMessage.Action.OPEN_TAB, tabIndex));
   }

   public boolean isOpenedByNonOwner() {
      return !this.player.getUUID().equals(this.tileEntity.getOwnerId());
   }

   public void updateTemplateIcon(SkillAltarData.SkillIcon icon) {
      if (this.player.getUUID().equals(this.tileEntity.getOwnerId())) {
         if (this.player.getLevel().isClientSide()) {
            this.player.playSound(SoundEvents.UI_BUTTON_CLICK, 1.0F, 1.0F);
            ModNetwork.CHANNEL
               .sendToServer(new ServerboundSkillAltarActionMessage(this.pos, ServerboundSkillAltarActionMessage.Action.UPDATE_ICON, this.templateIndex, icon));
         }

         if (this.player instanceof ServerPlayer serverPlayer) {
            SkillAltarData altarData = SkillAltarData.get(serverPlayer.getLevel());
            altarData.updateTemplateIcon(serverPlayer.getUUID(), this.templateIndex, icon);
            SkillAltarBlock.openGui(this.pos, serverPlayer, this.templateIndex, true);
         }
      }
   }

   public void copyToClipboard() {
      if (this.template != null) {
         Minecraft.getInstance().keyboardHandler.setClipboard(this.template.exportToString());
      }
   }

   public void openImportScreen(int templateIndex) {
      if (this.player.getLevel().isClientSide()) {
         this.player.playSound(SoundEvents.UI_BUTTON_CLICK, 1.0F, 1.0F);
         ModNetwork.CHANNEL
            .sendToServer(new ServerboundSkillAltarActionMessage(this.pos, ServerboundSkillAltarActionMessage.Action.OPEN_IMPORT, templateIndex));
      } else if (this.player instanceof ServerPlayer serverPlayer) {
         SkillAltarBlock.openGui(this.pos, serverPlayer, templateIndex, false);
      }
   }

   public static class Default extends SkillAltarContainer {
      private static final int PLAYER_INVENTORY_TOP_Y = 132;
      private AbstractElementContainer.SlotIndexRange hotbarSlotIndexRange;
      private AbstractElementContainer.SlotIndexRange inventorySlotIndexRange;
      private AbstractElementContainer.SlotIndexRange hotbarInventorySlotIndexRange;
      private AbstractElementContainer.SlotIndexRange regretOrbsIndexRange;
      private int missingRegretOrbs = -1;

      public Default(
         int id,
         Inventory playerInventory,
         BlockPos pos,
         @Nullable SkillAltarData.SkillTemplate template,
         int templateIndex,
         List<SkillAltarData.SkillIcon> skillIcons
      ) {
         super(ModContainers.SKILL_ALTAR_CONTAINER, id, playerInventory, pos, template, templateIndex, skillIcons);
         BlockEntity tile = this.player.level.getBlockEntity(pos);
         if (tile instanceof SkillAltarTileEntity) {
            this.initSlots(playerInventory);
         }
      }

      protected void initSlots(Inventory playerInventory) {
         int nextSlotIndex = 0;

         for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
               this.addSlot(new TabSlot(playerInventory, column + row * 9 + 9, 8 + column * 18, 132 + row * 18));
               nextSlotIndex++;
            }
         }

         this.inventorySlotIndexRange = new AbstractElementContainer.SlotIndexRange(0, nextSlotIndex);

         for (int hotbarSlot = 0; hotbarSlot < 9; hotbarSlot++) {
            this.addSlot(new TabSlot(playerInventory, hotbarSlot, 8 + hotbarSlot * 18, 190));
            nextSlotIndex++;
         }

         this.hotbarSlotIndexRange = new AbstractElementContainer.SlotIndexRange(this.inventorySlotIndexRange.end(), nextSlotIndex);
         this.hotbarInventorySlotIndexRange = new AbstractElementContainer.SlotIndexRange(this.inventorySlotIndexRange.start(), this.hotbarSlotIndexRange.end());
         OverSizedTabSlot paymentSlot = new OverSizedTabSlot(this.tileEntity.getRegretOrbInventory(), 0, 105, 100);
         paymentSlot.setFilter(stack -> stack.getItem() == ModItems.REGRET_ORB);
         paymentSlot.setBackground(InventoryMenu.BLOCK_ATLAS, ModSlotIcons.REGRET_ORB_NO_ITEM);
         this.addSlot(paymentSlot);
         this.regretOrbsIndexRange = new AbstractElementContainer.SlotIndexRange(
            this.hotbarInventorySlotIndexRange.end(), this.hotbarInventorySlotIndexRange.end() + 1
         );
      }

      public ItemStack quickMoveStack(Player player, int index) {
         ItemStack originalStack = ItemStack.EMPTY;
         Slot slot = (Slot)this.slots.get(index);
         if (slot != null && slot.hasItem()) {
            ItemStack slotStack = slot.getItem().copy();
            originalStack = slotStack.copy();
            boolean didNotMoveAnything;
            if (this.regretOrbsIndexRange.contains(index)) {
               didNotMoveAnything = !this.moveOverSizedItemStackTo(
                  slotStack, slot, this.hotbarInventorySlotIndexRange.start(), this.hotbarInventorySlotIndexRange.end(), false
               );
            } else if (this.hotbarSlotIndexRange.contains(index)) {
               didNotMoveAnything = !this.moveItemStackTo(slotStack, this.regretOrbsIndexRange.start(), this.regretOrbsIndexRange.end(), false)
                  && !this.moveItemStackTo(slotStack, this.inventorySlotIndexRange.start(), this.inventorySlotIndexRange.end(), false);
            } else {
               didNotMoveAnything = !this.moveItemStackTo(slotStack, this.regretOrbsIndexRange.start(), this.regretOrbsIndexRange.end(), false)
                  && !this.moveItemStackTo(slotStack, this.hotbarSlotIndexRange.start(), this.hotbarSlotIndexRange.end(), false);
            }

            if (didNotMoveAnything) {
               return ItemStack.EMPTY;
            }

            if (slotStack.isEmpty()) {
               slot.set(ItemStack.EMPTY);
            } else {
               slot.setChanged();
            }
         }

         return originalStack;
      }

      @Override
      public void clicked(int slotId, int dragType, ClickType clickTypeIn, Player player) {
         super.clicked(slotId, dragType, clickTypeIn, player);
         this.missingRegretOrbs = -1;
      }

      public int getNumberOfRegretOrbsRequired() {
         return this.isEmptyTemplate()
            ? Integer.MAX_VALUE
            : Math.max(1, (int)(ModConfigs.SKILL_ALTAR.getPerLevelCost() * SidedHelper.getVaultLevel(this.player)));
      }

      public int getNumberOfMissingRegretOrbs(int unspentRegretPoints) {
         if (this.player.getLevel().isClientSide() && this.missingRegretOrbs != -1) {
            return this.missingRegretOrbs;
         } else {
            int orbsRequired = this.getNumberOfRegretOrbsRequired();
            orbsRequired -= unspentRegretPoints;
            if (orbsRequired <= 0) {
               return 0;
            } else {
               orbsRequired -= this.tileEntity.getRegretOrbInventory().getItem(0).getCount();
               if (orbsRequired <= 0) {
                  return 0;
               } else {
                  orbsRequired -= this.player.getInventory().countItem(ModItems.REGRET_ORB);
                  this.missingRegretOrbs = Math.max(orbsRequired, 0);
                  return this.missingRegretOrbs;
               }
            }
         }
      }

      public void setPlayerAbilitiesAndTalentsFromTemplate() {
         if (this.player instanceof ServerPlayer serverPlayer) {
            int unspentRegretPoints = PlayerVaultStatsData.get(serverPlayer.getLevel()).getVaultStats(serverPlayer).getUnspentRegretPoints();
            this.setPlayerAbilitiesAndTalentsFromTemplate(unspentRegretPoints);
         }
      }

      public int getMissingSkillPoints(int unspentSkillPoints, int spentSkillPoints) {
         if (this.template == null) {
            return 0;
         } else {
            int cost = this.template.getAbilities().getSpentLearnPoints() + this.template.getTalents().getSpentLearnPoints();
            return cost - (unspentSkillPoints + spentSkillPoints);
         }
      }

      @OnlyIn(Dist.CLIENT)
      public int getMissingSkillPointsClient() {
         if (ClientAbilityData.getTree() != null && ClientTalentData.getTree() != null) {
            int spent = ClientAbilityData.getTree().getSpentLearnPoints() + ClientTalentData.getTree().getSpentLearnPoints();
            return this.getMissingSkillPoints(VaultBarOverlay.unspentSkillPoints, spent);
         } else {
            return 0;
         }
      }

      public int getMissingSkillPointsServer() {
         if (this.template == null) {
            return 0;
         } else {
            int spent = PlayerAbilitiesData.get((ServerLevel)this.player.level).getAbilities(this.player).getSpentLearnPoints()
               + PlayerTalentsData.get((ServerLevel)this.player.level).getTalents(this.player).getSpentLearnPoints();
            return this.getMissingSkillPoints(
               PlayerVaultStatsData.get((ServerLevel)this.player.level).getVaultStats(this.player).getUnspentSkillPoints(), spent
            );
         }
      }

      public void setPlayerAbilitiesAndTalentsFromTemplate(int unspentRegretPoints) {
         if (this.template != null && this.player.getUUID().equals(this.tileEntity.getOwnerId()) && this.getNumberOfMissingRegretOrbs(unspentRegretPoints) <= 0
            )
          {
            if (this.player.getLevel().isClientSide()) {
               if (this.getMissingSkillPointsClient() > 0) {
                  return;
               }
            } else if (this.getMissingSkillPointsServer() > 0) {
               return;
            }

            if (this.player.getLevel().isClientSide()) {
               this.player.playSound(ModSounds.SKILL_TREE_LEARN_SFX, 1.0F, 1.0F);
               ModNetwork.CHANNEL
                  .sendToServer(new ServerboundSkillAltarActionMessage(this.pos, ServerboundSkillAltarActionMessage.Action.LOAD, this.templateIndex));
            } else {
               if (this.player instanceof ServerPlayer serverPlayer) {
                  this.consumeRequiredRegretPointsOrOrbs(serverPlayer, unspentRegretPoints);
                  int skillPointsBefore = PlayerAbilitiesData.get(serverPlayer.getLevel()).getAbilities(serverPlayer).getSpentLearnPoints();
                  skillPointsBefore += PlayerTalentsData.get(serverPlayer.getLevel()).getTalents(serverPlayer).getSpentLearnPoints();
                  PlayerVaultStatsData statsData = PlayerVaultStatsData.get(serverPlayer.getLevel());
                  statsData.addSkillPoints(serverPlayer, skillPointsBefore);
                  this.setAbilitiesFromTemplate(serverPlayer);
                  this.setTalentsFromTemplate(serverPlayer);
                  int skillPointsAfter = PlayerAbilitiesData.get(serverPlayer.getLevel()).getAbilities(serverPlayer).getSpentLearnPoints();
                  skillPointsAfter += PlayerTalentsData.get(serverPlayer.getLevel()).getTalents(serverPlayer).getSpentLearnPoints();
                  statsData.spendSkillPoints(serverPlayer, skillPointsAfter);
                  SkillAltarBlock.openGui(this.pos, serverPlayer, this.templateIndex, true);
               }
            }
         }
      }

      private void setTalentsFromTemplate(ServerPlayer serverPlayer) {
         ModConfigs.TALENTS.get().ifPresent(tree -> {
            PlayerTalentsData playerTalentsData = PlayerTalentsData.get(serverPlayer.getLevel());
            TalentTree mergedTalents = (TalentTree)this.template.getTalents().<Skill>copy().mergeFrom(tree.copy(), SkillContext.of(serverPlayer));
            playerTalentsData.setTalents(serverPlayer, mergedTalents);
         });
      }

      private void setAbilitiesFromTemplate(ServerPlayer serverPlayer) {
         ModConfigs.ABILITIES.get().ifPresent(tree -> {
            PlayerAbilitiesData playerAbilitiesData = PlayerAbilitiesData.get(serverPlayer.getLevel());
            AbilityTree mergedAbilities = (AbilityTree)this.template.getAbilities().<Skill>copy().mergeFrom(tree.copy(), SkillContext.of(serverPlayer));
            playerAbilitiesData.setAbilities(serverPlayer, mergedAbilities);
            AttributeSnapshotHelper.getInstance().refreshSnapshotDelayed(serverPlayer);
         });
      }

      private void consumeRequiredRegretPointsOrOrbs(ServerPlayer serverPlayer, int unspentRegretPoints) {
         int requiredOrbs = this.getNumberOfRegretOrbsRequired();
         PlayerVaultStatsData statsData = PlayerVaultStatsData.get(serverPlayer.getLevel());
         statsData.spendRegretPoints(serverPlayer, Math.min(requiredOrbs, unspentRegretPoints));
         int remaining = requiredOrbs - unspentRegretPoints;
         if (remaining > 0) {
            int orbInventoryCount = this.tileEntity.getRegretOrbInventory().getItem(0).getCount();
            this.tileEntity.consumeOrbs(Math.min(remaining, orbInventoryCount));
            remaining -= orbInventoryCount;
            Inventory playerInventory = this.player.getInventory();

            for (int slot = 0; slot < playerInventory.getContainerSize() && remaining > 0; slot++) {
               ItemStack slotStack = playerInventory.getItem(slot);
               if (slotStack.getItem() == ModItems.REGRET_ORB) {
                  int toShrink = Math.min(remaining, slotStack.getCount());
                  playerInventory.removeItem(slot, toShrink);
                  remaining -= toShrink;
               }
            }
         }
      }

      public void saveTemplate() {
         if (this.player.getUUID().equals(this.tileEntity.getOwnerId())) {
            if (this.player.getLevel().isClientSide()) {
               this.player.playSound(SoundEvents.UI_BUTTON_CLICK, 1.0F, 1.0F);
               ModNetwork.CHANNEL
                  .sendToServer(new ServerboundSkillAltarActionMessage(this.pos, ServerboundSkillAltarActionMessage.Action.SAVE, this.templateIndex));
            } else {
               if (this.player instanceof ServerPlayer serverPlayer) {
                  SkillAltarData altarData = SkillAltarData.get(serverPlayer.getLevel());
                  AbilityTree abilities = PlayerAbilitiesData.get(serverPlayer.getLevel()).getAbilities(serverPlayer.getUUID());
                  List<SpecializedSkill> sortedAbilities = abilities.getAll(SpecializedSkill.class, SpecializedSkill::isUnlocked)
                     .stream()
                     .sorted(SkillAltarData.SPECIALIZED_SKILL_HIGHEST_LEVEL_COMPARATOR)
                     .toList();
                  SkillAltarData.SkillIcon icon;
                  if (sortedAbilities.isEmpty()) {
                     icon = new SkillAltarData.SkillIcon("", false);
                  } else {
                     icon = new SkillAltarData.SkillIcon(sortedAbilities.get(0).getSpecialization().getId(), false);
                  }

                  altarData.saveSkillTemplate(
                     serverPlayer.getUUID(),
                     abilities,
                     PlayerTalentsData.get(serverPlayer.getLevel()).getTalents(serverPlayer.getUUID()),
                     this.templateIndex,
                     icon
                  );
                  SkillAltarBlock.openGui(this.pos, serverPlayer, this.templateIndex, true);
               }
            }
         }
      }

      public void shareInChat() {
         if (this.template != null) {
            if (this.player.getLevel().isClientSide()) {
               ModNetwork.CHANNEL
                  .sendToServer(new ServerboundSkillAltarActionMessage(this.pos, ServerboundSkillAltarActionMessage.Action.SHARE, this.templateIndex));
            } else {
               if (this.player instanceof ServerPlayer serverPlayer) {
                  MutableComponent acceptTxt = new TextComponent(this.template.getIcon().key()).withStyle(ChatFormatting.AQUA);
                  acceptTxt.withStyle(
                     style -> style.withHoverEvent(new HoverEvent(Action.SHOW_TEXT, new TextComponent("Click to copy to clipboard")))
                        .withClickEvent(new ClickEvent(net.minecraft.network.chat.ClickEvent.Action.COPY_TO_CLIPBOARD, this.template.exportToString()))
                  );
                  Component acceptMessage = new TextComponent("Sharing skill template ")
                     .append(new TextComponent("[").withStyle(ChatFormatting.GREEN))
                     .append(acceptTxt)
                     .append(new TextComponent("]").withStyle(ChatFormatting.GREEN));
                  serverPlayer.getServer()
                     .getPlayerList()
                     .broadcastMessage(
                        new TranslatableComponent("chat.type.text", new Object[]{this.player.getDisplayName(), acceptMessage}), ChatType.CHAT, Util.NIL_UUID
                     );
               }
            }
         }
      }
   }

   public static class Import extends SkillAltarContainer {
      public Import(
         int id, Inventory playerInventory, BlockPos pos, SkillAltarData.SkillTemplate template, int templateIndex, List<SkillAltarData.SkillIcon> skillIcons
      ) {
         super(ModContainers.SKILL_ALTAR_IMPORT_CONTAINER, id, playerInventory, pos, template, templateIndex, skillIcons);
      }

      public void importTemplate(String input) {
         if (this.player.getLevel().isClientSide()) {
            ModNetwork.CHANNEL
               .sendToServer(new ServerboundSkillAltarActionMessage(this.pos, ServerboundSkillAltarActionMessage.Action.IMPORT, this.templateIndex, input));
         } else {
            if (this.player instanceof ServerPlayer serverPlayer) {
               SkillAltarData altarData = SkillAltarData.get(serverPlayer.getLevel());
               SkillAltarData.DeserializationResult<SkillAltarData.SkillTemplate> skillTemplate = SkillAltarData.SkillTemplate.fromString(input);
               if (skillTemplate.valid()) {
                  altarData.saveSkillTemplate(serverPlayer.getUUID(), this.templateIndex, skillTemplate.deserializedValue());
                  SkillAltarBlock.openGui(this.pos, serverPlayer, this.templateIndex, true);
               }
            }
         }
      }
   }
}
