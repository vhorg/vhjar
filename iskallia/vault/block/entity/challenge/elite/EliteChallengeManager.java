package iskallia.vault.block.entity.challenge.elite;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import iskallia.vault.VaultMod;
import iskallia.vault.block.challenge.EliteControllerProxyBlock;
import iskallia.vault.block.entity.challenge.ChallengeControllerBlockEntity;
import iskallia.vault.block.entity.challenge.ChallengeManager;
import iskallia.vault.block.entity.challenge.raid.ChallengeActionEntry;
import iskallia.vault.block.entity.challenge.raid.action.ChallengeAction;
import iskallia.vault.client.gui.helper.LightmapHelper;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.array.ArrayAdapter;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.event.common.BlockUseEvent;
import iskallia.vault.core.random.ChunkRandom;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.world.data.entity.PartialEntity;
import iskallia.vault.core.world.storage.BlockCuboid;
import iskallia.vault.core.world.storage.IZonedWorld;
import iskallia.vault.core.world.storage.WorldZone;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.item.crystal.data.serializable.ISerializable;
import iskallia.vault.network.message.AbsorbingParticleMessage;
import iskallia.vault.world.data.ServerVaults;
import iskallia.vault.world.data.WorldZonesData;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;

public class EliteChallengeManager extends ChallengeManager {
   private final Map<BlockPos, EliteChallengeManager.Proxy> proxies = new LinkedHashMap<>();
   private int zoneId;
   private BlockCuboid zone;
   private final EliteSpawner spawner;
   private final EliteAnimation animation;
   private final List<ChallengeAction<?>> actions;
   private final List<ChallengeAction<?>> scheduledActions;
   private BlockPos selected;
   public static ArrayAdapter<ChallengeAction<?>> ACTIONS = Adapters.ofArray(ChallengeAction[]::new, Adapters.RAID_ACTION);
   public static final ResourceLocation HUD = VaultMod.id("textures/gui/challenge/elite/hud.png");

   public EliteChallengeManager() {
      this.spawner = new EliteSpawner();
      this.animation = new EliteAnimation();
      this.actions = new ArrayList<>();
      this.scheduledActions = new ArrayList<>();
   }

   public EliteChallengeManager(UUID uuid, ResourceKey<Level> dimension, BlockPos pos) {
      super(uuid, dimension, pos);
      this.zoneId = -1;
      this.spawner = new EliteSpawner();
      this.animation = new EliteAnimation();
      this.actions = new ArrayList<>();
      this.scheduledActions = new ArrayList<>();
   }

   public Map<BlockPos, EliteChallengeManager.Proxy> getProxies() {
      return this.proxies;
   }

   public BlockCuboid getZone() {
      return this.zone;
   }

   public void setZone(BlockCuboid zone) {
      this.zone = zone;
   }

   public EliteSpawner getSpawner() {
      return this.spawner;
   }

   @Override
   public void onAttach(ServerLevel world) {
      super.onAttach(world);
      CommonEvents.BLOCK_USE.at(BlockUseEvent.Phase.HEAD).of(ModBlocks.ELITE_CONTROLLER_PROXY).register(this, data -> {
         if (data.getHand() != InteractionHand.MAIN_HAND) {
            data.setResult(InteractionResult.SUCCESS);
         } else if (data.getWorld() == world) {
            if (this.proxies.containsKey(data.getPos().subtract(this.pos))) {
               if (data.getWorld().getBlockEntity(data.getPos()) instanceof EliteControllerProxyBlockEntity proxy) {
                  this.onProxyClick(world, proxy);
               }
            }
         }
      });
      CommonEvents.ENTITY_DAMAGE.register(this, EventPriority.HIGHEST, event -> {
         if (world == event.getEntity().level) {
            for (EliteChallengeManager.Proxy proxy : this.proxies.values()) {
               if (event.getEntity().getUUID().equals(proxy.display)) {
                  event.setCanceled(true);
                  return;
               }
            }
         }
      });
   }

   @Override
   public void onClick(ServerLevel world, Player player) {
      if (!(this.selected != null && world.getBlockEntity(this.selected.offset(this.pos)) instanceof EliteControllerProxyBlockEntity entity)) {
         player.displayClientMessage(new TextComponent("Select an elite first.").withStyle(ChatFormatting.RED), true);
      } else if (this.animation.getState() == EliteAnimation.State.PROXIES) {
         this.scheduledActions.addAll(entity.getActions());
         this.animation.onStart(EliteAnimation.State.CLOSE_ROOM);
         super.onClick(world, player);
      }
   }

