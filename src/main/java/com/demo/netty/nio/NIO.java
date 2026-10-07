package com.demo.netty.nio;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.stream.IntStream;

import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "C.NIO")
public class NIO {
    private static String srcPath = "C:\\codes\\java\\demos\\demo-app\\1.txt";
    private static String dstPath = srcPath.replace("1", "2");

    // write string to channel to file
    public static void demo() {
        try (FileOutputStream fileOutputStream = new FileOutputStream(srcPath)) {
            String s = "hello zhangjingrui";
            // 1. channel between buffer and file
            FileChannel channel = fileOutputStream.getChannel();
            
            // 2. buffer
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            buffer.put(s.getBytes()); // buffer now is write

            buffer.flip(); // buffer now is read
            channel.write(buffer);
        } catch (IOException e) {
            e.printStackTrace(System.out);
        }
    }

    // read file bytes
    public static void demo2() {
        try (FileInputStream inputStream = new FileInputStream(srcPath)) {
            // 1. channel 
            FileChannel fileChannel = inputStream.getChannel();

            // 2. bytes buffer for read file bytes
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

            // 3. read bytes from channel to buffer
            fileChannel.read(byteBuffer);
            System.out.println(new String(byteBuffer.array()));
        } catch (IOException e) {
            e.printStackTrace(System.out);
        }
    }

    // read bytes from 1.txt and write it to 2.txt
    // read to buffer and write buffer to channel
    public static void demo3() {
        try (
            FileInputStream inputStream = new FileInputStream(srcPath);
            FileOutputStream outputStream = new FileOutputStream(dstPath);
        ) {
            // 1. get channels 
            FileChannel inChannel = inputStream.getChannel();
            FileChannel outChannel = outputStream.getChannel();

            // 2. get buffer 
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            
            // 3. read and write
            inChannel.read(buffer);

            buffer.flip(); // read -> write
            outChannel.write(buffer);
        } catch (IOException e) {
            e.printStackTrace(System.out);
        }
    }

    // transfer
    public static void demo4() {
        try (
            FileInputStream fileInputStream = new FileInputStream(srcPath);
            FileOutputStream fileOutputStream = new FileOutputStream(dstPath);
        ) {
            FileChannel sourceChannel = fileInputStream.getChannel();
            FileChannel dstChannel = fileOutputStream.getChannel();

            dstChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void readOnlyBuffer() {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        IntStream.range(0, 64).forEach(i -> buffer.put((byte)i));

        buffer.flip(); // write -> read

        ByteBuffer ronlyBuffer = buffer.asReadOnlyBuffer();
        System.out.println(ronlyBuffer.getClass());

        while (ronlyBuffer.hasRemaining()) {
            System.out.println(ronlyBuffer.get());
        }
        
        try {
            ronlyBuffer.put((byte)100); // throw exception
        } catch (Exception e) {
            System.out.println("ronlyBuffer can't be written");
        }
    }

    public static void main(String[] args) {
        readOnlyBuffer();
    }
}
