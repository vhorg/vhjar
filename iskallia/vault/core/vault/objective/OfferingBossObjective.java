package iskallia.vault.core.vault.objective;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.block.PlaceholderBlock;
import iskallia.vault.core.Version;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.key.FieldKey;
import iskallia.vault.core.data.key.SupplierKey;
import iskallia.vault.core.data.key.registry.FieldRegistry;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.event.common.BlockSetEvent;
import iskallia.vault.core.random.JavaRandom;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.objective.scavenger.ScavengeTask;
import iskallia.vault.core.vault.player.Listener;
import iskallia.vault.core.vault.player.Runner;
import iskallia.vault.core.world.data.entity.PartialCompoundNbt;
import iskallia.vault.core.world.data.tile.PartialBlockState;
import iskallia.vault.core.world.data.tile.PartialTile;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.entity.boss.VaultBossEntity;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.OfferingItem;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class OfferingBossObjective extends Objective {
   public static final SupplierKey<Objective> KEY = SupplierKey.of("offering_boss", Objective.class).with(Version.v1_27, OfferingBossObjective::new);
   public static final FieldRegistry FIELDS = Objective.FIELDS.merge(new FieldRegistry());
   public static final FieldKey<Float> OBJECTIVE_PROBABILITY = FieldKey.of("objective_probability", Float.class)
      .with(Version.v1_27, Adapters.FLOAT, DISK.all())
      .register(FIELDS);

   protected OfferingBossObjective() {
      this(1.0F);
   }

   protected OfferingBossObjective(float objectiveProbability) {
      this.set(OBJECTIVE_PROBABILITY, Float.valueOf(objectiveProbability));
   }

   public static OfferingBossObjective of(float objectiveProbability) {
      return new OfferingBossObjective(objectiveProbability);
   }

   @Override
   public SupplierKey<Objective> getKey() {
      return KEY;
   }

   @Override
   public FieldRegistry getFields() {
      return FIELDS;
   }

   public boolean isCompleted() {
      return false;
   }

   @Override
   public void initServer(VirtualWorld world, Vault vault) {
      CommonEvents.OBJECTIVE_PIECE_GENERATION
         .register(this, data -> this.ifPresent(OBJECTIVE_PROBABILITY, probability -> data.setProbability(probability.floatValue())));
      CommonEvents.BLOCK_SET.at(BlockSetEvent.Type.RETURN).in(world).register(this, data -> {
         PartialTile target = PartialTile.of(PartialBlockState.of(ModBlocks.PLACEHOLDER), PartialCompoundNbt.empty());
         target.getState().set(PlaceholderBlock.TYPE, PlaceholderBlock.Type.OBJECTIVE);
         if (target.isSubsetOf(PartialTile.of(data.getState()))) {
            data.getWorld().setBlock(data.getPos(), ModBlocks.OFFERING_PILLAR.defaultBlockState(), 3);
         }
      });
      CommonEvents.ENTITY_DEATH.register(this, event -> {
         if (event.getEntity().level == world) {
            if (event.getEntity() instanceof VaultBossEntity var2x) {
               ;
            }
         }
      });
      CommonEvents.ITEM_SCAVENGE_TASK.register(this, data -> {
         if (data.getWorld() == world) {
            RandomSource random = JavaRandom.ofNanoTime();
            data.getItems().forEach(stack -> {
               if (stack.getItem() == ModItems.OFFERING) {
                  OfferingItem.setModifier(stack, ModConfigs.VAULT_BOSS.getRandomModifier());
                  OfferingItem.setItems(stack, ModConfigs.VAULT_BOSS.getRandomLootItems(data.getVault().get(Vault.LEVEL).get(), random));
               }
            });
         }
      });

      for (ScavengeTask task : ModConfigs.OFFERING_BOSS.getTasks()) {
         task.initServer(world, vault, this);
      }

      super.initServer(world, vault);
   }

   @Override
   public void tickServer(VirtualWorld world, Vault vault) {
      if (this.isCompleted()) {
         super.tickServer(world, vault);
      }
   }

   @Override
   public void tickListener(VirtualWorld world, Vault vault, Listener listener) {
      if (listener.getPriority(this) < 0) {
         listener.addObjective(vault, this);
      }

      if (listener instanceof Runner && this.isCompleted()) {
         super.tickListener(world, vault, listener);
      }
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public boolean render(Vault vault, PoseStack matrixStack, Window window, float partialTicks, Player player) {
      if (this.isCompleted()) {
         boolean rendered = false;

         for (Objective objective : this.get(CHILDREN)) {
            rendered |= objective.render(vault, matrixStack, window, partialTicks, player);
         }

         if (rendered) {
            return true;
         }
      }

      return true;
   }

   @Override
   public boolean isActive(VirtualWorld world, Vault vault, Objective objective) {
      if (!this.isCompleted()) {
         return objective == this;
      } else {
         for (Objective child : this.get(CHILDREN)) {
            if (child.isActive(world, vault, objective)) {
               return true;
            }
         }

         return false;
      }
   }
}
