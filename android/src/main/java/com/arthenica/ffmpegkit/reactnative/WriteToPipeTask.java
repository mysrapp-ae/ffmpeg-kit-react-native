/*
 * Copyright (c) 2021 Taner Sener
 *
 * This file is part of FFmpegKit.
 *
 * FFmpegKit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FFmpegKit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with FFmpegKit.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.arthenica.ffmpegkit.reactnative;

import static com.arthenica.ffmpegkit.reactnative.FFmpegKitReactNativeModule.LIBRARY_NAME;

import android.util.Log;

import com.facebook.react.bridge.Promise;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class WriteToPipeTask implements Runnable {
  private final String inputPath;
  private final String namedPipePath;
  private final Promise promise;

  public WriteToPipeTask(final String inputPath, final String namedPipePath, final Promise promise) {
    this.inputPath = inputPath;
    this.namedPipePath = namedPipePath;
    this.promise = promise;
  }

  @Override
  public void run() {
    Log.d(LIBRARY_NAME, String.format("Starting copy %s to pipe %s operation.", inputPath, namedPipePath));
    final long startTime = System.currentTimeMillis();

    // SRP-1 (CWE-78): stream-copy, never `sh -c "cat ... > ..."` — paths as file
    // args can't inject shell metacharacters.
    try (final InputStream in = new FileInputStream(inputPath);
         final OutputStream out = new FileOutputStream(namedPipePath)) {
      final byte[] buffer = new byte[8192];
      int bytesRead;
      while ((bytesRead = in.read(buffer)) != -1) {
        out.write(buffer, 0, bytesRead);
      }
      out.flush();

      final long endTime = System.currentTimeMillis();
      Log.d(LIBRARY_NAME, String.format("Copying %s to pipe %s operation completed with rc 0 in %d seconds.", inputPath, namedPipePath, (endTime - startTime) / 1000));
      promise.resolve(0);

    } catch (final IOException e) {
      Log.e(LIBRARY_NAME, String.format("Copy %s to pipe %s failed with error.", inputPath, namedPipePath), e);
      promise.reject("Copy failed", String.format("Copy %s to pipe %s failed with error.", inputPath, namedPipePath), e);
    }
  }

}
