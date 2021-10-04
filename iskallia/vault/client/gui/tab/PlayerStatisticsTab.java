package iskallia.vault.client.gui.tab;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import iskallia.vault.client.ClientStatisticsData;
import iskallia.vault.client.gui.helper.ScreenDrawHelper;
import iskallia.vault.client.gui.helper.UIHelper;
import iskallia.vault.client.gui.screen.SkillTreeScreen;
import iskallia.vault.world.data.PlayerVaultStatsData;
import java.awt.Rectangle;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.ReadBookScreen;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.StringUtils;
import net.minecraft.util.Tuple;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class PlayerStatisticsTab extends SkillTab {
   public PlayerStatisticsTab(SkillTreeScreen parentScreen) {
      super(parentScreen, new StringTextComponent("Statistics Tab"));
      this.setScrollable(false);
   }

   @Override
   public String getTabName() {
      return "Statistics";
   }

   @Override
   public void refresh() {
   }

   @Override
   public List<Runnable> renderTab(Rectangle containerBounds, MatrixStack renderStack, int mouseX, int mouseY, float pTicks) {
      this.renderTabBackground(renderStack, containerBounds);
      this.renderBookBackground(containerBounds, renderStack);
      int pxOffsetX = 38;
      int pxOffsetY = 16;
      float pxWidth = containerBounds.width / 192.0F;
      float pxHeight = containerBounds.height / 192.0F;
      int offsetX = containerBounds.x + Math.round(pxWidth * pxOffsetX);
      int offsetY = containerBounds.y + Math.round(pxHeight * pxOffsetY);
      Rectangle bookCt = new Rectangle(offsetX, offsetY, Math.round(108.0F * pxWidth), Math.round(104.0F * pxWidth));
      renderStack.func_227860_a_();
      renderStack.func_227861_a_(offsetX + 5, offsetY + 5, 0.0);
      this.renderPlayerAttributes(bookCt, renderStack, mouseX, mouseY, pTicks);
      renderStack.func_227865_b_();
      return Collections.emptyList();
   }

   private void renderPlayerAttributes(Rectangle containerBounds, MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      FontRenderer fr = Minecraft.func_71410_x().field_71466_p;
      CompoundNBT vaultStats = ClientStatisticsData.getSerializedVaultStats();
      List<Tuple<ITextComponent, Tuple<ITextComponent, Integer>>> statDisplay = new ArrayList<>();
      int numberOffset = this.buildVaultStatisticsDisplay(vaultStats, statDisplay);
      int maxLength = 0;
      StringTextComponent text = new StringTextComponent("");

      for (Tuple<ITextComponent, Tuple<ITextComponent, Integer>> statTpl : statDisplay) {
         text.func_230529_a_((ITextComponent)statTpl.func_76341_a()).func_240702_b_("\n");
         int length = fr.func_238414_a_((ITextProperties)statTpl.func_76341_a());
         if (length > maxLength) {
            maxLength = length;
         }
      }

      maxLength += 5;
      maxLength += numberOffset;
      matrixStack.func_227860_a_();
      int yOffset = this.renderFastestVaultDisplay(matrixStack, vaultStats, maxLength);
      matrixStack.func_227861_a_(0.0, yOffset, 0.0);
      UIHelper.renderWrappedText(matrixStack, text, containerBounds.width, 0);
      matrixStack.func_227861_a_(maxLength, 0.0, 0.0);

      for (Tuple<ITextComponent, Tuple<ITextComponent, Integer>> statTplx : statDisplay) {
         Tuple<ITextComponent, Integer> valueDisplayTpl = (Tuple<ITextComponent, Integer>)statTplx.func_76340_b();
         matrixStack.func_227860_a_();
         matrixStack.func_227861_a_(-(Integer)valueDisplayTpl.func_76340_b(), 0.0, 0.0);
         fr.func_243248_b(matrixStack, (ITextComponent)valueDisplayTpl.func_76341_a(), 0.0F, 0.0F, -15130590);
         matrixStack.func_227865_b_();
         matrixStack.func_227861_a_(0.0, 10.0, 0.0);
      }

      matrixStack.func_227865_b_();
      RenderSystem.enableDepthTest();
   }

   private int renderFastestVaultDisplay(MatrixStack matrixStack, CompoundNBT vaultStats, int rightShift) {
      PlayerVaultStatsData.PlayerRecordEntry entry = PlayerVaultStatsData.PlayerRecordEntry.deserialize(vaultStats.func_74775_l("fastestVault"));
      String displayName = StringUtils.func_151246_b(entry.getPlayerName()) ? "Unclaimed" : entry.getPlayerName();
      FontRenderer fr = Minecraft.func_71410_x().field_71466_p;
      ITextComponent display = new TranslationTextComponent("stat.the_vault.fastestVault").func_240702_b_(":");
      fr.func_238422_b_(matrixStack, display.func_241878_f(), 0.0F, 0.0F, -15130590);
      fr.func_238422_b_(matrixStack, new StringTextComponent(displayName).func_241878_f(), 0.0F, 10.0F, -15130590);
      ITextComponent timeString = new StringTextComponent(UIHelper.formatTimeString(entry.getTickCount()));
      int xOffset = rightShift - fr.func_238414_a_(timeString);
      fr.func_238422_b_(matrixStack, timeString.func_241878_f(), xOffset, 10.0F, -15130590);
      return 25;
   }

   private int buildVaultStatisticsDisplay(CompoundNBT vaultStats, List<Tuple<ITextComponent, Tuple<ITextComponent, Integer>>> out) {
      new DecimalFormat("0.##");
      int numberOffset = 0;
      numberOffset = this.addVaultStat(out, "powerLevel", String.valueOf(vaultStats.func_74762_e("powerLevel")), numberOffset);
      numberOffset = this.addVaultStat(out, "knowledgeLevel", String.valueOf(vaultStats.func_74762_e("knowledgeLevel")), numberOffset);
      numberOffset = this.addVaultStat(out, "crystalsCrafted", String.valueOf(vaultStats.func_74762_e("crystalsCrafted")), numberOffset);
      numberOffset = this.addVaultStat(out, "vaultArtifacts", String.valueOf(vaultStats.func_74762_e("vaultArtifacts")), numberOffset);
      numberOffset = this.addVaultStat(out, "vaultTotal", String.valueOf(vaultStats.func_74762_e("vaultTotal")), numberOffset);
      numberOffset = this.addVaultStat(out, "vaultDeaths", String.valueOf(vaultStats.func_74762_e("vaultDeaths")), numberOffset);
      numberOffset = this.addVaultStat(out, "vaultBails", String.valueOf(vaultStats.func_74762_e("vaultBails")), numberOffset);
      return this.addVaultStat(out, "vaultBossKills", String.valueOf(vaultStats.func_74762_e("vaultBossKills")), numberOffset);
   }

   private int addVaultStat(List<Tuple<ITextComponent, Tuple<ITextComponent, Integer>>> out, String key, String value, int currentMaxOffset) {
      return this.addVaultStat(out, key, value, value, currentMaxOffset);
   }

   private int addVaultStat(
      List<Tuple<ITextComponent, Tuple<ITextComponent, Integer>>> out, String key, String value, String valueLengthStr, int currentMaxOffset
   ) {
      FontRenderer fr = Minecraft.func_71410_x().field_71466_p;
      int valueStrLength = fr.func_78256_a(valueLengthStr);
      if (valueStrLength > currentMaxOffset) {
         currentMaxOffset = valueStrLength;
      }

      Tuple<ITextComponent, Integer> valueDisplayTpl = new Tuple(new StringTextComponent(value), valueStrLength);
      out.add(new Tuple(new TranslationTextComponent("stat.the_vault." + key), valueDisplayTpl));
      return currentMaxOffset;
   }

   private void renderBookBackground(Rectangle containerBounds, MatrixStack renderStack) {
      Minecraft.func_71410_x().func_110434_K().func_110577_a(ReadBookScreen.field_214167_b);
      ScreenDrawHelper.draw(
         7,
         DefaultVertexFormats.field_227851_o_,
         buf -> ScreenDrawHelper.rect(buf, renderStack, containerBounds.width, containerBounds.height)
            .at(containerBounds.x, containerBounds.y)
            .texVanilla(0.0F, 0.0F, 192.0F, 192.0F)
            .draw()
      );
   }
}
