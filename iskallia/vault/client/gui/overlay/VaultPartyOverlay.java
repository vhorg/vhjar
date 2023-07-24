package iskallia.vault.client.gui.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.ClientActiveEternalData;
import iskallia.vault.client.ClientPartyData;
import iskallia.vault.client.gui.helper.FontHelper;
import iskallia.vault.config.EternalAuraConfig;
import iskallia.vault.core.vault.ClientVaults;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.objective.Objective;
import iskallia.vault.entity.eternal.ActiveEternalData;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModShaders;
import iskallia.vault.world.data.VaultPartyData;
import java.util.Set;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.IIngameOverlay;

@OnlyIn(Dist.CLIENT)
public class VaultPartyOverlay implements IIngameOverlay {
   public static final ResourceLocation VAULT_HUD_SPRITE = new ResourceLocation("the_vault", "textures/gui/vault_hud.png");

   public void render(ForgeIngameGui gui, PoseStack matrixStack, float partialTick, int width, int height) {
      Minecraft mc = Minecraft.getInstance();
      LocalPlayer player = mc.player;
      if (player != null) {
         float offsetY = Math.max(height / 3.0F, 45.0F);
         offsetY += renderPartyHUD(matrixStack, offsetY, width);
         offsetY += renderEternalHUD(matrixStack, offsetY, width);
         RenderSystem.setShader(GameRenderer::getPositionTexShader);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.setShaderTexture(0, GuiComponent.GUI_ICONS_LOCATION);
      }
   }

   private static int renderEternalHUD(PoseStack matrixStack, float offsetY, int right) {
      Set<ActiveEternalData.ActiveEternal> eternals = ClientActiveEternalData.getActiveEternals();
      if (eternals.isEmpty()) {
         return 0;
      } else {
         int height = 0;
         matrixStack.pushPose();
         matrixStack.translate(right - 5, offsetY, 0.0);
         matrixStack.pushPose();
         matrixStack.scale(0.8F, 0.8F, 1.0F);
         Component vpText = new TextComponent("Eternals").withStyle(ChatFormatting.GOLD);
         FontHelper.drawTextComponent(matrixStack, vpText, true);
         matrixStack.popPose();
         height += 8;
         matrixStack.translate(0.0, 8.0, -50.0);
         matrixStack.scale(0.7F, 0.7F, 1.0F);

         for (ActiveEternalData.ActiveEternal eternal : eternals) {
            int eternalHeight = renderEternalSection(matrixStack, eternal) + 4;
            matrixStack.translate(0.0, eternalHeight, 0.0);
            height = (int)(height + eternalHeight * 0.7F);
         }

         matrixStack.popPose();
         return height + 6;
      }
   }

   private static int renderEternalSection(PoseStack matrixStack, ActiveEternalData.ActiveEternal eternal) {
      int textSize = 8;
      int headSize = 16;
      int gap = 2;
      boolean dead = eternal.getHealth() <= 0.0F;
      ResourceLocation skin = eternal.getSkin().getLocationSkin();
      MutableComponent txt = new TextComponent("");
      matrixStack.pushPose();
      matrixStack.translate(-headSize, 0.0, 0.0);
      render2DHead(matrixStack, skin, headSize, dead);
      matrixStack.translate(-gap, (headSize - textSize) / 2.0F, 0.0);
      if (dead) {
         txt.append(new TextComponent("Unalived").withStyle(ChatFormatting.RED));
      } else {
         int heartSize = 9;
         int heartU = 86;
         int heartV = 2;
         matrixStack.translate(-heartSize, 0.0, 0.0);
         RenderSystem.setShader(GameRenderer::getPositionTexShader);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.setShaderTexture(0, VAULT_HUD_SPRITE);
         GuiComponent.blit(matrixStack, 0, 0, heartU, heartV, heartSize, heartSize, 256, 256);
         matrixStack.translate(-gap, 0.0, 0.0);
         txt.append(new TextComponent((int)eternal.getHealth() + "x").withStyle(ChatFormatting.WHITE));
      }

      int width = FontHelper.drawTextComponent(matrixStack, txt, true);
      if (eternal.getAbilityName() != null) {
         EternalAuraConfig.AuraConfig cfg = ModConfigs.ETERNAL_AURAS.getByName(eternal.getAbilityName());
         if (cfg != null) {
            matrixStack.translate(-(width + 18), -4.0, 0.0);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, new ResourceLocation(cfg.getIconPath()));
            GuiComponent.blit(matrixStack, 0, 0, 0.0F, 0.0F, 16, 16, 16, 16);
         }
      }

