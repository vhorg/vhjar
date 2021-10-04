package iskallia.vault.client.gui.component;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import iskallia.vault.client.ClientStatisticsData;
import iskallia.vault.client.gui.helper.UIHelper;
import iskallia.vault.client.gui.screen.SkillTreeScreen;
import iskallia.vault.client.gui.tab.PlayerStatisticsTab;
import iskallia.vault.client.gui.tab.SkillTab;
import iskallia.vault.container.slot.ReadOnlySlot;
import iskallia.vault.container.slot.player.ArmorViewSlot;
import iskallia.vault.container.slot.player.OffHandSlot;
import iskallia.vault.util.calc.PlayerStatisticsCollector;
import iskallia.vault.world.data.PlayerFavourData;
import java.awt.Point;
import java.awt.Rectangle;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.util.ITooltipFlag.TooltipFlags;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.EquipmentSlotType.Group;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;

public class PlayerStatisticsDialog extends ComponentDialog {
   private final List<Slot> slots = new ArrayList<>();

   public PlayerStatisticsDialog(SkillTreeScreen skillTreeScreen) {
      super(skillTreeScreen);
      this.descriptionComponent = new ScrollableContainer(this::renderPlayerAttributes);
   }

   private void createGearSlots() {
      PlayerEntity player = Minecraft.func_71410_x().field_71439_g;
      if (player != null) {
         int startX = this.bounds.width - 24;
         int startY = 6;
         this.slots.add(new ReadOnlySlot(player.field_71071_by, player.field_71071_by.field_70461_c, startX, startY));
         this.slots.add(new OffHandSlot(player, startX, startY + 18));

         for (EquipmentSlotType slotType : EquipmentSlotType.values()) {
            if (slotType.func_188453_a() == Group.ARMOR) {
               this.slots.add(new ArmorViewSlot(player, slotType, startX, startY + 36 + slotType.func_188454_b() * 18));
            }
         }
      }
   }

   @Override
   public void refreshWidgets() {
      this.slots.clear();
   }

   @Override
   public int getHeaderHeight() {
      return 0;
   }

   @Override
   public void setBounds(Rectangle bounds) {
      super.setBounds(bounds);
      this.slots.clear();
      this.createGearSlots();
   }

   @Override
   public SkillTab createTab() {
      return new PlayerStatisticsTab(this.getSkillTreeScreen());
   }

   @Override
   public Point getIconUV() {
      return new Point(48, 60);
   }

   public Rectangle getFavourBoxBounds() {
      int playerBoxWidth = 80;
      return new Rectangle(5, 5, this.bounds.width - playerBoxWidth - 30, 108);
   }

   public Rectangle getPlayerBoxBounds() {
      int playerBoxWidth = 80;
      Rectangle ctBounds = this.getFavourBoxBounds();
      return new Rectangle(ctBounds.x + ctBounds.width, ctBounds.y, playerBoxWidth, 108);
   }

   public Rectangle getStatBoxBounds() {
      Rectangle ctBounds = this.getFavourBoxBounds();
      return new Rectangle(5, ctBounds.y + ctBounds.height + 5, this.bounds.width - 12, this.bounds.height - ctBounds.height - 16);
   }

