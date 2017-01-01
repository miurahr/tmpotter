/* *************************************************************************
 *
 *  TMPotter - Bi-text Aligner/TMX Editor
 *
 *  Copyright (C) 2016 Hiroshi Miura
 *
 *  This file is part of TMPotter.
 *
 *  TMPotter is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  TMPotter is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with TMPotter.  If not, see http://www.gnu.org/licenses/.
 *
 * *************************************************************************/

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
        /**
         * Report download progress.
         * @param progress bytes downloaded.
         */
        void progress(final Long progress);

        /**
         * Get download result.
          * @param result resulted file.
         */
        void getResult(final File result);
    }

    /**
     * Constructor.
     * @param tempDirectory directory to place downloaded files.
     * @param target target URI.
     * @param cb callback.
     */
    public DownloadWorker(final File tempDirectory, final URI target, IDownloadCallback cb) {
        this.tempDirectory = tempDirectory;
        this.target = target;
        this.cb = cb;
    }

    /**
     * Swing worker background executor.
     * @return File downloaded.
     */
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

    /**
     * Swing worker process callback.
     * @param chunks bytes to downloaded.
     */
    @Override
    protected void process(List<Long> chunks) {
        for (Long len : chunks) {
            total += len;
        }
        cb.progress(total);
    }

    /**
     * Swing worker done callback.
     */
    @Override
    protected void done() {
        try {
            File downloaded = get();
            if (downloaded != null) {
                cb.getResult(downloaded);
            } else {
                // FIXME.
                //ShowEoor();
            }
        } catch (InterruptedException iex) {
            //ShowErrorDialog("Timeout");
        } catch (ExecutionException eex) {
            //ShowErrorDialog("error");
        }
    }
}
