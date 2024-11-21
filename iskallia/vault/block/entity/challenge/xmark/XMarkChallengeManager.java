package iskallia.vault.block.entity.challenge.xmark;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import iskallia.vault.VaultMod;
import iskallia.vault.block.entity.challenge.ChallengeControllerBlockEntity;
import iskallia.vault.block.entity.challenge.ChallengeManager;
import iskallia.vault.block.entity.challenge.raid.action.ChallengeAction;
import iskallia.vault.client.gui.helper.LightmapHelper;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.array.ArrayAdapter;
import iskallia.vault.core.random.ChunkRandom;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.world.storage.BlockCuboid;
import iskallia.vault.core.world.storage.WorldZone;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.AbsorbingParticleMessage;
import iskallia.vault.world.data.ServerVaults;
import iskallia.vault.world.data.WorldZonesData;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.PacketDistributor;

public class XMarkChallengeManager extends ChallengeManager {
   private int zoneId;
   private BlockCuboid zone;
   private double trapChance;
   private final XMarkSpawner spawner;
   private final XMarkAnimation animation;
   private final List<ChallengeAction<?>> actions;
   private final List<ChallengeAction<?>> scheduledActions;
   public static ArrayAdapter<ChallengeAction<?>> ACTIONS = Adapters.ofArray(ChallengeAction[]::new, Adapters.RAID_ACTION);
   public static final ResourceLocation HUD = VaultMod.id("textures/gui/challenge/x-mark/hud.png");

   public XMarkChallengeManager() {
      this.spawner = new XMarkSpawner();
      this.animation = new XMarkAnimation();
      this.actions = new ArrayList<>();
      this.scheduledActions = new ArrayList<>();
   }

   public XMarkChallengeManager(UUID uuid, ResourceKey<Level> dimension, BlockPos pos) {
      super(uuid, dimension, pos);
      this.zoneId = -1;
      this.spawner = new XMarkSpawner();
      this.animation = new XMarkAnimation();
      this.actions = new ArrayList<>();
      this.scheduledActions = new ArrayList<>();
   }

   public BlockCuboid getZone() {
      return this.zone;
   }

   public void setZone(BlockCuboid zone) {
      this.zone = zone;
   }

   public void setTrapChance(double trapChance) {
      this.trapChance = trapChance;
   }

   public void addActions(List<ChallengeAction<?>> actions) {
      this.scheduledActions.addAll(actions);
   }

   public XMarkSpawner getSpawner() {
      return this.spawner;
   }

   @Override
   public void onAttach(ServerLevel world) {
      super.onAttach(world);
      this.spawner.onAttach(world, this);
   }

   @Override
   public void onDetach() {
      super.onDetach();
      this.spawner.onDetach();
   }

   @Override
   public void onTick(ServerLevel world) {
      if (!this.isDeleted()) {
         this.refreshPlayers(world);
         ChunkRandom random = ChunkRandom.any();
         random.setDecoratorSeed(ServerVaults.get(world).map(vault -> vault.get(Vault.SEED)).orElse(0L), this.pos.getX(), this.pos.getZ(), 329057345);
         if (this.getBlockEntity(world.getServer()) instanceof ChallengeControllerBlockEntity<?> controller
            && controller.getState() == ChallengeControllerBlockEntity.State.ACTIVE
            && this.animation.getState() == XMarkAnimation.State.IDLE) {
            if (random.nextDouble() < this.trapChance) {
               if (this.zoneId < 0 && this.zone != null) {
                  this.zoneId = WorldZonesData.get(world.getServer())
                     .getOrCreate(world.dimension())
                     .add(new WorldZone().add(this.zone.offset(this.pos)).setModify(false));
               }

               this.animation.onStart(XMarkAnimation.State.CLOSE_ROOF);
            } else {
               this.animation.onStart(XMarkAnimation.State.OPEN_ROOM_LOOT);
            }
         }

         if (this.animation.getState() == XMarkAnimation.State.CLOSE_ROOF && this.animation.isCompleted()) {
            this.animation.onStart(XMarkAnimation.State.OPEN_ROOM_TRAP);
         }

         if (this.animation.getState() == XMarkAnimation.State.OPEN_ROOM_TRAP && this.animation.isCompleted()) {
            this.animation.onStart(XMarkAnimation.State.FIGHT);
            this.spawner.setActive(true);
         }

         this.spawner.onTick(world, this.pos, this.players);
         if (this.animation.getState() == XMarkAnimation.State.FIGHT && this.spawner.isWaveCompleted() && this.zoneId >= 0) {
            this.spawner.setActive(false);
            this.animation.onStart(XMarkAnimation.State.OPEN_ROOF);
            WorldZonesData.get(world.getServer()).getOrCreate(this.dimension).remove(this.zoneId);
         }

         if ((this.animation.getState() == XMarkAnimation.State.OPEN_ROOM_LOOT || this.animation.getState() == XMarkAnimation.State.OPEN_ROOF)
            && this.animation.isCompleted()) {
            this.computeScheduledActions(world, random);
            this.setDeleted(true);
         }

         this.animation.onTick(world, this.pos);
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

   public void refreshPlayers(ServerLevel world) {
      Set<ServerPlayer> toRemove = new HashSet<>();

      for (UUID uuid : this.players) {
         ServerPlayer player = world.getServer().getPlayerList().getPlayer(uuid);
         if (player != null && (player.level != world || !this.zone.offset(this.pos).grow(-1, -1, -1).contains(player.blockPosition()))) {
            toRemove.add(player);
         }
      }

      for (ServerPlayer player : toRemove) {
         this.removePlayer(player);
      }

      for (ServerPlayer player : world.players()) {
         if (this.zone.offset(this.pos).grow(-1, -1, -1).contains(player.blockPosition())) {
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

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.BLOCK_CUBOID.writeNbt(this.zone).ifPresent(tag -> nbt.put("zone", tag));
         Adapters.INT.writeNbt(Integer.valueOf(this.zoneId)).ifPresent(tag -> nbt.put("zoneId", tag));
         Adapters.DOUBLE.writeNbt(Double.valueOf(this.trapChance)).ifPresent(tag -> nbt.put("trapChance", tag));
         this.spawner.writeNbt().ifPresent(tag -> nbt.put("spawner", tag));
         ACTIONS.writeNbt(this.actions.toArray(ChallengeAction[]::new)).ifPresent(tag -> nbt.put("actions", tag));
         ACTIONS.writeNbt(this.scheduledActions.toArray(ChallengeAction[]::new)).ifPresent(tag -> nbt.put("scheduledActions", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.zone = Adapters.BLOCK_CUBOID.readNbt(nbt.get("zone")).orElse(null);
      this.zoneId = Adapters.INT.readNbt(nbt.get("zoneId")).orElse(-1);
      this.trapChance = Adapters.DOUBLE.readNbt(nbt.get("trapChance")).orElse(0.0);
      this.spawner.readNbt(nbt.getCompound("spawner"));
      this.actions.clear();
      ACTIONS.readNbt(nbt.get("actions")).ifPresent(actions -> this.actions.addAll(Arrays.asList(actions)));
      this.scheduledActions.clear();
      ACTIONS.readNbt(nbt.get("scheduledActions")).ifPresent(actions -> this.scheduledActions.addAll(Arrays.asList(actions)));
   }

   @Override
   public boolean shouldRenderObjectives() {
      return this.spawner.isWaveCompleted();
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public void onRender(PoseStack matrixStack, float partialTicks, Window window) {
      if (!this.spawner.isWaveCompleted()) {
         Component txt = new TextComponent("Clear all Mobs");
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
}
