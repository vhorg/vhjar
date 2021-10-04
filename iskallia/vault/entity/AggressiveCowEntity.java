package iskallia.vault.entity;

import iskallia.vault.entity.ai.CowDashAttackGoal;
import iskallia.vault.entity.ai.MobAttackGoal;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.world.data.VaultRaidData;
import iskallia.vault.world.vault.VaultRaid;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.AttributeModifierMap.MutableAttribute;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootContext.Builder;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class AggressiveCowEntity extends CowEntity {
   protected int dashCooldown = 0;

   public AggressiveCowEntity(EntityType<? extends CowEntity> type, World worldIn) {
      super(type, worldIn);
   }

   public static MutableAttribute getAttributes() {
      return MonsterEntity.func_234295_eP_()
         .func_233815_a_(Attributes.field_233819_b_, 100.0)
         .func_233815_a_(Attributes.field_233821_d_, 0.25)
         .func_233815_a_(Attributes.field_233823_f_, 3.0)
         .func_233815_a_(Attributes.field_233824_g_, 3.0)
         .func_233815_a_(Attributes.field_233820_c_, 0.4)
         .func_233815_a_(Attributes.field_233826_i_, 2.0);
   }

   protected void func_184651_r() {
      this.field_70714_bg.func_75776_a(8, new WaterAvoidingRandomWalkingGoal(this, 1.5));
      this.field_70714_bg.func_75776_a(8, new LookAtGoal(this, PlayerEntity.class, 16.0F));
      this.field_70714_bg.func_75776_a(0, new CowDashAttackGoal(this, 0.3F));
      this.field_70714_bg.func_75776_a(1, new MobAttackGoal(this, 1.5, true));
      this.field_70715_bh.func_75776_a(0, new NearestAttackableTargetGoal(this, PlayerEntity.class, 0, true, false, null));
   }

   protected void func_213354_a(DamageSource source, boolean attackedRecently) {
      ServerWorld world = (ServerWorld)this.field_70170_p;
      VaultRaid vault = VaultRaidData.get(world).getAt(world, this.func_233580_cy_());
      if (vault != null) {
         vault.getProperties().getBase(VaultRaid.HOST).flatMap(vault::getPlayer).ifPresent(player -> {
            int level = player.getProperties().getBase(VaultRaid.LEVEL).orElse(0);
            ResourceLocation id = ModConfigs.LOOT_TABLES.getForLevel(level).getCow();
            LootTable loot = this.field_70170_p.func_73046_m().func_200249_aQ().func_186521_a(id);
            Builder builder = this.func_213363_a(attackedRecently, source);
            LootContext ctx = builder.func_216022_a(LootParameterSets.field_216263_d);
            loot.func_216113_a(ctx).forEach(this::func_199701_a_);
         });
      }

      super.func_213354_a(source, attackedRecently);
   }

   public void func_70636_d() {
      super.func_70636_d();
      this.func_70873_a(0);
      if (this.dashCooldown > 0) {
         this.dashCooldown--;
      }
   }

   public boolean func_180431_b(DamageSource source) {
      return super.func_180431_b(source) || source == DamageSource.field_76379_h || source == DamageSource.field_76369_e;
   }

   public boolean canDash() {
      return this.dashCooldown <= 0;
   }

   public void onDash() {
      this.dashCooldown = 60;
      this.field_70699_by.func_75499_g();
      this.func_70642_aH();
   }
}
