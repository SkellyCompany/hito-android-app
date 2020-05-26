package com.skellyco.hito.view.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.r0adkll.slidr.Slidr;
import com.skellyco.hito.R;
import com.skellyco.hito.core.entity.Message;
import com.skellyco.hito.core.entity.PrivateConversation;
import com.skellyco.hito.core.entity.User;
import com.skellyco.hito.core.entity.dto.MessageDTO;
import com.skellyco.hito.core.shared.Resource;
import com.skellyco.hito.core.shared.error.FetchDataError;
import com.skellyco.hito.view.main.adapter.MessageAdapter;
import com.skellyco.hito.viewmodel.main.ChatViewModel;

import java.util.Calendar;
import java.util.Date;

public class ChatActivity extends AppCompatActivity {

    public static String TAG = "ChatActivity";
    public static String EXTRA_LOGGED_IN_UID = "EXTRA_LOGGED_IN_UID";
    public static String EXTRA_INTERLOCUTOR_UID = "INTERLOCUTOR_UID";
    private static String MESSAGE_PROMPT = "Message to @";

    private ImageButton btnBack;
    private TextView tvDisplayName;
    private RecyclerView recMessages;
    private EditText etMessageInput;
    private ImageButton btnSendMessage;

    private ChatViewModel chatViewModel;
    private MessageAdapter messageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Slidr.attach(this);
        String loggedInUid = getIntent().getStringExtra(EXTRA_LOGGED_IN_UID);
        String interlocutorId = getIntent().getStringExtra(EXTRA_INTERLOCUTOR_UID);

        initializeViews();
        initializeViewModel(loggedInUid, interlocutorId);
        initializeListeners();
        initializeRecyclerViewAndAdapter();
        initializeMessageInput();
    }

    private void initializeViews()
    {
        btnBack = findViewById(R.id.btnBack);
        tvDisplayName = findViewById(R.id.tvDisplayName);
        recMessages = findViewById(R.id.recMessages);
        etMessageInput = findViewById(R.id.etMessageInput);
        btnSendMessage = findViewById(R.id.btnSendMessage);
    }

    private void initializeViewModel(String loggedInUid, String interlocutorUid)
    {
        chatViewModel = new ViewModelProvider(this).get(ChatViewModel.class);
        chatViewModel.setUsers(loggedInUid, interlocutorUid);
    }

    private void initializeListeners()
    {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
    }

    private void initializeRecyclerViewAndAdapter()
    {
        recMessages.setLayoutManager(new LinearLayoutManager(this));
        recMessages.setHasFixedSize(true);
        messageAdapter = new MessageAdapter();
        recMessages.setAdapter(messageAdapter);
    }

    private void initializeMessageInput()
    {
        etMessageInput.setEnabled(false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        chatViewModel.fetchInterlocutor(this);
        chatViewModel.getInterlocutor().observe(this, new Observer<Resource<User, FetchDataError>>() {
            @Override
            public void onChanged(Resource<User, FetchDataError> resource) {
                if(resource.getStatus() == Resource.Status.SUCCESS)
                {
                    tvDisplayName.setText(resource.getData().getUsername());
                    etMessageInput.setHint(MESSAGE_PROMPT + resource.getData().getUsername());
                    initializeMessages();
                }
                // For right now I have not discovered any possible errors that can happen during fetching the data.
                // If some error will occur, it will be logged in domain layer so it won't pass unnoticed.
                // If this will happen, I will update error handling for FetchDataError both in domain and here.
            }
        });
    }

    private void initializeMessages()
    {
        chatViewModel.fetchPrivateConversation(this);
        chatViewModel.getPrivateConversation().observe(this, new Observer<Resource<PrivateConversation, FetchDataError>>() {
            @Override
            public void onChanged(Resource<PrivateConversation, FetchDataError> resource) {
                if(resource.getStatus() == Resource.Status.SUCCESS)
                {
                    etMessageInput.setEnabled(true);
                    //We are checking here if conversation between users exists
                    if(resource.getData() != null)
                    {
                        messageAdapter.submitList(resource.getData().getMessages());
                    }
                }
            }
        });
    }

    private void sendMessage()
    {
        String interlocutorId = chatViewModel.getLoggedInUid();
        String text = etMessageInput.getText().toString();
        Date postTime = Calendar.getInstance().getTime();
        MessageDTO messageDTO = new MessageDTO(interlocutorId, postTime, text)
        chatViewModel.sendMessage(messageDTO);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.activity_slide_in_left, R.anim.activity_slide_out_right);
    }

}
