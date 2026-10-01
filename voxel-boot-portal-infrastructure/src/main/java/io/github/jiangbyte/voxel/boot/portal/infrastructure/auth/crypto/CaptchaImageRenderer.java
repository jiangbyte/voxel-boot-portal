package io.github.jiangbyte.voxel.boot.portal.infrastructure.auth.crypto;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;
import java.util.zip.CRC32;
import java.util.zip.Deflater;

/**
 * 验证码图片渲染工具：输出 PNG Base64。
 *
 * Author: Charlie
 */
final class CaptchaImageRenderer {

    private static final int WIDTH = 140;
    private static final int HEIGHT = 44;
    private static final int NOISE_LINES = 6;

    private static final Map<Character, String[]> GLYPHS = Map.ofEntries(
            Map.entry('2', new String[]{"11110", "00001", "00001", "11110", "10000", "10000", "11111"}),
            Map.entry('3', new String[]{"11110", "00001", "00001", "01110", "00001", "00001", "11110"}),
            Map.entry('4', new String[]{"10001", "10001", "10001", "11111", "00001", "00001", "00001"}),
            Map.entry('5', new String[]{"11111", "10000", "10000", "11110", "00001", "00001", "11110"}),
            Map.entry('6', new String[]{"01111", "10000", "10000", "11110", "10001", "10001", "01110"}),
            Map.entry('7', new String[]{"11111", "00001", "00010", "00100", "01000", "01000", "01000"}),
            Map.entry('8', new String[]{"01110", "10001", "10001", "01110", "10001", "10001", "01110"}),
            Map.entry('9', new String[]{"01110", "10001", "10001", "01111", "00001", "00001", "11110"}),
            Map.entry('A', new String[]{"01110", "10001", "10001", "11111", "10001", "10001", "10001"}),
            Map.entry('B', new String[]{"11110", "10001", "10001", "11110", "10001", "10001", "11110"}),
            Map.entry('C', new String[]{"01111", "10000", "10000", "10000", "10000", "10000", "01111"}),
            Map.entry('D', new String[]{"11110", "10001", "10001", "10001", "10001", "10001", "11110"}),
            Map.entry('E', new String[]{"11111", "10000", "10000", "11110", "10000", "10000", "11111"}),
            Map.entry('F', new String[]{"11111", "10000", "10000", "11110", "10000", "10000", "10000"}),
            Map.entry('G', new String[]{"01111", "10000", "10000", "10011", "10001", "10001", "01111"}),
            Map.entry('H', new String[]{"10001", "10001", "10001", "11111", "10001", "10001", "10001"}),
            Map.entry('J', new String[]{"00111", "00010", "00010", "00010", "10010", "10010", "01100"}),
            Map.entry('K', new String[]{"10001", "10010", "10100", "11000", "10100", "10010", "10001"}),
            Map.entry('L', new String[]{"10000", "10000", "10000", "10000", "10000", "10000", "11111"}),
            Map.entry('M', new String[]{"10001", "11011", "10101", "10101", "10001", "10001", "10001"}),
            Map.entry('N', new String[]{"10001", "11001", "10101", "10011", "10001", "10001", "10001"}),
            Map.entry('P', new String[]{"11110", "10001", "10001", "11110", "10000", "10000", "10000"}),
            Map.entry('Q', new String[]{"01110", "10001", "10001", "10001", "10101", "10010", "01101"}),
            Map.entry('R', new String[]{"11110", "10001", "10001", "11110", "10100", "10010", "10001"}),
            Map.entry('S', new String[]{"01111", "10000", "10000", "01110", "00001", "00001", "11110"}),
            Map.entry('T', new String[]{"11111", "00100", "00100", "00100", "00100", "00100", "00100"}),
            Map.entry('U', new String[]{"10001", "10001", "10001", "10001", "10001", "10001", "01110"}),
            Map.entry('V', new String[]{"10001", "10001", "10001", "10001", "01010", "01010", "00100"}),
            Map.entry('W', new String[]{"10001", "10001", "10001", "10101", "10101", "11011", "10001"}),
            Map.entry('X', new String[]{"10001", "01010", "00100", "00100", "00100", "01010", "10001"}),
            Map.entry('Y', new String[]{"10001", "01010", "00100", "00100", "00100", "00100", "00100"}),
            Map.entry('Z', new String[]{"11111", "00001", "00010", "00100", "01000", "10000", "11111"})
    );

    private CaptchaImageRenderer() {
    }

