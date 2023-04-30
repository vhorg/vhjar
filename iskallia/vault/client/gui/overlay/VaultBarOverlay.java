package iskallia.vault.client.gui.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import iskallia.vault.client.gui.helper.AnimationTwoPhased;
import iskallia.vault.client.gui.helper.FontHelper;
import iskallia.vault.client.gui.screen.player.AbstractSkillTabElementContainerScreen;
import iskallia.vault.core.vault.ClientVaults;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.util.function.ObservableSupplier;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.IIngameOverlay;

@OnlyIn(Dist.CLIENT)
public class VaultBarOverlay implements IIngameOverlay {
   public static final ResourceLocation VAULT_HUD_SPRITE = new ResourceLocation("the_vault", "textures/gui/vault_hud.png");
   public static int vaultLevel;
   public static int vaultExp;
   public static int tnl;
   public static int unspentSkillPoints;
   public static int unspentExpertisePoints;
   public static int unspentKnowledgePoints;
   public static int unspentArchetypePoints;
   public static int unspentRegretPoints;
   private static final ObservableSupplier<Integer> VAULT_LEVEL_SUPPLIER = ObservableSupplier.of(() -> vaultLevel, Integer::equals);
   private static final ObservableSupplier<Integer> SKILL_POINT_SUPPLIER = ObservableSupplier.of(() -> unspentSkillPoints, Integer::equals);
   private static final ObservableSupplier<Integer> EXPERTISE_POINT_SUPPLIER = ObservableSupplier.of(() -> unspentExpertisePoints, Integer::equals);
   private static final ObservableSupplier<Integer> KNOWLEDGE_POINT_SUPPLIER = ObservableSupplier.of(() -> unspentKnowledgePoints, Integer::equals);
   private static final ObservableSupplier<Integer> ARCHETYPE_POINT_SUPPLIER = ObservableSupplier.of(() -> unspentArchetypePoints, Integer::equals);
   private static final ObservableSupplier<Integer> REGRET_POINT_SUPPLIER = ObservableSupplier.of(() -> unspentRegretPoints, Integer::equals);
   private static Component vaultLevelComponent;
   private static Component unspentSkillPointComponent;
   private static Component unspentExpertisePointComponent;
   private static Component unspentKnowledgePointComponent;
   private static Component unspentArchetypePointComponent;
   private static Component unspentRegretPointComponent;
   private static int unspentSkillPointComponentWidth;
   private static int unspentExpertisePointComponentWidth;
   private static int unspentKnowledgePointComponentWidth;
   private static int unspentArchetypePointComponentWidth;
   private static int unspentRegretPointComponentWidth;
   public static AnimationTwoPhased expGainedAnimation = new AnimationTwoPhased(0.0F, 1.0F, 0.0F, 500);
   public static long previousTick = System.currentTimeMillis();

