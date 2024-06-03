package iskallia.vault.core.vault.objective;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.block.entity.HologramTileEntity;
import iskallia.vault.block.entity.hologram.HologramElement;
import iskallia.vault.block.entity.hologram.ItemHologramElement;
import iskallia.vault.block.entity.hologram.RootHologramElement;
import iskallia.vault.core.Version;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.vault.CompoundAdapter;
import iskallia.vault.core.data.key.FieldKey;
import iskallia.vault.core.data.key.SupplierKey;
import iskallia.vault.core.data.key.TemplatePoolKey;
import iskallia.vault.core.data.key.registry.FieldRegistry;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.random.JavaRandom;
import iskallia.vault.core.util.RegionPos;
import iskallia.vault.core.util.WeightedList;
import iskallia.vault.core.vault.ClassicPortalLogic;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.VaultRegistry;
import iskallia.vault.core.vault.WorldManager;
import iskallia.vault.core.world.generator.GridGenerator;
import iskallia.vault.core.world.generator.layout.ArchitectRoomEntry;
import iskallia.vault.core.world.generator.layout.ClassicInfiniteLayout;
import iskallia.vault.core.world.generator.layout.VaultLayout;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.crystal.layout.preset.PoolKeyTemplatePreset;
import iskallia.vault.item.crystal.layout.preset.StructurePreset;
import iskallia.vault.item.data.InscriptionData;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class ArchitectObjective extends Objective {
   public static final SupplierKey<Objective> KEY = SupplierKey.of("architect", Objective.class).with(Version.v1_26, ArchitectObjective::new);
   public static final FieldRegistry FIELDS = Objective.FIELDS.merge(new FieldRegistry());
   public static final FieldKey<ArchitectRoomEntry.List> ROOM_ENTRIES = FieldKey.of("room_entries", ArchitectRoomEntry.List.class)
      .with(Version.v1_26, CompoundAdapter.of(ArchitectRoomEntry.List::new), DISK.all())
      .register(FIELDS);
   public static final FieldKey<StructurePreset> PRESET = FieldKey.of("preset", StructurePreset.class)
      .with(Version.v1_26, Adapters.of(StructurePreset::new, true), DISK.all())
      .register(FIELDS);

   protected ArchitectObjective() {
      this.set(ROOM_ENTRIES, new ArchitectRoomEntry.List());
   }

   public static ArchitectObjective create(Collection<ArchitectRoomEntry> roomEntries) {
      ArchitectObjective objective = new ArchitectObjective();
      objective.get(ROOM_ENTRIES).addAll(roomEntries);
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
      List<TemplatePoolKey> entries = this.get(ROOM_ENTRIES).flatten(null);
      JavaRandom random = JavaRandom.ofScrambled(vault.get(Vault.SEED));
      Collections.shuffle(entries, random.asRandomView());
      if (this.get(PRESET) == null) {
         StructurePreset preset = new StructurePreset();
         Map<Integer, Set<RegionPos>> rings = new LinkedHashMap<>();
         List<Integer> weights = ModConfigs.INSCRIPTION.getRingWeights();
         if (vault.get(Vault.WORLD).get(WorldManager.GENERATOR) instanceof GridGenerator generator
            && generator.get(GridGenerator.LAYOUT) instanceof VaultLayout layout) {
            RegionPos center = RegionPos.of(0, 0, generator.get(GridGenerator.CELL_X), generator.get(GridGenerator.CELL_Z));
            AtomicInteger noHits = new AtomicInteger(0);
            int i = 0;

            while (true) {
               Set<RegionPos> regions = new LinkedHashSet<>();
               AtomicBoolean noHit = new AtomicBoolean(true);
               this.iterateRing(center, i, region -> {
                  if (layout.getType(vault, region) == VaultLayout.PieceType.ROOM) {
                     regions.add(region);
                     noHit.set(false);
                  }
               });
               if (noHit.get()) {
                  noHits.getAndAdd(1);
               } else {
                  noHits.set(0);
               }

               if (noHits.get() >= 3) {
                  break;
               }

               if (!regions.isEmpty()) {
                  rings.put(i, regions);
               }

               if (rings.size() >= weights.size() && rings.values().stream().mapToInt(Set::size).sum() >= entries.size()) {
                  break;
               }

               i++;
            }
         }

         for (int i = entries.size() - 1; i >= 0; i--) {
            TemplatePoolKey entry = entries.get(i);
            WeightedList<Integer> weightedRings = new WeightedList<>();
            List<Integer> ringIndices = new ArrayList<>(rings.keySet());

            for (int j = 0; j < Math.min(rings.size(), weights.size()); j++) {
               weightedRings.add(ringIndices.get(j), weights.get(j));
            }

            weightedRings.getRandom(random).ifPresent(ringIndex -> {
               List<RegionPos> ring = new ArrayList<>(rings.get(ringIndex));
               preset.put(ring.remove(random.nextInt(ring.size())), new PoolKeyTemplatePreset(entry));
               if (ring.isEmpty()) {
                  rings.remove(ringIndex);
               }
            });
            entries.remove(i);
         }

         this.set(PRESET, preset);
      }

      CommonEvents.LAYOUT_TEMPLATE_GENERATION
         .register(
            this,
            data -> {
               if (data.getVault() == vault) {
                  this.ifPresent(
                     PRESET,
                     presetx -> {
                        if (presetx.get(data.getRegion()).orElse(null) instanceof PoolKeyTemplatePreset entry) {
                           TemplatePoolKey key = VaultRegistry.TEMPLATE_POOL.getKey(entry.getPool());
                           if (key == null) {
                              return;
                           }

                           data.setTemplate(
                              data.getLayout()
                                 .getRoom(key.get(vault.get(Vault.VERSION)), vault.get(Vault.VERSION), data.getRegion(), random, data.getSettings())
                           );
                        }
                     }
                  );
               }
            }
         );
      super.initServer(world, vault);
   }

   public void iterateRing(RegionPos center, int distance, Consumer<RegionPos> runnable) {
      for (int i = -distance; i <= distance; i++) {
         runnable.accept(center.with(i, distance));
         runnable.accept(center.with(i, -distance));
         runnable.accept(center.with(distance, i));
         runnable.accept(center.with(-distance, i));
      }
   }

   @Override
   public void tickServer(VirtualWorld world, Vault vault) {
      if (vault.get(Vault.WORLD).get(WorldManager.PORTAL_LOGIC) instanceof ClassicPortalLogic logic) {
         logic.getPlayerStartPos(vault).ifPresent(pos -> {
            pos = pos.below(3).relative(vault.get(Vault.WORLD).get(WorldManager.FACING), 1);
            if (!(world.getBlockEntity(pos) instanceof HologramTileEntity)) {
               world.setBlock(pos, ModBlocks.HOLOGRAM.defaultBlockState(), 3);
               if (world.getBlockEntity(pos) instanceof HologramTileEntity entity) {
                  entity.setTree(this.createHologram(entity, vault, world, pos));
               }
            }
         });
      }

      super.tickServer(world, vault);
   }

   private HologramElement createHologram(HologramTileEntity entity, Vault vault, VirtualWorld world, BlockPos pos) {
      RootHologramElement root = new RootHologramElement(entity)
         .<HologramElement>setTranslation(new Vec3(0.0, -1.5, 0.0))
         .setEulerRotation(new Vec3(90.0, 0.0, 0.0));
      Set<BlockPos> floor = new HashSet<>();
      floor.add(pos);
      this.get(PRESET)
         .getAll()
         .forEach(
            (region, entry) -> {
               if (entry instanceof PoolKeyTemplatePreset preset) {
                  ItemStack inscription = new ItemStack(ModItems.INSCRIPTION);
                  InscriptionData data = InscriptionData.from(inscription);
                  data.setModel(ModConfigs.INSCRIPTION.getModel(preset.getPool()));
                  data.write(inscription);
                  int x = region.getX();
                  int zx = region.getZ();
                  if (vault.get(Vault.WORLD).get(WorldManager.GENERATOR) instanceof GridGenerator generator
                     && generator.get(GridGenerator.LAYOUT) instanceof ClassicInfiniteLayout layout) {
                     x /= layout.get(ClassicInfiniteLayout.TUNNEL_SPAN) + 1;
                     zx /= layout.get(ClassicInfiniteLayout.TUNNEL_SPAN) + 1;
                  }

                  floor.add(pos.offset(x, 0, zx));

                  root.add(
                     new HologramElement[]{
                        new ItemHologramElement(inscription, true, true)
                           .<HologramElement>setTranslation(new Vec3(x, -zx, 0.0))
                           .setEulerRotation(switch ((Direction)vault.get(Vault.WORLD).get(WorldManager.FACING)) {
                              case NORTH -> new Vec3(0.0, 0.0, 180.0);
                              case EAST -> new Vec3(0.0, 0.0, 90.0);
                              case WEST -> new Vec3(0.0, 0.0, -90.0);
                              default -> Vec3.ZERO;
                           })
                     }
                  );
               }
            }
         );
      if (floor.isEmpty()) {
         return root;
      } else {
         int size = 0;

         for (BlockPos p : floor) {
            size = Math.max(size, Math.abs(p.getX() - pos.getX()));
            size = Math.max(size, Math.abs(p.getZ() - pos.getZ()));
         }

         for (int x = -size; x <= size; x++) {
            for (int z = -size; z <= size; z++) {
               BlockPos block = pos.offset(x, 0, z);
               if (floor.contains(block)) {
                  world.setBlock(block.above(1), ModBlocks.VAULT_STONE.defaultBlockState(), 3);
               } else {
                  world.setBlock(block.above(1), ModBlocks.POLISHED_VAULT_STONE.defaultBlockState(), 3);
               }
            }
         }

         world.setBlock(pos.above(1), ModBlocks.CHISELED_VAULT_STONE.defaultBlockState(), 3);
         return root;
      }
   }

   @Override
   public boolean render(Vault vault, PoseStack matrixStack, Window window, float partialTicks, Player player) {
      return false;
   }

   @Override
   public boolean isActive(Vault vault, Objective objective) {
      return objective == this;
   }
}
