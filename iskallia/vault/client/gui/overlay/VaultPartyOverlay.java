package iskallia.vault.client.gui.overlay;

import com.mojang.blaze3d.matrix.MatrixStack;
import iskallia.vault.client.ClientActiveEternalData;
import iskallia.vault.client.ClientPartyData;
import iskallia.vault.client.gui.helper.FontHelper;
import iskallia.vault.client.util.ShaderUtil;
import iskallia.vault.config.EternalAuraConfig;
import iskallia.vault.entity.eternal.ActiveEternalData;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.world.data.VaultPartyData;
import java.util.Set;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Post;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.opengl.ARBShaderObjects;

@OnlyIn(Dist.CLIENT)
public class VaultPartyOverlay {
   public static final ResourceLocation VAULT_HUD_SPRITE = new ResourceLocation("the_vault", "textures/gui/vault-hud.png");

   @SubscribeEvent
   public static void renderSidebarHUD(Post event) {
      if (event.getType() == ElementType.HOTBAR) {
         Minecraft mc = Minecraft.func_71410_x();
         ClientPlayerEntity player = mc.field_71439_g;
         if (player != null) {
            MatrixStack matrixStack = event.getMatrixStack();
            int bottom = mc.func_228018_at_().func_198087_p();
            int right = mc.func_228018_at_().func_198107_o();
            float offsetY = Math.max(bottom / 3.0F, 45.0F);
            offsetY += renderPartyHUD(matrixStack, offsetY, right);
            offsetY += renderEternalHUD(matrixStack, offsetY, right);
            mc.func_110434_K().func_110577_a(AbstractGui.field_230665_h_);
         }
      }
   }

   private static int renderEternalHUD(MatrixStack matrixStack, float offsetY, int right) {
      Set<ActiveEternalData.ActiveEternal> eternals = ClientActiveEternalData.getActiveEternals();
      if (eternals.isEmpty()) {
         return 0;
      } else {
         int height = 0;
         matrixStack.func_227860_a_();
         matrixStack.func_227861_a_(right - 5, offsetY, 0.0);
         matrixStack.func_227860_a_();
         matrixStack.func_227862_a_(0.8F, 0.8F, 1.0F);
         ITextComponent vpText = new StringTextComponent("Eternals").func_240699_a_(TextFormatting.GOLD);
         FontHelper.drawTextComponent(matrixStack, vpText, true);
         matrixStack.func_227865_b_();
         height += 8;
         matrixStack.func_227861_a_(0.0, 8.0, -50.0);
         matrixStack.func_227862_a_(0.7F, 0.7F, 1.0F);

         for (ActiveEternalData.ActiveEternal eternal : eternals) {
            int eternalHeight = renderEternalSection(matrixStack, eternal) + 4;
            matrixStack.func_227861_a_(0.0, eternalHeight, 0.0);
            height = (int)(height + eternalHeight * 0.7F);
         }

         matrixStack.func_227865_b_();
         return height + 6;
      }
   }

   private static int renderEternalSection(MatrixStack matrixStack, ActiveEternalData.ActiveEternal eternal) {
      int textSize = 8;
      int headSize = 16;
      int gap = 2;
      boolean dead = eternal.getHealth() <= 0.0F;
      ResourceLocation skin = eternal.getSkin().getLocationSkin();
      IFormattableTextComponent txt = new StringTextComponent("");
      matrixStack.func_227860_a_();
      matrixStack.func_227861_a_(-headSize, 0.0, 0.0);
      render2DHead(matrixStack, skin, headSize, dead);
      matrixStack.func_227861_a_(-gap, (headSize - textSize) / 2.0F, 0.0);
      if (dead) {
         txt.func_230529_a_(new StringTextComponent("Unalived").func_240699_a_(TextFormatting.RED));
      } else {
         int heartSize = 9;
         int heartU = 86;
         int heartV = 2;
         matrixStack.func_227861_a_(-heartSize, 0.0, 0.0);
         Minecraft.func_71410_x().func_110434_K().func_110577_a(VAULT_HUD_SPRITE);
         AbstractGui.func_238463_a_(matrixStack, 0, 0, heartU, heartV, heartSize, heartSize, 256, 256);
         matrixStack.func_227861_a_(-gap, 0.0, 0.0);
         txt.func_230529_a_(new StringTextComponent((int)eternal.getHealth() + "x").func_240699_a_(TextFormatting.WHITE));
      }

      int width = FontHelper.drawTextComponent(matrixStack, txt, true);
      if (eternal.getAbilityName() != null) {
         EternalAuraConfig.AuraConfig cfg = ModConfigs.ETERNAL_AURAS.getByName(eternal.getAbilityName());
         if (cfg != null) {
            matrixStack.func_227861_a_(-(width + 18), -4.0, 0.0);
            Minecraft.func_71410_x().func_110434_K().func_110577_a(new ResourceLocation(cfg.getIconPath()));
            AbstractGui.func_238463_a_(matrixStack, 0, 0, 0.0F, 0.0F, 16, 16, 16, 16);
         }
      }

      matrixStack.func_227865_b_();
      return headSize;
   }

