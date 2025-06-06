package com.ru.ami.hse.elgupo.photoTests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import com.ru.ami.hse.elgupo.serverrequests.clientPhoto.PhotoServerRequester;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@RunWith(MockitoJUnitRunner.class)
public class PhotoServerRequesterTest {

    private File realFile;
    private final Long userID = 1L;
    private final Long emptyUserID = 99L;

    @Before
    public void setUp() throws Exception {
        Context context = InstrumentationRegistry.getInstrumentation().getContext();

        InputStream is = context.getResources().openRawResource(com.ru.ami.hse.elgupo.test.R.raw.test_snoopy);
        realFile = File.createTempFile("test_snoopy", ".jpg");
        copyInputStreamToFile(is, realFile);
    }

    private void copyInputStreamToFile(InputStream in, File file) throws IOException {
        try (OutputStream out = new FileOutputStream(file)) {
            byte[] buf = new byte[8192];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
        } finally {
            in.close();
        }
    }


    @Test
    public void ComplexTest() throws InterruptedException {
        uploadPhotoTest();
        getPhotoTest();
        deletePhoto();
    }

    public void uploadPhotoTest() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean successResult = new AtomicBoolean(false);
        PhotoServerRequester.uploadPhoto(userID, realFile, new PhotoServerRequester.ApiCallback<URL>() {
            @Override
            public void onSuccess(URL result) {
                successResult.set(true);
                latch.countDown();
            }

            @Override
            public void onError(String error) {
                successResult.set(false);
                latch.countDown();
            }
        });
        assertTrue("Callback not called after 2 seconds", latch.await(2, TimeUnit.SECONDS));
        assertTrue("Success!", successResult.get());
    }

    public void getPhotoTest() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean successResult = new AtomicBoolean(false);
        PhotoServerRequester.getPhoto(userID, new PhotoServerRequester.ApiCallback<URL>() {
            @Override
            public void onSuccess(URL result) {
                successResult.set(true);
                latch.countDown();
            }

            @Override
            public void onError(String error) {
                successResult.set(false);
                latch.countDown();
            }
        });
        assertTrue("Callback not called after 2 seconds", latch.await(2, TimeUnit.SECONDS));
        assertTrue("Success result", successResult.get());
    }

    public void deletePhoto() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean successResult = new AtomicBoolean(false);
        PhotoServerRequester.deletePhoto(userID, new PhotoServerRequester.ApiCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                successResult.set(result);
                latch.countDown();
            }

            @Override
            public void onError(String error) {
                successResult.set(false);
                latch.countDown();
            }
        });
        assertTrue("Callback not called after 2 seconds", latch.await(2, TimeUnit.SECONDS));
        assertTrue("Success result", successResult.get());
    }

    @Test
    public void deleteNothing() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean errorFlag = new AtomicBoolean(false);
        AtomicBoolean successResult = new AtomicBoolean(false);
        PhotoServerRequester.deletePhoto(userID, new PhotoServerRequester.ApiCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                successResult.set(result);
                latch.countDown();
            }

            @Override
            public void onError(String error) {
                errorFlag.set(true);
                latch.countDown();
            }
        });
        assertTrue("Callback not called after 2 seconds", latch.await(2, TimeUnit.SECONDS));
        assertFalse("No errors", errorFlag.get());
        assertFalse("Success result", successResult.get());
    }

    @Test
    public void getNothing() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean errorFlag = new AtomicBoolean(false);
        AtomicBoolean successResult = new AtomicBoolean(false);
        PhotoServerRequester.getPhoto(emptyUserID, new PhotoServerRequester.ApiCallback<URL>() {
            @Override
            public void onSuccess(URL result) {
                successResult.set(true);
                latch.countDown();
            }

            @Override
            public void onError(String error) {
                errorFlag.set(true);
                latch.countDown();
            }
        });
        assertTrue("Callback not called after 2 seconds", latch.await(2, TimeUnit.SECONDS));
        assertFalse("Success result", successResult.get());
        assertTrue("Error appeared", errorFlag.get());
    }
}
