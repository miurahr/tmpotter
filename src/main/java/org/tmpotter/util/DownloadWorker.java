package org.tmpotter.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URI;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Download by swing worker thread.
 * @author Hiroshi Miura
 */
public class DownloadWorker extends javax.swing.SwingWorker<File, Long> {
    private File tempDirectory;
    private URI target;
    private long total = 0;
    private IDownloadCallback cb;

    private static final Logger LOGGER = LoggerFactory.getLogger(DownloadWorker.class.getName());

    public interface IDownloadCallback {
        void progress(final Long progress);
        void getResult(final File result);
    }

    public DownloadWorker(final File tempDirectory, final URI target, IDownloadCallback cb) {
        this.tempDirectory = tempDirectory;
        this.target = target;
        this.cb = cb;
    }

    @Override
    public File doInBackground() {
        try {
            File downloaded = MediaWikiDownloader.download(target, tempDirectory);
            if (downloaded != null) {
                publish(downloaded.length());
                return downloaded;
            }
        } catch (Exception ex) {
            LOGGER.info("Mediawiki downloader:", ex);
        }
        return null;
    }

    @Override
    protected void process(List<Long> chunks) {
        for (Long len : chunks) {
            total += len;
        }
        cb.progress(total);
    }

    @Override
    protected void done() {
        try {
            File downloaded = get();
            if (downloaded != null) {
                cb.getResult(downloaded);
            } else {
                //ShowEoor();
            }
        } catch (InterruptedException iex) {
            //ShowErrorDialog("Timeout");
        } catch (ExecutionException eex) {
            //ShowErrorDialog("error");
        }
    }
}
