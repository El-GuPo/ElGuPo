package com.ru.ami.hse.elgupo.profile.photo;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class downloadUtils {
    public static File downloadFileFromUri(Context context, Uri uri, Long userId) throws IOException {
        ContentResolver cR = context.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        String extension;
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            extension = mime.getExtensionFromMimeType(context.getContentResolver().getType(uri));
        } else {
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());
        }
        File tempFile = File.createTempFile("user_photo_" + userId, "." + extension, context.getCacheDir());

        try (InputStream in = cR.openInputStream(uri); OutputStream out = new FileOutputStream(tempFile)) {
            byte[] buf = new byte[8192];
            int len;
            while ((len = in.read(buf)) != -1) {
                out.write(buf, 0, len);
            }
        } catch (IOException e) {
            tempFile.delete();
            throw e;
        }
        return tempFile;
    }
}
