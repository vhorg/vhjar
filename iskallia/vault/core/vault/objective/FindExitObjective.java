package iskallia.vault.core.vault.objective;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import iskallia.vault.client.gui.helper.LightmapHelper;
import iskallia.vault.core.Version;
import iskallia.vault.core.data.adapter.Adapter;
import iskallia.vault.core.data.compound.IdentifierList;
import iskallia.vault.core.data.key.FieldKey;
import iskallia.vault.core.data.key.SupplierKey;
import iskallia.vault.core.data.key.registry.FieldRegistry;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.WorldManager;
import iskallia.vault.core.vault.player.Listener;
import iskallia.vault.core.vault.player.Runner;
import iskallia.vault.core.world.storage.VirtualWorld;
import java.util.Arrays;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class FindExitObjective extends Objective {
   public static final SupplierKey<Objective> KEY = SupplierKey.of("find_exit", Objective.class).with(Version.v1_0, FindExitObjective::new);
   public static final FieldRegistry FIELDS = Objective.FIELDS.merge(new FieldRegistry());
   public static final FieldKey<IdentifierList> TAGS = FieldKey.of("tags", IdentifierList.class)
      .with(Version.v1_0, Adapter.ofCompound(), DISK.all().or(CLIENT.all()), IdentifierList::create)
      .register(FIELDS);

   public FindExitObjective() {
      this.set(TAGS, IdentifierList.create());
   }

   public static FindExitObjective create(ResourceLocation... tags) {
      FindExitObjective objective = new FindExitObjective();
      objective.get(TAGS).addAll(Arrays.asList(tags));
      return objective;
   }

   @Override
   public SupplierKey<Objective> getKey() {
      return KEY;
   }

   @Override
   public FieldRegistry getFields() {
      return FIELDS;
   }

   @Override
   public void initServer(VirtualWorld world, Vault vault) {
      CommonEvents.VAULT_PORTAL_COLLIDE
         .in(world)
         .register(
            this,
            data -> {
               Listener listener = vault.get(Vault.LISTENERS).get(data.getPlayer().getUUID());
               if (listener instanceof Runner runner) {
                  if (listener.isActive(vault, this)) {
                     if (!data.getPlayer().isOnPortalCooldown()) {
                        if (vault.has(Vault.WORLD)) {
                           vault.get(Vault.WORLD)
                              .ifPresent(
                                 WorldManager.PORTAL_LOGIC,
                                 portalLogic -> portalLogic.getPortals()
                                    .filter(portal -> portal.contains(data.getPos()))
                                    .filter(portal -> portal.hasAnyTag(this.get(TAGS)))
                                    .findAny()
                                    .ifPresent(portal -> vault.get(Vault.LISTENERS).remove(world, vault, runner))
                              );
                        }
                     }
                  }
               }
            }
         );
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public boolean render(PoseStack matrixStack, Window window, float partialTicks, Player player) {
      int width = window.getGuiScaledWidth();
      int height = window.getGuiScaledHeight();
      BufferSource buffer = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
      Component txt = new TextComponent("Find the Exit!").withStyle(ChatFormatting.AQUA).withStyle(ChatFormatting.BOLD);
      Minecraft.getInstance()
         .font
         .drawInBatch(
            txt.getVisualOrderText(), 8.0F, height - 54, -1, true, matrixStack.last().pose(), buffer, false, 0, LightmapHelper.getPackedFullbrightCoords()
         );
      buffer.endBatch();
      return true;
   }

   @Override
   public boolean isActive(Objective objective) {
      return objective == this;
   }
}
