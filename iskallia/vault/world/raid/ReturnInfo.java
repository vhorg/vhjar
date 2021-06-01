package iskallia.vault.world.raid;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.INBTSerializable;

public class ReturnInfo implements INBTSerializable<CompoundNBT> {
   private Vector3d position;
   private float yaw;
   private float pitch;
   private GameType gamemode;
   private RegistryKey<World> dimension;

   public ReturnInfo() {
      this(Vector3d.field_186680_a, 0.0F, 0.0F, GameType.NOT_SET, ServerWorld.field_234918_g_);
   }

   public ReturnInfo(ServerPlayerEntity player) {
      this(player.func_213303_ch(), player.field_70177_z, player.field_70125_A, player.field_71134_c.func_73081_b(), player.field_70170_p.func_234923_W_());
   }

   public ReturnInfo(Vector3d position, float yaw, float pitch, GameType gamemode, RegistryKey<World> dimension) {
      this.position = position;
      this.yaw = yaw;
      this.pitch = pitch;
      this.gamemode = gamemode;
      this.dimension = dimension;
   }

   public void apply(MinecraftServer server, ServerPlayerEntity player) {
      ServerWorld world = server.func_71218_a(this.dimension);
      player.func_200619_a(world, this.position.field_72450_a, this.position.field_72448_b, this.position.field_72449_c, this.yaw, this.pitch);
      player.func_71033_a(this.gamemode);
   }

   public CompoundNBT serializeNBT() {
      CompoundNBT nbt = new CompoundNBT();
      nbt.func_74780_a("PosX", this.position.field_72450_a);
      nbt.func_74780_a("PosY", this.position.field_72448_b);
      nbt.func_74780_a("PosZ", this.position.field_72449_c);
      nbt.func_74776_a("Yaw", this.yaw);
      nbt.func_74776_a("Pitch", this.pitch);
      nbt.func_74768_a("Gamemode", this.gamemode.ordinal());
      nbt.func_74778_a("Dimension", this.dimension.func_240901_a_().toString());
      return nbt;
   }

   public void deserializeNBT(CompoundNBT nbt) {
      this.position = new Vector3d(nbt.func_74769_h("PosX"), nbt.func_74769_h("PosY"), nbt.func_74769_h("PosZ"));
      this.yaw = nbt.func_74760_g("Yaw");
      this.pitch = nbt.func_74760_g("Pitch");
      this.gamemode = GameType.func_77146_a(nbt.func_74762_e("Gamemode"));
      this.dimension = RegistryKey.func_240903_a_(Registry.field_239699_ae_, new ResourceLocation(nbt.func_74779_i("Dimension")));
   }
}
