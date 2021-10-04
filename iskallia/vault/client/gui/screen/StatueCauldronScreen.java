package iskallia.vault.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import iskallia.vault.block.entity.StatueCauldronTileEntity;
import iskallia.vault.block.render.VendingMachineRenderer;
import iskallia.vault.client.gui.component.ScrollableContainer;
import iskallia.vault.client.gui.widget.StatueWidget;
import iskallia.vault.entity.model.StatuePlayerModel;
import iskallia.vault.util.SkinProfile;
import java.awt.Rectangle;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.IRenderTypeBuffer.Impl;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.StringTextComponent;

public class StatueCauldronScreen extends Screen {
   public static final ResourceLocation HUD_RESOURCE = new ResourceLocation("the_vault", "textures/gui/statue_cauldron.png");
   public ScrollableContainer statuesContainer;
   public List<StatueWidget> statueWidgets;
   private SkinProfile selected;
   private final ClientWorld world;
   private final BlockPos pos;
   private int xSize;
   private int ySize;

   public StatueCauldronScreen(ClientWorld world, BlockPos pos) {
      super(new StringTextComponent("Statue Cauldron"));
      this.world = world;
      this.pos = pos;
      this.selected = new SkinProfile();
      this.statueWidgets = new LinkedList<>();
      this.statuesContainer = new ScrollableContainer(this::renderNames);
      this.refreshWidgets();
      this.xSize = 220;
      this.ySize = 166;
   }

   public boolean func_231177_au__() {
      return false;
   }

   public SkinProfile getSelected() {
      return this.selected;
   }

   private StatueCauldronTileEntity getTileEntity() {
      TileEntity tileEntity = this.world.func_175625_s(this.pos);
      return !(tileEntity instanceof StatueCauldronTileEntity) ? null : (StatueCauldronTileEntity)tileEntity;
   }

   public void func_230430_a_(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      StatueCauldronTileEntity statue = this.getTileEntity();
      if (statue == null) {
         this.func_231175_as__();
      } else {
         this.refreshWidgets();
         float midX = this.field_230708_k_ / 2.0F;
         float midY = this.field_230709_l_ / 2.0F;
         this.func_238651_a_(matrixStack, 0);
         this.field_230706_i_.func_110434_K().func_110577_a(HUD_RESOURCE);
         func_238463_a_(matrixStack, (int)(midX - this.xSize / 2), (int)(midY - this.ySize / 2), 0.0F, 0.0F, this.xSize, this.ySize, 512, 256);
         this.renderTitle(matrixStack);
         if (!this.statueWidgets.isEmpty()) {
            if (this.selected.getLatestNickname() == null || this.selected.getLatestNickname().isEmpty()) {
               this.selected.updateSkin(this.statueWidgets.get(0).getName());
            }

            drawSkin((int)midX + 46, (int)midY - 22, -45, this.selected, false);
         }

         Rectangle boundaries = this.getViewportBoundaries();
         this.statuesContainer.setBounds(boundaries);
         this.statuesContainer.setInnerHeight(27 * this.statueWidgets.size());
         this.statuesContainer.render(matrixStack, mouseX, mouseY, partialTicks);
         this.renderProgressBar(matrixStack, statue, midX, midY);
         super.func_230430_a_(matrixStack, mouseX, mouseY, partialTicks);
      }
   }

   private void renderProgressBar(MatrixStack matrixStack, StatueCauldronTileEntity statue, float midX, float midY) {
      float progress = (float)statue.getStatueCount() / statue.getRequiredAmount();
      int percent = MathHelper.func_76141_d(progress * 100.0F);
      float startX = midX + 97.0F - this.field_230712_o_.func_78256_a(percent + "%") / 2.0F;
      float startY = midY - 78.0F;
      this.field_230712_o_.func_243248_b(matrixStack, new StringTextComponent(percent + "%"), startX, startY, 4210752);
      int barHeight = 140;
      int textureHeight = MathHelper.func_76141_d(barHeight * progress);
      float barX = midX + 89.0F;
      float barY = midY - 65.0F + (barHeight - textureHeight);
      this.field_230706_i_.func_110434_K().func_110577_a(HUD_RESOURCE);
      func_238463_a_(matrixStack, (int)barX, (int)barY, 314.0F, 0.0F, 16, textureHeight, 512, 256);
   }

