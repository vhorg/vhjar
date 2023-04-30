package iskallia.vault.core.vault.objective;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.block.ObeliskBlock;
import iskallia.vault.block.PlaceholderBlock;
import iskallia.vault.client.gui.helper.UIHelper;
import iskallia.vault.core.Version;
import iskallia.vault.core.data.DataObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.vault.CompoundAdapter;
import iskallia.vault.core.data.compound.UUIDList;
import iskallia.vault.core.data.key.FieldKey;
import iskallia.vault.core.data.key.SupplierKey;
import iskallia.vault.core.data.key.registry.FieldRegistry;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.event.common.BlockSetEvent;
import iskallia.vault.core.event.common.BlockUseEvent;
import iskallia.vault.core.random.JavaRandom;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.overlay.VaultOverlay;
import iskallia.vault.core.vault.player.Listener;
import iskallia.vault.core.vault.player.Runner;
import iskallia.vault.core.world.data.PartialCompoundNbt;
import iskallia.vault.core.world.data.tile.PartialBlockState;
import iskallia.vault.core.world.data.tile.PartialTile;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.entity.entity.VaultGuardianEntity;
import iskallia.vault.entity.entity.guardian.GuardianType;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModEntities;
import java.util.Arrays;
import java.util.function.IntSupplier;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ObeliskObjective extends Objective {
   public static final SupplierKey<Objective> KEY = SupplierKey.of("obelisk", Objective.class)
      .with(Version.v1_0, LegacyObeliskObjective::new)
      .with(Version.v1_6, ObeliskObjective::new);
   public static final FieldRegistry FIELDS = Objective.FIELDS.merge(new FieldRegistry());
   public static final FieldKey<ObeliskObjective.Wave[]> WAVES = FieldKey.of("waves", ObeliskObjective.Wave[].class)
      .with(Version.v1_6, Adapters.ofArray(ObeliskObjective.Wave[]::new, CompoundAdapter.of(ObeliskObjective.Wave::new)), DISK.all().or(CLIENT.all()))
      .register(FIELDS);
   public static final FieldKey<Float> OBJECTIVE_PROBABILITY = FieldKey.of("objective_probability", Float.class)
      .with(Version.v1_6, Adapters.FLOAT, DISK.all())
      .register(FIELDS);

   protected ObeliskObjective() {
   }

   protected ObeliskObjective(int target, IntSupplier wave, float objectiveProbability) {
      this.set(WAVES, new ObeliskObjective.Wave[target]);

      for (int i = 0; i < ((ObeliskObjective.Wave[])this.get(WAVES)).length; i++) {
         this.get(WAVES)[i] = new ObeliskObjective.Wave(wave.getAsInt());
      }

      this.set(OBJECTIVE_PROBABILITY, Float.valueOf(objectiveProbability));
   }

   public static ObeliskObjective of(int target, IntSupplier wave, float objectiveProbability) {
      return new ObeliskObjective(target, wave, objectiveProbability);
   }

   @Override
   public SupplierKey<Objective> getKey() {
      return KEY;
   }

   @Override
   public FieldRegistry getFields() {
      return FIELDS;
   }

   public boolean hasObelisksLeft() {
      for (ObeliskObjective.Wave wave : this.get(WAVES)) {
         if (!wave.isActive()) {
            return true;
         }
      }

      return false;
   }

   public boolean isCompleted() {
      for (ObeliskObjective.Wave wave : this.get(WAVES)) {
         if (!wave.isCompleted()) {
            return false;
         }
      }

      return true;
   }

   @Override
   public void initServer(VirtualWorld world, Vault vault) {
      CommonEvents.OBJECTIVE_PIECE_GENERATION
         .register(this, data -> this.ifPresent(OBJECTIVE_PROBABILITY, probability -> data.setProbability(probability.floatValue())));
      CommonEvents.BLOCK_USE
         .in(world)
         .at(BlockUseEvent.Phase.HEAD)
         .of(ModBlocks.OBELISK)
         .register(
            this,
            data -> {
               if (data.getHand() != InteractionHand.MAIN_HAND) {
                  data.setResult(InteractionResult.SUCCESS);
               } else if (this.hasObelisksLeft()) {
                  BlockPos pos = data.getPos();
                  if ((Boolean)data.getState().getValue(ObeliskBlock.FILLED)) {
                     data.setResult(InteractionResult.SUCCESS);
                  } else if (data.getState().getValue(ObeliskBlock.HALF) == DoubleBlockHalf.UPPER
                     && world.getBlockState(pos = pos.below()).getBlock() != ModBlocks.OBELISK) {
                     data.setResult(InteractionResult.SUCCESS);
                  } else if (vault.get(Vault.LISTENERS).getObjectivePriority(data.getPlayer().getUUID(), this) != 0) {
                     data.setResult(InteractionResult.SUCCESS);
                  } else {
                     world.setBlock(pos, (BlockState)world.getBlockState(pos).setValue(ObeliskBlock.FILLED, true), 3);
                     world.setBlock(pos.above(), (BlockState)world.getBlockState(pos.above()).setValue(ObeliskBlock.FILLED, true), 3);
                     this.onObeliskActivated(world, vault, pos);
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
                  BlockState lower = (BlockState)((BlockState)ModBlocks.OBELISK.defaultBlockState().setValue(ObeliskBlock.HALF, DoubleBlockHalf.LOWER))
                     .setValue(ObeliskBlock.FILLED, false);
                  BlockState upper = (BlockState)((BlockState)ModBlocks.OBELISK.defaultBlockState().setValue(ObeliskBlock.HALF, DoubleBlockHalf.UPPER))
                     .setValue(ObeliskBlock.FILLED, false);
                  data.getWorld().setBlock(data.getPos(), lower, 3);
                  data.getWorld().setBlock(data.getPos().above(), upper, 3);
               }
            }
         );
      CommonEvents.ENTITY_DEATH.register(this, event -> {
         if (event.getEntity().level == world) {
            for (ObeliskObjective.Wave wave : this.get(WAVES)) {
               if (wave.get(ObeliskObjective.Wave.MOBS).remove(event.getEntity().getUUID())) {
                  wave.modify(ObeliskObjective.Wave.COUNT, x -> x + 1);
               }
            }
         }
      });
      super.initServer(world, vault);
   }

   private void onObeliskActivated(VirtualWorld world, Vault vault, BlockPos pos) {
      this.playActivationEffects(world, pos);
      ObeliskObjective.Wave wave = Arrays.stream(this.get(WAVES)).filter(w -> !w.has(ObeliskObjective.Wave.ACTIVE)).findFirst().orElseThrow();
      wave.set(ObeliskObjective.Wave.ACTIVE);
      RandomSource random = JavaRandom.ofNanoTime();

      for (int i = 0; i < wave.get(ObeliskObjective.Wave.TARGET); i++) {
         wave.get(ObeliskObjective.Wave.MOBS).add(this.doSpawn(world, vault, pos, random).getUUID());
      }
   }

   public LivingEntity doSpawn(VirtualWorld world, Vault vault, BlockPos pos, RandomSource random) {
      double min = 10.0;
      double max = 13.0;
      LivingEntity spawned = null;

      while (spawned == null) {
         double angle = (Math.PI * 2) * random.nextDouble();
         double distance = Math.sqrt(random.nextDouble() * (max * max - min * min) + min * min);
         int x = (int)Math.ceil(distance * Math.cos(angle));
         int z = (int)Math.ceil(distance * Math.sin(angle));
         double xzRadius = Math.sqrt(x * x + z * z);
         double yRange = Math.sqrt(max * max - xzRadius * xzRadius);
         int y = random.nextInt((int)Math.ceil(yRange) * 2 + 1) - (int)Math.ceil(yRange);
         spawned = spawnMob(world, vault, pos.getX() + x, pos.getY() + y, pos.getZ() + z, random);
      }

      return spawned;
   }

   @Nullable
   public static LivingEntity spawnMob(VirtualWorld world, Vault vault, int x, int y, int z, RandomSource random) {
      GuardianType type = ModConfigs.VAULT_GUARDIAN.getType(vault.get(Vault.LEVEL).get(), random);
      VaultGuardianEntity entity = null;
      if (type == GuardianType.BRUISER) {
         entity = (VaultGuardianEntity)ModEntities.BRUISER_GUARDIAN.create(world);
      } else if (type == GuardianType.ARBALIST) {
         entity = (VaultGuardianEntity)ModEntities.ARBALIST_GUARDIAN.create(world);
      }

      entity.setType(type);
      entity.setGlowingTag(true);
      BlockState state = world.getBlockState(new BlockPos(x, y - 1, z));
      if (!state.isValidSpawn(world, new BlockPos(x, y - 1, z), entity.getType())) {
         return null;
      } else {
         AABB entityBox = entity.getType().getAABB(x + 0.5, y, z + 0.5);
         if (!world.noCollision(entityBox)) {
            return null;
         } else {
            entity.moveTo(x + 0.5F, y + 0.2F, z + 0.5F, (float)(random.nextDouble() * 2.0 * Math.PI), 0.0F);
            entity.spawnAnim();
            entity.finalizeSpawn(world, new DifficultyInstance(Difficulty.PEACEFUL, 13000L, 0L, 0.0F), MobSpawnType.STRUCTURE, null, null);
            world.addWithUUID(entity);
            return entity;
         }
      }
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

      int midX = window.getGuiScaledWidth() / 2;
      int gapWidth = 7;
      int itemBoxWidth = 22;
      int iconWidth = 12;
      int iconHeight = 22;
      ObeliskObjective.Wave[] waves = this.get(WAVES);
      int totalWidth = waves.length * itemBoxWidth + (waves.length - 1) * gapWidth;
      int shiftX = -totalWidth / 2;
      matrixStack.pushPose();
      matrixStack.translate(midX + shiftX * 0.7F, 17.0, 0.0);
      matrixStack.scale(0.7F, 0.7F, 0.7F);

      for (ObeliskObjective.Wave wave : waves) {
         matrixStack.pushPose();
         matrixStack.translate(0.0, -itemBoxWidth / 2.0F, 0.0);
         RenderSystem.setShader(GameRenderer::getPositionTexShader);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         int previousTexture = RenderSystem.getShaderTexture(0);
         RenderSystem.setShaderTexture(0, VaultOverlay.VAULT_HUD);
         if (wave.isActive()) {
            GuiComponent.blit(matrixStack, 0, 0, 77.0F, 84.0F, iconWidth, iconHeight, 256, 256);
         } else {
            GuiComponent.blit(matrixStack, 0, 0, 64.0F, 84.0F, iconWidth, iconHeight, 256, 256);
         }

         RenderSystem.setShader(GameRenderer::getPositionTexShader);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.setShaderTexture(0, previousTexture);
         matrixStack.translate(6.0, 24.0, 0.0);
         String requiredText = wave.get(ObeliskObjective.Wave.COUNT) + "/" + wave.get(ObeliskObjective.Wave.TARGET);
         MutableComponent cmp = new TextComponent(requiredText);
         if (wave.isCompleted()) {
            cmp = cmp.withStyle(ChatFormatting.GREEN);
         } else if (wave.isActive()) {
            cmp = cmp.withStyle(ChatFormatting.RED);
         } else {
            cmp = cmp.setStyle(Style.EMPTY.withColor(13948116));
         }

         UIHelper.renderCenteredWrappedText(matrixStack, cmp, 30, 0);
         matrixStack.popPose();
         matrixStack.translate(itemBoxWidth + gapWidth, 0.0, 0.0);
      }

      matrixStack.popPose();
      return true;
   }

   @Override
   public boolean isActive(Vault vault, Objective objective) {
      if (!this.isCompleted()) {
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

      world.playSound(null, pos, SoundEvents.CONDUIT_ACTIVATE, SoundSource.BLOCKS, 1.0F, 1.0F);
   }

   public static class Wave extends DataObject<ObeliskObjective.Wave> {
      public static final FieldRegistry FIELDS = new FieldRegistry();
      public static final FieldKey<Integer> COUNT = FieldKey.of("count", Integer.class)
         .with(Version.v1_6, Adapters.INT_SEGMENTED_3, DISK.all().or(CLIENT.all()))
         .register(FIELDS);
      public static final FieldKey<Integer> TARGET = FieldKey.of("target", Integer.class)
         .with(Version.v1_6, Adapters.INT_SEGMENTED_3, DISK.all().or(CLIENT.all()))
         .register(FIELDS);
      public static final FieldKey<Void> ACTIVE = FieldKey.of("active", Void.class)
         .with(Version.v1_6, Adapters.ofVoid(), DISK.all().or(CLIENT.all()))
         .register(FIELDS);
      public static final FieldKey<UUIDList> MOBS = FieldKey.of("mobs", UUIDList.class)
         .with(Version.v1_6, CompoundAdapter.of(UUIDList::create), DISK.all().or(CLIENT.all()))
         .register(FIELDS);

      public Wave() {
      }

      public Wave(int target) {
         this.set(COUNT, Integer.valueOf(0));
         this.set(TARGET, Integer.valueOf(target));
         this.set(MOBS, UUIDList.create());
      }

      @Override
      public FieldRegistry getFields() {
         return FIELDS;
      }

      public boolean isActive() {
         return this.has(ACTIVE);
      }

      public boolean isCompleted() {
         return this.get(COUNT) >= this.get(TARGET);
      }
   }
}
