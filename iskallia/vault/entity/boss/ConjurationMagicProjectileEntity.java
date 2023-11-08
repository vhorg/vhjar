package iskallia.vault.entity.boss;

import iskallia.vault.block.VaultChestBlock;
import iskallia.vault.core.util.WeightedList;
import iskallia.vault.core.vault.influence.VaultGod;
import iskallia.vault.core.vault.stat.VaultChestType;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModEntities;
import iskallia.vault.mixin.AccessorChunkMap;
import java.util.Collections;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.EvokerFangs;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.phys.BlockHitResult;
import org.apache.commons.lang3.mutable.MutableObject;

public class ConjurationMagicProjectileEntity extends MagicProjectileEntity {
   private float chestChance;
   private float fangsChance;
   private float fangsDamage;
   private WeightedList<CatalystStageAttributes.EffectAttributes> effects = WeightedList.empty();
   private Map<VaultChestType, ResourceLocation> chestLootTables = Collections.emptyMap();

   protected ConjurationMagicProjectileEntity(
      Level level, ArtifactBossEntity boss, double x, double y, double z, Player target, CatalystStageAttributes attributes
   ) {
      super(ModEntities.CONJURATION_MAGIC_PROJECTILE, level, boss, x, y, z, target, attributes.getProjectileDamage());
      this.chestChance = attributes.getChestChance();
      this.fangsChance = attributes.getFangsChance();
      this.fangsDamage = attributes.getFangsDamage();
      this.effects = attributes.getEffects();
      this.chestLootTables = attributes.getChestLootTables();
   }

   public ConjurationMagicProjectileEntity(EntityType<? extends AbstractHurtingProjectile> entityType, Level level) {
      super(entityType, level);
   }

   protected void onHitBlock(BlockHitResult hitResult) {
      if (hitResult.getDirection() == Direction.UP) {
         if (this.level.random.nextFloat() < this.chestChance) {
            this.placeChest(hitResult);
         } else if (this.level.random.nextFloat() < this.fangsChance) {
            this.summonFangs(hitResult);
         } else {
            this.summonAreaEffectCloud(hitResult);
         }
      }
   }

   private void placeChest(BlockHitResult hitResult) {
      BlockPos chestPos = hitResult.getBlockPos().above();
      Block chestBlock = this.getRandomChestBlock();
      this.level.setBlockAndUpdate(chestPos, chestBlock.defaultBlockState());
      this.level
         .getBlockEntity(chestPos, ModBlocks.VAULT_CHEST_TILE_ENTITY)
         .ifPresent(chest -> chest.setLootTable(this.getChestLoottable(chestBlock), this.level.random.nextLong()));
      this.level
         .getServer()
         .tell(
            new TickTask(
               this.level.getServer().getTickCount() + 1,
               () -> {
                  ((ServerChunkCache)this.level.getChunkSource())
                     .chunkMap
                     .getPlayers(new ChunkPos(chestPos), false)
                     .forEach(
                        player -> {
                           this.level.getChunk((new ChunkPos(chestPos)).x, (new ChunkPos(chestPos)).z, ChunkStatus.FULL, true);
                           ((AccessorChunkMap)((ServerChunkCache)this.level.getChunkSource()).chunkMap)
                              .callUpdateChunkTracking(player, new ChunkPos(chestPos), new MutableObject(), false, true);
                        }
                     );
                  ((ServerChunkCache)this.level.getChunkSource()).blockChanged(chestPos);
               }
            )
         );
   }

   private ResourceLocation getChestLoottable(Block chestBlock) {
      return chestBlock instanceof VaultChestBlock vaultChestBlock && this.chestLootTables.containsKey(vaultChestBlock.getType())
         ? this.chestLootTables.getOrDefault(vaultChestBlock.getType(), new ResourceLocation(""))
         : this.chestLootTables.get(VaultChestType.WOODEN);
   }

