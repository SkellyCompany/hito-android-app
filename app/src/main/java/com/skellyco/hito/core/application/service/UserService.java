package com.skellyco.hito.core.application.service;

import androidx.lifecycle.LiveData;

import com.skellyco.hito.core.application.IUserService;
import com.skellyco.hito.core.domain.IUserRepository;
import com.skellyco.hito.core.entity.User;
import com.skellyco.hito.core.shared.Resource;
import com.skellyco.hito.core.shared.error.FetchDataError;

import java.util.List;

public class UserService implements IUserService {

    private IUserRepository userRepository;

    public UserService(IUserRepository userRepository)
    {
        this.userRepository = userRepository;
    }

    @Override
    public LiveData<Resource<List<User>, FetchDataError>> getLocalUsers(String uid)
    {
        return userRepository.getLocalUsers(uid);
    }
}