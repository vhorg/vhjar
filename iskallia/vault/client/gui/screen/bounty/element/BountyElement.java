package iskallia.vault.client.gui.screen.bounty.element;

import iskallia.vault.bounty.Bounty;
import iskallia.vault.client.ClientExpertiseData;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.framework.element.ContainerElement;
import iskallia.vault.client.gui.framework.element.DynamicLabelElement;
import iskallia.vault.client.gui.framework.element.NineSliceButtonElement;
import iskallia.vault.client.gui.framework.element.spi.IElement;
import iskallia.vault.client.gui.framework.render.Tooltips;
import iskallia.vault.client.gui.framework.screen.layout.ScreenLayout;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.spatial.spi.IPosition;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;
import iskallia.vault.client.gui.framework.text.LabelTextStyle;
import iskallia.vault.client.gui.framework.text.TextBorder;
import iskallia.vault.client.gui.screen.bounty.BountyScreen;
import iskallia.vault.client.gui.screen.bounty.element.task.TaskScrollContainerElement;
import iskallia.vault.container.BountyContainer;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.bounty.ServerboundAbandonBountyMessage;
import iskallia.vault.network.message.bounty.ServerboundActivateBountyMessage;
import iskallia.vault.network.message.bounty.ServerboundClaimRewardMessage;
import iskallia.vault.skill.base.TieredSkill;
import iskallia.vault.skill.expertise.type.BountyHunterExpertise;
import iskallia.vault.util.TextUtil;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.jetbrains.annotations.NotNull;

public class BountyElement extends ContainerElement<BountyElement> {
   private final BountyTableContainerElement parent;
   private final BountyContainer container;
   private final HeaderElement headerElement;
   private Bounty bounty;
   private BountyElement.Status status = BountyElement.Status.NONE;
   private final TaskScrollContainerElement taskScrollContainerElement;
   private final NineSliceButtonElement<?> actionButton;

   public BountyElement(BountyTableContainerElement parent, ISpatial spatial, BountyContainer container) {
      super(spatial);
      this.parent = parent;
      this.container = container;
      this.headerElement = this.addElement(
         new HeaderElement(
            Spatials.positionXY(1, 0).width(this.getWorldSpatial().width() - 5).height(20),
            new TextComponent("Select a bounty!"),
            ScreenTextures.BOUNTY_UNIDENTIFIED,
            false
         )
      );
      this.taskScrollContainerElement = this.addElement(
         new TaskScrollContainerElement(Spatials.positionXY(4, 20).width(this.width() - 10).height(this.height() - 53))
      );
      this.actionButton = this.addElement(
         new NineSliceButtonElement(
               Spatials.positionXY(4, this.taskScrollContainerElement.bottom() + 3).width(this.width() - 10).height(20),
               ScreenTextures.BUTTON_EMPTY_TEXTURES,
               this::onActionClicked
            )
            .setRenderButtonHeld(
               () -> (this.status == BountyElement.Status.ACTIVE || this.status == BountyElement.Status.LEGENDARY) && !this.bounty.getTask().isComplete()
            )
            .setDisabled(this::hasMaxActive)
            .label(this::getButtonLabel, LabelTextStyle.center().shadow())
      );
      this.headerElement.enableSpatialDebugRender(false, true);
      this.taskScrollContainerElement.enableSpatialDebugRender(false, true);
      this.actionButton.enableSpatialDebugRender(false, true);
      if (!this.container.getLegendary().isEmpty()) {
         this.setBounty(this.container.getLegendary().get(0).getId(), BountyElement.Status.LEGENDARY);
      } else if (!this.container.getActive().isEmpty()) {
         this.setBounty(this.container.getActive().get(0).getId(), BountyElement.Status.ACTIVE);
      }
   }

   private boolean hasMaxActive() {
      int maxActive = 0;
      Player player = Minecraft.getInstance().player;
      if (player != null) {
         for (TieredSkill learnedTalentNode : ClientExpertiseData.getLearnedTalentNodes()) {
            if (learnedTalentNode.getChild() instanceof BountyHunterExpertise bountyHunter) {
               maxActive += bountyHunter.getMaxActive();
            }
         }
      }

      if (maxActive == 0) {
         maxActive = 1;
      }

      return this.container.getActive().size() >= maxActive;
   }

   @NotNull
   private TextComponent getButtonLabel() {
      switch (this.status) {
         case ACTIVE:
         case LEGENDARY:
            if (this.bounty.getTask().isComplete()) {
               return new TextComponent("Claim Reward");
            }

            return new TextComponent("Abandon");
         case AVAILABLE:
            return new TextComponent("Activate");
         default:
            return new TextComponent("");
      }
   }

