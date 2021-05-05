package com.example.snakemessenger.general;

import androidx.room.Ignore;

import com.google.android.gms.nearby.connection.Strategy;

public interface Constants {
    String APP_TITLE = "Snake Messenger";
    String DATABASE_NAME = "SnakeMessengerDB";

    String CHATS_TAB = "Chats";
    String CONTACTS_TAB = "Contacts";

    Strategy STRATEGY = Strategy.P2P_CLUSTER;
    String SERVICE_ID = "com.example.snakemessenger";

    double BATTERY_THRESHOLD = 20.0;
    int MILLIS_IN_DAY = 1000 * 60 * 60 * 24;

    String DATE_TIME_FORMAT = "dd/MM/yyyy HH:mm";

    String SHARED_PREFERENCES = "LOGIN_DETAILS";
    String SHARED_PREFERENCES_NAME = "spName";
    String SHARED_PREFERENCES_DEVICE_ID = "spId";
    String SHARED_PREFERENCES_PASSWORD = "spPassword";
    String SHARED_PREFERENCES_STATUS = "spStatus";
    String SHARED_PREFERENCES_SIGNED_IN = "spSignedIn";
    String SHARED_PREFERENCES_PHOTO_URI = "spPhotoUri";
    String SHARED_PREFERENCES_SERVICE_STARTED = "spServiceStarted";

    String SHARED_PREFERENCES_STATUS_AVAILABLE = "Available";

    String ERROR_NAME_REQUIRED_TEXT = "Name is required!";
    String ERROR_PASSWORD_REQUIRED_TEXT = "Password is required!";
    String ERROR_PASSWORD_CONFIRMATION_REQUIRED_TEXT = "Password confirmation is required!";

    String TOAST_ALL_FIELDS_REQUIRED = "All fields are required";
    String TOAST_PASSWORD_TOO_SHORT = "Password must contain at least 6 characters";
    String TOAST_PASSWORDS_DONT_MATCH = "The two passwords do not match";
    String TOAST_ACCOUNT_CREATED = "Account successfully created";
    String TOAST_FAILED_TO_LOAD_IMAGE = "Failed to load image from device";
    String TOAST_PERMISSION_DENIED = "Permission denied...";
    String TOAST_MISSING_PERMISSIONS = "Missing permissions!";
    String TOAST_INVALID_CREDENTIALS = "Invalid credentials";
    String TOAST_MESSAGE_TOO_LONG = "The message is too long!";
    String TOAST_SELECT_AT_LEAST_ONE_CONTACT = "Please select at least one contact!";
    String TOAST_MESSAGE_SENT = "Message sent";
    String TOAST_CONTACT_DELETED = "Contact deleted";
    String TOAST_CONTACT_SAVED = "Contact saved";
    String TOAST_PROFILE_UPDATED = "Profile updated successfully";
    String TOAST_SIGNED_OUT = "Signed out";

    String EXTRA_IMAGE_CAPTURE_DATA = "data";
    String EXTRA_CONTACT_DEVICE_ID = "deviceId";
    String EXTRA_CONTACT_NAME = "name";
    String EXTRA_NOTIFICATION_ID = "notificationId";

    String SERVICE_NOTIFICATION_CHANNEL = "Service";
    String SERVICE_NOTIFICATION_CHANNEL_DESCRIPTION = "This channel is dedicated to the communication service running outside the application";
    String MESSAGES_NOTIFICATION_CHANNEL = "Messages";
    String MESSAGES_NOTIFICATION_CHANNEL_DESCRIPTION = "This channel is dedicated to notifications about new incoming messages inside the application";

    String REMOTE_INPUT_RESULT_KEY = "keyTextReply";
    String REMOTE_INPUT_LABEL_TEXT = "Your answer...";
    String NOTIFICATION_MESSAGE_REPLY_TEXT = "Reply";
    String NOTIFICATION_MESSAGE_USER_NAME = "You";

    String OPTION_CAMERA = "Camera";
    String OPTION_GALLERY = "Gallery";

    String PICK_PROFILE_PICTURE_TEXT = "Pick a profile picture";

    String JSON_BATTERY_KEY = "battery";
    String JSON_CONTACTS_KEY = "contacts";
    String JSON_DEVICE_ID_KEY = "deviceId";
    String JSON_DEVICE_LAST_CONTACT_KEY = "lastActive";

    String JSON_MESSAGE_ID_KEY = "id";
    String JSON_MESSAGE_TYPE_KEY = "type";
    String JSON_SOURCE_DEVICE_ID_KEY = "source";
    String JSON_DESTINATION_DEVICE_ID_KEY = "destination";
    String JSON_MESSAGE_CONTENT_KEY = "content";
    String JSON_MESSAGE_TIMESTAMP_KEY = "timestamp";
    String JSON_MESSAGES_KEY = "messages";

    int MESSAGE_TYPE_HELLO = 0;
    int MESSAGE_TYPE_MESSAGE = 1;
    int MESSAGE_TYPE_ACK = 2;

    int MESSAGE_STATUS_SENT = 0;
    int MESSAGE_STATUS_DELIVERED = 1;
    int MESSAGE_STATUS_RECEIVED = 2;
    int MESSAGE_STATUS_ROUTING = 3;

    int MAX_SEND_TIMES = 10;
}
