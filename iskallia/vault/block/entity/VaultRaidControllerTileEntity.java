package iskallia.vault.block.entity;

import iskallia.vault.block.StabilizerBlock;
import iskallia.vault.block.VaultRaidControllerBlock;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModParticles;
import iskallia.vault.util.PlayerFilter;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.logic.objective.raid.modifier.ModifierDoublingModifier;
import iskallia.vault.world.vault.logic.objective.raid.modifier.RaidModifier;
import iskallia.vault.world.vault.modifier.modifier.PlayerInventoryRestoreModifier;
import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class VaultRaidControllerTileEntity extends BlockEntity {
   private static final Random rand = new Random();
   private static final AABB RENDER_BOX = new AABB(-1.0, -1.0, -1.0, 1.0, 2.0, 1.0);
   private boolean triggeredRaid = false;
   private int activeTimeout = 0;
   private final LinkedHashMap<String, Float> raidModifiers = new LinkedHashMap<>();
   private final List<Object> particleReferences = new ArrayList<>();

   public VaultRaidControllerTileEntity(BlockPos pos, BlockState state) {
      super(ModBlocks.RAID_CONTROLLER_TILE_ENTITY, pos, state);
   }

   public boolean isActive() {
      return this.activeTimeout > 0;
   }

   public static void tick(Level level, BlockPos pos, BlockState state, VaultRaidControllerTileEntity tile) {
      if (level instanceof ServerLevel serverLevel) {
         BlockState up = level.getBlockState(pos.above());
         if (!(up.getBlock() instanceof VaultRaidControllerBlock)) {
            level.setBlockAndUpdate(
               pos.above(), (BlockState)ModBlocks.RAID_CONTROLLER_BLOCK.defaultBlockState().setValue(StabilizerBlock.HALF, DoubleBlockHalf.UPPER)
            );
         }

         if (tile.activeTimeout > 0) {
            tile.activeTimeout--;
            if (tile.activeTimeout <= 0) {
               tile.markForUpdate();
            }
         }
      } else {
         tile.setupParticles();
      }
   }

   private void generateModifiers(VaultRaid vault) {
      boolean cannotGetArtifact = vault.hasActiveModifierFor(PlayerFilter.any(), PlayerInventoryRestoreModifier.class, m -> m.properties().preventsArtifact());
      int level = vault.getProperties().getBase(VaultRaid.LEVEL).orElse(0);
      RaidModifier addedModifier = ModConfigs.RAID_MODIFIER_CONFIG.getRandomModifier(level, true, cannotGetArtifact).map(modifier -> {
         RaidModifier mod = modifier.getModifier();
         if (mod != null) {
            this.raidModifiers.put(mod.getName(), Float.valueOf(modifier.getRandomValue()));
         }

         return mod;
      }).orElse(null);
      if (addedModifier != null && !(addedModifier instanceof ModifierDoublingModifier)) {
         ModConfigs.RAID_MODIFIER_CONFIG.getRandomModifier(level, false, cannotGetArtifact).ifPresent(modifier -> {
            RaidModifier mod = modifier.getModifier();
            if (mod != null) {
               this.raidModifiers.put(mod.getName(), Float.valueOf(modifier.getRandomValue()));
            }
         });
      }

      this.markForUpdate();
   }

   private void generateModifiersFinal(VaultRaid vault) {
      boolean cannotGetArtifact = vault.hasActiveModifierFor(PlayerFilter.any(), PlayerInventoryRestoreModifier.class, m -> m.properties().preventsArtifact());
      int level = vault.getProperties().getBase(VaultRaid.LEVEL).orElse(0);
      ModConfigs.FINAL_RAID_MODIFIER_CONFIG.getRandomModifier(level, cannotGetArtifact).ifPresent(modifier -> {
         RaidModifier mod = modifier.getModifier();
         if (mod != null) {
            this.raidModifiers.put(mod.getName(), Float.valueOf(modifier.getRandomValue()));
         }
      });
      this.markForUpdate();
   }

   @OnlyIn(Dist.CLIENT)
   private void setupParticles() {
      if (this.particleReferences.size() < 3) {
         int toAdd = 3 - this.particleReferences.size();

         for (int i = 0; i < toAdd; i++) {
            ParticleEngine mgr = Minecraft.getInstance().particleEngine;
            Particle p = mgr.createParticle(
               (ParticleOptions)ModParticles.RAID_EFFECT_CUBE.get(),
               this.worldPosition.getX() + 0.5,
               this.worldPosition.getY() + 0.5,
               this.worldPosition.getZ() + 0.5,
               0.0,
               0.0,
               0.0
            );
            this.particleReferences.add(p);
         }
      }

      this.particleReferences.removeIf(ref -> !((Particle)ref).isAlive());
      if (this.isActive()) {
         ParticleEngine mgr = Minecraft.getInstance().particleEngine;
         Color c = new Color(11932948);
         if (rand.nextInt(3) == 0) {
            Vec3 pPos = new Vec3(
               this.worldPosition.getX() + 0.5 + rand.nextFloat() * 3.5 * (rand.nextBoolean() ? 1 : -1),
               this.worldPosition.getY() + 2.1 + rand.nextFloat() * 3.5 * (rand.nextBoolean() ? 1 : -1),
               this.worldPosition.getZ() + 0.5 + rand.nextFloat() * 3.5 * (rand.nextBoolean() ? 1 : -1)
            );
            SimpleAnimatedParticle fwParticle = (SimpleAnimatedParticle)mgr.createParticle(ParticleTypes.FIREWORK, pPos.x(), pPos.y(), pPos.z(), 0.0, 0.0, 0.0);
            fwParticle.setColor(c.getRed() / 255.0F, c.getGreen() / 255.0F, c.getBlue() / 255.0F);
            fwParticle.setLifetime(fwParticle.getLifetime() / 2);
            pPos = new Vec3(
               this.worldPosition.getX() + 0.5 + rand.nextFloat() * 0.3 * (rand.nextBoolean() ? 1 : -1),
               this.worldPosition.getY() + 2.25 + rand.nextFloat() * 0.3 * (rand.nextBoolean() ? 1 : -1),
               this.worldPosition.getZ() + 0.5 + rand.nextFloat() * 0.3 * (rand.nextBoolean() ? 1 : -1)
            );
            fwParticle = (SimpleAnimatedParticle)mgr.createParticle(ParticleTypes.FIREWORK, pPos.x(), pPos.y(), pPos.z(), 0.0, 0.0, 0.0);
            fwParticle.setColor(c.getRed() / 255.0F, c.getGreen() / 255.0F, c.getBlue() / 255.0F);
         }
      }
   }

   private void markForUpdate() {
      if (this.level != null) {
         this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
         this.level.updateNeighborsAt(this.worldPosition, this.getBlockState().getBlock());
         this.setChanged();
      }
   }

   public boolean didTriggerRaid() {
      return this.triggeredRaid;
   }

   public void setTriggeredRaid(boolean triggeredRaid) {
      this.triggeredRaid = triggeredRaid;
      this.markForUpdate();
   }

   public LinkedHashMap<String, Float> getRaidModifiers() {
      return this.raidModifiers;
   }

   public List<Component> getModifierDisplay() {
      return this.raidModifiers.entrySet().stream().map(modifierEntry -> {
         RaidModifier modifier = ModConfigs.RAID_MODIFIER_CONFIG.getByName(modifierEntry.getKey());
         return modifier == null ? null : new Tuple(modifier, modifierEntry.getValue());
      }).filter(Objects::nonNull).map(tpl -> ((RaidModifier)tpl.getA()).getDisplay((Float)tpl.getB())).collect(Collectors.toList());
   }

   public void load(CompoundTag pTag) {
      super.load(pTag);
      this.activeTimeout = pTag.getInt("timeout");
      this.triggeredRaid = pTag.getBoolean("triggeredRaid");
      this.raidModifiers.clear();
      ListTag modifiers = pTag.getList("raidModifiers", 10);

      for (int i = 0; i < modifiers.size(); i++) {
         CompoundTag modifierTag = modifiers.getCompound(i);
         String modifier = modifierTag.getString("name");
         float value = modifierTag.getFloat("value");
         this.raidModifiers.put(modifier, Float.valueOf(value));
      }
   }

   protected void saveAdditional(CompoundTag pTag) {
      super.saveAdditional(pTag);
      pTag.putInt("timeout", this.activeTimeout);
      pTag.putBoolean("triggeredRaid", this.triggeredRaid);
      ListTag modifiers = new ListTag();
      this.raidModifiers.forEach((modifier, value) -> {
         CompoundTag nbt = new CompoundTag();
         nbt.putString("name", modifier);
         nbt.putFloat("value", value);
         modifiers.add(nbt);
      });
      pTag.put("raidModifiers", modifiers);
   }

   public CompoundTag getUpdateTag() {
      return this.saveWithoutMetadata();
   }

   @Nullable
   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public AABB getRenderBoundingBox() {
      return RENDER_BOX.move(this.getBlockPos());
   }
}
