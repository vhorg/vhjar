package iskallia.vault.entity;

import iskallia.vault.init.ModConfigs;
import iskallia.vault.util.NameProviderPublic;
import iskallia.vault.world.data.VaultRaidData;
import iskallia.vault.world.vault.VaultRaid;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootContext.Builder;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class VaultFighterEntity extends FighterEntity {
   public VaultFighterEntity(EntityType<? extends ZombieEntity> type, World world) {
      super(type, world);
   }

   @Override
   protected void func_213354_a(DamageSource source, boolean attackedRecently) {
      ServerWorld world = (ServerWorld)this.field_70170_p;
      VaultRaid vault = VaultRaidData.get(world).getAt(world, this.func_233580_cy_());
      if (vault != null) {
         vault.getProperties().getBase(VaultRaid.HOST).flatMap(vault::getPlayer).ifPresent(player -> {
            int level = player.getProperties().getBase(VaultRaid.LEVEL).orElse(0);
            ResourceLocation id = ModConfigs.LOOT_TABLES.getForLevel(level).getVaultFighter();
            LootTable loot = this.field_70170_p.func_73046_m().func_200249_aQ().func_186521_a(id);
            Builder builder = this.func_213363_a(attackedRecently, source);
            LootContext ctx = builder.func_216022_a(LootParameterSets.field_216263_d);
            loot.func_216113_a(ctx).forEach(this::func_199701_a_);
         });
      }

      super.func_213354_a(source, attackedRecently);
   }

   @Override
   public ILivingEntityData func_213386_a(
      IServerWorld world, DifficultyInstance difficulty, SpawnReason reason, ILivingEntityData spawnData, CompoundNBT dataTag
   ) {
      ILivingEntityData livingData = super.func_213386_a(world, difficulty, reason, spawnData, dataTag);
      ServerWorld sWorld = (ServerWorld)this.field_70170_p;
      if (!this.field_70170_p.func_201670_d()) {
         VaultRaid vault = VaultRaidData.get(sWorld).getAt(sWorld, this.func_233580_cy_());
         if (vault != null) {
            String name = NameProviderPublic.getRandomName();
            this.func_200203_b(new StringTextComponent(name));
            this.getPersistentData().func_74778_a("VaultPlayerName", name);
         }
      }

      return livingData;
   }
}
