package iskallia.vault.client.gui.screen;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import iskallia.vault.Vault;
import iskallia.vault.block.entity.CryoChamberTileEntity;
import iskallia.vault.client.ClientEternalData;
import iskallia.vault.client.gui.helper.FontHelper;
import iskallia.vault.client.gui.helper.UIHelper;
import iskallia.vault.client.gui.widget.TooltipImageButton;
import iskallia.vault.client.util.ShaderUtil;
import iskallia.vault.config.EternalAuraConfig;
import iskallia.vault.config.entry.FloatRangeEntry;
import iskallia.vault.container.inventory.CryochamberContainer;
import iskallia.vault.entity.EternalEntity;
import iskallia.vault.entity.eternal.EternalDataSnapshot;
import iskallia.vault.entity.eternal.EternalHelper;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.EternalInteractionMessage;
import java.awt.Rectangle;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import org.lwjgl.opengl.ARBShaderObjects;

public class CryochamberScreen extends ContainerScreen<CryochamberContainer> {
   private static final DecimalFormat ATTRIBUTE_FORMAT = new DecimalFormat("0.0", DecimalFormatSymbols.getInstance(Locale.ROOT));
   private static final DecimalFormat ATTRIBUTE_MS_FORMAT = new DecimalFormat("0.00", DecimalFormatSymbols.getInstance(Locale.ROOT));
   private static final DecimalFormat PERCENT_FORMAT = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ROOT));
   private static final ResourceLocation TEXTURE = Vault.id("textures/gui/cryochamber_inventory.png");
   private EternalDataSnapshot prevSnapshot = null;
   private EternalEntity eternalSkinCache = null;

   public CryochamberScreen(CryochamberContainer screenContainer, PlayerInventory inv, ITextComponent title) {
      super(screenContainer, inv, title);
      this.field_146999_f = 176;
      this.field_147000_g = 211;
      this.field_238745_s_ = this.field_147000_g - 94;
   }

   protected void func_231160_c_() {
      super.func_231160_c_();
      this.refreshButtons();
   }

   private void refreshButtons() {
      this.field_230710_m_.clear();
      this.field_230705_e_.clear();
      EternalDataSnapshot snapshot = this.getEternal();
      if (snapshot != null) {
         if (snapshot.getUsedLevels() < snapshot.getLevel()) {
            int offsetX = this.field_147003_i + 78;
            int yOffset = 0;
            int yShift = 16;
            this.func_230480_a_(
               new ImageButton(
                  offsetX,
                  this.field_147009_r + 18,
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
                  StringTextComponent.field_240750_d_
               )
            );
            this.func_230480_a_(
               new ImageButton(
                  offsetX,
                  this.field_147009_r + 36,
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
                  StringTextComponent.field_240750_d_
               )
            );
            this.func_230480_a_(
               new ImageButton(
                  offsetX,
                  this.field_147009_r + 54,
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
                  StringTextComponent.field_240750_d_
               )
            );
         }

         if (snapshot.getAbilityName() == null) {
            List<EternalAuraConfig.AuraConfig> options = ModConfigs.ETERNAL_AURAS.getRandom(snapshot.getSeededRand(), 3);
            int abilityX = this.field_147003_i + 8;
            int abilityY = this.field_147009_r + 90;

            for (EternalAuraConfig.AuraConfig abilityOption : options) {
               this.func_230480_a_(
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

   private void renderAttributeHoverTooltip(FloatRangeEntry range, float multiplier, DecimalFormat format, MatrixStack matrixStack, int mouseX, int mouseY) {
      matrixStack.func_227860_a_();
      matrixStack.func_227861_a_(0.0, 0.0, 300.0);
      String min = format.format(range.getMin() * multiplier);
      String max = format.format(range.getMax() * multiplier);
      StringTextComponent txt = new StringTextComponent("Adds +" + min + " to +" + max);
      this.renderToolTip(matrixStack, Lists.newArrayList(new IReorderingProcessor[]{txt.func_241878_f()}), mouseX, mouseY, this.field_230712_o_);
      matrixStack.func_227865_b_();
   }

   public void func_231023_e_() {
      super.func_231023_e_();
      EternalDataSnapshot snapshot = this.getEternal();
      if (snapshot != null) {
         if (this.prevSnapshot == null || !this.prevSnapshot.areStatisticsEqual(snapshot)) {
            this.prevSnapshot = snapshot;
            this.refreshButtons();
         }
      }
   }

   protected void func_230450_a_(MatrixStack matrixStack, float partialTicks, int x, int y) {
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.field_230706_i_.func_110434_K().func_110577_a(TEXTURE);
      int offsetX = (this.field_230708_k_ - this.field_146999_f) / 2;
      int offsetY = (this.field_230709_l_ - this.field_147000_g) / 2;
      this.func_238474_b_(matrixStack, offsetX, offsetY, 0, 0, this.field_146999_f, this.field_147000_g);
   }

   protected void func_230451_b_(MatrixStack matrixStack, int mouseX, int mouseY) {
      EternalDataSnapshot snapshot = this.getEternal();
      if (snapshot != null) {
         if (this.eternalSkinCache == null) {
            this.eternalSkinCache = EternalHelper.spawnEternal(Minecraft.func_71410_x().field_71441_e, snapshot);
            this.eternalSkinCache.skin.updateSkin(snapshot.getName());
            Arrays.stream(EquipmentSlotType.values()).forEach(slot -> this.eternalSkinCache.func_184201_a(slot, ItemStack.field_190927_a));
         }

         if (snapshot.isAncient()) {
            FontHelper.drawStringWithBorder(matrixStack, this.field_230704_d_, (float)this.field_238742_p_, (float)this.field_238743_q_, 15910161, 4210752);
         } else {
            this.field_230712_o_.func_243248_b(matrixStack, this.field_230704_d_, this.field_238742_p_, this.field_238743_q_, 4210752);
         }

         this.field_230712_o_.func_243248_b(matrixStack, this.field_213127_e.func_145748_c_(), this.field_238744_r_, this.field_238745_s_, 4210752);
         this.renderEternal(snapshot, matrixStack, mouseX, mouseY);
         RenderSystem.enableDepthTest();
         this.renderLevel(snapshot, matrixStack);
         this.renderAttributeDisplay(snapshot, matrixStack);
         this.renderAbility(snapshot, matrixStack, mouseX, mouseY);
      }
   }

   private void renderAbility(EternalDataSnapshot snapshot, MatrixStack matrixStack, int mouseX, int mouseY) {
      if (snapshot.getAbilityName() == null) {
         List<EternalAuraConfig.AuraConfig> options = ModConfigs.ETERNAL_AURAS.getRandom(snapshot.getSeededRand(), 3);
         int abilityX = 12;
         int abilityY = 94;

         for (EternalAuraConfig.AuraConfig abilityOption : options) {
            this.field_230706_i_.func_110434_K().func_110577_a(new ResourceLocation(abilityOption.getIconPath()));
            func_238463_a_(matrixStack, abilityX, abilityY, 16.0F, 16.0F, 16, 16, 16, 16);
            abilityX += 30;
         }

         int var12 = 8;
         int var13 = 90;

         for (EternalAuraConfig.AuraConfig abilityOption : options) {
            Rectangle box = new Rectangle(var12, var13, 24, 24);
            if (box.contains(mouseX - this.field_147003_i, mouseY - this.field_147009_r)) {
               this.func_243308_b(matrixStack, abilityOption.getTooltip(), mouseX - this.field_147003_i, mouseY - this.field_147009_r);
            }

            var12 += 30;
         }
      } else {
         EternalAuraConfig.AuraConfig cfg = ModConfigs.ETERNAL_AURAS.getByName(snapshot.getAbilityName());
         if (cfg == null) {
            return;
         }

         this.field_230706_i_.func_110434_K().func_110577_a(new ResourceLocation(cfg.getIconPath()));
         func_238463_a_(matrixStack, 8, 92, 0.0F, 0.0F, 16, 16, 16, 16);
         matrixStack.func_227860_a_();
         matrixStack.func_227861_a_(26.0, 92.0, 0.0);
         matrixStack.func_227862_a_(0.8F, 0.8F, 0.8F);
         UIHelper.renderWrappedText(matrixStack, new StringTextComponent(cfg.getDescription()), 82, 0, 4210752);
         matrixStack.func_227865_b_();
      }
   }

   private void renderAttributeDisplay(EternalDataSnapshot snapshot, MatrixStack matrixStack) {
      String healthStr = ATTRIBUTE_FORMAT.format(snapshot.getEntityAttributes().get(Attributes.field_233818_a_));
      this.renderAttributeStats(matrixStack, "Health:", healthStr, 18, 32);
      String damageStr = ATTRIBUTE_FORMAT.format(snapshot.getEntityAttributes().get(Attributes.field_233823_f_));
      this.renderAttributeStats(matrixStack, "Damage:", damageStr, 36, 48);
      String speedStr = ATTRIBUTE_MS_FORMAT.format(snapshot.getEntityAttributes().get(Attributes.field_233821_d_) * 10.0F);
      this.renderAttributeStats(matrixStack, "Speed:", speedStr, 54, 64);
      int availableLevels = Math.max(snapshot.getLevel() - snapshot.getUsedLevels(), 0);
      if (availableLevels > 0) {
         String display = String.valueOf(availableLevels);
         int offsetX = this.field_230712_o_.func_78256_a(display) / 2;
         matrixStack.func_227860_a_();
         matrixStack.func_227861_a_(86.0, 13.0, 0.0);
         matrixStack.func_227862_a_(0.8F, 0.8F, 0.8F);
         matrixStack.func_227861_a_(-offsetX, 0.0, 0.0);
         this.field_230712_o_.func_238421_b_(matrixStack, display, 0.0F, 0.0F, 4210752);
         matrixStack.func_227865_b_();
      }

      String parryPercent = PERCENT_FORMAT.format(snapshot.getParry() * 100.0F) + "%";
      String resistPercent = PERCENT_FORMAT.format(snapshot.getResistance() * 100.0F) + "%";
      String armorAmount = PERCENT_FORMAT.format(snapshot.getArmor());
      this.field_230706_i_.func_110434_K().func_110577_a(TEXTURE);
      this.func_238474_b_(matrixStack, 8, 72, 216, 0, 16, 16);
      matrixStack.func_227860_a_();
      matrixStack.func_227861_a_(24.0, 72.0, 0.0);
      matrixStack.func_227862_a_(0.8F, 0.8F, 0.8F);
      this.field_230712_o_.func_238421_b_(matrixStack, parryPercent, 0.0F, 5.0F, 4210752);
      matrixStack.func_227865_b_();
      this.field_230706_i_.func_110434_K().func_110577_a(TEXTURE);
      this.func_238474_b_(matrixStack, 39, 71, 216, 16, 16, 16);
      matrixStack.func_227860_a_();
      matrixStack.func_227861_a_(55.0, 72.0, 0.0);
      matrixStack.func_227862_a_(0.8F, 0.8F, 0.8F);
      this.field_230712_o_.func_238421_b_(matrixStack, resistPercent, 0.0F, 5.0F, 4210752);
      matrixStack.func_227865_b_();
      this.field_230706_i_.func_110434_K().func_110577_a(TEXTURE);
      this.func_238474_b_(matrixStack, 70, 72, 216, 80, 16, 16);
      matrixStack.func_227860_a_();
      matrixStack.func_227861_a_(86.0, 72.0, 0.0);
      matrixStack.func_227862_a_(0.8F, 0.8F, 0.8F);
      this.field_230712_o_.func_238421_b_(matrixStack, armorAmount, 0.0F, 5.0F, 4210752);
      matrixStack.func_227865_b_();
   }

   private void renderAttributeStats(MatrixStack matrixStack, String description, String valueStr, int offsetY, int vOffset) {
      this.field_230706_i_.func_110434_K().func_110577_a(TEXTURE);
      this.func_238474_b_(matrixStack, 8, offsetY, 216, vOffset, 16, 16);
      matrixStack.func_227860_a_();
      matrixStack.func_227861_a_(26.0, offsetY + 6, 0.0);
      matrixStack.func_227862_a_(0.8F, 0.8F, 0.8F);
      this.field_230712_o_.func_238421_b_(matrixStack, description, 0.0F, 0.0F, 4210752);
      matrixStack.func_227865_b_();
      float xShift = this.field_230712_o_.func_78256_a(valueStr) * 0.8F;
      matrixStack.func_227860_a_();
      matrixStack.func_227861_a_(73.0, offsetY + 6, 0.0);
      matrixStack.func_227862_a_(0.8F, 0.8F, 0.8F);
      matrixStack.func_227861_a_(-xShift, 0.0, 0.0);
      this.field_230712_o_.func_238421_b_(matrixStack, valueStr, 0.0F, 0.0F, 4210752);
      matrixStack.func_227865_b_();
   }

   private void renderLevel(EternalDataSnapshot snapshot, MatrixStack matrixStack) {
      this.field_230706_i_.func_110434_K().func_110577_a(TEXTURE);
      int levelPart = MathHelper.func_76141_d(snapshot.getLevelPercent() * 62.0F);
      this.func_238474_b_(matrixStack, 103, 17, 0, 212, 62, 5);
      this.func_238474_b_(matrixStack, 103, 17, 0, 218, levelPart, 5);
      String lvlStr = snapshot.getLevel() + " / " + snapshot.getMaxLevel();
      float x = 136.0F - this.field_230712_o_.func_78256_a(lvlStr) / 2.0F;
      int y = 12;
      matrixStack.func_227860_a_();
      matrixStack.func_227861_a_(x, y, 0.0);
      matrixStack.func_227862_a_(0.8F, 0.8F, 1.0F);
      FontHelper.drawStringWithBorder(matrixStack, lvlStr, 0.0F, 0.0F, -6601, -12698050);
      matrixStack.func_227865_b_();
   }

   private void renderEternal(EternalDataSnapshot snapshot, MatrixStack matrixStack, int mouseX, int mouseY) {
      int offsetX = 125;
      int offsetY = 105;
      if (!snapshot.isAlive()) {
         ShaderUtil.useShader(ShaderUtil.GRAYSCALE_SHADER, () -> {
            int grayScaleFactor = ShaderUtil.getUniformLocation(ShaderUtil.GRAYSCALE_SHADER, "grayFactor");
            ARBShaderObjects.glUniform1fARB(grayScaleFactor, 0.0F);
            int brightnessFactor = ShaderUtil.getUniformLocation(ShaderUtil.GRAYSCALE_SHADER, "brightness");
            ARBShaderObjects.glUniform1fARB(brightnessFactor, 1.0F);
         });
      }

      int lookX = mouseX - this.field_147003_i - offsetX;
      int lookY = mouseY - this.field_147009_r - offsetY;
      if (!snapshot.isAlive()) {
         lookX = 0;
         lookY = -30;
      }

      matrixStack.func_227860_a_();
      matrixStack.func_227861_a_(offsetX, offsetY, -400.0);
      if (!snapshot.isAncient()) {
         matrixStack.func_227862_a_(1.2F, 1.2F, 1.2F);
      }

      UIHelper.drawFacingEntity(this.eternalSkinCache, matrixStack, lookX, lookY);
      matrixStack.func_227865_b_();
      if (!snapshot.isAlive()) {
         ShaderUtil.releaseShader();
      }

      ItemStack heldStack = this.field_213127_e.func_70445_o();
      if (!heldStack.func_190926_b() && EternalInteractionMessage.canBeFed(snapshot, heldStack)) {
         Rectangle feedRct = new Rectangle(99, 25, 51, 90);
         if (feedRct.contains(mouseX - this.field_147003_i, mouseY - this.field_147009_r)) {
            this.func_238652_a_(
               matrixStack, new StringTextComponent("Give to " + this.field_230704_d_.getString()), mouseX - this.field_147003_i, mouseY - this.field_147009_r
            );
         }
      }

      if (!snapshot.isAlive()) {
         String deadTxt = "Unalived";
         matrixStack.func_227860_a_();
         matrixStack.func_227861_a_(0.0, 0.0, 600.0);
         int width = this.field_230712_o_.func_78256_a(deadTxt);
         FontHelper.drawStringWithBorder(matrixStack, deadTxt, 125.0F - width / 2.0F, 100.0F, 16724016, 0);
         matrixStack.func_227865_b_();
      }

      if (snapshot.isAncient()) {
         String ancientTxt = "Ancient";
         matrixStack.func_227860_a_();
         matrixStack.func_227861_a_(0.0, 0.0, 600.0);
         int width = this.field_230712_o_.func_78256_a(ancientTxt);
         FontHelper.drawStringWithBorder(matrixStack, ancientTxt, 125.0F - width / 2.0F, 28.0F, 15910161, 0);
         matrixStack.func_227865_b_();
      }
   }

   public void func_230430_a_(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      this.func_230446_a_(matrixStack);
      super.func_230430_a_(matrixStack, mouseX, mouseY, partialTicks);
      this.func_230459_a_(matrixStack, mouseX, mouseY);
   }

   public boolean func_231044_a_(double mouseX, double mouseY, int button) {
      super.func_231044_a_(mouseX, mouseY, button);
      if (button != 0) {
         return false;
      } else {
         EternalDataSnapshot snapshot = this.getEternal();
         if (snapshot == null) {
            return false;
         } else {
            ItemStack heldStack = this.field_213127_e.func_70445_o();
            if (!heldStack.func_190926_b() && EternalInteractionMessage.canBeFed(snapshot, heldStack)) {
               Rectangle feedRct = new Rectangle(99, 25, 51, 90);
               if (!feedRct.contains(mouseX - this.field_147003_i, mouseY - this.field_147009_r)) {
                  return false;
               } else {
                  ModNetwork.CHANNEL.sendToServer(EternalInteractionMessage.feedItem(heldStack));
                  if (!Minecraft.func_71410_x().field_71439_g.func_184812_l_()) {
                     heldStack.func_190918_g(1);
                  }

                  return true;
               }
            } else {
               return false;
            }
         }
      }
   }

   public boolean func_231177_au__() {
      return false;
   }

   @Nullable
   private EternalDataSnapshot getEternal() {
      World world = Minecraft.func_71410_x().field_71441_e;
      if (world == null) {
         return null;
      } else {
         CryoChamberTileEntity tile = ((CryochamberContainer)this.field_147002_h).getCryoChamber(world);
         return tile == null ? null : ClientEternalData.getSnapshot(tile.getEternalId());
      }
   }
}
