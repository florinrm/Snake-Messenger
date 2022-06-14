package com.example.snakemessenger.general;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.example.snakemessenger.MainActivity;
import com.example.snakemessenger.chats.PreviewVideoActivity;
import com.example.snakemessenger.crypto.CryptoManager;
import com.example.snakemessenger.models.AudioMessage;
import com.example.snakemessenger.models.AudioPart;
import com.example.snakemessenger.models.Contact;
import com.example.snakemessenger.models.FileMessage;
import com.example.snakemessenger.models.FilePart;
import com.example.snakemessenger.models.ImageMessage;
import com.example.snakemessenger.models.ImagePart;
import com.example.snakemessenger.models.MediaMessageUri;
import com.example.snakemessenger.models.Message;
import com.example.snakemessenger.models.MessageExchangeLog;
import com.example.snakemessenger.models.VideoMessage;
import com.example.snakemessenger.models.VideoPart;
import com.example.snakemessenger.notifications.NotificationHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;

import static com.example.snakemessenger.MainActivity.currentChat;
import static com.example.snakemessenger.MainActivity.db;
import static com.example.snakemessenger.MainActivity.myDeviceId;
import static com.example.snakemessenger.services.BackgroundCommunicationService.TAG;

public class Utilities {
    public static Message saveOwnMessageToDatabase(JSONObject messageJSON, long payloadId, int messageStatus) {
        try {
            Message message = null;
            boolean isEncrypted = messageJSON.getBoolean(Constants.JSON_IS_ENCRYPTED);
            String encryptionKey = messageJSON.getString(Constants.JSON_ENCRYPTION_KEY);
            if (isEncrypted) {
                String encryptedMessage = messageJSON.getString(Constants.JSON_MESSAGE_CONTENT_KEY);
                String decryptedMessage = CryptoManager.INSTANCE.decryptMessage(encryptionKey, encryptedMessage);

                message = new Message(
                        0,
                        messageJSON.getString(Constants.JSON_MESSAGE_ID_KEY),
                        payloadId,
                        messageJSON.getInt(Constants.JSON_MESSAGE_TYPE_KEY),
                        messageJSON.getString(Constants.JSON_SOURCE_DEVICE_ID_KEY),
                        messageJSON.getString(Constants.JSON_DESTINATION_DEVICE_ID_KEY),
                        messageJSON.getInt(Constants.JSON_CONTENT_TYPE_KEY),
                        decryptedMessage,
                        messageJSON.getLong(Constants.JSON_MESSAGE_TOTAL_SIZE),
                        messageJSON.getLong(Constants.JSON_MESSAGE_TIMESTAMP_KEY),
                        0,
                        messageStatus
                );
            } else {
                message = new Message(
                        0,
                        messageJSON.getString(Constants.JSON_MESSAGE_ID_KEY),
                        payloadId,
                        messageJSON.getInt(Constants.JSON_MESSAGE_TYPE_KEY),
                        messageJSON.getString(Constants.JSON_SOURCE_DEVICE_ID_KEY),
                        messageJSON.getString(Constants.JSON_DESTINATION_DEVICE_ID_KEY),
                        messageJSON.getInt(Constants.JSON_CONTENT_TYPE_KEY),
                        messageJSON.getString(Constants.JSON_MESSAGE_CONTENT_KEY),
                        messageJSON.getLong(Constants.JSON_MESSAGE_TOTAL_SIZE),
                        messageJSON.getLong(Constants.JSON_MESSAGE_TIMESTAMP_KEY),
                        0,
                        messageStatus
                );
            }
            message.setEncryptionKey(encryptionKey);
            db.getMessageDao().addMessage(message);

            Log.d(MainActivity.TAG, "saveOwnMessageToDatabase: saved Own Message to Room");

            Contact contact;

            if (messageStatus == Constants.MESSAGE_STATUS_SENT) {
                contact = db.getContactDao().findByDeviceId(messageJSON.getString(Constants.JSON_DESTINATION_DEVICE_ID_KEY));
            } else {
                contact = db.getContactDao().findByDeviceId(messageJSON.getString(Constants.JSON_SOURCE_DEVICE_ID_KEY));
            }

            contact.setLastMessageTimestamp(messageJSON.getLong(Constants.JSON_MESSAGE_TIMESTAMP_KEY));

            if (!contact.isChat()) {
                contact.setChat(true);

                Log.d(MainActivity.TAG, "saveOwnMessageToDatabase: user is a new chat contact");
            }

            db.getContactDao().updateContact(contact);

            if (messageStatus == Constants.MESSAGE_STATUS_RECEIVED) {
                MessageExchangeLog messageExchangeLog = new MessageExchangeLog(0,
                        messageJSON.getString(Constants.JSON_SOURCE_DEVICE_ID_KEY),
                        messageJSON.getString(Constants.JSON_DESTINATION_DEVICE_ID_KEY),
                        messageJSON.getLong(Constants.JSON_MESSAGE_TIMESTAMP_KEY),
                        System.currentTimeMillis(),
                        messageJSON.getString(Constants.JSON_ROUTING_NODES)
                );

                db.getMessageExchangeLogDao().addMessageExchangeLog(messageExchangeLog);
            }

            return message;
        } catch (JSONException e) {
            Log.d(MainActivity.TAG, "saveOwnMessageToDatabase: could not save Own Message to Room. Error: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    public static void saveDataMemoryMessageToDatabase(JSONObject messageJSON, long payloadId, int messageStatus) {
        try {
            boolean isEncrypted = messageJSON.getBoolean(Constants.JSON_IS_ENCRYPTED);

            if (isEncrypted) {
                String encryptionKey = messageJSON.getString(Constants.JSON_ENCRYPTION_KEY);
                String encryptedMessage = messageJSON.getString(Constants.JSON_MESSAGE_CONTENT_KEY);
                String decryptedMessage = CryptoManager.INSTANCE.decryptMessage(encryptionKey, encryptedMessage);

                Message message = new Message(
                        0,
                        messageJSON.getString(Constants.JSON_MESSAGE_ID_KEY),
                        payloadId,
                        messageJSON.getInt(Constants.JSON_MESSAGE_TYPE_KEY),
                        messageJSON.getString(Constants.JSON_SOURCE_DEVICE_ID_KEY),
                        messageJSON.getString(Constants.JSON_DESTINATION_DEVICE_ID_KEY),
                        messageJSON.getInt(Constants.JSON_CONTENT_TYPE_KEY),
                        decryptedMessage,
                        messageJSON.getLong(Constants.JSON_MESSAGE_TOTAL_SIZE),
                        messageJSON.getLong(Constants.JSON_MESSAGE_TIMESTAMP_KEY),
                        0,
                        messageStatus
                );

                message.setEncrypted(true);
                message.setEncryptionKey(encryptionKey);
                db.getMessageDao().addMessage(message);
            } else {
                Message message = new Message(
                        0,
                        messageJSON.getString(Constants.JSON_MESSAGE_ID_KEY),
                        payloadId,
                        messageJSON.getInt(Constants.JSON_MESSAGE_TYPE_KEY),
                        messageJSON.getString(Constants.JSON_SOURCE_DEVICE_ID_KEY),
                        messageJSON.getString(Constants.JSON_DESTINATION_DEVICE_ID_KEY),
                        messageJSON.getInt(Constants.JSON_CONTENT_TYPE_KEY),
                        messageJSON.getString(Constants.JSON_MESSAGE_CONTENT_KEY),
                        messageJSON.getLong(Constants.JSON_MESSAGE_TOTAL_SIZE),
                        messageJSON.getLong(Constants.JSON_MESSAGE_TIMESTAMP_KEY),
                        0,
                        messageStatus
                );

                message.setEncrypted(false);
                db.getMessageDao().addMessage(message);
            }

            Log.d(MainActivity.TAG, "saveMessageInfoToDatabase: saved Data Memory Message to Room");
        } catch (JSONException e) {
            Log.d(MainActivity.TAG, "saveDataMemoryMessageToDatabase: could not save Data Memory Message to Room. Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void showImagePickDialog(Context context) {
        String[] options = {Constants.OPTION_CAMERA, Constants.OPTION_GALLERY};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(Constants.PICK_PICTURE_TEXT)
                .setItems(options, (dialogInterface, i) -> {
                    switch (i) {
                        case 0:
                            if (((Activity) context).checkSelfPermission(Manifest.permission.CAMERA) ==
                                    PackageManager.PERMISSION_DENIED) {
                                String[] permission = {Manifest.permission.CAMERA};

                                ((Activity) context).requestPermissions(permission, Constants.REQUEST_IMAGE_CAPTURE);
                            } else {
                                Utilities.dispatchTakePictureIntent(context);
                            }

                            break;

                        case 1:
                            if (context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                                    PackageManager.PERMISSION_DENIED) {
                                String[] permission = {Manifest.permission.READ_EXTERNAL_STORAGE};

                                ((Activity) context).requestPermissions(permission, Constants.REQUEST_ACCESS_GALLERY);
                            } else {
                                Utilities.dispatchPickPictureIntent(context);
                            }
                    }
                })
                .show();
    }

    public static void showVideoPickDialog(Context context) {
        String[] options = {Constants.OPTION_CAMERA, Constants.OPTION_GALLERY};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(Constants.PICK_PICTURE_TEXT)
                .setItems(options, (dialogInterface, i) -> {
                    switch (i) {
                        case 0:
                            if (((Activity) context).checkSelfPermission(Manifest.permission.CAMERA) ==
                                    PackageManager.PERMISSION_DENIED) {
                                String[] permission = {Manifest.permission.CAMERA};

                                ((Activity) context).requestPermissions(permission, Constants.REQUEST_VIDEO_CAPTURE);
                            } else {
                                Utilities.dispatchTakeVideoIntent(context);
                            }

                            break;

                        case 1:
                            if (context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                                    PackageManager.PERMISSION_DENIED) {
                                String[] permission = {Manifest.permission.READ_EXTERNAL_STORAGE};

                                ((Activity) context).requestPermissions(permission, Constants.REQUEST_ACCESS_GALLERY);
                            } else {
                                Utilities.dispatchPickVideoIntent(context);
                            }
                    }
                })
                .show();
    }

    public static void dispatchAttachFileIntent(Context context) {
        if (context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_DENIED) {
            String[] permission = {Manifest.permission.READ_EXTERNAL_STORAGE};

            ((Activity) context).requestPermissions(permission, Constants.REQUEST_ACCESS_FILE);
        } else {
            Intent attachFileIntent = new Intent(Intent.ACTION_GET_CONTENT);
            attachFileIntent.setType("application/*");

            try {
                ((Activity) context).startActivityForResult(attachFileIntent, Constants.REQUEST_ACCESS_FILE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void dispatchTakePictureIntent(Context context) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        try {
            ((Activity) context).startActivityForResult(takePictureIntent, Constants.REQUEST_IMAGE_CAPTURE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void dispatchTakeVideoIntent(Context context) {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

        try {
            ((Activity) context).startActivityForResult(takeVideoIntent, Constants.REQUEST_VIDEO_CAPTURE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void dispatchPickPictureIntent(Context context) {
        Intent pickPictureIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        try {
            ((Activity) context).startActivityForResult(pickPictureIntent, Constants.REQUEST_ACCESS_GALLERY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void dispatchPickVideoIntent(Context context) {
        Intent pickVideoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);

        try {
            ((Activity) context).startActivityForResult(pickVideoIntent, Constants.REQUEST_ACCESS_VIDEO_GALLERY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void dispatchRecordAudioIntent(Context context) {
        if (context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_DENIED) {
            String[] permission = {Manifest.permission.READ_EXTERNAL_STORAGE};

            ((Activity) context).requestPermissions(permission, Constants.REQUEST_AUDIO_CAPTURE);
        } if (context.checkSelfPermission(Manifest.permission.RECORD_AUDIO) ==
                PackageManager.PERMISSION_DENIED) {
            String[] permission = {Manifest.permission.RECORD_AUDIO};

            ((Activity) context).requestPermissions(permission, Constants.REQUEST_AUDIO_CAPTURE);
        } else {
            Intent attachFileIntent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);

            try {
                ((Activity) context).startActivityForResult(attachFileIntent, Constants.REQUEST_AUDIO_CAPTURE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void saveImageToDatabase(Context context, Contact contact, ImageMessage imageMessage) {
        List<ImagePart> imageParts = imageMessage.getParts();
        Collections.sort(imageParts);

        ByteBuffer imageByteBuffer = ByteBuffer.allocate(imageMessage.getTotalSize());

        for (ImagePart part : imageParts) {
            Log.d(TAG, "saveImageToDatabase: adding chunk " + part.getPartNo());
            imageByteBuffer.put(part.getContent());
        }

        byte[] imageBytes = imageByteBuffer.array();
        Log.d(TAG, "saveImageToDatabase: image byte array has size " + imageBytes.length);
        Bitmap imageBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

        if (imageBitmap == null) {
            Log.d(TAG, "saveImageToDatabase: image bitmap is null!");
            return;
        }

        String imagePath = MediaStore.Images.Media.insertImage(context.getContentResolver(), imageBitmap, imageMessage.getMessageId(), null);

        JSONObject messageJSON = new JSONObject();

        try {
            String encryptionKey = CryptoManager.INSTANCE.generateKey();
            String encryptedMessage = CryptoManager.INSTANCE.encryptMessage(encryptionKey, imagePath);
            messageJSON.put(Constants.JSON_MESSAGE_ID_KEY, imageMessage.getMessageId());
            messageJSON.put(Constants.JSON_SOURCE_DEVICE_ID_KEY, imageMessage.getSourceId());
            messageJSON.put(Constants.JSON_DESTINATION_DEVICE_ID_KEY, imageMessage.getDestinationId());
            messageJSON.put(Constants.JSON_MESSAGE_TIMESTAMP_KEY, imageMessage.getTimestamp());
            messageJSON.put(Constants.JSON_CONTENT_TYPE_KEY, Constants.CONTENT_IMAGE);
            messageJSON.put(Constants.JSON_MESSAGE_TYPE_KEY, Constants.MESSAGE_TYPE_MESSAGE);
            messageJSON.put(Constants.JSON_ENCRYPTION_KEY, encryptionKey);
            messageJSON.put(Constants.JSON_IS_ENCRYPTED, true);
            messageJSON.put(Constants.JSON_MESSAGE_CONTENT_KEY, encryptedMessage);
            messageJSON.put(Constants.JSON_MESSAGE_TOTAL_SIZE, imageMessage.getTotalSize());
        } catch (JSONException e) {
            Log.d(TAG, "saveImageToDatabase: could not save image. Error: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        String destinationId = imageMessage.getDestinationId();

        if (destinationId.equals(myDeviceId)) {
            Log.d(TAG, "saveImageToDatabase: the message is for the current device");

            Message receivedMessage = saveOwnMessageToDatabase(messageJSON, imageMessage.getPayloadId(), Constants.MESSAGE_STATUS_RECEIVED);

            if (currentChat == null || !currentChat.equals(contact.getDeviceID())) {
                NotificationHandler.sendMessageNotification(context, contact, receivedMessage);
            }
        } else {
            Log.d(TAG, "saveImageToDatabase: the message is routing to another device");

            String routingNodes;
            try {
                routingNodes = messageJSON.getString(Constants.JSON_ROUTING_NODES);
                routingNodes += myDeviceId + ";";
                messageJSON.put(Constants.JSON_ROUTING_NODES, routingNodes);
                saveDataMemoryMessageToDatabase(messageJSON, imageMessage.getPayloadId(), Constants.MESSAGE_STATUS_ROUTING);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static void saveFileToDatabase(Context context, Contact contact, FileMessage fileMessage) {
        List<FilePart> fileParts = fileMessage.getParts();
        Collections.sort(fileParts);

        ByteBuffer fileByteBuffer = ByteBuffer.allocate(fileMessage.getTotalSize());

        for (FilePart part : fileParts) {
            Log.d(TAG, "saveFileToDatabase: adding chunk " + part.getPartNo());
            fileByteBuffer.put(part.getContent());
        }

        byte[] fileBytes = fileByteBuffer.array();
        Log.d(TAG, "saveFileToDatabase: file byte array has size " + fileBytes.length);
        String filename = fileMessage.getFileName();
        Log.d(TAG, "Filename: " + filename);

        String filePath = "";

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString();
            File file = new File(path, filename);
            filePath = file.getAbsolutePath();
            try {
                FileOutputStream stream = new FileOutputStream(file);
                stream.write(fileBytes);
                stream.close();
            } catch (IOException e) {
                Log.d(TAG, "saveFileToDatabase: could not save file in storage. Error: " + e.getMessage());
                e.printStackTrace();
                return;
            }
        } else {
            try {
                ContentValues values = new ContentValues();
                values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileMessage.getFileName());
                values.put(MediaStore.MediaColumns.MIME_TYPE, fileMessage.getFileExtension());
                values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS + "/SnakeMessenger/");

                Uri uri = context.getContentResolver().insert(MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL), values);
                OutputStream stream = context.getContentResolver().openOutputStream(uri);
                filePath = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL).getPath();
                stream.write(fileBytes);
                stream.close();
            } catch (IOException e) {
                Log.d(TAG, "saveFileToDatabase: could not save file in storage. Error: " + e.getMessage());
                e.printStackTrace();
                return;
            }
        }

        JSONObject messageJSON = new JSONObject();

        try {
            String encryptionKey = CryptoManager.INSTANCE.generateKey();
            String encryptedMessage = CryptoManager.INSTANCE.encryptMessage(encryptionKey, filePath);
            messageJSON.put(Constants.JSON_MESSAGE_ID_KEY, fileMessage.getMessageId());
            messageJSON.put(Constants.JSON_SOURCE_DEVICE_ID_KEY, fileMessage.getSourceId());
            messageJSON.put(Constants.JSON_DESTINATION_DEVICE_ID_KEY, fileMessage.getDestinationId());
            messageJSON.put(Constants.JSON_MESSAGE_TIMESTAMP_KEY, fileMessage.getTimestamp());
            messageJSON.put(Constants.JSON_CONTENT_TYPE_KEY, Constants.CONTENT_FILE);
            messageJSON.put(Constants.JSON_MESSAGE_TYPE_KEY, Constants.MESSAGE_TYPE_MESSAGE);
            messageJSON.put(Constants.JSON_ENCRYPTION_KEY, encryptionKey);
            messageJSON.put(Constants.JSON_IS_ENCRYPTED, true);
            messageJSON.put(Constants.JSON_MESSAGE_CONTENT_KEY, encryptedMessage);
            messageJSON.put(Constants.JSON_MESSAGE_TOTAL_SIZE, fileMessage.getTotalSize());
        } catch (JSONException e) {
            Log.d(TAG, "saveFileToDatabase: could not save file. Error: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        String destinationId = fileMessage.getDestinationId();

        if (destinationId.equals(myDeviceId)) {
            Log.d(TAG, "saveFileToDatabase: the message is for the current device");

            Message receivedMessage = saveOwnMessageToDatabase(messageJSON, fileMessage.getPayloadId(), Constants.MESSAGE_STATUS_RECEIVED);

            if (currentChat == null || !currentChat.equals(contact.getDeviceID())) {
                NotificationHandler.sendMessageNotification(context, contact, receivedMessage);
            }
        } else {
            Log.d(TAG, "saveFileToDatabase: the message is routing to another device");

            String routingNodes = null;
            try {
                routingNodes = messageJSON.getString(Constants.JSON_ROUTING_NODES);
                routingNodes += myDeviceId + ";";
                messageJSON.put(Constants.JSON_ROUTING_NODES, routingNodes);
                saveDataMemoryMessageToDatabase(messageJSON, fileMessage.getPayloadId(), Constants.MESSAGE_STATUS_ROUTING);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static void saveVideoToDatabase(Context context, Contact contact, VideoMessage videoMessage) {
        List<VideoPart> audioParts = videoMessage.getParts();
        Collections.sort(audioParts);

        ByteBuffer fileByteBuffer = ByteBuffer.allocate(videoMessage.getTotalSize());

        for (VideoPart part : audioParts) {
            Log.d(TAG, "saveVideoToDatabase: adding chunk " + part.getPartNo());
            fileByteBuffer.put(part.getContent());
        }

        byte[] fileBytes = fileByteBuffer.array();
        Log.d(TAG, "saveVideoToDatabase: video byte array has size " + fileBytes.length);
        String filename = videoMessage.getFileName();

        String filePath = "";

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).toString();
            File file = new File(path, filename);
            filePath = file.getAbsolutePath();
            try {
                FileOutputStream stream = new FileOutputStream(file);
                stream.write(fileBytes);
                stream.close();
            } catch (IOException e) {
                Log.d(TAG, "saveVideoToDatabase: could not save file in storage. Error: " + e.getMessage());
                e.printStackTrace();
                return;
            }
        } else {
            try {
                ContentValues values = new ContentValues();
                values.put(MediaStore.MediaColumns.DISPLAY_NAME, videoMessage.getFileName());
                values.put(MediaStore.MediaColumns.MIME_TYPE, videoMessage.getFileExtension());
                values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS + "/SnakeMessenger/");

                Uri uri = context.getContentResolver().insert(MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL), values);
                OutputStream stream = context.getContentResolver().openOutputStream(uri);
                filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath() + "/SnakeMessenger/" + filename;
                stream.write(fileBytes);
                stream.close();
            } catch (IOException e) {
                Log.d(TAG, "saveVideoToDatabase: could not save file in storage. Error: " + e.getMessage());
                e.printStackTrace();
                return;
            }
        }

        JSONObject messageJSON = new JSONObject();
        db.getMediaMessageUriDao().addMediaMessageUri(new MediaMessageUri(
                videoMessage.getMessageId(), filePath
        ));

        try {
            String encryptionKey = CryptoManager.INSTANCE.generateKey();
            String encryptedMessage = CryptoManager.INSTANCE.encryptMessage(encryptionKey, filePath);
            messageJSON.put(Constants.JSON_MESSAGE_ID_KEY, videoMessage.getMessageId());
            messageJSON.put(Constants.JSON_SOURCE_DEVICE_ID_KEY, videoMessage.getSourceId());
            messageJSON.put(Constants.JSON_DESTINATION_DEVICE_ID_KEY, videoMessage.getDestinationId());
            messageJSON.put(Constants.JSON_MESSAGE_TIMESTAMP_KEY, videoMessage.getTimestamp());
            messageJSON.put(Constants.JSON_CONTENT_TYPE_KEY, Constants.CONTENT_VIDEO);
            messageJSON.put(Constants.JSON_MESSAGE_TYPE_KEY, Constants.MESSAGE_TYPE_MESSAGE);
            messageJSON.put(Constants.JSON_ENCRYPTION_KEY, encryptionKey);
            messageJSON.put(Constants.JSON_IS_ENCRYPTED, true);
            messageJSON.put(Constants.JSON_MESSAGE_CONTENT_KEY, encryptedMessage);
            messageJSON.put(Constants.JSON_MESSAGE_TOTAL_SIZE, videoMessage.getTotalSize());
        } catch (JSONException e) {
            Log.d(TAG, "saveVideoToDatabase: could not save video. Error: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        String destinationId = videoMessage.getDestinationId();

        if (destinationId.equals(myDeviceId)) {
            Log.d(TAG, "saveVideoToDatabase: the message is for the current device");

            Message receivedMessage = saveOwnMessageToDatabase(messageJSON, videoMessage.getPayloadId(), Constants.MESSAGE_STATUS_RECEIVED);

            if (currentChat == null || !currentChat.equals(contact.getDeviceID())) {
                NotificationHandler.sendMessageNotification(context, contact, receivedMessage);
            }
        } else {
            Log.d(TAG, "saveVideoToDatabase: the message is routing to another device");

            String routingNodes = null;
            try {
                routingNodes = messageJSON.getString(Constants.JSON_ROUTING_NODES);
                routingNodes += myDeviceId + ";";
                messageJSON.put(Constants.JSON_ROUTING_NODES, routingNodes);
                saveDataMemoryMessageToDatabase(messageJSON, videoMessage.getPayloadId(), Constants.MESSAGE_STATUS_ROUTING);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static void saveAudioToDatabase(Context context, Contact contact, AudioMessage audioMessage) {
        List<AudioPart> audioParts = audioMessage.getParts();
        Collections.sort(audioParts);

        ByteBuffer fileByteBuffer = ByteBuffer.allocate(audioMessage.getTotalSize());

        for (AudioPart part : audioParts) {
            Log.d(TAG, "saveAudioToDatabase: adding chunk " + part.getPartNo());
            fileByteBuffer.put(part.getContent());
        }

        byte[] fileBytes = fileByteBuffer.array();
        Log.d(TAG, "saveAudioToDatabase: video byte array has size " + fileBytes.length);
        String filename = audioMessage.getFileName();

        String filePath = "";

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).toString();
            File file = new File(path, filename);
            filePath = file.getAbsolutePath();
            try {
                FileOutputStream stream = new FileOutputStream(file);
                stream.write(fileBytes);
                stream.close();
            } catch (IOException e) {
                Log.d(TAG, "saveAudioToDatabase: could not save file in storage. Error: " + e.getMessage());
                e.printStackTrace();
                return;
            }
        } else {
            try {
                ContentValues values = new ContentValues();
                values.put(MediaStore.MediaColumns.DISPLAY_NAME, audioMessage.getFileName());
                values.put(MediaStore.MediaColumns.MIME_TYPE, audioMessage.getFileExtension());
                values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS + "/SnakeMessenger/");

                Uri uri = context.getContentResolver().insert(MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL), values);
                OutputStream stream = context.getContentResolver().openOutputStream(uri);
                filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath() + "/SnakeMessenger/" + filename;
                stream.write(fileBytes);
                stream.close();
            } catch (IOException e) {
                Log.d(TAG, "saveAudioToDatabase: could not save file in storage. Error: " + e.getMessage());
                e.printStackTrace();
                return;
            }
        }

        JSONObject messageJSON = new JSONObject();
        db.getMediaMessageUriDao().addMediaMessageUri(new MediaMessageUri(
                audioMessage.getMessageId(), filePath
        ));

        try {
            String encryptionKey = CryptoManager.INSTANCE.generateKey();
            String encryptedMessage = CryptoManager.INSTANCE.encryptMessage(encryptionKey, filePath);
            messageJSON.put(Constants.JSON_MESSAGE_ID_KEY, audioMessage.getMessageId());
            messageJSON.put(Constants.JSON_SOURCE_DEVICE_ID_KEY, audioMessage.getSourceId());
            messageJSON.put(Constants.JSON_DESTINATION_DEVICE_ID_KEY, audioMessage.getDestinationId());
            messageJSON.put(Constants.JSON_MESSAGE_TIMESTAMP_KEY, audioMessage.getTimestamp());
            messageJSON.put(Constants.JSON_CONTENT_TYPE_KEY, Constants.CONTENT_AUDIO);
            messageJSON.put(Constants.JSON_MESSAGE_TYPE_KEY, Constants.MESSAGE_TYPE_MESSAGE);
            messageJSON.put(Constants.JSON_ENCRYPTION_KEY, encryptionKey);
            messageJSON.put(Constants.JSON_IS_ENCRYPTED, true);
            messageJSON.put(Constants.JSON_MESSAGE_CONTENT_KEY, encryptedMessage);
            messageJSON.put(Constants.JSON_MESSAGE_TOTAL_SIZE, audioMessage.getTotalSize());
        } catch (JSONException e) {
            Log.d(TAG, "saveAudioToDatabase: could not save video. Error: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        String destinationId = audioMessage.getDestinationId();

        if (destinationId.equals(myDeviceId)) {
            Log.d(TAG, "saveAudioToDatabase: the message is for the current device");

            Message receivedMessage = saveOwnMessageToDatabase(messageJSON, audioMessage.getPayloadId(), Constants.MESSAGE_STATUS_RECEIVED);

            if (currentChat == null || !currentChat.equals(contact.getDeviceID())) {
                NotificationHandler.sendMessageNotification(context, contact, receivedMessage);
            }
        } else {
            Log.d(TAG, "saveAudioToDatabase: the message is routing to another device");
            String routingNodes;
            try {
                routingNodes = messageJSON.getString(Constants.JSON_ROUTING_NODES);
                routingNodes += myDeviceId + ";";
                messageJSON.put(Constants.JSON_ROUTING_NODES, routingNodes);
                saveDataMemoryMessageToDatabase(messageJSON, audioMessage.getPayloadId(), Constants.MESSAGE_STATUS_ROUTING);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