   public void refreshWidgets() {
      this.statueWidgets.clear();
      List<String> names = this.getTileEntity().getNames();
      Set<String> uniqueNames = new HashSet<>(names);
      HashMap<String, Integer> counts = new HashMap<>();

      for (String uniqueName : uniqueNames) {
         int amount = Collections.frequency(names, uniqueName);
         counts.put(uniqueName, amount);
      }

      counts = sortByValue(counts);
      int index = 0;

      for (String name : counts.keySet()) {
         int count = counts.get(name);
         int x = 0;
         int y = index * 27;
         this.statueWidgets.add(new StatueWidget(x, y, name, count, this));
         index++;
      }
   }

   public static HashMap<String, Integer> sortByValue(HashMap<String, Integer> map) {
      List<Entry<String, Integer>> list = new LinkedList<>(map.entrySet());
      list.sort(Entry.comparingByValue());
      Collections.reverse(list);
      HashMap<String, Integer> temp = new LinkedHashMap<>();

      for (Entry<String, Integer> aa : list) {
         temp.put(aa.getKey(), aa.getValue());
      }

      return temp;
   }

   public void renderNames(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      Rectangle boundaries = this.getViewportBoundaries();
      int containerX = mouseX - boundaries.x;
      int containerY = mouseY - boundaries.y + this.statuesContainer.getyOffset();

      for (StatueWidget statueWidget : this.statueWidgets) {
         statueWidget.func_230430_a_(matrixStack, containerX, containerY, partialTicks);
      }
   }

   public Rectangle getViewportBoundaries() {
      int midX = MathHelper.func_76141_d(this.field_230708_k_ / 2.0F);
      int midY = MathHelper.func_76141_d(this.field_230709_l_ / 2.0F);
      return new Rectangle(midX - 106, midY - 66, 100, 142);
   }

   public static void drawSkin(int posX, int posY, int yRotation, SkinProfile skin, boolean megahead) {
      float scale = 8.0F;
      float headScale = megahead ? 1.75F : 1.0F;
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
         matrixStack.func_227862_a_(headScale, headScale, headScale);
         model.field_178720_f.func_228309_a_(matrixStack, vertexBuilder, lighting, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
         model.field_78116_c.func_228309_a_(matrixStack, vertexBuilder, lighting, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
         matrixStack.func_227865_b_();
      });
      irendertypebuffer$impl.func_228461_a_();
      entityrenderermanager.func_178633_a(true);
      RenderSystem.popMatrix();
   }

   public void func_212927_b(double mouseX, double mouseY) {
      Rectangle boundaries = this.getViewportBoundaries();
      double containerX = mouseX - boundaries.x;
      double containerY = mouseY - boundaries.y;

      for (StatueWidget statueWidget : this.statueWidgets) {
         statueWidget.func_212927_b(containerX, containerY);
      }

      this.statuesContainer.mouseMoved(mouseX, mouseY);
   }

   public boolean func_231044_a_(double mouseX, double mouseY, int button) {
      Rectangle boundaries = this.getViewportBoundaries();
      double tradeContainerX = mouseX - boundaries.x;
      double tradeContainerY = mouseY - boundaries.y + this.statuesContainer.getyOffset();

      for (int i = 0; i < this.statueWidgets.size(); i++) {
         StatueWidget statueWidget = this.statueWidgets.get(i);
         boolean isHovered = statueWidget.field_230690_l_ <= tradeContainerX
            && tradeContainerX <= statueWidget.field_230690_l_ + 88
            && statueWidget.field_230691_m_ <= tradeContainerY
            && tradeContainerY <= statueWidget.field_230691_m_ + 27;
         if (isHovered) {
            this.selected.updateSkin(statueWidget.getName());
            Minecraft.func_71410_x().func_147118_V().func_147682_a(SimpleSound.func_184371_a(SoundEvents.field_187909_gi, 1.0F));
            break;
         }
      }

      this.statuesContainer.mouseClicked(mouseX, mouseY, button);
      return super.func_231044_a_(mouseX, mouseY, button);
   }

   public boolean func_231048_c_(double mouseX, double mouseY, int button) {
      this.statuesContainer.mouseReleased(mouseX, mouseY, button);
      return super.func_231048_c_(mouseX, mouseY, button);
   }

   public boolean func_231043_a_(double mouseX, double mouseY, double delta) {
      this.statuesContainer.mouseScrolled(mouseX, mouseY, delta);
      return true;
   }

   private void renderTitle(MatrixStack matrixStack) {
      int i = MathHelper.func_76141_d(this.field_230708_k_ / 2.0F);
      int j = MathHelper.func_76141_d(this.field_230709_l_ / 2.0F);
      float startX = i - this.field_230712_o_.func_78256_a(this.field_230704_d_.getString()) / 2.0F;
      float startY = j - 78;
      this.field_230712_o_.func_243248_b(matrixStack, this.field_230704_d_, startX, startY, 4210752);
   }
}