      matrixStack.popPose();
      return headSize;
   }

   private static int renderPartyHUD(PoseStack matrixStack, float offsetY, int right) {
      LocalPlayer player = Minecraft.getInstance().player;
      VaultPartyData.Party thisParty = ClientPartyData.getParty(player.getUUID());
      if (thisParty == null) {
         return 0;
      } else {
         int height = 0;
         matrixStack.pushPose();
         matrixStack.translate(right - 5, offsetY, 0.0);
         matrixStack.pushPose();
         matrixStack.scale(0.8F, 0.8F, 1.0F);
         Component vpText = new TextComponent("Vault Party").withStyle(ChatFormatting.GREEN);
         FontHelper.drawTextComponent(matrixStack, vpText, true);
         matrixStack.popPose();
         height += 8;
         matrixStack.translate(0.0, 8.0, -50.0);
         matrixStack.scale(0.7F, 0.7F, 1.0F);
         ClientPacketListener netHandler = Minecraft.getInstance().getConnection();
         if (netHandler != null) {
            for (UUID uuid : thisParty.getMembers()) {
               PlayerInfo info = netHandler.getPlayerInfo(uuid);
               int playerHeight = renderPartyPlayerSection(matrixStack, thisParty, uuid, info) + 4;
               matrixStack.translate(0.0, playerHeight, 0.0);
               height = (int)(height + playerHeight * 0.7F);
            }
         }

         matrixStack.popPose();
         return height + 6;
      }
   }

   private static int renderPartyPlayerSection(PoseStack matrixStack, VaultPartyData.Party party, UUID playerUUID, PlayerInfo info) {
      int textSize = 8;
      int headSize = 16;
      int gap = 2;
      boolean offline = info == null;
      ClientPartyData.PartyMember member = offline ? null : ClientPartyData.getCachedMember(info.getProfile().getId());
      ResourceLocation skin = offline ? DefaultPlayerSkin.getDefaultSkin() : info.getSkinLocation();
      String prefix = playerUUID.equals(party.getLeader()) ? "⭐ " : "";
      MutableComponent txt = new TextComponent(prefix).withStyle(ChatFormatting.GOLD);
      matrixStack.pushPose();
      matrixStack.translate(-headSize, 0.0, 0.0);
      render2DHead(matrixStack, skin, headSize, offline);
      matrixStack.translate(-gap, (headSize - textSize) / 2.0F, 0.0);
      int heartSize = 9;
      if (offline) {
         txt.append(new TextComponent(prefix + "OFFLINE").withStyle(ChatFormatting.GRAY));
      } else {
         int heartU = 86;
         int heartV = 2;
         matrixStack.translate(-heartSize, 0.0, 0.0);
         RenderSystem.setShader(GameRenderer::getPositionTexShader);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.setShaderTexture(0, VAULT_HUD_SPRITE);
         ClientPartyData.PartyMember.Status status = member == null ? ClientPartyData.PartyMember.Status.NORMAL : member.status;
         GuiComponent.blit(matrixStack, 0, 0, heartU + getPartyPlayerStatusOffset(status), heartV, heartSize, heartSize, 256, 256);
         matrixStack.translate(-gap, 0.0, 0.0);
         txt.append(new TextComponent(member == null ? "-" : (int)member.healthPts + "x").withStyle(ChatFormatting.WHITE));
      }

      FontHelper.drawTextComponent(matrixStack, txt, true);
      if (Minecraft.getInstance().gui.getTabList().visible) {
         if (offline) {
            matrixStack.translate(-heartSize - gap, 0.0, 0.0);
         }

         renderObjectives(matrixStack, playerUUID);
      }

      matrixStack.popPose();
      return headSize;
   }

   private static void renderObjectives(PoseStack matrixStack, UUID playerUUID) {
      ClientVaults.getActive().ifPresent(vault -> {
         if (vault.get(Vault.LISTENERS).contains(playerUUID)) {
            vault.get(Vault.OBJECTIVES).forEach(Objective.class, objective -> {
               objective.renderPartyInfo(matrixStack, playerUUID);
               return true;
            });
         }
      });
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

   private static void render2DHead(PoseStack matrixStack, ResourceLocation skin, int size, boolean grayscaled) {
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShaderTexture(0, skin);
      int u1 = 8;
      int v1 = 8;
      int u2 = 40;
      int v2 = 8;
      int w = 8;
      int h = 8;
      if (grayscaled) {
         ModShaders.getGrayscalePositionTexShader().withGrayscale(0.0F).withBrightness(1.0F).enable();
      }

      GuiComponent.blit(matrixStack, 0, 0, size, size, u1, v1, w, h, 64, 64);
      GuiComponent.blit(matrixStack, 0, 0, size, size, u2, v2, w, h, 64, 64);
   }
}
