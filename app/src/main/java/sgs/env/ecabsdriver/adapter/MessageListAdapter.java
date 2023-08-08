package sgs.env.ecabsdriver.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;

import sgs.env.ecabsdriver.R;
import sgs.env.ecabsdriver.model.FirestoreChatModel;
import sgs.env.ecabsdriver.util.AppConstants;
import sgs.env.ecabsdriver.util.SharedPrefsHelper;

public class MessageListAdapter extends RecyclerView.Adapter {
    private static final int VIEW_TYPE_MESSAGE_SENT_TEXT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED_TEXT = 2;
    private static final int VIEW_TYPE_MESSAGE_SENT_IMAGE = 3;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED_IMAGE = 4;
    private static final int VIEW_TYPE_MESSAGE_SENT_AUDIO = 5;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED_AUDIO = 6;
    private static final int VIEW_TYPE_MESSAGE_SENT_VIDEO = 7;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED_VIDEO = 8;
    private static final String dateFormate = "dd-MM-yyyy HH:mm";
    private List<FirestoreChatModel> mMessageList;
    private OnItemClickListener mItemClickListener;

    public MessageListAdapter(List<FirestoreChatModel> messageList) {
        mMessageList = messageList;
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    // Determines the appropriate ViewType according to the sender of the message.
    @Override
    public int getItemViewType(int position) {
        FirestoreChatModel message = (FirestoreChatModel) mMessageList.get(position);

        if (message.getName().toString().trim().equals(SharedPrefsHelper.getInstance().get(AppConstants.DRIVER_NAME, "").toString().trim())) {
            // If the current user is the sender of the message
            if (message.getMessageType().equals(AppConstants.MessageTypeImage)) {
                return VIEW_TYPE_MESSAGE_SENT_IMAGE;
            } else if (message.getMessageType().equals(AppConstants.MessageTypeAudio)) {
                return VIEW_TYPE_MESSAGE_SENT_AUDIO;
            } else if (message.getMessageType().equals(AppConstants.MessageTypeVideo)) {
                return VIEW_TYPE_MESSAGE_SENT_VIDEO;
            } else
                return VIEW_TYPE_MESSAGE_SENT_TEXT;
        } else {
            // If some other user sent the message

            if (message.getMessageType().equals(AppConstants.MessageTypeImage)) {
                return VIEW_TYPE_MESSAGE_RECEIVED_IMAGE;
            } else if (message.getMessageType().equals(AppConstants.MessageTypeAudio)) {
                return VIEW_TYPE_MESSAGE_RECEIVED_AUDIO;
            } else if (message.getMessageType().equals(AppConstants.MessageTypeVideo)) {
                return VIEW_TYPE_MESSAGE_RECEIVED_VIDEO;
            } else
                return VIEW_TYPE_MESSAGE_RECEIVED_TEXT;
        }
    }

    // Inflates the appropriate layout according to the ViewType.
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case VIEW_TYPE_MESSAGE_SENT_TEXT:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_message_sent, parent, false);
                return new SentMessageHolder(view);