   @Override
   public void onTick(ServerLevel world) {
      if (!this.isDeleted()) {
         this.refreshPlayers(world);
         ChunkRandom random = ChunkRandom.any();
         random.setDecoratorSeed(ServerVaults.get(world).map(vault -> vault.get(Vault.SEED)).orElse(0L), this.pos.getX(), this.pos.getZ(), 329057345);
         if (this.zoneId < 0 && this.zone != null) {
            this.zoneId = WorldZonesData.get(world.getServer()).getOrCreate(world.dimension()).add(new WorldZone().add(this.zone).setModify(false));
         }

         if (this.animation.getState() == EliteAnimation.State.IDLE) {
            this.summonProxies(world, random);
            this.animation.onStart(EliteAnimation.State.PROXIES);
         }

         if (this.animation.getState() == EliteAnimation.State.CLOSE_ROOM && this.animation.isCompleted()) {
            IZonedWorld.runWithBypass(world, true, () -> {
               for (BlockPos pos : this.proxies.keySet()) {
                  world.destroyBlock(this.pos.offset(pos), false);
               }
            });
            this.spawner.onSpawn(world, this.pos, this.proxies.get(this.selected).elite);
            this.animation.onStart(EliteAnimation.State.FIGHT);
         }

         if (this.animation.getState() == EliteAnimation.State.FIGHT && this.spawner.isCompleted()) {
            this.computeScheduledActions(world, random);
            this.animation.onStart(EliteAnimation.State.OPEN_ROOM);
         }

         if (this.animation.getState() == EliteAnimation.State.OPEN_ROOM && this.animation.isCompleted()) {
            this.setDeleted(true);
         }

         for (EliteChallengeManager.Proxy proxy : this.proxies.values()) {
            Entity entity = world.getEntity(proxy.display);
            if (entity != null) {
               Player player = world.getNearestPlayer(entity.getX(), entity.getY(), entity.getZ(), 48.0, target -> !target.isSpectator());
               if (player != null) {
                  entity.lookAt(Anchor.EYES, player.getEyePosition());
               }
            }
         }

         this.spawner.onTick(world);
         this.animation.onTick(world, this.pos, this.proxies);
      }
   }

   private void summonProxies(ServerLevel world, RandomSource random) {
      this.proxies
         .forEach(
            (offset, proxy) -> {
               Direction facing = Direction.fromNormal(Integer.compare(offset.getX(), 0), Integer.compare(offset.getY(), 0), Integer.compare(offset.getZ(), 0));
               if (facing != null) {
                  BlockPos proxyPos = this.pos.offset(offset);
                  IZonedWorld.runWithBypass(
                     world,
                     true,
                     () -> {
                        world.setBlock(
                           proxyPos,
                           (BlockState)ModBlocks.ELITE_CONTROLLER_PROXY.defaultBlockState().setValue(EliteControllerProxyBlock.FACING, facing.getOpposite()),
                           3
                        );
                        if (proxy.display == null) {
                           EntityType<?> type = (EntityType<?>)ForgeRegistries.ENTITIES.getValue(proxy.elite.getId());
                           BlockPos displayPos = proxyPos.relative(facing, 2).relative(Direction.UP, 1);
                           Entity entityx = type.spawn(world, null, null, null, displayPos, MobSpawnType.SPAWNER, false, false);
                           proxy.elite.getNbt().asWhole().ifPresent(nbt -> entityx.deserializeNBT(nbt));
                           entityx.setPos(displayPos.getX() + 0.5, displayPos.getY(), displayPos.getZ() + 0.5);
                           entityx.setInvulnerable(true);
                           entityx.setSilent(true);
                           if (entityx instanceof Mob mob) {
                              mob.setNoAi(true);
                              mob.setNoGravity(true);
                              mob.setBaby(false);
                              mob.setPersistenceRequired();
                           }

                           proxy.display = entityx.getUUID();
                        }
                     }
                  );
                  if (world.getBlockEntity(proxyPos) instanceof EliteControllerProxyBlockEntity entity) {
                     entity.setActions(
                        ChallengeAction.merge(
                           proxy.entries
                              .stream()
                              .map(entry -> {
                                 if (random.nextFloat() >= entry.getProbability()) {
                                    return null;
                                 } else {
                                    entry = entry.copy();
                                    entry.onPopulate(random);
                                    return entry;
                                 }
                              })
                              .filter(Objects::nonNull)
                              .flatMap(entry -> entry.getActions().stream())
                              .flatMap(action -> action.flatten(random))
                              .peek(action -> action.onPopulate(random))
                              .collect(Collectors.toList())
                        )
                     );
                     entity.setController(this.pos);
                  }

                  world.playSound(null, this.pos, SoundEvents.ANCIENT_DEBRIS_PLACE, SoundSource.BLOCKS, 1.4F, 1.0F);
               }
            }
         );
   }

