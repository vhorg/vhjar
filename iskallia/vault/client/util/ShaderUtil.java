package iskallia.vault.client.util;

import iskallia.vault.Vault;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import org.lwjgl.opengl.ARBShaderObjects;

public class ShaderUtil {
   private static final String PREFIX = "/assets/the_vault/shader/";
   public static int GRAYSCALE_SHADER = 0;
   public static int COLORIZE_SHADER = 0;
   private static final Map<Integer, Map<String, Integer>> UNIFORM_CONSTANTS = new HashMap<>();

   public static void initShaders() {
      GRAYSCALE_SHADER = createProgram("grayscale.vert", "grayscale.frag");
      COLORIZE_SHADER = createProgram("colorize.vert", "colorize.frag");
   }

   public static void useShader(int shader) {
      useShader(shader, null);
   }

   public static void useShader(int shader, @Nullable Runnable setter) {
      ARBShaderObjects.glUseProgramObjectARB(shader);
      if (shader != 0) {
         ARBShaderObjects.glUniform1iARB(getUniformLocation(shader, "texture_0"), 0);
         if (setter != null) {
            setter.run();
         }
      }
   }

   public static int getUniformLocation(int shaderProgram, String uniform) {
      Map<String, Integer> uniforms = UNIFORM_CONSTANTS.computeIfAbsent(shaderProgram, program -> new HashMap<>());
      return uniforms.computeIfAbsent(uniform, uniformKey -> ARBShaderObjects.glGetUniformLocationARB(shaderProgram, uniformKey));
   }

   public static void releaseShader() {
      useShader(0, null);
   }

   private static int createProgram(@Nullable String vert, @Nullable String frag) {
      int vertId = 0;
      int fragId = 0;
      if (vert != null) {
         vertId = createShader("/assets/the_vault/shader/" + vert, 35633);
      }

      if (frag != null) {
         fragId = createShader("/assets/the_vault/shader/" + frag, 35632);
      }

      int program = ARBShaderObjects.glCreateProgramObjectARB();
      if (program == 0) {
         return 0;
      } else {
         if (vert != null) {
            ARBShaderObjects.glAttachObjectARB(program, vertId);
         }

         if (frag != null) {
            ARBShaderObjects.glAttachObjectARB(program, fragId);
         }

         ARBShaderObjects.glLinkProgramARB(program);
         if (ARBShaderObjects.glGetObjectParameteriARB(program, 35714) == 0) {
            Vault.LOGGER.error(getLogInfo(program));
            return 0;
         } else {
            ARBShaderObjects.glValidateProgramARB(program);
            if (ARBShaderObjects.glGetObjectParameteriARB(program, 35715) == 0) {
               Vault.LOGGER.error(getLogInfo(program));
               return 0;
            } else {
               return program;
            }
         }
      }
   }

   private static int createShader(String filename, int shaderType) {
      int shader = 0;

      try {
         shader = ARBShaderObjects.glCreateShaderObjectARB(shaderType);
         if (shader == 0) {
            return 0;
         } else {
            ARBShaderObjects.glShaderSourceARB(shader, readFile(filename));
            ARBShaderObjects.glCompileShaderARB(shader);
            if (ARBShaderObjects.glGetObjectParameteriARB(shader, 35713) == 0) {
               throw new RuntimeException("Error creating shader \"" + filename + "\": " + getLogInfo(shader));
            } else {
               return shader;
            }
         }
      } catch (Exception var4) {
         ARBShaderObjects.glDeleteObjectARB(shader);
         var4.printStackTrace();
         return -1;
      }
   }

   private static String getLogInfo(int obj) {
      return ARBShaderObjects.glGetInfoLogARB(obj, ARBShaderObjects.glGetObjectParameteriARB(obj, 35716));
   }

   private static String readFile(String filename) throws Exception {
      InputStream in = ShaderUtil.class.getResourceAsStream(filename);
      if (in == null) {
         return "";
      } else {
         try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            StringBuilder result = new StringBuilder();

            while (true) {
               String line = reader.readLine();
               if (line == null) {
                  return result.toString();
               }

               result.append(line).append('\n');
            }
         }
      }
   }
}
