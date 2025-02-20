package com.example.snakemessenger.chats;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.snakemessenger.MainActivity;
import com.example.snakemessenger.R;
import com.example.snakemessenger.databinding.ChatReceivedAudioMessageBinding;
import com.example.snakemessenger.databinding.ChatReceivedFileMessageBinding;
import com.example.snakemessenger.databinding.ChatReceivedVideoMessageBinding;
import com.example.snakemessenger.databinding.ChatSentAudioMessageBinding;
import com.example.snakemessenger.databinding.ChatSentFileMessageBinding;
import com.example.snakemessenger.databinding.ChatSentVideoMessageBinding;
import com.example.snakemessenger.general.Constants;
import com.example.snakemessenger.managers.DateManager;
import com.example.snakemessenger.models.Contact;
import com.example.snakemessenger.models.Message;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;
import static com.example.snakemessenger.chats.ChatActivity.TAG;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context context;
    private List<Message> messages;
    private Contact contact;

    public ChatAdapter(Context context, List<Message> messages, Contact contact) {
        this.context = context;
        this.messages = messages;
        this.contact = contact;
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);

        int status = message.getStatus();
        int contentType = message.getContentType();

        if (status == Constants.MESSAGE_STATUS_RECEIVED) {
            if (contentType == Constants.CONTENT_TEXT) {
                return Constants.RECEIVED_TEXT_MESSAGE;
            }

            if (contentType == Constants.CONTENT_FILE) {
                return Constants.RECEIVED_FILE_MESSAGE;
            }

            if (contentType == Constants.CONTENT_VIDEO) {
                return Constants.RECEIVED_VIDEO_MESSAGE;
            }

            if (contentType == Constants.CONTENT_AUDIO) {
                return Constants.RECEIVED_AUDIO_MESSAGE;
            }

            return Constants.RECEIVED_IMAGE_MESSAGE;
        } else {
            if (contentType == Constants.CONTENT_TEXT) {
                return Constants.SENT_TEXT_MESSAGE;
            }

            if (contentType == Constants.CONTENT_FILE) {
                return Constants.SENT_FILE_MESSAGE;
            }

            if (contentType == Constants.CONTENT_VIDEO) {
                return Constants.SENT_VIDEO_MESSAGE;
            }

            if (contentType == Constants.CONTENT_AUDIO) {
                return Constants.SENT_AUDIO_MESSAGE;
            }

            return Constants.SENT_IMAGE_MESSAGE;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;

        switch (viewType) {
            case Constants.SENT_TEXT_MESSAGE:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.chat_sent_text_message, parent, false);
                return new CurrentUserTextMessageViewHolder(itemView);
            case Constants.SENT_IMAGE_MESSAGE:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.chat_sent_image_message, parent, false);
                return new CurrentUserImageMessageViewHolder(itemView);
            case Constants.SENT_FILE_MESSAGE:
                return new CurrentUserFileMessageViewHolder(ChatSentFileMessageBinding
                        .inflate(LayoutInflater.from(parent.getContext()), parent, false));
            case Constants.SENT_VIDEO_MESSAGE:
                return new CurrentUserVideoMessageViewHolder(ChatSentVideoMessageBinding
                        .inflate(LayoutInflater.from(parent.getContext()), parent, false));
            case Constants.SENT_AUDIO_MESSAGE:
                return new CurrentUserAudioMessageViewHolder(ChatSentAudioMessageBinding
                        .inflate(LayoutInflater.from(parent.getContext()), parent, false));
            case Constants.RECEIVED_TEXT_MESSAGE:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.chat_received_text_message, parent, false);
                return new OtherUserTextMessageViewHolder(itemView);
            case Constants.RECEIVED_FILE_MESSAGE:
                return new OtherUserFileMessageViewHolder(ChatReceivedFileMessageBinding
                        .inflate(LayoutInflater.from(parent.getContext()), parent, false));
            case Constants.RECEIVED_VIDEO_MESSAGE:
                return new OtherUserVideoMessageViewHolder(ChatReceivedVideoMessageBinding
                        .inflate(LayoutInflater.from(parent.getContext()), parent, false));
            case Constants.RECEIVED_AUDIO_MESSAGE:
                return new OtherUserAudioMessageViewHolder(ChatReceivedAudioMessageBinding
                        .inflate(LayoutInflater.from(parent.getContext()), parent, false));
            default:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.chat_received_image_message, parent, false);
                return new OtherUserImageMessageViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message currentMessage = messages.get(position);

        String messageContent = currentMessage.getContent();
        Date date = new Date(currentMessage.getTimestamp());
        SimpleDateFormat ft = new SimpleDateFormat(Constants.DATE_TIME_FORMAT, Locale.US);

        Date currentDate = Calendar.getInstance().getTime();

        int viewType = getItemViewType(position);

        switch (viewType) {
            case Constants.SENT_TEXT_MESSAGE:
                CurrentUserTextMessageViewHolder stHolder = (CurrentUserTextMessageViewHolder) holder;

                SharedPreferences stSharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES, MODE_PRIVATE);

                String stName = stSharedPreferences.getString(Constants.SHARED_PREFERENCES_NAME, "");
                String stPhotoUri = stSharedPreferences.getString(Constants.SHARED_PREFERENCES_PHOTO_URI, null);

                if (stPhotoUri != null) {
                    Uri imageUri = Uri.parse(stPhotoUri);
                    Glide.with(context).load(imageUri).into(stHolder.getSenderProfilePictureImageView());
                }

                stHolder.getSenderNameTextView().setText(stName);
                stHolder.getMessageContentTextView().setText(messageContent);
                stHolder.getTimestampTextView().setText(DateManager.getLastActiveText(ft.format(currentDate), ft.format(date)));

                if (currentMessage.getStatus() == Constants.MESSAGE_STATUS_SENT) {
                    stHolder.getMessageStatusImageView().setImageResource(R.drawable.ic_baseline_done_24);
                } else {
                    stHolder.getMessageStatusImageView().setImageResource(R.drawable.ic_baseline_done_all_24);
                }

                break;
            case Constants.SENT_IMAGE_MESSAGE:
                CurrentUserImageMessageViewHolder siHolder = (CurrentUserImageMessageViewHolder) holder;

                SharedPreferences siSharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES, MODE_PRIVATE);

                String siName = siSharedPreferences.getString(Constants.SHARED_PREFERENCES_NAME, "");
                String siPhotoUri = siSharedPreferences.getString(Constants.SHARED_PREFERENCES_PHOTO_URI, null);

                if (siPhotoUri != null) {
                    Uri imageUri = Uri.parse(siPhotoUri);
                    Glide.with(context).load(imageUri).into(siHolder.getSenderProfilePictureImageView());
                }

                siHolder.getSenderNameTextView().setText(siName);
                siHolder.getTimestampTextView().setText(DateManager.getLastActiveText(ft.format(currentDate), ft.format(date)));

                Uri siImageUri = Uri.parse(messageContent);
                Log.d(TAG, "onBindViewHolder: loading uri " + siImageUri + " in ImageView");

                Bitmap siImageBitmap = null;

                try {
                    siImageBitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(siImageUri));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                if (siImageBitmap != null) {
                    Glide.with(context).load(siImageBitmap).into(siHolder.getMessageContentImageView());
                } else {
                    Log.d(TAG, "onBindViewHolder: image bitmap is null!");
                }

                if (currentMessage.getStatus() == Constants.MESSAGE_STATUS_SENT) {
                    siHolder.getMessageStatusImageView().setImageResource(R.drawable.ic_baseline_done_24);
                } else {
                    siHolder.getMessageStatusImageView().setImageResource(R.drawable.ic_baseline_done_all_24);
                }
                break;
            case Constants.SENT_FILE_MESSAGE:
                CurrentUserFileMessageViewHolder sfHolder = (CurrentUserFileMessageViewHolder) holder;

                SharedPreferences sfSharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES, MODE_PRIVATE);

                String sfName = sfSharedPreferences.getString(Constants.SHARED_PREFERENCES_NAME, "");
                String sfPhotoUri = sfSharedPreferences.getString(Constants.SHARED_PREFERENCES_PHOTO_URI, null);

                if (sfPhotoUri != null) {
                    Uri imageUri = Uri.parse(sfPhotoUri);
                    Glide.with(context).load(imageUri).into(sfHolder.getSenderProfilePictureImageView());
                }

                sfHolder.getSenderNameTextView().setText(sfName);
                sfHolder.getTimestampTextView().setText(DateManager.getLastActiveText(ft.format(currentDate), ft.format(date)));
                sfHolder.getFileNameTextView().setText(messageContent);

                if (currentMessage.getStatus() == Constants.MESSAGE_STATUS_SENT) {
                    sfHolder.getMessageStatusImageView().setImageResource(R.drawable.ic_baseline_done_24);
                } else {
                    sfHolder.getMessageStatusImageView().setImageResource(R.drawable.ic_baseline_done_all_24);
                }
                break;
            case Constants.SENT_VIDEO_MESSAGE:
                CurrentUserVideoMessageViewHolder svHolder = (CurrentUserVideoMessageViewHolder) holder;

                SharedPreferences svSharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES, MODE_PRIVATE);

                String svName = svSharedPreferences.getString(Constants.SHARED_PREFERENCES_NAME, "");
                String svPhotoUri = svSharedPreferences.getString(Constants.SHARED_PREFERENCES_PHOTO_URI, null);

                if (svPhotoUri != null) {
                    Uri imageUri = Uri.parse(svPhotoUri);
                    Glide.with(context).load(imageUri).into(svHolder.getSenderProfilePictureImageView());
                }

                svHolder.getSenderNameTextView().setText(svName);
                svHolder.getTimestampTextView().setText(DateManager.getLastActiveText(ft.format(currentDate), ft.format(date)));

                Uri svVideoUri = Uri.parse(MainActivity.db.getMediaMessageUriDao()
                        .findByMessageId(currentMessage.getMessageId()).getVideoUri());
                MediaController svMediaController = new MediaController(context);
                svMediaController.setMediaPlayer(svHolder.getMessageContentVideoView());
                svHolder.getMessageContentVideoView().setVideoURI(svVideoUri);
                svHolder.getMessageContentVideoView().setMediaController(svMediaController);

                if (currentMessage.getStatus() == Constants.MESSAGE_STATUS_SENT) {
                    svHolder.getMessageStatusImageView().setImageResource(R.drawable.ic_baseline_done_24);
                } else {
                    svHolder.getMessageStatusImageView().setImageResource(R.drawable.ic_baseline_done_all_24);
                }
                break;
            case Constants.SENT_AUDIO_MESSAGE:
                CurrentUserAudioMessageViewHolder saHolder = (CurrentUserAudioMessageViewHolder) holder;

                SharedPreferences saSharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES, MODE_PRIVATE);

                String saName = saSharedPreferences.getString(Constants.SHARED_PREFERENCES_NAME, "");
                String saPhotoUri = saSharedPreferences.getString(Constants.SHARED_PREFERENCES_PHOTO_URI, null);

                if (saPhotoUri != null) {
                    Uri imageUri = Uri.parse(saPhotoUri);
                    Glide.with(context).load(imageUri).into(saHolder.getSenderProfilePictureImageView());
                }

                saHolder.getSenderNameTextView().setText(saName);
                saHolder.getTimestampTextView().setText(DateManager.getLastActiveText(ft.format(currentDate), ft.format(date)));

                Uri saVideoUri = Uri.parse(MainActivity.db.getMediaMessageUriDao()
                        .findByMessageId(currentMessage.getMessageId()).getVideoUri());
                MediaController saMediaController = new MediaController(context);
                saMediaController.setMediaPlayer(saHolder.getMessageContentVideoView());
                saHolder.getMessageContentVideoView().setVideoURI(saVideoUri);
                saHolder.getMessageContentVideoView().setMediaController(saMediaController);

                if (currentMessage.getStatus() == Constants.MESSAGE_STATUS_SENT) {
                    saHolder.getMessageStatusImageView().setImageResource(R.drawable.ic_baseline_done_24);
                } else {
                    saHolder.getMessageStatusImageView().setImageResource(R.drawable.ic_baseline_done_all_24);
                }
                break;
            case Constants.RECEIVED_TEXT_MESSAGE:
                OtherUserTextMessageViewHolder rtHolder = (OtherUserTextMessageViewHolder) holder;

                if (contact.getPhotoUri() != null) {
                    Uri imageUri = Uri.parse(contact.getPhotoUri());
                    Glide.with(context).load(imageUri).into(rtHolder.getSenderProfilePictureImageView());
                }

                rtHolder.getSenderNameTextView().setText(contact.getName());
                rtHolder.getMessageContentTextView().setText(messageContent);
                rtHolder.getTimestampTextView().setText(DateManager.getLastActiveText(ft.format(currentDate), ft.format(date)));
                break;
            case Constants.RECEIVED_FILE_MESSAGE:
                OtherUserFileMessageViewHolder rfHolder = (OtherUserFileMessageViewHolder) holder;

                if (contact.getPhotoUri() != null) {
                    Uri imageUri = Uri.parse(contact.getPhotoUri());
                    Glide.with(context).load(imageUri).into(rfHolder.getSenderProfilePictureImageView());
                }

                rfHolder.getSenderNameTextView().setText(contact.getName());
                rfHolder.getTimestampTextView().setText(DateManager.getLastActiveText(ft.format(currentDate), ft.format(date)));
                rfHolder.getFileNameTextView().setText(messageContent);
                break;
            case Constants.RECEIVED_VIDEO_MESSAGE:
                OtherUserVideoMessageViewHolder rvHolder = (OtherUserVideoMessageViewHolder) holder;

                if (contact.getPhotoUri() != null) {
                    Uri imageUri = Uri.parse(contact.getPhotoUri());
                    Glide.with(context).load(imageUri).into(rvHolder.getSenderProfilePictureImageView());
                }

                rvHolder.getSenderNameTextView().setText(contact.getName());
                rvHolder.getTimestampTextView().setText(DateManager.getLastActiveText(ft.format(currentDate), ft.format(date)));

                Uri rvVideoUri = Uri.parse(MainActivity.db.getMediaMessageUriDao()
                        .findByMessageId(currentMessage.getMessageId()).getVideoUri());
                MediaController rvMediaController = new MediaController(context);
                rvMediaController.setMediaPlayer(rvHolder.getMessageContentVideoView());
                rvHolder.getMessageContentVideoView().setVideoURI(rvVideoUri);
                rvHolder.getMessageContentVideoView().setMediaController(rvMediaController);
                break;
            case Constants.RECEIVED_AUDIO_MESSAGE:
                OtherUserAudioMessageViewHolder raHolder = (OtherUserAudioMessageViewHolder) holder;

                if (contact.getPhotoUri() != null) {
                    Uri imageUri = Uri.parse(contact.getPhotoUri());
                    Glide.with(context).load(imageUri).into(raHolder.getSenderProfilePictureImageView());
                }

                raHolder.getSenderNameTextView().setText(contact.getName());
                raHolder.getTimestampTextView().setText(DateManager.getLastActiveText(ft.format(currentDate), ft.format(date)));

                Uri raVideoUri = Uri.parse(MainActivity.db.getMediaMessageUriDao()
                        .findByMessageId(currentMessage.getMessageId()).getVideoUri());
                MediaController raMediaController = new MediaController(context);
                raMediaController.setMediaPlayer(raHolder.getMessageContentVideoView());
                raHolder.getMessageContentVideoView().setVideoURI(raVideoUri);
                raHolder.getMessageContentVideoView().setMediaController(raMediaController);
                break;
            default:
                OtherUserImageMessageViewHolder riHolder = (OtherUserImageMessageViewHolder) holder;

                if (contact.getPhotoUri() != null) {
                    Uri imageUri = Uri.parse(contact.getPhotoUri());
                    Glide.with(context).load(imageUri).into(riHolder.getSenderProfilePictureImageView());
                }

                riHolder.getSenderNameTextView().setText(contact.getName());
                riHolder.getTimestampTextView().setText(DateManager.getLastActiveText(ft.format(currentDate), ft.format(date)));

                Uri riImageUri = Uri.parse(messageContent);
                Log.d(TAG, "onBindViewHolder: loading uri " + riImageUri + " in ImageView");

                Bitmap riImageBitmap = null;

                try {
                    riImageBitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(riImageUri));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                if (riImageBitmap != null) {
                    Glide.with(context).load(riImageBitmap).into(riHolder.getMessageContentImageView());
                } else {
                    Log.d(TAG, "onBindViewHolder: image bitmap is null!");
                }

                break;
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void setMessages(List<Message> messages) {
        if (this.messages.size() > 0) {
            this.messages.clear();
        }

        this.messages = messages;
        notifyDataSetChanged();
    }

    public void setContact(Contact contact) {
        this.contact = contact;
        notifyDataSetChanged();
    }
}
