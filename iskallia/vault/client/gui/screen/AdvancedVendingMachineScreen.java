package iskallia.vault.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import iskallia.vault.block.entity.AdvancedVendingTileEntity;
import iskallia.vault.block.render.VendingMachineRenderer;
import iskallia.vault.client.gui.component.ScrollableContainer;
import iskallia.vault.client.gui.widget.AdvancedTradeWidget;
import iskallia.vault.container.AdvancedVendingContainer;
import iskallia.vault.entity.model.StatuePlayerModel;
import iskallia.vault.event.InputEvents;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.AdvancedVendingUIMessage;
import iskallia.vault.util.SkinProfile;
import iskallia.vault.vending.Trade;
import iskallia.vault.vending.TraderCore;
import java.awt.Rectangle;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.IRenderTypeBuffer.Impl;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;

public class AdvancedVendingMachineScreen extends ContainerScreen<AdvancedVendingContainer> {
   public static final ResourceLocation HUD_RESOURCE = new ResourceLocation("the_vault", "textures/gui/vending-machine.png");
   public ScrollableContainer tradesContainer;
   public List<AdvancedTradeWidget> tradeWidgets;
   public SkinProfile skin = new SkinProfile();

   public AdvancedVendingMachineScreen(AdvancedVendingContainer screenContainer, PlayerInventory inv, ITextComponent title) {
      super(screenContainer, inv, new StringTextComponent("Advanced Vending Machine"));
      this.tradesContainer = new ScrollableContainer(this::renderTrades);
      this.tradeWidgets = new LinkedList<>();
      this.refreshWidgets();
      this.field_146999_f = 394;
      this.field_147000_g = 170;
   }

   public void refreshWidgets() {
      this.tradeWidgets.clear();
      List<TraderCore> cores = ((AdvancedVendingContainer)this.func_212873_a_()).getTileEntity().getCores();

      for (int i = 0; i < cores.size(); i++) {
         TraderCore traderCore = cores.get(i);
         int x = 0;
         int y = i * 27;
         this.tradeWidgets.add(new AdvancedTradeWidget(x, y, traderCore, this));
      }
   }

   public Rectangle getTradeBoundaries() {
      int midX = MathHelper.func_76141_d(this.field_230708_k_ / 2.0F);
      int midY = MathHelper.func_76141_d(this.field_230709_l_ / 2.0F);
      return new Rectangle(midX - 134, midY - 66, 100, 142);
   }

   protected void func_231160_c_() {
      super.func_231160_c_();
   }

   public void func_212927_b(double mouseX, double mouseY) {
      Rectangle tradeBoundaries = this.getTradeBoundaries();
      double tradeContainerX = mouseX - tradeBoundaries.x;
      double tradeContainerY = mouseY - tradeBoundaries.y;

      for (AdvancedTradeWidget tradeWidget : this.tradeWidgets) {
         tradeWidget.func_212927_b(tradeContainerX, tradeContainerY);
      }

      this.tradesContainer.mouseMoved(mouseX, mouseY);
   }

   public boolean func_231044_a_(double mouseX, double mouseY, int button) {
      Rectangle tradeBoundaries = this.getTradeBoundaries();
      double tradeContainerX = mouseX - tradeBoundaries.x;
      double tradeContainerY = mouseY - tradeBoundaries.y + this.tradesContainer.getyOffset();

      for (int i = 0; i < this.tradeWidgets.size(); i++) {
         AdvancedTradeWidget tradeWidget = this.tradeWidgets.get(i);
         boolean isHovered = tradeWidget.field_230690_l_ <= tradeContainerX
            && tradeContainerX <= tradeWidget.field_230690_l_ + 88
            && tradeWidget.field_230691_m_ <= tradeContainerY
            && tradeContainerY <= tradeWidget.field_230691_m_ + 27;
         if (isHovered) {
            if (InputEvents.isShiftDown()) {
               ((AdvancedVendingContainer)this.func_212873_a_()).ejectCore(i);
               this.refreshWidgets();
               ModNetwork.CHANNEL.sendToServer(AdvancedVendingUIMessage.ejectTrade(i));
               Minecraft.func_71410_x().func_147118_V().func_147682_a(SimpleSound.func_184371_a(SoundEvents.field_187638_cR, 1.0F));
            } else {
               ((AdvancedVendingContainer)this.func_212873_a_()).selectTrade(i);
               ModNetwork.CHANNEL.sendToServer(AdvancedVendingUIMessage.selectTrade(i));
               Minecraft.func_71410_x().func_147118_V().func_147682_a(SimpleSound.func_184371_a(SoundEvents.field_187909_gi, 1.0F));
            }
            break;
         }
      }

      this.tradesContainer.mouseClicked(mouseX, mouseY, button);
      return super.func_231044_a_(mouseX, mouseY, button);
   }

