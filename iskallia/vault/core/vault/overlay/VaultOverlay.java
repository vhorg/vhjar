package iskallia.vault.core.vault.overlay;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.VaultMod;
import iskallia.vault.core.Version;
import iskallia.vault.core.data.DataObject;
import iskallia.vault.core.data.adapter.Adapter;
import iskallia.vault.core.data.key.FieldKey;
import iskallia.vault.core.data.key.registry.FieldRegistry;
import iskallia.vault.core.event.ClientEvents;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.WorldManager;
import iskallia.vault.core.vault.player.Listener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

public class VaultOverlay extends DataObject<VaultOverlay> {
   public static final ResourceLocation VAULT_HUD = VaultMod.id("textures/gui/vault_hud.png");
   public static final ResourceLocation ARCHITECT_HUD = VaultMod.id("textures/gui/architect_event_bar.png");
   public static final ResourceLocation VIGNETTE = VaultMod.id("textures/gui/vignette.png");
   public static final FieldRegistry FIELDS = new FieldRegistry();
   public static final FieldKey<Void> VISIBLE = FieldKey.of("visible", Void.class)
      .with(Version.v1_0, Adapter.ofVoid(), DISK.all().or(CLIENT.all()))
      .register(FIELDS);

   @Override
   public FieldRegistry getFields() {
      return FIELDS;
   }

   @OnlyIn(Dist.CLIENT)
   public void initClient(Vault vault) {
      ClientEvents.RENDER_OVERLAY.forType(ElementType.ALL).register(vault, data -> {
         ClientLevel world = Minecraft.getInstance().level;
         if (world != null && world.dimension().location().equals(vault.get(Vault.WORLD).get(WorldManager.KEY))) {
            this.render(vault, data.getMatrixStack(), data.getWindow(), data.getPartialTicks());
         }
      });
   }

   @OnlyIn(Dist.CLIENT)
   public void render(Vault vault, PoseStack matrixStack, Window window, float partialTicks) {
      LocalPlayer player = Minecraft.getInstance().player;
      vault.ifPresent(Vault.LISTENERS, listeners -> {
         if (player != null) {
            Listener listener = listeners.get(player.getUUID());
            if (listener != null) {
               listener.renderObjectives(vault, matrixStack, window, partialTicks, player);
            }
         }
      });
      vault.ifPresent(Vault.CLOCK, clock -> {
         matrixStack.pushPose();
         matrixStack.translate(92.0, window.getGuiScaledHeight() - 25, 0.0);
         clock.render(matrixStack);
         matrixStack.popPose();
      });
      vault.ifPresent(Vault.MODIFIERS, modifiers -> ModifiersRenderer.renderVaultModifiers(modifiers, matrixStack));
   }
}
