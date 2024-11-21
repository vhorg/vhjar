package iskallia.vault.task;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.array.ArrayAdapter;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.core.random.ChunkRandom;
import iskallia.vault.core.util.RegionPos;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.WorldManager;
import iskallia.vault.core.world.generator.GridGenerator;
import iskallia.vault.core.world.generator.layout.VaultGridLayout;
import iskallia.vault.core.world.generator.layout.VaultLayout;
import iskallia.vault.core.world.processor.ProcessorContext;
import iskallia.vault.core.world.template.JigsawTemplate;
import iskallia.vault.core.world.template.PlacementSettings;
import iskallia.vault.core.world.template.Template;
import iskallia.vault.task.counter.TaskCounter;
import iskallia.vault.task.source.EntityTaskSource;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class FindVaultRoomTask extends ProgressConfiguredTask<Integer, FindVaultRoomTask.Config> {
   private Set<BlockPos> found = new HashSet<>();
   public static ArrayAdapter<BlockPos> POSITIONS = Adapters.ofArray(BlockPos[]::new, Adapters.BLOCK_POS);

   public FindVaultRoomTask() {
      super(new FindVaultRoomTask.Config(), TaskCounter.Adapter.INT);
   }

   public FindVaultRoomTask(FindVaultRoomTask.Config config, TaskCounter<Integer, ?> counter) {
      super(config, counter, TaskCounter.Adapter.INT);
   }

   @Override
   public void onAttach(TaskContext context) {
      super.onAttach(context);
      CommonEvents.LISTENER_TICK
         .register(
            this,
            data -> {
               if (this.parent == null || this.parent.hasActiveChildren()) {
                  if (context.getSource() instanceof EntityTaskSource entitySource) {
                     if (entitySource.matches(data.getListener().getId())) {
                        ServerPlayer player = data.getListener().getPlayer().orElse(null);
                        if (player != null && player.getLevel() == data.getWorld()) {
                           if (data.getVault().get(Vault.WORLD).get(WorldManager.GENERATOR) instanceof GridGenerator generator) {
                              if (generator.get(GridGenerator.LAYOUT) instanceof VaultGridLayout layout) {
                                 RegionPos var18 = RegionPos.ofBlockPos(
                                    player.blockPosition(), generator.get(GridGenerator.CELL_X), generator.get(GridGenerator.CELL_Z)
                                 );
                                 if (!this.found.contains(new BlockPos(var18.getX(), 0, var18.getZ()))) {
                                    ChunkRandom random = ChunkRandom.any();
                                    random.setRegionSeed(data.getVault().get(Vault.SEED), var18.getX(), var18.getZ(), 1234567890L);
                                    PlacementSettings settings = new PlacementSettings(new ProcessorContext(data.getVault(), random)).setFlags(272);
                                    VaultLayout.PieceType type = layout.getType(data.getVault(), var18);
                                    if (type == VaultLayout.PieceType.ROOM) {
                                       Template template = layout.getAt(data.getVault(), var18, random, settings);
                                       if (template instanceof JigsawTemplate jigsaw) {
                                          template = jigsaw.getRoot();
                                       }

                                       if (this.getConfig().getFilter() == null) {
                                          this.found.add(new BlockPos(var18.getX(), 0, var18.getZ()));
                                          this.getCounter().onAdd(1, context);
                                       } else {
                                          for (ResourceLocation id : this.getConfig().getFilter()) {
                                             if (template != null && id.equals(template.getKey().getId())) {
                                                this.found.add(new BlockPos(var18.getX(), 0, var18.getZ()));
                                                this.getCounter().onAdd(1, context);
                                                break;
                                             }
                                          }
                                       }
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         );
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      POSITIONS.writeBits(this.found.toArray(BlockPos[]::new), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.found = new HashSet<>(Arrays.asList(POSITIONS.readBits(buffer).orElse(new BlockPos[0])));
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         POSITIONS.writeNbt(this.found.toArray(BlockPos[]::new)).ifPresent(tag -> nbt.put("found", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.found = new HashSet<>(Arrays.asList(POSITIONS.readNbt(nbt.get("found")).orElse(new BlockPos[0])));
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         POSITIONS.writeJson(this.found.toArray(BlockPos[]::new)).ifPresent(tag -> json.add("found", tag));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.found = new HashSet<>(Arrays.asList(POSITIONS.readJson(json.get("found")).orElse(new BlockPos[0])));
   }

   public static class Config extends ConfiguredTask.Config {
      private ResourceLocation[] filter = new ResourceLocation[0];
      public static final ArrayAdapter<ResourceLocation> TEMPLATES = Adapters.<ResourceLocation>ofArray(ResourceLocation[]::new, Adapters.IDENTIFIER)
         .asNullable();

      public ResourceLocation[] getFilter() {
         return this.filter;
      }

      @Override
      public void writeBits(BitBuffer buffer) {
         super.writeBits(buffer);
         TEMPLATES.writeBits(this.filter, buffer);
      }

      @Override
      public void readBits(BitBuffer buffer) {
         super.readBits(buffer);
         this.filter = TEMPLATES.readBits(buffer).orElse(null);
      }

      @Override
      public Optional<CompoundTag> writeNbt() {
         return super.writeNbt().map(nbt -> {
            TEMPLATES.writeNbt(this.filter).ifPresent(value -> nbt.put("filter", value));
            return (CompoundTag)nbt;
         });
      }

      @Override
      public void readNbt(CompoundTag nbt) {
         super.readNbt(nbt);
         this.filter = TEMPLATES.readNbt(nbt.get("filter")).orElse(null);
      }

      @Override
      public Optional<JsonObject> writeJson() {
         return super.writeJson().map(json -> {
            TEMPLATES.writeJson(this.filter).ifPresent(value -> json.add("filter", value));
            return (JsonObject)json;
         });
      }

      @Override
      public void readJson(JsonObject json) {
         super.readJson(json);
         this.filter = TEMPLATES.readJson(json.get("filter")).orElse(null);
      }
   }
}