   @Override
   public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      this.renderBackground(matrixStack, mouseX, mouseY, partialTicks);
      matrixStack.func_227860_a_();
      matrixStack.func_227861_a_(this.bounds.x, this.bounds.y, 0.0);
      this.renderContainers(matrixStack);
      this.renderPlayer(matrixStack, mouseX, mouseY, partialTicks);
      this.descriptionComponent.setBounds(this.getStatBoxBounds());
      this.descriptionComponent.render(matrixStack, mouseX, mouseY, partialTicks);
      this.renderPlayerFavour(matrixStack);
      this.renderPlayerItems(matrixStack, mouseX, mouseY, partialTicks);
      matrixStack.func_227865_b_();
   }

   private void renderContainers(MatrixStack matrixStack) {
      Minecraft.func_71410_x().func_110434_K().func_110577_a(SkillTreeScreen.UI_RESOURCE);
      UIHelper.renderContainerBorder(this, matrixStack, this.getFavourBoxBounds(), 14, 44, 2, 2, 2, 2, -7631989);
      UIHelper.renderContainerBorder(this, matrixStack, this.getPlayerBoxBounds(), 14, 44, 2, 2, 2, 2, -16777216);
      UIHelper.renderContainerBorder(this, matrixStack, this.getStatBoxBounds(), 14, 44, 2, 2, 2, 2, -7631989);
   }

   private void renderPlayerFavour(MatrixStack matrixStack) {
      Rectangle favBounds = this.getFavourBoxBounds();
      FontRenderer fr = Minecraft.func_71410_x().field_71466_p;
      int titleLengthRequired = 0;

      for (PlayerFavourData.VaultGodType vgType : PlayerFavourData.VaultGodType.values()) {
         int titleLength = fr.func_238414_a_(new StringTextComponent(vgType.getTitle()));
         if (titleLength > titleLengthRequired) {
            titleLengthRequired = titleLength;
         }
      }

      boolean drawTitles = titleLengthRequired + 10 + 10 < favBounds.width;
      matrixStack.func_227860_a_();
      matrixStack.func_227861_a_(favBounds.x, favBounds.y, 0.0);
      fr.func_243248_b(matrixStack, new StringTextComponent("Favour:"), 5.0F, 5.0F, -15130590);
      matrixStack.func_227860_a_();
      matrixStack.func_227861_a_(5.0, 20.0, 0.0);
      int maxLength = 0;

      for (PlayerFavourData.VaultGodType vgTypex : PlayerFavourData.VaultGodType.values()) {
         IFormattableTextComponent name = new StringTextComponent(vgTypex.getName()).func_240699_a_(vgTypex.getChatColor());
         fr.func_243246_a(matrixStack, name, 0.0F, 0.0F, -1);
         int length = fr.func_238414_a_(name);
         if (length > maxLength) {
            maxLength = length;
         }

         matrixStack.func_227861_a_(0.0, 10.0, 0.0);
         if (drawTitles) {
            IFormattableTextComponent title = new StringTextComponent(vgTypex.getTitle()).func_240699_a_(vgTypex.getChatColor());
            fr.func_243246_a(matrixStack, title, 5.0F, 0.0F, -1);
            matrixStack.func_227861_a_(0.0, 10.0, 0.0);
         }

         matrixStack.func_227861_a_(0.0, 2.0, 0.0);
      }

      matrixStack.func_227865_b_();
      maxLength += 5;
      matrixStack.func_227860_a_();
      matrixStack.func_227861_a_(5.0, 20.0, 0.0);
      matrixStack.func_227861_a_(maxLength, 0.0, 0.0);

      for (PlayerFavourData.VaultGodType vgTypex : PlayerFavourData.VaultGodType.values()) {
         int favour = ClientStatisticsData.getFavour(vgTypex);
         fr.func_243246_a(matrixStack, new StringTextComponent(String.valueOf(favour)), 0.0F, 0.0F, -1052689);
         matrixStack.func_227861_a_(0.0, drawTitles ? 22.0 : 12.0, 0.0);
      }

      matrixStack.func_227865_b_();
      matrixStack.func_227865_b_();
      RenderSystem.enableDepthTest();
   }

   private void renderPlayerAttributes(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      Rectangle plBounds = this.getStatBoxBounds();
      FontRenderer fr = Minecraft.func_71410_x().field_71466_p;
      int maxLength = 0;
      List<PlayerStatisticsCollector.AttributeSnapshot> snapshots = ClientStatisticsData.getPlayerAttributeSnapshots();
      Point offset = plBounds.getLocation();

      for (int i = 0; i < snapshots.size(); i++) {
         PlayerStatisticsCollector.AttributeSnapshot snapshot = snapshots.get(i);
         ITextComponent cmp = new TranslationTextComponent(snapshot.getAttributeName());
         fr.func_238422_b_(matrixStack, cmp.func_241878_f(), 10.0F, 10 * i + 10, -15130590);
         int length = fr.func_238414_a_(cmp);
         if (length > maxLength) {
            maxLength = length;
         }
      }

      this.descriptionComponent.setInnerHeight(snapshots.size() * 10 + 20);
      maxLength += 5;
      int intLength = 0;

      for (PlayerStatisticsCollector.AttributeSnapshot snapshot : snapshots) {
         int intStrLength = fr.func_78256_a(String.valueOf((int)snapshot.getValue()));
         if (intStrLength > intLength) {
            intLength = intStrLength;
         }
      }

      DecimalFormat format = new DecimalFormat("0.##");

      for (int ix = 0; ix < snapshots.size(); ix++) {
         matrixStack.func_227860_a_();
         matrixStack.func_227861_a_(maxLength + intLength + 4, ix * 10 + 10, 0.0);
         PlayerStatisticsCollector.AttributeSnapshot snapshotx = snapshots.get(ix);
         int intStrLength = fr.func_78256_a(String.valueOf((int)snapshotx.getValue()));
         String numberStr = format.format(snapshotx.getValue());
         if (snapshotx.isPercentage()) {
            numberStr = numberStr + "%";
         }

         IFormattableTextComponent txt;
         if (snapshotx.hasHitLimit()) {
            String limitStr = format.format(snapshotx.getLimit());
            if (snapshotx.isPercentage()) {
               limitStr = limitStr + "%";
            }

            txt = new StringTextComponent(limitStr).func_240700_a_(style -> style.func_240718_a_(Color.func_240743_a_(-8519680)));
         } else {
            txt = new StringTextComponent(numberStr).func_240700_a_(style -> style.func_240718_a_(Color.func_240743_a_(-15130590)));
         }

         int displayLength = fr.func_243245_a(txt.func_241878_f());
         matrixStack.func_227860_a_();
         matrixStack.func_227861_a_(-intStrLength, 0.0, 0.0);
         fr.func_238422_b_(matrixStack, txt.func_241878_f(), 0.0F, 0.0F, -1);
         matrixStack.func_227865_b_();
         matrixStack.func_227865_b_();
         Rectangle bounds = new Rectangle(
            this.bounds.x + offset.x + 10, this.bounds.y + offset.y + 10 * ix + 10 - this.descriptionComponent.getyOffset(), maxLength + displayLength, 8
         );
         if (bounds.contains(mouseX, mouseY)) {
            if (snapshotx.hasHitLimit()) {
               List<ITextComponent> list = new ArrayList<>();
               list.add(new StringTextComponent("Uncapped: ").func_240702_b_(numberStr));
               int offsetX = mouseX - (this.bounds.x + offset.x);
               int offsetY = mouseY - (this.bounds.y + offset.y) + this.descriptionComponent.getyOffset();
               GuiUtils.drawHoveringText(matrixStack, list, offsetX, offsetY, offset.x + plBounds.width - 14, offset.y + plBounds.height, -1, fr);
            } else if (snapshotx.hasLimit()) {
               String limitStr = format.format(snapshotx.getLimit());
               if (snapshotx.isPercentage()) {
                  limitStr = limitStr + "%";
               }

               List<ITextComponent> list = new ArrayList<>();
               list.add(new StringTextComponent("Limit: ").func_240702_b_(limitStr));
               int offsetX = mouseX - (this.bounds.x + offset.x);
               int offsetY = mouseY - (this.bounds.y + offset.y) + this.descriptionComponent.getyOffset();
               GuiUtils.drawHoveringText(matrixStack, list, offsetX, offsetY, offset.x + plBounds.width - 14, offset.y + plBounds.height, -1, fr);
            }
         }
      }

      RenderSystem.enableDepthTest();
   }

   private void renderPlayer(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      Rectangle plBounds = this.getPlayerBoxBounds();
      int offsetX = plBounds.x + plBounds.width / 2;
      int offsetY = plBounds.y + 108 - 10;
      matrixStack.func_227860_a_();
      matrixStack.func_227861_a_(offsetX, offsetY, 0.0);
      matrixStack.func_227862_a_(1.6F, 1.6F, 1.6F);
      UIHelper.drawFacingPlayer(matrixStack, mouseX - this.bounds.x - offsetX, mouseY - this.bounds.y - offsetY);
      matrixStack.func_227865_b_();
   }

   private void renderPlayerItems(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      Slot hoveredSlot = null;
      int slotHover = -2130706433;

      for (Slot slot : this.slots) {
         this.drawSlot(matrixStack, slot);
         Rectangle box = this.getSlotBox(slot);
         if (box.contains(mouseX - this.bounds.x, mouseY - this.bounds.y)) {
            int slotX = slot.field_75223_e;
            int slotY = slot.field_75221_f;
            matrixStack.func_227860_a_();
            matrixStack.func_227861_a_(slotX, slotY, 0.0);
            RenderSystem.disableDepthTest();
            RenderSystem.colorMask(true, true, true, false);
            this.func_238468_a_(matrixStack, 0, 0, 16, 16, slotHover, slotHover);
            RenderSystem.colorMask(true, true, true, true);
            RenderSystem.enableDepthTest();
            matrixStack.func_227865_b_();
            if (slot.func_75216_d()) {
               hoveredSlot = slot;
            }
         }
      }

      if (hoveredSlot != null) {
         ItemStack toHover = hoveredSlot.func_75211_c();
         FontRenderer fr = toHover.func_77973_b().getFontRenderer(toHover);
         List<ITextComponent> tooltip = toHover.func_82840_a(
            Minecraft.func_71410_x().field_71439_g, Minecraft.func_71410_x().field_71474_y.field_82882_x ? TooltipFlags.ADVANCED : TooltipFlags.NORMAL
         );
         Screen mainScreen = this.getSkillTreeScreen();
         matrixStack.func_227860_a_();
         matrixStack.func_227861_a_(0.0, 0.0, 550.0);
         GuiUtils.preItemToolTip(toHover);
         GuiUtils.drawHoveringText(
            matrixStack,
            tooltip,
            mouseX - this.bounds.x,
            mouseY - this.bounds.y,
            mainScreen.field_230708_k_ - this.bounds.x,
            mainScreen.field_230709_l_ - this.bounds.y,
            -1,
            fr == null ? Minecraft.func_71410_x().field_71466_p : fr
         );
         GuiUtils.postItemToolTip();
         matrixStack.func_227865_b_();
         RenderSystem.enableDepthTest();
      }
   }

   private void drawSlot(MatrixStack matrixStack, Slot slot) {
      ItemRenderer itemRenderer = Minecraft.func_71410_x().func_175599_af();
      ItemStack slotStack = slot.func_75211_c();
      int slotX = slot.field_75223_e;
      int slotY = slot.field_75221_f;
      matrixStack.func_227860_a_();
      matrixStack.func_227861_a_(slotX, slotY, 0.0);
      Minecraft.func_71410_x().func_110434_K().func_110577_a(SkillTreeScreen.UI_RESOURCE);
      this.func_238474_b_(matrixStack, -1, -1, 173, 0, 18, 18);
      this.func_230926_e_(100);
      itemRenderer.field_77023_b = 100.0F;
      if (slotStack.func_190926_b()) {
         Pair<ResourceLocation, ResourceLocation> pair = slot.func_225517_c_();
         if (pair != null) {
            TextureAtlasSprite textureatlassprite = (TextureAtlasSprite)Minecraft.func_71410_x()
               .func_228015_a_((ResourceLocation)pair.getFirst())
               .apply(pair.getSecond());
            Minecraft.func_71410_x().func_110434_K().func_110577_a(textureatlassprite.func_229241_m_().func_229223_g_());
            func_238470_a_(matrixStack, 0, 0, this.func_230927_p_(), 16, 16, textureatlassprite);
         }
      } else {
         RenderSystem.pushMatrix();
         RenderSystem.multMatrix(matrixStack.func_227866_c_().func_227870_a_());
         RenderSystem.enableDepthTest();
         itemRenderer.func_184391_a(Minecraft.func_71410_x().field_71439_g, slotStack, 0, 0);
         itemRenderer.func_180453_a(Minecraft.func_71410_x().field_71466_p, slotStack, 0, 0, null);
         RenderSystem.popMatrix();
      }

      itemRenderer.field_77023_b = 0.0F;
      this.func_230926_e_(0);
      matrixStack.func_227865_b_();
   }

   private Rectangle getSlotBox(Slot slot) {
      return new Rectangle(slot.field_75223_e - 1, slot.field_75221_f - 1, 18, 18);
   }
}
