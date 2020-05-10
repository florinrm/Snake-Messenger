package com.example.snakemessenger;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<GroupChatViewHolder> {
    private FirebaseFirestore db;
    private StorageReference storageReference;
    private List<Message> mMessages;

    public ChatAdapter(List<Message> mMessages) {
        this.mMessages = mMessages;
    }

    @NonNull
    @Override
    public GroupChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_message_item, parent, false);

        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        return new GroupChatViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final GroupChatViewHolder holder, int position) {
        Message currentMessage = mMessages.get(position);

        final String senderID = currentMessage.getSender();
        String messageContent = currentMessage.getContent();
        Date date = currentMessage.getTimestamp().toDate();
        SimpleDateFormat ft = new SimpleDateFormat("dd.MM.yy ',' HH:mm");

        db.collection("users")
                .document(senderID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            holder.getmSenderName().setText(documentSnapshot.getString("name"));
                            boolean hasPhoto = documentSnapshot.getBoolean("picture");

                            if (hasPhoto) {
                                final long TEN_MEGABYTES = 10 * 1024 * 1024;
                                if (MainActivity.profilePictures.containsKey(senderID)) {
                                    holder.getmSenderProfilePicture().setImageBitmap(MainActivity.profilePictures.get(senderID));
                                } else {
                                    storageReference.child(senderID + "-profile_pic")
                                            .getBytes(TEN_MEGABYTES)
                                            .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                                @Override
                                                public void onSuccess(byte[] bytes) {
                                                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                                    holder.getmSenderProfilePicture().setImageBitmap(bitmap);
                                                    MainActivity.profilePictures.put(senderID, bitmap);
                                                }
                                            });
                                }
                            }
                        }
                    }
                });

        holder.getmMessageContent().setText(messageContent);
        holder.getmTimestamp().setText(ft.format(date));
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }
}
