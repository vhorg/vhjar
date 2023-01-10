package iskallia.vault.client.gui.screen.bounty.element;

import com.mojang.datafixers.util.Pair;
import iskallia.vault.bounty.Bounty;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.framework.element.ButtonElement;
import iskallia.vault.client.gui.framework.element.ContainerElement;
import iskallia.vault.client.gui.framework.element.LabelElement;
import iskallia.vault.client.gui.framework.element.NineSliceElement;
import iskallia.vault.client.gui.framework.element.TextureAtlasElement;
import iskallia.vault.client.gui.framework.element.spi.IElement;
import iskallia.vault.client.gui.framework.render.Tooltips;
import iskallia.vault.client.gui.framework.render.spi.ITooltipRenderFunction;
import iskallia.vault.client.gui.framework.screen.layout.ScreenLayout;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.spatial.spi.IPosition;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;
import iskallia.vault.client.gui.framework.text.LabelTextStyle;
import iskallia.vault.client.gui.screen.bounty.BountyScreen;
import iskallia.vault.container.BountyContainer;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.bounty.ServerboundRerollMessage;
import iskallia.vault.util.TextUtil;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class BountyTableContainerElement extends ContainerElement<BountyTableContainerElement> {
   private final int marginLeft = 7;
   private final int marginTop = 7;
   private final BountyContainer container;
   BountyElement bountyElement;
   final List<Pair<ButtonElement<?>, TextureAtlasElement<?>>> buttons = new ArrayList<>();

   public BountyTableContainerElement(ISpatial spatial, BountyContainer container) {
      super(spatial);
      this.container = container;
      this.createBaseElements();
      this.createBountySelection();
      this.createRerollButton();
      this.refreshBountyElement();
   }

   private void createBaseElements() {
      this.addElement(
         new NineSliceElement(
            Spatials.positionXYZ(0, 0, -50).size(this.getWorldSpatial().width(), this.getWorldSpatial().height()), ScreenTextures.DEFAULT_WINDOW_BACKGROUND
         )
      );
      this.addElement(this.createLabel(7, 7, "Bounty Table"));
      this.addElement(
         new NineSliceElement(
            Spatials.positionXY(this.getBountyElementStart() - 1, 1).width(3).height(this.getWorldSpatial().height() - 2),
            ScreenTextures.INSET_VERTICAL_SEPARATOR
         )
      );
   }

   private void createRerollButton() {
      this.addElement(
         ((ButtonElement)new ButtonElement(Spatials.positionXY(95, 117), ScreenTextures.BUTTON_BUTTON_REROLL_TEXTURES, this::handleReroll)
               .tooltip(this.createRerollButtonTooltip()))
            .setDisabled(() -> {
               if (this.bountyElement.getSelectedBounty() == null) {
                  return true;
               } else if (this.bountyElement.getStatus() != BountyElement.Status.AVAILABLE) {
                  return true;
               } else {
                  Bounty bounty = this.bountyElement.getSelectedBounty();
                  ItemStack bronze = this.container.getBronzeSlot().getItem();
                  int amount = bronze.getCount();
                  int cost = ModConfigs.BOUNTY_CONFIG.getCost(this.container.getVaultLevel(), bounty.getExpiration() - Instant.now().toEpochMilli());
                  return cost > amount;
               }
            })
      );
   }

   @NotNull
   private ITooltipRenderFunction createRerollButtonTooltip() {
      return Tooltips.multi(
         () -> {
            Bounty bounty = this.bountyElement.getSelectedBounty();
            if (bounty == null) {
               return List.of(new TextComponent("Select a bounty to see the cost of reroll"));
            } else if (this.bountyElement.getStatus() != BountyElement.Status.AVAILABLE) {
               return List.of(new TextComponent("Only \"Available\" bounties can be rerolled."));
            } else {
               List<Component> tooltips = new ArrayList<>();
               tooltips.add(new TextComponent("Reroll Selected Bounty"));
               ItemStack bronze = this.container.getBronzeSlot().getItem();
               int amount = bronze.getCount();
               int cost = ModConfigs.BOUNTY_CONFIG.getCost(this.container.getVaultLevel(), bounty.getExpiration() - Instant.now().toEpochMilli());
               tooltips.add(new TextComponent(""));
               tooltips.add(
                  new TextComponent("Current Cost: ")
                     .append(new ItemStack(ModBlocks.VAULT_BRONZE).getHoverName())
                     .append(" x" + cost)
                     .append(" [%s]".formatted(bronze.getCount()))
                     .withStyle(cost > amount ? ChatFormatting.RED : ChatFormatting.GREEN)
               );
               return tooltips;
            }
         }
      );
   }

   private void handleReroll() {
      Bounty bounty = this.bountyElement.getSelectedBounty();
      if (bounty != null) {
         ItemStack bronze = this.container.getBronzeSlot().getItem();
         int amount = bronze.getCount();
         int cost = ModConfigs.BOUNTY_CONFIG.getCost(this.container.getVaultLevel(), bounty.getExpiration() - Instant.now().toEpochMilli());
         if (amount >= cost) {
            ModNetwork.CHANNEL.sendToServer(new ServerboundRerollMessage(bounty.getId()));
            Minecraft.getInstance().player.playSound(SoundEvents.UI_BUTTON_CLICK, 0.7F, 1.0F);
         }
      }
   }

   public void refreshBountySelection() {
      for (Pair<ButtonElement<?>, TextureAtlasElement<?>> pair : this.buttons) {
         ButtonElement<?> button = (ButtonElement<?>)pair.getFirst();
         if (button instanceof BountyTableContainerElement.BountyButtonElement bountyButton) {
            this.elementStore.removeElement(bountyButton);
            this.elementStore.removeElement((IElement)pair.getSecond());
         }
      }

      this.buttons.clear();
      this.refreshBountyElement();
      this.createBountySelection();
      ScreenLayout.requestLayout();
   }

   private void refreshBountyElement() {
      if (this.bountyElement != null) {
         this.elementStore.removeElement(this.bountyElement);
      }

      this.bountyElement = this.addElement(
         new BountyElement(
               this,
               Spatials.positionXY(this.getBountyElementStart() + 5, 7).width(this.getBountyElementStart() - 10).height(this.getWorldSpatial().height() - 5),
               this.container
            )
            .layout((screen, gui, parent, world) -> {
               world.width(this.getBountyElementStart() - 10);
               world.height(world.height() - 5);
            })
            .enableSpatialDebugRender(false, true)
      );
   }

   private void createBountySelection() {
      int buttonWidth = 18;
      int labelX = 7;
      int labelY = 23;
      int buttonX = this.getWorldSpatial().width() / 2 - 14 - buttonWidth * 3 - 32;
      int buttonY = labelY + 25;
      int activeRow = 0;
      int availableRow = 1;
      int completeRow = 2;
      this.addElement(
         new HeaderElement(
            Spatials.positionXY(labelX, labelY).width(this.getWorldSpatial().width() / 2 - 14 + 1).height(14 + buttonWidth * 3 - 3),
            new TextComponent("Bounty Selection"),
            true
         )
      );

      for (int row = 0; row < 3; row++) {
         if (row == activeRow) {
            BountyElement.Status active = BountyElement.Status.ACTIVE;
            this.addElement(
               new LabelElement(
                  Spatials.positionXY(buttonX - 4 - active.getWidth(), buttonY + 4).positionZ(100).width(active.getWidth()).height(buttonWidth + 7),
                  active.getDisplay(),
                  LabelTextStyle.shadow()
               )
            );
         } else if (row == availableRow) {
            BountyElement.Status available = BountyElement.Status.AVAILABLE;
            this.addElement(
               new LabelElement(
                  Spatials.positionXY(buttonX - 4 - available.getWidth(), buttonY + 4).positionZ(100).width(available.getWidth()).height(buttonWidth + 7),
                  available.getDisplay(),
                  LabelTextStyle.shadow()
               )
            );
         } else {
            BountyElement.Status complete = BountyElement.Status.COMPLETE;
            this.addElement(
               new LabelElement(
                  Spatials.positionXY(buttonX - 4 - complete.getWidth(), buttonY + 4).positionZ(100).width(complete.getWidth()).height(buttonWidth + 7),
                  complete.getDisplay(),
                  LabelTextStyle.shadow()
               )
            );
         }

         for (int column = 0; column < 3; column++) {
            if (row == activeRow && column == 0) {
               this.createButton(
                  buttonX, buttonY, this.container.getActive().size() > column ? this.container.getActive().get(column) : null, BountyElement.Status.ACTIVE
               );
            } else if (row == availableRow) {
               this.createButton(
                  buttonX,
                  buttonY,
                  this.container.getAvailable().size() > column ? this.container.getAvailable().get(column) : null,
                  BountyElement.Status.AVAILABLE
               );
            } else if (row == completeRow) {
               this.createButton(
                  buttonX,
                  buttonY,
                  this.container.getComplete().size() > column ? this.container.getComplete().get(column) : null,
                  BountyElement.Status.COMPLETE
               );
            }

            buttonX += buttonWidth;
         }

         labelX += 58;
         buttonX = this.getWorldSpatial().width() / 2 - 14 - buttonWidth * 3 - 32;
         buttonY += buttonWidth + 3;
      }
   }

   @NotNull
   private LabelElement<?> createLabel(int labelX, int labelY, String pText) {
      return new LabelElement(
         Spatials.positionXY(labelX, labelY), new TextComponent(pText).withStyle(Style.EMPTY.withColor(-12632257)), LabelTextStyle.defaultStyle()
      );
   }

   private void createButton(int buttonX, int buttonY, Bounty bounty, BountyElement.Status status) {
      if (bounty == null) {
         this.buttons
            .add(
               Pair.of(
                  this.addElement(
                     new BountyTableContainerElement.BountyButtonElement(null, Spatials.positionXY(buttonX, buttonY), ScreenTextures.BUTTON_EMPTY_16_TEXTURES)
                        .setDisabled(true)
                  ),
                  new TextureAtlasElement(Spatials.zero(), ScreenTextures.EMPTY)
               )
            );
      } else {
         this.buttons
            .add(
               Pair.of(
                  this.addElement(
                     new BountyTableContainerElement.BountyButtonElement(
                           bounty.getId(), Spatials.positionXY(buttonX, buttonY), ScreenTextures.BUTTON_EMPTY_16_TEXTURES, () -> {
                              this.bountyElement.setBounty(bounty.getId(), status);
                              Minecraft.getInstance().player.playSound(SoundEvents.UI_BUTTON_CLICK, 0.7F, 1.0F);
                           }
                        )
                        .tooltip(
                           Tooltips.shift(
                              Tooltips.single(() -> TextUtil.formatLocationPathAsProperNoun(bounty.getTask().getTaskType())),
                              Tooltips.single(() -> new TextComponent(bounty.getTask().getBountyId().toString()))
                           )
                        )
                        .enableSpatialDebugRender(false, true)
                  ),
                  this.addElement(
                     new TextureAtlasElement(Spatials.positionXYZ(buttonX + 1, buttonY + 1, 10), BountyScreen.TASK_ICON_MAP.get(bounty.getTask().getTaskType()))
                  )
               )
            );
      }
   }

   private int getBountyElementStart() {
      return this.getWorldSpatial().width() / 2;
   }

   private static class BountyButtonElement extends ButtonElement<BountyTableContainerElement.BountyButtonElement> {
      private final UUID bountyId;

      public BountyButtonElement(UUID bountyId, IPosition position, ButtonElement.ButtonTextures textures, Runnable onClick) {
         super(position, textures, onClick);
         this.bountyId = bountyId;
      }

      public BountyButtonElement(UUID bountyId, IPosition position, ButtonElement.ButtonTextures textures) {
         super(position, textures, () -> {});
         this.bountyId = bountyId;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (o != null && this.getClass() == o.getClass()) {
            BountyTableContainerElement.BountyButtonElement that = (BountyTableContainerElement.BountyButtonElement)o;
            return this.bountyId != null && that.bountyId != null ? this.bountyId.equals(that.bountyId) : true;
         } else {
            return false;
         }
      }

      @Override
      public int hashCode() {
         return this.bountyId != null ? this.bountyId.hashCode() : 0;
      }
   }
}
