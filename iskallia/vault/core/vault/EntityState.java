package iskallia.vault.core.vault;

import iskallia.vault.core.Version;
import iskallia.vault.core.data.DataObject;
import iskallia.vault.core.data.adapter.Adapter;
import iskallia.vault.core.data.key.FieldKey;
import iskallia.vault.core.data.key.registry.FieldRegistry;
import iskallia.vault.entity.entity.SpiritEntity;
import iskallia.vault.world.vault.VaultUtils;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.server.ServerLifecycleHooks;

public class EntityState extends DataObject<EntityState> implements INBTSerializable<CompoundTag> {
   public static final FieldRegistry FIELDS = new FieldRegistry();
   public static final FieldKey<Double> POS_X = FieldKey.of("pos_x", Double.class).with(Version.v1_0, Adapter.ofDouble(), DISK.all()).register(FIELDS);
   public static final FieldKey<Double> POS_Y = FieldKey.of("pos_y", Double.class).with(Version.v1_0, Adapter.ofDouble(), DISK.all()).register(FIELDS);
   public static final FieldKey<Double> POS_Z = FieldKey.of("pos_z", Double.class).with(Version.v1_0, Adapter.ofDouble(), DISK.all()).register(FIELDS);
   public static final FieldKey<Float> YAW = FieldKey.of("yaw", Float.class).with(Version.v1_0, Adapter.ofFloat(), DISK.all()).register(FIELDS);
   public static final FieldKey<Float> PITCH = FieldKey.of("pitch", Float.class).with(Version.v1_0, Adapter.ofFloat(), DISK.all()).register(FIELDS);
   public static final FieldKey<GameType> GAME_MODE = FieldKey.of("game_mode", GameType.class)
      .with(Version.v1_0, Adapter.ofOrdinal(Enum::ordinal, GameType.values()), DISK.all())
      .register(FIELDS);
   public static final FieldKey<ResourceKey<Level>> WORLD = FieldKey.ofResourceKey("world", Level.class)
      .with(Version.v1_0, Adapter.ofResourceKey(Registry.DIMENSION_REGISTRY), DISK.all())
      .register(FIELDS);

   public EntityState() {
   }

   public EntityState(ServerPlayer player) {
      this(
         player.position().x,
         player.position().y,
         player.position().z,
         player.getYRot(),
         player.getXRot(),
         player.gameMode.getGameModeForPlayer(),
         player.level.dimension()
      );
   }

   public EntityState(double x, double y, double z, float yaw, float pitch, GameType gameMode, ResourceKey<Level> world) {
      this.set(POS_X, Double.valueOf(x));
      this.set(POS_Y, Double.valueOf(y));
      this.set(POS_Z, Double.valueOf(z));
      this.set(YAW, Float.valueOf(yaw));
      this.set(PITCH, Float.valueOf(pitch));
      this.set(GAME_MODE, gameMode);
      this.set(WORLD, world);
   }

   @Override
   public FieldRegistry getFields() {
      return FIELDS;
   }

   public CompoundTag serializeNBT() {
      CompoundTag nbt = new CompoundTag();
      nbt.putDouble("PosX", this.get(POS_X));
      nbt.putDouble("PosY", this.get(POS_Y));
      nbt.putDouble("PosZ", this.get(POS_Z));
      nbt.putFloat("Yaw", this.get(YAW));
      nbt.putFloat("Pitch", this.get(PITCH));
      nbt.putInt("GameMode", this.get(GAME_MODE).ordinal());
      nbt.putString("World", this.get(WORLD).location().toString());
      return nbt;
   }

   public void deserializeNBT(CompoundTag nbt) {
      this.set(POS_X, Double.valueOf(nbt.getDouble("PosX")));
      this.set(POS_Y, Double.valueOf(nbt.getDouble("PosY")));
      this.set(POS_Z, Double.valueOf(nbt.getDouble("PosZ")));
      this.set(YAW, Float.valueOf(nbt.getFloat("Yaw")));
      this.set(PITCH, Float.valueOf(nbt.getFloat("Pitch")));
      this.set(GAME_MODE, GameType.values()[nbt.getInt("GameMode")]);
      this.set(WORLD, ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(nbt.getString("World"))));
   }

   public void teleport(Entity entity) {
      ServerLevel target = ServerLifecycleHooks.getCurrentServer().getLevel(this.get(WORLD));
      ServerLevel world = target != null ? target : ServerLifecycleHooks.getCurrentServer().overworld();
      Vec3 position = new Vec3(this.get(POS_X), this.get(POS_Y), this.get(POS_Z));
      Vec3 velocity = entity.getDeltaMovement();
      if (!entity.getPassengers().isEmpty() && entity.getPassengers().get(0) instanceof SpiritEntity spirit) {
         spirit.teleportOut();
      }

      if (entity instanceof Player || entity.level != target) {
         VaultUtils.changeDimension(world, entity, position, velocity, this.get(YAW), this.get(PITCH), repositionedEntity -> {});
      }

      if (entity instanceof ServerPlayer player) {
         this.ifPresent(GAME_MODE, player::setGameMode);
      }
   }
}
