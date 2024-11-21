package iskallia.vault.block.entity.challenge.raid;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import iskallia.vault.VaultMod;
import iskallia.vault.block.entity.challenge.ChallengeControllerBlockEntity;
import iskallia.vault.block.entity.challenge.ChallengeManager;
import iskallia.vault.block.entity.challenge.raid.action.AddMobsChallengeAction;
import iskallia.vault.block.entity.challenge.raid.action.ChallengeAction;
import iskallia.vault.block.entity.challenge.raid.action.FloatingItemRewardChallengeAction;
import iskallia.vault.block.entity.challenge.raid.action.MobVanillaAttributeChallengeAction;
import iskallia.vault.block.entity.challenge.raid.action.PlayerVanillaAttributeChallengeAction;
import iskallia.vault.block.entity.challenge.raid.action.TileRewardChallengeAction;
import iskallia.vault.client.gui.framework.text.TextAlign;
import iskallia.vault.client.gui.helper.LightmapHelper;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.array.ArrayAdapter;
import iskallia.vault.core.data.adapter.basic.EnumAdapter;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.event.common.BlockUseEvent;
import iskallia.vault.core.random.ChunkRandom;
import iskallia.vault.core.random.JavaRandom;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.world.storage.BlockCuboid;
import iskallia.vault.core.world.storage.IZonedWorld;
import iskallia.vault.core.world.storage.WorldZone;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModKeybinds;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.init.ModSounds;
import iskallia.vault.network.message.AbsorbingParticleMessage;
import iskallia.vault.util.ServerScheduler;
import iskallia.vault.world.data.ServerVaults;
import iskallia.vault.world.data.WorldZonesData;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.PacketDistributor;

public class RaidChallengeManager extends ChallengeManager {
   private final Map<BlockPos, List<RaidActionEntry>> proxies;
   private int zoneId;
   private BlockCuboid zone;
   private final RaidSpawner spawner;
   private final List<ChallengeAction<?>> actions;
   private final List<ChallengeAction<?>> scheduledActions;
   private final RaidAnimation animation;
   private int proxyTicker;
   private RaidChallengeManager.Phase phase;
   public static ArrayAdapter<ChallengeAction<?>> ACTIONS = Adapters.ofArray(ChallengeAction[]::new, Adapters.RAID_ACTION);
   public static ArrayAdapter<ChallengeActionEntry> ENTRIES = Adapters.ofArray(ChallengeActionEntry[]::new, Adapters.of(ChallengeActionEntry::new, false));
   public static EnumAdapter<RaidChallengeManager.Phase> PHASE = Adapters.ofEnum(RaidChallengeManager.Phase.class, EnumAdapter.Mode.NAME);
   public static final ResourceLocation HUD = VaultMod.id("textures/gui/challenge/raid/hud.png");
   public static final int ACTION_COLUMN_WIDTH = 170;

   public RaidChallengeManager() {
      this.proxies = new HashMap<>();
      this.spawner = new RaidSpawner();
      this.actions = new ArrayList<>();
      this.scheduledActions = new ArrayList<>();
      this.animation = new RaidAnimation();
      this.phase = RaidChallengeManager.Phase.IDLE;
   }

   public RaidChallengeManager(UUID uuid, ResourceKey<Level> dimension, BlockPos pos) {
      super(uuid, dimension, pos);
      this.proxies = new LinkedHashMap<>();
      this.zoneId = -1;
      this.spawner = new RaidSpawner();
      this.actions = new ArrayList<>();
      this.scheduledActions = new ArrayList<>();
      this.animation = new RaidAnimation();
      this.phase = RaidChallengeManager.Phase.IDLE;
   }

   public Map<BlockPos, List<RaidActionEntry>> getProxies() {
      return this.proxies;
   }

   public BlockCuboid getZone() {
      return this.zone;
   }

   public void setZone(BlockCuboid zone) {
      this.zone = zone;
   }

   public RaidSpawner getSpawner() {
      return this.spawner;
   }

   public List<ChallengeAction<?>> getActions() {
      return this.actions;
   }

   public void addActions(List<ChallengeAction<?>> actions) {
      this.scheduledActions.addAll(actions);
   }

