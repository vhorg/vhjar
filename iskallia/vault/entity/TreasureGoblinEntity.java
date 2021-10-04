package iskallia.vault.entity;

import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModSounds;
import iskallia.vault.world.data.VaultRaidData;
import iskallia.vault.world.vault.VaultRaid;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootContext.Builder;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.WorldTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class TreasureGoblinEntity extends MonsterEntity {
   protected int disappearTick;
   protected boolean shouldDisappear;
   protected PlayerEntity lastAttackedPlayer;

   public TreasureGoblinEntity(EntityType<? extends MonsterEntity> type, World worldIn) {
      super(type, worldIn);
   }

   protected void func_184651_r() {
      this.field_70714_bg.func_75776_a(3, new AvoidEntityGoal(this, PlayerEntity.class, 6.0F, 1.7F, 2.0));
      this.field_70714_bg.func_75776_a(5, new LookAtGoal(this, PlayerEntity.class, 20.0F));
      this.field_70714_bg.func_75776_a(5, new LookRandomlyGoal(this));
   }

   public boolean isHitByPlayer() {
      return this.lastAttackedPlayer != null;
   }

   protected int calcDisappearTicks(PlayerEntity player) {
      return 200;
   }

   public boolean func_70097_a(DamageSource source, float amount) {
      Entity entity = source.func_76346_g();
      if (entity instanceof PlayerEntity && !entity.field_70170_p.field_72995_K) {
         PlayerEntity player = (PlayerEntity)entity;
         if (!this.isHitByPlayer()) {
            this.lastAttackedPlayer = player;
            this.disappearTick = this.calcDisappearTicks(player);
            this.func_195064_c(new EffectInstance(Effects.field_188423_x, this.disappearTick));
         }
      }

      return super.func_70097_a(source, amount);
   }

   protected void func_213354_a(DamageSource source, boolean attackedRecently) {
      ServerWorld world = (ServerWorld)this.field_70170_p;
      VaultRaid vault = VaultRaidData.get(world).getAt(world, this.func_233580_cy_());
      if (vault != null) {
         vault.getProperties().getBase(VaultRaid.HOST).flatMap(vault::getPlayer).ifPresent(player -> {
            int level = player.getProperties().getBase(VaultRaid.LEVEL).orElse(0);
            ResourceLocation id = ModConfigs.LOOT_TABLES.getForLevel(level).getTreasureGoblin();
            LootTable loot = this.field_70170_p.func_73046_m().func_200249_aQ().func_186521_a(id);
            Builder builder = this.func_213363_a(attackedRecently, source);
            LootContext ctx = builder.func_216022_a(LootParameterSets.field_216263_d);
            loot.func_216113_a(ctx).forEach(this::func_199701_a_);
         });
      }

      super.func_213354_a(source, attackedRecently);
   }

   public void func_70071_h_() {
      super.func_70071_h_();
      if (this.func_70089_S() && this.isHitByPlayer()) {
         if (this.disappearTick <= 0) {
            this.shouldDisappear = true;
         }

         this.disappearTick--;
      }
   }

   public void disappear(ServerWorld world) {
      world.func_217467_h(this);
      if (this.lastAttackedPlayer != null) {
         StringTextComponent bailText = (StringTextComponent)new StringTextComponent("Treasure Goblin escaped from you.")
            .func_240703_c_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(8042883)));
         this.lastAttackedPlayer.func_146105_b(bailText, true);
         this.lastAttackedPlayer.func_213823_a(ModSounds.GOBLIN_BAIL, SoundCategory.MASTER, 0.7F, 1.0F);
         world.func_184148_a(
            this.lastAttackedPlayer,
            this.func_226277_ct_(),
            this.func_226278_cu_(),
            this.func_226281_cx_(),
            ModSounds.GOBLIN_BAIL,
            SoundCategory.MASTER,
            0.7F,
            1.0F
         );
      } else {
         world.func_184148_a(
            null, this.func_226277_ct_(), this.func_226278_cu_(), this.func_226281_cx_(), ModSounds.GOBLIN_BAIL, SoundCategory.MASTER, 0.7F, 1.0F
         );
      }
   }

   @SubscribeEvent
   public static void onWorldTick(WorldTickEvent event) {
      if (!event.world.func_201670_d() && event.phase != Phase.START) {
         ServerWorld world = (ServerWorld)event.world;
         List<TreasureGoblinEntity> goblins = world.getEntities()
            .filter(entity -> entity instanceof TreasureGoblinEntity)
            .map(entity -> (TreasureGoblinEntity)entity)
            .collect(Collectors.toList());
         goblins.stream().filter(goblin -> goblin.shouldDisappear).forEach(goblin -> goblin.disappear(world));
      }
   }

   @Nullable
   protected SoundEvent func_184639_G() {
      return ModSounds.GOBLIN_IDLE;
   }

   protected SoundEvent func_184615_bR() {
      return ModSounds.GOBLIN_DEATH;
   }

   protected SoundEvent func_184601_bQ(@Nonnull DamageSource damageSourceIn) {
      return ModSounds.GOBLIN_HURT;
   }
}
