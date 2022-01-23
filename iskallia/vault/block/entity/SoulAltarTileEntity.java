package iskallia.vault.block.entity;

import iskallia.vault.block.SoulAltarBlock;
import iskallia.vault.block.base.FillableAltarBlock;
import iskallia.vault.block.base.FillableAltarTileEntity;
import iskallia.vault.entity.EternalEntity;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.util.CodecUtils;
import iskallia.vault.util.MiscUtils;
import iskallia.vault.world.data.PlayerFavourData;
import iskallia.vault.world.vault.VaultRaid;
import java.awt.Color;
import java.util.Optional;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.IParticleData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
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
   private static final AxisAlignedBB SEARCH_BOX = new AxisAlignedBB(-8.0, -8.0, -8.0, 8.0, 8.0, 8.0);
   public static final String SOUL_ALTAR_TAG = "the_vault_SoulAltar";
   public static final String SOUL_ALTAR_REF = "the_vault_SoulAltarPos";
   private int ticksExisted = 0;

   public SoulAltarTileEntity() {
      super(ModBlocks.SOUL_ALTAR_TILE_ENTITY);
   }

   @Override
   public void func_73660_a() {
      super.func_73660_a();
      if (!this.func_145831_w().func_201670_d()) {
         this.ticksExisted++;
         if (this.ticksExisted % 10 != 0) {
            return;
         }

         this.func_145831_w()
            .func_225316_b(
               LivingEntity.class,
               SEARCH_BOX.func_186670_a(this.func_174877_v()),
               entity -> entity.func_70089_S()
                  && !entity.func_175149_v()
                  && !entity.func_190530_aW()
                  && entity.func_200600_R().func_220339_d() == EntityClassification.MONSTER
            )
            .forEach(entity -> {
               if (entity.func_184211_a("the_vault_SoulAltar")) {
                  CodecUtils.writeNBT(BlockPos.field_239578_a_, this.func_174877_v(), entity.getPersistentData(), "the_vault_SoulAltarPos");
               }
            });
      }
   }

   @SubscribeEvent
   public static void onLivingUpdate(LivingUpdateEvent event) {
      LivingEntity entity = event.getEntityLiving();
      World world = entity.func_130014_f_();
      if (!world.func_201670_d() && world instanceof ServerWorld) {
         if (entity.func_184216_O().contains("the_vault_SoulAltar")) {
            CompoundNBT tag = entity.getPersistentData();
            if (tag.func_74764_b("the_vault_SoulAltarPos")) {
               BlockPos altarRef = CodecUtils.readNBT(BlockPos.field_239578_a_, tag.func_74781_a("the_vault_SoulAltarPos"), null);
               if (altarRef != null && world.func_175667_e(altarRef)) {
                  BlockState state = world.func_180495_p(altarRef);
                  TileEntity te = world.func_175625_s(altarRef);
                  if (te instanceof SoulAltarTileEntity && state.func_177230_c() instanceof SoulAltarBlock) {
                     IParticleData particle = ((SoulAltarBlock)state.func_177230_c()).getFlameParticle();
                     Vector3d at = MiscUtils.getRandomOffset(entity.func_174813_aQ().func_186662_g(0.2F), rand);
                     ServerWorld sWorld = (ServerWorld)world;
                     sWorld.func_195598_a(particle, at.field_72450_a, at.field_72448_b, at.field_72449_c, 1, 0.0, 0.0, 0.0, 0.0);
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onEntityDead(LivingDeathEvent event) {
      LivingEntity deadEntity = event.getEntityLiving();
      World world = deadEntity.func_130014_f_();
      if (!world.func_201670_d()) {
         DamageSource src = event.getSource();
         Entity sourceEntity = src.func_76346_g();
         if (sourceEntity instanceof EternalEntity) {
            sourceEntity = (Entity)((EternalEntity)sourceEntity).getOwner().right().orElse(null);
         }

         if (sourceEntity instanceof ServerPlayerEntity) {
            ServerPlayerEntity killer = (ServerPlayerEntity)sourceEntity;
            if (deadEntity.func_184216_O().contains("the_vault_SoulAltar")) {
               CompoundNBT tag = deadEntity.getPersistentData();
               if (tag.func_74764_b("the_vault_SoulAltarPos")) {
                  BlockPos altarRef = CodecUtils.readNBT(BlockPos.field_239578_a_, tag.func_74781_a("the_vault_SoulAltarPos"), null);
                  if (altarRef != null && world.func_175667_e(altarRef)) {
                     TileEntity te = world.func_175625_s(altarRef);
                     if (te instanceof SoulAltarTileEntity && ((SoulAltarTileEntity)te).initialized() && !((SoulAltarTileEntity)te).isMaxedOut()) {
                        ((SoulAltarTileEntity)te).makeProgress(killer, 1, sPlayer -> {
                           PlayerFavourData data = PlayerFavourData.get(sPlayer.func_71121_q());
                           if (rand.nextFloat() < FillableAltarBlock.getFavourChance(sPlayer, PlayerFavourData.VaultGodType.MALEVOLENCE)) {
                              PlayerFavourData.VaultGodType vg = PlayerFavourData.VaultGodType.MALEVOLENCE;
                              if (data.addFavour(sPlayer, vg, 1)) {
                                 data.addFavour(sPlayer, vg.getOther(rand), -1);
                                 FillableAltarBlock.playFavourInfo(sPlayer);
                              }
                           }
                        });
                        te.func_70296_d();
                     }
                  }
               }
            }
         }
      }
   }

   @Override
   public ITextComponent getRequirementName() {
      return new StringTextComponent("Monster Soul");
   }

   @Override
   public PlayerFavourData.VaultGodType getAssociatedVaultGod() {
      return PlayerFavourData.VaultGodType.MALEVOLENCE;
   }

   @Override
   public ITextComponent getRequirementUnit() {
      return new StringTextComponent("kills");
   }

   @Override
   public Color getFillColor() {
      return new Color(-2158319);
   }

   @Override
   protected Optional<Integer> calcMaxProgress(VaultRaid vault) {
      return vault.getProperties().getBase(VaultRaid.LEVEL).map(vaultLevel -> {
         float multiplier = vault.getProperties().getBase(VaultRaid.HOST).map(this::getMaxProgressMultiplier).orElse(1.0F);
         int progress = 4 + vaultLevel / 7;
         return Math.round(progress * multiplier);
      });
   }
}
