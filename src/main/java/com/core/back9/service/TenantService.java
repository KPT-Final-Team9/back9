package com.core.back9.service;

import com.core.back9.dto.TenantDTO;
import com.core.back9.entity.Tenant;
import com.core.back9.entity.constant.Status;
import com.core.back9.exception.ApiErrorCode;
import com.core.back9.exception.ApiException;
import com.core.back9.mapper.TenantMapper;
import com.core.back9.repository.TenantRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class TenantService {

    private final TenantRepository tenantRepository;
    private final TenantMapper tenantMapper;

    @Transactional
    public TenantDTO.RegisterResponse register(TenantDTO.RegisterRequest request) {

        Tenant tenant = tenantMapper.toEntity(request);
        Tenant savedTenant = tenantRepository.save(tenant);

        return tenantMapper.toRegisterResponse(savedTenant);

    }

    public TenantDTO.InfoList getAllTenant(Pageable pageable) {

        Page<Tenant> tenantList = tenantRepository.selectAllByStatus(Status.REGISTER, pageable);

        long count = tenantList.getTotalElements();

        List<TenantDTO.Info> tenantInfoList = tenantList.stream()
                .map(tenantMapper::toInfo).
                collect(Collectors.toList());

        return tenantMapper.toInfoList(count, tenantInfoList);

    }

    public TenantDTO.Info getOneTenant(Long tenantId) {

        Tenant tenant = tenantRepository.getValidOneTenantOrThrow(Status.REGISTER, tenantId);

        return tenantMapper.toInfo(tenant);
    }

    @Transactional
    public TenantDTO.Info modifyTenant(
            Long tenantId,
            TenantDTO.RegisterRequest request
    ) {

        Tenant tenant = tenantRepository.getValidOneTenantOrThrow(Status.REGISTER, tenantId);
        tenant.update(request);

        return tenantMapper.toInfo(tenant);
    }

    @Transactional
    public Integer deleteTenant(Long tenantId) {

        return tenantRepository.deleteRegisteredTenant(Status.UNREGISTER, tenantId)
                .filter(result -> result != 0)
                .orElseThrow(() -> new ApiException(ApiErrorCode.DELETE_FAIL));

    }
}






