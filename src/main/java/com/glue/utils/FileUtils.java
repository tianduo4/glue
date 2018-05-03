package com.glue.utils;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

/**
 * @author xushipeng
 * @create 2018-05-03 16:54
 */
@Slf4j
@NoArgsConstructor
public class FileUtils {

    public static String readToString(String file) throws IOException {
        return readToString(Paths.get(file));
    }

    public static String readToString(Path path) throws IOException {
        BufferedReader bufferedReader = Files.newBufferedReader(path);
        return bufferedReader.lines().collect(Collectors.joining());
    }

    public static String readToString(InputStream input) throws IOException {
        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(input, "UTF-8"))) {
            return buffer.lines().collect(Collectors.joining("\n"));
        }
    }

    public static void copyFile(File source, File dest) throws IOException {
        FileChannel inputChannel  = null;
        FileChannel outputChannel = null;
        try {
            inputChannel = new FileInputStream(source).getChannel();
            outputChannel = new FileOutputStream(dest).getChannel();
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
        } finally {
            if (null != inputChannel) {
                inputChannel.close();
            }
            if (null != outputChannel) {
                outputChannel.close();
            }
        }
    }
}
