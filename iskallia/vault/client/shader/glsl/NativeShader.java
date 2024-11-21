package iskallia.vault.client.shader.glsl;

import com.mojang.blaze3d.systems.RenderSystem;
import iskallia.vault.VaultMod;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.GL20;

public class NativeShader {
   private int loadedShader = -1;
   private int fsh = -1;
   private int vsh = -1;

   public NativeShader(ResourceProvider resourceLoader, ResourceLocation shaderName) {
      this.loadShader(resourceLoader, shaderName);
   }

   public void applyFloatValue(String name, float value) {
      if (this.loadedShader != -1) {
         int location = GL20.glGetUniformLocation(this.loadedShader, name);
         GL20.glUniform1f(location, value);
      }
   }

   public void applyIntValue(String name, int value) {
      if (this.loadedShader != -1) {
         int location = GL20.glGetUniformLocation(this.loadedShader, name);
         GL20.glUniform1i(location, value);
      }
   }

   public void applyActiveTexture(String name) {
      if (this.loadedShader != -1) {
         int activeTex = RenderSystem.getShaderTexture(0);
         int location = GL20.glGetUniformLocation(this.loadedShader, name);
         GL20.glUniform1i(location, activeTex);
      }
   }

   public void run() {
      if (this.loadedShader != -1) {
         GL20.glUseProgram(this.loadedShader);
      }
   }

   public void destroy() {
      if (this.loadedShader != -1) {
         GL20.glDeleteProgram(this.loadedShader);
      }

      if (this.fsh != -1) {
         GL20.glDeleteShader(this.fsh);
      }

      if (this.vsh != -1) {
         GL20.glDeleteShader(this.vsh);
      }
   }

   private void loadShader(ResourceProvider resourceLoader, ResourceLocation shaderName) {
      ResourceLocation frag = new ResourceLocation(shaderName.getNamespace(), "shaders/" + shaderName.getPath() + ".fsh");
      ResourceLocation vert = new ResourceLocation(shaderName.getNamespace(), "shaders/" + shaderName.getPath() + ".vsh");

      int fsh;
      int vsh;
      try {
         fsh = this.compileShader(resourceLoader, frag, 35632);
         vsh = this.compileShader(resourceLoader, vert, 35633);
      } catch (IOException var8) {
         var8.printStackTrace();
         return;
      }

      int shaderProgram = GL20.glCreateProgram();
      GL20.glAttachShader(shaderProgram, vsh);
      GL20.glAttachShader(shaderProgram, fsh);
      GL20.glLinkProgram(shaderProgram);
      if (GL20.glGetProgrami(shaderProgram, 35714) == 0) {
         VaultMod.LOGGER.error("Shader linking failed: " + GL20.glGetProgramInfoLog(shaderProgram));
      } else {
         this.fsh = fsh;
         this.vsh = vsh;
         this.loadedShader = shaderProgram;
      }
   }

   private int compileShader(ResourceProvider resourceLoader, ResourceLocation shaderName, int type) throws IOException {
      Resource resource = resourceLoader.getResource(shaderName);
      String shader = IOUtils.toString(resource.getInputStream(), StandardCharsets.UTF_8);
      int shaderId = GL20.glCreateShader(type);
      GL20.glShaderSource(shaderId, shader);
      GL20.glCompileShader(shaderId);
      if (GL20.glGetShaderi(shaderId, 35713) == 0) {
         throw new IOException("Shader compilation failed: " + GL20.glGetShaderInfoLog(shaderId));
      } else {
         return shaderId;
      }
   }
}