   private static int renderPartyHUD(MatrixStack matrixStack, float offsetY, int right) {
      ClientPlayerEntity player = Minecraft.func_71410_x().field_71439_g;
      VaultPartyData.Party thisParty = ClientPartyData.getParty(player.func_110124_au());
      if (thisParty == null) {
         return 0;
      } else {
         int height = 0;
         matrixStack.func_227860_a_();
         matrixStack.func_227861_a_(right - 5, offsetY, 0.0);
         matrixStack.func_227860_a_();
         matrixStack.func_227862_a_(0.8F, 0.8F, 1.0F);
         ITextComponent vpText = new StringTextComponent("Vault Party").func_240699_a_(TextFormatting.GREEN);
         FontHelper.drawTextComponent(matrixStack, vpText, true);
         matrixStack.func_227865_b_();
         height += 8;
         matrixStack.func_227861_a_(0.0, 8.0, -50.0);
         matrixStack.func_227862_a_(0.7F, 0.7F, 1.0F);
         ClientPlayNetHandler netHandler = Minecraft.func_71410_x().func_147114_u();
         if (netHandler != null) {
            for (UUID uuid : thisParty.getMembers()) {
               NetworkPlayerInfo info = netHandler.func_175102_a(uuid);
               int playerHeight = renderPartyPlayerSection(matrixStack, thisParty, uuid, info) + 4;
               matrixStack.func_227861_a_(0.0, playerHeight, 0.0);
               height = (int)(height + playerHeight * 0.7F);
            }
         }

         matrixStack.func_227865_b_();
         return height + 6;
      }
   }

   private static int renderPartyPlayerSection(MatrixStack matrixStack, VaultPartyData.Party party, UUID playerUUID, NetworkPlayerInfo info) {
      int textSize = 8;
      int headSize = 16;
      int gap = 2;
      boolean offline = info == null;
      ClientPartyData.PartyMember member = offline ? null : ClientPartyData.getCachedMember(info.func_178845_a().getId());
      ResourceLocation skin = offline ? DefaultPlayerSkin.func_177335_a() : info.func_178837_g();
      String prefix = playerUUID.equals(party.getLeader()) ? "⭐ " : "";
      IFormattableTextComponent txt = new StringTextComponent(prefix).func_240699_a_(TextFormatting.GOLD);
      matrixStack.func_227860_a_();
      matrixStack.func_227861_a_(-headSize, 0.0, 0.0);
      render2DHead(matrixStack, skin, headSize, offline);
      matrixStack.func_227861_a_(-gap, (headSize - textSize) / 2.0F, 0.0);
      if (offline) {
         txt.func_230529_a_(new StringTextComponent(prefix + "OFFLINE").func_240699_a_(TextFormatting.GRAY));
      } else {
         int heartSize = 9;
         int heartU = 86;
         int heartV = 2;
         matrixStack.func_227861_a_(-heartSize, 0.0, 0.0);
         Minecraft.func_71410_x().func_110434_K().func_110577_a(VAULT_HUD_SPRITE);
         ClientPartyData.PartyMember.Status status = member == null ? ClientPartyData.PartyMember.Status.NORMAL : member.status;
         AbstractGui.func_238463_a_(matrixStack, 0, 0, heartU + getPartyPlayerStatusOffset(status), heartV, heartSize, heartSize, 256, 256);
         matrixStack.func_227861_a_(-gap, 0.0, 0.0);
         txt.func_230529_a_(new StringTextComponent(member == null ? "-" : (int)member.healthPts + "x").func_240699_a_(TextFormatting.WHITE));
      }

      FontHelper.drawTextComponent(matrixStack, txt, true);
      matrixStack.func_227865_b_();
      return headSize;
   }

   private static int getPartyPlayerStatusOffset(ClientPartyData.PartyMember.Status status) {
      switch (status) {
         case POISONED:
            return 10;
         case WITHERED:
            return 20;
         case DEAD:
            return 30;
         default:
            return 0;
      }
   }

   public static void render2DHead(MatrixStack matrixStack, ResourceLocation skin, int size, boolean grayscaled) {
      Minecraft minecraft = Minecraft.func_71410_x();
      minecraft.func_110434_K().func_110577_a(skin);
      int u1 = 8;
      int v1 = 8;
      int u2 = 40;
      int v2 = 8;
      int w = 8;
      int h = 8;
      if (grayscaled) {
         ShaderUtil.useShader(ShaderUtil.GRAYSCALE_SHADER, () -> {
            int grayScaleFactor = ShaderUtil.getUniformLocation(ShaderUtil.GRAYSCALE_SHADER, "grayFactor");
            ARBShaderObjects.glUniform1fARB(grayScaleFactor, 0.0F);
         });
      }

      AbstractGui.func_238466_a_(matrixStack, 0, 0, size, size, u1, v1, w, h, 64, 64);
      AbstractGui.func_238466_a_(matrixStack, 0, 0, size, size, u2, v2, w, h, 64, 64);
      if (grayscaled) {
         ShaderUtil.releaseShader();
      }
   }
}