   public void onProxyClick(ServerLevel world, EliteControllerProxyBlockEntity proxy) {
      if (this.animation.getState() == EliteAnimation.State.PROXIES) {
         ChunkRandom random = ChunkRandom.any();
         random.setDecoratorSeed(ServerVaults.get(world).map(vault -> vault.get(Vault.SEED)).orElse(0L), this.pos.getX(), this.pos.getZ(), 390973451);
         if (this.getBlockEntity(world.getServer()) instanceof ChallengeControllerBlockEntity<?> controller) {
            world.playSound(null, controller.getBlockPos(), SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1.0F, 2.0F);
            world.playSound(null, controller.getBlockPos(), SoundEvents.CONDUIT_ACTIVATE, SoundSource.BLOCKS, 1.0F, 2.0F);
            ModNetwork.CHANNEL
               .send(
                  PacketDistributor.ALL.noArg(),
                  new AbsorbingParticleMessage(
                     new Vec3(proxy.getBlockPos().getX() + 0.5, proxy.getBlockPos().getY(), proxy.getBlockPos().getZ() + 0.5),
                     new Vec3(this.pos.getX() + 0.5, this.pos.getY() + 1.65, this.pos.getZ() + 0.5),
                     controller.getRenderer().getParticleColor()
                  )
               );
            this.selected = proxy.getBlockPos().subtract(this.pos);
         }
      }
   }

   public void refreshPlayers(ServerLevel world) {
      Set<ServerPlayer> toRemove = new HashSet<>();

      for (UUID uuid : this.players) {
         ServerPlayer player = world.getServer().getPlayerList().getPlayer(uuid);
         if (player != null && (player.level != world || !this.zone.grow(-1, -1, -1).contains(player.blockPosition()))) {
            toRemove.add(player);
         }
      }

      for (ServerPlayer player : toRemove) {
         this.removePlayer(player);
      }

      for (ServerPlayer player : world.players()) {
         if (this.zone.grow(-1, -1, -1).contains(player.blockPosition())) {
            this.addPlayer(player);
         }
      }
   }

   @Override
   public void onRemove(MinecraftServer server) {
      if (this.zoneId >= 0) {
         WorldZonesData.get(server).getOrCreate(this.dimension).remove(this.zoneId);
      }

      if (this.getBlockEntity(server) instanceof ChallengeControllerBlockEntity<?> controller) {
         controller.setState(ChallengeControllerBlockEntity.State.DESTROYED);
         controller.getLevel().playSound(null, controller.getBlockPos(), SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1.0F, 2.0F);
         controller.getLevel().playSound(null, controller.getBlockPos(), SoundEvents.GLASS_BREAK, SoundSource.BLOCKS, 1.0F, 2.0F);
         ModNetwork.CHANNEL
            .send(
               PacketDistributor.ALL.noArg(),
               new AbsorbingParticleMessage(
                  new Vec3(controller.getBlockPos().getX(), controller.getBlockPos().getY() + 1.65, controller.getBlockPos().getZ()),
                  new Vec3(this.pos.getX() + 0.5, this.pos.getY() + 1.65, this.pos.getZ() + 0.5),
                  controller.getRenderer().getParticleColor()
               )
            );
      }
   }