   public RaidChallengeManager.Phase getPhase() {
      return this.phase;
   }

   public void setPhase(RaidChallengeManager.Phase phase) {
      this.phase = phase;
   }

   @Override
   public void onAttach(ServerLevel world) {
      super.onAttach(world);
      CommonEvents.BLOCK_USE.at(BlockUseEvent.Phase.HEAD).of(ModBlocks.RAID_CONTROLLER_PROXY).register(this, data -> {
         if (data.getHand() != InteractionHand.MAIN_HAND) {
            data.setResult(InteractionResult.SUCCESS);
         } else if (data.getWorld() == world) {
            if (this.proxies.containsKey(data.getPos().subtract(this.pos))) {
               if (data.getWorld().getBlockEntity(data.getPos()) instanceof RaidControllerProxyBlockEntity proxy) {
                  this.onProxyClick(world, data.getPlayer(), proxy);
               }
            }
         }
      });
      this.spawner.onAttach(world, this);
   }

   @Override
   public void onDetach() {
      super.onDetach();
      this.spawner.onDetach();
   }

   @Override
   public boolean addPlayer(Player player) {
      if (!super.addPlayer(player)) {
         return false;
      } else {
         for (ChallengeAction<?> action : this.getActions()) {
            action.onAddPlayer(player);
         }

         return true;
      }
   }

   @Override
   public boolean removePlayer(Player player) {
      if (!super.removePlayer(player)) {
         return false;
      } else {
         for (ChallengeAction<?> action : this.getActions()) {
            action.onRemovePlayer(player);
         }

         return true;
      }
   }

   @Override
   public void onTick(ServerLevel world) {
      if (!this.isDeleted()) {
         ChunkRandom random = ChunkRandom.any();
         random.setDecoratorSeed(
            ServerVaults.get(world).map(vault -> vault.get(Vault.SEED)).orElse(0L), this.pos.getX(), this.pos.getZ(), 329057345 * this.spawner.getWaveCount()
         );
         if (this.zoneId < 0 && this.zone != null) {
            this.zoneId = WorldZonesData.get(world.getServer())
               .getOrCreate(world.dimension())
               .add(new WorldZone().add(this.zone.offset(this.pos)).setModify(false));
         }

         if (this.getBlockEntity(world.getServer()) instanceof ChallengeControllerBlockEntity<?> controller
            && controller.getState() == ChallengeControllerBlockEntity.State.GENERATING
            && this.phase != RaidChallengeManager.Phase.STARTING) {
            this.phase = RaidChallengeManager.Phase.STARTING;
            this.animation.onStart(RaidAnimation.State.CLOSE_ROOM);
         }

         if (this.phase == RaidChallengeManager.Phase.STARTING && this.animation.isCompleted()) {
            if (this.spawner.hasWaveAvailable()) {
               this.phase = RaidChallengeManager.Phase.PROXIES;
               this.proxyTicker = 0;
            } else {
               this.phase = RaidChallengeManager.Phase.COMPLETED;
            }
         }

         if (this.phase == RaidChallengeManager.Phase.PROXIES) {
            this.tickProxySummon(world, this.pos, random);
         }

         if (this.phase == RaidChallengeManager.Phase.IN_WAVE && this.spawner.isWaveCompleted()) {
            this.spawner.onCompleteWave();
            if (this.spawner.hasWaveAvailable()) {
               this.phase = RaidChallengeManager.Phase.PROXIES;
               this.proxyTicker = 0;
            } else {
               this.phase = RaidChallengeManager.Phase.COMPLETED;
            }
         }

         if ((this.phase == RaidChallengeManager.Phase.COMPLETED || this.phase == RaidChallengeManager.Phase.FORFEITED) && this.animation.isCompleted()) {
            if (this.animation.getState() != RaidAnimation.State.OPEN_HATCH && this.animation.getState() != RaidAnimation.State.OPEN_ROOM) {
               this.animation.onStart(RaidAnimation.State.OPEN_ROOM);
            } else if (this.animation.getState() == RaidAnimation.State.OPEN_ROOM && this.phase == RaidChallengeManager.Phase.COMPLETED) {
               this.animation.onStart(RaidAnimation.State.OPEN_HATCH);
            }

            if (this.animation.isCompleted()) {
               this.setDeleted(true);
            }
         }

         this.animation.onTick(world, this.pos);
         this.spawner.onTick(world);
         if (this.getPhase() != RaidChallengeManager.Phase.IDLE
            && this.phase != RaidChallengeManager.Phase.COMPLETED
            && this.phase != RaidChallengeManager.Phase.FORFEITED) {
            for (ServerPlayer player : world.players()) {
               if (this.zone.offset(this.pos).grow(-1, -1, -1).contains(player.blockPosition())) {
                  this.addPlayer(player);
               }
            }
         }

         Set<ServerPlayer> toRemove = new HashSet<>();

         for (UUID uuid : this.players) {
            ServerPlayer playerx = world.getServer().getPlayerList().getPlayer(uuid);
            if (playerx != null && (playerx.level != world || !this.zone.offset(this.pos).grow(-1, -1, -1).contains(playerx.blockPosition()))) {
               toRemove.add(playerx);
            }
         }

         for (ServerPlayer playerx : toRemove) {
            this.removePlayer(playerx);
         }
      }
   }