    /** 渲染点阵 PNG 验证码并返回 Base64。 */
    static String pngBase64(String value, SecureRandom random) {
        byte[] pixels = new byte[WIDTH * HEIGHT * 3];
        for (int i = 0; i < pixels.length; i += 3) {
            pixels[i] = (byte) 248;
            pixels[i + 1] = (byte) 250;
            pixels[i + 2] = (byte) 252;
        }
        byte[] lineColor = {(byte) 148, (byte) 163, (byte) 184};
        byte[] textColor = {(byte) 15, (byte) 23, (byte) 42};
        for (int i = 0; i < NOISE_LINES; i++) {
            drawLine(pixels, random.nextInt(WIDTH), random.nextInt(HEIGHT),
                    random.nextInt(WIDTH), random.nextInt(HEIGHT), lineColor);
        }
        for (int index = 0; index < value.length(); index++) {
            drawGlyph(pixels, value.charAt(index),
                    18 + index * 28 + random.nextInt(3),
                    8 + random.nextInt(4),
                    4,
                    textColor);
        }
        try {
            return Base64.getEncoder().encodeToString(encodePng(pixels));
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to encode captcha PNG", ex);
        }
    }

    /** 按点阵字形绘制单个字符。 */
    private static void drawGlyph(byte[] pixels, char ch, int x, int y, int scale, byte[] color) {
        String[] glyph = GLYPHS.get(ch);
        if (glyph == null) {
            return;
        }
        for (int rowIndex = 0; rowIndex < glyph.length; rowIndex++) {
            String row = glyph[rowIndex];
            for (int columnIndex = 0; columnIndex < row.length(); columnIndex++) {
                if (row.charAt(columnIndex) != '1') {
                    continue;
                }
                for (int dy = 0; dy < scale; dy++) {
                    for (int dx = 0; dx < scale; dx++) {
                        setPixel(pixels, x + columnIndex * scale + dx, y + rowIndex * scale + dy, color);
                    }
                }
            }
        }
    }

    /** Bresenham 画线（噪点线）。 */
    private static void drawLine(byte[] pixels, int x1, int y1, int x2, int y2, byte[] color) {
        int dx = Math.abs(x2 - x1);
        int dy = -Math.abs(y2 - y1);
        int sx = x1 < x2 ? 1 : -1;
        int sy = y1 < y2 ? 1 : -1;
        int error = dx + dy;
        while (true) {
            setPixel(pixels, x1, y1, color);
            if (x1 == x2 && y1 == y2) {
                break;
            }
            int e2 = 2 * error;
            if (e2 >= dy) {
                error += dy;
                x1 += sx;
            }
            if (e2 <= dx) {
                error += dx;
                y1 += sy;
            }
        }
    }

    private static void setPixel(byte[] pixels, int x, int y, byte[] color) {
        if (x < 0 || x >= WIDTH || y < 0 || y >= HEIGHT) {
            return;
        }
        int offset = (y * WIDTH + x) * 3;
        pixels[offset] = color[0];
        pixels[offset + 1] = color[1];
        pixels[offset + 2] = color[2];
    }

    /** 将 RGB 像素编码为最小 PNG（IHDR/IDAT/IEND）。 */
    private static byte[] encodePng(byte[] rgbPixels) throws IOException {
        byte[] raw = new byte[HEIGHT * (1 + WIDTH * 3)];
        for (int row = 0; row < HEIGHT; row++) {
            int dest = row * (1 + WIDTH * 3);
            raw[dest] = 0;
            System.arraycopy(rgbPixels, row * WIDTH * 3, raw, dest + 1, WIDTH * 3);
        }
        ByteArrayOutputStream png = new ByteArrayOutputStream();
        png.write(new byte[]{(byte) 0x89, 'P', 'N', 'G', '\r', '\n', 0x1a, '\n'});
        png.write(chunk(new byte[]{'I', 'H', 'D', 'R'}, ihdr()));
        png.write(chunk(new byte[]{'I', 'D', 'A', 'T'}, deflate(raw)));
        png.write(chunk(new byte[]{'I', 'E', 'N', 'D'}, new byte[0]));
        return png.toByteArray();
    }

    private static byte[] ihdr() {
        ByteBuffer buffer = ByteBuffer.allocate(13).order(ByteOrder.BIG_ENDIAN);
        buffer.putInt(WIDTH);
        buffer.putInt(HEIGHT);
        buffer.put((byte) 8);
        buffer.put((byte) 2);
        buffer.put((byte) 0);
        buffer.put((byte) 0);
        buffer.put((byte) 0);
        return buffer.array();
    }

    private static byte[] deflate(byte[] raw) {
        Deflater deflater = new Deflater(Deflater.BEST_COMPRESSION);
        deflater.setInput(raw);
        deflater.finish();
        byte[] buffer = new byte[raw.length + 64];
        int length = deflater.deflate(buffer);
        deflater.end();
        byte[] out = new byte[length];
        System.arraycopy(buffer, 0, out, 0, length);
        return out;
    }

    private static byte[] chunk(byte[] type, byte[] data) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream(8 + data.length + 4);
        out.write(ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(data.length).array());
        out.write(type);
        out.write(data);
        CRC32 crc = new CRC32();
        crc.update(type);
        crc.update(data);
        out.write(ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt((int) crc.getValue()).array());
        return out.toByteArray();
    }
}
