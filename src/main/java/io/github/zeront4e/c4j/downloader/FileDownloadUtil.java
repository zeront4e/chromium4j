package io.github.zeront4e.c4j.downloader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

class FileDownloadUtil {
    public interface DownloadProgressCallback {
        void onDownloadProgress(long totalDownloadedBytes);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(FileDownloadUtil.class);

    private static final HttpClient httpClient = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.ALWAYS)
            .build();

    private static final int BUFFER_SIZE = 8 * 1024 * 1024; //Use 8 MiB as buffer size.
    private static final int LOG_INTERVAL_MB = 1024 * 1024; //Use 1 MiB as threshold.

    public static void downloadFileOrFail(String fileUrl, File file) throws Exception {
        DownloadProgressCallback downloadProgressCallback = totalDownloadedBytes ->
                LOGGER.debug("Downloaded {} MiB...", totalDownloadedBytes / (1024 * 1024));

        downloadFileOrFail(fileUrl, file, downloadProgressCallback);
    }

    public static void downloadFileOrFail(String fileUrl, File file, DownloadProgressCallback downloadProgressCallback) throws Exception {
        try {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(fileUrl))
                    .build();

            HttpResponse<InputStream> httpResponse = httpClient.send(httpRequest,
                    HttpResponse.BodyHandlers.ofInputStream());

            if (httpResponse.statusCode() != 200) {
                LOGGER.warn("Failed to download file \"{}\". HTTP status code: {}", file.getName(),
                        httpResponse.statusCode());

                throw new Exception("Failed to download file \"" + file.getName() + "\". HTTP status code: " +
                        httpResponse.statusCode());
            }

            try (InputStream inputStream = httpResponse.body();
                 FileOutputStream fileOutputStream = new FileOutputStream(file);

                 BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream, BUFFER_SIZE)) {

                byte[] buffer = new byte[BUFFER_SIZE];

                int bytesRead;

                long totalBytes = 0;

                long nextLogThreshold = LOG_INTERVAL_MB;

                LOGGER.info("Starting download: {}", file.getName());

                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    bufferedOutputStream.write(buffer, 0, bytesRead);
                    totalBytes += bytesRead;

                    if (totalBytes >= nextLogThreshold) {
                        downloadProgressCallback.onDownloadProgress(totalBytes);

                        nextLogThreshold += LOG_INTERVAL_MB;
                    }
                }

                LOGGER.info("Download of \"{}\" completed. Total size: {} MiB", file.getName(),
                        totalBytes / (1024 * 1024));
            }
        }
        catch (Exception exception) {
            LOGGER.error("Error downloading file \"{}\".", file.getName(), exception);

            throw exception;
        }
    }
}
