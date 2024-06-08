package com.core.back9.service;

import com.core.back9.dto.MemberDTO;
import com.core.back9.dto.TenantDTO;
import com.core.back9.entity.Tenant;
import com.core.back9.entity.constant.Role;
import com.core.back9.entity.constant.Status;
import com.core.back9.exception.ApiErrorCode;
import com.core.back9.exception.ApiException;
import com.core.back9.mapper.TenantMapper;
import com.core.back9.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class TenantService {

    private final TenantRepository tenantRepository;
    private final TenantMapper tenantMapper;

    @Transactional
    public TenantDTO.Response registerTenant(MemberDTO.Info member, TenantDTO.Request request) {

        if(member.getRole() != Role.ADMIN) {
            throw new ApiException(ApiErrorCode.DO_NOT_HAVE_PERMISSION, "관리자만 접근할 수 있습니다.");
        }

        Optional<Tenant> selectedTenant = tenantRepository.findByCompanyNumberAndStatus(request.getCompanyNumber(), Status.REGISTER);

        if(selectedTenant.isPresent()) {
            throw new ApiException(ApiErrorCode.DUPLICATE_COMPANYNUMBER);
        }

        Tenant tenant = tenantMapper.toEntity(request);

        return tenantMapper.toRegisterResponse(tenantRepository.save(tenant));

    }

    public TenantDTO.InfoList getAllTenant(Pageable pageable) {

        Page<Tenant> tenants = tenantRepository.selectAllRegisteredTenant(Status.REGISTER, pageable);

        long count = tenants.getTotalElements();

        List<TenantDTO.Info> tenantInfoList = tenants.stream()
                .map(tenantMapper::toInfo)
                .collect(Collectors.toList());

        return tenantMapper.toInfoList(count, tenantInfoList);

    }

    public TenantDTO.Info getOneTenant(Long tenantId) {

        Tenant tenant = tenantRepository.getValidOneTenantOrThrow(tenantId);

        return tenantMapper.toInfo(tenant);
    }

    @Transactional
    public TenantDTO.Info modifyTenant(
            MemberDTO.Info member, Long tenantId,
            TenantDTO.Request request
    ) {

        if(member.getRole() != Role.ADMIN) {
            throw new ApiException(ApiErrorCode.DO_NOT_HAVE_PERMISSION, "관리자만 접근할 수 있습니다.");
        }

        Tenant tenant = tenantRepository.getValidOneTenantOrThrow(tenantId);
        Tenant updatedTenant = tenant.update(request);

        return tenantMapper.toInfo(updatedTenant);
    }

    @Transactional
    public Integer deleteTenant(MemberDTO.Info member, Long tenantId) {

        if(member.getRole() != Role.ADMIN) {
            throw new ApiException(ApiErrorCode.DO_NOT_HAVE_PERMISSION, "관리자만 접근할 수 있습니다.");
        }

        return tenantRepository.deleteRegisteredTenant(Status.UNREGISTER, tenantId)
                .filter(result -> result != 0)
                .orElseThrow(() -> new ApiException(ApiErrorCode.DELETE_FAIL));

    }
}






