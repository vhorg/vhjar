package iskallia.vault.client.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.math.Vector3f;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Locale;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleOptions.Deserializer;
import net.minecraft.network.FriendlyByteBuf;

public record ArtifactBossImmunityParticleOptions(ParticleType<ArtifactBossImmunityParticleOptions> particleType, int lifetime, Vector3f color, Vector3f color2)
   implements ParticleOptions {
   public static final Deserializer<ArtifactBossImmunityParticleOptions> DESERIALIZER = new Deserializer<ArtifactBossImmunityParticleOptions>() {
      @Nonnull
      @ParametersAreNonnullByDefault
      public ArtifactBossImmunityParticleOptions fromCommand(ParticleType<ArtifactBossImmunityParticleOptions> particleType, StringReader reader) throws CommandSyntaxException {
         return new ArtifactBossImmunityParticleOptions(
            particleType, reader.readInt(), ArtifactBossImmunityParticleOptions.readVector3f(reader), ArtifactBossImmunityParticleOptions.readVector3f(reader)
         );
      }

      @Nonnull
      @ParametersAreNonnullByDefault
      public ArtifactBossImmunityParticleOptions fromNetwork(ParticleType<ArtifactBossImmunityParticleOptions> particleType, FriendlyByteBuf buffer) {
         return new ArtifactBossImmunityParticleOptions(
            particleType,
            buffer.readInt(),
            new Vector3f(buffer.readFloat(), buffer.readFloat(), buffer.readFloat()),
            new Vector3f(buffer.readFloat(), buffer.readFloat(), buffer.readFloat())
         );
      }
   };

   public static Codec<ArtifactBossImmunityParticleOptions> codec(ParticleType<ArtifactBossImmunityParticleOptions> particleType) {
      return RecordCodecBuilder.create(
         c -> c.group(
               Codec.INT.fieldOf("lifetime").forGetter(data -> data.lifetime),
               Vector3f.CODEC.fieldOf("color").forGetter(data -> data.color),
               Vector3f.CODEC.fieldOf("color").forGetter(data -> data.color)
            )
            .apply(c, (range, color, color2) -> new ArtifactBossImmunityParticleOptions(particleType, range, color, color2))
      );
   }

   @Nonnull
   public ParticleType<ArtifactBossImmunityParticleOptions> getType() {
      return this.particleType;
   }

   public void writeToNetwork(@Nonnull FriendlyByteBuf buffer) {
      buffer.writeFloat(this.lifetime);
      buffer.writeFloat(this.color.x());
      buffer.writeFloat(this.color.y());
      buffer.writeFloat(this.color.z());
      buffer.writeFloat(this.color2.x());
      buffer.writeFloat(this.color2.y());
      buffer.writeFloat(this.color2.z());
   }

   @Nonnull
   public String writeToString() {
      return String.format(
         Locale.ROOT,
         "%s %d %.2f %.2f %.2f %.2f %.2f %.2f",
         this.getType().getRegistryName(),
         this.lifetime,
         this.color.x(),
         this.color.y(),
         this.color.z(),
         this.color2.x(),
         this.color2.y(),
         this.color2.z()
      );
   }

   private static Vector3f readVector3f(StringReader stringReader) throws CommandSyntaxException {
      stringReader.expect(' ');
      float f0 = stringReader.readFloat();
      stringReader.expect(' ');
      float f1 = stringReader.readFloat();
      stringReader.expect(' ');
      float f2 = stringReader.readFloat();
      return new Vector3f(f0, f1, f2);
   }
}