   public void render(ForgeIngameGui gui, PoseStack matrixStack, float partialTick, int width, int height) {
      Minecraft minecraft = Minecraft.getInstance();
      ProfilerFiller profiler = minecraft.getProfiler();
      LocalPlayer player = minecraft.player;
      if (player != null) {
         profiler.push("VaultBarOverlay");
         int midX = width / 2;
         int barWidth = 83;
         float expPercentage = (float)vaultExp / tnl;
         if (vaultLevel >= ModConfigs.LEVELS_META.getMaxLevel()) {
            expPercentage = 1.0F;
         }

         long now = System.currentTimeMillis();
         expGainedAnimation.tick((int)(now - previousTick));
         previousTick = now;
         profiler.push("experienceBarTexture");
         if (minecraft.gameMode != null && minecraft.gameMode.hasExperience()) {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, VAULT_HUD_SPRITE);
            minecraft.gui.blit(matrixStack, midX + 9, height - 48, 1, 1, barWidth, 5);
            if (expGainedAnimation.getValue() != 0.0F) {
               RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, expGainedAnimation.getValue());
               minecraft.gui.blit(matrixStack, midX + 8, height - 49, 62, 41, 84, 7);
            }

            minecraft.gui.blit(matrixStack, midX + 9, height - 48, 1, 7, (int)(barWidth * expPercentage), 5);
            if (expGainedAnimation.getValue() != 0.0F) {
               RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, expGainedAnimation.getValue());
               minecraft.gui.blit(matrixStack, midX + 8, height - 49, 62, 49, (int)(barWidth * expPercentage), 7);
               RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            }
         }

         profiler.pop();
         profiler.push("text");
         profiler.push("batchExperienceBarText");
         BufferSource buffer = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
         if (!player.isCreative() && !player.isSpectator()) {
            VAULT_LEVEL_SUPPLIER.ifChanged(VaultBarOverlay::onVaultLevelChanged);
            int textX = midX + 51 - minecraft.font.width(vaultLevelComponent) / 2;
            int textY = height - 54;
            FontHelper.drawStringWithBorder(textX, textY, -6601, 3945472, vaultLevelComponent.getVisualOrderText(), minecraft, matrixStack, buffer);
         }

         profiler.popPush("batchKnowledgeAndSkillPointText");
         if (ClientVaults.getActive().isEmpty() && !(minecraft.screen instanceof AbstractSkillTabElementContainerScreen)) {
            renderPointText(minecraft, player, matrixStack, width, buffer);
         }

         profiler.popPush("renderText");
         RenderSystem.enableDepthTest();
         buffer.endBatch();
         RenderSystem.disableDepthTest();
         profiler.pop();
         profiler.pop();
         profiler.pop();
      }
   }

   private static void renderPointText(Minecraft minecraft, LocalPlayer player, PoseStack matrixStack, int right, BufferSource buffer) {
      minecraft.getProfiler().push("calculatePotionOffsetY");
      int potionOffsetY = potionOffsetY(player);
      int gap = 5;
      matrixStack.pushPose();
      if (potionOffsetY > 0) {
         matrixStack.translate(0.0, potionOffsetY, 0.0);
      }

      minecraft.getProfiler().popPush("batchSkillPointText");
      if (unspentSkillPoints > 0) {
         SKILL_POINT_SUPPLIER.ifChanged(VaultBarOverlay::onUnspentSkillPointsChanged);
         int x = right - unspentSkillPointComponentWidth - gap;
         minecraft.font.drawInBatch(unspentSkillPointComponent, x, 18.0F, 16777215, true, matrixStack.last().pose(), buffer, false, 0, 15728880);
         matrixStack.translate(0.0, 12.0, 0.0);
      }

      minecraft.getProfiler().popPush("batchExpertisePointText");
      if (unspentExpertisePoints > 0) {
         EXPERTISE_POINT_SUPPLIER.ifChanged(VaultBarOverlay::onUnspentExpertisePointsChanged);
         int x = right - unspentExpertisePointComponentWidth - gap;
         minecraft.font.drawInBatch(unspentExpertisePointComponent, x, 18.0F, 16777215, true, matrixStack.last().pose(), buffer, false, 0, 15728880);
         matrixStack.translate(0.0, 12.0, 0.0);
      }

      minecraft.getProfiler().popPush("batchRegretPointText");
      if (unspentRegretPoints > 0) {
         REGRET_POINT_SUPPLIER.ifChanged(VaultBarOverlay::onUnspentRegretPointsChanged);
         int x = right - unspentRegretPointComponentWidth - gap;
         minecraft.font.drawInBatch(unspentRegretPointComponent, x, 18.0F, 16777215, true, matrixStack.last().pose(), buffer, false, 0, 15728880);
         matrixStack.translate(0.0, 12.0, 0.0);
      }

      minecraft.getProfiler().popPush("batchKnowledgePointText");
      if (unspentKnowledgePoints > 0) {
         KNOWLEDGE_POINT_SUPPLIER.ifChanged(VaultBarOverlay::onUnspentKnowledgePointsChanged);
         int x = right - unspentKnowledgePointComponentWidth - gap;
         minecraft.font.drawInBatch(unspentKnowledgePointComponent, x, 18.0F, 16777215, true, matrixStack.last().pose(), buffer, false, 0, 15728880);
         matrixStack.translate(0.0, 12.0, 0.0);
      }

      minecraft.getProfiler().popPush("batchArchetypePointText");
      if (unspentArchetypePoints > 0) {
         ARCHETYPE_POINT_SUPPLIER.ifChanged(VaultBarOverlay::onUnspentArchetypePointsChanged);
         int x = right - unspentArchetypePointComponentWidth - gap;
         minecraft.font.drawInBatch(unspentArchetypePointComponent, x, 18.0F, 16777215, true, matrixStack.last().pose(), buffer, false, 0, 15728880);
         matrixStack.translate(0.0, 12.0, 0.0);
      }

      matrixStack.popPose();
      minecraft.getProfiler().pop();
   }

   private static void onVaultLevelChanged(int vaultLevel) {
      vaultLevelComponent = new TextComponent(String.valueOf(vaultLevel));
   }

   private static void onUnspentSkillPointsChanged(int unspentSkillPoints) {
      unspentSkillPointComponent = new TextComponent(String.valueOf(unspentSkillPoints))
         .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(16766976)))
         .append(new TextComponent(" unspent skill point" + (unspentSkillPoints == 1 ? "" : "s")).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(16777215))));
      unspentSkillPointComponentWidth = Minecraft.getInstance().font.width(unspentSkillPointComponent);
   }

   private static void onUnspentExpertisePointsChanged(int unspentExpertisePoints) {
      unspentExpertisePointComponent = new TextComponent(String.valueOf(unspentExpertisePoints))
         .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(16724414)))
         .append(
            new TextComponent(" unspent expertise point" + (unspentExpertisePoints == 1 ? "" : "s"))
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(16777215)))
         );
      unspentExpertisePointComponentWidth = Minecraft.getInstance().font.width(unspentExpertisePointComponent);
   }

   private static void onUnspentKnowledgePointsChanged(int unspentKnowledgePoints) {
      unspentKnowledgePointComponent = new TextComponent(String.valueOf(unspentKnowledgePoints))
         .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(4249521)))
         .append(
            new TextComponent(" unspent knowledge point" + (unspentKnowledgePoints == 1 ? "" : "s"))
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(16777215)))
         );
      unspentKnowledgePointComponentWidth = Minecraft.getInstance().font.width(unspentKnowledgePointComponent);
   }

   private static void onUnspentArchetypePointsChanged(int unspentArchetypePoints) {
      unspentArchetypePointComponent = new TextComponent(String.valueOf(unspentArchetypePoints))
         .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(14905599)))
         .append(
            new TextComponent(" unspent archetype point" + (unspentArchetypePoints == 1 ? "" : "s"))
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(16777215)))
         );
      unspentArchetypePointComponentWidth = Minecraft.getInstance().font.width(unspentArchetypePointComponent);
   }

   private static void onUnspentRegretPointsChanged(int unspentRegretPoints) {
      unspentRegretPointComponent = new TextComponent(String.valueOf(unspentRegretPoints))
         .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(14747439)))
         .append(
            new TextComponent(" unspent regret point" + (unspentRegretPoints == 1 ? "" : "s")).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(16777215)))
         );
      unspentRegretPointComponentWidth = Minecraft.getInstance().font.width(unspentRegretPointComponent);
   }

   private static int potionOffsetY(LocalPlayer player) {
      List<MobEffectInstance> effectInstances = player.getActiveEffects().stream().filter(MobEffectInstance::showIcon).toList();
      if (effectInstances.size() == 0) {
         return 0;
      } else {
         for (MobEffectInstance effectInstance : effectInstances) {
            if (effectInstance.getEffect().getCategory() == MobEffectCategory.HARMFUL) {
               return 36;
            }
         }

         return 18;
      }
   }
}
