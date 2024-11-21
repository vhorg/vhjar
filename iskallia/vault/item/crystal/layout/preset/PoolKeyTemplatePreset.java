package iskallia.vault.item.crystal.layout.preset;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.basic.EnumAdapter;
import iskallia.vault.core.data.key.TemplatePoolKey;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.core.world.generator.layout.VaultLayout;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public class PoolKeyTemplatePreset extends TemplatePreset {
   private VaultLayout.PieceType piece;
   private ResourceLocation pool;
   public static final EnumAdapter<VaultLayout.PieceType> PIECE = Adapters.ofEnum(VaultLayout.PieceType.class, EnumAdapter.Mode.NAME).asNullable();

   public PoolKeyTemplatePreset() {
   }

   public PoolKeyTemplatePreset(VaultLayout.PieceType piece, TemplatePoolKey pool) {
      this.piece = piece;
      this.pool = pool == null ? null : pool.getId();
   }

   public PoolKeyTemplatePreset(VaultLayout.PieceType piece) {
      this(piece, null);
   }

   public PoolKeyTemplatePreset(TemplatePoolKey pool) {
      this(null, pool);
   }

   public VaultLayout.PieceType getPiece() {
      return this.piece;
   }

   public ResourceLocation getPool() {
      return this.pool;
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.IDENTIFIER.writeBits(this.pool, buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.pool = Adapters.IDENTIFIER.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         PIECE.writeNbt(this.piece).ifPresent(tag -> nbt.put("piece", tag));
         Adapters.IDENTIFIER.writeNbt(this.pool).ifPresent(value -> nbt.put("pool", value));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.piece = PIECE.readNbt(nbt.get("piece")).orElse(null);
      this.pool = Adapters.IDENTIFIER.readNbt(nbt.get("pool")).orElse(null);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         PIECE.writeJson(this.piece).ifPresent(tag -> json.add("piece", tag));
         Adapters.IDENTIFIER.writeJson(this.pool).ifPresent(value -> json.add("pool", value));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.piece = PIECE.readJson(json.get("piece")).orElse(null);
      this.pool = Adapters.IDENTIFIER.readJson(json.get("pool")).orElse(null);
   }
}
