package iskallia.vault.core.vault.objective;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.block.CrakePedestalBlock;
import iskallia.vault.block.PlaceholderBlock;
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
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CrakePedestalObjective extends Objective {
   public static final SupplierKey<Objective> KEY = SupplierKey.of("crake_pedestal", Objective.class).with(Version.v1_13, CrakePedestalObjective::new);
   public static final FieldRegistry FIELDS = Objective.FIELDS.merge(new FieldRegistry());
   public static final FieldKey<UUIDList> COMPLETED_PLAYERS = FieldKey.of("completed_players", UUIDList.class)
      .with(Version.v1_13, CompoundAdapter.of(UUIDList::create), DISK.all().or(CLIENT.all()))
      .register(FIELDS);
   public static final FieldKey<Float> OBJECTIVE_PROBABILITY = FieldKey.of("objective_probability", Float.class)
      .with(Version.v1_13, Adapters.FLOAT, DISK.all())
      .register(FIELDS);

   protected CrakePedestalObjective() {
      this.set(COMPLETED_PLAYERS, UUIDList.create());
   }

   public CrakePedestalObjective(float objectiveProbability) {
      this.set(COMPLETED_PLAYERS, UUIDList.create());
      this.set(OBJECTIVE_PROBABILITY, Float.valueOf(objectiveProbability));
   }

   public static CrakePedestalObjective of(float objectiveProbability) {
      return new CrakePedestalObjective(objectiveProbability);
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
            data.getWorld().setBlock(data.getPos(), ModBlocks.CRAKE_COLUMN.defaultBlockState(), 3);
            data.getWorld().setBlock(data.getPos().above(), ModBlocks.CRAKE_PEDESTAL.defaultBlockState(), 3);
         }
      });
      CommonEvents.BLOCK_USE.in(world).at(BlockUseEvent.Phase.HEAD).of(ModBlocks.CRAKE_PEDESTAL).register(this, data -> {
         if (data.getHand() != InteractionHand.MAIN_HAND) {
            data.setResult(InteractionResult.SUCCESS);
         } else if (!(Boolean)data.getState().getValue(CrakePedestalBlock.CONSUMED)) {
            if (!this.get(COMPLETED_PLAYERS).contains(data.getPlayer().getUUID())) {
               if (vault.get(Vault.LISTENERS).get(data.getPlayer().getUUID()) instanceof Runner runner && runner.isActive(world, vault, this)) {
                  data.getWorld().setBlock(data.getPos(), (BlockState)data.getState().setValue(CrakePedestalBlock.CONSUMED, true), 3);
                  this.get(COMPLETED_PLAYERS).add(data.getPlayer().getUUID());
                  data.setResult(InteractionResult.SUCCESS);
               }
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
      if (listener.getPriority(this) < 0) {
         listener.addObjective(vault, this);
      }

      if (this.get(COMPLETED_PLAYERS).contains(listener.get(Listener.ID))) {
         super.tickListener(world, vault, listener);
      }
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public boolean render(Vault vault, PoseStack matrixStack, Window window, float partialTicks, Player player) {
      boolean rendered = false;

      for (Objective objective : this.get(CHILDREN)) {
         rendered |= objective.render(vault, matrixStack, window, partialTicks, player);
      }

      return rendered;
   }

   @Override
   public boolean isActive(VirtualWorld world, Vault vault, Objective objective) {
      return objective == this;
   }
}
