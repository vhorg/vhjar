package iskallia.vault.core.vault.objective;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import iskallia.vault.block.PlaceholderBlock;
import iskallia.vault.block.entity.LodestoneTileEntity;
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
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class LodestoneObjective extends Objective {
   public static final SupplierKey<Objective> KEY = SupplierKey.of("lodestone", Objective.class).with(Version.v1_13, LodestoneObjective::new);
   public static final FieldRegistry FIELDS = Objective.FIELDS.merge(new FieldRegistry());
   public static final FieldKey<UUIDList> COMPLETED_PLAYERS = FieldKey.of("completed_players", UUIDList.class)
      .with(Version.v1_13, CompoundAdapter.of(UUIDList::create), DISK.all().or(CLIENT.all()))
      .register(FIELDS);
   public static final FieldKey<Float> OBJECTIVE_PROBABILITY = FieldKey.of("objective_probability", Float.class)
      .with(Version.v1_13, Adapters.FLOAT, DISK.all())
      .register(FIELDS);

   protected LodestoneObjective() {
      this.set(COMPLETED_PLAYERS, UUIDList.create());
   }

   public LodestoneObjective(float objectiveProbability) {
      this.set(COMPLETED_PLAYERS, UUIDList.create());
      this.set(OBJECTIVE_PROBABILITY, Float.valueOf(objectiveProbability));
   }

   public static LodestoneObjective of(float objectiveProbability) {
      return new LodestoneObjective(objectiveProbability);
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
      BlockState targetState = (BlockState)ModBlocks.PLACEHOLDER.defaultBlockState().setValue(PlaceholderBlock.TYPE, PlaceholderBlock.Type.OBJECTIVE);
      CommonEvents.OBJECTIVE_PIECE_GENERATION
         .register(this, data -> this.ifPresent(OBJECTIVE_PROBABILITY, probability -> data.setProbability(probability.floatValue())));
      CommonEvents.BLOCK_SET.at(BlockSetEvent.Type.RETURN).in(world).register(this, data -> {
         PartialTile target = PartialTile.of(PartialBlockState.of(ModBlocks.PLACEHOLDER), PartialCompoundNbt.empty());
         target.getState().set(PlaceholderBlock.TYPE, PlaceholderBlock.Type.OBJECTIVE);
         if (target.isSubsetOf(PartialTile.of(data.getState()))) {
            data.getWorld().setBlock(data.getPos(), Blocks.AIR.defaultBlockState(), 3);
            data.getWorld().setBlock(data.getPos().above(), ModBlocks.LODESTONE.defaultBlockState(), 3);
         }
      });
      CommonEvents.BLOCK_USE.in(world).at(BlockUseEvent.Phase.HEAD).of(ModBlocks.LODESTONE).register(this, data -> {
         if (data.getHand() != InteractionHand.MAIN_HAND) {
            data.setResult(InteractionResult.SUCCESS);
         } else if (!this.get(COMPLETED_PLAYERS).contains(data.getPlayer().getUUID())) {
            if (vault.get(Vault.LISTENERS).get(data.getPlayer().getUUID()) instanceof Runner runner && runner.isActive(world, vault, this)) {
               if (world.getBlockEntity(data.getPos()) instanceof LodestoneTileEntity lodestone && !lodestone.isConsumed()) {
                  if (!world.isClientSide) {
                     lodestone.setConsumed(true);
                     world.setBlock(data.getPos(), Blocks.AIR.defaultBlockState(), 3);
                     world.playSound(null, data.getPos(), SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1.0F, 2.0F);
                  }

                  this.playActivationEffects(world, data.getPos());
                  this.get(COMPLETED_PLAYERS).add(data.getPlayer().getUUID());
               }

               data.setResult(InteractionResult.SUCCESS);
            } else {
               data.setResult(InteractionResult.SUCCESS);
            }
         }
      });
   }

   @Override
   public void tickServer(VirtualWorld world, Vault vault) {
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
         Component txt = new TextComponent("Consume a Lodestone!").withStyle(ChatFormatting.WHITE);
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

   protected void playActivationEffects(VirtualWorld world, BlockPos pos) {
      for (int i = 0; i < 20; i++) {
         double d0 = world.random.nextGaussian() * 0.02;
         double d1 = world.random.nextGaussian() * 0.02;
         double d2 = world.random.nextGaussian() * 0.02;
         world.sendParticles(
            ParticleTypes.POOF,
            pos.getX() + world.random.nextDouble() - d0,
            pos.getY() + world.random.nextDouble() - d1,
            pos.getZ() + world.random.nextDouble() - d2,
            10,
            d0,
            d1,
            d2,
            1.0
         );
      }

      world.playSound(null, pos, SoundEvents.CONDUIT_ACTIVATE, SoundSource.BLOCKS, 1.0F, 1.0F);
   }
}