   public boolean func_231048_c_(double mouseX, double mouseY, int button) {
      this.tradesContainer.mouseReleased(mouseX, mouseY, button);
      return super.func_231048_c_(mouseX, mouseY, button);
   }

   public boolean func_231043_a_(double mouseX, double mouseY, double delta) {
      this.tradesContainer.mouseScrolled(mouseX, mouseY, delta);
      return true;
   }

   protected void func_230450_a_(MatrixStack matrixStack, float partialTicks, int x, int y) {
   }

   protected void func_230451_b_(MatrixStack matrixStack, int x, int y) {
      this.field_230712_o_.func_243248_b(matrixStack, new StringTextComponent(""), this.field_238742_p_, this.field_238743_q_, 4210752);
   }

   public void func_230430_a_(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      this.func_230446_a_(matrixStack);
      float midX = this.field_230708_k_ / 2.0F;
      float midY = this.field_230709_l_ / 2.0F;
      Minecraft minecraft = this.getMinecraft();
      int containerWidth = 276;
      int containerHeight = 166;
      minecraft.func_110434_K().func_110577_a(HUD_RESOURCE);
      func_238463_a_(matrixStack, (int)(midX - containerWidth / 2), (int)(midY - containerHeight / 2), 0.0F, 0.0F, containerWidth, containerHeight, 512, 256);
      AdvancedVendingContainer container = (AdvancedVendingContainer)this.func_212873_a_();
      AdvancedVendingTileEntity tileEntity = container.getTileEntity();
      Rectangle tradeBoundaries = this.getTradeBoundaries();
      this.tradesContainer.setBounds(tradeBoundaries);
      this.tradesContainer.setInnerHeight(27 * this.tradeWidgets.size());
      this.tradesContainer.render(matrixStack, mouseX, mouseY, partialTicks);
      super.func_230430_a_(matrixStack, mouseX, mouseY, partialTicks);
      TraderCore coreToRender = container.getSelectedTrade();
      if (coreToRender != null) {
         this.skin.updateSkin(coreToRender.getName());
      }

      if (coreToRender != null) {
         drawSkin((int)midX + 175, (int)midY - 10, -45, this.skin);
      }

      minecraft.field_71466_p.func_238421_b_(matrixStack, "Trades", midX - 108.0F, midY - 77.0F, -12632257);
      if (coreToRender != null) {
         String name = "Vendor - " + coreToRender.getName();
         int nameWidth = minecraft.field_71466_p.func_78256_a(name);
         minecraft.field_71466_p.func_238421_b_(matrixStack, name, midX + 50.0F - nameWidth / 2.0F, midY - 70.0F, -12632257);
      }

      this.func_230459_a_(matrixStack, mouseX, mouseY);
   }

   public void renderTrades(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      Rectangle tradeBoundaries = this.getTradeBoundaries();
      int tradeContainerX = mouseX - tradeBoundaries.x;
      int tradeContainerY = mouseY - tradeBoundaries.y + this.tradesContainer.getyOffset();

      for (AdvancedTradeWidget tradeWidget : this.tradeWidgets) {
         tradeWidget.func_230430_a_(matrixStack, tradeContainerX, tradeContainerY, partialTicks);
      }
   }

