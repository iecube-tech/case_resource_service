package com.iecube.community.model.exportProgress.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

@Slf4j
public class FileCompressor {

    public static File compressToTarGz(List<File> files, String outputFileName) throws IOException {
        File tarGzFile = new File(outputFileName);

        try (FileOutputStream fos = new FileOutputStream(tarGzFile);
            GzipCompressorOutputStream gzos = new GzipCompressorOutputStream(fos);
            TarArchiveOutputStream tos = new TarArchiveOutputStream(gzos)) {
            tos.setLongFileMode(TarArchiveOutputStream.LONGFILE_POSIX);
            for (File file : files) {
                TarArchiveEntry entry = new TarArchiveEntry(file);
                entry.setName(file.getName());
                tos.putArchiveEntry(entry);

                try (FileInputStream fis = new FileInputStream(file)) {
                    IOUtils.copy(fis, tos);
                }

                tos.closeArchiveEntry();
            }

            tos.finish();
        }

        log.info("压缩文件生成成功: {}", tarGzFile.getAbsolutePath());
        return tarGzFile;
    }
}
