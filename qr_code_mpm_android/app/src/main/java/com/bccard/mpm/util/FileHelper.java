/****************************************************************************************
 * 이 소스는 ㈜비씨카드 소유입니다.
 * 이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
 * 클래스명   : FileHelper
 * 작성자명   : 20170448
 * 상세설명   : 파일 관련 Helper 클래스
 * 적용범위   : mpm
 * 작성일자   : 2017.12.13
 * @요청자 :
 * @결재자 :
 * @개발자 :
 ************************** 수정이력 ********************************************************
 * 수정일자   :
 * 수정내용   :
 * @요청자 :
 * @결재자 :
 * @개발자 :
 ****************************************************************************************/

package com.bccard.mpm.util;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileHelper {

    public final static String FOLDER_NAME = "mpm";

    /**
     * 디렉토리 경로
     */
    public static File getDirectory(Context context) {
        File directory = new File(context.getExternalFilesDir(null), FOLDER_NAME);

        if (!directory.exists()) {
            directory.mkdirs();
        } else {
            LogHelper.i("Directory Already Create");
        }

        directory.setExecutable(false, true);
        directory.setReadable(true);
        directory.setWritable(false, true);

        return directory;
    }

    /**
     * (dir/file) 절대 경로 얻어오기
     */
    public static String getAbsolutePath(File file) {
        return file.getAbsolutePath();
    }

    /**
     * (dir/file) 삭제 하기
     */
    public static boolean deleteFile(File file) {
        boolean result;

        if (file != null && file.exists()) {
            file.delete();
            result = true;
        } else {
            result = false;
        }
        return result;
    }

    public static void deleteAllFile(Context context) {
        String path = FileHelper.getDirectory(context).getAbsolutePath();
        String[] directoryList = FileHelper.getList(FileHelper.getDirectory(context));
        if (directoryList != null && directoryList.length > 0) {
            for (int i = 0; i < directoryList.length; i++) {
                File deleteFile = new File(path + "/" + directoryList[i]);
                deleteFile(deleteFile);
            }
        }
    }

    public static void deleteAllFileWithDirectory(Context context) {
        String path = FileHelper.getDirectory(context).getAbsolutePath();
        String[] directoryList = FileHelper.getList(FileHelper.getDirectory(context));
        if (directoryList != null && directoryList.length > 0) {
            for (int i = 0; i < directoryList.length; i++) {
                File deleteFile = new File(path + "/" + directoryList[i]);
                deleteFile(deleteFile);
            }
        }
    }

    /**
     * 파일여부 체크 하기
     */
    public static boolean isFile(File file) {
        boolean result;

        result = file != null && file.exists() && file.isFile();

        return result;
    }

    /**
     * 디렉토리에 안에 내용을 보여 준다.
     */
    public static String[] getList(File dir) {
        if (dir != null && dir.exists())
            return dir.list();
        return null;
    }

    /**
     * 파일 이름
     */
    public static String getFileTimeName() {

        long systemTime = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        Date dateFormat = new Date(systemTime);
        String timeFileName = sdf.format(dateFormat);

        return timeFileName;
    }

    /**
     * 폴더 생성
     */
    public static File makeDirectory(String dirPath) {
        File directory = new File(dirPath);

        if (!directory.exists()) {
            directory.mkdirs();
            LogHelper.i("Make Directory : " + dirPath);
        } else {
        }

        return directory;
    }

    /**
     * 파일 생성
     */
    public static File makeFile(Context context, String fileName) {
        File file = null;
        File directory = getDirectory(context);
        boolean isSuccess = false;

        if (directory.isDirectory()) {
            file = new File(directory.getAbsolutePath() + "/" + fileName);

            if (file != null && !file.exists()) {
                try {
                    isSuccess = file.createNewFile();
                } catch (IOException e) {
                    LogHelper.printException(e);
                }
            }
        }

        return file;
    }

    public static String getFilePathFromUri(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {

            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            } // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        } // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static Uri getUriFromFilePath(String filePath) {
        return Uri.parse( new File(filePath).toString() );
    }

    public static String getFileName(File file) {
        return file.getName();
    }

    public static String getFilePath(File file) {
        return file.getAbsolutePath();
    }

    public static String getFileOnlyPath(String filePath) {
        int fileNameIndex = filePath.lastIndexOf("/");

        if( fileNameIndex >=0 & fileNameIndex < filePath.length() ) {
            return filePath.substring( 0, fileNameIndex );
        }

        return filePath;
    }

    public static String getFileName(String filePath) {
        int lastIndex = filePath.lastIndexOf("/");

        if( lastIndex >= 0 && lastIndex < filePath.length() ) {
            return filePath.substring(lastIndex+1);
        }
        return "";
    }

    public static String getFileOnlyName(String filePath) {
        String fileName = getFileName( filePath );
        int extensionIndex = fileName.lastIndexOf(".");

        if( extensionIndex >=0 & extensionIndex < fileName.length() ) {
            return fileName.substring( 0, extensionIndex );
        }

        return fileName;
    }

    public static String getFileExtensionName(String filePath) {
        int lastIndex = filePath.lastIndexOf(".");

        if( lastIndex >= 0 && lastIndex < filePath.length() ) {
            return filePath.substring(lastIndex+1);
        }
        return "";
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return null;
    }


    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
}