   private Block getRandomChestBlock() {
      return switch (this.level.random.nextInt(4)) {
         case 0 -> ModBlocks.WOODEN_CHEST;
         case 1 -> ModBlocks.LIVING_CHEST;
         case 2 -> ModBlocks.GILDED_CHEST;
         case 3 -> ModBlocks.ORNATE_CHEST;
         default -> ModBlocks.WOODEN_CHEST;
      };
   }

   private void summonFangs(BlockHitResult hitResult) {
      this.spawnFangs(hitResult, 4, 1);
      this.spawnFangs(hitResult, 4, 2);
      this.spawnFangs(hitResult, 4, 3);
      this.spawnFangs(hitResult, 4, 4);
      this.spawnFangs(hitResult, 4, 8);
   }

   private void spawnFangs(BlockHitResult hitResult, int numberOfFangs, int fangRadius) {
      for (int i = 0; i < numberOfFangs; i++) {
         double x = hitResult.getLocation().x + this.level.random.nextInt(fangRadius * 2) - fangRadius;
         double z = hitResult.getLocation().z + this.level.random.nextInt(fangRadius * 2) - fangRadius;
         this.level
            .addFreshEntity(
               new EvokerFangs(
                  this.level, x, hitResult.getBlockPos().getY() + 1, z, this.level.random.nextFloat((float) Math.PI), this.level.random.nextInt(10), null
               ) {
                  public void dealDamageTo(LivingEntity livingEntity) {
                     if (livingEntity.isAlive() && !livingEntity.isInvulnerable()) {
                        livingEntity.hurt(DamageSource.MAGIC, ConjurationMagicProjectileEntity.this.fangsDamage);
                     }
                  }
               }
            );
      }
   }

   private void summonAreaEffectCloud(BlockHitResult hitResult) {
      if (!this.effects.isEmpty()) {
         this.effects.getRandom(this.level.random).ifPresent(effectAttributes -> {
            BlockPos hitPos = hitResult.getBlockPos();
            AreaOfEffectBossEntity areaEffectCloud = new AreaOfEffectBossEntity(this.level, hitPos.getX() + 0.5, hitPos.getY() + 1.5, hitPos.getZ() + 0.5);
            areaEffectCloud.setRadius(8.0F);
            areaEffectCloud.setRadiusOnUse(-0.1F);
            areaEffectCloud.setWaitTime(0);
            areaEffectCloud.setDuration(160);
            areaEffectCloud.addEffect(new MobEffectInstance(effectAttributes.effect(), effectAttributes.duration(), effectAttributes.amplifier()));
            areaEffectCloud.setRadiusPerTick(0.0F);
            areaEffectCloud.setFixedColor(VaultGod.TENOS.getColor());
            areaEffectCloud.setParticle(ParticleTypes.AMBIENT_ENTITY_EFFECT);
            this.level.addFreshEntity(areaEffectCloud);
         });
      }
   }

   @Override
   public void addAdditionalSaveData(CompoundTag tag) {
      super.addAdditionalSaveData(tag);
      tag.putFloat("ChestChance", this.chestChance);
      tag.putFloat("FangsChance", this.fangsChance);
      tag.putFloat("FangsDamage", this.fangsDamage);
      tag.put("Effects", CatalystStageAttributes.serializeEffects(this.effects));
      tag.put("ChestLootTables", CatalystStageAttributes.serializeChestLootTables(this.chestLootTables));
   }

   @Override
   public void readAdditionalSaveData(CompoundTag tag) {
      super.readAdditionalSaveData(tag);
      this.chestChance = tag.getFloat("ChestChance");
      this.fangsChance = tag.getFloat("FangsChance");
      this.fangsDamage = tag.getFloat("FangsDamage");
      this.effects = CatalystStageAttributes.deserializeEffects(tag.getList("Effects", 10));
      this.chestLootTables = CatalystStageAttributes.deserializeChestLootTables(tag.getCompound("ChestLootTables"));
   }
}
