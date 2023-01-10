package iskallia.vault.client.gui.screen;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.VaultMod;
import iskallia.vault.block.entity.CryoChamberTileEntity;
import iskallia.vault.client.ClientEternalData;
import iskallia.vault.client.gui.helper.FontHelper;
import iskallia.vault.client.gui.helper.UIHelper;
import iskallia.vault.client.gui.widget.TooltipImageButton;
import iskallia.vault.config.EternalAuraConfig;
import iskallia.vault.config.entry.FloatRangeEntry;
import iskallia.vault.container.inventory.CryochamberContainer;
import iskallia.vault.entity.entity.EternalEntity;
import iskallia.vault.entity.eternal.EternalDataSnapshot;
import iskallia.vault.entity.eternal.EternalHelper;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.init.ModShaders;
import iskallia.vault.network.message.EternalInteractionMessage;
import iskallia.vault.network.message.ServerboundRenameEternalMessage;
import iskallia.vault.network.message.ServerboundToggleEternalPlayerSkinMessage;
import java.awt.Rectangle;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class CryochamberScreen extends AbstractContainerScreen<CryochamberContainer> {
   private static final DecimalFormat ATTRIBUTE_FORMAT = new DecimalFormat("0.0", DecimalFormatSymbols.getInstance(Locale.ROOT));
   private static final DecimalFormat ATTRIBUTE_MS_FORMAT = new DecimalFormat("0.00", DecimalFormatSymbols.getInstance(Locale.ROOT));
   private static final DecimalFormat PERCENT_FORMAT = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ROOT));
   private static final ResourceLocation TEXTURE = VaultMod.id("textures/gui/cryochamber_inventory.png");
   private EternalDataSnapshot prevSnapshot = null;
   private EternalEntity eternalSkinCache = null;

   public CryochamberScreen(CryochamberContainer screenContainer, Inventory inv, Component title) {
      super(screenContainer, inv, title);
      this.imageWidth = 176;
      this.imageHeight = 211;
      this.inventoryLabelY = this.imageHeight - 94;
      this.titleLabelX = 19;
   }

   protected void init() {
      super.init();
      this.refreshButtons();
   }

   private void refreshButtons() {
      this.clearWidgets();
      EternalDataSnapshot snapshot = this.getEternal();
      if (snapshot != null) {
         if (snapshot.getUsedLevels() < snapshot.getLevel()) {
            int offsetX = this.leftPos + 78;
            int yOffset = 0;
            int yShift = 16;
            this.addRenderableWidget(
               new ImageButton(
                  offsetX,
                  this.topPos + 18,
                  16,
                  16,
                  176,
                  yOffset,
                  yShift,
                  TEXTURE,
                  256,
                  256,
                  btn -> {
                     if (snapshot.getUsedLevels() < snapshot.getLevel()) {
                        ModNetwork.CHANNEL.sendToServer(EternalInteractionMessage.levelUp("health"));
                     }
                  },
                  (btn, matrixStack, mouseX, mouseY) -> this.renderAttributeHoverTooltip(
                     ModConfigs.ETERNAL_ATTRIBUTES.getHealthRollRange(), 1.0F, ATTRIBUTE_FORMAT, matrixStack, mouseX, mouseY
                  ),
                  TextComponent.EMPTY
               )
            );
            this.addRenderableWidget(
               new ImageButton(
                  offsetX,
                  this.topPos + 36,
                  16,
                  16,
                  176,
                  yOffset,
                  yShift,
                  TEXTURE,
                  256,
                  256,
                  btn -> {
                     if (snapshot.getUsedLevels() < snapshot.getLevel()) {
                        ModNetwork.CHANNEL.sendToServer(EternalInteractionMessage.levelUp("damage"));
                     }
                  },
                  (btn, matrixStack, mouseX, mouseY) -> this.renderAttributeHoverTooltip(
                     ModConfigs.ETERNAL_ATTRIBUTES.getDamageRollRange(), 1.0F, ATTRIBUTE_FORMAT, matrixStack, mouseX, mouseY
                  ),
                  TextComponent.EMPTY
               )
            );
            this.addRenderableWidget(
               new ImageButton(
                  offsetX,
                  this.topPos + 54,
                  16,
                  16,
                  176,
                  yOffset,
                  yShift,
                  TEXTURE,
                  256,
                  256,
                  btn -> {
                     if (snapshot.getUsedLevels() < snapshot.getLevel()) {
                        ModNetwork.CHANNEL.sendToServer(EternalInteractionMessage.levelUp("movespeed"));
                     }
                  },
                  (btn, matrixStack, mouseX, mouseY) -> this.renderAttributeHoverTooltip(
                     ModConfigs.ETERNAL_ATTRIBUTES.getMoveSpeedRollRange(), 10.0F, ATTRIBUTE_MS_FORMAT, matrixStack, mouseX, mouseY
                  ),
                  TextComponent.EMPTY
               )
            );
         }

         if (snapshot.getAbilityName() == null) {
            List<EternalAuraConfig.AuraConfig> options = ModConfigs.ETERNAL_AURAS.getRandom(snapshot.getSeededRand(), 3);
            int abilityX = this.leftPos + 8;
            int abilityY = this.topPos + 90;

            for (EternalAuraConfig.AuraConfig abilityOption : options) {
               this.addRenderableWidget(
                  new TooltipImageButton(
                     abilityX,
                     abilityY,
                     24,
                     24,
                     192,
                     0,
                     24,
                     TEXTURE,
                     256,
                     256,
                     btn -> ModNetwork.CHANNEL.sendToServer(EternalInteractionMessage.selectEffect(abilityOption.getName()))
                  )
               );
               abilityX += 30;
            }
         }
      }
   }

   private void renderAttributeHoverTooltip(FloatRangeEntry range, float multiplier, DecimalFormat format, PoseStack matrixStack, int mouseX, int mouseY) {
      matrixStack.pushPose();
      matrixStack.translate(0.0, 0.0, 300.0);
      String min = format.format(range.getMin() * multiplier);
      String max = format.format(range.getMax() * multiplier);
      Component txt = new TextComponent("Adds +" + min + " to +" + max);
      this.renderTooltip(matrixStack, List.of(txt.getVisualOrderText()), mouseX, mouseY, this.font);
      matrixStack.popPose();
   }

   public void containerTick() {
      super.containerTick();
      EternalDataSnapshot snapshot = this.getEternal();
      if (snapshot != null) {
         if (this.prevSnapshot == null || !this.prevSnapshot.areStatisticsEqual(snapshot)) {
            this.prevSnapshot = snapshot;
            this.refreshButtons();
         }
      }
   }

   protected void renderBg(PoseStack matrixStack, float partialTicks, int x, int y) {
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShaderTexture(0, TEXTURE);
      int offsetX = (this.width - this.imageWidth) / 2;
      int offsetY = (this.height - this.imageHeight) / 2;
      this.blit(matrixStack, offsetX, offsetY, 0, 0, this.imageWidth, this.imageHeight);
   }

   protected void renderLabels(PoseStack matrixStack, int mouseX, int mouseY) {
      EternalDataSnapshot snapshot = this.getEternal();
      if (snapshot != null) {
         if (this.eternalSkinCache == null) {
            this.eternalSkinCache = EternalHelper.spawnEternal(Minecraft.getInstance().level, snapshot);
            this.eternalSkinCache.skin.updateSkin(snapshot.getName());
            Arrays.stream(EquipmentSlot.values()).forEach(slot -> this.eternalSkinCache.setItemSlot(slot, ItemStack.EMPTY));
            this.eternalSkinCache.setCustomNameVisible(false);
         }

         if (snapshot.isAncient()) {
            FontHelper.drawStringWithBorder(matrixStack, this.title, (float)this.titleLabelX, (float)this.titleLabelY, 15910161, 4210752);
         } else {
            this.font.draw(matrixStack, this.title, this.titleLabelX, this.titleLabelY, 4210752);
         }

         this.font.draw(matrixStack, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 4210752);
         this.renderEternal(snapshot, matrixStack, mouseX, mouseY);
         RenderSystem.enableDepthTest();
         this.renderLevel(snapshot, matrixStack);
         this.renderAttributeDisplay(snapshot, matrixStack);
         this.renderAbility(snapshot, matrixStack, mouseX, mouseY);
         RenderSystem.setShaderTexture(0, TEXTURE);
         boolean isHovered = this.leftPos + 5 <= mouseX && mouseX <= this.leftPos + 5 + 11 && this.topPos + 5 <= mouseY && mouseY <= this.topPos + 5 + 11;
         this.blit(matrixStack, 5, 5, 179, isHovered ? 82 : 66, 11, 11);
         isHovered = this.leftPos + 100 <= mouseX && mouseX <= this.leftPos + 100 + 11 && this.topPos + 26 <= mouseY && mouseY <= this.topPos + 26 + 11;
         if (isHovered) {
            this.blit(matrixStack, 100, 26, 178, 97, 13, 13);
         }

         this.blit(matrixStack, 101, 27, 179, snapshot.isUsingPlayerSkin() ? 50 : 34, 11, 11);
      }
   }

   private void renderAbility(EternalDataSnapshot snapshot, PoseStack matrixStack, int mouseX, int mouseY) {
      if (snapshot.getAbilityName() == null) {
         List<EternalAuraConfig.AuraConfig> options = ModConfigs.ETERNAL_AURAS.getRandom(snapshot.getSeededRand(), 3);
         int abilityX = 12;
         int abilityY = 94;

         for (EternalAuraConfig.AuraConfig abilityOption : options) {
            RenderSystem.setShaderTexture(0, new ResourceLocation(abilityOption.getIconPath()));
            blit(matrixStack, abilityX, abilityY, 16.0F, 16.0F, 16, 16, 16, 16);
            abilityX += 30;
         }

         int var12 = 8;
         int var13 = 90;

         for (EternalAuraConfig.AuraConfig abilityOption : options) {
            Rectangle box = new Rectangle(var12, var13, 24, 24);
            if (box.contains(mouseX - this.leftPos, mouseY - this.topPos)) {
               this.renderComponentTooltip(matrixStack, abilityOption.getTooltip(), mouseX - this.leftPos, mouseY - this.topPos);
            }

            var12 += 30;
         }
      } else {
         EternalAuraConfig.AuraConfig cfg = ModConfigs.ETERNAL_AURAS.getByName(snapshot.getAbilityName());
         if (cfg == null) {
            return;
         }

         RenderSystem.setShaderTexture(0, new ResourceLocation(cfg.getIconPath()));
         blit(matrixStack, 8, 92, 0.0F, 0.0F, 16, 16, 16, 16);
         matrixStack.pushPose();
         matrixStack.translate(26.0, 92.0, 0.0);
         matrixStack.scale(0.8F, 0.8F, 0.8F);
         UIHelper.renderWrappedText(matrixStack, new TextComponent(cfg.getDescription()), 82, 0, 4210752);
         matrixStack.popPose();
      }
   }

   private void renderAttributeDisplay(EternalDataSnapshot snapshot, PoseStack matrixStack) {
      String healthStr = ATTRIBUTE_FORMAT.format(snapshot.getEntityAttributes().get(Attributes.MAX_HEALTH));
      this.renderAttributeStats(matrixStack, "Health:", healthStr, 18, 32);
      String damageStr = ATTRIBUTE_FORMAT.format(snapshot.getEntityAttributes().get(Attributes.ATTACK_DAMAGE));
      this.renderAttributeStats(matrixStack, "Damage:", damageStr, 36, 48);
      String speedStr = ATTRIBUTE_MS_FORMAT.format(snapshot.getEntityAttributes().get(Attributes.MOVEMENT_SPEED) * 10.0F);
      this.renderAttributeStats(matrixStack, "Speed:", speedStr, 54, 64);
      int availableLevels = Math.max(snapshot.getLevel() - snapshot.getUsedLevels(), 0);
      if (availableLevels > 0) {
         String display = String.valueOf(availableLevels);
         int offsetX = this.font.width(display) / 2;
         matrixStack.pushPose();
         matrixStack.translate(86.0, 13.0, 0.0);
         matrixStack.scale(0.8F, 0.8F, 0.8F);
         matrixStack.translate(-offsetX, 0.0, 0.0);
         this.font.draw(matrixStack, display, 0.0F, 0.0F, 4210752);
         matrixStack.popPose();
      }

      String resistPercent = PERCENT_FORMAT.format(snapshot.getResistance() * 100.0F) + "%";
      String armorAmount = PERCENT_FORMAT.format(EternalHelper.getEternalGearModifierAdjustments(snapshot, Attributes.ARMOR, 0.0F));
      RenderSystem.setShaderTexture(0, TEXTURE);
      this.blit(matrixStack, 8, 72, 216, 16, 16, 16);
      matrixStack.pushPose();
      matrixStack.translate(24.0, 72.0, 0.0);
      matrixStack.scale(0.8F, 0.8F, 0.8F);
      this.font.draw(matrixStack, resistPercent, 0.0F, 5.0F, 4210752);
      matrixStack.popPose();
      RenderSystem.setShaderTexture(0, TEXTURE);
      this.blit(matrixStack, 39, 72, 216, 80, 16, 16);
      matrixStack.pushPose();
      matrixStack.translate(55.0, 72.0, 0.0);
      matrixStack.scale(0.8F, 0.8F, 0.8F);
      this.font.draw(matrixStack, armorAmount, 0.0F, 5.0F, 4210752);
      matrixStack.popPose();
   }

   private void renderAttributeStats(PoseStack matrixStack, String description, String valueStr, int offsetY, int vOffset) {
      RenderSystem.setShaderTexture(0, TEXTURE);
      this.blit(matrixStack, 8, offsetY, 216, vOffset, 16, 16);
      matrixStack.pushPose();
      matrixStack.translate(26.0, offsetY + 6, 0.0);
      matrixStack.scale(0.8F, 0.8F, 0.8F);
      this.font.draw(matrixStack, description, 0.0F, 0.0F, 4210752);
      matrixStack.popPose();
      float xShift = this.font.width(valueStr) * 0.8F;
      matrixStack.pushPose();
      matrixStack.translate(73.0, offsetY + 6, 0.0);
      matrixStack.scale(0.8F, 0.8F, 0.8F);
      matrixStack.translate(-xShift, 0.0, 0.0);
      this.font.draw(matrixStack, valueStr, 0.0F, 0.0F, 4210752);
      matrixStack.popPose();
   }

   private void renderLevel(EternalDataSnapshot snapshot, PoseStack matrixStack) {
      RenderSystem.setShaderTexture(0, TEXTURE);
      int levelPart = Mth.floor(snapshot.getLevelPercent() * 62.0F);
      this.blit(matrixStack, 103, 17, 0, 212, 62, 5);
      this.blit(matrixStack, 103, 17, 0, 218, levelPart, 5);
      String lvlStr = snapshot.getLevel() + " / " + snapshot.getMaxLevel();
      float x = 136.0F - this.font.width(lvlStr) / 2.0F;
      int y = 12;
      matrixStack.pushPose();
      matrixStack.translate(x, y, 0.0);
      matrixStack.scale(0.8F, 0.8F, 1.0F);
      FontHelper.drawStringWithBorder(matrixStack, lvlStr, 0.0F, 0.0F, -6601, -12698050);
      matrixStack.popPose();
   }

   private void renderEternal(EternalDataSnapshot snapshot, PoseStack matrixStack, int mouseX, int mouseY) {
      int offsetX = 125;
      int offsetY = 105;
      if (!snapshot.isAlive()) {
         ModShaders.getGrayscalePositionTexShader().withGrayscale(0.0F).withBrightness(1.0F).enable();
      }

      int lookX = this.leftPos - mouseX + offsetX;
      int lookY = this.topPos - mouseY + offsetY;
      if (!snapshot.isAlive()) {
         lookX = 0;
         lookY = -30;
      }

      matrixStack.pushPose();
      matrixStack.translate(offsetX, offsetY, 0.0);
      if (!snapshot.isAncient()) {
         matrixStack.scale(1.2F, 1.2F, 1.2F);
      }

      UIHelper.drawFacingEntity(this.eternalSkinCache, matrixStack, lookX, lookY - 50);
      Lighting.setupFor3DItems();
      matrixStack.popPose();
      ItemStack heldStack = ((CryochamberContainer)this.menu).getCarried();
      if (!heldStack.isEmpty() && EternalInteractionMessage.canBeFed(snapshot, heldStack)) {
         Rectangle feedRct = new Rectangle(99, 25, 51, 90);
         if (feedRct.contains(mouseX - this.leftPos, mouseY - this.topPos)) {
            this.renderTooltip(matrixStack, new TextComponent("Give to " + this.title.getString()), mouseX - this.leftPos, mouseY - this.topPos);
         }
      }

      if (!snapshot.isAlive()) {
         String deadTxt = "Unalived";
         matrixStack.pushPose();
         matrixStack.translate(0.0, 0.0, 600.0);
         int width = this.font.width(deadTxt);
         FontHelper.drawStringWithBorder(matrixStack, deadTxt, 125.0F - width / 2.0F, 100.0F, 16724016, 0);
         matrixStack.popPose();
      }

      if (snapshot.isAncient()) {
         String ancientTxt = "Ancient";
         matrixStack.pushPose();
         matrixStack.translate(0.0, 0.0, 600.0);
         int width = this.font.width(ancientTxt);
         FontHelper.drawStringWithBorder(matrixStack, ancientTxt, 125.0F - width / 2.0F, 28.0F, 15910161, 0);
         matrixStack.popPose();
      }
   }

   public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      this.renderBackground(matrixStack);
      super.render(matrixStack, mouseX, mouseY, partialTicks);
      this.renderTooltip(matrixStack, mouseX, mouseY);
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      super.mouseClicked(mouseX, mouseY, button);
      if (button != 0) {
         return false;
      } else {
         EternalDataSnapshot snapshot = this.getEternal();
         if (snapshot == null) {
            return false;
         } else {
            if (this.leftPos + 5 <= mouseX && mouseX <= this.leftPos + 5 + 11 && this.topPos + 5 <= mouseY && mouseY <= this.topPos + 5 + 11) {
               ServerboundRenameEternalMessage.send(((CryochamberContainer)this.menu).getTilePos());
            }

            if (this.leftPos + 101 <= mouseX && mouseX <= this.leftPos + 101 + 11 && this.topPos + 27 <= mouseY && mouseY <= this.topPos + 27 + 11) {
               ServerboundToggleEternalPlayerSkinMessage.send(((CryochamberContainer)this.menu).getTilePos());
               this.eternalSkinCache = null;
               this.prevSnapshot = null;
            }

            ItemStack heldStack = ((CryochamberContainer)this.menu).getCarried();
            if (!heldStack.isEmpty() && EternalInteractionMessage.canBeFed(snapshot, heldStack)) {
               Rectangle feedRct = new Rectangle(99, 25, 51, 90);
               if (!feedRct.contains(mouseX - this.leftPos, mouseY - this.topPos)) {
                  return false;
               } else {
                  long window = Minecraft.getInstance().getWindow().getWindow();
                  boolean shiftDown = InputConstants.isKeyDown(window, 340) || InputConstants.isKeyDown(window, 344);
                  ModNetwork.CHANNEL.sendToServer(EternalInteractionMessage.feedItem(heldStack, shiftDown));
                  if (!Minecraft.getInstance().player.isCreative()) {
                     heldStack.shrink(1);
                  }

                  return true;
               }
            } else {
               return false;
            }
         }
      }
   }

   public boolean isPauseScreen() {
      return false;
   }

   @Nullable
   private EternalDataSnapshot getEternal() {
      Level world = Minecraft.getInstance().level;
      if (world == null) {
         return null;
      } else {
         CryoChamberTileEntity tile = ((CryochamberContainer)this.menu).getCryoChamber(world);
         return tile == null ? null : ClientEternalData.getSnapshot(tile.getEternalId());
      }
   }
}
