/*
 * Copyright (C) 2010 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.ownpass.client.android;

import android.graphics.Bitmap;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.util.Map;

import net.ownpass.BinaryBitmap;
import net.ownpass.DecodeHintType;
import net.ownpass.FakeR;
import net.ownpass.LuminanceSource;
import net.ownpass.MultiFormatReader;
import net.ownpass.PlanarYUVLuminanceSource;
import net.ownpass.ReaderException;
import net.ownpass.Result;
import net.ownpass.common.HybridBinarizer;

final class DecodeHandler extends Handler {

  private static final String TAG = DecodeHandler.class.getSimpleName();

  private final CaptureActivity activity;
  private final MultiFormatReader multiFormatReader;
  private boolean running = true;

  private static FakeR fakeR;
  DecodeHandler(CaptureActivity activity, Map<DecodeHintType,Object> hints) {
	fakeR = new FakeR(activity);
    multiFormatReader = new MultiFormatReader();
    multiFormatReader.setHints(hints);
    this.activity = activity;
  }

  @Override
  public void handleMessage(Message message) {
    if (!running) {
      return;
    }
    if (message.what == fakeR.getId("id", "decode")) {
        decode((byte[]) message.obj, message.arg1, message.arg2);
    } else if (message.what == fakeR.getId("id", "quit")) {
        running = false;
        Looper.myLooper().quit();
    }
  }

  /**
   * Decode the data within the viewfinder rectangle, and time how long it took. For efficiency,
   * reuse the same reader objects from one decode to the next.
   *
   * @param data   The YUV preview frame.
   * @param width  The width of the preview frame.
   * @param height The height of the preview frame.
   */
  private void decode(byte[] data, int width, int height) {
    long start = System.currentTimeMillis();
    Result rawResult = null;
    PlanarYUVLuminanceSource source = activity.getCameraManager().buildLuminanceSource(data, width, height);
    if (source != null) {
      BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
      try {
        rawResult = multiFormatReader.decodeWithState(bitmap);
      } catch (ReaderException re) {
        // continue
      } finally {
        multiFormatReader.reset();
      }
    }

    Handler handler = activity.getHandler();
    if (rawResult != null) {
      // Don't log the barcode contents for security.
      long end = System.currentTimeMillis();
      Log.d(TAG, "Found barcode in " + (end - start) + " ms");
      if (handler != null) {
        Message message = Message.obtain(handler, fakeR.getId("id", "decode_succeeded"), rawResult);
        Bundle bundle = new Bundle();
        Bitmap grayscaleBitmap = toBitmap(source, source.renderCroppedGreyscaleBitmap());
        bundle.putParcelable(DecodeThread.BARCODE_BITMAP, grayscaleBitmap);
        message.setData(bundle);
        message.sendToTarget();
      }
    } else {
      if (handler != null) {
        Message message = Message.obtain(handler, fakeR.getId("id", "decode_failed"));
        message.sendToTarget();
      }
    }
  }

  private static Bitmap toBitmap(LuminanceSource source, int[] pixels) {
    int width = source.getWidth();
    int height = source.getHeight();
    Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
    bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
    return bitmap;
  }

}
