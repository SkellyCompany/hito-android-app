package com.skellyco.hito.core.application;

import android.app.Activity;

import androidx.lifecycle.LiveData;

import com.skellyco.hito.core.entity.PrivateConversation;
import com.skellyco.hito.core.entity.dto.MessageDTO;
import com.skellyco.hito.core.entity.dto.PrivateConversationDTO;
import com.skellyco.hito.core.shared.Resource;
import com.skellyco.hito.core.shared.error.FetchDataError;
import com.skellyco.hito.core.shared.error.InsertDataError;

public interface IPrivateConversationService {

    LiveData<Resource<Void, InsertDataError>> createPrivateConversation(PrivateConversationDTO privateConversationDTO, MessageDTO messageDTO);

    LiveData<Resource<Void, InsertDataError>> insertMessage(String privateConversationId, MessageDTO messageDTO);

    LiveData<Resource<PrivateConversation, FetchDataError>> getPrivateConversation(final Activity activity,
                                                                                   final String firstInterlocutorId,
                                                                                   final String secondInterlocutorId);
}
