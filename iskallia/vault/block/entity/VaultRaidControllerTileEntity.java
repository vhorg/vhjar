package iskallia.vault.block.entity;

import iskallia.vault.block.StabilizerBlock;
import iskallia.vault.block.VaultRaidControllerBlock;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModParticles;
import iskallia.vault.util.PlayerFilter;
import iskallia.vault.world.data.VaultRaidData;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.logic.objective.raid.RaidChallengeObjective;
import iskallia.vault.world.vault.logic.objective.raid.modifier.ModifierDoublingModifier;
import iskallia.vault.world.vault.logic.objective.raid.modifier.RaidModifier;
import iskallia.vault.world.vault.modifier.InventoryRestoreModifier;
import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class VaultRaidControllerTileEntity extends TileEntity implements ITickableTileEntity {
   private static final Random rand = new Random();
   private static final AxisAlignedBB RENDER_BOX = new AxisAlignedBB(-1.0, -1.0, -1.0, 1.0, 2.0, 1.0);
   private boolean triggeredRaid = false;
   private int activeTimeout = 0;
   private final LinkedHashMap<String, Float> raidModifiers = new LinkedHashMap<>();
   private final List<Object> particleReferences = new ArrayList<>();

   public VaultRaidControllerTileEntity() {
      super(ModBlocks.RAID_CONTROLLER_TILE_ENTITY);
   }

   public boolean isActive() {
      return this.activeTimeout > 0;
   }

   public void func_73660_a() {
      if (!this.func_145831_w().func_201670_d()) {
         BlockState up = this.func_145831_w().func_180495_p(this.func_174877_v().func_177984_a());
         if (!(up.func_177230_c() instanceof VaultRaidControllerBlock)) {
            this.func_145831_w()
               .func_175656_a(
                  this.func_174877_v().func_177984_a(),
                  (BlockState)ModBlocks.RAID_CONTROLLER_BLOCK.func_176223_P().func_206870_a(StabilizerBlock.HALF, DoubleBlockHalf.UPPER)
               );
         }

         if (this.activeTimeout > 0) {
            this.activeTimeout--;
            if (this.activeTimeout <= 0) {
               this.markForUpdate();
            }
         }

         if (this.func_145831_w() instanceof ServerWorld) {
            ServerWorld sWorld = (ServerWorld)this.func_145831_w();
            VaultRaid vault = VaultRaidData.get(sWorld).getAt(sWorld, this.func_174877_v());
            if (vault != null) {
               if (vault.getActiveRaid() != null && vault.getActiveRaid().getController().equals(this.func_174877_v())) {
                  boolean needsUpdate = this.activeTimeout <= 0;
                  this.activeTimeout = 20;
                  if (needsUpdate) {
                     this.markForUpdate();
                  }
               }

               vault.getActiveObjective(RaidChallengeObjective.class)
                  .ifPresent(
                     raidObjective -> {
                        if (this.raidModifiers.isEmpty()) {
                           boolean cannotGetArtifact = vault.getActiveModifiersFor(PlayerFilter.any(), InventoryRestoreModifier.class)
                              .stream()
                              .anyMatch(InventoryRestoreModifier::preventsArtifact);
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
                     }
                  );
            }
         }
      } else {
         this.setupParticles();
      }
   }

   @OnlyIn(Dist.CLIENT)
   private void setupParticles() {
      if (this.particleReferences.size() < 3) {
         int toAdd = 3 - this.particleReferences.size();

         for (int i = 0; i < toAdd; i++) {
            ParticleManager mgr = Minecraft.func_71410_x().field_71452_i;
            Particle p = mgr.func_199280_a(
               (IParticleData)ModParticles.RAID_EFFECT_CUBE.get(),
               this.field_174879_c.func_177958_n() + 0.5,
               this.field_174879_c.func_177956_o() + 0.5,
               this.field_174879_c.func_177952_p() + 0.5,
               0.0,
               0.0,
               0.0
            );
            this.particleReferences.add(p);
         }
      }

      this.particleReferences.removeIf(ref -> !((Particle)ref).func_187113_k());
      if (this.isActive()) {
         ParticleManager mgr = Minecraft.func_71410_x().field_71452_i;
         Color c = new Color(11932948);
         if (rand.nextInt(3) == 0) {
            Vector3d pPos = new Vector3d(
               this.field_174879_c.func_177958_n() + 0.5 + rand.nextFloat() * 3.5 * (rand.nextBoolean() ? 1 : -1),
               this.field_174879_c.func_177956_o() + 2.1 + rand.nextFloat() * 3.5 * (rand.nextBoolean() ? 1 : -1),
               this.field_174879_c.func_177952_p() + 0.5 + rand.nextFloat() * 3.5 * (rand.nextBoolean() ? 1 : -1)
            );
            SimpleAnimatedParticle fwParticle = (SimpleAnimatedParticle)mgr.func_199280_a(
               ParticleTypes.field_197629_v, pPos.func_82615_a(), pPos.func_82617_b(), pPos.func_82616_c(), 0.0, 0.0, 0.0
            );
            fwParticle.func_70538_b(c.getRed() / 255.0F, c.getGreen() / 255.0F, c.getBlue() / 255.0F);
            fwParticle.field_187149_H = -0.001F;
            fwParticle.func_187114_a(fwParticle.func_206254_h() / 2);
            pPos = new Vector3d(
               this.field_174879_c.func_177958_n() + 0.5 + rand.nextFloat() * 0.3 * (rand.nextBoolean() ? 1 : -1),
               this.field_174879_c.func_177956_o() + 2.25 + rand.nextFloat() * 0.3 * (rand.nextBoolean() ? 1 : -1),
               this.field_174879_c.func_177952_p() + 0.5 + rand.nextFloat() * 0.3 * (rand.nextBoolean() ? 1 : -1)
            );
            fwParticle = (SimpleAnimatedParticle)mgr.func_199280_a(
               ParticleTypes.field_197629_v, pPos.func_82615_a(), pPos.func_82617_b(), pPos.func_82616_c(), 0.0, 0.0, 0.0
            );
            fwParticle.func_70538_b(c.getRed() / 255.0F, c.getGreen() / 255.0F, c.getBlue() / 255.0F);
            fwParticle.field_187149_H = 0.0F;
         }
      }
   }

   private void markForUpdate() {
      if (this.field_145850_b != null) {
         this.field_145850_b.func_184138_a(this.field_174879_c, this.func_195044_w(), this.func_195044_w(), 3);
         this.field_145850_b.func_195593_d(this.field_174879_c, this.func_195044_w().func_177230_c());
         this.func_70296_d();
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

   public List<ITextComponent> getModifierDisplay() {
      return this.raidModifiers.entrySet().stream().map(modifierEntry -> {
         RaidModifier modifier = ModConfigs.RAID_MODIFIER_CONFIG.getByName(modifierEntry.getKey());
         return modifier == null ? null : new Tuple(modifier, modifierEntry.getValue());
      }).filter(Objects::nonNull).map(tpl -> ((RaidModifier)tpl.func_76341_a()).getDisplay((Float)tpl.func_76340_b())).collect(Collectors.toList());
   }

   public void func_230337_a_(BlockState state, CompoundNBT tag) {
      super.func_230337_a_(state, tag);
      this.activeTimeout = tag.func_74762_e("timeout");
      this.triggeredRaid = tag.func_74767_n("triggeredRaid");
      this.raidModifiers.clear();
      ListNBT modifiers = tag.func_150295_c("raidModifiers", 10);

      for (int i = 0; i < modifiers.size(); i++) {
         CompoundNBT modifierTag = modifiers.func_150305_b(i);
         String modifier = modifierTag.func_74779_i("name");
         float value = modifierTag.func_74760_g("value");
         this.raidModifiers.put(modifier, Float.valueOf(value));
      }
   }

   public CompoundNBT func_189515_b(CompoundNBT tag) {
      tag.func_74768_a("timeout", this.activeTimeout);
      tag.func_74757_a("triggeredRaid", this.triggeredRaid);
      ListNBT modifiers = new ListNBT();
      this.raidModifiers.forEach((modifier, value) -> {
         CompoundNBT nbt = new CompoundNBT();
         nbt.func_74778_a("name", modifier);
         nbt.func_74776_a("value", value);
         modifiers.add(nbt);
      });
      tag.func_218657_a("raidModifiers", modifiers);
      return super.func_189515_b(tag);
   }

   public CompoundNBT func_189517_E_() {
      CompoundNBT nbt = super.func_189517_E_();
      this.func_189515_b(nbt);
      return nbt;
   }

   public void handleUpdateTag(BlockState state, CompoundNBT nbt) {
      this.func_230337_a_(state, nbt);
   }

   @Nullable
   public SUpdateTileEntityPacket func_189518_D_() {
      return new SUpdateTileEntityPacket(this.field_174879_c, 1, this.func_189517_E_());
   }

   public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
      CompoundNBT nbt = pkt.func_148857_g();
      this.handleUpdateTag(this.func_195044_w(), nbt);
   }

   public AxisAlignedBB getRenderBoundingBox() {
      return RENDER_BOX.func_186670_a(this.func_174877_v());
   }
}
