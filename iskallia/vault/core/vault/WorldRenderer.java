package iskallia.vault.core.vault;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import iskallia.vault.core.Version;
import iskallia.vault.core.data.DataObject;
import iskallia.vault.core.data.adapter.Adapter;
import iskallia.vault.core.data.key.FieldKey;
import iskallia.vault.core.data.key.registry.FieldRegistry;
import iskallia.vault.core.event.ClientEvents;
import iskallia.vault.core.world.generator.theme.Theme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.AmbientParticleSettings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

public class WorldRenderer extends DataObject<WorldRenderer> {
   public static final FieldRegistry FIELDS = new FieldRegistry();
   public static final FieldKey<Float> AMBIENT_LIGHT = FieldKey.of("ambient_light", Float.class)
      .with(Version.v1_0, Adapter.ofFloat(), DISK.all().or(CLIENT.all()))
      .register(FIELDS);
   public static final FieldKey<Integer> FOG_COLOR = FieldKey.of("fog_color", Integer.class)
      .with(Version.v1_0, Adapter.ofInt(), DISK.all().or(CLIENT.all()))
      .register(FIELDS);
   public static final FieldKey<Integer> GRASS_COLOR = FieldKey.of("grass_color", Integer.class)
      .with(Version.v1_0, Adapter.ofInt(), DISK.all().or(CLIENT.all()))
      .register(FIELDS);
   public static final FieldKey<Integer> FOLIAGE_COLOR = FieldKey.of("foliage_color", Integer.class)
      .with(Version.v1_0, Adapter.ofInt(), DISK.all().or(CLIENT.all()))
      .register(FIELDS);
   public static final FieldKey<Integer> WATER_COLOR = FieldKey.of("water_color", Integer.class)
      .with(Version.v1_0, Adapter.ofInt(), DISK.all().or(CLIENT.all()))
      .register(FIELDS);
   public static final FieldKey<Integer> WATER_FOG_COLOR = FieldKey.of("water_fog_color", Integer.class)
      .with(Version.v1_0, Adapter.ofInt(), DISK.all().or(CLIENT.all()))
      .register(FIELDS);
   public static final FieldKey<String> PARTICLE = FieldKey.of("particle", String.class)
      .with(Version.v1_0, Adapter.ofString(), DISK.all().or(CLIENT.all()))
      .register(FIELDS);
   public static final FieldKey<Float> PARTICLE_PROBABILITY = FieldKey.of("particle_probability", Float.class)
      .with(Version.v1_0, Adapter.ofFloat(), DISK.all().or(CLIENT.all()))
      .register(FIELDS);
   private float prevAmbientLight;
   private float[] ambientLightRamp;

   @Override
   public FieldRegistry getFields() {
      return FIELDS;
   }

   @OnlyIn(Dist.CLIENT)
   public void initClient(Vault vault) {
      ClientEvents.AMBIENT_LIGHT.register(vault, data -> {
         ClientLevel world = Minecraft.getInstance().level;
         if (world != null && world.dimension().location().equals(vault.get(Vault.WORLD).get(WorldManager.KEY))) {
            data.setBrightness(this.getBrightness(data.getLightLevel()));
         }
      });
      ClientEvents.BIOME_COLORS.register(vault, data -> {
         ClientLevel world = Minecraft.getInstance().level;
         if (world != null && world.dimension().location().equals(vault.get(Vault.WORLD).get(WorldManager.KEY))) {
            switch (data.getType()) {
               case FOG:
                  this.ifPresent(FOG_COLOR, data::setColor);
                  break;
               case GRASS:
                  this.ifPresent(GRASS_COLOR, data::setColor);
                  break;
               case FOLIAGE:
                  this.ifPresent(FOLIAGE_COLOR, data::setColor);
                  break;
               case WATER:
                  this.ifPresent(WATER_COLOR, data::setColor);
                  break;
               case WATER_FOG:
                  this.ifPresent(WATER_FOG_COLOR, data::setColor);
            }
         }
      });
      ClientEvents.AMBIENT_PARTICLE.register(vault, data -> {
         ClientLevel world = Minecraft.getInstance().level;
         if (world != null && world.dimension().location().equals(vault.get(Vault.WORLD).get(WorldManager.KEY))) {
            this.ifPresent(PARTICLE, particle -> data.setSettings(new AmbientParticleSettings(this.readParticle(particle), this.get(PARTICLE_PROBABILITY))));
         }
      });
   }

   public WorldRenderer setTheme(Theme theme) {
      if (theme.getParticle() != null) {
         this.set(PARTICLE, theme.getParticle());
         this.set(PARTICLE_PROBABILITY, Float.valueOf(theme.getParticleProbability()));
      }

      return this.set(AMBIENT_LIGHT, Float.valueOf(theme.getAmbientLight()))
         .set(FOG_COLOR, Integer.valueOf(theme.getFogColor()))
         .set(GRASS_COLOR, Integer.valueOf(theme.getGrassColor()))
         .set(FOLIAGE_COLOR, Integer.valueOf(theme.getFoliageColor()))
         .set(WATER_COLOR, Integer.valueOf(theme.getWaterColor()))
         .set(WATER_FOG_COLOR, Integer.valueOf(theme.getWaterFogColor()));
   }

   public float getBrightness(int lightLevel) {
      if (this.ambientLightRamp == null || this.prevAmbientLight != this.get(AMBIENT_LIGHT)) {
         this.ambientLightRamp = new float[16];

         for (int i = 0; i <= 15; i++) {
            float f = i / 15.0F;
            float f1 = f / (4.0F - 3.0F * f);
            this.ambientLightRamp[i] = Mth.lerp(this.get(AMBIENT_LIGHT), f1, 1.0F);
         }

         this.prevAmbientLight = this.get(AMBIENT_LIGHT);
      }

      return this.ambientLightRamp[lightLevel];
   }

   public ParticleOptions readParticle(String string) {
      StringReader reader = new StringReader(string);

      try {
         return this.readParticle(reader, (ParticleType<ParticleOptions>)ForgeRegistries.PARTICLE_TYPES.getValue(ResourceLocation.read(reader)));
      } catch (CommandSyntaxException var4) {
         throw new RuntimeException(var4);
      }
   }

   private <T extends ParticleOptions> T readParticle(StringReader reader, ParticleType<T> type) throws CommandSyntaxException {
      return (T)(type == null ? null : type.getDeserializer().fromCommand(type, reader));
   }
}
