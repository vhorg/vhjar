package iskallia.vault.core.vault.objective;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import iskallia.vault.core.Version;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.key.FieldKey;
import iskallia.vault.core.data.key.SupplierKey;
import iskallia.vault.core.data.key.registry.FieldRegistry;
import iskallia.vault.core.event.ClientEvents;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.random.JavaRandom;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.player.Listener;
import iskallia.vault.core.vault.player.Runner;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.init.ModKeybinds;
import iskallia.vault.task.BingoTask;
import iskallia.vault.task.ProgressConfiguredTask;
import iskallia.vault.task.Task;
import iskallia.vault.task.TaskContext;
import iskallia.vault.task.counter.TargetTaskCounter;
import iskallia.vault.task.renderer.context.BingoRendererContext;
import iskallia.vault.task.source.EntityTaskSource;
import iskallia.vault.task.source.TaskSource;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BingoObjective extends Objective {
   public static final SupplierKey<Objective> KEY = SupplierKey.of("bingo", Objective.class).with(Version.v1_27, BingoObjective::new);
   public static final FieldRegistry FIELDS = Objective.FIELDS.merge(new FieldRegistry());
   public static final FieldKey<Task> TASK = FieldKey.of("task", Task.class)
      .with(Version.v1_27, Adapters.TASK_NBT, DISK.all().or(CLIENT.all()))
      .register(FIELDS);
   public static final FieldKey<TaskSource> TASK_SOURCE = FieldKey.of("task_source", TaskSource.class)
      .with(Version.v1_27, Adapters.TASK_SOURCE_NBT, DISK.all().or(CLIENT.all()))
      .register(FIELDS);
   public static final FieldKey<Integer> JOINED = FieldKey.of("joined", Integer.class)
      .with(Version.v1_27, Adapters.INT_SEGMENTED_3, DISK.all().or(CLIENT.all()))
      .register(FIELDS);

   protected BingoObjective() {
   }

   public static BingoObjective of(BingoTask task) {
      return (BingoObjective)new BingoObjective().set(TASK, task);
   }

   @Override
   public SupplierKey<Objective> getKey() {
      return KEY;
   }

   @Override
   public FieldRegistry getFields() {
      return FIELDS;
   }

   public TaskContext getContext(VirtualWorld world, Vault vault) {
      this.setIfAbsent(TASK_SOURCE, () -> EntityTaskSource.ofUuids(JavaRandom.ofInternal(vault.get(Vault.SEED))));
      return TaskContext.of(this.get(TASK_SOURCE), world.getServer()).setVault(vault);
   }

   public boolean isCompleted() {
      return this.get(TASK) instanceof BingoTask bingo && bingo.areAllCompleted();
   }

   public int getBingos() {
      return this.get(TASK) instanceof BingoTask bingo ? bingo.getCompletedBingos() : 0;
   }

   @Override
   public void initServer(VirtualWorld world, Vault vault) {
      CommonEvents.LISTENER_JOIN.register(this, data -> {
         if (data.getVault() == vault) {
            if (data.getListener() instanceof Runner runner) {
               if (this.get(TASK_SOURCE) instanceof EntityTaskSource entitySource) {
                  entitySource.add(runner.getId());
               }

               this.set(JOINED, Integer.valueOf(this.getOr(JOINED, Integer.valueOf(0)) + 1));
            }
         }
      });
      CommonEvents.LISTENER_LEAVE.register(this, data -> {
         if (data.getVault() == vault) {
            if (data.getListener() instanceof Runner runner) {
               if (this.get(TASK_SOURCE) instanceof EntityTaskSource entitySource) {
                  entitySource.remove(runner.getId());
               }
            }
         }
      });
      this.get(TASK).onAttach(this.getContext(world, vault));
      CommonEvents.GRID_GATEWAY_UPDATE.register(this, data -> {
         if (data.getLevel() == world) {
            data.getEntity().setCompletedBingos(this.getBingos());
         }
      });
      super.initServer(world, vault);
   }

   @Override
   public void tickServer(VirtualWorld world, Vault vault) {
      if (this.getBingos() > 0) {
         super.tickServer(world, vault);
         if (this.isCompleted()) {
            return;
         }
      }

      if (world.getTickCount() % 20 == 0 && this.get(TASK) instanceof BingoTask root) {
         for (int index = 0; index < root.getWidth() * root.getHeight(); index++) {
            if (!root.isCompleted(index)) {
               root.getChild(index).streamSelfAndDescendants(ProgressConfiguredTask.class).forEach(task -> {
                  if (task.getCounter() instanceof TargetTaskCounter<?, ?> counter && counter.isPopulated()) {
                     counter.get("targetPlayerContribution", Adapters.DOUBLE).ifPresent(contribution -> {
                        int additional = Math.max(this.getOr(JOINED, Integer.valueOf(0)) - 1, 0);
                        if (counter.getBaseTarget() instanceof Integer base) {
                           counter.setTarget((int)(base.intValue() + additional * contribution * base.intValue()));
                        } else {
                           if (!(counter.getBaseTarget() instanceof Float base)) {
                              throw new UnsupportedOperationException();
                           }

                           counter.setTarget((float)(base.floatValue() + additional * contribution * base.floatValue()));
                        }
                     });
                  }
               });
            }
         }
      }
   }

   @Override
   public void tickListener(VirtualWorld world, Vault vault, Listener listener) {
      if (listener instanceof Runner && listener.getPriority(this) < 0) {
         listener.addObjective(vault, this);
      }

      if (listener instanceof Runner && this.getBingos() > 0) {
         super.tickListener(world, vault, listener);
      }
   }

   @Override
   public void releaseServer() {
      this.get(TASK).onDetach();
      super.releaseServer();
   }

   public void onScroll(Player player, double delta) {
      if (this.get(TASK) instanceof BingoTask bingo) {
         bingo.progressBingoLine(player.getUUID(), delta < 0.0 ? 1 : -1);
      }
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public void initClient(Vault vault) {
      ClientEvents.MOUSE_SCROLL
         .register(
            vault,
            event -> {
               BingoRendererContext context = new BingoRendererContext(
                  null, 0.0F, MultiBufferSource.immediate(Tesselator.getInstance().getBuilder()), Minecraft.getInstance().font
               );
               if (this.get(TASK).onMouseScrolled(event.getScrollDelta(), context)) {
                  event.setCanceled(true);
               }
            }
         );
      super.initClient(vault);
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public boolean render(Vault vault, PoseStack poseStack, Window window, float partialTicks, Player player) {
      if (this.isCompleted() && (Minecraft.getInstance().screen != null || !ModKeybinds.openBingo.isDown())) {
         boolean rendered = false;

         for (Objective objective : this.get(CHILDREN)) {
            rendered |= objective.render(vault, poseStack, window, partialTicks, player);
         }

         if (rendered) {
            return true;
         }
      }

      BingoRendererContext context = new BingoRendererContext(
         poseStack, partialTicks, MultiBufferSource.immediate(Tesselator.getInstance().getBuilder()), Minecraft.getInstance().font
      );
      this.get(TASK).onRender(context);
      return true;
   }

   @Override
   public boolean isActive(VirtualWorld world, Vault vault, Objective objective) {
      if (this.isCompleted()) {
         for (Objective child : this.get(CHILDREN)) {
            if (child.isActive(world, vault, objective)) {
               return true;
            }
         }

         return false;
      } else {
         if (this.getBingos() > 0) {
            for (Objective childx : this.get(CHILDREN)) {
               if (childx.isActive(world, vault, objective)) {
                  return true;
               }
            }
         }

         return objective == this;
      }
   }
}