            case VIEW_TYPE_MESSAGE_RECEIVED_TEXT:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_message_received, parent, false);
                return new ReceivedMessageHolder(view);
            case VIEW_TYPE_MESSAGE_SENT_IMAGE:
                View myImageFileMsgView = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_message_image_sent, parent, false);
                return new MyImageFileMessageHolder(myImageFileMsgView);
            case VIEW_TYPE_MESSAGE_RECEIVED_IMAGE:
                View otherImageFileMsgView = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_message_image_receive, parent, false);
                return new OtherImageFileMessageHolder(otherImageFileMsgView);
            case VIEW_TYPE_MESSAGE_SENT_AUDIO:
                View myAudioFileMsgView = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_message_audio_sent, parent, false);
                return new MyAudioFileMessageHolder(myAudioFileMsgView);
            case VIEW_TYPE_MESSAGE_RECEIVED_AUDIO:
                View otherAudioFileMsgView = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_message_audio_receive, parent, false);
                return new OtherAudioFileMessageHolder(otherAudioFileMsgView);
            case VIEW_TYPE_MESSAGE_SENT_VIDEO:
                View myVideoFileMsgView = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_message_video_sent, parent, false);
                return new MyVideoFileMessageHolder(myVideoFileMsgView);
            case VIEW_TYPE_MESSAGE_RECEIVED_VIDEO:
                View otherVideoFileMsgView = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_message_video_receive, parent, false);
                return new OtherVideoFileMessageHolder(otherVideoFileMsgView);

        }
        return null;
    }

    // Passes the message object to a ViewHolder so that the contents can be bound to UI.
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        FirestoreChatModel message = (FirestoreChatModel) mMessageList.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT_TEXT:
                ((SentMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED_TEXT:
                ((ReceivedMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_SENT_IMAGE:
                ((MyImageFileMessageHolder) holder).bind(message,mItemClickListener);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED_IMAGE:
                ((OtherImageFileMessageHolder) holder).bind(message,mItemClickListener);
                break;
            case VIEW_TYPE_MESSAGE_SENT_AUDIO:
                ((MyAudioFileMessageHolder) holder).bind(message,mItemClickListener);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED_AUDIO:
                ((OtherAudioFileMessageHolder) holder).bind(message,mItemClickListener);
                break;
            case VIEW_TYPE_MESSAGE_SENT_VIDEO:
                ((MyVideoFileMessageHolder) holder).bind(message,mItemClickListener);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED_VIDEO:
                ((OtherVideoFileMessageHolder) holder).bind(message,mItemClickListener);
                break;
        }
    }

    private static class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText;
        LinearLayout mediaTypeText, mediaTypeAudio;

        SentMessageHolder(View itemView) {
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.text_group_chat_message);
            timeText = (TextView) itemView.findViewById(R.id.text_group_chat_time);
            mediaTypeText = itemView.findViewById(R.id.mediaTypeText);
            mediaTypeAudio = itemView.findViewById(R.id.mediaTypeAudio);
        }

        void bind(FirestoreChatModel message) {
            messageText.setText(message.getMessage());

            // Format the stored timestamp into a readable String using method.
            try {
                timeText.setText(new SimpleDateFormat(dateFormate).format(Objects.requireNonNull(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse(message.getTime()))));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    private static class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText, nameText;
        ImageView profileImage;
        LinearLayout group_chat_message_text;

        ReceivedMessageHolder(View itemView) {
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.text_group_chat_message);
            timeText = (TextView) itemView.findViewById(R.id.text_group_chat_time);
            nameText = (TextView) itemView.findViewById(R.id.text_group_chat_nickname);
            profileImage = (ImageView) itemView.findViewById(R.id.image_group_chat_profile);
            group_chat_message_text = itemView.findViewById(R.id.group_chat_message_text);
        }

        void bind(FirestoreChatModel message) {
            messageText.setText(message.getMessage());
            // Format the stored timestamp into a readable String using method.
            try {

                timeText.setText(new SimpleDateFormat(dateFormate).format(Objects.requireNonNull(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse(message.getTime()))));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            nameText.setText(message.getName());

            // Insert the profile image from the URL into the ImageView.
            Glide.with(itemView.getContext()).load(R.mipmap.ic_launcher).into(profileImage);
        }
    }

    private static class MyImageFileMessageHolder extends RecyclerView.ViewHolder {
        TextView timeText;
        ImageView fileThumbnailImage;

        MyImageFileMessageHolder(View itemView) {
            super(itemView);

            timeText = itemView.findViewById(R.id.text_group_chat_time);
            fileThumbnailImage = itemView.findViewById(R.id.image_group_chat_file_thumbnail);
        }

        void bind(FirestoreChatModel message, OnItemClickListener mItemClickListener) {

            RequestOptions myOptions = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true);

            Glide.with(itemView.getContext())
                    .load(message.getMessage())
                    .apply(myOptions)
                    .placeholder(R.drawable.blur_image)
                    .into(fileThumbnailImage);
            // Format the stored timestamp into a readable String using method.
            try {
                timeText.setText(new SimpleDateFormat(dateFormate).format(Objects.requireNonNull(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse(message.getTime()))));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (mItemClickListener != null) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mItemClickListener.onImageMessageItemClick(message);
                    }
                });
            }
        }

    }

    private static class OtherImageFileMessageHolder extends RecyclerView.ViewHolder {
        TextView timeText, nameText;
        ImageView profileImage, fileThumbnailImage;

        OtherImageFileMessageHolder(View itemView) {
            super(itemView);

            timeText = (TextView) itemView.findViewById(R.id.text_group_chat_time);
            nameText = (TextView) itemView.findViewById(R.id.text_group_chat_nickname);
            profileImage = (ImageView) itemView.findViewById(R.id.image_group_chat_profile);
            fileThumbnailImage = itemView.findViewById(R.id.image_group_chat_file_thumbnail);
        }

        void bind(FirestoreChatModel message, OnItemClickListener mItemClickListener) {
            RequestOptions myOptions = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true);

            Glide.with(itemView.getContext())
                    .load(String.valueOf(message.getMessage()))
                    .apply(myOptions)
                    .placeholder(R.drawable.blur_image)
                    .into(fileThumbnailImage);

            // Insert the profile image from the URL into the ImageView.
            Glide.with(itemView.getContext()).load(R.mipmap.ic_launcher).into(profileImage);

            nameText.setText(message.getName());

            // Format the stored timestamp into a readable String using method.
            try {

                timeText.setText(new SimpleDateFormat(dateFormate).format(Objects.requireNonNull(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse(message.getTime()))));
            } catch (ParseException e) {
                e.printStackTrace();
            }


            if (mItemClickListener != null) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mItemClickListener.onImageMessageItemClick(message);
                    }
                });
            }
        }
    }

    private static class MyAudioFileMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText;
        LinearLayout mediaTypeText, mediaTypeAudio;
        CardView card_group_chat_Audio;

        MyAudioFileMessageHolder(View itemView) {
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.text_group_chat_message);
            timeText = (TextView) itemView.findViewById(R.id.text_group_chat_time);
            mediaTypeText = itemView.findViewById(R.id.mediaTypeText);
            mediaTypeAudio = itemView.findViewById(R.id.mediaTypeAudio);
            card_group_chat_Audio = itemView.findViewById(R.id.group_chat_message_audio);
        }

        void bind(FirestoreChatModel message, OnItemClickListener mItemClickListener) {

            // Format the stored timestamp into a readable String using method.
            try {
                timeText.setText(new SimpleDateFormat(dateFormate).format(Objects.requireNonNull(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse(message.getTime()))));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (mItemClickListener != null) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mItemClickListener.onAudioMessageItemClick(message);
                    }
                });
            }
        }

    }

    private static class OtherAudioFileMessageHolder extends RecyclerView.ViewHolder {
        TextView timeText, nameText;
        ImageView profileImage;
        LinearLayout group_chat_message_audio;

        OtherAudioFileMessageHolder(View itemView) {
            super(itemView);

            timeText = (TextView) itemView.findViewById(R.id.text_group_chat_time);
            nameText = (TextView) itemView.findViewById(R.id.text_group_chat_nickname);
            profileImage = (ImageView) itemView.findViewById(R.id.image_group_chat_profile);
            group_chat_message_audio = itemView.findViewById(R.id.group_chat_message_audio);
        }

        void bind(FirestoreChatModel message, OnItemClickListener mItemClickListener) {


            // Insert the profile image from the URL into the ImageView.
            Glide.with(itemView.getContext()).load(R.mipmap.ic_launcher).into(profileImage);

            nameText.setText(message.getName());

            // Format the stored timestamp into a readable String using method.
            try {

                timeText.setText(new SimpleDateFormat(dateFormate).format(Objects.requireNonNull(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse(message.getTime()))));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (mItemClickListener != null) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mItemClickListener.onAudioMessageItemClick(message);
                    }
                });
            }
        }
    }

    private static class MyVideoFileMessageHolder extends RecyclerView.ViewHolder {
        TextView timeText;
        CardView card_group_chat_video;
        ImageView video_thumbnail;

        MyVideoFileMessageHolder(View itemView) {
            super(itemView);

            timeText = (TextView) itemView.findViewById(R.id.text_group_chat_time);
            card_group_chat_video = itemView.findViewById(R.id.card_group_chat_video);
            video_thumbnail = itemView.findViewById(R.id.video_thumbnail);
        }

        void bind(FirestoreChatModel message, OnItemClickListener mItemClickListener) {
            // Format the stored timestamp into a readable String using method.

            RequestOptions myOptions = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true);

            Glide.with(itemView.getContext())
                    .load("")
                    .apply(myOptions)
                    .placeholder(R.drawable.blur_video_img)
                    .thumbnail(Glide.with(itemView.getContext()).load(message.getMessage()))
                    .into(video_thumbnail);

            try {
                timeText.setText(new SimpleDateFormat(dateFormate).format(Objects.requireNonNull(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse(message.getTime()))));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (mItemClickListener != null) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mItemClickListener.onVideoMessageItemClick(message);
                    }
                });
            }
        }
    }

    private static class OtherVideoFileMessageHolder extends RecyclerView.ViewHolder {
        TextView timeText, nameText;
        ImageView profileImage, video_thumbnail;
        CardView card_group_chat_video;

        OtherVideoFileMessageHolder(View itemView) {
            super(itemView);

            timeText = (TextView) itemView.findViewById(R.id.text_group_chat_time);
            nameText = (TextView) itemView.findViewById(R.id.text_group_chat_nickname);
            profileImage = (ImageView) itemView.findViewById(R.id.image_group_chat_profile);
            card_group_chat_video = itemView.findViewById(R.id.card_group_chat_video);
            video_thumbnail = itemView.findViewById(R.id.video_thumbnail);
        }

        void bind(FirestoreChatModel message, OnItemClickListener mItemClickListener) {


            RequestOptions myOptions = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true);

            Glide.with(itemView.getContext())
                    .load("")
                    .apply(myOptions)
                    .placeholder(R.drawable.blur_video_img)
                    .thumbnail(Glide.with(itemView.getContext()).load(message.getMessage()))
                    .into(video_thumbnail);

            // Insert the profile image from the URL into the ImageView.
            Glide.with(itemView.getContext()).load(R.mipmap.ic_launcher).into(profileImage);

            nameText.setText(message.getName());

            // Format the stored timestamp into a readable String using method.
            try {

                timeText.setText(new SimpleDateFormat(dateFormate).format(Objects.requireNonNull(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse(message.getTime()))));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (mItemClickListener != null) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mItemClickListener.onVideoMessageItemClick(message);
                    }
                });
            }
        }
    }

    public void setItemClickListener(OnItemClickListener listener) {
        mItemClickListener = listener;
    }

    public interface OnItemClickListener {

        void onImageMessageItemClick(FirestoreChatModel message);

        void onAudioMessageItemClick(FirestoreChatModel message);

        void onVideoMessageItemClick(FirestoreChatModel message);


    }
}
