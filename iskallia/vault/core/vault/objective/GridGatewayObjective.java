package iskallia.vault.core.vault.objective;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import iskallia.vault.block.PlaceholderBlock;
import iskallia.vault.client.gui.helper.LightmapHelper;
import iskallia.vault.core.Version;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.vault.CompoundAdapter;
import iskallia.vault.core.data.compound.UUIDList;
import iskallia.vault.core.data.key.FieldKey;
import iskallia.vault.core.data.key.SupplierKey;
import iskallia.vault.core.data.key.registry.FieldRegistry;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.event.common.BlockSetEvent;
import iskallia.vault.core.event.common.BlockUseEvent;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.player.Listener;
import iskallia.vault.core.vault.player.Runner;
import iskallia.vault.core.world.data.entity.PartialCompoundNbt;
import iskallia.vault.core.world.data.tile.PartialBlockState;
import iskallia.vault.core.world.data.tile.PartialTile;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.init.ModBlocks;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class GridGatewayObjective extends Objective {
   public static final SupplierKey<Objective> KEY = SupplierKey.of("grid_gateway", Objective.class).with(Version.v1_27, GridGatewayObjective::new);
   public static final FieldRegistry FIELDS = Objective.FIELDS.merge(new FieldRegistry());
   public static final FieldKey<Float> OBJECTIVE_PROBABILITY = FieldKey.of("objective_probability", Float.class)
      .with(Version.v1_27, Adapters.FLOAT, DISK.all())
      .register(FIELDS);
   public static final FieldKey<UUIDList> COMPLETED_PLAYERS = FieldKey.of("completed_players", UUIDList.class)
      .with(Version.v1_27, CompoundAdapter.of(UUIDList::create), DISK.all().or(CLIENT.all()))
      .register(FIELDS);
   public static final FieldKey<Void> NOTIFIED = FieldKey.of("notified", Void.class)
      .with(Version.v1_27, Adapters.ofVoid(), DISK.all().or(CLIENT.all()))
      .register(FIELDS);

   public GridGatewayObjective() {
      this.set(COMPLETED_PLAYERS, UUIDList.create());
   }

   public static GridGatewayObjective of(float objectiveProbability) {
      return (GridGatewayObjective)new GridGatewayObjective().set(OBJECTIVE_PROBABILITY, Float.valueOf(objectiveProbability));
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
      CommonEvents.OBJECTIVE_PIECE_GENERATION
         .register(this, data -> this.ifPresent(OBJECTIVE_PROBABILITY, probability -> data.setProbability(probability.floatValue())));
      CommonEvents.BLOCK_SET.at(BlockSetEvent.Type.RETURN).in(world).register(this, data -> {
         PartialTile target = PartialTile.of(PartialBlockState.of(ModBlocks.PLACEHOLDER), PartialCompoundNbt.empty());
         target.getState().set(PlaceholderBlock.TYPE, PlaceholderBlock.Type.OBJECTIVE);
         if (target.isSubsetOf(PartialTile.of(data.getState()))) {
            data.getWorld().setBlock(data.getPos(), ModBlocks.GRID_GATEWAY.defaultBlockState(), 3);
         }
      });
      CommonEvents.BLOCK_USE.in(world).at(BlockUseEvent.Phase.HEAD).of(ModBlocks.GRID_GATEWAY).register(this, data -> {
         if (data.getHand() != InteractionHand.MAIN_HAND) {
            data.setResult(InteractionResult.SUCCESS);
         } else if (!this.get(COMPLETED_PLAYERS).contains(data.getPlayer().getUUID())) {
            if (vault.get(Vault.LISTENERS).get(data.getPlayer().getUUID()) instanceof Runner runner && runner.isActive(world, vault, this)) {
               this.get(COMPLETED_PLAYERS).add(data.getPlayer().getUUID());
               data.setResult(InteractionResult.SUCCESS);
            } else {
               data.setResult(InteractionResult.SUCCESS);
            }
         }
      });
   }

   @Override
   public void tickServer(VirtualWorld world, Vault vault) {
      if (!this.has(NOTIFIED)) {
         for (Listener listener : vault.get(Vault.LISTENERS).getAll()) {
            listener.getPlayer()
               .ifPresent(
                  other -> {
                     MutableComponent text = new TextComponent("")
                        .append(new TextComponent("Grid Gateways"))
                        .append(new TextComponent(" are now open!").withStyle(ChatFormatting.GRAY));
                     world.playSound(null, other.getX(), other.getY(), other.getZ(), SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 0.9F, 1.2F);
                     other.displayClientMessage(text, false);
                  }
               );
         }

         this.set(NOTIFIED);
      }

      if (vault.get(Vault.LISTENERS).getAll(Runner.class).stream().allMatch(runner -> this.get(COMPLETED_PLAYERS).contains(runner.getId()))) {
         super.tickServer(world, vault);
      }
   }

   @Override
   public void tickListener(VirtualWorld world, Vault vault, Listener listener) {
      if (this.get(COMPLETED_PLAYERS).contains(listener.get(Listener.ID))) {
         super.tickListener(world, vault, listener);
      }
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public boolean render(Vault vault, PoseStack matrixStack, Window window, float partialTicks, Player player) {
      if (!this.get(COMPLETED_PLAYERS).contains(player.getUUID())) {
         int midX = window.getGuiScaledWidth() / 2;
         Font font = Minecraft.getInstance().font;
         BufferSource buffer = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
         Component text = new TextComponent("Find Grid Gateway!").withStyle(ChatFormatting.AQUA);
         font.drawInBatch(
            text.getVisualOrderText(),
            midX - font.width(text) / 2.0F,
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
      } else {
         boolean rendered = false;

         for (Objective objective : this.get(CHILDREN)) {
            rendered |= objective.render(vault, matrixStack, window, partialTicks, player);
         }

         return rendered;
      }
   }

   @Override
   public boolean isActive(VirtualWorld world, Vault vault, Objective objective) {
      return objective == this;
   }
}
