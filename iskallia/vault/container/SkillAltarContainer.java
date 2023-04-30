package iskallia.vault.container;

import iskallia.vault.block.SkillAltarBlock;
import iskallia.vault.block.entity.SkillAltarTileEntity;
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
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class SkillAltarContainer extends OverSizedSlotContainer {
   private static final int PLAYER_INVENTORY_TOP_Y = 132;
   private final BlockPos pos;
   @Nullable
   private SkillAltarData.SkillTemplate template;
   private int templateIndex;
   private List<SkillAltarData.SkillIcon> skillIcons;
   protected AbstractElementContainer.SlotIndexRange hotbarSlotIndexRange;
   protected AbstractElementContainer.SlotIndexRange inventorySlotIndexRange;
   protected AbstractElementContainer.SlotIndexRange hotbarInventorySlotIndexRange;
   protected AbstractElementContainer.SlotIndexRange regretOrbsIndexRange;
   private final SkillAltarTileEntity tileEntity;
   private int missingRegretOrbs = -1;

   @Nullable
   public SkillAltarData.SkillTemplate getTemplate() {
      return this.template;
   }

   public SkillAltarContainer(
      int id,
      Inventory playerInventory,
      BlockPos pos,
      @Nullable SkillAltarData.SkillTemplate template,
      int templateIndex,
      List<SkillAltarData.SkillIcon> skillIcons
   ) {
      super(ModContainers.SKILL_ALTAR_CONTAINER, id, playerInventory.player);
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
         this.initSlots(playerInventory);
      } else {
         this.tileEntity = null;
      }
   }

   public boolean isEmptyTemplate() {
      return this.template == null;
   }

   public int getNumberOfRegretOrbsRequired() {
      return this.template == null ? Integer.MAX_VALUE : Math.max(1, (int)(ModConfigs.SKILL_ALTAR.getPerLevelCost() * SidedHelper.getVaultLevel(this.player)));
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

   public boolean stillValid(Player pPlayer) {
      return true;
   }

   public void setPlayerAbilitiesAndTalentsFromTemplate() {
      if (this.player instanceof ServerPlayer serverPlayer) {
         int unspentRegretPoints = PlayerVaultStatsData.get(serverPlayer.getLevel()).getVaultStats(serverPlayer).getUnspentRegretPoints();
         this.setPlayerAbilitiesAndTalentsFromTemplate(unspentRegretPoints);
      }
   }

   public void setPlayerAbilitiesAndTalentsFromTemplate(int unspentRegretPoints) {
      if (this.template != null && this.player.getUUID().equals(this.tileEntity.getOwnerId()) && this.getNumberOfMissingRegretOrbs(unspentRegretPoints) <= 0) {
         if (this.player.getLevel().isClientSide()) {
            this.player.playSound(ModSounds.SKILL_TREE_LEARN_SFX, 1.0F, 1.0F);
            ModNetwork.CHANNEL
               .sendToServer(new ServerboundSkillAltarActionMessage(this.pos, ServerboundSkillAltarActionMessage.Action.LOAD, this.templateIndex));
         } else {
            if (this.player instanceof ServerPlayer serverPlayer) {
               this.consumeRequiredRegretPointsOrOrbs(serverPlayer, unspentRegretPoints);
               int skillPointsBefore = PlayerAbilitiesData.get(serverPlayer.getLevel()).getAbilities(serverPlayer).getSpentLearntPoints();
               skillPointsBefore += PlayerTalentsData.get(serverPlayer.getLevel()).getTalents(serverPlayer).getSpentLearntPoints();
               PlayerVaultStatsData statsData = PlayerVaultStatsData.get(serverPlayer.getLevel());
               statsData.addSkillPoints(serverPlayer, skillPointsBefore);
               this.setAbilitiesFromTemplate(serverPlayer);
               this.setTalentsFromTemplate(serverPlayer);
               int skillPointsAfter = PlayerAbilitiesData.get(serverPlayer.getLevel()).getAbilities(serverPlayer).getSpentLearntPoints();
               skillPointsAfter += PlayerTalentsData.get(serverPlayer.getLevel()).getTalents(serverPlayer).getSpentLearntPoints();
               statsData.spendSkillPoints(serverPlayer, skillPointsAfter);
               SkillAltarBlock.openGui(this.pos, serverPlayer, this.templateIndex);
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
               SkillAltarBlock.openGui(this.pos, serverPlayer, this.templateIndex);
            }
         }
      }
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

   @Override
   public void clicked(int slotId, int dragType, ClickType clickTypeIn, Player player) {
      super.clicked(slotId, dragType, clickTypeIn, player);
      this.missingRegretOrbs = -1;
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
            SkillAltarBlock.openGui(this.pos, serverPlayer, this.templateIndex);
         }
      }
   }
}
