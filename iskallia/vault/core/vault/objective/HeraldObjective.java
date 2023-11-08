package iskallia.vault.core.vault.objective;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.VaultMod;
import iskallia.vault.block.HeraldControllerBlock;
import iskallia.vault.block.PlaceholderBlock;
import iskallia.vault.block.VaultChestBlock;
import iskallia.vault.block.item.HeraldTrophyItem;
import iskallia.vault.core.Version;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.basic.EnumAdapter;
import iskallia.vault.core.data.adapter.vault.LegacyBlockPosAdapter;
import iskallia.vault.core.data.key.FieldKey;
import iskallia.vault.core.data.key.SupplierKey;
import iskallia.vault.core.data.key.TemplateKey;
import iskallia.vault.core.data.key.registry.FieldRegistry;
import iskallia.vault.core.event.ClientEvents;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.event.common.BlockSetEvent;
import iskallia.vault.core.event.common.BlockUseEvent;
import iskallia.vault.core.random.ChunkRandom;
import iskallia.vault.core.vault.ClassicPortalLogic;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.VaultRegistry;
import iskallia.vault.core.vault.WorldManager;
import iskallia.vault.core.vault.player.ClassicListenersLogic;
import iskallia.vault.core.vault.player.Listener;
import iskallia.vault.core.vault.player.Listeners;
import iskallia.vault.core.vault.stat.StatCollector;
import iskallia.vault.core.vault.time.TickClock;
import iskallia.vault.core.world.data.entity.PartialCompoundNbt;
import iskallia.vault.core.world.data.tile.PartialBlockState;
import iskallia.vault.core.world.data.tile.PartialTile;
import iskallia.vault.core.world.processor.ProcessorContext;
import iskallia.vault.core.world.processor.tile.TileProcessor;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.core.world.template.JigsawTemplate;
import iskallia.vault.core.world.template.PlacementSettings;
import iskallia.vault.core.world.template.Template;
import iskallia.vault.entity.boss.ArtifactBossEntity;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModDynamicModels;
import iskallia.vault.init.ModEntities;
import iskallia.vault.init.ModSounds;
import iskallia.vault.item.CatalystInhibitorItem;
import iskallia.vault.mixin.AccessorChunkMap;
import iskallia.vault.mixin.AccessorClientLevel;
import iskallia.vault.world.data.DiscoveredModelsData;
import iskallia.vault.world.data.PlayerGreedData;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.EventPriority;
import org.apache.commons.lang3.mutable.MutableObject;

public class HeraldObjective extends Objective {
   private static final ResourceLocation HUD = VaultMod.id("textures/gui/boss/herald_bar.png");
   public static final SupplierKey<Objective> KEY = SupplierKey.of("herald", Objective.class).with(Version.v1_19, () -> new HeraldObjective());
   public static final FieldRegistry FIELDS = Objective.FIELDS.merge(new FieldRegistry());
   public static final FieldKey<UUID> BOSS_ID = FieldKey.of("boss_id", UUID.class)
      .with(Version.v1_19, Adapters.UUID.asNullable(), DISK.all().or(CLIENT.all()))
      .register(FIELDS);
   public static final FieldKey<BlockPos> BOSS_POS = FieldKey.of("boss_pos", BlockPos.class)
      .with(Version.v1_19, LegacyBlockPosAdapter.create().asNullable(), DISK.all())
      .register(FIELDS);
   public static final FieldKey<Direction> BOSS_FACING = FieldKey.of("boss_direction", Direction.class)
      .with(Version.v1_19, Adapters.ofEnum(Direction.class, EnumAdapter.Mode.NAME).asNullable(), DISK.all())
      .register(FIELDS);
   public static final FieldKey<Void> BOSS_DEAD = FieldKey.of("boss_dead", Void.class)
      .with(Version.v1_19, Adapters.ofVoid(), DISK.all().or(CLIENT.all()))
      .register(FIELDS);
   public static final FieldKey<Integer> TIMER = FieldKey.of("timer", Integer.class)
      .with(Version.v1_19, Adapters.INT_SEGMENTED_7.asNullable(), DISK.all().or(CLIENT.all()))
      .register(FIELDS);
   public static final FieldKey<Float> PROGRESS = FieldKey.of("progress", Float.class)
      .with(Version.v1_19, Adapters.FLOAT.asNullable(), DISK.all().or(CLIENT.all()))
      .register(FIELDS);

