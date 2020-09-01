package ru.reactiveturtle.game.engine.material;

import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBImage;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.Arrays;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Texture {
    private int textureId;

    private ByteBuffer pixels;

    private int width, height;

    public Texture(String file) {
        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer components = BufferUtils.createIntBuffer(1);

        pixels = STBImage.stbi_load_from_memory(ioResourceToByteBuffer("src/main/resources/" + file), width, height, components, 4);
        System.out.println(pixels.capacity());

        textureId = glGenTextures();

        glBindTexture(GL_TEXTURE_2D, textureId);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        this.width = width.get();
        this.height = height.get();
        System.out.println(this.width + ", " + this.height + ", " + this.width * this.height * 4);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, this.width, this.height, 0, GL_RGBA, GL_UNSIGNED_BYTE, pixels);

        glGenerateMipmap(GL_TEXTURE_2D);

        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public Texture(int width, int height, int pixelFormat) {
        textureId = glGenTextures();
        this.width = width;
        this.height = height;
        glBindTexture(GL_TEXTURE_2D, textureId);
        float[] pixels = new float[width * height * 3];
        Arrays.fill(pixels, 1);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height, 0, pixelFormat, GL_FLOAT, pixels);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public Texture(int width, int height) {
        this.width = width;
        this.height = height;

        textureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureId);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT16, width, height, 0, GL_DEPTH_COMPONENT, GL_FLOAT, NULL);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);
        float[] borderColor = {1.0f, 1.0f, 1.0f, 1.0f};
        glTexParameterfv(GL_TEXTURE_2D, GL_TEXTURE_BORDER_COLOR, borderColor);
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public Texture(BufferedImage image) {
        this.width = image.getWidth();
        this.height = image.getHeight();
        textureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureId);
        float[] floatPixels = new float[image.getWidth() * image.getHeight() * 4];
        for (int i = 0; i < image.getHeight(); i++) {
            for (int j = 0; j < image.getWidth(); j++) {
                int index = i * image.getWidth() + j;
                Color color = new Color(image.getRGB(j, i), true);
                floatPixels[index * 4] = color.getRed() / 255f;
                floatPixels[index * 4 + 1] = color.getGreen() / 255f;
                floatPixels[index * 4 + 2] = color.getBlue() / 255f;
                floatPixels[index * 4 + 3] = color.getAlpha() / 255f;
            }
        }
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_FLOAT, floatPixels);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glGenerateMipmap(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public void destroy() {
        STBImage.stbi_image_free(pixels);
        glDeleteTextures(textureId);
    }

    private ByteBuffer ioResourceToByteBuffer(String res) {
        ByteBuffer buffer = null;
        File file = new File(res);

        try {
            URL url = file.toURI().toURL();
            if (file.isFile()) {
                try {
                    FileInputStream fis = new FileInputStream(file);
                    FileChannel fc = fis.getChannel();
                    buffer = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
                    fc.close();
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                buffer = BufferUtils.createByteBuffer(128);
                try {
                    try (InputStream source = url.openStream()) {
                        try (ReadableByteChannel rbc = Channels.newChannel(source)) {
                            while (true) {
                                int bytes = rbc.read(buffer);

                                if (bytes == -1) break;

                                if (buffer.remaining() == 0)
                                    buffer = resizeBuffer(buffer, buffer.capacity() * 2);
                            }
                            buffer.flip();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return buffer;
    }

    private ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity) {
        ByteBuffer newBuffer = BufferUtils.createByteBuffer(newCapacity);
        buffer.flip();
        newBuffer.put(buffer);
        return newBuffer;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public ByteBuffer getPixelsBuffer() {
        return pixels;
    }

    public int getTextureId() {
        return textureId;
    }

    public void set(BufferedImage image) {
        glBindTexture(GL_TEXTURE_2D, textureId);
        float[] floatPixels = new float[image.getWidth() * image.getHeight() * 4];
        for (int i = 0; i < image.getHeight(); i++) {
            for (int j = 0; j < image.getWidth(); j++) {
                int index = i * image.getWidth() + j;
                Color color = new Color(image.getRGB(j, i), true);
                floatPixels[index * 4] = color.getRed() / 255f;
                floatPixels[index * 4 + 1] = color.getGreen() / 255f;
                floatPixels[index * 4 + 2] = color.getBlue() / 255f;
                floatPixels[index * 4 + 3] = color.getAlpha() / 255f;
            }
        }
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_FLOAT, floatPixels);
        glBindTexture(GL_TEXTURE_2D, 0);
    }
}