   @Override
   public void onRemove(MinecraftServer server) {
      if (this.phase == RaidChallengeManager.Phase.COMPLETED && this.zoneId >= 0) {
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

   private void tickProxySummon(ServerLevel world, BlockPos pos, RandomSource random) {
      if (random instanceof JavaRandom rng) {
         rng.setSeed(rng.getSeed() ^ this.proxyTicker ^ this.spawner.getWaveCount());
         rng.nextLong();
      }

      int proxyIndex = this.proxyTicker % 20 == 0 ? this.proxyTicker / 20 : -1;
      if (proxyIndex >= 0 && proxyIndex < this.proxies.size()) {
         BlockPos offset = new ArrayList<>(this.proxies.keySet()).get(proxyIndex);
         this.summonProxy(world, pos, random, offset, this.spawner.getWaveCount());
      }

      this.proxyTicker++;
   }

   private void summonProxy(ServerLevel world, BlockPos pos, RandomSource random, BlockPos offset, int wave) {
      List<RaidActionEntry> entries = this.proxies.get(offset);
      if (entries != null) {
         BlockPos proxyPos = pos.offset(offset);
         IZonedWorld.runWithBypass(world, true, () -> world.setBlock(proxyPos, ModBlocks.RAID_CONTROLLER_PROXY.defaultBlockState(), 11));
         if (world.getBlockEntity(proxyPos) instanceof RaidControllerProxyBlockEntity entity) {
            entity.setActions(
               ChallengeAction.merge(
                  entries.stream()
                     .map(entry -> {
                        if (random.nextFloat() >= entry.getProbability()) {
                           return null;
                        } else if (!entry.matchesWave(wave + 1)) {
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
            entity.setController(pos);
         }

         world.playSound(null, pos, SoundEvents.ANCIENT_DEBRIS_PLACE, SoundSource.BLOCKS, 1.4F, 1.0F);
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

   public void onProxyClick(ServerLevel world, Player player, RaidControllerProxyBlockEntity proxy) {
      if (this.phase == RaidChallengeManager.Phase.PROXIES) {
         ChunkRandom random = ChunkRandom.any();
         random.setDecoratorSeed(
            ServerVaults.get(world).map(vault -> vault.get(Vault.SEED)).orElse(0L), this.pos.getX(), this.pos.getZ(), 390973451 * this.spawner.getWaveCount()
         );
         this.scheduledActions.addAll(proxy.getActions());
         this.computeScheduledActions(world, random);
         IZonedWorld.runWithBypass(world, true, () -> {
            for (BlockPos pos : this.proxies.keySet()) {
               world.destroyBlock(this.pos.offset(pos), false);
            }
         });
         if (this.phase == RaidChallengeManager.Phase.PROXIES) {
            this.spawner.onSpawn(world, this.pos);
            this.phase = RaidChallengeManager.Phase.IN_WAVE;
            ServerScheduler.INSTANCE.schedule(20, () -> world.playSound(null, this.pos, ModSounds.ARENA_HORNS_SFX, SoundSource.BLOCKS, 1.0F, 1.0F));
         }

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
         }
      }
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.BLOCK_CUBOID.writeNbt(this.zone).ifPresent(tag -> nbt.put("zone", tag));
         Adapters.INT.writeNbt(Integer.valueOf(this.zoneId)).ifPresent(tag -> nbt.put("zoneId", tag));
         this.spawner.writeNbt().ifPresent(tag -> nbt.put("spawner", tag));
         ACTIONS.writeNbt(this.actions.toArray(ChallengeAction[]::new)).ifPresent(tag -> nbt.put("actions", tag));
         ACTIONS.writeNbt(this.scheduledActions.toArray(ChallengeAction[]::new)).ifPresent(tag -> nbt.put("scheduledActions", tag));
         ListTag proxies = new ListTag();
         this.proxies.forEach((pos, entries) -> {
            CompoundTag entry = new CompoundTag();
            Adapters.BLOCK_POS.writeNbt(pos).ifPresent(tag -> entry.put("pos", tag));
            ENTRIES.writeNbt(entries.toArray(RaidActionEntry[]::new)).ifPresent(tag -> entry.put("entries", tag));
            proxies.add(entry);
         });
         nbt.put("proxies", proxies);
         PHASE.asNullable().writeNbt(this.phase).ifPresent(tag -> nbt.put("phase", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.zone = Adapters.BLOCK_CUBOID.readNbt(nbt.get("zone")).orElse(null);
      this.zoneId = Adapters.INT.readNbt(nbt.get("zoneId")).orElse(-1);
      this.spawner.readNbt(nbt.getCompound("spawner"));
      this.actions.clear();
      ACTIONS.readNbt(nbt.get("actions")).ifPresent(actions -> this.actions.addAll(Arrays.asList(actions)));
      this.scheduledActions.clear();
      ACTIONS.readNbt(nbt.get("scheduledActions")).ifPresent(actions -> this.scheduledActions.addAll(Arrays.asList(actions)));
      ListTag proxies = nbt.getList("proxies", 10);
      this.proxies.clear();

      for (int i = 0; i < proxies.size(); i++) {
         CompoundTag entry = proxies.getCompound(i);
         this.proxies
            .put(
               Adapters.BLOCK_POS.readNbt(entry.get("pos")).orElseThrow(),
               Arrays.stream(ENTRIES.readNbt(entry.get("entries")).orElseThrow())
                  .filter(e -> e instanceof RaidActionEntry)
                  .map(e -> (RaidActionEntry)e)
                  .collect(Collectors.toList())
            );
      }

      this.phase = PHASE.readNbt(nbt.get("phase")).orElse(RaidChallengeManager.Phase.IDLE);
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public void onRender(PoseStack matrixStack, float partialTicks, Window window) {
      Minecraft minecraft = Minecraft.getInstance();
      if (minecraft.screen == null && ModKeybinds.showRaidInfo.isDown()) {
         this.onTabRender(matrixStack, partialTicks, window, minecraft);
      } else {
         int waveCount = this.spawner.getWaveCount();
         int waveTarget = this.spawner.getWaveTarget();
         Component txt;
         if (waveTarget >= 0 && waveCount >= waveTarget) {
            txt = new TextComponent("").withStyle(ChatFormatting.WHITE).append(new TextComponent("Complete").withStyle(ChatFormatting.WHITE));
         } else {
            txt = new TextComponent("")
               .withStyle(ChatFormatting.WHITE)
               .append(new TextComponent("Wave ").withStyle(ChatFormatting.WHITE))
               .append(
                  new TextComponent(waveCount + 1 + (this.getPhase() != RaidChallengeManager.Phase.IN_WAVE && waveTarget > 0 ? " of " + waveTarget : ""))
                     .withStyle(ChatFormatting.WHITE)
               );
         }

         int midX = window.getGuiScaledWidth() / 2;
         matrixStack.pushPose();
         RenderSystem.setShader(GameRenderer::getPositionTexShader);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         int previousTexture = RenderSystem.getShaderTexture(0);
         RenderSystem.setShaderTexture(0, HUD);
         double progress;
         if (this.getPhase() == RaidChallengeManager.Phase.IN_WAVE) {
            progress = this.spawner.getCurrentHealth() / this.spawner.getTotalHealth();
         } else {
            progress = waveTarget < 0 ? 0.0 : (double)waveCount / waveTarget;
         }

         matrixStack.translate(midX - 80, 8.0, 0.0);
         GuiComponent.blit(matrixStack, 0, 0, 0.0F, 0.0F, 200, 26, 200, 50);
         GuiComponent.blit(matrixStack, 0, 8, 0.0F, 28.0F, 15 + (int)(130.0 * progress), 10, 200, 50);
         RenderSystem.setShader(GameRenderer::getPositionTexShader);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.setShaderTexture(0, previousTexture);
         matrixStack.popPose();
         Font font = minecraft.font;
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

   @OnlyIn(Dist.CLIENT)
   private void onTabRender(PoseStack matrixStack, float partialTicks, Window window, Minecraft minecraft) {
      if (!this.actions.isEmpty()) {
         List<ChallengeAction<?>> mobActions = this.getActions(AddMobsChallengeAction.class);
         List<ChallengeAction<?>> attributeActions = this.getActions(PlayerVanillaAttributeChallengeAction.class, MobVanillaAttributeChallengeAction.class);
         List<ChallengeAction<?>> rewardActions = this.getActions(TileRewardChallengeAction.class, FloatingItemRewardChallengeAction.class);
         if (!mobActions.isEmpty() || !attributeActions.isEmpty() || !rewardActions.isEmpty()) {
            Font font = minecraft.font;
            matrixStack.pushPose();
            Minecraft mc = Minecraft.getInstance();
            if (mc.gui.getTabList().visible) {
               matrixStack.translate(0.0, mc.player.connection.getOnlinePlayers().size() * 9 + 10, 0.0);
            }

            matrixStack.translate(0.0, 6.0, 0.0);
            float scale = 0.6F;
            matrixStack.scale(scale, scale, scale);
            int midX = (int)(window.getGuiScaledWidth() / 2.0 / scale);
            BufferSource buffer = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
            int maxLines = Math.max(mobActions.size(), Math.max(attributeActions.size(), rewardActions.size()));
            GuiComponent.fill(matrixStack, midX - 170 - 85 - 5, -5, midX + 170 + 85 + 5, maxLines * 9 + (maxLines - 1) * 2 + 3, 1711276032);
            renderActionTexts(matrixStack, mobActions, font, midX - 170 - 85, buffer, TextAlign.LEFT, 170);
            renderActionTexts(matrixStack, attributeActions, font, midX - 85, buffer, TextAlign.CENTER, 170);
            renderActionTexts(matrixStack, rewardActions, font, midX + 85, buffer, TextAlign.RIGHT, 170);
            buffer.endBatch();
            matrixStack.popPose();
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   private static void renderActionTexts(
      PoseStack matrixStack, List<ChallengeAction<?>> actions, Font font, int leftX, BufferSource buffer, TextAlign.ITextAlign textAlign, int columnWidth
   ) {
      List<Component> lines = new ArrayList<>();
      int maxWidth = 0;

      for (ChallengeAction<?> action : actions) {
         Component txt = action.getText();
         lines.add(txt);
         maxWidth = Math.max(maxWidth, font.width(txt));
      }

      int y = 0;

      for (Component txt : lines) {
         int x = textAlign.calculateX(0, font.width(txt), columnWidth);
         font.drawInBatch(
            txt.getVisualOrderText(), leftX + x, y, -1, true, matrixStack.last().pose(), buffer, false, 0, LightmapHelper.getPackedFullbrightCoords()
         );
         y += 9 + 2;
      }
   }

   public List<ChallengeAction<?>> getActions(Class<?>... types) {
      List<ChallengeAction<?>> result = new ArrayList<>();

      for (Class<?> type : types) {
         for (ChallengeAction<?> action : this.actions) {
            if (type.isAssignableFrom(action.getClass())) {
               result.add(action);
            }
         }
      }

      return result;
   }

   public static enum Phase {
      IDLE,
      STARTING,
      PROXIES,
      IN_WAVE,
      COMPLETED,
      FORFEITED;
   }
}