   protected HeraldObjective() {
   }

   public static HeraldObjective of() {
      return new HeraldObjective();
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
      CommonEvents.PLAYER_ACTION.register(this, data -> {
         if (data.getWorld() == world && data.getGameMode() == GameType.ADVENTURE) {
            if (this.isWhitelisted(data.getWorld().getBlockState(data.getPos()))) {
               data.setRestricted(false);
            }
         }
      });
      CommonEvents.PLAYER_MINE.register(this, EventPriority.HIGHEST, true, event -> {
         if (event.getWorld() == world) {
            if (this.isWhitelisted(event.getState())) {
               event.setCanceled(false);
            }
         }
      });
      CommonEvents.ENTITY_DEATH
         .register(
            this,
            event -> {
               if (event.getEntity().getUUID().equals(this.get(BOSS_ID))) {
                  if (event.getEntity().level != world) {
                     return;
                  }

                  this.set(BOSS_DEAD);
                  this.set(TIMER, Integer.valueOf(224));
                  int time = vault.get(Vault.CLOCK).get(TickClock.DISPLAY_TIME);

                  for (Listener listener : vault.get(Vault.LISTENERS).getAll()) {
                     vault.get(Vault.STATS)
                        .get(listener.getId())
                        .get(StatCollector.REWARD)
                        .add(
                           HeraldTrophyItem.create(
                              listener.getId(),
                              listener.getPlayer().map(serverPlayer -> serverPlayer.getGameProfile().getName()).orElse(null),
                              ModConfigs.HERALD_TROPHY.getTrophy(time),
                              time
                           )
                        );
                     listener.getPlayer()
                        .ifPresent(player -> DiscoveredModelsData.get(world).discoverAllArmorPieceAndBroadcast(player, ModDynamicModels.Armor.VICTORY));
                     PlayerGreedData.get(world.getServer()).onHeraldCompleted(listener.getId());
                  }
               }
            }
         );
      CommonEvents.BLOCK_USE
         .in(world)
         .at(BlockUseEvent.Phase.HEAD)
         .of(ModBlocks.HERALD_CONTROLLER)
         .register(
            this,
            data -> {
               if (data.getHand() != InteractionHand.MAIN_HAND) {
                  data.setResult(InteractionResult.SUCCESS);
               } else {
                  BlockPos pos = data.getPos();
                  if ((Boolean)data.getState().getValue(HeraldControllerBlock.FILLED)) {
                     data.setResult(InteractionResult.SUCCESS);
                  } else if (data.getState().getValue(HeraldControllerBlock.HALF) == DoubleBlockHalf.UPPER
                     && world.getBlockState(pos = pos.below()).getBlock() != ModBlocks.HERALD_CONTROLLER) {
                     data.setResult(InteractionResult.SUCCESS);
                  } else {
                     this.set(TIMER, Integer.valueOf(168));
                     this.set(BOSS_POS, pos);
                     this.set(BOSS_FACING, (Direction)data.getState().getValue(HeraldControllerBlock.FACING));
                     this.set(PROGRESS, Float.valueOf(1.0F));
                     world.setBlock(pos, (BlockState)world.getBlockState(pos).setValue(HeraldControllerBlock.FILLED, true), 3);
                     world.setBlock(pos.above(), (BlockState)world.getBlockState(pos.above()).setValue(HeraldControllerBlock.FILLED, true), 3);
                     this.playActivationEffects(world, data.getPos());
                     BlockPos finalPos = pos;
                     world.getServer()
                        .tell(
                           new TickTask(
                              world.getServer().getTickCount() + 1,
                              () -> {
                                 world.getChunkSource()
                                    .chunkMap
                                    .getPlayers(new ChunkPos(finalPos), false)
                                    .forEach(
                                       player -> {
                                          world.getChunk((new ChunkPos(finalPos)).x, (new ChunkPos(finalPos)).z, ChunkStatus.FULL, true);
                                          ((AccessorChunkMap)world.getChunkSource().chunkMap)
                                             .callUpdateChunkTracking(player, new ChunkPos(finalPos), new MutableObject(), false, true);
                                       }
                                    );
                                 world.getChunkSource().blockChanged(finalPos);
                              }
                           )
                        );
                     data.setResult(InteractionResult.SUCCESS);
                  }
               }
            }
         );
      CommonEvents.BLOCK_SET
         .at(BlockSetEvent.Type.RETURN)
         .in(world)
         .register(
            this,
            data -> {
               PartialTile target = PartialTile.of(PartialBlockState.of(ModBlocks.PLACEHOLDER), PartialCompoundNbt.empty());
               target.getState().set(PlaceholderBlock.TYPE, PlaceholderBlock.Type.OBJECTIVE);
               if (target.isSubsetOf(PartialTile.of(data.getState()))) {
                  BlockState lower = (BlockState)((BlockState)((BlockState)ModBlocks.HERALD_CONTROLLER
                           .defaultBlockState()
                           .setValue(HeraldControllerBlock.HALF, DoubleBlockHalf.LOWER))
                        .setValue(HeraldControllerBlock.FACING, (Direction)data.getState().getValue(PlaceholderBlock.FACING)))
                     .setValue(HeraldControllerBlock.FILLED, false);
                  BlockState upper = (BlockState)((BlockState)((BlockState)ModBlocks.HERALD_CONTROLLER
                           .defaultBlockState()
                           .setValue(HeraldControllerBlock.HALF, DoubleBlockHalf.UPPER))
                        .setValue(HeraldControllerBlock.FACING, (Direction)data.getState().getValue(PlaceholderBlock.FACING)))
                     .setValue(HeraldControllerBlock.FILLED, false);
                  data.getWorld().setBlock(data.getPos(), lower, 3);
                  data.getWorld().setBlock(data.getPos().above(), upper, 3);
               }
            }
         );
      CommonEvents.CHEST_LOOT_GENERATION.post().register(this, data -> {
         if (data.getPlayer().level == world) {
            data.getLoot().forEach(stack -> {
               if (stack.getItem() instanceof CatalystInhibitorItem inhibitorItem) {
                  inhibitorItem.setVaultId(stack, vault.get(Vault.ID));
               }
            });
         }
      });
      super.initServer(world, vault);
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

      world.playSound(null, pos, SoundEvents.CONDUIT_ACTIVATE, SoundSource.MASTER, 1.0F, 1.0F);
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public void initClient(Vault vault) {
      CommonEvents.PLAYER_ACTION.register(vault, data -> {
         ClientLevel world = Minecraft.getInstance().level;
         if (world != null && world.dimension().location().equals(vault.get(Vault.WORLD).get(WorldManager.KEY))) {
            if (this.isWhitelisted(data.getWorld().getBlockState(data.getPos()))) {
               data.setRestricted(false);
            }
         }
      });
      ClientEvents.CLIENT_TICK
         .register(
            vault,
            EventPriority.HIGH,
            data -> HeraldMusicHandler.tick(!this.has(BOSS_DEAD) && this.getOr(TIMER, Integer.valueOf(0)) < 0 || this.has(BOSS_DEAD))
         );
      super.initClient(vault);
   }

   @Override
   public void tickServer(VirtualWorld world, Vault vault) {
      if (this.has(TIMER)) {
         if (this.get(TIMER) < 0) {
            if (!this.has(BOSS_DEAD) && !this.has(BOSS_ID)) {
               this.summonBoss(world, vault);
            } else if (this.has(BOSS_DEAD)) {
               this.spawnPortal(world, vault);
               this.remove(TIMER);
            }
         } else {
            this.tickDoor(world, vault);
         }

         if (this.has(TIMER)) {
            this.modify(TIMER, timer -> timer - 1);
         }
      }

      if (this.has(PROGRESS)) {
         this.getBoss(world).flatMap(ArtifactBossEntity::getCurrentStage).ifPresent(stage -> {
            float progress = stage.getProgress();
            if (this.get(PROGRESS) != stage.getProgress()) {
               this.set(PROGRESS, Float.valueOf(stage.getProgress()));
            }
         });
      }

      if (this.has(BOSS_DEAD)) {
         super.tickServer(world, vault);
      }
   }

   private Optional<ArtifactBossEntity> getBoss(Level world) {
      return world instanceof VirtualWorld virtualWorld && virtualWorld.getEntity(this.get(BOSS_ID)) instanceof ArtifactBossEntity boss
         ? Optional.of(boss)
         : Optional.empty();
   }

   private void spawnPortal(VirtualWorld world, Vault vault) {
      BlockPos offset = new BlockPos(0, -3, 0).offset((Vec3i)this.get(BOSS_POS)).relative(this.get(BOSS_FACING), -120);
      MinecraftServer server = world.getServer();
      server.tell(
         new TickTask(
            server.getTickCount() + 1,
            () -> {
               TemplateKey key = VaultRegistry.TEMPLATE.getKey(VaultMod.id("boss_dimension/boss_exit_portal1"));
               if (key != null) {
                  Version version = vault.get(Vault.VERSION);
                  Template template = key.get(version);
                  if (template != null) {
                     ChunkRandom random = ChunkRandom.any();
                     random.setDecoratorSeed(vault.get(Vault.SEED), this.get(BOSS_POS).getX(), this.get(BOSS_POS).getZ(), 329057345);
                     Template var16 = JigsawTemplate.of(version, template, new ArrayList<>(), 10, random);
                     PlacementSettings settings = new PlacementSettings(new ProcessorContext(vault, random)).setFlags(3);
                     settings.addProcessors(TileProcessor.translate(new BlockPos(-6, 0, 0)));
                     if (this.get(BOSS_FACING) != Direction.SOUTH) {
                        if (this.get(BOSS_FACING) == Direction.NORTH) {
                           settings.addProcessors(TileProcessor.rotate(Rotation.CLOCKWISE_180, 0, 0, true));
                        } else if (this.get(BOSS_FACING) == Direction.EAST) {
                           settings.addProcessors(TileProcessor.rotate(Rotation.COUNTERCLOCKWISE_90, 0, 0, true));
                        } else if (this.get(BOSS_FACING) == Direction.WEST) {
                           settings.addProcessors(TileProcessor.rotate(Rotation.CLOCKWISE_90, 0, 0, true));
                        }
                     }

                     settings.addProcessors(TileProcessor.translate(offset));
                     AtomicReference<BoundingBox> pointer = new AtomicReference<>(null);
                     settings.addProcessor(TileProcessor.of((_tile, context) -> {
                        pointer.getAndUpdate(
                           value -> value == null ? BoundingBox.fromCorners(_tile.getPos(), _tile.getPos()) : value.encapsulate(_tile.getPos())
                        );
                        return _tile;
                     }));
                     var16.place(world, settings);
                     List<ResourceLocation> tags = new ArrayList<>();
                     tags.add(ClassicPortalLogic.EXIT);
                     vault.get(Vault.WORLD).get(WorldManager.PORTAL_LOGIC).addPortal(var16, settings, tags);
                     BoundingBox box = pointer.get();
                     ServerChunkCache source = world.getChunkSource();

                     for (int x = box.minX(); x < box.maxX(); x += x + 16 < box.maxX() ? 16 : 16 - Math.floorMod(x, 16)) {
                        for (int z = box.minZ(); z < box.maxZ(); z += z + 16 < box.maxZ() ? 16 : 16 - Math.floorMod(z, 16)) {
                           ChunkPos chunkPos = new ChunkPos(x >> 4, z >> 4);
                           world.getServer()
                              .tell(new TickTask(world.getServer().getTickCount() + 1, () -> source.chunkMap.getPlayers(chunkPos, false).forEach(player -> {
                                 world.getChunk(chunkPos.x, chunkPos.z, ChunkStatus.FULL, true);
                                 ((AccessorChunkMap)source.chunkMap).callUpdateChunkTracking(player, chunkPos, new MutableObject(), false, true);
                              })));
                        }
                     }
                  }
               }
            }
         )
      );
   }

   private void summonBoss(VirtualWorld world, Vault vault) {
      this.playActivationEffects(world, this.get(BOSS_POS));
      world.setBlock(this.get(BOSS_POS), Blocks.AIR.defaultBlockState(), 3);
      world.setBlock(this.get(BOSS_POS).above(), Blocks.AIR.defaultBlockState(), 3);
      ArtifactBossEntity boss = new ArtifactBossEntity(ModEntities.ARTIFACT_BOSS, world);
      boss.setPos(this.get(BOSS_POS).getX() + 0.5, this.get(BOSS_POS).getY(), this.get(BOSS_POS).getZ() + 0.5);
      boss.setYRot(this.get(BOSS_FACING).toYRot());
      boss.yRotO = this.get(BOSS_FACING).toYRot();
      boss.yHeadRotO = this.get(BOSS_FACING).toYRot();
      boss.yHeadRot = this.get(BOSS_FACING).toYRot();
      boss.yBodyRot = this.get(BOSS_FACING).toYRot();
      boss.yBodyRotO = this.get(BOSS_FACING).toYRot();
      boss.spawnPosition = boss.position();
      world.addFreshEntity(boss);
      this.set(BOSS_ID, boss.getUUID());
      if (vault.get(Vault.LISTENERS).get(Listeners.LOGIC) instanceof ClassicListenersLogic classic) {
         classic.set(ClassicListenersLogic.MAX_PLAYERS, Integer.valueOf(0));
      }
   }

   private void tickDoor(VirtualWorld world, Vault vault) {
      int timer = this.get(TIMER);
      ResourceLocation frame = null;
      BlockPos offset = null;
      if (!this.has(BOSS_DEAD)) {
         float frameTime = timer / 6.0F;
         if (frameTime != (int)frameTime) {
            return;
         }

         frame = VaultMod.id("boss_dimension/arena_gate_frame" + (int)frameTime);
         offset = new BlockPos(0, -4, 0).offset((Vec3i)this.get(BOSS_POS)).relative(this.get(BOSS_FACING), 66);
         if (timer == 168) {
            world.getServer()
               .getPlayerList()
               .broadcast(
                  null,
                  offset.getX(),
                  offset.getY(),
                  offset.getZ(),
                  120.0,
                  world.dimension(),
                  new ClientboundSoundPacket(ModSounds.GATE_CLOSE, SoundSource.MASTER, offset.getX(), offset.getY(), offset.getZ(), 0.5F, 1.0F)
               );
         }
      } else {
         float frameTimex = timer / 8.0F;
         if (frameTimex != (int)frameTimex) {
            return;
         }

         frame = VaultMod.id("boss_dimension/arena_gate_frame" + (27 - (int)frameTimex));
         offset = new BlockPos(0, -4, 0).offset((Vec3i)this.get(BOSS_POS)).relative(this.get(BOSS_FACING), -66);
         if (timer == 224) {
            world.getServer()
               .getPlayerList()
               .broadcast(
                  null,
                  offset.getX(),
                  offset.getY(),
                  offset.getZ(),
                  120.0,
                  world.dimension(),
                  new ClientboundSoundPacket(ModSounds.GATE_OPEN, SoundSource.MASTER, offset.getX(), offset.getY(), offset.getZ(), 0.5F, 1.0F)
               );
         }
      }

      this.generateFrame(world, vault, frame, offset);
   }

   private void generateFrame(VirtualWorld world, Vault vault, ResourceLocation frame, BlockPos offset) {
      MinecraftServer server = world.getServer();
      server.tell(
         new TickTask(
            server.getTickCount() + 1,
            () -> {
               TemplateKey key = VaultRegistry.TEMPLATE.getKey(frame);
               if (key != null) {
                  Version version = vault.get(Vault.VERSION);
                  Template template = key.get(version);
                  if (template != null) {
                     ChunkRandom random = ChunkRandom.any();
                     random.setDecoratorSeed(vault.get(Vault.SEED), this.get(BOSS_POS).getX(), this.get(BOSS_POS).getZ(), 329057345);
                     Template var16 = JigsawTemplate.of(version, template, new ArrayList<>(), 10, random);
                     PlacementSettings settings = new PlacementSettings(new ProcessorContext(vault, random)).setFlags(3);
                     settings.addProcessors(TileProcessor.translate(new BlockPos(-5, 1, -1)));
                     if (this.get(BOSS_FACING) != Direction.SOUTH) {
                        if (this.get(BOSS_FACING) == Direction.NORTH) {
                           settings.addProcessors(TileProcessor.rotate(Rotation.CLOCKWISE_180, 0, 0, true));
                        } else if (this.get(BOSS_FACING) == Direction.EAST) {
                           settings.addProcessors(TileProcessor.rotate(Rotation.COUNTERCLOCKWISE_90, 0, 0, true));
                        } else if (this.get(BOSS_FACING) == Direction.WEST) {
                           settings.addProcessors(TileProcessor.rotate(Rotation.CLOCKWISE_90, 0, 0, true));
                        }
                     }

                     settings.addProcessors(TileProcessor.translate(offset));
                     AtomicReference<BoundingBox> pointer = new AtomicReference<>(null);
                     settings.addProcessor(TileProcessor.of((_tile, context) -> {
                        pointer.getAndUpdate(
                           value -> value == null ? BoundingBox.fromCorners(_tile.getPos(), _tile.getPos()) : value.encapsulate(_tile.getPos())
                        );
                        return _tile;
                     }));
                     var16.place(world, settings);
                     BoundingBox box = pointer.get();
                     ServerChunkCache source = world.getChunkSource();

                     for (int x = box.minX(); x < box.maxX(); x += x + 16 < box.maxX() ? 16 : 16 - Math.floorMod(x, 16)) {
                        for (int z = box.minZ(); z < box.maxZ(); z += z + 16 < box.maxZ() ? 16 : 16 - Math.floorMod(z, 16)) {
                           ChunkPos chunkPos = new ChunkPos(x >> 4, z >> 4);
                           world.getServer()
                              .tell(new TickTask(world.getServer().getTickCount() + 1, () -> source.chunkMap.getPlayers(chunkPos, false).forEach(player -> {
                                 world.getChunk(chunkPos.x, chunkPos.z, ChunkStatus.FULL, true);
                                 ((AccessorChunkMap)source.chunkMap).callUpdateChunkTracking(player, chunkPos, new MutableObject(), false, true);
                              })));
                        }
                     }

                     world.getServer()
                        .getPlayerList()
                        .broadcast(
                           null,
                           offset.getX(),
                           offset.getY(),
                           offset.getZ(),
                           120.0,
                           world.dimension(),
                           new ClientboundSoundPacket(SoundEvents.IRON_DOOR_CLOSE, SoundSource.MASTER, offset.getX(), offset.getY(), offset.getZ(), 0.5F, 1.5F)
                        );
                  }
               }
            }
         )
      );
   }

   @Override
   public void tickListener(VirtualWorld world, Vault vault, Listener listener) {
      if (listener.getPriority(this) < 0) {
         listener.addObjective(vault, this);
      }

      if (this.has(BOSS_DEAD)) {
         super.tickListener(world, vault, listener);
      }
   }

   public boolean isWhitelisted(BlockState state) {
      return state.getBlock() instanceof VaultChestBlock || state.getBlock() == ModBlocks.SPARK;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public boolean render(Vault vault, PoseStack matrixStack, Window window, float partialTicks, Player player) {
      if (!this.has(BOSS_DEAD) && this.has(PROGRESS)) {
         this.renderBossBar(matrixStack, window, this.get(PROGRESS), this.get(BOSS_ID));
      }

      return true;
   }

   @OnlyIn(Dist.CLIENT)
   private void renderBossBar(PoseStack matrixStack, Window window, float progress, UUID bossId) {
      if (((AccessorClientLevel)Minecraft.getInstance().level).getEntityStorage().get(bossId) instanceof ArtifactBossEntity boss) {
         boss.getCurrentStage().ifPresent(currentStage -> {
            matrixStack.pushPose();
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            int previousTexture = RenderSystem.getShaderTexture(0);
            RenderSystem.setShaderTexture(0, HUD);
            int midX = window.getGuiScaledWidth() / 2;
            matrixStack.translate(midX - 95, 4.0, 0.0);
            GuiComponent.blit(matrixStack, 4, 22, 4.0F, 308.0F, 180, 5, 189, 368);
            GuiComponent.blit(matrixStack, 4, 22, 4.0F, ((Integer)currentStage.getBossBarTextureVs().getB()).intValue(), (int)(180.0F * progress), 5, 189, 368);
            GuiComponent.blit(matrixStack, 0, 0, 0.0F, ((Integer)currentStage.getBossBarTextureVs().getA()).intValue(), 189, 37, 189, 368);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, previousTexture);
            matrixStack.popPose();
         });
      }
   }

   @Override
   public boolean isActive(Vault vault, Objective objective) {
      if (!this.has(BOSS_DEAD)) {
         return objective == this;
      } else {
         for (Objective child : this.get(CHILDREN)) {
            if (child.isActive(vault, objective)) {
               return true;
            }
         }

         return false;
      }
   }
}