   private void onActionClicked() {
      switch (this.status) {
         case ACTIVE:
         case LEGENDARY:
            LocalPlayer player = Minecraft.getInstance().player;
            if (this.bounty.getTask().isComplete()) {
               ModNetwork.CHANNEL.sendToServer(new ServerboundClaimRewardMessage(this.bounty.getId()));
               if (this.status == BountyElement.Status.LEGENDARY) {
                  this.container.getLegendary().removeById(this.bounty.getId());
               } else {
                  this.container.getActive().removeById(this.bounty.getId());
                  this.container.getComplete().add(this.bounty);
                  this.bounty.setExpiration(Instant.now().plus(ModConfigs.BOUNTY_CONFIG.getWaitingPeriodSeconds(), ChronoUnit.SECONDS).toEpochMilli());
               }

               if (player != null) {
                  player.playSound(SoundEvents.PLAYER_LEVELUP, 0.7F, 1.0F);
               }
            } else if (this.actionButton.getTimeHeld() > 60.0) {
               if (this.status == BountyElement.Status.LEGENDARY) {
                  ModNetwork.CHANNEL.sendToServer(new ServerboundAbandonBountyMessage(this.bounty.getId()));
                  this.container.getLegendary().removeById(this.bounty.getId());
               } else {
                  this.status = BountyElement.Status.COMPLETE;
                  ModNetwork.CHANNEL.sendToServer(new ServerboundAbandonBountyMessage(this.bounty.getId()));
                  this.container.getActive().removeById(this.bounty.getId());
                  this.container.getComplete().add(this.bounty);
                  this.bounty.setExpiration(Instant.now().plus(ModConfigs.BOUNTY_CONFIG.getAbandonedPenaltySeconds(), ChronoUnit.SECONDS).toEpochMilli());
               }

               if (player != null) {
                  player.playSound(SoundEvents.CANDLE_EXTINGUISH, 1.0F, 1.0F);
               }
            }

            if (this.bounty != null) {
               this.update(this.bounty);
            }

            this.parent.refreshBountySelection();
            break;
         case AVAILABLE:
            if (!this.hasMaxActive()) {
               this.container.getActive().add(this.bounty);
               this.container.getAvailable().removeById(this.bounty.getId());
               this.status = BountyElement.Status.ACTIVE;
               ModNetwork.CHANNEL.sendToServer(new ServerboundActivateBountyMessage(this.bounty.getId()));
               this.update(this.bounty);
               this.parent.refreshBountySelection();
            }
      }
   }

   public void setBounty(UUID bountyId, BountyElement.Status status) {
      Optional<Bounty> optionalBounty = this.container.getBountyById(bountyId);
      if (!optionalBounty.isEmpty()) {
         this.bounty = optionalBounty.get();
         this.status = status;
         this.update(this.bounty);
      }
   }

   public void update(Bounty bounty) {
      MutableComponent bountyTitle = TextUtil.formatLocationPathAsProperNoun(bounty.getTask().getTaskType()).withStyle(this.status.display.getStyle());
      this.headerElement.setTitle(bountyTitle);
      this.headerElement.setIcon(BountyScreen.TASK_ICON_MAP.get(bounty.getTask().getTaskType()));
      this.headerElement.tooltip(Tooltips.single(() -> this.status.getDisplay()));
      this.taskScrollContainerElement.setTaskElement(bounty.getTask(), this.status);
      this.actionButton.label(this::getButtonLabel, LabelTextStyle.center().shadow());
      this.actionButton.setDisabled(this.status == BountyElement.Status.COMPLETE || this.bounty == null);
      if (this.status == BountyElement.Status.COMPLETE) {
         this.replaceExpirationLabel();
      } else {
         if (this.status == BountyElement.Status.AVAILABLE && this.hasMaxActive()) {
            this.actionButton.setDisabled(true);
         }

         this.removeExpirationLabel();
      }

      ScreenLayout.requestLayout();
   }

   private void replaceExpirationLabel() {
      this.removeExpirationLabel();
      BountyElement.ExpirationElement expirationElement = new BountyElement.ExpirationElement(
            Spatials.positionXYZ(0, 0, 1),
            () -> this.bounty.getExpiration() - Instant.now().toEpochMilli(),
            LabelTextStyle.center().shadow(),
            !this.bounty.getTask().isComplete()
         )
         .layout((screen, gui, parent, world) -> {
            world.size(this.actionButton.width(), this.actionButton.height());
            world.translateXYZ(4, this.height() - 19, 1);
         })
         .enableSpatialDebugRender(false, true);
      this.addElement(expirationElement);
   }

   private void removeExpirationLabel() {
      BountyElement.ExpirationElement element = null;

      for (IElement e : this.elementStore.getSpatialElementList()) {
         if (e instanceof BountyElement.ExpirationElement expirationElement) {
            element = expirationElement;
            break;
         }
      }

      if (element != null) {
         this.removeElement(element);
      }
   }

   public Bounty getSelectedBounty() {
      return this.bounty;
   }

   public BountyElement.Status getStatus() {
      return this.status;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         BountyElement that = (BountyElement)o;
         return this.bounty.getId().equals(that.bounty.getId());
      } else {
         return false;
      }
   }

   public static class ExpirationElement extends DynamicLabelElement<Long, BountyElement.ExpirationElement> {
      private final boolean isAbandoned;

      public ExpirationElement(IPosition position, Supplier<Long> valueSupplier, LabelTextStyle.Builder labelTextStyle, boolean isAbandoned) {
         super(position, valueSupplier, labelTextStyle);
         this.isAbandoned = isAbandoned;
      }

      protected void onValueChanged(Long value) {
         if (value >= 0L) {
            String timeRemaining = DurationFormatUtils.formatDuration(value, "HH:mm:ss");
            String labelText = this.isAbandoned ? "Abandon Penalty: " : "New Bounty in: ";
            this.set(new TextComponent(labelText + timeRemaining).withStyle(ChatFormatting.WHITE));
         }
      }
   }

   public static enum Status {
      ACTIVE(new TextComponent("Active").withStyle(ChatFormatting.GREEN)),
      AVAILABLE(new TextComponent("Available").withStyle(ChatFormatting.WHITE)),
      COMPLETE(new TextComponent("Complete").withStyle(ChatFormatting.AQUA)),
      LEGENDARY(new TextComponent("Legendary").withStyle(ChatFormatting.YELLOW)),
      NONE(new TextComponent(""));

      private final MutableComponent display;

      private Status(MutableComponent display) {
         this.display = display;
      }

      public MutableComponent getDisplay() {
         return this.display;
      }

      public int getWidth() {
         return TextBorder.DEFAULT_FONT.get().width(this.getDisplay());
      }
   }
}
