package iskallia.vault.block.entity;

import iskallia.vault.block.SoulAltarBlock;
import iskallia.vault.block.base.FillableAltarBlock;
import iskallia.vault.block.base.FillableAltarTileEntity;
import iskallia.vault.entity.entity.EternalEntity;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.util.CodecUtils;
import iskallia.vault.util.MiscUtils;
import iskallia.vault.world.data.PlayerFavourData;
import iskallia.vault.world.vault.VaultRaid;
import java.awt.Color;
import java.util.Optional;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   bus = Bus.FORGE
)
public class SoulAltarTileEntity extends FillableAltarTileEntity {
   private static final Random rand = new Random();
   private static final float range = 8.0F;
   private static final AABB SEARCH_BOX = new AABB(-8.0, -8.0, -8.0, 8.0, 8.0, 8.0);
   public static final String SOUL_ALTAR_TAG = "the_vault_SoulAltar";
   public static final String SOUL_ALTAR_REF = "the_vault_SoulAltarPos";
   private int ticksExisted = 0;

   public SoulAltarTileEntity(BlockPos pos, BlockState state) {
      super(ModBlocks.SOUL_ALTAR_TILE_ENTITY, pos, state);
   }

   public static void tick(Level level, BlockPos pos, BlockState state, SoulAltarTileEntity tile) {
      FillableAltarTileEntity.tick(level, pos, state, tile);
      if (!level.isClientSide()) {
         tile.ticksExisted++;
         if (tile.ticksExisted % 10 != 0) {
            return;
         }

         level.getEntitiesOfClass(
               LivingEntity.class,
               SEARCH_BOX.move(pos),
               entity -> entity.isAlive() && !entity.isSpectator() && !entity.isInvulnerable() && entity.getType().getCategory() == MobCategory.MONSTER
            )
            .forEach(entity -> {
               if (entity.addTag("the_vault_SoulAltar")) {
                  CodecUtils.writeNBT(BlockPos.CODEC, pos, entity.getPersistentData(), "the_vault_SoulAltarPos");
               }
            });
      }
   }

   @SubscribeEvent
   public static void onLivingUpdate(LivingUpdateEvent event) {
      LivingEntity entity = event.getEntityLiving();
      Level world = entity.getCommandSenderWorld();
      if (!world.isClientSide() && world instanceof ServerLevel sWorld) {
         if (entity.getTags().contains("the_vault_SoulAltar")) {
            CompoundTag tag = entity.getPersistentData();
            if (tag.contains("the_vault_SoulAltarPos")) {
               BlockPos altarRef = CodecUtils.readNBT(BlockPos.CODEC, tag.get("the_vault_SoulAltarPos"), null);
               if (altarRef != null && world.hasChunkAt(altarRef)) {
                  BlockState state = world.getBlockState(altarRef);
                  BlockEntity te = world.getBlockEntity(altarRef);
                  if (te instanceof SoulAltarTileEntity && state.getBlock() instanceof SoulAltarBlock) {
                     ParticleOptions particle = ((SoulAltarBlock)state.getBlock()).getFlameParticle();
                     Vec3 at = MiscUtils.getRandomOffset(entity.getBoundingBox().inflate(0.2F), rand);
                     sWorld.sendParticles(particle, at.x, at.y, at.z, 1, 0.0, 0.0, 0.0, 0.0);
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onEntityDead(LivingDeathEvent event) {
      LivingEntity deadEntity = event.getEntityLiving();
      Level world = deadEntity.getCommandSenderWorld();
      if (!world.isClientSide()) {
         DamageSource src = event.getSource();
         Entity sourceEntity = src.getEntity();
         if (sourceEntity instanceof EternalEntity) {
            sourceEntity = (Entity)((EternalEntity)sourceEntity).getOwner().right().orElse(null);
         }

         if (sourceEntity instanceof ServerPlayer killer) {
            if (deadEntity.getTags().contains("the_vault_SoulAltar")) {
               CompoundTag tag = deadEntity.getPersistentData();
               if (tag.contains("the_vault_SoulAltarPos")) {
                  BlockPos altarRef = CodecUtils.readNBT(BlockPos.CODEC, tag.get("the_vault_SoulAltarPos"), null);
                  if (altarRef != null && world.hasChunkAt(altarRef)) {
                     BlockEntity te = world.getBlockEntity(altarRef);
                     if (te instanceof SoulAltarTileEntity && ((SoulAltarTileEntity)te).initialized() && !((SoulAltarTileEntity)te).isMaxedOut()) {
                        ((SoulAltarTileEntity)te).makeProgress(killer, 1, sPlayer -> {
                           PlayerFavourData data = PlayerFavourData.get(sPlayer.getLevel());
                           if (rand.nextFloat() < FillableAltarBlock.getFavourChance(sPlayer, PlayerFavourData.VaultGodType.MALEVOLENT)) {
                              PlayerFavourData.VaultGodType vg = PlayerFavourData.VaultGodType.MALEVOLENT;
                              if (data.addFavour(sPlayer, vg, 1)) {
                                 data.addFavour(sPlayer, vg.getOther(rand), -1);
                                 FillableAltarBlock.playFavourInfo(sPlayer);
                              }
                           }
                        });
                        te.setChanged();
                     }
                  }
               }
            }
         }
      }
   }

   @Override
   public Component getRequirementName() {
      return new TextComponent("Monster Soul");
   }

   @Override
   public PlayerFavourData.VaultGodType getAssociatedVaultGod() {
      return PlayerFavourData.VaultGodType.MALEVOLENT;
   }

   @Override
   public Component getRequirementUnit() {
      return new TextComponent("kills");
   }

   @Override
   public Color getFillColor() {
      return new Color(-2158319);
   }

   @Override
   protected Optional<Integer> calcMaxProgress(VaultRaid vault) {
      return vault.getProperties().getBase(VaultRaid.LEVEL).map(vaultLevel -> {
         float multiplier = vault.getProperties().getBase(VaultRaid.HOST).map(x$0 -> this.getMaxProgressMultiplier(x$0)).orElse(1.0F);
         int progress = 4 + vaultLevel / 7;
         return Math.round(progress * multiplier);
      });
   }
}
