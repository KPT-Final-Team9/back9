package com.core.back9.service;

import com.core.back9.dto.TenantDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class TenantService {



    public TenantDTO.RegisterResponse register(TenantDTO.RegisterRequest request) {

        return null;

    }
}
