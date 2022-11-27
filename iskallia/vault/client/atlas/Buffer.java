package iskallia.vault.client.atlas;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;

public record Buffer(Mode mode, VertexFormat format, BufferBuilder bufferBuilder) {
}
