package iskallia.vault.core.vault.objective;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.block.PlaceholderBlock;
import iskallia.vault.block.entity.GateLockTileEntity;
import iskallia.vault.core.Version;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.basic.EnumAdapter;
import iskallia.vault.core.data.key.FieldKey;
import iskallia.vault.core.data.key.SupplierKey;
import iskallia.vault.core.data.key.registry.FieldRegistry;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.random.ChunkRandom;
import iskallia.vault.core.util.RegionPos;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.WorldManager;
import iskallia.vault.core.vault.influence.VaultGod;
import iskallia.vault.core.vault.modifier.VaultModifierStack;
import iskallia.vault.core.world.generator.GridGenerator;
import iskallia.vault.core.world.generator.VaultGenerator;
import iskallia.vault.core.world.generator.layout.ClassicPresetLayout;
import iskallia.vault.core.world.generator.layout.GridLayout;
import iskallia.vault.core.world.loot.generator.LootTableGenerator;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.item.GodBlessingItem;
import iskallia.vault.item.KeystoneItem;
import iskallia.vault.item.crystal.layout.preset.ParadoxTemplatePreset;
import iskallia.vault.item.crystal.layout.preset.PoolTemplatePreset;
import iskallia.vault.item.crystal.layout.preset.TemplatePreset;
import iskallia.vault.util.InventoryUtil;
import iskallia.vault.world.data.ParadoxCrystalData;
import iskallia.vault.world.data.PlayerReputationData;
import java.util.ArrayList;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ParadoxObjective extends Objective {
   public static final SupplierKey<Objective> KEY = SupplierKey.of("divine_paradox", Objective.class).with(Version.v1_19, () -> new ParadoxObjective());
   public static final FieldRegistry FIELDS = Objective.FIELDS.merge(new FieldRegistry());
   public static final FieldKey<ParadoxObjective.Type> TYPE = FieldKey.of("type", ParadoxObjective.Type.class)
      .with(Version.v1_19, Adapters.ofEnum(ParadoxObjective.Type.class, EnumAdapter.Mode.ORDINAL), DISK.all().or(CLIENT.all()))
      .register(FIELDS);
   public static final FieldKey<UUID> PLAYER = FieldKey.of("player", UUID.class)
      .with(Version.v1_19, Adapters.UUID, DISK.all().or(CLIENT.all()))
      .register(FIELDS);
   public static final FieldKey<Long> SEED = FieldKey.of("seed", Long.class).with(Version.v1_19, Adapters.LONG, DISK.all().or(CLIENT.all())).register(FIELDS);

   protected ParadoxObjective() {
   }

   protected ParadoxObjective(ParadoxObjective.Type type, UUID player, long seed) {
      this.set(TYPE, type);
      this.set(PLAYER, player);
      this.set(SEED, Long.valueOf(seed));
   }

   public static ParadoxObjective of(ParadoxObjective.Type type, UUID player, long seed) {
      return new ParadoxObjective(type, player, seed);
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
      CommonEvents.OBJECTIVE_PIECE_GENERATION.register(this, data -> data.setProbability(0.0));
      CommonEvents.LOOT_GENERATION.register(this, data -> {
         if (this.get(TYPE) == ParadoxObjective.Type.BUILD) {
            if (data.getGenerator() instanceof LootTableGenerator generator) {
               if (!(generator.source instanceof ServerPlayer player)) {
                  return;
               }

               if (player.level != world) {
                  return;
               }

               generator.setItems(new ArrayList<>());
            }
         }
      });
      CommonEvents.PLACEHOLDER_PROCESSING
         .register(
            this,
            data -> {
               if (data.getContext().getVault() == vault) {
                  if (data.getTile().getState().get(PlaceholderBlock.TYPE) == PlaceholderBlock.Type.GATE) {
                     Direction direction = ((Direction)data.getTile().getState().get(PlaceholderBlock.FACING)).getOpposite();
                     VaultGenerator generator = vault.get(Vault.WORLD).get(WorldManager.GENERATOR);
                     if (generator instanceof GridGenerator gridGenerator) {
                        ChunkRandom random = ChunkRandom.any();
                        RegionPos region = RegionPos.ofBlockPos(
                              data.getTile().getPos(), gridGenerator.get(GridGenerator.CELL_X), gridGenerator.get(GridGenerator.CELL_Z)
                           )
                           .add(direction, 2);
                        random.setRegionSeed(this.get(PLAYER).getMostSignificantBits(), region.getX(), region.getZ(), this.get(SEED));
                        data.getContext().setRandom(data.getTile().getPos(), random);
                     }
                  }
               }
            }
         );
      CommonEvents.GATE_LOCK_UPDATE
         .register(
            this,
            data -> {
               if (data.getLevel() == world) {
                  Direction direction = data.getEntity().getDirection().getOpposite();
                  VaultGenerator generator = vault.get(Vault.WORLD).get(WorldManager.GENERATOR);
                  if (generator instanceof GridGenerator gridGenerator) {
                     RegionPos region = RegionPos.ofBlockPos(data.getPos(), gridGenerator.get(GridGenerator.CELL_X), gridGenerator.get(GridGenerator.CELL_Z));
                     GridLayout layout = gridGenerator.get(GridGenerator.LAYOUT);
                     if (layout instanceof ClassicPresetLayout presetLayout) {
                        boolean hasTunnel = presetLayout.hasGenerated(region.add(direction, 1));
                        boolean hasRoom = presetLayout.hasGenerated(region.add(direction, 2));
                        GateLockTileEntity.State state = data.getEntity().getState();
                        ChunkRandom random = ChunkRandom.any();
                        random.setBlockSeed(this.get(PLAYER).getMostSignificantBits(), data.getPos(), this.get(SEED));
                        int count = (int)presetLayout.get(ClassicPresetLayout.PRESET)
                              .getAll()
                              .values()
                              .stream()
                              .filter(
                                 templatePreset -> templatePreset instanceof ParadoxTemplatePreset paradoxTemplatePreset
                                    ? paradoxTemplatePreset.getGod() == data.getEntity().getGod()
                                    : false
                              )
                              .count()
                           + 1;
                        data.getEntity().setReputationCost(count);
                        if (!hasTunnel && !hasRoom) {
                           if (state != GateLockTileEntity.State.TUNNEL_AND_ROOM) {
                              data.getEntity().setState(GateLockTileEntity.State.TUNNEL_AND_ROOM);
                              data.getEntity().generateCostAndModifiers(random);
                           }
                        } else if (!hasTunnel) {
                           if (state != GateLockTileEntity.State.TUNNEL) {
                              data.getEntity().setState(GateLockTileEntity.State.TUNNEL);
                              data.getEntity().generateCostAndModifiers(random);
                           }
                        } else {
                           data.getEntity().setStep(GateLockTileEntity.Step.REMOVED);
                        }

                        if (state == GateLockTileEntity.State.TUNNEL_AND_ROOM) {
                           data.getEntity().setReputationCost(count);
                        } else if (state == GateLockTileEntity.State.TUNNEL) {
                           data.getEntity().setReputationCost(0);
                        }
                     }
                  }
               }
            }
         );
      CommonEvents.GATE_LOCK_OPEN
         .register(
            this,
            data -> {
               if (data.getWorld() == world && this.get(TYPE) != ParadoxObjective.Type.RUN) {
                  if (InventoryUtil.consumeInputs(data.getEntity().getCost(), data.getPlayer().getInventory(), true)) {
                     if (PlayerReputationData.getReputation(data.getPlayer().getUUID(), data.getEntity().getGod()) >= data.getEntity().getReputationCost()) {
                        InventoryUtil.consumeInputs(data.getEntity().getCost(), data.getPlayer().getInventory(), false);
                        Direction direction = data.getEntity().getDirection().getOpposite();
                        VaultGenerator generator = vault.get(Vault.WORLD).get(WorldManager.GENERATOR);
                        if (generator instanceof GridGenerator gridGenerator) {
                           RegionPos region = RegionPos.ofBlockPos(
                              data.getPos(), gridGenerator.get(GridGenerator.CELL_X), gridGenerator.get(GridGenerator.CELL_Z)
                           );
                           GridLayout layout = gridGenerator.get(GridGenerator.LAYOUT);
                           if (layout instanceof ClassicPresetLayout presetLayout) {
                              boolean changed = false;
                              if (!presetLayout.hasGenerated(region.add(direction, 1))) {
                                 presetLayout.append(vault, world, region.add(direction, 1), new PoolTemplatePreset(data.getEntity().getTunnel()));
                                 changed = true;
                              }

                              if (!presetLayout.hasGenerated(region.add(direction, 2))) {
                                 presetLayout.append(
                                    vault, world, region.add(direction, 2), new ParadoxTemplatePreset(data.getEntity().getRoom(), data.getEntity().getGod())
                                 );
                                 changed = true;
                              }

                              if (changed) {
                                 ParadoxCrystalData.Entry entry = ParadoxCrystalData.get(world).getOrCreate(this.get(PLAYER));
                                 entry.preset = presetLayout.get(ClassicPresetLayout.PRESET);
                                 entry.mergeModifiers(data.getEntity().getModifiers());
                                 ChunkRandom random = ChunkRandom.any();
                                 random.setBlockSeed(this.get(PLAYER).getLeastSignificantBits(), data.getPos(), this.get(SEED));

                                 for (VaultModifierStack modifier : data.getEntity().getModifiers()) {
                                    vault.get(Vault.MODIFIERS).addModifier(modifier.getModifier(), modifier.getSize(), true, random);
                                 }

                                 entry.changed = true;
                              }
                           }
                        }

                        data.getEntity().setStep(GateLockTileEntity.Step.REMOVED);
                        world.playSound(
                           null, data.getPos().getX(), data.getPos().getY(), data.getPos().getZ(), SoundEvents.NOTE_BLOCK_BELL, SoundSource.PLAYERS, 0.9F, 1.2F
                        );
                     }
                  }
               }
            }
         );
      CommonEvents.ITEM_SCAVENGE_TASK.register(this, data -> {
         if (data.getWorld() == world) {
            VaultGod god = this.getGod(vault, data.getPos());
            if (god != null) {
               data.getItems().removeIf(stack -> stack.getItem() instanceof KeystoneItem keystone && keystone.getGod() != god);
               data.getItems().removeIf(stack -> stack.getItem() instanceof GodBlessingItem && GodBlessingItem.getGod(stack) != god);
            }
         }
      });
      super.initServer(world, vault);
   }

   private VaultGod getGod(Vault vault, BlockPos pos) {
      VaultGenerator generator = vault.get(Vault.WORLD).get(WorldManager.GENERATOR);
      if (generator instanceof GridGenerator gridGenerator) {
         RegionPos region = RegionPos.ofBlockPos(pos, gridGenerator.get(GridGenerator.CELL_X), gridGenerator.get(GridGenerator.CELL_Z));
         GridLayout layout = gridGenerator.get(GridGenerator.LAYOUT);
         if (layout instanceof ClassicPresetLayout presetLayout) {
            TemplatePreset preset = presetLayout.get(ClassicPresetLayout.PRESET).get(region).orElse(null);
            if (preset instanceof ParadoxTemplatePreset paradoxPreset) {
               return paradoxPreset.getGod();
            }

            return null;
         }
      }

      return null;
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
   public boolean isActive(Vault vault, Objective objective) {
      return objective == this;
   }

   public static enum Type {
      BUILD("Build"),
      RUN("Run");

      private final String name;

      private Type(String name) {
         this.name = name;
      }

      public String getName() {
         return this.name;
      }
   }
}