   protected void func_230459_a_(MatrixStack matrixStack, int mouseX, int mouseY) {
      Rectangle tradeBoundaries = this.getTradeBoundaries();
      int tradeContainerX = mouseX - tradeBoundaries.x;
      int tradeContainerY = mouseY - tradeBoundaries.y + this.tradesContainer.getyOffset();

      for (AdvancedTradeWidget tradeWidget : this.tradeWidgets) {
         if (tradeWidget.isHovered(tradeContainerX, tradeContainerY)) {
            Trade trade = tradeWidget.getTraderCode().getTrade();
            if (trade.getTradesLeft() != 0) {
               ItemStack sellStack = trade.getSell().toStack();
               this.func_230457_a_(matrixStack, sellStack, mouseX, mouseY);
            } else {
               StringTextComponent text = new StringTextComponent("Sold out, sorry!");
               text.func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(16711680)));
               this.func_238652_a_(matrixStack, text, mouseX, mouseY);
            }
         }
      }

      super.func_230459_a_(matrixStack, mouseX, mouseY);
   }

   public static void drawSkin(int posX, int posY, int yRotation, SkinProfile skin) {
      float scale = 8.0F;
      RenderSystem.pushMatrix();
      RenderSystem.translatef(posX, posY, 1050.0F);
      RenderSystem.scalef(1.0F, 1.0F, -1.0F);
      MatrixStack matrixStack = new MatrixStack();
      matrixStack.func_227861_a_(0.0, 0.0, 1000.0);
      matrixStack.func_227862_a_(scale, scale, scale);
      Quaternion quaternion = Vector3f.field_229183_f_.func_229187_a_(200.0F);
      Quaternion quaternion1 = Vector3f.field_229179_b_.func_229187_a_(45.0F);
      quaternion.func_195890_a(quaternion1);
      EntityRendererManager entityrenderermanager = Minecraft.func_71410_x().func_175598_ae();
      quaternion1.func_195892_e();
      entityrenderermanager.func_229089_a_(quaternion1);
      entityrenderermanager.func_178633_a(false);
      Impl irendertypebuffer$impl = Minecraft.func_71410_x().func_228019_au_().func_228487_b_();
      StatuePlayerModel<PlayerEntity> model = VendingMachineRenderer.PLAYER_MODEL;
      RenderSystem.runAsFancy(() -> {
         matrixStack.func_227862_a_(scale, scale, scale);
         matrixStack.func_227863_a_(Vector3f.field_229179_b_.func_229187_a_(20.0F));
         matrixStack.func_227863_a_(Vector3f.field_229180_c_.func_229187_a_(yRotation));
         int lighting = 15728640;
         int overlay = 983040;
         RenderType renderType = model.func_228282_a_(skin.getLocationSkin());
         IVertexBuilder vertexBuilder = irendertypebuffer$impl.getBuffer(renderType);
         model.field_78115_e.func_228309_a_(matrixStack, vertexBuilder, lighting, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
         model.field_178722_k.func_228309_a_(matrixStack, vertexBuilder, lighting, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
         model.field_178721_j.func_228309_a_(matrixStack, vertexBuilder, lighting, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
         model.field_178724_i.func_228309_a_(matrixStack, vertexBuilder, lighting, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
         model.field_178723_h.func_228309_a_(matrixStack, vertexBuilder, lighting, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
         model.field_178730_v.func_228309_a_(matrixStack, vertexBuilder, lighting, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
         model.field_178733_c.func_228309_a_(matrixStack, vertexBuilder, lighting, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
         model.field_178731_d.func_228309_a_(matrixStack, vertexBuilder, lighting, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
         model.field_178734_a.func_228309_a_(matrixStack, vertexBuilder, lighting, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
         matrixStack.func_227860_a_();
         matrixStack.func_227861_a_(0.0, 0.0, -0.62F);
         model.field_178732_b.func_228309_a_(matrixStack, vertexBuilder, lighting, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
         matrixStack.func_227865_b_();
         model.field_178720_f.func_228309_a_(matrixStack, vertexBuilder, lighting, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
         model.field_78116_c.func_228309_a_(matrixStack, vertexBuilder, lighting, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
         matrixStack.func_227865_b_();
      });
      irendertypebuffer$impl.func_228461_a_();
      entityrenderermanager.func_178633_a(true);
      RenderSystem.popMatrix();
   }
}
