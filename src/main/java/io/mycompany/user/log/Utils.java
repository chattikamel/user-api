package io.mycompany.user.log;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.core.io.buffer.DataBuffer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import java.util.Optional;

import static java.nio.charset.StandardCharsets.UTF_8;

@Slf4j
public class Utils {
    public static Optional<String> payload(DataBuffer dataBuffer) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Channels.newChannel(baos).write(dataBuffer.toByteBuffer());
            return Optional.of(baos.toString(UTF_8));
        } catch (IOException e) {
            log.error("Error when logging response", e);
        }
        return Optional.empty();
    }
}
