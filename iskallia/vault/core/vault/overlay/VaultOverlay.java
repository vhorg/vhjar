package iskallia.vault.core.vault.overlay;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.VaultMod;
import iskallia.vault.core.Version;
import iskallia.vault.core.data.DataObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.key.FieldKey;
import iskallia.vault.core.data.key.registry.FieldRegistry;
import iskallia.vault.core.event.ClientEvents;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.WorldManager;
import iskallia.vault.core.vault.influence.Influences;
import iskallia.vault.core.vault.modifier.spi.VaultModifier;
import iskallia.vault.core.vault.player.Listener;
import iskallia.vault.core.vault.player.Runner;
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import java.util.Map;
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
   public static final FieldKey<Void> HIDDEN = FieldKey.of("hidden", Void.class)
      .with(Version.v1_0, Adapters.ofVoid(), DISK.all().or(CLIENT.all()))
      .register(FIELDS);

   @Override
   public FieldRegistry getFields() {
      return FIELDS;
   }

   @OnlyIn(Dist.CLIENT)
   public void initClient(Vault vault) {
      ClientEvents.RENDER_OVERLAY.forType(ElementType.ALL).register(vault, data -> {
         if (!this.has(HIDDEN)) {
            ClientLevel world = Minecraft.getInstance().level;
            if (world != null && world.dimension().location().equals(vault.get(Vault.WORLD).get(WorldManager.KEY))) {
               this.render(vault, data.getMatrixStack(), data.getWindow(), data.getPartialTicks());
            }
         }
      });
   }

   @OnlyIn(Dist.CLIENT)
   public void render(Vault vault, PoseStack matrixStack, Window window, float partialTicks) {
      LocalPlayer player = Minecraft.getInstance().player;
      if (player != null) {
         Listener listener = vault.getOptional(Vault.LISTENERS).map(listeners -> listeners.get(player.getUUID())).orElse(null);
         if (listener != null) {
            matrixStack.pushPose();
            Minecraft mc = Minecraft.getInstance();
            if (mc.gui.getTabList().visible) {
               matrixStack.translate(0.0, mc.player.connection.getOnlinePlayers().size() * 9 + 10, 0.0);
            }

            listener.renderObjectives(vault, matrixStack, window, partialTicks, player);
            matrixStack.popPose();
         }

         vault.ifPresent(Vault.CLOCK, clock -> {
            matrixStack.pushPose();
            matrixStack.translate(92.0, window.getGuiScaledHeight() - 25, 0.0);
            clock.render(matrixStack);
            matrixStack.popPose();
         });
         Map<VaultModifier<?>, Integer> renderModifiers = new Object2IntLinkedOpenHashMap();
         if (listener != null) {
            listener.getOptional(Runner.INFLUENCES)
               .map(influences -> influences.get(Influences.FAVOURS))
               .ifPresent(favours -> renderModifiers.putAll(favours.getDisplayGroup()));
         }

         vault.ifPresent(Vault.MODIFIERS, modifiers -> renderModifiers.putAll(modifiers.getDisplayGroup()));
         ModifiersRenderer.renderVaultModifiers(renderModifiers, matrixStack);
      }
   }
}
