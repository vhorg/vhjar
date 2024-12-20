package iskallia.vault.core.vault.objective;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import iskallia.vault.client.gui.helper.LightmapHelper;
import iskallia.vault.core.Version;
import iskallia.vault.core.data.adapter.vault.CompoundAdapter;
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
import net.minecraft.client.gui.Font;
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
      .with(Version.v1_0, CompoundAdapter.of(IdentifierList::create), DISK.all().or(CLIENT.all()))
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
                  if (listener.isActive(world, vault, this)) {
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

   @Override
   public void tickListener(VirtualWorld world, Vault vault, Listener listener) {
      if (listener instanceof Runner && !listener.isActive(world, vault, this)) {
         listener.addObjective(vault, this);
      }

      super.tickListener(world, vault, listener);
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public boolean render(Vault vault, PoseStack matrixStack, Window window, float partialTicks, Player player) {
      int midX = window.getGuiScaledWidth() / 2;
      Font font = Minecraft.getInstance().font;
      BufferSource buffer = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
      Component txt = new TextComponent("Find the Exit!").withStyle(ChatFormatting.AQUA);
      font.drawInBatch(
         txt.getVisualOrderText(),
         midX - font.width(txt) / 2.0F,
         9.0F,
         -1,
         true,
         matrixStack.last().pose(),
         buffer,
         false,
         0,
         LightmapHelper.getPackedFullbrightCoords()
      );
      buffer.endBatch();
      return true;
   }

   @Override
   public boolean isActive(VirtualWorld world, Vault vault, Objective objective) {
      return objective == this;
   }
}