   public void computeScheduledActions(ServerLevel world, RandomSource random) {
      List<ChallengeAction<?>> merged = ChallengeAction.merge(this.scheduledActions.stream().map(action -> {
         action = action.copy();
         action.onPopulate(random);
         return action;
      }).flatMap(action -> action.flatten(random)).peek(action -> {
         action.onPopulate(random);
         action.onActivate(world, this, random);
      }).collect(Collectors.toList()));
      ChallengeAction.merge(this.actions, merged);
      this.scheduledActions.clear();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.BLOCK_CUBOID.writeNbt(this.zone).ifPresent(tag -> nbt.put("zone", tag));
         Adapters.INT.writeNbt(Integer.valueOf(this.zoneId)).ifPresent(tag -> nbt.put("zoneId", tag));
         this.spawner.writeNbt().ifPresent(tag -> nbt.put("spawner", tag));
         ListTag proxies = new ListTag();
         this.proxies.forEach((pos, proxy) -> {
            CompoundTag entry = new CompoundTag();
            Adapters.BLOCK_POS.writeNbt(pos).ifPresent(tag -> entry.put("pos", tag));
            proxy.writeNbt().ifPresent(tag -> entry.put("proxy", tag));
            proxies.add(entry);
         });
         nbt.put("proxies", proxies);
         ACTIONS.writeNbt(this.actions.toArray(ChallengeAction[]::new)).ifPresent(tag -> nbt.put("actions", tag));
         ACTIONS.writeNbt(this.scheduledActions.toArray(ChallengeAction[]::new)).ifPresent(tag -> nbt.put("scheduledActions", tag));
         Adapters.BLOCK_POS.writeNbt(this.selected).ifPresent(tag -> nbt.put("selected", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.zone = Adapters.BLOCK_CUBOID.readNbt(nbt.get("zone")).orElse(null);
      this.zoneId = Adapters.INT.readNbt(nbt.get("zoneId")).orElse(-1);
      this.spawner.readNbt(nbt.getCompound("spawner"));
      ListTag proxies = nbt.getList("proxies", 10);
      this.proxies.clear();

      for (int i = 0; i < proxies.size(); i++) {
         CompoundTag entry = proxies.getCompound(i);
         EliteChallengeManager.Proxy proxy = new EliteChallengeManager.Proxy();
         proxy.readNbt(entry.getCompound("proxy"));
         this.proxies.put(Adapters.BLOCK_POS.readNbt(entry.get("pos")).orElseThrow(), proxy);
      }

      this.actions.clear();
      ACTIONS.readNbt(nbt.get("actions")).ifPresent(actions -> this.actions.addAll(Arrays.asList(actions)));
      this.scheduledActions.clear();
      ACTIONS.readNbt(nbt.get("scheduledActions")).ifPresent(actions -> this.scheduledActions.addAll(Arrays.asList(actions)));
      this.selected = Adapters.BLOCK_POS.readNbt(nbt.get("selected")).orElse(null);
   }

   @Override
   public boolean shouldRenderObjectives() {
      return this.spawner.isCompleted();
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public void onRender(PoseStack matrixStack, float partialTicks, Window window) {
      if (!this.spawner.isCompleted()) {
         Component txt = new TextComponent("Defeat the Elite");
         int midX = window.getGuiScaledWidth() / 2;
         matrixStack.pushPose();
         RenderSystem.setShader(GameRenderer::getPositionTexShader);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         int previousTexture = RenderSystem.getShaderTexture(0);
         RenderSystem.setShaderTexture(0, HUD);
         double progress = this.spawner.getCurrentHealth() / this.spawner.getTotalHealth();
         matrixStack.translate(midX - 80, 8.0, 0.0);
         GuiComponent.blit(matrixStack, 0, 0, 0.0F, 0.0F, 200, 26, 200, 50);
         GuiComponent.blit(matrixStack, 0, 8, 0.0F, 28.0F, 15 + (int)(130.0 * progress), 10, 200, 50);
         RenderSystem.setShader(GameRenderer::getPositionTexShader);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.setShaderTexture(0, previousTexture);
         matrixStack.popPose();
         Font font = Minecraft.getInstance().font;
         BufferSource buffer = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
         matrixStack.pushPose();
         matrixStack.scale(0.6F, 0.6F, 0.6F);
         font.drawInBatch(
            txt.getVisualOrderText(),
            midX / 0.6F - font.width(txt) / 2.0F,
            9 + 22,
            -1,
            true,
            matrixStack.last().pose(),
            buffer,
            false,
            0,
            LightmapHelper.getPackedFullbrightCoords()
         );
         buffer.endBatch();
         matrixStack.popPose();
      }
   }

   public static class Proxy implements ISerializable<CompoundTag, JsonObject> {
      public List<ChallengeActionEntry> entries;
      public PartialEntity elite;
      public UUID display;
      public static ArrayAdapter<ChallengeActionEntry> ENTRIES = Adapters.ofArray(ChallengeActionEntry[]::new, Adapters.of(ChallengeActionEntry::new, false));

      public Proxy() {
      }

      public Proxy(List<ChallengeActionEntry> entries, PartialEntity elite) {
         this.entries = entries;
         this.elite = elite;
      }

      @Override
      public Optional<CompoundTag> writeNbt() {
         return Optional.of(new CompoundTag()).map(nbt -> {
            ENTRIES.writeNbt(this.entries.toArray(ChallengeActionEntry[]::new)).ifPresent(tag -> nbt.put("entries", tag));
            Adapters.PARTIAL_ENTITY.writeNbt(this.elite).ifPresent(tag -> nbt.put("elite", tag));
            Adapters.UUID.writeNbt(this.display).ifPresent(tag -> nbt.put("display", tag));
            return (CompoundTag)nbt;
         });
      }

      public void readNbt(CompoundTag nbt) {
         this.entries = Arrays.stream(ENTRIES.readNbt(nbt.get("entries")).orElseThrow()).collect(Collectors.toList());
         this.elite = Adapters.PARTIAL_ENTITY.readNbt(nbt.get("elite")).orElse(null);
         this.display = Adapters.UUID.readNbt(nbt.get("display")).orElse(null);
      }
   }
}
