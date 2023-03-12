package iskallia.vault.core.vault.objective.elixir;

import com.google.gson.annotations.Expose;
import iskallia.vault.core.Version;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.key.FieldKey;
import iskallia.vault.core.data.key.SupplierKey;
import iskallia.vault.core.data.key.registry.FieldRegistry;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.util.WeightedList;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.objective.ElixirObjective;
import iskallia.vault.core.world.roll.IntRoll;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.init.ModConfigs;
import java.util.UUID;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class MobElixirTask extends ElixirTask {
   public static final SupplierKey<ElixirTask> KEY = SupplierKey.of("mob", ElixirTask.class).with(Version.v1_12, MobElixirTask::new);
   public static final FieldRegistry FIELDS = ElixirTask.FIELDS.merge(new FieldRegistry());
   public static final FieldKey<ResourceLocation> GROUP = FieldKey.of("group", ResourceLocation.class)
      .with(Version.v1_12, Adapters.IDENTIFIER, DISK.all())
      .register(FIELDS);

   protected MobElixirTask() {
   }

   public MobElixirTask(int elixir) {
      this.set(ELIXIR, Integer.valueOf(elixir));
   }

   @Override
   public SupplierKey<ElixirTask> getKey() {
      return KEY;
   }

   @Override
   public FieldRegistry getFields() {
      return FIELDS;
   }

   @Override
   public void initServer(VirtualWorld world, Vault vault, ElixirObjective objective, UUID listener) {
      if (this.get(ELIXIR) != 0) {
         CommonEvents.ENTITY_DROPS.register(this, event -> {
            LivingEntity entity = event.getEntityLiving();
            if (entity.level == world) {
               if (event.getSource().getEntity() instanceof Player player) {
                  if (player.getUUID().equals(listener)) {
                     if (ModConfigs.ELIXIR.isEntityInGroup(event.getEntity(), this.get(GROUP))) {
                        this.summonOrbs(world, entity.position(), this.get(ELIXIR));
                        objective.addProgress(listener, this.get(ELIXIR));
                     }
                  }
               }
            }
         });
      }
   }

   @Override
   public void releaseServer() {
      CommonEvents.ENTITY_DROPS.release(this);
   }

   public static class Config extends ElixirTask.Config<MobElixirTask> {
      @Expose
      protected ResourceLocation group;

      public Config(WeightedList<IntRoll> rolls, ResourceLocation group) {
         super(rolls);
         this.group = group;
      }

      protected MobElixirTask create() {
         return new MobElixirTask();
      }

      protected MobElixirTask configure(MobElixirTask task, RandomSource random) {
         task.set(MobElixirTask.GROUP, this.group);
         return super.configure(task, random);
      }
   }
}
