package ru.reactiveturtle.engine.texture;

import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBImage;
import ru.reactiveturtle.engine.base.GameContext;

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
import java.util.Objects;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL30.*;

public class Texture {
    protected int textureId;
    private int width, height;
    private ByteBuffer pixels;

    public Texture(String file) {
        IntBuffer widthBuffer = BufferUtils.createIntBuffer(1);
        IntBuffer heightBuffer = BufferUtils.createIntBuffer(1);
        IntBuffer glPixelFormatBuffer = BufferUtils.createIntBuffer(1);

        ByteBuffer pixelsBuffer = STBImage.stbi_load_from_memory(ioResourceToByteBuffer(GameContext.RESOURCE_PATH + file), widthBuffer, heightBuffer, glPixelFormatBuffer, 4);

        init(widthBuffer.get(), heightBuffer.get(), pixelsBuffer, GL_RGBA);
    }

    public Texture(int width, int height, PixelFormat pixelFormat) {
        int glPixelFormat;
        int componentsCount;
        switch (pixelFormat) {
            case RGB:
                glPixelFormat = GL_RGB;
                componentsCount = 3;
                break;
            case RGBA:
                glPixelFormat = GL_RGBA;
                componentsCount = 4;
                break;
            default:
                throw new IllegalArgumentException("Undeclared pixel format");
        }

        int capacity = width * height * componentsCount;

        ByteBuffer pixelsBuffer = null;
        if (capacity > 0) {
            byte[] pixels = new byte[capacity];
            Arrays.fill(pixels, Byte.MAX_VALUE);
            pixelsBuffer = allocate(pixels);
        }

        init(width, height, pixelsBuffer, glPixelFormat);
    }

    public Texture(BufferedImage image) {
        int width = image.getWidth(), height = image.getHeight();
        byte[] pixels = new byte[width * height * 4];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int index = i * width + j;
                Color color = new Color(image.getRGB(j, i), true);
                pixels[index * 4] = (byte) (color.getRed());
                pixels[index * 4 + 1] = (byte) (color.getGreen());
                pixels[index * 4 + 2] = (byte) (color.getBlue());
                pixels[index * 4 + 3] = (byte) (color.getAlpha());
            }
        }

        init(width, height, allocate(pixels), GL_RGBA);
    }

    protected Texture() {
    }

    public void set(BufferedImage image) {
        int width = image.getWidth(), height = image.getHeight();
        byte[] pixels = new byte[width * height * 4];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int index = i * width + j;
                Color color = new Color(image.getRGB(j, i), true);
                pixels[index * 4] = (byte) (color.getRed());
                pixels[index * 4 + 1] = (byte) (color.getGreen());
                pixels[index * 4 + 2] = (byte) (color.getBlue());
                pixels[index * 4 + 3] = (byte) (color.getAlpha());
            }
        }

        set(width, height, allocate(pixels), GL_RGBA);
    }

    public void destroy() {
        pixels.clear();
        glDeleteTextures(textureId);
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

    protected void init(int width, int height, ByteBuffer pixels, int glPixelFormat) {
        this.textureId = glGenTextures();
        set(width, height, pixels, glPixelFormat);
    }

    protected void configureTexture(ByteBuffer pixels, int glPixelFormat) {
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

        glTexImage2D(GL_TEXTURE_2D, 0, glPixelFormat, width, height, 0, glPixelFormat, GL_UNSIGNED_BYTE, pixels);
        glGenerateMipmap(GL_TEXTURE_2D);
    }

    private void set(int width, int height, ByteBuffer pixels, int glPixelFormat) {
        this.width = width;
        this.height = height;
        if (pixels != null) {
            if (this.pixels != null) {
                this.pixels.clear();
            }
            this.pixels = pixels.asReadOnlyBuffer();
        }

        glBindTexture(GL_TEXTURE_2D, textureId);
        configureTexture(this.pixels, glPixelFormat);
        glBindTexture(GL_TEXTURE_2D, 0);
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

    private static ByteBuffer allocate(byte[] bytes) {
        Objects.requireNonNull(bytes);
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(bytes.length);
        byteBuffer.put(bytes);
        byteBuffer.position(0);
        return byteBuffer;
    }

    public enum PixelFormat {
        RGB, RGBA
    }
}
