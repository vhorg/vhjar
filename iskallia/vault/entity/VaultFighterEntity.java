package iskallia.vault.entity;

import iskallia.vault.world.data.VaultRaidData;
import iskallia.vault.world.raid.VaultRaid;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
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
   public ILivingEntityData func_213386_a(
      IServerWorld world, DifficultyInstance difficulty, SpawnReason reason, ILivingEntityData spawnData, CompoundNBT dataTag
   ) {
      ILivingEntityData livingData = super.func_213386_a(world, difficulty, reason, spawnData, dataTag);
      if (!this.field_70170_p.field_72995_K) {
         VaultRaid raid = VaultRaidData.get((ServerWorld)this.field_70170_p).getAt(this.func_233580_cy_());
         if (raid != null) {
            ServerPlayerEntity player = ((ServerWorld)world).func_73046_m().func_184103_al().func_177451_a(raid.playerIds.get(0));
            String name = player != null ? player.func_200200_C_().getString() : "";
            this.func_200203_b(new StringTextComponent(name));
         }
      }

      return livingData;
   }
}
